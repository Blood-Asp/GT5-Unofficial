package gregtech.common;

import java.util.HashMap;
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
        processChunk(aWorld, aX, aZ);
    }

    //returns a coordinate of a center chunk of 3x3 square; the argument belongs to this square
    public static int getVeinCenterCoordinate(int c) {
        c += c < 0 ? 1 : 3;
        return c - c % 3 - 2;
    }

    protected static boolean areSurroundingChunksExist(World aWorld, ChunkCoordIntPair aCoord) {
    	int count = 0;
    	for (int i = -1; i < 2; i++)
			for (int j = -1; j < 2; j++)
				if (aWorld.getChunkProvider().chunkExists(aCoord.chunkXPos + i, aCoord.chunkZPos + j)) count++;
    	return count >= 9;
    }

    private static final HashMap<Integer, HashSet<ChunkCoordIntPair>> sGeneratedCenters = new HashMap<>(), sUngeneratedChunks = new HashMap<>();

    public static void processChunk(World aWorld, int aX, int aZ) {
    	ChunkCoordIntPair cCoord = new ChunkCoordIntPair(getVeinCenterCoordinate(aX), getVeinCenterCoordinate(aZ));
    	if (!isCenterGenerated(aWorld, cCoord)) {
    		if (areSurroundingChunksExist(aWorld, cCoord) && !aWorld.isRemote) {
    			putTo(aWorld, sGeneratedCenters, cCoord);
    			(new WorldGenContainer(cCoord, aWorld)).run();
    		} else {
    			putTo(aWorld, sUngeneratedChunks, new ChunkCoordIntPair(aX, aZ));
    		}
    	}
    }

    protected static void putTo(World aWorld, HashMap<Integer, HashSet<ChunkCoordIntPair>> aMap, ChunkCoordIntPair aCoord) {
    	synchronized (aMap) {
    		HashSet<ChunkCoordIntPair> set;
        	if ((set = aMap.get(aWorld.provider.dimensionId)) == null) aMap.put(aWorld.provider.dimensionId, set = new HashSet<ChunkCoordIntPair>(100));
        	set.add(aCoord);
    	}
    }

    protected static void removeFrom(World aWorld, HashMap<Integer, HashSet<ChunkCoordIntPair>> aMap, ChunkCoordIntPair aCoord) {
    	synchronized (aMap) {
    		HashSet<ChunkCoordIntPair> set;
        	if ((set = aMap.get(aWorld.provider.dimensionId)) != null) set.remove(aCoord);;
    	}
    }

    protected static boolean isCenterGenerated(World aWorld, ChunkCoordIntPair cCoord) {
    	synchronized (sGeneratedCenters) {
    		HashSet<ChunkCoordIntPair> set;
        	if ((set = sGeneratedCenters.get(aWorld.provider.dimensionId)) == null) sGeneratedCenters.put(aWorld.provider.dimensionId, set = new HashSet<ChunkCoordIntPair>(100));
    		return set.contains(cCoord);
    	}
    }

    protected static boolean isChunkUngenerated(World aWorld, ChunkCoordIntPair aCoord) {
    	synchronized (sUngeneratedChunks) {
    		HashSet<ChunkCoordIntPair> set;
        	if ((set = sUngeneratedChunks.get(aWorld.provider.dimensionId)) == null) sUngeneratedChunks.put(aWorld.provider.dimensionId, set = new HashSet<ChunkCoordIntPair>(100));
    		return set.contains(aCoord);
    	}
    }

    public static class WorldGenContainer implements Runnable {
        public int mX;
        public int mZ;
        public final int mDimensionType;
        public final World mWorld;
        public final IChunkProvider mChunkGenerator;
        public final IChunkProvider mChunkProvider;
        public final String mBiome;

        public WorldGenContainer(ChunkCoordIntPair aCoord, World aWorld) {
        	this.mChunkGenerator = aWorld.provider.createChunkGenerator();
        	this.mChunkProvider = aWorld.getChunkProvider();
        	this.mX = aCoord.chunkXPos << 4;
            this.mZ = aCoord.chunkZPos << 4;
            this.mWorld = aWorld;
            this.mBiome = aWorld.getBiomeGenForCoords(aCoord.chunkXPos << 4 + 8, aCoord.chunkZPos << 4 + 8).biomeName;
            int tempDimensionId = aWorld.provider.dimensionId;
            if (tempDimensionId != -1 && tempDimensionId != 1 && !mChunkGenerator.getClass().getName().contains("galacticraft")) {
                tempDimensionId = 0;
            }
            this.mDimensionType = tempDimensionId;
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
            Random random = getRandom(mX, mZ);
            for (int i = -1; i < 2; i++)
				for (int j = -1; j < 2; j++)
					removeFrom(mWorld, sUngeneratedChunks, new ChunkCoordIntPair(mX >> 4 + i, mZ >> 4 + j));
            GT_Worldgen_GT_Ore_Layer tOreGen;
            for (int i = 0; i < 256; i++) {
            	if ((tOreGen = GT_Worldgen_GT_Ore_Layer.getRandomOreVein(this.mWorld, this.mDimensionType, null, random)) == null) break;
        		try {
        			if (tOreGen.executeWorldgen(this.mWorld, random, this.mBiome, this.mDimensionType, mX, mZ, this.mChunkGenerator, this.mChunkProvider))
        				break;
        		} catch (Throwable t) {
        			t.printStackTrace(GT_Log.err);
        		}
        	}
            for (int i = -16; i <= 16; i += 16)
            	for (int j = -16; j <= 16; j += 16) {
            		try {
                        for (GT_Worldgen tWorldGen : GregTech_API.sWorldgenList) {
                            tWorldGen.executeWorldgen(this.mWorld, random, this.mBiome, this.mDimensionType, mX + i, mZ + j, this.mChunkGenerator, this.mChunkProvider);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace(GT_Log.err);
                    }
            		Chunk tChunk = this.mWorld.getChunkFromBlockCoords(mX + i, mZ + j);
                    if (tChunk != null) {
                        tChunk.isModified = true;
                    }
            	}
        }
    }
}