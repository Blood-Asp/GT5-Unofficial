package gregtech.api.interfaces.tileentity;

/**
 * This File has just internal Information about the Fibre Redstone State of a TileEntity
 */
public interface IFibreConnected extends IColouredTileEntity, IHasWorldObjectAndCoords {
    /**
     * If this Blocks accepts Fibre from this Side
     */
    public void inputFibreFrom(byte aSide);

    /**
     * If this Blocks emits Fibre to this Side
     */
    public void outputsFibreTo(byte aSide);

    /**
     * Sets the Signal this Blocks outputs to this Fibre Colour
     */
    public void setFibreOutput(byte aSide, byte aColour, byte aRedstoneStrength);

    /**
     * Gets the Signal this Blocks outputs to this Fibre Colour
     */
    public byte getFibreOutput(byte aSide, byte aColour);

    /**
     * Gets the Signal this Blocks receives from this Fibre Colour
     */
    public byte getFibreInput(byte aSide, byte aColour);
}