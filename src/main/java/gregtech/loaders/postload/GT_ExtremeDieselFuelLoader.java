package gregtech.loaders.postload;

import gregtech.api.enums.Materials;
import gregtech.api.util.GT_Log;
import gregtech.api.util.GT_Recipe;

public class GT_ExtremeDieselFuelLoader implements Runnable {
    @Override
    public void run() {
        GT_Log.out.println("GT_Mod: Adding extreme diesel fuel.");
        for (GT_Recipe r : GT_Recipe.GT_Recipe_Map.sDieselFuels.mRecipeList) {
            if (r.mFluidInputs.length == 1 && Materials.GasolinePremium.getFluid(1).isFluidEqual(r.mFluidInputs[0])) {
                GT_Recipe.GT_Recipe_Map.sExtremeDieselFuels.add(r);
                return;
            }
        }
        GT_Log.out.println("GT_Mod: No extreme diesel fuel found.");
    }
}
