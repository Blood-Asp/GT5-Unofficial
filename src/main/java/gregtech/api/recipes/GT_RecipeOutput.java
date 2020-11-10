package gregtech.api.recipes;

import gregtech.api.util.GT_OreDictUnificator;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Represents an item stack as used for output in a GregTech machine recipe (with chance).
 * Heavily based on/inspired by the recipe system in Ender IO, which is public domain.
 * (see https://github.com/SleepyTrousers/EnderIO/blob/release/1.7.10/2.3/LICENSE to confirm)
 */
public class GT_RecipeOutput {
    
    private final ItemStack mItemStack;
    private final int mChance;
    
    public GT_RecipeOutput(Block aBlock) {
        this(new ItemStack(aBlock, 1));
    }
    
    public GT_RecipeOutput(Block aBlock, float aChance) {
        this(new ItemStack(aBlock, 1), aChance);
    }
    
    public GT_RecipeOutput(Block aBlock, int aCount) {
        this(new ItemStack(aBlock, aCount));
    }
    
    public GT_RecipeOutput(Block aBlock, int aCount, float aChance) {
        this(new ItemStack(aBlock, aCount), aChance);
    }
    
    public GT_RecipeOutput(Item aItem) {
        this(new ItemStack(aItem, 1));
    }
    
    public GT_RecipeOutput(Item aItem, float aChance) {
        this(new ItemStack(aItem, 1), aChance);
    }
    
    public GT_RecipeOutput(Item aItem, int aCount) {
        this(new ItemStack(aItem, aCount));
    }
    
    public GT_RecipeOutput(Item aItem, int aCount, float aChance) {
        this(new ItemStack(aItem, aCount), aChance);
    }
    
    public GT_RecipeOutput(String aOredictName) {
        this(aOredictName, 1, 1.0f);
    }
    
    public GT_RecipeOutput(String aOredictName, float aChance) {
        this(aOredictName, 1, aChance);
    }
    
    public GT_RecipeOutput(String aOredictName, int aCount) {
        this(aOredictName, aCount, 1.0f);
    }
    
    public GT_RecipeOutput(String aOredictName, int aCount, float aChance) {
        this(GT_OreDictUnificator.getFirstOre(aOredictName, aCount), aChance);
    }
    
    public GT_RecipeOutput(ItemStack aItemStack) {
        this(aItemStack, 1.0f);
    }
    
    public GT_RecipeOutput(ItemStack aItemStack, float aChance) {
        mItemStack = aItemStack;
        mChance = Math.min(10000, Math.max((int)(aChance * 10000), 0));
    }
    
    /**
     * Get the actual output, considering chances.
     * @param aRandom - the pseudo-random number generator object to use.
     * If the chance is 0 or 1 the random state will be unchanged.
     * @return a stack of items, considering the chances.
     */
    public ItemStack getActualOutput(Random aRandom) {
        if (mItemStack == null) {
            return null;
        }
        int tCount = 0;
        if (mChance == 10000) {
            tCount = mItemStack.stackSize;
        } else if (mChance > 0.0f) {
            for (int i = 0; i < mItemStack.stackSize; i++) {
                if (aRandom.nextInt(10000) < mChance) {
                    tCount++;
                }
            }
        }
        ItemStack tItemStack = mItemStack.copy();
        tItemStack.stackSize = tCount;
        return tItemStack;
    }
    
    /**
     * Get the output to show, e.g. for an NEI recipe.
     */
    public ItemStack getShownOutput() {
        if (mItemStack == null) {
            return null;
        }
        return mItemStack.copy();
    }
    
    public int getChance() {
        return mChance;
    }
    
    public int getCount() {
        if (mItemStack == null) {
            return 0;
        }
        return mItemStack.stackSize;
    }
    
    public void setCount(int aCount) {
        if (mItemStack != null) {
            mItemStack.stackSize = aCount;
        }
    }
    
    public String getOutputDescription() {
        if (mItemStack == null) {
            return null;
        }
        String rDescription = mItemStack.stackSize + " " + mItemStack.getDisplayName();
        if (mChance > 0 && mChance < 10000) {
            rDescription += String.format(" (Chance: %d.%2d)", mChance / 100, mChance % 100);
        }
        return rDescription;
    }
    
    @Override
    public String toString() {
        return getOutputDescription();
    }
    
}