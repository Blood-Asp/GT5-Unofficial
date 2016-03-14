package gregtech.forestry.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract interface IFabricatorManager
  extends ICraftingProvider<IFabricatorRecipe>
{
  public abstract void addRecipe(ItemStack paramItemStack1, FluidStack paramFluidStack, ItemStack paramItemStack2, Object[] paramArrayOfObject);
  
  @Deprecated
  public abstract void addSmelting(ItemStack paramItemStack, FluidStack paramFluidStack, int paramInt);
}
