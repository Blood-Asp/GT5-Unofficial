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
import gregtech.common.worldgen.GT_Worldgen_Asteroids;
import gregtech.common.worldgen.GT_Worldgen_Layer;
import gregtech.common.worldgen.GT_Worldgen_Layer.WeightedOreList;
import gregtech.common.worldgen.GT_Worldgen_SmallPieces;
import gregtech.common.worldgen.GT_Worldgen_Stone;
import gregtech.common.worldgen.GT_Worldgenerator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.common.util.ForgeDirection;

public class GT_Worldgenloader
        implements Runnable {
	private static final GT_Config ADV_FILE = GregTech_API.sAdvWorldgenFile, OLD_FILE = GregTech_API.oldWorldgenFile; 
	private static final String dims = "DimensionWhiteList", biomes = "RestrictedBiomes";

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
		ADV_FILE.get(category, "MinHeight", 50);
		ADV_FILE.get(category, "MaxHeight", 200);
		ADV_FILE.get(category, dims, new String[]{"The End"});
		ADV_FILE.get(category, "Block", getBlockName(Blocks.end_stone));
		ADV_FILE.get(category, "BlockMeta", 0);
		ADV_FILE.get(category, biomes, new String[0]);
		
		category = "worldgen.asteroid.gcasteroids";
		transferValue("worldgen", "gcasteroids", "asteroid.gcasteroids", "GenerateGCAsteroids", true);
		transferValue(category, "gcasteroids", "Probability", "GCAsteroidProbability", 300);
		transferValue(category, "gcasteroids", "MinSize", "GCAsteroidMinSize", 100);
		transferValue(category, "gcasteroids", "MaxSize", "GCAsteroidMaxSize", 400);
		ADV_FILE.get(category, "MinHeight", 50);
		ADV_FILE.get(category, "MaxHeight", 200);
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
					if (e.getValue().getBoolean()) tDimList.addEnd(); break;
				case "Asteroid":
					if (e.getValue().getBoolean()) tDimList.addAsteroid(); break;
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
		DimListBuffer tDimList = new DimListBuffer(), tAsteroidList = new DimListBuffer();
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
					if (e.getValue().getBoolean()) tDimList.addEnd(); break;
				case "EndAsteroid":
					if (e.getValue().getBoolean()) tAsteroidList.add("endasteroids"); break;
				case "Asteroid":
					if (e.getValue().getBoolean()) tAsteroidList.add("gcasteroids"); break;
				case "RestrictToBiomeName":
					String biome = e.getValue().getString();
					ADV_FILE.get(category, biomes, biome.equals("None") ? new String[0] : new String[]{biome}); break;
				}
			}
		}
		
		transferOldDimList("ore.mix." + aOldName, tDimList);
		ADV_FILE.get(category, dims, tDimList.get());
		ADV_FILE.get(category, "Asteroids", tAsteroidList.get());
		
		transferValue("worldgen", "worldgen", "ore.mix." + aName, "ore.mix." + aOldName, true);
	}


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

 	private static class StoneProp {
 		int mMinY, mMaxY, mSize, mProbability, mAmount, mMeta;
 		Block mBlock;
 		boolean mVoidGeneration;
 		public DimListBuffer mDimList = new DimListBuffer();
 		public StoneProp() {
 			this(0, 0, 0, 0, 0, null, 0, "", false);
 		}
 		public StoneProp(int aMinY, int aMaxY, int aAmount, int aSize, int aProbability, Block aBlock, int aMeta, String aDimList, boolean aVoidGeneration) {
 			mMinY = aMinY;
			mMaxY = aMaxY;
			mSize = aSize;
			mAmount = aAmount;
			mProbability = aProbability;
			mBlock = aBlock;
			mMeta = aMeta;
			mVoidGeneration = aVoidGeneration;
			if (aDimList != null)
				for (String s : aDimList.split(";"))
					mDimList.add(s);
 		}
 		public StoneProp(String aType, String aScale) {
 			boolean flag = true;
 			switch (aType) {
        	case "blackgranite": mBlock = GregTech_API.sBlockGranites; mMeta = 0; break;
			case "redgranite": mBlock = GregTech_API.sBlockGranites; mMeta = 8; break;
			case "marble": mBlock = GregTech_API.sBlockStones; mMeta = 0; break;
			case "basalt": mBlock = GregTech_API.sBlockStones; mMeta = 8; break;
			default: mBlock = null; mMeta = 0; flag = false;
        	}
 			switch (aScale) {
    		case "tiny": mSize = 50; mProbability = 48; break;
			case "small": mSize = 100; mProbability = 96; break;
			case "medium": mSize = 200; mProbability = 144; break;
			case "large": mSize = 300; mProbability = 196; break;
			case "huge": mSize = 400; mProbability = 240; break;
			default: mSize = 0; mProbability = 0; flag = false;
    		}
 			mMinY = 0;
 			mMaxY = flag ? 120 : 0;
 			mAmount = flag ? 1 : 0;
 			mVoidGeneration = false;
 			if (flag) mDimList.add("Overworld");
 		}
 	}

	private static class AsteroidProp {
		public int mMinY, mMaxY, mMinSize, mMaxSize, mProbability, mMeta;
		public DimListBuffer mDimList = new DimListBuffer();
		public Block mBlock;
		public AsteroidProp() {
			this(0, 0, 0, 0, 0, null, 0, "");
		}
		public AsteroidProp(int aMinY, int aMaxY, int aMinSize, int aMaxSize, int aProbability, Block aBlock, int aMeta, String aDimList) {
			mMinY = aMinY;
			mMaxY = aMaxY;
			mMinSize = aMinSize;
			mMaxSize = aMaxSize;
			mProbability = aProbability;
			mBlock = aBlock;
			mMeta = aMeta;
			if (aDimList != null)
				for (String s : aDimList.split(";"))
					mDimList.add(s);
		}
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

	private static class OreVeinProp {
		public int mMinY, mMaxY, mWeight, mDensity, mSize;
		public DimListBuffer mDimList = new DimListBuffer(), mAsteroidList = new DimListBuffer();
		public WeightedOreList mPrimaries = new WeightedOreList(), mSecondaries = new WeightedOreList(), mBetweens = new WeightedOreList(), mSporadics = new WeightedOreList();
		public OreVeinProp() {
			this(0, 0, 0, 0, 0, false, false, false, false, false, false, Materials._NULL, Materials._NULL, Materials._NULL, Materials._NULL);
		}
		public OreVeinProp(int aMinY, int aMaxY, int aWeight, int aDensity, int aSize, boolean aOverworld, boolean aNether, boolean aEnd, boolean aMoon, boolean aMars, boolean aAsteroid, Materials aPrimary, Materials aSecondary, Materials aBetween, Materials aSporadic) {
			this(aMinY, aMaxY, aWeight, aDensity, aSize, aOverworld, aNether, aEnd, aMoon, aMars, aAsteroid, aPrimary.mName + ":100", aSecondary.mName + ":100", aBetween.mName + ":100", aSporadic.mName + ":100");
		}
		public OreVeinProp(int aMinY, int aMaxY, int aWeight, int aDensity, int aSize, boolean aOverworld, boolean aNether, boolean aEnd, boolean aMoon, boolean aMars, boolean aAsteroid, String aPrimaries, String aSecondaries, String aBetweens, String aSporadics) {
			mMinY = aMinY;
			mMaxY = aMaxY;
			mWeight = aWeight;
			mDensity = aDensity;
			mSize = aSize;
			if (aOverworld) mDimList.addOverworld();
			if (aNether) mDimList.addNether();
			if (aEnd) mDimList.addEnd();
			if (aMoon) mDimList.addMoon();
			if (aMars) mDimList.addMars();
			if (aEnd) mAsteroidList.add("endasteroids");
			if (aAsteroid) mAsteroidList.add("gcasteroids");
			mPrimaries.addAll(aPrimaries.split(";"));
			mSecondaries.addAll(aSecondaries.split(";"));
			mBetweens.addAll(aBetweens.split(";"));
			mSporadics.addAll(aSporadics.split(";"));
		}
	}

    public void run() {
    	if (GregTech_API.worldgenFileUpdate) transferOldFile2();
        boolean tPFAA = (ADV_FILE.get(ConfigCategories.general, "AutoDetectPFAA", true)) && (Loader.isModLoaded("PFAAGeologica"));

        new GT_Worldgenerator();
        if (Loader.isModLoaded("GalacticraftCore") && Loader.isModLoaded("GalacticraftMars")) {
            //new GT_Worldgenerator_Space();
        }

        final String textWorldgen = "worldgen.";
        
        // Format examples for custom worldgens
        ADV_FILE.get(textWorldgen + "stone.custom_stone.custom_scale", dims, new String[0]);
        ADV_FILE.get(textWorldgen + "asteroid.custom_asteroid", dims, new String[0]);
        ADV_FILE.get(textWorldgen + "ore.small.custom", dims, new String[0]);
        ADV_FILE.get(textWorldgen + "ore.mix.custom", dims, new String[0]);
        
        StoneProp _prop;
        int cMeta, cSize, cProb, cMinY, cMaxY, cAmount;
        String cName;
        String[] cDims, cBiomes;
        Block cBlock;
        boolean cVoid;
        for (ConfigCategory cStone : ADV_FILE.mConfig.getCategory("worldgen.stone").getChildren()) {
        	for (ConfigCategory cScale : cStone.getChildren()) {
        		_prop = new StoneProp(cStone.getName(), cScale.getName());
        		cName = "stone." + cStone.getName() + "." + cScale.getName();
        		cBlock = getBlock(ADV_FILE.get(textWorldgen + cName, "Block", getBlockName(_prop.mBlock)));
        		cMeta = ADV_FILE.get(textWorldgen + cName, "BlockMeta", _prop.mMeta);
        		cAmount = ADV_FILE.get(textWorldgen + cName, "Amount", _prop.mAmount);
        		cSize = ADV_FILE.get(textWorldgen + cName, "Size", _prop.mSize);
        		cProb = ADV_FILE.get(textWorldgen + cName, "Probability", _prop.mProbability);
        		cMinY = ADV_FILE.get(textWorldgen + cName, "MinHeight", _prop.mMinY);
        		cMaxY = ADV_FILE.get(textWorldgen + cName, "MaxY", _prop.mMaxY);
        		cVoid = ADV_FILE.get(textWorldgen + cName, "AllowedToGenerateInVoid", _prop.mVoidGeneration);
        		cDims = ADV_FILE.get(textWorldgen + cName, dims, _prop.mDimList.get());
        		cBiomes = ADV_FILE.get(textWorldgen + cName, biomes, new String[0]);
        		if (cBlock == null || cBlock.equals(Blocks.air) || cAmount <= 0 || cSize <= 0 || cProb <= 0 || cMinY <= 0) continue;
        		new GT_Worldgen_Stone(cName, true, cBlock, cMeta, cAmount, cSize, cProb, cMinY, cMaxY, cDims, cBiomes, false);
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
        
        SmallOreProp propSmallOre;
        for (ConfigCategory cOre : ADV_FILE.mConfig.getCategory("worldgen.ore.small").getChildren()) {
        	propSmallOre = defaultSmallOres.get(cOre.getName());
        	if (propSmallOre == null) propSmallOre = new SmallOreProp();
        	cName = "ore.small." + cOre.getName();
        	cMinY = ADV_FILE.get(textWorldgen + cName, "MinHeight", propSmallOre.mMinY);
        	cMaxY = ADV_FILE.get(textWorldgen + cName, "MaxHeight", propSmallOre.mMaxY);
        	cAmount = ADV_FILE.get(textWorldgen + cName, "Amount", propSmallOre.mAmount);
        	cMeta = getMaterialID(ADV_FILE.get(textWorldgen + cName, "Ore", propSmallOre.mMaterial.mName));
        	cDims = ADV_FILE.get(textWorldgen + cName, dims, propSmallOre.mDimList.get());
        	cBiomes = ADV_FILE.get(textWorldgen + cName, biomes, new String[0]);
        	if (cMinY < 0 || cMeta <= 0) continue;
        	new GT_Worldgen_SmallPieces(cName, true, cMinY, cMaxY, cAmount, cDims, cBiomes, cMeta);
        }
        
        HashMap<String, AsteroidProp> defaultAsteroids = new HashMap<>();
        defaultAsteroids.put("endasteroids", new AsteroidProp(50, 200, 50, 200, 300, Blocks.end_stone, 0, "The End"));
        defaultAsteroids.put("gcasteroids", new AsteroidProp(50, 200, 100, 400, 300, GregTech_API.sBlockGranites, 8, "Asteroids"));
        
        AsteroidProp propAsteroid;
        int cMinSize, cMaxSize;
        for (ConfigCategory cOre : ADV_FILE.mConfig.getCategory("worldgen.asteroid").getChildren()) {
        	propAsteroid = defaultAsteroids.get(cOre.getName());
        	if (propAsteroid == null) propAsteroid = new AsteroidProp();
        	cName = "asteroid." + cOre.getName();
        	cMinY = ADV_FILE.get(textWorldgen + cName, "MinHeight", propAsteroid.mMinY);
        	cMaxY = ADV_FILE.get(textWorldgen + cName, "MaxHeight", propAsteroid.mMaxY);
        	cMinSize = ADV_FILE.get(textWorldgen + cName, "MinSize", propAsteroid.mMinSize);
        	cMaxSize = ADV_FILE.get(textWorldgen + cName, "MaxSize", propAsteroid.mMaxSize);
        	cProb = ADV_FILE.get(textWorldgen + cName, "Probability", propAsteroid.mProbability);
        	cBlock = getBlock(ADV_FILE.get(textWorldgen + cName, "Block", getBlockName(propAsteroid.mBlock)));
    		cMeta = ADV_FILE.get(textWorldgen + cName, "BlockMeta", propAsteroid.mMeta);
        	cDims = ADV_FILE.get(textWorldgen + cName, dims, propAsteroid.mDimList.get());
        	cBiomes = ADV_FILE.get(textWorldgen + cName, biomes, new String[0]);
        	if (cBlock == null || cBlock.equals(Blocks.air) || cMinSize < 0 || cProb <= 0 || cMinY <= 0) continue;
        	new GT_Worldgen_Asteroids(cName, true, cBlock, cMeta, cMinY, cMaxY, cMinSize, cMaxSize, cProb, cDims, cBiomes);
        }
        
        HashMap<String, OreVeinProp> defaultOreVeins = new HashMap<>();
        defaultOreVeins.put("naquadah", new OreVeinProp(10, 60, 10, 5, 32, false, false, true, false, true, true, Materials.Naquadah, Materials.Naquadah, Materials.Naquadah, Materials.NaquadahEnriched));
        defaultOreVeins.put("lignite", new OreVeinProp(50, 130, 160, 8, 32, !tPFAA, false, false, false, false, false, Materials.Lignite, Materials.Lignite, Materials.Lignite, Materials.Coal));
        defaultOreVeins.put("coal", new OreVeinProp(50, 80, 80, 6, 32, !tPFAA, false, false, false, false, false, Materials.Coal, Materials.Coal, Materials.Coal, Materials.Lignite));
        defaultOreVeins.put("magnetite", new OreVeinProp(50, 120, 160, 3, 32, !tPFAA, true, false, true, true, false, Materials.Magnetite, Materials.Magnetite, Materials.Iron, Materials.VanadiumMagnetite));
        defaultOreVeins.put("gold", new OreVeinProp(60, 80, 160, 3, 32, !tPFAA, false, false, true, true, true, Materials.Magnetite, Materials.Magnetite, Materials.VanadiumMagnetite, Materials.Gold));
        defaultOreVeins.put("iron", new OreVeinProp(10, 40, 120, 4, 24, !tPFAA, true, false, true, true, false, Materials.BrownLimonite, Materials.YellowLimonite, Materials.BandedIron, Materials.Malachite));
        defaultOreVeins.put("cassiterite", new OreVeinProp(40, 120, 50, 5, 24, !tPFAA, false, true, true, true, true, Materials.Tin, Materials.Tin, Materials.Cassiterite, Materials.Tin));
        defaultOreVeins.put("tetrahedrite", new OreVeinProp(80, 120, 70, 4, 24, !tPFAA, true, false, true, true, true, Materials.Tetrahedrite, Materials.Tetrahedrite, Materials.Copper, Materials.Stibnite));
        defaultOreVeins.put("netherquartz", new OreVeinProp(40, 80, 80, 5, 24, false, true, false, false, false, false, Materials.NetherQuartz, Materials.NetherQuartz, Materials.NetherQuartz, Materials.NetherQuartz));
        defaultOreVeins.put("sulfur", new OreVeinProp(5, 20, 100, 5, 24, false, true, false, false, true, false, Materials.Sulfur, Materials.Sulfur, Materials.Pyrite, Materials.Sphalerite));
        defaultOreVeins.put("copper", new OreVeinProp(10, 30, 80, 4, 24, !tPFAA, true, false, true, true, false, Materials.Chalcopyrite, Materials.Iron, Materials.Pyrite, Materials.Copper));
        defaultOreVeins.put("bauxite", new OreVeinProp(50, 90, 80, 4, 24, !tPFAA, tPFAA, false, true, true, true, Materials.Bauxite, Materials.Bauxite, Materials.Aluminium, Materials.Ilmenite));
        defaultOreVeins.put("salts", new OreVeinProp(50, 60, 50, 3, 24, !tPFAA, false, false, true, false, false, Materials.RockSalt, Materials.Salt, Materials.Lepidolite, Materials.Spodumene));
        defaultOreVeins.put("redstone", new OreVeinProp(10, 40, 60, 3, 24, !tPFAA, true, false, true, true, true, Materials.Redstone, Materials.Redstone, Materials.Ruby, Materials.Cinnabar));
        defaultOreVeins.put("soapstone", new OreVeinProp(10, 40, 40, 3, 16, !tPFAA, false, false, true, true, false, Materials.Soapstone, Materials.Talc, Materials.Glauconite, Materials.Pentlandite));
        defaultOreVeins.put("nickel", new OreVeinProp(10, 40, 40, 3, 16, !tPFAA, true, true, true, true, true, Materials.Garnierite, Materials.Nickel, Materials.Cobaltite, Materials.Pentlandite));
        defaultOreVeins.put("platinum", new OreVeinProp(40, 50, 5, 3, 16, !tPFAA, false, true, false, true, true, Materials.Cooperite, Materials.Palladium, Materials.Platinum, Materials.Iridium));
        defaultOreVeins.put("pitchblende", new OreVeinProp(10, 40, 40, 3, 16, !tPFAA, false, false, true, true, true, Materials.Pitchblende, Materials.Pitchblende, Materials.Uraninite, Materials.Uraninite));
        defaultOreVeins.put("uranium", new OreVeinProp(20, 30, 20, 3, 16, !tPFAA, false, false, true, true, true, Materials.Uraninite, Materials.Uraninite, Materials.Uranium, Materials.Uranium));
        defaultOreVeins.put("monazite", new OreVeinProp(20, 40, 30, 3, 16, !tPFAA, tPFAA, false, true, true, true, Materials.Bastnasite, Materials.Bastnasite, Materials.Monazite, Materials.Neodymium));
        defaultOreVeins.put("molybdenum", new OreVeinProp(20, 50, 5, 3, 16, !tPFAA, false, true, true, true, true, Materials.Wulfenite, Materials.Molybdenite, Materials.Molybdenum, Materials.Powellite));
        defaultOreVeins.put("tungstate", new OreVeinProp(20, 50, 10, 3, 16, !tPFAA, false, true, true, true, true, Materials.Scheelite, Materials.Scheelite, Materials.Tungstate, Materials.Lithium));
        defaultOreVeins.put("sapphire", new OreVeinProp(10, 40, 60, 3, 16, !tPFAA, tPFAA, tPFAA, true, true, true, Materials.Almandine, Materials.Pyrope, Materials.Sapphire, Materials.GreenSapphire));
        defaultOreVeins.put("manganese", new OreVeinProp(20, 30, 20, 3, 16, !tPFAA, false, true, true, false, true, Materials.Grossular, Materials.Spessartine, Materials.Pyrolusite, Materials.Tantalite));
        defaultOreVeins.put("quartz", new OreVeinProp(40, 80, 60, 3, 16, !tPFAA, tPFAA, false, true, true, true, Materials.Quartzite, Materials.Barite, Materials.CertusQuartz, Materials.CertusQuartz));
        defaultOreVeins.put("diamond", new OreVeinProp(5, 20, 40, 2, 16, !tPFAA, false, false, true, true, true, Materials.Graphite, Materials.Graphite, Materials.Diamond, Materials.Coal));
        defaultOreVeins.put("olivine", new OreVeinProp(10, 40, 60, 3, 16, !tPFAA, false, true, true, true, true, Materials.Bentonite, Materials.Magnesite, Materials.Olivine, Materials.Glauconite));
        defaultOreVeins.put("apatite", new OreVeinProp(40, 60, 60, 3, 16, !tPFAA, false, false, false, false, false, "Apatite:100", "Apatite:100", "Phosphorus:100", "Pyrochlore:30;Phosphate:70"));
        defaultOreVeins.put("galena", new OreVeinProp(30, 60, 40, 5, 16, !tPFAA, false, false, true, true, true, Materials.Galena, Materials.Galena, Materials.Silver, Materials.Lead));
        defaultOreVeins.put("lapis", new OreVeinProp(20, 50, 40, 5, 16, !tPFAA, false, true, true, true, true, Materials.Lazurite, Materials.Sodalite, Materials.Lapis, Materials.Calcite));
        defaultOreVeins.put("beryllium", new OreVeinProp(5, 30, 30, 3, 16, !tPFAA, false, true, true, true, true, Materials.Beryllium, Materials.Beryllium, Materials.Emerald, Materials.Thorium));
        defaultOreVeins.put("oilsand", new OreVeinProp(50, 80, 80, 6, 32, !tPFAA, false, false, false, false, false, Materials.Oilsands, Materials.Oilsands, Materials.Oilsands, Materials.Oilsands));
        
        if (GregTech_API.mImmersiveEngineering && GT_Mod.gregtechproxy.mImmersiveEngineeringRecipes) {
            blusunrize.immersiveengineering.api.tool.ExcavatorHandler.mineralList.clear();
            blusunrize.immersiveengineering.api.tool.ExcavatorHandler.mineralCache.clear();
        }
        
        OreVeinProp propVein;
        int cDens;
        String[] cAsteroids;
        WeightedOreList[] cOres = new WeightedOreList[4];
        for (ConfigCategory cOre : ADV_FILE.mConfig.getCategory("worldgen.ore.mix").getChildren()) {
        	propVein = defaultOreVeins.get(cOre.getName());
        	if (propVein == null) propVein = new OreVeinProp();
        	cName = "ore.mix." + cOre.getName();
        	cMinY = ADV_FILE.get(textWorldgen + cName, "MinHeight", propVein.mMinY);
        	cMaxY = ADV_FILE.get(textWorldgen + cName, "MaxHeight", propVein.mMaxY);
        	cProb = ADV_FILE.get(textWorldgen + cName, "RandomWeight", propVein.mWeight);
        	cDens = ADV_FILE.get(textWorldgen + cName, "Density", propVein.mDensity);
        	cSize = ADV_FILE.get(textWorldgen + cName, "Size", propVein.mSize);
        	cOres[0] = new WeightedOreList(ADV_FILE.get(textWorldgen + cName, "OrePrimaryLayer", propVein.mPrimaries.toConfig()));
        	cOres[1] = new WeightedOreList(ADV_FILE.get(textWorldgen + cName, "OreSecondaryLayer", propVein.mSecondaries.toConfig()));
        	cOres[2] = new WeightedOreList(ADV_FILE.get(textWorldgen + cName, "OreSporadiclyInbetween", propVein.mBetweens.toConfig()));
        	cOres[3] = new WeightedOreList(ADV_FILE.get(textWorldgen + cName, "OreSporaticlyAround", propVein.mSporadics.toConfig()));
        	cDims = ADV_FILE.get(textWorldgen + cName, dims, propVein.mDimList.get());
        	cAsteroids = ADV_FILE.get(textWorldgen + cName, "Asteroids", propVein.mAsteroidList.get());
        	cBiomes = ADV_FILE.get(textWorldgen + cName, biomes, new String[0]);
        	if (cMinY < 0 || cProb <= 0 || cDens <= 0 || (cOres[0].isEmpty() && cOres[1].isEmpty() && cOres[2].isEmpty() && cOres[3].isEmpty())) continue;
        	new GT_Worldgen_Layer(cName, true, cMinY, cMaxY, cProb, cDens, cSize, cDims, cAsteroids, cBiomes, cOres[0], cOres[1], cOres[2], cOres[3]);
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
    	return new String[]{aConfig.substring(0, aConfig.lastIndexOf('_')), aConfig.substring(aConfig.lastIndexOf('_') + 1)};
    }

    public static String getBlockName(Block aBlock) {
    	String tName = Block.blockRegistry.getNameForObject(aBlock);
    	return GT_Utility.isStringValid(tName) ? tName : "NULL";
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
	
	private static String getSideName(int aSide) {
		if (aSide < 0 || aSide > 6) aSide = 0;
		return ForgeDirection.getOrientation(aSide).name();
	}

	private static Block getBlock(String aName) {
		return Block.getBlockFromName(aName);
	}
	
	private static int getSide(String aSide) {
		try {return ForgeDirection.valueOf(aSide).ordinal();}
		catch (Throwable t) {return 0;}
	}

    
}