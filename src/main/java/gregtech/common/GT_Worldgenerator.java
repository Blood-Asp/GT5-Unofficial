package gregtech.common;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.objects.XSTR;
import gregtech.api.util.GT_Config;
import gregtech.api.util.GT_Log;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.GT_Worldgen_GT_Ore_Layer.WorldgenList;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class GT_Worldgenerator implements IWorldGenerator {
    public static class AsteroidConfig {
        public boolean enabled;
        public int minSize;
        public int maxSize;
        public int probability;
    }

    public static AsteroidConfig endAsteroids = new AsteroidConfig();
    public static AsteroidConfig gcAsteroids = new AsteroidConfig();

    static {
        endAsteroids.enabled = true;
        endAsteroids.minSize = 50;
        endAsteroids.maxSize = 200;
        endAsteroids.probability = 300;

        gcAsteroids.enabled = true;
        gcAsteroids.minSize = 100;
        gcAsteroids.maxSize = 400;
        gcAsteroids.probability = 50;
    }

    public GT_Worldgenerator() {
        configure(endAsteroids, "endasteroids", "GenerateAsteroids", "AsteroidMinSize", "AsteroidMaxSize", "AsteroidProbability");
        configure(gcAsteroids, "gcasteroids", "GenerateGCAsteroids", "GCAsteroidMinSize", "GCAsteroidMaxSize", "GCAsteroidProbability");
        GameRegistry.registerWorldGenerator(this, 1073741823);
    }

    private void configure(AsteroidConfig obj, String category, String oldEnabledCfg, String oldMinSizeCfg, String oldMaxSizeCfg, String oldProbabilityCfg) {
        obj.enabled = GT_Config.getWorldgenConfig(category, oldEnabledCfg, "Enabled", obj.enabled);
        obj.minSize = GT_Config.getWorldgenConfig(category, oldMinSizeCfg, "MinSize", obj.minSize);
        obj.maxSize = GT_Config.getWorldgenConfig(category, oldMaxSizeCfg, "MaxSize", obj.maxSize);
        obj.probability = GT_Config.getWorldgenConfig(category, oldProbabilityCfg, "Probability", obj.probability);
    }

    public void generate(Random aRandom, int aX, int aZ, World aWorld, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        new WorldGenContainer(aX, aZ, aWorld).run();
    }

    public static class WorldGenContainer implements Runnable {
        public int mX;
        public int mZ;
        public final World mWorld;
        int xCenter;
        int zCenter;

        public WorldGenContainer(int aX, int aZ, World aWorld) {
            mX = aX;
            mZ = aZ;
            mWorld = aWorld;
            xCenter = getVeinCenterCoordinate(mX);
            zCenter = getVeinCenterCoordinate(mZ);
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
            long chunkSeed = xSeed * xChunk + zSeed * zChunk ^ worldSeed;
            fmlRandom.setSeed(chunkSeed);
            return new XSTR(fmlRandom.nextInt());
        }

        public void generateOreLayerAt(int xCenter, int zCenter) {
            Random random = getRandom(xCenter, zCenter);
            WorldgenList list = GT_Worldgen_GT_Ore_Layer.getWorldgenList(mWorld);
            try {
                list.getWorldgen(random.nextInt(list.totalWeight)).executeLayerWorldgen(mWorld, random, mX << 4, mZ << 4, xCenter << 4, zCenter << 4);
            } catch (Throwable e) {
                e.printStackTrace(GT_Log.err);
            }
        }

        public void run() {
            generateOreLayerAt(xCenter, zCenter);
            generateOreLayerAt(xCenter, zCenter - 3);
            generateOreLayerAt(xCenter, zCenter + 3);
            generateOreLayerAt(xCenter - 3, zCenter);
            generateOreLayerAt(xCenter + 3, zCenter);
            generateOreLayerAt(xCenter - 3, zCenter - 3);
            generateOreLayerAt(xCenter - 3, zCenter + 3);
            generateOreLayerAt(xCenter + 3, zCenter - 3);
            generateOreLayerAt(xCenter + 3, zCenter + 3);
            try {
                for (GT_Worldgen tWorldGen : GregTech_API.sWorldgenList) {
                    tWorldGen.executeWorldgen(mWorld, getRandom(mX, mZ), mX << 4, mZ << 4);
                }
            } catch (Throwable e) {
                e.printStackTrace(GT_Log.err);
            }
            //Asteroid Worldgen
            Random aRandom = getRandom(mX, mZ);
            if ((mWorld.provider.dimensionId == 1 && endAsteroids.enabled && (endAsteroids.probability <= 1 || aRandom.nextInt(endAsteroids.probability) == 0))
                    || (mWorld.provider.getClass().getName().contains("Asteroids") && gcAsteroids.enabled && (gcAsteroids.probability <= 1 || aRandom.nextInt(gcAsteroids.probability) == 0))) {
                GT_Worldgen_GT_Ore_Layer ores = null;
                WorldgenList list;
                if (mWorld.provider.dimensionId == 1) {
                    list = GT_Worldgen_GT_Ore_Layer.getWorldgenList("EndAsteroids");
                } else {
                    list = GT_Worldgen_GT_Ore_Layer.getWorldgenList(mWorld);
                }
                try {
                    ores = list.getWorldgen(aRandom.nextInt(list.totalWeight));
                } catch (Throwable e) {
                    e.printStackTrace(GT_Log.err);
                }
                if (GT_Values.D1) System.out.println("do asteroid gen: " + mX + " " + mZ);
                int tX = (mX << 4) + aRandom.nextInt(16);
                int tY = 50 + aRandom.nextInt(200 - 50);
                int tZ = (mZ << 4) + aRandom.nextInt(16);
                AsteroidConfig ac = mWorld.provider.dimensionId == 1 ? endAsteroids : gcAsteroids;
                int mSize = aRandom.nextInt(ac.maxSize - ac.minSize);
                if (mWorld.getBlock(tX, tY, tZ).isAir(mWorld, tX, tY, tZ)) {
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
                                            if (var39 * var39 + var42 * var42 + var45 * var45 < 1.0D && mWorld.getBlock(tX, tY, tZ).isAir(mWorld, tX, tY, tZ)) {
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
                                                if (mWorld.provider.dimensionId == 1) {
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

            Chunk tChunk = mWorld.getChunkFromChunkCoords(mX, mZ);
            if (tChunk != null) {
                tChunk.isModified = true;
            }
        }
    }
}