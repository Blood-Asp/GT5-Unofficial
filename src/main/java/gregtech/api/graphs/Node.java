package gregtech.api.graphs;

import gregtech.api.graphs.consumers.ConsumerNode;
import gregtech.api.graphs.paths.NodePath;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import java.util.ArrayList;

//base Node class
public class Node {
    public Node(int aNodeValue,TileEntity aTileEntity,ArrayList<ConsumerNode> aConsumers){
        this.mNodeValue = aNodeValue;
        this.mTileEntity = aTileEntity;
        this.mConsumers = aConsumers;
        mHigestNodeValue = aNodeValue;
        //you dont want to generate map multiple times in the same tick
        mCreationTime = MinecraftServer.getServer().getTickCounter();
    }


    public int mCreationTime;
    public NodePath mSelfPath;
    public ArrayList<ConsumerNode> mConsumers;
    public int mNodeValue;
    public final TileEntity mTileEntity;
    public Node[] mNeigbourNodes = new Node[6];
    public NodePath[] mNodePats = new NodePath[6];
    public NodePath mReturnPath;
    public int mHigestNodeValue;
}
