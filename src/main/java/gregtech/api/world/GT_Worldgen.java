package gregtech.api.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import gregtech.api.GregTech_API;
import gregtech.api.util.GT_Utility;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public abstract class GT_Worldgen {

    public final String mWorldGenName;
    public final boolean mEnabled;
    protected final Collection<String> mDimensionWhiteList = new ArrayList<>();
    protected final Collection<Integer> mDimensionIDWhiteList = new ArrayList<>();

    public GT_Worldgen(String aName, List aList, boolean aDefault, String[] aDimWhiteList) {
        mWorldGenName = aName;
        mEnabled = GregTech_API.sAdvWorldgenFile.get("worldgen", mWorldGenName, aDefault);
        if (mEnabled) aList.add(this);
        for (String s : GregTech_API.sAdvWorldgenFile.get("worldgen." + mWorldGenName, "DimensionWhiteList", aDimWhiteList)) {
        	if (GT_Utility.isStringValid(s)) {
        		try {mDimensionIDWhiteList.add(Integer.parseInt(s));}
        		catch (NumberFormatException e) {mDimensionWhiteList.add(s);}
        	}
        }        	
    }

    /**
     * @param aWorld         The World Object
     * @param aRandom        The Random Generator to use
     * @param aBiome         The Name of the Biome (always != null)
     * @param aDimensionType The Type of Worldgeneration to add. -1 = Nether, 0 = Overworld, +1 = End
     * @param aChunkX        xCoord of the Chunk
     * @param aChunkZ        zCoord of the Chunk
     * @return if the Worldgeneration has been successfully completed
     */
    public boolean executeWorldgen(World aWorld, Random aRandom, String aBiome, int aDimensionType, int aChunkX, int aChunkZ, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        return false;
    }

    /**
     * @param aWorld         The World Object
     * @param aRandom        The Random Generator to use
     * @param aBiome         The Name of the Biome (always != null)
     * @param aDimensionType The Type of Worldgeneration to add. -1 = Nether, 0 = Overworld, +1 = End
     * @param aChunkX        xCoord of the Chunk
     * @param aChunkZ        zCoord of the Chunk
     * @return if the Worldgeneration has been successfully completed
     */
    public boolean executeCavegen(World aWorld, Random aRandom, String aBiome, int aDimensionType, int aChunkX, int aChunkZ, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        return false;
    }

    public boolean isGenerationAllowed(World aWorld, int aDimensionType, int aAllowedDimensionType) {
    	if (isGenerationAllowed(aWorld)) return true; 
    	if (aDimensionType == aAllowedDimensionType) {
    		mDimensionWhiteList.add(aWorld.provider.getDimensionName());
    		Collection<String> tList = new ArrayList<>();
    		tList.addAll(mDimensionWhiteList);
    		for (Integer i : mDimensionIDWhiteList) tList.add(i.toString());
    		GregTech_API.sAdvWorldgenFile.get("worldgen." + mWorldGenName, "DimensionWhiteList", tList.toArray(new String[0]));
    		return true;
    	}
    	return false;
    }

    public boolean isGenerationAllowed(World aWorld) {
        return mDimensionWhiteList.contains(aWorld.provider.getDimensionName()) || mDimensionWhiteList.contains(aWorld.provider.dimensionId);
    }
}
