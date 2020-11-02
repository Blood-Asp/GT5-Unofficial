package gregtech.api.recipes;

import java.util.Collection;

public class GT_RecipeMap {

    /**
     * The List of all Recipes
     */
    public final Collection<GT_MachineRecipe> mRecipeList;
    /**
     * String used as an unlocalised Name.
     */
    public String mUnlocalizedName = "";
    /**
     * String used in NEI for the Recipe Lists. If null it will use the unlocalised Name instead
     */
    public String mNEIName = "";
    /**
     * GUI used for NEI Display. Usually the GUI of the Machine itself
     */
    public String mNEIGUIPath = "";
    public String mNEISpecialValuePre = "", mNEISpecialValuePost = "";
    public int mInputSlots = 1;
    public int mOutputSlots = 1;
    public int mNEISpecialValueMultiplier = 1;
    public int mMinimalInputItems = 1;
    public int mMinimalInputFluids = 0;
    public int mAmperage = 1;
    public boolean mNEIAllowed = true;
    public boolean mShowVoltageAmperageInNEI = true;
    
    public GT_RecipeMap(Collection<GT_MachineRecipe> aRecipeList) {
        mRecipeList = aRecipeList;
    }
    
    public GT_RecipeMap setUnlocalizedName(String aUnlocalizedName) {
        mUnlocalizedName = aUnlocalizedName;
        return this;
    }
    
    public GT_RecipeMap setNEIName(String aNEIName) {
        mNEIName = aNEIName;
        return this;
    }
    
    public GT_RecipeMap setNEIGUIPath(String aNEIGUIPath) {
        mNEIGUIPath = aNEIGUIPath;
        return this;
    }
    
    public GT_RecipeMap setNEISpecialValuePre(String aNEISpecialValuePre) {
        mNEISpecialValuePre = aNEISpecialValuePre;
        return this;
    }
    
    public GT_RecipeMap setNEISpecialValuePost(String aNEISpecialValuePost) {
        mNEISpecialValuePost = aNEISpecialValuePost;
        return this;
    }
    
    public GT_RecipeMap setInputSlots(int aInputSlots) {
        mInputSlots = aInputSlots;
        return this;
    }
    
    public GT_RecipeMap setOutputSlots(int aOutputSlots) {
        mOutputSlots = aOutputSlots;
        return this;
    }
    
    public GT_RecipeMap setNEISpecialValueMultiplier(int aNEISpecialValueMultiplier) {
        mNEISpecialValueMultiplier = aNEISpecialValueMultiplier;
        return this;
    }
    
    public GT_RecipeMap setMinimalInputItems(int aMinimalInputItems) {
        mMinimalInputItems = aMinimalInputItems;
        return this;
    }
    
    public GT_RecipeMap setMinimalInputFluids(int aMinimalInputFluids) {
        mMinimalInputFluids = aMinimalInputFluids;
        return this;
    }
    
    public GT_RecipeMap setAmperage(int aAmperage) {
        mAmperage = aAmperage;
        return this;
    }
    
    public GT_RecipeMap setNEIAllowed(boolean aNEIAllowed) {
        mNEIAllowed = aNEIAllowed;
        return this;
    }
    
    public GT_RecipeMap setShowVoltageAmperageInNEI(boolean aShowVoltageAmperageInNEI) {
        mShowVoltageAmperageInNEI = aShowVoltageAmperageInNEI;
        return this;
    }
    
}