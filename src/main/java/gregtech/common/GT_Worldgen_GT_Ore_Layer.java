package gregtech.common;

import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.util.GT_Log;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.blocks.GT_TileEntity_Ores;
import gregtech.loaders.misc.GT_Achievements;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.ArrayList;
import java.util.Random;

import static gregtech.api.enums.GT_Values.D1;

public class GT_Worldgen_GT_Ore_Layer
        extends GT_Worldgen {
    public static ArrayList<GT_Worldgen_GT_Ore_Layer> sList = new ArrayList();
    public static int sWeight = 0;
    public final short mMinY;
    public final short mMaxY;
    public final short mWeight;
    public final short mDensity;
    public final short mSize;
    public final short mPrimaryMeta;
    public final short mSecondaryMeta;
    public final short mBetweenMeta;
    public final short mSporadicMeta;
    //public final String mBiome;
    public final String mRestrictBiome;
    public final boolean mOverworld;
    public final boolean mNether;
    public final boolean mEnd;
    public final boolean mEndAsteroid;
    //public final boolean mMoon;
    //public final boolean mMars;
    //public final boolean mAsteroid;
    public final String aTextWorldgen = "worldgen.";

    public GT_Worldgen_GT_Ore_Layer(String aName, boolean aDefault, int aMinY, int aMaxY, int aWeight, int aDensity, int aSize, boolean aOverworld, boolean aNether, boolean aEnd, boolean GC_UNUSED1, boolean GC_UNUSED2, boolean GC_UNUSED3,  Materials aPrimary, Materials aSecondary, Materials aBetween, Materials aSporadic) {
        super(aName, sList, aDefault);
        this.mOverworld = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Overworld", aOverworld);
        this.mNether = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Nether", aNether);
        this.mEnd = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "TheEnd", aEnd);
        this.mEndAsteroid = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "EndAsteroid", aEnd);
        //this.mMoon = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Moon", aMoon);
        //this.mMars = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Mars", aMars);
        //this.mAsteroid = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Asteroid", aAsteroid);
        this.mMinY = ((short) GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "MinHeight", aMinY));
        this.mMaxY = ((short) Math.max(this.mMinY + 5, GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "MaxHeight", aMaxY)));
        this.mWeight = ((short) GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "RandomWeight", aWeight));
        this.mDensity = ((short) GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Density", aDensity));
        this.mSize = ((short) Math.max(1, GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Size", aSize)));
        this.mPrimaryMeta = ((short) GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "OrePrimaryLayer", aPrimary.mMetaItemSubID));
        this.mSecondaryMeta = ((short) GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "OreSecondaryLayer", aSecondary.mMetaItemSubID));
        this.mBetweenMeta = ((short) GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "OreSporadiclyInbetween", aBetween.mMetaItemSubID));
        this.mSporadicMeta = ((short) GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "OreSporaticlyAround", aSporadic.mMetaItemSubID));
        this.mRestrictBiome = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "RestrictToBiomeName", "None");

        //if (mPrimaryMeta != -1 && GregTech_API.sGeneratedMaterials[(mPrimaryMeta % 1000)] == null) throw new IllegalArgumentException("A Material for the supplied ID " + mPrimaryMeta + " for " + mWorldGenName + " does not exist");
        //if (mSecondaryMeta != -1 && GregTech_API.sGeneratedMaterials[(mSecondaryMeta % 1000)] == null) throw new IllegalArgumentException("A Material for the supplied ID " + mSecondaryMeta + " for " + mWorldGenName + " does not exist");
        //if (mBetweenMeta != -1 && GregTech_API.sGeneratedMaterials[(mBetweenMeta % 1000)] == null) throw new IllegalArgumentException("A Material for the supplied ID " + mBetweenMeta + " for " + mWorldGenName + " does not exist");
        //if (mPrimaryMeta != -1 && GregTech_API.sGeneratedMaterials[(mSporadicMeta % 1000)] == null) throw new IllegalArgumentException("A Material for the supplied ID " + mSporadicMeta + " for " + mWorldGenName + " does not exist");

        if (this.mEnabled) {
            GT_Achievements.registerOre(GregTech_API.sGeneratedMaterials[(mPrimaryMeta % 1000)], aMinY, aMaxY, aWeight, aOverworld, aNether, aEnd);
            GT_Achievements.registerOre(GregTech_API.sGeneratedMaterials[(mSecondaryMeta % 1000)], aMinY, aMaxY, aWeight, aOverworld, aNether, aEnd);
            GT_Achievements.registerOre(GregTech_API.sGeneratedMaterials[(mBetweenMeta % 1000)], aMinY, aMaxY, aWeight, aOverworld, aNether, aEnd);
            GT_Achievements.registerOre(GregTech_API.sGeneratedMaterials[(mSporadicMeta % 1000)], aMinY, aMaxY, aWeight, aOverworld, aNether, aEnd);
            sWeight += this.mWeight;
            if(GregTech_API.mImmersiveEngineering && GT_Mod.gregtechproxy.mImmersiveEngineeringRecipes){
            	blusunrize.immersiveengineering.api.tool.ExcavatorHandler.addMineral(aName.substring(8, 9).toUpperCase()+aName.substring(9), aWeight, 0.2f, new String[]{"ore"+aPrimary.mName,"ore"+aSecondary.mName,"ore"+aBetween.mName,"ore"+aSporadic.mName}, new float[]{.4f,.4f,.15f,.05f});
            }
            
        }
    }

    public boolean executeWorldgen(World aWorld, Random aRandom, String aBiome, int aDimensionType, int aChunkX, int aChunkZ, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        if (!this.mRestrictBiome.equals("None") && !(this.mRestrictBiome.equals(aBiome))) {
            return false; //Not the correct biome for ore mix
        }
		//if (!isGenerationAllowed(aWorld, aDimensionType, ((aDimensionType == -1) && (this.mNether)) || ((aDimensionType == 0) && (this.mOverworld)) || ((aDimensionType == 1) && (this.mEnd)) || ((aWorld.provider.getDimensionName().equals("Moon")) && (this.mMoon)) || ((aWorld.provider.getDimensionName().equals("Mars")) && (this.mMars)) ? aDimensionType : aDimensionType ^ 0xFFFFFFFF)) {
        if (!isGenerationAllowed(aWorld, aDimensionType, ((aDimensionType == -1) && (this.mNether)) || ((aDimensionType == 0) && (this.mOverworld)) || ((aDimensionType == 1) && (this.mEnd)) ? aDimensionType : aDimensionType ^ 0xFFFFFFFF)) {
            return false;
        }
        int tMinY = this.mMinY + aRandom.nextInt(this.mMaxY - this.mMinY - 5);

        int cX = aChunkX - aRandom.nextInt(this.mSize);
        int eX = aChunkX + 16 + aRandom.nextInt(this.mSize);

        int[] execCount=new int[4];
        for (int tX = cX; tX <= eX; tX++) {
            int cZ = aChunkZ - aRandom.nextInt(this.mSize);
            int eZ = aChunkZ + 16 + aRandom.nextInt(this.mSize);
            for (int tZ = cZ; tZ <= eZ; tZ++) {
                if (this.mSecondaryMeta > 0) {
                    for (int i = tMinY - 1; i < tMinY + 2; i++) {
                        if ((aRandom.nextInt(Math.max(1, Math.max(MathHelper.abs_int(cZ - tZ), MathHelper.abs_int(eZ - tZ)) / this.mDensity)) == 0) || (aRandom.nextInt(Math.max(1, Math.max(MathHelper.abs_int(cX - tX), MathHelper.abs_int(eX - tX)) / this.mDensity)) == 0)) {
                            GT_TileEntity_Ores.setOreBlock(aWorld, tX, i, tZ, this.mSecondaryMeta, false);
                            execCount[1]++;
                        }
                    }
                }
                if ((this.mBetweenMeta > 0) && ((aRandom.nextInt(Math.max(1, Math.max(MathHelper.abs_int(cZ - tZ), MathHelper.abs_int(eZ - tZ)) / this.mDensity)) == 0) || (aRandom.nextInt(Math.max(1, Math.max(MathHelper.abs_int(cX - tX), MathHelper.abs_int(eX - tX)) / this.mDensity)) == 0))) {
                    GT_TileEntity_Ores.setOreBlock(aWorld, tX, tMinY + 2 + aRandom.nextInt(2), tZ, this.mBetweenMeta, false);
                    execCount[2]++;
                }
                if (this.mPrimaryMeta > 0) {
                    for (int i = tMinY + 3; i < tMinY + 6; i++) {
                        if ((aRandom.nextInt(Math.max(1, Math.max(MathHelper.abs_int(cZ - tZ), MathHelper.abs_int(eZ - tZ)) / this.mDensity)) == 0) || (aRandom.nextInt(Math.max(1, Math.max(MathHelper.abs_int(cX - tX), MathHelper.abs_int(eX - tX)) / this.mDensity)) == 0)) {
                            GT_TileEntity_Ores.setOreBlock(aWorld, tX, i, tZ, this.mPrimaryMeta, false);
                            execCount[0]++;
                        }
                    }
                }
                if ((this.mSporadicMeta > 0) && ((aRandom.nextInt(Math.max(1, Math.max(MathHelper.abs_int(cZ - tZ), MathHelper.abs_int(eZ - tZ)) / this.mDensity)) == 0) || (aRandom.nextInt(Math.max(1, Math.max(MathHelper.abs_int(cX - tX), MathHelper.abs_int(eX - tX)) / this.mDensity)) == 0))) {
                    GT_TileEntity_Ores.setOreBlock(aWorld, tX, tMinY - 1 + aRandom.nextInt(7), tZ, this.mSporadicMeta, false);
                    execCount[3]++;
                }
            }
        }
        if (D1) {
            GT_Log.out.println(
                            "Generated Orevein:" + this.mWorldGenName +
                            " @ dim="+aDimensionType+
                            " chunkX="+aChunkX+
                            " chunkZ="+aChunkZ+
                            " Secondary="+execCount[1]+" "+new ItemStack(GregTech_API.sBlockOres1,1,mPrimaryMeta).getDisplayName()+
                            " Between="+execCount[2]+" "+new ItemStack(GregTech_API.sBlockOres1,1,mPrimaryMeta).getDisplayName()+
                            " Primary="+execCount[0]+" "+new ItemStack(GregTech_API.sBlockOres1,1,mPrimaryMeta).getDisplayName()+
                            " Sporadic="+execCount[3]+" "+new ItemStack(GregTech_API.sBlockOres1,1,mPrimaryMeta).getDisplayName()
            );
        }
        return true;
    }
}
