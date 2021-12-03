package gregtech.common.net;

import com.google.common.io.ByteArrayDataInput;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import eu.usrv.yamcore.network.client.AbstractClientMessageHandler;
import eu.usrv.yamcore.network.server.AbstractServerMessageHandler;
import gregtech.api.events.IntegratedCircuitScroll;
import gregtech.api.net.GT_Packet_New;
import gregtech.common.items.GT_IntegratedCircuit_Item;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;

public class MessageSetIntegratedCircuit implements IMessage {
    byte metaValue;

    public MessageSetIntegratedCircuit() {
    }

    public MessageSetIntegratedCircuit(byte metaValue) {
        this.metaValue = metaValue;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        metaValue = buf.getByte(0);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(metaValue);
    }

    public static class ServerHandler extends AbstractServerMessageHandler<MessageSetIntegratedCircuit> {
        @Override
        public IMessage handleServerMessage(EntityPlayer player, MessageSetIntegratedCircuit message, MessageContext ctx) {
            if (message.metaValue >= IntegratedCircuitScroll.minValue || message.metaValue  <= IntegratedCircuitScroll.maxValue) {
                int currentItem = player.inventory.currentItem;
                if (currentItem < 0 || player.inventory.mainInventory.length < currentItem) return null;
                ItemStack circuit = player.inventory.mainInventory[player.inventory.currentItem];
                if (circuit.getItem() instanceof GT_IntegratedCircuit_Item) {
                    circuit.setItemDamage(message.metaValue);
                    return new MessageSetIntegratedCircuit(message.metaValue);
                }
            }
            return null;
        }
    }

    public static class clientHandler extends AbstractClientMessageHandler<MessageSetIntegratedCircuit> {
        @Override
        public IMessage handleClientMessage(EntityPlayer player, MessageSetIntegratedCircuit message, MessageContext ctx) {
            if (message.metaValue >= IntegratedCircuitScroll.minValue || message.metaValue  <= IntegratedCircuitScroll.maxValue) {
                int currentItem = player.inventory.currentItem;
                if (currentItem < 0 || player.inventory.mainInventory.length < currentItem) return null;
                ItemStack circuit = player.inventory.mainInventory[player.inventory.currentItem];
                if (circuit.getItem() instanceof GT_IntegratedCircuit_Item) {
                    if (circuit.getItemDamage() != message.metaValue) {
                        circuit.setItemDamage(message.metaValue);
                    }
                }
            }
            return null;
        }
    }
}
