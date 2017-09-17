package gregtech.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.util.GT_Log;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public class GT_Worldgen_Asteroid extends GT_Worldgen {
	public final int mEndAsteroidProbability;
    public final int mGCAsteroidProbability;
    public final int endMinSize;
    public final int endMaxSize;
    public final int gcMinSize;
    public final int gcMaxSize;
    public final boolean endAsteroids;
    public final boolean gcAsteroids;

	public GT_Worldgen_Asteroid(boolean aDefault) {
		super("Asteroid", GregTech_API.sWorldgenList, aDefault);
		endAsteroids = GregTech_API.sWorldgenFile.get("endasteroids", "GenerateAsteroids", true);
        endMinSize = GregTech_API.sWorldgenFile.get("endasteroids", "AsteroidMinSize", 50);
        endMaxSize = GregTech_API.sWorldgenFile.get("endasteroids", "AsteroidMaxSize", 200);
        mEndAsteroidProbability = GregTech_API.sWorldgenFile.get("endasteroids", "AsteroidProbability", 300);
        gcAsteroids = GregTech_API.sWorldgenFile.get("gcasteroids", "GenerateGCAsteroids", true);
        gcMinSize = GregTech_API.sWorldgenFile.get("gcasteroids", "GCAsteroidMinSize", 100);
        gcMaxSize = GregTech_API.sWorldgenFile.get("gcasteroids", "GCAsteroidMaxSize", 400);
        mGCAsteroidProbability = GregTech_API.sWorldgenFile.get("gcasteroids", "GCAsteroidProbability", 300);
	}

	@Override
	public boolean executeWorldgen(World aWorld, Random aRandom, String aBiome, int aDimensionType, int aChunkX, int aChunkZ, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
		int tDimensionType = aWorld.provider.dimensionId;
        String tDimensionName = aWorld.provider.getDimensionName();
        aRandom = new Random();
        if (((tDimensionType == 1) && endAsteroids && ((mEndAsteroidProbability <= 1) || (aRandom.nextInt(mEndAsteroidProbability) == 0))) || ((tDimensionName.equals("Asteroids")) && gcAsteroids && ((mGCAsteroidProbability <= 1) || (aRandom.nextInt(mGCAsteroidProbability) == 0)))) {
        	int tMaxSize = 100, tMinSize = 0;
        	if (tDimensionType == 1) {
        		tMaxSize = endMaxSize;
        		tMinSize = endMinSize;
            } else if (tDimensionName.equals("Asteroids")) {
            	tMaxSize = gcMaxSize;
        		tMinSize = gcMinSize;
            }
        	
            short primaryMeta = 0;
            short secondaryMeta = 0;
            short betweenMeta = 0;
            short sporadicMeta = 0;
            int tWeight = GT_Worldgen_GT_Ore_Layer.getOreGenWeight(aWorld, tDimensionType, true);
            ArrayList<GT_Worldgen_GT_Ore_Layer> tList = GT_Worldgen_GT_Ore_Layer.getOreGenList(aWorld, tDimensionType, true);
            if ((tWeight > 0) && (tList != null) && (tList.size() > 0)) {
                boolean temp = true;
                int tRandomWeight;
                for (int i = 0; (i < 256) && (temp); i++) {
                    tRandomWeight = aRandom.nextInt(tWeight);
                    for (GT_Worldgen_GT_Ore_Layer tWorldGen : tList) {
                        tRandomWeight -= tWorldGen.mWeight;
                        if (tRandomWeight <= 0) {
                            try {
                                //if ((tWorldGen.mEndAsteroid && tDimensionType == 1) || (tWorldGen.mAsteroid && tDimensionType == -30)) {
                                    primaryMeta = tWorldGen.mPrimaryMeta;
                                    secondaryMeta = tWorldGen.mSecondaryMeta;
                                    betweenMeta = tWorldGen.mBetweenMeta;
                                    sporadicMeta = tWorldGen.mSporadicMeta;
                                    temp = false;
                                    break;
                                //}
                            } catch (Throwable e) {
                                e.printStackTrace(GT_Log.err);
                            }
                        }
                    }
                }
            }
            if (GT_Values.D1) System.out.println("do asteroid gen: " + aChunkX + " " + aChunkZ);
            int tX = aChunkX + aRandom.nextInt(16);
            int tY = 50 + aRandom.nextInt(200 - 50);
            int tZ = aChunkZ + aRandom.nextInt(16);
            int tSize = tMinSize + aRandom.nextInt(tMaxSize - tMinSize);
            if ((aWorld.getBlock(tX, tY, tZ).isAir(aWorld, tX, tY, tZ))) {
                float var6 = aRandom.nextFloat() * 3.141593F;
                double var7 = tX + 8 + MathHelper.sin(var6) * tSize / 8.0F;
                double var9 = tX + 8 - MathHelper.sin(var6) * tSize / 8.0F;
                double var11 = tZ + 8 + MathHelper.cos(var6) * tSize / 8.0F;
                double var13 = tZ + 8 - MathHelper.cos(var6) * tSize / 8.0F;
                double var15 = tY + aRandom.nextInt(3) - 2;
                double var17 = tY + aRandom.nextInt(3) - 2;
                for (int var19 = 0; var19 <= tSize; var19++) {
                    double var20 = var7 + (var9 - var7) * var19 / tSize;
                    double var22 = var15 + (var17 - var15) * var19 / tSize;
                    double var24 = var11 + (var13 - var11) * var19 / tSize;
                    double var26 = aRandom.nextDouble() * tSize / 16.0D;
                    double var28 = (MathHelper.sin(var19 * 3.141593F / tSize) + 1.0F) * var26 + 1.0D;
                    double var30 = (MathHelper.sin(var19 * 3.141593F / tSize) + 1.0F) * var26 + 1.0D;
                    int tMinX = MathHelper.floor_double(var20 - var28 / 2.0D);
                    int tMinY = MathHelper.floor_double(var22 - var30 / 2.0D);
                    int tMinZ = MathHelper.floor_double(var24 - var28 / 2.0D);
                    int tMaxX = MathHelper.floor_double(var20 + var28 / 2.0D);
                    int tMaxY = MathHelper.floor_double(var22 + var30 / 2.0D);
                    int tMaxZ = MathHelper.floor_double(var24 + var28 / 2.0D);
                    for (int eX = tMinX; eX <= tMaxX; eX++) {
                        double var39 = (eX + 0.5D - var20) / (var28 / 2.0D);
                        if (var39 * var39 < 1.0D) {
                            for (int eY = tMinY; eY <= tMaxY; eY++) {
                                double var42 = (eY + 0.5D - var22) / (var30 / 2.0D);
                                if (var39 * var39 + var42 * var42 < 1.0D) {
                                    for (int eZ = tMinZ; eZ <= tMaxZ; eZ++) {
                                        double var45 = (eZ + 0.5D - var24) / (var28 / 2.0D);
                                        if ((var39 * var39 + var42 * var42 + var45 * var45 < 1.0D) && (aWorld.getBlock(tX, tY, tZ).isAir(aWorld, tX, tY, tZ))) {
                                            int ranOre = aRandom.nextInt(50);
                                            if (ranOre < 3) {
                                                GT_TileEntity_Ores.setOreBlock(aWorld, eX, eY, eZ, primaryMeta, false, true);
                                            } else if (ranOre < 6) {
                                                GT_TileEntity_Ores.setOreBlock(aWorld, eX, eY, eZ, secondaryMeta, false, true);
                                            } else if (ranOre < 8) {
                                                GT_TileEntity_Ores.setOreBlock(aWorld, eX, eY, eZ, betweenMeta, false, true);
                                            } else if (ranOre < 10) {
                                                GT_TileEntity_Ores.setOreBlock(aWorld, eX, eY, eZ, sporadicMeta, false, true);
                                            } else {
                                                if (tDimensionType == 1) {
                                                    aWorld.setBlock(eX, eY, eZ, Blocks.end_stone, 0, 2);
                                                } else if (tDimensionName.equals("Asteroids")) {
                                                    aWorld.setBlock(eX, eY, eZ, GregTech_API.sBlockGranites, 8, 3);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
        	return false;
        }
		return true;
	}
}
