package gregtech.common.gui;

import gregtech.api.gui.GT_ContainerMetaTile_Machine;
import gregtech.api.gui.GT_Slot_Holo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_Teleporter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GT_Container_Teleporter
        extends GT_ContainerMetaTile_Machine {
    public int mEgg = 0;
    public int mTargetD = 0;
    public int mTargetZ = 0;
    public int mTargetY = 0;
    public int mTargetX = 0;
    public GT_Container_Teleporter(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity) {
        super(aInventoryPlayer, aTileEntity);
    }

    @Override
    public void addSlots(InventoryPlayer aInventoryPlayer) {
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 2, 8, 5, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 2, 8, 23, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 2, 8, 41, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 2, 8, 59, false, false, 1));

        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 2, 26, 5, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 2, 26, 23, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 2, 26, 41, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 2, 26, 59, false, false, 1));

        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 2, 152, 5, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 2, 152, 23, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 2, 152, 41, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 2, 152, 59, false, false, 1));

        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 2, 134, 5, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 2, 134, 23, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 2, 134, 41, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(this.mTileEntity, 2, 134, 59, false, false, 1));
    }

    @Override
    public ItemStack slotClick(int aSlotIndex, int aMouseclick, ClickType aShifthold, EntityPlayer aPlayer) {
        if (aSlotIndex < 0) {
            return super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
        }
        Slot tSlot = this.inventorySlots.get(aSlotIndex);
        if ((tSlot != null) && (this.mTileEntity.getMetaTileEntity() != null)) {
            switch (aSlotIndex) {
                case 0:
                    ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetX -= (aShifthold == ClickType.PICKUP ? 512 : 64);
                    return null;
                case 1:
                    ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetY -= (aShifthold == ClickType.PICKUP ? 512 : 64);
                    return null;
                case 2:
                    ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetZ -= (aShifthold == ClickType.PICKUP ? 512 : 64);
                    return null;
                case 3:
                    ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetD -= (aShifthold == ClickType.PICKUP ? 16 : 8);
                    return null;
                case 4:
                    ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetX -= (aShifthold == ClickType.PICKUP ? 16 : 1);
                    return null;
                case 5:
                    ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetY -= (aShifthold == ClickType.PICKUP ? 16 : 1);
                    return null;
                case 6:
                    ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetZ -= (aShifthold == ClickType.PICKUP ? 16 : 1);
                    return null;
                case 7:
                    ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetD -= (aShifthold == ClickType.PICKUP ? 4 : 1);
                    return null;
                case 8:
                    ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetX += (aShifthold == ClickType.PICKUP ? 512 : 64);
                    return null;
                case 9:
                    ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetY += (aShifthold == ClickType.PICKUP ? 512 : 64);
                    return null;
                case 10:
                    ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetZ += (aShifthold == ClickType.PICKUP ? 512 : 64);
                    return null;
                case 11:
                    ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetD += (aShifthold == ClickType.PICKUP ? 16 : 8);
                    return null;
                case 12:
                    ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetX += (aShifthold == ClickType.PICKUP ? 16 : 1);
                    return null;
                case 13:
                    ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetY += (aShifthold == ClickType.PICKUP ? 16 : 1);
                    return null;
                case 14:
                    ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetZ += (aShifthold == ClickType.PICKUP ? 16 : 1);
                    return null;
                case 15:
                    ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetD += (aShifthold == ClickType.PICKUP ? 4 : 1);
                    return null;
            }
        }
        return super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if ((this.mTileEntity.isClientSide()) || (this.mTileEntity.getMetaTileEntity() == null)) {
            return;
        }
        this.mTargetX = ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetX;
        this.mTargetY = ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetY;
        this.mTargetZ = ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetZ;
        this.mTargetD = ((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).mTargetD;
        this.mEgg = (((GT_MetaTileEntity_Teleporter) this.mTileEntity.getMetaTileEntity()).hasDimensionalTeleportCapability() ? 1 : 0);

        for (IContainerListener var1 : this.listeners) {
            var1.sendProgressBarUpdate(this, 100, this.mTargetX & 0xFFFF);
            var1.sendProgressBarUpdate(this, 101, this.mTargetX >>> 16);
            var1.sendProgressBarUpdate(this, 102, this.mTargetY & 0xFFFF);
            var1.sendProgressBarUpdate(this, 103, this.mTargetY >>> 16);
            var1.sendProgressBarUpdate(this, 104, this.mTargetZ & 0xFFFF);
            var1.sendProgressBarUpdate(this, 105, this.mTargetZ >>> 16);
            var1.sendProgressBarUpdate(this, 106, this.mTargetD & 0xFFFF);
            var1.sendProgressBarUpdate(this, 107, this.mTargetD >>> 16);
            var1.sendProgressBarUpdate(this, 108, this.mEgg);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int par1, int par2) {
        super.updateProgressBar(par1, par2);
        switch (par1) {
            case 100:
                this.mTargetX = (this.mTargetX & 0xFFFF0000 | par2);
                break;
            case 101:
                this.mTargetX = (this.mTargetX & 0xFFFF | par2 << 16);
                break;
            case 102:
                this.mTargetY = (this.mTargetY & 0xFFFF0000 | par2);
                break;
            case 103:
                this.mTargetY = (this.mTargetY & 0xFFFF | par2 << 16);
                break;
            case 104:
                this.mTargetZ = (this.mTargetZ & 0xFFFF0000 | par2);
                break;
            case 105:
                this.mTargetZ = (this.mTargetZ & 0xFFFF | par2 << 16);
                break;
            case 106:
                this.mTargetD = (this.mTargetD & 0xFFFF0000 | par2);
                break;
            case 107:
                this.mTargetD = (this.mTargetD & 0xFFFF | par2 << 16);
                break;
            case 108:
                this.mEgg = par2;
        }
    }
}
