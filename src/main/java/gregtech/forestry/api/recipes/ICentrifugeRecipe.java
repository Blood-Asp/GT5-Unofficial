package gregtech.forestry.api.recipes;

import java.util.Collection;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;

public abstract interface ICentrifugeRecipe
  extends IForestryRecipe
{
  public abstract ItemStack getInput();
  
  public abstract int getProcessingTime();
  
  public abstract Collection<ItemStack> getProducts(Random paramRandom);
  
  public abstract Map<ItemStack, Float> getAllProducts();
}
