package gregtech.forestry.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract interface ICarpenterManager
  extends ICraftingProvider<ICarpenterRecipe>
{
  public abstract void addRecipe(ItemStack paramItemStack1, ItemStack paramItemStack2, Object... paramVarArgs);
  
  public abstract void addRecipe(int paramInt, ItemStack paramItemStack1, ItemStack paramItemStack2, Object... paramVarArgs);
  
  public abstract void addRecipe(int paramInt, FluidStack paramFluidStack, ItemStack paramItemStack1, ItemStack paramItemStack2, Object... paramVarArgs);
}
