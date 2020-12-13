package gregtech.common.covers;

import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.util.GT_ModHandler;
import net.minecraftforge.fluids.FluidStack;

public class GT_Cover_SteamValve extends GT_Cover_Pump {

    public GT_Cover_SteamValve(int aTransferRate) {
        super(aTransferRate);
    }

    @Override
    public int doCoverThings(byte aSide, byte aInputRedstone, int aCoverID, int aCoverVariable, ICoverable aTileEntity, long aTimer) {
        return super.doCoverThings(aSide, aInputRedstone, aCoverID, aCoverVariable, aTileEntity, aTimer);
    }

    @Override
    protected boolean canTransferFluid(FluidStack fluid) {
        String fluidName = fluid.getFluid().getUnlocalizedName(fluid);
        return GT_ModHandler.isSteam(fluid) || fluidName.equals("fluid.steam") || fluidName.equals("ic2.fluidSteam") || fluidName.equals("fluid.mfr.steam.still.name");
    }
}
