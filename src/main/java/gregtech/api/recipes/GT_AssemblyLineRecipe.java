package gregtech.api.recipes;

import java.util.ArrayList;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class GT_AssemblyLineRecipe extends GT_MachineRecipe {
    
    public static final ArrayList<GT_AssemblyLineRecipe> sAssemblyLineRecipes = new ArrayList<>(50);
    
    public ItemStack mResearchItem;
    public int mResearchTime;
    
    public GT_AssemblyLineRecipe(GT_RecipeInput[] aInputs, GT_RecipeOutput[] aOutputs, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs) {
        super(aInputs, aOutputs, aFluidInputs, aFluidOutputs);
    }
 
    public GT_AssemblyLineRecipe setResearchItem(ItemStack aResearchItem) {
        mResearchItem = aResearchItem;
        return this;
    }
    
    public GT_AssemblyLineRecipe setResearchTime(int aResearchTime) {
        mResearchTime = aResearchTime;
        return this;
    }
 
    @Override
    public boolean isValidRecipe() {
        if (mResearchItem == null) return false;
        if (mResearchTime <= 0) return false;
        return super.isValidRecipe();
    }
 
}
