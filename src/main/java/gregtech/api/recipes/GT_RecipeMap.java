package gregtech.api.recipes;

import gregtech.api.enums.GT_Values;
import gregtech.api.util.GT_LanguageManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;



public class GT_RecipeMap {

    /**
     * Contains all Recipe Maps
     */
    public static final Collection<GT_RecipeMap> sMappings = new ArrayList<GT_RecipeMap>(100);
    
    public static final GT_RecipeMap sOreWasherRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(120)).setNames("gt.recipe.orewasher", "Ore Washing Plant").setNEIGUIPathBasic("OreWasher").setOutputSlots(3).setMinimalInputFluids(1);
    public static final GT_RecipeMap sThermalCentrifugeRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(120)).setNames("gt.recipe.thermalcentrifuge", "Thermal Centrifuge").setNEIGUIPathBasic("ThermalCentrifuge").setOutputSlots(3).setAmperage(2);
    public static final GT_RecipeMap sCompressorRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(120)).setNames("gt.recipe.compressor", "Compressor").setNEIGUIPathBasic("Compressor");
    public static final GT_RecipeMap sExtractorRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(30)).setNames("gt.recipe.extractor", "Extractor").setNEIGUIPathBasic("Extractor");
    public static final GT_RecipeMap sRecyclerRecipes = new GT_RecipeMapRecycler(new HashSet<GT_MachineRecipe>(0)).setNames("ic.recipe.recycler", "Recycler").setNEIName("ic2.recycler").setNEIGUIPathBasic("Recycler").setNEIAllowed(false);
    public static final GT_RecipeMap sFurnaceRecipes = new GT_RecipeMapFurnace(new HashSet<GT_MachineRecipe>(0)).setNames("mc.recipe.furnace", "Furnace").setNEIName("smelting").setNEIGUIPathBasic("E_Furnace").setNEIAllowed(false);
    public static final GT_RecipeMap sMicrowaveRecipes = new GT_RecipeMapMicrowave(new HashSet<GT_MachineRecipe>(0)).setNames("gt.recipe.microwave", "Microwave").setNEIName("smelting").setNEIGUIPathBasic("E_Furance").setNEIAllowed(false);
    
    public static final GT_RecipeMap sScannerFakeRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(300)).setNames("gt.recipe.scanner", "Scanner").setNEIGUIPathBasic("Scanner");
    public static final GT_RecipeMap sRockBreakerFakeRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(3)).setNames("gt.recipe.rockbreaker", "Rock Breaker").setNEIGUIPathBasic("RockBreaker").setMinimalInputItems(0);
    public static final GT_RecipeMap sByproductList = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(1000)).setNames("gt.recipe.byproductlist", "Ore Byproduct List").setNEIGUIPathBasic("Default").setOutputSlots(6);
    public static final GT_RecipeMap sReplicatorFakeRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(100)).setNames("gt.recipe.replicator", "Replicator").setNEIGUIPathBasic("Replicator").setInputSlots(0).setMinimalInputItems(0).setMinimalInputFluids(1);
    public static final GT_RecipeMap sOrganicReplicatorFakeRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(1)).setNames("gt.recipe.organicreplicator", "Organic Replicator").setNEIGUIPathBasic("OrganicReplicator").setMinimalInputFluids(1);
    public static final GT_RecipeMap sAssemblyLineVisualRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(30)).setNames("gt.recipe.fakeAssemblylineProcess", "Assemblyline Process").setNEIGUIPathRes("FakeAssemblyline").setNEIAllowed(false);
    public static final GT_RecipeMap sPlasmaArcFurnaceRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(10000)).setNames("gt.recipe.plasmaarcfurnace", "Plasma Arc Furnace").setNEIGUIPathBasic("PlasmaArcFurnace").setOutputSlots(4).setMinimalInputFluids(1);
    public static final GT_RecipeMap sArcFurnaceRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(10000)).setNames("gt.recipe.arcfurnace", "Arc Furnace").setNEIGUIPathBasic("ArcFurnace").setOutputSlots(4).setMinimalInputFluids(1).setAmperage(3);
    public static final GT_RecipeMap sPrinterRecipes = new GT_RecipeMapPrinter(new HashSet<GT_MachineRecipe>(100)).setNames("gt.recipe.printer", "Printer").setNEIGUIPathBasic("Printer").setMinimalInputFluids(1);
    public static final GT_RecipeMap sSifterRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(100)).setNames("gt.recipe.sifter", "Sifter").setNEIGUIPathBasic("Sifter").setOutputSlots(9);
    public static final GT_RecipeMap sPressRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(100)).setNames("gt.recipe.press", "Forming Press").setNEIGUIPathBasic("FormingPress").setInputSlots(2).setMinimalInputItems(2);
    public static final GT_RecipeMap sLaserEngraverRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(100)).setNames("gt.recipe.laserengraver", "Precision Laser Engraver").setNEIGUIPathBasic("LaserEngraver").setInputSlots(2).setMinimalInputItems(2);
    public static final GT_RecipeMap sMixerRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(100)).setNames("gt.recipe.mixer", "Mixer").setNEIGUIPathBasic("Mixer").setInputSlots(4).setMinimalTotalInputs(2);
    public static final GT_RecipeMap sAutoclaveRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(200)).setNames("gt.recipe.autoclave", "Autoclave").setNEIGUIPathBasic("Autoclave").setMinimalInputFluids(1);
    public static final GT_RecipeMap sElectroMagneticSeparatorRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(50)).setNames("gt.recipe.electromagneticseparator", "Electromagnetic Separator").setNEIGUIPathBasic("ElectromagneticSeparator").setOutputSlots(3);
    public static final GT_RecipeMap sPolarizerRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(50)).setNames("gt.recipe.polarizer", "Electromagnetic Polarizer").setNEIGUIPathBasic("Polarizer");
    public static final GT_RecipeMap sMaceratorRecipes = new GT_RecipeMapMacerator(new HashSet<GT_MachineRecipe>(10000)).setNames("gt.recipe.macerator", "Pulverization").setNEIGUIPathBasic("Macerator4").setOutputSlots(4);
    public static final GT_RecipeMap sChemicalBathRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(200)).setNames("gt.recipe.chemicalbath", "Chemical Bath").setNEIGUIPathBasic("ChemicalBath").setOutputSlots(3).setMinimalInputFluids(1);
    public static final GT_RecipeMap sFluidCannerRecipes = new GT_RecipeMapFluidCanner(new HashSet<GT_MachineRecipe>(1000)).setNames("gt.recipe.fluidcanner", "Fluid Canning Machine").setNEIGUIPathBasic("FluidCannerNEI");
    public static final GT_RecipeMap sBrewingRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(100)).setNames("gt.recipe.brewer", "Brewing Machine").setNEIGUIPathBasic("PotionBrewer").setOutputSlots(0).setMinimalInputFluids(1);
    public static final GT_RecipeMap sFluidHeaterRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(100)).setNames("gt.recipe.fluidheater", "Fluid Heater").setNEIGUIPathBasic("FluidHeater").setOutputSlots(0).setMinimalInputFluids(1);

    public static final GT_RecipeMap sDistilleryRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(100)).setNames("gt.recipe.distillery", "Distillery").setNEIGUIPathBasic("Distillery").setMinimalInputFluids(1);
    public static final GT_RecipeMap sFermentingRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(100)).setNames("gt.recipe.fermenter", "Fermenter").setNEIGUIPathBasic("Fermenter").setInputSlots(0).setOutputSlots(0).setMinimalInputItems(0);
    public static final GT_RecipeMap sFluidSolidificationRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(100)).setNames("gt.recipe.fluidsolidifier", "Fluid Solidifier").setNEIGUIPathBasic("FluidSolidifier").setMinimalInputFluids(1);
    public static final GT_RecipeMap sFluidExtractionRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(100)).setNames("gt.recipe.fluidextractor", "Fluid Extractor").setNEIGUIPathBasic("FluidExtractor");
    public static final GT_RecipeMap sBoxinatorRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(2500)).setNames("gt.recipe.packager", "Packager").setNEIGUIPathBasic("Packager").setInputSlots(2).setMinimalInputItems(2);
    public static final GT_RecipeMap sUnboxinatorRecipes = new GT_RecipeMapUnboxinator(new HashSet<GT_MachineRecipe>(2500)).setNames("gt.recipe.unpackager", "Unpackager").setNEIGUIPathBasic("Unpackager").setOutputSlots(2);
    public static final GT_RecipeMap sFusionRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(50)).setNames("gt.recipe.fusionreactor", "Fusion Reactor").setNEIGUIPathBasic("Default").setInputSlots(0).setOutputSlots(0).setMinimalInputItems(0).setMinimalInputFluids(2).setNEISpecialValuePre("Start: ").setNEISpecialValuePost(" EU");
    public static final GT_RecipeMap sCentrifugeRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(1000)).setNames("gt.recipe.centrifuge", "Centrifuge").setNEIGUIPathBasic("Centrifuge").setInputSlots(2).setOutputSlots(6).setMinimalInputItems(0).setMinimalInputFluids(0).setMinimalTotalInputs(1);
    public static final GT_RecipeMap sElectrolyzerRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(200)).setNames("gt.recipe.electrolyzer", "Electrolyzer").setNEIGUIPathBasic("Electrolyzer").setInputSlots(2).setOutputSlots(6).setMinimalInputItems(0).setMinimalInputFluids(0).setMinimalTotalInputs(1);
    public static final GT_RecipeMap sBlastRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(500)).setNames("gt.recipe.blastfurnace", "Blast Furnace").setNEIGUIPathBasic("Default").setInputSlots(2).setOutputSlots(2).setNEISpecialValuePre("Heat Capacity: ").setNEISpecialValuePost(" K").setShowVoltageAmperageInNEI(false);
    public static final GT_RecipeMap sPrimitiveBlastRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(500)).setNames("gt.recipe.primitiveblastfurnace", "Primitive Blast Furnace").setNEIGUIPathBasic("Default").setInputSlots(3).setOutputSlots(3).setMinimalInputItems(2).setShowVoltageAmperageInNEI(false);
    public static final GT_RecipeMap sImplosionRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(50)).setNames("gt.recipe.implosioncompressor", "Implosion Compressor").setNEIGUIPathBasic("Default").setInputSlots(2).setOutputSlots(2).setMinimalInputItems(2);
    public static final GT_RecipeMap sVacuumRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(100)).setNames("gt.recipe.vacuumfreezer", "Vacuum Freezer").setNEIGUIPathBasic("Default");
    public static final GT_RecipeMap sChemicalRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(100)).setNames("gt.recipe.chemicalreactor", "Chemical Reactor").setNEIGUIPathBasic("ChemicalReactor").setInputSlots(2).setOutputSlots(2);
    public static final GT_RecipeMap sMultiblockChemicalRecipes = new GT_RecipeMapLargeChemicalReactor();
    public static final GT_RecipeMap sDistillationRecipes = new GT_RecipeMapDistillationTower();
    public static final GT_RecipeMap sCrackingRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(50)).setNames("gt.recipe.craker", "Oil Cracker").setNEIGUIPathBasic("Default").setMinimalInputFluids(2);
    public static final GT_RecipeMap sPyrolyseRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(50)).setNames("gt.recipe.pyro", "Pyrolyse Oven").setNEIGUIPathBasic("Default").setInputSlots(2);
    public static final GT_RecipeMap sWiremillRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(50)).setNames("gt.recipe.wiremill", "Wiremill").setNEIGUIPathBasic("Wiremill");
    public static final GT_RecipeMap sBenderRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(400)).setNames("gt.recipe.metalbender", "Metal Bender").setNEIGUIPathBasic("Bender").setInputSlots(2).setMinimalInputItems(2);
    public static final GT_RecipeMap sAlloySmelterRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(3000)).setNames("gt.recipe.alloysmelter", "Alloy Smelter").setNEIGUIPathBasic("AlloySmelter").setInputSlots(2).setMinimalInputItems(2);
    public static final GT_RecipeMap sAssemblerRecipes = new GT_RecipeMapAssembler(new HashSet<GT_MachineRecipe>(300)).setNames("gt.recipe.assembler", "Assembler").setNEIGUIPathBasic("Assembler").setInputSlots(6);
    public static final GT_RecipeMap sCircuitAssemblerRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(300)).setNames("gt.recipe.circuitassembler", "Circuit Assembler").setNEIGUIPathBasic("CircuitAssembler").setInputSlots(6).setMinimalInputItems(3).setMinimalInputFluids(1);
    public static final GT_RecipeMap sCannerRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(300)).setNames("gt.recipe.canner", "Canning Machine").setNEIGUIPathBasic("Canner").setInputSlots(2).setMinimalInputItems(2);
    public static final GT_RecipeMap sLatheRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(400)).setNames("gt.recipe.lathe", "Lathe").setNEIGUIPathBasic("Lathe").setOutputSlots(2);
    public static final GT_RecipeMap sCutterRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(200)).setNames("gt.recipe.cuttingsaw", "Cutting Saw").setNEIGUIPathBasic("Cutter").setOutputSlots(2).setMinimalInputFluids(2);
    public static final GT_RecipeMap sSlicerRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(200)).setNames("gt.recipe.slicer", "Slicer").setNEIGUIPathBasic("Slicer").setInputSlots(2).setMinimalInputItems(2);
    public static final GT_RecipeMap sExtruderRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(1000)).setNames("gt.recipe.extruder", "Extruder").setNEIGUIPathBasic("Extruder").setInputSlots(2).setMinimalInputItems(2);
    public static final GT_RecipeMap sHammerRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(200)).setNames("gt.recipe.hammer", "Hammer").setNEIGUIPathBasic("Hammer");
    public static final GT_RecipeMap sAmplifiers = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(10)).setNames("gt.recipe.uuamplifier", "UU Amplifier").setNEIGUIPathBasic("Amplifabricator").setOutputSlots(0);
    public static final GT_RecipeMap sMassFabFakeRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(10)).setNames("gt.recipe.massfab", "Mass Fabrication").setNEIGUIPathBasic("Massfabricator").setOutputSlots(0);
    public static final GT_RecipeMapFuel sDieselFuels = (GT_RecipeMapFuel) new GT_RecipeMapFuel(new HashSet<GT_MachineRecipe>(10)).setNames("gt.recipe.dieselgeneratorfuel", "Diesel Generator Fuel").setNEIGUIPathBasic("Default").setMinimalInputItems(0).setNEISpecialValuePre("Fuel Value: ").setNEISpecialValueMultiplier(1000).setNEISpecialValuePost(" EU");
    public static final GT_RecipeMapFuel sTurbineFuels = (GT_RecipeMapFuel) new GT_RecipeMapFuel(new HashSet<GT_MachineRecipe>(10)).setNames("gt.recipe.gasturbinefuel", "Gas Turbine Fuel").setNEIGUIPathBasic("Default").setMinimalInputItems(0).setNEISpecialValuePre("Fuel Value: ").setNEISpecialValueMultiplier(1000).setNEISpecialValuePost(" EU");
    public static final GT_RecipeMapFuel sHotFuels = (GT_RecipeMapFuel) new GT_RecipeMapFuel(new HashSet<GT_MachineRecipe>(10)).setNames("gt.recipe.thermalgeneratorfuel", "Thermal Generator Fuel").setNEIGUIPathBasic("Default").setMinimalInputItems(0).setNEISpecialValuePre("Fuel Value: ").setNEISpecialValueMultiplier(1000).setNEISpecialValuePost(" EU").setNEIAllowed(false);
    public static final GT_RecipeMapFuel sDenseLiquidFuels = (GT_RecipeMapFuel) new GT_RecipeMapFuel(new HashSet<GT_MachineRecipe>(10)).setNames("gt.recipe.semifluidboilerfuels", "Semifluid Boiler Fuels").setNEIGUIPathBasic("Default").setMinimalInputItems(0).setNEISpecialValuePre("Fuel Value: ").setNEISpecialValueMultiplier(1000).setNEISpecialValuePost(" EU");
    public static final GT_RecipeMapFuel sPlasmaFuels = (GT_RecipeMapFuel) new GT_RecipeMapFuel(new HashSet<GT_MachineRecipe>(10)).setNames("gt.recipe.plasmageneratorfuels", "Plasma generator Fuels").setNEIGUIPathBasic("Default").setMinimalInputItems(0).setNEISpecialValuePre("Fuel Value: ").setNEISpecialValueMultiplier(1000).setNEISpecialValuePost(" EU");
    public static final GT_RecipeMapFuel sMagicFuels = (GT_RecipeMapFuel) new GT_RecipeMapFuel(new HashSet<GT_MachineRecipe>(10)).setNames("gt.recipe.magicfuels", "Magic Fuels").setNEIGUIPathBasic("Default").setMinimalInputItems(0).setNEISpecialValuePre("Fuel Value: ").setNEISpecialValueMultiplier(1000).setNEISpecialValuePost(" EU");
    public static final GT_RecipeMapFuel sSmallNaquadahReactorFuels = (GT_RecipeMapFuel) new GT_RecipeMapFuel(new HashSet<GT_MachineRecipe>(10)).setNames("gt.recipe.smallnaquadahreactor", "Small Naquadah Reactor").setNEIGUIPathBasic("Default").setMinimalInputItems(0).setNEISpecialValuePre("Fuel Value: ").setNEISpecialValueMultiplier(1000).setNEISpecialValuePost(" EU");
    public static final GT_RecipeMapFuel sLargeNaquadahReactorFuels = (GT_RecipeMapFuel) new GT_RecipeMapFuel(new HashSet<GT_MachineRecipe>(10)).setNames("gt.recipe.largenaquadahreactor", "Large Naquadah Reactor").setNEIGUIPathBasic("Default").setMinimalInputItems(0).setNEISpecialValuePre("Fuel Value: ").setNEISpecialValueMultiplier(1000).setNEISpecialValuePost(" EU");
    public static final GT_RecipeMapFuel sFluidNaquadahReactorFuels = (GT_RecipeMapFuel) new GT_RecipeMapFuel(new HashSet<GT_MachineRecipe>(10)).setNames("gt.recipe.fluidnaquadahreactor", "Fluid Naquadah Reactor").setNEIGUIPathBasic("Default").setMinimalInputItems(0).setNEISpecialValuePre("Fuel Value: ").setNEISpecialValueMultiplier(1000).setNEISpecialValuePost(" EU");
    public static final GT_RecipeMapLargeBoilerFakeFuels sLargeBoilerFakeFuels = new GT_RecipeMapLargeBoilerFakeFuels();
   
    /**
     * The List of all Recipes
     */
    public final Collection<GT_MachineRecipe> mRecipeList;
    /**
     * String used as an unlocalised Name.
     */
    public String mUnlocalizedName = "";
    /**
     * String used in NEI for the Recipe Lists. If null it will use the unlocalised Name instead
     */
    public String mNEIName = "";
    /**
     * GUI used for NEI Display. Usually the GUI of the Machine itself
     */
    public String mNEIGUIPath = "";
    public String mNEISpecialValuePre = "", mNEISpecialValuePost = "";
    public int mInputSlots = 1;
    public int mOutputSlots = 1;
    public int mNEISpecialValueMultiplier = 1;
    public int mMinimalInputItems = 1;
    public int mMinimalInputFluids = 0;
    public int mMinimalTotalInputs = 0;  // For a few machines like the mixer that are item + fluid or item + item but no single-item or single-fluid input.
    public int mAmperage = 1;
    public boolean mNEIAllowed = true;
    public boolean mShowVoltageAmperageInNEI = true;
    
    public GT_RecipeMap(Collection<GT_MachineRecipe> aRecipeList) {
        mRecipeList = aRecipeList;
        sMappings.add(this);
    }
    
    public final GT_RecipeMap setUnlocalizedName(String aUnlocalizedName) {
        mUnlocalizedName = aUnlocalizedName;
        if ("".equals(mNEIName)) {
            mNEIName = aUnlocalizedName;
        }
        return this;
    }
    
    // unlocalized name and local name should usually be set together, but local name isn't technically stored in this object.
    public final GT_RecipeMap setNames(String aUnlocalizedName, String aLocalName) {
        mUnlocalizedName = aUnlocalizedName;
        if ("".equals(mNEIName)) {
            mNEIName = aUnlocalizedName;
        }
        GT_LanguageManager.addStringLocalization(aUnlocalizedName, aLocalName);
        return this;
    }
    
    public final GT_RecipeMap setNEIName(String aNEIName) {
        mNEIName = aNEIName;
        return this;
    }
    
    public final GT_RecipeMap setNEIGUIPath(String aNEIGUIPath) {
        if (aNEIGUIPath.endsWith(".png")) {
            mNEIGUIPath = aNEIGUIPath;
        } else {
            mNEIGUIPath = aNEIGUIPath + ".png";
        }
        return this;
    }
    
    // Convenience method to prepend GT_Values.RES_PATH_GUI to the passed path,
    // since it is nearly always used.
    public final GT_RecipeMap setNEIGUIPathRes(String aNEIGUIPath) {
        if (aNEIGUIPath.endsWith(".png")) {
            mNEIGUIPath = GT_Values.RES_PATH_GUI + aNEIGUIPath;
        } else {
            mNEIGUIPath = GT_Values.RES_PATH_GUI + aNEIGUIPath + ".png";
        }
        return this;
    }
    
    public final GT_RecipeMap setNEIGUIPathBasic(String aNEIGUIPath) {
        return setNEIGUIPathRes("basicmachines/" + aNEIGUIPath);
    }
    
    public final GT_RecipeMap setNEISpecialValuePre(String aNEISpecialValuePre) {
        mNEISpecialValuePre = aNEISpecialValuePre;
        return this;
    }
    
    public final GT_RecipeMap setNEISpecialValuePost(String aNEISpecialValuePost) {
        mNEISpecialValuePost = aNEISpecialValuePost;
        return this;
    }
    
    public final GT_RecipeMap setInputSlots(int aInputSlots) {
        mInputSlots = aInputSlots;
        return this;
    }
    
    public final GT_RecipeMap setOutputSlots(int aOutputSlots) {
        mOutputSlots = aOutputSlots;
        return this;
    }
    
    public final GT_RecipeMap setNEISpecialValueMultiplier(int aNEISpecialValueMultiplier) {
        mNEISpecialValueMultiplier = aNEISpecialValueMultiplier;
        return this;
    }
    
    public final GT_RecipeMap setMinimalInputItems(int aMinimalInputItems) {
        mMinimalInputItems = aMinimalInputItems;
        return this;
    }
    
    public final GT_RecipeMap setMinimalInputFluids(int aMinimalInputFluids) {
        mMinimalInputFluids = aMinimalInputFluids;
        return this;
    }
    
    public final GT_RecipeMap setMinimalTotalInputs(int aMinimalTotalInputs) {
        mMinimalTotalInputs = aMinimalTotalInputs;
        return this;
    }
    
    public final GT_RecipeMap setAmperage(int aAmperage) {
        mAmperage = aAmperage;
        return this;
    }
    
    public final GT_RecipeMap setNEIAllowed(boolean aNEIAllowed) {
        mNEIAllowed = aNEIAllowed;
        return this;
    }
    
    public final GT_RecipeMap setShowVoltageAmperageInNEI(boolean aShowVoltageAmperageInNEI) {
        mShowVoltageAmperageInNEI = aShowVoltageAmperageInNEI;
        return this;
    }
 
    public static class GT_RecipeMapAssembler extends GT_RecipeMap {
        
        public GT_RecipeMapAssembler(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
        
    }
    
    public static class GT_RecipeMapDistillationTower extends GT_RecipeMap {
        
        public GT_RecipeMapDistillationTower() {
            super(new HashSet<GT_MachineRecipe>(50));
            setNames("gt.recipe.distillationtower", "Distillation Tower");
            setNEIGUIPathBasic("DistillationTower");
            setInputSlots(2);
            setOutputSlots(4);
            setMinimalInputItems(0);
            setMinimalInputFluids(1);
        }
        
    }
    
    public static class GT_RecipeMapFluidCanner extends GT_RecipeMap {
        
        public GT_RecipeMapFluidCanner(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
        
    }
    
    public static class GT_RecipeMapFuel extends GT_RecipeMap {
        
        public GT_RecipeMapFuel(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
        
    }
    
    public static class GT_RecipeMapFurnace extends GT_RecipeMap {
        
        public GT_RecipeMapFurnace(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
        
    }
    
    public static class GT_RecipeMapLargeBoilerFakeFuels extends GT_RecipeMap {
        
        public GT_RecipeMapLargeBoilerFakeFuels() {
            super (new HashSet<GT_MachineRecipe>(30));
            setNames("gt.recipe.largeboilerfakefuels", "Large Boiler");
            setNEIGUIPathBasic("Default");
            setOutputSlots(0);
        }
        
    }
    
    public static class GT_RecipeMapLargeChemicalReactor extends GT_RecipeMap {
        
        private static final int INPUT_COUNT = 2;
    	private static final int OUTPUT_COUNT = 2;
        
        public GT_RecipeMapLargeChemicalReactor() {
            super(new HashSet<GT_MachineRecipe>(200));
            setNames("gt.recipe.largechemicalreactor", "Large Chemical Reactor");
            setNEIGUIPathBasic("Default");
            setInputSlots(INPUT_COUNT);
            setOutputSlots(OUTPUT_COUNT);
            setMinimalInputItems(0);
            setMinimalTotalInputs(1);
        }
        
    }
    
    public static class GT_RecipeMapMacerator extends GT_RecipeMap {
        
        public GT_RecipeMapMacerator(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
        
    }
    
    public static class GT_RecipeMapMicrowave extends GT_RecipeMap {
        
        public GT_RecipeMapMicrowave(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
        
    }

    public static class GT_RecipeMapPrinter extends GT_RecipeMap {
        
        public GT_RecipeMapPrinter(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
        
    }
    
    public static class GT_RecipeMapRecycler extends GT_RecipeMap {

        public GT_RecipeMapRecycler(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
    }  
 
    public static class GT_RecipeMapUnboxinator extends GT_RecipeMap {
        
        public GT_RecipeMapUnboxinator(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
        
    }
    
}