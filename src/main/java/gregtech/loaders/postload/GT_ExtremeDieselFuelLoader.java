package gregtech.loaders.postload;

import gregtech.api.enums.Materials;
import gregtech.api.util.GT_Log;
import gregtech.api.util.GT_Recipe;
import net.minecraftforge.fluids.FluidStack;

public class GT_ExtremeDieselFuelLoader implements Runnable {
    @Override
    public void run() {
        GT_Log.out.println("GT_Mod: Adding extreme diesel fuel.");
        FluidStack tHOGStack = Materials.GasolinePremium.getFluid(1);
        GT_Recipe tFuel = GT_Recipe.GT_Recipe_Map.sDieselFuels.findFuel(tHOGStack);
        if (tFuel != null) {
            GT_Recipe.GT_Recipe_Map.sExtremeDieselFuels.add(tFuel);
        } else {
            GT_Log.out.println("GT_Mod: No extreme diesel fuel found.");
        }
    }
}
