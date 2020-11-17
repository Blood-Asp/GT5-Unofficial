package gregtech.loaders.postload;

import codechicken.nei.api.API;
import com.google.gson.stream.JsonReader;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.*;
import gregtech.api.objects.MaterialStack;
import gregtech.api.recipes.GT_RecipeListJsonReader;
import gregtech.api.util.*;
import gregtech.api.util.GT_Recipe.GT_Recipe_Map;
import gregtech.common.GT_DummyWorld;
import gregtech.common.items.GT_MetaGenerated_Item_03;
import ic2.api.recipe.ILiquidHeatExchangerManager.HeatExchangeProperty;
import ic2.api.recipe.Recipes;
import ic2.core.Ic2Items;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.items.RailcraftToolItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public class GT_MachineRecipeLoader implements Runnable {
    private final MaterialStack[][] mAlloySmelterList = {
            {new MaterialStack(Materials.Tetrahedrite, 3L), new MaterialStack(Materials.Tin, 1L), new MaterialStack(Materials.Bronze, 3L)},
            {new MaterialStack(Materials.Tetrahedrite, 3L), new MaterialStack(Materials.Zinc, 1L), new MaterialStack(Materials.Brass, 3L)},
            {new MaterialStack(Materials.Copper, 3L), new MaterialStack(Materials.Tin, 1L), new MaterialStack(Materials.Bronze, 4L)},
            {new MaterialStack(Materials.Copper, 3L), new MaterialStack(Materials.Zinc, 1L), new MaterialStack(Materials.Brass, 4L)},
            {new MaterialStack(Materials.Copper, 1L), new MaterialStack(Materials.Nickel, 1L), new MaterialStack(Materials.Cupronickel, 2L)},
            {new MaterialStack(Materials.Copper, 1L), new MaterialStack(Materials.Redstone, 4L), new MaterialStack(Materials.RedAlloy, 1L)},
            {new MaterialStack(Materials.AnnealedCopper, 3L), new MaterialStack(Materials.Tin, 1L), new MaterialStack(Materials.Bronze, 4L)},
            {new MaterialStack(Materials.AnnealedCopper, 3L), new MaterialStack(Materials.Zinc, 1L), new MaterialStack(Materials.Brass, 4L)},
            {new MaterialStack(Materials.AnnealedCopper, 1L), new MaterialStack(Materials.Nickel, 1L), new MaterialStack(Materials.Cupronickel, 2L)},
            {new MaterialStack(Materials.AnnealedCopper, 1L), new MaterialStack(Materials.Redstone, 4L), new MaterialStack(Materials.RedAlloy, 1L)},
            {new MaterialStack(Materials.Iron, 1L), new MaterialStack(Materials.Tin, 1L), new MaterialStack(Materials.TinAlloy, 2L)},
            {new MaterialStack(Materials.WroughtIron, 1L), new MaterialStack(Materials.Tin, 1L), new MaterialStack(Materials.TinAlloy, 2L)},
            {new MaterialStack(Materials.Iron, 2L), new MaterialStack(Materials.Nickel, 1L), new MaterialStack(Materials.Invar, 3L)},
            {new MaterialStack(Materials.WroughtIron, 2L), new MaterialStack(Materials.Nickel, 1L), new MaterialStack(Materials.Invar, 3L)},
            {new MaterialStack(Materials.Tin, 9L), new MaterialStack(Materials.Antimony, 1L), new MaterialStack(Materials.SolderingAlloy, 10L)},
            {new MaterialStack(Materials.Lead, 4L), new MaterialStack(Materials.Antimony, 1L), new MaterialStack(Materials.BatteryAlloy, 5L)},
            {new MaterialStack(Materials.Gold, 1L), new MaterialStack(Materials.Silver, 1L), new MaterialStack(Materials.Electrum, 2L)},
            {new MaterialStack(Materials.Magnesium, 1L), new MaterialStack(Materials.Aluminium, 2L), new MaterialStack(Materials.Magnalium, 3L)},
            {new MaterialStack(Materials.Silver, 1L), new MaterialStack(Materials.Nikolite, 4L), new MaterialStack(Materials.BlueAlloy, 1L)},
            {new MaterialStack(Materials.Boron, 1L), new MaterialStack(Materials.Glass, 7L), new MaterialStack(Materials.BorosilicateGlass, 8L)}};
    private final static String aTextAE = "appliedenergistics2"; private final static String aTextAEMM = "item.ItemMultiMaterial"; private final static String aTextForestry = "Forestry";
    private final static String aTextEBXL = "ExtrabiomesXL"; private final static String aTextTCGTPage = "gt.research.page.1.";
    private final static Boolean isNEILoaded = Loader.isModLoaded("NotEnoughItems");

    // found in an old Minecraft Forge forum post
    public static File getMcDir() {
        if (MinecraftServer.getServer() != null && MinecraftServer.getServer().isDedicatedServer()) {
            return new File(".");
        }
        return Minecraft.getMinecraft().mcDataDir;
    }
    
    public void run() {
        GT_Log.out.println("GT_Mod: Adding non-OreDict Machine Recipes.");
        try {
            GT_Utility.removeSimpleIC2MachineRecipe(GT_Values.NI, ic2.api.recipe.Recipes.metalformerExtruding.getRecipes(), ItemList.Cell_Empty.get(3L, new Object[0]));
            GT_Utility.removeSimpleIC2MachineRecipe(ItemList.IC2_Energium_Dust.get(1L, new Object[0]), ic2.api.recipe.Recipes.compressor.getRecipes(), GT_Values.NI);
            GT_Utility.removeSimpleIC2MachineRecipe(new ItemStack(Items.gunpowder), ic2.api.recipe.Recipes.extractor.getRecipes(), GT_Values.NI);
            GT_Utility.removeSimpleIC2MachineRecipe(new ItemStack(Blocks.wool, 1, 32767), ic2.api.recipe.Recipes.extractor.getRecipes(), GT_Values.NI);
            GT_Utility.removeSimpleIC2MachineRecipe(new ItemStack(Blocks.gravel), ic2.api.recipe.Recipes.oreWashing.getRecipes(), GT_Values.NI);
        } catch (Throwable e) {
        }
        GT_Utility.removeIC2BottleRecipe(GT_ModHandler.getIC2Item("fuelRod", 1), GT_ModHandler.getIC2Item("UranFuel", 1), ic2.api.recipe.Recipes.cannerBottle.getRecipes(), GT_ModHandler.getIC2Item("reactorUraniumSimple", 1, 1));
        GT_Utility.removeIC2BottleRecipe(GT_ModHandler.getIC2Item("fuelRod", 1), GT_ModHandler.getIC2Item("MOXFuel", 1), ic2.api.recipe.Recipes.cannerBottle.getRecipes(), GT_ModHandler.getIC2Item("reactorMOXSimple", 1, 1));

        Map<GT_Recipe_Map, String> tRecipeFileMap = new HashMap<>(30);
        tRecipeFileMap.put(GT_Recipe_Map.sAlloySmelterRecipes, "alloy_smelter.json");
        tRecipeFileMap.put(GT_Recipe_Map.sArcFurnaceRecipes, "arc_furnace.json");
        tRecipeFileMap.put(GT_Recipe_Map.sAssemblerRecipes, "assembler.json");
        tRecipeFileMap.put(GT_Recipe_Map.sAutoclaveRecipes, "autoclave.json");
        tRecipeFileMap.put(GT_Recipe_Map.sBenderRecipes, "bender.json");
        tRecipeFileMap.put(GT_Recipe_Map.sBlastRecipes, "blast_furnace.json");
        tRecipeFileMap.put(GT_Recipe_Map.sBrewingRecipes, "brewery.json");
        tRecipeFileMap.put(GT_Recipe_Map.sCentrifugeRecipes, "centrifuge.json");
        tRecipeFileMap.put(GT_Recipe_Map.sChemicalBathRecipes, "chemical_bath.json");
        tRecipeFileMap.put(GT_Recipe_Map.sChemicalRecipes, "chemical_reactor.json");
        tRecipeFileMap.put(GT_Recipe_Map.sMultiblockChemicalRecipes, "large_chemical_reactor.json");
        tRecipeFileMap.put(GT_Recipe_Map.sCircuitAssemblerRecipes, "circuit_assembler.json");
        tRecipeFileMap.put(GT_Recipe_Map.sCutterRecipes, "cutter.json");
        tRecipeFileMap.put(GT_Recipe_Map.sDistillationRecipes, "distillation_tower.json");
        tRecipeFileMap.put(GT_Recipe_Map.sDistilleryRecipes, "distillery.json");
        tRecipeFileMap.put(GT_Recipe_Map.sCutterRecipes, "cutter.json");
        tRecipeFileMap.put(GT_Recipe_Map.sElectrolyzerRecipes, "electrolyzer.json");
        tRecipeFileMap.put(GT_Recipe_Map.sExtruderRecipes, "extruder.json");
        tRecipeFileMap.put(GT_Recipe_Map.sFermentingRecipes, "fermenter.json");
        tRecipeFileMap.put(GT_Recipe_Map.sFluidCannerRecipes, "fluid_canner.json");
        tRecipeFileMap.put(GT_Recipe_Map.sFluidExtractionRecipes, "fluid_extractor.json");
        tRecipeFileMap.put(GT_Recipe_Map.sFluidHeaterRecipes, "fluid_heater.json");
        tRecipeFileMap.put(GT_Recipe_Map.sFluidSolidficationRecipes, "fluid_solidifier.json");
        tRecipeFileMap.put(GT_Recipe_Map.sHammerRecipes, "forge_hammer.json");
        tRecipeFileMap.put(GT_Recipe_Map.sPressRecipes, "forming_press.json");
        tRecipeFileMap.put(GT_Recipe_Map.sFusionRecipes, "fusion_reactor.json");
        tRecipeFileMap.put(GT_Recipe_Map.sLaserEngraverRecipes, "laser_engraver.json");
        tRecipeFileMap.put(GT_Recipe_Map.sLatheRecipes, "lathe.json");
        tRecipeFileMap.put(GT_Recipe_Map.sMixerRecipes, "mixer.json");
        tRecipeFileMap.put(GT_Recipe_Map.sPlasmaArcFurnaceRecipes, "plasma_arc_furnace.json");
        tRecipeFileMap.put(GT_Recipe_Map.sPrinterRecipes, "printer.json");
        tRecipeFileMap.put(GT_Recipe_Map.sSifterRecipes, "sifter.json");
        tRecipeFileMap.put(GT_Recipe_Map.sSlicerRecipes, "slicer.json");
        tRecipeFileMap.put(GT_Recipe_Map.sVacuumRecipes, "vacuum_freezer.json");
        tRecipeFileMap.put(GT_Recipe_Map.sWiremillRecipes, "wiremill.json");
        
        File tConfigRecipeFolder = new File(getMcDir(), "config" + File.separator + "GregTech" + File.separator + "recipes");
        try {
            tConfigRecipeFolder.mkdirs();
        } catch (Throwable e) {
            GT_Log.err.println("Failed to create config/GregTech/recipes folder.");
            e.printStackTrace(GT_Log.err);
        }
        InputStream tExplanation = GT_MachineRecipeLoader.class.getResourceAsStream("/assets/gregtech/recipes/Explanation.cfg");
        if (tExplanation != null) {
            try (BufferedReader tReader = new BufferedReader(new InputStreamReader(tExplanation)); PrintWriter tWriter = new PrintWriter(new FileWriter(new File(tConfigRecipeFolder, "Explanation.txt")))) {
                String tLine = tReader.readLine();
                while (tLine != null) {
                    tWriter.println(tLine);
                    tLine = tReader.readLine();
                }
                String[] tJsonFileList = tRecipeFileMap.values().toArray(new String[0]);
                Arrays.sort(tJsonFileList);
                for (String tFileName : tJsonFileList) {
                    tWriter.println(tFileName);
                }
            } catch (IOException e) {
                e.printStackTrace(GT_Log.err);
            }
        }
        for (Map.Entry<GT_Recipe_Map, String> tEntry : tRecipeFileMap.entrySet()) {
            try {
                File tFile = new File(tConfigRecipeFolder, tEntry.getValue());
                if (tFile.isFile()) {
                    FileReader tFileReader = new FileReader(tFile);
                    List<GT_Recipe> tRecipeList = GT_RecipeListJsonReader.readRecipes(new JsonReader(tFileReader));
                    addRecipesToMap(tEntry.getKey(), tRecipeList);
                    tFileReader.close();
                }
            } catch (Throwable e) {
                GT_Log.err.println("Error while reading file " + tEntry.getValue());
                e.printStackTrace(GT_Log.err);
            }
            try {
                InputStream tStream = GT_MachineRecipeLoader.class.getResourceAsStream("/assets/gregtech/recipes/" + tEntry.getValue());
                if (tStream != null) {
                    List<GT_Recipe> tRecipeList = GT_RecipeListJsonReader.readRecipes(new JsonReader(new InputStreamReader(tStream)));
                    addRecipesToMap(tEntry.getKey(), tRecipeList);
                    tStream.close();
                }
            } catch (Throwable e) {
                GT_Log.err.println("Error while reading resource " + tEntry.getValue());
                e.printStackTrace(GT_Log.err);
            }
        }
        try {
            GT_DummyWorld tWorld = (GT_DummyWorld) GT_Values.DW;
            while (tWorld.mRandom.mIterationStep > 0) {
                GT_Values.RA.addFluidExtractionRecipe(GT_Utility.copyAmount(1L, new Object[]{ForgeHooks.getGrassSeed(tWorld)}), GT_Values.NI, Materials.SeedOil.getFluid(10), 10000, 64, 2);
            }
        } catch (Throwable e) {
            GT_Log.out.println("GT_Mod: failed to iterate somehow, maybe it's your Forge Version causing it. But it's not that important\n");
            e.printStackTrace(GT_Log.err);
        }

//        GT_Values.RA.addArcFurnaceRecipe(ItemList.Block_BronzePlate.get(1, new Object[]{}), new ItemStack[]{ GT_OreDictUnificator.get(OrePrefixes.ingot,Materials.Bronze,4), GT_OreDictUnificator.get(OrePrefixes.dust,Materials.Stone,1)}, null, 160, 96);
//        GT_Values.RA.addArcFurnaceRecipe(ItemList.Block_IridiumTungstensteel.get(1, new Object[]{}), new ItemStack[]{ GT_OreDictUnificator.get(OrePrefixes.ingot,Materials.Bronze,4), GT_OreDictUnificator.get(OrePrefixes.dust,Materials.Stone,1)}, null, 160, 96);
        GT_Values.RA.addArcFurnaceRecipe(ItemList.Block_TungstenSteelReinforced.get(1, new Object[]{}), new ItemStack[]{ GT_OreDictUnificator.get(OrePrefixes.ingot,Materials.TungstenSteel,2), GT_OreDictUnificator.get(OrePrefixes.dust,Materials.Concrete,1)}, null, 160, 96);

        //Temporary until circuit overhaul
//        GT_Values.RA.addAlloySmelterRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Rubber, 2), GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.Copper, 1), GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.Copper, 1), 100, 16);

        GT_Values.RA.addPrinterRecipe(GT_OreDictUnificator.get(OrePrefixes.plateDouble, Materials.Paper, 1L), FluidRegistry.getFluidStack("squidink", 36), GT_Values.NI, ItemList.Paper_Punch_Card_Empty.get(1L, new Object[0]), 100, 2);
        GT_Values.RA.addPrinterRecipe(ItemList.Paper_Punch_Card_Empty.get(1L, new Object[0]), FluidRegistry.getFluidStack("squidink", 36), ItemList.Tool_DataStick.getWithName(0L, "With Punch Card Data", new Object[0]), ItemList.Paper_Punch_Card_Encoded.get(1L, new Object[0]), 100, 2);
        GT_Values.RA.addPrinterRecipe(GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Paper, 3L), FluidRegistry.getFluidStack("squidink", 144), ItemList.Tool_DataStick.getWithName(0L, "With Scanned Book Data", new Object[0]), ItemList.Paper_Printed_Pages.get(1L, new Object[0]), 400, 2);
        GT_Values.RA.addPrinterRecipe(new ItemStack(Items.map, 1, 32767), FluidRegistry.getFluidStack("squidink", 144), ItemList.Tool_DataStick.getWithName(0L, "With Scanned Map Data", new Object[0]), new ItemStack(Items.filled_map, 1, 0), 400, 2);
        GT_Values.RA.addPrinterRecipe(new ItemStack(Items.book, 1, 32767), FluidRegistry.getFluidStack("squidink", 144), GT_Values.NI, GT_Utility.getWrittenBook("Manual_Printer", ItemList.Book_Written_01.get(1L, new Object[0])), 400, 2);

        for (int j = 0; j < Dyes.dyeRed.getSizeOfFluidList(); j++) {
            GT_Values.RA.addChemicalBathRecipe(GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.RedAlloy, 1L), Dyes.dyeRed.getFluidDye(j, 72L), GT_ModHandler.getModItem("BuildCraft|Transport", "pipeWire", 4L, 0), GT_Values.NI, GT_Values.NI, null, 32, 16);
        }
        for (int j = 0; j < Dyes.dyeBlue.getSizeOfFluidList(); j++) {
            GT_Values.RA.addChemicalBathRecipe(GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.RedAlloy, 1L), Dyes.dyeBlue.getFluidDye(j, 72L), GT_ModHandler.getModItem("BuildCraft|Transport", "pipeWire", 4L, 1), GT_Values.NI, GT_Values.NI, null, 32, 16);
        }
        for (int j = 0; j < Dyes.dyeGreen.getSizeOfFluidList(); j++) {
            GT_Values.RA.addChemicalBathRecipe(GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.RedAlloy, 1L), Dyes.dyeGreen.getFluidDye(j, 72L), GT_ModHandler.getModItem("BuildCraft|Transport", "pipeWire", 4L, 2), GT_Values.NI, GT_Values.NI, null, 32, 16);
        }
        for (int j = 0; j < Dyes.dyeYellow.getSizeOfFluidList(); j++) {
            GT_Values.RA.addChemicalBathRecipe(GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.RedAlloy, 1L), Dyes.dyeYellow.getFluidDye(j, 72L), GT_ModHandler.getModItem("BuildCraft|Transport", "pipeWire", 4L, 3), GT_Values.NI, GT_Values.NI, null, 32, 16);
        }
        for (byte i = 0; i < 16; i = (byte) (i + 1)) {
            for (int j = 0; j < Dyes.VALUES[i].getSizeOfFluidList(); j++) {
                if (i != 15) {
                    GT_Values.RA.addChemicalBathRecipe(new ItemStack(Blocks.wool, 1, 0), Dyes.VALUES[i].getFluidDye(j, 72L), new ItemStack(Blocks.wool, 1, 15 - i), GT_Values.NI, GT_Values.NI, null, 64, 2);
                }
                GT_Values.RA.addAssemblerRecipe(new ItemStack(Items.string, 3), ItemList.Circuit_Integrated.getWithDamage(0L, 3L, new Object[0]), Dyes.VALUES[i].getFluidDye(j, 24L), new ItemStack(Blocks.carpet, 2, 15 - i), 128, 5);
                GT_Values.RA.addChemicalBathRecipe(new ItemStack(Blocks.glass, 1, 0), Dyes.VALUES[i].getFluidDye(j, 18L), new ItemStack(Blocks.stained_glass, 1, 15 - i), GT_Values.NI, GT_Values.NI, null, 64, 2);
                GT_Values.RA.addChemicalBathRecipe(new ItemStack(Blocks.hardened_clay, 1, 0), Dyes.VALUES[i].getFluidDye(j, 18L), new ItemStack(Blocks.stained_hardened_clay, 1, 15 - i), GT_Values.NI, GT_Values.NI, null, 64, 2);
            }
        }

//Circuit Recipes!!!
        Object[] o = new Object[0];
        GT_ModHandler.addCraftingRecipe(ItemList.Circuit_Board_Coated.get(3, o), new Object[]{" R ","PPP"," R ",'P',GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Wood, 1),'R',ItemList.IC2_Resin.get(1, o)});
        GT_ModHandler.addCraftingRecipe(ItemList.Circuit_Board_Phenolic.get(8, o), new Object[]{"PRP","PPP","PPP",'P',GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Wood, 1),'R',GT_OreDictUnificator.get(OrePrefixes.cell, Materials.Glue, 1)});
        GT_ModHandler.addCraftingRecipe(ItemList.Circuit_Board_Phenolic.get(32, o), new Object[]{"PRP","PPP","PPP",'P',GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Wood, 1),'R',GT_OreDictUnificator.get(OrePrefixes.cell, Materials.BisphenolA, 1)});

        GT_ModHandler.addCraftingRecipe(ItemList.Circuit_Parts_Resistor.get(3, o), new Object[]{" P ","FCF"," P ",'P',new ItemStack(Items.paper),'F',OrePrefixes.wireFine.get(Materials.Copper),'C',OrePrefixes.dust.get(Materials.Coal)});
        GT_ModHandler.addCraftingRecipe(ItemList.Circuit_Parts_Resistor.get(3, o), new Object[]{" P ","FCF"," P ",'P',new ItemStack(Items.paper),'F',OrePrefixes.wireFine.get(Materials.Copper),'C',OrePrefixes.dust.get(Materials.Charcoal)});
        GT_ModHandler.addCraftingRecipe(ItemList.Circuit_Parts_Resistor.get(3, o), new Object[]{" P ","FCF"," P ",'P',new ItemStack(Items.paper),'F',OrePrefixes.wireFine.get(Materials.Copper),'C',OrePrefixes.dust.get(Materials.Carbon)});
        GT_ModHandler.addCraftingRecipe(ItemList.Circuit_Parts_Vacuum_Tube.get(1, o), new Object[]{"PGP","FFF",'G',ItemList.Circuit_Parts_Glass_Tube,'P',new ItemStack(Items.paper),'F',OrePrefixes.wireFine.get(Materials.Copper)});
        GT_ModHandler.addCraftingRecipe(ItemList.Circuit_Parts_Vacuum_Tube.get(1, o), new Object[]{"PGP","FFF",'G',ItemList.Circuit_Parts_Glass_Tube,'P',new ItemStack(Items.paper),'F',OrePrefixes.wireGt01.get(Materials.Copper)});
        GT_ModHandler.addCraftingRecipe(ItemList.Circuit_Parts_Diode.get(1,o), new Object[]{"BG ","WDW","BG ",'B',OrePrefixes.dye.get(Materials.Black),'G',new ItemStack(Blocks.glass_pane),'D',ItemList.Circuit_Silicon_Wafer.get(1, new Object[0]),'W',OrePrefixes.wireGt01.get(Materials.Tin)});
        GT_ModHandler.addCraftingRecipe(ItemList.Circuit_Parts_Diode.get(1,o), new Object[]{"BG ","WDW","BG ",'B',OrePrefixes.dye.get(Materials.Black),'G',new ItemStack(Blocks.glass_pane),'D',ItemList.Circuit_Silicon_Wafer.get(1, new Object[0]),'W',OrePrefixes.wireFine.get(Materials.Tin)});
        GT_ModHandler.addCraftingRecipe(ItemList.Circuit_Parts_Diode.get(4,o), new Object[]{"BG ","WDW","BG ",'B',OrePrefixes.dye.get(Materials.Black),'G',new ItemStack(Blocks.glass_pane),'D',GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Gallium, 1),'W',OrePrefixes.wireGt01.get(Materials.Tin)});
        GT_ModHandler.addCraftingRecipe(ItemList.Circuit_Parts_Diode.get(4,o), new Object[]{"BG ","WDW","BG ",'B',OrePrefixes.dye.get(Materials.Black),'G',new ItemStack(Blocks.glass_pane),'D',GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Gallium, 1),'W',OrePrefixes.wireFine.get(Materials.Tin)});
        GT_ModHandler.addCraftingRecipe(ItemList.Circuit_Parts_Coil.get(2,o), new Object[]{"WWW","WDW","WWW",'G',new ItemStack(Blocks.glass_pane),'D',OrePrefixes.bolt.get(Materials.Steel),'W',OrePrefixes.wireFine.get(Materials.Copper)});
        GT_ModHandler.addCraftingRecipe(ItemList.Circuit_Parts_Coil.get(4,o), new Object[]{"WWW","WDW","WWW",'G',new ItemStack(Blocks.glass_pane),'D',OrePrefixes.bolt.get(Materials.NickelZincFerrite),'W',OrePrefixes.wireFine.get(Materials.Copper)});

        GT_ModHandler.addShapelessCraftingRecipe(ItemList.Circuit_Parts_RawCrystalChip.get(9,o), new Object[]{ItemList.Circuit_Chip_CrystalCPU.get(1,o)});

        GT_ModHandler.addCraftingRecipe(ItemList.Circuit_Good.get(1,o), new Object[]{"IVC","VDV","CVI",'D',ItemList.Circuit_Parts_Diode.get(1,o),'C',GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.RedAlloy, 1),'V', Ic2Items.electronicCircuit ,'I',ItemList.IC2_Item_Casing_Steel.get(1,o)});

        if (GregTech_API.sSpecialFile.get("general", "EnableLagencyOilGalactiCraft", false) && FluidRegistry.getFluid("oilgc") != null) {
            GT_Values.RA.addUniversalDistillationRecipe(new FluidStack(FluidRegistry.getFluid("oilgc"), 50), new FluidStack[]{Materials.SulfuricHeavyFuel.getFluid(15), Materials.SulfuricLightFuel.getFluid(50), Materials.SulfuricNaphtha.getFluid(20), Materials.SulfuricGas.getGas(60)}, null, 20, 96);
        }

        addPotionRecipes("waterbreathing", new ItemStack(Items.fish, 1, 3));
        addPotionRecipes("fireresistance", new ItemStack(Items.magma_cream, 1, 0));
        addPotionRecipes("nightvision", new ItemStack(Items.golden_carrot, 1, 0));
        addPotionRecipes("weakness", new ItemStack(Items.fermented_spider_eye, 1, 0));
        addPotionRecipes("poison", new ItemStack(Items.spider_eye, 1, 0));
        addPotionRecipes("health", new ItemStack(Items.speckled_melon, 1, 0));
        addPotionRecipes("regen", new ItemStack(Items.ghast_tear, 1, 0));
        addPotionRecipes("speed", GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Sugar, 1L));
        addPotionRecipes("strength", GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Blaze, 1L));

        GT_ModHandler.addSmeltingRecipe(ItemList.Food_Raw_PotatoChips.get(1L, new Object[0]), ItemList.Food_PotatoChips.get(1L, new Object[0]));
        GT_ModHandler.addSmeltingRecipe(ItemList.Food_Potato_On_Stick.get(1L, new Object[0]), ItemList.Food_Potato_On_Stick_Roasted.get(1L, new Object[0]));
        GT_ModHandler.addSmeltingRecipe(ItemList.Food_Raw_Bun.get(1L, new Object[0]), ItemList.Food_Baked_Bun.get(1L, new Object[0]));
        GT_ModHandler.addSmeltingRecipe(ItemList.Food_Raw_Bread.get(1L, new Object[0]), ItemList.Food_Baked_Bread.get(1L, new Object[0]));
        GT_ModHandler.addSmeltingRecipe(ItemList.Food_Raw_Baguette.get(1L, new Object[0]), ItemList.Food_Baked_Baguette.get(1L, new Object[0]));
        GT_ModHandler.addSmeltingRecipe(ItemList.Food_Raw_Pizza_Veggie.get(1L, new Object[0]), ItemList.Food_Baked_Pizza_Veggie.get(1L, new Object[0]));
        GT_ModHandler.addSmeltingRecipe(ItemList.Food_Raw_Pizza_Cheese.get(1L, new Object[0]), ItemList.Food_Baked_Pizza_Cheese.get(1L, new Object[0]));
        GT_ModHandler.addSmeltingRecipe(ItemList.Food_Raw_Pizza_Meat.get(1L, new Object[0]), ItemList.Food_Baked_Pizza_Meat.get(1L, new Object[0]));
        GT_ModHandler.addSmeltingRecipe(ItemList.Food_Raw_Baguette.get(1L, new Object[0]), ItemList.Food_Baked_Baguette.get(1L, new Object[0]));
        GT_ModHandler.addSmeltingRecipe(ItemList.Food_Raw_Cake.get(1L, new Object[0]), ItemList.Food_Baked_Cake.get(1L, new Object[0]));
        GT_ModHandler.addSmeltingRecipe(ItemList.Food_Raw_Cookie.get(1L, new Object[0]), new ItemStack(Items.cookie, 1));
        GT_ModHandler.addSmeltingRecipe(new ItemStack(Items.slime_ball, 1), ItemList.IC2_Resin.get(1L, new Object[0]));

        GT_ModHandler.addExtractionRecipe(new ItemStack(Blocks.bookshelf, 1, 32767), new ItemStack(Items.book, 3, 0));
        GT_ModHandler.addExtractionRecipe(new ItemStack(Items.slime_ball, 1), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.RawRubber, 2L));
        GT_ModHandler.addExtractionRecipe(ItemList.IC2_Resin.get(1L, new Object[0]), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.RawRubber, 3L));
        GT_ModHandler.addExtractionRecipe(GT_ModHandler.getIC2Item("rubberSapling", 1L), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.RawRubber, 1L));
        GT_ModHandler.addExtractionRecipe(GT_ModHandler.getIC2Item("rubberLeaves", 16L), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.RawRubber, 1L));
        GT_ModHandler.addExtractionRecipe(ItemList.Cell_Air.get(1L, new Object[0]), ItemList.Cell_Empty.get(1L, new Object[0]));
        if (Loader.isModLoaded(aTextEBXL)) {
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "waterplant1", 1, 0), new ItemStack(Items.dye, 4, 2));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "vines", 1, 0), new ItemStack(Items.dye, 4, 1));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower3", 1, 11), new ItemStack(Items.dye, 4, 11));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower3", 1, 10), new ItemStack(Items.dye, 4, 5));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower3", 1, 9), new ItemStack(Items.dye, 4, 14));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower3", 1, 8), new ItemStack(Items.dye, 4, 14));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower3", 1, 7), new ItemStack(Items.dye, 4, 1));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower3", 1, 6), new ItemStack(Items.dye, 4, 1));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower3", 1, 5), new ItemStack(Items.dye, 4, 11));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower3", 1, 0), new ItemStack(Items.dye, 4, 9));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower3", 1, 4), new ItemStack(Items.dye, 4, 11));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower3", 1, 3), new ItemStack(Items.dye, 4, 13));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower1", 1, 3), new ItemStack(Items.dye, 4, 5));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower3", 1, 2), new ItemStack(Items.dye, 4, 5));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower1", 1, 1), new ItemStack(Items.dye, 4, 12));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower2", 1, 15), new ItemStack(Items.dye, 4, 11));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower2", 1, 14), new ItemStack(Items.dye, 4, 1));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower2", 1, 13), new ItemStack(Items.dye, 4, 9));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower2", 1, 12), new ItemStack(Items.dye, 4, 14));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower2", 1, 11), new ItemStack(Items.dye, 4, 7));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower1", 1, 7), new ItemStack(Items.dye, 4, 7));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower1", 1, 2), new ItemStack(Items.dye, 4, 11));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower3", 1, 13), new ItemStack(Items.dye, 4, 6));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower2", 1, 6), new ItemStack(Items.dye, 4, 12));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower2", 1, 5), new ItemStack(Items.dye, 4, 10));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower2", 1, 2), new ItemStack(Items.dye, 4, 1));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower2", 1, 1), new ItemStack(Items.dye, 4, 9));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower2", 1, 0), new ItemStack(Items.dye, 4, 13));

            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower2", 1, 7), GT_ModHandler.getModItem(aTextEBXL, "extrabiomes.dye", 1, 0));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower3", 1, 1), GT_ModHandler.getModItem(aTextEBXL, "extrabiomes.dye", 1, 1));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower3", 1,12), GT_ModHandler.getModItem(aTextEBXL, "extrabiomes.dye", 1, 1));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower2", 1, 4), GT_ModHandler.getModItem(aTextEBXL, "extrabiomes.dye", 1, 1));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower1", 1, 6), GT_ModHandler.getModItem(aTextEBXL, "extrabiomes.dye", 1, 2));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower2", 1, 8), GT_ModHandler.getModItem(aTextEBXL, "extrabiomes.dye", 1, 3));
            GT_ModHandler.addExtractionRecipe(GT_ModHandler.getModItem(aTextEBXL, "flower2", 1, 3), GT_ModHandler.getModItem(aTextEBXL, "extrabiomes.dye", 1, 3));

            GT_ModHandler.addCompressionRecipe(GT_ModHandler.getModItem(aTextEBXL, "saplings_1", 4, 0), ItemList.IC2_Plantball.get(1, new Object[0]));
            GT_ModHandler.addCompressionRecipe(GT_ModHandler.getModItem(aTextEBXL, "saplings_1", 4, 1), ItemList.IC2_Plantball.get(1, new Object[0]));
            GT_ModHandler.addCompressionRecipe(GT_ModHandler.getModItem(aTextEBXL, "saplings_1", 4, 2), ItemList.IC2_Plantball.get(1, new Object[0]));
            GT_ModHandler.addCompressionRecipe(GT_ModHandler.getModItem(aTextEBXL, "saplings_1", 4, 3), ItemList.IC2_Plantball.get(1, new Object[0]));
            GT_ModHandler.addCompressionRecipe(GT_ModHandler.getModItem(aTextEBXL, "saplings_1", 4, 4), ItemList.IC2_Plantball.get(1, new Object[0]));
            GT_ModHandler.addCompressionRecipe(GT_ModHandler.getModItem(aTextEBXL, "saplings_1", 4, 5), ItemList.IC2_Plantball.get(1, new Object[0]));
            GT_ModHandler.addCompressionRecipe(GT_ModHandler.getModItem(aTextEBXL, "saplings_1", 4, 6), ItemList.IC2_Plantball.get(1, new Object[0]));
            GT_ModHandler.addCompressionRecipe(GT_ModHandler.getModItem(aTextEBXL, "saplings_1", 4, 7), ItemList.IC2_Plantball.get(1, new Object[0]));
            GT_ModHandler.addCompressionRecipe(GT_ModHandler.getModItem(aTextEBXL, "saplings_2", 4, 0), ItemList.IC2_Plantball.get(1, new Object[0]));
            GT_ModHandler.addCompressionRecipe(GT_ModHandler.getModItem(aTextEBXL, "saplings_2", 4, 1), ItemList.IC2_Plantball.get(1, new Object[0]));
            GT_ModHandler.addCompressionRecipe(GT_ModHandler.getModItem(aTextEBXL, "saplings_2", 4, 2), ItemList.IC2_Plantball.get(1, new Object[0]));
            GT_ModHandler.addCompressionRecipe(GT_ModHandler.getModItem(aTextEBXL, "saplings_2", 4, 3), ItemList.IC2_Plantball.get(1, new Object[0]));
            GT_ModHandler.addCompressionRecipe(GT_ModHandler.getModItem(aTextEBXL, "saplings_2", 4, 4), ItemList.IC2_Plantball.get(1, new Object[0]));

        }
        GT_ModHandler.addCompressionRecipe(ItemList.IC2_Compressed_Coal_Chunk.get(1L, new Object[0]), ItemList.IC2_Industrial_Diamond.get(1L, new Object[0]));
        GT_ModHandler.addCompressionRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 1L), GT_ModHandler.getIC2Item("Uran238", 1L));
        GT_ModHandler.addCompressionRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Uranium235, 1L), GT_ModHandler.getIC2Item("Uran235", 1L));
        GT_ModHandler.addCompressionRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 1L), GT_ModHandler.getIC2Item("Plutonium", 1L));
        GT_ModHandler.addCompressionRecipe(GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Uranium235, 1L), GT_ModHandler.getIC2Item("smallUran235", 1L));
        GT_ModHandler.addCompressionRecipe(GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Plutonium, 1L), GT_ModHandler.getIC2Item("smallPlutonium", 1L));
        GT_ModHandler.addCompressionRecipe(new ItemStack(Blocks.ice, 2, 32767), new ItemStack(Blocks.packed_ice, 1, 0));
        GT_ModHandler.addCompressionRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Ice, 1L), new ItemStack(Blocks.ice, 1, 0));
        GT_ModHandler.addCompressionRecipe(GT_OreDictUnificator.get(OrePrefixes.gem, Materials.CertusQuartz, 4L), GT_ModHandler.getModItem(aTextAE, "tile.BlockQuartz", 1L));
        GT_ModHandler.addCompressionRecipe(GT_ModHandler.getModItem(aTextAE, aTextAEMM, 8L, 10), GT_ModHandler.getModItem(aTextAE, "tile.BlockQuartz", 1L));
        GT_ModHandler.addCompressionRecipe(GT_ModHandler.getModItem(aTextAE, aTextAEMM, 8L, 11), new ItemStack(Blocks.quartz_block, 1, 0));
        GT_ModHandler.addCompressionRecipe(GT_ModHandler.getModItem(aTextAE, aTextAEMM, 8L, 12), GT_ModHandler.getModItem(aTextAE, "tile.BlockFluix", 1L));
        GT_ModHandler.addCompressionRecipe(new ItemStack(Items.quartz, 4, 0), new ItemStack(Blocks.quartz_block, 1, 0));
        GT_ModHandler.addCompressionRecipe(new ItemStack(Items.wheat, 9, 0), new ItemStack(Blocks.hay_block, 1, 0));
        GT_ModHandler.addCompressionRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 4L), new ItemStack(Blocks.glowstone, 1));
        GT_ModHandler.addCompressionRecipe(Materials.Fireclay.getDust(1), ItemList.CompressedFireclay.get(1));

        GameRegistry.addSmelting(ItemList.CompressedFireclay.get(1, new Object[0]), ItemList.Firebrick.get(1, new Object[0]), 0);

        GT_Values.RA.addCutterRecipe(GT_OreDictUnificator.get(OrePrefixes.block, Materials.Graphite, 1L), GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Graphite, 9L), GT_Values.NI, 500, 48);
        GT_ModHandler.removeFurnaceSmelting(GT_OreDictUnificator.get(OrePrefixes.ore, Materials.Graphite, 1L));
        GT_ModHandler.addSmeltingRecipe(GT_OreDictUnificator.get(OrePrefixes.ore, Materials.Graphite, 1L), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Graphite, 1L));
        GT_ModHandler.removeFurnaceSmelting(GT_OreDictUnificator.get(OrePrefixes.oreBlackgranite, Materials.Graphite, 1L));
        GT_ModHandler.addSmeltingRecipe(GT_OreDictUnificator.get(OrePrefixes.oreBlackgranite, Materials.Graphite, 1L), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Graphite, 1L));
        GT_ModHandler.removeFurnaceSmelting(GT_OreDictUnificator.get(OrePrefixes.oreEndstone, Materials.Graphite, 1L));
        GT_ModHandler.addSmeltingRecipe(GT_OreDictUnificator.get(OrePrefixes.oreEndstone, Materials.Graphite, 1L), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Graphite, 1L));
        GT_ModHandler.removeFurnaceSmelting(GT_OreDictUnificator.get(OrePrefixes.oreNetherrack, Materials.Graphite, 1L));
        GT_ModHandler.addSmeltingRecipe(GT_OreDictUnificator.get(OrePrefixes.oreNetherrack, Materials.Graphite, 1L), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Graphite, 1L));
        GT_ModHandler.removeFurnaceSmelting(GT_OreDictUnificator.get(OrePrefixes.oreRedgranite, Materials.Graphite, 1L));
        GT_ModHandler.addSmeltingRecipe(GT_OreDictUnificator.get(OrePrefixes.oreRedgranite, Materials.Graphite, 1L), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Graphite, 1L));

        GT_ModHandler.addPulverisationRecipe(GT_ModHandler.getModItem(aTextAE, "tile.BlockSkyStone", 1L, 32767), GT_ModHandler.getModItem(aTextAE, aTextAEMM, 1L, 45), GT_Values.NI, 0, false);
        GT_ModHandler.addPulverisationRecipe(GT_ModHandler.getModItem(aTextAE, "tile.BlockSkyChest", 1L, 32767), GT_ModHandler.getModItem(aTextAE, aTextAEMM, 8L, 45), GT_Values.NI, 0, false);
        GT_ModHandler.addPulverisationRecipe(new ItemStack(Items.blaze_rod, 1), new ItemStack(Items.blaze_powder, 3), new ItemStack(Items.blaze_powder, 1), 50, false);
        GT_ModHandler.addPulverisationRecipe(GT_ModHandler.getModItem("Railcraft", "cube.crushed.obsidian", 1L), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Obsidian, 1L), GT_Values.NI, 0, true);
        GT_ModHandler.addPulverisationRecipe(new ItemStack(Items.flint, 1, 32767), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Flint, 4L), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Flint, 1L), 40, true);
        GT_ModHandler.addPulverisationRecipe(new ItemStack(Blocks.red_mushroom, 1, 32767), ItemList.IC2_Grin_Powder.get(1L, new Object[0]));
        GT_ModHandler.addPulverisationRecipe(new ItemStack(Items.item_frame, 1, 32767), new ItemStack(Items.leather, 1), GT_OreDictUnificator.getDust(Materials.Wood, OrePrefixes.stick.mMaterialAmount * 4L), 95, false);
        GT_ModHandler.addPulverisationRecipe(new ItemStack(Items.bow, 1, 0), new ItemStack(Items.string, 3), GT_OreDictUnificator.getDust(Materials.Wood, OrePrefixes.stick.mMaterialAmount * 3L), 95, false);

        GT_ModHandler.addPulverisationRecipe(Materials.Brick.getIngots(1), Materials.Brick.getDustSmall(1));
        GT_ModHandler.addPulverisationRecipe(ItemList.CompressedFireclay.get(1, new Object[0]), Materials.Fireclay.getDustSmall(1));
        GT_ModHandler.addPulverisationRecipe(ItemList.Firebrick.get(1, new Object[0]), Materials.Brick.getDust(1));
        GT_ModHandler.addPulverisationRecipe(ItemList.Casing_Firebricks.get(1, new Object[0]), Materials.Brick.getDust(4));
        GT_ModHandler.addPulverisationRecipe(ItemList.Machine_Bricked_BlastFurnace.get(1, new Object[0]), Materials.Brick.getDust(8), Materials.Iron.getDust(1), true);

        GT_ModHandler.addPulverisationRecipe(GT_ModHandler.getModItem("HardcoreEnderExpansion", "endium_ore", 1), GT_OreDictUnificator.get(OrePrefixes.crushed, Materials.Endium, 2), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Endstone, 1), 50, GT_Values.NI, 0, true);
        GT_OreDictUnificator.set(OrePrefixes.ingot, Materials.Endium, GT_ModHandler.getModItem("HardcoreEnderExpansion", "endium_ingot", 1), true, true);

        GT_Values.RA.addAmplifier(ItemList.IC2_Scrap.get(9L, new Object[0]), 180, 1);
        GT_Values.RA.addAmplifier(ItemList.IC2_Scrapbox.get(1L, new Object[0]), 180, 1);

        GT_Values.RA.addBoxingRecipe(ItemList.IC2_Scrap.get(9L, new Object[0]), ItemList.Schematic_3by3.get(0L, new Object[0]), ItemList.IC2_Scrapbox.get(1L, new Object[0]), 16, 1);
        GT_Values.RA.addBoxingRecipe(ItemList.Food_Fries.get(1L, new Object[0]), GT_OreDictUnificator.get(OrePrefixes.plateDouble, Materials.Paper, 1L), ItemList.Food_Packaged_Fries.get(1L, new Object[0]), 64, 16);
        GT_Values.RA.addBoxingRecipe(ItemList.Food_PotatoChips.get(1L, new Object[0]), GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Aluminium, 1L), ItemList.Food_Packaged_PotatoChips.get(1L, new Object[0]), 64, 16);
        GT_Values.RA.addBoxingRecipe(ItemList.Food_ChiliChips.get(1L, new Object[0]), GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Aluminium, 1L), ItemList.Food_Packaged_ChiliChips.get(1L, new Object[0]), 64, 16);

        GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Steel, 3L), GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Silicon, 1L), GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.ElectricalSteel, 4L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.DarkAsh, 2L), (int) Math.max(Materials.ElectricalSteel.getMass() / 40L, 1L) * Materials.ElectricalSteel.mBlastFurnaceTemp, 120, Materials.ElectricalSteel.mBlastFurnaceTemp);
        GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Gold, 1L), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 1L), Materials.Redstone.getMolten(144), GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.EnergeticAlloy, 1L), null, Materials.EnergeticAlloy.mBlastFurnaceTemp / 10, 120, Materials.EnergeticAlloy.mBlastFurnaceTemp);
        GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.EnergeticAlloy, 1L), GT_OreDictUnificator.get(OrePrefixes.gem, Materials.EnderPearl, 1L), GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.PhasedGold, 1L), null, Materials.VibrantAlloy.mBlastFurnaceTemp / 10, 480, Materials.VibrantAlloy.mBlastFurnaceTemp);
        GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Iron, 1L), GT_OreDictUnificator.get(OrePrefixes.gem, Materials.EnderPearl, 1L), GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.PhasedIron, 1L), null, Materials.PulsatingIron.mBlastFurnaceTemp / 10, 120, Materials.PulsatingIron.mBlastFurnaceTemp);

        GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Tungsten, 1L), GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Steel, 1L), GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingotHot, Materials.TungstenSteel, 2L), GT_Values.NI, (int) Math.max(Materials.TungstenSteel.getMass() / 80L, 1L) * Materials.TungstenSteel.mBlastFurnaceTemp, 480, Materials.TungstenSteel.mBlastFurnaceTemp);
        GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Tungsten, 1L), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1L), GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingotHot, Materials.TungstenCarbide, 2L), GT_Values.NI, (int) Math.max(Materials.TungstenCarbide.getMass() / 40L, 1L) * Materials.TungstenCarbide.mBlastFurnaceTemp, 480, Materials.TungstenCarbide.mBlastFurnaceTemp);
        GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Vanadium, 3L), GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Gallium, 1L), GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingotHot, Materials.VanadiumGallium, 4L), GT_Values.NI, (int) Math.max(Materials.VanadiumGallium.getMass() / 40L, 1L) * Materials.VanadiumGallium.mBlastFurnaceTemp, 480, Materials.VanadiumGallium.mBlastFurnaceTemp);
        GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Niobium, 1L), GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Titanium, 1L), GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingotHot, Materials.NiobiumTitanium, 2L), GT_Values.NI, (int) Math.max(Materials.NiobiumTitanium.getMass() / 80L, 1L) * Materials.NiobiumTitanium.mBlastFurnaceTemp, 480, Materials.NiobiumTitanium.mBlastFurnaceTemp);
        GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Nickel, 4L), GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Chrome, 1L), GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingotHot, Materials.Nichrome, 5L), GT_Values.NI, (int) Math.max(Materials.Nichrome.getMass() / 32L, 1L) * Materials.Nichrome.mBlastFurnaceTemp, 480, Materials.Nichrome.mBlastFurnaceTemp);
        
        GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Magnesium, 2L), GT_Values.NI, Materials.Titaniumtetrachloride.getFluid(1000L), GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingotHot, Materials.Titanium, 1L), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Magnesiumchloride, 2L), 800, 480, Materials.Titanium.mBlastFurnaceTemp + 200);
        
        GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Naquadah, 1L), GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Osmiridium, 1L), Materials.Argon.getGas(1000), null, GT_OreDictUnificator.get(OrePrefixes.ingotHot, Materials.NaquadahAlloy, 2L), null, 500, 30720, Materials.NaquadahAlloy.mBlastFurnaceTemp);
        GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Gallium, 1L), GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Arsenic, 1L), null, null, GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.GalliumArsenide, 2L), null, 600, 120, Materials.GalliumArsenide.mBlastFurnaceTemp);
        GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.FerriteMixture, 1L), GT_Values.NI, Materials.Oxygen.getGas(2000), GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.NickelZincFerrite, 1L), null, 600, 120, Materials.NickelZincFerrite.mBlastFurnaceTemp);
        if(!GregTech_API.mIC2Classic){
            GT_Values.RA.addCannerRecipe(GT_ModHandler.getIC2Item("fuelRod", 1), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Lithium, 1L), GT_ModHandler.getIC2Item("reactorLithiumCell", 1, 1), null, 16, 64);
            GT_Values.RA.addFluidExtractionRecipe(GT_ModHandler.getIC2Item("TritiumCell", 1), GT_ModHandler.getIC2Item("fuelRod", 1), Materials.Tritium.getGas(32), 10000, 16, 64);
            GT_Values.RA.addCannerRecipe(GT_ModHandler.getIC2Item("fuelRod", 1), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Thorium, 3), ItemList.ThoriumCell_1.get(1L, new Object[0]), null, 30, 16);
            GT_Values.RA.addCannerRecipe(GT_ModHandler.getIC2Item("fuelRod", 1), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.NaquadahEnriched, 3), ItemList.NaquadahCell_1.get(1L, new Object[0]), null, 30, 16);
            GT_Values.RA.addCannerRecipe(GT_ModHandler.getIC2Item("fuelRod", 1), GT_ModHandler.getIC2Item("UranFuel", 1), ItemList.Uraniumcell_1.get(1, new Object[0]), null, 30, 16);
            GT_Values.RA.addCannerRecipe(GT_ModHandler.getIC2Item("fuelRod", 1), GT_ModHandler.getIC2Item("MOXFuel", 1), ItemList.Moxcell_1.get(1, new Object[0]), null, 30, 16);
        }
        GT_ModHandler.removeRecipeByOutput(ItemList.IC2_Fertilizer.get(1L, new Object[0]));
        GT_Values.RA.addImplosionRecipe(ItemList.IC2_Compressed_Coal_Chunk.get(1L, new Object[0]), 8, ItemList.IC2_Industrial_Diamond.get(1L, new Object[0]), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.DarkAsh, 4L));
        GT_Values.RA.addImplosionRecipe(ItemList.Ingot_IridiumAlloy.get(1L, new Object[0]), 8, GT_OreDictUnificator.get(OrePrefixes.plateAlloy, Materials.Iridium, 1L), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.DarkAsh, 4L));

        if (Loader.isModLoaded("GalacticraftMars")) {
            GT_ModHandler.addCraftingRecipe(ItemList.Ingot_Heavy1.get(1L, new Object[0]), GT_ModHandler.RecipeBits.NOT_REMOVABLE, new Object[]{"BhB", "CAS", "B B", 'B', OrePrefixes.bolt.get(Materials.StainlessSteel), 'C', OrePrefixes.compressed.get(Materials.Bronze), 'A', OrePrefixes.compressed.get(Materials.Aluminium), 'S', OrePrefixes.compressed.get(Materials.Steel)});
            GT_ModHandler.addCraftingRecipe(ItemList.Ingot_Heavy2.get(1L, new Object[0]), GT_ModHandler.RecipeBits.NOT_REMOVABLE, new Object[]{" BB", "hPC", " BB", 'B', OrePrefixes.bolt.get(Materials.Tungsten), 'C', OrePrefixes.compressed.get(Materials.MeteoricIron), 'P', GT_ModHandler.getModItem("GalacticraftCore", "item.heavyPlating", 1L)});
            GT_ModHandler.addCraftingRecipe(ItemList.Ingot_Heavy3.get(1L, new Object[0]), GT_ModHandler.RecipeBits.NOT_REMOVABLE, new Object[]{" BB", "hPC", " BB", 'B', OrePrefixes.bolt.get(Materials.TungstenSteel), 'C', OrePrefixes.compressed.get(Materials.Desh), 'P', GT_ModHandler.getModItem("GalacticraftMars", "item.null", 1L, 3)});

            GT_Values.RA.addImplosionRecipe(ItemList.Ingot_Heavy1.get(1L, new Object[0]), 8, GT_ModHandler.getModItem("GalacticraftCore", "item.heavyPlating", 1L), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.StainlessSteel, 2L));
            GT_Values.RA.addImplosionRecipe(ItemList.Ingot_Heavy2.get(1L, new Object[0]), 8, GT_ModHandler.getModItem("GalacticraftMars", "item.null", 1L, 3), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Tungsten, 2L));
            GT_Values.RA.addImplosionRecipe(ItemList.Ingot_Heavy3.get(1L, new Object[0]), 8, GT_ModHandler.getModItem("GalacticraftMars", "item.itemBasicAsteroids", 1L), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.TungstenSteel, 2L));

            GT_Values.RA.addCentrifugeRecipe(GT_ModHandler.getModItem("GalacticraftCore", "tile.moonBlock", 1L, 5), null, null, Materials.Helium_3.getGas(33), new ItemStack(Blocks.sand,1), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Calcite, 1), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Magnesium, 1), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Rutile, 1), new int[]{5000,400,400,100,100,100}, 400, 8);
            GT_Values.RA.addPulveriserRecipe(GT_ModHandler.getModItem("GalacticraftCore", "tile.moonBlock", 1L, 4), new ItemStack[]{GT_ModHandler.getModItem("GalacticraftCore", "tile.moonBlock", 1L, 5)}, null, 400, 2);
            GT_Values.RA.addFluidExtractionRecipe(GT_ModHandler.getModItem("GalacticraftMars", "tile.mars", 1L, 9), new ItemStack(Blocks.stone, 1), Materials.Iron.getMolten(50), 10000, 250, 16);
            GT_Values.RA.addPulveriserRecipe(GT_ModHandler.getModItem("GalacticraftMars", "tile.asteroidsBlock", 1L, 1), new ItemStack[]{GT_ModHandler.getModItem("GalacticraftMars", "tile.asteroidsBlock", 1L, 0)}, null, 400, 2);
            GT_Values.RA.addPulveriserRecipe(GT_ModHandler.getModItem("GalacticraftMars", "tile.asteroidsBlock", 1L, 2), new ItemStack[]{GT_ModHandler.getModItem("GalacticraftMars", "tile.asteroidsBlock", 1L, 0)}, null, 400, 2);
            GT_Values.RA.addCentrifugeRecipe(GT_ModHandler.getModItem("GalacticraftMars", "tile.asteroidsBlock", 1L, 0), null, null, Materials.Nitrogen.getGas(33), new ItemStack(Blocks.sand,1), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Aluminium, 1), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Nickel, 1), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Gallium, 1), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Platinum, 1), new int[]{5000,400,400,100,100,100}, 400, 8);
        }


        if(!GregTech_API.mIC2Classic){
            GT_Values.RA.addDistillationTowerRecipe(new FluidStack(FluidRegistry.getFluid("ic2biomass"), 2000), new FluidStack[]{new FluidStack(FluidRegistry.getFluid("ic2biogas"), 8000), Materials.Water.getFluid(125L)}, ItemList.IC2_Fertilizer.get(1, new Object[0]), 250, 480);
            GT_Values.RA.addFuel(GT_ModHandler.getIC2Item("biogasCell", 1L), null, 32, 1);

            GT_Values.RA.addDistilleryRecipe(ItemList.Circuit_Integrated.getWithDamage(0L, 1L, new Object[0]), new FluidStack(FluidRegistry.getFluid("ic2biomass"), 12), new FluidStack(FluidRegistry.getFluid("ic2biogas"), 32), 40, 16, false);
            GT_Values.RA.addDistilleryRecipe(ItemList.Circuit_Integrated.getWithDamage(0L, 2L, new Object[0]), new FluidStack(FluidRegistry.getFluid("ic2biomass"), 4), Materials.Water.getFluid(2), 80, 30, false);
        }

        GT_Values.RA.addFuel(new ItemStack(Items.golden_apple,1,1), new ItemStack(Items.apple,1), 6400, 5);
        GT_Values.RA.addFuel(GT_ModHandler.getModItem("Thaumcraft", "ItemShard", 1L, 6), null, 720, 5);
        GT_Values.RA.addFuel(GT_ModHandler.getModItem("ForbiddenMagic", "GluttonyShard", 1L), null, 720, 5);
        GT_Values.RA.addFuel(GT_ModHandler.getModItem("ForbiddenMagic", "FMResource", 1L, 3), null, 720, 5);
        GT_Values.RA.addFuel(GT_ModHandler.getModItem("ForbiddenMagic", "NetherShard", 1L), null, 720, 5);
        GT_Values.RA.addFuel(GT_ModHandler.getModItem("ForbiddenMagic", "NetherShard", 1L, 1), null, 720, 5);
        GT_Values.RA.addFuel(GT_ModHandler.getModItem("ForbiddenMagic", "NetherShard", 1L, 2), null, 720, 5);
        GT_Values.RA.addFuel(GT_ModHandler.getModItem("ForbiddenMagic", "NetherShard", 1L, 3), null, 720, 5);
        GT_Values.RA.addFuel(GT_ModHandler.getModItem("ForbiddenMagic", "NetherShard", 1L, 4), null, 720, 5);
        GT_Values.RA.addFuel(GT_ModHandler.getModItem("ForbiddenMagic", "NetherShard", 1L, 5), null, 720, 5);
        GT_Values.RA.addFuel(GT_ModHandler.getModItem("ForbiddenMagic", "NetherShard", 1L, 6), null, 720, 5);
        GT_Values.RA.addFuel(GT_ModHandler.getModItem("TaintedMagic", "WarpedShard", 1L), null, 720, 5);
        GT_Values.RA.addFuel(GT_ModHandler.getModItem("TaintedMagic", "FluxShard", 1L), null, 720, 5);
        GT_Values.RA.addFuel(GT_ModHandler.getModItem("TaintedMagic", "EldritchShard", 1L), null, 720, 5);
        GT_Values.RA.addFuel(GT_ModHandler.getModItem("ThaumicTinkerer", "kamiResource", 1L, 6), null, 720, 5);
        GT_Values.RA.addFuel(GT_ModHandler.getModItem("ThaumicTinkerer", "kamiResource", 1L, 7), null, 720, 5);


        GT_Values.RA.addPulveriserRecipe(GT_OreDictUnificator.get(OrePrefixes.block, Materials.Marble, 1L), new ItemStack[]{GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Marble, 1L)}, null, 160, 4);

        for (int i = 0; i < 16; i++) {
            GT_Values.RA.addCutterRecipe(new ItemStack(Blocks.stained_glass, 3, i), new ItemStack(Blocks.stained_glass_pane, 8, i), GT_Values.NI, 50, 8);
        }
        for (byte i = 0; i < 16; i = (byte) (i + 1)) {
            GT_Values.RA.addCutterRecipe(new ItemStack(Blocks.wool, 1, i), new ItemStack(Blocks.carpet, 2, i), GT_Values.NI, 50, 8);
        }
        boolean loaded = Loader.isModLoaded(aTextForestry);
        ItemStack[] coverIDs = new ItemStack[]{
                ItemList.Plank_Larch.get(2L, new Object[0]),
                ItemList.Plank_Teak.get(2L, new Object[0]),
                ItemList.Plank_Acacia_Green.get(2L, new Object[0]),
                ItemList.Plank_Lime.get(2L, new Object[0]),
                ItemList.Plank_Chestnut.get(2L, new Object[0]),
                ItemList.Plank_Wenge.get(2L, new Object[0]),
                ItemList.Plank_Baobab.get(2L, new Object[0]),
                ItemList.Plank_Sequoia.get(2L, new Object[0]),
                ItemList.Plank_Kapok.get(2L, new Object[0]),
                ItemList.Plank_Ebony.get(2L, new Object[0]),
                ItemList.Plank_Mahagony.get(2L, new Object[0]),
                ItemList.Plank_Balsa.get(2L, new Object[0]),
                ItemList.Plank_Willow.get(2L, new Object[0]),
                ItemList.Plank_Walnut.get(2L, new Object[0]),
                ItemList.Plank_Greenheart.get(2L, new Object[0]),
                ItemList.Plank_Cherry.get(2L, new Object[0]),
                ItemList.Plank_Mahoe.get(2L, new Object[0]),
                ItemList.Plank_Poplar.get(2L, new Object[0]),
                ItemList.Plank_Palm.get(2L, new Object[0]),
                ItemList.Plank_Papaya.get(2L, new Object[0]),
                ItemList.Plank_Pine.get(2L, new Object[0]),
                ItemList.Plank_Plum.get(2L, new Object[0]),
                ItemList.Plank_Maple.get(2L, new Object[0]),
                ItemList.Plank_Citrus.get(2L, new Object[0])};
        int i = 0;
        for(ItemStack cover : coverIDs){
            if(loaded){
                ItemStack slabWood = GT_ModHandler.getModItem(aTextForestry, "slabs", 1, i);
                ItemStack slabWoodFireproof = GT_ModHandler.getModItem(aTextForestry, "slabsFireproof", 1, i);
                GT_ModHandler.addCraftingRecipe(cover, GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE, new Object[]{"s ", " P", 'P', slabWood});
                GT_ModHandler.addCraftingRecipe(cover, GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE, new Object[]{"s ", " P", 'P', slabWoodFireproof});
                GT_Values.RA.addCutterRecipe(slabWood, cover, null, 40, 8);
                GT_Values.RA.addCutterRecipe(slabWoodFireproof, cover, null, 40, 8);
            } else if (isNEILoaded) {
                API.hideItem(cover);
            }
            i++;
        }
        for(int g=0;g<16;g++){
            if(!isNEILoaded) {
                break;
            }
            API.hideItem(new ItemStack(GT_MetaGenerated_Item_03.INSTANCE,1,g));
        }

        if (!GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, "torchesFromCoal", false)) {
            GT_Values.RA.addAssemblerRecipe(GT_OreDictUnificator.get(OrePrefixes.stick, Materials.Wood, 1L), new ItemStack(Items.coal, 1, 32767), new ItemStack(Blocks.torch, 4), 400, 1);
        }
        for (ItemStack tRail : new ItemStack[]{ItemList.RC_Rail_Standard.get(6L, new Object[0]), ItemList.RC_Rail_Adv.get(6L, new Object[0]), ItemList.RC_Rail_Reinforced.get(6L, new Object[0]), ItemList.RC_Rail_Electric.get(6L, new Object[0]), ItemList.RC_Rail_HS.get(6L, new Object[0]), ItemList.RC_Rail_Wooden.get(6L, new Object[0])}) {
            for (ItemStack tBed : new ItemStack[]{ItemList.RC_Bed_Wood.get(1L, new Object[0]), ItemList.RC_Bed_Stone.get(1L, new Object[0])}) {
                GT_Values.RA.addAssemblerRecipe(tBed, tRail, GT_ModHandler.getRecipeOutput(new ItemStack[]{tRail, GT_Values.NI, tRail, tRail, tBed, tRail, tRail, GT_Values.NI, tRail}), 400, 4);
                GT_Values.RA.addAssemblerRecipe(tBed, tRail, Materials.Redstone.getMolten(144L), GT_ModHandler.getRecipeOutput(new ItemStack[]{tRail, GT_Values.NI, tRail, tRail, tBed, tRail, tRail, GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1L), tRail}), 400, 4);
                GT_Values.RA.addAssemblerRecipe(tBed, tRail, Materials.Redstone.getMolten(288L), GT_ModHandler.getRecipeOutput(new ItemStack[]{tRail, GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1L), tRail, tRail, tBed, tRail, tRail, GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1L), tRail}), 400, 4);
            }
        }
        GT_ModHandler.removeRecipe(new ItemStack[]{new ItemStack(Items.lava_bucket), ItemList.Cell_Empty.get(1L, new Object[0])});
        GT_ModHandler.removeRecipe(new ItemStack[]{new ItemStack(Items.water_bucket), ItemList.Cell_Empty.get(1L, new Object[0])});

        GT_ModHandler.removeFurnaceSmelting(ItemList.IC2_Resin.get(1L, new Object[0]));
        if(!GregTech_API.mIC2Classic) {
            GT_Values.RA.addMixerRecipe(GT_ModHandler.getIC2Item("biochaff", 1), GT_Values.NI, GT_Values.NI, GT_Values.NI, Materials.Water.getFluid(1000), new FluidStack(FluidRegistry.getFluid("ic2biomass"), 1000), GT_Values.NI, 400, 8);
            GT_Values.RA.addPyrolyseRecipe(GT_ModHandler.getIC2Item("biochaff", 1), Materials.Water.getFluid(1000), 1, null, new FluidStack(FluidRegistry.getFluid("ic2biomass"), 1500), 100, 10);
        }
        if (Loader.isModLoaded("Railcraft")) {
            GT_Values.RA.addPyrolyseRecipe(GT_OreDictUnificator.get(OrePrefixes.gem, Materials.Coal, 16), null, 1, RailcraftToolItems.getCoalCoke(16), Materials.Creosote.getFluid(8000), 640, 64);
            GT_Values.RA.addPyrolyseRecipe(GT_OreDictUnificator.get(OrePrefixes.gem, Materials.Coal, 16), Materials.Nitrogen.getGas(1000), 2, RailcraftToolItems.getCoalCoke(16), Materials.Creosote.getFluid(8000), 320, 96);
            GT_Values.RA.addPyrolyseRecipe(GT_OreDictUnificator.get(OrePrefixes.block, Materials.Coal, 8), null, 1, EnumCube.COKE_BLOCK.getItem(8), Materials.Creosote.getFluid(32000), 2560, 64);
            GT_Values.RA.addPyrolyseRecipe(GT_OreDictUnificator.get(OrePrefixes.block, Materials.Coal, 8), Materials.Nitrogen.getGas(1000), 2, EnumCube.COKE_BLOCK.getItem(8), Materials.Creosote.getFluid(32000), 1280, 96);
        }
        run2();

    }

    public void addProcess(ItemStack tCrop, Materials aMaterial, int chance, boolean aMainOutput) {
        if(tCrop==null||aMaterial==null||GT_OreDictUnificator.get(OrePrefixes.crushed, aMaterial,1)==null)return;
        if (GT_Mod.gregtechproxy.mNerfedCrops) {
            GT_Values.RA.addChemicalRecipe(GT_Utility.copyAmount(9, tCrop), GT_OreDictUnificator.get(OrePrefixes.crushed, aMaterial, 1), Materials.Water.getFluid(1000), aMaterial.mOreByProducts.isEmpty() ? null : aMaterial.mOreByProducts.get(0).getMolten(144), GT_OreDictUnificator.get(OrePrefixes.crushedPurified, aMaterial, 4), 96, 24);
            GT_Values.RA.addAutoclaveRecipe(GT_Utility.copyAmount(16, tCrop), Materials.UUMatter.getFluid(Math.max(1, ((aMaterial.getMass()+9)/10))), GT_OreDictUnificator.get(OrePrefixes.crushedPurified, aMaterial, 1), 10000, (int) (aMaterial.getMass() * 128), 384);
        } else {
            if (aMainOutput) GT_ModHandler.addExtractionRecipe(tCrop, GT_OreDictUnificator.get(OrePrefixes.dustTiny, aMaterial, 1));
        }
    }

    public void addProcess(ItemStack tCrop, Materials aMaterial, int chance){
        if(tCrop==null||aMaterial==null||GT_OreDictUnificator.get(OrePrefixes.crushed, aMaterial,1)==null)return;
        if (GT_Mod.gregtechproxy.mNerfedCrops) {
            GT_Values.RA.addChemicalRecipe(GT_Utility.copyAmount(9, tCrop), GT_OreDictUnificator.get(OrePrefixes.crushed, aMaterial, 1), Materials.Water.getFluid(1000), aMaterial.mOreByProducts.isEmpty() ? null : aMaterial.mOreByProducts.get(0).getMolten(144), GT_OreDictUnificator.get(OrePrefixes.crushedPurified, aMaterial, 4), 96, 24);
            GT_Values.RA.addAutoclaveRecipe(GT_Utility.copyAmount(16, tCrop), Materials.UUMatter.getFluid(Math.max(1, ((aMaterial.getMass()+9)/10))), GT_OreDictUnificator.get(OrePrefixes.crushedPurified, aMaterial, 1), 10000, (int) (aMaterial.getMass() * 128), 384);
        } else {
            GT_ModHandler.addExtractionRecipe(tCrop, GT_OreDictUnificator.get(OrePrefixes.dustTiny, aMaterial, 1));
        }
    }

    public void addProcess(ItemStack tCrop, Materials aMaterial, Materials aMaterialOut, int chance, boolean aMainOutput){
        if(tCrop==null||aMaterial==null||GT_OreDictUnificator.get(OrePrefixes.crushed, aMaterial,1)==null)return;
        if (GT_Mod.gregtechproxy.mNerfedCrops) {
            GT_Values.RA.addChemicalRecipe(GT_Utility.copyAmount(9, tCrop), GT_OreDictUnificator.get(OrePrefixes.crushed, aMaterial, 1), Materials.Water.getFluid(1000), aMaterialOut.mOreByProducts.isEmpty() ? null : aMaterialOut.mOreByProducts.get(0).getMolten(144), GT_OreDictUnificator.get(OrePrefixes.crushedPurified, aMaterialOut, 4), 96, 24);
            GT_Values.RA.addAutoclaveRecipe(GT_Utility.copyAmount(16, tCrop), Materials.UUMatter.getFluid(Math.max(1, ((aMaterial.getMass()+9)/10))), GT_OreDictUnificator.get(OrePrefixes.crushedPurified, aMaterial, 1), 10000, (int) (aMaterial.getMass() * 128), 384);
        } else {
            if (aMainOutput) GT_ModHandler.addExtractionRecipe(tCrop, GT_OreDictUnificator.get(OrePrefixes.dustTiny, aMaterial, 1));
        }
    }

    public void addProcess(ItemStack tCrop, Materials aMaterial, Materials aMaterialOut, int chance){
        if(tCrop==null||aMaterial==null||GT_OreDictUnificator.get(OrePrefixes.crushed, aMaterial,1)==null)return;
        if (GT_Mod.gregtechproxy.mNerfedCrops) {
            GT_Values.RA.addChemicalRecipe(GT_Utility.copyAmount(9, tCrop), GT_OreDictUnificator.get(OrePrefixes.crushed, aMaterial, 1), Materials.Water.getFluid(1000), aMaterialOut.mOreByProducts.isEmpty() ? null : aMaterialOut.mOreByProducts.get(0).getMolten(144), GT_OreDictUnificator.get(OrePrefixes.crushedPurified, aMaterialOut, 4), 96, 24);
            GT_Values.RA.addAutoclaveRecipe(GT_Utility.copyAmount(16, tCrop), Materials.UUMatter.getFluid(Math.max(1, ((aMaterial.getMass()+9)/10))), GT_OreDictUnificator.get(OrePrefixes.crushedPurified, aMaterial, 1), 10000, (int) (aMaterial.getMass() * 128), 384);
        } else {
            GT_ModHandler.addExtractionRecipe(tCrop, GT_OreDictUnificator.get(OrePrefixes.dustTiny, aMaterial, 1));
        }
    }

    private void run2(){
        if(GT_Mod.gregtechproxy.mHardRadonRecipe)
            GT_Values.RA.addChemicalRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot,Materials.Plutonium, 6), null, null, Materials.Radon.getGas(2), GT_OreDictUnificator.get(OrePrefixes.dust,Materials.Plutonium, 6), 12000, 8);
        else
            GT_Values.RA.addChemicalRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot,Materials.Plutonium, 6), null, null, Materials.Radon.getGas(100), GT_OreDictUnificator.get(OrePrefixes.dust,Materials.Plutonium, 6), 12000, 8);

        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.golden_apple, 1, 1), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(4608L), new ItemStack(Items.gold_ingot, 64), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 9216, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.golden_apple, 1, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), new ItemStack(Items.gold_ingot, 7), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 9216, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.golden_carrot, 1, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), new ItemStack(Items.gold_nugget, 6), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 9216, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.speckled_melon, 1, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), new ItemStack(Items.gold_nugget, 6), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 9216, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.mushroom_stew, 16, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), new ItemStack(Items.bowl, 16, 0), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.apple, 32, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.bread, 64, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.porkchop, 12, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.cooked_porkchop, 16, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.beef, 12, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.cooked_beef, 16, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.fish, 12, 32767), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.cooked_fished, 16, 32767), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.chicken, 12, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.cooked_chicken, 16, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.melon, 64, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Blocks.pumpkin, 16, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.rotten_flesh, 16, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.spider_eye, 32, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.carrot, 16, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(ItemList.Food_Raw_Potato.get(16L, new Object[0]), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(ItemList.Food_Poisonous_Potato.get(12L, new Object[0]), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(ItemList.Food_Baked_Potato.get(24L, new Object[0]), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.cookie, 64, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.cake, 8, 0), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Blocks.brown_mushroom_block, 12, 32767), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Blocks.red_mushroom_block, 12, 32767), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Blocks.brown_mushroom, 32, 32767), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Blocks.red_mushroom, 32, 32767), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.nether_wart, 32, 32767), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(GT_ModHandler.getIC2Item("terraWart", 16L), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(GT_ModHandler.getModItem("TwilightForest", "item.meefRaw", 12L, 32767), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(GT_ModHandler.getModItem("TwilightForest", "item.meefSteak", 16L, 32767), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(GT_ModHandler.getModItem("TwilightForest", "item.venisonRaw", 12L, 32767), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addCentrifugeRecipe(GT_ModHandler.getModItem("TwilightForest", "item.venisonCooked", 16L, 32767), GT_Values.NI, GT_Values.NF, Materials.Methane.getGas(576L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 4608, 5);
        GT_Values.RA.addAssemblerRecipe(new ItemStack[]{GT_OreDictUnificator.get(OrePrefixes.plate,Materials.HSSG,4L),GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Europium,2L), ItemList.Casing_RobustTungstenSteel.get(1L,new Object[0])},GT_Values.NF,ItemList.Casing_RobustHSSG.get(2L,new Object[0]),50,16);

        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Blocks.sand, 1, 1), GT_Values.NI, GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Iron, 1L), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Diamond, 1L), new ItemStack(Blocks.sand, 1), GT_Values.NI, GT_Values.NI, GT_Values.NI, new int[]{5000, 100, 5000}, 50, 30);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Blocks.dirt, 1, 32767), GT_Values.NI, GT_Values.NF, GT_Values.NF, ItemList.IC2_Plantball.get(1L, new Object[0]), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Clay, 1L), new ItemStack(Blocks.sand, 1), GT_Values.NI, GT_Values.NI, GT_Values.NI, new int[]{1250, 5000, 5000}, 250, 30);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Blocks.grass, 1, 32767), GT_Values.NI, GT_Values.NF, GT_Values.NF, ItemList.IC2_Plantball.get(1L, new Object[0]), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Clay, 1L), new ItemStack(Blocks.sand, 1), GT_Values.NI, GT_Values.NI, GT_Values.NI, new int[]{2500, 5000, 5000}, 250, 30);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Blocks.mycelium, 1, 32767), GT_Values.NI, GT_Values.NF, GT_Values.NF, new ItemStack(Blocks.brown_mushroom, 1), new ItemStack(Blocks.red_mushroom, 1), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Clay, 1L), new ItemStack(Blocks.sand, 1), GT_Values.NI, GT_Values.NI, new int[]{2500, 2500, 5000, 5000}, 650, 30);
        GT_Values.RA.addCentrifugeRecipe(ItemList.IC2_Resin.get(1L, new Object[0]), GT_Values.NI, GT_Values.NF, Materials.Glue.getFluid(100L), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.RawRubber, 3L), ItemList.IC2_Plantball.get(1L, new Object[0]), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, new int[]{10000, 1000}, 300, 5);
        GT_Values.RA.addCentrifugeRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.DarkAsh, 1), 0, GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Ash, 1L), ItemList.TE_Slag.get(1L, new Object[]{GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Carbon, 1L)}), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, 250);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Items.magma_cream, 1), 0, new ItemStack(Items.blaze_powder, 1), new ItemStack(Items.slime_ball, 1), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, 500);
        GT_Values.RA.addCentrifugeRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Uranium, 1L), GT_Values.NI, GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Uranium235, 1L), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Plutonium, 1L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, new int[]{2000, 200}, 800, 320);
        GT_Values.RA.addCentrifugeRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Plutonium, 1L), GT_Values.NI, GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Plutonium241, 1L), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Uranium, 1L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, new int[]{2000, 3000}, 1600, 320);
        GT_Values.RA.addCentrifugeRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Naquadah, 1L), GT_Values.NI, GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.NaquadahEnriched, 1L), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Naquadria, 1L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, new int[]{5000, 1000}, 3200, 320);
        GT_Values.RA.addCentrifugeRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.NaquadahEnriched, 1L), GT_Values.NI, GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Naquadria, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Naquadah, 1L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, new int[]{2000, 3000}, 6400, 640);
        GT_Values.RA.addCentrifugeRecipe(GT_Values.NI, GT_Values.NI, Materials.Hydrogen.getGas(160L), Materials.Deuterium.getGas(40L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 160, 20);
        GT_Values.RA.addCentrifugeRecipe(GT_Values.NI, GT_Values.NI, Materials.Deuterium.getGas(160L), Materials.Tritium.getGas(40L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 160, 80);
        GT_Values.RA.addCentrifugeRecipe(GT_Values.NI, GT_Values.NI, Materials.Helium.getGas(80L), Materials.Helium_3.getGas(5L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 160, 80);
        GT_Values.RA.addCentrifugeRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 1L), GT_Values.NI, GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Redstone, 2L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Gold, 2L), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, null, 488, 80);
        GT_Values.RA.addCentrifugeRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Endstone, 1L), GT_Values.NI, GT_Values.NF, Materials.Helium.getGas(120L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Tungstate, 1L), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Platinum, 1L), new ItemStack(Blocks.sand, 1), GT_Values.NI, GT_Values.NI, GT_Values.NI, new int[]{1250, 625, 9000, 0, 0, 0}, 320, 20);
        GT_Values.RA.addCentrifugeRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Netherrack, 1L), GT_Values.NI, GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Redstone, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Sulfur, 1L), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Coal, 1L), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Gold, 1L), GT_Values.NI, GT_Values.NI, new int[]{5625, 9900, 5625, 625, 0, 0}, 160, 20);
        GT_Values.RA.addCentrifugeRecipe(new ItemStack(Blocks.soul_sand, 1), GT_Values.NI, GT_Values.NF, Materials.Oil.getFluid(80L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Saltpeter, 1L), GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Coal, 1L), new ItemStack(Blocks.sand, 1), GT_Values.NI, GT_Values.NI, GT_Values.NI, new int[]{8000, 2000, 9000, 0, 0, 0}, 200, 80);
        GT_Values.RA.addCentrifugeRecipe(GT_Values.NI, GT_Values.NI, Materials.Lava.getFluid(100L), GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Copper, 1L), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Tin, 1L), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Gold, 1L), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Silver, 1L), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Tantalum, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Tungstate, 1L), new int[]{2000, 1000, 250, 250, 250, 250}, 80, 80);
        GT_Values.RA.addCentrifugeRecipe(GT_Values.NI, GT_Values.NI, FluidRegistry.getFluidStack("ic2pahoehoelava", 100), GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Copper, 1L), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Tin, 1L), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Gold, 1L), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Silver, 1L), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Tantalum, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Tungstate, 1L), new int[]{2000, 1000, 250, 250, 250, 250}, 40, 80);

        GT_Values.RA.addCentrifugeRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.RareEarth, 1L), GT_Values.NI, GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Neodymium, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Yttrium, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Lanthanum, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Cerium, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Cadmium, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Caesium, 1L), new int[]{2500, 2500, 2500, 2500, 2500, 2500}, 64, 20);
        GT_Values.RA.addCentrifugeRecipe(GT_ModHandler.getModItem(aTextAE, aTextAEMM, 1L, 45), GT_Values.NI, GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.BasalticMineralSand, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Olivine, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Obsidian, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Basalt, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Flint, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.RareEarth, 1L), new int[]{2000, 2000, 2000, 2000, 2000, 2000}, 64, 20);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Electric_Motor_IV.get(1, new Object(){}),144000,new ItemStack[]{
                GT_OreDictUnificator.get(OrePrefixes.stickLong, Materials.NeodymiumMagnetic, 1L),
                GT_OreDictUnificator.get(OrePrefixes.stickLong, Materials.HSSG, 2L),
                GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.AnnealedCopper, 64L),
                GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.AnnealedCopper, 64L),
                GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.AnnealedCopper, 64L),
                GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.AnnealedCopper, 64L),
                GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.YttriumBariumCuprate, 2L)}, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(144),
                Materials.Lubricant.getFluid(250)}, ItemList.Electric_Motor_LuV.get(1, new Object[]{}), 600, 6000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Electric_Motor_LuV.get(1, new Object(){}),144000,new ItemStack[]{
                GT_OreDictUnificator.get(OrePrefixes.stickLong, Materials.NeodymiumMagnetic, 2L),
                GT_OreDictUnificator.get(OrePrefixes.stickLong, Materials.HSSE, 4L),
                GT_OreDictUnificator.get(OrePrefixes.ring, Materials.HSSE, 4L),
                GT_OreDictUnificator.get(OrePrefixes.round, Materials.HSSE, 16L),
                GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Platinum, 64L),
                GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Platinum, 64L),
                GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Platinum, 64L),
                GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Platinum, 64L),
                GT_OreDictUnificator.get(OrePrefixes.cableGt04, Materials.VanadiumGallium, 2L)}, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(288),
                Materials.Lubricant.getFluid(750)}, ItemList.Electric_Motor_ZPM.get(1, new Object[]{}), 600, 24000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Electric_Motor_ZPM.get(1, new Object(){}),288000,new ItemStack[]{
                GT_OreDictUnificator.get(OrePrefixes.block, Materials.NeodymiumMagnetic, 1L),
                GT_OreDictUnificator.get(OrePrefixes.stickLong, Materials.Neutronium, 4L),
                GT_OreDictUnificator.get(OrePrefixes.ring, Materials.Neutronium, 4L),
                GT_OreDictUnificator.get(OrePrefixes.round, Materials.Neutronium, 16),
                GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.Superconductor, 64L),
                GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.Superconductor, 64L),
                GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.Superconductor, 64L),
                GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.Superconductor, 64L),
                GT_OreDictUnificator.get(OrePrefixes.cableGt04, Materials.NiobiumTitanium, 2L)}, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(1296),
                Materials.Lubricant.getFluid(2000)}, ItemList.Electric_Motor_UV.get(1, new Object[]{}), 600, 100000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Electric_Pump_IV.get(1, new Object(){}),144000,new ItemStack[]{
                ItemList.Electric_Motor_LuV.get(1, new Object(){}),
                GT_OreDictUnificator.get(OrePrefixes.pipeSmall, Materials.Ultimate, 2L),
                GT_OreDictUnificator.get(OrePrefixes.plate, Materials.HSSG, 2L),
                GT_OreDictUnificator.get(OrePrefixes.screw, Materials.HSSG, 8L),
                GT_OreDictUnificator.get(OrePrefixes.ring, Materials.Silicone, 4L),
                GT_OreDictUnificator.get(OrePrefixes.rotor, Materials.HSSG, 2L),
                GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.YttriumBariumCuprate, 2L)}, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(144),
                Materials.Lubricant.getFluid(250)}, ItemList.Electric_Pump_LuV.get(1, new Object[]{}), 600, 6000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Electric_Pump_LuV.get(1, new Object(){}),144000,new ItemStack[]{
                ItemList.Electric_Motor_ZPM.get(1, new Object(){}),
                GT_OreDictUnificator.get(OrePrefixes.pipeMedium, Materials.Ultimate, 2L),
                GT_OreDictUnificator.get(OrePrefixes.plate, Materials.HSSE, 2L),
                GT_OreDictUnificator.get(OrePrefixes.screw, Materials.HSSE, 8L),
                GT_OreDictUnificator.get(OrePrefixes.ring, Materials.Silicone, 16L),
                GT_OreDictUnificator.get(OrePrefixes.rotor, Materials.HSSE, 2L),
                GT_OreDictUnificator.get(OrePrefixes.cableGt04, Materials.VanadiumGallium, 2L)}, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(288),
                Materials.Lubricant.getFluid(750)}, ItemList.Electric_Pump_ZPM.get(1, new Object[]{}), 600, 24000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Electric_Pump_ZPM.get(1, new Object(){}),288000,new ItemStack[]{
                ItemList.Electric_Motor_UV.get(1, new Object(){}),
                GT_OreDictUnificator.get(OrePrefixes.pipeLarge, Materials.Ultimate, 2L),
                GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Neutronium, 2L),
                GT_OreDictUnificator.get(OrePrefixes.screw, Materials.Neutronium, 8L),
                GT_OreDictUnificator.get(OrePrefixes.ring, Materials.Silicone, 16L),
                GT_OreDictUnificator.get(OrePrefixes.rotor, Materials.Neutronium, 2L),
                GT_OreDictUnificator.get(OrePrefixes.cableGt04, Materials.NiobiumTitanium, 2L)}, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(1296),
                Materials.Lubricant.getFluid(2000)}, ItemList.Electric_Pump_UV.get(1, new Object[]{}), 600, 100000);

//        Conveyor

        GT_Values.RA.addAssemblylineRecipe(ItemList.Conveyor_Module_IV.get(1, new Object(){}),144000,new ItemStack[]{
                ItemList.Electric_Motor_LuV.get(2, new Object(){}),
                GT_OreDictUnificator.get(OrePrefixes.plate, Materials.HSSG, 2L),
                GT_OreDictUnificator.get(OrePrefixes.ring, Materials.HSSG, 4L),
                GT_OreDictUnificator.get(OrePrefixes.round, Materials.HSSG, 32L),
                GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.YttriumBariumCuprate, 2L)}, new FluidStack[]{
                Materials.StyreneButadieneRubber.getMolten(1440),
                Materials.Lubricant.getFluid(250)}, ItemList.Conveyor_Module_LuV.get(1, new Object[]{}), 600, 6000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Conveyor_Module_LuV.get(1, new Object(){}),144000,new ItemStack[]{
                ItemList.Electric_Motor_ZPM.get(2, new Object(){}),
                GT_OreDictUnificator.get(OrePrefixes.plate, Materials.HSSE, 2L),
                GT_OreDictUnificator.get(OrePrefixes.ring, Materials.HSSE, 4L),
                GT_OreDictUnificator.get(OrePrefixes.round, Materials.HSSE, 32L),
                GT_OreDictUnificator.get(OrePrefixes.cableGt04, Materials.VanadiumGallium, 2L)}, new FluidStack[]{
                Materials.StyreneButadieneRubber.getMolten(2880),
                Materials.Lubricant.getFluid(750)}, ItemList.Conveyor_Module_ZPM.get(1, new Object[]{}), 600, 24000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Conveyor_Module_ZPM.get(1, new Object(){}),288000,new ItemStack[]{
                ItemList.Electric_Motor_UV.get(2, new Object(){}),
                GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Neutronium, 2L),
                GT_OreDictUnificator.get(OrePrefixes.ring, Materials.Neutronium, 4L),
                GT_OreDictUnificator.get(OrePrefixes.round, Materials.Neutronium, 32L),
                GT_OreDictUnificator.get(OrePrefixes.cableGt04, Materials.NiobiumTitanium, 2L)}, new FluidStack[]{
                Materials.StyreneButadieneRubber.getMolten(2880),
                Materials.Lubricant.getFluid(2000)}, ItemList.Conveyor_Module_UV.get(1, new Object[]{}), 600, 100000);

//        Piston

        GT_Values.RA.addAssemblylineRecipe(ItemList.Electric_Piston_IV.get(1, new Object(){}),144000,new ItemStack[]{
                ItemList.Electric_Motor_LuV.get(1, new Object(){}),
                GT_OreDictUnificator.get(OrePrefixes.plate, Materials.HSSG, 6L),
                GT_OreDictUnificator.get(OrePrefixes.ring, Materials.HSSG, 4L),
                GT_OreDictUnificator.get(OrePrefixes.round, Materials.HSSG, 32L),
                GT_OreDictUnificator.get(OrePrefixes.stick, Materials.HSSG, 4L),
                GT_OreDictUnificator.get(OrePrefixes.gear, Materials.HSSG, 1L),
                GT_OreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.HSSG, 2L),
                GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.YttriumBariumCuprate, 4L)}, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(144),
                Materials.Lubricant.getFluid(250)}, ItemList.Electric_Piston_LuV.get(1, new Object[]{}), 600, 6000);


        GT_Values.RA.addAssemblylineRecipe(ItemList.Electric_Piston_LuV.get(1, new Object(){}),144000,new ItemStack[]{
                ItemList.Electric_Motor_ZPM.get(1, new Object(){}),
                GT_OreDictUnificator.get(OrePrefixes.plate, Materials.HSSE, 6L),
                GT_OreDictUnificator.get(OrePrefixes.ring, Materials.HSSE, 4L),
                GT_OreDictUnificator.get(OrePrefixes.round, Materials.HSSE, 32L),
                GT_OreDictUnificator.get(OrePrefixes.stick, Materials.HSSE, 4L),
                GT_OreDictUnificator.get(OrePrefixes.gear, Materials.HSSE, 1L),
                GT_OreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.HSSE, 2L),
                GT_OreDictUnificator.get(OrePrefixes.cableGt04, Materials.VanadiumGallium, 4L)}, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(288),
                Materials.Lubricant.getFluid(750)}, ItemList.Electric_Piston_ZPM.get(1, new Object[]{}), 600, 24000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Electric_Piston_ZPM.get(1, new Object(){}),288000,new ItemStack[]{
                ItemList.Electric_Motor_UV.get(1, new Object(){}),
                GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Neutronium, 6L),
                GT_OreDictUnificator.get(OrePrefixes.ring, Materials.Neutronium, 4L),
                GT_OreDictUnificator.get(OrePrefixes.round, Materials.Neutronium, 32L),
                GT_OreDictUnificator.get(OrePrefixes.stick, Materials.Neutronium, 4L),
                GT_OreDictUnificator.get(OrePrefixes.gear, Materials.Neutronium, 1L),
                GT_OreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.Neutronium, 2L),
                GT_OreDictUnificator.get(OrePrefixes.cableGt04, Materials.NiobiumTitanium, 4L)}, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(1296),
                Materials.Lubricant.getFluid(2000)}, ItemList.Electric_Piston_UV.get(1, new Object[]{}), 600, 100000);

//        RobotArm

        Object o = new Object[0];
        GT_Values.RA.addAssemblylineRecipe(ItemList.Robot_Arm_IV.get(1, new Object(){}),144000,new Object[]{
                GT_OreDictUnificator.get(OrePrefixes.stickLong, Materials.HSSG, 4L),
                GT_OreDictUnificator.get(OrePrefixes.gear, Materials.HSSG, 1L),
                GT_OreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.HSSG, 3L),
                ItemList.Electric_Motor_LuV.get(2, new Object(){}),
                ItemList.Electric_Piston_LuV.get(1, new Object(){}),
                new Object[]{OrePrefixes.circuit.get(Materials.Master), 2},
                new Object[]{OrePrefixes.circuit.get(Materials.Elite), 2},
                new Object[]{OrePrefixes.circuit.get(Materials.Data), 6},
                GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.YttriumBariumCuprate, 6L)}, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(576),
                Materials.Lubricant.getFluid(250)}, ItemList.Robot_Arm_LuV.get(1, new Object[]{}), 600, 6000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Robot_Arm_LuV.get(1, new Object(){}),144000,new Object[]{
                GT_OreDictUnificator.get(OrePrefixes.stickLong, Materials.HSSE, 4L),
                GT_OreDictUnificator.get(OrePrefixes.gear, Materials.HSSE, 1L),
                GT_OreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.HSSE, 3L),
                ItemList.Electric_Motor_ZPM.get(2, new Object(){}),
                ItemList.Electric_Piston_ZPM.get(1, new Object(){}),
                new Object[]{OrePrefixes.circuit.get(Materials.Master), 4},
                new Object[]{OrePrefixes.circuit.get(Materials.Elite), 4},
                new Object[]{OrePrefixes.circuit.get(Materials.Data), 12},
                GT_OreDictUnificator.get(OrePrefixes.cableGt04, Materials.VanadiumGallium, 6L)}, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(1152),
                Materials.Lubricant.getFluid(750)}, ItemList.Robot_Arm_ZPM.get(1, new Object[]{}), 600, 24000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Robot_Arm_ZPM.get(1, new Object(){}),288000,new Object[]{
                GT_OreDictUnificator.get(OrePrefixes.stickLong, Materials.Neutronium, 4L),
                GT_OreDictUnificator.get(OrePrefixes.gear, Materials.Neutronium, 1L),
                GT_OreDictUnificator.get(OrePrefixes.gearGtSmall, Materials.Neutronium, 3L),
                ItemList.Electric_Motor_UV.get(2, new Object(){}),
                ItemList.Electric_Piston_UV.get(1, new Object(){}),
                new Object[]{OrePrefixes.circuit.get(Materials.Master), 8},
                new Object[]{OrePrefixes.circuit.get(Materials.Elite), 8},
                new Object[]{OrePrefixes.circuit.get(Materials.Data), 24},
                GT_OreDictUnificator.get(OrePrefixes.cableGt04, Materials.NiobiumTitanium, 6L)}, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(2304),
                Materials.Lubricant.getFluid(2000)}, ItemList.Robot_Arm_UV.get(1, new Object[]{}), 600, 100000);


//        Emitter

        GT_Values.RA.addAssemblylineRecipe(ItemList.Emitter_IV.get(1, new Object(){}),144000,new Object[]{
                        GT_OreDictUnificator.get(OrePrefixes.frameGt, Materials.HSSG, 1L),
                        ItemList.Emitter_IV.get(1, new Object(){}),
                        ItemList.Emitter_EV.get(2, new Object(){}),
                        ItemList.Emitter_HV.get(4, new Object(){}),
                        new Object[]{OrePrefixes.circuit.get(Materials.Data), 7},
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Electrum, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Electrum, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Electrum, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.YttriumBariumCuprate, 7L)}, new FluidStack[]{
                        Materials.SolderingAlloy.getMolten(576)},
                ItemList.Emitter_LuV.get(1, new Object[]{}), 600, 6000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Emitter_LuV.get(1, new Object(){}),144000,new Object[]{
                        GT_OreDictUnificator.get(OrePrefixes.frameGt, Materials.HSSE, 1L),
                        ItemList.Emitter_LuV.get(1, new Object(){}),
                        ItemList.Emitter_IV.get(2, new Object(){}),
                        ItemList.Emitter_EV.get(4, new Object(){}),
                        new Object[]{OrePrefixes.circuit.get(Materials.Elite), 7},
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Platinum, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Platinum, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Platinum, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.cableGt04, Materials.VanadiumGallium, 7L)}, new FluidStack[]{
                        Materials.SolderingAlloy.getMolten(576)},
                ItemList.Emitter_ZPM.get(1, new Object[]{}), 600, 24000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Emitter_ZPM.get(1, new Object(){}),288000,new Object[]{
                        GT_OreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 1L),
                        ItemList.Emitter_ZPM.get(1, new Object(){}),
                        ItemList.Emitter_LuV.get(2, new Object(){}),
                        ItemList.Emitter_IV.get(4, new Object(){}),
                        new Object[]{OrePrefixes.circuit.get(Materials.Master), 7},
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Osmiridium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Osmiridium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Osmiridium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.cableGt04, Materials.NiobiumTitanium, 7L)}, new FluidStack[]{
                        Materials.SolderingAlloy.getMolten(576)},
                ItemList.Emitter_UV.get(1, new Object[]{}), 600, 100000);

//        Sensor

        GT_Values.RA.addAssemblylineRecipe(ItemList.Sensor_IV.get(1, new Object(){}),144000,new Object[]{
                        GT_OreDictUnificator.get(OrePrefixes.frameGt, Materials.HSSG, 1L),
                        ItemList.Sensor_IV.get(1, new Object(){}),
                        ItemList.Sensor_EV.get(2, new Object(){}),
                        ItemList.Sensor_HV.get(4, new Object(){}),
                        new Object[]{OrePrefixes.circuit.get(Materials.Data), 7},
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Electrum, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Electrum, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Electrum, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.YttriumBariumCuprate, 7L)}, new FluidStack[]{
                        Materials.SolderingAlloy.getMolten(576)},
                ItemList.Sensor_LuV.get(1, new Object[]{}), 600, 6000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Sensor_LuV.get(1, new Object(){}),144000,new Object[]{
                        GT_OreDictUnificator.get(OrePrefixes.frameGt, Materials.HSSE, 1L),
                        ItemList.Sensor_LuV.get(1, new Object(){}),
                        ItemList.Sensor_IV.get(2, new Object(){}),
                        ItemList.Sensor_EV.get(4, new Object(){}),
                        new Object[]{OrePrefixes.circuit.get(Materials.Elite), 7},
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Platinum, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Platinum, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Platinum, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.cableGt04, Materials.VanadiumGallium, 7L)}, new FluidStack[]{
                        Materials.SolderingAlloy.getMolten(576)},
                ItemList.Sensor_ZPM.get(1, new Object[]{}), 600, 24000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Sensor_ZPM.get(1, new Object(){}),288000,new Object[]{
                        GT_OreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 1L),
                        ItemList.Sensor_ZPM.get(1, new Object(){}),
                        ItemList.Sensor_LuV.get(2, new Object(){}),
                        ItemList.Sensor_IV.get(4, new Object(){}),
                        new Object[]{OrePrefixes.circuit.get(Materials.Master), 7},
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Osmiridium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Osmiridium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Osmiridium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.cableGt04, Materials.NiobiumTitanium, 7L)}, new FluidStack[]{
                        Materials.SolderingAlloy.getMolten(576)},
                ItemList.Sensor_UV.get(1, new Object[]{}), 600, 100000);

//        Field Generator

        GT_Values.RA.addAssemblylineRecipe(ItemList.Field_Generator_IV.get(1, new Object(){}),144000,new Object[]{
                        GT_OreDictUnificator.get(OrePrefixes.frameGt, Materials.HSSG, 1L),
                        GT_OreDictUnificator.get(OrePrefixes.plate, Materials.HSSG, 6L),
                        ItemList.QuantumStar.get(1, new Object(){}),
                        ItemList.Emitter_LuV.get(4, new Object(){}),
                        new ItemStack[]{ItemList.Circuit_Masterquantumcomputer.get(8, o), ItemList.Circuit_Crystalcomputer.get(8, o), ItemList.Circuit_Neuroprocessor.get(8, o)},
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.YttriumBariumCuprate, 8L)}, new FluidStack[]{
                        Materials.SolderingAlloy.getMolten(576)},
                ItemList.Field_Generator_LuV.get(1, new Object[]{}), 600, 6000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Field_Generator_LuV.get(1, new Object(){}),144000,new Object[]{
                        GT_OreDictUnificator.get(OrePrefixes.frameGt, Materials.HSSE, 1L),
                        GT_OreDictUnificator.get(OrePrefixes.plate, Materials.HSSE, 6L),
                        ItemList.QuantumStar.get(4, new Object(){}),
                        ItemList.Emitter_ZPM.get(4, new Object(){}),
                        new ItemStack[]{ItemList.Circuit_Crystalcomputer.get(16, o), ItemList.Circuit_Neuroprocessor.get(16, o)},
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.cableGt04, Materials.VanadiumGallium, 8L)}, new FluidStack[]{
                        Materials.SolderingAlloy.getMolten(1152)},
                ItemList.Field_Generator_ZPM.get(1, new Object[]{}), 600, 24000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Field_Generator_ZPM.get(1, new Object(){}),288000,new ItemStack[]{
                        GT_OreDictUnificator.get(OrePrefixes.frameGt, Materials.Neutronium, 1L),
                        GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Neutronium, 6L),
                        ItemList.Gravistar.get(1, new Object(){}),
                        ItemList.Emitter_UV.get(4, new Object(){}),
                        ItemList.Circuit_Neuroprocessor.get(64, o),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Osmium, 64L),
                        GT_OreDictUnificator.get(OrePrefixes.cableGt04, Materials.NiobiumTitanium, 8L)}, new FluidStack[]{
                        Materials.SolderingAlloy.getMolten(2304)},
                ItemList.Field_Generator_UV.get(1, new Object[]{}), 600, 100000);

//        Quantumsuite
        GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item("quantumHelmet", 1L));
        GT_Values.RA.addAssemblylineRecipe(GT_ModHandler.getIC2Item("nanoHelmet", 1L, GT_Values.W), 144000, new Object[]{
                GT_ModHandler.getIC2Item("nanoHelmet", 1L, GT_Values.W),
                new Object[]{OrePrefixes.circuit.get(Materials.Master), 2},
                GT_OreDictUnificator.get(OrePrefixes.plateAlloy, Materials.Iridium, 4),
                ItemList.Energy_LapotronicOrb.get(1, new Object[]{}),
                ItemList.Sensor_IV.get(1, new Object[]{}),
                ItemList.Field_Generator_EV.get(1, new Object[]{}),
                GT_OreDictUnificator.get(OrePrefixes.screw, Materials.Tungsten, 4)
        }, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(2304),
                Materials.Titanium.getMolten(1440)
        }, GT_ModHandler.getIC2Item("quantumHelmet", 1L), 1500, 4096);

        GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item("quantumBodyarmor", 1L));
        GT_Values.RA.addAssemblylineRecipe(Loader.isModLoaded("GraviSuite") ? GT_ModHandler.getModItem("GraviSuite", "advNanoChestPlate", 1, GT_Values.W) : GT_ModHandler.getIC2Item("nanoBodyarmor", 1L, GT_Values.W), 144000, new Object[]{
                Loader.isModLoaded("GraviSuite") ? GT_ModHandler.getModItem("GraviSuite", "advNanoChestPlate", 1, GT_Values.W) : GT_ModHandler.getIC2Item("nanoBodyarmor", 1L, GT_Values.W),
                new Object[]{OrePrefixes.circuit.get(Materials.Master), 2},
                GT_OreDictUnificator.get(OrePrefixes.plateAlloy, Materials.Iridium, 6),
                ItemList.Energy_LapotronicOrb.get(1, new Object[]{}),
                ItemList.Field_Generator_HV.get(2, new Object[]{}),
                ItemList.Electric_Motor_IV.get(2, new Object[]{}),
                GT_OreDictUnificator.get(OrePrefixes.screw, Materials.Tungsten, 4)
        }, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(2304),
                Materials.Titanium.getMolten(1440)
        }, GT_ModHandler.getIC2Item("quantumBodyarmor", 1L), 1500, 4096);

        GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item("quantumLeggings", 1L));
        GT_Values.RA.addAssemblylineRecipe(GT_ModHandler.getIC2Item("nanoLeggings", 1L, GT_Values.W), 144000, new Object[]{
                GT_ModHandler.getIC2Item("nanoLeggings", 1L, GT_Values.W),
                new Object[]{OrePrefixes.circuit.get(Materials.Master), 2},
                GT_OreDictUnificator.get(OrePrefixes.plateAlloy, Materials.Iridium, 6),
                ItemList.Energy_LapotronicOrb.get(1, new Object[]{}),
                ItemList.Field_Generator_HV.get(2, new Object[]{}),
                ItemList.Electric_Motor_IV.get(4, new Object[]{}),
                GT_OreDictUnificator.get(OrePrefixes.screw, Materials.Tungsten, 4)
        }, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(2304),
                Materials.Titanium.getMolten(1440)
        }, GT_ModHandler.getIC2Item("quantumLeggings", 1L), 1500, 4096);

        GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item("quantumBoots", 1L));
        GT_Values.RA.addAssemblylineRecipe(GT_ModHandler.getIC2Item("nanoBoots", 1L, GT_Values.W), 144000, new Object[]{
                GT_ModHandler.getIC2Item("nanoBoots", 1L, GT_Values.W),
                new Object[]{OrePrefixes.circuit.get(Materials.Master), 2},
                GT_OreDictUnificator.get(OrePrefixes.plateAlloy, Materials.Iridium, 4),
                ItemList.Energy_LapotronicOrb.get(1, new Object[]{}),
                ItemList.Field_Generator_HV.get(1, new Object[]{}),
                ItemList.Electric_Piston_IV.get(2, new Object[]{}),
                GT_OreDictUnificator.get(OrePrefixes.screw, Materials.Tungsten, 4)
        }, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(2304),
                Materials.Titanium.getMolten(1440)
        }, GT_ModHandler.getIC2Item("quantumBoots", 1L), 1500, 4096);

        if(Loader.isModLoaded("GraviSuite")){
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getModItem("GraviSuite", "graviChestPlate", 1, GT_Values.W));
            GT_Values.RA.addAssemblylineRecipe(GT_ModHandler.getIC2Item("quantumBodyarmor", 1L, GT_Values.W), 144000, new Object[]{
                    GT_ModHandler.getIC2Item("quantumBodyarmor", 1L, GT_Values.W),
                    GT_ModHandler.getModItem("GraviSuite", "ultimateLappack", 1, GT_Values.W),
                    new Object[]{OrePrefixes.circuit.get(Materials.Ultimate), 2},
                    GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Duranium, 6),
                    ItemList.Energy_LapotronicOrb2.get(1, new Object[]{}),
                    ItemList.Field_Generator_IV.get(2, new Object[]{}),
                    GT_ModHandler.getModItem("GraviSuite", "itemSimpleItem", 4, 3),
                    ItemList.Electric_Motor_ZPM.get(2, new Object[]{}),
                    GT_OreDictUnificator.get(OrePrefixes.wireGt02, Materials.Superconductor, 32),
                    GT_OreDictUnificator.get(OrePrefixes.screw, Materials.Duranium, 4)
            }, new FluidStack[]{
                    Materials.SolderingAlloy.getMolten(2304),
                    Materials.Tritanium.getMolten(1440)
            }, GT_ModHandler.getModItem("GraviSuite", "graviChestPlate", 1, 27), 1500, 16388);
        }

        GT_Values.RA.addAssemblylineRecipe(ItemList.Circuit_Crystalmainframe.get(1,o), 72000, new ItemStack[]{
                ItemList.Circuit_Board_Wetware.get(1,o),
                ItemList.Circuit_Chip_Stemcell.get(8,o),
                ItemList.Circuit_Parts_Glass_Tube.get(8,o),
                GT_OreDictUnificator.get(OrePrefixes.pipeTiny, Materials.Plastic, 4),
                ItemList.IC2_Item_Casing_Gold.get(8,o),
                GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Silicone, 64),
                GT_OreDictUnificator.get(OrePrefixes.plate, Materials.StainlessSteel, 4),
        }, new FluidStack[]{
                GregTech_API.mIC2Classic ? Materials.Water.getFluid(250) : Materials.GrowthMediumSterilized.getFluid(250),
                Materials.UUMatter.getFluid(100),
                GregTech_API.mIC2Classic ? Materials.Lava.getFluid(1000) : new FluidStack(FluidRegistry.getFluid("ic2coolant"), 1000)
        }, ItemList.Circuit_Chip_NeuroCPU.get(1,o), 200, 80000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Circuit_Wetwaresupercomputer.get(1,o), 288000, new ItemStack[]{
                GT_OreDictUnificator.get(OrePrefixes.frameGt, Materials.Tritanium, 4),
                ItemList.Circuit_Wetwaresupercomputer.get(8,o),
                ItemList.Circuit_Parts_Coil.get(4,o),
                ItemList.Circuit_Parts_CapacitorSMD.get(24,o),
                ItemList.Circuit_Parts_ResistorSMD.get(64,o),
                ItemList.Circuit_Parts_TransistorSMD.get(32,o),
                ItemList.Circuit_Parts_DiodeSMD.get(16,o),
                ItemList.Circuit_Chip_Ram.get(16,o),GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.Superconductor, 32),
                GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Silicone, 64)
        }, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(2880),
                GregTech_API.mIC2Classic ? Materials.Water.getFluid(10000) : new FluidStack(FluidRegistry.getFluid("ic2coolant"), 10000)
        }, ItemList.Circuit_Wetwaremainframe.get(1,o), 2000, 300000);

        GT_Values.RA.addAssemblylineRecipe(ItemList.Energy_LapotronicOrb.get(1, o), 288000, new ItemStack[]{
                ItemList.Circuit_Board_Multifiberglass.get(1,o),
                GT_OreDictUnificator.get(OrePrefixes.foil, Materials.Europium, 32L),
                ItemList.Circuit_Neuroprocessor.get(4, o),
                ItemList.Circuit_Parts_Crystal_Chip_Master.get(36L,o),
                ItemList.Circuit_Parts_Crystal_Chip_Master.get(36L,o),
                ItemList.Circuit_Chip_HPIC.get(64, o),
                ItemList.Circuit_Parts_DiodeSMD.get(32, o),
                ItemList.Circuit_Parts_CapacitorSMD.get(32, o),
                ItemList.Circuit_Parts_ResistorSMD.get(32, o),
                ItemList.Circuit_Parts_TransistorSMD.get(32, o),
                GT_OreDictUnificator.get(OrePrefixes.wireFine, Materials.Platinum, 64),
        }, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(720)
        }, ItemList.Energy_LapotronicOrb2.get(1, o), 1000, 80000);

        if (GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "EnableZPMandUVBatteries", false)) {
            GT_Values.RA.addAssemblylineRecipe(ItemList.Energy_LapotronicOrb2.get(1, o), 288000, new ItemStack[]{
                    GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Europium, 16L),
                    ItemList.Circuit_Wetwarecomputer.get(1, o),
                    ItemList.Circuit_Wetwarecomputer.get(1, o),
                    ItemList.Circuit_Wetwarecomputer.get(1, o),
                    ItemList.Circuit_Wetwarecomputer.get(1, o),
                    ItemList.Energy_LapotronicOrb2.get(8L, new Object[0]),
                    ItemList.Field_Generator_LuV.get(2, o),
                    ItemList.Circuit_Wafer_SoC2.get(64, o),
                    ItemList.Circuit_Wafer_SoC2.get(64, o),
                    ItemList.Circuit_Parts_DiodeSMD.get(8, o),
                    GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.Naquadah, 32),
            }, new FluidStack[]{
                    Materials.SolderingAlloy.getMolten(2880),
                    GregTech_API.mIC2Classic ? Materials.Water.getFluid(8000) : new FluidStack(FluidRegistry.getFluid("ic2coolant"), 16000)
            }, ItemList.Energy_Module.get(1, o), 2000, 100000);

            GT_Values.RA.addAssemblylineRecipe(ItemList.Energy_Module.get(1, o), 288000, new ItemStack[]{
                    GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Americium, 16L),
                    ItemList.Circuit_Wetwaresupercomputer.get(1, o),
                    ItemList.Circuit_Wetwaresupercomputer.get(1, o),
                    ItemList.Circuit_Wetwaresupercomputer.get(1, o),
                    ItemList.Circuit_Wetwaresupercomputer.get(1, o),
                    ItemList.Energy_Module.get(8L, new Object[0]),
                    ItemList.Field_Generator_ZPM.get(2, o),
                    ItemList.Circuit_Wafer_HPIC.get(64, o),
                    ItemList.Circuit_Wafer_HPIC.get(64, o),
                    ItemList.Circuit_Parts_DiodeSMD.get(16, o),
                    GT_OreDictUnificator.get(OrePrefixes.cableGt01, Materials.NaquadahAlloy, 32),
            }, new FluidStack[]{
                    Materials.SolderingAlloy.getMolten(2880),
                    GregTech_API.mIC2Classic ? Materials.Water.getFluid(16000) : new FluidStack(FluidRegistry.getFluid("ic2coolant"), 16000)
            }, ItemList.Energy_Cluster.get(1, o), 2000, 200000);

            GT_Values.RA.addAssemblylineRecipe(ItemList.Energy_Cluster.get(1, o), 288000, new ItemStack[]{
                    GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Neutronium, 16L),
                    ItemList.Circuit_Wetwaremainframe.get(1, o),
                    ItemList.Circuit_Wetwaremainframe.get(1, o),
                    ItemList.Circuit_Wetwaremainframe.get(1, o),
                    ItemList.Circuit_Wetwaremainframe.get(1, o),
                    ItemList.Energy_Cluster.get(8L, new Object[0]),
                    ItemList.Field_Generator_UV.get(2, o),
                    ItemList.Circuit_Neuroprocessor.get(64, o),
                    ItemList.Circuit_Neuroprocessor.get(64, o),
                    ItemList.Circuit_Parts_DiodeSMD.get(16, o),
                    GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.Superconductor, 32),
            }, new FluidStack[]{
                    Materials.SolderingAlloy.getMolten(2880),
                    GregTech_API.mIC2Classic ? Materials.Water.getFluid(16000) : new FluidStack(FluidRegistry.getFluid("ic2coolant"), 16000),
                    Materials.Naquadria.getMolten(1152)
            }, ItemList.ZPM2.get(1, o), 2000, 300000);
        }else {
            GT_Values.RA.addAssemblylineRecipe(ItemList.Energy_LapotronicOrb2.get(1, o), 288000, new ItemStack[]{
                    GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Neutronium, 16L),
                    ItemList.Circuit_Wetwaremainframe.get(1, o),
                    ItemList.Circuit_Wetwaremainframe.get(1, o),
                    ItemList.Circuit_Wetwaremainframe.get(1, o),
                    ItemList.Circuit_Wetwaremainframe.get(1, o),
                    ItemList.Energy_LapotronicOrb2.get(8L, new Object[0]),
                    ItemList.Field_Generator_UV.get(2, o),
                    ItemList.Circuit_Wafer_HPIC.get(64, o),
                    ItemList.Circuit_Wafer_HPIC.get(64, o),
                    ItemList.Circuit_Parts_DiodeSMD.get(16, o),
                    GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.Superconductor, 32),
            }, new FluidStack[]{
                    Materials.SolderingAlloy.getMolten(2880),
                    GregTech_API.mIC2Classic ? Materials.Water.getFluid(16000) : new FluidStack(FluidRegistry.getFluid("ic2coolant"), 16000)
            }, ItemList.ZPM2.get(1, o), 2000, 300000);
        }
        GT_Values.RA.addAssemblylineRecipe(GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.Superconductor, 1), 144000, new Object[]{
                ItemList.Casing_Fusion_Coil.get(1,o),
                new Object[]{OrePrefixes.circuit.get(Materials.Ultimate), 1},
                new Object[]{OrePrefixes.circuit.get(Materials.Ultimate), 1},
                new Object[]{OrePrefixes.circuit.get(Materials.Ultimate), 1},
                new Object[]{OrePrefixes.circuit.get(Materials.Ultimate), 1},
                GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Plutonium241, 1L),
                GT_OreDictUnificator.get(OrePrefixes.plate, Materials.NetherStar, 1L),
                ItemList.Field_Generator_IV.get(2,o),
                ItemList.Circuit_Wafer_HPIC.get(32,o),
                GT_OreDictUnificator.get(OrePrefixes.wireGt01, Materials.Superconductor, 32),
        }, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(2880),
        }, ItemList.FusionComputer_LuV.get(1,o), 1000, 30000);

        GT_Values.RA.addAssemblylineRecipe(GT_OreDictUnificator.get(OrePrefixes.block, Materials.Europium, 1), 288000, new Object[]{
                ItemList.Casing_Fusion_Coil.get(1,o),
                new Object[]{OrePrefixes.circuit.get(Materials.Superconductor), 1},
                new Object[]{OrePrefixes.circuit.get(Materials.Superconductor), 1},
                new Object[]{OrePrefixes.circuit.get(Materials.Superconductor), 1},
                new Object[]{OrePrefixes.circuit.get(Materials.Superconductor), 1},
                GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Europium, 4L),
                ItemList.Field_Generator_LuV.get(2,o),
                ItemList.Circuit_Wafer_HPIC.get(48,o),
                GT_OreDictUnificator.get(OrePrefixes.wireGt02, Materials.Superconductor, 32),
        }, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(2880),
        }, ItemList.FusionComputer_ZPMV.get(1,o), 1000, 60000);

        GT_Values.RA.addAssemblylineRecipe(GT_OreDictUnificator.get(OrePrefixes.block, Materials.Americium, 1), 432000, new ItemStack[]{
                ItemList.Casing_Fusion_Coil.get(1,o),
                ItemList.Circuit_Wetwaresupercomputer.get(1,o),
                ItemList.Circuit_Wetwaresupercomputer.get(1,o),
                ItemList.Circuit_Wetwaresupercomputer.get(1,o),
                ItemList.Circuit_Wetwaresupercomputer.get(1,o),
                GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Americium, 4L),
                ItemList.Field_Generator_ZPM.get(2,o),
                ItemList.Circuit_Wafer_HPIC.get(64,o),
                GT_OreDictUnificator.get(OrePrefixes.wireGt04, Materials.Superconductor, 32),
        }, new FluidStack[]{
                Materials.SolderingAlloy.getMolten(2880),
        }, ItemList.FusionComputer_UV.get(1,o), 1000, 90000);

        if (GregTech_API.sThaumcraftCompat != null) {
            String tKey = "GT_WOOD_TO_CHARCOAL";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "You have discovered a way of making charcoal magically instead of using regular ovens for this purpose.<BR><BR>To create charcoal from wood you first need an air-free environment, some vacuus essentia is needed for that, then you need to incinerate the wood using ignis essentia and wait until all the water inside the wood is burned away.<BR><BR>This method however doesn't create creosote oil as byproduct.");

            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Charcoal Transmutation", "Turning wood into charcoal", new String[]{"ALUMENTUM"}, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.gem, Materials.Charcoal, 1L), 2, 0, 13, 5, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.ARBOR, 10L), new TC_Aspects.TC_AspectStack(TC_Aspects.VACUOS, 8L), new TC_Aspects.TC_AspectStack(TC_Aspects.IGNIS, 8L)}), null, new Object[]{aTextTCGTPage + tKey, GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.log.get(Materials.Wood), GT_OreDictUnificator.get(OrePrefixes.gem, Materials.Charcoal, 1L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.VACUOS, 2L), new TC_Aspects.TC_AspectStack(TC_Aspects.IGNIS, 1L)}))});

            tKey = "GT_FILL_WATER_BUCKET";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "You have discovered a way of filling a bucket with aqua essentia in order to simply get water.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Water Transmutation", "Filling buckets with water", null, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.bucket, Materials.Water, 1L), 2, 0, 16, 5, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 4L), new TC_Aspects.TC_AspectStack(TC_Aspects.AQUA, 4L)}), null, new Object[]{aTextTCGTPage + tKey, GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, GT_OreDictUnificator.get(OrePrefixes.bucket, Materials.Empty, 1L), GT_OreDictUnificator.get(OrePrefixes.bucket, Materials.Water, 1L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.AQUA, 4L)})), GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, GT_OreDictUnificator.get(OrePrefixes.capsule, Materials.Empty, 1L), GT_OreDictUnificator.get(OrePrefixes.capsule, Materials.Water, 1L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.AQUA, 4L)})), GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, GT_OreDictUnificator.get(OrePrefixes.cell, Materials.Empty, 1L), GT_OreDictUnificator.get(OrePrefixes.cell, Materials.Water, 1L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.AQUA, 4L)}))});

            tKey = "GT_TRANSZINC";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "You have discovered a way to multiply zinc by steeping zinc nuggets in metallum harvested from other metals.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Zinc Transmutation", "Transformation of metals into zinc", new String[]{"TRANSTIN"}, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Zinc, 1L), 2, 1, 9, 13, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 5L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.SANO, 3L)}), null, new Object[]{aTextTCGTPage + tKey, GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.nugget.get(Materials.Zinc), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Zinc, 3L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 2L), new TC_Aspects.TC_AspectStack(TC_Aspects.SANO, 1L)}))});

            tKey = "GT_TRANSANTIMONY";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "You have discovered a way to multiply antimony by steeping antimony nuggets in metallum harvested from other metals.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Antimony Transmutation", "Transformation of metals into antimony", new String[]{"GT_TRANSZINC", "TRANSLEAD"}, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Antimony, 1L), 2, 1, 9, 14, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 5L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.AQUA, 3L)}), null, new Object[]{aTextTCGTPage + tKey, GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.nugget.get(Materials.Antimony), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Antimony, 3L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 2L), new TC_Aspects.TC_AspectStack(TC_Aspects.AQUA, 1L)}))});

            tKey = "GT_TRANSNICKEL";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "You have discovered a way to multiply nickel by steeping nickel nuggets in metallum harvested from other metals.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Nickel Transmutation", "Transformation of metals into nickel", new String[]{"TRANSLEAD"}, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Nickel, 1L), 2, 1, 9, 15, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 5L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.IGNIS, 3L)}), null, new Object[]{aTextTCGTPage + tKey, GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.nugget.get(Materials.Nickel), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Nickel, 3L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 2L), new TC_Aspects.TC_AspectStack(TC_Aspects.IGNIS, 1L)}))});

            tKey = "GT_TRANSCOBALT";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "You have discovered a way to multiply cobalt by steeping cobalt nuggets in metallum harvested from other metals.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Cobalt Transmutation", "Transformation of metals into cobalt", new String[]{"GT_TRANSNICKEL"}, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Cobalt, 1L), 2, 1, 9, 16, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 5L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.INSTRUMENTUM, 3L)}), null, new Object[]{aTextTCGTPage + tKey, GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.nugget.get(Materials.Cobalt), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Cobalt, 3L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 2L), new TC_Aspects.TC_AspectStack(TC_Aspects.INSTRUMENTUM, 1L)}))});

            tKey = "GT_TRANSBISMUTH";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "You have discovered a way to multiply bismuth by steeping bismuth nuggets in metallum harvested from other metals.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Bismuth Transmutation", "Transformation of metals into bismuth", new String[]{"GT_TRANSCOBALT"}, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Bismuth, 1L), 2, 1, 11, 17, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 5L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.INSTRUMENTUM, 3L)}), null, new Object[]{aTextTCGTPage + tKey, GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.nugget.get(Materials.Bismuth), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Bismuth, 3L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 2L), new TC_Aspects.TC_AspectStack(TC_Aspects.INSTRUMENTUM, 1L)}))});

            tKey = "GT_IRON_TO_STEEL";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "You have discovered a way of making Iron harder by just re-ordering its components.<BR><BR>This Method can be used to create a Material called Steel, which is used in many non-Thaumaturgic applications.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Steel Transmutation", "Transforming iron to steel", new String[]{"TRANSIRON", "GT_WOOD_TO_CHARCOAL"}, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Steel, 1L), 3, 0, 13, 8, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 5L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.ORDO, 3L)}), null, new Object[]{aTextTCGTPage + tKey, GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.nugget.get(Materials.Iron), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Steel, 1L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.ORDO, 1L)}))});

            tKey = "GT_TRANSBRONZE";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "You have discovered a way of creating Alloys using the already known transmutations of Copper and Tin.<BR><BR>This Method can be used to create a Bronze directly without having to go through an alloying process.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Bronze Transmutation", "Transformation of metals into bronze", new String[]{"TRANSTIN", "TRANSCOPPER"}, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Bronze, 1L), 2, 0, 13, 11, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 5L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.INSTRUMENTUM, 3L)}), null, new Object[]{aTextTCGTPage + tKey, GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.nugget.get(Materials.Bronze), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Bronze, 3L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 2L), new TC_Aspects.TC_AspectStack(TC_Aspects.INSTRUMENTUM, 1L)}))});

            tKey = "GT_TRANSELECTRUM";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "Your discovery of Bronze Transmutation has lead you to the conclusion it works with other Alloys such as Electrum as well.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Electrum Transmutation", "Transformation of metals into electrum", new String[]{"GT_TRANSBRONZE", "TRANSGOLD", "TRANSSILVER"}, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Electrum, 1L), 2, 1, 11, 11, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 5L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.LUCRUM, 3L)}), null, new Object[]{aTextTCGTPage + tKey, GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.nugget.get(Materials.Electrum), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Electrum, 3L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 2L), new TC_Aspects.TC_AspectStack(TC_Aspects.LUCRUM, 1L)}))});

            tKey = "GT_TRANSBRASS";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "Your discovery of Bronze Transmutation has lead you to the conclusion it works with other Alloys such as Brass as well.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Brass Transmutation", "Transformation of metals into brass", new String[]{"GT_TRANSBRONZE", "GT_TRANSZINC"}, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Brass, 1L), 2, 1, 11, 12, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 5L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.INSTRUMENTUM, 3L)}), null, new Object[]{aTextTCGTPage + tKey, GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.nugget.get(Materials.Brass), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Brass, 3L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 2L), new TC_Aspects.TC_AspectStack(TC_Aspects.INSTRUMENTUM, 1L)}))});

            tKey = "GT_TRANSINVAR";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "Your discovery of Bronze Transmutation has lead you to the conclusion it works with other Alloys such as Invar as well.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Invar Transmutation", "Transformation of metals into invar", new String[]{"GT_TRANSBRONZE", "GT_TRANSNICKEL"}, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Invar, 1L), 2, 1, 11, 15, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 5L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.GELUM, 3L)}), null, new Object[]{aTextTCGTPage + tKey, GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.nugget.get(Materials.Invar), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Invar, 3L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 2L), new TC_Aspects.TC_AspectStack(TC_Aspects.GELUM, 1L)}))});

            tKey = "GT_TRANSCUPRONICKEL";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "Your discovery of Bronze Transmutation has lead you to the conclusion it works with other Alloys such as Cupronickel as well.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Cupronickel Transmutation", "Transformation of metals into cupronickel", new String[]{"GT_TRANSBRONZE", "GT_TRANSNICKEL"}, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Cupronickel, 1L), 2, 1, 11, 16, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 5L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.IGNIS, 3L)}), null, new Object[]{aTextTCGTPage + tKey, GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.nugget.get(Materials.Cupronickel), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Cupronickel, 3L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 2L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 1L), new TC_Aspects.TC_AspectStack(TC_Aspects.IGNIS, 1L)}))});

            tKey = "GT_TRANSBATTERYALLOY";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "Your discovery of Bronze Transmutation has lead you to the conclusion it works with other Alloys such as Battery Alloy as well.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Battery Alloy Transmutation", "Transformation of metals into battery alloy", new String[]{"GT_TRANSBRONZE", "GT_TRANSANTIMONY"}, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.BatteryAlloy, 1L), 2, 1, 11, 13, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 5L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.IGNIS, 3L)}), null, new Object[]{aTextTCGTPage + tKey, GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.nugget.get(Materials.BatteryAlloy), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.BatteryAlloy, 3L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 2L), new TC_Aspects.TC_AspectStack(TC_Aspects.AQUA, 1L), new TC_Aspects.TC_AspectStack(TC_Aspects.ORDO, 1L)}))});

            tKey = "GT_TRANSSOLDERINGALLOY";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "Your discovery of Bronze Transmutation has lead you to the conclusion it works with other Alloys such as Soldering Alloy as well.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Soldering Alloy Transmutation", "Transformation of metals into soldering alloy", new String[]{"GT_TRANSBRONZE", "GT_TRANSANTIMONY"}, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.SolderingAlloy, 1L), 2, 1, 11, 14, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 5L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.IGNIS, 3L)}), null, new Object[]{aTextTCGTPage + tKey, GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.nugget.get(Materials.SolderingAlloy), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.SolderingAlloy, 3L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 2L), new TC_Aspects.TC_AspectStack(TC_Aspects.AQUA, 1L), new TC_Aspects.TC_AspectStack(TC_Aspects.VITREUS, 1L)}))});

            tKey = "GT_ADVANCEDMETALLURGY";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "Now that you have discovered all the basic metals, you can finally move on to the next Level of magic metallurgy and create more advanced metals");
            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Advanced Metallurgic Transmutation", "Mastering the basic metals", new String[]{"GT_TRANSBISMUTH", "GT_IRON_TO_STEEL", "GT_TRANSSOLDERINGALLOY", "GT_TRANSBATTERYALLOY", "GT_TRANSBRASS", "GT_TRANSELECTRUM", "GT_TRANSCUPRONICKEL", "GT_TRANSINVAR"}, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Iron, 1L), 3, 0, 16, 14, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 50L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 20L), new TC_Aspects.TC_AspectStack(TC_Aspects.COGNITIO, 20L), new TC_Aspects.TC_AspectStack(TC_Aspects.PRAECANTATIO, 20L), new TC_Aspects.TC_AspectStack(TC_Aspects.NEBRISUM, 20L), new TC_Aspects.TC_AspectStack(TC_Aspects.MAGNETO, 20L)}), null, new Object[]{aTextTCGTPage + tKey});

            tKey = "GT_TRANSALUMINIUM";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "You have discovered a way to multiply aluminium by steeping aluminium nuggets in metallum harvested from other metals.<BR><BR>This transmutation is slightly harder to achieve, because aluminium has special properties, which require more order to achieve the desired result.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Aluminium Transmutation", "Transformation of metals into aluminium", new String[]{"GT_ADVANCEDMETALLURGY"}, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Aluminium, 1L), 4, 0, 19, 14, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 5L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.VOLATUS, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.ORDO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.IGNIS, 3L)}), null, new Object[]{aTextTCGTPage + tKey, GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.nugget.get(Materials.Aluminium), GT_OreDictUnificator.get(OrePrefixes.nugget, Materials.Aluminium, 3L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.METALLUM, 2L), new TC_Aspects.TC_AspectStack(TC_Aspects.VOLATUS, 1L), new TC_Aspects.TC_AspectStack(TC_Aspects.ORDO, 1L), new TC_Aspects.TC_AspectStack(TC_Aspects.IGNIS, 1L)}))});

            tKey = "GT_CRYSTALLISATION";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey, "Sometimes when processing your Crystal Shards they become a pile of Dust instead of the mostly required Shard.<BR><BR>You have finally found a way to reverse this Process by using Vitreus Essentia for recrystallising the Shards.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey, "Shard Recrystallisation", "Fixing your precious crystals", new String[]{"ALCHEMICALMANUFACTURE"}, "ALCHEMY", GT_OreDictUnificator.get(OrePrefixes.gem, Materials.InfusedOrder, 1L), 3, 0, -11, -3, Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.VITREUS, 5L), new TC_Aspects.TC_AspectStack(TC_Aspects.PERMUTATIO, 3L), new TC_Aspects.TC_AspectStack(TC_Aspects.ORDO, 3L)}), null, new Object[]{aTextTCGTPage + tKey, GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.dust.get(Materials.Amber), GT_OreDictUnificator.get(OrePrefixes.gem, Materials.Amber, 1L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.VITREUS, 4L)})), GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.dust.get(Materials.InfusedOrder), GT_OreDictUnificator.get(OrePrefixes.gem, Materials.InfusedOrder, 1L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.VITREUS, 4L)})), GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.dust.get(Materials.InfusedEntropy), GT_OreDictUnificator.get(OrePrefixes.gem, Materials.InfusedEntropy, 1L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.VITREUS, 4L)})), GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.dust.get(Materials.InfusedAir), GT_OreDictUnificator.get(OrePrefixes.gem, Materials.InfusedAir, 1L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.VITREUS, 4L)})), GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.dust.get(Materials.InfusedEarth), GT_OreDictUnificator.get(OrePrefixes.gem, Materials.InfusedEarth, 1L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.VITREUS, 4L)})), GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.dust.get(Materials.InfusedFire), GT_OreDictUnificator.get(OrePrefixes.gem, Materials.InfusedFire, 1L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.VITREUS, 4L)})), GregTech_API.sThaumcraftCompat.addCrucibleRecipe(tKey, OrePrefixes.dust.get(Materials.InfusedWater), GT_OreDictUnificator.get(OrePrefixes.gem, Materials.InfusedWater, 1L), Arrays.asList(new TC_Aspects.TC_AspectStack[]{new TC_Aspects.TC_AspectStack(TC_Aspects.VITREUS, 4L)}))});

            tKey = "GT_MAGICENERGY";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey,
                    "While trying to find new ways to integrate magic into your industrial factories, you have discovered a way to convert magical energy into electrical power.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey,
                    "Magic Energy Conversion",
                    "Magic to Power",
                    new String[]{"ARCANEBORE"},
                    "ARTIFICE",
                    ItemList.MagicEnergyConverter_LV.get(1L, new Object[0]),
                    3, 0, -3, 10,
                    Arrays.asList(new TC_Aspects.TC_AspectStack[]{
                            new TC_Aspects.TC_AspectStack(TC_Aspects.MACHINA, 10L),
                            new TC_Aspects.TC_AspectStack(TC_Aspects.COGNITIO, 10L),
                            new TC_Aspects.TC_AspectStack(TC_Aspects.POTENTIA, 20L),
                            new TC_Aspects.TC_AspectStack(TC_Aspects.ELECTRUM, 10L)}),
                    null, new Object[]{aTextTCGTPage + tKey,
                            GregTech_API.sThaumcraftCompat.addInfusionRecipe(tKey,
                                    ItemList.Hull_LV.get(1L, new Object[0]),
                                    new ItemStack[]{
                                            new ItemStack(Blocks.beacon),
                                            GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Advanced, 1L),
                                            GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Thaumium, 1L),
                                            ItemList.Sensor_MV.get(2L, new Object[0]),
                                            GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Advanced, 1L),
                                            GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Thaumium, 1L),
                                            ItemList.Sensor_MV.get(2L, new Object[0])
                                    },
                                    ItemList.MagicEnergyConverter_LV.get(1L, new Object[0]),
                                    5,
                                    Arrays.asList(new TC_Aspects.TC_AspectStack[]{
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.POTENTIA, 32L),
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.ELECTRUM, 16L),
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.MACHINA, 32L)}))});

            tKey = "GT_MAGICENERGY2";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey,
                    "Attempts to increase the output of your Magic Energy generators have resulted in significant improvements.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey,
                    "Adept Magic Energy Conversion",
                    "Magic to Power",
                    new String[]{"GT_MAGICENERGY"},
                    "ARTIFICE",
                    ItemList.MagicEnergyConverter_MV.get(1L, new Object[0]),
                    1, 1, -4, 12,
                    Arrays.asList(new TC_Aspects.TC_AspectStack[]{
                            new TC_Aspects.TC_AspectStack(TC_Aspects.MACHINA, 10L),
                            new TC_Aspects.TC_AspectStack(TC_Aspects.COGNITIO, 10L),
                            new TC_Aspects.TC_AspectStack(TC_Aspects.POTENTIA, 20L),
                            new TC_Aspects.TC_AspectStack(TC_Aspects.ELECTRUM, 10L)}),
                    null, new Object[]{aTextTCGTPage + tKey,
                            GregTech_API.sThaumcraftCompat.addInfusionRecipe(tKey,
                                    ItemList.Hull_MV.get(1L, new Object[0]),
                                    new ItemStack[]{
                                            new ItemStack(Blocks.beacon),
                                            GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Data, 1L),
                                            GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Thaumium, 1L),
                                            ItemList.Sensor_HV.get(2L, new Object[0]),
                                            GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Data, 1L),
                                            GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Iridium, 1L),
                                            ItemList.Sensor_HV.get(2L, new Object[0])
                                    },
                                    ItemList.MagicEnergyConverter_MV.get(1L, new Object[0]),
                                    6,
                                    Arrays.asList(new TC_Aspects.TC_AspectStack[]{
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.POTENTIA, 64L),
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.ELECTRUM, 32L),
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.MACHINA, 64L)}))});

            tKey = "GT_MAGICENERGY3";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey,
                    "Attempts to further increase the output of your Magic Energy generators have resulted in great improvements.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey,
                    "Master Magic Energy Conversion",
                    "Magic to Power",
                    new String[]{"GT_MAGICENERGY2"},
                    "ARTIFICE",
                    ItemList.MagicEnergyConverter_HV.get(1L, new Object[0]),
                    1, 1, -4, 14,
                    Arrays.asList(new TC_Aspects.TC_AspectStack[]{
                            new TC_Aspects.TC_AspectStack(TC_Aspects.MACHINA, 20L),
                            new TC_Aspects.TC_AspectStack(TC_Aspects.COGNITIO, 20L),
                            new TC_Aspects.TC_AspectStack(TC_Aspects.POTENTIA, 40L),
                            new TC_Aspects.TC_AspectStack(TC_Aspects.ELECTRUM, 20L)}),
                    null, new Object[]{aTextTCGTPage + tKey,
                            GregTech_API.sThaumcraftCompat.addInfusionRecipe(tKey,
                                    ItemList.Hull_HV.get(1L, new Object[0]),
                                    new ItemStack[]{
                                            new ItemStack(Blocks.beacon),
                                            GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Elite, 1L),
                                            GT_OreDictUnificator.get(OrePrefixes.plateDouble, Materials.Thaumium, 1L),
                                            ItemList.Field_Generator_MV.get(1L, new Object[0]),
                                            GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Elite, 1L),
                                            GT_OreDictUnificator.get(OrePrefixes.plateDouble, Materials.Osmium, 1L),
                                            ItemList.Field_Generator_MV.get(1L, new Object[0])
                                    },
                                    ItemList.MagicEnergyConverter_HV.get(1L, new Object[0]),
                                    8,
                                    Arrays.asList(new TC_Aspects.TC_AspectStack[]{
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.POTENTIA, 128L),
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.ELECTRUM, 64L),
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.MACHINA, 128L)}))});


            tKey = "GT_MAGICABSORB";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey,
                    "Research into magical energy conversion methods has identified a way to convert surrounding energies into electrical power.");
            GregTech_API.sThaumcraftCompat.addResearch(tKey,
                    "Magic Energy Absorption",
                    "Harvesting Magic",
                    new String[]{"GT_MAGICENERGY"},
                    "ARTIFICE",
                    ItemList.MagicEnergyAbsorber_LV.get(1L, new Object[0]),
                    3, 0, -2, 12,
                    Arrays.asList(new TC_Aspects.TC_AspectStack[]{
                            new TC_Aspects.TC_AspectStack(TC_Aspects.MACHINA, 10L),
                            new TC_Aspects.TC_AspectStack(TC_Aspects.COGNITIO, 10L),
                            new TC_Aspects.TC_AspectStack(TC_Aspects.POTENTIA, 20L),
                            new TC_Aspects.TC_AspectStack(TC_Aspects.ELECTRUM, 10L)}),
                    null, new Object[]{aTextTCGTPage + tKey,
                            GregTech_API.sThaumcraftCompat.addInfusionRecipe(tKey,
                                    ItemList.Hull_LV.get(1L, new Object[0]),
                                    new ItemStack[]{
                                            ItemList.MagicEnergyConverter_LV.get(1L, new Object[0]),
                                            GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Advanced, 1L),
                                            GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Thaumium, 1L),
                                            ItemList.Sensor_MV.get(2L, new Object[0])
                                    },
                                    ItemList.MagicEnergyAbsorber_LV.get(1L, new Object[0]),
                                    6,
                                    Arrays.asList(new TC_Aspects.TC_AspectStack[]{
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.POTENTIA, 32L),
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.ELECTRUM, 16L),
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.MACHINA, 32L),
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.VACUOS, 16L),
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.INSTRUMENTUM, 32L),
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.STRONTIO, 4L)}))});

            tKey = "GT_MAGICABSORB2";
            GT_LanguageManager.addStringLocalization(aTextTCGTPage + tKey,
                    "Moar output! Drain all the Magic!");
            GregTech_API.sThaumcraftCompat.addResearch(tKey,
                    "Improved Magic Energy Absorption",
                    "Harvesting Magic",
                    new String[]{"GT_MAGICABSORB"},
                    "ARTIFICE",
                    ItemList.MagicEnergyAbsorber_EV.get(1L, new Object[0]),
                    3, 1, -2, 14,
                    Arrays.asList(new TC_Aspects.TC_AspectStack[]{
                            new TC_Aspects.TC_AspectStack(TC_Aspects.MACHINA, 10L),
                            new TC_Aspects.TC_AspectStack(TC_Aspects.COGNITIO, 10L),
                            new TC_Aspects.TC_AspectStack(TC_Aspects.POTENTIA, 20L),
                            new TC_Aspects.TC_AspectStack(TC_Aspects.ELECTRUM, 10L)}),
                    null, new Object[]{aTextTCGTPage + tKey,
                            GregTech_API.sThaumcraftCompat.addInfusionRecipe(tKey,
                                    ItemList.Hull_MV.get(1L, new Object[0]),
                                    new ItemStack[]{
                                            ItemList.MagicEnergyConverter_MV.get(1L, new Object[0]),
                                            GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Advanced, 1L),
                                            GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Thaumium, 1L),
                                            ItemList.Sensor_HV.get(2L, new Object[0]),
                                            GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Advanced, 1L),
                                            GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Thaumium, 1L)
                                    },
                                    ItemList.MagicEnergyAbsorber_MV.get(1L, new Object[0]),
                                    6,
                                    Arrays.asList(new TC_Aspects.TC_AspectStack[]{
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.POTENTIA, 64L),
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.ELECTRUM, 32L),
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.MACHINA, 64L),
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.VACUOS, 32L),
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.INSTRUMENTUM, 64L),
                                            new TC_Aspects.TC_AspectStack(TC_Aspects.STRONTIO, 8L)}))


                            , GregTech_API.sThaumcraftCompat.addInfusionRecipe(tKey,
                            ItemList.Hull_HV.get(1L, new Object[0]),
                            new ItemStack[]{
                                    ItemList.MagicEnergyConverter_MV.get(1L, new Object[0]),
                                    GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Elite, 1L),
                                    GT_ModHandler.getModItem("Thaumcraft", "ItemResource", 1, 16),
                                    ItemList.Field_Generator_MV.get(1L, new Object[0]),
                                    GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Elite, 1L),
                                    GT_ModHandler.getModItem("Thaumcraft", "ItemResource", 1, 16)
                            },
                            ItemList.MagicEnergyAbsorber_HV.get(1L, new Object[0]),
                            8,
                            Arrays.asList(new TC_Aspects.TC_AspectStack[]{
                                    new TC_Aspects.TC_AspectStack(TC_Aspects.POTENTIA, 128L),
                                    new TC_Aspects.TC_AspectStack(TC_Aspects.ELECTRUM, 64L),
                                    new TC_Aspects.TC_AspectStack(TC_Aspects.MACHINA, 128L),
                                    new TC_Aspects.TC_AspectStack(TC_Aspects.VACUOS, 64L),
                                    new TC_Aspects.TC_AspectStack(TC_Aspects.INSTRUMENTUM, 128L),
                                    new TC_Aspects.TC_AspectStack(TC_Aspects.STRONTIO, 16L)}))


                            , GregTech_API.sThaumcraftCompat.addInfusionRecipe(tKey,
                            ItemList.Hull_EV.get(1L, new Object[0]),
                            new ItemStack[]{
                                    ItemList.MagicEnergyConverter_HV.get(1L, new Object[0]),
                                    GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Master, 1L),
                                    GT_ModHandler.getModItem("Thaumcraft", "ItemResource", 1, 16),
                                    ItemList.Field_Generator_HV.get(1L, new Object[0]),
                                    GT_OreDictUnificator.get(OrePrefixes.circuit, Materials.Master, 1L),
                                    GT_ModHandler.getModItem("Thaumcraft", "ItemResource", 1, 16)
                            },
                            ItemList.MagicEnergyAbsorber_EV.get(1L, new Object[0]),
                            10,
                            Arrays.asList(new TC_Aspects.TC_AspectStack[]{
                                    new TC_Aspects.TC_AspectStack(TC_Aspects.POTENTIA, 256L),
                                    new TC_Aspects.TC_AspectStack(TC_Aspects.ELECTRUM, 128L),
                                    new TC_Aspects.TC_AspectStack(TC_Aspects.MACHINA, 256L),
                                    new TC_Aspects.TC_AspectStack(TC_Aspects.VACUOS, 128L),
                                    new TC_Aspects.TC_AspectStack(TC_Aspects.INSTRUMENTUM, 256L),
                                    new TC_Aspects.TC_AspectStack(TC_Aspects.STRONTIO, 64L)}))
                    });
        }
        //Digital Transformers
        GT_Values.RA.addAssemblerRecipe(new ItemStack[]{
                ItemList.Transformer_LV_ULV.get(8L),
                ItemList.Transformer_MV_LV.get(4L),
                ItemList.Transformer_HV_MV.get(2L),
                ItemList.Transformer_EV_HV.get(1L),
                ItemList.Cover_Screen.get(1L)}, OrePrefixes.circuit.get(Materials.Data), 4, GT_Values.NF, ItemList.Machine_DigitalTransformer_EV.get(1L), 50, 1920);
        GT_Values.RA.addAssemblerRecipe(new ItemStack[]{
                ItemList.Transformer_IV_EV.get(1L),
                ItemList.Machine_DigitalTransformer_EV.get(2L),
                ItemList.Cover_Screen.get(1L)}, OrePrefixes.circuit.get(Materials.Elite), 4, GT_Values.NF, ItemList.Machine_DigitalTransformer_IV.get(1L), 50, 1920);
        GT_Values.RA.addAssemblerRecipe(new ItemStack[]{
                ItemList.Transformer_LuV_IV.get(1L),
                ItemList.Machine_DigitalTransformer_IV.get(2L),
                ItemList.Cover_Screen.get(1L)}, OrePrefixes.circuit.get(Materials.Master), 4, GT_Values.NF, ItemList.Machine_DigitalTransformer_LuV.get(1L), 50, 1920);
        GT_Values.RA.addAssemblerRecipe(new ItemStack[]{
                ItemList.Transformer_ZPM_LuV.get(1L),
                ItemList.Machine_DigitalTransformer_LuV.get(2L),
                ItemList.Cover_Screen.get(1L)}, OrePrefixes.circuit.get(Materials.Ultimate), 4, GT_Values.NF, ItemList.Machine_DigitalTransformer_ZPM.get(1L), 50, 1920);
        GT_Values.RA.addAssemblerRecipe(new ItemStack[]{
                ItemList.Transformer_UV_ZPM.get(1L),
                ItemList.Machine_DigitalTransformer_ZPM.get(2L),
                ItemList.Cover_Screen.get(1L)}, OrePrefixes.circuit.get(Materials.Superconductor), 4, GT_Values.NF, ItemList.Machine_DigitalTransformer_UV.get(1L), 50, 1920);
        GT_Values.RA.addAssemblerRecipe(new ItemStack[]{
                ItemList.Transformer_MAX_UV.get(1L),
                ItemList.Machine_DigitalTransformer_UV.get(2L),
                ItemList.Cover_Screen.get(1L)}, OrePrefixes.circuit.get(Materials.Infinite), 4, GT_Values.NF, ItemList.Machine_DigitalTransformer_MAX.get(1L), 50, 1920);

        GT_Utility.removeSimpleIC2MachineRecipe(new ItemStack(Blocks.cobblestone), GT_ModHandler.getMaceratorRecipeList(), GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Stone, 1L));
        GT_Utility.removeSimpleIC2MachineRecipe(GT_OreDictUnificator.get(OrePrefixes.gem, Materials.Lapis, 1L), GT_ModHandler.getMaceratorRecipeList(), ItemList.IC2_Plantball.get(1L, new Object[0]));
        GT_Utility.removeSimpleIC2MachineRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1L), GT_ModHandler.getMaceratorRecipeList(), ItemList.IC2_Plantball.get(1L, new Object[0]));
        GT_Utility.removeSimpleIC2MachineRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Glowstone, 1L), GT_ModHandler.getMaceratorRecipeList(), ItemList.IC2_Plantball.get(1L, new Object[0]));

        if(GregTech_API.mMagneticraft && GT_Mod.gregtechproxy.mMagneticraftRecipes){
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getModItem("Magneticraft", "item.ingotCarbide", 8));
            GT_Values.RA.addAlloySmelterRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 8), GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.WroughtIron, 1), GT_ModHandler.getModItem("Magneticraft", "item.ingotCarbide", 1), 600, 24);
            GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 8), GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.TungstenCarbide, 1), null, null, GT_ModHandler.getModItem("Magneticraft", "item.ingotCarbide", 8), null, 100, 120, 2600);
            GT_ModHandler.removeFurnaceSmelting(GT_ModHandler.getModItem("Magneticraft", "item.chunks", 1, 4));
            GT_ModHandler.removeFurnaceSmelting(GT_ModHandler.getModItem("Magneticraft", "item.pebbles", 1, 4));
            GT_ModHandler.removeFurnaceSmelting(GT_ModHandler.getModItem("Magneticraft", "item.rubble", 1, 4));
            GT_ModHandler.removeFurnaceSmelting(GT_ModHandler.getModItem("Magneticraft", "item.chunks", 1, 13));
            GT_ModHandler.removeFurnaceSmelting(GT_ModHandler.getModItem("Magneticraft", "item.pebbles", 1, 13));
            GT_ModHandler.removeFurnaceSmelting(GT_ModHandler.getModItem("Magneticraft", "item.rubble", 1, 13));
            GT_ModHandler.removeFurnaceSmelting(GT_ModHandler.getModItem("Magneticraft", "item.chunks", 1, 15));
            GT_ModHandler.removeFurnaceSmelting(GT_ModHandler.getModItem("Magneticraft", "item.pebbles", 1, 15));
            GT_ModHandler.removeFurnaceSmelting(GT_ModHandler.getModItem("Magneticraft", "item.rubble", 1, 15));
            GT_ModHandler.removeFurnaceSmelting(GT_ModHandler.getModItem("Magneticraft", "item.chunks", 1, 16));
            GT_ModHandler.removeFurnaceSmelting(GT_ModHandler.getModItem("Magneticraft", "item.pebbles", 1, 16));
            GT_ModHandler.removeFurnaceSmelting(GT_ModHandler.getModItem("Magneticraft", "item.rubble", 1, 16));
            GT_ModHandler.removeFurnaceSmelting(GT_ModHandler.getModItem("Magneticraft", "item.chunks", 1, 21));
            GT_ModHandler.removeFurnaceSmelting(GT_ModHandler.getModItem("Magneticraft", "item.pebbles", 1, 21));
            GT_ModHandler.removeFurnaceSmelting(GT_ModHandler.getModItem("Magneticraft", "item.rubble", 1, 21));
        }

        for (MaterialStack[] tMats : this.mAlloySmelterList) {
            ItemStack tDust1 = GT_OreDictUnificator.get(OrePrefixes.dust, tMats[0].mMaterial, tMats[0].mAmount);
            ItemStack tDust2 = GT_OreDictUnificator.get(OrePrefixes.dust, tMats[1].mMaterial, tMats[1].mAmount);
            ItemStack tIngot1 = GT_OreDictUnificator.get(OrePrefixes.ingot, tMats[0].mMaterial, tMats[0].mAmount);
            ItemStack tIngot2 = GT_OreDictUnificator.get(OrePrefixes.ingot, tMats[1].mMaterial, tMats[1].mAmount);
            ItemStack tOutputIngot = GT_OreDictUnificator.get(OrePrefixes.ingot, tMats[2].mMaterial, tMats[2].mAmount);
            if (tOutputIngot != GT_Values.NI) {
                GT_ModHandler.addAlloySmelterRecipe(tIngot1, tDust2, tOutputIngot, (int) tMats[2].mAmount * 50, 16, false);
                GT_ModHandler.addAlloySmelterRecipe(tIngot1, tIngot2, tOutputIngot, (int) tMats[2].mAmount * 50, 16, false);
                GT_ModHandler.addAlloySmelterRecipe(tDust1, tIngot2, tOutputIngot, (int) tMats[2].mAmount * 50, 16, false);
                GT_ModHandler.addAlloySmelterRecipe(tDust1, tDust2, tOutputIngot, (int) tMats[2].mAmount * 50, 16, false);
            }
        }

        if(!GregTech_API.mIC2Classic){
            try {
                Map<String, HeatExchangeProperty> tLiqExchange = ic2.api.recipe.Recipes.liquidCooldownManager.getHeatExchangeProperties();
                Iterator<Map.Entry<String, HeatExchangeProperty>> tIterator = tLiqExchange.entrySet().iterator();
                while (tIterator.hasNext()) {
                    Map.Entry<String, HeatExchangeProperty> tEntry = tIterator.next();
                    if(tEntry.getKey().equals("ic2hotcoolant")){
                        tIterator.remove();
                        Recipes.liquidCooldownManager.addFluid("ic2hotcoolant", "ic2coolant", 100);
                    }
                }
            } catch (Throwable e) {/*Do nothing*/}

            try {
                Map<String, HeatExchangeProperty> tLiqExchange = ic2.api.recipe.Recipes.liquidHeatupManager.getHeatExchangeProperties();
                Iterator<Map.Entry<String, HeatExchangeProperty>> tIterator = tLiqExchange.entrySet().iterator();
                while (tIterator.hasNext()) {
                    Map.Entry<String, HeatExchangeProperty> tEntry = tIterator.next();
                    if(tEntry.getKey().equals("ic2coolant")){
                        tIterator.remove();
                        Recipes.liquidHeatupManager.addFluid("ic2coolant", "ic2hotcoolant", 100);
                    }
                }
            } catch (Throwable e) {/*Do nothing*/}
        }
        GT_Utility.removeSimpleIC2MachineRecipe(ItemList.Crop_Drop_BobsYerUncleRanks.get(1L, new Object[0]), GT_ModHandler.getExtractorRecipeList(), null);
        GT_Utility.removeSimpleIC2MachineRecipe(ItemList.Crop_Drop_Ferru.get(1L, new Object[0]), GT_ModHandler.getExtractorRecipeList(), null);
        GT_Utility.removeSimpleIC2MachineRecipe(ItemList.Crop_Drop_Aurelia.get(1L, new Object[0]), GT_ModHandler.getExtractorRecipeList(), null);

        ItemStack tCrop;
        // Metals Line
        tCrop = ItemList.Crop_Drop_Coppon.get(1, new Object[0]);
        addProcess(tCrop, Materials.Copper, 100, true);
        addProcess(tCrop, Materials.Tetrahedrite, 100, false);
        addProcess(tCrop, Materials.Chalcopyrite, 100, false);
        addProcess(tCrop, Materials.Malachite, 100, false);
        addProcess(tCrop, Materials.Pyrite, 100, false);
        addProcess(tCrop, Materials.Stibnite, 100, false);
        tCrop = ItemList.Crop_Drop_Tine.get(1, new Object[0]);
        addProcess(tCrop, Materials.Tin, 100, true);
        addProcess(tCrop, Materials.Cassiterite, 100, false);
        tCrop = ItemList.Crop_Drop_Plumbilia.get(1, new Object[0]);
        addProcess(tCrop, Materials.Lead, 100, true);
        addProcess(tCrop, Materials.Galena, 100, false);
        tCrop = ItemList.Crop_Drop_Ferru.get(1, new Object[0]);
        addProcess(tCrop, Materials.Iron, 100, true);
        addProcess(tCrop, Materials.Magnetite, 100, false);
        addProcess(tCrop, Materials.BrownLimonite, 100, false);
        addProcess(tCrop, Materials.YellowLimonite, 100, false);
        addProcess(tCrop, Materials.VanadiumMagnetite, 100, false);
        addProcess(tCrop, Materials.BandedIron, 100, false);
        addProcess(tCrop, Materials.Pyrite, 100, false);
        addProcess(tCrop, Materials.MeteoricIron, 100, false);
        tCrop = ItemList.Crop_Drop_Nickel.get(1, new Object[0]);
        addProcess(tCrop, Materials.Nickel, 100, true);
        addProcess(tCrop, Materials.Garnierite, 100, false);
        addProcess(tCrop, Materials.Pentlandite, 100, false);
        addProcess(tCrop, Materials.Cobaltite, 100, false);
        addProcess(tCrop, Materials.Wulfenite, 100, false);
        addProcess(tCrop, Materials.Powellite, 100, false);
        tCrop = ItemList.Crop_Drop_Zinc.get(1, new Object[0]);
        addProcess(tCrop, Materials.Zinc, 100, true);
        addProcess(tCrop, Materials.Sphalerite, 100, false);
        addProcess(tCrop, Materials.Sulfur, 100, false);
        tCrop = ItemList.Crop_Drop_Argentia.get(1, new Object[0]);
        addProcess(tCrop, Materials.Silver, 100, true);
        tCrop = ItemList.Crop_Drop_Aurelia.get(1, new Object[0]);
        addProcess(tCrop, Materials.Gold, 100, true);
        addProcess(tCrop, Materials.Magnetite, Materials.Gold, 100, false);

        // Rare Metals Line
        tCrop = ItemList.Crop_Drop_Bauxite.get(1, new Object[0]);
        addProcess(tCrop,Materials.Aluminium,60, true);
        addProcess(tCrop,Materials.Bauxite,100, false);
        tCrop = ItemList.Crop_Drop_Manganese.get(1, new Object[0]);
        addProcess(tCrop,Materials.Manganese,30, true);
        addProcess(tCrop,Materials.Grossular,100, false);
        addProcess(tCrop,Materials.Spessartine,100, false);
        addProcess(tCrop,Materials.Pyrolusite,100, false);
        addProcess(tCrop,Materials.Tantalite,100, false);
        tCrop = ItemList.Crop_Drop_Ilmenite.get(1, new Object[0]);
        addProcess(tCrop,Materials.Titanium,100, true);
        addProcess(tCrop,Materials.Ilmenite,100, false);
        addProcess(tCrop,Materials.Bauxite,100, false);
        tCrop = ItemList.Crop_Drop_Scheelite.get(1, new Object[0]);
        addProcess(tCrop,Materials.Scheelite,100, true);
        addProcess(tCrop,Materials.Tungstate,100, false);
        addProcess(tCrop,Materials.Lithium,100, false);
        tCrop = ItemList.Crop_Drop_Platinum.get(1, new Object[0]);
        addProcess(tCrop,Materials.Platinum,40, true);
        addProcess(tCrop,Materials.Cooperite,40, false);
        addProcess(tCrop,Materials.Palladium,40, false);
        addProcess(tCrop, Materials.Neodymium, 100, false);
        addProcess(tCrop, Materials.Bastnasite, 100, false);
        tCrop = ItemList.Crop_Drop_Iridium.get(1, new Object[0]);
        addProcess(tCrop,Materials.Iridium,20, true);
        tCrop = ItemList.Crop_Drop_Osmium.get(1, new Object[0]);
        addProcess(tCrop,Materials.Osmium,20, true);

        // Radioactive Line
        tCrop = ItemList.Crop_Drop_Pitchblende.get(1, new Object[0]);
        addProcess(tCrop,Materials.Pitchblende,50, true);
        tCrop = ItemList.Crop_Drop_Uraninite.get(1, new Object[0]);
        addProcess(tCrop,Materials.Uraninite,50, false);
        addProcess(tCrop,Materials.Uranium,50, true);
        addProcess(tCrop,Materials.Pitchblende,50, false);
        addProcess(tCrop,Materials.Uranium235,50, false);
        tCrop = ItemList.Crop_Drop_Thorium.get(1, new Object[0]);
        addProcess(tCrop,Materials.Thorium,50, true);
        tCrop = ItemList.Crop_Drop_Naquadah.get(1, new Object[0]);
        addProcess(tCrop,Materials.Naquadah,10, true);
        addProcess(tCrop,Materials.NaquadahEnriched,10, false);
        addProcess(tCrop,Materials.Naquadria,10, false);

        //Gem Line
        tCrop = ItemList.Crop_Drop_BobsYerUncleRanks.get(1, new Object[0]);
        addProcess(tCrop, Materials.Emerald, 100, true);
        addProcess(tCrop, Materials.Beryllium, 100, false);

        if (!GT_Mod.gregtechproxy.mDisableOldChemicalRecipes) {
            addOldChemicalRecipes();
        }
        addChemicalRecipesShared();
        if (GT_Mod.gregtechproxy.mMoreComplicatedChemicalRecipes) {
            addChemicalRecipesComplicated();
        } else {
            addChemicalRecipesSimple();
        }
        addRecipesMay2017OilRefining();
        addPyrometallurgicalRecipes();
    }

    private void addChemicalRecipesSimple(){
        try {
            InputStream tStream = GT_MachineRecipeLoader.class.getResourceAsStream("/assets/gregtech/recipes/chemical_simple.json");
            if (tStream != null) {
                List<GT_Recipe> tRecipeList = GT_RecipeListJsonReader.readRecipes(new JsonReader(new InputStreamReader(tStream)));
                addRecipesToMap(GT_Recipe_Map.sChemicalRecipes, tRecipeList);
                addRecipesToMap(GT_Recipe_Map.sMultiblockChemicalRecipes, tRecipeList);
                tStream.close();
            }
        } catch (Throwable e) {
            GT_Log.err.println("Error while reading resource chemical_simple.json");
            e.printStackTrace(GT_Log.err);
        }
        try {
            InputStream tStream = GT_MachineRecipeLoader.class.getResourceAsStream("/assets/gregtech/recipes/chemical_simple_small.json");
            if (tStream != null) {
                List<GT_Recipe> tRecipeList = GT_RecipeListJsonReader.readRecipes(new JsonReader(new InputStreamReader(tStream)));
                addRecipesToMap(GT_Recipe_Map.sChemicalRecipes, tRecipeList);
                tStream.close();
            }
        } catch (Throwable e) {
            GT_Log.err.println("Error while reading resource chemical_simple_small.json");
            e.printStackTrace(GT_Log.err);
        }

    }

    private void addChemicalRecipesComplicated(){
        try {
            InputStream tStream = GT_MachineRecipeLoader.class.getResourceAsStream("/assets/gregtech/recipes/chemical_complicated.json");
            if (tStream != null) {
                List<GT_Recipe> tRecipeList = GT_RecipeListJsonReader.readRecipes(new JsonReader(new InputStreamReader(tStream)));
                addRecipesToMap(GT_Recipe_Map.sChemicalRecipes, tRecipeList);
                addRecipesToMap(GT_Recipe_Map.sMultiblockChemicalRecipes, tRecipeList);
                tStream.close();
            }
        } catch (Throwable e) {
            GT_Log.err.println("Error while reading resource chemical_complicated.json");
            e.printStackTrace(GT_Log.err);
        }
        try {
            InputStream tStream = GT_MachineRecipeLoader.class.getResourceAsStream("/assets/gregtech/recipes/chemical_complicated_small.json");
            if (tStream != null) {
                List<GT_Recipe> tRecipeList = GT_RecipeListJsonReader.readRecipes(new JsonReader(new InputStreamReader(tStream)));
                addRecipesToMap(GT_Recipe_Map.sChemicalRecipes, tRecipeList);
                tStream.close();
            }
        } catch (Throwable e) {
            GT_Log.err.println("Error while reading resource chemical_complicated.json");
            e.printStackTrace(GT_Log.err);
        }
        try {
            InputStream tStream = GT_MachineRecipeLoader.class.getResourceAsStream("/assets/gregtech/recipes/chemical_complicated_large.json");
            if (tStream != null) {
                List<GT_Recipe> tRecipeList = GT_RecipeListJsonReader.readRecipes(new JsonReader(new InputStreamReader(tStream)));
                for (GT_Recipe tRecipe : tRecipeList) {
                    // A couple of the recipe maps wrap the recipe in a subclass, but don't override the addRecipe method that takes a GT_Recipe argument.
                    GT_Recipe tAddedRecipe = GT_Recipe_Map.sMultiblockChemicalRecipes.addRecipe(false, tRecipe.mInputs, tRecipe.mOutputs, tRecipe.mSpecialItems,
                            tRecipe.mChances, tRecipe.mFluidInputs, tRecipe.mFluidOutputs, tRecipe.mDuration, tRecipe.mEUt, tRecipe.mSpecialValue);
                    tAddedRecipe.mEnabled = tRecipe.mEnabled;
                    tAddedRecipe.mHidden = tRecipe.mHidden;
                }
                tStream.close();
            }
        } catch (Throwable e) {
            GT_Log.err.println("Error while reading resource chemical_complicated.json");
            e.printStackTrace(GT_Log.err);
        }
        GT_Values.RA.addDefaultPolymerizationRecipes(Materials.VinylAcetate.mFluid, Materials.VinylAcetate.getCells(1), Materials.PolyvinylAcetate.mFluid);

    }

    private void addChemicalRecipesShared(){
        if(!GregTech_API.mIC2Classic){
            GT_Values.RA.addFermentingRecipe(new FluidStack(FluidRegistry.getFluid("ic2biomass"), 100), Materials.FermentedBiomass.getFluid(100), 150, false);

            GT_Values.RA.addPyrolyseRecipe(GT_ModHandler.getIC2Item("biochaff", 1), Materials.Water.getFluid(1500), 2, GT_Values.NI, Materials.FermentedBiomass.getFluid(1500), 200, 10);
            GT_Values.RA.addPyrolyseRecipe(GT_Values.NI, new FluidStack(FluidRegistry.getFluid("ic2biomass"), 1000), 2, GT_Values.NI, Materials.FermentedBiomass.getFluid(1000), 100, 10);
        }
        if(!GregTech_API.mIC2Classic){
            GT_Values.RA.addDistilleryRecipe(17, Materials.FermentedBiomass.getFluid(1000), new FluidStack(FluidRegistry.getFluid("ic2biogas"), 1800), ItemList.IC2_Fertilizer.get(1, new Object[0]), 1600, 8, false);
            GT_Values.RA.addDistilleryRecipe(1, Materials.Methane.getGas(1000), new FluidStack(FluidRegistry.getFluid("ic2biogas"), 3000), GT_Values.NI, 160, 8, false);
        }

        GT_Values.RA.addDefaultPolymerizationRecipes(Materials.Ethylene.mGas, Materials.Ethylene.getCells(1), Materials.Plastic.mStandardMoltenFluid);

        GT_Values.RA.addDefaultPolymerizationRecipes(Materials.Tetrafluoroethylene.mGas, Materials.Tetrafluoroethylene.getCells(1), Materials.Polytetrafluoroethylene.mStandardMoltenFluid);

        GT_Values.RA.addDefaultPolymerizationRecipes(Materials.VinylChloride.mGas, Materials.VinylChloride.getCells(1), Materials.PolyvinylChloride.mStandardMoltenFluid);

        GT_Values.RA.addDefaultPolymerizationRecipes(Materials.Styrene.mFluid, Materials.Styrene.getCells(1), Materials.Polystyrene.mStandardMoltenFluid);

    }

    private void addOldChemicalRecipes() {
        try {
            InputStream tStream = GT_MachineRecipeLoader.class.getResourceAsStream("/assets/gregtech/recipes/chemical_old.json");
            if (tStream != null) {
                List<GT_Recipe> tRecipeList = GT_RecipeListJsonReader.readRecipes(new JsonReader(new InputStreamReader(tStream)));
                addRecipesToMap(GT_Recipe_Map.sChemicalRecipes, tRecipeList);
                addRecipesToMap(GT_Recipe_Map.sMultiblockChemicalRecipes, tRecipeList);
                tStream.close();
            }
        } catch (Throwable e) {
            GT_Log.err.println("Error while reading resource chemical_old.json");
            e.printStackTrace(GT_Log.err);
        }
    }

    private void addRecipesMay2017OilRefining() {
        GT_Values.RA.addUniversalDistillationRecipe(Materials.Gas.getGas(1000),
                new FluidStack[]{Materials.Butane.getGas(60), Materials.Propane.getGas(70), Materials.Ethane.getGas(100), Materials.Methane.getGas(750), Materials.Helium.getGas(20)},
                GT_Values.NI, 240, 120);

        GT_Values.RA.addCentrifugeRecipe(null, null, Materials.Propane.getGas(320), Materials.LPG.getFluid(290), null, null, null, null, null, null, null, 20, 5);
        GT_Values.RA.addCentrifugeRecipe(null, null, Materials.Butane.getGas(320), Materials.LPG.getFluid(370), null, null, null, null, null, null, null, 20, 5);

        GT_Values.RA.addChemicalRecipe(GT_Utility.getIntegratedCircuit(2), GT_Values.NI,                        Materials.Propene.getGas(1000),  Materials.Ethylene.getGas(1000), GT_Values.NI,                   720, 120);
        GT_Values.RA.addChemicalRecipe(Materials.Propene.getCells(1),      GT_Utility.getIntegratedCircuit(2),  GT_Values.NF,                    Materials.Ethylene.getGas(1000), Materials.Empty.getCells(1),    720, 120);
        GT_Values.RA.addChemicalRecipe(Materials.Empty.getCells(1),        GT_Utility.getIntegratedCircuit(12), Materials.Propene.getGas(1000),  GT_Values.NF,                    Materials.Ethylene.getCells(1), 720, 120);
        GT_Values.RA.addChemicalRecipe(Materials.Propene.getCells(1),      GT_Utility.getIntegratedCircuit(12), GT_Values.NF,                    GT_Values.NF,                    Materials.Ethylene.getCells(1), 720, 120);

        GT_Values.RA.addChemicalRecipe(GT_Utility.getIntegratedCircuit(3),  GT_Values.NI,                        Materials.Ethylene.getGas(1000), Materials.Propene.getGas(1000),  GT_Values.NI,                  720, 120);
        GT_Values.RA.addChemicalRecipe(Materials.Ethylene.getCells(1),      GT_Utility.getIntegratedCircuit(3),  GT_Values.NF,                    Materials.Propene.getGas(1000),  Materials.Empty.getCells(1),   720, 120);
        GT_Values.RA.addChemicalRecipe(Materials.Empty.getCells(1),         GT_Utility.getIntegratedCircuit(13), Materials.Ethylene.getGas(1000), GT_Values.NF,                    Materials.Propene.getCells(1), 720, 120);
        GT_Values.RA.addChemicalRecipe(Materials.Ethylene.getCells(1),      GT_Utility.getIntegratedCircuit(13), GT_Values.NF,                    GT_Values.NF,                    Materials.Propene.getCells(1), 720, 120);

        GT_Values.RA.addChemicalRecipe(GT_Utility.getIntegratedCircuit(2),  GT_Values.NI,                        Materials.Butene.getGas(1000), Materials.Ethylene.getGas(1000), GT_Values.NI,                   720, 120);
        GT_Values.RA.addChemicalRecipe(Materials.Butene.getCells(1),        GT_Utility.getIntegratedCircuit(2),  GT_Values.NF,                  Materials.Ethylene.getGas(1000), Materials.Empty.getCells(1),    720, 120);
        GT_Values.RA.addChemicalRecipe(Materials.Empty.getCells(1),         GT_Utility.getIntegratedCircuit(12), Materials.Butene.getGas(1000), GT_Values.NF,                    Materials.Ethylene.getCells(1), 720, 120);
        GT_Values.RA.addChemicalRecipe(Materials.Butene.getCells(1),        GT_Utility.getIntegratedCircuit(12), GT_Values.NF,                  GT_Values.NF,                    Materials.Ethylene.getCells(1), 720, 120);

        GT_Values.RA.addChemicalRecipe(GT_Utility.getIntegratedCircuit(4),  GT_Values.NI,                        Materials.Ethylene.getGas(1000), Materials.Butene.getGas(1000),  GT_Values.NI,                  720, 120);
        GT_Values.RA.addChemicalRecipe(Materials.Ethylene.getCells(1),      GT_Utility.getIntegratedCircuit(4),  GT_Values.NF,                    Materials.Butene.getGas(1000),  Materials.Empty.getCells(1),   720, 120);
        GT_Values.RA.addChemicalRecipe(Materials.Empty.getCells(1),         GT_Utility.getIntegratedCircuit(14), Materials.Ethylene.getGas(1000), GT_Values.NF,                   Materials.Butene.getCells(1),  720, 120);
        GT_Values.RA.addChemicalRecipe(Materials.Ethylene.getCells(1),      GT_Utility.getIntegratedCircuit(14), GT_Values.NF,                    GT_Values.NF,                   Materials.Butene.getCells(1),  720, 120);

        //This piece of code assumes that these Materials are all gases
        Materials[] saturated = new Materials[]{Materials.Ethane, Materials.Propane, Materials.Butane, Materials.Butene};
        Materials[] desaturated = new Materials[]{Materials.Ethylene, Materials.Propene, Materials.Butene, Materials.Butadiene};

        for (int i = 0; i < saturated.length; i++) {
            int sFS = 0; //saturatedFormulaSize
            for (MaterialStack materialStack : saturated[i].mMaterialList) {
                sFS += materialStack.mAmount;
            }
            //Dehydrogenation
            GT_Values.RA.addChemicalRecipe(Materials.Empty.getCells(2),       GT_Utility.getIntegratedCircuit(8),  saturated[i].getGas(sFS * 1000), desaturated[i].getGas((sFS - 2) * 1000), Materials.Hydrogen.getCells(2),   640, 120);
            GT_Values.RA.addChemicalRecipe(Materials.Empty.getCells(sFS - 2), GT_Utility.getIntegratedCircuit(18), saturated[i].getGas(sFS * 1000), Materials.Hydrogen.getGas(2000),         desaturated[i].getCells(sFS - 2), 640, 120);
            GT_Values.RA.addChemicalRecipe(saturated[i].getCells(sFS),        GT_Utility.getIntegratedCircuit(8),  GT_Values.NF,                    desaturated[i].getGas((sFS - 2) * 1000), Materials.Hydrogen.getCells(2),   Materials.Empty.getCells(sFS - 2), 640, 120);
            GT_Values.RA.addChemicalRecipe(saturated[i].getCells(sFS),        GT_Utility.getIntegratedCircuit(18), GT_Values.NF,                    Materials.Hydrogen.getGas(2000),         desaturated[i].getCells(sFS - 2), Materials.Empty.getCells(2), 640, 120);
        }

        GT_Values.RA.addDistillationTowerRecipe(Materials.Ethane.getHydroCracked(1000),
                new FluidStack[]{Materials.Methane.getGas(2000)},
                GT_Values.NI, 120, 120);
        GT_Values.RA.addDistillationTowerRecipe(Materials.Ethane.getSteamCracked(1000),
                new FluidStack[]{Materials.Ethylene.getGas(1000), Materials.Methane.getGas(500)},
                Materials.Carbon.getDustSmall(1), 120, 120);

        GT_Values.RA.addDistillationTowerRecipe(Materials.Propane.getHydroCracked(1000),
                new FluidStack[]{Materials.Ethane.getGas(1000), Materials.Methane.getGas(1000)},
                GT_Values.NI, 120, 120);
        GT_Values.RA.addDistillationTowerRecipe(Materials.Propane.getSteamCracked(1000),
                new FluidStack[]{Materials.Propene.getGas(500), Materials.Ethylene.getGas(1000), Materials.Methane.getGas(500)},
                Materials.Carbon.getDustSmall(1), 120, 120);

        GT_Values.RA.addDistillationTowerRecipe(Materials.Butane.getHydroCracked(1000),
                new FluidStack[]{Materials.Propane.getGas(1000), Materials.Ethane.getGas(1000), Materials.Methane.getGas(1000)},
                GT_Values.NI, 120, 120);
        GT_Values.RA.addDistillationTowerRecipe(Materials.Butane.getSteamCracked(1000),
                new FluidStack[]{Materials.Butadiene.getGas(500), Materials.Propene.getGas(1000), Materials.Ethylene.getGas(1000), Materials.Methane.getGas(500)},
                Materials.Carbon.getDustSmall(1), 120, 120);

        GT_Values.RA.addDistillationTowerRecipe(Materials.Gas.getHydroCracked(1000),
                new FluidStack[]{Materials.Methane.getGas(1500), Materials.Helium.getGas(20)},
                GT_Values.NI, 120, 120);
        GT_Values.RA.addDistillationTowerRecipe(Materials.Gas.getSteamCracked(1000),
                new FluidStack[]{Materials.Butadiene.getGas(60), Materials.Propene.getGas(70), Materials.Ethylene.getGas(100), Materials.Methane.getGas(750), Materials.Helium.getGas(20)},
                Materials.Carbon.getDustSmall(1), 120, 120);

        GT_Values.RA.addDistillationTowerRecipe(Materials.Naphtha.getHydroCracked(1000),
                new FluidStack[]{Materials.Butane.getGas(750), Materials.Propane.getGas(750), Materials.Ethane.getGas(750), Materials.Methane.getGas(750)},
                GT_Values.NI, 120, 120);
        GT_Values.RA.addDistillationTowerRecipe(Materials.Naphtha.getSteamCracked(1000),
                new FluidStack[]{Materials.HeavyFuel.getFluid(100), Materials.LightFuel.getFluid(100), Materials.Toluene.getFluid(200), Materials.Benzene.getFluid(400), Materials.Butadiene.getGas(400),
                        Materials.Propene.getGas(600), Materials.Ethylene.getGas(600), Materials.Methane.getGas(600)},
                Materials.Carbon.getDustSmall(1), 120, 120);

        GT_Values.RA.addDistilleryRecipe(3, Materials.Naphtha.getFluid(1000), Materials.Toluene.getFluid(200), 120, 120, false);
        GT_Values.RA.addDistilleryRecipe(4, Materials.Naphtha.getFluid(1000), Materials.Benzene.getFluid(400), 120, 120, false);
        GT_Values.RA.addDistilleryRecipe(5, Materials.Naphtha.getFluid(1000), Materials.Butadiene.getGas(400), 120, 120, false);
        GT_Values.RA.addDistilleryRecipe(6, Materials.Naphtha.getFluid(1000), Materials.Propene.getGas(600),   120, 120, false);
        GT_Values.RA.addDistilleryRecipe(7, Materials.Naphtha.getFluid(1000), Materials.Ethylene.getGas(600),  120, 120, false);
        GT_Values.RA.addDistilleryRecipe(8, Materials.Naphtha.getFluid(1000), Materials.Methane.getGas(600),   120, 120, false);

        GT_Values.RA.addDistillationTowerRecipe(Materials.LightFuel.getHydroCracked(1000),
                new FluidStack[]{Materials.Naphtha.getFluid(800), Materials.Butane.getGas(400), Materials.Propane.getGas(400), Materials.Ethane.getGas(200), Materials.Methane.getGas(200)},
                GT_Values.NI, 120, 120);
        GT_Values.RA.addDistillationTowerRecipe(Materials.LightFuel.getSteamCracked(1000),
                new FluidStack[]{Materials.HeavyFuel.getFluid(100), Materials.Naphtha.getFluid(100), Materials.Toluene.getFluid(150), Materials.Benzene.getFluid(300), Materials.Butadiene.getGas(300),
                        Materials.Propene.getGas(450), Materials.Ethylene.getGas(450), Materials.Methane.getGas(450)},
                Materials.Carbon.getDustSmall(1), 120, 120);


        GT_Values.RA.addDistillationTowerRecipe(Materials.HeavyFuel.getHydroCracked(1000),
                new FluidStack[]{Materials.LightFuel.getFluid(800), Materials.Naphtha.getFluid(400), Materials.Butane.getGas(100), Materials.Propane.getGas(100), Materials.Ethane.getGas(75), Materials.Methane.getGas(75)},
                GT_Values.NI, 120, 120);
        GT_Values.RA.addDistillationTowerRecipe(Materials.HeavyFuel.getSteamCracked(1000),
                new FluidStack[]{Materials.LightFuel.getFluid(100), Materials.Naphtha.getFluid(100), Materials.Toluene.getFluid(100), Materials.Benzene.getFluid(200), Materials.Butadiene.getGas(200),
                        Materials.Propene.getGas(300), Materials.Ethylene.getGas(300), Materials.Methane.getGas(300)},
                Materials.Carbon.getDustSmall(1), 120, 120);
        //Recipes for gasoline

        GT_Values.RA.addChemicalRecipe(                   Materials.Nitrogen.getCells(2), GT_Utility.getIntegratedCircuit(2), Materials.Oxygen.getGas(1000),   Materials.NitrousOxide.getGas(3000), Materials.Empty.getCells(2),                      200, 30);
        GT_Values.RA.addChemicalRecipe(                   Materials.Oxygen.getCells(1),   GT_Utility.getIntegratedCircuit(2), Materials.Nitrogen.getGas(2000), Materials.NitrousOxide.getGas(3000), Materials.Empty.getCells(1),                      200, 30);
        GT_Values.RA.addChemicalRecipeForBasicMachineOnly(Materials.Oxygen.getCells(1),   Materials.Empty.getCells(2),        Materials.Nitrogen.getGas(2000), GT_Values.NF,                        Materials.NitrousOxide.getCells(3), GT_Values.NI, 200, 30);
        GT_Values.RA.addChemicalRecipeForBasicMachineOnly(Materials.Nitrogen.getCells(2), Materials.Empty.getCells(1),        Materials.Oxygen.getGas(1000),   GT_Values.NF,                        Materials.NitrousOxide.getCells(3), GT_Values.NI, 200, 30);
        GT_Values.RA.addChemicalRecipeForBasicMachineOnly(Materials.Nitrogen.getCells(2), Materials.Oxygen.getCells(1),       GT_Values.NF,                    GT_Values.NF,                        Materials.NitrousOxide.getCells(3), GT_Values.NI, 200, 30);
        GT_Values.RA.addChemicalRecipe(Materials.Ethanol.getCells(1), Materials.Butene.getCells(1), GT_Values.NF, GT_Values.NF, Materials.AntiKnock.getCells(2), 400, 480);
        GT_Values.RA.addMixerRecipe(Materials.Naphtha.getCells(16), Materials.Toluene.getCells(2), Materials.Methanol.getCells(1), (GT_Mod.gregtechproxy.mMoreComplicatedChemicalRecipes ? Materials.Acetone : Materials.AceticAcid).getCells(1), GT_Values.NF, GT_Values.NF, Materials.GasolineRegular.getCells(20), 200, 120);
        GT_Values.RA.addMixerRecipe(Materials.GasolineRegular.getCells(20), Materials.AntiKnock.getCells(3), Materials.NitrousOxide.getCells(6), Materials.Toluene.getCells(1), GT_Values.NF, GT_Values.NF, Materials.GasolinePremium.getCells(30), 200, 120);
    }

    public void addPotionRecipes(String aName,ItemStack aItem){
        //normal
        GT_Values.RA.addBrewingRecipe(aItem, FluidRegistry.getFluid("potion.awkward"), FluidRegistry.getFluid("potion."+aName), false);
        //strong
        GT_Values.RA.addBrewingRecipe(aItem, FluidRegistry.getFluid("potion.thick"), FluidRegistry.getFluid("potion."+aName+".strong"), false);
        //long
        GT_Values.RA.addBrewingRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1L), FluidRegistry.getFluid("potion."+aName), FluidRegistry.getFluid("potion."+aName+".long"), false);
        //splash
        if(!(FluidRegistry.getFluid("potion."+aName)==null||FluidRegistry.getFluid("potion."+aName+".splash")==null))
            GT_Values.RA.addMixerRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Gunpowder, 1L), null, null, null, new FluidStack(FluidRegistry.getFluid("potion."+aName),750), new FluidStack(FluidRegistry.getFluid("potion."+aName+".splash"),750), null, 200, 24);
        //splash strong
        if(!(FluidRegistry.getFluid("potion."+aName+".strong")==null||FluidRegistry.getFluid("potion."+aName+".strong.splash")==null))
            GT_Values.RA.addMixerRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Gunpowder, 1L), null, null, null, new FluidStack(FluidRegistry.getFluid("potion."+aName+".strong"),750), new FluidStack(FluidRegistry.getFluid("potion."+aName+".strong.splash"),750), null, 200, 24);
        //splash long
        if(!(FluidRegistry.getFluid("potion."+aName+".long")==null||FluidRegistry.getFluid("potion."+aName+".long.splash")==null))
            GT_Values.RA.addMixerRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Gunpowder, 1L), null, null, null, new FluidStack(FluidRegistry.getFluid("potion."+aName+".long"),750), new FluidStack(FluidRegistry.getFluid("potion."+aName+".long.splash"),750), null, 200, 24);
    }

    /**
     * Adds recipes related to producing Steel in a Primitive Blast Furnace.
     * Adds recipes related to roasting sulfuric ores and reducing oxidic ores in the Electric Blast Furnace.
     */
    private void addPyrometallurgicalRecipes() {
        GT_Values.RA.addPrimitiveBlastRecipe(Materials.Iron.getIngots(1), GT_Values.NI, 4, Materials.Steel.getIngots(1), GT_Values.NI, 7200);
        GT_Values.RA.addPrimitiveBlastRecipe(Materials.Iron.getDust(1), GT_Values.NI, 4, Materials.Steel.getIngots(1), GT_Values.NI, 7200);
        GT_Values.RA.addPrimitiveBlastRecipe(Materials.Iron.getBlocks(1), GT_Values.NI, 36, Materials.Steel.getIngots(9), GT_Values.NI, 64800);
        GT_Values.RA.addPrimitiveBlastRecipe(Materials.Steel.getDust(1), GT_Values.NI, 2, Materials.Steel.getIngots(1), GT_Values.NI, 7200);

        //Roasting
        GT_Values.RA.addBlastRecipe(Materials.Tetrahedrite.getDust(1), GT_Values.NI, Materials.Oxygen.getGas(3000), Materials.SulfurDioxide.getGas(2000), Materials.CupricOxide.getDust(1), Materials.AntimonyTrioxide.getDustTiny(3), 120, 120, 1200);
        GT_Values.RA.addBlastRecipe(Materials.Chalcopyrite.getDust(1), Materials.SiliconDioxide.getDust(1), Materials.Oxygen.getGas(3000), Materials.SulfurDioxide.getGas(2000), Materials.CupricOxide.getDust(1), Materials.Ferrosilite.getDust(1), 120, 120, 1200);

        GT_Values.RA.addBlastRecipe(Materials.Pyrite.getDust(1), GT_Values.NI, Materials.Oxygen.getGas(3000), Materials.SulfurDioxide.getGas(2000), Materials.BandedIron.getDust(1), Materials.Ash.getDustTiny(1), 120, 120, 1200);

        GT_Values.RA.addBlastRecipe(Materials.Pentlandite.getDust(1), GT_Values.NI, Materials.Oxygen.getGas(3000), Materials.SulfurDioxide.getGas(1000), Materials.Garnierite.getDust(1), Materials.Ash.getDustTiny(1), 120, 120, 1200);

        GT_Values.RA.addBlastRecipe(Materials.Sphalerite.getDust(1), GT_Values.NI, Materials.Oxygen.getGas(3000), Materials.SulfurDioxide.getGas(1000), Materials.Zincite.getDust(1), Materials.Ash.getDustTiny(1), 120, 120, 1200);

        GT_Values.RA.addBlastRecipe(Materials.Cobaltite.getDust(1), GT_Values.NI, Materials.Oxygen.getGas(3000), Materials.SulfurDioxide.getGas(1000), Materials.CobaltOxide.getDust(1), Materials.ArsenicTrioxide.getDust(1), 120, 120, 1200);

        GT_Values.RA.addBlastRecipe(Materials.Stibnite.getDust(1), GT_Values.NI, Materials.Oxygen.getGas(3000), Materials.SulfurDioxide.getGas(1500), Materials.AntimonyTrioxide.getDust(1), Materials.Ash.getDustTiny(1), 120, 120, 1200);

        GT_Values.RA.addBlastRecipe(Materials.Galena.getDust(1), GT_Values.NI, Materials.Oxygen.getGas(3000), Materials.SulfurDioxide.getGas(1000), Materials.Massicot.getDust(1), Materials.Silver.getIngots(1), 120, 120, 1200);

        //Carbothermic Reduction
        int outputIngotAmount = GT_Mod.gregtechproxy.mMixedOreOnlyYieldsTwoThirdsOfPureOre ? 2 : 3;
        GT_Values.RA.addBlastRecipe(Materials.CupricOxide.getDust(2),         Materials.Carbon.getDust(1),      GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Copper.getIngots(outputIngotAmount),   Materials.Ash.getDustTiny(2), 240, 120, 1200);
        GT_Values.RA.addBlastRecipe(Materials.Malachite.getDust(2),           Materials.Carbon.getDust(1),      GT_Values.NF, Materials.CarbonDioxide.getGas(3000),  Materials.Copper.getIngots(outputIngotAmount),   Materials.Ash.getDustTiny(2), 240, 120, 1200);

        GT_Values.RA.addBlastRecipe(Materials.AntimonyTrioxide.getDust(2),    Materials.Carbon.getDust(1),      GT_Values.NF, Materials.CarbonDioxide.getGas(3000),  Materials.Antimony.getIngots(outputIngotAmount), Materials.Ash.getDustTiny(2), 240, 120, 1200);

        GT_Values.RA.addBlastRecipe(Materials.BandedIron.getDust(2),          Materials.Carbon.getDust(1),      GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Iron.getIngots(outputIngotAmount),     Materials.Ash.getDustTiny(2), 240, 120, 1200);
        GT_Values.RA.addBlastRecipe(Materials.Magnetite.getDust(2),           Materials.Carbon.getDust(1),      GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Iron.getIngots(outputIngotAmount),     Materials.Ash.getDustTiny(2), 240, 120, 1200);
        GT_Values.RA.addBlastRecipe(Materials.YellowLimonite.getDust(2),      Materials.Carbon.getDust(1),      GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Iron.getIngots(outputIngotAmount),     Materials.Ash.getDustTiny(2), 240, 120, 1200);
        GT_Values.RA.addBlastRecipe(Materials.BrownLimonite.getDust(2),       Materials.Carbon.getDust(1),      GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Iron.getIngots(outputIngotAmount),     Materials.Ash.getDustTiny(2), 240, 120, 1200);
        GT_Values.RA.addBlastRecipe(Materials.BasalticMineralSand.getDust(2), Materials.Carbon.getDust(1),      GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Iron.getIngots(outputIngotAmount),     Materials.Ash.getDustTiny(2), 240, 120, 1200);
        GT_Values.RA.addBlastRecipe(Materials.GraniticMineralSand.getDust(2), Materials.Carbon.getDust(1),      GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Iron.getIngots(outputIngotAmount),     Materials.Ash.getDustTiny(2), 240, 120, 1200);

        GT_Values.RA.addBlastRecipe(Materials.Cassiterite.getDust(2),         Materials.Carbon.getDust(1),      GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Tin.getIngots(outputIngotAmount),      Materials.Ash.getDustTiny(2), 240, 120, 1200);
        GT_Values.RA.addBlastRecipe(Materials.CassiteriteSand.getDust(2),     Materials.Carbon.getDust(1),      GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Tin.getIngots(outputIngotAmount),      Materials.Ash.getDustTiny(2), 240, 120, 1200);

        GT_Values.RA.addBlastRecipe(Materials.Garnierite.getDust(2),          Materials.Carbon.getDust(1),      GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Nickel.getIngots(outputIngotAmount),   Materials.Ash.getDustTiny(2), 240, 120, 1200);

        GT_Values.RA.addBlastRecipe(Materials.CobaltOxide.getDust(2),         Materials.Carbon.getDust(1),      GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Cobalt.getIngots(outputIngotAmount),   Materials.Ash.getDustTiny(2), 240, 120, 1200);

        GT_Values.RA.addBlastRecipe(Materials.ArsenicTrioxide.getDust(2),     Materials.Carbon.getDust(1),      GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Arsenic.getIngots(outputIngotAmount),  Materials.Ash.getDustTiny(2), 240, 120, 1200);

        GT_Values.RA.addBlastRecipe(Materials.Massicot.getDust(2),            Materials.Carbon.getDust(1),      GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Lead.getIngots(outputIngotAmount),     Materials.Ash.getDustTiny(2), 240, 120, 1200);

        GT_Values.RA.addBlastRecipe(Materials.Zincite.getDust(2),             Materials.Carbon.getDust(1),      GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Zinc.getIngots(outputIngotAmount),     Materials.Ash.getDustTiny(2), 240, 120, 1200);

        GT_Values.RA.addBlastRecipe(Materials.SiliconDioxide.getDust(1),      Materials.Carbon.getDust(2),      GT_Values.NF, Materials.CarbonMonoxide.getGas(2000), Materials.Silicon.getIngots(1),  Materials.Ash.getDustTiny(1), 240, 120, 1200);

        if (GT_Mod.gregtechproxy.mMixedOreOnlyYieldsTwoThirdsOfPureOre) {
            GT_Values.RA.addBlastRecipe(Materials.CupricOxide.getDust(2),         Materials.Carbon.getDustSmall(4), GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Copper.getIngots(outputIngotAmount),   Materials.Ash.getDustTiny(2), 240, 120, 1200);
            GT_Values.RA.addBlastRecipe(Materials.Malachite.getDust(2),           Materials.Carbon.getDustSmall(4), GT_Values.NF, Materials.CarbonDioxide.getGas(3000),  Materials.Copper.getIngots(outputIngotAmount),   Materials.Ash.getDustTiny(2), 240, 120, 1200);
            GT_Values.RA.addBlastRecipe(Materials.AntimonyTrioxide.getDust(2),    Materials.Carbon.getDustSmall(4), GT_Values.NF, Materials.CarbonDioxide.getGas(3000),  Materials.Antimony.getIngots(outputIngotAmount), Materials.Ash.getDustTiny(2), 240, 120, 1200);
            GT_Values.RA.addBlastRecipe(Materials.BandedIron.getDust(2),          Materials.Carbon.getDustSmall(4), GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Iron.getIngots(outputIngotAmount),     Materials.Ash.getDustTiny(2), 240, 120, 1200);
            GT_Values.RA.addBlastRecipe(Materials.Magnetite.getDust(2),           Materials.Carbon.getDustSmall(4), GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Iron.getIngots(outputIngotAmount),     Materials.Ash.getDustTiny(2), 240, 120, 1200);
            GT_Values.RA.addBlastRecipe(Materials.YellowLimonite.getDust(2),      Materials.Carbon.getDustSmall(4), GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Iron.getIngots(outputIngotAmount),     Materials.Ash.getDustTiny(2), 240, 120, 1200);
            GT_Values.RA.addBlastRecipe(Materials.BrownLimonite.getDust(2),       Materials.Carbon.getDustSmall(4), GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Iron.getIngots(outputIngotAmount),     Materials.Ash.getDustTiny(2), 240, 120, 1200);
            GT_Values.RA.addBlastRecipe(Materials.BasalticMineralSand.getDust(2), Materials.Carbon.getDustSmall(4), GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Iron.getIngots(outputIngotAmount),     Materials.Ash.getDustTiny(2), 240, 120, 1200);
            GT_Values.RA.addBlastRecipe(Materials.GraniticMineralSand.getDust(2), Materials.Carbon.getDustSmall(4), GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Iron.getIngots(outputIngotAmount),     Materials.Ash.getDustTiny(2), 240, 120, 1200);
            GT_Values.RA.addBlastRecipe(Materials.Cassiterite.getDust(2),         Materials.Carbon.getDustSmall(4), GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Tin.getIngots(outputIngotAmount),      Materials.Ash.getDustTiny(2), 240, 120, 1200);
            GT_Values.RA.addBlastRecipe(Materials.CassiteriteSand.getDust(2),     Materials.Carbon.getDustSmall(4), GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Tin.getIngots(outputIngotAmount),      Materials.Ash.getDustTiny(2), 240, 120, 1200);
            GT_Values.RA.addBlastRecipe(Materials.Garnierite.getDust(2),          Materials.Carbon.getDustSmall(4), GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Nickel.getIngots(outputIngotAmount),   Materials.Ash.getDustTiny(2), 240, 120, 1200);
            GT_Values.RA.addBlastRecipe(Materials.CobaltOxide.getDust(2),         Materials.Carbon.getDustSmall(4), GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Cobalt.getIngots(outputIngotAmount),   Materials.Ash.getDustTiny(2), 240, 120, 1200);
            GT_Values.RA.addBlastRecipe(Materials.ArsenicTrioxide.getDust(2),     Materials.Carbon.getDustSmall(4), GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Arsenic.getIngots(outputIngotAmount),  Materials.Ash.getDustTiny(2), 240, 120, 1200);
            GT_Values.RA.addBlastRecipe(Materials.Massicot.getDust(2),            Materials.Carbon.getDustSmall(4), GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Lead.getIngots(outputIngotAmount),     Materials.Ash.getDustTiny(2), 240, 120, 1200);
            GT_Values.RA.addBlastRecipe(Materials.Zincite.getDust(2),             Materials.Carbon.getDustSmall(4), GT_Values.NF, Materials.CarbonDioxide.getGas(1000),  Materials.Zinc.getIngots(outputIngotAmount),     Materials.Ash.getDustTiny(2), 240, 120, 1200);
        }
    }

    private static void addRecipesToMap(GT_Recipe_Map aMap, List<GT_Recipe> aList) {
        for (GT_Recipe tRecipe : aList) {
            // A couple of the recipe maps wrap the recipe in a subclass, but don't override the addRecipe method that takes a GT_Recipe argument.
            GT_Recipe tAddedRecipe = aMap.addRecipe(false, tRecipe.mInputs, tRecipe.mOutputs, tRecipe.mSpecialItems,
                    tRecipe.mChances, tRecipe.mFluidInputs, tRecipe.mFluidOutputs, tRecipe.mDuration, tRecipe.mEUt, tRecipe.mSpecialValue);
            if (tAddedRecipe != null) {
                tAddedRecipe.mEnabled = tRecipe.mEnabled;
                tAddedRecipe.mHidden = tRecipe.mHidden;
            }
        }
    }
    
}
