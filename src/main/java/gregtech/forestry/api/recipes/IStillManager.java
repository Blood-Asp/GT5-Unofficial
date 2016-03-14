package gregtech.forestry.api.recipes;

import net.minecraftforge.fluids.FluidStack;

public abstract interface IStillManager
  extends ICraftingProvider<IStillRecipe>
{
  public abstract void addRecipe(int paramInt, FluidStack paramFluidStack1, FluidStack paramFluidStack2);
}
