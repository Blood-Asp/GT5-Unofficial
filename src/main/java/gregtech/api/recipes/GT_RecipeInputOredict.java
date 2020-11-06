package gregtech.api.recipes;

import gregtech.api.util.GT_OreDictUnificator;
import java.util.List;
import net.minecraft.item.ItemStack;

/**
 * Represents an ore dictionary entry (and count) for use in a GregTech machine recipe.
 * Heavily based on/inspired by the recipe system in Ender IO, which is public domain.
 * (see https://github.com/SleepyTrousers/EnderIO/blob/release/1.7.10/2.3/LICENSE to confirm)
 */
public class GT_RecipeInputOredict extends GT_RecipeInput {

    private final String mOredictName;
    private int mCount;

    public GT_RecipeInputOredict(String aOredictName, int aCount) {
        super(null);
        mOredictName = aOredictName;
        mCount = aCount;
    }

    @Override
    public boolean inputMatches(ItemStack aItemStack, boolean aIgnoreCount) {
        if (aItemStack == null) return false;
        if (!aIgnoreCount && aItemStack.stackSize < mCount) return false;
        return GT_OreDictUnificator.isItemStackInstanceOf(aItemStack, mOredictName);
    }
    
    @Override
    public List<ItemStack> getInputStacks() {
        List<ItemStack> rList = GT_OreDictUnificator.getOres(mOredictName);
        for (int i = 0; i < rList.size(); i++) {
            ItemStack tItemStack = rList.get(i).copy();
            tItemStack.stackSize = mCount;
            rList.set(i, tItemStack);
        }
        return rList;
    }
    
    @Override
    public String getInputDescription() {
        return mCount + " " + mOredictName;
    }
    
    @Override
    public String toString() {
        return getInputDescription();
    }
    
    @Override
    public int getCount() {
        return mCount;
    }
    
    @Override
    public void setCount(int aCount) {
        mCount = aCount;
    }
    
}