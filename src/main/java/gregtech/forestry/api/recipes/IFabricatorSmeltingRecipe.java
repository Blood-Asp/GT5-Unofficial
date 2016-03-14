package gregtech.forestry.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract interface IFabricatorSmeltingRecipe
  extends IForestryRecipe
{
  public abstract ItemStack getResource();
  
  public abstract int getMeltingPoint();
  
  public abstract FluidStack getProduct();
}
