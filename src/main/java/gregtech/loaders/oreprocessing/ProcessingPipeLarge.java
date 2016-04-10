package gregtech.loaders.oreprocessing;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.SubTag;
import gregtech.api.enums.ToolDictNames;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_Utility;
import net.minecraft.item.ItemStack;

public class ProcessingPipeLarge implements gregtech.api.interfaces.IOreRecipeRegistrator {
    public ProcessingPipeLarge() {
        OrePrefixes.pipeLarge.add(this);
    }

    public void registerOre(OrePrefixes aPrefix, Materials aMaterial, String aOreDictName, String aModName, ItemStack aStack) {
        if ((!aMaterial.contains(SubTag.NO_WORKING)) && ((aMaterial.contains(SubTag.WOOD)) || (!aMaterial.contains(SubTag.NO_SMASHING)))) {
            GT_ModHandler.addCraftingRecipe(GT_Utility.copyAmount(1L, new Object[]{aStack}), GT_ModHandler.RecipeBits.MIRRORED | GT_ModHandler.RecipeBits.DO_NOT_CHECK_FOR_COLLISIONS | GT_ModHandler.RecipeBits.BUFFERED, new Object[]{"PHP", "P P", "PWP", Character.valueOf('P'), aMaterial == Materials.Wood ? OrePrefixes.plank.get(aMaterial) : OrePrefixes.plate.get(aMaterial), Character.valueOf('H'), aMaterial.contains(SubTag.WOOD) ? ToolDictNames.craftingToolSoftMallet : ToolDictNames.craftingToolHardHammer, Character.valueOf('W'), aMaterial.contains(SubTag.WOOD) ? ToolDictNames.craftingToolSaw : ToolDictNames.craftingToolWrench});
        }
    }
}
