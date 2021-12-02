package gregtech.api.metatileentity.implementations;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_LanguageManager;
import gregtech.api.util.GT_Utility;
import gregtech.common.gui.GT_Container_OutputHatch;
import gregtech.common.gui.GT_GUIContainer_OutputHatch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_PIPE_OUT;

public class GT_MetaTileEntity_Hatch_Output extends GT_MetaTileEntity_Hatch {
    private String lockedFluidName = null;
    private EntityPlayer playerThatLockedfluid = null;
    public byte mMode = 0;

    public GT_MetaTileEntity_Hatch_Output(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 4, new String[]{
                "Fluid Output for Multiblocks",
                "Capacity: "  + GT_Utility.formatNumbers(8000*(1<<aTier)) + "L",
                "Right click with screwdriver to restrict output",
                "Can be restricted to put out Items and/or Steam/No Steam/1 specific Fluid",
                "Restricted Output Hatches are given priority for Multiblock Fluid output"});
    }

    public GT_MetaTileEntity_Hatch_Output(String aName, int aTier, String aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 4, aDescription, aTextures);
    }

    public GT_MetaTileEntity_Hatch_Output(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 4, aDescription, aTextures);
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_OUT)};
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_PIPE_OUT)};
    }

    @Override
    public boolean isSimpleMachine() {
        return true;
    }

    @Override
    public boolean isFacingValid(byte aFacing) {
        return true;
    }

    @Override
    public boolean isAccessAllowed(EntityPlayer aPlayer) {
        return true;
    }

    @Override
    public boolean isLiquidInput(byte aSide) {
        return false;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_Hatch_Output(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) return true;
        aBaseMetaTileEntity.openGUI(aPlayer);
        return true;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isServerSide() && aBaseMetaTileEntity.isAllowedToWork() && mFluid != null) {
            IFluidHandler tTileEntity = aBaseMetaTileEntity.getITankContainerAtSide(aBaseMetaTileEntity.getFrontFacing());
            if (tTileEntity != null) {
                FluidStack tDrained = aBaseMetaTileEntity.drain(ForgeDirection.getOrientation(aBaseMetaTileEntity.getFrontFacing()), Math.max(1, mFluid.amount), false);
                if (tDrained != null) {
                    int tFilledAmount = tTileEntity.fill(ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()), tDrained, false);
                    if (tFilledAmount > 0) {
                        tTileEntity.fill(ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()), aBaseMetaTileEntity.drain(ForgeDirection.getOrientation(aBaseMetaTileEntity.getFrontFacing()), tFilledAmount, true), true);
                    }
                }
            }
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setByte("mMode", mMode);
        if(lockedFluidName!=null && lockedFluidName.length()!=0) aNBT.setString("lockedFluidName", lockedFluidName);
        else aNBT.removeTag("lockedFluidName");
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mMode = aNBT.getByte("mMode");
        lockedFluidName = aNBT.getString("lockedFluidName");
        lockedFluidName = lockedFluidName.length() == 0 ? null : lockedFluidName;
    }

    @Override
    public boolean doesFillContainers() {
        return true;
    }

    @Override
    public boolean doesEmptyContainers() {
        return false;
    }

    @Override
    public boolean canTankBeFilled() {
        return true;
    }

    @Override
    public boolean canTankBeEmptied() {
        return true;
    }

    @Override
    public boolean displaysItemStack() {
        return true;
    }

    @Override
    public boolean displaysStackSize() {
        return false;
    }

    @Override
    public void updateFluidDisplayItem() {
        super.updateFluidDisplayItem();
        if (lockedFluidName == null || mMode < 8) mInventory[3] = null;
        else {
            FluidStack tLockedFluid = FluidRegistry.getFluidStack(lockedFluidName.replace("fluid.", "")
                .replace("tile.", "").replace(".name", "").replace("ic2.fluid", "ic2").toLowerCase(), 1);
            // Because getStackDisplaySlot() only allow return one int, this place I only can manually set.
            if (tLockedFluid != null) {
                mInventory[3] = GT_Utility.getFluidDisplayStack(tLockedFluid, false, true);
            }
            else {
                mInventory[3] = null;
            }
        }
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        // Because getStackDisplaySlot() only allow return one int, this place I only can manually set.
        return aIndex != getStackDisplaySlot() && aIndex != 3;
    }

    @Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_Container_OutputHatch(aPlayerInventory, aBaseMetaTileEntity);
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_OutputHatch(aPlayerInventory, aBaseMetaTileEntity, getLocalName());
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return aSide == aBaseMetaTileEntity.getFrontFacing() && aIndex == 1;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return aSide == aBaseMetaTileEntity.getFrontFacing() && aIndex == 0;
    }

    @Override
    public int getCapacity() {
        return 8000*(1<<mTier);
    }

    @Override
    public void onScrewdriverRightClick(byte aSide, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (!getBaseMetaTileEntity().getCoverBehaviorAtSideNew(aSide).isGUIClickable(aSide, getBaseMetaTileEntity().getCoverIDAtSide(aSide), getBaseMetaTileEntity().getComplexCoverDataAtSide(aSide), getBaseMetaTileEntity()))
            return;
        if (aPlayer.isSneaking()) {
            mMode = (byte) ((mMode + 9) % 10);
        } else {
            mMode = (byte) ((mMode + 1) % 10);
        }
        String inBrackets;
        switch (mMode) {
            case 0:
                GT_Utility.sendChatToPlayer(aPlayer, trans("108","Outputs misc. Fluids, Steam and Items"));
                this.setLockedFluidName(null);
                break;
            case 1:
                GT_Utility.sendChatToPlayer(aPlayer, trans("109","Outputs Steam and Items"));
                this.setLockedFluidName(null);
                break;
            case 2:
                GT_Utility.sendChatToPlayer(aPlayer, trans("110","Outputs Steam and misc. Fluids"));
                this.setLockedFluidName(null);
                break;
            case 3:
                GT_Utility.sendChatToPlayer(aPlayer, trans("111","Outputs Steam"));
                this.setLockedFluidName(null);
                break;
            case 4:
                GT_Utility.sendChatToPlayer(aPlayer, trans("112","Outputs misc. Fluids and Items"));
                this.setLockedFluidName(null);
                break;
            case 5:
                GT_Utility.sendChatToPlayer(aPlayer, trans("113","Outputs only Items"));
                this.setLockedFluidName(null);
                break;
            case 6:
                GT_Utility.sendChatToPlayer(aPlayer, trans("114","Outputs only misc. Fluids"));
                this.setLockedFluidName(null);
                break;
            case 7:
                GT_Utility.sendChatToPlayer(aPlayer, trans("115","Outputs nothing"));
                this.setLockedFluidName(null);
                break;
            case 8:
                playerThatLockedfluid = aPlayer;
                if (mFluid == null) {
                    this.setLockedFluidName(null);
                    inBrackets = trans("115.3","currently none, will be locked to the next that is put in (or use fluid cell to lock)");
                } else {
                    this.setLockedFluidName(this.getDrainableStack().getUnlocalizedName());
                    inBrackets = this.getDrainableStack().getLocalizedName();
                }
                GT_Utility.sendChatToPlayer(aPlayer, String.format("%s (%s)", trans("151.1", "Outputs items and 1 specific Fluid"), inBrackets));
                break;
            case 9:
                playerThatLockedfluid = aPlayer;
                if (mFluid == null) {
                    this.setLockedFluidName(null);
                    inBrackets = trans("115.3","currently none, will be locked to the next that is put in (or use fluid cell to lock)");
                } else {
                    this.setLockedFluidName(this.getDrainableStack().getUnlocalizedName());
                    inBrackets = this.getDrainableStack().getLocalizedName();
                }
                GT_Utility.sendChatToPlayer(aPlayer, String.format("%s (%s)", trans("151.2", "Outputs 1 specific Fluid"), inBrackets));
                break;
        }
    }

    private boolean tryToLockHatch(EntityPlayer aPlayer, byte aSide) {
        if (!getBaseMetaTileEntity().getCoverBehaviorAtSideNew(aSide).isGUIClickable(aSide, getBaseMetaTileEntity().getCoverIDAtSide(aSide), getBaseMetaTileEntity().getComplexCoverDataAtSide(aSide), getBaseMetaTileEntity()))
            return false;
        if (!isFluidLocked())
            return false;
        ItemStack tCurrentItem = aPlayer.inventory.getCurrentItem();
        if (tCurrentItem == null)
            return false;
        FluidStack tFluid = FluidContainerRegistry.getFluidForFilledItem(tCurrentItem);
        if (tFluid == null && tCurrentItem.getItem() instanceof IFluidContainerItem)
            tFluid = ((IFluidContainerItem)tCurrentItem.getItem()).getFluid(tCurrentItem);
        if (tFluid != null) {
            if (getLockedFluidName() != null && !getLockedFluidName().equals(tFluid.getUnlocalizedName())) {
                GT_Utility.sendChatToPlayer(aPlayer, String.format("%s %s", trans("151.3",
                        "Hatch is locked to a different fluid. To change the locking, empty it and made it locked to the next fluid with a screwdriver. Currently locked to")
                        , StatCollector.translateToLocal(getLockedFluidName())));
            }
            else {
                setLockedFluidName(tFluid.getUnlocalizedName());
                if (mMode == 8)
                    GT_Utility.sendChatToPlayer(aPlayer, String.format("%s (%s)", trans("151.1", "Outputs items and 1 specific Fluid"), tFluid.getLocalizedName()));
                else
                    GT_Utility.sendChatToPlayer(aPlayer, String.format("%s (%s)", trans("151.2", "Outputs 1 specific Fluid"), tFluid.getLocalizedName()));
            }
            return true;
        }
        return false;
    }

    public byte getMode() {
        return mMode;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer, byte aSide, float aX, float aY, float aZ) {
        if (tryToLockHatch(aPlayer, aSide))
            return true;
        return super.onRightclick(aBaseMetaTileEntity, aPlayer, aSide, aX, aY, aZ);
    }

    @Override
    public String trans(String aKey, String aEnglish){
        return GT_LanguageManager.addStringLocalization("Interaction_DESCRIPTION_Index_"+aKey, aEnglish, false);
    }

    public boolean outputsSteam() {
        return mMode < 4;
    }

    public boolean outputsLiquids() {
        return mMode % 2 == 0 || mMode == 9;
    }

    public boolean outputsItems() {
        return mMode % 4 < 2 && mMode != 9;
    }

    public boolean isFluidLocked() {
        return mMode == 8 || mMode == 9;
    }

    public String getLockedFluidName() {
        return lockedFluidName;
    }

    public void setLockedFluidName(String lockedFluidName) {
        this.lockedFluidName = lockedFluidName;
    }

    @Override
    public int getTankPressure() {
        return +100;
    }

    @Override
    protected void onEmptyingContainerWhenEmpty() {
        if (this.lockedFluidName == null && this.mFluid != null) {
            this.setLockedFluidName(this.mFluid.getUnlocalizedName());
            GT_Utility.sendChatToPlayer(playerThatLockedfluid, String.format(trans("151.4","Sucessfully locked Fluid to %s"), mFluid.getLocalizedName()));
        }
    }

    @Override
    public boolean isGivingInformation() {
        return true;
    }

    @Override
    public String[] getInfoData() {
        return new String[]{
                EnumChatFormatting.BLUE + "Output Hatch" + EnumChatFormatting.RESET,
                "Stored Fluid:",
                EnumChatFormatting.GOLD + (mFluid == null ? "No Fluid" : mFluid.getLocalizedName()) + EnumChatFormatting.RESET,
                EnumChatFormatting.GREEN + GT_Utility.formatNumbers(mFluid == null ? 0 : mFluid.amount) + " L" + EnumChatFormatting.RESET + " " +
                        EnumChatFormatting.YELLOW + GT_Utility.formatNumbers(getCapacity()) + " L"+ EnumChatFormatting.RESET,
            (!isFluidLocked() || lockedFluidName == null) ? "Not Locked" : ("Locked to " + StatCollector.translateToLocal(getLockedFluidName()))
        };
    }
}