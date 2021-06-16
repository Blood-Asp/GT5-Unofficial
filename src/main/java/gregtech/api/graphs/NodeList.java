package gregtech.api.graphs;

//keep track on wich node is being looked for accrouse the recursif functions
public class NodeList {
    Node[] mNodes;
    int mConter = 0;
    public NodeList(Node[] mNodes) {
        this.mNodes = mNodes;
    }

    Node getNextNode() {
        if (++mConter < mNodes.length)
            return mNodes[mConter];
        else
            return null;
    }

    Node getNode() {
        if (mConter < mNodes.length)
            return mNodes[mConter];
        else
            return null;
    }
}
