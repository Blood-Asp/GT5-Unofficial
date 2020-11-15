package gregtech.api.recipes;

import codechicken.nei.PositionedStack;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Dyes;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.SubTag;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.interfaces.tileentity.IHasWorldObjectAndCoords;
import gregtech.api.objects.GT_ItemStack;
import gregtech.api.objects.ItemData;
import gregtech.api.objects.MaterialStack;
import gregtech.api.util.GT_LanguageManager;
import gregtech.api.util.GT_Log;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;
import gregtech.nei.GT_NEI_DefaultHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

import static gregtech.api.enums.GT_Values.L;
import static gregtech.api.enums.GT_Values.W;



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
    public static final GT_RecipeMap sMicrowaveRecipes = new GT_RecipeMapMicrowave(new HashSet<GT_MachineRecipe>(0)).setNames("gt.recipe.microwave", "Microwave").setNEIName("smelting").setNEIGUIPathBasic("E_Furnace").setNEIAllowed(false);
    
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
    public static final GT_RecipeMap sPressRecipes = new GT_RecipeMapFormingPress(new HashSet<GT_MachineRecipe>(100)).setNames("gt.recipe.press", "Forming Press").setNEIGUIPathBasic("Press").setInputSlots(2).setMinimalInputItems(2);
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
    public static final GT_RecipeMap sCutterRecipes = new GT_RecipeMap(new HashSet<GT_MachineRecipe>(200)).setNames("gt.recipe.cuttingsaw", "Cutting Saw").setNEIGUIPathBasic("Cutter").setOutputSlots(2).setMinimalInputFluids(1);
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
     * HashMap of Recipes based on their Items
     */
    public final Map<GT_ItemStack, Collection<GT_MachineRecipe>> mRecipeItemMap = new HashMap<>(1000);
    /**
     * HashMap of Recipes based on their Fluids
     */
    public final Map<Fluid, Collection<GT_MachineRecipe>> mRecipeFluidMap = new HashMap<>(200);

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
        GregTech_API.sFluidMappings.add(mRecipeFluidMap);
        GregTech_API.sItemStackMappings.add(mRecipeItemMap);
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
 
    public GT_MachineRecipe addRecipe(boolean aOptimize, ItemStack[] aInputs, ItemStack[] aOutputs, Object aSpecial, int[] aOutputChances, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
        GT_RecipeInput[] tInputs = GT_MachineRecipe.wrapInputs(aInputs);
        GT_RecipeOutput[] tOutputs = GT_MachineRecipe.wrapOutputs(aOutputs);
        if (aOutputChances != null) {
            for (int i = 0; i < aOutputChances.length && i < tOutputs.length; i++) {
                if (aOutputs[i] != null) {
                    tOutputs[i] = new GT_RecipeOutput(aOutputs[i], aOutputChances[i] / 10000f);
                }
            }
            for (int i = aOutputChances.length; i < tOutputs.length; i++) {
                if (aOutputs[i] != null) {
                    tOutputs[i] = new GT_RecipeOutput(aOutputs[i]);
                }
            }
        }
        GT_MachineRecipe tRecipe = new GT_MachineRecipe(tInputs, tOutputs, aFluidInputs, aFluidOutputs);
        tRecipe.setSpecialItems(aSpecial);
        tRecipe.setDuration(aDuration);
        tRecipe.setEUt(aEUt);
        tRecipe.setSpecialValue(aSpecialValue);
        if (aOptimize) {
            tRecipe.optimize();
        }
        return addRecipe(tRecipe);
    }

    public GT_MachineRecipe addRecipe(int[] aOutputChances, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
        return addRecipe(false, new ItemStack[0], new ItemStack[0], null, aOutputChances, aFluidInputs, aFluidOutputs, aDuration, aEUt, aSpecialValue);
    }
    
    public GT_MachineRecipe addRecipe(boolean aOptimize, ItemStack[] aInputs, ItemStack[] aOutputs, Object aSpecial, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
        return addRecipe(aOptimize, aInputs, aOutputs, aSpecial, null, aFluidInputs, aFluidOutputs, aDuration, aEUt, aSpecialValue);
    }

    public GT_MachineRecipe addRecipe(GT_MachineRecipe aRecipe) {
        return addRecipe(aRecipe, true, false, false);
    }

    protected GT_MachineRecipe addRecipe(GT_MachineRecipe aRecipe, boolean aCheckForCollisions, boolean aFakeRecipe, boolean aHidden) {
        aRecipe.mHidden = aHidden;
        aRecipe.mFakeRecipe = aFakeRecipe;
        if (aRecipe.mFluidInputs.length < mMinimalInputFluids && aRecipe.mInputs.length < mMinimalInputItems) {
            return null;
        }
        if (aCheckForCollisions) {
            // Allowing ore dictionary inputs complicates checking for collision, but try looking for the first possible item stack for each input.
            ItemStack[] tInputs = new ItemStack[aRecipe.mInputs.length];
            for (int i = 0; i < tInputs.length; i++) {
                tInputs[i] = aRecipe.mInputs[i].getInputStacks().get(0);
            }
            if (findAnyRecipe(aRecipe.mFluidInputs, null, tInputs) != null) {
                return null;
            }
        }
        return add(aRecipe);
    }

    /**
     * Only used for fake Recipe Handlers to show something in NEI, do not use
     * this for adding actual Recipes! findRecipe wont find fake Recipes,
     * containsInput WILL find fake Recipes
     */
    public GT_MachineRecipe addFakeRecipe(boolean aCheckForCollisions, GT_MachineRecipe aRecipe) {
        return addRecipe(aRecipe, aCheckForCollisions, true, false);
    }

    public GT_MachineRecipe addFakeRecipe(boolean aCheckForCollisions, GT_MachineRecipe aRecipe, boolean hidden) {
        return addRecipe(aRecipe, aCheckForCollisions, true, hidden);
    }

    public GT_MachineRecipe addFakeRecipe(boolean aOptimize, ItemStack[] aInputs, ItemStack[] aOutputs, Object aSpecial, int[] aOutputChances, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
        return addRecipe(aOptimize, aInputs, aOutputs, aSpecial, aOutputChances, aFluidInputs, aFluidOutputs, aDuration, aEUt, aSpecialValue).setFakeRecipe(true);
    }    
    
    public GT_MachineRecipe addFakeRecipe(boolean aOptimize, ItemStack[] aInputs, ItemStack[] aOutputs, Object aSpecial, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
        return addFakeRecipe(aOptimize, aInputs, aOutputs, aSpecial, null, aFluidInputs, aFluidOutputs, aDuration, aEUt, aSpecialValue);
    }

    public GT_MachineRecipe addFakeRecipe(int[] aOutputChances, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
        return addFakeRecipe(false, new ItemStack[0], new ItemStack[0], null, aOutputChances, aFluidInputs, aFluidOutputs, aDuration, aEUt, aSpecialValue);
    }

    public GT_MachineRecipe add(GT_MachineRecipe aRecipe) {
        mRecipeList.add(aRecipe);
        addToFluidMap(aRecipe);
        return addToItemMap(aRecipe);
    }
    
    public void addAll(Collection<GT_MachineRecipe> aRecipeList) {
        for (GT_MachineRecipe tRecipe : aRecipeList) {
            addRecipe(tRecipe);
        }
    }
    
    protected GT_MachineRecipe addToFluidMap(GT_MachineRecipe aRecipe) {
        for (FluidStack aFluid : aRecipe.mFluidInputs) {
            if (aFluid != null) {
                Collection<GT_MachineRecipe> tList = mRecipeFluidMap.get(aFluid.getFluid());
                if (tList == null) {
                    tList = new HashSet<>(1);
                    mRecipeFluidMap.put(aFluid.getFluid(), tList);
                }
                tList.add(aRecipe);
            }
        }
        return aRecipe;
    }
    
    protected GT_MachineRecipe addToItemMap(GT_MachineRecipe aRecipe) {
        for (GT_RecipeInput aInput : aRecipe.mInputs) {
            for (ItemStack aStack : aInput.getInputStacks()) {
                if (aStack != null) {
                    GT_ItemStack tStack = new GT_ItemStack(aStack);
                    Collection<GT_MachineRecipe> tList = mRecipeItemMap.get(tStack);
                    if (tList == null) {
                        tList = new HashSet<>(1);
                        mRecipeItemMap.put(tStack, tList);
                    }
                    tList.add(aRecipe);
                }
            }
        }
        return aRecipe;
    }

    public GT_MachineRecipe findRecipe(IHasWorldObjectAndCoords aTileEntity, GT_MachineRecipe aRecipe, long aVoltage, FluidStack[] aFluids, ItemStack aSpecialSlot, ItemStack... aInputs) {
        return findRecipe(aTileEntity, aRecipe, false, aVoltage, aFluids, aSpecialSlot, aInputs);
    }
    
    /**
     * finds a Recipe matching the aFluid and ItemStack Inputs.
     *
     * @param aTileEntity an Object representing the current coordinates of the
     * executing Block/Entity/Whatever. This may be null, especially during
     * Startup.
     * @param aRecipe in case this is != null it will try to use this Recipe
     * first when looking things up.
     * @param aIgnoreCounts if set to false will only return recipes that can be
     * executed at least once with the provided input
     * @param aVoltage Voltage of the Machine or Long.MAX_VALUE if it has no
     * Voltage
     * @param aFluids the Fluid Inputs
     * @param aSpecialSlot the content of the Special Slot, the regular Manager
     * doesn't do anything with this, but some custom ones do.
     * @param aInputs the Item Inputs
     * @return the Recipe it has found or null for no matching Recipe
     */
    public GT_MachineRecipe findRecipe(IHasWorldObjectAndCoords aTileEntity, GT_MachineRecipe aRecipe, boolean aIgnoreCounts, long aVoltage, FluidStack[] aFluids, ItemStack aSpecialSlot, ItemStack... aInputs) {
        // No Recipes? Well, nothing to be found then.
        if (mRecipeList.isEmpty()) {
            return null;
        }

        // Some Recipe Classes require a certain amount of Inputs of certain kinds. Like "at least 1 Fluid + 1 Stack" or "at least 2 Stacks" before they start searching for Recipes.
        // This improves Performance massively, especially if people leave things like Circuits, Molds or Shapes in their Machines to select Sub Recipes.
        int tFluidCount = 0;
        int tItemCount = 0;
        if (GregTech_API.sPostloadFinished) {
            if (mMinimalInputFluids > 0 || mMinimalTotalInputs > 0) {
                if (aFluids == null) {
                    return null;
                }
                for (FluidStack aFluid : aFluids) {
                    if (aFluid != null) {
                        tFluidCount++;
                    }
                }
                if (tFluidCount < mMinimalInputFluids) {
                    return null;
                }
            }
            if (mMinimalInputItems > 0 || mMinimalTotalInputs > 0) {
                if (aInputs == null) {
                    return null;
                }
                for (ItemStack aInput : aInputs) {
                    if (aInput != null) {
                        tItemCount++;
                    }
                }
                if (tItemCount < mMinimalInputItems) {
                    return null;
                }
            }
            if (tItemCount + tFluidCount < mMinimalTotalInputs) {
                return null;
            }
        }

        // Check the Recipe which has been used last time in order to not have to search for it again, if possible.
        if (aRecipe != null) {
            if (!aRecipe.mFakeRecipe && aRecipe.mCanBeBuffered && aRecipe.isRecipeInputEqual(false, aIgnoreCounts, aFluids, aInputs)) {
                if (aRecipe.isEnabled() && aVoltage * mAmperage >= aRecipe.mEUt) {
                    return aRecipe;
                }
                return null;
            }
        }

        // Now look for the Recipes inside the Item HashMaps, but only when the Recipes usually have Items.
        if (mInputSlots > 0 && aInputs != null) {
            for (ItemStack tStack : aInputs) {
                if (tStack != null) {
                    Collection<GT_MachineRecipe> tRecipes = mRecipeItemMap.get(new GT_ItemStack(tStack));
                    if (tRecipes != null) {
                        for (GT_MachineRecipe tRecipe : tRecipes) {
                            if (!tRecipe.mFakeRecipe && tRecipe.isRecipeInputEqual(false, aIgnoreCounts, aFluids, aInputs)) {
                                if (tRecipe.isEnabled() && aVoltage * mAmperage >= tRecipe.mEUt) {
                                    return tRecipe;
                                }
                                return null;
                            }
                        }
                    }
                    tRecipes = mRecipeItemMap.get(new GT_ItemStack(GT_Utility.copyMetaData(W, tStack)));
                    if (tRecipes != null) {
                        for (GT_MachineRecipe tRecipe : tRecipes) {
                            if (!tRecipe.mFakeRecipe && tRecipe.isRecipeInputEqual(false, aIgnoreCounts, aFluids, aInputs)) {
                                if (tRecipe.isEnabled() && aVoltage * mAmperage >= tRecipe.mEUt) {
                                    return tRecipe;
                                }
                                return null;
                            }
                        }
                    }
                }
            }
        }

        // If the minimal Amount of Items for the Recipe is 0, then it could be a Fluid-Only Recipe, so check that Map too.
        if (mMinimalInputItems == 0 && aFluids != null) {
            for (FluidStack aFluid : aFluids) {
                if (aFluid != null) {
                    Collection<GT_MachineRecipe> tRecipes = mRecipeFluidMap.get(aFluid.getFluid());
                    if (tRecipes != null) {
                        for (GT_MachineRecipe tRecipe : tRecipes) {
                            if (!tRecipe.mFakeRecipe && tRecipe.isRecipeInputEqual(false, aIgnoreCounts, aFluids, aInputs)) {
                                if (tRecipe.isEnabled() && aVoltage * mAmperage >= tRecipe.mEUt) {
                                    return tRecipe;
                                }
                                return null;
                            }
                        }
                    }
                }
            }
        }

        // And nothing has been found.
        return null;
    }

    /**
     * finds a Recipe matching the aFluid and ItemStack Inputs.
     * Special version to be used when checking for collisions, which doesn't bother checking voltage or cached recipes, but allows returning disabled recipes.
     * 
     * @param aFluids the Fluid Inputs
     * @param aSpecialSlot the content of the Special Slot, the regular Manager
     * doesn't do anything with this, but some custom ones do.
     * @param aInputs the Item Inputs
     * @return the Recipe it has found or null for no matching Recipe
     */
    protected GT_MachineRecipe findAnyRecipe(FluidStack[] aFluids, ItemStack aSpecialSlot, ItemStack... aInputs) {
        // No Recipes? Well, nothing to be found then.
        if (mRecipeList.isEmpty()) {
            return null;
        }

        // Some Recipe Classes require a certain amount of Inputs of certain kinds. Like "at least 1 Fluid + 1 Stack" or "at least 2 Stacks" before they start searching for Recipes.
        // This improves Performance massively, especially if people leave things like Circuits, Molds or Shapes in their Machines to select Sub Recipes.
        int tFluidCount = 0;
        int tItemCount = 0;
        if (GregTech_API.sPostloadFinished) {
            if (mMinimalInputFluids > 0 || mMinimalTotalInputs > 0) {
                if (aFluids == null) {
                    return null;
                }
                for (FluidStack aFluid : aFluids) {
                    if (aFluid != null) {
                        tFluidCount++;
                    }
                }
                if (tFluidCount < mMinimalInputFluids) {
                    return null;
                }
            }
            if (mMinimalInputItems > 0 || mMinimalTotalInputs > 0) {
                if (aInputs == null) {
                    return null;
                }
                for (ItemStack aInput : aInputs) {
                    if (aInput != null) {
                        tItemCount++;
                    }
                }
                if (tItemCount < mMinimalInputItems) {
                    return null;
                }
            }
            if (tItemCount + tFluidCount < mMinimalTotalInputs) {
                return null;
            }
        }

        // Now look for the Recipes inside the Item HashMaps, but only when the Recipes usually have Items.
        if (mInputSlots > 0 && aInputs != null) {
            for (ItemStack tStack : aInputs) {
                if (tStack != null) {
                    Collection<GT_MachineRecipe> tRecipes = mRecipeItemMap.get(new GT_ItemStack(tStack));
                    if (tRecipes != null) {
                        for (GT_MachineRecipe tRecipe : tRecipes) {
                            if (!tRecipe.mFakeRecipe && tRecipe.isRecipeInputEqual(false, true, aFluids, aInputs)) {
                                return tRecipe;
                            }
                        }
                    }
                    tRecipes = mRecipeItemMap.get(new GT_ItemStack(GT_Utility.copyMetaData(W, tStack)));
                    if (tRecipes != null) {
                        for (GT_MachineRecipe tRecipe : tRecipes) {
                            if (!tRecipe.mFakeRecipe && tRecipe.isRecipeInputEqual(false, true, aFluids, aInputs)) {
                                return tRecipe;
                            }
                        }
                    }
                }
            }
        }

        // If the minimal Amount of Items for the Recipe is 0, then it could be a Fluid-Only Recipe, so check that Map too.
        if (mMinimalInputItems == 0 && aFluids != null) {
            for (FluidStack aFluid : aFluids) {
                if (aFluid != null) {
                    Collection<GT_MachineRecipe> tRecipes = mRecipeFluidMap.get(aFluid.getFluid());
                    if (tRecipes != null) {
                        for (GT_MachineRecipe tRecipe : tRecipes) {
                            if (!tRecipe.mFakeRecipe && tRecipe.isRecipeInputEqual(false, true, aFluids, aInputs)) {
                                return tRecipe;
                            }
                        }
                    }
                }
            }
        }

        // And nothing has been found.
        return null;
    }

    /**
     * @return if this Item is a valid Input for any for the Recipes
     */
    public boolean containsInput(ItemStack aStack) {
        return aStack != null && (mRecipeItemMap.containsKey(new GT_ItemStack(aStack)) || mRecipeItemMap.containsKey(new GT_ItemStack(GT_Utility.copyMetaData(W, aStack))));
    }

    /**
     * @return if this Fluid is a valid Input for any for the Recipes
     */
    public boolean containsInput(FluidStack aFluid) {
        return aFluid != null && containsInput(aFluid.getFluid());
    }

    /**
     * @return if this Fluid is a valid Input for any for the Recipes
     */
    public boolean containsInput(Fluid aFluid) {
        return aFluid != null && mRecipeFluidMap.containsKey(aFluid);
    }

    public static void reInitAll() {
        GT_Log.out.println("GT_Mod: Re-Unificating Recipes.");
        for (GT_RecipeMap tMapEntry : GT_RecipeMap.sMappings) {
            tMapEntry.reInit();
        }
    }

    public void reInit() {
        Map<GT_ItemStack, Collection<GT_MachineRecipe>> tMap = mRecipeItemMap;
        if (tMap != null) {
            tMap.clear();
        }
        for (GT_MachineRecipe tRecipe : mRecipeList) {
            if (tMap != null) {
                addToItemMap(tRecipe);
            }
        }
    }

    public static abstract class GT_RecipeMap_NonGT extends GT_RecipeMap {
        
        public GT_RecipeMap_NonGT(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
        
        @Override
        public boolean containsInput(ItemStack aStack) {
            return false;
        }

        @Override
        public boolean containsInput(FluidStack aFluid) {
            return false;
        }

        @Override
        public boolean containsInput(Fluid aFluid) {
            return false;
        }

    
        @Override
        public GT_MachineRecipe addRecipe(GT_MachineRecipe aRecipe) {
            return null;
        }

        @Override
        public GT_MachineRecipe addFakeRecipe(boolean aCheckForCollisions, GT_MachineRecipe aRecipe) {
            return null;
        }

        @Override
        public GT_MachineRecipe add(GT_MachineRecipe aRecipe) {
            return null;
        }

        @Override
        public final void reInit() {/**/}

        @Override
        protected GT_MachineRecipe addToItemMap(GT_MachineRecipe aRecipe) {
            return null;
        }
    }
    
    public static class GT_RecipeMapAssembler extends GT_RecipeMap {
        
        public GT_RecipeMapAssembler(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
        
        @Override
        public GT_MachineRecipe findRecipe(IHasWorldObjectAndCoords aTileEntity, GT_MachineRecipe aRecipe, boolean aIgnoreCounts, long aVoltage, FluidStack[] aFluids, ItemStack aSpecialSlot, ItemStack... aInputs) {
            GT_MachineRecipe rRecipe = super.findRecipe(aTileEntity, aRecipe, aIgnoreCounts, aVoltage, aFluids, aSpecialSlot, aInputs);
            if (aInputs == null || aInputs.length <= 0 || aInputs[0] == null || rRecipe == null || !GregTech_API.sPostloadFinished) {
                return rRecipe;
            }
            for (ItemStack aInput : aInputs) {
                if (ItemList.Paper_Printed_Pages.isStackEqual(aInput, false, true)) {
                    rRecipe = rRecipe.copy();
                    rRecipe.mCanBeBuffered = false;
                    ItemStack tStack = rRecipe.mOutputs[0].getShownOutput();
                    tStack.setTagCompound(aInput.getTagCompound());
                    rRecipe.mOutputs[0] = new GT_RecipeOutput(tStack);
                }
            }
            return rRecipe;
        }
    }
    
    public static class GT_RecipeMapDistillationTower extends GT_RecipeMap {
        
    	private static final int FLUID_OUTPUT_COUNT = 11;
    	private static final int ROW_SIZE = 3;
        
        public GT_RecipeMapDistillationTower() {
            super(new HashSet<GT_MachineRecipe>(50));
            setNames("gt.recipe.distillationtower", "Distillation Tower");
            setNEIGUIPathBasic("DistillationTower");
            setInputSlots(2);
            setOutputSlots(4);
            setMinimalInputItems(0);
            setMinimalInputFluids(1);
        }
        
        @Override
        public GT_MachineRecipe addRecipe(boolean aOptimize, ItemStack[] aInputs, ItemStack[] aOutputs, Object aSpecial, int[] aOutputChances, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
            return addRecipe(new GT_Recipe_DistillationTower(aInputs, aOutputs, aFluidInputs, aFluidOutputs).setDuration(aDuration).setEUt(aEUt).setSpecialValue(aSpecialValue));
        }

        @Override
        public GT_MachineRecipe addRecipe(boolean aOptimize, ItemStack[] aInputs, ItemStack[] aOutputs, Object aSpecial, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
            return addRecipe(aOptimize, aInputs, aOutputs, aSpecial, null, aFluidInputs, aFluidOutputs, aDuration, aEUt, aSpecialValue);
        }

        @Override
        public GT_MachineRecipe addRecipe(GT_MachineRecipe aRecipe) {
            return super.addRecipe(new GT_Recipe_DistillationTower(aRecipe));
        }
        
        private static class GT_Recipe_DistillationTower extends GT_MachineRecipe {

            protected GT_Recipe_DistillationTower(ItemStack[] aInputs, ItemStack[] aOutputs, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs) {
                super(aInputs, aOutputs, aFluidInputs, aFluidOutputs);
            }

            protected GT_Recipe_DistillationTower(GT_MachineRecipe aRecipe) {
                super(aRecipe.mInputs, aRecipe.mOutputs, aRecipe.mFluidInputs, aRecipe.mFluidOutputs);
                this.mCanBeBuffered = aRecipe.mCanBeBuffered;
                this.mDuration = aRecipe.mDuration;
                this.mEUt = aRecipe.mEUt;
                this.mEnableCondition = aRecipe.mEnableCondition;
                this.mFakeRecipe = aRecipe.mFakeRecipe;
                this.mHidden = aRecipe.mHidden;
                this.mInvertCondition = aRecipe.mInvertCondition;
                this.mNeedsEmptyOutput = aRecipe.mNeedsEmptyOutput;
                this.mSpecialItems = aRecipe.mSpecialItems;
                this.mSpecialValue = aRecipe.mSpecialValue;
            }
            
            @Override
            public ArrayList<PositionedStack> getInputPositionedStacks() {
                ArrayList<PositionedStack> inputStacks = new ArrayList<>(1);

                if (this.mFluidInputs.length > 0 && this.mFluidInputs[0] != null) {
                    inputStacks.add(new GT_NEI_DefaultHandler.FixedPositionedStack(GT_Utility.getFluidDisplayStack(this.mFluidInputs[0], true), 48, 52));
                }
                return inputStacks;
            }

            @Override
            public ArrayList<PositionedStack> getOutputPositionedStacks() {
                int fluidLimit = Math.min(mFluidOutputs.length, FLUID_OUTPUT_COUNT);
                ArrayList<PositionedStack> outputStacks = new ArrayList<>(1 + fluidLimit);

                if (this.mOutputs.length > 0 && this.mOutputs[0] != null) {
                    outputStacks.add(new GT_NEI_DefaultHandler.FixedPositionedStack(this.mOutputs[0].getShownOutput(), 102, 52));
                }

                for (int i = 0; i < fluidLimit; i++) {
                    int x = 102 + ((i + 1) % ROW_SIZE) * 18;
                    int y = 52 - ((i + 1) / ROW_SIZE) * 18;
                    outputStacks.add(new GT_NEI_DefaultHandler.FixedPositionedStack(GT_Utility.getFluidDisplayStack(this.mFluidOutputs[i], true), x, y));
                }

                return outputStacks;
            }

        }
    }
    
    public static class GT_RecipeMapFluidCanner extends GT_RecipeMap {
        
        public GT_RecipeMapFluidCanner(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
        
        @Override
        public GT_MachineRecipe findRecipe(IHasWorldObjectAndCoords aTileEntity, GT_MachineRecipe aRecipe, boolean aIgnoreCounts, long aVoltage, FluidStack[] aFluids, ItemStack aSpecialSlot, ItemStack... aInputs) {
            GT_MachineRecipe rRecipe = super.findRecipe(aTileEntity, aRecipe, aIgnoreCounts, aVoltage, aFluids, aSpecialSlot, aInputs);
            if (aInputs == null || aInputs.length <= 0 || aInputs[0] == null || rRecipe != null || !GregTech_API.sPostloadFinished) {
                return rRecipe;
            }
            if (aFluids != null && aFluids.length > 0 && aFluids[0] != null) {
                ItemStack tOutput = GT_Utility.fillFluidContainer(aFluids[0], aInputs[0], false, true);
                FluidStack tFluid = GT_Utility.getFluidForFilledItem(tOutput, true);
                if (tFluid != null) {
                    rRecipe = new GT_MachineRecipe(new ItemStack[]{GT_Utility.copyAmount(1, aInputs[0])}, new ItemStack[]{tOutput}, new FluidStack[]{tFluid}, null).setDuration(Math.max(tFluid.amount / 64, 16)).setEUt(1);
                }
            }
            if (rRecipe == null) {
                FluidStack tFluid = GT_Utility.getFluidForFilledItem(aInputs[0], true);
                if (tFluid != null) {
                    rRecipe = new GT_MachineRecipe(new ItemStack[]{GT_Utility.copyAmount(1, aInputs[0])}, new ItemStack[]{GT_Utility.getContainerItem(aInputs[0], true)}, null, new FluidStack[]{tFluid}).setDuration(Math.max(tFluid.amount / 64, 16)).setEUt(1);
                }
            }
            if (rRecipe != null) {
                rRecipe.mCanBeBuffered = false;
            }
            return rRecipe;
        }

        @Override
        public boolean containsInput(ItemStack aStack) {
            return aStack != null && (super.containsInput(aStack) || (aStack.getItem() instanceof IFluidContainerItem && ((IFluidContainerItem) aStack.getItem()).getCapacity(aStack) > 0));
        }

        @Override
        public boolean containsInput(FluidStack aFluid) {
            return true;
        }

        @Override
        public boolean containsInput(Fluid aFluid) {
            return true;
        }
    }
    
    public static class GT_RecipeMapFormingPress extends GT_RecipeMap {
        
        public GT_RecipeMapFormingPress(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
        
        @Override
        public GT_MachineRecipe findRecipe(IHasWorldObjectAndCoords aTileEntity, GT_MachineRecipe aRecipe, boolean aIgnoreCounts, long aVoltage, FluidStack[] aFluids, ItemStack aSpecialSlot, ItemStack... aInputs) {
            GT_MachineRecipe rRecipe = super.findRecipe(aTileEntity, aRecipe, aIgnoreCounts, aVoltage, aFluids, aSpecialSlot, aInputs);
            if (aInputs == null || aInputs.length < 2 || aInputs[0] == null || aInputs[1] == null || !GregTech_API.sPostloadFinished) {
                return rRecipe;
            }
            if (rRecipe == null) {
                if (ItemList.Shape_Mold_Name.isStackEqual(aInputs[0], false, true)) {
                    ItemStack tOutput = GT_Utility.copyAmount(1, aInputs[1]);
                    tOutput.setStackDisplayName(aInputs[0].getDisplayName());
                    rRecipe = new GT_MachineRecipe(new ItemStack[]{ItemList.Shape_Mold_Name.get(0), GT_Utility.copyAmount(1, aInputs[1])}, new ItemStack[]{tOutput}, null, null).setDuration(128).setEUt(8);
                    rRecipe.mCanBeBuffered = false;
                    return rRecipe;
                }
                if (ItemList.Shape_Mold_Name.isStackEqual(aInputs[1], false, true)) {
                    ItemStack tOutput = GT_Utility.copyAmount(1, aInputs[0]);
                    tOutput.setStackDisplayName(aInputs[1].getDisplayName());
                    rRecipe = new GT_MachineRecipe(new ItemStack[]{ItemList.Shape_Mold_Name.get(0), GT_Utility.copyAmount(1, aInputs[0])}, new ItemStack[]{tOutput}, null, null).setDuration(128).setEUt(8);
                    rRecipe.mCanBeBuffered = false;
                    return rRecipe;
                }
                return null;
            }
            for (ItemStack aMold : aInputs) {
                if (ItemList.Shape_Mold_Credit.isStackEqual(aMold, false, true)) {
                    NBTTagCompound tNBT = aMold.getTagCompound();
                    if (tNBT == null) {
                        tNBT = new NBTTagCompound();
                    }
                    if (!tNBT.hasKey("credit_security_id")) {
                        tNBT.setLong("credit_security_id", System.nanoTime());
                    }
                    aMold.setTagCompound(tNBT);

                    rRecipe = rRecipe.copy();
                    rRecipe.mCanBeBuffered = false;
                    ItemStack tStack = rRecipe.mOutputs[0].getShownOutput();
                    tStack.setTagCompound(tNBT);
                    rRecipe.mOutputs[0] = new GT_RecipeOutput(tStack);
                    return rRecipe;
                }
            }
            return rRecipe;
        }
        
    }
    
    public static class GT_RecipeMapFuel extends GT_RecipeMap {
        
        public GT_RecipeMapFuel(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
        
        public GT_MachineRecipe addFuel(ItemStack aInput, ItemStack aOutput, int aFuelValueInEU) {
            return addFuel(aInput, aOutput, null, null, 10000, aFuelValueInEU);
        }

        public GT_MachineRecipe addFuel(ItemStack aInput, ItemStack aOutput, int aChance, int aFuelValueInEU) {
            return addFuel(aInput, aOutput, null, null, aChance, aFuelValueInEU);
        }

        public GT_MachineRecipe addFuel(FluidStack aFluidInput, FluidStack aFluidOutput, int aFuelValueInEU) {
            return addFuel(null, null, aFluidInput, aFluidOutput, 10000, aFuelValueInEU);
        }

        public GT_MachineRecipe addFuel(ItemStack aInput, ItemStack aOutput, FluidStack aFluidInput, FluidStack aFluidOutput, int aFuelValueInEU) {
            return addFuel(aInput, aOutput, aFluidInput, aFluidOutput, 10000, aFuelValueInEU);
        }

        public GT_MachineRecipe addFuel(ItemStack aInput, ItemStack aOutput, FluidStack aFluidInput, FluidStack aFluidOutput, int aChance, int aFuelValueInEU) {
            return addRecipe(true, new ItemStack[]{aInput}, new ItemStack[]{aOutput}, null, new int[]{aChance}, new FluidStack[]{aFluidInput}, new FluidStack[]{aFluidOutput}, 0, 0, aFuelValueInEU);
        }
    }
    
    public static class GT_RecipeMapFurnace extends GT_RecipeMap_NonGT {
        
        public GT_RecipeMapFurnace(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
        
        @Override
        public GT_MachineRecipe findRecipe(IHasWorldObjectAndCoords aTileEntity, GT_MachineRecipe aRecipe, boolean aIgnoreCounts, long aVoltage, FluidStack[] aFluids, ItemStack aSpecialSlot, ItemStack... aInputs) {
            if (aInputs == null || aInputs.length <= 0 || aInputs[0] == null) {
                return null;
            }
            if (aRecipe != null && aRecipe.isRecipeInputEqual(false, true, aFluids, aInputs)) {
                return aRecipe;
            }
            ItemStack tOutput = GT_ModHandler.getSmeltingOutput(aInputs[0], false, null);
            if (tOutput == null) {
                return null;
            }
            return new GT_MachineRecipe(new ItemStack[]{GT_Utility.copyAmount(1, aInputs[0])}, new ItemStack[]{tOutput}, new FluidStack[0], new FluidStack[0]).setDuration(128).setEUt(4);
        }

        @Override
        public boolean containsInput(ItemStack aStack) {
            return GT_ModHandler.getSmeltingOutput(aStack, false, null) != null;
        }
    }
    
    public static class GT_RecipeMapLargeBoilerFakeFuels extends GT_RecipeMap {
        
        public GT_RecipeMapLargeBoilerFakeFuels() {
            super (new HashSet<GT_MachineRecipe>(30));
            setNames("gt.recipe.largeboilerfakefuels", "Large Boiler");
            setNEIGUIPathBasic("Default");
            setOutputSlots(0);
            GT_MachineRecipe explanatoryRecipe = new GT_MachineRecipe(new ItemStack[]{}, new ItemStack[]{}, null, null).setDuration(1).setEUt(1).setSpecialValue(1);
            explanatoryRecipe.setNeiDesc("Not all solid fuels are listed.", "Any item that burns in a", "vanilla furnace will burn in", "a Large Boiler.");
            addRecipe(explanatoryRecipe);
        }
        
        public GT_MachineRecipe addDenseLiquidRecipe(GT_MachineRecipe recipe) {
            return addRecipe(recipe, ((double) recipe.mSpecialValue) / 10);
        }

        public GT_MachineRecipe addDieselRecipe(GT_MachineRecipe recipe) {
            return addRecipe(recipe, ((double) recipe.mSpecialValue) / 40);
        }

        public void addSolidRecipes(ItemStack... itemStacks) {
            for (ItemStack itemStack : itemStacks) {
                addSolidRecipe(itemStack);
            }
        }

        public GT_MachineRecipe addSolidRecipe(ItemStack fuelItemStack) {
            return addRecipe(new GT_MachineRecipe(new ItemStack[]{fuelItemStack}, new ItemStack[]{}, null, null).setDuration(1).setSpecialValue(GT_ModHandler.getFuelValue(fuelItemStack) / 1600), ((double) GT_ModHandler.getFuelValue(fuelItemStack)) / 1600);
        }

        private GT_MachineRecipe addRecipe(GT_MachineRecipe recipe, double baseBurnTime) {
            GT_MachineRecipe tRecipe = recipe.copy();

            double bronzeBurnTime = baseBurnTime * 2;
            double steelBurnTime = baseBurnTime * 1.5;
            double titaniumBurnTime = baseBurnTime * 1.3;
            double tungstensteelBurnTime = baseBurnTime * 1.2;

            tRecipe.setNeiDesc("Burn time in seconds:",
                    String.format("Bronze Boiler: %.4f", bronzeBurnTime),
                    String.format("Steel Boiler: %.4f", steelBurnTime),
                    String.format("Titanium Boiler: %.4f", titaniumBurnTime),
                    String.format("Tungstensteel Boiler: %.4f", tungstensteelBurnTime));
            return super.addRecipe(tRecipe);
        }
    }
    
    public static class GT_RecipeMapLargeChemicalReactor extends GT_RecipeMap {
        
        private static final int INPUT_COUNT = 2;
    	private static final int OUTPUT_COUNT = 2;
    	private static final int FLUID_INPUT_COUNT = 4;
    	private static final int FLUID_OUTPUT_COUNT = 4;
        
        public GT_RecipeMapLargeChemicalReactor() {
            super(new HashSet<GT_MachineRecipe>(200));
            setNames("gt.recipe.largechemicalreactor", "Large Chemical Reactor");
            setNEIGUIPathBasic("Default");
            setInputSlots(INPUT_COUNT);
            setOutputSlots(OUTPUT_COUNT);
            setMinimalInputItems(0);
            setMinimalTotalInputs(1);
        }
        
        @Override
        public GT_MachineRecipe addRecipe(GT_MachineRecipe aRecipe) {
            ArrayList<GT_RecipeInput> adjustedInputs = new ArrayList<>(5);
            ArrayList<GT_RecipeOutput> adjustedOutputs = new ArrayList<>(5);
            ArrayList<FluidStack> adjustedFluidInputs = new ArrayList<>(5);
            ArrayList<FluidStack> adjustedFluidOutputs = new ArrayList<>(5);

            GT_RecipeInput[] tInputs = aRecipe.mInputs;
            if (tInputs == null) {
                tInputs = new GT_RecipeInput[0];
            }
            for (GT_RecipeInput input : tInputs) {
                FluidStack inputFluidContent = FluidContainerRegistry.getFluidForFilledItem(input.getInputStacks().get(0));
                if (inputFluidContent != null) {
                    inputFluidContent.amount *= input.getCount();
                    if (inputFluidContent.getFluid().getName().equals("ic2steam")) {
                        inputFluidContent = GT_ModHandler.getSteam(inputFluidContent.amount);
                    }
                    adjustedFluidInputs.add(inputFluidContent);
                } else {
                    ItemData itemData = GT_OreDictUnificator.getItemData(input.getInputStacks().get(0));
                    if ((itemData == null || !itemData.hasValidPrefixMaterialData()) || itemData.mMaterial.mMaterial != Materials.Empty) {
                        if (itemData != null && itemData.hasValidPrefixMaterialData() && itemData.mPrefix == OrePrefixes.cell) {
                            ItemStack dustStack = itemData.mMaterial.mMaterial.getDust(input.getCount());
                            if (dustStack != null) {
                                adjustedInputs.add(new GT_RecipeInput(dustStack));
                            } else {
                                adjustedInputs.add(input);
                            }
                        } else {
                            adjustedInputs.add(input);
                        }
                    }
                }
            }
            FluidStack[] tFluidInputs = aRecipe.mFluidInputs;
            if (tFluidInputs == null) {
                tFluidInputs = new FluidStack[0];
            }
            adjustedFluidInputs.addAll(Arrays.asList(tFluidInputs));
            tInputs = adjustedInputs.toArray(new GT_RecipeInput[adjustedInputs.size()]);
            tFluidInputs = adjustedFluidInputs.toArray(new FluidStack[adjustedFluidInputs.size()]);

            GT_RecipeOutput[] tOutputs = aRecipe.mOutputs;
            if (tOutputs == null) {
                tOutputs = new GT_RecipeOutput[0];
            }
            for (GT_RecipeOutput output : tOutputs) {
                FluidStack outputFluidContent = FluidContainerRegistry.getFluidForFilledItem(output.getShownOutput());
                if (outputFluidContent != null) {
                    outputFluidContent.amount *= output.getCount();
                    if (outputFluidContent.getFluid().getName().equals("ic2steam")) {
                        outputFluidContent = GT_ModHandler.getSteam(outputFluidContent.amount);
                    }
                    adjustedFluidOutputs.add(outputFluidContent);
                } else {
                    ItemData itemData = GT_OreDictUnificator.getItemData(output.getShownOutput());
                    if ((itemData == null || !itemData.hasValidPrefixMaterialData()) || itemData.mMaterial.mMaterial != Materials.Empty) {
                        adjustedOutputs.add(output);
                    }
                }
            }

            FluidStack[] tFluidOutputs = aRecipe.mFluidOutputs;
            if (tFluidOutputs == null) {
                tFluidOutputs = new FluidStack[0];
            }
            adjustedFluidOutputs.addAll(Arrays.asList(tFluidOutputs));
            tOutputs = adjustedOutputs.toArray(new GT_RecipeOutput[adjustedOutputs.size()]);
            tFluidOutputs = adjustedFluidOutputs.toArray(new FluidStack[adjustedFluidOutputs.size()]);

            GT_MachineRecipe_LargeChemicalReactor rRecipe = (GT_MachineRecipe_LargeChemicalReactor) new GT_MachineRecipe_LargeChemicalReactor(tInputs, tOutputs, tFluidInputs, tFluidOutputs).setDuration(aRecipe.mDuration).setEUt(aRecipe.mEUt).setSpecialValue(aRecipe.mSpecialValue);
            return super.addRecipe(rRecipe);
            
        }
        
        @Override
        public GT_MachineRecipe addRecipe(boolean aOptimize, ItemStack[] aInputs, ItemStack[] aOutputs, Object aSpecial, int[] aOutputChances, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
            ArrayList<ItemStack> adjustedInputs = new ArrayList<>(5);
            ArrayList<ItemStack> adjustedOutputs = new ArrayList<>(5);
            ArrayList<FluidStack> adjustedFluidInputs = new ArrayList<>(5);
            ArrayList<FluidStack> adjustedFluidOutputs = new ArrayList<>(5);

            ItemStack[] tInputs = aInputs;
            if (tInputs == null) {
                tInputs = new ItemStack[0];
            }
            for (ItemStack input : tInputs) {
                FluidStack inputFluidContent = FluidContainerRegistry.getFluidForFilledItem(input);
                if (inputFluidContent != null) {
                    inputFluidContent.amount *= input.stackSize;
                    if (inputFluidContent.getFluid().getName().equals("ic2steam")) {
                        inputFluidContent = GT_ModHandler.getSteam(inputFluidContent.amount);
                    }
                    adjustedFluidInputs.add(inputFluidContent);
                } else {
                    ItemData itemData = GT_OreDictUnificator.getItemData(input);
                    if ((itemData == null || !itemData.hasValidPrefixMaterialData()) || itemData.mMaterial.mMaterial != Materials.Empty) {
                        if (itemData != null && itemData.hasValidPrefixMaterialData() && itemData.mPrefix == OrePrefixes.cell) {
                            ItemStack dustStack = itemData.mMaterial.mMaterial.getDust(input.stackSize);
                            if (dustStack != null) {
                                adjustedInputs.add(dustStack);
                            } else {
                                adjustedInputs.add(input);
                            }
                        } else {
                            adjustedInputs.add(input);
                        }
                    }
                }
            }
            FluidStack[] tFluidInputs = aFluidInputs;
            if (tFluidInputs == null) {
                tFluidInputs = new FluidStack[0];
            }
            adjustedFluidInputs.addAll(Arrays.asList(tFluidInputs));
            tInputs = adjustedInputs.toArray(new ItemStack[adjustedInputs.size()]);
            tFluidInputs = adjustedFluidInputs.toArray(new FluidStack[adjustedFluidInputs.size()]);

            ItemStack[] tOutputs = aOutputs;
            if (tOutputs == null) {
                tOutputs = new ItemStack[0];
            }
            for (ItemStack output : tOutputs) {
                FluidStack outputFluidContent = FluidContainerRegistry.getFluidForFilledItem(output);
                if (outputFluidContent != null) {
                    outputFluidContent.amount *= output.stackSize;
                    if (outputFluidContent.getFluid().getName().equals("ic2steam")) {
                        outputFluidContent = GT_ModHandler.getSteam(outputFluidContent.amount);
                    }
                    adjustedFluidOutputs.add(outputFluidContent);
                } else {
                    ItemData itemData = GT_OreDictUnificator.getItemData(output);
                    if ((itemData == null || !itemData.hasValidPrefixMaterialData()) || itemData.mMaterial.mMaterial != Materials.Empty) {
                        adjustedOutputs.add(output);
                    }
                }
            }

            FluidStack[] tFluidOutputs = aFluidOutputs;
            if (tFluidOutputs == null) {
                tFluidOutputs = new FluidStack[0];
            }
            adjustedFluidOutputs.addAll(Arrays.asList(tFluidOutputs));
            tOutputs = adjustedOutputs.toArray(new ItemStack[adjustedOutputs.size()]);
            tFluidOutputs = adjustedFluidOutputs.toArray(new FluidStack[adjustedFluidOutputs.size()]);

            GT_MachineRecipe_LargeChemicalReactor rRecipe = (GT_MachineRecipe_LargeChemicalReactor) new GT_MachineRecipe_LargeChemicalReactor(tInputs, tOutputs, tFluidInputs, tFluidOutputs).setDuration(aDuration).setEUt(aEUt).setSpecialValue(aSpecialValue);
            if (aOptimize) {
                rRecipe.optimize();
            }
            return super.addRecipe(rRecipe);

        }

        private static class GT_MachineRecipe_LargeChemicalReactor extends GT_MachineRecipe {

            protected GT_MachineRecipe_LargeChemicalReactor(ItemStack[] aInputs, ItemStack[] aOutputs, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs) {
                super(aInputs, aOutputs, aFluidInputs, aFluidOutputs);
            }

            protected GT_MachineRecipe_LargeChemicalReactor(GT_RecipeInput[] aInputs, GT_RecipeOutput[] aOutputs, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs) {
                super(aInputs, aOutputs, aFluidInputs, aFluidOutputs);
            }

            @Override
            public ArrayList<PositionedStack> getInputPositionedStacks() {
                int itemLimit = Math.min(mInputs.length, INPUT_COUNT);
                int fluidLimit = Math.min(mFluidInputs.length, FLUID_INPUT_COUNT);
                ArrayList<PositionedStack> inputStacks = new ArrayList<>(itemLimit + fluidLimit);

                for (int i = 0; i < itemLimit; i++) {
                    inputStacks.add(new GT_NEI_DefaultHandler.FixedPositionedStack(this.mInputs[i].getInputStacks(), 48 - i * 18, 5));
                }

                for (int i = 0; i < fluidLimit; i++) {
                    if (i < 3) {
                        inputStacks.add(new GT_NEI_DefaultHandler.FixedPositionedStack(GT_Utility.getFluidDisplayStack(this.mFluidInputs[i], true), 48 - i * 18, 23));
                    } else {
                        inputStacks.add(new GT_NEI_DefaultHandler.FixedPositionedStack(GT_Utility.getFluidDisplayStack(this.mFluidInputs[i], true), 12, 5));
                    }
                }

                return inputStacks;
            }

            @Override
            public ArrayList<PositionedStack> getOutputPositionedStacks() {
                int itemLimit = Math.min(mOutputs.length, OUTPUT_COUNT);
                int fluidLimit = Math.min(mFluidOutputs.length, FLUID_OUTPUT_COUNT);
                ArrayList<PositionedStack> outputStacks = new ArrayList<PositionedStack>(itemLimit + fluidLimit);

                for (int i = 0; i < itemLimit; i++) {
                    outputStacks.add(new GT_NEI_DefaultHandler.FixedPositionedStack(this.mOutputs[i].getShownOutput().copy(), 102 + i * 18, 5));
                }

                for (int i = 0; i < fluidLimit; i++) {
                    outputStacks.add(new GT_NEI_DefaultHandler.FixedPositionedStack(GT_Utility.getFluidDisplayStack(this.mFluidOutputs[i], true), 102 + i * 18, 23));
                }

                return outputStacks;
            }

        }
    }
    
    public static class GT_RecipeMapMacerator extends GT_RecipeMap {
        
        public GT_RecipeMapMacerator(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }

        @Optional.Method(modid = "Railcraft")
        private List<ItemStack> getRockCrusherOutputs(ItemStack aStack) {
            return mods.railcraft.api.crafting.RailcraftCraftingManager.rockCrusher.getRecipe(GT_Utility.copyAmount(1, aStack)).getRandomizedOuputs();
        }
        
        @Override
        public GT_MachineRecipe findRecipe(IHasWorldObjectAndCoords aTileEntity, GT_MachineRecipe aRecipe, boolean aIgnoreCounts, long aVoltage, FluidStack[] aFluids, ItemStack aSpecialSlot, ItemStack... aInputs) {
            if (aInputs == null || aInputs.length <= 0 || aInputs[0] == null || !GregTech_API.sPostloadFinished)
                return super.findRecipe(aTileEntity, aRecipe, aIgnoreCounts, aVoltage, aFluids, aSpecialSlot, aInputs);
            GT_MachineRecipe rRecipe = super.findRecipe(aTileEntity, aRecipe, aIgnoreCounts, aVoltage, aFluids, aSpecialSlot, aInputs);
            if (rRecipe != null) return rRecipe;

            if (Loader.isModLoaded("Railcraft")) {
                List<ItemStack> tRecipeOutputs = getRockCrusherOutputs(aInputs[0]);
                if (tRecipeOutputs != null) {
                    rRecipe = new GT_MachineRecipe(new ItemStack[]{GT_Utility.copyAmount(1, aInputs[0])}, tRecipeOutputs.toArray(new ItemStack[tRecipeOutputs.size()]), null, null).setDuration(800).setEUt(2);
                    rRecipe.mCanBeBuffered = false;
                    rRecipe.mNeedsEmptyOutput = true;
                    return rRecipe;
                }
            }

            ItemStack tComparedInput = GT_Utility.copy(aInputs[0]);
            ItemStack[] tOutputItems = GT_ModHandler.getMachineOutput(tComparedInput, ic2.api.recipe.Recipes.macerator.getRecipes(), true, new NBTTagCompound(), null, null, null);
            if (GT_Utility.arrayContainsNonNull(tOutputItems)) {
                return new GT_MachineRecipe(new ItemStack[]{GT_Utility.copyAmount(aInputs[0].stackSize - tComparedInput.stackSize, aInputs[0])}, tOutputItems, null, null).setDuration(400).setEUt(2);
            }
            return null;
        }

        @Override
        public boolean containsInput(ItemStack aStack) {
            return super.containsInput(aStack) || GT_Utility.arrayContainsNonNull(GT_ModHandler.getMachineOutput(GT_Utility.copyAmount(64, aStack), ic2.api.recipe.Recipes.macerator.getRecipes(), false, new NBTTagCompound(), null, null, null));
        }
    }
    
    public static class GT_RecipeMapMicrowave extends GT_RecipeMap_NonGT {
        
        public GT_RecipeMapMicrowave(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
        
        @Override
        public GT_MachineRecipe findRecipe(IHasWorldObjectAndCoords aTileEntity, GT_MachineRecipe aRecipe, boolean aIgnoreCounts, long aVoltage, FluidStack[] aFluids, ItemStack aSpecialSlot, ItemStack... aInputs) {
            if (aInputs == null || aInputs.length <= 0 || aInputs[0] == null) {
                return null;
            }
            if (aRecipe != null && aRecipe.isRecipeInputEqual(false, true, aFluids, aInputs)) {
                return aRecipe;
            }
            ItemStack tOutput = GT_ModHandler.getSmeltingOutput(aInputs[0], false, null);

            if (GT_Utility.areStacksEqual(aInputs[0], new ItemStack(Items.book, 1, W))) {
                return new GT_MachineRecipe(new ItemStack[]{GT_Utility.copyAmount(1, aInputs[0])}, new ItemStack[]{GT_Utility.getWrittenBook("Manual_Microwave", ItemList.Book_Written_03.get(1))}, new FluidStack[0], new FluidStack[0]).setDuration(32).setEUt(4);
            }

            // Check Container Item of Input since it is around the Input, then the Input itself, then Container Item of Output and last check the Output itself
            for (ItemStack tStack : new ItemStack[]{GT_Utility.getContainerItem(aInputs[0], true), aInputs[0], GT_Utility.getContainerItem(tOutput, true), tOutput}) {
                if (tStack != null) {
                    if (GT_Utility.areStacksEqual(tStack, new ItemStack(Blocks.netherrack, 1, W), true)
                            || GT_Utility.areStacksEqual(tStack, new ItemStack(Blocks.tnt, 1, W), true)
                            || GT_Utility.areStacksEqual(tStack, new ItemStack(Items.egg, 1, W), true)
                            || GT_Utility.areStacksEqual(tStack, new ItemStack(Items.firework_charge, 1, W), true)
                            || GT_Utility.areStacksEqual(tStack, new ItemStack(Items.fireworks, 1, W), true)
                            || GT_Utility.areStacksEqual(tStack, new ItemStack(Items.fire_charge, 1, W), true)
                            ) {
                        if (aTileEntity instanceof IGregTechTileEntity) {
                            ((IGregTechTileEntity) aTileEntity).doExplosion(aVoltage * 4);
                        }
                        return null;
                    }
                    ItemData tData = GT_OreDictUnificator.getItemData(tStack);
                    
                    
                    if (tData != null) {
                        if (tData.mMaterial != null && tData.mMaterial.mMaterial != null) {
                            if (tData.mMaterial.mMaterial.contains(SubTag.METAL) || tData.mMaterial.mMaterial.contains(SubTag.EXPLOSIVE)) {
                                if (aTileEntity instanceof IGregTechTileEntity) {
                                    ((IGregTechTileEntity) aTileEntity).doExplosion(aVoltage * 4);
                                }
                                return null;
                            }
                            if (tData.mMaterial.mMaterial.contains(SubTag.FLAMMABLE)) {
                                if (aTileEntity instanceof IGregTechTileEntity) {
                                    ((IGregTechTileEntity) aTileEntity).setOnFire();
                                }
                                return null;
                            }
                        }
                        for (MaterialStack tMaterial : tData.mByProducts) {
                            if (tMaterial != null) {
                                if (tMaterial.mMaterial.contains(SubTag.METAL) || tMaterial.mMaterial.contains(SubTag.EXPLOSIVE)) {
                                    if (aTileEntity instanceof IGregTechTileEntity) {
                                        ((IGregTechTileEntity) aTileEntity).doExplosion(aVoltage * 4);
                                    }
                                    return null;
                                }
                                if (tMaterial.mMaterial.contains(SubTag.FLAMMABLE)) {
                                    if (aTileEntity instanceof IGregTechTileEntity) {
                                        ((IGregTechTileEntity) aTileEntity).setOnFire();
                                    }
                                    return null;
                                }
                            }
                        }
                    }
                    if (TileEntityFurnace.getItemBurnTime(tStack) > 0) {
                        if (aTileEntity instanceof IGregTechTileEntity) {
                            ((IGregTechTileEntity) aTileEntity).setOnFire();
                        }
                        return null;
                    }
                    
                }
            }

            if (tOutput == null) {
                return null;
            }
            return new GT_MachineRecipe(new ItemStack[]{GT_Utility.copyAmount(1, aInputs[0])}, new ItemStack[]{tOutput}, new FluidStack[0], new FluidStack[0]).setDuration(32).setEUt(4);
        }

        @Override
        public boolean containsInput(ItemStack aStack) {
            return GT_ModHandler.getSmeltingOutput(aStack, false, null) != null;
        }
    }

    public static class GT_RecipeMapPrinter extends GT_RecipeMap {
        
        public GT_RecipeMapPrinter(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
        
        @Override
        public GT_MachineRecipe findRecipe(IHasWorldObjectAndCoords aTileEntity, GT_MachineRecipe aRecipe, boolean aIgnoreCounts, long aVoltage, FluidStack[] aFluids, ItemStack aSpecialSlot, ItemStack... aInputs) {
            GT_MachineRecipe rRecipe = super.findRecipe(aTileEntity, aRecipe, aIgnoreCounts, aVoltage, aFluids, aSpecialSlot, aInputs);
            if (aInputs == null || aInputs.length <= 0 || aInputs[0] == null || aFluids == null || aFluids.length <= 0 || aFluids[0] == null || !GregTech_API.sPostloadFinished) {
                return rRecipe;
            }

            Dyes aDye = null;
            for (Dyes tDye : Dyes.VALUES) {
                if (tDye.isFluidDye(aFluids[0])) {
                    aDye = tDye;
                    break;
                }
            }

            if (aDye == null) {
                return rRecipe;
            }

            if (rRecipe == null) {
                ItemStack
                        tOutput = GT_ModHandler.getAllRecipeOutput(aTileEntity == null ? null : aTileEntity.getWorld(), aInputs[0], aInputs[0], aInputs[0], aInputs[0], ItemList.DYE_ONLY_ITEMS[aDye.mIndex].get(1), aInputs[0], aInputs[0], aInputs[0], aInputs[0]);
                if (tOutput != null) {
                    return addRecipe(new GT_MachineRecipe(new ItemStack[]{GT_Utility.copyAmount(8, aInputs[0])}, new ItemStack[]{tOutput}, new FluidStack[]{new FluidStack(aFluids[0].getFluid(), (int) L)}, null).setDuration(256).setEUt(2).optimize(), false, false, true);
                }

                tOutput = GT_ModHandler.getAllRecipeOutput(aTileEntity == null ? null : aTileEntity.getWorld(), aInputs[0], ItemList.DYE_ONLY_ITEMS[aDye.mIndex].get(1));
                if (tOutput != null) {
                    return addRecipe(new GT_MachineRecipe(new ItemStack[]{GT_Utility.copyAmount(1, aInputs[0])}, new ItemStack[]{tOutput}, new FluidStack[]{new FluidStack(aFluids[0].getFluid(), (int) L)}, null).setDuration(32).setEUt(2).optimize(), false, false, true);
                }
            } else {
                if (aInputs[0].getItem() == Items.paper) {
                    if (!ItemList.Tool_DataStick.isStackEqual(aSpecialSlot, false, true)) {
                        return null;
                    }
                    NBTTagCompound tNBT = aSpecialSlot.getTagCompound();
                    if (tNBT == null || GT_Utility.isStringInvalid(tNBT.getString("title")) || GT_Utility.isStringInvalid(tNBT.getString("author"))) {
                        return null;
                    }

                    rRecipe = rRecipe.copy();
                    rRecipe.mCanBeBuffered = false;
                    ItemStack tStack = rRecipe.mOutputs[0].getShownOutput();
                    tStack.setTagCompound(tNBT);
                    rRecipe.mOutputs[0] = new GT_RecipeOutput(tStack);
                    return rRecipe;
                }
                if (aInputs[0].getItem() == Items.map) {
                    if (!ItemList.Tool_DataStick.isStackEqual(aSpecialSlot, false, true)) {
                        return null;
                    }
                    NBTTagCompound tNBT = aSpecialSlot.getTagCompound();
                    if (tNBT == null || !tNBT.hasKey("map_id")) {
                        return null;
                    }

                    rRecipe = rRecipe.copy();
                    rRecipe.mCanBeBuffered = false;
                    ItemStack tStack = rRecipe.mOutputs[0].getShownOutput();
                    tStack.setItemDamage(tNBT.getShort("map_id"));
                    rRecipe.mOutputs[0] = new GT_RecipeOutput(tStack);
                    return rRecipe;
                }
                if (ItemList.Paper_Punch_Card_Empty.isStackEqual(aInputs[0], false, true)) {
                    if (!ItemList.Tool_DataStick.isStackEqual(aSpecialSlot, false, true)) {
                        return null;
                    }
                    NBTTagCompound tNBT = aSpecialSlot.getTagCompound();
                    if (tNBT == null || !tNBT.hasKey("GT.PunchCardData")) {
                        return null;
                    }

                    rRecipe = rRecipe.copy();
                    rRecipe.mCanBeBuffered = false;
                    ItemStack tStack = rRecipe.mOutputs[0].getShownOutput();
                    tStack.setTagCompound(GT_Utility.getNBTContainingString(new NBTTagCompound(), "GT.PunchCardData", tNBT.getString("GT.PunchCardData")));
                    rRecipe.mOutputs[0] = new GT_RecipeOutput(tStack);
                    return rRecipe;
                }
            }
            return rRecipe;
        }

        @Override
        public boolean containsInput(ItemStack aStack) {
            return true;
        }

        @Override
        public boolean containsInput(FluidStack aFluid) {
            return super.containsInput(aFluid) || Dyes.isAnyFluidDye(aFluid);
        }

        @Override
        public boolean containsInput(Fluid aFluid) {
            return super.containsInput(aFluid) || Dyes.isAnyFluidDye(aFluid);
        }
    }
    
    public static class GT_RecipeMapRecycler extends GT_RecipeMap_NonGT {

        public GT_RecipeMapRecycler(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }

        @Override
        public GT_MachineRecipe findRecipe(IHasWorldObjectAndCoords aTileEntity, GT_MachineRecipe aRecipe, boolean aIgnoreCounts, long aVoltage, FluidStack[] aFluids, ItemStack aSpecialSlot, ItemStack... aInputs) {
            if (aInputs == null || aInputs.length <= 0 || aInputs[0] == null) {
                return null;
            }
            if (aRecipe != null && aRecipe.isRecipeInputEqual(false, true, aFluids, aInputs)) {
                return aRecipe;
            }
            return new GT_MachineRecipe(new GT_RecipeInput[]{new GT_RecipeInput(GT_Utility.copyAmount(1, aInputs[0]))}, GT_ModHandler.getRecyclerOutput(GT_Utility.copyAmount(64, aInputs[0]), 0) == null ? null : new GT_RecipeOutput[]{new GT_RecipeOutput(ItemList.IC2_Scrap.get(1), 0.125f)}, null, null).setDuration(45).setEUt(1);
        }

        @Override
        public boolean containsInput(ItemStack aStack) {
            return GT_ModHandler.getRecyclerOutput(GT_Utility.copyAmount(64, aStack), 0) != null;
        }
    }  
 
    public static class GT_RecipeMapUnboxinator extends GT_RecipeMap {
        
        public GT_RecipeMapUnboxinator(Collection<GT_MachineRecipe> aRecipeList) {
            super(aRecipeList);
        }
        
        @Override
        public GT_MachineRecipe findRecipe(IHasWorldObjectAndCoords aTileEntity, GT_MachineRecipe aRecipe, boolean aIgnoreCounts, long aVoltage, FluidStack[] aFluids, ItemStack aSpecialSlot, ItemStack... aInputs) {
            if (aInputs == null || aInputs.length <= 0 || !ItemList.IC2_Scrapbox.isStackEqual(aInputs[0], false, true)) {
                return super.findRecipe(aTileEntity, aRecipe, aIgnoreCounts, aVoltage, aFluids, aSpecialSlot, aInputs);
            }
            ItemStack tOutput = GT_ModHandler.getRandomScrapboxDrop();
            if (tOutput == null) {
                return super.findRecipe(aTileEntity, aRecipe, aVoltage, aFluids, aSpecialSlot, aInputs);
            }
            GT_MachineRecipe rRecipe = new GT_MachineRecipe(new ItemStack[]{ItemList.IC2_Scrapbox.get(1)}, new ItemStack[]{tOutput}, new FluidStack[0], new FluidStack[0]).setDuration(16).setEUt(1);
            // It is not allowed to be buffered due to the random Output
            rRecipe.mCanBeBuffered = false;
            // Due to its randomness it is not good if there are Items in the Output Slot, because those Items could manipulate the outcome.
            rRecipe.mNeedsEmptyOutput = true;
            return rRecipe;
        }

        @Override
        public boolean containsInput(ItemStack aStack) {
            return ItemList.IC2_Scrapbox.isStackEqual(aStack, false, true) || super.containsInput(aStack);
        }
    }
    
}