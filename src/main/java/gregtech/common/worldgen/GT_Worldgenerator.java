package gregtech.common.worldgen;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTech_API;
import gregtech.api.objects.XSTR;
import gregtech.api.util.GT_Log;
import gregtech.api.world.GT_Worldgen;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class GT_Worldgenerator implements IWorldGenerator {
    public GT_Worldgenerator() {
        GameRegistry.registerWorldGenerator(this, 1073741823);
    }

    public int getVeinCenterCoordinate(int c) {
        c += c < 0 ? 1 : 3;
        return c - c % 3 - 2;
    }

    public long getRandomSeed(World world, int xChunk, int zChunk) {
        long worldSeed = world.getSeed();
        Random fmlRandom = new Random(worldSeed);
        long xSeed = fmlRandom.nextLong() >> 2 + 1L;
        long zSeed = fmlRandom.nextLong() >> 2 + 1L;
        long chunkSeed = xSeed * xChunk + zSeed * zChunk ^ worldSeed;
        fmlRandom.setSeed(chunkSeed);
        return fmlRandom.nextInt();
    }

    public void generateOreLayerAt(World aWorld, Random aRandom, int aX, int aZ, int xCenter, int zCenter) {
        aRandom.setSeed(getRandomSeed(aWorld, xCenter, zCenter));
        String aBiome = aWorld.getBiomeGenForCoords(xCenter << 4 + 8, zCenter << 4 + 8).biomeName;
        GT_Worldgen_Layer tOreGen = GT_Worldgen_Layer.getRandomOreVein(aWorld, null, aBiome, aRandom);
        try {
            if (tOreGen != null)
            	tOreGen.executeLayerWorldgen(aWorld, aRandom, aX << 4, aZ << 4, xCenter << 4, zCenter << 4);
        } catch (Throwable e) {
            e.printStackTrace(GT_Log.err);
        }
    }

    public void generate(Random aRandom, int aX, int aZ, World aWorld, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        Random r = new XSTR();
        int xCenter = getVeinCenterCoordinate(aX), zCenter = getVeinCenterCoordinate(aZ);
        boolean bXN = aX < xCenter, bXP = aX > xCenter, bZN = aZ < zCenter, bZP = aZ > zCenter;
        generateOreLayerAt(aWorld, r, aX, aZ, xCenter, zCenter);
        if (bZN) generateOreLayerAt(aWorld, r, aX, aZ, xCenter, zCenter - 3);
        if (bZP) generateOreLayerAt(aWorld, r, aX, aZ, xCenter, zCenter + 3);
        if (bXN) generateOreLayerAt(aWorld, r, aX, aZ, xCenter - 3, zCenter);
        if (bXP) generateOreLayerAt(aWorld, r, aX, aZ, xCenter + 3, zCenter);
        if (bXN && bZN) generateOreLayerAt(aWorld, r, aX, aZ, xCenter - 3, zCenter - 3);
        if (bXN && bZP) generateOreLayerAt(aWorld, r, aX, aZ, xCenter - 3, zCenter + 3);
        if (bXP && bZN) generateOreLayerAt(aWorld, r, aX, aZ, xCenter + 3, zCenter - 3);
        if (bXP && bZP) generateOreLayerAt(aWorld, r, aX, aZ, xCenter + 3, zCenter + 3);
        long seed = getRandomSeed(aWorld, aX, aZ);
        String aBiome = aWorld.getBiomeGenForCoords(aX << 4 + 8, aZ << 4 + 8).biomeName;
        try {
            for (GT_Worldgen tWorldGen : GregTech_API.sWorldgenList) {
                r.setSeed(seed);
                tWorldGen.executeWorldgen(aWorld, r, aX << 4, aZ << 4, aBiome);
            }
        } catch (Throwable e) {
            e.printStackTrace(GT_Log.err);
        }
        Chunk tChunk = aWorld.getChunkFromChunkCoords(aX, aZ);
        if (tChunk != null) {
            tChunk.isModified = true;
        }
    }
}