package gregtech.common;

import blusunrize.immersiveengineering.api.tool.ExcavatorHandler;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.blocks.GT_TileEntity_Ores;
import gregtech.loaders.misc.GT_Achievements;
import net.minecraft.world.World;

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
        public final int id;
        public final int weight;

        public WeightedOre(String config) {
            int id = -1, weight = 0;
            try {
                String[] rawData = config.split("=");
                if (rawData.length == 2) {
                    id = Integer.parseInt(rawData[0]);
                    weight = Integer.parseInt(rawData[1]);
                }
            } catch (NumberFormatException ignored) {
            }
            this.id = id;
            this.weight = weight;
        }
    }

    public GT_Worldgen_GT_Ore_Layer(String aName, int aMinY, int aMaxY, int aWeight, int aDensity, int aSize, String[] dimWhiteList, String[] ores) {
        super(aName, dimWhiteList);
        mMinY = aMinY;
        mMaxY = aMaxY;
        mWeight = aWeight;
        mDensity = aDensity;
        mSize = Math.min(16, aSize);
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

    public boolean executeLayerWorldgen(World world, Random rnd, int chunkX, int chunkZ, int centerX, int centerZ) {
        if (!isGenerationAllowed(world)) {
            return false;
        }
        int minY = 90;//this.mMinY + rnd.nextInt(this.mMaxY - this.mMinY - 5);
        int maxY = minY + 14;
        int minX = Math.max(centerX - rnd.nextInt(mSize), chunkX);
        int maxX = Math.min(centerX + 16 + rnd.nextInt(mSize), chunkX + 16);
        int minZ = Math.max(centerZ - rnd.nextInt(mSize), chunkZ);
        int maxZ = Math.min(centerZ + 16 + rnd.nextInt(mSize), chunkZ + 16);
        float nv = mDensity / 20f;
        Random rand = new Random(rnd.nextLong() ^ ((((long) chunkX) << 32) | chunkZ));
        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                for (int y = minY; y < maxY; y++) {
                    float noiseValue = rand.nextFloat();
                    if (noiseValue > nv) continue;

                    int randomWeight = rand.nextInt(oreWeight);
                    for (WeightedOre ore : oreList) {
                        randomWeight -= ore.weight;
                        if (randomWeight > 0) continue;
                        GT_TileEntity_Ores.setOreBlock(world, x, y, z, ore.id, false, true);
                        break;
                    }
                }
            }
        }
        if (GT_Values.D1) {
            System.out.println("Generated Orevein: " + mWorldGenName + " " + chunkX + " " + chunkZ);
        }
        return true;
    }
}