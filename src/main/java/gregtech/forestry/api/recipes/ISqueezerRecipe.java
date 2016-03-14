package gregtech.forestry.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract interface ISqueezerRecipe
  extends IForestryRecipe
{
  public abstract ItemStack[] getResources();
  
  public abstract int getProcessingTime();
  
  public abstract ItemStack getRemnants();
  
  public abstract float getRemnantsChance();
  
  public abstract FluidStack getFluidOutput();
}
