package gregtech.common;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.objects.XSTR;
import gregtech.api.util.GT_Log;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class GT_Worldgenerator implements IWorldGenerator {
    private static int mEndAsteroidProbability = 300;
    private static int mGCAsteroidProbability = 50;
    private static int mSize = 100;
    private static int endMinSize = 50;
    private static int endMaxSize = 200;
    private static int gcMinSize = 100;
    private static int gcMaxSize = 400;
    private static boolean endAsteroids = true;
    private static boolean gcAsteroids = true;


    public GT_Worldgenerator() {
        endAsteroids = GregTech_API.sWorldgenFile.get("endasteroids", "GenerateAsteroids", true);
        endMinSize = GregTech_API.sWorldgenFile.get("endasteroids", "AsteroidMinSize", 50);
        endMaxSize = GregTech_API.sWorldgenFile.get("endasteroids", "AsteroidMaxSize", 200);
        mEndAsteroidProbability = GregTech_API.sWorldgenFile.get("endasteroids", "AsteroidProbability", 300);
        gcAsteroids = GregTech_API.sWorldgenFile.get("gcasteroids", "GenerateGCAsteroids", true);
        gcMinSize = GregTech_API.sWorldgenFile.get("gcasteroids", "GCAsteroidMinSize", 100);
        gcMaxSize = GregTech_API.sWorldgenFile.get("gcasteroids", "GCAsteroidMaxSize", 400);
        mGCAsteroidProbability = GregTech_API.sWorldgenFile.get("gcasteroids", "GCAsteroidProbability", 300);
        GameRegistry.registerWorldGenerator(this, 1073741823);
    }

    public void generate(Random aRandom, int aX, int aZ, World aWorld, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        int tempDimensionId = aWorld.provider.dimensionId;
        if (tempDimensionId != -1 && tempDimensionId != 1 && !aChunkGenerator.getClass().getName().contains("galacticraft")) {
            tempDimensionId = 0;
        }
        new WorldGenContainer(aX, aZ, tempDimensionId, aWorld, aChunkGenerator, aChunkProvider).run();
    }

    public static class WorldGenContainer implements Runnable {
        public int mX;
        public int mZ;
        public final int mDimensionType;
        public final World mWorld;
        public final IChunkProvider mChunkGenerator;
        public final IChunkProvider mChunkProvider;

        public WorldGenContainer(int aX, int aZ, int aDimensionType, World aWorld, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
            this.mX = aX;
            this.mZ = aZ;
            this.mDimensionType = aDimensionType;
            this.mWorld = aWorld;
            this.mChunkGenerator = aChunkGenerator;
            this.mChunkProvider = aChunkProvider;
        }

        //returns a coordinate of a center chunk of 3x3 square; the argument belongs to this square
        public int getVeinCenterCoordinate(int c) {
            c += c < 0 ? 1 : 3;
            return c - c % 3 - 2;
        }

        public Random getRandom(int xChunk, int zChunk) {
            long worldSeed = mWorld.getSeed();
            Random fmlRandom = new Random(worldSeed);
            long xSeed = fmlRandom.nextLong() >> 2 + 1L;
            long zSeed = fmlRandom.nextLong() >> 2 + 1L;
            long chunkSeed = (xSeed * xChunk + zSeed * zChunk) ^ worldSeed;
            fmlRandom.setSeed(chunkSeed);
            return new XSTR(fmlRandom.nextInt());
        }

        public void run() {
            int xCenter = getVeinCenterCoordinate(mX);
            int zCenter = getVeinCenterCoordinate(mZ);
            Random random = getRandom(xCenter, zCenter);
            {
                if ((GT_Worldgen_GT_Ore_Layer.sWeight > 0) && (GT_Worldgen_GT_Ore_Layer.sList.size() > 0)) {
                    int tRandomWeight;
                    generated:
                    for (int i = 0; i < 64; i++) {
                        tRandomWeight = random.nextInt(GT_Worldgen_GT_Ore_Layer.sWeight);
                        for (GT_Worldgen tWorldGen : GT_Worldgen_GT_Ore_Layer.sList) {
                            tRandomWeight -= ((GT_Worldgen_GT_Ore_Layer) tWorldGen).mWeight;
                            if (tRandomWeight <= 0) {
                                try {
                                    if (tWorldGen.executeLayerWorldgen(mWorld, random, mX << 4, mZ << 4, xCenter << 4, zCenter << 4)) {
                                        break generated;
                                    }
                                    break;
                                } catch (Throwable e) {
                                    e.printStackTrace(GT_Log.err);
                                }
                            }
                        }
                    }
                }
                try {
                    for (GT_Worldgen tWorldGen : GregTech_API.sWorldgenList) {
                        tWorldGen.executeWorldgen(mWorld, random, "null", mDimensionType, mX << 4, mZ << 4, mChunkGenerator, mChunkProvider);
                    }
                } catch (Throwable e) {
                    e.printStackTrace(GT_Log.err);
                }
            }
            //Asteroid Worldgen
            int tDimensionType = mWorld.provider.dimensionId;
            String tDimensionName = mWorld.provider.getClass().getName();
            Random aRandom = new Random();
            if (((tDimensionType == 1) && endAsteroids && ((mEndAsteroidProbability <= 1) || (aRandom.nextInt(mEndAsteroidProbability) == 0))) || ((tDimensionName.contains("Asteroids")) && gcAsteroids && ((mGCAsteroidProbability <= 1) || (aRandom.nextInt(mGCAsteroidProbability) == 0)))) {
                GT_Worldgen_GT_Ore_Layer ores = null;
                if ((GT_Worldgen_GT_Ore_Layer.sWeight > 0) && (GT_Worldgen_GT_Ore_Layer.sList.size() > 0)) {
                    asteroidsGen:
                    for (int i = 0; i < 256; i++) {
                        int tRandomWeight = aRandom.nextInt(GT_Worldgen_GT_Ore_Layer.sWeight);
                        for (GT_Worldgen_GT_Ore_Layer tWorldGen : GT_Worldgen_GT_Ore_Layer.sList) {
                            tRandomWeight -= tWorldGen.mWeight;
                            if (tRandomWeight <= 0) {
                                try {
                                    if ((tDimensionType == 1 && tWorldGen.isGenerationAllowed("EndAsteroids")) || (tWorldGen.isGenerationAllowed(mWorld))) {
                                        ores = tWorldGen;
                                        break asteroidsGen;
                                    }
                                } catch (Throwable e) {
                                    e.printStackTrace(GT_Log.err);
                                }
                            }
                        }
                    }
                }
                if (GT_Values.D1) System.out.println("do asteroid gen: " + mX + " " + mZ);
                int tX = mX + aRandom.nextInt(16);
                int tY = 50 + aRandom.nextInt(200 - 50);
                int tZ = mZ + aRandom.nextInt(16);
                if (tDimensionType == 1) {
                    mSize = aRandom.nextInt(endMaxSize - endMinSize);
                } else {
                    mSize = aRandom.nextInt(gcMaxSize - gcMinSize);
                }
                if ((mWorld.getBlock(tX, tY, tZ).isAir(mWorld, tX, tY, tZ))) {
                    float var6 = aRandom.nextFloat() * 3.141593F;
                    double var7 = tX + 8 + MathHelper.sin(var6) * mSize / 8.0F;
                    double var9 = tX + 8 - MathHelper.sin(var6) * mSize / 8.0F;
                    double var11 = tZ + 8 + MathHelper.cos(var6) * mSize / 8.0F;
                    double var13 = tZ + 8 - MathHelper.cos(var6) * mSize / 8.0F;
                    double var15 = tY + aRandom.nextInt(3) - 2;
                    double var17 = tY + aRandom.nextInt(3) - 2;
                    for (int var19 = 0; var19 <= mSize; var19++) {
                        double var20 = var7 + (var9 - var7) * var19 / mSize;
                        double var22 = var15 + (var17 - var15) * var19 / mSize;
                        double var24 = var11 + (var13 - var11) * var19 / mSize;
                        double var26 = aRandom.nextDouble() * mSize / 16.0D;
                        double var28 = (MathHelper.sin(var19 * 3.141593F / mSize) + 1.0F) * var26 + 1.0D;
                        double var30 = (MathHelper.sin(var19 * 3.141593F / mSize) + 1.0F) * var26 + 1.0D;
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
                                        oreGen:
                                        for (int eZ = tMinZ; eZ <= tMaxZ; eZ++) {
                                            double var45 = (eZ + 0.5D - var24) / (var28 / 2.0D);
                                            if ((var39 * var39 + var42 * var42 + var45 * var45 < 1.0D) && (mWorld.getBlock(tX, tY, tZ).isAir(mWorld, tX, tY, tZ))) {
                                                int ranOre = aRandom.nextInt(5);
                                                if (ores != null && ranOre == 0) {
                                                    ranOre = aRandom.nextInt(ores.oreWeight);
                                                    for (GT_Worldgen_GT_Ore_Layer.WeightedOre ore : ores.oreList) {
                                                        ranOre -= ore.weight;
                                                        if (ranOre > 0) continue;
                                                        GT_TileEntity_Ores.setOreBlock(mWorld, eX, eY, eZ, ore.id, false, true);
                                                        continue oreGen;
                                                    }
                                                }
                                                if (tDimensionType == 1) {
                                                    mWorld.setBlock(eX, eY, eZ, Blocks.end_stone, 0, 2);
                                                } else {
                                                    mWorld.setBlock(eX, eY, eZ, GregTech_API.sBlockGranites, 8, 3);
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

            Chunk tChunk = this.mWorld.getChunkFromBlockCoords(this.mX, this.mZ);
            if (tChunk != null) {
                tChunk.isModified = true;
            }
        }
    }
}