package gregtech.common.covers;

import gregtech.api.util.GT_ModHandler;
import net.minecraftforge.fluids.FluidStack;

public class GT_Cover_SteamValve extends GT_Cover_Pump {

    public GT_Cover_SteamValve(int aTransferRate) {
        super(aTransferRate);
    }

    @Override
    protected boolean canTransferFluid(FluidStack fluid) {
        return GT_ModHandler.isAnySteam(fluid);
    }
}
