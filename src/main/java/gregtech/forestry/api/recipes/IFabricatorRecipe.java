package gregtech.forestry.api.recipes;

import javax.annotation.Nullable;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public abstract interface IFabricatorRecipe
  extends IForestryRecipe
{
  public abstract FluidStack getLiquid();
  
  public abstract Object[] getIngredients();
  
  public abstract int getWidth();
  
  public abstract int getHeight();
  
  public abstract boolean preservesNbt();
  
  @Nullable
  public abstract ItemStack getPlan();
  
  public abstract ItemStack getRecipeOutput();
  
  @Deprecated
  public abstract ItemStack getCraftingResult(IInventory paramIInventory);
  
  @Deprecated
  public abstract boolean matches(@Nullable ItemStack paramItemStack, ItemStack[][] paramArrayOfItemStack);
}
