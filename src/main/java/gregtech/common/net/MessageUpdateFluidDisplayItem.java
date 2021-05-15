package gregtech.common.net;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import gregtech.api.interfaces.IHasFluidDisplayItem;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.net.GT_Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

public class MessageUpdateFluidDisplayItem extends GT_Packet {
    private int mBlockX, mBlockY, mBlockZ, mDim;

    public MessageUpdateFluidDisplayItem() {
        super(true);
    }

    public MessageUpdateFluidDisplayItem(int mBlockX, int mBlockY, int mBlockZ, int mDim) {
        super(false);
        this.mBlockX = mBlockX;
        this.mBlockY = mBlockY;
        this.mBlockZ = mBlockZ;
        this.mDim = mDim;
    }

    @Override
    public byte getPacketID() {
        return 8;
    }

    @Override
    public byte[] encode() {
        ByteArrayDataOutput os = ByteStreams.newDataOutput(32);
        os.writeInt(mBlockX);
        os.writeInt(mBlockY);
        os.writeInt(mBlockZ);
        os.writeInt(mDim);
        return os.toByteArray();
    }

    @Override
    public GT_Packet decode(ByteArrayDataInput aData) {
        return new MessageUpdateFluidDisplayItem(aData.readInt(), aData.readInt(), aData.readInt(), aData.readInt());
    }

    @Override
    public void process(IBlockAccess aWorld) {
        WorldServer world = DimensionManager.getWorld(mDim);
        if (world != null) {
            if (world.blockExists(mBlockX, mBlockY, mBlockZ)) {
                TileEntity tileEntity = world.getTileEntity(mBlockX, mBlockY, mBlockZ);
                if (tileEntity instanceof IGregTechTileEntity) {
                    IGregTechTileEntity gtTile = (IGregTechTileEntity) tileEntity;
                    if (gtTile.getMetaTileEntity() instanceof IHasFluidDisplayItem) {
                        ((IHasFluidDisplayItem) gtTile.getMetaTileEntity()).updateFluidDisplayItem();
                    }
                }
            }
        }
    }
}
