package gregtech.common.tileentities.machines.multi;

import gregtech.api.GregTech_API;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.IHeatingCoil;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Input;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.mutable.MutableInt;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.BitSet;

public class GT_MetaTileEntity_OilCracker extends GT_MetaTileEntity_MultiBlockBase {
    private ForgeDirection orientation;
    private int controllerX, controllerZ;

    private static final byte CASING_INDEX = 49;
    private HeatingCoilLevel heatLevel;

    public GT_MetaTileEntity_OilCracker(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_OilCracker(String aName) {
        super(aName);
    }

    public String[] getDescription() {
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
        .addInfo("Processing speed scales linearly with Coil tier:")
        .addInfo("CuNi: 100%, FeAlCr: 150%, Ni4Cr: 200%, Fe50CW: 250%, etc.")
		.addEnergyHatch("Any casing")
		.addMaintenanceHatch("Any casing")
		.addInputHatch("Steam/Hydrogen, Any middle ring casing")
		.addInputHatch("Any left/right side casing")
		.addOutputHatch("Any left/right side casing")
		.addStructureInfo("Input/Output Hatches must be on opposite sides!")
		.toolTipFinisher("Gregtech");
		if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) return tt.getInformation();
        return tt.getStructureInformation();
    }

    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) return new ITexture[]{Textures.BlockIcons.casingTexturePages[0][CASING_INDEX],
                new GT_RenderedTexture(aActive ? Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER_ACTIVE : Textures.BlockIcons.OVERLAY_FRONT_OIL_CRACKER)};
        return new ITexture[]{Textures.BlockIcons.casingTexturePages[0][CASING_INDEX]};
    }

    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "OilCrackingUnit.png");
    }

    @Override
    public boolean checkRecipe(ItemStack aStack) {
        ArrayList<FluidStack> tInputList = getStoredFluids();
        FluidStack[] tFluidInputs = tInputList.toArray(new FluidStack[0]);
        long tVoltage = getMaxInputVoltage();
        byte tTier = (byte) Math.max(1, GT_Utility.getTier(tVoltage));

        GT_Recipe tRecipe = GT_Recipe.GT_Recipe_Map.sCrakingRecipes.findRecipe(
                getBaseMetaTileEntity(),
                false,
                gregtech.api.enums.GT_Values.V[tTier],
                tFluidInputs ,
                mInventory[1]
        );

        if (tRecipe == null)
            return false;

        if (tRecipe.isRecipeInputEqual(true, tFluidInputs, mInventory[1])) {
            this.mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
            this.mEfficiencyIncrease = 10000;
            this.mEUt = tRecipe.mEUt;
            this.mMaxProgresstime = tRecipe.mDuration;
            while (this.mEUt <= gregtech.api.enums.GT_Values.V[(tTier - 1)]) {
                this.mEUt *= 4;
                this.mMaxProgresstime /= 2;
            }

            if (this.mEUt > 0)
                this.mEUt = (-this.mEUt);

            this.mMaxProgresstime = Math.max(mMaxProgresstime / heatLevel.getTier(), 1);

            this.mOutputFluids = new FluidStack[]{tRecipe.getFluidOutput(0)};
            return true;
        }
        return false;
    }

    private boolean coilsNotPresent(IGregTechTileEntity aBaseMetaTileEntity, int x , int y, int z) {

        Block coil = aBaseMetaTileEntity.getBlockOffset(x, y, z);

        if (!(coil instanceof IHeatingCoil))
            return true;

        IHeatingCoil heatingCoil = (IHeatingCoil) coil;
        byte meta = aBaseMetaTileEntity.getMetaIDOffset(x, y, z);
        HeatingCoilLevel heatLevel = heatingCoil.getCoilHeat(meta);
        if (heatLevel == HeatingCoilLevel.None)
            return true;

        if (this.heatLevel == HeatingCoilLevel.None)
            this.heatLevel = heatLevel;

        return this.heatLevel != heatLevel;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        this.heatLevel = HeatingCoilLevel.None;
        this.orientation = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing());
        this.controllerX = aBaseMetaTileEntity.getXCoord();
        this.controllerZ = aBaseMetaTileEntity.getZCoord();
        int xDir = this.orientation.offsetX;
        int zDir = this.orientation.offsetZ;
        MutableInt amount = new MutableInt(0);
        replaceDeprecatedCoils(aBaseMetaTileEntity);
        BitSet flags = new BitSet(4);

        for (int depth = -1; depth < 2; depth++)
            for (int height = -1; height < 2; height++)
                for (int slice = -2; slice < 3; slice++)
                    if (xDir != 0) {
                        if (isStructureBroken(xDir, zDir, depth, height, slice, aBaseMetaTileEntity, amount, flags))
                            return false;
                    } else {
                        if (isStructureBroken(xDir, zDir, slice, height, depth, aBaseMetaTileEntity, amount, flags))
                            return false;
                    }

        if(checkInputOutputBroken(flags))
            return false;

        return amount.intValue() >= 18;
    }

    private boolean checkInputOutputBroken(BitSet flags){
        if (flags.get(0) && flags.get(2)) //input and output on side 1
            return true;
        if (flags.get(1) && flags.get(3)) //input and output on side 2
            return true;
        if (flags.get(1) && flags.get(2)) //input on both sides
            return true;
        return flags.get(2) && flags.get(3); //output on both sides
    }

    private boolean isStructureBroken(
            int xDir,
            int zDir,
            int a,
            int b,
            int c,
            IGregTechTileEntity aBaseMetaTileEntity,
            MutableInt amount,
            BitSet flags) {
        if (b == 0 && c == 0 && (a == -1 || a == 0 || a == 1))
            return false;
        if (a == 1 || a == -1) {
            return coilsNotPresent(aBaseMetaTileEntity, xDir + a, b, c + zDir);
        }
        else if (a == 2 || a == -2) {
            return checkEndsBroken(xDir, zDir, a, b, c, aBaseMetaTileEntity, amount, flags);
        }
        else if (a == 0)
            return checkMiddleBroken(xDir, zDir, a, b, c, aBaseMetaTileEntity, amount);

        return false;
    }

    private boolean checkEndsBroken(
            int xDir,
            int zDir,
            int a,
            int b,
            int c,
            IGregTechTileEntity aBaseMetaTileEntity,
            MutableInt amount,
            BitSet flags
    ){
        IGregTechTileEntity tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir + c, b, a + zDir);
        if (addInputToMachineList(tTileEntity, CASING_INDEX))
            if (a == -2)
                flags.set(0); //input on side 1
            else
                flags.set(1); //input on side 2
        else if (addOutputToMachineList(tTileEntity, CASING_INDEX))
            if (a == -2)
                flags.set(2); //output on side 1
            else
                flags.set(3); //output on side 2
        else if (!addEnergyInputToMachineList(tTileEntity, CASING_INDEX))
            if (!addMaintenanceToMachineList(tTileEntity, CASING_INDEX)) {
                if (aBaseMetaTileEntity.getBlockOffset(xDir + c, b, a + zDir) != GregTech_API.sBlockCasings4)
                    return true;
                if (aBaseMetaTileEntity.getMetaIDOffset(xDir + c, b, a + zDir) != 1)
                    return true;
                amount.increment();
            }
        return false;
    }

    private boolean checkMiddleBroken( int xDir,
                                       int zDir,
                                       int a,
                                       int b,
                                       int c,
                                       IGregTechTileEntity aBaseMetaTileEntity,
                                       MutableInt amount){
        IGregTechTileEntity tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir + c, b, a + zDir);
        if (addMaintenanceToMachineList(tTileEntity, CASING_INDEX))
            return false;
        if (addInputToMachineList(tTileEntity, CASING_INDEX))
            return false;
        if (addEnergyInputToMachineList(tTileEntity, CASING_INDEX))
            return false;
        if ((xDir + c) == 0 && b == 0 && (a + zDir) == 0)
            return false;
        if (aBaseMetaTileEntity.getBlockOffset(xDir + c, b, a + zDir) != GregTech_API.sBlockCasings4)
            return true;
        if (aBaseMetaTileEntity.getMetaIDOffset(xDir + c, b, a + zDir) != 1)
            return true;
        amount.increment();
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
        return 0;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    @Override
    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return false;
    }

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
                FluidStack tStack = tHatch.getFillableStack();
                if (tStack.isFluidEqual(GT_ModHandler.getSteam(1000)) || tStack.isFluidEqual(Materials.Hydrogen.getGas(1000))) {
                    if (isHatchInMiddleRing(tHatch))
                        rList.add(tStack);
                } else if (!isHatchInMiddleRing(tHatch))
                    rList.add(tStack);
            }
        }
        return rList;
    }

    private boolean isHatchInMiddleRing(GT_MetaTileEntity_Hatch_Input inputHatch){
        if (orientation == ForgeDirection.NORTH || orientation == ForgeDirection.SOUTH)
            return inputHatch.getBaseMetaTileEntity().getXCoord() == this.controllerX;
        return inputHatch.getBaseMetaTileEntity().getZCoord() == this.controllerZ;
    }
}