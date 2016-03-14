package gregtech.forestry.api.recipes;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract interface IFabricatorSmeltingManager
  extends ICraftingProvider<IFabricatorSmeltingRecipe>
{
  public abstract void addSmelting(ItemStack paramItemStack, FluidStack paramFluidStack, int paramInt);
}
