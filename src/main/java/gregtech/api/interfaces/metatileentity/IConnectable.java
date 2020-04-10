package gregtech.api.interfaces.metatileentity;

/**
 * For pipes, wires, and other MetaTiles which need to be decided whether they should connect to the block at each side.
 */
public interface IConnectable {
    /**
     * Try to connect to the Block at the specified side
     * returns the connection state. Non-positive values for failed, others for succeeded.
     */
    int connect(byte aSide);

    /**
     * Try to disconnect to the Block at the specified side
     */
    void disconnect(byte aSide);

    boolean isConnectedAtSide(int aSide);
}
