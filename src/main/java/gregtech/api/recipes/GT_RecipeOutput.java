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
    private final float mChance;
    
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
        mChance = Math.min(1.0f, Math.max(aChance, 0.0f));
    }
    
    /**
     * Get the actual output, considering chances.
     * @param aRandom - the pseudo-random number generator object to use.
     * If the chance is 0 or 1 the random state will be unchanged.
     */
    public ItemStack getActualOutput(Random aRandom) {
        int tCount = 0;
        if (mChance == 1.0f) {
            tCount = mItemStack.stackSize;
        } else if (mChance > 0.0f) {
            for (int i = 0; i < mItemStack.stackSize; i++) {
                if (aRandom.nextFloat() < mChance) {
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
        return mItemStack.copy();
    }
    
    public float getChance() {
        return mChance;
    }
}