package gregtech.forestry.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public abstract interface IFermenterRecipe
  extends IForestryRecipe
{
  public abstract ItemStack getResource();
  
  public abstract FluidStack getFluidResource();
  
  public abstract int getFermentationValue();
  
  public abstract float getModifier();
  
  public abstract Fluid getOutput();
}
