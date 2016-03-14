package gregtech.forestry.api.recipes;

import net.minecraft.item.ItemStack;

public abstract interface IMoistenerRecipe
  extends IForestryRecipe
{
  public abstract int getTimePerItem();
  
  public abstract ItemStack getResource();
  
  public abstract ItemStack getProduct();
}
