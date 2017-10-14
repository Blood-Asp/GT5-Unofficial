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
import gregtech.common.blocks.GT_Block_Ores;
import gregtech.common.blocks.GT_TileEntity_Ores;
import gregtech.common.worldgen.GT_Worldgen_Layer.WeightedOreList.WeightedOre;
import gregtech.loaders.misc.GT_Achievements;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class GT_Worldgen_Layer
        extends GT_Worldgen {
    public static ArrayList<GT_Worldgen_Layer> sList = new ArrayList();
    protected static ConcurrentHashMap<String, OreGenList> sDimSpecifiedOreGenMap = new ConcurrentHashMap<>();
    public final short mMinY;
    public final short mMaxY;
    public final short mWeight;
    public final short mDensity;
    public final short mSize;
    public final short mThickness;
    
    public final HashSet<String> mAsteroidList = new HashSet<>();
    public final WeightedOreList mPrimaries, mSecondaries, mBetweens, mSporadics; 

    public static class WeightedOreList {
    	
    	protected static class WeightedOre {
        	protected int mWeight, mMeta;
        	protected Block mBlock = null;
        	
        	public WeightedOre() {
        		this(0, 0);
        	}
        	
        	public WeightedOre(int aMeta, int aWeight) {
        		mMeta = aMeta; mWeight = aWeight;
        	}
        	
        	public WeightedOre(Materials aMaterial, int aWeight) {
        		this(aMaterial == null ? 0 : aMaterial.mMetaItemSubID, aWeight);
        	}
        	
        	public WeightedOre(String aConfig) {
        		if (GT_Utility.isStringValid(aConfig)) {
        			String[] k = aConfig.split("::");
        			if (k[0].startsWith("Material:")) {
        				k[0] = k[0].substring(9);
        				try {mMeta = Integer.parseInt(k[0]);}
            			catch (Exception e) {mMeta = Materials.get(k[0]).mMetaItemSubID;}
        				if (mMeta > 0) {
        					try {mWeight = Integer.parseInt(k[1]);}
                			catch (Exception e) {mWeight = 100;}
        					return;
        				}
        			} else if (k[0].startsWith("Block:")) {
        				k[0] = k[0].substring(6);
        				boolean flag = false;
        				try {mMeta = Integer.parseInt(k[0].substring(k[0].lastIndexOf(":") + 1));}
        				catch (Exception e) {mMeta = 0; flag = true;}
        				try {mBlock = Block.getBlockFromName(flag ? k[0] : k[0].substring(0, k[0].lastIndexOf(":")));}
        				catch (Exception e) {}
        				if (mBlock != null) {
        					mMeta = -(++mMeta);
        					try {mWeight = Integer.parseInt(k[1]);}
                			catch (Exception e) {mWeight = 100;}
        					return;
        				}
        				
        			}
        		}
        		mMeta = 0;
        		mWeight = 0;
        	}
        	
        	public WeightedOre copy() {
        		WeightedOre o = new WeightedOre();
        		o.mBlock = mBlock;
        		o.mMeta = mMeta;
        		o.mWeight = mWeight;
        		return o;
        	}
        	
        	@Override
        	public String toString() {
        		if (isValid()) {
        			if (mMeta > 0) {
        				Materials tMaterial = GregTech_API.sMaterials[mMeta];
            			if (tMaterial != null) {
            				return "Material:" + tMaterial.mName + "::" + mWeight;
            			}
        			} else {
        				return "Block:" + Block.blockRegistry.getNameForObject(mBlock) + "::" + mWeight;
        			}
        		}
        		return "NULL:0";
        	}
        	
        	public boolean isValid() {
        		return (mMeta > 0 && mMeta < 1000 && mWeight > 0) || (mBlock != null && mMeta < 0 && mMeta >= -16);
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
    		for (WeightedOre o : aOreList.mOres) {
    			WeightedOre o0 = o.copy();
    			o0.mWeight *= aWeight;
    			add(o0);
    		}
    	}
    	
    	public String[] toConfig() {
    		ArrayList<String> tConfigs = new ArrayList<>();
    		for (WeightedOre o : mOres) if (o.isValid()) tConfigs.add(o.toString());
    		return tConfigs.toArray(new String[tConfigs.size()]);
    	}
    	
    	public boolean isEmpty() {
    		return mOres.isEmpty();
    	}
    	
    	private WeightedOre getOre(Random aRandom) {
    		if (!mOres.isEmpty()) {
    			if (mOres.size() == 1) return mOres.get(0);
    			int tWeight = aRandom.nextInt(mWeight);
    			for (WeightedOre o : mOres) {
    				if ((tWeight -= o.mWeight) <= 0) return o;
    			}
    		}
    		return null;
    	}
    	
    	public void generateOre(World aWorld, int aX, int aY, int aZ, Random aRandom, boolean air) {
    		WeightedOre tOre = getOre(aRandom);
    		if (tOre != null && tOre.isValid()) {
    			try {
    				if (tOre.mMeta > 0) {
    					Block tBlock = aWorld.getBlock(aX, aY, aZ);
    					if (tBlock == Blocks.air && !air) return;
    					int tMeta = aWorld.getBlockMetadata(aX, aY, aZ);
    					String BlockName = Block.blockRegistry.getNameForObject(tBlock) + ":" + tMeta;
    					if (GT_Block_Ores.getBlockReplaceData(BlockName) == 0 &&
    							!tBlock.isReplaceableOreGen(aWorld, aX, aY, aZ, Blocks.netherrack) &&
    							!tBlock.isReplaceableOreGen(aWorld, aX, aY, aZ, Blocks.end_stone) &&
    							!tBlock.isReplaceableOreGen(aWorld, aX, aY, aZ, GregTech_API.sBlockGranites) &&
    							!tBlock.isReplaceableOreGen(aWorld, aX, aY, aZ, GregTech_API.sBlockStones) &&
    							!tBlock.isReplaceableOreGen(aWorld, aX, aY, aZ, Blocks.stone))
    						return;
        				GT_TileEntity_Ores.setOreBlock(aWorld, aX, aY, aZ, tOre.mMeta, false, air);
    				}
        			else {
        				aWorld.setBlock(aX, aY, aZ, tOre.mBlock, -1 - tOre.mMeta, 2);
        			}
    			} catch (Exception e) {}
    		}
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
    			if (GT_Utility.isStringValid(aAsteroid) ? tOreGen.mAsteroidList.contains(aAsteroid) : tOreGen.isGenerationAllowed(aWorld)) {
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

    public GT_Worldgen_Layer(String aName, boolean aDefault, int aMinY, int aMaxY, int aWeight, int aDensity, int aSize, int aThickness, String[] aDimList, String[] aAsteroidList, String[] aBiomeList, WeightedOreList aPrimaries, WeightedOreList aSecondaries, WeightedOreList aBetweens, WeightedOreList aSporadics) {
    	super(aName, sList, aDefault, aDimList, aBiomeList);
    	this.mMinY = (short) aMinY;
    	this.mMaxY = (short) Math.max(aMinY + 5, aMaxY);
    	this.mWeight = (short) aWeight;
    	this.mDensity = (short) aDensity;
    	this.mSize = (short) Math.max(1, aSize);
    	this.mThickness = (short) Math.max(5, aThickness);
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
    	float f = 0.0f;
    	for (WeightedOre o : tOreList.mOres) {
    		if (o.mMeta > 0) {
    			Materials tMaterial = GregTech_API.sGeneratedMaterials[o.mMeta];
        		GT_Achievements.registerOre(tMaterial, this.mMinY, this.mMaxY, this.mWeight, tOverworld, tNether, tEnd);
        		tIEWeightedOres.put("ore" + tMaterial.mName, ((float) o.mWeight) / tOreList.mWeight);
    		} else {
    			f += 0.8f * o.mWeight / tOreList.mWeight; //TODO ?
    		}
    	}
    	if(GregTech_API.mImmersiveEngineering && GT_Mod.gregtechproxy.mImmersiveEngineeringRecipes){
    		String[] tNames = new String[tIEWeightedOres.size()];
    		float[] tWeights = new float[tIEWeightedOres.size()];
    		int i = 0;
    		for (Map.Entry<String, Float> e : tIEWeightedOres.entrySet()) {
    			tNames[i] = e.getKey();
    			tWeights[i++] = e.getValue();
    		}
        	blusunrize.immersiveengineering.api.tool.ExcavatorHandler.addMineral(mWorldGenName.substring(8, 9).toUpperCase() + mWorldGenName.substring(9), mWeight, 0.2f + f, tNames, tWeights);
        }
    }

    private int getDense(int a, int b, int x) {
    	return Math.max(1, Math.max(MathHelper.abs_int(a - x), MathHelper.abs_int(b - x)) / this.mDensity);
    }

    private int getRandomSize(Random aRandom) {
        return Math.max(0, (int) ((aRandom.nextGaussian() / 10 + 0.5) * this.mSize));
    }

    private void generateOre(World aWorld, int aX, int aY, int aZ, int aMinX, int aMaxX, int aMinZ, int aMaxZ, WeightedOreList aOres, Random aRandom) {
    	if (aY < 0) return;
    	int tOreMeta;
    	if (aRandom.nextInt(getDense(aMinX, aMaxX, aX)) == 0 || aRandom.nextInt(getDense(aMinZ, aMaxZ, aZ)) == 0)
    		aOres.generateOre(aWorld, aMaxZ, aY, aZ, aRandom, false);
    }

    public void executeLayerWorldgen(World aWorld, Random aRandom, int chunkX, int chunkZ, int centerX, int centerZ) {
    	int tMinY = this.mMinY + aRandom.nextInt(this.mMaxY - this.mMinY - 5);
        int aMinX = centerX - getRandomSize(aRandom);
        int aMaxX = centerX + 16 + getRandomSize(aRandom);
        int aMinZ = centerZ - getRandomSize(aRandom);
        int aMaxZ = centerZ + 16 + getRandomSize(aRandom);
        int tMaxX = Math.min(chunkX + 16, aMaxX);
        int tMinX = Math.max(chunkX, aMinX);
        int tMaxZ = Math.min(chunkZ + 16, aMaxZ);
        int tMinZ = Math.max(chunkZ, aMinZ);
        
        if (tMinX < tMaxX && tMinZ < tMaxZ) {
            aRandom.setSeed(aRandom.nextLong() ^ chunkX ^ chunkZ);
            int var00 = (this.mThickness + 2) / 3;
            int var01 = (this.mThickness - 2) / 3;
            int var02 = this.mThickness - var01 - var01 + 2;
            int var03 = this.mThickness % 3;
            int var04 = tMinY - var01;
            int var05 = var01 & 1;
            int var06 = tMinY + var00;
            int var07 = var06 - var01 - ((var01 + (var03 == 0 ? 0 : 1)) >> 1);
            int var08 = var01 + var01 + 2;
            int[] arg0 = {0, 0}, arg1 = {1, 0}, arg2 = {0, 1};
            int tOreMeta;
            for (int tX = tMinX; tX <= tMaxX; tX++) {
            	for (int tZ = tMinZ; tZ <= tMaxZ; tZ++) {
            		if (var03 == 0) {
            			if (aRandom.nextBoolean())
            				arg0 = arg1;
            			else
            				arg0 = arg2;
            		}
            		int[] arg3 = {0, 1, 2, 3};
            		for (int i = 3; i >= 0; i--) {
            			int j = aRandom.nextInt(i + 1);
            			switch (arg3[j]) {
            			case 0:
            				if (!this.mSecondaries.isEmpty())
                                for (int tY = 0, rY = var04; tY < var00 + arg0[0]; tY++)
                                	generateOre(aWorld, tX, rY + tY, tZ, aMinX, aMaxX, aMinZ, aMaxZ, this.mSecondaries, aRandom);
            				break;
            			case 1:
            				if (!this.mPrimaries.isEmpty())
                                for (int tY = 0, rY = var06 + arg0[0]; tY < var00 + arg0[1]; tY++)
                                	generateOre(aWorld, tX, rY + tY, tZ, aMinX, aMaxX, aMinZ, aMaxZ, this.mPrimaries, aRandom);
            				break;
            			case 2:
            				if (!this.mBetweens.isEmpty())
                            	for (int tY = 0, rY = var07 + ((var05 + aRandom.nextInt(var08)) >> 1); tY < var01; tY ++)
                            		generateOre(aWorld, tX, rY + tY, tZ, aMinX, aMaxX, aMinZ, aMaxZ, this.mBetweens, aRandom);
            				break;
            			case 3:
            				if (!this.mSporadics.isEmpty())
                            	for (int tY = 0, rY = var04 + aRandom.nextInt(var02); tY < var01; tY++)
                            		generateOre(aWorld, tX, rY + tY + tY, tZ, aMinX, aMaxX, aMinZ, aMaxZ, this.mSporadics, aRandom);
            				break;
            			}
            			arg3[j] = arg3[i];
            		}
                }
            }
            if (GT_Values.D1) System.out.println("Generated Orevein: " + mWorldGenName + " " + chunkX + " " + chunkZ);
        }
    }
}