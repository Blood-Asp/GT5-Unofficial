package gregtech.common;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.util.GT_Log;
import gregtech.api.world.GT_Worldgen;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

import static gregtech.api.enums.GT_Values.D1;
import static gregtech.api.enums.GT_Values.D2;

public class GT_Worldgen_GT_Ore_SmallPieces
        extends GT_Worldgen {
    public final short mMinY;
    public final short mMaxY;
    public final short mAmount;
    public final short mMeta;
    public final boolean mOverworld;
    public final boolean mNether;
    public final boolean mEnd;

    public final String mBiome;
    public final String aTextWorldgen = "worldgen.";
    //TODO CHECk IF INSTANTIATION IS CORRECT
    public GT_Worldgen_GT_Ore_SmallPieces(String aName, boolean aDefault, int aMinY, int aMaxY, int aAmount, boolean aOverworld, boolean aNether, boolean aEnd, boolean GC_UNUSED1, boolean GC_UNUSED2, boolean GC_UNUSED3, Materials aPrimary) {
        super(aName, GregTech_API.sWorldgenList, aDefault);
        this.mOverworld = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Overworld", aOverworld);
        this.mNether = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Nether", aNether);
        this.mEnd = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "TheEnd", aEnd);
        this.mMinY = ((short) GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "MinHeight", aMinY));
        this.mMaxY = ((short) Math.max(this.mMinY + 1, GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "MaxHeight", aMaxY)));
        this.mAmount = ((short) Math.max(1, GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Amount", aAmount)));
        this.mMeta = ((short) GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "Ore", aPrimary.mMetaItemSubID));
        this.mBiome = GregTech_API.sWorldgenFile.get(aTextWorldgen + this.mWorldGenName, "BiomeName", "None");
    }

    public boolean executeWorldgen(World aWorld, Random aRandom, String aBiome, int aDimensionType, int aChunkX, int aChunkZ, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        if (!this.mBiome.equals("None") && !(this.mBiome.equals(aBiome))) {
            return false; //Not the correct biome for ore mix
        }
        if (!isGenerationAllowed(aWorld, aDimensionType, ((aDimensionType == -1) && (this.mNether)) || ((aDimensionType == 0) && (this.mOverworld)) || ((aDimensionType == 1) && (this.mEnd)) ? aDimensionType : aDimensionType ^ 0xFFFFFFFF)) {
            return false;
        }
        int count=0;
        if (this.mMeta > 0) {
            int i = 0;
            for (int j = Math.max(1, this.mAmount / 2 + aRandom.nextInt(this.mAmount) / 2); i < j; i++) {
                GT_TileEntity_Ores.setOreBlock(aWorld, aChunkX + aRandom.nextInt(16), this.mMinY + aRandom.nextInt(Math.max(1, this.mMaxY - this.mMinY)), aChunkZ + aRandom.nextInt(16), this.mMeta, true);
                count++;
            }
        }
        if(D2){
            GT_Log.out.println(
                    "Small Ore:" + this.mWorldGenName +
                            " @ dim="+aDimensionType+
                            " chunkX="+aChunkX+
                            " chunkZ="+aChunkZ+
                            " ore="+count+" "+new ItemStack(GregTech_API.sBlockOres1,1,mMeta).getDisplayName()
            );
        }
        return true;
    }
}
