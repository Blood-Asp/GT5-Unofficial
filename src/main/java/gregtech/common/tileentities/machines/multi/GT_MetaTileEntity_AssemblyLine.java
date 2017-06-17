package gregtech.common.tileentities.machines.multi;

import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

public class GT_MetaTileEntity_AssemblyLine
        extends GT_MetaTileEntity_MultiBlockBase {
    public GT_MetaTileEntity_AssemblyLine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_AssemblyLine(String aName) {
        super(aName);
    }

    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_AssemblyLine(this.mName);
    }

    public String[] getDescription() {
        return new String[]{"Assembling Line",
                "Size: 3x(5-16)x4, variable length",
                "Bottom: Solid Steel Machine Casing(or Maintenance or Input Hatch),",
                "Input Bus(Last Output Bus), Steel Machine Casing",
                "Middle: Reinforced Glass, Assembling Line Casing, Reinforced Glass",
                "UpMiddle: Grate Machine Casing, Assembler Machine Casing,",
                "Grate Machine Casing(or Controller)",
                "Top: Solid Steel Casing(or Energy Hatch)",
                "Up to 16 repeating slices, last is Output Bus"};
    }

    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[16], new GT_RenderedTexture(aActive ? Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE_ACTIVE : Textures.BlockIcons.OVERLAY_FRONT_ASSEMBLY_LINE)};
        }
        return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[16]};
    }

    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "AssemblyLine.png");
    }

    public GT_Recipe.GT_Recipe_Map getRecipeMap() {
        return null;
    }

    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    public boolean isFacingValid(byte aFacing) {
        return aFacing > 1;
    }

    public boolean checkRecipe(ItemStack aStack) {
    	if(GT_Values.D1)System.out.println("Start ALine recipe check");
        if (!GT_Utility.isStackValid(mInventory[1]) || !ItemList.Tool_DataStick.isStackEqual(mInventory[1], false, true))
            return false;
    	if(GT_Values.D1)System.out.println("Stick accepted");

        NBTTagCompound tTag = mInventory[1].getTagCompound();
        if (tTag == null) return false;
        ItemStack tStack[] = new ItemStack[15];
        for (int i = 0; i < 15; i++) {
            if (!tTag.hasKey("" + i)) continue;
            if (mInputBusses.get(i) == null) return false;
            tStack[i] = GT_Utility.loadItem(tTag, "" + i);
            if (tStack[i] == null) continue;
        	if(GT_Values.D1)System.out.println("Item "+i+" : "+tStack[i].getUnlocalizedName());
            ItemStack stackInSlot = mInputBusses.get(i).getBaseMetaTileEntity().getStackInSlot(0);
            if (!GT_Utility.areStacksEqual(tStack[i], stackInSlot, true) || tStack[i].stackSize > stackInSlot.stackSize) {
            	if(GT_Values.D1)System.out.println(i +" not accepted");
                return false;
            }
        	if(GT_Values.D1)System.out.println(i+" accepted");
        }
    	if(GT_Values.D1)System.out.println("All Items done, start fluid check");
        FluidStack[] tFluids = new FluidStack[4];
        for (int i = 0; i < 4; i++) {
            if (!tTag.hasKey("f" + i)) continue;
            tFluids[i] = GT_Utility.loadFluid(tTag, "f" + i);
            if (tFluids[i] == null) continue;
        	if(GT_Values.D1)System.out.println("Fluid "+i+" "+tFluids[i].getUnlocalizedName());
            if (mInputHatches.get(i) == null) return false;
            FluidStack fluidInHatch = mInputHatches.get(i).mFluid;
            if (fluidInHatch == null || !GT_Utility.areFluidsEqual(fluidInHatch, tFluids[i], true) || fluidInHatch.amount < tFluids[i].amount) {
            	if(GT_Values.D1)System.out.println(i+" not accepted");
                return false;
            }
        	if(GT_Values.D1)System.out.println(i+" accepted");
        }
    	if(GT_Values.D1)System.out.println("Input accepted, check other values");
        if (!tTag.hasKey("output")) return false;
        mOutputItems = new ItemStack[]{GT_Utility.loadItem(tTag, "output")};
        if (mOutputItems[0] == null || !GT_Utility.isStackValid(mOutputItems[0]))
            return false;

        if (!tTag.hasKey("time")) return false;
        mMaxProgresstime = tTag.getInteger("time");
        if (mMaxProgresstime <= 0) return false;

        if (!tTag.hasKey("eu")) return false;
        mEUt = tTag.getInteger("eu");
    	if(GT_Values.D1)System.out.println("All checked start consuming inputs");
        for (int i = 0; i < 15; i++) {
            if (tStack[i] == null) continue;
            ItemStack stackInSlot = mInputBusses.get(i).getBaseMetaTileEntity().getStackInSlot(0);
            stackInSlot.stackSize -= tStack[i].stackSize;
        }

        for (int i = 0; i < 4; i++) {
            if (tFluids[i] == null) continue;
            mInputHatches.get(i).mFluid.amount -= tFluids[i].amount;
            if (mInputHatches.get(i).mFluid.amount <= 0) {
                mInputHatches.get(i).mFluid = null;
            }
        }
    	if(GT_Values.D1)System.out.println("Check overclock");

        byte tTier = (byte) Math.max(1, GT_Utility.getTier(getMaxInputVoltage()));
        this.mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
        this.mEfficiencyIncrease = 10000;
        calculateOverclockedNessMulti(mEUt, mMaxProgresstime, 1, getMaxInputVoltage());
        //In case recipe is too OP for that machine
        if (mMaxProgresstime == Integer.MAX_VALUE - 1 && mEUt == Integer.MAX_VALUE - 1)
            return false;
        this.mEUt = this.mEUt > 0 ? -this.mEUt : this.mEUt;//makes it use power...
        updateSlots();
    	if(GT_Values.D1)System.out.println("Recipe sucessfull");
        return true;
    }

    public void startSoundLoop(byte aIndex, double aX, double aY, double aZ) {
        super.startSoundLoop(aIndex, aX, aY, aZ);
        if (aIndex == 20) {
            GT_Utility.doSoundAtClient(GregTech_API.sSoundList.get(212), 10, 1.0F, aX, aY, aZ);
        }
    }

    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        int xDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetX;
        int zDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetZ;
        if (xDir != 0) {
            for (int r = 0; r <= 16; r++) {
                int i = r * xDir;

                if (i != 0 && !(aBaseMetaTileEntity.getBlockOffset(0, 0, i) == GregTech_API.sBlockCasings3 && aBaseMetaTileEntity.getMetaIDOffset(0, 0, i) == 10)) {
                    return false;
                }
                if (!aBaseMetaTileEntity.getBlockOffset(0, -1, i).getUnlocalizedName().equals("blockAlloyGlass")) {
                    return false;
                }
                IGregTechTileEntity tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(0, -2, i);
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
                    return r > 0 && mEnergyHatches.size() > 0;
                }
            }
        } else {
            for (int r = 0; r <= 16; r++) {
                int i = r * -zDir;

                if (i != 0 && !(aBaseMetaTileEntity.getBlockOffset(i, 0, 0) == GregTech_API.sBlockCasings3 && aBaseMetaTileEntity.getMetaIDOffset(i, 0, 0) == 10)) {
                    return false;
                }
                if (!aBaseMetaTileEntity.getBlockOffset(i, -1, 0).getUnlocalizedName().equals("blockAlloyGlass")) {
                    return false;
                }
                IGregTechTileEntity tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(i, -2, 0);
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
                    return r > 0 && mEnergyHatches.size() > 0;
                }
            }
        }
        return false;
    }

    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    public int getPollutionPerTick(ItemStack aStack) {
        return 0;
    }

    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return false;
    }
}
