package gregtech.common.tileentities.machines.multi;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import gregtech.api.GregTech_API;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_EnhancedMultiBlockBase;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Input;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Output;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Log;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Utility;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_HEAT_EXCHANGER;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_HEAT_EXCHANGER_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_HEAT_EXCHANGER_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_HEAT_EXCHANGER_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
import static gregtech.api.util.GT_StructureUtility.ofHatchAdder;

public class GT_MetaTileEntity_HeatExchanger extends GT_MetaTileEntity_EnhancedMultiBlockBase<GT_MetaTileEntity_HeatExchanger> {
    private static final int CASING_INDEX = 50;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final IStructureDefinition<GT_MetaTileEntity_HeatExchanger> STRUCTURE_DEFINITION = StructureDefinition.<GT_MetaTileEntity_HeatExchanger>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(new String[][]{
                    {"ccc", "cCc", "ccc"},
                    {"ccc", "cPc", "ccc"},
                    {"ccc", "cPc", "ccc"},
                    {"c~c", "cHc", "ccc"},
            }))
            .addElement('P', ofBlock(GregTech_API.sBlockCasings2, 14))
            .addElement('C', ofHatchAdder(GT_MetaTileEntity_HeatExchanger::addColdFluidOutputToMachineList, CASING_INDEX, 2))
            .addElement('H', ofHatchAdder(GT_MetaTileEntity_HeatExchanger::addHotFluidInputToMachineList, CASING_INDEX, 3))
            .addElement('c', ofChain(
                    ofHatchAdder(GT_MetaTileEntity_HeatExchanger::addInputToMachineList, CASING_INDEX, 1),
                    ofHatchAdder(GT_MetaTileEntity_HeatExchanger::addOutputToMachineList, CASING_INDEX, 1),
                    ofHatchAdder(GT_MetaTileEntity_HeatExchanger::addMaintenanceToMachineList, CASING_INDEX, 1),
                    onElementPass(GT_MetaTileEntity_HeatExchanger::onCasingAdded, ofBlock(GregTech_API.sBlockCasings4, (byte) 2))
            ))
            .build();
    public static float penalty_per_config = 0.015f;  // penalize 1.5% efficiency per circuitry level (1-25)

    private GT_MetaTileEntity_Hatch_Input mInputHotFluidHatch;
    private GT_MetaTileEntity_Hatch_Output mOutputColdFluidHatch;
    private boolean superheated = false;
    private int superheated_threshold=0;
    private float water;
    private int mCasingAmount;

    public GT_MetaTileEntity_HeatExchanger(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }
    public GT_MetaTileEntity_HeatExchanger(String aName) {
        super(aName);
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Heat Exchanger")
                .addInfo("Controller Block for the Large Heat Exchanger")
                .addInfo("More complicated than a Fusion Reactor. Seriously")
                .addInfo("Inputs are Hot Coolant or Lava")
                .addInfo("Outputs Coolant or Pahoehoe Lava and SH Steam/Steam")
                .addInfo("Read the wiki article to understand how it works")
                .addInfo("Then go to the Discord to understand the wiki")
                .addSeparator()
                .beginStructureBlock(3, 4, 3, false)
                .addController("Front bottom")
                .addCasingInfo("Stable Titanium Machine Casing", 20)
                .addOtherStructurePart("Titanium Pipe Casing", "Center 2 blocks")
                .addMaintenanceHatch("Any casing", 1)
                .addInputHatch("Hot fluid, bottom center", 2)
                .addInputHatch("Distilled water, any casing", 1)
                .addOutputHatch("Cold fluid, top center", 3)
                .addOutputHatch("Steam/SH Steam, any casing", 1)
                .toolTipFinisher("Gregtech");
        return tt;
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        superheated = aNBT.getBoolean("superheated");
        super.loadNBTData(aNBT);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setBoolean("superheated", superheated);
        super.saveNBTData(aNBT);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            if (aActive)
                return new ITexture[]{
                        casingTexturePages[0][CASING_INDEX],
                        TextureFactory.builder().addIcon(OVERLAY_FRONT_HEAT_EXCHANGER_ACTIVE).extFacing().build(),
                        TextureFactory.builder().addIcon(OVERLAY_FRONT_HEAT_EXCHANGER_ACTIVE_GLOW).extFacing().glow().build()};
            return new ITexture[]{
                    casingTexturePages[0][CASING_INDEX],
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_HEAT_EXCHANGER).extFacing().build(),
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_HEAT_EXCHANGER_GLOW).extFacing().glow().build()};
        }
        return new ITexture[]{casingTexturePages[0][CASING_INDEX]};
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "LargeHeatExchanger.png");
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    protected IAlignmentLimits getInitialAlignmentLimits() {
        return (d, r, f) -> !r.isUpsideDown() && !f.isVerticallyFliped();
    }

    @Override
    public boolean checkRecipe(ItemStack aStack) {
        if (mInputHotFluidHatch.getFluid() == null)
            return true;

        int fluidAmountToConsume = mInputHotFluidHatch.getFluidAmount(); // how much fluid is in hatch

        superheated_threshold = 4000;   // default: must have 4000L per second to generate superheated steam
        float efficiency = 1f;              // default: operate at 100% efficiency with no integrated circuitry
        int shs_reduction_per_config = 150; // reduce threshold 150L/s per circuitry level (1-25)
        float steam_output_multiplier = 20f; // default: multiply output by 4 * 10 (boosted x5)
        float penalty = 0.0f;               // penalty to apply to output based on circuitry level (1-25).
        boolean do_lava = false;

        // Do we have an integrated circuit with a valid configuration?
        if (mInventory[1] != null && mInventory[1].getUnlocalizedName().startsWith("gt.integrated_circuit")) {
            int circuit_config = mInventory[1].getItemDamage();
            if (circuit_config >= 1 && circuit_config <= 25) {
                // If so, apply the penalty and reduced threshold.
                penalty = (circuit_config - 1) * penalty_per_config;
                superheated_threshold -= (shs_reduction_per_config * (circuit_config - 1));
            }
        }
        efficiency -= penalty;

        // If we're working with lava, adjust the threshold and multipliers accordingly.
        if (GT_ModHandler.isLava(mInputHotFluidHatch.getFluid())) {
            steam_output_multiplier /= 5f; // lava is not boosted
            superheated_threshold /= 4f; // unchanged
            do_lava = true;
        } else if (mInputHotFluidHatch.getFluid().isFluidEqual(FluidRegistry.getFluidStack("ic2hotcoolant", 1))) {
            steam_output_multiplier /= 2f; // was boosted x2 on top of x5 -> total x10 -> nerf with this code back to 5x
            superheated_threshold /=5f; // 10x smaller since the Hot Things production in reactor is the same.
        } else {
            // If we're working with neither, fail out
            superheated_threshold=0;
            return false;
        }

        superheated = fluidAmountToConsume >= superheated_threshold; // set the internal superheated flag if we have enough hot fluid.  Used in the onRunningTick method.
        fluidAmountToConsume = Math.min(fluidAmountToConsume, superheated_threshold * 2); // Don't consume too much hot fluid per second
        mInputHotFluidHatch.drain(fluidAmountToConsume, true);
        this.mMaxProgresstime = 20;
        this.mEUt = (int) (fluidAmountToConsume * steam_output_multiplier * efficiency);
        if (do_lava) {
            mOutputColdFluidHatch.fill(FluidRegistry.getFluidStack("ic2pahoehoelava", fluidAmountToConsume), true);
        } else {
            mOutputColdFluidHatch.fill(FluidRegistry.getFluidStack("ic2coolant", fluidAmountToConsume), true);
        }
        this.mEfficiencyIncrease = 80;
        return true;
    }

    private int useWater(float input) {
        water = water + input;
        int usage = (int) water;
        water = water - usage;
        return usage;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        if (this.mEUt > 0) {
            int tGeneratedEU = (int) (this.mEUt * 2L * this.mEfficiency / 10000L); // APPROXIMATELY how much steam to generate.
            if (tGeneratedEU > 0) {

                if (superheated) tGeneratedEU /= 2; // We produce half as much superheated steam if necessary

                int distilledConsumed = useWater(tGeneratedEU / 160f); // how much distilled water to consume
                //tGeneratedEU = distilledConsumed * 160; // EXACTLY how much steam to generate, producing a perfect 1:160 ratio with distilled water consumption

                FluidStack distilledStack = GT_ModHandler.getDistilledWater(distilledConsumed);
                if (depleteInput(distilledStack)) // Consume the distilled water
                {
                    if (superheated) {
                        addOutput(FluidRegistry.getFluidStack("ic2superheatedsteam", tGeneratedEU)); // Generate superheated steam
                    } else {
                        addOutput(GT_ModHandler.getSteam(tGeneratedEU)); // Generate regular steam
                    }
                } else {
                    GT_Log.exp.println(this.mName+" had no more Distilled water!");
                    explodeMultiblock(); // Generate crater
                }
            }
            return true;
        }
        return true;
    }

    @Override
    public IStructureDefinition<GT_MetaTileEntity_HeatExchanger> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    private void onCasingAdded() {
        mCasingAmount++;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mOutputColdFluidHatch = null;
        mInputHotFluidHatch = null;
        mCasingAmount = 0;
        return checkPiece(STRUCTURE_PIECE_MAIN, 1, 3, 0) && mCasingAmount >= 20 && mMaintenanceHatches.size() == 1;
    }

    public boolean addColdFluidOutputToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Output) {
            ((GT_MetaTileEntity_Hatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
            mOutputColdFluidHatch = (GT_MetaTileEntity_Hatch_Output) aMetaTileEntity;
            return true;
        }
        return false;
    }

    public boolean addHotFluidInputToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Input) {
            ((GT_MetaTileEntity_Hatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
            ((GT_MetaTileEntity_Hatch_Input) aMetaTileEntity).mRecipeMap = getRecipeMap();
            mInputHotFluidHatch = (GT_MetaTileEntity_Hatch_Input) aMetaTileEntity;
            return true;
        }
        return false;
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

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_HeatExchanger(this.mName);
    }

    @Override
    public boolean isGivingInformation() {
        return super.isGivingInformation();
    }

    @Override
    public String[] getInfoData() {
        return new String[]{
                StatCollector.translateToLocal("GT5U.multiblock.Progress") + ": " +
                        EnumChatFormatting.GREEN + GT_Utility.formatNumbers(mProgresstime / 20) + EnumChatFormatting.RESET + " s / " +
                        EnumChatFormatting.YELLOW + GT_Utility.formatNumbers(mMaxProgresstime / 20) + EnumChatFormatting.RESET + " s",
                StatCollector.translateToLocal("GT5U.multiblock.usage") + " " + StatCollector.translateToLocal("GT5U.LHE.steam") + ": " +
                        (superheated ? EnumChatFormatting.RED : EnumChatFormatting.YELLOW) + GT_Utility.formatNumbers(superheated ? -2 * mEUt : -mEUt) + EnumChatFormatting.RESET + " EU/t",
                StatCollector.translateToLocal("GT5U.multiblock.problems") + ": " +
                        EnumChatFormatting.RED + (getIdealStatus() - getRepairStatus()) + EnumChatFormatting.RESET + " " +
                        StatCollector.translateToLocal("GT5U.multiblock.efficiency") + ": " +
                        EnumChatFormatting.YELLOW + mEfficiency / 100.0F + EnumChatFormatting.RESET + " %",
                StatCollector.translateToLocal("GT5U.LHE.superheated") + ": " +
                        (superheated ? EnumChatFormatting.RED : EnumChatFormatting.BLUE) + superheated + EnumChatFormatting.RESET,
                StatCollector.translateToLocal("GT5U.LHE.superheated") + " " + StatCollector.translateToLocal("GT5U.LHE.threshold") + ": " +
                        EnumChatFormatting.GREEN + GT_Utility.formatNumbers(superheated_threshold) + EnumChatFormatting.RESET
        };
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 1, 3, 0);
    }
}
