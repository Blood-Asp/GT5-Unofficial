package gregtech.api.graphs;

import gregtech.api.graphs.consumers.ConsumerNode;
import gregtech.api.graphs.paths.NodePath;
import gregtech.api.metatileentity.*;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.HashSet;

import static gregtech.api.util.GT_Utility.getOppositeSide;


//generates the node map
abstract public class GenerateNodeMap {
    //clearign the node map to make sure it is gone on reset
    public static void clearNodeMap(Node aNode,int aReturnNodeValue) {
        if (aNode.mTileEntity instanceof BaseMetaPipeEntity) {
            BaseMetaPipeEntity tPipe = (BaseMetaPipeEntity) aNode.mTileEntity;
            tPipe.setNode(null);
            tPipe.setNodePath(null);
            if (aNode.mSelfPath != null) {
                aNode.mSelfPath.clearPath();
                aNode.mSelfPath = null;
            }
        }
        for (int i = 0;i<6;i++) {
            NodePath tPath = aNode.mNodePats[i];
            if (tPath != null) {
                tPath.clearPath();
                aNode.mNodePats[i] = null;
            }
            Node tNextNode = aNode.mNeigbourNodes[i];
            if (tNextNode == null) continue;
            if (tNextNode.mNodeValue != aReturnNodeValue)
                clearNodeMap(tNextNode,aNode.mNodeValue);
            aNode.mNeigbourNodes[i] = null;
        }
    }

    //gets the next node
    protected void generateNextNode(BaseMetaPipeEntity aPipe, Node aPipeNode, int aInvalidSide, int aNextNodeVale,
                                         ArrayList<ConsumerNode> tConsumers, HashSet<Node> tNodeMap) {
        MetaPipeEntity tMetaPipe = (MetaPipeEntity) aPipe.getMetaTileEntity();
        for (byte i = 0;i<6;i++) {
            if (i==aInvalidSide) {
                continue;
            }
            TileEntity tNextTileEntity = aPipe.getTileEntityAtSide(i);
            if (tNextTileEntity == null || (tMetaPipe != null && !tMetaPipe.isConnectedAtSide(i))) continue;
            ArrayList<MetaPipeEntity> tNewPipes = new ArrayList<MetaPipeEntity>();
            Pair nextTileEntity = getNextValidTileEntity(tNextTileEntity,tNewPipes,i,tNodeMap);
            if (nextTileEntity != null) {
                Node tNextNode = generateNode(nextTileEntity.mTileEntity,aPipeNode,aNextNodeVale+1,tNewPipes,
                        nextTileEntity.mSide,tConsumers,tNodeMap);
                if (tNextNode != null) {
                    aNextNodeVale = tNextNode.mHigestNodeValue;
                    aPipeNode.mHigestNodeValue = tNextNode.mHigestNodeValue;
                    aPipeNode.mNeigbourNodes[i] = tNextNode;
                    aPipeNode.mNodePats[i] = aPipeNode.mReturnPath;
                }
            }
        }
    }

    //on a valid tilentity create a new node
    protected Node generateNode(TileEntity aTileEntity, Node aPreviousNode, int aNextNodeVale, ArrayList<MetaPipeEntity> aPipes,
                                     int aSide, ArrayList<ConsumerNode> aConsumers, HashSet<Node> aNodeMap) {
        if (aTileEntity.isInvalid()) return null;
        int tSideOp = getOppositeSide(aSide);
        int tInvalidSide = aPreviousNode == null ? -1 : tSideOp;
        Node tThisNode = null;
        if (isPipe(aTileEntity)){
            BaseMetaPipeEntity tPipe = (BaseMetaPipeEntity) aTileEntity;
            MetaPipeEntity tMetaPipe = (MetaPipeEntity) tPipe.getMetaTileEntity();
            int tConections = getNumberOfConections(tMetaPipe);
            Node tPipeNode;
            if (tConections == 1) {
                tPipeNode = getEmptyNode(aNextNodeVale,tSideOp,aTileEntity,aConsumers);
                if (tPipeNode == null) return null;
            } else {
                tPipeNode = getPipeNode(aNextNodeVale,tSideOp,aTileEntity,aConsumers);
            }
            tPipe.setNode(tPipeNode);
            aNodeMap.add(tPipeNode);
            tPipeNode.mSelfPath = getNewPath(new MetaPipeEntity[]{tMetaPipe});
            tThisNode = tPipeNode;
            if (tInvalidSide>0) {
                tPipeNode.mNeigbourNodes[tInvalidSide] = aPreviousNode;
                tPipeNode.mNodePats[tInvalidSide] = getNewPath(aPipes.toArray(new MetaPipeEntity[0]));
                aPreviousNode.mReturnPath = tPipeNode.mNodePats[tInvalidSide];
            }
            if (tConections > 1)
                generateNextNode(tPipe,tPipeNode,tInvalidSide,aNextNodeVale,aConsumers,aNodeMap);
        } else if (addConsumer(aTileEntity,tSideOp,aNextNodeVale,aConsumers)) {
            ConsumerNode tConsumeNode = aConsumers.get(aConsumers.size()-1);
            tConsumeNode.mNeigbourNodes[tSideOp] = aPreviousNode;
            tConsumeNode.mNodePats[tSideOp] = getNewPath(aPipes.toArray(new MetaPipeEntity[0]));
            aPreviousNode.mReturnPath = tConsumeNode.mNodePats[tSideOp];
            tThisNode = tConsumeNode;
        }
        return tThisNode;
    }

    //go over the pipes until we see a valid tileentity that needs a node
    protected Pair getNextValidTileEntity(TileEntity aTileEntity, ArrayList<MetaPipeEntity> aPipes, int aSide, HashSet<Node> aNodeMap) {
        if (isPipe(aTileEntity)) {
            BaseMetaPipeEntity tPipe = (BaseMetaPipeEntity) aTileEntity;
            MetaPipeEntity tMetaPipe = (MetaPipeEntity) tPipe.getMetaTileEntity();
            Node tNode = tPipe.getNode();
            if (tNode != null) {
                if (aNodeMap.contains(tNode))
                    return null;
            }
            int tConections = getNumberOfConections(tMetaPipe);
            if (tConections == 2) {
                int tSideOp = getOppositeSide(aSide);
                for (byte i = 0;i<6;i++) {
                    if (i == tSideOp || !(tMetaPipe.isConnectedAtSide(i))) continue;
                    TileEntity tNewTileEntity = tPipe.getTileEntityAtSide(i);
                    if (tNewTileEntity == null) continue;
                    if (isPipe(tNewTileEntity)) {
                        aPipes.add(tMetaPipe);
                        return getNextValidTileEntity(tNewTileEntity,aPipes,i,aNodeMap);
                    } else {
                        return new Pair(aTileEntity,i);
                    }
                }
            } else {
                return new Pair(aTileEntity,aSide);
            }
        } else {
            return new Pair(aTileEntity,aSide);
        }
        return null;
    }

    private class Pair {
        public int mSide;
        public TileEntity mTileEntity;
        public Pair(TileEntity aTileEntity, int aSide) {
            this.mTileEntity = aTileEntity;
            this.mSide = aSide;
        }
    }

    //if check if the tileentity is the correct pipe
    protected boolean isPipe(TileEntity aTileEntity) {
        return aTileEntity instanceof BaseMetaPipeEntity;
    }
    //checks if the tileentity is a consumer and add to the list
    abstract protected boolean addConsumer(TileEntity aTileEntity, int aSide, int aNodeValue, ArrayList<ConsumerNode> aConsumers);
    //get correct pathClass  that you need for your node network
    protected abstract NodePath getNewPath(MetaPipeEntity[] aPipes);

    //used for if you need to use death ends for somthing
    //can be null
    protected Node getEmptyNode(int aNodeValue, int aSide, TileEntity aTileEntity, ArrayList<ConsumerNode> aConsumers) {
        return null;
    }
    //get correct node type you need for your network
    protected Node getPipeNode(int aNodeValue, int aSide, TileEntity aTileEntity, ArrayList<ConsumerNode> aConsumers) {
        return new Node(aNodeValue,aTileEntity,aConsumers);
    }

    //get how many conections the pipe have
    private static int getNumberOfConections(MetaPipeEntity aPipe) {
        int tCons = 0;
        for (int i = 0; i < 6; i++) {
            if (aPipe.isConnectedAtSide(i)) tCons++;
        }
        return tCons;
    }
}
