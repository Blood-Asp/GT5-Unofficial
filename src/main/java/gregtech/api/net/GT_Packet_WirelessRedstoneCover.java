package gregtech.api.net;

import com.google.common.io.ByteArrayDataInput;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

public class GT_Packet_WirelessRedstoneCover extends GT_Packet_TileEntityCover {
    private static final int PRIVATE_MASK = 0xFFFE0000;
    private static final int PUBLIC_MASK = 0x0000FFFF;
    private static final int CHECKBOX_MASK = 0x00010000;

    protected int mX;
    protected short mY;
    protected int mZ;

    protected byte side;
    protected int coverID, coverData, dimID;

    private EntityPlayerMP mPlayer;

    public GT_Packet_WirelessRedstoneCover() {
        super();
    }

    public GT_Packet_WirelessRedstoneCover(int mX, short mY, int mZ, byte coverSide, int coverID, int coverData, int dimID) {
        super(mX, mY, mZ, coverSide, coverID, coverData, dimID);
    }
    public GT_Packet_WirelessRedstoneCover(byte coverSide, int coverID, int coverData, ICoverable tile) {
        super(coverSide, coverID, coverData, tile);
    }

    @Override
    public byte getPacketID() {
        return 10;
    }

    @Override
    public void setINetHandler(INetHandler aHandler) {
        if (aHandler instanceof NetHandlerPlayServer) {
            mPlayer = ((NetHandlerPlayServer) aHandler).playerEntity;
        }
    }

    @Override
    public GT_Packet_New decode(ByteArrayDataInput aData) {
        return new GT_Packet_WirelessRedstoneCover(
                aData.readInt(),
                aData.readShort(),
                aData.readInt(),

                aData.readByte(),
                aData.readInt(),
                aData.readInt(),

                aData.readInt());
    }

    @Override
    public void process(IBlockAccess aWorld) {
        World world = DimensionManager.getWorld(dimID);
        if (world != null && world.blockExists(mX, mY, mZ)) {
            TileEntity tile = world.getTileEntity(mX, mY, mZ);
            if (tile instanceof IGregTechTileEntity && !((IGregTechTileEntity) tile).isDead()) {
                ((IGregTechTileEntity) tile).receiveCoverData(side, coverID, (mPlayer.getUniqueID().hashCode() & PRIVATE_MASK) | (coverData & PUBLIC_MASK | CHECKBOX_MASK));
            }
        }
    }
}
