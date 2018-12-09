package gregtech.loaders.oreprocessing;

import gregtech.api.GregTech_API;
import gregtech.api.enums.*;
import gregtech.api.objects.GT_CopiedBlockTexture;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;
import gregtech.common.GT_Proxy;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class ProcessingPlate implements gregtech.api.interfaces.IOreRecipeRegistrator {
    public ProcessingPlate() {
        OrePrefixes.plate.add(this);
        OrePrefixes.plateDouble.add(this);
        OrePrefixes.plateTriple.add(this);
        OrePrefixes.plateQuadruple.add(this);
        OrePrefixes.plateQuintuple.add(this);
        OrePrefixes.plateDense.add(this);
        OrePrefixes.plateAlloy.add(this);
    }

    public void registerOre(OrePrefixes aPrefix, Materials aMaterial, String aOreDictName, String aModName, ItemStack aStack) {
        boolean aNoSmashing = aMaterial.contains(SubTag.NO_SMASHING);
        boolean aNoWorking = aMaterial.contains(SubTag.NO_WORKING);
        long aMaterialMass = aMaterial.getMass();
        
        switch (aPrefix) {
            case plate:
                GT_ModHandler.removeRecipeByOutput(aStack);
                GT_ModHandler.removeRecipe(new ItemStack[]{aStack});

                if (aMaterial.mStandardMoltenFluid != null) {
                    GT_Values.RA.addFluidSolidifierRecipe(ItemList.Shape_Mold_Plate.get(0L, new Object[0]), aMaterial.getMolten(144L), GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 1L), 32, 8);
                }
                switch (aMaterial.mName) {
                    case "Iron":
                        GregTech_API.registerCover(aStack, new GT_CopiedBlockTexture(Blocks.iron_block, 1, 0), null);
                        break;
                    case "Gold":
                        GregTech_API.registerCover(aStack, new GT_CopiedBlockTexture(Blocks.gold_block, 1, 0), null);
                        break;
                    case "Diamond":
                        GregTech_API.registerCover(aStack, new GT_CopiedBlockTexture(Blocks.diamond_block, 1, 0), null);
                        break;
                    case "Emerald":
                        GregTech_API.registerCover(aStack, new GT_CopiedBlockTexture(Blocks.emerald_block, 1, 0), null);
                        break;
                    case "Lapis":
                        GregTech_API.registerCover(aStack, new GT_CopiedBlockTexture(Blocks.lapis_block, 1, 0), null);
                        break;
                    case "Coal":
                        GregTech_API.registerCover(aStack, new GT_CopiedBlockTexture(Blocks.coal_block, 1, 0), null);
                        break;
                    case "Redstone":
                        GregTech_API.registerCover(aStack, new GT_CopiedBlockTexture(Blocks.redstone_block, 1, 0), null);
                        break;
                    case "Glowstone":
                        GregTech_API.registerCover(aStack, new GT_CopiedBlockTexture(Blocks.glowstone, 1, 0), null);
                        break;
                    case "NetherQuartz":
                        GregTech_API.registerCover(aStack, new GT_CopiedBlockTexture(Blocks.quartz_block, 1, 0), null);
                        break;
                    case "Obsidian":
                        GregTech_API.registerCover(aStack, new GT_CopiedBlockTexture(Blocks.obsidian, 1, 0), null);
                        break;
                    case "Stone":
                        GregTech_API.registerCover(aStack, new GT_CopiedBlockTexture(Blocks.stone, 1, 0), null);
                        break;
                    case "GraniteBlack":
                        GregTech_API.registerCover(aStack, new GT_RenderedTexture(gregtech.api.enums.Textures.BlockIcons.GRANITE_BLACK_SMOOTH), null);
                        break;
                    case "GraniteRed":
                        GregTech_API.registerCover(aStack, new GT_RenderedTexture(gregtech.api.enums.Textures.BlockIcons.GRANITE_RED_SMOOTH), null);
                        break;
                    case "Concrete":
                        GregTech_API.registerCover(aStack, new GT_RenderedTexture(gregtech.api.enums.Textures.BlockIcons.CONCRETE_LIGHT_SMOOTH), null);
                        break;
                    default:
                        GregTech_API.registerCover(aStack, new GT_RenderedTexture(aMaterial.mIconSet.mTextures[71], aMaterial.mRGBa, false), null);
                }

                if (aMaterial.mFuelPower > 0)
                    GT_Values.RA.addFuel(GT_Utility.copyAmount(1L, new Object[]{aStack}), null, aMaterial.mFuelPower, aMaterial.mFuelType);
                GT_Utility.removeSimpleIC2MachineRecipe(GT_Utility.copyAmount(9L, new Object[]{aStack}), GT_ModHandler.getCompressorRecipeList(), GT_OreDictUnificator.get(OrePrefixes.plateDense, aMaterial, 1L));
                GT_Values.RA.addImplosionRecipe(GT_Utility.copyAmount(aMaterial == Materials.MeteoricIron ? 1 : 2, new Object[]{aStack}), 2, GT_OreDictUnificator.get(OrePrefixes.compressed, aMaterial, 1L), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.DarkAsh, 1L));
                GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.foil, aMaterial, 2L), GT_Proxy.tBits, new Object[]{"hX", 'X', OrePrefixes.plate.get(aMaterial)});
                
                if (aMaterial == Materials.Paper)
                    GT_ModHandler.addCraftingRecipe(GT_Utility.copyAmount(GregTech_API.sRecipeFile.get(gregtech.api.enums.ConfigCategories.Recipes.harderrecipes, aStack, true) ? 2L : 3L, new Object[]{aStack}), new Object[]{"XXX", 'X', new ItemStack(net.minecraft.init.Items.reeds, 1, 32767)});

                if ((aMaterial.mUnificatable) && (aMaterial.mMaterialInto == aMaterial)) {
                    if (!aNoSmashing && GregTech_API.sRecipeFile.get(ConfigCategories.Tools.hammerplating, aMaterial.toString(), true)) {
                        GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 1L), GT_Proxy.tBits, new Object[]{"h", "X", "X", 'X', OrePrefixes.ingot.get(aMaterial)});
                        GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 1L), GT_Proxy.tBits, new Object[]{"H", "X", 'H', ToolDictNames.craftingToolForgeHammer, 'X', OrePrefixes.ingot.get(aMaterial)});
                        GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 1L), GT_Proxy.tBits, new Object[]{"h", "X", 'X', OrePrefixes.gem.get(aMaterial)});
                        GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 1L), GT_Proxy.tBits, new Object[]{"H", "X", 'H', ToolDictNames.craftingToolForgeHammer, 'X', OrePrefixes.gem.get(aMaterial)});
                        GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 1L), GT_Proxy.tBits, new Object[]{"h", "X", 'X', OrePrefixes.ingotDouble.get(aMaterial)});
                        GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 2L), GT_Proxy.tBits, new Object[]{"H", "X", 'H', ToolDictNames.craftingToolForgeHammer, 'X', OrePrefixes.ingotDouble.get(aMaterial)});
                    }
                    if ((aMaterial.contains(SubTag.MORTAR_GRINDABLE)) && (GregTech_API.sRecipeFile.get(ConfigCategories.Tools.mortar, aMaterial.mName, true)))
                        GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, aMaterial, 1L), GT_Proxy.tBits, new Object[]{"X", "m", 'X', OrePrefixes.plate.get(aMaterial)});
                }
                break;
            case plateDouble:
                GT_ModHandler.removeRecipeByOutput(aStack);
                GregTech_API.registerCover(aStack, new gregtech.api.objects.GT_RenderedTexture(aMaterial.mIconSet.mTextures[72], aMaterial.mRGBa, false), null);
                if (!aNoSmashing) {
                    GT_Values.RA.addBenderRecipe(GT_Utility.copyAmount(2L, new Object[]{aStack}), GT_OreDictUnificator.get(OrePrefixes.plateQuadruple, aMaterial, 1L), (int) Math.max(aMaterialMass * 2L, 1L), 96);
                    if (GregTech_API.sRecipeFile.get(gregtech.api.enums.ConfigCategories.Tools.hammerdoubleplate, OrePrefixes.plate.get(aMaterial).toString(), true)) {
                        Object aPlateStack = OrePrefixes.plate.get(aMaterial);
                        GT_ModHandler.addCraftingRecipe(GT_Utility.copyAmount(1L, new Object[]{aStack}), gregtech.api.util.GT_ModHandler.RecipeBits.DO_NOT_CHECK_FOR_COLLISIONS | gregtech.api.util.GT_ModHandler.RecipeBits.BUFFERED, new Object[]{"I", "B", "h", 'I', aPlateStack, 'B', aPlateStack});
                        GT_ModHandler.addShapelessCraftingRecipe(GT_Utility.copyAmount(1L, new Object[]{aStack}), new Object[]{gregtech.api.enums.ToolDictNames.craftingToolForgeHammer, aPlateStack, aPlateStack});
                    }
                    GT_Values.RA.addBenderRecipe(GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 2L), GT_Utility.copyAmount(1L, new Object[]{aStack}), (int) Math.max(aMaterialMass * 2L, 1L), 96);
                } else {
                        GT_Values.RA.addAssemblerRecipe(GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 2L), gregtech.api.enums.ItemList.Circuit_Integrated.getWithDamage(0L, 2L, new Object[0]), Materials.Glue.getFluid(10L), GT_Utility.copyAmount(1L, new Object[]{aStack}), 64, 8);
                    }
                break;
            case plateTriple:
                GT_ModHandler.removeRecipeByOutput(aStack);
                GregTech_API.registerCover(aStack, new gregtech.api.objects.GT_RenderedTexture(aMaterial.mIconSet.mTextures[73], aMaterial.mRGBa, false), null);
                if (!aNoSmashing) {
                    GT_Values.RA.addBenderRecipe(GT_Utility.copyAmount(3L, new Object[]{aStack}), GT_OreDictUnificator.get(OrePrefixes.plateDense, aMaterial, 1L), (int) Math.max(aMaterialMass * 3L, 1L), 96);
                    if (GregTech_API.sRecipeFile.get(gregtech.api.enums.ConfigCategories.Tools.hammertripleplate, OrePrefixes.plate.get(aMaterial).toString(), true)) {
                        Object aPlateStack = OrePrefixes.plate.get(aMaterial);
                        GT_ModHandler.addCraftingRecipe(GT_Utility.copyAmount(1L, new Object[]{aStack}), gregtech.api.util.GT_ModHandler.RecipeBits.DO_NOT_CHECK_FOR_COLLISIONS | gregtech.api.util.GT_ModHandler.RecipeBits.BUFFERED, new Object[]{"I", "B", "h", 'I', OrePrefixes.plateDouble.get(aMaterial), 'B', aPlateStack});
                        GT_ModHandler.addShapelessCraftingRecipe(GT_Utility.copyAmount(1L, new Object[]{aStack}), new Object[]{gregtech.api.enums.ToolDictNames.craftingToolForgeHammer, aPlateStack, aPlateStack, aPlateStack});
                    } 
                    GT_Values.RA.addBenderRecipe(GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 3L), GT_Utility.copyAmount(1L, new Object[]{aStack}), (int) Math.max(aMaterialMass * 3L, 1L), 96);
                }else {
                        GT_Values.RA.addAssemblerRecipe(GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 3L), gregtech.api.enums.ItemList.Circuit_Integrated.getWithDamage(0L, 3L, new Object[0]), Materials.Glue.getFluid(20L), GT_Utility.copyAmount(1L, new Object[]{aStack}), 96, 8);
                    }
                break;
            case plateQuadruple:
                GT_ModHandler.removeRecipeByOutput(aStack);
                GregTech_API.registerCover(aStack, new gregtech.api.objects.GT_RenderedTexture(aMaterial.mIconSet.mTextures[74], aMaterial.mRGBa, false), null);
                if (!aNoWorking)
                    GT_Values.RA.addCNCRecipe(GT_Utility.copyAmount(1L, new Object[]{aStack}), GT_OreDictUnificator.get(OrePrefixes.gearGt, aMaterial, 1L), (int) Math.max(aMaterialMass * 2L, 1L), 32);
                if (!aNoSmashing) {
                    if (GregTech_API.sRecipeFile.get(gregtech.api.enums.ConfigCategories.Tools.hammerquadrupleplate, OrePrefixes.plate.get(aMaterial).toString(), true)) {
                        Object aPlateStack = OrePrefixes.plate.get(aMaterial);
                        GT_ModHandler.addCraftingRecipe(GT_Utility.copyAmount(1L, new Object[]{aStack}), gregtech.api.util.GT_ModHandler.RecipeBits.DO_NOT_CHECK_FOR_COLLISIONS | gregtech.api.util.GT_ModHandler.RecipeBits.BUFFERED, new Object[]{"I", "B", "h", 'I', OrePrefixes.plateTriple.get(aMaterial), 'B', aPlateStack});
                        GT_ModHandler.addShapelessCraftingRecipe(GT_Utility.copyAmount(1L, new Object[]{aStack}), new Object[]{gregtech.api.enums.ToolDictNames.craftingToolForgeHammer, aPlateStack, aPlateStack, aPlateStack, aPlateStack});
                    }
                    GT_Values.RA.addBenderRecipe(GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 4L), GT_Utility.copyAmount(1L, new Object[]{aStack}), (int) Math.max(aMaterialMass * 4L, 1L), 96);
                } else {
                        GT_Values.RA.addAssemblerRecipe(GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 4L), gregtech.api.enums.ItemList.Circuit_Integrated.getWithDamage(0L, 4L, new Object[0]), Materials.Glue.getFluid(30L), GT_Utility.copyAmount(1L, new Object[]{aStack}), 128, 8);
                    }
                break;
            case plateQuintuple:
                GT_ModHandler.removeRecipeByOutput(aStack);
                GregTech_API.registerCover(aStack, new gregtech.api.objects.GT_RenderedTexture(aMaterial.mIconSet.mTextures[75], aMaterial.mRGBa, false), null);
                if (!aNoSmashing) {
                    if (GregTech_API.sRecipeFile.get(gregtech.api.enums.ConfigCategories.Tools.hammerquintupleplate, OrePrefixes.plate.get(aMaterial).toString(), true)) {
                        Object aPlateStack = OrePrefixes.plate.get(aMaterial);
                        GT_ModHandler.addCraftingRecipe(GT_Utility.copyAmount(1L, new Object[]{aStack}), GT_ModHandler.RecipeBits.DO_NOT_CHECK_FOR_COLLISIONS | GT_ModHandler.RecipeBits.BUFFERED, new Object[]{"I", "B", "h", 'I', OrePrefixes.plateQuadruple.get(aMaterial), 'B', aPlateStack});
                        GT_ModHandler.addShapelessCraftingRecipe(GT_Utility.copyAmount(1L, new Object[]{aStack}), new Object[]{ToolDictNames.craftingToolForgeHammer, aPlateStack, aPlateStack, aPlateStack, aPlateStack, aPlateStack});
                    }
                    GT_Values.RA.addBenderRecipe(GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 5L), GT_Utility.copyAmount(1L, new Object[]{aStack}), (int) Math.max(aMaterialMass * 5L, 1L), 96);
                } else {
                        gregtech.api.enums.GT_Values.RA.addAssemblerRecipe(gregtech.api.util.GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 5L), ItemList.Circuit_Integrated.getWithDamage(0L, 5L, new Object[0]), Materials.Glue.getFluid(40L), GT_Utility.copyAmount(1L, new Object[]{aStack}), 160, 8);
                    }
                break;
            case plateDense:
                GT_ModHandler.removeRecipeByOutput(aStack);
                GregTech_API.registerCover(aStack, new GT_RenderedTexture(aMaterial.mIconSet.mTextures[76], aMaterial.mRGBa, false), null);
                if (!aNoSmashing) {
                    GT_Values.RA.addBenderRecipe(GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 9L), GT_Utility.copyAmount(1L, new Object[]{aStack}), (int) Math.max(aMaterialMass * 9L, 1L), 96);
                }
                break;
            case plateAlloy:
                switch (aOreDictName) {
                    case "plateAlloyCarbon":
                        GT_Values.RA.addAssemblerRecipe(GT_ModHandler.getIC2Item("generator", 1L), GT_Utility.copyAmount(4L, new Object[]{aStack}), GT_ModHandler.getIC2Item("windMill", 1L), 6400, 8);
                    case "plateAlloyAdvanced":
                        GT_ModHandler.addAlloySmelterRecipe(GT_Utility.copyAmount(1L, new Object[]{aStack}), new ItemStack(Blocks.glass, 3, 32767), GT_ModHandler.getIC2Item("reinforcedGlass", 4L), 400, 4, false);
                        GT_ModHandler.addAlloySmelterRecipe(GT_Utility.copyAmount(1L, new Object[]{aStack}), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Glass, 3L), GT_ModHandler.getIC2Item("reinforcedGlass", 4L), 400, 4, false);
                    case "plateAlloyIridium":
                        GT_ModHandler.removeRecipeByOutput(aStack);
                }
                break;
        }
    }
}
