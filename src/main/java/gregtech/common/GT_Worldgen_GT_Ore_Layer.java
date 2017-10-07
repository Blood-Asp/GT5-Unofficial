package gregtech.common;

import java.util.ArrayList;
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
    public final String aTextWorldgen = "worldgen.";
    
    public final ArrayList<String> mBiomeList = new ArrayList<>();

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
    			catch (Exception e) {mWeight = 0;}
    		} else {mMeta = -1; mWeight = 0;}
    	}
    	
    	public boolean isValid() {
    		return mMeta > 0 && mMeta < 1000 && mWeight > 0;
    	}
    }

    protected static class WeightedOreList {
    	protected ArrayList<WeightedOre> mOres = new ArrayList<>();
    	protected int mWeight = 0;
    	
    	public void add(WeightedOre aOre) {
    		if (aOre.isValid()) {
    			mOres.add(aOre);
    			mWeight += aOre.mWeight;
    		}
    	}
    	
    	public void addAll(String... aConfigs) {
    		for (String s : aConfigs) add(new WeightedOre(s));
    	}
    	
    	public int getOre() {
    		return mOres.isEmpty() ? -1 : mOres.get(0).mMeta;
    	}
    	
    	public int getOre(Random aRandom) {
    		if (!mOres.isEmpty()) {
    			int tWeight = aRandom.nextInt(mWeight);
    			for (WeightedOre o : mOres) {
    				if ((tWeight -= o.mWeight) < 0) return o.mMeta;
    			}
    		}
    		return -1;
    	}
    }

    protected static class OreGenList {
    	public ArrayList<GT_Worldgen_GT_Ore_Layer> list = new ArrayList<>();
    	public int mWeight = 0;
    }

    protected static OreGenList getOreGenData(World aWorld, int aDimensionType, boolean tAsteroid) {
    	String aDimName = aWorld.provider.getDimensionName() + (tAsteroid ? ".asteroid" : "");
    	OreGenList rList;
    	if ((rList = sDimSpecifiedOreGenMap.get(aDimName)) == null) {
    		if (GT_Values.D1) System.out.println("Initializing dimensional-specified Orevein list for: " + aDimName);
    		rList = new OreGenList();
    		for (GT_Worldgen_GT_Ore_Layer tOreGen : sList)
    			if ((tAsteroid && ((tOreGen.mEndAsteroid && aDimensionType == 1) || (tOreGen.mAsteroid && aDimensionType == -30))) ||
    					tOreGen.isGenerationAllowed(aWorld, aDimensionType, ((aDimensionType == -1) && (tOreGen.mNether)) || ((aDimensionType == 0) && (tOreGen.mOverworld)) || ((aDimensionType == 1) && (tOreGen.mEnd)) || ((aWorld.provider.getDimensionName().equals("Moon")) && (tOreGen.mMoon)) || ((aWorld.provider.getDimensionName().equals("Mars")) && (tOreGen.mMars)) ? aDimensionType : ~aDimensionType)) {
    				rList.list.add(tOreGen);
    				rList.mWeight += tOreGen.mWeight;
    			}
    		
    		sDimSpecifiedOreGenMap.put(aDimName, rList);
    	}
    	return rList;
    }

    public static int getOreGenWeight(World aWorld, int aDimensionType, boolean tAsteroid) {
    	return getOreGenData(aWorld, aDimensionType, tAsteroid).mWeight;
    }

    public static ArrayList<GT_Worldgen_GT_Ore_Layer> getOreGenList(World aWorld, int aDimensionType, boolean tAsteroid) {
    	return getOreGenData(aWorld, aDimensionType, tAsteroid).list;
    }

    @Deprecated
    public GT_Worldgen_GT_Ore_Layer(String aName, boolean aDefault, int aMinY, int aMaxY, int aWeight, int aDensity, int aSize, boolean aOverworld, boolean aNether, boolean aEnd, boolean aMoon, boolean aMars, boolean aAsteroid, Materials aPrimary, Materials aSecondary, Materials aBetween, Materials aSporadic) {
        super(aName, sList, aDefault, null);// TODO
        this.mOverworld = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Overworld", aOverworld);
        this.mNether = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Nether", aNether);
        this.mEnd = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "TheEnd", aEnd);
        this.mEndAsteroid = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "EndAsteroid", aEnd);
        this.mMoon = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Moon", aMoon);
        this.mMars = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Mars", aMars);
        this.mAsteroid = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Asteroid", aAsteroid);
        this.mMinY = ((short) GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "MinHeight", aMinY));
        this.mMaxY = ((short) Math.max(this.mMinY + 5, GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "MaxHeight", aMaxY)));
        this.mWeight = ((short) GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "RandomWeight", aWeight));
        this.mDensity = ((short) GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Density", aDensity));
        this.mSize = ((short) Math.max(1, GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Size", aSize)));
        this.mPrimaryMeta = ((short) GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "OrePrimaryLayer", aPrimary.mMetaItemSubID));
        this.mSecondaryMeta = ((short) GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "OreSecondaryLayer", aSecondary.mMetaItemSubID));
        this.mBetweenMeta = ((short) GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "OreSporadiclyInbetween", aBetween.mMetaItemSubID));
        this.mSporadicMeta = ((short) GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "OreSporaticlyAround", aSporadic.mMetaItemSubID));
        this.mRestrictBiome = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "RestrictToBiomeName", "None");

        //if (mPrimaryMeta != -1 && GregTech_API.sGeneratedMaterials[(mPrimaryMeta % 1000)] == null) throw new IllegalArgumentException("A Material for the supplied ID " + mPrimaryMeta + " for " + mWorldGenName + " does not exist");
        //if (mSecondaryMeta != -1 && GregTech_API.sGeneratedMaterials[(mSecondaryMeta % 1000)] == null) throw new IllegalArgumentException("A Material for the supplied ID " + mSecondaryMeta + " for " + mWorldGenName + " does not exist");
        //if (mBetweenMeta != -1 && GregTech_API.sGeneratedMaterials[(mBetweenMeta % 1000)] == null) throw new IllegalArgumentException("A Material for the supplied ID " + mBetweenMeta + " for " + mWorldGenName + " does not exist");
        //if (mPrimaryMeta != -1 && GregTech_API.sGeneratedMaterials[(mSporadicMeta % 1000)] == null) throw new IllegalArgumentException("A Material for the supplied ID " + mSporadicMeta + " for " + mWorldGenName + " does not exist");

        if (this.mEnabled) {
            GT_Achievements.registerOre(GregTech_API.sGeneratedMaterials[(mPrimaryMeta % 1000)], aMinY, aMaxY, aWeight, aOverworld, aNether, aEnd);
            GT_Achievements.registerOre(GregTech_API.sGeneratedMaterials[(mSecondaryMeta % 1000)], aMinY, aMaxY, aWeight, aOverworld, aNether, aEnd);
            GT_Achievements.registerOre(GregTech_API.sGeneratedMaterials[(mBetweenMeta % 1000)], aMinY, aMaxY, aWeight, aOverworld, aNether, aEnd);
            GT_Achievements.registerOre(GregTech_API.sGeneratedMaterials[(mSporadicMeta % 1000)], aMinY, aMaxY, aWeight, aOverworld, aNether, aEnd);
            sWeight += this.mWeight;
            if(GregTech_API.mImmersiveEngineering && GT_Mod.gregtechproxy.mImmersiveEngineeringRecipes){
            	blusunrize.immersiveengineering.api.tool.ExcavatorHandler.addMineral(aName.substring(8, 9).toUpperCase()+aName.substring(9), aWeight, 0.2f, new String[]{"ore"+aPrimary.mName,"ore"+aSecondary.mName,"ore"+aBetween.mName,"ore"+aSporadic.mName}, new float[]{.4f,.4f,.15f,.05f});
            }
            
        }
    }

    public boolean executeWorldgen(World aWorld, Random aRandom, String aBiome, int aDimensionType, int aChunkX, int aChunkZ, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        if (!this.mRestrictBiome.equals("None") && !(this.mRestrictBiome.equals(aBiome))) {
            return false; //Not the correct biome for ore mix
        }
        /*
        if (!isGenerationAllowed(aWorld, aDimensionType, ((aDimensionType == -1) && (this.mNether)) || ((aDimensionType == 0) && (this.mOverworld)) || ((aDimensionType == 1) && (this.mEnd)) || ((aWorld.provider.getDimensionName().equals("Moon")) && (this.mMoon)) || ((aWorld.provider.getDimensionName().equals("Mars")) && (this.mMars)) ? aDimensionType : aDimensionType ^ 0xFFFFFFFF)) {
            return false;
        }
        */
        int tMinY = this.mMinY + aRandom.nextInt(this.mMaxY - this.mMinY - 5);

        int cX = aChunkX - aRandom.nextInt(this.mSize);
        int eX = aChunkX + 16 + aRandom.nextInt(this.mSize);
        for (int tX = cX; tX <= eX; tX++) {
            int cZ = aChunkZ - aRandom.nextInt(this.mSize);
            int eZ = aChunkZ + 16 + aRandom.nextInt(this.mSize);
            for (int tZ = cZ; tZ <= eZ; tZ++) {
                if (this.mSecondaryMeta > 0) {
                    for (int i = tMinY - 1; i < tMinY + 2; i++) {
                        if ((aRandom.nextInt(Math.max(1, Math.max(MathHelper.abs_int(cZ - tZ), MathHelper.abs_int(eZ - tZ)) / this.mDensity)) == 0) || (aRandom.nextInt(Math.max(1, Math.max(MathHelper.abs_int(cX - tX), MathHelper.abs_int(eX - tX)) / this.mDensity)) == 0)) {
                            GT_TileEntity_Ores.setOreBlock(aWorld, tX, i, tZ, this.mSecondaryMeta, false);
                        }
                    }
                }
                if ((this.mBetweenMeta > 0) && ((aRandom.nextInt(Math.max(1, Math.max(MathHelper.abs_int(cZ - tZ), MathHelper.abs_int(eZ - tZ)) / this.mDensity)) == 0) || (aRandom.nextInt(Math.max(1, Math.max(MathHelper.abs_int(cX - tX), MathHelper.abs_int(eX - tX)) / this.mDensity)) == 0))) {
                    GT_TileEntity_Ores.setOreBlock(aWorld, tX, tMinY + 2 + aRandom.nextInt(2), tZ, this.mBetweenMeta, false);
                }
                if (this.mPrimaryMeta > 0) {
                    for (int i = tMinY + 3; i < tMinY + 6; i++) {
                        if ((aRandom.nextInt(Math.max(1, Math.max(MathHelper.abs_int(cZ - tZ), MathHelper.abs_int(eZ - tZ)) / this.mDensity)) == 0) || (aRandom.nextInt(Math.max(1, Math.max(MathHelper.abs_int(cX - tX), MathHelper.abs_int(eX - tX)) / this.mDensity)) == 0)) {
                            GT_TileEntity_Ores.setOreBlock(aWorld, tX, i, tZ, this.mPrimaryMeta, false);
                        }
                    }
                }
                if ((this.mSporadicMeta > 0) && ((aRandom.nextInt(Math.max(1, Math.max(MathHelper.abs_int(cZ - tZ), MathHelper.abs_int(eZ - tZ)) / this.mDensity)) == 0) || (aRandom.nextInt(Math.max(1, Math.max(MathHelper.abs_int(cX - tX), MathHelper.abs_int(eX - tX)) / this.mDensity)) == 0))) {
                    GT_TileEntity_Ores.setOreBlock(aWorld, tX, tMinY - 1 + aRandom.nextInt(7), tZ, this.mSporadicMeta, false);
                }
            }
        }
        if (GT_Values.D1) {
            System.out.println("Generated Orevein: " + this.mWorldGenName+" "+aChunkX +" "+ aChunkZ);
        }
        return true;
    }
}