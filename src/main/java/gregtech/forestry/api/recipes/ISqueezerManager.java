package gregtech.forestry.api.recipes;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract interface ISqueezerManager
  extends ICraftingProvider<ISqueezerRecipe>
{
  public abstract void addRecipe(int paramInt1, ItemStack[] paramArrayOfItemStack, FluidStack paramFluidStack, ItemStack paramItemStack, int paramInt2);
  
  public abstract void addRecipe(int paramInt, ItemStack[] paramArrayOfItemStack, FluidStack paramFluidStack);
  
  public abstract void addContainerRecipe(int paramInt, ItemStack paramItemStack1, @Nullable ItemStack paramItemStack2, float paramFloat);
}
