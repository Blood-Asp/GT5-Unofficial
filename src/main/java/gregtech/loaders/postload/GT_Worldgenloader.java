package gregtech.loaders.postload;

import cpw.mods.fml.common.Loader;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.util.GT_Config;
import gregtech.common.*;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class GT_Worldgenloader implements Runnable {
    public void addOldOreRecordToConfig(String name, int minY, int maxY, int weight, int density, int size, boolean over, boolean nether, boolean end, boolean moon, boolean mars, boolean asteroids, Materials p, Materials s, Materials b, Materials a) {
        String oldCatName = "worldgen.ore.mix." + (name.startsWith("custom") ? name.replace('_', '.') : name);
        ConfigCategory oldCat = GregTech_API.oldWorldgenFile != null && GregTech_API.oldWorldgenFile.hasCategory(oldCatName)
                ? GregTech_API.oldWorldgenFile.getCategory(oldCatName) : null;
        Configuration c = GregTech_API.advancedWorldgenFile.mConfig;

        List<String> dims = new ArrayList<>();
        if (oldCat != null ? GT_Config.findProperty(oldCat, "Overworld", over).getBoolean() : over) {
            dims.add("Surface");
        }
        if (oldCat != null ? GT_Config.findProperty(oldCat, "Nether", nether).getBoolean() : nether) {
            dims.add("Hell");
        }
        if (oldCat != null) {
            if (GT_Config.findProperty(oldCat, "TheEnd", end).getBoolean()) {
                dims.add("End");
            }
            if (GT_Config.findProperty(oldCat, "EndAsteroid", end).getBoolean()) {
                dims.add("EndAsteroids");
            }
        } else if (end) {
            dims.add("End");
            dims.add("EndAsteroids");
        }
        if (oldCat != null ? GT_Config.findProperty(oldCat, "Moon", moon).getBoolean() : moon) {
            dims.add("Moon");
        }
        if (oldCat != null ? GT_Config.findProperty(oldCat, "Mars", mars).getBoolean() : mars) {
            dims.add("Mars");
        }
        if (oldCat != null ? GT_Config.findProperty(oldCat, "Asteroid", asteroids).getBoolean() : asteroids) {
            dims.add("Asteroids");
        }

        String category = "generation.veins." + name;
        c.get(category, "Dimensions", dims.toArray(new String[dims.size()])).getStringList();
        c.get(category, "Density", oldCat != null ? GT_Config.findProperty(oldCat, "Density", density).getInt() : density).getInt();
        c.get(category, "Weight", oldCat != null ? GT_Config.findProperty(oldCat, "RandomWeight", weight).getInt() : weight).getInt();
        c.get(category, "Size", oldCat != null ? GT_Config.findProperty(oldCat, "Size", size).getInt() : size).getInt();
        c.get(category, "SizeY", 7).getInt();
        c.get(category, "MaxHeight", oldCat != null ? GT_Config.findProperty(oldCat, "MaxHeight", maxY).getInt() : maxY).getInt();
        c.get(category, "MinHeight", oldCat != null ? GT_Config.findProperty(oldCat, "MinHeight", minY).getInt() : minY).getInt();

        List<GT_Worldgen_Layer.WeightedOre> oreList = new ArrayList<>();
        addOreToList(oreList, oldCat != null ? GT_Config.findProperty(oldCat, "OrePrimaryLayer", p.mMetaItemSubID).getInt() : p.mMetaItemSubID, 3);
        addOreToList(oreList, oldCat != null ? GT_Config.findProperty(oldCat, "OreSecondaryLayer", s.mMetaItemSubID).getInt() : s.mMetaItemSubID, 3);
        addOreToList(oreList, oldCat != null ? GT_Config.findProperty(oldCat, "OreSporadiclyInbetween", b.mMetaItemSubID).getInt() : b.mMetaItemSubID, 1);
        addOreToList(oreList, oldCat != null ? GT_Config.findProperty(oldCat, "OreSporaticlyAround", a.mMetaItemSubID).getInt() : a.mMetaItemSubID, 1);

        String[] ores = new String[oreList.size()];
        for (int i = 0; i < oreList.size(); i++) {
            ores[i] = oreList.get(i).id + "=" + oreList.get(i).weight;
        }
        c.get(category, "Ores", ores).getStringList();
    }

    private void addOreToList(List<GT_Worldgen_Layer.WeightedOre> list, int id, int weight) {
        boolean isExist = false;
        for (GT_Worldgen_Layer.WeightedOre wo : list) {
            if (wo.id != id) continue;
            isExist = true;
            wo.weight += weight;
            break;
        }
        if (!isExist) {
            list.add(new GT_Worldgen_Layer.WeightedOre(id, weight));
        }
    }

    public void addOldSmallOreRecordToConfig(String name, int minY, int maxY, int amount, boolean over, boolean nether, boolean end, boolean moon, boolean mars, boolean asteroids, Materials p) {
        String category = "generation.small." + name;
        Configuration c = GregTech_API.advancedWorldgenFile.mConfig;
        List<String> list = new ArrayList<>();
        if (over) {
            list.add("Surface");
        }
        if (nether) {
            list.add("Hell");
        }
        if (end) {
            list.add("End");
        }
        if (moon) {
            list.add("Moon");
        }
        if (mars) {
            list.add("Mars");
        }
        if (asteroids) {
            list.add("Asteroids");
        }
        c.get(category, "Dimensions", list.toArray(new String[list.size()])).getStringList();
        c.get(category, "Amount", amount).getInt();
        c.get(category, "MaxHeight", maxY).getInt();
        c.get(category, "MinHeight", minY).getInt();
        c.get(category, "Ore", p.mMetaItemSubID).getInt();
    }

    public void addOldStoneRecordToConfig(String name, Block block, int meta, int amount, int size, int probability, int minY, int maxY) {
        String category = "generation.blocks." + name;
        Configuration c = GregTech_API.advancedWorldgenFile.mConfig;
        c.get(category, "Dimensions", new String[]{"Surface"}).getStringList();
        c.get(category, "Amount", amount).getInt();
        c.get(category, "Size", size).getInt();
        c.get(category, "Probability", probability).getInt();
        c.get(category, "Metadata", meta).getInt();
        c.get(category, "MaxHeight", maxY).getInt();
        c.get(category, "MinHeight", minY).getInt();
        c.get(category, "GenerateInVoid", false).getBoolean();
        c.get(category, "Block", Block.blockRegistry.getNameForObject(block)).getString();
    }

    public void addOldAsteroidsRecordToConfig(String name, Block block, int meta, int probability, int minSize, int maxSize, String dimension) {
        String category = "generation.asteroids." + name;
        Configuration c = GregTech_API.advancedWorldgenFile.mConfig;
        c.get(category, "Dimensions", new String[]{dimension}).getStringList();
        c.get(category, "Probability", probability).getInt();
        c.get(category, "Metadata", meta).getInt();
        c.get(category, "MaxSize", maxSize).getInt();
        c.get(category, "MinSize", minSize).getInt();
        c.get(category, "Block", Block.blockRegistry.getNameForObject(block)).getString();
    }

    public void run() {
        boolean tPFAA = Loader.isModLoaded("PFAAGeologica") && GT_Config.getWorldgenConfig("general", "AutoDetectPFAA", true);

        new GT_Worldgenerator();

        Configuration config = GregTech_API.advancedWorldgenFile.mConfig;
        if (!config.hasCategory("generation.blocks")) {
            addOldStoneRecordToConfig("blackgranite_tiny", GregTech_API.sBlockGranites, 0, 1, 50, 48, 0, 120);
            addOldStoneRecordToConfig("blackgranite_small", GregTech_API.sBlockGranites, 0, 1, 100, 96, 0, 120);
            addOldStoneRecordToConfig("blackgranite_medium", GregTech_API.sBlockGranites, 0, 1, 200, 144, 0, 120);
            addOldStoneRecordToConfig("blackgranite_large", GregTech_API.sBlockGranites, 0, 1, 300, 192, 0, 120);
            addOldStoneRecordToConfig("blackgranite_huge", GregTech_API.sBlockGranites, 0, 1, 400, 240, 0, 120);
            addOldStoneRecordToConfig("redgranite_tiny", GregTech_API.sBlockGranites, 8, 1, 50, 48, 0, 120);
            addOldStoneRecordToConfig("redgranite_small", GregTech_API.sBlockGranites, 8, 1, 100, 96, 0, 120);
            addOldStoneRecordToConfig("redgranite_medium", GregTech_API.sBlockGranites, 8, 1, 200, 144, 0, 120);
            addOldStoneRecordToConfig("redgranite_large", GregTech_API.sBlockGranites, 8, 1, 300, 192, 0, 120);
            addOldStoneRecordToConfig("redgranite_huge", GregTech_API.sBlockGranites, 8, 1, 400, 240, 0, 120);
            addOldStoneRecordToConfig("marble_tiny", GregTech_API.sBlockStones, 0, 1, 50, 48, 0, 120);
            addOldStoneRecordToConfig("marble_small", GregTech_API.sBlockStones, 0, 1, 100, 96, 0, 120);
            addOldStoneRecordToConfig("marble_medium", GregTech_API.sBlockStones, 0, 1, 200, 144, 0, 120);
            addOldStoneRecordToConfig("marble_large", GregTech_API.sBlockStones, 0, 1, 300, 192, 0, 120);
            addOldStoneRecordToConfig("marble_huge", GregTech_API.sBlockStones, 0, 1, 400, 240, 0, 120);
            addOldStoneRecordToConfig("basalt_tiny", GregTech_API.sBlockStones, 8, 1, 50, 48, 0, 120);
            addOldStoneRecordToConfig("basalt_small", GregTech_API.sBlockStones, 8, 1, 100, 96, 0, 120);
            addOldStoneRecordToConfig("basalt_medium", GregTech_API.sBlockStones, 8, 1, 200, 144, 0, 120);
            addOldStoneRecordToConfig("basalt_large", GregTech_API.sBlockStones, 8, 1, 300, 192, 0, 120);
            addOldStoneRecordToConfig("basalt_huge", GregTech_API.sBlockStones, 8, 1, 400, 240, 0, 120);
        }

        for (ConfigCategory c : config.getCategory("generation.blocks").getChildren()) {
            int minH = c.get("MinHeight").getInt(), maxH = c.get("MaxHeight").getInt(), amount = c.get("Amount").getInt(), meta = c.get("Metadata").getInt(), size = c.get("Size").getInt(), prob = c.get("Probability").getInt();
            String block = c.get("Block").getString();
            boolean allow = c.get("GenerateInVoid").getBoolean();
            String[] dims = c.get("Dimensions").getStringList();
            if (minH > -1 && maxH > 0 && amount > 0 && meta > -1 && size > -1 && block != null && dims.length > 0) {
                new GT_Worldgen_Stone(c.getName(), dims, block, meta, amount, size, prob, minH, maxH, allow);
            }
        }

        if (!config.hasCategory("generation.small")) {
            addOldSmallOreRecordToConfig("copper", 60, 120, 32, !tPFAA, true, true, true, true, false, Materials.Copper);
            addOldSmallOreRecordToConfig("tin", 60, 120, 32, !tPFAA, true, true, true, true, true, Materials.Tin);
            addOldSmallOreRecordToConfig("bismuth", 80, 120, 8, !tPFAA, true, false, true, true, false, Materials.Bismuth);
            addOldSmallOreRecordToConfig("coal", 60, 100, 24, !tPFAA, false, false, false, false, false, Materials.Coal);
            addOldSmallOreRecordToConfig("iron", 40, 80, 16, !tPFAA, true, true, true, true, false, Materials.Iron);
            addOldSmallOreRecordToConfig("lead", 40, 80, 16, !tPFAA, true, true, true, true, true, Materials.Lead);
            addOldSmallOreRecordToConfig("zinc", 30, 60, 12, !tPFAA, true, true, true, true, false, Materials.Zinc);
            addOldSmallOreRecordToConfig("gold", 20, 40, 8, !tPFAA, true, true, true, true, true, Materials.Gold);
            addOldSmallOreRecordToConfig("silver", 20, 40, 8, !tPFAA, true, true, true, true, true, Materials.Silver);
            addOldSmallOreRecordToConfig("nickel", 20, 40, 8, !tPFAA, true, true, true, true, true, Materials.Nickel);
            addOldSmallOreRecordToConfig("lapis", 20, 40, 4, !tPFAA, false, false, true, false, true, Materials.Lapis);
            addOldSmallOreRecordToConfig("diamond", 5, 10, 2, !tPFAA, true, false, true, true, true, Materials.Diamond);
            addOldSmallOreRecordToConfig("emerald", 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Emerald);
            addOldSmallOreRecordToConfig("ruby", 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Ruby);
            addOldSmallOreRecordToConfig("sapphire", 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Sapphire);
            addOldSmallOreRecordToConfig("greensapphire", 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.GreenSapphire);
            addOldSmallOreRecordToConfig("olivine", 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Olivine);
            addOldSmallOreRecordToConfig("topaz", 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Topaz);
            addOldSmallOreRecordToConfig("tanzanite", 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Tanzanite);
            addOldSmallOreRecordToConfig("amethyst", 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Amethyst);
            addOldSmallOreRecordToConfig("opal", 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Opal);
            addOldSmallOreRecordToConfig("jasper", 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Jasper);
            addOldSmallOreRecordToConfig("bluetopaz", 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.BlueTopaz);
            addOldSmallOreRecordToConfig("amber", 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Amber);
            addOldSmallOreRecordToConfig("foolsruby", 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.FoolsRuby);
            addOldSmallOreRecordToConfig("garnetred", 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.GarnetRed);
            addOldSmallOreRecordToConfig("garnetyellow", 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.GarnetYellow);
            addOldSmallOreRecordToConfig("redstone", 5, 20, 8, !tPFAA, true, false, true, true, true, Materials.Redstone);
            addOldSmallOreRecordToConfig("platinum", 20, 40, 8, false, false, true, false, true, true, Materials.Platinum);
            addOldSmallOreRecordToConfig("iridium", 20, 40, 8, false, false, true, false, true, true, Materials.Iridium);
            addOldSmallOreRecordToConfig("netherquartz", 30, 120, 64, false, true, false, false, false, false, Materials.NetherQuartz);
            addOldSmallOreRecordToConfig("saltpeter", 10, 60, 8, false, true, false, false, false, false, Materials.Saltpeter);
            addOldSmallOreRecordToConfig("sulfur_n", 10, 60, 32, false, true, false, false, false, false, Materials.Sulfur);
            addOldSmallOreRecordToConfig("sulfur_o", 5, 15, 8, !tPFAA, false, false, false, false, false, Materials.Sulfur);
        }

        for (ConfigCategory c : config.getCategory("generation.small").getChildren()) {
            int minH = c.get("MinHeight").getInt(), maxH = c.get("MaxHeight").getInt(), amount = c.get("Amount").getInt(), ore = c.get("Ore").getInt();
            String[] dim = c.get("Dimensions").getStringList();
            if (minH > -1 && maxH > 0 && amount > 0 && ore > 0 && dim.length > 0) {
                new GT_Worldgen_SmallPieces(c.getName(), dim, minH, maxH, amount, ore);
            }
        }

        if (GregTech_API.mImmersiveEngineering && GT_Mod.gregtechproxy.mImmersiveEngineeringRecipes) {
            blusunrize.immersiveengineering.api.tool.ExcavatorHandler.mineralList.clear();
            blusunrize.immersiveengineering.api.tool.ExcavatorHandler.mineralCache.clear();
        }

        if (!config.hasCategory("generation.veins")) {
            addOldOreRecordToConfig("naquadah", 10, 60, 10, 5, 32, false, false, true, false, true, true, Materials.Naquadah, Materials.Naquadah, Materials.Naquadah, Materials.NaquadahEnriched);
            addOldOreRecordToConfig("lignite", 50, 130, 160, 8, 32, !tPFAA, false, false, false, false, false, Materials.Lignite, Materials.Lignite, Materials.Lignite, Materials.Coal);
            addOldOreRecordToConfig("coal", 50, 80, 80, 6, 32, !tPFAA, false, false, false, false, false, Materials.Coal, Materials.Coal, Materials.Coal, Materials.Lignite);
            addOldOreRecordToConfig("magnetite", 50, 120, 160, 3, 32, !tPFAA, true, false, true, true, false, Materials.Magnetite, Materials.Magnetite, Materials.Iron, Materials.VanadiumMagnetite);
            addOldOreRecordToConfig("gold", 60, 80, 160, 3, 32, !tPFAA, false, false, true, true, true, Materials.Magnetite, Materials.Magnetite, Materials.VanadiumMagnetite, Materials.Gold);
            addOldOreRecordToConfig("iron", 10, 40, 120, 4, 24, !tPFAA, true, false, true, true, false, Materials.BrownLimonite, Materials.YellowLimonite, Materials.BandedIron, Materials.Malachite);
            addOldOreRecordToConfig("cassiterite", 40, 120, 50, 5, 24, !tPFAA, false, true, true, true, true, Materials.Tin, Materials.Tin, Materials.Cassiterite, Materials.Tin);
            addOldOreRecordToConfig("tetrahedrite", 80, 120, 70, 4, 24, !tPFAA, true, false, true, true, true, Materials.Tetrahedrite, Materials.Tetrahedrite, Materials.Copper, Materials.Stibnite);
            addOldOreRecordToConfig("netherquartz", 40, 80, 80, 5, 24, false, true, false, false, false, false, Materials.NetherQuartz, Materials.NetherQuartz, Materials.NetherQuartz, Materials.NetherQuartz);
            addOldOreRecordToConfig("sulfur", 5, 20, 100, 5, 24, false, true, false, false, true, false, Materials.Sulfur, Materials.Sulfur, Materials.Pyrite, Materials.Sphalerite);
            addOldOreRecordToConfig("copper", 10, 30, 80, 4, 24, !tPFAA, true, false, true, true, false, Materials.Chalcopyrite, Materials.Iron, Materials.Pyrite, Materials.Copper);
            addOldOreRecordToConfig("bauxite", 50, 90, 80, 4, 24, !tPFAA, tPFAA, false, true, true, true, Materials.Bauxite, Materials.Bauxite, Materials.Aluminium, Materials.Ilmenite);
            addOldOreRecordToConfig("salts", 50, 60, 50, 3, 24, !tPFAA, false, false, true, false, false, Materials.RockSalt, Materials.Salt, Materials.Lepidolite, Materials.Spodumene);
            addOldOreRecordToConfig("redstone", 10, 40, 60, 3, 24, !tPFAA, true, false, true, true, true, Materials.Redstone, Materials.Redstone, Materials.Ruby, Materials.Cinnabar);
            addOldOreRecordToConfig("soapstone", 10, 40, 40, 3, 16, !tPFAA, false, false, true, true, false, Materials.Soapstone, Materials.Talc, Materials.Glauconite, Materials.Pentlandite);
            addOldOreRecordToConfig("nickel", 10, 40, 40, 3, 16, !tPFAA, true, true, true, true, true, Materials.Garnierite, Materials.Nickel, Materials.Cobaltite, Materials.Pentlandite);
            addOldOreRecordToConfig("platinum", 40, 50, 5, 3, 16, !tPFAA, false, true, false, true, true, Materials.Cooperite, Materials.Palladium, Materials.Platinum, Materials.Iridium);
            addOldOreRecordToConfig("pitchblende", 10, 40, 40, 3, 16, !tPFAA, false, false, true, true, true, Materials.Pitchblende, Materials.Pitchblende, Materials.Uraninite, Materials.Uraninite);
            addOldOreRecordToConfig("uranium", 20, 30, 20, 3, 16, !tPFAA, false, false, true, true, true, Materials.Uraninite, Materials.Uraninite, Materials.Uranium, Materials.Uranium);
            addOldOreRecordToConfig("monazite", 20, 40, 30, 3, 16, !tPFAA, tPFAA, false, true, true, true, Materials.Bastnasite, Materials.Bastnasite, Materials.Monazite, Materials.Neodymium);
            addOldOreRecordToConfig("molybdenum", 20, 50, 5, 3, 16, !tPFAA, false, true, true, true, true, Materials.Wulfenite, Materials.Molybdenite, Materials.Molybdenum, Materials.Powellite);
            addOldOreRecordToConfig("tungstate", 20, 50, 10, 3, 16, !tPFAA, false, true, true, true, true, Materials.Scheelite, Materials.Scheelite, Materials.Tungstate, Materials.Lithium);
            addOldOreRecordToConfig("sapphire", 10, 40, 60, 3, 16, !tPFAA, tPFAA, tPFAA, true, true, true, Materials.Almandine, Materials.Pyrope, Materials.Sapphire, Materials.GreenSapphire);
            addOldOreRecordToConfig("manganese", 20, 30, 20, 3, 16, !tPFAA, false, true, true, false, true, Materials.Grossular, Materials.Spessartine, Materials.Pyrolusite, Materials.Tantalite);
            addOldOreRecordToConfig("quartz", 40, 80, 60, 3, 16, !tPFAA, tPFAA, false, true, true, true, Materials.Quartzite, Materials.Barite, Materials.CertusQuartz, Materials.CertusQuartz);
            addOldOreRecordToConfig("diamond", 5, 20, 40, 2, 16, !tPFAA, false, false, true, true, true, Materials.Graphite, Materials.Graphite, Materials.Diamond, Materials.Coal);
            addOldOreRecordToConfig("olivine", 10, 40, 60, 3, 16, !tPFAA, false, true, true, true, true, Materials.Bentonite, Materials.Magnesite, Materials.Olivine, Materials.Glauconite);
            addOldOreRecordToConfig("apatite", 40, 60, 60, 3, 16, !tPFAA, false, false, false, false, false, Materials.Apatite, Materials.Apatite, Materials.Phosphorus, Materials.Pyrochlore);
            addOldOreRecordToConfig("galena", 30, 60, 40, 5, 16, !tPFAA, false, false, true, true, true, Materials.Galena, Materials.Galena, Materials.Silver, Materials.Lead);
            addOldOreRecordToConfig("lapis", 20, 50, 40, 5, 16, !tPFAA, false, true, true, true, true, Materials.Lazurite, Materials.Sodalite, Materials.Lapis, Materials.Calcite);
            addOldOreRecordToConfig("beryllium", 5, 30, 30, 3, 16, !tPFAA, false, true, true, true, true, Materials.Beryllium, Materials.Beryllium, Materials.Emerald, Materials.Thorium);
            addOldOreRecordToConfig("oilsand", 50, 80, 80, 6, 32, !tPFAA, false, false, false, false, false, Materials.Oilsands, Materials.Oilsands, Materials.Oilsands, Materials.Oilsands);
            convertCustomVeins();
        }

        for (ConfigCategory c : config.getCategory("generation.veins").getChildren()) {
            int minH = c.get("MinHeight").getInt(), maxH = c.get("MaxHeight").getInt(), den = c.get("Density").getInt(), size = c.get("Size").getInt(), sizeY = c.get("SizeY").getInt(), weight = c.get("Weight").getInt();
            String[] dims = c.get("Dimensions").getStringList(), ores = c.get("Ores").getStringList();
            if (minH > -1 && maxH > 0 && den > 0 && sizeY > 0 && weight > 0 && dims.length > 0 && ores.length > 0) {
                new GT_Worldgen_Layer(c.getName(), minH, maxH, weight, den, size,sizeY, dims, ores);
            }
        }

        if (!config.hasCategory("generation.asteroids")) {
            addOldAsteroidsRecordToConfig("EndAsteroids", Blocks.end_stone, 0, 300, 50, 200, "End");
            addOldAsteroidsRecordToConfig("Asteroids", GregTech_API.sBlockGranites, 8, 300, 100, 400, "Asteroids");
        }

        for (ConfigCategory c : config.getCategory("generation.asteroids").getChildren()) {
            int minH = c.get("MinSize").getInt(), maxH = c.get("MaxSize").getInt(), meta = c.get("Metadata").getInt(), prob = c.get("Probability").getInt();
            String block = c.get("Block").getString();
            String[] dims = c.get("Dimensions").getStringList();
            if (minH > -1 && maxH > 0 && meta > -1 && block != null && dims.length > 0) {
                new GT_Worldgen_Asteroids(c.getName(), dims, block, meta, prob, minH, maxH);
            }
        }

        if (GregTech_API.mImmersiveEngineering && GT_Mod.gregtechproxy.mImmersiveEngineeringRecipes) {
            blusunrize.immersiveengineering.api.tool.ExcavatorHandler.recalculateChances(true);
        }
    }

    private void convertCustomVeins() {
        if (GregTech_API.oldWorldgenFile == null || !GregTech_API.oldWorldgenFile.hasCategory("worldgen.ore.mix.custom"))
            return;
        for (ConfigCategory c : GregTech_API.oldWorldgenFile.getCategory("worldgen.ore.mix.custom").getChildren()) {
            if (!GregTech_API.oldWorldgenFile.get("worldgen", "ore.mix.custom." + c.getName() + "_false", false).getBoolean())
                continue;
            addOldOreRecordToConfig("custom_" + c.getName(), 0, 0, 0, 0, 0, false, false, false, false, false, false, Materials._NULL, Materials._NULL, Materials._NULL, Materials._NULL);
        }
    }
}
