package gregtech.common.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.gui.GT_ContainerMetaTile_Machine;
import gregtech.api.gui.GT_Slot_Holo;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_Digital_Transformer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class GT_Container_DigitalTransformer extends GT_ContainerMetaTile_Machine {
    public int oTier, oAMP, iAmp = -1;

    public GT_Container_DigitalTransformer(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity) {
        super(aInventoryPlayer, aTileEntity);
    }

    @Override
    public void addSlots(InventoryPlayer aInventoryPlayer) {
        addSlotToContainer(new GT_Slot_Holo(mTileEntity, 2, 134, 23, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(mTileEntity, 2, 134, 23 + 18, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(mTileEntity, 2, 134, 23 + 36, false, false, 1));

        addSlotToContainer(new GT_Slot_Holo(mTileEntity, 2, 152, 23, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(mTileEntity, 2, 152, 23 + 18, false, false, 1));
        addSlotToContainer(new GT_Slot_Holo(mTileEntity, 2, 152, 23 + 36, false, false, 1));
    }

    @Override
    public ItemStack slotClick(int aSlotIndex, int aMouseclick, int aShifthold, EntityPlayer aPlayer) {
        if (aSlotIndex < 0) {
            return super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
        }
        Slot tSlot = (Slot) inventorySlots.get(aSlotIndex);
        if (tSlot != null && mTileEntity.getMetaTileEntity() != null) {
            GT_MetaTileEntity_Digital_Transformer dpg = (GT_MetaTileEntity_Digital_Transformer) mTileEntity.getMetaTileEntity();
            if (aSlotIndex > 5) {
                return super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
            }
            int mult = (aSlotIndex > 2 ? 1 : -1);
            int step = mult * (aShifthold == 1 ? 8 : 1);
            switch (aSlotIndex % 3) {
                case 0:
                    dpg.iAmp += step;
                    dpg.iAmp = Math.max(0, Math.min(64, dpg.iAmp));
                    break;
                case 1:
                    dpg.oTier += mult * (aShifthold == 1 ? 4 : 1);
                    dpg.oTier = Math.max(0, Math.min(dpg.mTier, dpg.oTier));
                    break;
                case 2:
                    dpg.oAmp += step;
                    dpg.oAmp = Math.max(0, Math.min(64, dpg.oAmp));
                    break;
            }
            return null;
        }
        return super.slotClick(aSlotIndex, aMouseclick, aShifthold, aPlayer);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (mTileEntity.isClientSide() || mTileEntity.getMetaTileEntity() == null) {
            return;
        }

        GT_MetaTileEntity_Digital_Transformer dpg = (GT_MetaTileEntity_Digital_Transformer) mTileEntity.getMetaTileEntity();
        if (oTier == dpg.oTier && oAMP == dpg.oAmp && iAmp == dpg.iAmp)
            return;
        oTier = dpg.oTier;
        oAMP = dpg.oAmp;
        iAmp = dpg.iAmp;
        for (Object crafter : crafters) {
            ICrafting var1 = (ICrafting) crafter;
            var1.sendProgressBarUpdate(this, 100, oTier);
            var1.sendProgressBarUpdate(this, 101, oAMP);
            var1.sendProgressBarUpdate(this, 102, iAmp);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
        super.updateProgressBar(par1, par2);
        switch (par1) {
            case 100:
                oTier = par2;
                break;
            case 101:
                oAMP = par2;
                break;
            case 102:
                iAmp = par2;
                break;
        }
    }
}
