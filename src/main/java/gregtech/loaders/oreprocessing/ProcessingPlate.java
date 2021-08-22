package gregtech.loaders.oreprocessing;

import gregtech.api.GregTech_API;
import gregtech.api.enums.ConfigCategories;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.SubTag;
import gregtech.api.enums.TextureSet;
import gregtech.api.enums.ToolDictNames;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_RecipeRegistrator;
import gregtech.api.util.GT_Utility;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import static gregtech.api.enums.ConfigCategories.Recipes.harderrecipes;
import static gregtech.api.enums.GT_Values.L;
import static gregtech.api.enums.GT_Values.NI;
import static gregtech.api.enums.GT_Values.RA;
import static gregtech.api.enums.GT_Values.W;
import static gregtech.api.util.GT_ModHandler.RecipeBits.BUFFERED;
import static gregtech.api.util.GT_ModHandler.RecipeBits.DO_NOT_CHECK_FOR_COLLISIONS;
import static gregtech.common.GT_Proxy.tBits;

public class ProcessingPlate implements gregtech.api.interfaces.IOreRecipeRegistrator {
    public ProcessingPlate() {
        OrePrefixes.plate.add(this);
        OrePrefixes.plateDouble.add(this);
        OrePrefixes.plateTriple.add(this);
        OrePrefixes.plateQuadruple.add(this);
        OrePrefixes.plateQuintuple.add(this);
        OrePrefixes.plateDense.add(this);
        OrePrefixes.plateAlloy.add(this);
        OrePrefixes.itemCasing.add(this);
    }

    /**
     * Register processes for the {@link ItemStack} with Ore Dictionary Name Prefix "plate"
     *
     * @param aPrefix      always != null, the {@link OrePrefixes} of the {@link ItemStack}
     * @param aMaterial    always != null, and can be == _NULL if the Prefix is Self Referencing or not Material based!
     *                     the {@link Materials} of the {@link ItemStack}
     * @param aOreDictName the Ore Dictionary Name {@link String} of the {@link ItemStack}
     * @param aModName     the ModID {@link String} of the mod providing this {@link ItemStack}
     * @param aStack       always != null, the {@link ItemStack} to register
     */
    @Override
    public void registerOre(OrePrefixes aPrefix,
                            Materials aMaterial,
                            String aOreDictName,
                            String aModName,
                            ItemStack aStack) {

        final boolean aNoSmashing = aMaterial.contains(SubTag.NO_SMASHING);
        final boolean aNoWorking = aMaterial.contains(SubTag.NO_WORKING);
        final long aMaterialMass = aMaterial.getMass();

        switch (aPrefix) {
            case plate:
                registerPlate(aMaterial, aStack, aNoSmashing);
                break;
            case plateDouble:
                registerPlateDouble(aMaterial, aStack, aNoSmashing, aMaterialMass);
                break;
            case plateTriple:
                registerPlateTriple(aMaterial, aStack, aNoSmashing, aMaterialMass);
                break;
            case plateQuadruple:
                registerPlateQuadruple(aMaterial, aStack, aNoSmashing, aMaterialMass, aNoWorking);
                break;
            case plateQuintuple:
                registerPlateQuintuple(aMaterial, aStack, aNoSmashing, aMaterialMass);
                break;
            case plateDense:
                registerPlateDense(aMaterial, aStack, aNoSmashing, aMaterialMass);
                break;
            case itemCasing:
                registerItemCasing(aPrefix, aMaterial, aStack, aNoSmashing);
                break;
            case plateAlloy:
                registerPlateAlloy(aOreDictName, aStack);
                break;
            default:
                break;
        }
    }

    private void registerPlate(final Materials aMaterial, final ItemStack aStack, final boolean aNoSmashing) {

        registerCover(aMaterial, aStack);

        GT_ModHandler.removeRecipeByOutputDelayed(aStack);
        GT_ModHandler.removeRecipeDelayed(aStack);

        GT_Utility.removeSimpleIC2MachineRecipe(
                GT_Utility.copyAmount(9L, aStack),
                GT_ModHandler.getCompressorRecipeList(),
                GT_OreDictUnificator.get(OrePrefixes.plateDense, aMaterial, 1L));

        if (aMaterial.mFuelPower > 0) {

            RA.addFuel(
                    GT_Utility.copyAmount(1L, aStack),
                    NI,
                    aMaterial.mFuelPower,
                    aMaterial.mFuelType);

        }

        if (aMaterial.mStandardMoltenFluid != null &&
                !(aMaterial == Materials.AnnealedCopper || aMaterial == Materials.WroughtIron)) {

            RA.addFluidSolidifierRecipe(
                    ItemList.Shape_Mold_Plate.get(0L),
                    aMaterial.getMolten(L),
                    aMaterial.getPlates(1),
                    32, 8);

        }

        GT_ModHandler.addCraftingRecipe(
                GT_OreDictUnificator.get(OrePrefixes.foil, aMaterial, 2L),
                tBits, // DO_NOT_CHECK_FOR_COLLISIONS|BUFFERED|ONLY_ADD_IF_RESULT_IS_NOT_NULL|NOT_REMOVABLE
                new Object[]{
                        "hX",
                        'X', OrePrefixes.plate.get(aMaterial)});

        if (aMaterial == Materials.Paper) {

            GT_ModHandler.addCraftingRecipe(
                    GT_Utility.copyAmount(
                            GregTech_API.sRecipeFile.get(harderrecipes, aStack, true) ? 2L : 3L,
                            aStack),
                    BUFFERED,
                    new Object[]{
                            "XXX",
                            'X', new ItemStack(Items.reeds, 1, W)});
        }

        if (aMaterial.mUnificatable && aMaterial.mMaterialInto == aMaterial) {

            if (!aNoSmashing &&
                    GregTech_API.sRecipeFile.get(ConfigCategories.Tools.hammerplating, aMaterial.toString(), true)) {

                GT_ModHandler.addCraftingRecipe(
                        aMaterial.getPlates(1),
                        tBits, // DO_NOT_CHECK_FOR_COLLISIONS|BUFFERED|ONLY_ADD_IF_RESULT_IS_NOT_NULL|NOT_REMOVABLE
                        new Object[]{
                                "h", // craftingToolHardHammer
                                "X",
                                "X",
                                'X', OrePrefixes.ingot.get(aMaterial)});

                // Only added if IC2 Forge Hammer is enabled in Recipes.cfg: B:ic2forgehammer_true=false
                GT_ModHandler.addCraftingRecipe(
                        aMaterial.getPlates(1),
                        tBits, // DO_NOT_CHECK_FOR_COLLISIONS|BUFFERED|ONLY_ADD_IF_RESULT_IS_NOT_NULL|NOT_REMOVABLE
                        new Object[]{
                                "H", // craftingToolForgeHammer
                                "X",
                                'H', ToolDictNames.craftingToolForgeHammer,
                                'X', OrePrefixes.ingot.get(aMaterial)});

                GT_ModHandler.addCraftingRecipe(
                        aMaterial.getPlates(1),
                        tBits, // DO_NOT_CHECK_FOR_COLLISIONS|BUFFERED|ONLY_ADD_IF_RESULT_IS_NOT_NULL|NOT_REMOVABLE
                        new Object[]{
                                "h", // craftingToolHardHammer
                                "X",
                                'X', OrePrefixes.gem.get(aMaterial)});

                // Only added if IC2 Forge Hammer is enabled in Recipes.cfg: B:ic2forgehammer_true=false
                GT_ModHandler.addCraftingRecipe(
                        aMaterial.getPlates(1),
                        tBits, // DO_NOT_CHECK_FOR_COLLISIONS|BUFFERED|ONLY_ADD_IF_RESULT_IS_NOT_NULL|NOT_REMOVABLE
                        new Object[]{
                                "H", // craftingToolForgeHammer
                                "X",
                                'H', ToolDictNames.craftingToolForgeHammer,
                                'X', OrePrefixes.gem.get(aMaterial)});

            }

            if ((aMaterial.contains(SubTag.MORTAR_GRINDABLE)) &&
                    (GregTech_API.sRecipeFile.get(ConfigCategories.Tools.mortar, aMaterial.mName, true))) {

                GT_ModHandler.addCraftingRecipe(
                        aMaterial.getDust(1),
                        tBits, // DO_NOT_CHECK_FOR_COLLISIONS|BUFFERED|ONLY_ADD_IF_RESULT_IS_NOT_NULL|NOT_REMOVABLE
                        new Object[]{
                                "X",
                                "m",
                                'X', OrePrefixes.plate.get(aMaterial)});

            }
        }
    }

    private void registerPlateDouble(final Materials aMaterial,
                                     final ItemStack aStack,
                                     final boolean aNoSmashing,
                                     final long aMaterialMass) {

        registerCover(aMaterial, aStack);

        GT_ModHandler.removeRecipeByOutputDelayed(aStack);

        if (!aNoSmashing) {

            RA.addBenderRecipe(
                    GT_Utility.copyAmount(2L, aStack),
                    GT_OreDictUnificator.get(OrePrefixes.plateQuadruple, aMaterial, 1L),
                    (int) Math.max(aMaterialMass * 2L, 1L),
                    96);

            if (GregTech_API.sRecipeFile.get(
                    gregtech.api.enums.ConfigCategories.Tools.hammerdoubleplate,
                    OrePrefixes.plate.get(aMaterial).toString(), true)) {

                Object aPlateStack = OrePrefixes.plate.get(aMaterial);

                GT_ModHandler.addCraftingRecipe(
                        GT_Utility.copyAmount(1L, aStack),
                        DO_NOT_CHECK_FOR_COLLISIONS | BUFFERED,
                        new Object[]{
                                "I",
                                "B",
                                "h", // craftingToolHardHammer
                                'I', aPlateStack,
                                'B', aPlateStack});

                // Only added if IC2 Forge Hammer is enabled in Recipes.cfg: B:ic2forgehammer_true=false
                GT_ModHandler.addShapelessCraftingRecipe(
                        GT_Utility.copyAmount(1L, aStack),
                        DO_NOT_CHECK_FOR_COLLISIONS | BUFFERED,
                        new Object[]{
                                gregtech.api.enums.ToolDictNames.craftingToolForgeHammer,
                                aPlateStack,
                                aPlateStack});

            }

            RA.addBenderRecipe(
                    GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 2L),
                    GT_Utility.copyAmount(1L, aStack),
                    (int) Math.max(aMaterialMass * 2L, 1L),
                    96);

        } else {

            RA.addAssemblerRecipe(
                    GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 2L),
                    gregtech.api.enums.ItemList.Circuit_Integrated.getWithDamage(0L, 2L),
                    Materials.Glue.getFluid(10L),
                    GT_Utility.copyAmount(1L, aStack),
                    64, 8);

        }
    }

    private void registerPlateTriple(final Materials aMaterial,
                                     final ItemStack aStack,
                                     final boolean aNoSmashing,
                                     final long aMaterialMass) {

        registerCover(aMaterial, aStack);

        GT_ModHandler.removeRecipeByOutputDelayed(aStack);

        if (!aNoSmashing) {

            RA.addBenderRecipe(
                    GT_Utility.copyAmount(3L, aStack),
                    GT_OreDictUnificator.get(OrePrefixes.plateDense, aMaterial, 1L),
                    (int) Math.max(aMaterialMass * 3L, 1L),
                    96);

            if (GregTech_API.sRecipeFile.get(
                    gregtech.api.enums.ConfigCategories.Tools.hammertripleplate,
                    OrePrefixes.plate.get(aMaterial).toString(), true)) {

                Object aPlateStack = OrePrefixes.plate.get(aMaterial);

                GT_ModHandler.addCraftingRecipe(
                        GT_Utility.copyAmount(1L, aStack),
                        DO_NOT_CHECK_FOR_COLLISIONS | BUFFERED,
                        new Object[]{
                                "I",
                                "B",
                                "h", // craftingToolHardHammer
                                'I', OrePrefixes.plateDouble.get(aMaterial),
                                'B', aPlateStack});

                GT_ModHandler.addShapelessCraftingRecipe(
                        GT_Utility.copyAmount(1L, aStack),
                        DO_NOT_CHECK_FOR_COLLISIONS | BUFFERED,
                        new Object[]{
                                gregtech.api.enums.ToolDictNames.craftingToolForgeHammer,
                                aPlateStack, aPlateStack, aPlateStack});

            }

            RA.addBenderRecipe(
                    GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 3L),
                    GT_Utility.copyAmount(1L, aStack),
                    (int) Math.max(aMaterialMass * 3L, 1L),
                    96);

        } else {

            RA.addAssemblerRecipe(
                    GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 3L),
                    gregtech.api.enums.ItemList.Circuit_Integrated.getWithDamage(0L, 3L),
                    Materials.Glue.getFluid(20L),
                    GT_Utility.copyAmount(1L, aStack),
                    96, 8);

        }

        RA.addImplosionRecipe(
                GT_Utility.copyAmount(1L, aStack),
                2,
                GT_OreDictUnificator.get(OrePrefixes.compressed, aMaterial, 1L),
                GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.DarkAsh, 1L));

    }

    private void registerPlateQuadruple(final Materials aMaterial,
                                        final ItemStack aStack,
                                        final boolean aNoSmashing,
                                        final long aMaterialMass,
                                        final boolean aNoWorking) {

        registerCover(aMaterial, aStack);

        GT_ModHandler.removeRecipeByOutputDelayed(aStack);

        if (!aNoWorking)

            RA.addCNCRecipe(
                    GT_Utility.copyAmount(1L, aStack),
                    GT_OreDictUnificator.get(OrePrefixes.gearGt, aMaterial, 1L),
                    (int) Math.max(aMaterialMass * 2L, 1L),
                    30);

        if (!aNoSmashing) {

            if (GregTech_API.sRecipeFile.get(
                    gregtech.api.enums.ConfigCategories.Tools.hammerquadrupleplate,
                    OrePrefixes.plate.get(aMaterial).toString(), true)) {

                Object aPlateStack = OrePrefixes.plate.get(aMaterial);

                GT_ModHandler.addCraftingRecipe(
                        GT_Utility.copyAmount(1L, aStack),
                        DO_NOT_CHECK_FOR_COLLISIONS | BUFFERED,
                        new Object[]{
                                "I",
                                "B",
                                "h", // craftingToolHardHammer
                                'I', OrePrefixes.plateTriple.get(aMaterial),
                                'B', aPlateStack});

                GT_ModHandler.addShapelessCraftingRecipe(
                        GT_Utility.copyAmount(1L, aStack),
                        DO_NOT_CHECK_FOR_COLLISIONS | BUFFERED,
                        new Object[]{gregtech.api.enums.ToolDictNames.craftingToolForgeHammer,
                                aPlateStack, aPlateStack, aPlateStack, aPlateStack});

            }

            RA.addBenderRecipe(
                    GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 4L),
                    GT_Utility.copyAmount(1L, aStack),
                    (int) Math.max(aMaterialMass * 4L, 1L),
                    96);

        } else {

            RA.addAssemblerRecipe(
                    GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 4L),
                    gregtech.api.enums.ItemList.Circuit_Integrated.getWithDamage(0L, 4L),
                    Materials.Glue.getFluid(30L), GT_Utility.copyAmount(1L, aStack),
                    128, 8);

        }
    }

    private void registerPlateQuintuple(final Materials aMaterial,
                                        final ItemStack aStack,
                                        final boolean aNoSmashing,
                                        final long aMaterialMass) {

        registerCover(aMaterial, aStack);

        GT_ModHandler.removeRecipeByOutputDelayed(aStack);

        if (!aNoSmashing) {

            if (GregTech_API.sRecipeFile.get(
                    gregtech.api.enums.ConfigCategories.Tools.hammerquintupleplate,
                    OrePrefixes.plate.get(aMaterial).toString(), true)) {

                Object aPlateStack = OrePrefixes.plate.get(aMaterial);

                GT_ModHandler.addCraftingRecipe(
                        GT_Utility.copyAmount(1L, aStack),
                        DO_NOT_CHECK_FOR_COLLISIONS | BUFFERED,
                        new Object[]{
                                "I",
                                "B",
                                "h", // craftingToolHardHammer
                                'I', OrePrefixes.plateQuadruple.get(aMaterial),
                                'B', aPlateStack});

                GT_ModHandler.addShapelessCraftingRecipe(
                        GT_Utility.copyAmount(1L, aStack),
                        DO_NOT_CHECK_FOR_COLLISIONS | BUFFERED,
                        new Object[]{
                                ToolDictNames.craftingToolForgeHammer,
                                aPlateStack, aPlateStack, aPlateStack, aPlateStack, aPlateStack});

            }

            RA.addBenderRecipe(
                    GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 5L),
                    GT_Utility.copyAmount(1L, aStack),
                    (int) Math.max(aMaterialMass * 5L, 1L),
                    96);

        } else {

            RA.addAssemblerRecipe(
                    gregtech.api.util.GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 5L),
                    ItemList.Circuit_Integrated.getWithDamage(0L, 5L),
                    Materials.Glue.getFluid(40L),
                    GT_Utility.copyAmount(1L, aStack),
                    160, 8);

        }
    }

    private void registerPlateDense(final Materials aMaterial,
                                    final ItemStack aStack,
                                    final boolean aNoSmashing,
                                    final long aMaterialMass) {

        registerCover(aMaterial, aStack);

        GT_ModHandler.removeRecipeByOutputDelayed(aStack);

        if (!aNoSmashing) {

            RA.addBenderRecipe(
                    GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 9L),
                    GT_Utility.copyAmount(1L, aStack), (int) Math.max(aMaterialMass * 9L, 1L),
                    96);

        }

    }

    private void registerItemCasing(final OrePrefixes aPrefix,
                                    final Materials aMaterial,
                                    final ItemStack aStack,
                                    final boolean aNoSmashing) {

        GT_ModHandler.removeRecipeByOutputDelayed(aStack);

        if (aMaterial.mStandardMoltenFluid != null) {

            RA.addFluidSolidifierRecipe(
                    ItemList.Shape_Mold_Casing.get(0L),
                    aMaterial.getMolten(L / 2),
                    GT_OreDictUnificator.get(OrePrefixes.itemCasing, aMaterial, 1L),
                    16, 8);

        }

        if (aMaterial.mUnificatable &&
                aMaterial.mMaterialInto == aMaterial &&
                !aNoSmashing &&
                GregTech_API.sRecipeFile.get(ConfigCategories.Tools.hammerplating, aMaterial.toString(), true)) {

            GT_ModHandler.addCraftingRecipe(
                    GT_OreDictUnificator.get(OrePrefixes.itemCasing, aMaterial, 1L),
                    tBits, // DO_NOT_CHECK_FOR_COLLISIONS|BUFFERED|ONLY_ADD_IF_RESULT_IS_NOT_NULL|NOT_REMOVABLE
                    new Object[]{
                            "h X",
                            'X', OrePrefixes.plate.get(aMaterial)});

            // Only added if IC2 Forge Hammer is enabled in Recipes.cfg: B:ic2forgehammer_true=false
            GT_ModHandler.addCraftingRecipe(
                    GT_OreDictUnificator.get(OrePrefixes.itemCasing, aMaterial, 1L),
                    tBits, // DO_NOT_CHECK_FOR_COLLISIONS|BUFFERED|ONLY_ADD_IF_RESULT_IS_NOT_NULL|NOT_REMOVABLE
                    new Object[]{
                            "H X",
                            'H', ToolDictNames.craftingToolForgeHammer,
                            'X', OrePrefixes.plate.get(aMaterial)});

        }

        RA.addAlloySmelterRecipe(
                GT_OreDictUnificator.get(OrePrefixes.ingot, aMaterial, 2L),
                ItemList.Shape_Mold_Casing.get(0L), GT_Utility.copyAmount(3L, aStack), 128, 15);

        RA.addCutterRecipe(
                GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 1L),
                GT_OreDictUnificator.get(OrePrefixes.itemCasing, aMaterial, 2L),
                NI,
                (int) Math.max(aMaterial.getMass(), 1L),
                16);

        RA.addExtruderRecipe(
                GT_OreDictUnificator.get(OrePrefixes.ingot, aMaterial, 1L),
                ItemList.Shape_Extruder_Casing.get(0L),
                GT_OreDictUnificator.get(OrePrefixes.itemCasing, aMaterial, 2L),
                (int) Math.max(aMaterial.getMass(), 1L),
                45);

        GT_RecipeRegistrator.registerReverseFluidSmelting(aStack, aMaterial, aPrefix.mMaterialAmount, null);

    }

    private void registerPlateAlloy(final String aOreDictName,
                                    final ItemStack aStack) {

        switch (aOreDictName) {

            case "plateAlloyCarbon":

                RA.addAssemblerRecipe(
                        GT_ModHandler.getIC2Item("generator", 1L),
                        GT_Utility.copyAmount(4L, aStack),
                        GT_ModHandler.getIC2Item("windMill", 1L),
                        6400, 8);

                GT_ModHandler.addAlloySmelterRecipe(
                        GT_Utility.copyAmount(1L, aStack),
                        new ItemStack(Blocks.glass, 3, W),
                        GT_ModHandler.getIC2Item("reinforcedGlass", 4L),
                        400, 4, false);

                GT_ModHandler.addAlloySmelterRecipe(
                        GT_Utility.copyAmount(1L, aStack),
                        Materials.Glass.getDust(3),
                        GT_ModHandler.getIC2Item("reinforcedGlass", 4L),
                        400, 4, false);

                break;

            case "plateAlloyAdvanced":

                GT_ModHandler.addAlloySmelterRecipe(
                        GT_Utility.copyAmount(1L, aStack),
                        new ItemStack(Blocks.glass, 3, W),
                        GT_ModHandler.getIC2Item("reinforcedGlass", 4L),
                        400, 4, false);

                GT_ModHandler.addAlloySmelterRecipe(
                        GT_Utility.copyAmount(1L, aStack),
                        Materials.Glass.getDust(3),
                        GT_ModHandler.getIC2Item("reinforcedGlass", 4L),
                        400, 4, false);

                break;

            case "plateAlloyIridium":

                // Remove IC2 Shaped recipe for Iridium Reinforced Plate
                GT_ModHandler.removeRecipeByOutputDelayed(aStack);

                break;

            default:
                break;

        }

    }

    private void registerCover(final Materials aMaterial, final ItemStack aStack) {

        // Get ItemStack of Block matching Materials
        ItemStack tStack = NI;
        // Try different prefixes to use same smooth stones as older GT5U
        for (OrePrefixes orePrefix : new OrePrefixes[]{
                OrePrefixes.block, OrePrefixes.block_, OrePrefixes.stoneSmooth, OrePrefixes.stone}) {
            if ((tStack = GT_OreDictUnificator.get(orePrefix, aMaterial, 1)) != NI) break;
        }

        // Register the cover
        GregTech_API.registerCover(
                aStack,
                // If there is an ItemStack of Block for Materials
                tStack == NI ?
                        // Use Materials mRGBa dyed blocs/materialicons/MATERIALSET/block1 icons
                        TextureFactory.builder()
                                .addIcon(aMaterial.mIconSet.mTextures[TextureSet.INDEX_block1])
                                .setRGBA(aMaterial.mRGBa)
                                .stdOrient().build() :
                        // or copy Block texture
                        TextureFactory.of(Block.getBlockFromItem(tStack.getItem()), tStack.getItemDamage()),
                null);

    }

}
