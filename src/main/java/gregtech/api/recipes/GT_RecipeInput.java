package gregtech.api.recipes;

import gregtech.api.util.GT_Utility;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;

/**
 * Represents an item stack as used for input in a GregTech machine recipe.
 * Heavily based on/inspired by the recipe system in Ender IO, which is public domain.
 * (see https://github.com/SleepyTrousers/EnderIO/blob/release/1.7.10/2.3/LICENSE to confirm)
 */
public class GT_RecipeInput {
    
    private final ItemStack mItemStack;
    protected int mCount; 

    protected GT_RecipeInput() {
        mItemStack = null;
    }
    
    public GT_RecipeInput(ItemStack aItemStack) {
        mItemStack = aItemStack.copy();
        mCount = aItemStack.stackSize;
    }
    
    public boolean inputMatches(ItemStack aItemStack) {
        return inputMatches(aItemStack, true);
    }
    
    public boolean inputMatches(ItemStack aItemStack, boolean aIgnoreCount) {
        if (aItemStack == null) return false;
        if (!aIgnoreCount && aItemStack.stackSize < mItemStack.stackSize) return false;
        if (!GT_Utility.areStacksEqual(aItemStack, mItemStack, true)) {
            return false;
        }
        // Now check for NBT tags specified in recipe input. Extra tags specified on provided item but not in recipe can be ignored.
        if (mItemStack.stackTagCompound != null) {
            if (aItemStack.stackTagCompound == null) return false;
            for (Object tTag : mItemStack.stackTagCompound.func_150296_c()) { // gets the key set
                if (!mItemStack.stackTagCompound.getTag(tTag.toString()).equals(aItemStack.stackTagCompound.getTag(tTag.toString()))) return false;
            }
        }
        return true;
    }
 
    /**
     * Gets a list of item stacks represented by this recipe input, possibly for display in NEI.
     * @return either a singleton list of the item stack or list of interchangeable item stacks usable as input for the current recipe.
     */
    public List<ItemStack> getInputStacks() {
        mItemStack.stackSize = mCount;
        return Collections.singletonList(mItemStack.copy());
    }
 
    /**
     * Gets a description of this input, probably for an assembly line instruction book
     * @return a description of the item or items that can be used as input for the current recipe.
     */
    public String getInputDescription() {
        return mItemStack.stackSize + " " + mItemStack.getDisplayName();
    }

    @Override
    public String toString() {
        return getInputDescription();
    }
    
    public int getCount() {
        mItemStack.stackSize = mCount;
        return mCount;
    }
    
    public void setCount(int aCount) {
        mItemStack.stackSize = aCount;
        mCount = aCount;
    }
}