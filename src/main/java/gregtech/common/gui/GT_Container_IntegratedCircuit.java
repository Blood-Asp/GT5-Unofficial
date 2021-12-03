package gregtech.common.gui;


import gregtech.common.items.GT_IntegratedCircuit_Item;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class GT_Container_IntegratedCircuit extends Container {
    public ItemStack circuit;

    public GT_Container_IntegratedCircuit(ItemStack circuit) {
        this.circuit = circuit;
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return circuit != null && circuit.getItem() instanceof GT_IntegratedCircuit_Item;
    }
}
