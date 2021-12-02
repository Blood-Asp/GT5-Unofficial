package gregtech.common.tileentities.machines.multi;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import gregtech.GT_Mod;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.enums.Textures.BlockIcons;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_EnhancedMultiBlockBase;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Log;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.lazy;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.GT_Values.STEAM_PER_WATER;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER_GLOW;
import static gregtech.api.util.GT_StructureUtility.ofHatchAdder;

public abstract class GT_MetaTileEntity_LargeBoiler extends GT_MetaTileEntity_EnhancedMultiBlockBase<GT_MetaTileEntity_LargeBoiler> {
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final ClassValue<IStructureDefinition<GT_MetaTileEntity_LargeBoiler>> STRUCTURE_DEFINITION =new ClassValue<IStructureDefinition<GT_MetaTileEntity_LargeBoiler>>() {
        @Override
        protected IStructureDefinition<GT_MetaTileEntity_LargeBoiler> computeValue(Class<?> type) {
            return  StructureDefinition.<GT_MetaTileEntity_LargeBoiler>builder()
                    .addShape(STRUCTURE_PIECE_MAIN, transpose(new String[][]{
                            {"ccc", "ccc", "ccc"},
                            {"ccc", "cPc", "ccc"},
                            {"ccc", "cPc", "ccc"},
                            {"ccc", "cPc", "ccc"},
                            {"f~f", "fff", "fff"},
                    }))
                    .addElement('P', lazy(t -> ofBlock(t.getPipeBlock(), t.getPipeMeta())))
                    .addElement('c', lazy(t -> ofChain(
                            ofHatchAdder(GT_MetaTileEntity_LargeBoiler::addOutputToMachineList, t.getCasingTextureIndex(), 2),
                            onElementPass(GT_MetaTileEntity_LargeBoiler::onCasingAdded, ofBlock(t.getCasingBlock(), t.getCasingMeta()))
                    )))
                    .addElement('f', lazy(t -> ofChain(
                            ofHatchAdder(GT_MetaTileEntity_LargeBoiler::addMaintenanceToMachineList, t.getFireboxTextureIndex(), 1),
                            ofHatchAdder(GT_MetaTileEntity_LargeBoiler::addInputToMachineList, t.getFireboxTextureIndex(), 1),
                            ofHatchAdder(GT_MetaTileEntity_LargeBoiler::addMufflerToMachineList, t.getFireboxTextureIndex(), 1),
                            onElementPass(GT_MetaTileEntity_LargeBoiler::onFireboxAdded, ofBlock(t.getFireboxBlock(), t.getFireboxMeta()))
                    )))
                    .build();
        }
    };
    private boolean firstRun = true;
    private int mSuperEfficencyIncrease = 0;
    private int integratedCircuitConfig = 0; //Steam output is reduced by 1000L per config
    private int excessWater = 0; //Eliminate rounding errors for water
    private int excessFuel = 0; //Eliminate rounding errors for fuels that burn half items
    private int excessProjectedEU = 0; //Eliminate rounding errors from throttling the boiler
    private int mCasingAmount;
    private int mFireboxAmount;
    protected int pollutionPerSecond = 1; //placeholder for the child classes

    public GT_MetaTileEntity_LargeBoiler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_LargeBoiler(String aName) {
        super(aName);
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Boiler")
                .addInfo("Controller block for the Large " + getCasingMaterial() + " Boiler")
                .addInfo("Produces " + (getEUt() * 40) * (runtimeBoost(20) / 20f) + "L of Steam with 1 Coal at " + getEUt() * 40 + "L/s")//?
                .addInfo("A programmed circuit in the main block throttles the boiler (-1000L/s per config)")
                .addInfo(String.format("Diesel fuels have 1/4 efficiency - Takes %.2f seconds to heat up", 500.0 / getEfficiencyIncrease()))//? check semifluid again
                .addPollutionAmount(getPollutionPerSecond(null))
                .addSeparator()
                .beginStructureBlock(3, 5, 3, false)
                .addController("Front bottom")
                .addCasingInfo(getCasingMaterial() + " " + getCasingBlockType() + " Casing", 24)//?
                .addOtherStructurePart(getCasingMaterial() + " Fire Boxes", "Bottom layer, 3 minimum")
                .addOtherStructurePart(getCasingMaterial() + " Pipe Casing Blocks", "Inner 3 blocks")
                .addMaintenanceHatch("Any firebox", 1)
                .addMufflerHatch("Any firebox", 1)
                .addInputBus("Solid fuel, Any firebox", 1)
                .addInputHatch("Liquid fuel, Any firebox", 1)
                .addStructureInfo("You can use either, or both")
                .addInputHatch("Water, Any firebox", 1)
                .addOutputHatch("Steam, any casing", 2)
                .toolTipFinisher("Gregtech");
        return tt;
    }

    public abstract String getCasingMaterial();

    public abstract Block getCasingBlock();

    public abstract String getCasingBlockType();

    public abstract byte getCasingMeta();

    public abstract byte getCasingTextureIndex();

    public abstract Block getPipeBlock();

    public abstract byte getPipeMeta();

    public abstract Block getFireboxBlock();

    public abstract byte getFireboxMeta();

    public abstract byte getFireboxTextureIndex();

    public abstract int getEUt();

    public abstract int getEfficiencyIncrease();

    public int getIntegratedCircuitConfig(){
        return integratedCircuitConfig;
    }

    @Override
    public int getPollutionPerSecond(ItemStack aStack) {
        //allows for 0 pollution if circuit throttle is too high
        return Math.max(0, (int) (pollutionPerSecond * (1-GT_Mod.gregtechproxy.mPollutionReleasedByThrottle*getIntegratedCircuitConfig())));
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            if (aActive) return new ITexture[]{
                    BlockIcons.getCasingTextureForId(getCasingTextureIndex()),
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_LARGE_BOILER_ACTIVE).extFacing().build(),
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_LARGE_BOILER_ACTIVE_GLOW).extFacing().glow().build()};
            return new ITexture[]{
                    BlockIcons.getCasingTextureForId(getCasingTextureIndex()),
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_LARGE_BOILER).extFacing().build(),
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_LARGE_BOILER_GLOW).extFacing().glow().build()};
        }
        return new ITexture[]{Textures.BlockIcons.getCasingTextureForId(getCasingTextureIndex())};
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "LargeBoiler.png");
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public boolean checkRecipe(ItemStack aStack) {
        //Do we have an integrated circuit with a valid configuration?
        if (mInventory[1] != null && mInventory[1].getUnlocalizedName().startsWith("gt.integrated_circuit")) {
            int circuit_config = mInventory[1].getItemDamage();
            if (circuit_config >= 1 && circuit_config <= 25) {
                // If so, overwrite the current config
                this.integratedCircuitConfig = circuit_config;
            }
        } else {
            //If not, set the config to zero
            this.integratedCircuitConfig = 0;
        }

        this.mSuperEfficencyIncrease = 0;
        for (GT_Recipe tRecipe : GT_Recipe.GT_Recipe_Map.sDieselFuels.mRecipeList) {
            FluidStack tFluid = GT_Utility.getFluidForFilledItem(tRecipe.getRepresentativeInput(0), true);
            if (tFluid != null && tRecipe.mSpecialValue > 1) {
                tFluid.amount = 1000;
                if (depleteInput(tFluid)) {
                    this.mMaxProgresstime = adjustBurnTimeForConfig(runtimeBoost(tRecipe.mSpecialValue / 2));
                    this.mEUt = adjustEUtForConfig(getEUt());
                    this.mEfficiencyIncrease = this.mMaxProgresstime * getEfficiencyIncrease() * 4;
                    return true;
                }
            }
        }
        for (GT_Recipe tRecipe : GT_Recipe.GT_Recipe_Map.sDenseLiquidFuels.mRecipeList) {
            FluidStack tFluid = GT_Utility.getFluidForFilledItem(tRecipe.getRepresentativeInput(0), true);
            if (tFluid != null) {
                tFluid.amount = 1000;
                if (depleteInput(tFluid)) {
                    this.mMaxProgresstime = adjustBurnTimeForConfig(Math.max(1, runtimeBoost(tRecipe.mSpecialValue * 2)));
                    this.mEUt = adjustEUtForConfig(getEUt());
                    this.mEfficiencyIncrease = this.mMaxProgresstime * getEfficiencyIncrease();
                    return true;
                }
            }
        }
        ArrayList<ItemStack> tInputList = getStoredInputs();
        if (!tInputList.isEmpty()) {
            for (ItemStack tInput : tInputList) {
                if (tInput != GT_OreDictUnificator.get(OrePrefixes.bucket, Materials.Lava, 1)){
                    if (GT_Utility.getFluidForFilledItem(tInput, true) == null && (this.mMaxProgresstime = GT_ModHandler.getFuelValue(tInput) / 80) > 0) {
                        this.excessFuel += GT_ModHandler.getFuelValue(tInput) % 80;
                        this.mMaxProgresstime += this.excessFuel / 80;
                        this.excessFuel %= 80;
                        this.mMaxProgresstime = adjustBurnTimeForConfig(runtimeBoost(this.mMaxProgresstime));
                        this.mEUt = adjustEUtForConfig(getEUt());
                        this.mEfficiencyIncrease = this.mMaxProgresstime * getEfficiencyIncrease();
                        this.mOutputItems = new ItemStack[]{GT_Utility.getContainerItem(tInput, true)};
                        tInput.stackSize -= 1;
                        updateSlots();
                        if (this.mEfficiencyIncrease > 5000) {
                            this.mEfficiencyIncrease = 0;
                            this.mSuperEfficencyIncrease = 20;
                        }
                        return true;
                    }
                }
            }
        }
        this.mMaxProgresstime = 0;
        this.mEUt = 0;
        return false;
    }

    abstract int runtimeBoost(int mTime);

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (this.mEUt > 0) {
            if (this.mSuperEfficencyIncrease > 0)
                mEfficiency = Math.max(0, Math.min(mEfficiency + mSuperEfficencyIncrease, getMaxEfficiency(mInventory[1]) - ((getIdealStatus() - getRepairStatus()) * 1000)));
            int tGeneratedEU = (int) (this.mEUt * 2L * this.mEfficiency / 10000L);
            if (tGeneratedEU > 0) {
                long amount = (tGeneratedEU + STEAM_PER_WATER) / STEAM_PER_WATER;
                excessWater += amount * STEAM_PER_WATER - tGeneratedEU;
                amount -= excessWater / STEAM_PER_WATER;
                excessWater %= STEAM_PER_WATER;
                if (depleteInput(Materials.Water.getFluid(amount)) || depleteInput(GT_ModHandler.getDistilledWater(amount))) {
                    addOutput(GT_ModHandler.getSteam(tGeneratedEU));
                } else {
                    GT_Log.exp.println("Boiler "+this.mName+" had no Water!");
                    explodeMultiblock();
                }
            }
            return true;
        }
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setInteger("excessFuel", excessFuel);
        aNBT.setInteger("excessWater", excessWater);
        aNBT.setInteger("excessProjectedEU", excessProjectedEU);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        excessFuel = aNBT.getInteger("excessFuel");
        excessWater = aNBT.getInteger("excessWater");
        excessProjectedEU = aNBT.getInteger("excessProjectedEU");
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (mProgresstime > 0 && firstRun) {
            firstRun = false;
            GT_Mod.achievements.issueAchievement(aBaseMetaTileEntity.getWorld().getPlayerEntityByName(aBaseMetaTileEntity.getOwnerName()), "extremepressure");
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public IStructureDefinition<GT_MetaTileEntity_LargeBoiler> getStructureDefinition() {
        return STRUCTURE_DEFINITION.get(getClass());
    }

    private void onCasingAdded() {
        mCasingAmount++;
    }

    private void onFireboxAdded() {
        mFireboxAmount++;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mCasingAmount = 0;
        mFireboxAmount = 0;
        return checkPiece(STRUCTURE_PIECE_MAIN, 1, 4, 0) && mCasingAmount >= 24 && mFireboxAmount >= 3 &&
                mMaintenanceHatches.size() == 1 && !mMufflerHatches.isEmpty();
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    @Override
    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return false;
    }

    private int adjustEUtForConfig(int rawEUt) {
        int adjustedSteamOutput = rawEUt - 25 * integratedCircuitConfig;
        return Math.max(adjustedSteamOutput, 25);
    }

    private int adjustBurnTimeForConfig(int rawBurnTime) {
        if (mEfficiency < getMaxEfficiency(mInventory[1]) - ((getIdealStatus() - getRepairStatus()) * 1000)) {
            return rawBurnTime;
        }
        int adjustedEUt = Math.max(25, getEUt() - 25 * integratedCircuitConfig);
        int adjustedBurnTime = rawBurnTime * getEUt() / adjustedEUt;
        this.excessProjectedEU += getEUt() * rawBurnTime - adjustedEUt * adjustedBurnTime;
        adjustedBurnTime += this.excessProjectedEU / adjustedEUt;
        this.excessProjectedEU %= adjustedEUt;
        return adjustedBurnTime;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 1, 4, 0);
    }
}
