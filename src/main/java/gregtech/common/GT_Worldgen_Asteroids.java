package gregtech.common;

import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class GT_Worldgen_Asteroids extends GT_Worldgen {
    public int minSize;
    public int maxSize;
    public int probability;
    public Block block;
    public int meta;

    public GT_Worldgen_Asteroids(String aName, String[] whiteList, String bl, int m, int prob, int minS, int maxS) {
        super(aName, whiteList);
        GregTech_API.sWorldgenList.add(this);
        minSize = minS;
        maxSize = maxS;
        probability = prob;
        block = Block.getBlockFromName(bl);
        meta = m;
    }

    public void executeWorldgen(World aWorld, Random aRandom, int aChunkX, int aChunkZ) {
        if (!(isGenerationAllowed(aWorld) && (probability <= 1 || aRandom.nextInt(probability) == 0))) {
            return;
        }

        GT_Worldgen_Layer ores;
        GT_Worldgen_Layer.WorldgenList list;
        if (aWorld.provider.dimensionId == 1) {
            list = GT_Worldgen_Layer.getWorldgenList("EndAsteroids");
        } else {
            list = GT_Worldgen_Layer.getWorldgenList(aWorld);
        }
        ores = list.getWorldgen(aRandom.nextInt(list.totalWeight));
        if (GT_Values.D1) System.out.println("do asteroid gen: " + aChunkX + " " + aChunkZ);
        int tX = aChunkX + aRandom.nextInt(16);
        int tY = 50 + aRandom.nextInt(200 - 50);
        int tZ = aChunkZ + aRandom.nextInt(16);
        int mSize = aRandom.nextInt(maxSize - minSize);
        if (aWorld.getBlock(tX, tY, tZ).isAir(aWorld, tX, tY, tZ)) {
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
                                    if (var39 * var39 + var42 * var42 + var45 * var45 < 1.0D && aWorld.getBlock(tX, tY, tZ).isAir(aWorld, tX, tY, tZ)) {
                                        aWorld.setBlock(eX, eY, eZ, block, meta, 2);
                                        int ranOre = aRandom.nextInt(5);
                                        if (ores != null && ranOre == 0) {
                                            ranOre = aRandom.nextInt(ores.oreWeight);
                                            for (GT_Worldgen_Layer.WeightedOre ore : ores.oreList) {
                                                ranOre -= ore.weight;
                                                if (ranOre > 0) continue;
                                                GT_TileEntity_Ores.setOreBlock(aWorld, eX, eY, eZ, ore.id, false);
                                                continue oreGen;
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
    }
}
