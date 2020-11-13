package gregtech.api.recipes;

import codechicken.nei.PositionedStack;
import gregtech.api.objects.GT_FluidStack;
import gregtech.api.util.GT_Utility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;



/**
 * Heavily modified version of GT_Recipe, which will eventually allow oredicted input and output as well as checking NBT tags,
 * and loading of XML (or maybe other format) recipes.
 */
public class GT_MachineRecipe implements Comparable<GT_MachineRecipe> {
    public GT_RecipeInput[] mInputs;
    public GT_RecipeOutput[] mOutputs;
    public FluidStack[] mFluidInputs;
    public FluidStack[] mFluidOutputs;
    
    /**
     * An Item that needs to be inside the Special Slot, like for example the Copy Slot inside the Printer. This is only useful for Fake Recipes in NEI, since findRecipe() and containsInput() don't give a shit about this Field. Lists are also possible.
     */
    public Object mSpecialItems;
    
    public int mDuration, mEUt, mSpecialValue;
    
    /**
     * Use this to just disable a specific Recipe, but the Configuration enables that already for every single Recipe.
     */
    private boolean mEnabled = true;
    /**
     * Use this to make a recipe enabled depending on a certain config setting.
     */
    public String mEnableCondition = null;
    /**
     * If this is true, having the setting specified in mEnableCondition false will enable this recipe.
     */
    public boolean mInvertCondition = false;
    /**
     * If this Recipe is hidden from NEI
     */
    public boolean mHidden = false;
    /**
     * If this Recipe is Fake and therefore doesn't get found by the findRecipe Function (It is still in the HashMaps, so that containsInput does return T on those fake Inputs)
     */
    public boolean mFakeRecipe = false;
    /**
     * If this Recipe can be stored inside a Machine in order to make Recipe searching more Efficient by trying the previously used Recipe first. In case you have a Recipe Map overriding things and returning one time use Recipes, you have to set this to F.
     */
    public boolean mCanBeBuffered = true;
    /**
     * If this Recipe needs the Output Slots to be completely empty. Needed in case you have randomised Outputs
     */
    public boolean mNeedsEmptyOutput = false;
    /**
     * Used for describing recipes that do not fit the default recipe pattern (for example Large Boiler Fuels)
     */
    private String[] neiDesc = null;
    
    private boolean mOptimized = false;
    
    /**
     * The main constructor with inputs and outputs.  Other details can be added with chainable setters.
     */
    public GT_MachineRecipe(GT_RecipeInput[] aInputs, GT_RecipeOutput[] aOutputs, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs) {
        mInputs = aInputs;
        mOutputs = aOutputs;
        mFluidInputs = aFluidInputs;
        mFluidOutputs = aFluidOutputs;

        if (mInputs == null) mInputs = new GT_RecipeInput[0];
        if (mOutputs == null) mOutputs = new GT_RecipeOutput[0];
        if (mFluidInputs == null) mFluidInputs = new FluidStack[0];
        if (mFluidOutputs == null) mFluidOutputs = new FluidStack[0];
        
        mInputs = GT_Utility.getArrayListWithoutNulls(mInputs).toArray(new GT_RecipeInput[0]);
        mOutputs = GT_Utility.getArrayListWithoutNulls(mOutputs).toArray(new GT_RecipeOutput[0]);
        mFluidInputs = GT_Utility.getArrayListWithoutNulls(mFluidInputs).toArray(new FluidStack[0]);
        mFluidOutputs = GT_Utility.getArrayListWithoutNulls(mFluidOutputs).toArray(new FluidStack[0]);
        
        for (int i = 0; i < mFluidInputs.length; i++) mFluidInputs[i] = new GT_FluidStack(mFluidInputs[i]);
        for (int i = 0; i < mFluidOutputs.length; i++) mFluidOutputs[i] = new GT_FluidStack(mFluidOutputs[i]);
    }
    
    public static GT_RecipeInput[] wrapInputs(ItemStack[] aInputs) {
        if (aInputs == null) {
            return new GT_RecipeInput[0];
        }
        GT_RecipeInput[] rInputs = new GT_RecipeInput[aInputs.length];
        for (int i = 0; i < aInputs.length; i++) {
            if (aInputs[i] != null) {
                rInputs[i] = new GT_RecipeInput(aInputs[i]);
            }
        }
        return rInputs;
    }
    
    public static GT_RecipeOutput[] wrapOutputs(ItemStack[] aOutputs) {
        if (aOutputs == null) {
            return new GT_RecipeOutput[0];
        }
        GT_RecipeOutput[] rOutputs = new GT_RecipeOutput[aOutputs.length];
        for (int i = 0; i < aOutputs.length; i++) {
            if (aOutputs[i] != null) {
                rOutputs[i] = new GT_RecipeOutput(aOutputs[i]);
            }
        }
        return rOutputs;
    }
    
    public static ItemStack[] unwrapOutputs(GT_RecipeOutput[] aOutputs) {
        if (aOutputs == null) {
            return new ItemStack[0];
        }
        ItemStack[] rOutputs = new ItemStack[aOutputs.length];
        for (int i = 0; i < aOutputs.length; i++) {
            if (aOutputs[i] != null) {
                rOutputs[i] = aOutputs[i].getShownOutput();
            }
        }
        return rOutputs;
    }
    
    public GT_MachineRecipe(ItemStack[] aInputs, ItemStack[] aOutputs, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs) {
        this(wrapInputs(aInputs), wrapOutputs(aOutputs), aFluidInputs, aFluidOutputs);
    }
    
    public GT_MachineRecipe setSpecialItems(Object aSpecialItems) {
        mSpecialItems = aSpecialItems;
        return this;
    }
    
    public GT_MachineRecipe setDuration(int aDuration) {
        mDuration = aDuration;
        return this;
    }
    
    public GT_MachineRecipe setEUt(int aEUt) {
        mEUt = aEUt;
        return this;
    }
    
    public GT_MachineRecipe setSpecialValue(int aSpecialValue) {
        mSpecialValue = aSpecialValue;
        return this;
    }
    
    public GT_MachineRecipe setEnabled(boolean aEnabled) {
        mEnabled = aEnabled;
        return this;
    }
    
    public boolean isEnabled() {
        if (mEnableCondition != null) {
            return GT_RecipeConditions.getConditionValue(mEnableCondition) ^ mInvertCondition;
        }
        return mEnabled;
    }
    
    public GT_MachineRecipe setEnableCondition(String aEnableCondition) {
        mEnableCondition = aEnableCondition;
        return this;
    }
    
    public GT_MachineRecipe setInvertCondition(boolean aInvertCondition) {
        mInvertCondition = aInvertCondition;
        return this;
    }
    
    public GT_MachineRecipe setHidden(boolean aHidden) {
        mHidden = aHidden;
        return this;
    }
    
    public GT_MachineRecipe setFakeRecipe(boolean aFakeRecipe) {
        mFakeRecipe = aFakeRecipe;
        return this;
    }
    
    public GT_MachineRecipe setCanBeBuffered(boolean aCanBeBuffered) {
        mCanBeBuffered = aCanBeBuffered;
        return this;
    }
    
    public GT_MachineRecipe setNeedsEmptyOutput(boolean aNeedsEmptyOutput) {
        mNeedsEmptyOutput = aNeedsEmptyOutput;
        return this;
    }
    
    public GT_MachineRecipe setNeiDesc(String... aNeiDesc) {
        neiDesc = aNeiDesc;
        return this;
    }
    
    public String[] getNeiDesc() {
        return neiDesc;
    }
    
    public List<ItemStack> getRepresentativeInput(int aIndex) {
        if (aIndex < 0 || aIndex >= mInputs.length || mInputs[aIndex] == null) {
            return null;
        }
        return mInputs[aIndex].getInputStacks();
    }
    
    public FluidStack getFluidOutput(int aIndex) {
        if (aIndex < 0 || aIndex >= mFluidOutputs.length || mFluidOutputs[aIndex] == null) {
            return null;
        }
        return mFluidOutputs[aIndex].copy();
    }

    public ItemStack getOutput(int aIndex) {
        if (aIndex < 0 || aIndex >= mOutputs.length || mOutputs[aIndex] == null) {
            return null;
        }
        return mOutputs[aIndex].getShownOutput().copy();
    }
    
    public int getOutputChance(int aIndex) {
        if (aIndex < 0 || aIndex >= mOutputs.length || mOutputs[aIndex] == null) {
            return 0;
        }
        return mOutputs[aIndex].getChance();
    }
    
    /**
     * Overriding this method and getOutputPositionedStacks allows for custom NEI stack placement
     * @return A list of input stacks
     */
    public ArrayList<PositionedStack> getInputPositionedStacks(){
    	return null;
    }

    /**
     * Overriding this method and getInputPositionedStacks allows for custom NEI stack placement
     * @return A list of output stacks
     */
    public ArrayList<PositionedStack> getOutputPositionedStacks(){
    	return null;
    }

    /**
     * Validates the recipe by making sure the duration and EU/t have been set, it has input items and/or fluid as well
     * as output items and/or fluid, unless it is a fake recipe (fake recipes are always considered valid).
     * Because of the chained setters, some details could accidentally be omitted, and this method should be used before
     * adding a recipe to a list or map.
     */
    public boolean isValidRecipe() {
        if (mFakeRecipe) return true;
        if (mEUt <= 0) return false;
        if (mInputs.length == 0 && mFluidInputs.length == 0) return false;
        if (mOutputs.length == 0 && mFluidOutputs.length == 0) return false;
        // Ore dictionary inputs/outputs that refer to un-registered entries can be considered invalid.
        for (GT_RecipeInput mInput : mInputs) {
            if (mInput.getInputStacks().isEmpty()) {
                return false;
            }
        }
        for (GT_RecipeOutput mOutput : mOutputs) {
            if (mOutput.getShownOutput() == null) {
                return false;
            }
        }
        return true;
    }
    
    // Copied from StackOverflow
    private static int gcd(int a, int b) { return b==0 ? a : gcd(b, a%b); }
    
    public GT_MachineRecipe optimize() {
        assert(isValidRecipe());
        if (!mFakeRecipe && !mOptimized) { // fake recipes probably aren't worth optimizing
            int tDivisor = mDuration;
            if (tDivisor >= 32) {
                for (int i = 0; i < mInputs.length && tDivisor > 1; i++) {
                    tDivisor = gcd(tDivisor, mInputs[i].getCount());
                }
                for (int i = 0; i < mOutputs.length && tDivisor > 1; i++) {
                    tDivisor = gcd(tDivisor, mOutputs[i].getCount());
                }
                for (int i = 0; i < mFluidInputs.length && tDivisor > 1; i++) {
                    tDivisor = gcd(tDivisor, mFluidInputs[i].amount);
                }
                for (int i = 0; i < mFluidOutputs.length && tDivisor > 1; i++) {
                    tDivisor = gcd(tDivisor, mFluidOutputs[i].amount);
                }
                if (tDivisor > 1) {
                    for (GT_RecipeInput tInput : mInputs) {
                        tInput.setCount(tInput.getCount() / tDivisor);
                    }
                    for (GT_RecipeOutput tOutput : mOutputs) {
                        tOutput.setCount(tOutput.getCount() / tDivisor);
                    }
                    for (FluidStack tFluidInput : mFluidInputs) {
                        tFluidInput.amount /= tDivisor;
                    }
                    for (FluidStack tFluidOutput : mFluidOutputs) {
                        tFluidOutput.amount /= tDivisor;
                    }
                }
            }
            mOptimized = true;
        }
        return this;
    }
    
    public GT_MachineRecipe copy() {
        GT_MachineRecipe rRecipe = new GT_MachineRecipe(mInputs, mOutputs, mFluidInputs, mFluidOutputs);
        rRecipe.mCanBeBuffered = this.mCanBeBuffered;
        rRecipe.mDuration = this.mDuration;
        rRecipe.mEUt = this.mEUt;
        rRecipe.mEnableCondition = this.mEnableCondition;
        rRecipe.mEnabled = this.mEnabled;
        rRecipe.mFakeRecipe = this.mFakeRecipe;
        rRecipe.mHidden = this.mHidden;
        rRecipe.mInvertCondition = this.mInvertCondition;
        rRecipe.mNeedsEmptyOutput = this.mNeedsEmptyOutput;
        rRecipe.mOptimized = this.mOptimized;
        rRecipe.mSpecialItems = this.mSpecialItems;
        rRecipe.mSpecialValue = this.mSpecialValue;
        rRecipe.neiDesc = this.neiDesc;
        
        return rRecipe;
    }
    
    public boolean isRecipeInputEqual(boolean aDecreaseStacksizeBySuccess, FluidStack[] aFluidInputs, ItemStack... aInputs) {
        return isRecipeInputEqual(aDecreaseStacksizeBySuccess, false, aFluidInputs, aInputs);
    }

    public boolean isRecipeInputEqual(boolean aDecreaseStacksizeBySuccess, boolean aIgnoreCounts, FluidStack[] aFluidInputs, ItemStack... aInputs) {
        if (mFluidInputs.length > 0 && aFluidInputs == null) {
            return false;
        }
        int amt;
        for (FluidStack tFluid : mFluidInputs) {
            if (tFluid != null) {
                boolean temp = true;
                amt = tFluid.amount;
                for (FluidStack aFluid : aFluidInputs) {
                    if (aFluid != null && aFluid.isFluidEqual(tFluid)) {
                        if (aIgnoreCounts) {
                            temp = false;
                            break;
                        }
                        amt -= aFluid.amount;
                        if (amt < 1) {
                            temp = false;
                            break;
                        }
                    }
                }
                if (temp) {
                    return false;
                }
            }
        }

        if (mInputs.length > 0 && aInputs == null) {
            return false;
        }
        
        for (GT_RecipeInput tInput : mInputs) {
            if (tInput != null) {
                amt = tInput.getCount();
                boolean temp = true;
                for (ItemStack tStack : aInputs) {
                    if (tInput.inputMatches(tStack)) {
                        if (aIgnoreCounts) {
                            temp = false;
                            break;
                        }
                        amt -= tStack.stackSize;
                        if (amt < 1) {
                            temp = false;
                            break;
                        }
                    }
                }
                if (temp) {
                    return false;
                }
            }
        }
        if (aDecreaseStacksizeBySuccess) {
            if (aFluidInputs != null) {
                for (FluidStack tFluid : mFluidInputs) {
                    if (tFluid != null) {
                        amt = tFluid.amount;
                        for (FluidStack aFluid : aFluidInputs) {
                            if (aFluid != null && aFluid.isFluidEqual(tFluid)) {
                                if (aIgnoreCounts) {
                                    aFluid.amount -= amt;
                                    break;
                                }
                                if (aFluid.amount < amt) {
                                    amt -= aFluid.amount;
                                    aFluid.amount = 0;
                                } else {
                                    aFluid.amount -= amt;
                                    amt = 0;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if (aInputs != null) {
                for (GT_RecipeInput tInput : mInputs) {
                    if (tInput != null) {
                        amt = tInput.getCount();
                        for (ItemStack aStack : aInputs) {
                            if (tInput.inputMatches(aStack)) {
                                if (aIgnoreCounts) {
                                    aStack.stackSize -= amt;
                                    break;
                                }
                                if (aStack.stackSize < amt) {
                                    amt -= aStack.stackSize;
                                    aStack.stackSize = 0;
                                } else {
                                    aStack.stackSize -= amt;
                                    amt = 0;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public int compareTo(GT_MachineRecipe aRecipe) {
        // first lowest tier recipes
        // then fastest
        // then with lowest special value
        // then dry recipes
        // then with fewer inputs
        // and finally by earlier fluid id or item id, to keep ordering consistent
        // at least when modlist stays the same.
        if (this.mEUt != aRecipe.mEUt) {
            return this.mEUt - aRecipe.mEUt;
        } else if (this.mDuration != aRecipe.mDuration) {
            return this.mDuration - aRecipe.mDuration;
        } else if (this.mSpecialValue != aRecipe.mSpecialValue) {
            return this.mSpecialValue - aRecipe.mSpecialValue;
        } else if (this.mFluidInputs.length != aRecipe.mFluidInputs.length) {
            return this.mFluidInputs.length - aRecipe.mFluidInputs.length;
        } else if (this.mInputs.length != aRecipe.mInputs.length) {
            return this.mInputs.length - aRecipe.mInputs.length;
        }
        for (int i = 0; i < mFluidInputs.length; i++) {
            if (this.mFluidInputs[i].getFluidID() != aRecipe.mFluidInputs[i].getFluidID()) {
                return this.mFluidInputs[i].getFluidID() - aRecipe.mFluidInputs[i].getFluidID();
            }
        }
        for (int i = 0; i < mInputs.length; i++) {
            if (Item.getIdFromItem(this.mInputs[i].getInputStacks().get(0).getItem()) != Item.getIdFromItem(aRecipe.mInputs[i].getInputStacks().get(0).getItem())) {
                return Item.getIdFromItem(this.mInputs[i].getInputStacks().get(0).getItem()) - Item.getIdFromItem(aRecipe.mInputs[i].getInputStacks().get(0).getItem());
            }
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return String.format("Inputs: %s, Fluid Inputs: %s, Outputs: %s, Fluid Outputs: %s", Arrays.toString(mInputs), Arrays.toString(mFluidInputs), Arrays.toString(mOutputs), Arrays.toString(mFluidOutputs));
    }
    
}