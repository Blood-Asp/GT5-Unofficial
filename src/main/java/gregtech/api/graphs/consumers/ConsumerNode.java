package gregtech.api.graphs.consumers;

import gregtech.api.graphs.Node;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;

//node atached to a tileentity that can consume stuff from the network
public class ConsumerNode extends Node {
    public int mSide;
    public ConsumerNode(int aNodeValue, TileEntity aTileEntity, int aSide, ArrayList<ConsumerNode> aConsumers) {
        super(aNodeValue,aTileEntity,aConsumers);
        this.mSide = aSide;
    }

    public boolean needsEnergy() {
        return !mTileEntity.isInvalid();
    }

    public int injectEnergy(int aVoltage, int aMaxApms) {
        return 0;
    }
}
