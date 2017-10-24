package gregtech.common.worldgen;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import gregtech.api.enums.GT_Values;
import gregtech.api.objects.XSTR;
import gregtech.common.worldgen.GT_Worldgen_Layer.WeightedOreList;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class GT_Worldgen_Asteroids extends GT_Worldgen_Stone {
	public final int mMinSize;

	public static class Pos3 {
		int x, y, z;
		public Pos3(int aX, int aY, int aZ) {
			this.x = aX; this.y = aY; this.z = aZ;
		}
		public boolean equals(Object obj) {
			if (obj instanceof Pos3) {
				Pos3 pos = (Pos3) obj;
				return pos.x == this.x && pos.y == this.y && pos.z == this.z;
			}
			return false;
		}
		public int hashCode() {
			return this.x ^ (this.y * 31) ^ (this.z * 8191);
		}
		public String toString() {
			return String.format("(%d, %d, %d)", this.x, this.y, this.z);
		}
	}
	
	public GT_Worldgen_Asteroids(String aName, boolean aDefault, Block aBlock, int aBlockMeta, int aMinY, int aMaxY, int aMinSize, int aMaxSize, int aProbability, String[] aDimList, String[] aBiomeList) {
		super(aName, aDefault, aBlock, aBlockMeta, 1, aMaxSize, aProbability, aMinY, aMaxY, aDimList, aBiomeList, true);
		this.mMinSize = aMinSize;
	}

	@Override
	public void executeWorldgen(World aWorld, Random aRandom, int aChunkX, int aChunkZ, String aBiome) {
		String tDimensionName = aWorld.provider.getDimensionName();
        String tAsteroidName = this.mWorldGenName.substring(9);
        Random tRandom = new XSTR();
        if (isGenerationAllowed(aWorld) && isBiomeAllowed(aBiome) && (this.mProbability <= 0 || tRandom.nextInt(mProbability) == 0)) {
            WeightedOreList ores = new WeightedOreList();
            int tOreMeta = -1;
            int tDensity = 1;
            GT_Worldgen_Layer tOreGen = null;
            for (int i = 0; i < 256; i++) {
            	if ((tOreGen = GT_Worldgen_Layer.getRandomOreVein(aWorld, tAsteroidName, aBiome, tRandom)) != null) {
                	ores.addAll(tOreGen.mPrimaries, 3);
                	ores.addAll(tOreGen.mSecondaries, 3);
                	ores.addAll(tOreGen.mBetweens, 2);
                	ores.addAll(tOreGen.mSporadics, 2);
                	tDensity = Math.max(1, tOreGen.mDensity);
                	if (!ores.isEmpty()) break;
                }
            }
            int tX = aChunkX + tRandom.nextInt(16);
            int tY = mMinY + tRandom.nextInt(mMaxY - mMinY);
            int tZ = aChunkZ + tRandom.nextInt(16);
            int tSize = mMinSize + tRandom.nextInt(mSize - mMinSize);
            if ((aWorld.getBlock(tX, tY, tZ).isAir(aWorld, tX, tY, tZ))) {
            	Map<Pos3, Double> tBlockPos = new HashMap<>();
                float var6 = tRandom.nextFloat() * 1.57079633F;
                double var7 = tX + 8 + MathHelper.sin(var6) * tSize / 8.0F;
                double var9 = tX + 8 - MathHelper.sin(var6) * tSize / 8.0F;
                double var11 = tZ + 8 + MathHelper.cos(var6) * tSize / 8.0F;
                double var13 = tZ + 8 - MathHelper.cos(var6) * tSize / 8.0F;
                double var15 = tY + tRandom.nextInt(3) - 2;
                double var17 = tY + tRandom.nextInt(3) - 2;
                int var18 = (tSize + 3) / 4;
                for (int var19 = 0; var19 <= var18; var19++) {
                    double var20 = var7 + (var9 - var7) * var19 / var18;
                    double var22 = var15 + (var17 - var15) * var19 / var18;
                    double var24 = var11 + (var13 - var11) * var19 / var18;
                    double var26 = (tRandom.nextDouble() * .75D + .25D) * tSize / 16.0D;
                    double var27 = (tRandom.nextDouble() + .5D) * var26;
                    double var28 = (tRandom.nextDouble() + .5D) * var26;
                    double var29 = (tRandom.nextDouble() + .5D) * var26;
                    double var30 =  MathHelper.sin(var19 * 3.141593F / var18) + 1.0F;
                    double var31 = var30 * var27 + 1.0D;
                    double var32 = var30 * var28 + 1.0D;
                    double var33 = var30 * var29 + 1.0D;
                    int tMinX = MathHelper.floor_double(var20 - var31 / 2.0D);
                    int tMinY = MathHelper.floor_double(var22 - var32 / 2.0D);
                    int tMinZ = MathHelper.floor_double(var24 - var33 / 2.0D);
                    int tMaxX = MathHelper.floor_double(var20 + var31 / 2.0D);
                    int tMaxY = MathHelper.floor_double(var22 + var32 / 2.0D);
                    int tMaxZ = MathHelper.floor_double(var24 + var33 / 2.0D);
                    for (int eX = tMinX; eX <= tMaxX; eX++) {
                        double var39 = (eX + 0.5D - var20) / (var31 / 2.0D);
                        if (var39 * var39 < 1.0D) {
                            for (int eY = tMinY; eY <= tMaxY; eY++) {
                                double var42 = (eY + 0.5D - var22) / (var32 / 2.0D);
                                if (var39 * var39 + var42 * var42 < 1.0D) {
                                    for (int eZ = tMinZ; eZ <= tMaxZ; eZ++) {
                                        double var45 = (eZ + 0.5D - var24) / (var33 / 2.0D);
                                        double var50 = var39 * var39 + var42 * var42 + var45 * var45;
                                        if (var50 < 1.0D) {
                                        	Pos3 pos = new Pos3(eX, eY, eZ);
                                        	if (!tBlockPos.containsKey(pos) || tBlockPos.get(pos) > var50)
                                        		tBlockPos.put(pos, var50);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                for (Map.Entry<Pos3, Double> e : tBlockPos.entrySet()) {
                	aWorld.setBlock(e.getKey().x, e.getKey().y, e.getKey().z, this.mBlock, this.mBlockMeta, 2);
                	if (tRandom.nextInt(Math.max(2, (int) (80.0D * e.getValue() / tDensity))) == 0)
                		ores.generateOre(aWorld, e.getKey().x, e.getKey().y, e.getKey().z, aRandom, true);
                }
            }
            if (GT_Values.D1) System.out.println("Generated Asteroid: " + (tOreGen == null ? "Empty" : tOreGen.mWorldGenName) +" "+aChunkX +" "+ aChunkZ);
        }
	}
}