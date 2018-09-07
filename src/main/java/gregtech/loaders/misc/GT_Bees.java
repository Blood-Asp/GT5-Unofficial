package gregtech.loaders.misc;

import cpw.mods.fml.common.Loader;
import gregtech.GT_Mod;
import gregtech.common.items.ItemComb;
import gregtech.common.items.ItemDrop;

public class GT_Bees {

    public static ItemComb combs;
    public static ItemDrop drops;

    public GT_Bees() {
        if (Loader.isModLoaded("Forestry") && GT_Mod.gregtechproxy.mGTBees) {
            combs = new ItemComb();
            drops = new ItemDrop();
            combs.initCombsRecipes();
            GT_BeeDefinition.initBees();            
        }
    }
}
