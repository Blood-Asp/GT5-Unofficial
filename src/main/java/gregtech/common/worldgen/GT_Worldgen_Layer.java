package gregtech.common.worldgen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
import gregtech.common.worldgen.GT_Worldgen_Layer.WeightedOreList.WeightedOre;
import gregtech.loaders.misc.GT_Achievements;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public class GT_Worldgen_Layer
        extends GT_Worldgen {
    public static ArrayList<GT_Worldgen_Layer> sList = new ArrayList();
    protected static ConcurrentHashMap<String, OreGenList> sDimSpecifiedOreGenMap = new ConcurrentHashMap<>();
    public final short mMinY;
    public final short mMaxY;
    public final short mWeight;
    public final short mDensity;
    public final short mSize;
    
    public final HashSet<String> mAsteroidList = new HashSet<>();
    public final WeightedOreList mPrimaries, mSecondaries, mBetweens, mSporadics; 

    public static class WeightedOreList {
    	
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
    	protected ArrayList<GT_Worldgen_Layer> list = new ArrayList<>();
    	protected int mWeight = 0;
    }

    public static OreGenList getOreGenData(World aWorld, String aAsteroid) {
    	String aDimName = aWorld.provider.getDimensionName() + (GT_Utility.isStringValid(aAsteroid) ? "." + aAsteroid : "");
    	OreGenList rList;
    	if ((rList = sDimSpecifiedOreGenMap.get(aDimName)) == null) {
    		if (GT_Values.D1) System.out.println("Initializing dimensional-specified Orevein list for: " + aDimName);
    		rList = new OreGenList();
    		for (GT_Worldgen_Layer tOreGen : sList)
    			if ((GT_Utility.isStringValid(aAsteroid) && tOreGen.mAsteroidList.contains(aAsteroid)) || tOreGen.isGenerationAllowed(aWorld)) {
    				rList.list.add(tOreGen);
    				rList.mWeight += tOreGen.mWeight;
    			}
    		
    		sDimSpecifiedOreGenMap.put(aDimName, rList);
    	}
    	return rList;
    }

    public static GT_Worldgen_Layer getRandomOreVein(World aWorld, String aAsteroid, String aBiome, Random aRandom) {
    	OreGenList tList = getOreGenData(aWorld, aAsteroid);
    	if (tList.mWeight > 0 && tList.list.size() > 0) {
    		for (int i = 0; i < 256; i++) {
    			int tWeight = aRandom.nextInt(tList.mWeight);
        		for (GT_Worldgen_Layer tOreGen : tList.list)
        			if ((tWeight -= tOreGen.mWeight) <= 0) {
        				if (tOreGen.isBiomeAllowed(aBiome))
        					return tOreGen;
        				else
        					break;
        			}
    		}
    	}
    	return null;
    }

    public GT_Worldgen_Layer(String aName, boolean aDefault, int aMinY, int aMaxY, int aWeight, int aDensity, int aSize, String[] aDimList, String[] aAsteroidList, String[] aBiomeList, WeightedOreList aPrimaries, WeightedOreList aSecondaries, WeightedOreList aBetweens, WeightedOreList aSporadics) {
    	super(aName, sList, aDefault, aDimList, aBiomeList);
    	this.mMinY = (short) aMinY;
    	this.mMaxY = (short) Math.max(aMinY + 5, aMaxY);
    	this.mWeight = (short) aWeight;
    	this.mDensity = (short) aDensity;
    	this.mSize = (short) Math.max(1, aSize);
    	this.mPrimaries = aPrimaries;
    	this.mSecondaries = aSecondaries;
    	this.mBetweens = aBetweens;
    	this.mSporadics = aSporadics;
    	this.mAsteroidList.addAll(Arrays.asList(aAsteroidList));
    	
    	Map<String, Float> tIEWeightedOres = new HashMap<>();
    	WeightedOreList tOreList = new WeightedOreList();
    	tOreList.addAll(this.mPrimaries, 8);
    	tOreList.addAll(this.mSecondaries, 8);
    	tOreList.addAll(this.mBetweens, 3);
    	tOreList.addAll(this.mSporadics, 1);
    	boolean tOverworld = this.isGenerationAllowed("Overworld", "Surface") || this.isGenerationAllowed(0), tNether = this.isGenerationAllowed("Nether", "Hell") || this.isGenerationAllowed(-1), tEnd = this.isGenerationAllowed("The End", "End") || this.isGenerationAllowed(1);
    	for (WeightedOre o : tOreList.mOres) {
    		Materials tMaterial = GregTech_API.sGeneratedMaterials[o.mMeta];
    		GT_Achievements.registerOre(tMaterial, this.mMinY, this.mMaxY, this.mWeight, tOverworld, tNether, tEnd);
    		tIEWeightedOres.put("ore" + tMaterial.mName, ((float) o.mWeight) / tOreList.mWeight);
    	}
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

    private int getDense(int a, int b, int x) {
    	return Math.max(1, Math.max(MathHelper.abs_int(a - x), MathHelper.abs_int(b - x)) / this.mDensity);
    }

    public double nextGausian(Random aRandom) {
        return aRandom.nextGaussian() / 10 + 0.5;
    }

    public void executeLayerWorldgen(World aWorld, Random aRandom, int chunkX, int chunkZ, int centerX, int centerZ) {
    	int tMinY = this.mMinY + aRandom.nextInt(this.mMaxY - this.mMinY - 5);
        int aMinX = centerX - (int) (nextGausian(aRandom) * mSize);
        int aMaxX = centerX + 16 + (int) (nextGausian(aRandom) * mSize);
        int aMinZ = centerZ - (int) (nextGausian(aRandom) * mSize);
        int aMaxZ = centerZ + 16 + (int) (nextGausian(aRandom) * mSize);
        int tMaxX = Math.min(chunkX + 16, aMaxX);
        int tMinX = Math.max(chunkX, aMinX);
        int tMaxZ = Math.min(chunkZ + 16, aMaxZ);
        int tMinZ = Math.max(chunkZ, aMinZ);
        
        if (tMinX < tMaxX && tMinZ < tMaxZ) {
            aRandom.setSeed(aRandom.nextLong() ^ chunkX ^ chunkZ);
            int tOreMeta;
            for (int tX = tMinX; tX <= tMaxX; tX++) {
            	for (int tZ = tMinZ; tZ <= tMaxZ; tZ++) {
                    if (!this.mSecondaries.isEmpty()) {
                        for (int i = tMinY - 1; i < tMinY + 2; i++) {
                            if ((aRandom.nextInt(getDense(aMinZ, aMaxZ, tZ)) == 0) || (aRandom.nextInt(getDense(aMinX, aMaxX, tX)) == 0)) {
                                if ((tOreMeta = this.mSecondaries.getOre(aRandom)) > 0)
                                	GT_TileEntity_Ores.setOreBlock(aWorld, tX, i, tZ, tOreMeta, false);
                            }
                        }
                    }
                    if (!this.mPrimaries.isEmpty()) {
                        for (int i = tMinY + 3; i < tMinY + 6; i++) {
                            if ((aRandom.nextInt(getDense(aMinZ, aMaxZ, tZ)) == 0) || (aRandom.nextInt(getDense(aMinX, aMaxX, tX)) == 0)) {
                                if ((tOreMeta = this.mPrimaries.getOre(aRandom)) > 0)
                                	GT_TileEntity_Ores.setOreBlock(aWorld, tX, i, tZ, tOreMeta, false);
                            }
                        }
                    }
                    if ((!this.mBetweens.isEmpty()) && ((aRandom.nextInt(getDense(aMinZ, aMaxZ, tZ)) == 0) || (aRandom.nextInt(getDense(aMinX, aMaxX, tX)) == 0))) {
                        if ((tOreMeta = this.mBetweens.getOre(aRandom)) > 0)
                        	GT_TileEntity_Ores.setOreBlock(aWorld, tX, tMinY + 1 + aRandom.nextInt(3), tZ, tOreMeta, false);
                    }
                    if ((!this.mSporadics.isEmpty()) && ((aRandom.nextInt(getDense(aMinZ, aMaxZ, tZ)) == 0) || (aRandom.nextInt(getDense(aMinX, aMaxX, tX)) == 0))) {
                        if ((tOreMeta = this.mSporadics.getOre(aRandom)) > 0)
                        	GT_TileEntity_Ores.setOreBlock(aWorld, tX, tMinY - 1 + aRandom.nextInt(7), tZ, tOreMeta, false);
                    }
                }
            }
            if (GT_Values.D1) System.out.println("Generated Orevein: " + mWorldGenName + " " + chunkX + " " + chunkZ);
        }
    }
}