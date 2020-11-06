package gregtech.api.recipes;

import gregtech.api.util.GT_Utility;
import java.util.Arrays;
import java.util.List;
import net.minecraft.item.ItemStack;

/**
 * Represents a choice of several item stacks for use in a recipe (most likely an assembly line)
 * which don't even have to use the same stack size.
 */
public class GT_RecipeInputAlts extends GT_RecipeInput {
    
    private final ItemStack[] mItems;
    private int mCount = Integer.MAX_VALUE;
    
    public GT_RecipeInputAlts(ItemStack[] aItems) {
        super(null);
        this.mItems = aItems;
        for (ItemStack aItem : aItems) {
            mCount = Math.min(mCount, aItem.stackSize);
        }
    }

    @Override
    public boolean inputMatches(ItemStack aItemStack) {
        return inputMatches(aItemStack, true);
    }
    
    @Override
    public boolean inputMatches(ItemStack aItemStack, boolean aIgnoreCount) {
        if (aItemStack == null) {
            return false;
        }
        for (ItemStack tItemStack : mItems) {
            if (GT_Utility.areStacksEqual(tItemStack, aItemStack)) {
                if (!aIgnoreCount && aItemStack.stackSize < tItemStack.stackSize) {
                    return false;
                }
                if (tItemStack.stackTagCompound != null) {
                    if (aItemStack.stackTagCompound == null) {
                        return false;
                    }
                    for (Object tTag : tItemStack.stackTagCompound.func_150296_c()) { // gets the key set
                        if (!tItemStack.stackTagCompound.getTag(tTag.toString()).equals(aItemStack.stackTagCompound.getTag(tTag.toString()))) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    @Override
    public List<ItemStack> getInputStacks() {
        return Arrays.asList(mItems);
    }
    
    @Override
    public String getInputDescription() {
        StringBuilder rDescription = new StringBuilder(100);
        for (ItemStack tItem : mItems) {
            rDescription.append(tItem.stackSize);
            rDescription.append(' ');
            rDescription.append(tItem.getDisplayName());
            rDescription.append(" or ");
        }
        return rDescription.substring(0, rDescription.length() - 4);
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
        for (ItemStack tItem : mItems) {
            tItem.stackSize = tItem.stackSize * aCount / mCount;
        }
        mCount = aCount;
    }
    
}
