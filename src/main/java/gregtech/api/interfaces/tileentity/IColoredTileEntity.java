package gregtech.api.interfaces.tileentity;

public interface IColouredTileEntity {
    /**
     * @return 0 - 15 are Colours, while -1 means uncoloured
     */
    public byte getColourization();

    /**
     * Sets the Colour Modulation of the Block
     *
     * @param aColour the Colour you want to set it to. -1 for reset.
     */
    public byte setColourization(byte aColour);
}
