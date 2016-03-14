package gregtech.forestry.api.recipes;

import net.minecraftforge.fluids.FluidStack;

public abstract interface IStillRecipe
  extends IForestryRecipe
{
  public abstract int getCyclesPerUnit();
  
  public abstract FluidStack getInput();
  
  public abstract FluidStack getOutput();
}
