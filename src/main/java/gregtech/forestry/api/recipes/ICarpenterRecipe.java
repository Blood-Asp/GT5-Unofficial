package gregtech.forestry.api.recipes;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract interface ICarpenterRecipe
  extends IForestryRecipe
{
  public abstract int getPackagingTime();
  
  public abstract IDescriptiveRecipe getCraftingGridRecipe();
  
  @Nullable
  public abstract ItemStack getBox();
  
  @Nullable
  public abstract FluidStack getFluidResource();
}
