package gregtech.api.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicTank;
import gregtech.api.util.GT_Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

/**
 * NEVER INCLUDE THIS FILE IN YOUR MOD!!!
 * <p/>
 * The Container I use for all my Basic Tanks
 */
public class GT_Container_BasicTank extends GT_ContainerMetaTile_Machine {

    public int mContent = 0;

    public GT_Container_BasicTank(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity) {
        super(aInventoryPlayer, aTileEntity);
    }

    @Override
    public void addSlots(InventoryPlayer aInventoryPlayer) {
        addSlotToContainer(new Slot(mTileEntity, 0, 80, 17));
        addSlotToContainer(new GT_Slot_Output(mTileEntity, 1, 80, 53));
        addSlotToContainer(new GT_Slot_Render(mTileEntity, 2, 59, 42));
    }

    @Override
    public ItemStack slotClick(int aSlotIndex, int aMouseclick, int aShifthold, EntityPlayer aPlayer) {
        if (aSlotIndex == 2 && aMouseclick < 2) {
            if (mTileEntity.isClientSide()) {
                /*
                 * While a logical client don't really need to process fluid cells upon click (it could have just wait
                 * for server side to send the result), doing so would result in every fluid interaction having a
                 * noticeable delay between clicking and changes happening even on single player.
                 * I'd imagine this delay to get only more severe when playing MP over ethernet, which would have much more latency
                 * than a memory connection
                 */
                GT_MetaTileEntity_BasicTank tTank = (GT_MetaTileEntity_BasicTank) mTileEntity.getMetaTileEntity();
                tTank.setDrainableStack(GT_Utility.getFluidFromDisplayStack(tTank.getStackInSlot(2)));
            }
            GT_MetaTileEntity_BasicTank tTank = (GT_MetaTileEntity_BasicTank) mTileEntity.getMetaTileEntity();
            IFluidAccess tDrainableAccess = IFluidAccess.from(tTank, false);
            return handleFluidSlotClick(tDrainableAccess, aPlayer, aMouseclick == 0, true, !tTank.isDrainableStackSeparate());
        }
        if (mTileEntity.isServerSide()) {
            aPlayer.addChatComponentMessage(new ChatComponentText(String.format("Slot Index: %d, aMouseClick: %d, Shift Hold: %d", aShifthold, aMouseclick, aShifthold)));
        }
        return super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
    }

    /**
     * Expected to be called on client side only. Load fluid stacks from fluid display items as they were not sent
     * over the network.
     * Override this if you have more than one fluid display stack. This implementation will set drainable stack according to items
     * in slot indexed 2.
     *
     */
    protected void syncFluidFromFluidDisplayItems() {
        GT_MetaTileEntity_BasicTank tTank = (GT_MetaTileEntity_BasicTank) mTileEntity.getMetaTileEntity();
        tTank.setDrainableStack(GT_Utility.getFluidFromDisplayStack(tTank.getStackInSlot(2)));
    }

    protected static ItemStack handleFluidSlotClick(IFluidAccess aFluidAccess, EntityPlayer aPlayer, boolean aProcessFullStack, boolean aCanDrain, boolean aCanFill) {
        ItemStack tStackHeld = aPlayer.inventory.getItemStack();
        ItemStack tStackSizedOne = GT_Utility.copyAmount(1, tStackHeld);
        if (tStackSizedOne == null || tStackHeld.stackSize == 0) return null;
        FluidStack tInputFluid = aFluidAccess.get();
        FluidStack tFluidHeld = GT_Utility.getFluidForFilledItem(tStackSizedOne, true);
        if (tFluidHeld != null && tFluidHeld.amount <= 0)
            tFluidHeld = null;
        if (tInputFluid == null) {
            // tank empty, consider fill only from now on
            if (!aCanFill)
                // cannot fill and nothing to take, bail out
                return null;
            if (tFluidHeld == null)
                // no fluid to fill
                return null;
            return fillFluid(aFluidAccess, aPlayer, tFluidHeld, aProcessFullStack);
        }
        // tank not empty, both action possible
        if (tFluidHeld != null && tInputFluid.amount < aFluidAccess.getCapacity()) {
            // both nonnull and have space left for filling.
            if (aCanFill)
                // actually both pickup and fill is reasonable, but I'll go with fill here
                return fillFluid(aFluidAccess, aPlayer, tFluidHeld, aProcessFullStack);
            if (!aCanDrain)
                // cannot take AND cannot fill, why make this call then?
                return null;
            // the slot does not allow filling, so try take some
            return drainFluid(aFluidAccess.get(), aPlayer, aProcessFullStack);
        } else {
            // cannot fill and there is something to take
            if (!aCanDrain)
                // but the slot does not allow taking, so bail out
                return null;
            ItemStack tResultStack = drainFluid(tInputFluid, aPlayer, aProcessFullStack);
            if (tInputFluid.amount == 0)
                aFluidAccess.set(null);
            return tResultStack;
        }
    }

    protected static ItemStack drainFluid(FluidStack aTankStack, EntityPlayer aPlayer, boolean aProcessFullStack) {
        if (aTankStack == null) return null;
        ItemStack tStackHeld = aPlayer.inventory.getItemStack();
        ItemStack tStackSizedOne = GT_Utility.copyAmount(1, tStackHeld);
        if (tStackSizedOne == null || tStackHeld.stackSize == 0) return null;
        int tOriginalFluidAmount = aTankStack.amount;
        ItemStack tFilled = GT_Utility.fillFluidContainer(aTankStack, tStackSizedOne, true, false);
        if (tFilled == null && tStackSizedOne.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem tContainerItem = (IFluidContainerItem) tStackSizedOne.getItem();
            int tFilledAmount = tContainerItem.fill(tStackSizedOne, aTankStack, true);
            if (tFilledAmount > 0) {
                tFilled = tStackSizedOne;
                aTankStack.amount -= tFilledAmount;
            }
        }
        if (tFilled != null) {
            if (aProcessFullStack) {
                int tFilledAmount = tOriginalFluidAmount - aTankStack.amount;
                /*
                 work out how many more items we can fill
                 one cell is already used, so account for that
                 the round down behavior will left over a fraction of a cell worth of fluid
                 the user then get to decide what to do with it
                 it will not be too fancy if it spills out partially filled cells
                */
                int tAdditionalParallel = Math.min(tStackHeld.stackSize - 1, aTankStack.amount / tFilledAmount);
                aTankStack.amount -= tFilledAmount * tAdditionalParallel;
                tFilled.stackSize += tAdditionalParallel;
            }
            replaceCursorItemStack(aPlayer, tFilled);
        }
        return tFilled;
    }

    protected static ItemStack fillFluid(IFluidAccess aFluidAccess, EntityPlayer aPlayer, FluidStack aFluidHeld, boolean aProcessFullStack) {
        // we are not using aMachine.fill() here any more, so we need to check for fluid type here ourselves
        if (aFluidAccess.get() != null && !aFluidAccess.get().isFluidEqual(aFluidHeld))
            return null;
        ItemStack tStackHeld = aPlayer.inventory.getItemStack();
        ItemStack tStackSizedOne = GT_Utility.copyAmount(1, tStackHeld);
        if (tStackSizedOne == null)
            return null;

        int tFreeSpace = aFluidAccess.getCapacity() - (aFluidAccess.get() != null ? aFluidAccess.get().amount : 0);
        if (tFreeSpace <= 0)
            // no space left
            return null;

        // find out how much fluid can be taken
        // some cells cannot be partially filled
        ItemStack tStackEmptied = null;
        int tAmountTaken = 0;
        if (tFreeSpace >= aFluidHeld.amount) {
            // fully accepted - try take it from item now
            // IFluidContainerItem is intentionally not checked here. it will be checked later
            tStackEmptied = GT_Utility.getContainerForFilledItem(tStackSizedOne, false);
            tAmountTaken = aFluidHeld.amount;
        }
        if (tStackEmptied == null && tStackSizedOne.getItem() instanceof IFluidContainerItem) {
            // either partially accepted, or is IFluidContainerItem
            IFluidContainerItem container = (IFluidContainerItem) tStackSizedOne.getItem();
            FluidStack tDrained = container.drain(tStackSizedOne, tFreeSpace, true);
            if (tDrained != null && tDrained.amount > 0) {
                // something is actually drained - change the cell and drop it to player
                tStackEmptied = tStackSizedOne;
                tAmountTaken = tDrained.amount;
            }
        }
        if (tStackEmptied == null)
            // somehow the cell refuse to give out that amount of fluid, no op then
            return null;

        // find out how many fill can we do
        // same round down behavior as above
        // however here the fluid stack is not changed at all, so the exact code will slightly differ
        int tParallel = aProcessFullStack ? Math.min(tFreeSpace / tAmountTaken, tStackHeld.stackSize) : 1;
        if (aFluidAccess.get() == null) {
            FluidStack tNewFillableStack = aFluidHeld.copy();
            tNewFillableStack.amount = tAmountTaken * tParallel;
            aFluidAccess.set(tNewFillableStack);
        } else {
            aFluidAccess.get().amount += tAmountTaken * tParallel;
        }
        tStackEmptied.stackSize = tParallel;
        replaceCursorItemStack(aPlayer, tStackEmptied);
        return tStackEmptied;
    }

    private static void replaceCursorItemStack(EntityPlayer aPlayer, ItemStack tStackResult) {
        int tStackResultMaxStackSize = tStackResult.getMaxStackSize();
        while (tStackResult.stackSize > tStackResultMaxStackSize) {
            aPlayer.inventory.getItemStack().stackSize -= tStackResultMaxStackSize;
            GT_Utility.addItemToPlayerInventory(aPlayer, tStackResult.splitStack(tStackResultMaxStackSize));
        }
        if (aPlayer.inventory.getItemStack().stackSize == tStackResult.stackSize) {
            // every cell is mutated. it could just stay on the cursor.
            aPlayer.inventory.setItemStack(tStackResult);
        } else {
            // some cells not mutated. The mutated cells must go into the inventory
            // or drop into the world if there isn't enough space.
            ItemStack tStackHeld = aPlayer.inventory.getItemStack();
            tStackHeld.stackSize -= tStackResult.stackSize;
            GT_Utility.addItemToPlayerInventory(aPlayer, tStackResult);
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (mTileEntity.isClientSide() || mTileEntity.getMetaTileEntity() == null) return;
        if (((GT_MetaTileEntity_BasicTank) mTileEntity.getMetaTileEntity()).mFluid != null)
            mContent = ((GT_MetaTileEntity_BasicTank) mTileEntity.getMetaTileEntity()).mFluid.amount;
        else
            mContent = 0;
        for (Object crafter : this.crafters) {
            ICrafting var1 = (ICrafting) crafter;
            var1.sendProgressBarUpdate(this, 100, mContent & 65535);
            var1.sendProgressBarUpdate(this, 101, mContent >>> 16);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
        super.updateProgressBar(par1, par2);
        switch (par1) {
            case 100:
                mContent = mContent & -65536 | par2;
                break;
            case 101:
                mContent = mContent & 65535 | par2 << 16;
                break;
        }
    }

    @Override
    public int getSlotCount() {
        return 2;
    }

    @Override
    public int getShiftClickSlotCount() {
        return 1;
    }

    protected interface IFluidAccess {
        void set(FluidStack stack);
        FluidStack get();
        int getCapacity();
        static IFluidAccess from(GT_MetaTileEntity_BasicTank aTank, boolean aIsFillableStack) {
            return new BasicTankFluidAccess(aTank, aIsFillableStack);
        }
    }

    static class BasicTankFluidAccess implements IFluidAccess {
        private final GT_MetaTileEntity_BasicTank mTank;
        private final boolean mIsFillableStack;

        public BasicTankFluidAccess(GT_MetaTileEntity_BasicTank aTank, boolean aIsFillableStack) {
            this.mTank = aTank;
            this.mIsFillableStack = aIsFillableStack;
        }

        @Override
        public void set(FluidStack stack) {
            if (mIsFillableStack)
                mTank.setFillableStack(stack);
            else
                mTank.setDrainableStack(stack);
        }

        @Override
        public FluidStack get() {
            return mIsFillableStack ? mTank.getFillableStack() : mTank.getDrainableStack();
        }

        @Override
        public int getCapacity() {
            return mTank.getCapacity();
        }
    }
}
