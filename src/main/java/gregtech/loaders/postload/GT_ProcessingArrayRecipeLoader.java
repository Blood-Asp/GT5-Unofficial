package gregtech.loaders.postload;

import gregtech.api.util.GT_ProcessingArray_Manager;
import gregtech.api.util.GT_Recipe;
import gregtech.api.recipes.GT_RecipeMap;

public class GT_ProcessingArrayRecipeLoader {
		
	public static void registerDefaultGregtechMaps() {
		
		// Centrifuge
		registerMapBetweenRange(361, 368, GT_RecipeMap.sCentrifugeRecipes);
		
		// Electrolyzer
		registerMapBetweenRange(371, 378, GT_RecipeMap.sElectrolyzerRecipes);
		
		// Assembler
		registerMapBetweenRange(211, 218, GT_RecipeMap.sAssemblerRecipes);
		
		// Compressor
		registerMapBetweenRange(241, 248, GT_RecipeMap.sCompressorRecipes);
		
		//Extractor
		registerMapBetweenRange(271, 278, GT_RecipeMap.sExtractorRecipes);

		//Macerator
		registerMapBetweenRange(301, 308, GT_RecipeMap.sMaceratorRecipes);
		
		// Microwave (New)
		registerMapBetweenRange(311, 318, GT_RecipeMap.sMicrowaveRecipes);
		
		//Recycler
		registerMapBetweenRange(331, 338, GT_RecipeMap.sRecyclerRecipes);
		
		//Thermal Centrifuge
		registerMapBetweenRange(381, 388, GT_RecipeMap.sThermalCentrifugeRecipes);
		
		// Ore Washer
		registerMapBetweenRange(391, 398, GT_RecipeMap.sOreWasherRecipes);
		
		// Chemical Reactor
		registerMapBetweenRange(421, 428, GT_RecipeMap.sChemicalRecipes);
		
		// Chemical Bath
		registerMapBetweenRange(541, 548, GT_RecipeMap.sChemicalBathRecipes);
		
		// Magnetic Seperator
		registerMapBetweenRange(561, 568, GT_RecipeMap.sElectroMagneticSeparatorRecipes);
		
		// Autoclave
		registerMapBetweenRange(571, 578, GT_RecipeMap.sAutoclaveRecipes);
		
		// Mixer
		registerMapBetweenRange(581, 588, GT_RecipeMap.sMixerRecipes);
		
		// Forge Hammer
		registerMapBetweenRange(611, 618, GT_RecipeMap.sHammerRecipes);
		
		// Sifter
		registerMapBetweenRange(641, 648, GT_RecipeMap.sSifterRecipes);
		
		// Extruder
		registerMapBetweenRange(281, 288, GT_RecipeMap.sExtruderRecipes);
		
		// Laser Engraver
		registerMapBetweenRange(591, 598, GT_RecipeMap.sLaserEngraverRecipes);
		
		// Bender
		registerMapBetweenRange(221, 228, GT_RecipeMap.sBenderRecipes);
		
		// Wiremill
		registerMapBetweenRange(351, 358, GT_RecipeMap.sWiremillRecipes);
		
		// Arc Furnace
		registerMapBetweenRange(651, 658, GT_RecipeMap.sArcFurnaceRecipes);
		
		// Plasma Arc Furnace
		registerMapBetweenRange(661, 668, GT_RecipeMap.sPlasmaArcFurnaceRecipes);
		
		// Brewery
		registerMapBetweenRange(491, 498, GT_RecipeMap.sBrewingRecipes);
		
		// Canner
		registerMapBetweenRange(231, 238, GT_RecipeMap.sCannerRecipes);
		
		// Cutter
		registerMapBetweenRange(251, 258, GT_RecipeMap.sCutterRecipes);
		
		// Fermenter
		registerMapBetweenRange(501, 508, GT_RecipeMap.sFermentingRecipes);
		
		// Fluid Extractor
		registerMapBetweenRange(511, 518, GT_RecipeMap.sFluidExtractionRecipes);
		
		// Fluid Solidifier
		registerMapBetweenRange(521, 528, GT_RecipeMap.sFluidSolidificationRecipes);
		
		// Lathe
		registerMapBetweenRange(291, 298, GT_RecipeMap.sLatheRecipes);
		
		// Boxinator
		registerMapBetweenRange(401, 408, GT_RecipeMap.sBoxinatorRecipes);
		
		// Unboxinator
		registerMapBetweenRange(411, 408, GT_RecipeMap.sUnboxinatorRecipes);
		
		// Polarizer
		registerMapBetweenRange(551, 558, GT_RecipeMap.sPolarizerRecipes);
		
		// Printer
		registerMapBetweenRange(321, 328, GT_RecipeMap.sPrinterRecipes);
		
		// Fluid Canner
		registerMapBetweenRange(431, 438, GT_RecipeMap.sFluidCannerRecipes);
		
		// Fluid Heater
		registerMapBetweenRange(621, 628, GT_RecipeMap.sFluidHeaterRecipes);
		
		// Distillery
		registerMapBetweenRange(531, 538, GT_RecipeMap.sDistilleryRecipes);
		
		// Slicer
		registerMapBetweenRange(631, 638, GT_RecipeMap.sSlicerRecipes);
		
		// Matter Amplifier
		registerMapBetweenRange(471, 478, GT_RecipeMap.sAmplifiers);
		
		// Circuit Assembler
		registerMapBetweenRange(1180, 1187, GT_RecipeMap.sCircuitAssemblerRecipes);

		// Alloy Smelter
		registerMapBetweenRange(201, 208, GT_RecipeMap.sAlloySmelterRecipes);
		
		// Forming Press
		registerMapBetweenRange(601, 608, GT_RecipeMap.sPressRecipes);
		
	}
	
	private static final void registerMapBetweenRange(int aMin, int aMax, GT_RecipeMap aMap) {
		for (int i=aMin; i<=aMax;i++) {
			GT_ProcessingArray_Manager.registerRecipeMapForMeta(i, aMap);
		}
	}
}
