package gregtech.loaders.postload;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import cpw.mods.fml.common.Loader;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.ConfigCategories;
import gregtech.api.enums.Materials;
import gregtech.api.util.GT_Config;
import gregtech.api.util.GT_Utility;
import gregtech.common.GT_Worldgen_Asteroid;
import gregtech.common.GT_Worldgen_GT_Ore_Layer;
import gregtech.common.GT_Worldgen_GT_Ore_SmallPieces;
import gregtech.common.GT_Worldgen_Stone;
import gregtech.common.GT_Worldgenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;

public class GT_Worldgenloader
        implements Runnable {
	private static final GT_Config ADV_FILE = GregTech_API.sAdvWorldgenFile, OLD_FILE = GregTech_API.sWorldgenFile; 
	private static final String dims = "DimensionWhiteList", biomes = "RestrictedBiomes";

 	private static class DimListBuffer {
		HashSet<String> mList = new HashSet<>();
		
		public void add(String aDimList) {
			if (GT_Utility.isStringValid(aDimList)) mList.add(aDimList);
		}
		
		public void clear() {
			mList.clear();
		}
		
		public String[] get() {
			return mList.toArray(new String[mList.size()]);
		}
	}

	public static void transferOldFile1() {
		System.out.println("Transfer basic worldgen configs");
		transferValue("general", "enableBlackGraniteOres", GT_Mod.gregtechproxy.enableBlackGraniteOres);
		transferValue("general", "enableRedGraniteOres", GT_Mod.gregtechproxy.enableRedGraniteOres);
		transferValue("general", "enableMarbleOres", GT_Mod.gregtechproxy.enableMarbleOres);
		transferValue("general", "enableBasaltOres", GT_Mod.gregtechproxy.enableBasaltOres);
		transferValue("general", "enableGCOres", GT_Mod.gregtechproxy.enableGCOres);
		transferValue("general", "enableUBOres", GT_Mod.gregtechproxy.enableUBOres);
		System.out.println("Transfer of basic configs finished.");
	}

	private void transferOldFile2() {
		System.out.println("Transfer multiple worldgen configs:");
		transferValue(ConfigCategories.general, "AutoDetectPFAA", true);
		
		String block = "", category;
		int meta = 0;
		DimListBuffer tDimList = new DimListBuffer();
		
		System.out.println("Transfer stone worldgen configs");
		for (String type : new String[]{"blackgranite", "redgranite", "marble", "basalt"}) {
			switch (type) {
			case "blackgranite": block = getBlockName(GregTech_API.sBlockGranites); meta = 0; break;
			case "redgranite": block = getBlockName(GregTech_API.sBlockGranites); meta = 8; break;
			case "marble": block = getBlockName(GregTech_API.sBlockStones); meta = 0; break;
			case "basalt": block = getBlockName(GregTech_API.sBlockStones); meta = 8; break;
			}
			int prob = 0, range = 0;
			for (String size : new String[]{"tiny", "small", "medium", "large", "huge"}) {
				switch (size) {
				case "tiny": range = 50; prob = 48; break;
				case "small": range = 100; prob = 96; break;
				case "medium": range = 200; prob = 144; break;
				case "large": range = 300; prob = 196; break;
				case "huge": range = 400; prob = 240; break;
				}
				String tName = "overworld.stone." + type + "." + size;
				category = "worldgen.stone." + type + "." + size;
				transferValue("worldgen", "worldgen", "stone." + type + "." + size, tName, true);
				transferValue(category, "worldgen." + tName, "Probability", "Probability", prob);
				transferValue(category, "worldgen." + tName, "Amount", "Amount", 1);
				transferValue(category, "worldgen." + tName, "Size", "Size", range);
				transferValue(category, "worldgen." + tName, "MinHeight", "MinHeight", 0);
				transferValue(category, "worldgen." + tName, "MaxHeight", "MaxHeight", 120);
				tDimList.clear();
				if (OLD_FILE.find("worldgen", tName, true))
					tDimList.add("Overworld");
				transferOldDimList(tName, tDimList);
				ADV_FILE.get(category, dims, tDimList.get());
				ADV_FILE.get(category, "Block", block);
				ADV_FILE.get(category, "BlockMeta", meta);
				ADV_FILE.get(category, biomes, new String[0]);
			}
		}
		System.out.println("Transfer of stone configs finished.");
		
		System.out.println("Transfer asteroid worldgen configs");
		category = "worldgen.asteroid.endasteroids";
		transferValue("worldgen", "endasteroids", "asteroid.endasteroids", "GenerateAsteroids", true);
		transferValue(category, "endasteroids", "Probability", "AsteroidProbability", 300);
		transferValue(category, "endasteroids", "MinSize", "AsteroidMinSize", 50);
		transferValue(category, "endasteroids", "MaxSize", "AsteroidMaxSize", 200);
		ADV_FILE.get(category, dims, new String[]{"The End"});
		ADV_FILE.get(category, "Block", getBlockName(Blocks.end_stone));
		ADV_FILE.get(category, "BlockMeta", 0);
		ADV_FILE.get(category, biomes, new String[0]);
		
		category = "worldgen.asteroid.gcasteroids";
		transferValue("worldgen", "gcasteroids", "asteroid.gcasteroids", "GenerateGCAsteroids", true);
		transferValue(category, "gcasteroids", "Probability", "GCAsteroidProbability", 300);
		transferValue(category, "gcasteroids", "MinSize", "GCAsteroidMinSize", 100);
		transferValue(category, "gcasteroids", "MaxSize", "GCAsteroidMaxSize", 400);
		ADV_FILE.get(category, dims, new String[]{"Asteroids"});
		ADV_FILE.get(category, "Block", getBlockName(GregTech_API.sBlockGranites));
		ADV_FILE.get(category, "BlockMeta", 8);
		ADV_FILE.get(category, biomes, new String[0]);
		System.out.println("Transfer of asteroid configs finished.");
		
		System.out.println("Transfer small ore worldgen configs");
		for (ConfigCategory c : OLD_FILE.mConfig.getCategory("worldgen.ore.small").getChildren()) {
			if (c.getName().equals("custom")) {
				System.out.println("Transfer custom small ore configs");
				HashSet<String> names = new HashSet<>();
				for (ConfigCategory _c : c.getChildren()) {
					if ((block = getMaterialName(_c.get("Ore_-1").getInt())) != null) {
						if (names.contains(block)) {
							int i = 1;
							for (; names.contains(block + i); i++);
							block = block + i;
						}
						transferSmallOres(_c, block, "custom." + _c.getName());
						names.add(block);
					}
				}
				System.out.println("Transfer of custom small ore finished.");
			} else {
				transferSmallOres(c, c.getName(), c.getName());
			}
		}
		System.out.println("Transfer of all small ore configs finished");
		
		System.out.println("Transfer oregen configs");
		for (ConfigCategory c : OLD_FILE.mConfig.getCategory("worldgen.ore.mix").getChildren()) {
			if (c.getName().equals("custom")) {
				System.out.println("Transfer custom oregen configs");
				HashSet<String> names = new HashSet<>();
				for (ConfigCategory _c : c.getChildren()) {
					block = getMaterialName(_c.get("OrePrimaryLayer_-1").getInt());
					if (block == null || names.contains(block)) block = getMaterialName(_c.get("OreSecondaryLayer_-1").getInt());
					if (block == null || names.contains(block)) block = getMaterialName(_c.get("OreSporadiclyInbetween_-1").getInt());
					if (block == null || names.contains(block)) block = getMaterialName(_c.get("OreSporaticlyAround_-1").getInt());
					if (block != null) {
						if (names.contains(block)) {
							int i = 1;
							for (; names.contains(block + "-" + i); i++);
							block = block + "-" + i;
						}
						transferOreVeins(_c, block, "custom." + _c.getName());
						names.add(block);
					}
				}
				System.out.println("Transfer of custom oregen configs finished.");
			} else {
				transferOreVeins(c, c.getName(), c.getName());
			}
		}
		System.out.println("Transfer of all oregen configs finished");
	}

	private void transferSmallOres(ConfigCategory c, String aName, String aOldName) {
		DimListBuffer tDimList = new DimListBuffer();
		String oldCategory = "worldgen.ore.small." + aOldName;
		String category = "worldgen.ore.small." + aName;
		boolean isCustom = aOldName.startsWith("custom");
		for (Map.Entry<String, Property> e : c.getValues().entrySet()) {
			if (e.getKey().startsWith("Ore")) {
				String[] k = splitConfig(e.getKey());
				String defaultMaterialName = getMaterialName(Integer.parseInt(k[1])), materialName = getMaterialName(e.getValue().getInt());
				if (materialName == null) return;
				if (defaultMaterialName == null) if (isCustom) defaultMaterialName = materialName; else return;
				ADV_FILE.set(category, "Ore", defaultMaterialName, materialName);
				break;
			}
		}
		for (Map.Entry<String, Property> e : c.getValues().entrySet()) {
			String[] k = splitConfig(e.getKey());
			if (k.length == 2) {
				switch (k[0]) {
				case "Ore": break;
				case "Amount":
				case "MinHeight":
				case "MaxHeight":
					transferValue(category, oldCategory, k[0], k[0], Integer.parseInt(k[1]), isCustom); break;
				case "Overworld":
				case "Nether":
				case "Moon":
				case "Mars":
					if (e.getValue().getBoolean()) tDimList.add(k[0]); break;
				case "TheEnd":
					if (e.getValue().getBoolean()) tDimList.add("The End"); break;
				case "Asteroid":
					if (e.getValue().getBoolean()) tDimList.add("Asteroids"); break;
				case "RestrictToBiomeName":
					String biome = e.getValue().getString();
					ADV_FILE.get(category, biomes, biome.equals("None") ? new String[0] : new String[]{biome}); break;
				}
			}
		}
		transferOldDimList("ore.small." + aOldName, tDimList);
		ADV_FILE.get(category, dims, tDimList.get());
		
		transferValue("worldgen", "worldgen", "ore.small." + aName, "ore.small." + aOldName, true);
	}

	private void transferOreVeins(ConfigCategory c, String aName, String aOldName) {
		DimListBuffer tDimList = new DimListBuffer();
		String oldCategory = "worldgen.ore.mix." + aOldName;
		String category = "worldgen.ore.mix." + aName;
		boolean isCustom = aOldName.startsWith("custom");
		HashMap<String, String> oreBuffer = new HashMap<>();
		int count = 0;
		for (Map.Entry<String, Property> e : c.getValues().entrySet()) {
			if (e.getKey().startsWith("Ore")) {
				String[] k = splitConfig(e.getKey());
				String materialName = getMaterialName(e.getValue().getInt());
				if (materialName == null) {
					oreBuffer.put(k[0], null);
					continue;
				}
				oreBuffer.put(k[0], materialName);
				count++;
				if (oreBuffer.size() == 4) break;
			}
		}
		if (count == 0) return;
		for (Map.Entry<String, String> e : oreBuffer.entrySet()) {
			ADV_FILE.get(category, e.getKey(), e.getValue() == null ? new String[0] : aOldName.equals("apatite") && e.getKey().endsWith("Around") ?new String[]{"Pyrochlore:30", "Phosphate:70"} : new String[]{e.getValue() + ":100"});
		}
		
		for (Map.Entry<String, Property> e : c.getValues().entrySet()) {
			String[] k = splitConfig(e.getKey());
			if (k.length == 2) {
				switch (k[0]) {
				case "OrePrimaryLayer":
				case "OreSecondaryLayer":
				case "OreSporadiclyInbetween":
				case "OreSporaticlyAround":
					break;
				case "Size":
				case "RandomWeight":
				case "Density":
				case "MinHeight":
				case "MaxHeight":
					transferValue(category, oldCategory, k[0], k[0], Integer.parseInt(k[1]), isCustom); break;
				case "Overworld":
				case "Nether":
				case "Moon":
				case "Mars":
					if (e.getValue().getBoolean()) tDimList.add(k[0]); break;
				case "TheEnd":
					if (e.getValue().getBoolean()) tDimList.add("The End"); break;
				case "EndAsteroid":
					if (e.getValue().getBoolean()) tDimList.add("endasteroids"); break;
				case "Asteroid":
					if (e.getValue().getBoolean()) tDimList.add("gcasteroids"); break;
				case "RestrictToBiomeName":
					String biome = e.getValue().getString();
					ADV_FILE.get(category, biomes, biome.equals("None") ? new String[0] : new String[]{biome}); break;
				}
			}
		}
		
		transferOldDimList("ore.mix." + aOldName, tDimList);
		ADV_FILE.get(category, dims, tDimList.get());
		
		transferValue("worldgen", "worldgen", "ore.mix." + aName, "ore.mix." + aOldName, true);
	}

    public void run() {
    	/*if (GregTech_API.worldgenFileUpdate)*/ transferOldFile2();
        boolean tPFAA = (GregTech_API.sAdvWorldgenFile.get(ConfigCategories.general, "AutoDetectPFAA", true)) && (Loader.isModLoaded("PFAAGeologica"));

        new GT_Worldgenerator();
        if (Loader.isModLoaded("GalacticraftCore") && Loader.isModLoaded("GalacticraftMars")) {
            //new GT_Worldgenerator_Space();
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
        
        new GT_Worldgen_Asteroid(true);

        int i = 0;
        for (int j = GregTech_API.sWorldgenFile.get("worldgen", "AmountOfCustomSmallOreSlots", 16); i < j; i++) {
            new GT_Worldgen_GT_Ore_SmallPieces("ore.small.custom." + (i < 10 ? "0" : "") + i, false, 0, 0, 0, false, false, false, false, false, false, Materials._NULL);
        }
        if (GregTech_API.mImmersiveEngineering && GT_Mod.gregtechproxy.mImmersiveEngineeringRecipes) {
            blusunrize.immersiveengineering.api.tool.ExcavatorHandler.mineralList.clear();
            blusunrize.immersiveengineering.api.tool.ExcavatorHandler.mineralCache.clear();
        }

        new GT_Worldgen_GT_Ore_Layer("ore.mix.naquadah", true, 10, 60, 10, 5, 32, false, false, true, false, true, true, Materials.Naquadah, Materials.Naquadah, Materials.Naquadah, Materials.NaquadahEnriched);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.lignite", true, 50, 130, 160, 8, 32, !tPFAA, false, false, false, false, false, Materials.Lignite, Materials.Lignite, Materials.Lignite, Materials.Coal);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.coal", true, 50, 80, 80, 6, 32, !tPFAA, false, false, false, false, false, Materials.Coal, Materials.Coal, Materials.Coal, Materials.Lignite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.magnetite", true, 50, 120, 160, 3, 32, !tPFAA, true, false, true, true, false, Materials.Magnetite, Materials.Magnetite, Materials.Iron, Materials.VanadiumMagnetite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.gold", true, 60, 80, 160, 3, 32, !tPFAA, false, false, true, true, true, Materials.Magnetite, Materials.Magnetite, Materials.VanadiumMagnetite, Materials.Gold);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.iron", true, 10, 40, 120, 4, 24, !tPFAA, true, false, true, true, false, Materials.BrownLimonite, Materials.YellowLimonite, Materials.BandedIron, Materials.Malachite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.cassiterite", true, 40, 120, 50, 5, 24, !tPFAA, false, true, true, true, true, Materials.Tin, Materials.Tin, Materials.Cassiterite, Materials.Tin);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.tetrahedrite", true, 80, 120, 70, 4, 24, !tPFAA, true, false, true, true, true, Materials.Tetrahedrite, Materials.Tetrahedrite, Materials.Copper, Materials.Stibnite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.netherquartz", true, 40, 80, 80, 5, 24, false, true, false, false, false, false, Materials.NetherQuartz, Materials.NetherQuartz, Materials.NetherQuartz, Materials.NetherQuartz);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.sulfur", true, 5, 20, 100, 5, 24, false, true, false, false, true, false, Materials.Sulfur, Materials.Sulfur, Materials.Pyrite, Materials.Sphalerite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.copper", true, 10, 30, 80, 4, 24, !tPFAA, true, false, true, true, false, Materials.Chalcopyrite, Materials.Iron, Materials.Pyrite, Materials.Copper);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.bauxite", true, 50, 90, 80, 4, 24, !tPFAA, tPFAA, false, true, true, true, Materials.Bauxite, Materials.Bauxite, Materials.Aluminium, Materials.Ilmenite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.salts", true, 50, 60, 50, 3, 24, !tPFAA, false, false, true, false, false, Materials.RockSalt, Materials.Salt, Materials.Lepidolite, Materials.Spodumene);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.redstone", true, 10, 40, 60, 3, 24, !tPFAA, true, false, true, true, true, Materials.Redstone, Materials.Redstone, Materials.Ruby, Materials.Cinnabar);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.soapstone", true, 10, 40, 40, 3, 16, !tPFAA, false, false, true, true, false, Materials.Soapstone, Materials.Talc, Materials.Glauconite, Materials.Pentlandite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.nickel", true, 10, 40, 40, 3, 16, !tPFAA, true, true, true, true, true, Materials.Garnierite, Materials.Nickel, Materials.Cobaltite, Materials.Pentlandite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.platinum", true, 40, 50, 5, 3, 16, !tPFAA, false, true, false, true, true, Materials.Cooperite, Materials.Palladium, Materials.Platinum, Materials.Iridium);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.pitchblende", true, 10, 40, 40, 3, 16, !tPFAA, false, false, true, true, true, Materials.Pitchblende, Materials.Pitchblende, Materials.Uraninite, Materials.Uraninite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.uranium", true, 20, 30, 20, 3, 16, !tPFAA, false, false, true, true, true, Materials.Uraninite, Materials.Uraninite, Materials.Uranium, Materials.Uranium);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.monazite", true, 20, 40, 30, 3, 16, !tPFAA, tPFAA, false, true, true, true, Materials.Bastnasite, Materials.Bastnasite, Materials.Monazite, Materials.Neodymium);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.molybdenum", true, 20, 50, 5, 3, 16, !tPFAA, false, true, true, true, true, Materials.Wulfenite, Materials.Molybdenite, Materials.Molybdenum, Materials.Powellite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.tungstate", true, 20, 50, 10, 3, 16, !tPFAA, false, true, true, true, true, Materials.Scheelite, Materials.Scheelite, Materials.Tungstate, Materials.Lithium);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.sapphire", true, 10, 40, 60, 3, 16, !tPFAA, tPFAA, tPFAA, true, true, true, Materials.Almandine, Materials.Pyrope, Materials.Sapphire, Materials.GreenSapphire);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.manganese", true, 20, 30, 20, 3, 16, !tPFAA, false, true, true, false, true, Materials.Grossular, Materials.Spessartine, Materials.Pyrolusite, Materials.Tantalite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.quartz", true, 40, 80, 60, 3, 16, !tPFAA, tPFAA, false, true, true, true, Materials.Quartzite, Materials.Barite, Materials.CertusQuartz, Materials.CertusQuartz);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.diamond", true, 5, 20, 40, 2, 16, !tPFAA, false, false, true, true, true, Materials.Graphite, Materials.Graphite, Materials.Diamond, Materials.Coal);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.olivine", true, 10, 40, 60, 3, 16, !tPFAA, false, true, true, true, true, Materials.Bentonite, Materials.Magnesite, Materials.Olivine, Materials.Glauconite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.apatite", true, 40, 60, 60, 3, 16, !tPFAA, false, false, false, false, false, Materials.Apatite, Materials.Apatite, Materials.Phosphorus, Materials.Pyrochlore);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.galena", true, 30, 60, 40, 5, 16, !tPFAA, false, false, true, true, true, Materials.Galena, Materials.Galena, Materials.Silver, Materials.Lead);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.lapis", true, 20, 50, 40, 5, 16, !tPFAA, false, true, true, true, true, Materials.Lazurite, Materials.Sodalite, Materials.Lapis, Materials.Calcite);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.beryllium", true, 5, 30, 30, 3, 16, !tPFAA, false, true, true, true, true, Materials.Beryllium, Materials.Beryllium, Materials.Emerald, Materials.Thorium);
        new GT_Worldgen_GT_Ore_Layer("ore.mix.oilsand", true, 50, 80, 80, 6, 32, !tPFAA, false, false, false, false, false, Materials.Oilsands, Materials.Oilsands, Materials.Oilsands, Materials.Oilsands);

        i = 0;
        for (int j = GregTech_API.sWorldgenFile.get("worldgen", "AmountOfCustomLargeVeinSlots", 16); i < j; i++) {
            new GT_Worldgen_GT_Ore_Layer("ore.mix.custom." + (i < 10 ? "0" : "") + i, false, 0, 0, 0, 0, 0, false, false, false, false, false, false, Materials._NULL, Materials._NULL, Materials._NULL, Materials._NULL);
        }
        
        if (GregTech_API.mImmersiveEngineering && GT_Mod.gregtechproxy.mImmersiveEngineeringRecipes) {
            blusunrize.immersiveengineering.api.tool.ExcavatorHandler.recalculateChances(true);
        }
    }

    private static boolean transferValue(Object aCategory, String aName, boolean aDefault) {
    	return transferValue(aCategory, aCategory, aName, aName, aDefault);
    }

    private static int transferValue(Object aCategory, String aName, int aDefault) {
    	return transferValue(aCategory, aCategory, aName, aName, aDefault);
    }

    private static double transferValue(Object aCategory, String aName, double aDefault) {
    	return transferValue(aCategory, aCategory, aName, aName, aDefault);
    }

    private static String transferValue(Object aCategory, String aName, String aDefault) {
    	return transferValue(aCategory, aCategory, aName, aName, aDefault);
    }

    private static boolean transferValue(Object aCategory, Object aOldCategory, String aName, String aOldName, boolean aDefault) {
    	return transferValue(aCategory, aOldCategory, aName, aOldName, aDefault, false);
    }

    private static int transferValue(Object aCategory, Object aOldCategory, String aName, String aOldName, int aDefault) {
    	return transferValue(aCategory, aOldCategory, aName, aOldName, aDefault, false);
    }

    private static double transferValue(Object aCategory, Object aOldCategory, String aName, String aOldName, double aDefault) {
    	return transferValue(aCategory, aOldCategory, aName, aOldName, aDefault, false);
    }

    private static String transferValue(Object aCategory, Object aOldCategory, String aName, String aOldName, String aDefault) {
    	return transferValue(aCategory, aOldCategory, aName, aOldName, aDefault, false);
    }

    private static boolean transferValue(Object aCategory, Object aOldCategory, String aName, String aOldName, boolean aDefault, boolean aCustom) {
    	boolean aPrefered = OLD_FILE.find(aOldCategory, aOldName, aDefault);
    	return ADV_FILE.set(aCategory, aName, aCustom ? aDefault : aPrefered, aPrefered);
    }

    private static int transferValue(Object aCategory, Object aOldCategory, String aName, String aOldName, int aDefault, boolean aCustom) {
    	int aPrefered = OLD_FILE.find(aOldCategory, aOldName, aDefault);
    	return ADV_FILE.set(aCategory, aName, aCustom ? aDefault : aPrefered, aPrefered);
    }

    private static double transferValue(Object aCategory, Object aOldCategory, String aName, String aOldName, double aDefault, boolean aCustom) {
    	double aPrefered = OLD_FILE.find(aOldCategory, aOldName, aDefault);
    	return ADV_FILE.set(aCategory, aName, aCustom ? aDefault : aPrefered, aPrefered);
    }

    private static String transferValue(Object aCategory, Object aOldCategory, String aName, String aOldName, String aDefault, boolean aCustom) {
    	String aPrefered = OLD_FILE.find(aOldCategory, aOldName, aDefault);
    	return ADV_FILE.set(aCategory, aName, aCustom ? aDefault : aPrefered, aPrefered);
    }

    private static void transferOldDimList(String aName, DimListBuffer aBuffer) {
    	ConfigCategory aCategory = OLD_FILE.mConfig.getCategory("worldgen.dimensions." + aName);
    	for (Property p : aCategory.getOrderedValues())
    		if (p.getBoolean()) aBuffer.add(splitConfig(p.getName())[0]);
    }

    private static String[] splitConfig(String aConfig) {
    	if (!aConfig.contains("_")) throw new IllegalArgumentException("Argument must be seperated by '_'.");
    	return new String[]{aConfig.substring(0, aConfig.lastIndexOf('_')), aConfig.substring(aConfig.lastIndexOf('_') + 1, aConfig.length())};
    }

    private static String getBlockName(Block aBlock) {
    	return Block.blockRegistry.getNameForObject(aBlock);
    }

    private static String getMaterialName(int aID) {
    	try {
    		return GregTech_API.sMaterials[aID].mName;
    	} catch (Throwable t) {
    		return null;
    	}
    }
}
