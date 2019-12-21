package gregtech.common.tileentities.storage;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.storage.IExternalStorageHandler;
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
import net.minecraftforge.common.util.ForgeDirection;

public abstract class GT_MetaTileEntity_DigitalChestBase extends GT_MetaTileEntity_TieredMachineBlock implements IMEInventory<IAEItemStack> {
    public GT_MetaTileEntity_DigitalChestBase(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 3, "This Chest stores " + CommonSizeCompute(aTier) + " Blocks");
    }

    public GT_MetaTileEntity_DigitalChestBase(String aName, int aTier, String aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 3, aDescription, aTextures);
    }

    public GT_MetaTileEntity_DigitalChestBase(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
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
                setItemStack(null);
                setItemCount(0);
            }
            if (getItemStack() == null && mInventory[0] != null) {
                setItemStack(mInventory[0].copy());
            }
            int count = getItemCount();
            ItemStack stack = getItemStack();
            if ((mInventory[0] != null) && (count < getMaxItemCount()) && GT_Utility.areStacksEqual(mInventory[0], stack)) {
                count += mInventory[0].stackSize;
                if (count > getMaxItemCount()) {
                    mInventory[0].stackSize = (count - getMaxItemCount());
                    count = getMaxItemCount();
                } else {
                    mInventory[0] = null;
                }
            }
            if (mInventory[1] == null && stack != null) {
                mInventory[1] = stack.copy();
                mInventory[1].stackSize = Math.min(stack.getMaxStackSize(), count);
                count -= mInventory[1].stackSize;
            } else if ((count > 0) && GT_Utility.areStacksEqual(mInventory[1], stack) && mInventory[1].getMaxStackSize() > mInventory[1].stackSize) {
                int tmp = Math.min(count, mInventory[1].getMaxStackSize() - mInventory[1].stackSize);
                mInventory[1].stackSize += tmp;
                count -= tmp;
            }
            setItemCount(count);
            if (stack != null) {
                mInventory[2] = stack.copy();
                mInventory[2].stackSize = Math.min(stack.getMaxStackSize(), count);
            } else {
                mInventory[2] = null;
            }
        }
    }

    abstract protected int getItemCount();
    abstract public void setItemCount(int aCount);
    abstract protected ItemStack getItemStack();
    abstract protected void setItemStack(ItemStack s);

    @Override
    public int getProgresstime() {
        return getItemCount() + (mInventory[0] == null ? 0 : mInventory[0].stackSize) + (mInventory[1] == null ? 0 : mInventory[1].stackSize);
    }

    @Override
    public int maxProgresstime() {
        return getMaxItemCount();
    }

    protected static int CommonSizeCompute(int tier){
        switch(tier){
            case 1:
                return    4000000;
            case 2:
                return    8000000;
            case 3:
                return   16000000;
            case 4:
                return   32000000;
            case 5:
                return   64000000;
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

    @Override
    public int getMaxItemCount() {
        return CommonSizeCompute(mTier);
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return aIndex==1;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return aIndex==0&&(mInventory[0]==null||GT_Utility.areStacksEqual(mInventory[0], aStack));
    }
    abstract protected String chestName();
    @Override
    public String[] getInfoData() {

        if (getItemStack() == null) {
            return new String[]{
                    EnumChatFormatting.BLUE + chestName() + EnumChatFormatting.RESET,
                    "Stored Items:",
                    EnumChatFormatting.GOLD+ "No Items"+ EnumChatFormatting.RESET,
                    EnumChatFormatting.GREEN + "0" + EnumChatFormatting.RESET+" "+
                            EnumChatFormatting.YELLOW + Integer.toString(getMaxItemCount())+ EnumChatFormatting.RESET
            };
        }
        return new String[]{
                EnumChatFormatting.BLUE + chestName() + EnumChatFormatting.RESET,
                "Stored Items:",
                EnumChatFormatting.GOLD + getItemStack().getDisplayName() + EnumChatFormatting.RESET,
                EnumChatFormatting.GREEN + Integer.toString(getItemCount()) + EnumChatFormatting.RESET+" "+
                        EnumChatFormatting.YELLOW + Integer.toString(getMaxItemCount())+ EnumChatFormatting.RESET
        };
    }

    @Override
    public boolean isGivingInformation() {
        return true;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("mItemCount", getItemCount());
        if (getItemStack() != null)
            aNBT.setTag("mItemStack", getItemStack().writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        if (aNBT.hasKey("mItemCount"))
            setItemCount(aNBT.getInteger("mItemCount"));
        if (aNBT.hasKey("mItemStack"))
            setItemStack(ItemStack.loadItemStackFromNBT((NBTTagCompound) aNBT.getTag("mItemStack")));
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
        ItemStack storedStack = getItemStack();
        if (storedStack != null) {
            if (GT_Utility.areStacksEqual(storedStack, inputStack)) {
                if (input.getStackSize() + getItemCount() > getMaxItemCount())
                    return createOverflowStack(input.getStackSize() + getItemCount(), mode);
                else
                    setItemCount(getItemCount() + (int)input.getStackSize());
                return null;
            } else
                return input;
        } else {
            if (mode != Actionable.SIMULATE)
                setItemStack(inputStack.copy());
            if (input.getStackSize() > getMaxItemCount())
                return createOverflowStack(input.getStackSize(), mode);
            else if (mode != Actionable.SIMULATE)
                setItemCount((int)input.getStackSize());
            return null;
        }
    }

    private IAEItemStack createOverflowStack(long size, Actionable mode) {
        final IAEItemStack overflow = AEItemStack.create(getItemStack());
        overflow.setStackSize(size - getMaxItemCount());
        if (mode != Actionable.SIMULATE)
            setItemCount(getMaxItemCount());
        return overflow;
    }

    @Override
    public IAEItemStack extractItems( final IAEItemStack request, final Actionable mode, final BaseActionSource src ) {
        if (request.equals(getItemStack())) {
            if (request.getStackSize() >= getItemCount()) {
                AEItemStack result = AEItemStack.create(getItemStack());
                result.setStackSize(getItemCount());
                if (mode != Actionable.SIMULATE)
                    setItemCount(0);
                return result;
            } else {
                if (mode != Actionable.SIMULATE)
                    setItemCount(getItemCount() - (int) request.getStackSize());
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
        ItemStack storedStack = getItemStack();
        if (storedStack != null) {
            AEItemStack s = AEItemStack.create(storedStack);
            s.setStackSize(getItemCount());
            out.add(s);
        }
        return out;
    }

    static class AEHandler implements IExternalStorageHandler
    {
        @Override
        public boolean canHandle(final TileEntity te, final ForgeDirection d, final StorageChannel chan, final BaseActionSource mySrc )
        {
            return chan == StorageChannel.ITEMS && te instanceof BaseMetaTileEntity && ((BaseMetaTileEntity)te).getMetaTileEntity() instanceof GT_MetaTileEntity_DigitalChestBase;
        }
        public IMEInventory getInventory(final TileEntity te, final ForgeDirection d, final StorageChannel chan, final BaseActionSource src ) {
            if (chan == StorageChannel.ITEMS) {
                return new MEMonitorIInventory( new IMEAdaptor( (GT_MetaTileEntity_DigitalChestBase)(((BaseMetaTileEntity)te).getMetaTileEntity()), src));
            }
            return null;
        }
    }
    public static void registerAEIntegration() {
        AEApi.instance().registries().externalStorage().addExternalStorageInterface( new GT_MetaTileEntity_DigitalChestBase.AEHandler() );
    }
}
