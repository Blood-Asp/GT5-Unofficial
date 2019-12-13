package gregtech.loaders.postload;

import gregtech.api.util.GT_ProcessingArray_Manager;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Recipe.GT_Recipe_Map;

public class GT_ProcessingArrayRecipeLoader {
		
	public static void registerDefaultGregtechMaps() {
		
		// Centrifuge
		registerMapBetweenRange(361, 368, GT_Recipe.GT_Recipe_Map.sCentrifugeRecipes);
		
		// Electrolyzer
		registerMapBetweenRange(371, 378, GT_Recipe.GT_Recipe_Map.sElectrolyzerRecipes);
		
		// Assembler
		registerMapBetweenRange(211, 218, GT_Recipe.GT_Recipe_Map.sAssemblerRecipes);
		
		// Compressor
		registerMapBetweenRange(241, 248, GT_Recipe.GT_Recipe_Map.sCompressorRecipes);
		
		//Extractor
		registerMapBetweenRange(271, 278, GT_Recipe.GT_Recipe_Map.sExtractorRecipes);

		//Macerator
		registerMapBetweenRange(301, 308, GT_Recipe.GT_Recipe_Map.sMaceratorRecipes);
		
		// Microwave (New)
		registerMapBetweenRange(311, 318, GT_Recipe.GT_Recipe_Map.sMicrowaveRecipes);
		
		//Recycler
		registerMapBetweenRange(331, 338, GT_Recipe.GT_Recipe_Map.sRecyclerRecipes);
		
		//Thermal Centrifuge
		registerMapBetweenRange(381, 388, GT_Recipe.GT_Recipe_Map.sThermalCentrifugeRecipes);
		
		// Ore Washer
		registerMapBetweenRange(391, 398, GT_Recipe.GT_Recipe_Map.sOreWasherRecipes);
		
		// Chemical Reactor
		registerMapBetweenRange(421, 428, GT_Recipe.GT_Recipe_Map.sChemicalRecipes);
		
		// Chemical Bath
		registerMapBetweenRange(541, 548, GT_Recipe.GT_Recipe_Map.sChemicalBathRecipes);
		
		// Magnetic Seperator
		registerMapBetweenRange(561, 568, GT_Recipe.GT_Recipe_Map.sElectroMagneticSeparatorRecipes);
		
		// Autoclave
		registerMapBetweenRange(571, 578, GT_Recipe.GT_Recipe_Map.sAutoclaveRecipes);
		
		// Mixer
		registerMapBetweenRange(581, 588, GT_Recipe.GT_Recipe_Map.sMixerRecipes);
		
		// Forge Hammer
		registerMapBetweenRange(611, 618, GT_Recipe.GT_Recipe_Map.sHammerRecipes);
		
		// Sifter
		registerMapBetweenRange(641, 648, GT_Recipe.GT_Recipe_Map.sSifterRecipes);
		
		// Extruder
		registerMapBetweenRange(281, 288, GT_Recipe.GT_Recipe_Map.sExtruderRecipes);
		
		// Laser Engraver
		registerMapBetweenRange(591, 598, GT_Recipe.GT_Recipe_Map.sLaserEngraverRecipes);
		
		// Bender
		registerMapBetweenRange(221, 228, GT_Recipe.GT_Recipe_Map.sBenderRecipes);
		
		// Wiremill
		registerMapBetweenRange(351, 358, GT_Recipe.GT_Recipe_Map.sWiremillRecipes);
		
		// Arc Furnace
		registerMapBetweenRange(651, 658, GT_Recipe.GT_Recipe_Map.sArcFurnaceRecipes);
		
		// Plasma Arc Furnace
		registerMapBetweenRange(661, 668, GT_Recipe.GT_Recipe_Map.sPlasmaArcFurnaceRecipes);
		
		// Brewery
		registerMapBetweenRange(491, 498, GT_Recipe.GT_Recipe_Map.sBrewingRecipes);
		
		// Canner
		registerMapBetweenRange(231, 238, GT_Recipe.GT_Recipe_Map.sCannerRecipes);
		
		// Cutter
		registerMapBetweenRange(251, 258, GT_Recipe.GT_Recipe_Map.sCutterRecipes);
		
		// Fermenter
		registerMapBetweenRange(501, 508, GT_Recipe.GT_Recipe_Map.sFermentingRecipes);
		
		// Fluid Extractor
		registerMapBetweenRange(511, 518, GT_Recipe.GT_Recipe_Map.sFluidExtractionRecipes);
		
		// Fluid Solidifier
		registerMapBetweenRange(521, 528, GT_Recipe.GT_Recipe_Map.sFluidSolidficationRecipes);
		
		// Lathe
		registerMapBetweenRange(291, 298, GT_Recipe.GT_Recipe_Map.sLatheRecipes);
		
		// Boxinator
		registerMapBetweenRange(401, 408, GT_Recipe.GT_Recipe_Map.sBoxinatorRecipes);
		
		// Unboxinator
		registerMapBetweenRange(411, 408, GT_Recipe.GT_Recipe_Map.sUnboxinatorRecipes);
		
		// Polarizer
		registerMapBetweenRange(551, 558, GT_Recipe.GT_Recipe_Map.sPolarizerRecipes);
		
		// Printer
		registerMapBetweenRange(321, 328, GT_Recipe.GT_Recipe_Map.sPrinterRecipes);
		
		// Fluid Canner
		registerMapBetweenRange(431, 438, GT_Recipe.GT_Recipe_Map.sFluidCannerRecipes);
		
		// Fluid Heater
		registerMapBetweenRange(621, 628, GT_Recipe.GT_Recipe_Map.sFluidHeaterRecipes);
		
		// Distillery
		registerMapBetweenRange(531, 538, GT_Recipe.GT_Recipe_Map.sDistilleryRecipes);
		
		// Slicer
		registerMapBetweenRange(631, 638, GT_Recipe.GT_Recipe_Map.sSlicerRecipes);
		
		// Matter Amplifier
		registerMapBetweenRange(471, 478, GT_Recipe.GT_Recipe_Map.sAmplifiers);
		
		// Circuit Assembler
		registerMapBetweenRange(1180, 1187, GT_Recipe.GT_Recipe_Map.sCircuitAssemblerRecipes);

		// Alloy Smelter
		registerMapBetweenRange(201, 208, GT_Recipe.GT_Recipe_Map.sAlloySmelterRecipes);
		
		// Forming Press
		registerMapBetweenRange(601, 608, GT_Recipe.GT_Recipe_Map.sPressRecipes);
		
	}
	
	private static final void registerMapBetweenRange(int aMin, int aMax, GT_Recipe_Map aMap) {
		for (int i=aMin; i<aMax;i++) {
			GT_ProcessingArray_Manager.registerRecipeMapForMeta(i, aMap);
		}
	}
}
