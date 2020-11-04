package gregtech.api.recipes;

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
    private final boolean mUseMeta;
 
    public GT_RecipeInput(ItemStack aItemStack) {
        this(aItemStack, true);
    }
    
    public GT_RecipeInput(ItemStack aItemStack, boolean aUseMeta) {
        mItemStack = aItemStack;
        mUseMeta = aUseMeta;
    }
 
    public boolean inputMatches(ItemStack aItemStack) {
        return inputMatches(aItemStack, true);
    }
    
    public boolean inputMatches(ItemStack aItemStack, boolean aIgnoreCount) {
        if (aItemStack == null) return false;
        if (!aIgnoreCount && aItemStack.stackSize < mItemStack.stackSize) return false;
        if (mUseMeta && aItemStack.getItemDamage() != mItemStack.getItemDamage()) return false;
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
        return Collections.singletonList(mItemStack.copy());
    }
 
    public String getInputDescription() {
        return mItemStack.stackSize + " " + mItemStack.getDisplayName();
    }

    public int getCount() {
        return mItemStack.stackSize;
    }
    
    public void setCount(int aCount) {
        mItemStack.stackSize = aCount;
    }
}