package gregtech.api.recipes;

import gregtech.api.objects.GT_FluidStack;
import net.minecraftforge.fluids.FluidStack;


/**
 * Heavily modified version of GT_Recipe, which will eventually allow oredicted input and output as well as checking NBT tags,
 * and loading of XML (or maybe other format) recipes.
 */
public class GT_MachineRecipe {
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
    public boolean mEnabled = true;
    /**
     * Use this to make a recipe enabled depending on a certain config setting.
     * TODO: implement map for looking up said config settings.
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
        
        for (int i = 0; i < mFluidInputs.length; i++) mFluidInputs[i] = new GT_FluidStack(mFluidInputs[i]);
        for (int i = 0; i < mFluidOutputs.length; i++) mFluidOutputs[i] = new GT_FluidStack(mFluidOutputs[i]);
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
    
}