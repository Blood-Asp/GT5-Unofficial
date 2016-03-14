package gregtech.forestry.api.recipes;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public abstract interface IDescriptiveRecipe
  extends IRecipe
{
  public abstract int getWidth();
  
  public abstract int getHeight();
  
  public abstract Object[] getIngredients();
  
  public abstract boolean preserveNBT();
  
  @Deprecated
  public abstract boolean matches(IInventory paramIInventory, World paramWorld);
}
