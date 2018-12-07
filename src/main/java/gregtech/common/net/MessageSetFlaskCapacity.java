package gregtech.common.net;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import gregtech.common.items.GT_VolumetricFlask;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class MessageSetFlaskCapacity implements IMessage {
    private int capacity;

    public MessageSetFlaskCapacity() {
    }

    public MessageSetFlaskCapacity(int capacity) {
        this.capacity = capacity;
    }


    public void fromBytes(ByteBuf buf) {
        this.capacity = buf.readInt();
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.capacity);
    }

    public static final class Handler
            implements IMessageHandler<MessageSetFlaskCapacity, IMessage> {
        public IMessage onMessage(MessageSetFlaskCapacity message, MessageContext ctx) {
            ItemStack stack = ctx.getServerHandler().playerEntity.getHeldItem();
            if ((stack != null) && (stack.stackSize > 0)) {
                Item item = stack.getItem();
                if ((item instanceof GT_VolumetricFlask))
                    ((GT_VolumetricFlask) item).setCapacity(stack, message.capacity);
            }
            return null;
        }
    }
}