package gregtech.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTech_API;
import gregtech.api.objects.XSTR;
import gregtech.api.util.GT_Log;
import gregtech.api.world.GT_Worldgen;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

public class GT_Worldgenerator implements IWorldGenerator {

    public GT_Worldgenerator() {
        GameRegistry.registerWorldGenerator(this, 1073741823);
    }

    public synchronized void generate(Random aRandom, int aX, int aZ, World aWorld, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        int tempDimensionId = aWorld.provider.dimensionId;
        if (tempDimensionId != -1 && tempDimensionId != 1 && !aChunkGenerator.getClass().getName().contains("galacticraft")) {
            tempDimensionId = 0;
        }
        new WorldGenContainer(aX * 16, aZ * 16, tempDimensionId, aWorld, aChunkGenerator, aChunkProvider, aWorld.getBiomeGenForCoords(aX * 16 + 8, aZ * 16 + 8).biomeName).run();
    }

    public static class WorldGenContainer implements Runnable {
        public int mX;
        public int mZ;
        public final int mDimensionType;
        public final World mWorld;
        public final IChunkProvider mChunkGenerator;
        public final IChunkProvider mChunkProvider;
        public final String mBiome;
        public static HashSet<ChunkCoordIntPair> mGenerated = new HashSet<>(2000);

        public WorldGenContainer(int aX, int aZ, int aDimensionType, World aWorld, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider, String aBiome) {
            this.mX = aX;
            this.mZ = aZ;
            this.mDimensionType = aDimensionType;
            this.mWorld = aWorld;
            this.mChunkGenerator = aChunkGenerator;
            this.mChunkProvider = aChunkProvider;
            this.mBiome = aBiome;
        }

        //returns a coordinate of a center chunk of 3x3 square; the argument belongs to this square
        public int getVeinCenterCoordinate(int c) {
            c += c < 0 ? 1 : 3;
            return c - c % 3 - 2;
        }

        public boolean surroundingChunksLoaded(int xCenter, int zCenter) {
            return mWorld.checkChunksExist(xCenter - 16, 0, zCenter - 16, xCenter + 16, 0, zCenter + 16);
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
            int xCenter = getVeinCenterCoordinate(mX >> 4);
            int zCenter = getVeinCenterCoordinate(mZ >> 4);
            Random random = getRandom(xCenter, zCenter);
            xCenter <<= 4;
            zCenter <<= 4;
            ChunkCoordIntPair centerChunk = new ChunkCoordIntPair(xCenter, zCenter);
            if (!mGenerated.contains(centerChunk) && surroundingChunksLoaded(xCenter, zCenter)) {
                mGenerated.add(centerChunk);
                int tWeight = GT_Worldgen_GT_Ore_Layer.getOreGenWeight(this.mWorld, this.mDimensionType, false);
                ArrayList<GT_Worldgen_GT_Ore_Layer> tList = GT_Worldgen_GT_Ore_Layer.getOreGenList(this.mWorld, this.mDimensionType, false);
                if ((tWeight > 0) && (tList != null) && (tList.size() > 0)) {
                    boolean temp = true;
                    int tRandomWeight;
                    for (int i = 0; (i < 256) && (temp); i++) {
                        tRandomWeight = random.nextInt(tWeight);
                        for (GT_Worldgen tWorldGen : tList) {
                            tRandomWeight -= ((GT_Worldgen_GT_Ore_Layer) tWorldGen).mWeight;
                            if (tRandomWeight <= 0) {
                                try {
                                    if (tWorldGen.executeWorldgen(this.mWorld, random, this.mBiome, this.mDimensionType, xCenter, zCenter, this.mChunkGenerator, this.mChunkProvider)) {
                                        temp = false;
                                    }
                                    break;
                                } catch (Throwable e) {
                                    e.printStackTrace(GT_Log.err);
                                }
                            }
                        }
                    }
                }
                int i = 0;
                for (int tX = xCenter - 16; i < 3; tX += 16) {
                    int j = 0;
                    for (int tZ = zCenter - 16; j < 3; tZ += 16) {
                        try {
                            for (GT_Worldgen tWorldGen : GregTech_API.sWorldgenList) {
                                tWorldGen.executeWorldgen(this.mWorld, random, this.mBiome, this.mDimensionType, tX, tZ, this.mChunkGenerator, this.mChunkProvider);
                            }
                        } catch (Throwable e) {
                            e.printStackTrace(GT_Log.err);
                        }
                        j++;
                    }
                    i++;
                }
            }
            //Asteroid Worldgen
            //I mean why don't we just put it into the sWorldgenList?
            Chunk tChunk = this.mWorld.getChunkFromBlockCoords(this.mX, this.mZ);
            if (tChunk != null) {
                tChunk.isModified = true;
            }
        }
    }
}