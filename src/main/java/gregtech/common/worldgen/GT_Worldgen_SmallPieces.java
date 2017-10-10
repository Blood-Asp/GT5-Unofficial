package gregtech.common.worldgen;

import gregtech.api.GregTech_API;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.world.World;

import java.util.Random;

public class GT_Worldgen_SmallPieces extends GT_Worldgen {
    public final int mMinY;
    public final int mMaxY;
    public final int mAmount;
    public final int mMeta;

    public GT_Worldgen_SmallPieces(String aName, boolean aDefault, int aMinY, int aMaxY, int aAmount, String[] aDimWhiteList, String[] aBiomeWhiteList, int aOreMeta) {
        super(aName, GregTech_API.sWorldgenList, aDefault, aDimWhiteList, aBiomeWhiteList);
        mMinY = aMinY;
        mMaxY = aMaxY;
        mAmount = aAmount;
        mMeta = aOreMeta;
    }

    @Override
    public void executeWorldgen(World aWorld, Random aRandom, int aChunkX, int aChunkZ, String aBiome) {
        if (!isGenerationAllowed(aWorld) || !isBiomeAllowed(aBiome)) {
            return;
        }
        if (mMeta > 0) {
            int i = 0;
            for (int j = Math.max(1, mAmount / 2 + aRandom.nextInt(mAmount) / 2); i < j; i++) {
                GT_TileEntity_Ores.setOreBlock(aWorld, aChunkX + aRandom.nextInt(16), mMinY + aRandom.nextInt(Math.max(1, mMaxY - mMinY)), aChunkZ + aRandom.nextInt(16), mMeta, true);
            }
        }
    }
}
