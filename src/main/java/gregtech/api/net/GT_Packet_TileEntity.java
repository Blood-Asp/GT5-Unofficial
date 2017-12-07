package gregtech.api.net;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import gregtech.api.metatileentity.BaseMetaPipeEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

public class GT_Packet_TileEntity extends GT_Packet {
    private int mX, mZ, mC0, mC1, mC2, mC3, mC4, mC5;
    private short mY, mID;
    private byte mTexture, mTexturePage, mUpdate, mRedstone, mColour;

    public GT_Packet_TileEntity() {
        super(true);
    }

    //For tiles
    public GT_Packet_TileEntity(int aX, short aY, int aZ, short aID, int aC0, int aC1, int aC2, int aC3, int aC4, int aC5, byte aTexture, byte aTexturePage, byte aUpdate, byte aRedstone, byte aColour) {
        super(false);
        mX = aX;
        mY = aY;
        mZ = aZ;
        mC0 = aC0;
        mC1 = aC1;
        mC2 = aC2;
        mC3 = aC3;
        mC4 = aC4;
        mC5 = aC5;
        mID = aID;
        mTexture = aTexture;
        mTexturePage=aTexturePage;
        mUpdate = aUpdate;
        mRedstone = aRedstone;
        mColour = aColour;
    }

    //For pipes
    public GT_Packet_TileEntity(int aX, short aY, int aZ, short aID, int aC0, int aC1, int aC2, int aC3, int aC4, int aC5, byte aTexture, byte aUpdate, byte aRedstone, byte aColour) {
        super(false);
        mX = aX;
        mY = aY;
        mZ = aZ;
        mC0 = aC0;
        mC1 = aC1;
        mC2 = aC2;
        mC3 = aC3;
        mC4 = aC4;
        mC5 = aC5;
        mID = aID;
        mTexture = aTexture;
        mTexturePage=0;
        mUpdate = aUpdate;
        mRedstone = aRedstone;
        mColour = aColour;
    }

    @Override
    public byte[] encode() {
        ByteArrayDataOutput tOut = ByteStreams.newDataOutput(41);

        tOut.writeInt(mX);
        tOut.writeShort(mY);
        tOut.writeInt(mZ);
        tOut.writeShort(mID);

        tOut.writeInt(mC0);
        tOut.writeInt(mC1);
        tOut.writeInt(mC2);
        tOut.writeInt(mC3);
        tOut.writeInt(mC4);
        tOut.writeInt(mC5);

        tOut.writeByte(mTexture);
        tOut.writeByte(mTexturePage);
        tOut.writeByte(mUpdate);
        tOut.writeByte(mRedstone);
        tOut.writeByte(mColour);

        return tOut.toByteArray();
    }

    @Override
    public GT_Packet decode(ByteArrayDataInput aData) {
        return new GT_Packet_TileEntity(aData.readInt(), aData.readShort(), aData.readInt(), aData.readShort(), aData.readInt(), aData.readInt(), aData.readInt(), aData.readInt(), aData.readInt(), aData.readInt(), aData.readByte(), aData.readByte(), aData.readByte(), aData.readByte(), aData.readByte());
    }

    @Override
    public void process(IBlockAccess aWorld) {
        if (aWorld != null) {
            TileEntity tTileEntity = aWorld.getTileEntity(mX, mY, mZ);
            if (tTileEntity != null) {
                if (tTileEntity instanceof BaseMetaTileEntity)
                    ((BaseMetaTileEntity) tTileEntity).receiveMetaTileEntityData(mID, mC0, mC1, mC2, mC3, mC4, mC5, mTexture, mTexturePage, mUpdate, mRedstone, mColour);
                else if (tTileEntity instanceof BaseMetaPipeEntity)
                    ((BaseMetaPipeEntity) tTileEntity).receiveMetaTileEntityData(mID, mC0, mC1, mC2, mC3, mC4, mC5, mTexture, mUpdate, mRedstone, mColour);
            }
        }
    }

    @Override
    public byte getPacketID() {
        return 0;
    }
}