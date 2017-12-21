package gregtech.common.covers;

import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.util.GT_CoverBehavior;

public class GT_Cover_Lens
        extends GT_CoverBehavior {
    private final byte mColour;

    public GT_Cover_Lens(byte aColour) {
        this.mColour = aColour;
    }

    public byte getLensColour(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return this.mColour;
    }
}
