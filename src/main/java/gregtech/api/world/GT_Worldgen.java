package gregtech.api.world;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class GT_Worldgen {

    public final String mWorldGenName;
    public final String[] dimensionNameWhiteList;
    public final int[] dimensionIDWhiteList;

    public GT_Worldgen(String aName, String[] whiteList) {
        mWorldGenName = aName;
        List<String> name = new ArrayList<>();
        List<Integer> id = new ArrayList<>();
        for (String s : whiteList) {
            try {
                id.add(Integer.parseInt(s));
            } catch (NumberFormatException ignored) {
                name.add(s);
            }
        }
        dimensionNameWhiteList = name.toArray(new String[name.size()]);
        dimensionIDWhiteList = new int[id.size()];
        for (int i = 0; i < dimensionIDWhiteList.length; i++) {
            dimensionIDWhiteList[i] = id.get(i);
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
    @Deprecated
    public boolean executeCavegen(World aWorld, Random aRandom, String aBiome, int aDimensionType, int aChunkX, int aChunkZ, IChunkProvider aChunkGenerator, IChunkProvider aChunkProvider) {
        return false;
    }

    @Deprecated
    public boolean isGenerationAllowed(World aWorld, int aDimensionType, int aAllowedDimensionType) {
        return isGenerationAllowed(aWorld);
    }

    public boolean isGenerationAllowed(World aWorld) {
        for (int i : dimensionIDWhiteList) {
            if (aWorld.provider.dimensionId == i) {
                return true;
            }
        }
        String dimName = aWorld.provider.getClass().getName();
        for (String s : dimensionNameWhiteList) {
            if (dimName.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean isGenerationAllowed(String name){
        for (String s : dimensionNameWhiteList) {
            if (name.equals(s)) {
                return true;
            }
        }
        return false;
    }
}
