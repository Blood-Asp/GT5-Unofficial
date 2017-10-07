package gregtech.loaders.postload;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import cpw.mods.fml.common.Loader;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.ConfigCategories;
import gregtech.api.enums.GT_Values;
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
		
		public void addOverworld() {add("Overworld");}
		public void addNether() {add("Nether");}
		public void addEnd() {add("The End");}
		public void addMoon() {add("Moon");}
		public void addMars() {add("Mars");}
		public void addAsteroid() {add("Asteroids");}
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
		if (GT_Values.D1) System.out.println("Transfer multiple worldgen configs:");
		transferValue(ConfigCategories.general, "AutoDetectPFAA", true);
		
		String block = "", category;
		int meta = 0;
		DimListBuffer tDimList = new DimListBuffer();
		
		if (GT_Values.D1) System.out.println("Transfer stone worldgen configs");
		for (String type : new String[]{"blackgranite", "redgranite", "marble", "basalt"}) {
			switch (type) {
			case "blackgranite": block = getBlockName(GregTech_API.sBlockGranites); meta = 0; break;
			case "redgranite": block = getBlockName(GregTech_API.sBlockGranites); meta = 8; break;
			case "marble": block = getBlockName(GregTech_API.sBlockStones); meta = 0; break;
			case "basalt": block = getBlockName(GregTech_API.sBlockStones); meta = 8; break;
			}
			int prob = 0, size = 0;
			for (String scale : new String[]{"tiny", "small", "medium", "large", "huge"}) {
				switch (scale) {
				case "tiny": size = 50; prob = 48; break;
				case "small": size = 100; prob = 96; break;
				case "medium": size = 200; prob = 144; break;
				case "large": size = 300; prob = 196; break;
				case "huge": size = 400; prob = 240; break;
				}
				String tName = "overworld.stone." + type + "." + scale;
				category = "worldgen.stone." + type + "." + scale;
				transferValue("worldgen", "worldgen", "stone." + type + "." + scale, tName, true);
				transferValue(category, "worldgen." + tName, "Probability", "Probability", prob);
				transferValue(category, "worldgen." + tName, "Amount", "Amount", 1);
				transferValue(category, "worldgen." + tName, "Size", "Size", size);
				transferValue(category, "worldgen." + tName, "MinHeight", "MinHeight", 0);
				transferValue(category, "worldgen." + tName, "MaxHeight", "MaxHeight", 120);
				tDimList.clear();
				if (OLD_FILE.find("worldgen", tName, true)) tDimList.addOverworld();
				if (OLD_FILE.find("worldgen", "nether.stone." + type + "." + scale, true)) tDimList.addNether();
				transferOldDimList(tName, tDimList);
				ADV_FILE.get(category, dims, tDimList.get());
				ADV_FILE.get(category, "Block", block);
				ADV_FILE.get(category, "BlockMeta", meta);
				ADV_FILE.get(category, biomes, new String[0]);
			}
		}
		if (GT_Values.D1) System.out.println("Transfer of stone configs finished.");
		
		if (GT_Values.D1) System.out.println("Transfer asteroid worldgen configs");
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
		if (GT_Values.D1) System.out.println("Transfer of asteroid configs finished.");
		
		if (GT_Values.D1) System.out.println("Transfer small ore worldgen configs");
		for (ConfigCategory c : OLD_FILE.mConfig.getCategory("worldgen.ore.small").getChildren()) {
			if (c.getName().equals("custom")) {
				if (GT_Values.D1) System.out.println("Transfer custom small ore configs");
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
				if (GT_Values.D1) System.out.println("Transfer of custom small ore finished.");
			} else {
				transferSmallOres(c, c.getName(), c.getName());
			}
		}
		if (GT_Values.D1) System.out.println("Transfer of all small ore configs finished");
		
		if (GT_Values.D1) System.out.println("Transfer oregen configs");
		for (ConfigCategory c : OLD_FILE.mConfig.getCategory("worldgen.ore.mix").getChildren()) {
			if (c.getName().equals("custom")) {
				if (GT_Values.D1) System.out.println("Transfer custom oregen configs");
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
				if (GT_Values.D1) System.out.println("Transfer of custom oregen configs finished.");
			} else {
				transferOreVeins(c, c.getName(), c.getName());
			}
		}
		if (GT_Values.D1) System.out.println("Transfer of all oregen configs finished");
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
					if (e.getValue().getBoolean()) tDimList.addEnd();; break;
				case "Asteroid":
					if (e.getValue().getBoolean()) tDimList.addAsteroid();; break;
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
					if (e.getValue().getBoolean()) tDimList.addEnd();; break;
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

	
	private static class SmallOreProp {
		public int mMinY, mMaxY, mAmount;
		public DimListBuffer mDimList = new DimListBuffer();
		public Materials mMaterial;
		public SmallOreProp() {
			this(0, 0, 0, false, false, false, false, false, false, Materials._NULL);
		}
		public SmallOreProp(int aMinY, int aMaxY, int aAmount, boolean aOverworld, boolean aNether, boolean aEnd, boolean aMoon, boolean aMars, boolean aAsteroid, Materials aMaterial) {
			mMinY = aMinY;
			mMaxY = aMaxY;
			mAmount = aAmount;
			if (aOverworld) mDimList.addOverworld();
			if (aNether) mDimList.addNether();
			if (aEnd) mDimList.addEnd();
			if (aMoon) mDimList.addMoon();
			if (aMars) mDimList.addMars();
			if (aAsteroid) mDimList.addAsteroid();
			mMaterial = aMaterial;
		}
	}

    public void run() {
    	/*if (GregTech_API.worldgenFileUpdate)*/ transferOldFile2();
        boolean tPFAA = (GregTech_API.sAdvWorldgenFile.get(ConfigCategories.general, "AutoDetectPFAA", true)) && (Loader.isModLoaded("PFAAGeologica"));

        new GT_Worldgenerator();
        if (Loader.isModLoaded("GalacticraftCore") && Loader.isModLoaded("GalacticraftMars")) {
            //new GT_Worldgenerator_Space();
        }

        final String textWorldgen = "worldgen";
        int dMeta, cMeta, dSize, cSize, dProb, cProb, dMinY, cMinY, dMaxY, cMaxY, dAmount, cAmount;
        String dBlock, cName;
        String[] cDims, cBiomes;
        Block cBlock;
        for (ConfigCategory cStone : ADV_FILE.mConfig.getCategory("worldgen.stone").getChildren()) {
        	switch (cStone.getName()) {
        	case "blackgranite": dBlock = getBlockName(GregTech_API.sBlockGranites); dMeta = 0; break;
			case "redgranite": dBlock = getBlockName(GregTech_API.sBlockGranites); dMeta = 8; break;
			case "marble": dBlock = getBlockName(GregTech_API.sBlockStones); dMeta = 0; break;
			case "basalt": dBlock = getBlockName(GregTech_API.sBlockStones); dMeta = 8; break;
			default: dBlock = ""; dMeta = 0;
        	}
        	for (ConfigCategory cScale : cStone.getChildren()) {
        		switch (cScale.getName()) {
        		case "tiny": dSize = 50; dProb = 48; break;
				case "small": dSize = 100; dProb = 96; break;
				case "medium": dSize = 200; dProb = 144; break;
				case "large": dSize = 300; dProb = 196; break;
				case "huge": dSize = 400; dProb = 240; break;
				default: dSize = 0; dProb = 0; break;
        		}
        		dMinY = 0; dMaxY = 120; dAmount = 1;
        		cName = "stone." + cStone.getName() + "." + cScale.getName();
        		cBlock = Block.getBlockFromName(ADV_FILE.get(textWorldgen + cName, "Block", dBlock));
        		cMeta = ADV_FILE.get(textWorldgen + cName, "BlockMeta", dMeta);
        		cAmount = ADV_FILE.get(textWorldgen + cName, "Amount", dAmount);
        		cSize = ADV_FILE.get(textWorldgen + cName, "Size", dSize);
        		cProb = ADV_FILE.get(textWorldgen + cName, "Probability", dProb);
        		cMinY = ADV_FILE.get(textWorldgen + cName, "MinHeight", dMinY);
        		cMaxY = ADV_FILE.get(textWorldgen + cName, "MaxY", dMaxY);
        		cDims = ADV_FILE.get(textWorldgen + cName, dims, new String[]{"Overworld"});
        		cBiomes = ADV_FILE.get(textWorldgen + cName, biomes, new String[0]);
        		if (cBlock == null || cAmount <= 0 || cSize <= 0 || cProb <= 0 || cMinY < 0) continue;
        		new GT_Worldgen_Stone(cName, true, cBlock, cMeta, 0, cAmount, cSize, cProb, cMinY, cMaxY, cDims, cBiomes, false);
        	}
        }
        
        HashMap<String, SmallOreProp> defaultSmallOres = new HashMap<>();
        defaultSmallOres.put("copper", new SmallOreProp(60, 120, 32, !tPFAA, true, true, true, true, false, Materials.Copper));
        defaultSmallOres.put("tin", new SmallOreProp(60, 120, 32, !tPFAA, true, true, true, true, true, Materials.Tin));
        defaultSmallOres.put("bismuth", new SmallOreProp(80, 120, 8, !tPFAA, true, false, true, true, false, Materials.Bismuth));
        defaultSmallOres.put("coal", new SmallOreProp(60, 100, 24, !tPFAA, false, false, false, false, false, Materials.Coal));
        defaultSmallOres.put("iron", new SmallOreProp(40, 80, 16, !tPFAA, true, true, true, true, false, Materials.Iron));
        defaultSmallOres.put("lead", new SmallOreProp(40, 80, 16, !tPFAA, true, true, true, true, true, Materials.Lead));
        defaultSmallOres.put("zinc", new SmallOreProp(30, 60, 12, !tPFAA, true, true, true, true, false, Materials.Zinc));
        defaultSmallOres.put("gold", new SmallOreProp(20, 40, 8, !tPFAA, true, true, true, true, true, Materials.Gold));
        defaultSmallOres.put("silver", new SmallOreProp(20, 40, 8, !tPFAA, true, true, true, true, true, Materials.Silver));
        defaultSmallOres.put("nickel", new SmallOreProp(20, 40, 8, !tPFAA, true, true, true, true, true, Materials.Nickel));
        defaultSmallOres.put("lapis", new SmallOreProp(20, 40, 4, !tPFAA, false, false, true, false, true, Materials.Lapis));
        defaultSmallOres.put("diamond", new SmallOreProp(5, 10, 2, !tPFAA, true, false, true, true, true, Materials.Diamond));
        defaultSmallOres.put("emerald", new SmallOreProp(5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Emerald));
        defaultSmallOres.put("ruby", new SmallOreProp(5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Ruby));
        defaultSmallOres.put("sapphire", new SmallOreProp(5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Sapphire));
        defaultSmallOres.put("greensapphire", new SmallOreProp(5, 250, 1, !tPFAA, true, false, false, true, true, Materials.GreenSapphire));
        defaultSmallOres.put("olivine", new SmallOreProp(5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Olivine));
        defaultSmallOres.put("topaz", new SmallOreProp(5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Topaz));
        defaultSmallOres.put("tanzanite", new SmallOreProp(5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Tanzanite));
        defaultSmallOres.put("amethyst", new SmallOreProp(5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Amethyst));
        defaultSmallOres.put("opal", new SmallOreProp(5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Opal));
        defaultSmallOres.put("jasper", new SmallOreProp(5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Jasper));
        defaultSmallOres.put("bluetopaz", new SmallOreProp(5, 250, 1, !tPFAA, true, false, false, true, true, Materials.BlueTopaz));
        defaultSmallOres.put("amber", new SmallOreProp(5, 250, 1, !tPFAA, true, false, false, true, true, Materials.Amber));
        defaultSmallOres.put("foolsruby", new SmallOreProp(5, 250, 1, !tPFAA, true, false, false, true, true, Materials.FoolsRuby));
        defaultSmallOres.put("garnetred", new SmallOreProp(5, 250, 1, !tPFAA, true, false, false, true, true, Materials.GarnetRed));
        defaultSmallOres.put("garnetyellow", new SmallOreProp(5, 250, 1, !tPFAA, true, false, false, true, true, Materials.GarnetYellow));
        defaultSmallOres.put("redstone", new SmallOreProp(5, 20, 8, !tPFAA, true, false, true, true, true, Materials.Redstone));
        defaultSmallOres.put("platinum", new SmallOreProp(20, 40, 8, false, false, true, false, true, true, Materials.Platinum));
        defaultSmallOres.put("iridium", new SmallOreProp(20, 40, 8, false, false, true, false, true, true, Materials.Iridium));
        defaultSmallOres.put("netherquartz", new SmallOreProp(30, 120, 64, false, true, false, false, false, false, Materials.NetherQuartz));
        defaultSmallOres.put("saltpeter", new SmallOreProp(10, 60, 8, false, true, false, false, false, false, Materials.Saltpeter));
        defaultSmallOres.put("sulfur_n", new SmallOreProp(10, 60, 32, false, true, false, false, false, false, Materials.Sulfur));
        defaultSmallOres.put("sulfur_o", new SmallOreProp(5, 15, 8, !tPFAA, false, false, false, false, false, Materials.Sulfur));
        
        SmallOreProp prop;
        for (ConfigCategory cOre : ADV_FILE.mConfig.getCategory("worldgen.ore.small").getChildren()) {
        	prop = defaultSmallOres.get(cOre.getName());
        	if (prop == null) prop = new SmallOreProp();
        	cName = "ore.small." + cOre.getName();
        	cMinY = ADV_FILE.get(textWorldgen + cName, "MinHeight", prop.mMinY);
        	cMaxY = ADV_FILE.get(textWorldgen + cName, "MaxHeight", prop.mMaxY);
        	cAmount = ADV_FILE.get(textWorldgen + cName, "Amount", prop.mAmount);
        	cMeta = getMaterialID(ADV_FILE.get(textWorldgen + cName, "Ore", prop.mMaterial.mName));
        	cDims = ADV_FILE.get(textWorldgen + cName, dims, prop.mDimList.get());
        	cBiomes = ADV_FILE.get(textWorldgen + cName, biomes, new String[0]);
        	if (cMinY < 0 || cMeta <= 0) continue;
        	new GT_Worldgen_GT_Ore_SmallPieces(cName, true, cMinY, cMaxY, cAmount, cDims, cBiomes, cMeta);
        }
        
        new GT_Worldgen_Asteroid(true);

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

    public static String getBlockName(Block aBlock) {
    	return Block.blockRegistry.getNameForObject(aBlock);
    }

    private static String getMaterialName(int aID) {
    	try {
    		return GregTech_API.sMaterials[aID].mName;
    	} catch (Throwable t) {
    		return null;
    	}
    }

    private static int getMaterialID(String aConfig) {
    	int tID = -1;
    	try {tID = Integer.parseInt(aConfig);}
    	catch (NumberFormatException e) {tID = Materials.get(aConfig).mMetaItemSubID;}
    	return tID;
    }
}
