package gregtech.api.recipes;

import gregtech.api.util.GT_OreDictUnificator;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Represents an ore dictionary entry (and count) for use in a GregTech machine recipe.
 * Heavily based on/inspired by the recipe system in Ender IO, which is public domain.
 * (see https://github.com/SleepyTrousers/EnderIO/blob/release/1.7.10/2.3/LICENSE to confirm)
 */
public class GT_RecipeInputOredict extends GT_RecipeInput {

    private final String mOredictName;
    private final int mCount;

    public GT_RecipeInputOredict(String aOredictName, int aCount) {
        super(null, false);
        mOredictName = aOredictName;
        mCount = aCount;
    }

    public boolean inputMatches(ItemStack aItemStack, boolean aCheckCount) {
        if (aItemStack == null) return false;
        if (aCheckCount && aItemStack.stackSize < mCount) return false;
        ArrayList<ItemStack> tList = OreDictionary.getOres(mOredictName);
        if (tList == null) return false;
        for (ItemStack tStack : tList) {
            if (OreDictionary.itemMatches(tStack, aItemStack, false)) return true;
        }
        return false;
    }
    
    @Override
    public List<ItemStack> getInputStacks() {
        List<ItemStack> result = GT_OreDictUnificator.getOres(mOredictName);
        for (int i = 0; i < result.size(); i++) {
            ItemStack tItemStack = result.get(i).copy();
            tItemStack.stackSize = mCount;
            result.set(i, tItemStack);
        }
        return result;
    }
    
    @Override
    public String getInputDescription() {
        return mCount + " " + mOredictName;
    }
    
}