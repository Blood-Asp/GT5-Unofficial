package gregtech.common;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTech_API;
import gregtech.api.objects.XSTR;
import gregtech.api.util.GT_Log;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.GT_Worldgen_Layer.WorldgenList;
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

    public void generateOreLayerAt(World world, Random random, int mX, int mZ, int xCenter, int zCenter) {
        random.setSeed(getRandomSeed(world, xCenter, zCenter));
        WorldgenList list = GT_Worldgen_Layer.getWorldgenList(world);
        try {
            list.getWorldgen(random.nextInt(list.totalWeight)).executeLayerWorldgen(world, random, mX << 4, mZ << 4, xCenter << 4, zCenter << 4);
        } catch (Throwable e) {
            e.printStackTrace(GT_Log.err);
        }
    }

    public void generate(Random aRandom, int mX, int mZ, World aWorld, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        Random r = new XSTR();
        int xCenter = getVeinCenterCoordinate(mX), zCenter = getVeinCenterCoordinate(mZ);
        generateOreLayerAt(aWorld, r, mX, mZ, xCenter, zCenter);
        if (mZ < zCenter) generateOreLayerAt(aWorld, r, mX, mZ, xCenter, zCenter - 3);
        if (mZ > zCenter) generateOreLayerAt(aWorld, r, mX, mZ, xCenter, zCenter + 3);
        if (mX < xCenter) generateOreLayerAt(aWorld, r, mX, mZ, xCenter - 3, zCenter);
        if (mX > xCenter) generateOreLayerAt(aWorld, r, mX, mZ, xCenter + 3, zCenter);
        if (mX < xCenter && mZ < zCenter) generateOreLayerAt(aWorld, r, mX, mZ, xCenter - 3, zCenter - 3);
        if (mX < xCenter && mZ > zCenter) generateOreLayerAt(aWorld, r, mX, mZ, xCenter - 3, zCenter + 3);
        if (mX > xCenter && mZ < zCenter) generateOreLayerAt(aWorld, r, mX, mZ, xCenter + 3, zCenter - 3);
        if (mX > xCenter && mZ > zCenter) generateOreLayerAt(aWorld, r, mX, mZ, xCenter + 3, zCenter + 3);
        long seed = getRandomSeed(aWorld, mX, mZ);
        try {
            for (GT_Worldgen tWorldGen : GregTech_API.sWorldgenList) {
                r.setSeed(seed);
                tWorldGen.executeWorldgen(aWorld, r, mX << 4, mZ << 4);
            }
        } catch (Throwable e) {
            e.printStackTrace(GT_Log.err);
        }
        Chunk tChunk = aWorld.getChunkFromChunkCoords(mX, mZ);
        if (tChunk != null) {
            tChunk.isModified = true;
        }
    }
}