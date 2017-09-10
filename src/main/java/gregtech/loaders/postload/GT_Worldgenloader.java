package gregtech.loaders.postload;

import bloodasp.galacticgreg.GT_Worldgenerator_Space;
import cpw.mods.fml.common.Loader;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.ConfigCategories;
import gregtech.api.enums.Materials;
import gregtech.common.GT_Worldgen_GT_Ore_Layer;
import gregtech.common.GT_Worldgen_GT_Ore_SmallPieces;
import gregtech.common.GT_Worldgen_Stone;
import gregtech.common.GT_Worldgenerator;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class GT_Worldgenloader
        implements Runnable {
    public void addOldOreRecordToConfig(String name, int minY, int maxY, int weight, int density, int size, boolean over, boolean nether, boolean end, boolean moon, boolean mars, boolean asteroids, Materials p, Materials s, Materials b, Materials sp) {
        String category = "generation.mixes." + name;
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
            list.add("EndAsteroids");
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
        c.get(category, "Density", density).getInt();
        c.get(category, "Weight", weight).getInt();
        c.get(category, "Size", size).getInt();
        c.get(category, "MaxHeight", maxY).getInt();
        c.get(category, "MinHeight", minY).getInt();
        Map<Integer, Integer> map = new TreeMap<>();
        map.put(p.mMetaItemSubID, 375);
        if (map.containsKey(s.mMetaItemSubID)) {
            map.put(s.mMetaItemSubID, map.get(s.mMetaItemSubID) + 375);
        } else {
            map.put(s.mMetaItemSubID, 375);
        }
        if (map.containsKey(b.mMetaItemSubID)) {
            map.put(b.mMetaItemSubID, map.get(b.mMetaItemSubID) + 125);
        } else {
            map.put(b.mMetaItemSubID, 125);
        }
        if (map.containsKey(sp.mMetaItemSubID)) {
            map.put(sp.mMetaItemSubID, map.get(sp.mMetaItemSubID) + 125);
        } else {
            map.put(sp.mMetaItemSubID, 125);
        }
        String[] ores = new String[map.size()];
        int j = 0;
        for (Map.Entry<Integer, Integer> i : map.entrySet()) {
            ores[j++] = i.getKey() + "=" + i.getValue();
        }
        c.get(category, "Ore", ores).getStringList();
    }

    public void run() {
        boolean tPFAA = (GregTech_API.sWorldgenFile.get(ConfigCategories.general, "AutoDetectPFAA", true)) && (Loader.isModLoaded("PFAAGeologica"));

        new GT_Worldgenerator();
        if (Loader.isModLoaded("GalacticraftCore") && Loader.isModLoaded("GalacticraftMars")) {
            new GT_Worldgenerator_Space();
        }

        new GT_Worldgen_Stone("overworld.stone.blackgranite.tiny", true, GregTech_API.sBlockGranites, 0, 0, 1, 50, 48, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.blackgranite.small", true, GregTech_API.sBlockGranites, 0, 0, 1, 100, 96, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.blackgranite.medium", true, GregTech_API.sBlockGranites, 0, 0, 1, 200, 144, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.blackgranite.large", true, GregTech_API.sBlockGranites, 0, 0, 1, 300, 192, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.blackgranite.huge", true, GregTech_API.sBlockGranites, 0, 0, 1, 400, 240, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.redgranite.tiny", true, GregTech_API.sBlockGranites, 8, 0, 1, 50, 48, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.redgranite.small", true, GregTech_API.sBlockGranites, 8, 0, 1, 100, 96, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.redgranite.medium", true, GregTech_API.sBlockGranites, 8, 0, 1, 200, 144, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.redgranite.large", true, GregTech_API.sBlockGranites, 8, 0, 1, 300, 192, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.redgranite.huge", true, GregTech_API.sBlockGranites, 8, 0, 1, 400, 240, 0, 120, null, false);

        new GT_Worldgen_Stone("nether.stone.blackgranite.tiny", false, GregTech_API.sBlockGranites, 0, -1, 1, 50, 48, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.blackgranite.small", false, GregTech_API.sBlockGranites, 0, -1, 1, 100, 96, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.blackgranite.medium", false, GregTech_API.sBlockGranites, 0, -1, 1, 200, 144, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.blackgranite.large", false, GregTech_API.sBlockGranites, 0, -1, 1, 300, 192, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.blackgranite.huge", false, GregTech_API.sBlockGranites, 0, -1, 1, 400, 240, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.redgranite.tiny", false, GregTech_API.sBlockGranites, 8, -1, 1, 50, 48, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.redgranite.small", false, GregTech_API.sBlockGranites, 8, -1, 1, 100, 96, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.redgranite.medium", false, GregTech_API.sBlockGranites, 8, -1, 1, 200, 144, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.redgranite.large", false, GregTech_API.sBlockGranites, 8, -1, 1, 300, 192, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.redgranite.huge", false, GregTech_API.sBlockGranites, 8, -1, 1, 400, 240, 0, 120, null, false);

        new GT_Worldgen_Stone("overworld.stone.marble.tiny", true, GregTech_API.sBlockStones, 0, 0, 1, 50, 48, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.marble.small", true, GregTech_API.sBlockStones, 0, 0, 1, 100, 96, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.marble.medium", true, GregTech_API.sBlockStones, 0, 0, 1, 200, 144, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.marble.large", true, GregTech_API.sBlockStones, 0, 0, 1, 300, 192, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.marble.huge", true, GregTech_API.sBlockStones, 0, 0, 1, 400, 240, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.basalt.tiny", true, GregTech_API.sBlockStones, 8, 0, 1, 50, 48, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.basalt.small", true, GregTech_API.sBlockStones, 8, 0, 1, 100, 96, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.basalt.medium", true, GregTech_API.sBlockStones, 8, 0, 1, 200, 144, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.basalt.large", true, GregTech_API.sBlockStones, 8, 0, 1, 300, 192, 0, 120, null, false);
        new GT_Worldgen_Stone("overworld.stone.basalt.huge", true, GregTech_API.sBlockStones, 8, 0, 1, 400, 240, 0, 120, null, false);

        new GT_Worldgen_Stone("nether.stone.marble.tiny", false, GregTech_API.sBlockStones, 0, -1, 1, 50, 48, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.marble.small", false, GregTech_API.sBlockStones, 0, -1, 1, 100, 96, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.marble.medium", false, GregTech_API.sBlockStones, 0, -1, 1, 200, 144, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.marble.large", false, GregTech_API.sBlockStones, 0, -1, 1, 300, 192, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.marble.huge", false, GregTech_API.sBlockStones, 0, -1, 1, 400, 240, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.basalt.tiny", false, GregTech_API.sBlockStones, 8, -1, 1, 50, 48, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.basalt.small", false, GregTech_API.sBlockStones, 8, -1, 1, 100, 96, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.basalt.medium", false, GregTech_API.sBlockStones, 8, -1, 1, 200, 144, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.basalt.large", false, GregTech_API.sBlockStones, 8, -1, 1, 300, 192, 0, 120, null, false);
        new GT_Worldgen_Stone("nether.stone.basalt.huge", false, GregTech_API.sBlockStones, 8, -1, 1, 400, 240, 0, 120, null, false);

        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.copper", true, 60, 120, 32, !tPFAA, true, true, true, true, false, Materials.Copper);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.tin", true, 60, 120, 32, !tPFAA, true, true, true, true, true, Materials.Tin);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.bismuth", true, 80, 120, 8, !tPFAA, true, false, true, true, false, Materials.Bismuth);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.coal", true, 60, 100, 24, !tPFAA, false, false, false, false, false, Materials.Coal);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.iron", true, 40, 80, 16, !tPFAA, true, true, true, true, false, Materials.Iron);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.lead", true, 40, 80, 16, !tPFAA, true, true, true, true, true, Materials.Lead);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.zinc", true, 30, 60, 12, !tPFAA, true, true, true, true, false, Materials.Zinc);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.gold", true, 20, 40, 8, !tPFAA, true, true, true, true, true, Materials.Gold);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.silver", true, 20, 40, 8, !tPFAA, true, true, true, true, true, Materials.Silver);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.nickel", true, 20, 40, 8, !tPFAA, true, true, true, true, true, Materials.Nickel);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.lapis", true, 20, 40, 4, !tPFAA, false, false, true, false, true, Materials.Lapis);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.diamond", true, 5, 10, 2, !tPFAA, true, false, true, true, true, Materials.Diamond);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.emerald", true, 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Emerald);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.ruby", true, 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Ruby);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.sapphire", true, 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Sapphire);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.greensapphire", true, 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.GreenSapphire);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.olivine", true, 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Olivine);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.topaz", true, 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Topaz);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.tanzanite", true, 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Tanzanite);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.amethyst", true, 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Amethyst);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.opal", true, 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Opal);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.jasper", true, 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Jasper);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.bluetopaz", true, 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.BlueTopaz);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.amber", true, 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Amber);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.foolsruby", true, 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.FoolsRuby);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.garnetred", true, 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.GarnetRed);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.garnetyellow", true, 5, 250, 1, !tPFAA, true, false, false, true, true, Materials.GarnetYellow);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.redstone", true, 5, 20, 8, !tPFAA, true, false, true, true, true, Materials.Redstone);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.platinum", true, 20, 40, 8, false, false, true, false, true, true, Materials.Platinum);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.iridium", true, 20, 40, 8, false, false, true, false, true, true, Materials.Iridium);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.netherquartz", true, 30, 120, 64, false, true, false, false, false, false, Materials.NetherQuartz);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.saltpeter", true, 10, 60, 8, false, true, false, false, false, false, Materials.Saltpeter);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.sulfur_n", true, 10, 60, 32, false, true, false, false, false, false, Materials.Sulfur);
        new GT_Worldgen_GT_Ore_SmallPieces("ore.small.sulfur_o", true, 5, 15, 8, !tPFAA, false, false, false, false, false, Materials.Sulfur);

        int i = 0;
        for (int j = GregTech_API.sWorldgenFile.get("worldgen", "AmountOfCustomSmallOreSlots", 16); i < j; i++) {
            new GT_Worldgen_GT_Ore_SmallPieces("ore.small.custom." + (i < 10 ? "0" : "") + i, false, 0, 0, 0, false, false, false, false, false, false, Materials._NULL);
        }
        if (GregTech_API.mImmersiveEngineering && GT_Mod.gregtechproxy.mImmersiveEngineeringRecipes) {
            blusunrize.immersiveengineering.api.tool.ExcavatorHandler.mineralList.clear();
            blusunrize.immersiveengineering.api.tool.ExcavatorHandler.mineralCache.clear();
        }

        if (!GregTech_API.advancedWorldgenFile.mConfig.hasCategory("generation.mixes")) {
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
        }

        for (ConfigCategory c : GregTech_API.advancedWorldgenFile.mConfig.getCategory("generation.mixes").getChildren()) {
            int minH = c.get("MinHeight").getInt(-1), maxH = c.get("MaxHeight").getInt(-1), den = c.get("Density").getInt(-1), size = c.get("Size").getInt(-1), weight = c.get("Weight").getInt(-1);
            String[] dim = c.get("Dimensions").getStringList(), ore = c.get("Ore").getStringList();
            if (minH > 0 && maxH > 0 && den > 0 && size > 0 && weight > 0 && dim.length > 0 && ore.length > 0) {
                new GT_Worldgen_GT_Ore_Layer(c.getName(), minH, maxH, weight, den, size, dim, ore);
            }
        }

        if (GregTech_API.mImmersiveEngineering && GT_Mod.gregtechproxy.mImmersiveEngineeringRecipes) {
            blusunrize.immersiveengineering.api.tool.ExcavatorHandler.recalculateChances(true);
        }
    }
}
