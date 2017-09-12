package gregtech.common;

import gregtech.api.GregTech_API;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class GT_Worldgen_GT_Ore_SmallPieces
        extends GT_Worldgen {
    public final int mMinY;
    public final int mMaxY;
    public final int mAmount;
    public final int mMeta;

    public GT_Worldgen_GT_Ore_SmallPieces(String aName, String[] dimWhiteList, int aMinY, int aMaxY, int aAmount, int aPrimary) {
        super(aName, dimWhiteList);
        mMinY = aMinY;
        mMaxY = aMaxY;
        mAmount = aAmount;
        mMeta = aPrimary;
        GregTech_API.sWorldgenList.add(this);
    }

    public boolean executeWorldgen(World aWorld, Random aRandom, String aBiome, int aDimensionType, int aChunkX, int aChunkZ, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        if (!isGenerationAllowed(aWorld)) {
            return false;
        }
        if (mMeta > 0) {
            int i = 0;
            for (int j = Math.max(1, mAmount / 2 + aRandom.nextInt(mAmount) / 2); i < j; i++) {
                GT_TileEntity_Ores.setOreBlock(aWorld, aChunkX + aRandom.nextInt(16), mMinY + aRandom.nextInt(Math.max(1, mMaxY - mMinY)), aChunkZ + aRandom.nextInt(16), mMeta, true);
            }
        }
        return true;
    }
}
