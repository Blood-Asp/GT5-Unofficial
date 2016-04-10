package gregtech.loaders.postload;

import gregtech.api.enums.GT_Values;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.objects.MaterialStack;
import gregtech.api.util.GT_OreDictUnificator;


public class EX_MachineRecipeLoader {

	@SuppressWarnings("unused")
	private static MaterialStack[][] mAlloySmelterList;

	public void LOAD_RECIPES(){
		run();
	}

	private final static void run(){

		// Solid EBF Recipes
		GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Tungsten, 1L), GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Tantalum, 9L), GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingotHot, Materials.Tantaloy60, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.DarkAsh, 1L), (int) Math.max(Materials.Tantaloy60.getMass() / 80L, 1L) * Materials.Tantaloy60.mBlastFurnaceTemp, 480, Materials.Tantaloy60.mBlastFurnaceTemp);
		GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Titanium, 1L), GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Uranium, 9L), GT_Values.NF, GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingotHot, Materials.Staballoy, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.Carbon, 1L), (int) Math.max(Materials.Staballoy.getMass() / 80L, 1L) * Materials.Staballoy.mBlastFurnaceTemp, 480, Materials.Staballoy.mBlastFurnaceTemp);


		//  Gas/Liquid EBF Recipes
		//  GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Iron, 1L), GT_Values.NI, Materials.Oxygen.getGas(1000L), GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Steel, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.DarkAsh, 1L), 500, 120, 1000);
		//  GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.PigIron, 1L), GT_Values.NI, Materials.Oxygen.getGas(1000L), GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Steel, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.DarkAsh, 1L), 100, 120, 1000);
		//  GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.WroughtIron, 1L), GT_Values.NI, Materials.Oxygen.getGas(1000L), GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Steel, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.DarkAsh, 1L), 100, 120, 1000);
		//  GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.ShadowIron, 1L), GT_Values.NI, Materials.Oxygen.getGas(1000L), GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.ShadowSteel, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.DarkAsh, 1L), 500, 120, 1100);
		//  GT_Values.RA.addBlastRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.MeteoricIron, 1L), GT_Values.NI, Materials.Oxygen.getGas(1000L), GT_Values.NF, GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.MeteoricSteel, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.DarkAsh, 1L), 500, 120, 1200);


		// Alloy Smelter
		/* for (MaterialStack[] tMats : mAlloySmelterList) {
            ItemStack tDust1 = GT_OreDictUnificator.get(OrePrefixes.dust, tMats[0].mMaterial, tMats[0].mAmount);
            ItemStack tDust2 = GT_OreDictUnificator.get(OrePrefixes.dust, tMats[1].mMaterial, tMats[1].mAmount);
            ItemStack tIngot1 = GT_OreDictUnificator.get(OrePrefixes.ingot, tMats[0].mMaterial, tMats[0].mAmount);
            ItemStack tIngot2 = GT_OreDictUnificator.get(OrePrefixes.ingot, tMats[1].mMaterial, tMats[1].mAmount);
            ItemStack tOutputIngot = GT_OreDictUnificator.get(OrePrefixes.ingot, tMats[2].mMaterial, tMats[2].mAmount);
            if (tOutputIngot != GT_Values.NI) {
                GT_ModHandler.addAlloySmelterRecipe(tIngot1, tDust2, tOutputIngot, (int) tMats[2].mAmount * 50, 16, false);
                GT_ModHandler.addAlloySmelterRecipe(tIngot1, tIngot2, tOutputIngot, (int) tMats[2].mAmount * 50, 16, false);
                GT_ModHandler.addAlloySmelterRecipe(tDust1, tIngot2, tOutputIngot, (int) tMats[2].mAmount * 50, 16, false);
                GT_ModHandler.addAlloySmelterRecipe(tDust1, tDust2, tOutputIngot, (int) tMats[2].mAmount * 50, 16, false);
            }
        }*/

	}


}
