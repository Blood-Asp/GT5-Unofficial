package gregtech.forestry.api.recipes;

import java.util.Collection;
import java.util.Map;

public abstract interface ICraftingProvider<T extends IForestryRecipe>
{
  public abstract boolean addRecipe(T paramT);
  
  public abstract boolean removeRecipe(T paramT);
  
  public abstract Collection<T> recipes();
  
  @Deprecated
  public abstract Map<Object[], Object[]> getRecipes();
}
