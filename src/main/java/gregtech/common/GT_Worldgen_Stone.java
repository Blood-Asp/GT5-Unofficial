package gregtech.common;

import gregtech.api.GregTech_API;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.blocks.GT_Block_Ores_Abstract;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class GT_Worldgen_Stone
        extends GT_Worldgen {
    public final int blockMeta, amount, size, minY, maxY, probability;
    public final Block block;
    public final boolean allowToGenerateInVoid;

    public GT_Worldgen_Stone(String aName, String[] dimWhiteList, String aBlock, int aBlockMeta, int aAmount, int aSize, int aProbability, int aMinY, int aMaxY, boolean aAllowToGenerateinVoid) {
        super(aName, dimWhiteList);
        minY = aMinY;
        maxY = aMaxY;
        probability = aProbability;
        amount = aAmount;
        size = aSize;
        blockMeta = aBlockMeta;
        block = Block.getBlockFromName(aBlock);
        allowToGenerateInVoid = aAllowToGenerateinVoid;
    }

    public boolean executeWorldgen(World aWorld, Random aRandom, String aBiome, int aDimensionType, int aChunkX, int aChunkZ, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        if ((isGenerationAllowed(aWorld)) && ((probability <= 1) || (aRandom.nextInt(probability) == 0))) {
            for (int i = 0; i < amount; i++) {
                int tX = aChunkX + aRandom.nextInt(16);
                int tY = minY + aRandom.nextInt(maxY - minY);
                int tZ = aChunkZ + aRandom.nextInt(16);
                if ((this.allowToGenerateInVoid) || (!aWorld.getBlock(tX, tY, tZ).isAir(aWorld, tX, tY, tZ))) {
                    float math_pi = 3.141593F;//FB: CNT - CNT_ROUGH_CONSTANT_VALUE
                    float var6 = aRandom.nextFloat() * math_pi;
                    float var1d = size / 8.0F;
                    int var2d = tX + 8;
                    int var3d = tZ + 8;
                    int var4d = tY - 2;
                    float mh_s_0 = MathHelper.sin(var6) * var1d;
                    float mh_c_0 = MathHelper.cos(var6) * var1d;
                    float var7 = var2d + mh_s_0;
                    float var11 = var3d + mh_c_0;
                    int var15r = aRandom.nextInt(3);
                    int var17r = aRandom.nextInt(3);
                    int var15 = var4d + var15r;
                    int mh_n_4 = var17r - var15r;
                    float mh_n_0 = -2 * mh_s_0;
                    float mh_n_1 = -2 * mh_c_0;
                    for (int var19 = 0; var19 <= size; var19++) {
                        float var5d = var19 / size;
                        float var20 = var7 + mh_n_0 * var5d;
                        float var22 = var15 + mh_n_4 * var5d;
                        float var24 = var11 + mh_n_1 * var5d;
                        float var6d = var19 * math_pi / size;
                        float var26 = aRandom.nextFloat() * size / 16.0F;
                        float var28 = ((MathHelper.sin(var6d) + 1.0F) * var26 + 1.0F) / 2.0F;
                        int tMinX = MathHelper.floor_float(var20 - var28);
                        int tMinY = MathHelper.floor_float(var22 - var28);
                        int tMinZ = MathHelper.floor_float(var24 - var28);
                        int tMaxX = MathHelper.floor_float(var20 + var28);
                        int tMaxY = MathHelper.floor_float(var22 + var28);
                        int tMaxZ = MathHelper.floor_float(var24 + var28);
                        for (int eX = tMinX; eX <= tMaxX; eX++) {
                            float var39 = (eX + 0.5F - var20) / (var28);
                            float var10d = var39 * var39;
                            if (var10d < 1.0F) {
                                for (int eY = tMinY; eY <= tMaxY; eY++) {
                                    float var42 = (eY + 0.5F - var22) / (var28);
                                    float var12d = var10d + var42 * var42;
                                    if (var12d < 1.0F) {
                                        for (int eZ = tMinZ; eZ <= tMaxZ; eZ++) {
                                            float var45 = (eZ + 0.5F - var24) / (var28);
                                            if (var12d + var45 * var45 < 1.0F) {
                                                Block tTargetedBlock = aWorld.getBlock(eX, eY, eZ);
                                                if (tTargetedBlock instanceof GT_Block_Ores_Abstract) {
                                                    TileEntity tTileEntity = aWorld.getTileEntity(eX, eY, eZ);
                                                    if ((tTileEntity instanceof GT_TileEntity_Ores)) {
                                                        if (tTargetedBlock != GregTech_API.sBlockOres1) {
                                                            ((GT_TileEntity_Ores) tTileEntity).convertOreBlock(aWorld, eX, eY, eZ);
                                                        }
                                                        ((GT_TileEntity_Ores) tTileEntity).overrideOreBlockMaterial(block, (byte) blockMeta);
                                                    }
                                                } else if (((allowToGenerateInVoid) && (aWorld.getBlock(eX, eY, eZ).isAir(aWorld, eX, eY, eZ))) || ((tTargetedBlock != null) && ((tTargetedBlock.isReplaceableOreGen(aWorld, eX, eY, eZ, Blocks.stone)) || (tTargetedBlock.isReplaceableOreGen(aWorld, eX, eY, eZ, Blocks.end_stone)) || (tTargetedBlock.isReplaceableOreGen(aWorld, eX, eY, eZ, Blocks.netherrack)) || (tTargetedBlock.isReplaceableOreGen(aWorld, eX, eY, eZ, GregTech_API.sBlockGranites)) || (tTargetedBlock.isReplaceableOreGen(aWorld, eX, eY, eZ, GregTech_API.sBlockStones))))) {
                                                    aWorld.setBlock(eX, eY, eZ, block, blockMeta, 0);
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
            return true;
        }
        return false;
    }
}