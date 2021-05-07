package gregtech.common.tileentities.machines.multi;

import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.enums.Textures.BlockIcons;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_DataAccess;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.objects.GT_RenderedGlowTexture;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static gregtech.GT_Mod.GT_FML_LOGGER;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_GLOW;

public class GT_MetaTileEntity_AssemblyLine
        extends GT_MetaTileEntity_MultiBlockBase {

    public ArrayList<GT_MetaTileEntity_Hatch_DataAccess> mDataAccessHatches = new ArrayList<>();

    public GT_MetaTileEntity_AssemblyLine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_AssemblyLine(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_AssemblyLine(this.mName);
    }

    @Override
    public String[] getDescription() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Assembling Line")
                .addInfo("Controller block for the Assembling Line")
                .addInfo("Used to make complex machine parts (LuV+)")
                .addInfo("Does not make Assembler items")
                .addSeparator()
                .beginVariableStructureBlock(5, 15, 4, 4, 3, 3, false)//?
                .addStructureInfo("From Bottom to Top, Left to Right")
                .addStructureInfo("Layer 1 - Solid Steel Machine Casing, Input Bus (last is Output Bus), Solid Steel Machine Casing")
                .addStructureInfo("Layer 2 - Reinforced Glass, Assembling Line Casing, Reinforced Glass")
                .addStructureInfo("Layer 3 - Grate Machine Casing, Assembler Machine Casing, Grate Machine Casing")
                .addStructureInfo("Layer 4 - Empty, Solid Steel Machine Casing, Empty")
                .addStructureInfo("Up to 16 repeating slices, each one allows for 1 more item in recipes, aside from the last")
                .addStructureInfo("Optional - Replace 1x Grate with (Advanced) Data Access Hatch next to the Controller")
                .addStructureInfo("Optional - Replace 1x Grate with (Advanced) Data Access Hatch next to the Controller")//TT

                .addController("Either Grate on layer 3 of the first slice")
                .addEnergyHatch("Any layer 4 casing")
                .addMaintenanceHatch("Any layer 1 casing")
                .addInputBus("As specified on layer 1")
                .addInputHatch("Any layer 1 casing")
                .addOutputBus("Replaces Input Bus on final slice")
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
                        BlockIcons.casingTexturePages[0][16],
                        new GT_RenderedTexture(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE),
                        new GT_RenderedGlowTexture(OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE_GLOW)};
            return new ITexture[]{
                    BlockIcons.casingTexturePages[0][16],
                    new GT_RenderedTexture(OVERLAY_FRONT_ASSEMBLY_LINE),
                    new GT_RenderedGlowTexture(OVERLAY_FRONT_ASSEMBLY_LINE_GLOW)};
        }
        return new ITexture[]{Textures.BlockIcons.casingTexturePages[0][16]};
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "AssemblyLine.png");
    }

    @Override
    public GT_Recipe.GT_Recipe_Map getRecipeMap() {
        return null;
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
        if (GT_Values.D1)
            GT_FML_LOGGER.info("Start ALine recipe check");
        ArrayList<ItemStack> tDataStickList = getDataItems(2);
        if (tDataStickList.isEmpty()) return false;
        if (GT_Values.D1)
            GT_FML_LOGGER.info("Stick accepted, " + tDataStickList.size() + " Data Sticks found");

        ItemStack[] tStack = new ItemStack[15];
        FluidStack[] tFluids = new FluidStack[4];
        boolean findRecipe = false;
        nextDS:
        for (ItemStack tDataStick : tDataStickList) {
            NBTTagCompound tTag = tDataStick.getTagCompound();
            if (tTag == null)
                continue;
            for (int i = 0; i < 15; i++) {
                int count = tTag.getInteger("a" + i);
                if (!tTag.hasKey("" + i) && count <= 0)
                    continue;
                if (mInputBusses.get(i) == null) {
                    continue nextDS;
                }

                ItemStack stackInSlot = mInputBusses.get(i).getBaseMetaTileEntity().getStackInSlot(0);
                boolean flag = true;
                if (count > 0) {
                    for (int j = 0; j < count; j++) {
                        tStack[i] = GT_Utility.loadItem(tTag, "a" + i + ":" + j);
                        if (tStack[i] == null) continue;
                        if (GT_Values.D1)
                            GT_FML_LOGGER.info("Item " + i + " : " + tStack[i].getUnlocalizedName());
                        if (GT_Utility.areStacksEqual(tStack[i], stackInSlot, true) && tStack[i].stackSize <= stackInSlot.stackSize) {
                            flag = false;
                            break;
                        }
                    }
                }
                if (flag) {
                    tStack[i] = GT_Utility.loadItem(tTag, "" + i);
                    if (tStack[i] == null) {
                        flag = false;
                        continue;
                    }
                    if (GT_Values.D1)
                        GT_FML_LOGGER.info("Item " + i + " : " + tStack[i].getUnlocalizedName());
                    if (GT_Utility.areStacksEqual(tStack[i], stackInSlot, true) && tStack[i].stackSize <= stackInSlot.stackSize) {
                        flag = false;
                    }
                }
                if (GT_Values.D1)
                    GT_FML_LOGGER.info(i + (flag ? " not accepted" : " accepted"));
                if (flag)
                    continue nextDS;
            }

            if (GT_Values.D1) GT_FML_LOGGER.info("All Items done, start fluid check");
            for (int i = 0; i < 4; i++) {
                if (!tTag.hasKey("f" + i)) continue;
                tFluids[i] = GT_Utility.loadFluid(tTag, "f" + i);
                if (tFluids[i] == null) continue;
                if (GT_Values.D1)
                    GT_FML_LOGGER.info("Fluid " + i + " " + tFluids[i].getUnlocalizedName());
                if (mInputHatches.get(i) == null) {
                    continue nextDS;
                }
                FluidStack fluidInHatch = mInputHatches.get(i).mFluid;
                if (!GT_Utility.areFluidsEqual(fluidInHatch, tFluids[i], true) || fluidInHatch.amount < tFluids[i].amount) {
                    if (GT_Values.D1)
                        GT_FML_LOGGER.info(i + " not accepted");
                    continue nextDS;
                }
                if (GT_Values.D1)
                    GT_FML_LOGGER.info(i + " accepted");
            }

            if (GT_Values.D1)
                GT_FML_LOGGER.info("Input accepted, check other values");
            if (!tTag.hasKey("output"))
                continue;
            mOutputItems = new ItemStack[]{GT_Utility.loadItem(tTag, "output")};
            if (mOutputItems[0] == null || !GT_Utility.isStackValid(mOutputItems[0]))
                continue;

            if (!tTag.hasKey("time"))
                continue;
            mMaxProgresstime = tTag.getInteger("time");
            if (mMaxProgresstime <= 0)
                continue;

            if (!tTag.hasKey("eu"))
                continue;
            mEUt = tTag.getInteger("eu");

            if (GT_Values.D1) GT_FML_LOGGER.info("Find avaiable recipe");
            findRecipe = true;
            break;
        }
        if (!findRecipe) return false;

        if (GT_Values.D1) GT_FML_LOGGER.info("All checked start consuming inputs");
        for (int i = 0; i < 15; i++) {
            if (tStack[i] == null)
                continue;
            ItemStack stackInSlot = mInputBusses.get(i).getBaseMetaTileEntity().getStackInSlot(0);
            stackInSlot.stackSize -= tStack[i].stackSize;
        }

        for (int i = 0; i < 4; i++) {
            if (tFluids[i] == null)
                continue;
            mInputHatches.get(i).mFluid.amount -= tFluids[i].amount;
            if (mInputHatches.get(i).mFluid.amount <= 0) {
                mInputHatches.get(i).mFluid = null;
            }
        }
        if (GT_Values.D1) GT_FML_LOGGER.info("Check overclock");

        byte tTier = (byte) Math.max(1, GT_Utility.getTier(getMaxInputVoltage()));
        this.mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
        this.mEfficiencyIncrease = 10000;
        if (mEUt <= 16) {
            this.mEUt = (mEUt * (1 << tTier - 1) * (1 << tTier - 1));
            this.mMaxProgresstime = (mMaxProgresstime / (1 << tTier - 1));
        } else {
            while (this.mEUt <= gregtech.api.enums.GT_Values.V[(tTier - 1)]) {
                this.mEUt *= 4;
                this.mMaxProgresstime /= 2;
            }
        }
        if (this.mEUt > 0) {
            this.mEUt = -this.mEUt;
        }
        this.mMaxProgresstime = Math.max(1, this.mMaxProgresstime);
        updateSlots();
        if (GT_Values.D1)
            GT_FML_LOGGER.info("Recipe sucessfull");
        return true;
    }

    @Override
    public boolean onRunningTick(ItemStack aStack) {
        for(GT_MetaTileEntity_Hatch_DataAccess hatch_dataAccess:mDataAccessHatches){
            hatch_dataAccess.setActive(true);
        }
        return super.onRunningTick(aStack);
    }

    @Override
    public void startSoundLoop(byte aIndex, double aX, double aY, double aZ) {
        super.startSoundLoop(aIndex, aX, aY, aZ);
        if (aIndex == 20) {
            GT_Utility.doSoundAtClient(GregTech_API.sSoundList.get(212), 10, 1.0F, aX, aY, aZ);
        }
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        mDataAccessHatches.clear();
        int xDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetX;
        int zDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetZ;
        if (xDir != 0) {
            for (int r = 0; r <= 16; r++) {
                int i = r * xDir;

                IGregTechTileEntity tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(0, 0, i);
                if (i != 0 && !(aBaseMetaTileEntity.getBlockOffset(0, 0, i) == GregTech_API.sBlockCasings3 && aBaseMetaTileEntity.getMetaIDOffset(0, 0, i) == 10)) {
                    if(r == 1 && !addDataAccessToMachineList(tTileEntity, 16)){
                        return false;
                    }
                }
                if (!aBaseMetaTileEntity.getBlockOffset(0, -1, i).getUnlocalizedName().equals("blockAlloyGlass")) {
                    return false;
                }
                tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(0, -2, i);
                if ((!addMaintenanceToMachineList(tTileEntity, 16)) && (!addInputToMachineList(tTileEntity, 16))) {
                    if (aBaseMetaTileEntity.getBlockOffset(0, -2, i) != GregTech_API.sBlockCasings2) {
                        return false;
                    }
                    if (aBaseMetaTileEntity.getMetaIDOffset(0, -2, i) != 0) {
                        return false;
                    }
                }

                tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir, 1, i);
                if (!addEnergyInputToMachineList(tTileEntity, 16)) {
                    if (aBaseMetaTileEntity.getBlockOffset(xDir, 1, i) != GregTech_API.sBlockCasings2) {
                        return false;
                    }
                    if (aBaseMetaTileEntity.getMetaIDOffset(xDir, 1, i) != 0) {
                        return false;
                    }
                }
                if (i != 0 && !(aBaseMetaTileEntity.getBlockOffset(xDir, 0, i) == GregTech_API.sBlockCasings2 && aBaseMetaTileEntity.getMetaIDOffset(xDir, 0, i) == 9)) {
                    return false;
                }
                if (i != 0 && !(aBaseMetaTileEntity.getBlockOffset(xDir, -1, i) == GregTech_API.sBlockCasings2 && aBaseMetaTileEntity.getMetaIDOffset(xDir, -1, i) == 5)) {
                    return false;
                }


                if (!(aBaseMetaTileEntity.getBlockOffset(xDir * 2, 0, i) == GregTech_API.sBlockCasings3 && aBaseMetaTileEntity.getMetaIDOffset(xDir * 2, 0, i) == 10)) {
                    return false;
                }
                if (!aBaseMetaTileEntity.getBlockOffset(xDir * 2, -1, i).getUnlocalizedName().equals("blockAlloyGlass")) {
                    return false;
                }
                tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir * 2, -2, i);
                if ((!addMaintenanceToMachineList(tTileEntity, 16)) && (!addInputToMachineList(tTileEntity, 16))) {
                    if (aBaseMetaTileEntity.getBlockOffset(xDir * 2, -2, i) != GregTech_API.sBlockCasings2) {
                        return false;
                    }
                    if (aBaseMetaTileEntity.getMetaIDOffset(xDir * 2, -2, i) != 0) {
                        return false;
                    }
                }
                tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir, -2, i);
                if (!addInputToMachineList(tTileEntity, 16) && addOutputToMachineList(tTileEntity, 16)) {
                    return r > 0 && !mEnergyHatches.isEmpty();
                }
            }
        } else {
            for (int r = 0; r <= 16; r++) {
                int i = r * -zDir;

                IGregTechTileEntity tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(i, 0, 0);
                if (i != 0 && !(aBaseMetaTileEntity.getBlockOffset(i, 0, 0) == GregTech_API.sBlockCasings3 && aBaseMetaTileEntity.getMetaIDOffset(i, 0, 0) == 10)) {
                    if(r == 1 && !addDataAccessToMachineList(tTileEntity, 16)){
                        return false;
                    }
                }
                if (!aBaseMetaTileEntity.getBlockOffset(i, -1, 0).getUnlocalizedName().equals("blockAlloyGlass")) {
                    return false;
                }
                tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(i, -2, 0);
                if ((!addMaintenanceToMachineList(tTileEntity, 16)) && (!addInputToMachineList(tTileEntity, 16))) {
                    if (aBaseMetaTileEntity.getBlockOffset(i, -2, 0) != GregTech_API.sBlockCasings2) {
                        return false;
                    }
                    if (aBaseMetaTileEntity.getMetaIDOffset(i, -2, 0) != 0) {
                        return false;
                    }
                }

                tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(i, 1, zDir);
                if (!addEnergyInputToMachineList(tTileEntity, 16)) {
                    if (aBaseMetaTileEntity.getBlockOffset(i, 1, zDir) != GregTech_API.sBlockCasings2) {
                        return false;
                    }
                    if (aBaseMetaTileEntity.getMetaIDOffset(i, 1, zDir) != 0) {
                        return false;
                    }
                }
                if (i != 0 && !(aBaseMetaTileEntity.getBlockOffset(i, 0, zDir) == GregTech_API.sBlockCasings2 && aBaseMetaTileEntity.getMetaIDOffset(i, 0, zDir) == 9)) {
                    return false;
                }
                if (i != 0 && !(aBaseMetaTileEntity.getBlockOffset(i, -1, zDir) == GregTech_API.sBlockCasings2 && aBaseMetaTileEntity.getMetaIDOffset(i, -1, zDir) == 5)) {
                    return false;
                }


                if (!(aBaseMetaTileEntity.getBlockOffset(i, 0, zDir * 2) == GregTech_API.sBlockCasings3 && aBaseMetaTileEntity.getMetaIDOffset(i, 0, zDir * 2) == 10)) {
                    return false;
                }
                if (!aBaseMetaTileEntity.getBlockOffset(i, -1, zDir * 2).getUnlocalizedName().equals("blockAlloyGlass")) {
                    return false;
                }
                tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(i, -2, zDir * 2);
                if ((!addMaintenanceToMachineList(tTileEntity, 16)) && (!addInputToMachineList(tTileEntity, 16))) {
                    if (aBaseMetaTileEntity.getBlockOffset(i, -2, zDir * 2) != GregTech_API.sBlockCasings2) {
                        return false;
                    }
                    if (aBaseMetaTileEntity.getMetaIDOffset(i, -2, zDir * 2) != 0) {
                        return false;
                    }
                }
                tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(i, -2, zDir);
                if (!addInputToMachineList(tTileEntity, 16) && addOutputToMachineList(tTileEntity, 16)) {
                    return r > 0 && !mEnergyHatches.isEmpty();
                }
            }
        }
        return false;
    }

    /**
     * @param state using bitmask, 1 for IntegratedCircuit, 2 for DataStick, 4 for DataOrb
     */
    private boolean isCorrectDataItem(ItemStack aStack, int state){
        if ((state & 1) != 0 && ItemList.Circuit_Integrated.isStackEqual(aStack, true, true)) return true;
        if ((state & 2) != 0 && ItemList.Tool_DataStick.isStackEqual(aStack, false, true)) return true;
        return (state & 4) != 0 && ItemList.Tool_DataOrb.isStackEqual(aStack, false, true);
    }

    /**
     * @param state using bitmask, 1 for IntegratedCircuit, 2 for DataStick, 4 for DataOrb
     */
    public ArrayList<ItemStack> getDataItems(int state) {
        ArrayList<ItemStack> rList = new ArrayList<>();
        if (GT_Utility.isStackValid(mInventory[1]) && isCorrectDataItem(mInventory[1], state)) {
            rList.add(mInventory[1]);
        }
        for (GT_MetaTileEntity_Hatch_DataAccess tHatch : mDataAccessHatches) {
            if (isValidMetaTileEntity(tHatch)) {
                for (int i = 0; i < tHatch.getBaseMetaTileEntity().getSizeInventory(); i++) {
                    if (tHatch.getBaseMetaTileEntity().getStackInSlot(i) != null
                            && isCorrectDataItem(tHatch.getBaseMetaTileEntity().getStackInSlot(i), state))
                        rList.add(tHatch.getBaseMetaTileEntity().getStackInSlot(i));
                }
            }
        }
        return rList;
    }

    public boolean addDataAccessToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_DataAccess) {
            ((GT_MetaTileEntity_Hatch) aMetaTileEntity).updateTexture(aBaseCasingIndex);
            return mDataAccessHatches.add((GT_MetaTileEntity_Hatch_DataAccess) aMetaTileEntity);
        }
        return false;
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
}
