package gregtech.api.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.GT_Mod;
import gregtech.common.items.GT_IntegratedCircuit_Item;
import gregtech.common.net.MessageSetIntegratedCircuit;
import gregtech.loaders.misc.NetworkDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;

public class IntegratedCircuitScroll {
    public static byte maxValue = 24;
    public static byte minValue = 0;


    public IntegratedCircuitScroll() {
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onMouseEvent(MouseEvent event) {
        if (event.dwheel != 0) {
            if (Minecraft.getMinecraft().currentScreen == null) {
                EntityClientPlayerMP player = Minecraft.getMinecraft().thePlayer;
                if (player.movementInput.sneak) {
                    ItemStack heldItem = player.inventory.mainInventory[player.inventory.currentItem];
                    if (heldItem != null && heldItem.getItem() instanceof GT_IntegratedCircuit_Item) {
                        if (event.dwheel > 0) {
                            int newValue = heldItem.getItemDamage() + 1;
                            if (newValue <= maxValue) {
                                heldItem.setItemDamage(newValue);
                                NetworkDispatcher.INSTANCE.sendToServer(new MessageSetIntegratedCircuit((byte) newValue));
                            }
                        } else {
                            int newValue = heldItem.getItemDamage() - 1;
                            if (newValue >= minValue) {
                                heldItem.setItemDamage(newValue);
                                NetworkDispatcher.INSTANCE.sendToServer(new MessageSetIntegratedCircuit((byte) newValue));
                            }
                        }
                        //send packet
                        event.setCanceled(true);
                    }
                }
            }
        }
    }
}
