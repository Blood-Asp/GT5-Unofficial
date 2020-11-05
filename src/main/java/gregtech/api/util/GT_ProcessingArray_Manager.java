package gregtech.api.util;

import gregtech.api.recipes.GT_RecipeMap;
import java.util.HashMap;

public class GT_ProcessingArray_Manager {

	private static final HashMap<Integer, String> mMetaKeyMap = new HashMap<Integer, String>();
	private static final HashMap<String, GT_RecipeMap> mRecipeCache = new HashMap<String, GT_RecipeMap>();
	
	public static boolean registerRecipeMapForMeta(int aMeta, GT_RecipeMap aMap) {
		if (aMeta < 0 || aMeta > Short.MAX_VALUE || aMap == null) {
			return false;
		}	
		if (mMetaKeyMap.containsKey(aMeta)) {
			return false;
		}	
		String aMapNameKey = aMap.mUnlocalizedName;		
		mMetaKeyMap.put(aMeta, aMapNameKey);
		if (!mRecipeCache.containsKey(aMapNameKey)) {
			mRecipeCache.put(aMapNameKey, aMap);			
		}	
		return true;
	}
	
	public static GT_RecipeMap getRecipeMapForMeta(int aMeta) {
		return mRecipeCache.get(mMetaKeyMap.get(aMeta));
	}
	
}
