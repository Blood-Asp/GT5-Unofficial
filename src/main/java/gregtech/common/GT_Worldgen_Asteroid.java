package gregtech.common;

import java.util.ArrayList;
import java.util.Random;

import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.util.GT_Log;
import gregtech.api.world.GT_Worldgen_Ore;
import gregtech.common.GT_Worldgen_GT_Ore_Layer.WeightedOreList;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public class GT_Worldgen_Asteroid extends GT_Worldgen_Ore {
	public final int mMinSize, mMaxSize;
	
	public GT_Worldgen_Asteroid(String aName, boolean aDefault, Block aBlock, int aBlockMeta, int aMinY, int aMaxY, int aMinSize, int aMaxSize, int aProbability, String[] aDimList, String[] aBiomeList) {
		super(aName, aDefault, aBlock, aBlockMeta, ~0, 1, 0, aProbability, aMinY, aMaxY, aDimList, aBiomeList, true);
		mMinSize = aMinSize;
		mMaxSize = aMaxSize;
	}

	@Override
	public boolean executeWorldgen(World aWorld, Random aRandom, String aBiome, int aDimensionType, int aChunkX, int aChunkZ, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
		int tDimensionType = aWorld.provider.dimensionId;
        String tDimensionName = aWorld.provider.getDimensionName();
        String tAsteroidName = this.mWorldGenName.substring(9);
        aRandom = new Random();
        if (isGenerationAllowed(aWorld, aDimensionType, mDimensionType) && (this.mProbability <= 0 || aRandom.nextInt(mProbability) == 0)) {
            WeightedOreList ores = new WeightedOreList();
            int tOreMeta;
            int tDens = 1; //TODO
            GT_Worldgen_GT_Ore_Layer tOreGen = GT_Worldgen_GT_Ore_Layer.getRandomOreVein(aWorld, tDimensionType, tAsteroidName, aRandom);
            if (tOreGen != null) {
            	ores.addAll(tOreGen.mPrimaries, 3);
            	ores.addAll(tOreGen.mSecondaries, 3);
            	ores.addAll(tOreGen.mBetweens, 2);
            	ores.addAll(tOreGen.mSporadics, 2);
            	tDens = Math.max(1, tOreGen.mDensity);
            }
            if (GT_Values.D1) System.out.println("do asteroid gen: " + aChunkX + " " + aChunkZ);
            int tX = aChunkX + aRandom.nextInt(16);
            int tY = mMinY + aRandom.nextInt(mMaxY - mMinY);
            int tZ = aChunkZ + aRandom.nextInt(16);
            int tSize = mMinSize + aRandom.nextInt(mMaxSize - mMinSize);
            int tDensity = Math.max(tDens, tDens * tSize / 100);
            if ((aWorld.getBlock(tX, tY, tZ).isAir(aWorld, tX, tY, tZ))) {
                float var6 = aRandom.nextFloat() * 3.141593F / 2;
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
                                        double var50 = var39 * var39 + var42 * var42 + var45 * var45;
                                        if ((var50 < 1.0D) && (aWorld.getBlock(tX, tY, tZ).isAir(aWorld, tX, tY, tZ))) {
                                            int ranOre = aRandom.nextInt(50);
                                            if (ranOre < 10) {
                                            	if ((tOreMeta = ores.getOre(aRandom)) > 0)
                                            		GT_TileEntity_Ores.setOreBlock(aWorld, eX, eY, eZ, tOreMeta, false, true);
                                            } else {
                                            	aWorld.setBlock(eX, eY, eZ, mBlock, mBlockMeta, 2);
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
