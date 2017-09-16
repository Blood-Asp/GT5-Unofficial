package gregtech.common;

import blusunrize.immersiveengineering.api.tool.ExcavatorHandler;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.Materials;
import gregtech.api.objects.XSTR;
import gregtech.api.util.GT_Log;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.blocks.GT_TileEntity_Ores;
import gregtech.loaders.misc.GT_Achievements;
import net.minecraft.world.World;

import java.util.*;

public class GT_Worldgen_GT_Ore_Layer extends GT_Worldgen {
    public static List<GT_Worldgen_GT_Ore_Layer> sList = new ArrayList<>();
    public static Map<Object, WorldgenList> dimensionToOregen = new TreeMap<>();
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
            } catch (Exception e) {
                e.printStackTrace(GT_Log.err);
            }
        }
    }

    public static class WorldgenList {
        public List<GT_Worldgen_GT_Ore_Layer> list = new ArrayList<>();
        public int totalWeight;

        public GT_Worldgen_GT_Ore_Layer getWorldgen(int randomWeight) {
            for (GT_Worldgen_GT_Ore_Layer i : list) {
                randomWeight -= i.mWeight;
                if (randomWeight <= 0) {
                    return i;
                }
            }
            return null;
        }
    }

    public static WorldgenList getWorldgenList(World world) {
        if (dimensionToOregen.containsKey(world.provider.dimensionId)) {
            return dimensionToOregen.get(world.provider.dimensionId);
        }
        WorldgenList w = new WorldgenList();
        for (GT_Worldgen_GT_Ore_Layer i : sList) {
            if (i.isGenerationAllowed(world)) {
                w.list.add(i);
                w.totalWeight += i.mWeight;
            }
        }
        dimensionToOregen.put(world.provider.dimensionId, w);
        return w;
    }

    public static WorldgenList getWorldgenList(String world) {
        if (dimensionToOregen.containsKey(world)) {
            return dimensionToOregen.get(world);
        }
        WorldgenList w = new WorldgenList();
        for (GT_Worldgen_GT_Ore_Layer i : sList) {
            if (i.isGenerationAllowed(world)) {
                w.list.add(i);
                w.totalWeight += i.mWeight;
            }
        }
        dimensionToOregen.put(world, w);
        return w;
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
        for (String oreLine : ores) {
            WeightedOre ore = new WeightedOre(oreLine);
            if (ore.id == -1 || GregTech_API.sGeneratedMaterials[ore.id] == null) continue;
            oreList.add(ore);
            addOreToAchievements(ore.id);
            totalOresWeight += ore.weight;
        }
        oreWeight = totalOresWeight;
        sList.add(this);

        if (GregTech_API.mImmersiveEngineering && GT_Mod.gregtechproxy.mImmersiveEngineeringRecipes) {
            int size = oreList.size();
            float[] chances = new float[size];
            String[] names = new String[size];
            for (int i = 0; i < size; i++) {
                names[i] = "ore" + GregTech_API.sGeneratedMaterials[oreList.get(i).id].mName;
                chances[i] = (float) oreList.get(i).weight / oreWeight;
            }
            String upperCasedName = aName.substring(0, 1).toUpperCase() + aName.substring(1);
            ExcavatorHandler.addMineral(upperCasedName, aWeight, 0.2f, names, chances);
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

    public void executeLayerWorldgen(World world, Random rnd, int chunkX, int chunkZ, int centerX, int centerZ) {
        int minY = mMinY + rnd.nextInt(mMaxY - mMinY) - 3;
        int maxY = minY + 7;
        int minX = centerX - rnd.nextInt(mSize);
        int maxX = centerX + 16 + rnd.nextInt(mSize);
        int minZ = centerZ - rnd.nextInt(mSize);
        int maxZ = minZ + ((int) ((maxX - minX) * (rnd.nextFloat() / 2 - 0.25f))) + (maxX - minX);
        maxX = Math.min(chunkX + 16, maxX);
        minX = Math.max(chunkX, minX);
        maxZ = Math.min(chunkZ + 16, maxZ);
        minZ = Math.max(chunkZ, minZ);
        if (minX < maxX && minZ < maxZ) {
            float nv = mDensity / 15f;
            Random rand = new XSTR(rnd.nextLong() ^ chunkX ^ chunkZ);
            for (int x = minX; x < maxX; x++) {
                for (int z = minZ; z < maxZ; z++) {
                    for (int y = minY; y < maxY; y++) {
                        float noiseValue = rand.nextFloat();
                        if (noiseValue > nv) continue;

                        int randomWeight = rand.nextInt(oreWeight);
                        for (WeightedOre ore : oreList) {
                            randomWeight -= ore.weight;
                            if (randomWeight > 0) continue;
                            GT_TileEntity_Ores.setOreBlock(world, x, y, z, ore.id, false);
                            break;
                        }
                    }
                }
            }
            if (GT_Values.D1) {
                System.out.println("Generated Orevein: " + mWorldGenName + " " + chunkX + " " + chunkZ);
            }
        }
    }
}