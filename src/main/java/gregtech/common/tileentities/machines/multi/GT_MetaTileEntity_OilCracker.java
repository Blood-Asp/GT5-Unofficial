package gregtech.common.tileentities.machines.multi;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import gregtech.api.GregTech_API;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_EnhancedMultiBlockBase;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Input;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Output;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;
import static gregtech.api.util.GT_StructureUtility.ofCoil;
import static gregtech.api.util.GT_StructureUtility.ofHatchAdder;

public class GT_MetaTileEntity_OilCracker extends GT_MetaTileEntity_EnhancedMultiBlockBase<GT_MetaTileEntity_OilCracker> {
    private static final byte CASING_INDEX = 49;
    private static final String STRUCTURE_PIECE_MAIN = "main";
    private static final IStructureDefinition<GT_MetaTileEntity_OilCracker> STRUCTURE_DEFINITION = StructureDefinition.<GT_MetaTileEntity_OilCracker>builder()
            .addShape(STRUCTURE_PIECE_MAIN, transpose(new String[][]{
                    {"lcmcr", "lcmcr", "lcmcr"},
                    {"lc~cr", "l---r", "lcmcr"},
                    {"lcmcr", "lcmcr", "lcmcr"},
            }))
            .addElement('c', ofCoil(GT_MetaTileEntity_OilCracker::setCoilLevel, GT_MetaTileEntity_OilCracker::getCoilLevel))
            .addElement('l', ofChain(
                    ofHatchAdder(GT_MetaTileEntity_OilCracker::addLeftHatchToMachineList, CASING_INDEX, 2),
                    ofHatchAdder(GT_MetaTileEntity_OilCracker::addEnergyInputToMachineList, CASING_INDEX, 1),
                    ofHatchAdder(GT_MetaTileEntity_OilCracker::addMaintenanceToMachineList, CASING_INDEX, 1),
                    onElementPass(GT_MetaTileEntity_OilCracker::onCasingAdded, ofBlock(GregTech_API.sBlockCasings4, 1))
            ))
            .addElement('r', ofChain(
                    ofHatchAdder(GT_MetaTileEntity_OilCracker::addRightHatchToMachineList, CASING_INDEX, 3),
                    ofHatchAdder(GT_MetaTileEntity_OilCracker::addEnergyInputToMachineList, CASING_INDEX, 1),
                    ofHatchAdder(GT_MetaTileEntity_OilCracker::addMaintenanceToMachineList, CASING_INDEX, 1),
                    onElementPass(GT_MetaTileEntity_OilCracker::onCasingAdded, ofBlock(GregTech_API.sBlockCasings4, 1))
            ))
            .addElement('m', ofChain(
                    ofHatchAdder(GT_MetaTileEntity_OilCracker::addMiddleInputToMachineList, CASING_INDEX, 1),
                    ofHatchAdder(GT_MetaTileEntity_OilCracker::addEnergyInputToMachineList, CASING_INDEX, 1),
                    ofHatchAdder(GT_MetaTileEntity_OilCracker::addMaintenanceToMachineList, CASING_INDEX, 1),
                    onElementPass(GT_MetaTileEntity_OilCracker::onCasingAdded, ofBlock(GregTech_API.sBlockCasings4, 1))
            ))
            .build();
    private HeatingCoilLevel heatLevel;
    protected final List<GT_MetaTileEntity_Hatch_Input> mMiddleInputHatches = new ArrayList<>();
    // 0 -> left, 1 -> right, any other -> not found
    protected int mInputOnSide;
    protected int mOutputOnSide;
    protected int mCasingAmount;

    public GT_MetaTileEntity_OilCracker(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_OilCracker(String aName) {
        super(aName);
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Cracker")
                .addInfo("Controller block for the Oil Cracking Unit")
                .addInfo("Thermally cracks heavy hydrocarbons into lighter fractions")
                .addInfo("More efficient than the Chemical Reactor")
                .addInfo("Place the appropriate circuit in the controller")
                .addSeparator()
                .beginStructureBlock(5, 3, 3, true)
                .addController("Front center")
                .addCasingInfo("Clean Stainless Steel Machine Casing", 18)
                .addOtherStructurePart("2 Rings of 8 Coils", "Each side of the controller")
                .addInfo("Gets 5% EU/t reduction per coil tier")
                .addEnergyHatch("Any casing", 1)
                .addMaintenanceHatch("Any casing", 1)
                .addInputHatch("Steam/Hydrogen ONLY, Any middle ring casing", 1)
                .addInputHatch("Any left/right side casing",2, 3)
                .addOutputHatch("Any right/left side casing", 2, 3)
                .addStructureInfo("Input/Output Hatches must be on opposite sides!")
                .addStructureHint("GT5U.cracker.io_side")
                .toolTipFinisher("Gregtech");
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            if (aActive) return new ITexture[]{casingTexturePages[0][CASING_INDEX],
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_OIL_CRACKER_ACTIVE).extFacing().build(),
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_OIL_CRACKER_ACTIVE_GLOW).extFacing().glow().build()};
            return new ITexture[]{casingTexturePages[0][CASING_INDEX],
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_OIL_CRACKER).extFacing().build(),
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_OIL_CRACKER_GLOW).extFacing().glow().build()};
        }
        return new ITexture[]{casingTexturePages[0][CASING_INDEX]};
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "OilCrackingUnit.png");
    }

    @Override
    public GT_Recipe.GT_Recipe_Map getRecipeMap() {
        return GT_Recipe.GT_Recipe_Map.sCrackingRecipes;
    }

    @Override
    public boolean checkRecipe(ItemStack aStack) {
        ArrayList<FluidStack> tInputList = getStoredFluids();
        FluidStack[] tFluidInputs = tInputList.toArray(new FluidStack[0]);
        long tVoltage = getMaxInputVoltage();
        byte tTier = (byte) Math.max(1, GT_Utility.getTier(tVoltage));

        GT_Recipe tRecipe = getRecipeMap().findRecipe(
                getBaseMetaTileEntity(),
                false,
                gregtech.api.enums.GT_Values.V[tTier],
                tFluidInputs,
                mInventory[1]
        );

        if (tRecipe == null)
            return false;

        if (tRecipe.isRecipeInputEqual(true, tFluidInputs, mInventory[1])) {
            this.mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
            this.mEfficiencyIncrease = 10000;
            calculateOverclockedNessMulti(tRecipe.mEUt, tRecipe.mDuration, 1, tVoltage);
            //In case recipe is too OP for that machine
            if (mMaxProgresstime == Integer.MAX_VALUE - 1 && mEUt == Integer.MAX_VALUE - 1)
                return false;

            this.mEUt *= Math.pow(0.95D, this.heatLevel.getTier());

            if (this.mEUt > 0)
                this.mEUt = (-this.mEUt);

            this.mOutputFluids = new FluidStack[]{tRecipe.getFluidOutput(0)};
            return true;
        }
        return false;
    }

    @Override
    public IStructureDefinition<GT_MetaTileEntity_OilCracker> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    public HeatingCoilLevel getCoilLevel() {
        return heatLevel;
    }

    public void setCoilLevel(HeatingCoilLevel aCoilLevel) {
        heatLevel = aCoilLevel;
    }

    private boolean addMiddleInputToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Input) {
            GT_MetaTileEntity_Hatch_Input tHatch = (GT_MetaTileEntity_Hatch_Input) aMetaTileEntity;
            tHatch.updateTexture(aBaseCasingIndex);
            tHatch.mRecipeMap = getRecipeMap();
            return mMiddleInputHatches.add(tHatch);
        }
        return false;
    }

    private boolean addLeftHatchToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Input) {
            if (mInputOnSide == 1)
                return false;
            mInputOnSide = 0;
            mOutputOnSide = 1;
            GT_MetaTileEntity_Hatch_Input tHatch = (GT_MetaTileEntity_Hatch_Input) aMetaTileEntity;
            tHatch.updateTexture(aBaseCasingIndex);
            tHatch.mRecipeMap = getRecipeMap();
            return mInputHatches.add(tHatch);
        }
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Output) {
            if (mOutputOnSide == 1)
                return false;
            mInputOnSide = 1;
            mOutputOnSide = 0;
            GT_MetaTileEntity_Hatch_Output tHatch = (GT_MetaTileEntity_Hatch_Output) aMetaTileEntity;
            tHatch.updateTexture(aBaseCasingIndex);
            return mOutputHatches.add(tHatch);
        }
        return false;
    }

    private boolean addRightHatchToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Input) {
            if (mInputOnSide == 0)
                return false;
            mInputOnSide = 1;
            mOutputOnSide = 0;
            GT_MetaTileEntity_Hatch_Input tHatch = (GT_MetaTileEntity_Hatch_Input) aMetaTileEntity;
            tHatch.updateTexture(aBaseCasingIndex);
            tHatch.mRecipeMap = getRecipeMap();
            return mInputHatches.add(tHatch);
        }
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Output) {
            if (mOutputOnSide == 0)
                return false;
            mInputOnSide = 0;
            mOutputOnSide = 1;
            GT_MetaTileEntity_Hatch_Output tHatch = (GT_MetaTileEntity_Hatch_Output) aMetaTileEntity;
            tHatch.updateTexture(aBaseCasingIndex);
            return mOutputHatches.add(tHatch);
        }
        return false;
    }

    private void onCasingAdded() {
        mCasingAmount++;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        setCoilLevel(HeatingCoilLevel.None);
        mCasingAmount = 0;
        mMiddleInputHatches.clear();
        mInputOnSide = -1;
        mOutputOnSide = -1;
        replaceDeprecatedCoils(aBaseMetaTileEntity);
        return checkPiece(STRUCTURE_PIECE_MAIN, 2, 1, 0) &&
                mInputOnSide != -1 && mOutputOnSide != -1 && mCasingAmount >= 18 &&
                mMaintenanceHatches.size() == 1 && !mMiddleInputHatches.isEmpty();
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
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
        return new GT_MetaTileEntity_OilCracker(this.mName);
    }

    private void replaceDeprecatedCoils(IGregTechTileEntity aBaseMetaTileEntity) {
        int xDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetX;
        int zDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetZ;
        int tX = aBaseMetaTileEntity.getXCoord() + xDir;
        int tY = aBaseMetaTileEntity.getYCoord();
        int tZ = aBaseMetaTileEntity.getZCoord() + zDir;
        for (int xPos = tX - 1; xPos <= tX + 1; xPos += (xDir != 0 ? 1 : 2))
            for (int yPos = tY - 1; yPos <= tY + 1; yPos++)
                for (int zPos = tZ - 1; zPos <= tZ + 1; zPos += (xDir != 0 ? 2 : 1)) {
                    if ((yPos == tY) && (xPos == tX || zPos == tZ))
                        continue;
                    byte tUsedMeta = aBaseMetaTileEntity.getMetaID(xPos, yPos, zPos);
                    if (tUsedMeta < 12)
                        continue;
                    if (tUsedMeta > 14)
                        continue;
                    if (aBaseMetaTileEntity.getBlock(xPos, yPos, zPos) != GregTech_API.sBlockCasings1)
                        continue;

                    aBaseMetaTileEntity.getWorld().setBlock(
                            xPos,
                            yPos,
                            zPos,
                            GregTech_API.sBlockCasings5,
                            tUsedMeta - 12,
                            3
                    );
                }
    }

    @Override
    public ArrayList<FluidStack> getStoredFluids() {
        ArrayList<FluidStack> rList = new ArrayList<>();
        for (GT_MetaTileEntity_Hatch_Input tHatch : mInputHatches) {
            tHatch.mRecipeMap = getRecipeMap();
            if (isValidMetaTileEntity(tHatch) && tHatch.getFillableStack() != null) {
                if (!GT_Recipe.GT_Recipe_Map.sCrackingRecipes.isValidCatalystFluid(tHatch.getFillableStack()))
                    rList.add(tHatch.getFillableStack());
            }
        }
        for (GT_MetaTileEntity_Hatch_Input tHatch : mMiddleInputHatches) {
            tHatch.mRecipeMap = getRecipeMap();
            if (isValidMetaTileEntity(tHatch) && tHatch.getFillableStack() != null) {
                FluidStack tStack = tHatch.getFillableStack();
                if (GT_Recipe.GT_Recipe_Map.sCrackingRecipes.isValidCatalystFluid(tStack)) {
                    rList.add(tStack);
                }
            }
        }
        return rList;
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_MAIN, stackSize, hintsOnly, 2, 1, 0);
    }
}
