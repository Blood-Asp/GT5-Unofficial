package gregtech.common.tileentities.machines.multi;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import cpw.mods.fml.common.Loader;
import gregtech.api.GregTech_API;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Textures;
import gregtech.api.enums.Textures.BlockIcons;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_EnhancedMultiBlockBase;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlock;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofBlockUnlocalizedName;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.enums.GT_Values.MOD_ID_DC;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_PYROLYSE_OVEN;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_PYROLYSE_OVEN_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_PYROLYSE_OVEN_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_PYROLYSE_OVEN_GLOW;
import static gregtech.api.util.GT_StructureUtility.ofCoil;
import static gregtech.api.util.GT_StructureUtility.ofHatchAdder;

public class GT_MetaTileEntity_PyrolyseOven extends GT_MetaTileEntity_EnhancedMultiBlockBase<GT_MetaTileEntity_PyrolyseOven> {

    private HeatingCoilLevel coilHeat;
    //public static GT_CopiedBlockTexture mTextureULV = new GT_CopiedBlockTexture(Block.getBlockFromItem(ItemList.Casing_ULV.get(1).getItem()), 6, 0, Dyes.MACHINE_METAL.mRGBa);
    private static final int CASING_INDEX = 1090;
    private static final IStructureDefinition<GT_MetaTileEntity_PyrolyseOven> STRUCTURE_DEFINITION = createStructureDefinition();

    private static IStructureDefinition<GT_MetaTileEntity_PyrolyseOven> createStructureDefinition() {
        IStructureElement<GT_MetaTileEntity_PyrolyseOven> tCasingElement =
                Loader.isModLoaded(MOD_ID_DC) ?
                        ofBlockUnlocalizedName(MOD_ID_DC, "gt.blockcasingsNH", 2) :
                        ofBlock(GregTech_API.sBlockCasings1, 0);

        return StructureDefinition.<GT_MetaTileEntity_PyrolyseOven>builder()
                .addShape("main", transpose(new String[][]{
                        {"ccccc", "ctttc", "ctttc", "ctttc", "ccccc"},
                        {"ccccc", "c---c", "c---c", "c---c", "ccccc"},
                        {"ccccc", "c---c", "c---c", "c---c", "ccccc"},
                        {"bb~bb", "bCCCb", "bCCCb", "bCCCb", "bbbbb"},
                }))
                .addElement('c', onElementPass(GT_MetaTileEntity_PyrolyseOven::onCasingAdded, tCasingElement))
                .addElement('C', ofCoil(GT_MetaTileEntity_PyrolyseOven::setCoilLevel, GT_MetaTileEntity_PyrolyseOven::getCoilLevel))
                .addElement('b', ofChain(
                        ofHatchAdder(GT_MetaTileEntity_PyrolyseOven::addMaintenanceToMachineList, CASING_INDEX, 1),
                        ofHatchAdder(GT_MetaTileEntity_PyrolyseOven::addOutputToMachineList, CASING_INDEX, 1),
                        ofHatchAdder(GT_MetaTileEntity_PyrolyseOven::addEnergyInputToMachineList, CASING_INDEX, 1),
                        onElementPass(GT_MetaTileEntity_PyrolyseOven::onCasingAdded, tCasingElement)
                ))
                .addElement('t', ofChain(
                        ofHatchAdder(GT_MetaTileEntity_PyrolyseOven::addInputToMachineList, CASING_INDEX, 2),
                        ofHatchAdder(GT_MetaTileEntity_PyrolyseOven::addMufflerToMachineList, CASING_INDEX, 2),
                        onElementPass(GT_MetaTileEntity_PyrolyseOven::onCasingAdded, tCasingElement)
                ))
                .build();
    }

    private int mCasingAmount;

    public GT_MetaTileEntity_PyrolyseOven(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_PyrolyseOven(String aName) {
        super(aName);
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Coke Oven")
                .addInfo("Controller block for the Pyrolyse Oven")
                .addInfo("Industrial Charcoal producer")
                .addInfo("Processing speed scales linearly with Coil tier:")
                .addInfo("CuNi: 50%, FeAlCr: 100%, Ni4Cr: 150%, Fe50CW: 200%, etc.")
                .addInfo("EU/t is not affected by Coil tier")
                .addPollutionAmount(20 * getPollutionPerTick(null))
                .addSeparator()
                .beginStructureBlock(5, 4, 5, true)
                .addController("Front center")
                .addCasingInfo("Pyrolyse Oven Casing", 60)
                .addOtherStructurePart("Heating Coils", "Center 3x1x3 of the bottom layer")
                .addEnergyHatch("Any bottom layer casing", 1)
                .addMaintenanceHatch("Any bottom layer casing", 1)
                .addMufflerHatch("Center 3x1x3 area in top layer", 2)
                .addInputBus("Center 3x1x3 area in top layer", 2)
                .addInputHatch("Center 3x1x3 area in top layer", 2)
                .addOutputBus("Any bottom layer casing", 1)
                .addOutputHatch("Any bottom layer casing", 1)
                .toolTipFinisher("Gregtech");
        return tt;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            if (aActive)
                return new ITexture[]{
                        BlockIcons.casingTexturePages[8][66],
                        TextureFactory.builder().addIcon(OVERLAY_FRONT_PYROLYSE_OVEN_ACTIVE).extFacing().build(),
                        TextureFactory.builder().addIcon(OVERLAY_FRONT_PYROLYSE_OVEN_ACTIVE_GLOW).extFacing().glow().build()};
            return new ITexture[]{
                    BlockIcons.casingTexturePages[8][66],
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_PYROLYSE_OVEN).extFacing().build(),
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_PYROLYSE_OVEN_GLOW).extFacing().glow().build()};
        }
        return new ITexture[]{Textures.BlockIcons.casingTexturePages[8][66]};
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "PyrolyseOven.png");
    }

    @Override
    public boolean checkRecipe(ItemStack aStack) {
        ItemStack[] tInputs = getCompactedInputs();
        FluidStack[] tFluids = getCompactedFluids();

        if (tInputs.length <= 0)
            return false;

        long tVoltage = getMaxInputVoltage();
        byte tTier = (byte) Math.max(1, GT_Utility.getTier(tVoltage));
        GT_Recipe tRecipe = GT_Recipe.GT_Recipe_Map.sPyrolyseRecipes.findRecipe(getBaseMetaTileEntity(), false, gregtech.api.enums.GT_Values.V[tTier], tFluids, tInputs);

        if (tRecipe == null || !tRecipe.isRecipeInputEqual(true, tFluids, tInputs))
            return false;

        this.mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
        this.mEfficiencyIncrease = 10000;

        calculateOverclockedNessMulti(tRecipe.mEUt, tRecipe.mDuration, 1, tVoltage);
        //In case recipe is too OP for that machine
        if (mMaxProgresstime == Integer.MAX_VALUE - 1 && mEUt == Integer.MAX_VALUE - 1)
            return false;
        if (this.mEUt > 0)
            this.mEUt = (-this.mEUt);
        this.mMaxProgresstime = Math.max(mMaxProgresstime * 2 / (1 + coilHeat.getTier()), 1);
        if (tRecipe.mOutputs.length > 0)
            this.mOutputItems = new ItemStack[]{tRecipe.getOutput(0)};
        if (tRecipe.mFluidOutputs.length > 0)
            this.mOutputFluids = new FluidStack[]{tRecipe.getFluidOutput(0)};
        updateSlots();
        return true;
    }

    @Override
    public IStructureDefinition<GT_MetaTileEntity_PyrolyseOven> getStructureDefinition() {
        return STRUCTURE_DEFINITION;
    }

    private void onCasingAdded() {
        mCasingAmount++;
    }

    public HeatingCoilLevel getCoilLevel() {
        return coilHeat;
    }

    private void setCoilLevel(HeatingCoilLevel aCoilLevel) {
        coilHeat = aCoilLevel;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        coilHeat = HeatingCoilLevel.None;
        mCasingAmount = 0;
        replaceDeprecatedCoils(aBaseMetaTileEntity);
        return checkPiece("main", 2, 3, 0) && mCasingAmount >= 60 &&
                mMaintenanceHatches.size() == 1 && !mMufflerHatches.isEmpty();
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
    public int getPollutionPerTick(ItemStack aStack) {
        return 30;
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
        return new GT_MetaTileEntity_PyrolyseOven(this.mName);
    }

    private void replaceDeprecatedCoils(IGregTechTileEntity aBaseMetaTileEntity) {
        int xDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetX;
        int zDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetZ;
        int tX = aBaseMetaTileEntity.getXCoord() + xDir * 2;
        int tY = aBaseMetaTileEntity.getYCoord();
        int tZ = aBaseMetaTileEntity.getZCoord() + zDir * 2;
        for (int xPos = tX - 1; xPos <= tX + 1; xPos++) {
            for (int zPos = tZ - 1; zPos <= tZ + 1; zPos++) {
                if (aBaseMetaTileEntity.getBlock(xPos, tY, zPos) == GregTech_API.sBlockCasings1 &&
                        aBaseMetaTileEntity.getMetaID(xPos, tY, zPos) == 13) {
                    aBaseMetaTileEntity.getWorld().setBlock(xPos, tY, zPos, GregTech_API.sBlockCasings5, 1, 3);
                }
            }
        }
    }

    @Override
    public void construct(ItemStack stackSize, boolean hintsOnly) {
        buildPiece("main", stackSize, hintsOnly, 2, 3, 0);
    }
}
