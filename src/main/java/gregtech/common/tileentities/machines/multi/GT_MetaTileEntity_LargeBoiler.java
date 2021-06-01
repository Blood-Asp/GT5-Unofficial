package gregtech.common.tileentities.machines.multi;

import gregtech.GT_Mod;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.enums.Textures.BlockIcons;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
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
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static gregtech.api.enums.GT_Values.STEAM_PER_WATER;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER_GLOW;

public abstract class GT_MetaTileEntity_LargeBoiler extends GT_MetaTileEntity_MultiBlockBase {
    private boolean firstRun = true;
    private int mSuperEfficencyIncrease = 0;
    private int integratedCircuitConfig = 0; //Steam output is reduced by 1000L per config
    private int excessWater = 0; //Eliminate rounding errors for water
    private int excessFuel = 0; //Eliminate rounding errors for fuels that burn half items
    private int excessProjectedEU = 0; //Eliminate rounding errors from throttling the boiler

    public GT_MetaTileEntity_LargeBoiler(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_LargeBoiler(String aName) {
        super(aName);
    }

    @Override
    public String[] getDescription() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Boiler")
                .addInfo("Controller block for the Large " + getCasingMaterial() + " Boiler")
                .addInfo("Produces " + (getEUt() * 40) * (runtimeBoost(20) / 20f) + "L of Steam with 1 Coal at " + getEUt() * 40 + "L/s")//?
                .addInfo("A programmed circuit in the main block throttles the boiler (-1000L/s per config)")
                .addInfo(String.format("Diesel fuels have 1/4 efficiency - Takes %.2f seconds to heat up", 500.0 / getEfficiencyIncrease()))//? check semifluid again
                .addPollutionAmount(20 * getPollutionPerTick(null))
                .addSeparator()
                .beginStructureBlock(3, 5, 3, false)
                .addController("Front bottom")
                .addCasingInfo(getCasingMaterial() + " " + getCasingBlockType() + " Casing", 24)//?
                .addOtherStructurePart(getCasingMaterial() + " Fire Boxes", "Bottom layer, 3 minimum")
                .addOtherStructurePart(getCasingMaterial() + " Pipe Casing Blocks", "Inner 3 blocks")
                .addMaintenanceHatch("Any firebox")
                .addMufflerHatch("Any firebox")
                .addInputBus("Solid fuel, Any firebox")
                .addInputHatch("Liquid fuel, Any firebox")
                .addStructureInfo("You can use either, or both")
                .addInputHatch("Water, Any firebox")
                .addOutputHatch("Steam, any casing")
                .toolTipFinisher("Gregtech");
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            return tt.getStructureInformation();
        } else {
            return tt.getInformation();
        }
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

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            if (aActive) return new ITexture[]{
                    BlockIcons.getCasingTextureForId(getCasingTextureIndex()),
                    TextureFactory.of(OVERLAY_FRONT_LARGE_BOILER_ACTIVE),
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_LARGE_BOILER_ACTIVE_GLOW).glow().build()};
            return new ITexture[]{
                    BlockIcons.getCasingTextureForId(getCasingTextureIndex()),
                    TextureFactory.of(OVERLAY_FRONT_LARGE_BOILER),
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_LARGE_BOILER_GLOW).glow().build()};
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
    public boolean isFacingValid(byte aFacing) {
        return aFacing > 1;
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
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        int xDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetX;
        int zDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetZ;

        int tCasingAmount = 0;
        int tFireboxAmount = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (i != 0 || j != 0) {
                    for (int k = 1; k <= 4; k++) {
                        if (!addOutputToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir + i, k, zDir + j), getCasingTextureIndex())) {
                            if (aBaseMetaTileEntity.getBlockOffset(xDir + i, k, zDir + j) != getCasingBlock()) {
                                return false;
                            }
                            if (aBaseMetaTileEntity.getMetaIDOffset(xDir + i, k, zDir + j) != getCasingMeta()) {
                                return false;
                            }
                            tCasingAmount++;
                        }
                    }
                } else {
                    for (int k = 1; k <= 3; k++) {
                        if (aBaseMetaTileEntity.getBlockOffset(xDir + i, k, zDir + j) != getPipeBlock()) {
                            return false;
                        }
                        if (aBaseMetaTileEntity.getMetaIDOffset(xDir + i, k, zDir + j) != getPipeMeta()) {
                            return false;
                        }
                    }
                    if (!addOutputToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir + i, 4, zDir + j), getCasingTextureIndex())) {
                        if (aBaseMetaTileEntity.getBlockOffset(xDir + i, 4, zDir + j) != getCasingBlock()) {
                            return false;
                        }
                        if (aBaseMetaTileEntity.getMetaIDOffset(xDir + i, 4, zDir + j) != getCasingMeta()) {
                            return false;
                        }
                        tCasingAmount++;
                    }
                }
            }
        }
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (xDir + i != 0 || zDir + j != 0) {
                    IGregTechTileEntity tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir + i, 0, zDir + j);
                    if (!addMaintenanceToMachineList(tTileEntity, getFireboxTextureIndex()) && !addInputToMachineList(tTileEntity, getFireboxTextureIndex()) && !addMufflerToMachineList(tTileEntity, getFireboxTextureIndex())) {
                        if (aBaseMetaTileEntity.getBlockOffset(xDir + i, 0, zDir + j) != getFireboxBlock()) {
                            return false;
                        }
                        if (aBaseMetaTileEntity.getMetaIDOffset(xDir + i, 0, zDir + j) != getFireboxMeta()) {
                            return false;
                        }
                        tFireboxAmount++;
                    }
                }
            }
        }
        return tCasingAmount >= 24 && tFireboxAmount >= 3;
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    @Override
    public int getPollutionPerTick(ItemStack aStack) {
        int adjustedEUOutput = Math.max(25, getEUt() - 25 * integratedCircuitConfig);
        return Math.max(1, 12 * adjustedEUOutput / getEUt());
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
}
