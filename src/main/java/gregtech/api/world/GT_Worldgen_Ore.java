package gregtech.api.world;

import java.util.ArrayList;
import java.util.Collection;

import gregtech.api.GregTech_API;
import gregtech.loaders.postload.GT_Worldgenloader;
import net.minecraft.block.Block;
import scala.actors.threadpool.Arrays;

public abstract class GT_Worldgen_Ore extends GT_Worldgen {
    public final int mBlockMeta, mAmount, mSize, mMinY, mMaxY, mProbability, mDimensionType;
    public final Block mBlock;
    public final Collection<String> mBiomeList;
    public final boolean mAllowToGenerateinVoid;
    private final String aTextWorldgen = "worldgen.";

    public GT_Worldgen_Ore(String aName, boolean aDefault, Block aBlock, int aBlockMeta, int aDimensionType, int aAmount, int aSize, int aProbability, int aMinY, int aMaxY, Collection<String> aBiomeList, boolean aAllowToGenerateinVoid) {
        super(aName, GregTech_API.sWorldgenList, aDefault, null);
        this.mDimensionType = aDimensionType;
        this.mBlock = Block.getBlockFromName(GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + mWorldGenName, "Block", GT_Worldgenloader.getBlockName(aBlock)));
        this.mBlockMeta = GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + mWorldGenName, "BlockMeta", Math.min(Math.max(aBlockMeta, 0), 15));
        this.mProbability = GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + mWorldGenName, "Probability", aProbability);
        this.mAmount = GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + mWorldGenName, "Amount", aAmount);
        this.mSize = GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + mWorldGenName, "Size", aSize);
        this.mMinY = GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + mWorldGenName, "MinHeight", aMinY);
        this.mMaxY = Math.max(this.mMinY + 1, GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + mWorldGenName, "MaxHeight", aMaxY));
        this.mBiomeList = new ArrayList<String>(Arrays.asList(GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + mWorldGenName, "RestrictedBiomes", aBiomeList == null ? new String[0] : aBiomeList.toArray(new String[0]))));
        this.mAllowToGenerateinVoid = aAllowToGenerateinVoid;
    }
    
    public GT_Worldgen_Ore(String aName, boolean aDefault, Block aBlock, int aBlockMeta, int aDimensionType, int aAmount, int aSize, int aProbability, int aMinY, int aMaxY, String[] aDimensionList, String[] aBiomeList, boolean aAllowToGenerateinVoid) {
    	super(aName, GregTech_API.sWorldgenList, aDefault, aDimensionList);
        this.mDimensionType = aDimensionType;
        this.mBlock = aBlock;
        this.mBlockMeta = Math.min(Math.max(aBlockMeta, 0), 15);
        this.mProbability = aProbability;
        this.mAmount = aAmount;
        this.mSize = aSize;
        this.mMinY = aMinY;
        this.mMaxY = Math.max(this.mMinY + 1, aMaxY);
        this.mBiomeList = new ArrayList<String>(Arrays.asList(aBiomeList));
        this.mAllowToGenerateinVoid = aAllowToGenerateinVoid;
    }
}