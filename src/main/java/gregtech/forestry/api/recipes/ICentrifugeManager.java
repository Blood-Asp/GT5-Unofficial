package gregtech.forestry.api.recipes;

import java.util.Map;

import net.minecraft.item.ItemStack;

public abstract interface ICentrifugeManager
  extends ICraftingProvider<ICentrifugeRecipe>
{
  public abstract void addRecipe(int paramInt, ItemStack paramItemStack, Map<ItemStack, Float> paramMap);
}
