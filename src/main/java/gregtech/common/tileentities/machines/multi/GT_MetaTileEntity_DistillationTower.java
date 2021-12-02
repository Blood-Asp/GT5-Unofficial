package gregtech.common.tileentities.machines.multi;

import com.gtnewhorizon.structurelib.alignment.IAlignmentLimits;
import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.enums.Textures.BlockIcons;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_EnhancedMultiBlockBase;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Output;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.isAir;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_DISTILLATION_TOWER_GLOW;
import static gregtech.api.util.GT_StructureUtility.ofHatchAdder;

public class GT_MetaTileEntity_DistillationTower extends GT_MetaTileEntity_EnhancedMultiBlockBase<GT_MetaTileEntity_DistillationTower> {
    protected static final int CASING_INDEX = 49;
    protected static final String STRUCTURE_PIECE_BASE = "base";
    protected static final String STRUCTURE_PIECE_LAYER = "layer";
    protected static final String STRUCTURE_PIECE_LAYER_HINT = "layerHint";
    protected static final String STRUCTURE_PIECE_TOP_HINT = "topHint";
    private static final IStructureDefinition<GT_MetaTileEntity_DistillationTower> STRUCTURE_DEFINITION = StructureDefinition.<GT_MetaTileEntity_DistillationTower>builder()
            .addShape(STRUCTURE_PIECE_BASE, transpose(new String[][]{
                    {"b~b", "bbb", "bbb"},
            }))
            .addShape(STRUCTURE_PIECE_LAYER, transpose(new String[][]{
                    {"lll", "lcl", "lll"}
            }))
            .addShape(STRUCTURE_PIECE_LAYER_HINT, transpose(new String[][]{
                    {"lll", "l-l", "lll"}
            }))
            .addShape(STRUCTURE_PIECE_TOP_HINT, transpose(new String[][]{
                    {"lll", "lll", "lll"}
            }))
            .addElement('b', ofChain(
                    ofHatchAdder(GT_MetaTileEntity_DistillationTower::addEnergyInputToMachineList, CASING_INDEX, 1),
                    ofHatchAdder(GT_MetaTileEntity_DistillationTower::addOutputToMachineList, CASING_INDEX, 1),
                    ofHatchAdder(GT_MetaTileEntity_DistillationTower::addInputToMachineList, CASING_INDEX, 1),
                    ofHatchAdder(GT_MetaTileEntity_DistillationTower::addMaintenanceToMachineList, CASING_INDEX, 1),
                    onElementPass(GT_MetaTileEntity_DistillationTower::onCasingFound, ofBlock(GregTech_API.sBlockCasings4, 1))
            ))
            .addElement('l', ofChain(
                    ofHatchAdder(GT_MetaTileEntity_DistillationTower::addEnergyInputToMachineList, CASING_INDEX, 2),
                    ofHatchAdder(GT_MetaTileEntity_DistillationTower::addLayerOutputHatch, CASING_INDEX, 2),
                    ofHatchAdder(GT_MetaTileEntity_DistillationTower::addMaintenanceToMachineList, CASING_INDEX, 2),
                    onElementPass(GT_MetaTileEntity_DistillationTower::onCasingFound, ofBlock(GregTech_API.sBlockCasings4, 1))
            ))
            .addElement('c', ofChain(
                    onElementPass(t -> t.onTopLayerFound(false), ofHatchAdder(GT_MetaTileEntity_DistillationTower::addOutputToMachineList, CASING_INDEX, 3)),
                    onElementPass(t -> t.onTopLayerFound(false), ofHatchAdder(GT_MetaTileEntity_DistillationTower::addMaintenanceToMachineList, CASING_INDEX, 3)),
                    onElementPass(t -> t.onTopLayerFound(true), ofBlock(GregTech_API.sBlockCasings4, 1)),
                    isAir()
            ))
            .build();
    protected final List<List<GT_MetaTileEntity_Hatch_Output>> mOutputHatchesByLayer = new ArrayList<>();
    protected int mHeight;
    protected int mCasing;
    protected boolean mTopLayerFound;

    public GT_MetaTileEntity_DistillationTower(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_DistillationTower(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_DistillationTower(this.mName);
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Distillery")
                .addInfo("Controller block for the Distillation Tower")
                .addInfo("Fluids are only put out at the correct height")
                .addInfo("The correct height equals the slot number in the NEI recipe")
                .addSeparator()
                .beginVariableStructureBlock(3, 3, 3, 12, 3, 3, true)
                .addController("Front bottom")
                .addOtherStructurePart("Clean Stainless Steel Machine Casing", "7 x h - 5 (minimum)")
                .addEnergyHatch("Any casing", 1, 2)
                .addMaintenanceHatch("Any casing", 1, 2, 3)
                .addInputHatch("Any bottom layer casing", 1)
                .addOutputBus("Any bottom layer casing", 1)
                .addOutputHatch("2-11x Output Hatches (At least one per layer except bottom layer)", 2, 3)
                .toolTipFinisher("Gregtech");
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            if (aActive)
                return new ITexture[]{
                        BlockIcons.getCasingTextureForId(CASING_INDEX),
                        TextureFactory.builder().addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE).extFacing().build(),
                        TextureFactory.builder().addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_ACTIVE_GLOW).extFacing().glow().build()};
            return new ITexture[]{
                    BlockIcons.getCasingTextureForId(CASING_INDEX),
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_DISTILLATION_TOWER).extFacing().build(),
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_DISTILLATION_TOWER_GLOW).extFacing().glow().build()};
        }
        return new ITexture[]{Textures.BlockIcons.getCasingTextureForId(CASING_INDEX)};
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "DistillationTower.png");
    }

    @Override
    public GT_Recipe.GT_Recipe_Map getRecipeMap() {
        return GT_Recipe.GT_Recipe_Map.sDistillationRecipes;
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public boolean checkRecipe(ItemStack aStack) {
        ArrayList<FluidStack> tFluidList = getStoredFluids();
        for (int i = 0; i < tFluidList.size() - 1; i++) {
            for (int j = i + 1; j < tFluidList.size(); j++) {
                if (GT_Utility.areFluidsEqual(tFluidList.get(i), tFluidList.get(j))) {
                    if (tFluidList.get(i).amount >= tFluidList.get(j).amount) {
                        tFluidList.remove(j--);
                    } else {
                        tFluidList.remove(i--);
                        break;
                    }
                }
            }
        }

        long tVoltage = getMaxInputVoltage();
        byte tTier = (byte) Math.max(0, GT_Utility.getTier(tVoltage));
        FluidStack[] tFluids = tFluidList.toArray(new FluidStack[0]);
        if (tFluids.length > 0) {
            for (FluidStack tFluid : tFluids) {
                GT_Recipe tRecipe = GT_Recipe.GT_Recipe_Map.sDistillationRecipes.findRecipe(getBaseMetaTileEntity(), false, gregtech.api.enums.GT_Values.V[tTier], new FluidStack[]{tFluid});
                if (tRecipe != null) {
                    if (tRecipe.isRecipeInputEqual(true, tFluids)) {
                        this.mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
                        this.mEfficiencyIncrease = 10000;
                        calculateOverclockedNessMulti(tRecipe.mEUt, tRecipe.mDuration, 1, tVoltage);
                        //In case recipe is too OP for that machine
                        if (mMaxProgresstime == Integer.MAX_VALUE - 1 && mEUt == Integer.MAX_VALUE - 1)
                            return false;
                        if (this.mEUt > 0) {
                            this.mEUt = (-this.mEUt);
                        }
                        this.mMaxProgresstime = Math.max(1, this.mMaxProgresstime);
                        this.mOutputItems = new ItemStack[]{tRecipe.getOutput(0)};
                        this.mOutputFluids = tRecipe.mFluidOutputs.clone();
                        updateSlots();
                        return true;
                    }
                }
            }
        }

        return false;
    }

    protected void onCasingFound() {
        mCasing++;
    }

    protected void onTopLayerFound(boolean aIsCasing) {
        mTopLayerFound = true;
        if (aIsCasing)
            onCasingFound();
    }

    protected boolean addLayerOutputHatch(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null || aTileEntity.isDead() || !(aTileEntity.getMetaTileEntity() instanceof GT_MetaTileEntity_Hatch_Output))
            return false;
        while (mOutputHatchesByLayer.size() < mHeight)
            mOutputHatchesByLayer.add(new ArrayList<>());
        GT_MetaTileEntity_Hatch_Output tHatch = (GT_MetaTileEntity_Hatch_Output) aTileEntity.getMetaTileEntity();
        tHatch.updateTexture(aBaseCasingIndex);
        return mOutputHatchesByLayer.get(mHeight - 1).add(tHatch);
    }

    @Override
    protected IAlignmentLimits getInitialAlignmentLimits() {
        // don't rotate a freaking tower, it won't work
        return (d, r, f) -> d.offsetY == 0 && r.isNotRotated() && !f.isVerticallyFliped();
    }

    @Override
    public IStructureDefinition<GT_MetaTileEntity_DistillationTower> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        // reset
        mOutputHatchesByLayer.forEach(List::clear);
        mHeight = 1;
        mTopLayerFound = false;
        mCasing = 0;

        // check base
        if (!checkPiece(STRUCTURE_PIECE_BASE, 1, 0, 0))
            return false;

        // check each layer
        while (mHeight < 12 && checkPiece(STRUCTURE_PIECE_LAYER, 1, mHeight, 0) && !mTopLayerFound) {
            if (mOutputHatchesByLayer.get(mHeight - 1).isEmpty())
                // layer without output hatch
                return false;
            // not top
            mHeight++;
        }

        // validate final invariants...
        return mCasing >= 7 * mHeight - 5 && mHeight >= 2 && mTopLayerFound && mMaintenanceHatches.size() == 1;
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
    protected void addFluidOutputs(FluidStack[] mOutputFluids2) {
        for (int i = 0; i < mOutputFluids2.length && i < mOutputHatchesByLayer.size(); i++) {
            FluidStack tStack = mOutputFluids2[i].copy();
            if (!dumpFluid(mOutputHatchesByLayer.get(i), tStack, true))
                dumpFluid(mOutputHatchesByLayer.get(i), tStack, false);
        }
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece(STRUCTURE_PIECE_BASE, stackSize, hintsOnly, 1, 0, 0);
        int tTotalHeight = Math.min(12, stackSize.stackSize + 2); // min 2 output layer, so at least 1 + 2 height
        for (int i = 1; i < tTotalHeight - 1; i++) {
            buildPiece(STRUCTURE_PIECE_LAYER_HINT, stackSize, hintsOnly, 1, i, 0);
        }
        buildPiece(STRUCTURE_PIECE_TOP_HINT, stackSize, hintsOnly, 1, tTotalHeight - 1, 0);
    }
}
