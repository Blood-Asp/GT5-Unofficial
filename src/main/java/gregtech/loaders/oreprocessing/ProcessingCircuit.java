package gregtech.loaders.oreprocessing;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import net.minecraft.item.ItemStack;

public class ProcessingCircuit implements gregtech.api.interfaces.IOreRecipeRegistrator {
    public ProcessingCircuit() {
        OrePrefixes.circuit.add(this);
    }

    public void registerOre(OrePrefixes aPrefix, Materials aMaterial, String aOreDictName, String aModName, ItemStack aStack) {
    	if(gregtech.api.util.GT_OreDictUnificator.isBlacklisted(aStack)&&aModName.equals("gregtech"))return;
        switch (aMaterial.mName) {
            case "Good":
            case "Data":
            case "Elite":
            case "Master":
            case "Ultimate":
                if (!gregtech.api.util.GT_OreDictUnificator.isBlacklisted(aStack)&&!aModName.equals("gregtech"))
                    GT_ModHandler.removeRecipeByOutput(aStack);
                break;
            case "Primitive":
                GT_ModHandler.removeRecipeByOutput(aStack);
               break;
            case "Basic":
                GT_ModHandler.removeRecipeByOutput(aStack);
                GT_ModHandler.addShapelessCraftingRecipe(aStack, new Object[]{ItemList.Circuit_Integrated.getWildcard(1L, new Object[0])});
               break;
            case "Advanced":
                GT_ModHandler.removeRecipeByOutput(aStack);
               break;
        }
    }
}
