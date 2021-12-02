package gregtech.api.net;

import com.google.common.io.ByteArrayDataInput;
import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.ISerializableObject;
import gregtech.common.GT_Proxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Server -> Client: Show GUI
 */
public class GT_Packet_TileEntityCoverGUI extends GT_Packet_New {
    protected int mX;
    protected short mY;
    protected int mZ;

    protected byte side;
    protected int coverID, dimID, playerID;
    protected ISerializableObject coverData;

    public GT_Packet_TileEntityCoverGUI() {
        super(true);
    }

    public GT_Packet_TileEntityCoverGUI(int mX, short mY, int mZ, byte coverSide, int coverID, int coverData, int dimID, int playerID) {
        super(false);
        this.mX = mX;
        this.mY = mY;
        this.mZ = mZ;

        this.side = coverSide;
        this.coverID = coverID;
        this.coverData = new ISerializableObject.LegacyCoverData(coverData);

        this.dimID = dimID;
        this.playerID = playerID;
    }

    public GT_Packet_TileEntityCoverGUI(int mX, short mY, int mZ, byte coverSide, int coverID, ISerializableObject coverData, int dimID, int playerID) {
        super(false);
        this.mX = mX;
        this.mY = mY;
        this.mZ = mZ;

        this.side = coverSide;
        this.coverID = coverID;
        this.coverData = coverData;
        this.dimID = dimID;
        this.playerID = playerID;
    }


    public GT_Packet_TileEntityCoverGUI(byte side, int coverID, int coverData, ICoverable tile, EntityPlayerMP aPlayer) {
        super(false);

        this.mX = tile.getXCoord();
        this.mY = tile.getYCoord();
        this.mZ = tile.getZCoord();

        this.side = side;
        this.coverID = coverID;
        this.coverData = new ISerializableObject.LegacyCoverData(coverData);

        this.dimID = tile.getWorld().provider.dimensionId;
        this.playerID = aPlayer.getEntityId();
    }

    public GT_Packet_TileEntityCoverGUI(byte coverSide, int coverID, int coverData, IGregTechTileEntity tile) {
        super(false);
        this.mX = tile.getXCoord();
        this.mY = tile.getYCoord();
        this.mZ = tile.getZCoord();

        this.side = coverSide;
        this.coverID = coverID;
        this.coverData = new ISerializableObject.LegacyCoverData(coverData);

        this.dimID = tile.getWorld().provider.dimensionId;
    }

    public GT_Packet_TileEntityCoverGUI(byte side, int coverID, ISerializableObject coverData, ICoverable tile, EntityPlayerMP aPlayer) {
        super(false);
        this.mX = tile.getXCoord();
        this.mY = tile.getYCoord();
        this.mZ = tile.getZCoord();

        this.side = side;
        this.coverID = coverID;
        this.coverData = coverData.copy(); // make a copy so we don't get a race condition

        this.dimID = tile.getWorld().provider.dimensionId;
        this.playerID = aPlayer.getEntityId();
    }

    @Override
    public byte getPacketID() {
        return 7;
    }

    @Override
    public void encode(ByteBuf aOut) {
        aOut.writeInt(mX);
        aOut.writeShort(mY);
        aOut.writeInt(mZ);

        aOut.writeByte(side);
        aOut.writeInt(coverID);
        coverData.writeToByteBuf(aOut);

        aOut.writeInt(dimID);
        aOut.writeInt(playerID);
    }

    @Override
    public GT_Packet_New decode(ByteArrayDataInput aData) {
        int coverID;
        return new GT_Packet_TileEntityCoverGUI(
                aData.readInt(),
                aData.readShort(),
                aData.readInt(),

                aData.readByte(),
                coverID = aData.readInt(),
                GregTech_API.getCoverBehaviorNew(coverID).createDataObject().readFromPacket(aData, null),

                aData.readInt(),
                aData.readInt());
    }

    @Override
    public void process(IBlockAccess aWorld) {
        if (aWorld instanceof World) {
            EntityClientPlayerMP a = Minecraft.getMinecraft().thePlayer;
            TileEntity tile = aWorld.getTileEntity(mX, mY, mZ);
            if (tile instanceof IGregTechTileEntity && !((IGregTechTileEntity) tile).isDead()) {

                ((IGregTechTileEntity) tile).setCoverDataAtSide(side, coverData); //Set it client side to read later.
                a.openGui(GT_Values.GT, GT_Proxy.GUI_ID_COVER_SIDE_BASE + side, a.worldObj, mX, mY, mZ);
            }
        }
    }
}
