package gregtech.common.covers;

import gregtech.api.GregTech_API;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.interfaces.tileentity.IMachineProgress;

public class GT_Cover_RedstoneReceiverInternal
        extends GT_Cover_RedstoneWirelessBase {
    public int doCoverThings(byte aSide, byte aInputRedstone, int aCoverID, int aCoverVariable, ICoverable aTileEntity, long aTimer) {
        if (aTileEntity instanceof IMachineProgress) {
            if (getRedstoneInput(aSide, aInputRedstone, aCoverID, aCoverVariable, aTileEntity) >0)
                ((IMachineProgress) aTileEntity).enableWorking();
            else
                ((IMachineProgress) aTileEntity).disableWorking();
            ((IMachineProgress) aTileEntity).setWorkDataValue(aInputRedstone);
        }
        return aCoverVariable;
    }

    public byte getRedstoneInput(byte aSide, byte aInputRedstone, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return GregTech_API.sWirelessRedstone.get(Integer.valueOf(aCoverVariable)) == null ? 0 : ((Byte) GregTech_API.sWirelessRedstone.get(Integer.valueOf(aCoverVariable))).byteValue();
    }

    public int getTickRate(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return 1;
    }
}
