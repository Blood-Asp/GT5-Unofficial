package gregtech.api.recipes;

import cpw.mods.fml.common.Loader;
import gregtech.GT_Mod;
import gregtech.api.enums.Materials;
import gregtech.api.util.GT_Log;
import gregtech.common.GT_Proxy;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import net.minecraftforge.fluids.FluidRegistry;

/**
 *
 * Maps the conditions that certain recipes might need to be enabled or disabled based on.
 * Some of these are boolean fields in other classes.<br>
 * Plus a few special cases:
 * <ul><li>ModLoaded(<i>ModName</i>) - Checks if the named mod is loaded
 * <li>ModsLoaded(<i>ModName</i>, <i>ModName</i>, ...) - Checks if all the mods in the comma separated list are loaded.
 * <li>FluidExists(<i>FluidName</i>) - Checks if the named fluid has been registered.
 * <li>MaterialParent(<i>MaterialName</i>) - Checks if the named material has a "parent mod" according to the field in the Materials enum.
 * </ul>
 */
public class GT_RecipeConditions {
    private final static Map<String, Field> sConditionMap = new HashMap<>(100);
    
    private GT_RecipeConditions() {
        // Utility class
    }
    
    public static boolean getConditionValue(String aConditionName) {
        String tModLoadedPattern = "ModLoaded\\(([A-Za-z0-9_]+)\\)";
        String tModsLoadedPattern = "ModsLoaded\\(([A-Za-z0-9_, ]+)\\)";
        String tFluidExistsPattern = "FluidExists\\(([A-Za-z]+)\\)";
        String tMaterialParentPattern = "MaterialParent\\(([A-Za-z0-9_]+)\\)";
        if (aConditionName.matches(tModLoadedPattern)) {
            return Loader.isModLoaded(aConditionName.replaceFirst(tModLoadedPattern, "$1"));
        } else if (aConditionName.matches(tModsLoadedPattern)) {
            String[] tModNames = aConditionName.replaceFirst(tModsLoadedPattern, "$1").split(", *");
            for (String tModName : tModNames) {
                if (!Loader.isModLoaded(tModName)) {
                    return false;
                }
            }
            return true;
        } else if (aConditionName.matches(tFluidExistsPattern)) {
            return FluidRegistry.isFluidRegistered(aConditionName.replaceFirst(tFluidExistsPattern, "$1"));
        } else if (aConditionName.matches(tMaterialParentPattern)) {
            Materials tMaterial = Materials.get(aConditionName.replaceFirst(tMaterialParentPattern, "$1"));
            if (tMaterial != Materials._NULL) {
                return tMaterial.mHasParentMod;
            }
        }
        try {
            return sConditionMap.get(aConditionName).getBoolean(GT_Mod.gregtechproxy);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // ignore exceptions
        }
        return false;
    }
    
    static {
        try {
            sConditionMap.put("DisableIC2Cables", GT_Proxy.class.getField("mDisableIC2Cables"));
            sConditionMap.put("HardMachineCasings", GT_Proxy.class.getField("mHardMachineCasings"));
            sConditionMap.put("DisableOldChemicalRecipes", GT_Proxy.class.getField("mDisableOldChemicalRecipes"));
            sConditionMap.put("MoreComplicatedChemicalRecipes", GT_Proxy.class.getField("mMoreComplicatedChemicalRecipes"));
        } catch (Throwable e) {
            e.printStackTrace(GT_Log.err);
        }
    }
}
