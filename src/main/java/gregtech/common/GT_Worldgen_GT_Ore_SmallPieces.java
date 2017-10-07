package gregtech.common;

import java.util.ArrayList;
import java.util.Random;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import scala.actors.threadpool.Arrays;

public class GT_Worldgen_GT_Ore_SmallPieces
        extends GT_Worldgen {
    public final short mMinY;
    public final short mMaxY;
    public final short mAmount;
    public final short mMeta;
    @Deprecated
    public final boolean mOverworld, mNether, mEnd, mMoon, mMars, mAsteroid;
    @Deprecated
    public final String mRestrictBiome;
    public final ArrayList<String> mBiomeList;
    public final static String aTextWorldgen = "worldgen.";

    @Deprecated
    public GT_Worldgen_GT_Ore_SmallPieces(String aName, boolean aDefault, int aMinY, int aMaxY, int aAmount, boolean aOverworld, boolean aNether, boolean aEnd, boolean aMoon, boolean aMars, boolean aAsteroid, Materials aPrimary) {
        super(aName, GregTech_API.sWorldgenList, aDefault, GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + aName, "DimensionWhiteList", new String[]{aOverworld ? "Overworld" : "", aNether ? "Nether" : "", aEnd ? "The End" : "", aMoon ? "Moon" : "", aMars ? "Mars" : "", aAsteroid ? "Asteroids" : ""}));
        this.mMinY = ((short) GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "MinHeight", aMinY));
        this.mMaxY = ((short) Math.max(this.mMinY + 1, GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "MaxHeight", aMaxY)));
        this.mAmount = ((short) Math.max(1, GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Amount", aAmount)));
        this.mMeta = ((short) Materials.get(GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Ore", aPrimary.mName)).mMetaItemSubID);
        this.mBiomeList = new ArrayList<String>(Arrays.asList(GregTech_API.sAdvWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "RestrictedBiomes", new String[0])));
        this.mOverworld = this.mDimensionWhiteList.contains("OverWorld") || this.mDimensionIDWhiteList.contains(0);
    	this.mNether = this.mDimensionWhiteList.contains("Nether") || this.mDimensionIDWhiteList.contains(-1);
    	this.mEnd = this.mDimensionWhiteList.contains("The End") || this.mDimensionIDWhiteList.contains(1);
    	this.mMoon = this.mDimensionWhiteList.contains("Moon");
    	this.mMars = this.mDimensionWhiteList.contains("Mars");
    	this.mAsteroid = this.mDimensionWhiteList.contains("Asteroids");
        this.mRestrictBiome = this.mBiomeList.isEmpty() ? "None" : this.mBiomeList.get(0);
    }

    public GT_Worldgen_GT_Ore_SmallPieces(String aName, boolean aDefault, int aMinY, int aMaxY, int aAmount, String[] aDimWhiteList, String[] aBiomeWhiteList, int aOreMeta) {
    	super(aName, GregTech_API.sWorldgenList, aDefault, aDimWhiteList);
    	this.mMinY = (short) aMinY;
    	this.mMaxY = (short) Math.max(this.mMinY + 1, aMaxY);
    	this.mAmount = (short) Math.max(1, aAmount);
    	this.mMeta = (short) aOreMeta;
    	this.mBiomeList = new ArrayList<String>(Arrays.asList(aBiomeWhiteList));
    	this.mOverworld = this.mDimensionWhiteList.contains("OverWorld") || this.mDimensionIDWhiteList.contains(0);
    	this.mNether = this.mDimensionWhiteList.contains("Nether") || this.mDimensionIDWhiteList.contains(-1);
    	this.mEnd = this.mDimensionWhiteList.contains("The End") || this.mDimensionIDWhiteList.contains(1);
    	this.mMoon = this.mDimensionWhiteList.contains("Moon");
    	this.mMars = this.mDimensionWhiteList.contains("Mars");
    	this.mAsteroid = this.mDimensionWhiteList.contains("Asteroids");
    	this.mRestrictBiome = this.mBiomeList.isEmpty() ? "None" : this.mBiomeList.get(0);
    }

    public boolean executeWorldgen(World aWorld, Random aRandom, String aBiome, int aDimensionType, int aChunkX, int aChunkZ, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        if (!this.mBiomeList.isEmpty() && !this.mBiomeList.contains(aBiome)) {
            return false; //Not the correct biome for small ore
        }
        if (!isGenerationAllowed(aWorld, aDimensionType, ((aDimensionType == -1) && (this.mNether)) || ((aDimensionType == 0) && (this.mOverworld)) || ((aDimensionType == 1) && (this.mEnd)) ? aDimensionType : aDimensionType ^ 0xFFFFFFFF)) {
            return false;
        }
        if (this.mMeta > 0) {
            int i = 0;
            for (int j = Math.max(1, this.mAmount / 2 + aRandom.nextInt(this.mAmount) / 2); i < j; i++) {
                GT_TileEntity_Ores.setOreBlock(aWorld, aChunkX + aRandom.nextInt(16), this.mMinY + aRandom.nextInt(Math.max(1, this.mMaxY - this.mMinY)), aChunkZ + aRandom.nextInt(16), this.mMeta, true);
            }
        }
        return true;
    }
}
