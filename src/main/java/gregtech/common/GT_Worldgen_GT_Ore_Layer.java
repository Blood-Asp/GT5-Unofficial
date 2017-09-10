package gregtech.common;

import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.blocks.GT_TileEntity_Ores;
import gregtech.loaders.misc.GT_Achievements;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.NoiseGeneratorImproved;

import java.util.ArrayList;
import java.util.Random;

public class GT_Worldgen_GT_Ore_Layer
        extends GT_Worldgen {
    public static ArrayList<GT_Worldgen_GT_Ore_Layer> sList = new ArrayList();
    public static int sWeight = 0;
    public final int mMinY;
    public final int mMaxY;
    public final int mWeight;
    public final int mDensity;
    public final int mSize;
    public final int[][] oreList;
    public final int oreWeight;

    public GT_Worldgen_GT_Ore_Layer(String aName, int aMinY, int aMaxY, int aWeight, int aDensity, int aSize, String[] dimWhiteList, String[] ores) {
        super(aName, dimWhiteList);
        mMinY = aMinY;
        mMaxY = aMaxY;
        mWeight = aWeight;
        mDensity = aDensity;
        mSize = aSize;
        oreList = new int[ores.length][2];
        int w = 0;
        for (int i = 0; i < ores.length; i++) {
            String[] ore = ores[i].split("=");
            if (ore.length == 2) {
                try {
                    oreList[i][0] = Integer.parseInt(ore[0]);
                    oreList[i][1] = Integer.parseInt(ore[1]);
                    w += oreList[i][1];
                    addOreToAchivenments(oreList[i][0]);
                } catch (NumberFormatException ignored) {

                }
            }
        }
        oreWeight = w;
        sWeight += mWeight;
        sList.add(this);
        /*if (GregTech_API.mImmersiveEngineering && GT_Mod.gregtechproxy.mImmersiveEngineeringRecipes) {
            blusunrize.immersiveengineering.api.tool.ExcavatorHandler.addMineral(aName.substring(0, 1).toUpperCase() + aName.substring(1), aWeight, 0.2f, new String[]{"ore" + aPrimary.mName, "ore" + aSecondary.mName, "ore" + aBetween.mName, "ore" + aSporadic.mName}, new float[]{.4f, .4f, .15f, .05f});
        }*/
    }

    private void addOreToAchivenments(int id) {
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

    public boolean executeWorldgen(World aWorld, Random aRandom, String aBiome, int aDimensionType, int aChunkX, int aChunkZ, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        if (!isGenerationAllowed(aWorld)) {
            return false;
        }
        int tMinY = 90;//this.mMinY + aRandom.nextInt(this.mMaxY - this.mMinY - 5);

        int cX = aChunkX - aRandom.nextInt(mSize);
        int eX = aChunkX + 16 + aRandom.nextInt(mSize);
        int cZ = aChunkZ - aRandom.nextInt(mSize);
        int eZ = aChunkZ + 16 + aRandom.nextInt(mSize);
        int sx = eX - cX, sz = eZ - cZ, sy = 7;
        double mx = sx / 2, my = sy / 2, mz = sz / 2;
        double[] perlin = new double[sx * sy * sz];
        NoiseGeneratorImproved noise = new NoiseGeneratorImproved(new Random(aRandom.nextLong()));
        Random rand = new Random(aRandom.nextLong());
        noise.populateNoiseArray(perlin, 0, 0, 0, sx, sy, sz, 0.5, 0.5, 0.5, 0.7);
        for (int x = 0; x < sx; x++) {
            for (int z = 0; z < sz; z++) {
                for (int y = 0; y < sy; y++) {
                    double arg = Math.abs(perlin[z + y * sx + x * sx * sy]);
                    if (arg < mDensity / 50d) {
                        double distance = qrt(x - mx) / qrt(mx) + qrt(y - my) / qrt(my) + qrt(z - mz) / qrt(mz);
                        if (distance < 1) {
                            aWorld.setBlock(cX + x, tMinY + y, cZ + z, Block.getBlockFromName("stone"));
                            int r = rand.nextInt(oreWeight);
                            for (int[] anOreList : oreList) {
                                if (r < anOreList[1]) {
                                    GT_TileEntity_Ores.setOreBlock(aWorld, cX + x, tMinY + y, cZ + z, anOreList[0], false);
                                    break;
                                }
                                r -= anOreList[1];
                            }
                        }
                    }
                }
            }
        }
        if (GT_Values.D1) {
            System.out.println("Generated Orevein: " + this.mWorldGenName + " " + aChunkX + " " + aChunkZ);
        }
        return true;
    }

    public static double qrt(double arg) {
        return arg * arg;
    }
}