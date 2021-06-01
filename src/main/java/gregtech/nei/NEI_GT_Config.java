package gregtech.nei;

import codechicken.nei.api.IConfigureNEI;
import cpw.mods.fml.common.FMLCommonHandler;
import gregtech.api.enums.ItemList;
import gregtech.api.util.GT_Recipe;

public class NEI_GT_Config implements IConfigureNEI {
    public static boolean sIsAdded = true;
    public static GT_NEI_AssLineHandler ALH;

    @Override
    public void loadConfig() {
        sIsAdded = false;
        for (GT_Recipe.GT_Recipe_Map tMap : GT_Recipe.GT_Recipe_Map.sMappings) {
            if (tMap.mNEIAllowed) {
                new GT_NEI_DefaultHandler(tMap);
            }
        }
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            ALH = new GT_NEI_AssLineHandler(GT_Recipe.GT_Recipe_Map.sAssemblylineVisualRecipes);
            codechicken.nei.api.API.addItemListEntry(ItemList.VOLUMETRIC_FLASK.get(1));
        }
        sIsAdded = true;
    }

    @Override
    public String getName() {
        return "GregTech NEI Plugin";
    }

    @Override
    public String getVersion() {
        return "(5.03a)";
    }
}
