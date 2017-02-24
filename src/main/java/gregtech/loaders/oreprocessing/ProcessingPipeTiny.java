package gregtech.loaders.oreprocessing;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.SubTag;
import gregtech.api.enums.ToolDictNames;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_Utility;
import net.minecraft.item.ItemStack;

public class ProcessingPipeTiny implements gregtech.api.interfaces.IOreRecipeRegistrator {
    public ProcessingPipeTiny() {
        OrePrefixes.pipeTiny.add(this);
    }

    public void registerOre(OrePrefixes aPrefix, Materials aMaterial, String aOreDictName, String aModName, ItemStack aStack) {
        if ((!aMaterial.contains(SubTag.NO_WORKING)) || (!aMaterial.contains(SubTag.NO_SMASHING))) {
            if (!(aMaterial == Materials.Redstone || aMaterial == Materials.Glowstone)) {
                GT_ModHandler.addCraftingRecipe(GT_Utility.copyAmount(8L, new Object[]{aStack}), GT_ModHandler.RecipeBits.DO_NOT_CHECK_FOR_COLLISIONS | GT_ModHandler.RecipeBits.BUFFERED, new Object[]{"PPP", "H W", "PPP", 'P', OrePrefixes.plate.get(aMaterial), 'H', ToolDictNames.craftingToolHardHammer, 'W', ToolDictNames.craftingToolWrench});
            }
        }
    }
}
