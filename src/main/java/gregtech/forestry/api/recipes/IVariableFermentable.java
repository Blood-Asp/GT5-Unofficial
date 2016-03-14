package gregtech.forestry.api.recipes;

import net.minecraft.item.ItemStack;

public abstract interface IVariableFermentable
{
  public abstract float getFermentationModifier(ItemStack paramItemStack);
}
