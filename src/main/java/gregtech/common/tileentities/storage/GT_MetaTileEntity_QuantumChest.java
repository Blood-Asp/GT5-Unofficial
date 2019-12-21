package gregtech.common.tileentities.storage;

import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IMEInventory;
import appeng.api.storage.StorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;
import appeng.me.storage.MEMonitorIInventory;
import appeng.util.inv.IMEAdaptor;
import appeng.util.item.AEItemStack;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_TieredMachineBlock;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Utility;
import gregtech.common.gui.GT_Container_QuantumChest;
import gregtech.common.gui.GT_GUIContainer_QuantumChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import appeng.api.AEApi;
import appeng.api.storage.IExternalStorageHandler;
import net.minecraftforge.common.util.ForgeDirection;

public class GT_MetaTileEntity_QuantumChest extends GT_MetaTileEntity_TieredMachineBlock implements IMEInventory<IAEItemStack> {
    public int mItemCount = 0;
    public ItemStack mItemStack = null;
    public GT_MetaTileEntity_QuantumChest(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 3, "This Chest stores " + CommonSizeCompute(aTier) + " Blocks");
    }

    public GT_MetaTileEntity_QuantumChest(String aName, int aTier, String aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 3, aDescription, aTextures);
    }

    public GT_MetaTileEntity_QuantumChest(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 3, aDescription, aTextures);
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
    public boolean isValidSlot(int aIndex) {
        return true;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_QuantumChest(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) return true;
        aBaseMetaTileEntity.openGUI(aPlayer);
        return true;
    }

    @Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_Container_QuantumChest(aPlayerInventory, aBaseMetaTileEntity);
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_QuantumChest(aPlayerInventory, aBaseMetaTileEntity, getLocalName());
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {

        if (getBaseMetaTileEntity().isServerSide() && getBaseMetaTileEntity().isAllowedToWork()) {
            if ((getItemCount() <= 0)) {
                this.mItemStack = null;
                this.mItemCount = 0;
            }
            if (this.mItemStack == null && this.mInventory[0] != null) {
                this.mItemStack = mInventory[0].copy();
            }
            if ((this.mInventory[0] != null) && (this.mItemCount < getMaxItemCount()) && GT_Utility.areStacksEqual(this.mInventory[0], this.mItemStack)) {
                this.mItemCount += this.mInventory[0].stackSize;
                if (this.mItemCount > getMaxItemCount()) {
                    this.mInventory[0].stackSize = (this.mItemCount - getMaxItemCount());
                    this.mItemCount = getMaxItemCount();
                } else {
                    this.mInventory[0] = null;
                }
            }
            if (this.mInventory[1] == null && mItemStack != null) {
                this.mInventory[1] = mItemStack.copy();
                this.mInventory[1].stackSize = Math.min(mItemStack.getMaxStackSize(), this.mItemCount);
                this.mItemCount -= this.mInventory[1].stackSize;
            } else if ((this.mItemCount > 0) && GT_Utility.areStacksEqual(this.mInventory[1], this.mItemStack) && this.mInventory[1].getMaxStackSize() > this.mInventory[1].stackSize) {
                int tmp = Math.min(this.mItemCount, this.mInventory[1].getMaxStackSize() - this.mInventory[1].stackSize);
                this.mInventory[1].stackSize += tmp;
                this.mItemCount -= tmp;
            }
            if (this.mItemStack != null) {
                this.mInventory[2] = this.mItemStack.copy();
                this.mInventory[2].stackSize = Math.min(mItemStack.getMaxStackSize(), this.mItemCount);
            } else {
                this.mInventory[2] = null;
            }
        }
    }

    private int getItemCount() {
        return this.mItemCount;
    }

    public void setItemCount(int aCount) {
        this.mItemCount = aCount;
    }

    public int getProgresstime() {
        return this.mItemCount + (this.mInventory[0] == null ? 0 : this.mInventory[0].stackSize) + (this.mInventory[1] == null ? 0 : this.mInventory[1].stackSize);
    }

    public int maxProgresstime() {
        return getMaxItemCount();
    }

    private static int CommonSizeCompute(int tier){
        switch(tier){
            case 6:
                return  128000000;
            case 7:
                return  256000000;
            case 8:
                return  512000000;
            case 9:
                return 1024000000;
            case 10:
                return 2147483640;
            default:
                return 0;
        }
    }

    public int getMaxItemCount() {
        return CommonSizeCompute(mTier);
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return aIndex==1;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return aIndex==0&&(mInventory[0]==null||GT_Utility.areStacksEqual(this.mInventory[0], aStack));
    }

    @Override
    public String[] getInfoData() {

        if (mItemStack == null) {
            return new String[]{
                    EnumChatFormatting.BLUE + "Quantum Chest"+ EnumChatFormatting.RESET,
                    "Stored Items:",
                    EnumChatFormatting.GOLD+ "No Items"+ EnumChatFormatting.RESET,
                    EnumChatFormatting.GREEN + "0" + EnumChatFormatting.RESET+" "+
                    EnumChatFormatting.YELLOW + Integer.toString(getMaxItemCount())+ EnumChatFormatting.RESET
            };
        }
        return new String[]{
                EnumChatFormatting.BLUE + "Quantum Chest"+ EnumChatFormatting.RESET,
                "Stored Items:",
                EnumChatFormatting.GOLD + mItemStack.getDisplayName() + EnumChatFormatting.RESET,
                EnumChatFormatting.GREEN + Integer.toString(mItemCount) + EnumChatFormatting.RESET+" "+
                EnumChatFormatting.YELLOW + Integer.toString(getMaxItemCount())+ EnumChatFormatting.RESET
        };
    }

    @Override
    public boolean isGivingInformation() {
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("mItemCount", this.mItemCount);
        if (this.mItemStack != null)
            aNBT.setTag("mItemStack", this.mItemStack.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        if (aNBT.hasKey("mItemCount"))
            this.mItemCount = aNBT.getInteger("mItemCount");
        if (aNBT.hasKey("mItemStack"))
            this.mItemStack = ItemStack.loadItemStackFromNBT((NBTTagCompound) aNBT.getTag("mItemStack"));
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aBaseMetaTileEntity.getFrontFacing() == 0 && aSide == 4) {
            return new ITexture[]{Textures.BlockIcons.MACHINE_CASINGS[mTier][aColorIndex + 1], new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_QCHEST)};
        }
        return aSide == aBaseMetaTileEntity.getFrontFacing() ? new ITexture[]{Textures.BlockIcons.MACHINE_CASINGS[mTier][aColorIndex + 1], new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_QCHEST)} : new ITexture[]{Textures.BlockIcons.MACHINE_CASINGS[mTier][aColorIndex + 1]};//aSide != aFacing ? mMachineBlock != 0 ? new ITexture[] {Textures.BlockIcons.CASING_BLOCKS[mMachineBlock]} : new ITexture[] {Textures.BlockIcons.MACHINE_CASINGS[mTier][aColorIndex+1]} : mMachineBlock != 0 ? aActive ? getTexturesActive(Textures.BlockIcons.CASING_BLOCKS[mMachineBlock]) : getTexturesInactive(Textures.BlockIcons.CASING_BLOCKS[mMachineBlock]) : aActive ? getTexturesActive(Textures.BlockIcons.MACHINE_CASINGS[mTier][aColorIndex+1]) : getTexturesInactive(Textures.BlockIcons.MACHINE_CASINGS[mTier][aColorIndex+1]);
    }

    @Override
    public ITexture[][][] getTextureSet(ITexture[] aTextures) {
        return new ITexture[0][0][0];
    }
    @Override
    public IAEItemStack injectItems(final IAEItemStack input, final Actionable mode, final BaseActionSource src ) {
        final ItemStack inputStack = input.getItemStack();
        if (inputStack == null)
            return null;
        if (mItemStack != null) {
            if (GT_Utility.areStacksEqual(mItemStack, inputStack)) {
                if (input.getStackSize() + mItemCount > getMaxItemCount())
                    return createOverflowStack(input.getStackSize() + mItemCount, mode);
                else
                    mItemCount += input.getStackSize();
                return null;
            } else
                return input;
        } else {
            if (mode == Actionable.MODULATE)
                mItemStack = inputStack.copy();
            if (input.getStackSize() > getMaxItemCount())
                return createOverflowStack(input.getStackSize(), mode);
            else if (mode == Actionable.MODULATE)
                mItemCount = (int) input.getStackSize();
            return null;
        }
    }

    private IAEItemStack createOverflowStack(long size, Actionable mode) {
        final IAEItemStack overflow = AEItemStack.create(mItemStack);
        overflow.setStackSize(size - getMaxItemCount());
        if (mode == Actionable.MODULATE)
            mItemCount = getMaxItemCount();
        return overflow;
    }

    @Override
    public IAEItemStack extractItems( final IAEItemStack request, final Actionable mode, final BaseActionSource src ) {
        if (request.equals(mItemStack)) {
            if (request.getStackSize() >= mItemCount) {
                AEItemStack result = AEItemStack.create(mItemStack);
                result.setStackSize(mItemCount);
                if (mode == Actionable.MODULATE)
                    mItemCount = 0;
                return result;
            } else {
                if (mode == Actionable.MODULATE)
                    mItemCount -= (int) request.getStackSize();
                return request.copy();
            }
        }
        return null;
    }

    @Override
    public StorageChannel getChannel()
    {
        return StorageChannel.ITEMS;
    }

    @Override
    public IItemList<IAEItemStack> getAvailableItems(final IItemList<IAEItemStack> out )
    {
        if (mItemStack != null) {
            AEItemStack s = AEItemStack.create(mItemStack);
            s.setStackSize(mItemCount);
            out.add(s);
        }
        return out;
    }

    static class AEHandler implements IExternalStorageHandler
    {
        @Override
        public boolean canHandle(final TileEntity te, final ForgeDirection d, final StorageChannel chan, final BaseActionSource mySrc )
        {
            return chan == StorageChannel.ITEMS && te instanceof BaseMetaTileEntity && ((BaseMetaTileEntity)te).getMetaTileEntity() instanceof GT_MetaTileEntity_QuantumChest;
        }
        public IMEInventory getInventory(final TileEntity te, final ForgeDirection d, final StorageChannel chan, final BaseActionSource src ) {
            if (chan == StorageChannel.ITEMS) {
                return new MEMonitorIInventory( new IMEAdaptor( (GT_MetaTileEntity_QuantumChest)(((BaseMetaTileEntity)te).getMetaTileEntity()), src));
            }
            return null;
        }
    }
    public static void registerAEIntegration() {
        AEApi.instance().registries().externalStorage().addExternalStorageInterface( new AEHandler() );
    }
}
