package gregtech.api.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import gregtech.api.GregTech_API;
import gregtech.api.util.GT_Utility;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public abstract class GT_Worldgen {

	public final boolean mEnabled;
    public final String mWorldGenName;
    public final String[] mDimensionWhiteList, mBiomeWhiteList;
    public final int[] mDimensionIDWhiteList;

    public GT_Worldgen(String aName, List aList, boolean aDefault, String[] aDimWhiteList, String[] aBiomeWhiteList) {
        mWorldGenName = aName;
        mEnabled = GregTech_API.sAdvWorldgenFile.get("worldgen", mWorldGenName, aDefault);
        if (mEnabled) aList.add(this);
        List<String> aDimName = new ArrayList<>(), aBiomeName = new ArrayList<>();
        List<Integer> aDimID = new ArrayList<>();
        for (String s : aDimWhiteList) {
        	if (GT_Utility.isStringValid(s)) {
        		try {aDimID.add(Integer.parseInt(s));}
        		catch (NumberFormatException e) {aDimName.add(s);}
        	}
        }
        for (String s : aBiomeWhiteList) {
        	if (GT_Utility.isStringValid(s))
        		aBiomeName.add(s);
        }
        mDimensionWhiteList = aDimName.toArray(new String[aDimName.size()]);
        mBiomeWhiteList = aBiomeName.toArray(new String[aBiomeName.size()]);
        mDimensionIDWhiteList = new int[aDimID.size()];
        for (int i = 0; i < mDimensionIDWhiteList.length; i++) {
            mDimensionIDWhiteList[i] = aDimID.get(i);
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
    @Deprecated
    public boolean executeWorldgen(World aWorld, Random aRandom, String aBiome, int aDimensionType, int aChunkX, int aChunkZ, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        return false;
    }

    /**
     * @param world         The World Object
     * @param random        The Random Generator to use
     * @param chunkX        xCoord of the Chunk
     * @param chunkZ        zCoord of the Chunk
     */
    public void executeWorldgen(World world, Random random, int chunkX, int chunkZ, String aBiome) {
        executeWorldgen(world, random, aBiome, 0, chunkX, chunkZ, null, null);
    }

    /**
     * @param world   The World Object
     * @param random  The Random Generator to use
     * @param chunkX  x coord of the Chunk to generate
     * @param chunkZ  z coord of the Chunk to generate
     * @param centerX x coord of the vein center
     * @param centerZ z coord of the vein center
     */
    public void executeLayerWorldgen(World world, Random random, int chunkX, int chunkZ, int centerX, int centerZ) {
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
    @Deprecated
    public boolean executeCavegen(World aWorld, Random aRandom, String aBiome, int aDimensionType, int aChunkX, int aChunkZ, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        return false;
    }

    @Deprecated
    public boolean isGenerationAllowed(World aWorld, int aDimensionType, int aAllowedDimensionType) {
        return isGenerationAllowed(aWorld);
    }

    public boolean isGenerationAllowed(World aWorld) {
        String aDimName = aWorld.provider.getDimensionName();
        String aClassName = aWorld.provider.getClass().getName();
        aClassName = aClassName.substring(aClassName.lastIndexOf(".") + 1).replace("WorldProvider", "");
        if (isGenerationAllowed(aWorld.provider.dimensionId)) return true;
        if (isGenerationAllowed(aDimName, aClassName)) return true;
        return false;
    }
    
    public boolean isGenerationAllowed(String aWorldName, String aWorldProviderClassName) {
    	if (aWorldName == null) aWorldName = "";
    	if (aWorldProviderClassName == null) aWorldProviderClassName = "";
    	for (String s : mDimensionWhiteList) {
            if (aWorldName.equals(s))
                return true;
            if (s.startsWith("?") && aWorldName.contains(s.substring(1)))
    			return true;
            if (s.startsWith("#") && aWorldProviderClassName.contains(s.substring(1)))
    			return true;
        }
        return false;
    }

    public boolean isGenerationAllowed(int aDimensionID) {
    	for (int i : mDimensionIDWhiteList) {
            if (aDimensionID == i)
                return true;
        }
    	return false;
    }
    
    public boolean isBiomeAllowed(String aBiomeName) {
    	if (mBiomeWhiteList.length == 0) return true;
    	for (String s : mBiomeWhiteList) {
    		if (aBiomeName.equals(s))
    			return true;
    		if (s.startsWith("?") && aBiomeName.contains(s.substring(1)))
    			return true;
    	}
    	return false;
    }
}
