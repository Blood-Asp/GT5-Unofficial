package gregtech.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.Materials;
import gregtech.api.util.GT_Utility;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.blocks.GT_TileEntity_Ores;
import gregtech.loaders.misc.GT_Achievements;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import scala.actors.threadpool.Arrays;

public class GT_Worldgen_GT_Ore_Layer
        extends GT_Worldgen {
    public static ArrayList<GT_Worldgen_GT_Ore_Layer> sList = new ArrayList();
    @Deprecated
    public static int sWeight = 0;
    protected static ConcurrentHashMap<String, OreGenList> sDimSpecifiedOreGenMap = new ConcurrentHashMap<>();
    public final short mMinY;
    public final short mMaxY;
    public final short mWeight;
    public final short mDensity;
    public final short mSize;
    @Deprecated
    public final short mPrimaryMeta, mSecondaryMeta, mBetweenMeta, mSporadicMeta;
    @Deprecated
    public final String mRestrictBiome;
    @Deprecated
    public final boolean mOverworld, mNether, mEnd, mEndAsteroid, mMoon, mMars, mAsteroid;
    public final static String aTextWorldgen = "worldgen.";
    
    public final ArrayList<String> mBiomeList = new ArrayList<>(), mAsteroidList = new ArrayList<>();
    public final WeightedOreList mPrimaries, mSecondaries, mBetweens, mSporadics; 

    protected static class WeightedOre {
    	protected int mWeight, mMeta;
    	
    	public WeightedOre() {
    		this(-1, 0);
    	}
    	
    	public WeightedOre(int aMeta, int aWeight) {
    		mMeta = aMeta; mWeight = aWeight;
    	}
    	
    	public WeightedOre(Materials aMaterial, int aWeight) {
    		this(aMaterial == null ? -1 : aMaterial.mMetaItemSubID, aWeight);
    	}
    	
    	public WeightedOre(String aConfig) {
    		if (GT_Utility.isStringValid(aConfig)) {
    			String[] k = aConfig.split(":");
        		try {mMeta = Integer.parseInt(k[0]);}
    			catch (Exception e) {mMeta = Materials.get(k[0]).mMetaItemSubID;}
    			try {mWeight = Integer.parseInt(k[1]);}
    			catch (Exception e) {mWeight = 100;}
    		} else {mMeta = -1; mWeight = 0;}
    	}
    	
    	@Override
    	public String toString() {
    		if (isValid()) {
    			Materials tMaterial = GregTech_API.sMaterials[mMeta];
    			if (tMaterial != null) {
    				return tMaterial.mName + ":" + mWeight;
    			}
    		}
    		return "NULL:0";
    	}
    	
    	public boolean isValid() {
    		return mMeta > 0 && mMeta < 1000 && mWeight > 0;
    	}
    }

    public static class WeightedOreList {
    	protected ArrayList<WeightedOre> mOres = new ArrayList<>();
    	protected int mWeight = 0;
    	
    	public WeightedOreList(String... aConfigs) {
    		addAll(aConfigs);
    	}
    	
    	public void add(WeightedOre aOre) {
    		if (aOre.isValid()) {
    			mOres.add(aOre);
    			mWeight += aOre.mWeight;
    		}
    	}
    	
    	public void addAll(String... aConfigs) {
    		for (String s : aConfigs)
    			add(new WeightedOre(s));
    	}
    	
    	public void addAll(WeightedOreList aOreList, int aWeight) {
    		for (WeightedOre o : aOreList.mOres)
    			add(new WeightedOre(o.mMeta, o.mWeight * aWeight));
    	}
    	
    	public String[] toConfig() {
    		ArrayList<String> tConfigs = new ArrayList<>();
    		for (WeightedOre o : mOres) if (o.isValid()) tConfigs.add(o.toString());
    		return tConfigs.toArray(new String[tConfigs.size()]);
    	}
    	
    	public boolean isEmpty() {
    		return mOres.isEmpty();
    	}
    	
    	private int getOre() {
    		return mOres.isEmpty() ? -1 : mOres.get(0).mMeta;
    	}
    	
    	public int getOre(Random aRandom) {
    		if (!mOres.isEmpty()) {
    			if (mOres.size() == 1) return mOres.get(0).mMeta;
    			int tWeight = aRandom.nextInt(mWeight);
    			for (WeightedOre o : mOres) {
    				if ((tWeight -= o.mWeight) <= 0) return o.mMeta;
    			}
    		}
    		return -1;
    	}
    }

    public static class OreGenList {
    	protected ArrayList<GT_Worldgen_GT_Ore_Layer> list = new ArrayList<>();
    	protected int mWeight = 0;
    }

    public static OreGenList getOreGenData(World aWorld, int aDimensionType, String aAsteroid) {
    	String aDimName = aWorld.provider.getDimensionName() + (GT_Utility.isStringValid(aAsteroid) ? "." + aAsteroid : "");
    	OreGenList rList;
    	if ((rList = sDimSpecifiedOreGenMap.get(aDimName)) == null) {
    		if (GT_Values.D1) System.out.println("Initializing dimensional-specified Orevein list for: " + aDimName);
    		rList = new OreGenList();
    		for (GT_Worldgen_GT_Ore_Layer tOreGen : sList)
    			if ((GT_Utility.isStringValid(aAsteroid) && tOreGen.mAsteroidList.contains(aAsteroid)) ||
    					tOreGen.isGenerationAllowed(aWorld, aDimensionType, ((aDimensionType == -1) && (tOreGen.isNetherAllowed())) || ((aDimensionType == 0) && (tOreGen.isOverWorldAllowed())) || ((aDimensionType == 1) && (tOreGen.isEndAllowed())) ? aDimensionType : ~aDimensionType)) {
    				rList.list.add(tOreGen);
    				rList.mWeight += tOreGen.mWeight;
    			}
    		
    		sDimSpecifiedOreGenMap.put(aDimName, rList);
    	}
    	return rList;
    }

    public static GT_Worldgen_GT_Ore_Layer getRandomOreVein(World aWorld, int aDimensionType, String aAsteroid, Random aRandom) {
    	OreGenList tList = getOreGenData(aWorld, aDimensionType, aAsteroid);
    	if (tList.mWeight > 0 && tList.list.size() > 0) {
    		int tWeight = aRandom.nextInt(tList.mWeight);
    		for (GT_Worldgen_GT_Ore_Layer tOreGen : tList.list)
    			if ((tWeight -= tOreGen.mWeight) <= 0) return tOreGen;
    	}
    	return null;
    }

    @Deprecated
    public GT_Worldgen_GT_Ore_Layer(String aName, boolean aDefault, int aMinY, int aMaxY, int aWeight, int aDensity, int aSize, boolean aOverworld, boolean aNether, boolean aEnd, boolean aMoon, boolean aMars, boolean aAsteroid, Materials aPrimary, Materials aSecondary, Materials aBetween, Materials aSporadic) {
        super(aName, sList, aDefault, GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + aName, "DimensionWhiteList", new String[]{aOverworld ? "Overworld" : "", aNether ? "Nether" : "", aEnd ? "The End" : "", aMoon ? "Moon" : "", aMars ? "Mars" : ""}));
        this.mMinY = ((short) GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "MinHeight", aMinY));
        this.mMaxY = ((short) Math.max(this.mMinY + 5, GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "MaxHeight", aMaxY)));
        this.mWeight = ((short) GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "RandomWeight", aWeight));
        this.mDensity = ((short) GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Density", aDensity));
        this.mSize = ((short) Math.max(1, GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Size", aSize)));
        this.mPrimaries = new WeightedOreList(GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "OrePrimaryLayer", new String[]{aPrimary.mName}));
        this.mSecondaries = new WeightedOreList(GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "OreSecondaryLayer", new String[]{aSecondary.mName}));
        this.mBetweens = new WeightedOreList(GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "OreSporadiclyInbetween", new String[]{aBetween.mName}));
        this.mSporadics = new WeightedOreList(GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "OreSporaticlyAround", new String[]{aSporadic.mName}));
        this.mBiomeList.addAll(Arrays.asList(GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "RestrictedBiomes", new String[0])));
        ArrayList<String> aAsteroids = new ArrayList<>();
        if (aEnd) aAsteroids.add("endasteroids");
        if (aAsteroid) aAsteroids.add("gcasteroids");
        this.mAsteroidList.addAll(Arrays.asList(GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Asteroids", aAsteroids.toArray(new String[0]))));
        this.mOverworld = this.isOverWorldAllowed();
    	this.mNether = this.isNetherAllowed();
    	this.mEnd = this.isEndAllowed();
    	this.mMoon = this.isMoonAllowed();
    	this.mMars = this.isMarsAllowed();
    	this.mEndAsteroid = this.mAsteroidList.contains("endasteroids");
    	this.mAsteroid = this.mAsteroidList.contains("gcasteroids");
    	this.mPrimaryMeta = (short) this.mPrimaries.getOre();
    	this.mSecondaryMeta = (short) this.mSecondaries.getOre();
    	this.mBetweenMeta = (short) this.mBetweens.getOre();
    	this.mSporadicMeta = (short) this.mSporadics.getOre();
    	this.mRestrictBiome = this.mBiomeList.isEmpty() ? "None" : this.mBiomeList.get(0);

        if (this.mEnabled) registerOres();
    }

    public GT_Worldgen_GT_Ore_Layer(String aName, boolean aDefault, int aMinY, int aMaxY, int aWeight, int aDensity, int aSize, String[] aDimList, String[] aAsteroidList, String[] aBiomeList, WeightedOreList aPrimaries, WeightedOreList aSecondaries, WeightedOreList aBetweens, WeightedOreList aSporadics) {
    	super(aName, sList, aDefault, aDimList);
    	this.mMinY = (short) aMinY;
    	this.mMaxY = (short) Math.max(aMinY + 5, aMaxY);
    	this.mWeight = (short) aWeight;
    	this.mDensity = (short) aDensity;
    	this.mSize = (short) Math.max(1, aSize);
    	this.mPrimaries = aPrimaries;
    	this.mSecondaries = aSecondaries;
    	this.mBetweens = aBetweens;
    	this.mSporadics = aSporadics;
    	this.mBiomeList.addAll(Arrays.asList(aBiomeList));
    	this.mAsteroidList.addAll(Arrays.asList(aAsteroidList));
    	this.mOverworld = this.isOverWorldAllowed();
    	this.mNether = this.isNetherAllowed();
    	this.mEnd = this.isEndAllowed();
    	this.mMoon = this.isMoonAllowed();
    	this.mMars = this.isMarsAllowed();
    	this.mEndAsteroid = this.mAsteroidList.contains("endasteroids");
    	this.mAsteroid = this.mAsteroidList.contains("gcasteroids");
    	this.mPrimaryMeta = (short) this.mPrimaries.getOre();
    	this.mSecondaryMeta = (short) this.mSecondaries.getOre();
    	this.mBetweenMeta = (short) this.mBetweens.getOre();
    	this.mSporadicMeta = (short) this.mSporadics.getOre();
    	this.mRestrictBiome = this.mBiomeList.isEmpty() ? "None" : this.mBiomeList.get(0);
    	if (this.mEnabled) registerOres();
    }

    private void registerOres() {
    	Map<String, Float> tIEWeightedOres = new HashMap<>();
    	registerOresFromList(this.mPrimaries, tIEWeightedOres, .4f);
    	registerOresFromList(this.mSecondaries, tIEWeightedOres, .4f);
    	registerOresFromList(this.mBetweens, tIEWeightedOres, .15f);
    	registerOresFromList(this.mSporadics, tIEWeightedOres, .05f);
    	sWeight += this.mWeight;
    	if(GregTech_API.mImmersiveEngineering && GT_Mod.gregtechproxy.mImmersiveEngineeringRecipes){
    		String[] tNames = new String[tIEWeightedOres.size()];
    		float[] tWeights = new float[tIEWeightedOres.size()];
    		int i = 0;
    		for (Map.Entry<String, Float> e : tIEWeightedOres.entrySet()) {
    			tNames[i] = e.getKey();
    			tWeights[i++] = e.getValue();
    		}
        	blusunrize.immersiveengineering.api.tool.ExcavatorHandler.addMineral(mWorldGenName.substring(8, 9).toUpperCase() + mWorldGenName.substring(9), mWeight, 0.2f, tNames, tWeights);
        }
    }

    private void registerOresFromList(WeightedOreList aOreList, Map<String, Float> aIEOres, float aTotalIEWeight) {
    	boolean tOverworld = this.isOverWorldAllowed(), tNether = this.isNetherAllowed(), tEnd = this.isEndAllowed();
    	for (WeightedOre o : aOreList.mOres) {
    		Materials tMaterial = GregTech_API.sGeneratedMaterials[o.mMeta];
    		GT_Achievements.registerOre(tMaterial, this.mMinY, this.mMaxY, this.mWeight, tOverworld, tNether, tEnd);
    		aIEOres.put("ore" + tMaterial.mName, aTotalIEWeight * o.mWeight / aOreList.mWeight);
    	}
    }

    private int getDense(int a, int b, int x) {
    	return Math.max(1, Math.max(MathHelper.abs_int(a - x), MathHelper.abs_int(b - x)) / this.mDensity);
    }

    public boolean executeWorldgen(World aWorld, Random aRandom, String aBiome, int aDimensionType, int aChunkX, int aChunkZ, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        if (!this.mBiomeList.isEmpty() && !this.mBiomeList.contains(aBiome)) {
            return false; //Not the correct biome for ore mix
        }
        int tMinY = this.mMinY + aRandom.nextInt(this.mMaxY - this.mMinY - 5);

        int cX = aChunkX - aRandom.nextInt(this.mSize);
        int eX = aChunkX + 16 + aRandom.nextInt(this.mSize);
        int tOreMeta;
        for (int tX = cX; tX <= eX; tX++) {
        	int cZ = aChunkZ - aRandom.nextInt(this.mSize);
            int eZ = aChunkZ + 16 + aRandom.nextInt(this.mSize);
            for (int tZ = cZ; tZ <= eZ; tZ++) {
                if (!this.mSecondaries.isEmpty()) {
                    for (int i = tMinY - 1; i < tMinY + 2; i++) {
                        if ((aRandom.nextInt(getDense(cZ, eZ, tZ)) == 0) || (aRandom.nextInt(getDense(cX, eX, tX)) == 0)) {
                            if ((tOreMeta = this.mSecondaries.getOre(aRandom)) > 0)
                            	GT_TileEntity_Ores.setOreBlock(aWorld, tX, i, tZ, tOreMeta, false);
                        }
                    }
                }
                if (!this.mPrimaries.isEmpty()) {
                    for (int i = tMinY + 3; i < tMinY + 6; i++) {
                        if ((aRandom.nextInt(getDense(cZ, eZ, tZ)) == 0) || (aRandom.nextInt(getDense(cX, eX, tX)) == 0)) {
                            if ((tOreMeta = this.mPrimaries.getOre(aRandom)) > 0)
                            	GT_TileEntity_Ores.setOreBlock(aWorld, tX, i, tZ, tOreMeta, false);
                        }
                    }
                }
                if ((!this.mBetweens.isEmpty()) && ((aRandom.nextInt(getDense(cZ, eZ, tZ)) == 0) || (aRandom.nextInt(getDense(cX, eX, tX)) == 0))) {
                    if ((tOreMeta = this.mBetweens.getOre(aRandom)) > 0)
                    	GT_TileEntity_Ores.setOreBlock(aWorld, tX, tMinY + 1 + aRandom.nextInt(3), tZ, tOreMeta, false);
                }
                if ((!this.mSporadics.isEmpty()) && ((aRandom.nextInt(getDense(cZ, eZ, tZ)) == 0) || (aRandom.nextInt(getDense(cX, eX, tX)) == 0))) {
                    if ((tOreMeta = this.mSporadics.getOre(aRandom)) > 0)
                    	GT_TileEntity_Ores.setOreBlock(aWorld, tX, tMinY - 1 + aRandom.nextInt(7), tZ, tOreMeta, false);
                }
            }
        }
        if (GT_Values.D1) System.out.println("Generated Orevein: " + this.mWorldGenName+" "+aChunkX +" "+ aChunkZ);
        return true;
    }
}