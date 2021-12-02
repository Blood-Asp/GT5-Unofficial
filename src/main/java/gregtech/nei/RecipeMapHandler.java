package gregtech.nei;

import codechicken.nei.recipe.TemplateRecipeHandler;
import gregtech.api.util.GT_Recipe;

/**
 * This abstract class represents an NEI handler that is constructed from a
 * {@link GT_Recipe.GT_Recipe_Map}, and allows us to sort NEI handlers by recipe map.
 */
abstract class RecipeMapHandler extends TemplateRecipeHandler {
    protected final GT_Recipe.GT_Recipe_Map mRecipeMap;

    RecipeMapHandler(GT_Recipe.GT_Recipe_Map mRecipeMap) {
        this.mRecipeMap = mRecipeMap;
    }

    GT_Recipe.GT_Recipe_Map getRecipeMap() {
        return mRecipeMap;
    }
}
