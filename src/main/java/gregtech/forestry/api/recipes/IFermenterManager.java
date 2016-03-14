package gregtech.forestry.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract interface IFermenterManager
  extends ICraftingProvider<IFermenterRecipe>
{
  public abstract void addRecipe(ItemStack paramItemStack, int paramInt, float paramFloat, FluidStack paramFluidStack1, FluidStack paramFluidStack2);
  
  public abstract void addRecipe(ItemStack paramItemStack, int paramInt, float paramFloat, FluidStack paramFluidStack);
}
