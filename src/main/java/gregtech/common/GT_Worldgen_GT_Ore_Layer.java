package gregtech.common;

import blusunrize.immersiveengineering.api.tool.ExcavatorHandler;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.blocks.GT_TileEntity_Ores;
import gregtech.loaders.misc.GT_Achievements;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.NoiseGeneratorImproved;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GT_Worldgen_GT_Ore_Layer extends GT_Worldgen {
    public static ArrayList<GT_Worldgen_GT_Ore_Layer> sList = new ArrayList<>();
    public static int sWeight = 0;
    public final int mMinY;
    public final int mMaxY;
    public final int mWeight;
    public final int mDensity;
    public final int mSize;
    public final List<WeightedOre> oreList;
    public final int oreWeight;

    public static class WeightedOre {
        public int id = -1;
        public int weight = 0;

        public WeightedOre(int id, int weight) {
            this.id = id;
            this.weight = weight;
        }

        public WeightedOre(String config) {
            try {
                String[] rawData = config.split("=");
                if (rawData.length == 2) {
                    id = Integer.parseInt(rawData[0]);
                    weight = Integer.parseInt(rawData[1]);
                }
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public GT_Worldgen_GT_Ore_Layer(String aName, int aMinY, int aMaxY, int aWeight, int aDensity, int aSize, String[] dimWhiteList, String[] ores) {
        super(aName, dimWhiteList);
        mMinY = aMinY;
        mMaxY = aMaxY;
        mWeight = aWeight;
        mDensity = aDensity;
        mSize = aSize;
        oreList = new ArrayList<>();
        int totalOresWeight = 0;
        float[] chances = new float[ores.length];
        String[] names = new String[ores.length];
        int i = 0;
        for (String oreLine : ores) {
            WeightedOre ore = new WeightedOre(oreLine);
            oreList.add(ore);
            totalOresWeight += ore.weight;
            addOreToAchievements(ore.id);
            names[i] = "ore" + GregTech_API.sGeneratedMaterials[ore.id].mName;
            chances[i++] = ore.weight;
        }
        oreWeight = totalOresWeight;
        for (int j = 0; j < chances.length; j++) {
            chances[j] /= totalOresWeight;
        }
        sWeight += mWeight;
        sList.add(this);
        if (GregTech_API.mImmersiveEngineering && GT_Mod.gregtechproxy.mImmersiveEngineeringRecipes) {
            ExcavatorHandler.addMineral(aName.substring(0, 1).toUpperCase() + aName.substring(1), aWeight, 0.2f, names, chances);
        }
    }

    private void addOreToAchievements(int id) {
        boolean over = false, hell = false, end = false;
        for (String s : dimensionNameWhiteList) {
            switch (s) {
                case "Surface":
                    over = true;
                    break;
                case "Hell":
                    hell = true;
                    break;
                case "End":
                    end = true;
                    break;
            }
        }
        for (int s : dimensionIDWhiteList) {
            switch (s) {
                case 0:
                    over = true;
                    break;
                case -1:
                    hell = true;
                    break;
                case 1:
                    end = true;
                    break;
            }
        }
        GT_Achievements.registerOre(GregTech_API.sGeneratedMaterials[id % 1000], mMinY, mMaxY, mWeight, over, hell, end);
    }

    public boolean executeWorldgen(World world, Random rnd, String aBiome, int aDimensionType, int aChunkX, int aChunkZ, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        if (!isGenerationAllowed(world)) {
            return false;
        }
        int minY = 90;//this.mMinY + rnd.nextInt(this.mMaxY - this.mMinY - 5);
        int maxY = minY + 4 + rnd.nextInt(mSize);
        int minX = aChunkX - rnd.nextInt(mSize);
        int maxX = aChunkX + 16 + rnd.nextInt(mSize);
        int minZ = aChunkZ - rnd.nextInt(mSize);
        int maxZ = aChunkZ + 16 + rnd.nextInt(mSize);
        int sizeX = maxX - minX, sizeY = maxY - minY, sizeZ = maxZ - minZ;
        int midX = sizeX / 2, midY = sizeY / 2, midZ = sizeZ / 2;
        double[] noiseValues = new double[sizeX * sizeY * sizeZ];
        NoiseGeneratorImproved noise = new NoiseGeneratorImproved(new Random(rnd.nextLong()));
        Random rand = new Random(rnd.nextLong());
        noise.populateNoiseArray(noiseValues, 0, 0, 0, sizeX, sizeY, sizeZ, 0.5, 0.5, 0.5, 0.7);
//        for (int x = 0; x < sizeX; x++) {
//            for (int z = 0; z < sizeZ; z++) {
//                for (int y = 0; y < sizeY; y++) {
//                    double noiseValue = Math.abs(noiseValues[z + y * sizeX + x * sizeX * sizeY]);
//                    if (noiseValue > mDensity / 50d) continue;
//                    double distance = sqr(x - midX) / sqr(midX) + sqr(y - midY) / sqr(midY) + sqr(z - midZ) / sqr(midZ);
//                    if (distance > 1) continue;
//
//                    int randomWeight = rand.nextInt(oreWeight);
//                    for (WeightedOre ore : oreList) {
//                        randomWeight -= ore.weight;
//                        if (randomWeight > 0) continue;
//                        GT_TileEntity_Ores.setOreBlock(world, minX + x, minY + y, minZ + z, ore.id, false, true);
//                        break;
//                    }
//                }
//            }
//        }
        int offX = sizeZ * sizeY;
        double nv = mDensity / 50d;
        int[] c = {sqr(midY) * sqr(midZ), sqr(midX) * sqr(midZ), sqr(midX) * sqr(midY), sqr(midX) * sqr(midY) * sqr(midZ)};
        for (int i = 0; i < noiseValues.length; i++) {
            if (Math.abs(noiseValues[i]) > nv) continue;
            int x = i / offX, y = (i / sizeZ) % sizeY, z = i % sizeZ;
            double d = sqr(x - midX) * c[0] + sqr(y - midY) * c[1] + sqr(z - midZ) * c[2];
            if (d > c[3]) continue;
            int randomWeight = rand.nextInt(oreWeight);
            for (WeightedOre ore : oreList) {
                randomWeight -= ore.weight;
                if (randomWeight > 0) continue;
                GT_TileEntity_Ores.setOreBlock(world, minX + x, minY + y, minZ + z, ore.id, false, true);
                break;
            }
        }
        if (GT_Values.D1) {
            System.out.println("Generated Orevein: " + this.mWorldGenName + " " + aChunkX + " " + aChunkZ);
        }
        return true;
    }

    public static int sqr(int n) {
        return n * n;
    }
}