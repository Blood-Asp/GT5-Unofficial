package gregtech.api.recipes;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Maps the conditions that certain recipes might need to be enabled or disabled based on.
 * These are boolean fields in other classes.
 */
public class GT_RecipeConditions {
    private final static Map<String, Field> sConditionMap = new HashMap<>(100);
    
    private GT_RecipeConditions() {
        // Utility class
    }
    
    public static boolean getConditionValue(String aConditionName) {
        try {
            return sConditionMap.get(aConditionName).getBoolean(null);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            // ignore exceptions
        }
        return false;
    }
}
