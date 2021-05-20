package gregtech.common.tileentities.machines.multi;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTech_API;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Textures;
import gregtech.api.enums.Textures.BlockIcons;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.IHeatingCoil;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import gregtech.loaders.oreprocessing.ProcessingLog;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.lwjgl.input.Keyboard;

import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_PYROLYSE_OVEN;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_PYROLYSE_OVEN_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_PYROLYSE_OVEN_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_PYROLYSE_OVEN_GLOW;

public class GT_MetaTileEntity_PyrolyseOven extends GT_MetaTileEntity_MultiBlockBase {

    private HeatingCoilLevel coilHeat;
    //public static GT_CopiedBlockTexture mTextureULV = new GT_CopiedBlockTexture(Block.getBlockFromItem(ItemList.Casing_ULV.get(1).getItem()), 6, 0, Dyes.MACHINE_METAL.mRGBa);
    private static final int CASING_INDEX = 1090;

    public GT_MetaTileEntity_PyrolyseOven(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_PyrolyseOven(String aName) {
        super(aName);
    }

    @Override
    public String[] getDescription() {
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
                .addEnergyHatch("Any bottom layer casing")
                .addMaintenanceHatch("Any bottom layer casing")
                .addMufflerHatch("Center 3x1x3 area in top layer")
                .addInputBus("Center 3x1x3 area in top layer")
                .addInputHatch("Center 3x1x3 area in top layer")
                .addOutputBus("Any bottom layer casing")
                .addOutputHatch("Any bottom layer casing")
                .toolTipFinisher("Gregtech");
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            return tt.getStructureInformation();
        } else {
            return tt.getInformation();
        }
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            if (aActive)
                return new ITexture[]{
                        BlockIcons.casingTexturePages[8][66],
                        TextureFactory.of(OVERLAY_FRONT_PYROLYSE_OVEN_ACTIVE),
                        TextureFactory.builder().addIcon(OVERLAY_FRONT_PYROLYSE_OVEN_ACTIVE_GLOW).glow().build()};
            return new ITexture[]{
                    BlockIcons.casingTexturePages[8][66],
                    TextureFactory.of(OVERLAY_FRONT_PYROLYSE_OVEN),
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_PYROLYSE_OVEN_GLOW).glow().build()};
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

        //Dynamic recipe adding for newly found logWoods - wont be visible in nei most probably
        if (tRecipe == null)
            tRecipe = addRecipesDynamically(tInputs, tFluids, tTier);

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

    private GT_Recipe addRecipesDynamically(ItemStack[] tInputs, FluidStack[] tFluids, int tTier) {
        if (tInputs.length > 1 || (tInputs[0] != null && tInputs[0].getItem() != GT_Utility.getIntegratedCircuit(0).getItem())) {
            int oreId = OreDictionary.getOreID("logWood");
            for (ItemStack is : tInputs) {
                for (int id : OreDictionary.getOreIDs(is)) {
                    if (oreId == id) {
                        ProcessingLog.addPyrolyeOvenRecipes(is);
                        return GT_Recipe.GT_Recipe_Map.sPyrolyseRecipes.findRecipe(getBaseMetaTileEntity(), false, gregtech.api.enums.GT_Values.V[tTier], tFluids, tInputs);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        int xDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetX * 2;
        int zDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetZ * 2;
        Block casingBlock;
        int casingMeta;

        if (Loader.isModLoaded("dreamcraft")) {
            casingBlock = GameRegistry.findBlock("dreamcraft", "gt.blockcasingsNH");
            casingMeta = 2;
        } else {
            casingBlock = GregTech_API.sBlockCasings1;
            casingMeta = 0;
        }

        replaceDeprecatedCoils(aBaseMetaTileEntity);
        MutableBoolean firstCoil = new MutableBoolean(true);
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                for (int h = 0; h < 4; h++) {
                    IGregTechTileEntity tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir + i, h, zDir + j);
                    if ((i != -2 && i != 2) && (j != -2 && j != 2)) {// inner 3x3 without height
                        if (checkInnerBroken(
                                xDir, zDir,
                                i, h, j,
                                aBaseMetaTileEntity, tTileEntity,
                                casingBlock, casingMeta,
                                firstCoil
                        )
                        )
                            return false;
                    } else {// outer 5x5 without height
                        if (checkOuterBroken(
                                xDir, zDir,
                                i, h, j,
                                aBaseMetaTileEntity, tTileEntity,
                                casingBlock, casingMeta
                        )
                        )
                            return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean checkInnerBroken(int xDir,
                                     int zDir,
                                     int a,
                                     int b,
                                     int c,
                                     IGregTechTileEntity aBaseMetaTileEntity,
                                     IGregTechTileEntity tTileEntity,
                                     Block casingBlock,
                                     int casingMeta,
                                     MutableBoolean firstCoil
    ) {
        if (b == 0) {// inner floor (Coils)
            return areCoilsBroken(xDir, zDir, a, b, c, aBaseMetaTileEntity, firstCoil);
        } else if (b == 3) {// inner ceiling (ulv casings + input + muffler)
            return checkInnerCeilingBroken(xDir, zDir, a, b, c, aBaseMetaTileEntity, tTileEntity, casingBlock, casingMeta);
        } else {// inside air
            return !aBaseMetaTileEntity.getAirOffset(xDir + a, b, zDir + c);
        }
    }

    private boolean checkOuterBroken(int xDir,
                                     int zDir,
                                     int a,
                                     int b,
                                     int c,
                                     IGregTechTileEntity aBaseMetaTileEntity,
                                     IGregTechTileEntity tTileEntity,
                                     Block casingBlock,
                                     int casingMeta) {
        if (b == 0) {// outer floor (controller, output, energy, maintainance, rest ulv casings)
            if (checkOuterFloorLoopControl(xDir, zDir, a, c, tTileEntity))
                return false;
        }
        // outer above floor (ulv casings)
        if (aBaseMetaTileEntity.getBlockOffset(xDir + a, b, zDir + c) != casingBlock) {
            return true;
        }
        return aBaseMetaTileEntity.getMetaIDOffset(xDir + a, b, zDir + c) != casingMeta;
    }

    private boolean checkOuterFloorLoopControl(int xDir,
                                               int zDir,
                                               int a,
                                               int c,
                                               IGregTechTileEntity tTileEntity
    ) {
        if (addMaintenanceToMachineList(tTileEntity, CASING_INDEX)) {
            return true;
        }
        if (addOutputToMachineList(tTileEntity, CASING_INDEX)) {
            return true;
        }

        if (addEnergyInputToMachineList(tTileEntity, CASING_INDEX)) {
            return true;
        }

        return (xDir + a == 0) && (zDir + c == 0);
    }

    private boolean checkInnerCeilingBroken(int xDir,
                                            int zDir,
                                            int a,
                                            int b,
                                            int c,
                                            IGregTechTileEntity aBaseMetaTileEntity,
                                            IGregTechTileEntity tTileEntity,
                                            Block casingBlock,
                                            int casingMeta) {
        if ((addInputToMachineList(tTileEntity, CASING_INDEX)) || (addMufflerToMachineList(tTileEntity, CASING_INDEX))) {
            return false;
        }
        if (aBaseMetaTileEntity.getBlockOffset(xDir + a, b, zDir + c) != casingBlock) {
            return true;
        }
        return aBaseMetaTileEntity.getMetaIDOffset(xDir + a, b, zDir + c) != casingMeta;
    }

    private boolean areCoilsBroken(int xDir,
                                   int zDir,
                                   int a,
                                   int b,
                                   int c,
                                   IGregTechTileEntity aBaseMetaTileEntity,
                                   MutableBoolean firstCoil
    ) {
        Block coil = aBaseMetaTileEntity.getBlockOffset(xDir + a, b, zDir + c);

        if (!(coil instanceof IHeatingCoil))
            return true;

        int metaID = aBaseMetaTileEntity.getMetaIDOffset(xDir + a, b, zDir + c);

        HeatingCoilLevel coilHeat = ((IHeatingCoil) coil).getCoilHeat(metaID);

        if (coilHeat == HeatingCoilLevel.None) {
            return true;
        } else {
            if (firstCoil.isTrue()) {
                this.coilHeat = coilHeat;
                firstCoil.setFalse();
            } else return coilHeat != this.coilHeat;
        }
        return false;
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
}
