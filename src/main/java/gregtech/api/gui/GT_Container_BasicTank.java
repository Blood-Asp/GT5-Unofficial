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
    private int oContent = 0;

    public GT_Container_BasicTank(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity) {
        super(aInventoryPlayer, aTileEntity);
    }

    /**
     * Subclasses must ensure third slot (aSlotIndex==2) is drainable fluid display item slot.
     * Otherwise, subclasses must intercept the appropriate the slotClick event and call super.slotClick(2, xxx) if necessary
     */
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
                 * I'd imagine this lag to become only more severe when playing MP over ethernet, which would have much more latency
                 * than a memory connection
                 */
                GT_MetaTileEntity_BasicTank tTank = (GT_MetaTileEntity_BasicTank) mTileEntity.getMetaTileEntity();
                tTank.setDrainableStack(GT_Utility.getFluidFromDisplayStack(tTank.getStackInSlot(2)));
            }
            GT_MetaTileEntity_BasicTank tTank = (GT_MetaTileEntity_BasicTank) mTileEntity.getMetaTileEntity();
            IFluidAccess tDrainableAccess = IFluidAccess.from(tTank, false);
            return handleFluidSlotClick(tDrainableAccess, aPlayer, aMouseclick == 0, true, !tTank.isDrainableStackSeparate());
        }
        return super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
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
            return drainFluid(aFluidAccess, aPlayer, aProcessFullStack);
        } else {
            // cannot fill and there is something to take
            if (!aCanDrain)
                // but the slot does not allow taking, so bail out
                return null;
            return drainFluid(aFluidAccess, aPlayer, aProcessFullStack);
        }
    }

    protected static ItemStack drainFluid(IFluidAccess aFluidAccess, EntityPlayer aPlayer, boolean aProcessFullStack) {
        FluidStack tTankStack = aFluidAccess.get();
        if (tTankStack == null) return null;
        ItemStack tStackHeld = aPlayer.inventory.getItemStack();
        ItemStack tStackSizedOne = GT_Utility.copyAmount(1, tStackHeld);
        if (tStackSizedOne == null || tStackHeld.stackSize == 0) return null;
        int tOriginalFluidAmount = tTankStack.amount;
        ItemStack tFilledContainer = GT_Utility.fillFluidContainer(tTankStack, tStackSizedOne, true, false);
        if (tFilledContainer == null && tStackSizedOne.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem tContainerItem = (IFluidContainerItem) tStackSizedOne.getItem();
            int tFilledAmount = tContainerItem.fill(tStackSizedOne, tTankStack, true);
            if (tFilledAmount > 0) {
                tFilledContainer = tStackSizedOne;
                tTankStack.amount -= tFilledAmount;
            }
        }
        if (tFilledContainer != null) {
            if (aProcessFullStack) {
                int tFilledAmount = tOriginalFluidAmount - tTankStack.amount;
                /*
                 work out how many more items we can fill
                 one cell is already used, so account for that
                 the round down behavior will left over a fraction of a cell worth of fluid
                 the user then get to decide what to do with it
                 it will not be too fancy if it spills out partially filled cells
                */
                int tAdditionalParallel = Math.min(tStackHeld.stackSize - 1, tTankStack.amount / tFilledAmount);
                tTankStack.amount -= tFilledAmount * tAdditionalParallel;
                tFilledContainer.stackSize += tAdditionalParallel;
            }
            replaceCursorItemStack(aPlayer, tFilledContainer);
        }
        if (tTankStack.amount <= 0)
            aFluidAccess.set(null);
        return tFilledContainer;
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
            if (mTimer % 500 == 0 || oContent != mContent) {
                var1.sendProgressBarUpdate(this, 100, mContent & 65535);
                var1.sendProgressBarUpdate(this, 101, mContent >>> 16);
            }
        }

        oContent = mContent;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
        super.updateProgressBar(par1, par2);
        switch (par1) {
            case 100:
                mContent = mContent & 0xffff0000 | par2 & 0x0000ffff;
                break;
            case 101:
                mContent = mContent & 0xffff | par2 << 16;
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
