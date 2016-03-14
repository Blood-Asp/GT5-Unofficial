package gregtech.forestry.api.recipes;

import net.minecraft.item.ItemStack;

public abstract interface IMoistenerManager
  extends ICraftingProvider<IMoistenerRecipe>
{
  public abstract void addRecipe(ItemStack paramItemStack1, ItemStack paramItemStack2, int paramInt);
}
