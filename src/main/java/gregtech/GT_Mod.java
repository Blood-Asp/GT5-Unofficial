package gregtech;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.EntityRegistry;
import forestry.api.recipes.ICentrifugeRecipe;
import forestry.api.recipes.ISqueezerRecipe;
import forestry.api.recipes.RecipeManagers;
import gregtech.api.GregTech_API;
import gregtech.api.enchants.Enchantment_EnderDamage;
import gregtech.api.enchants.Enchantment_Radioactivity;
import gregtech.api.enums.*;
import gregtech.api.interfaces.internal.IGT_Mod;
import gregtech.api.objects.ItemData;
import gregtech.api.objects.MaterialStack;
import gregtech.api.objects.XSTR;
import gregtech.api.util.*;
import gregtech.common.GT_DummyWorld;
import gregtech.common.GT_Network;
import gregtech.common.GT_Proxy;
import gregtech.common.GT_RecipeAdder;
import gregtech.common.blocks.GT_Block_Ores_Abstract;
import gregtech.common.entities.GT_Entity_Arrow;
import gregtech.common.entities.GT_Entity_Arrow_Potion;
import gregtech.common.items.armor.components.LoadArmorComponents;
import gregtech.common.items.behaviors.Behaviour_DataOrb;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_Massfabricator;
import gregtech.loaders.load.GT_CoverBehaviorLoader;
import gregtech.loaders.load.GT_FuelLoader;
import gregtech.loaders.load.GT_ItemIterator;
import gregtech.loaders.load.GT_SonictronLoader;
import gregtech.loaders.misc.GT_Achievements;
import gregtech.loaders.misc.GT_Bees;
import gregtech.loaders.misc.GT_CoverLoader;
import gregtech.loaders.postload.*;
import gregtech.loaders.preload.*;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeOutput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod(modid = "gregtech", name = "GregTech", version = "MC1710", useMetadata = false, dependencies = "required-after:IC2; after:Forestry; after:PFAAGeologica; after:Thaumcraft; after:Railcraft; after:appliedenergistics2; after:ThermalExpansion; after:TwilightForest; after:harvestcraft; after:magicalcrops; after:BuildCraft|Transport; after:BuildCraft|Silicon; after:BuildCraft|Factory; after:BuildCraft|Energy; after:BuildCraft|Core; after:BuildCraft|Builders; after:GalacticraftCore; after:GalacticraftMars; after:GalacticraftPlanets; after:ThermalExpansion|Transport; after:ThermalExpansion|Energy; after:ThermalExpansion|Factory; after:RedPowerCore; after:RedPowerBase; after:RedPowerMachine; after:RedPowerCompat; after:RedPowerWiring; after:RedPowerLogic; after:RedPowerLighting; after:RedPowerWorld; after:RedPowerControl; after:UndergroundBiomes;")
public class GT_Mod implements IGT_Mod {
    public static final int VERSION = 509, SUBVERSION = 31;
    public static final int TOTAL_VERSION = calculateTotalGTVersion(VERSION, SUBVERSION);
    public static final int REQUIRED_IC2 = 624;
    @Mod.Instance("gregtech")
    public static GT_Mod instance;
    @SidedProxy(modId = "gregtech", clientSide = "gregtech.common.GT_Client", serverSide = "gregtech.common.GT_Server")
    public static GT_Proxy gregtechproxy;
    public static int MAX_IC2 = 2147483647;
    public static GT_Achievements achievements;
    private final String aTextGeneral = "general";
    private final String aTextIC2 = "ic2_";
   
    static {
        if ((509 != GregTech_API.VERSION) || (509 != GT_ModHandler.VERSION) || (509 != GT_OreDictUnificator.VERSION) || (509 != GT_Recipe.VERSION) || (509 != GT_Utility.VERSION) || (509 != GT_RecipeRegistrator.VERSION) || (509 != Element.VERSION) || (509 != Materials.VERSION) || (509 != OrePrefixes.VERSION)) {
            throw new GT_ItsNotMyFaultException("One of your Mods included GregTech-API Files inside it's download, mention this to the Mod Author, who does this bad thing, and tell him/her to use reflection. I have added a Version check, to prevent Authors from breaking my Mod that way.");
        }
    }

    public GT_Mod() {
        try {
            Class.forName("ic2.core.IC2").getField("enableOreDictCircuit").set(null, Boolean.FALSE);
        } catch (Throwable e) {
        }
        try {
            Class.forName("ic2.core.IC2").getField("enableCraftingBucket").set(null, Boolean.FALSE);
        } catch (Throwable e) {
        }
        try {
            Class.forName("ic2.core.IC2").getField("enableEnergyInStorageBlockItems").set(null, Boolean.FALSE);
        } catch (Throwable e) {
        }
        GT_Values.GT = this;
        GT_Values.DW = new GT_DummyWorld();
        GT_Values.NW = new GT_Network();
        GregTech_API.sRecipeAdder = GT_Values.RA = new GT_RecipeAdder();

        Textures.BlockIcons.VOID.name();
        Textures.ItemIcons.VOID.name();
    }

    @Mod.EventHandler
    public void onPreLoad(FMLPreInitializationEvent aEvent) {
        Locale.setDefault(Locale.ENGLISH);
        if (GregTech_API.sPreloadStarted) {
            return;
        }
        try {
            for (Runnable tRunnable : GregTech_API.sBeforeGTPreload) {
                tRunnable.run();
            }
        } catch (Throwable e) {e.printStackTrace(GT_Log.err);}
        File tFile = new File(new File(aEvent.getModConfigurationDirectory(), "GregTech"), "GregTech.cfg");
        Configuration tMainConfig = new Configuration(tFile);
        tMainConfig.load();
        tFile = new File(new File(aEvent.getModConfigurationDirectory(), "GregTech"), "IDs.cfg");
        GT_Config.sConfigFileIDs = new Configuration(tFile);
        GT_Config.sConfigFileIDs.load();
        GT_Config.sConfigFileIDs.save();
        GregTech_API.sRecipeFile = new GT_Config(new Configuration(new File(new File(aEvent.getModConfigurationDirectory(), "GregTech"), "Recipes.cfg")));
        GregTech_API.sMachineFile = new GT_Config(new Configuration(new File(new File(aEvent.getModConfigurationDirectory(), "GregTech"), "MachineStats.cfg")));
        GregTech_API.sWorldgenFile = new GT_Config(new Configuration(new File(new File(aEvent.getModConfigurationDirectory(), "GregTech"), "WorldGeneration.cfg")));
        GregTech_API.sMaterialProperties = new GT_Config(new Configuration(new File(new File(aEvent.getModConfigurationDirectory(), "GregTech"), "MaterialProperties.cfg")));
        GregTech_API.sMaterialComponents = new GT_Config(new Configuration(new File(new File(aEvent.getModConfigurationDirectory(), "GregTech"), "MaterialComponents.cfg")));
        GregTech_API.sUnification = new GT_Config(new Configuration(new File(new File(aEvent.getModConfigurationDirectory(), "GregTech"), "Unification.cfg")));
        GregTech_API.sSpecialFile = new GT_Config(new Configuration(new File(new File(aEvent.getModConfigurationDirectory(), "GregTech"), "Other.cfg")));
        GregTech_API.sOPStuff = new GT_Config(new Configuration(new File(new File(aEvent.getModConfigurationDirectory(), "GregTech"), "OverpoweredStuff.cfg")));
        GregTech_API.sModularArmor = new GT_Config(new Configuration(new File(new File(aEvent.getModConfigurationDirectory(), "GregTech"), "ModularArmor.cfg")));

        GregTech_API.sClientDataFile = new GT_Config(new Configuration(new File(aEvent.getModConfigurationDirectory().getParentFile(), "GregTech.cfg")));
        GregTech_API.mIC2Classic = Loader.isModLoaded("IC2-Classic-Spmod");
        GregTech_API.mMagneticraft = Loader.isModLoaded("Magneticraft");
        GregTech_API.mImmersiveEngineering = Loader.isModLoaded("ImmersiveEngineering");
        GregTech_API.mGTPlusPlus = Loader.isModLoaded("miscutils");        
        GT_Log.mLogFile = new File(aEvent.getModConfigurationDirectory().getParentFile(), "logs/GregTech.log");
        if (!GT_Log.mLogFile.exists()) {
            try {
                GT_Log.mLogFile.createNewFile();
            } catch (Throwable e) {
            }
        }
        try {
            GT_Log.out = GT_Log.err = new PrintStream(GT_Log.mLogFile);
        } catch (FileNotFoundException e) {
        }
        GT_Log.mOreDictLogFile = new File(aEvent.getModConfigurationDirectory().getParentFile(), "logs/OreDict.log");
        if (!GT_Log.mOreDictLogFile.exists()) {
            try {
                GT_Log.mOreDictLogFile.createNewFile();
            } catch (Throwable e) {
            }
        }
        if (tMainConfig.get(aTextGeneral, "LoggingPlayerActivity", true).getBoolean(true)) {
            GT_Log.mPlayerActivityLogFile = new File(aEvent.getModConfigurationDirectory().getParentFile(), "logs/PlayerActivity.log");
            if (!GT_Log.mPlayerActivityLogFile.exists()) {
                try {
                    GT_Log.mPlayerActivityLogFile.createNewFile();
                } catch (Throwable e) {
                }
            }
            try {
                GT_Log.pal = new PrintStream(GT_Log.mPlayerActivityLogFile);
            } catch (Throwable e) {
            }
        }
        try {
            List<String> tList = ((GT_Log.LogBuffer) GT_Log.ore).mBufferedOreDictLog;
            GT_Log.ore.println("******************************************************************************");
            GT_Log.ore.println("* This is the complete log of the GT5-Unofficial OreDictionary Handler. It   *");
            GT_Log.ore.println("* processes all OreDictionary entries and can sometimes cause errors. All    *");
            GT_Log.ore.println("* entries and errors are being logged. If you see an error please raise an   *");
            GT_Log.ore.println("* issue at https://github.com/Blood-Asp/GT5-Unofficial.                      *");
            GT_Log.ore.println("******************************************************************************");
            String tString;
            for (Iterator i$ = tList.iterator(); i$.hasNext(); GT_Log.ore.println(tString)) {
                tString = (String) i$.next();
            }
        } catch (Throwable e) {
        }
        gregtechproxy.onPreLoad();

        GT_Log.out.println("GT_Mod: Setting Configs");
        GT_Values.D1 = tMainConfig.get(aTextGeneral, "Debug", false).getBoolean(false);
        GT_Values.D2 = tMainConfig.get(aTextGeneral, "Debug2", false).getBoolean(false);

        GregTech_API.TICKS_FOR_LAG_AVERAGING = tMainConfig.get(aTextGeneral, "TicksForLagAveragingWithScanner", 25).getInt(25);
        GregTech_API.MILLISECOND_THRESHOLD_UNTIL_LAG_WARNING = tMainConfig.get(aTextGeneral, "MillisecondsPassedInGTTileEntityUntilLagWarning", 100).getInt(100);
        if (tMainConfig.get(aTextGeneral, "disable_STDOUT", false).getBoolean(false)) {
            System.out.close();
        }
        if (tMainConfig.get(aTextGeneral, "disable_STDERR", false).getBoolean(false)) {
            System.err.close();
        }
        GregTech_API.sMachineExplosions = tMainConfig.get("machines", "machines_explosion_damage", true).getBoolean(false);
        GregTech_API.sMachineFlammable = tMainConfig.get("machines", "machines_flammable", true).getBoolean(false);
        GregTech_API.sMachineNonWrenchExplosions = tMainConfig.get("machines", "explosions_on_nonwrenching", true).getBoolean(false);
        GregTech_API.sMachineWireFire = tMainConfig.get("machines", "wirefire_on_explosion", true).getBoolean(false);
        GregTech_API.sMachineFireExplosions = tMainConfig.get("machines", "fire_causes_explosions", true).getBoolean(false);
        GregTech_API.sMachineRainExplosions = tMainConfig.get("machines", "rain_causes_explosions", true).getBoolean(false);
        GregTech_API.sMachineThunderExplosions = tMainConfig.get("machines", "lightning_causes_explosions", true).getBoolean(false);
        GregTech_API.sConstantEnergy = tMainConfig.get("machines", "constant_need_of_energy", true).getBoolean(false);
        GregTech_API.sColoredGUI = tMainConfig.get("machines", "colored_guis_when_painted", true).getBoolean(false);

        GregTech_API.sTimber = tMainConfig.get(aTextGeneral, "timber_axe", true).getBoolean(true);
        GregTech_API.sDrinksAlwaysDrinkable = tMainConfig.get(aTextGeneral, "drinks_always_drinkable", false).getBoolean(false);
        GregTech_API.sDoShowAllItemsInCreative = tMainConfig.get(aTextGeneral, "show_all_metaitems_in_creative_and_NEI", false).getBoolean(false);
        GregTech_API.sMultiThreadedSounds = tMainConfig.get(aTextGeneral, "sound_multi_threading", false).getBoolean(false);
        String SBdye0 = "ColorModulation.";
        for (Dyes tDye : Dyes.values()) {
            if ((tDye != Dyes._NULL) && (tDye.mIndex < 0)) {
                String SBdye1 = new StringBuilder(18).append(SBdye0).append(tDye).toString();
                tDye.mRGBa[0] = ((short) Math.min(255, Math.max(0, GregTech_API.sClientDataFile.get(SBdye1, "R", tDye.mRGBa[0]))));
                tDye.mRGBa[1] = ((short) Math.min(255, Math.max(0, GregTech_API.sClientDataFile.get(SBdye1, "G", tDye.mRGBa[1]))));
                tDye.mRGBa[2] = ((short) Math.min(255, Math.max(0, GregTech_API.sClientDataFile.get(SBdye1, "B", tDye.mRGBa[2]))));
            }
        }
        gregtechproxy.mMaxEqualEntitiesAtOneSpot = tMainConfig.get(aTextGeneral, "MaxEqualEntitiesAtOneSpot", 3).getInt(3);
        gregtechproxy.mSkeletonsShootGTArrows = tMainConfig.get(aTextGeneral, "SkeletonsShootGTArrows", 16).getInt(16);
        gregtechproxy.mFlintChance = tMainConfig.get(aTextGeneral, "FlintAndSteelChance", 30).getInt(30);
        gregtechproxy.mItemDespawnTime = tMainConfig.get(aTextGeneral, "ItemDespawnTime", 6000).getInt(6000);
        gregtechproxy.mNerfStorageBlocks = tMainConfig.get(aTextGeneral,"NerfStorageBlocks",true).getBoolean(true);
        gregtechproxy.mAllowSmallBoilerAutomation = tMainConfig.get(aTextGeneral,"AllowSmallBoilerAutomation",false).getBoolean(false);
        gregtechproxy.mHardMachineCasings= tMainConfig.get(aTextGeneral,"HardMachineCasings",true).getBoolean(true);
        gregtechproxy.mDisableVanillaOres = tMainConfig.get(aTextGeneral, "DisableVanillaOres", true).getBoolean(true);
        gregtechproxy.mNerfDustCrafting = tMainConfig.get(aTextGeneral, "NerfDustCrafting", true).getBoolean(true);
        gregtechproxy.mIncreaseDungeonLoot = tMainConfig.get(aTextGeneral, "IncreaseDungeonLoot", true).getBoolean(true);
        gregtechproxy.mAxeWhenAdventure = tMainConfig.get(aTextGeneral, "AdventureModeStartingAxe", true).getBoolean(true);
        gregtechproxy.mHardcoreCables = tMainConfig.get(aTextGeneral, "HardCoreCableLoss", false).getBoolean(false);
        gregtechproxy.mSmallLavaBoilerEfficiencyLoss = tMainConfig.get(aTextGeneral, "SmallLavaBoilerEfficiencyLoss", true).getBoolean(true);
        gregtechproxy.mSurvivalIntoAdventure = tMainConfig.get(aTextGeneral, "forceAdventureMode", false).getBoolean(false);
        gregtechproxy.mHungerEffect = tMainConfig.get(aTextGeneral, "AFK_Hunger", false).getBoolean(false);
        gregtechproxy.mHardRock = tMainConfig.get(aTextGeneral, "harderstone", false).getBoolean(false);
        gregtechproxy.mInventoryUnification = tMainConfig.get(aTextGeneral, "InventoryUnification", true).getBoolean(true);
        gregtechproxy.mGTBees = tMainConfig.get(aTextGeneral, "GTBees", true).getBoolean(true);
        gregtechproxy.mCraftingUnification = tMainConfig.get(aTextGeneral, "CraftingUnification", true).getBoolean(true);
        gregtechproxy.mNerfedWoodPlank = tMainConfig.get(aTextGeneral, "WoodNeedsSawForCrafting", true).getBoolean(true);
        gregtechproxy.mNerfedVanillaTools = tMainConfig.get(aTextGeneral, "smallerVanillaToolDurability", true).getBoolean(true);
        gregtechproxy.mSortToTheEnd = tMainConfig.get(aTextGeneral, "EnsureToBeLoadedLast", true).getBoolean(true);
        gregtechproxy.mDisableIC2Cables = tMainConfig.get(aTextGeneral, "DisableIC2Cables", true).getBoolean(true);
        gregtechproxy.mAchievements = tMainConfig.get(aTextGeneral, "EnableAchievements", true).getBoolean(true);
        gregtechproxy.mAE2Integration = GregTech_API.sSpecialFile.get(ConfigCategories.general, "EnableAE2Integration", Loader.isModLoaded("appliedenergistics2"));
        gregtechproxy.mAE2Tunnel = GregTech_API.sSpecialFile.get(ConfigCategories.general, "EnableAE2Tunnel", false);
        gregtechproxy.mNerfedCombs = tMainConfig.get(aTextGeneral, "NerfCombs", true).getBoolean(true);
        gregtechproxy.mNerfedCrops = tMainConfig.get(aTextGeneral, "NerfCrops", true).getBoolean(true);
        gregtechproxy.mHideUnusedOres = tMainConfig.get(aTextGeneral, "HideUnusedOres", true).getBoolean(true);
        gregtechproxy.mHideRecyclingRecipes = tMainConfig.get(aTextGeneral, "HideRecyclingRecipes", true).getBoolean(true);
        gregtechproxy.mArcSmeltIntoAnnealed = tMainConfig.get(aTextGeneral, "ArcSmeltIntoAnnealedWrought", true).getBoolean(true);
        gregtechproxy.mMagneticraftRecipes = tMainConfig.get(aTextGeneral, "EnableMagneticraftSupport", true).getBoolean(true);
        gregtechproxy.mImmersiveEngineeringRecipes = tMainConfig.get(aTextGeneral, "EnableImmersiveEngineeringRSupport", true).getBoolean(true);
        gregtechproxy.mMagneticraftBonusOutputPercent = tMainConfig.get(aTextGeneral, "MagneticraftBonusOutputPercent", 100.0f).getDouble();
        gregtechproxy.mTEMachineRecipes = tMainConfig.get("general", "TEMachineRecipes", false).getBoolean(false);
        gregtechproxy.mEnableAllMaterials = tMainConfig.get("general", "EnableAllMaterials", false).getBoolean(false);
        gregtechproxy.mEnableAllComponents = tMainConfig.get("general", "EnableAllComponents", false).getBoolean(false);
        gregtechproxy.mPollution = tMainConfig.get("Pollution", "EnablePollution", true).getBoolean(true);
        gregtechproxy.mPollutionSmogLimit = tMainConfig.get("Pollution", "SmogLimit", 500000).getInt(500000);
        gregtechproxy.mPollutionPoisonLimit = tMainConfig.get("Pollution", "PoisonLimit", 750000).getInt(750000);
        gregtechproxy.mPollutionVegetationLimit = tMainConfig.get("Pollution", "VegetationLimit", 1000000).getInt(1000000);
        gregtechproxy.mPollutionSourRainLimit = tMainConfig.get("Pollution", "SourRainLimit", 2000000).getInt(2000000);
        gregtechproxy.mExplosionItemDrop = tMainConfig.get("general", "ExplosionItemDrops", false).getBoolean(false);
        gregtechproxy.mAddGTRecipesToIC2Machines = tMainConfig.get("general", "AddGTRecipesToIC2Machines", true).getBoolean(true);
        gregtechproxy.mUndergroundOil.getConfig(tMainConfig, "undergroundfluid");
        gregtechproxy.mEnableCleanroom = tMainConfig.get("general", "EnableCleanroom", true).getBoolean(true);
        gregtechproxy.mLowGravProcessing = Loader.isModLoaded(GT_Values.MOD_ID_GC_CORE) && tMainConfig.get("general", "LowGravProcessing", true).getBoolean(true);
        Calendar now = Calendar.getInstance();
        gregtechproxy.mAprilFool = GregTech_API.sSpecialFile.get(ConfigCategories.general, "AprilFool", now.get(Calendar.MONTH) == Calendar.APRIL && now.get(Calendar.DAY_OF_MONTH) == 1);
        gregtechproxy.mCropNeedBlock = tMainConfig.get("general", "CropNeedBlockBelow", true).getBoolean(true);
        gregtechproxy.mDisableOldChemicalRecipes = tMainConfig.get("general", "DisableOldChemicalRecipes", false).getBoolean(false);
        gregtechproxy.mMoreComplicatedChemicalRecipes = tMainConfig.get("general", "MoreComplicatedChemicalRecipes", false).getBoolean(false);
        gregtechproxy.mAMHInteraction = tMainConfig.get("general", "AllowAutoMaintenanceHatchInteraction", false).getBoolean(false);
        GregTech_API.mOutputRF = GregTech_API.sOPStuff.get(ConfigCategories.general, "OutputRF", true);
        GregTech_API.mInputRF = GregTech_API.sOPStuff.get(ConfigCategories.general, "InputRF", false);
        GregTech_API.mEUtoRF = GregTech_API.sOPStuff.get(ConfigCategories.general, "100EUtoRF", 360);
        GregTech_API.mRFtoEU = GregTech_API.sOPStuff.get(ConfigCategories.general, "100RFtoEU", 20);
        GregTech_API.mRFExplosions = GregTech_API.sOPStuff.get(ConfigCategories.general, "RFExplosions", false);
        GregTech_API.meIOLoaded = Loader.isModLoaded("EnderIO");
        gregtechproxy.mForceFreeFace = GregTech_API.sMachineFile.get(ConfigCategories.machineconfig, "forceFreeFace",false);
        gregtechproxy.mEasierIVPlusCables = tMainConfig.get("general", "EasierIVPlusCables", false).getBoolean(false);
        gregtechproxy.mBrickedBlastFurnace = tMainConfig.get("general", "BrickedBlastFurnace", true).getBoolean(true);
        gregtechproxy.mMixedOreOnlyYieldsTwoThirdsOfPureOre = tMainConfig.get("general", "MixedOreOnlyYieldsTwoThirdsOfPureOre", false).getBoolean(false);
        gregtechproxy.enableBlackGraniteOres = GregTech_API.sWorldgenFile.get("general", "enableBlackGraniteOres", gregtechproxy.enableBlackGraniteOres);
        gregtechproxy.enableRedGraniteOres = GregTech_API.sWorldgenFile.get("general", "enableRedGraniteOres", gregtechproxy.enableRedGraniteOres);
        gregtechproxy.enableMarbleOres = GregTech_API.sWorldgenFile.get("general", "enableMarbleOres", gregtechproxy.enableMarbleOres);
        gregtechproxy.enableBasaltOres = GregTech_API.sWorldgenFile.get("general", "enableBasaltOres", gregtechproxy.enableBasaltOres);
        gregtechproxy.enableGCOres = GregTech_API.sWorldgenFile.get("general", "enableGCOres", gregtechproxy.enableGCOres);
        gregtechproxy.enableUBOres = GregTech_API.sWorldgenFile.get("general", "enableUBOres", gregtechproxy.enableUBOres);
        gregtechproxy.gt6Pipe = tMainConfig.get("general", "GT6StyledPipesConnection", true).getBoolean(true);
        gregtechproxy.gt6Cable = tMainConfig.get("general", "GT6StyledWiresConnection", false).getBoolean(false);
        gregtechproxy.costlyCableConnection = tMainConfig.get("general", "CableConnectionRequiresSolderingMaterial", false).getBoolean(false);
        GT_LanguageManager.i18nPlaceholder = tMainConfig.get("general", "UsePlaceholderForMaterialNamesInLangFile", true).getBoolean(true);

        Materials[] tDisableOres = new Materials[]{Materials.Chrome, Materials.Naquadria, Materials.Silicon, Materials.Cobalt, Materials.Cadmium, Materials.Indium, Materials.Tungsten,
        		Materials.Adamantium, Materials.Mithril, Materials.DarkIron, Materials.Rutile, Materials.Alduorite, Materials.Magnesium, Materials.Nikolite};
        for(Materials tMat : tDisableOres){
        	if(!GregTech_API.sMaterialComponents.get("disableLaserDrillOres", tMat.mName, false))
        		GT_Block_Ores_Abstract.aBlockedOres.add(tMat);
        }
        
        GregTech_API.mUseOnlyGoodSolderingMaterials = GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.harderrecipes, "useonlygoodsolderingmaterials", GregTech_API.mUseOnlyGoodSolderingMaterials);

        if (tMainConfig.get("general", "hardermobspawners", true).getBoolean(true)) {
            Blocks.mob_spawner.setHardness(500.0F).setResistance(6000000.0F);
        }
        gregtechproxy.mOnline = tMainConfig.get(aTextGeneral, "online", true).getBoolean(false);

        gregtechproxy.mUpgradeCount = Math.min(64, Math.max(1, tMainConfig.get("features", "UpgradeStacksize", 4).getInt()));
        for (OrePrefixes tPrefix : OrePrefixes.values()) {
            if (tPrefix.mIsUsedForOreProcessing) {
                tPrefix.mDefaultStackSize = ((byte) Math.min(64, Math.max(1, tMainConfig.get("features", "MaxOreStackSize", 64).getInt())));
            } else if (tPrefix == OrePrefixes.plank) {
                tPrefix.mDefaultStackSize = ((byte) Math.min(64, Math.max(16, tMainConfig.get("features", "MaxPlankStackSize", 64).getInt())));
            } else if ((tPrefix == OrePrefixes.wood) || (tPrefix == OrePrefixes.treeLeaves) || (tPrefix == OrePrefixes.treeSapling) || (tPrefix == OrePrefixes.log)) {
                tPrefix.mDefaultStackSize = ((byte) Math.min(64, Math.max(16, tMainConfig.get("features", "MaxLogStackSize", 64).getInt())));
            } else if (tPrefix.mIsUsedForBlocks) {
                tPrefix.mDefaultStackSize = ((byte) Math.min(64, Math.max(16, tMainConfig.get("features", "MaxOtherBlockStackSize", 64).getInt())));
            }
        }
        
        new Enchantment_EnderDamage();
        new Enchantment_Radioactivity();
        
        //GT_Config.troll = (Calendar.getInstance().get(2) + 1 == 4) && (Calendar.getInstance().get(5) >= 1) && (Calendar.getInstance().get(5) <= 2);
        Materials.init();

        GT_Log.out.println("GT_Mod: Saving Main Config");
        tMainConfig.save();

        GT_Log.out.println("GT_Mod: Generating Lang-File");
        GT_LanguageManager.sEnglishFile = new Configuration(new File(aEvent.getModConfigurationDirectory().getParentFile(), "GregTech.lang"));
        GT_LanguageManager.sEnglishFile.load();
        for (Materials aMaterial : Materials.values()) {
        	if (aMaterial != null)
        		aMaterial.mLocalizedName = GT_LanguageManager.addStringLocalization("Material." + aMaterial.mName.toLowerCase(), aMaterial.mDefaultLocalName);
        }

        GT_Log.out.println("GT_Mod: Removing all original Scrapbox Drops.");
        try {
            GT_Utility.getField("ic2.core.item.ItemScrapbox$Drop", "topChance", true, true).set(null, Integer.valueOf(0));
            ((List) GT_Utility.getFieldContent(GT_Utility.getFieldContent("ic2.api.recipe.Recipes", "scrapboxDrops", true, true), "drops", true, true)).clear();
        } catch (Throwable e) {
            if (GT_Values.D1) {
                e.printStackTrace(GT_Log.err);
            }
        }
        GT_Log.out.println("GT_Mod: Adding Scrap with a Weight of 200.0F to the Scrapbox Drops.");
        GT_ModHandler.addScrapboxDrop(200.0F, GT_ModHandler.getIC2Item("scrap", 1L));

        EntityRegistry.registerModEntity(GT_Entity_Arrow.class, "GT_Entity_Arrow", 1, GT_Values.GT, 160, 1, true);
        EntityRegistry.registerModEntity(GT_Entity_Arrow_Potion.class, "GT_Entity_Arrow_Potion", 2, GT_Values.GT, 160, 1, true);

        System.out.println("preReader");
        List<String> oreTags = new ArrayList<String>();
        if(Loader.isModLoaded("MineTweaker3")){
        	File globalDir = new File("scripts");
        	if (globalDir.exists()){
        		List<String> scripts = new ArrayList<String>();
        			for (File file : globalDir.listFiles()) {
        				 if (file.getName().endsWith(".zs")) {
        					 try (BufferedReader br = new BufferedReader(new FileReader(file))) {
        						    String line;
        						    while ((line = br.readLine()) != null) {
        						       scripts.add(line);
        						    }
        						} catch (Exception e) {e.printStackTrace();}
        				}
        			}
        			String pattern1 = "<";
        			String pattern2 = ">";

        			Pattern p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
        			for(String text : scripts){
        			Matcher m = p.matcher(text);
        			while (m.find()) {
        			  String hit = m.group(1);
        			  if(hit.startsWith("ore:")){
        				  hit = hit.substring(4);
        				  if(!oreTags.contains(hit)) oreTags.add(hit);
        			  }else if(hit.startsWith("gregtech:gt.metaitem.0")){
        				  hit = hit.substring(22);
        				  int mIt = Integer.parseInt(hit.substring(0, 1));
        				  if(mIt>0){
        					  int meta = 0;
        					  try{
        					  hit = hit.substring(2);
        					  meta = Integer.parseInt(hit);
        					  }catch(Exception e){System.out.println("parseError: "+hit);}
        					  if(meta>0&&meta<32000){
        					  int prefix = meta/1000;
        					  int material = meta % 1000;
        					  String tag = "";
        					  String[] tags = new String[]{};
        					  if(mIt==1)tags = new String[]{"dustTiny","dustSmall","dust","dustImpure","dustPure","crushed","crushedPurified","crushedCentrifuged","gem","nugget",null,"ingot","ingotHot","ingotDouble","ingotTriple","ingotQuadruple","ingotQuintuple","plate","plateDouble","plateTriple","plateQuadruple","plateQuintuple","plateDense","stick","lens","round","bolt","screw","ring","foil","cell","cellPlasma"};
        					  if(mIt==2)tags = new String[]{"toolHeadSword", "toolHeadPickaxe", "toolHeadShovel", "toolHeadAxe", "toolHeadHoe", "toolHeadHammer", "toolHeadFile", "toolHeadSaw", "toolHeadDrill", "toolHeadChainsaw", "toolHeadWrench", "toolHeadUniversalSpade", "toolHeadSense", "toolHeadPlow", "toolHeadArrow", "toolHeadBuzzSaw", "turbineBlade", null, null, "wireFine", "gearGtSmall", "rotor", "stickLong", "springSmall", "spring", "arrowGtWood", "arrowGtPlastic", "gemChipped", "gemFlawed", "gemFlawless", "gemExquisite", "gearGt"};
        					  if(mIt==3)tags = new String[]{"crateGtDust", "crateGtIngot", "crateGtGem", "crateGtPlate"};
        					  if(tags.length>prefix) tag = tags[prefix];
        					  if(GregTech_API.sGeneratedMaterials[material]!=null){
        						  tag += GregTech_API.sGeneratedMaterials[material].mName;
        						  if(!oreTags.contains(tag)) oreTags.add(tag);
        					  }else if(material>0){System.out.println("MaterialDisabled: "+material+" "+m.group(1));}
        					  }
        				  }
        			  }
        			}
        		}
        	}
        }
        String[] preS = new String[]{"dustTiny","dustSmall","dust","dustImpure","dustPure","crushed","crushedPurified","crushedCentrifuged","gem","nugget","ingot","ingotHot","ingotDouble","ingotTriple","ingotQuadruple","ingotQuintuple","plate","plateDouble","plateTriple","plateQuadruple","plateQuintuple","plateDense","stick","lens","round","bolt","screw","ring","foil","cell","cellPlasma","toolHeadSword", "toolHeadPickaxe", "toolHeadShovel", "toolHeadAxe", "toolHeadHoe", "toolHeadHammer", "toolHeadFile", "toolHeadSaw", "toolHeadDrill", "toolHeadChainsaw", "toolHeadWrench", "toolHeadUniversalSpade", "toolHeadSense", "toolHeadPlow", "toolHeadArrow", "toolHeadBuzzSaw", "turbineBlade", "wireFine", "gearGtSmall", "rotor", "stickLong", "springSmall", "spring", "arrowGtWood", "arrowGtPlastic", "gemChipped", "gemFlawed", "gemFlawless", "gemExquisite", "gearGt","crateGtDust", "crateGtIngot", "crateGtGem", "crateGtPlate"};

        List<String> mMTTags = new ArrayList<String>();
        for(String test : oreTags){
        	if(StringUtils.startsWithAny(test, preS)){
        	mMTTags.add(test);
        	if(GT_Values.D1)
        	System.out.println("oretag: "+test);
        	}}
        
        System.out.println("reenableMetaItems");
        for(String reEnable : mMTTags){
        OrePrefixes tPrefix = OrePrefixes.getOrePrefix(reEnable);
        if(tPrefix!=null){
        Materials tName = Materials.get(reEnable.replaceFirst(tPrefix.toString(), ""));
        if(tName!=null){
        tPrefix.mDisabledItems.remove(tName);
        tPrefix.mGeneratedItems.add(tName);
        if(tPrefix == OrePrefixes.screw){
        	OrePrefixes.bolt.mDisabledItems.remove(tName);
        	OrePrefixes.bolt.mGeneratedItems.add(tName);
        	OrePrefixes.stick.mDisabledItems.remove(tName);
        	OrePrefixes.stick.mGeneratedItems.add(tName);
        }
        if(tPrefix == OrePrefixes.round){
        	OrePrefixes.nugget.mDisabledItems.remove(tName);
        	OrePrefixes.nugget.mGeneratedItems.add(tName);
        }
        if(tPrefix == OrePrefixes.spring){
        	OrePrefixes.stickLong.mDisabledItems.remove(tName);
        	OrePrefixes.stickLong.mGeneratedItems.add(tName);
        	OrePrefixes.stick.mDisabledItems.remove(tName);
        	OrePrefixes.stick.mGeneratedItems.add(tName);
        }
        if(tPrefix == OrePrefixes.springSmall){
        	OrePrefixes.stick.mDisabledItems.remove(tName);
        	OrePrefixes.stick.mGeneratedItems.add(tName);
        }
        if(tPrefix == OrePrefixes.stickLong){
        	OrePrefixes.stick.mDisabledItems.remove(tName);
        	OrePrefixes.stick.mGeneratedItems.add(tName);
        }
        if(tPrefix == OrePrefixes.rotor){
        	OrePrefixes.ring.mDisabledItems.remove(tName);
        	OrePrefixes.ring.mGeneratedItems.add(tName);
        }
        }else{System.out.println("noMaterial "+reEnable);}
        }else{System.out.println("noPrefix "+reEnable);}}

        new GT_Loader_OreProcessing().run();
        new GT_Loader_OreDictionary().run();
        new GT_Loader_ItemData().run();
        new GT_Loader_Item_Block_And_Fluid().run();
        new GT_Loader_MetaTileEntities().run();

        new GT_Loader_CircuitBehaviors().run();
        new GT_CoverBehaviorLoader().run();
        new GT_SonictronLoader().run();
        new GT_SpawnEventHandler();
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.gregtechrecipes, "SolarPanel", true)) {
            GT_ModHandler.addCraftingRecipe(ItemList.Cover_SolarPanel.get(1L, new Object[0]),    GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE, new Object[]{"SGS", "CPC", 'C', OrePrefixes.circuit.get(Materials.Basic), 'G', new ItemStack(Blocks.glass_pane, 1), 'P', OrePrefixes.plateAlloy.get(Materials.Carbon), 'S', ItemList.Circuit_Silicon_Wafer});
            GT_ModHandler.addCraftingRecipe(ItemList.Cover_SolarPanel_8V.get(1L, new Object[0]), GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE, new Object[]{"SGS", "CPC","R R", 'C', OrePrefixes.circuit.get(Materials.Advanced), 'G', new ItemStack(Blocks.glass_pane, 1), 'P', OrePrefixes.wireGt04.get(Materials.Graphene), 'S', ItemList.Circuit_Silicon_Wafer2,'R', OrePrefixes.plate.get(Materials.GalliumArsenide)});
            GT_ModHandler.addCraftingRecipe(ItemList.Cover_SolarPanel_LV.get(1L, new Object[0]), GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE, new Object[]{"SGS", "CPC","R R", 'C', OrePrefixes.circuit.get(Materials.Master), 'G', GT_ModHandler.getIC2Item("reinforcedGlass", 1L), 'P', OrePrefixes.wireGt16.get(Materials.Graphene), 'S', ItemList.Circuit_Silicon_Wafer3,'R', OrePrefixes.plate.get(Materials.IndiumGalliumPhosphide)});
                               }
        if (GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "SolarPanel8V", false)) {
            GT_ModHandler.addCraftingRecipe(ItemList.Cover_SolarPanel_8V.get(1L, new Object[0]), GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE, new Object[]{"SSS", "STS", "SSS", 'S', ItemList.Cover_SolarPanel, 'T', OrePrefixes.circuit.get(Materials.Advanced)});
        }
        if (GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "SolarPanelLV", false)) {
            GT_ModHandler.addCraftingRecipe(ItemList.Cover_SolarPanel_LV.get(1L, new Object[0]), GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE, new Object[]{" S ", "STS", " S ", 'S', ItemList.Cover_SolarPanel_8V, 'T', ItemList.Transformer_LV_ULV});
        }
        if (GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "SolarPanelMV", false)) {
            GT_ModHandler.addCraftingRecipe(ItemList.Cover_SolarPanel_MV.get(1L, new Object[0]), GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE, new Object[]{" S ", "STS", " S ", 'S', ItemList.Cover_SolarPanel_LV, 'T', ItemList.Transformer_MV_LV});
        }
        if (GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "SolarPanelHV", false)) {
            GT_ModHandler.addCraftingRecipe(ItemList.Cover_SolarPanel_HV.get(1L, new Object[0]), GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE, new Object[]{" S ", "STS", " S ", 'S', ItemList.Cover_SolarPanel_MV, 'T', ItemList.Transformer_HV_MV});
        }
        if (GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "SolarPanelEV", false)) {
            GT_ModHandler.addCraftingRecipe(ItemList.Cover_SolarPanel_EV.get(1L, new Object[0]), GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE, new Object[]{" S ", "STS", " S ", 'S', ItemList.Cover_SolarPanel_HV, 'T', ItemList.Transformer_EV_HV});
        }
        if (GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "SolarPanelIV", false)) {
            GT_ModHandler.addCraftingRecipe(ItemList.Cover_SolarPanel_IV.get(1L, new Object[0]), GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE, new Object[]{" S ", "STS", " S ", 'S', ItemList.Cover_SolarPanel_EV, 'T', ItemList.Transformer_IV_EV});
        }
        if (GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "SolarPanelLuV", false)) {
            GT_ModHandler.addCraftingRecipe(ItemList.Cover_SolarPanel_LuV.get(1L, new Object[0]), GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE, new Object[]{" S ", "STS", " S ", 'S', ItemList.Cover_SolarPanel_IV, 'T', ItemList.Transformer_LuV_IV});
        }
        if (GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "SolarPanelZPM", false)) {
            GT_ModHandler.addCraftingRecipe(ItemList.Cover_SolarPanel_ZPM.get(1L, new Object[0]), GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE, new Object[]{" S ", "STS", " S ", 'S', ItemList.Cover_SolarPanel_LuV, 'T', ItemList.Transformer_ZPM_LuV});
        }
        if (GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "SolarPanelUV", false)) {
            GT_ModHandler.addCraftingRecipe(ItemList.Cover_SolarPanel_UV.get(1L, new Object[0]), GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE, new Object[]{" S ", "STS", " S ", 'S', ItemList.Cover_SolarPanel_ZPM, 'T', ItemList.Transformer_UV_ZPM});
        }
    	double outputMultiplier = Math.min(GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "StoneDustOutputMultiplier", 0.6), 1.0);
        if (GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "StoneDustCentrifugation", false)) {
        	int fullChance = (int)(10000 * outputMultiplier);
        	int[] chances = new int[]{fullChance, fullChance, fullChance, fullChance, fullChance, (int)(5500 * outputMultiplier)};
			GT_Values.RA.addCentrifugeRecipe(Materials.Stone.getDust(1), GT_Values.NI, GT_Values.NF, GT_Values.NF, 
					Materials.Quartzite.getDustSmall(1),Materials.PotassiumFeldspar.getDustSmall(1),Materials.Marble.getDustTiny(2),
					Materials.Biotite.getDustTiny(1), 	Materials.MetalMixture.getDustTiny(1), 		Materials.Sodalite.getDustTiny(1), 
					chances, 500, 72);
			GT_Values.RA.addCentrifugeRecipe(Materials.MetalMixture.getDust(1), GT_Values.NI, GT_Values.NF, GT_Values.NF, 
					Materials.BandedIron.getDustSmall(1), 	Materials.Bauxite.getDustSmall(1), Materials.Pyrolusite.getDustTiny(2),
					Materials.Barite.getDustTiny(1), 		Materials.Chromite.getDustTiny(1), Materials.Ilmenite.getDustTiny(1), 
					new int[]{10000, 10000, 10000, 10000, 10000, 6000}, 500, 120);
        }
        double ashOutputMultiplier = Math.min(GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "AshOutputMultiplier", 1.0), 1.0);
        if (GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "AshYieldsAlkaliMetalOxides", true)) {
        	int[] outputChances = new int[]{(int) (9900 * ashOutputMultiplier), (int) (6400 * ashOutputMultiplier), 
        			(int) (6000 * ashOutputMultiplier), (int) (500 * ashOutputMultiplier), 
        			(int) (5000 * ashOutputMultiplier), (int) (2500 * ashOutputMultiplier)};
            GT_Values.RA.addCentrifugeRecipe(Materials.Ash.getDust(1), GT_Values.NI, GT_Values.NF, GT_Values.NF, 
            		Materials.Quicklime.getDustSmall(2),           Materials.Potash.getDustSmall(1), Materials.Magnesia.getDustSmall(1), 
            		Materials.PhosphorousPentoxide.getDustTiny(1), Materials.SodaAsh.getDustTiny(1), Materials.BandedIron.getDustTiny(1), 
            		outputChances, 240, 30);
        } else {
        	GT_Values.RA.addCentrifugeRecipe(Materials.Ash.getDust(1), GT_Values.NI, GT_Values.NF, GT_Values.NF, 
        			Materials.Carbon.getDust(1), GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, GT_Values.NI, 
        			new int[]{(int) (10000 * ashOutputMultiplier), 0, 0, 0, 0, 0}, 40, 16);
        }
        if (gregtechproxy.mSortToTheEnd) {
            try {
                GT_Log.out.println("GT_Mod: Sorting GregTech to the end of the Mod List for further processing.");
                LoadController tLoadController = (LoadController) GT_Utility.getFieldContent(Loader.instance(), "modController", true, true);
                List<ModContainer> tModList = tLoadController.getActiveModList();
                List<ModContainer> tNewModsList = new ArrayList();
                ModContainer tGregTech = null;
                short tModList_sS= (short) tModList.size();
                for (short i = 0; i < tModList_sS; i = (short) (i + 1)) {
                    ModContainer tMod = (ModContainer) tModList.get(i);
                    if (tMod.getModId().equalsIgnoreCase("gregtech")) {
                        tGregTech = tMod;
                    } else {
                        tNewModsList.add(tMod);
                    }
                }
                if (tGregTech != null) {
                    tNewModsList.add(tGregTech);
                }
                GT_Utility.getField(tLoadController, "activeModList", true, true).set(tLoadController, tNewModsList);
            } catch (Throwable e) {
                if (GT_Values.D1) {
                    e.printStackTrace(GT_Log.err);
                }
            }
        }
        GregTech_API.sPreloadFinished = true;
        GT_Log.out.println("GT_Mod: Preload-Phase finished!");
        GT_Log.ore.println("GT_Mod: Preload-Phase finished!");
        try {
            for (Runnable tRunnable : GregTech_API.sAfterGTPreload) {
                tRunnable.run();
            }
        } catch (Throwable e) {e.printStackTrace(GT_Log.err);}
    }

    @Mod.EventHandler
    public void onLoad(FMLInitializationEvent aEvent) {
        if (GregTech_API.sLoadStarted) {
            return;
        }
        try {
            for (Runnable tRunnable : GregTech_API.sBeforeGTLoad) {
                tRunnable.run();
            }
        } catch (Throwable e) {e.printStackTrace(GT_Log.err);}

        new GT_Bees();
		
		//Disable Low Grav regardless of config if Cleanroom is disabled.
		if (!gregtechproxy.mEnableCleanroom){
			gregtechproxy.mLowGravProcessing = false;
		}

        gregtechproxy.onLoad();
        if (gregtechproxy.mSortToTheEnd) {
            new GT_ItemIterator().run();
            gregtechproxy.registerUnificationEntries();
            new GT_FuelLoader().run();
        }
        GregTech_API.sLoadFinished = true;
        GT_Log.out.println("GT_Mod: Load-Phase finished!");
        GT_Log.ore.println("GT_Mod: Load-Phase finished!");
        try {
            for (Runnable tRunnable : GregTech_API.sAfterGTLoad) {
                tRunnable.run();
            }
        } catch (Throwable e) {e.printStackTrace(GT_Log.err);}
    }

    @Mod.EventHandler
    public void onPostLoad(FMLPostInitializationEvent aEvent) {
        if (GregTech_API.sPostloadStarted) {
            return;
        }
        try {
            for (Runnable tRunnable : GregTech_API.sBeforeGTPostload) {
                tRunnable.run();
            }
        } catch (Throwable e) {e.printStackTrace(GT_Log.err);}
        gregtechproxy.onPostLoad();
        if (gregtechproxy.mSortToTheEnd) {
            gregtechproxy.registerUnificationEntries();
        } else {
            new GT_ItemIterator().run();
            gregtechproxy.registerUnificationEntries();
            new GT_FuelLoader().run();
        }
        new GT_BookAndLootLoader().run();
        new GT_ItemMaxStacksizeLoader().run();
        new GT_BlockResistanceLoader().run();
        new GT_RecyclerBlacklistLoader().run();
        new GT_MinableRegistrator().run();
        new GT_MachineRecipeLoader().run();
        new GT_ScrapboxDropLoader().run();
        new GT_CropLoader().run();
        new GT_Worldgenloader().run();
        new GT_CoverLoader().run();
        new GT_AE2EnergyTunnelLoader().run();
        LoadArmorComponents.init();

        GT_RecipeRegistrator.registerUsagesForMaterials(new ItemStack(Blocks.planks, 1), null, false);
        GT_RecipeRegistrator.registerUsagesForMaterials(new ItemStack(Blocks.cobblestone, 1), null, false);
        GT_RecipeRegistrator.registerUsagesForMaterials(new ItemStack(Blocks.stone, 1), null, false);
        GT_RecipeRegistrator.registerUsagesForMaterials(new ItemStack(Items.leather, 1), null, false);

        GT_OreDictUnificator.addItemData(GT_ModHandler.getRecipeOutput(new ItemStack[]{null, GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Tin, 1L), null, GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Tin, 1L), null, GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Tin, 1L), null, null, null}), new ItemData(Materials.Tin, 10886400L, new MaterialStack[0]));
        if (!GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.storageblockcrafting, "tile.glowstone", false)) {
            GT_ModHandler.removeRecipe(new ItemStack[]{new ItemStack(Items.glowstone_dust, 1), new ItemStack(Items.glowstone_dust, 1), null, new ItemStack(Items.glowstone_dust, 1), new ItemStack(Items.glowstone_dust, 1)});
        }
        GT_ModHandler.removeRecipe(new ItemStack[]{new ItemStack(Blocks.wooden_slab, 1, 0), new ItemStack(Blocks.wooden_slab, 1, 1), new ItemStack(Blocks.wooden_slab, 1, 2)});
        GT_ModHandler.addCraftingRecipe(new ItemStack(Blocks.wooden_slab, 6, 0), GT_ModHandler.RecipeBits.NOT_REMOVABLE, new Object[]{"WWW", 'W', new ItemStack(Blocks.planks, 1, 0)});

        //Save a copy of these list before activateOreDictHandler(), then loop over them.
        Map<IRecipeInput, RecipeOutput> aMaceratorRecipeList = GT_ModHandler.getMaceratorRecipeList();
        Map<IRecipeInput, RecipeOutput> aCompressorRecipeList = GT_ModHandler.getCompressorRecipeList();
        Map<IRecipeInput, RecipeOutput> aExtractorRecipeList = GT_ModHandler.getExtractorRecipeList();
        Map<IRecipeInput, RecipeOutput> aOreWashingRecipeList = GT_ModHandler.getOreWashingRecipeList();
        Map<IRecipeInput, RecipeOutput> aThermalCentrifugeRecipeList = GT_ModHandler.getThermalCentrifugeRecipeList();

        GT_Log.out.println("GT_Mod: Activating OreDictionary Handler, this can take some time, as it scans the whole OreDictionary");
        FMLLog.info("If your Log stops here, you were too impatient. Wait a bit more next time, before killing Minecraft with the Task Manager.", new Object[0]);
        gregtechproxy.activateOreDictHandler();
        FMLLog.info("Congratulations, you have been waiting long enough. Have a Cake.", new Object[0]);
        GT_Log.out.println("GT_Mod: List of Lists of Tool Recipes: "+GT_ModHandler.sSingleNonBlockDamagableRecipeList_list.toString());
        GT_Log.out.println("GT_Mod: Vanilla Recipe List -> Outputs null or stackSize <=0: " + GT_ModHandler.sVanillaRecipeList_warntOutput.toString());
        GT_Log.out.println("GT_Mod: Single Non Block Damagable Recipe List -> Outputs null or stackSize <=0: " + GT_ModHandler.sSingleNonBlockDamagableRecipeList_warntOutput.toString());
        //GT_Log.out.println("GT_Mod: sRodMaterialList cycles: " + GT_RecipeRegistrator.sRodMaterialList_cycles);

        //Add default IC2 recipe to GT
        GT_ModHandler.addIC2RecipesToGT(aMaceratorRecipeList, GT_Recipe.GT_Recipe_Map.sMaceratorRecipes, true, true, true);
        GT_ModHandler.addIC2RecipesToGT(aCompressorRecipeList, GT_Recipe.GT_Recipe_Map.sCompressorRecipes, true, true, true);
        GT_ModHandler.addIC2RecipesToGT(aExtractorRecipeList, GT_Recipe.GT_Recipe_Map.sExtractorRecipes, true, true, true);
        GT_ModHandler.addIC2RecipesToGT(aOreWashingRecipeList, GT_Recipe.GT_Recipe_Map.sOreWasherRecipes, false, true, true);
        GT_ModHandler.addIC2RecipesToGT(aThermalCentrifugeRecipeList, GT_Recipe.GT_Recipe_Map.sThermalCentrifugeRecipes, true, true, true);

        if (GT_Values.D1) {
            IRecipe tRecipe;
            for (Iterator i$ = GT_ModHandler.sSingleNonBlockDamagableRecipeList.iterator(); i$.hasNext(); GT_Log.out.println("=> " + tRecipe.getRecipeOutput().getDisplayName())) {
                tRecipe = (IRecipe) i$.next();
            }
        }
        new GT_CraftingRecipeLoader().run();
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, "ic2forgehammer", true)) {
            GT_ModHandler.removeRecipeByOutput(ItemList.IC2_ForgeHammer.getWildcard(1L, new Object[0]));
        }
        GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item("machine", 1L));
        GT_ModHandler.addCraftingRecipe(GT_ModHandler.getIC2Item("machine", 1L), GT_ModHandler.RecipeBits.BUFFERED | GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE, new Object[]{"RRR", "RwR", "RRR", 'R', OrePrefixes.plate.get(Materials.Iron)});
        ItemStack ISdata0 = new ItemStack(Items.potionitem, 1, 0);
        ItemStack ILdata0 = ItemList.Bottle_Empty.get(1L, new Object[0]);
        for (FluidContainerRegistry.FluidContainerData tData : FluidContainerRegistry.getRegisteredFluidContainerData()) {
            if ((tData.filledContainer.getItem() == Items.potionitem) && (tData.filledContainer.getItemDamage() == 0)) {
                GT_Recipe.GT_Recipe_Map.sFluidCannerRecipes.addRecipe(true, new ItemStack[]{ILdata0}, new ItemStack[]{ISdata0}, null, new FluidStack[]{Materials.Water.getFluid(250L)}, null, 4, 1, 0);
                GT_Recipe.GT_Recipe_Map.sFluidCannerRecipes.addRecipe(true, new ItemStack[]{ISdata0}, new ItemStack[]{ILdata0}, null, null, null, 4, 1, 0);
            } else {
                GT_Recipe.GT_Recipe_Map.sFluidCannerRecipes.addRecipe(true, new ItemStack[]{tData.emptyContainer}, new ItemStack[]{tData.filledContainer}, null, new FluidStack[]{tData.fluid}, null, tData.fluid.amount / 62, 1, 0);
                GT_Recipe.GT_Recipe_Map.sFluidCannerRecipes.addRecipe(true, new ItemStack[]{tData.filledContainer}, new ItemStack[]{GT_Utility.getContainerItem(tData.filledContainer, true)}, null, null, new FluidStack[]{tData.fluid}, tData.fluid.amount / 62, 1, 0);
            }
        }
        try {
            for (ICentrifugeRecipe tRecipe : RecipeManagers.centrifugeManager.recipes()) {
                Map<ItemStack, Float> outputs = tRecipe.getAllProducts();
                ItemStack[] tOutputs = new ItemStack[outputs.size()];
                int[] tChances = new int[outputs.size()];
                int i = 0;
                for (Map.Entry<ItemStack, Float> entry : outputs.entrySet()) {
                    tChances[i] = (int) (entry.getValue() * 10000);
                    tOutputs[i] = entry.getKey().copy();
                    i++;
                }
                GT_Recipe.GT_Recipe_Map.sCentrifugeRecipes.addRecipe(true, new ItemStack[]{tRecipe.getInput()}, tOutputs, null, tChances, null, null, 128, 5, 0);
            }
        } catch (Throwable e) {
            if (GT_Values.D1) {
                e.printStackTrace(GT_Log.err);
            }
        }
        try {
            for (ISqueezerRecipe tRecipe : RecipeManagers.squeezerManager.recipes()) {
                if ((tRecipe.getResources().length == 1) && (tRecipe.getFluidOutput() != null)) {
                    GT_Recipe.GT_Recipe_Map.sFluidExtractionRecipes.addRecipe(true, new ItemStack[]{tRecipe.getResources()[0]}, new ItemStack[]{tRecipe.getRemnants()}, null, new int[]{(int) (tRecipe.getRemnantsChance() * 10000)}, null, new FluidStack[]{tRecipe.getFluidOutput()}, 400, 2, 0);
                }
            }
        } catch (Throwable e) {
            if (GT_Values.D1) {
                e.printStackTrace(GT_Log.err);
            }
        }
        String tName = "";
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "blastfurnace"), true)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "blockcutter"), true)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "inductionFurnace"), true)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "generator"), false)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "windMill"), true)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "waterMill"), true)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "solarPanel"), true)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "centrifuge"), true)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "electrolyzer"), false)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "compressor"), true)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "electroFurnace"), true)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "extractor"), true)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "macerator"), true)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "recycler"), true)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "metalformer"), true)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "orewashingplant"), true)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "massFabricator"), true)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + (tName = "replicator"), true)) {
            GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item(tName, 1L));
        }
        if (gregtechproxy.mNerfedVanillaTools) {
            GT_Log.out.println("GT_Mod: Nerfing Vanilla Tool Durability");
            Items.wooden_sword.setMaxDamage(12);
            Items.wooden_pickaxe.setMaxDamage(12);
            Items.wooden_shovel.setMaxDamage(12);
            Items.wooden_axe.setMaxDamage(12);
            Items.wooden_hoe.setMaxDamage(12);

            Items.stone_sword.setMaxDamage(48);
            Items.stone_pickaxe.setMaxDamage(48);
            Items.stone_shovel.setMaxDamage(48);
            Items.stone_axe.setMaxDamage(48);
            Items.stone_hoe.setMaxDamage(48);

            Items.iron_sword.setMaxDamage(256);
            Items.iron_pickaxe.setMaxDamage(256);
            Items.iron_shovel.setMaxDamage(256);
            Items.iron_axe.setMaxDamage(256);
            Items.iron_hoe.setMaxDamage(256);

            Items.golden_sword.setMaxDamage(24);
            Items.golden_pickaxe.setMaxDamage(24);
            Items.golden_shovel.setMaxDamage(24);
            Items.golden_axe.setMaxDamage(24);
            Items.golden_hoe.setMaxDamage(24);

            Items.diamond_sword.setMaxDamage(768);
            Items.diamond_pickaxe.setMaxDamage(768);
            Items.diamond_shovel.setMaxDamage(768);
            Items.diamond_axe.setMaxDamage(768);
            Items.diamond_hoe.setMaxDamage(768);
        }
        GT_Log.out.println("GT_Mod: Adding buffered Recipes.");
        GT_ModHandler.stopBufferingCraftingRecipes();

        GT_Log.out.println("GT_Mod: Saving Lang File.");
        GT_LanguageManager.sEnglishFile.save();
        GregTech_API.sPostloadFinished = true;
        GT_Log.out.println("GT_Mod: PostLoad-Phase finished!");
        GT_Log.ore.println("GT_Mod: PostLoad-Phase finished!");
        try {
            for (Runnable tRunnable : GregTech_API.sAfterGTPostload) {
                tRunnable.run();
            }
        } catch (Throwable e) {e.printStackTrace(GT_Log.err);}
        GT_Log.out.println("GT_Mod: Adding Fake Recipes for NEI");
        if (ItemList.FR_Bee_Drone.get(1L, new Object[0]) != null) {
            GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.FR_Bee_Drone.getWildcard(1L, new Object[0])}, new ItemStack[]{ItemList.FR_Bee_Drone.getWithName(1L, "Scanned Drone", new Object[0])}, null, new FluidStack[]{Materials.Honey.getFluid(100L)}, null, 500, 2, 0);
        }
        if (ItemList.FR_Bee_Princess.get(1L, new Object[0]) != null) {
            GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.FR_Bee_Princess.getWildcard(1L, new Object[0])}, new ItemStack[]{ItemList.FR_Bee_Princess.getWithName(1L, "Scanned Princess", new Object[0])}, null, new FluidStack[]{Materials.Honey.getFluid(100L)}, null, 500, 2, 0);
        }
        if (ItemList.FR_Bee_Queen.get(1L, new Object[0]) != null) {
            GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.FR_Bee_Queen.getWildcard(1L, new Object[0])}, new ItemStack[]{ItemList.FR_Bee_Queen.getWithName(1L, "Scanned Queen", new Object[0])}, null, new FluidStack[]{Materials.Honey.getFluid(100L)}, null, 500, 2, 0);
        }
        if (ItemList.FR_Tree_Sapling.get(1L, new Object[0]) != null) {
            GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.FR_Tree_Sapling.getWildcard(1L, new Object[0])}, new ItemStack[]{ItemList.FR_Tree_Sapling.getWithName(1L, "Scanned Sapling", new Object[0])}, null, new FluidStack[]{Materials.Honey.getFluid(100L)}, null, 500, 2, 0);
        }
        if (ItemList.FR_Butterfly.get(1L, new Object[0]) != null) {
            GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.FR_Butterfly.getWildcard(1L, new Object[0])}, new ItemStack[]{ItemList.FR_Butterfly.getWithName(1L, "Scanned Butterfly", new Object[0])}, null, new FluidStack[]{Materials.Honey.getFluid(100L)}, null, 500, 2, 0);
        }
        if (ItemList.FR_Larvae.get(1L, new Object[0]) != null) {
            GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.FR_Larvae.getWildcard(1L, new Object[0])}, new ItemStack[]{ItemList.FR_Larvae.getWithName(1L, "Scanned Larvae", new Object[0])}, null, new FluidStack[]{Materials.Honey.getFluid(100L)}, null, 500, 2, 0);
        }
        if (ItemList.FR_Serum.get(1L, new Object[0]) != null) {
            GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.FR_Serum.getWildcard(1L, new Object[0])}, new ItemStack[]{ItemList.FR_Serum.getWithName(1L, "Scanned Serum", new Object[0])}, null, new FluidStack[]{Materials.Honey.getFluid(100L)}, null, 500, 2, 0);
        }
        if (ItemList.FR_Caterpillar.get(1L, new Object[0]) != null) {
            GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.FR_Caterpillar.getWildcard(1L, new Object[0])}, new ItemStack[]{ItemList.FR_Caterpillar.getWithName(1L, "Scanned Caterpillar", new Object[0])}, null, new FluidStack[]{Materials.Honey.getFluid(100L)}, null, 500, 2, 0);
        }
        if (ItemList.FR_PollenFertile.get(1L, new Object[0]) != null) {
            GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.FR_PollenFertile.getWildcard(1L, new Object[0])}, new ItemStack[]{ItemList.FR_PollenFertile.getWithName(1L, "Scanned Pollen", new Object[0])}, null, new FluidStack[]{Materials.Honey.getFluid(100L)}, null, 500, 2, 0);
        }
        if (ItemList.IC2_Crop_Seeds.get(1L, new Object[0]) != null) {
            GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.IC2_Crop_Seeds.getWildcard(1L, new Object[0])}, new ItemStack[]{ItemList.IC2_Crop_Seeds.getWithName(1L, "Scanned Seeds", new Object[0])}, null, null, null, 160, 8, 0);
        }
        GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{new ItemStack(Items.written_book, 1, 32767)}, new ItemStack[]{ItemList.Tool_DataStick.getWithName(1L, "Scanned Book Data", new Object[0])}, ItemList.Tool_DataStick.getWithName(1L, "Stick to save it to", new Object[0]), null, null, 128, 32, 0);
        GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{new ItemStack(Items.filled_map, 1, 32767)}, new ItemStack[]{ItemList.Tool_DataStick.getWithName(1L, "Scanned Map Data", new Object[0])}, ItemList.Tool_DataStick.getWithName(1L, "Stick to save it to", new Object[0]), null, null, 128, 32, 0);
        GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.Tool_DataOrb.getWithName(1L, "Orb to overwrite", new Object[0])}, new ItemStack[]{ItemList.Tool_DataOrb.getWithName(1L, "Copy of the Orb", new Object[0])}, ItemList.Tool_DataOrb.getWithName(0L, "Orb to copy", new Object[0]), null, null, 512, 32, 0);
        GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.Tool_DataStick.getWithName(1L, "Stick to overwrite", new Object[0])}, new ItemStack[]{ItemList.Tool_DataStick.getWithName(1L, "Copy of the Stick", new Object[0])}, ItemList.Tool_DataStick.getWithName(0L, "Stick to copy", new Object[0]), null, null, 128, 32, 0);
        GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.Tool_DataStick.getWithName(1L, "Raw Prospection Data", new Object[0])}, new ItemStack[]{ItemList.Tool_DataStick.getWithName(1L, "Analyzed Prospection Data", new Object[0])}, null, null, null, 1000, 32, 0);
        for (Materials tMaterial : Materials.values()) {
            if ((tMaterial.mElement != null) && (!tMaterial.mElement.mIsIsotope) && (tMaterial != Materials.Magic) && (tMaterial.getMass() > 0L)) {
                ItemStack tOutput = ItemList.Tool_DataOrb.get(1L, new Object[0]);
                Behaviour_DataOrb.setDataTitle(tOutput, "Elemental-Scan");
                Behaviour_DataOrb.setDataName(tOutput, tMaterial.mElement.name());
                ItemStack tInput = GT_OreDictUnificator.get(OrePrefixes.dust, tMaterial, 1L);
                ItemStack[] ISmat0 = new ItemStack[]{tInput};
                ItemStack[] ISmat1 = new ItemStack[]{tOutput};
                if (tInput != null) {
                    GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, ISmat0, ISmat1, ItemList.Tool_DataOrb.get(1L, new Object[0]), null, null, (int) (tMaterial.getMass() * 8192L), 32, 0);
                    GT_Recipe.GT_Recipe_Map.sReplicatorFakeRecipes.addFakeRecipe(false, null, ISmat0, ISmat1, new FluidStack[]{Materials.UUMatter.getFluid(tMaterial.getMass())}, null, (int) (tMaterial.getMass() * 512L), 32, 0);

                }
                tInput = GT_OreDictUnificator.get(OrePrefixes.cell, tMaterial, 1L);
                ISmat0 = new ItemStack[]{tInput};
                if (tInput != null) {
                    GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, ISmat0, ISmat1, ItemList.Tool_DataOrb.get(1L, new Object[0]), null, null, (int) (tMaterial.getMass() * 8192L), 32, 0);
                    GT_Recipe.GT_Recipe_Map.sReplicatorFakeRecipes.addFakeRecipe(false, null, ISmat0, ISmat1, new FluidStack[]{Materials.UUMatter.getFluid(tMaterial.getMass())}, null, (int) (tMaterial.getMass() * 512L), 32, 0);
                }
            }
        }
        if (!GT_MetaTileEntity_Massfabricator.sRequiresUUA) GT_Recipe.GT_Recipe_Map.sMassFabFakeRecipes.addFakeRecipe(false, null, null, null, null, new FluidStack[]{Materials.UUMatter.getFluid(1L)}, GT_MetaTileEntity_Massfabricator.sDurationMultiplier, 256, 0);
        GT_Recipe.GT_Recipe_Map.sMassFabFakeRecipes.addFakeRecipe(false, null, null, null, new FluidStack[]{Materials.UUAmplifier.getFluid(GT_MetaTileEntity_Massfabricator.sUUAperUUM)}, new FluidStack[]{Materials.UUMatter.getFluid(1L)}, GT_MetaTileEntity_Massfabricator.sDurationMultiplier / GT_MetaTileEntity_Massfabricator.sUUASpeedBonus, 256, 0);
        GT_Recipe.GT_Recipe_Map.sRockBreakerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.Display_ITS_FREE.getWithName(0L, "Place Lava on Side", new Object[0])}, new ItemStack[]{new ItemStack(Blocks.cobblestone, 1)}, null, null, null, 16, 32, 0);
        GT_Recipe.GT_Recipe_Map.sRockBreakerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.Display_ITS_FREE.getWithName(0L, "Place Lava on Top", new Object[0])}, new ItemStack[]{new ItemStack(Blocks.stone, 1)}, null, null, null, 16, 32, 0);
        GT_Recipe.GT_Recipe_Map.sRockBreakerFakeRecipes.addFakeRecipe(false, new ItemStack[]{GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1L)}, new ItemStack[]{new ItemStack(Blocks.obsidian, 1)}, null, null, null, 128, 32, 0);

        if(GregTech_API.mOutputRF||GregTech_API.mInputRF){
            GT_Utility.checkAvailabilities();
            if(!GT_Utility.RF_CHECK){
                GregTech_API.mOutputRF = false;
                GregTech_API.mInputRF = false;
            }
        }
        
        addSolidFakeLargeBoilerFuels();
        
        achievements = new GT_Achievements();
        GT_Log.out.println("GT_Mod: Loading finished, deallocating temporary Init Variables.");
        GregTech_API.sBeforeGTPreload = null;
        GregTech_API.sAfterGTPreload = null;
        GregTech_API.sBeforeGTLoad = null;
        GregTech_API.sAfterGTLoad = null;
        GregTech_API.sBeforeGTPostload = null;
        GregTech_API.sAfterGTPostload = null;
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent aEvent) {
        try {
            for (Runnable tRunnable : GregTech_API.sBeforeGTServerstart) {
                tRunnable.run();
            }
        } catch (Throwable e) {e.printStackTrace(GT_Log.err);}
        gregtechproxy.onServerStarting();
        //Check for more IC2 recipes on ServerStart to also catch MineTweaker additions
        GT_ModHandler.addIC2RecipesToGT(GT_ModHandler.getMaceratorRecipeList(), GT_Recipe.GT_Recipe_Map.sMaceratorRecipes, true, true, true);
        GT_ModHandler.addIC2RecipesToGT(GT_ModHandler.getCompressorRecipeList(), GT_Recipe.GT_Recipe_Map.sCompressorRecipes, true, true, true);
        GT_ModHandler.addIC2RecipesToGT(GT_ModHandler.getExtractorRecipeList(), GT_Recipe.GT_Recipe_Map.sExtractorRecipes, true, true, true);
        GT_ModHandler.addIC2RecipesToGT(GT_ModHandler.getOreWashingRecipeList(), GT_Recipe.GT_Recipe_Map.sOreWasherRecipes, false, true, true);
        GT_ModHandler.addIC2RecipesToGT(GT_ModHandler.getThermalCentrifugeRecipeList(), GT_Recipe.GT_Recipe_Map.sThermalCentrifugeRecipes, true, true, true);
        GT_Log.out.println("GT_Mod: Unificating outputs of all known Recipe Types.");
        ArrayList<ItemStack> tStacks = new ArrayList(10000);
        GT_Log.out.println("GT_Mod: IC2 Machines");
        for (RecipeOutput tRecipe : ic2.api.recipe.Recipes.cannerBottle.getRecipes().values()) {
            ItemStack tStack;
            for (Iterator i$ = tRecipe.items.iterator(); i$.hasNext(); tStacks.add(tStack)) {
                tStack = (ItemStack) i$.next();
            }
        }
        for (RecipeOutput tRecipe : ic2.api.recipe.Recipes.centrifuge.getRecipes().values()) {
            ItemStack tStack;
            for (Iterator i$ = tRecipe.items.iterator(); i$.hasNext(); tStacks.add(tStack)) {
                tStack = (ItemStack) i$.next();
            }
        }
        for (RecipeOutput tRecipe : ic2.api.recipe.Recipes.compressor.getRecipes().values()) {
            ItemStack tStack;
            for (Iterator i$ = tRecipe.items.iterator(); i$.hasNext(); tStacks.add(tStack)) {
                tStack = (ItemStack) i$.next();
            }
        }
        for (RecipeOutput tRecipe : ic2.api.recipe.Recipes.extractor.getRecipes().values()) {
            ItemStack tStack;
            for (Iterator i$ = tRecipe.items.iterator(); i$.hasNext(); tStacks.add(tStack)) {
                tStack = (ItemStack) i$.next();
            }
        }
        for (RecipeOutput tRecipe : ic2.api.recipe.Recipes.macerator.getRecipes().values()) {
            ItemStack tStack;
            for (Iterator i$ = tRecipe.items.iterator(); i$.hasNext(); tStacks.add(tStack)) {
                tStack = (ItemStack) i$.next();
            }
        }
        for (RecipeOutput tRecipe : ic2.api.recipe.Recipes.metalformerCutting.getRecipes().values()) {
            ItemStack tStack;
            for (Iterator i$ = tRecipe.items.iterator(); i$.hasNext(); tStacks.add(tStack)) {
                tStack = (ItemStack) i$.next();
            }
        }
        for (RecipeOutput tRecipe : ic2.api.recipe.Recipes.metalformerExtruding.getRecipes().values()) {
            ItemStack tStack;
            for (Iterator i$ = tRecipe.items.iterator(); i$.hasNext(); tStacks.add(tStack)) {
                tStack = (ItemStack) i$.next();
            }
        }
        for (RecipeOutput tRecipe : ic2.api.recipe.Recipes.metalformerRolling.getRecipes().values()) {
            ItemStack tStack;
            for (Iterator i$ = tRecipe.items.iterator(); i$.hasNext(); tStacks.add(tStack)) {
                tStack = (ItemStack) i$.next();
            }
        }
        for (RecipeOutput tRecipe : ic2.api.recipe.Recipes.matterAmplifier.getRecipes().values()) {
            ItemStack tStack;
            for (Iterator i$ = tRecipe.items.iterator(); i$.hasNext(); tStacks.add(tStack)) {
                tStack = (ItemStack) i$.next();
            }
        }
        for (RecipeOutput tRecipe : ic2.api.recipe.Recipes.oreWashing.getRecipes().values()) {
            ItemStack tStack;
            for (Iterator i$ = tRecipe.items.iterator(); i$.hasNext(); tStacks.add(tStack)) {
                tStack = (ItemStack) i$.next();
            }
        }
        GT_Log.out.println("GT_Mod: Dungeon Loot");
        for (WeightedRandomChestContent tContent : ChestGenHooks.getInfo("dungeonChest").getItems(new XSTR())) {
            tStacks.add(tContent.theItemId);
        }
        for (WeightedRandomChestContent tContent : ChestGenHooks.getInfo("bonusChest").getItems(new XSTR())) {
            tStacks.add(tContent.theItemId);
        }
        for (WeightedRandomChestContent tContent : ChestGenHooks.getInfo("villageBlacksmith").getItems(new XSTR())) {
            tStacks.add(tContent.theItemId);
        }
        for (WeightedRandomChestContent tContent : ChestGenHooks.getInfo("strongholdCrossing").getItems(new XSTR())) {
            tStacks.add(tContent.theItemId);
        }
        for (WeightedRandomChestContent tContent : ChestGenHooks.getInfo("strongholdLibrary").getItems(new XSTR())) {
            tStacks.add(tContent.theItemId);
        }
        for (WeightedRandomChestContent tContent : ChestGenHooks.getInfo("strongholdCorridor").getItems(new XSTR())) {
            tStacks.add(tContent.theItemId);
        }
        for (WeightedRandomChestContent tContent : ChestGenHooks.getInfo("pyramidJungleDispenser").getItems(new XSTR())) {
            tStacks.add(tContent.theItemId);
        }
        for (WeightedRandomChestContent tContent : ChestGenHooks.getInfo("pyramidJungleChest").getItems(new XSTR())) {
            tStacks.add(tContent.theItemId);
        }
        for (WeightedRandomChestContent tContent : ChestGenHooks.getInfo("pyramidDesertyChest").getItems(new XSTR())) {
            tStacks.add(tContent.theItemId);
        }
        for (WeightedRandomChestContent tContent : ChestGenHooks.getInfo("mineshaftCorridor").getItems(new XSTR())) {
            tStacks.add(tContent.theItemId);
        }
        GT_Log.out.println("GT_Mod: Smelting");
        Object tStack;
        for (Iterator i$ = FurnaceRecipes.smelting().getSmeltingList().values().iterator(); i$.hasNext(); tStacks.add((ItemStack) tStack)) {
            tStack = i$.next();
        }
        if (gregtechproxy.mCraftingUnification) {
            GT_Log.out.println("GT_Mod: Crafting Recipes");
            for (Object tRecipe : CraftingManager.getInstance().getRecipeList()) {
                if ((tRecipe instanceof IRecipe)) {
                    tStacks.add(((IRecipe) tRecipe).getRecipeOutput());
                }
            }
        }
        for (ItemStack tOutput : tStacks) {
            if (gregtechproxy.mRegisteredOres.contains(tOutput)) {
                FMLLog.severe("GT-ERR-01: @ " + tOutput.getUnlocalizedName() + "   " + tOutput.getDisplayName(), new Object[0]);
                FMLLog.severe("A Recipe used an OreDict Item as Output directly, without copying it before!!! This is a typical CallByReference/CallByValue Error", new Object[0]);
                FMLLog.severe("Said Item will be renamed to make the invalid Recipe visible, so that you can report it properly.", new Object[0]);
                FMLLog.severe("Please check all Recipes outputting this Item, and report the Recipes to their Owner.", new Object[0]);
                FMLLog.severe("The Owner of the ==>RECIPE<==, NOT the Owner of the Item, which has been mentioned above!!!", new Object[0]);
                FMLLog.severe("And ONLY Recipes which are ==>OUTPUTTING<== the Item, sorry but I don't want failed Bug Reports.", new Object[0]);
                FMLLog.severe("GregTech just reports this Error to you, so you can report it to the Mod causing the Problem.", new Object[0]);
                FMLLog.severe("Even though I make that Bug visible, I can not and will not fix that for you, that's for the causing Mod to fix.", new Object[0]);
                FMLLog.severe("And speaking of failed Reports:", new Object[0]);
                FMLLog.severe("Both IC2 and GregTech CANNOT be the CAUSE of this Problem, so don't report it to either of them.", new Object[0]);
                FMLLog.severe("I REPEAT, BOTH, IC2 and GregTech CANNOT be the source of THIS BUG. NO MATTER WHAT.", new Object[0]);
                FMLLog.severe("Asking in the IC2 Forums, which Mod is causing that, won't help anyone, since it is not possible to determine, which Mod it is.", new Object[0]);
                FMLLog.severe("If it would be possible, then I would have had added the Mod which is causing it to the Message already. But it is not possible.", new Object[0]);
                FMLLog.severe("Sorry, but this Error is serious enough to justify this Wall-O-Text and the partially allcapsed Language.", new Object[0]);
                FMLLog.severe("Also it is a Ban Reason on the IC2-Forums to post this seriously.", new Object[0]);
                tOutput.setStackDisplayName("ERROR! PLEASE CHECK YOUR LOG FOR 'GT-ERR-01'!");
            } else {
                GT_OreDictUnificator.setStack(tOutput);
            }
        }
        GregTech_API.mServerStarted = true;
        GT_Log.out.println("GT_Mod: ServerStarting-Phase finished!");
        GT_Log.ore.println("GT_Mod: ServerStarting-Phase finished!");
        try {
            for (Runnable tRunnable : GregTech_API.sAfterGTServerstart) {
                tRunnable.run();
            }
        } catch (Throwable e) {e.printStackTrace(GT_Log.err);}
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent aEvent) {
        gregtechproxy.onServerStarted();
    }

    @Mod.EventHandler
    public void onIDChangingEvent(FMLModIdMappingEvent aEvent) {
        GT_Utility.reInit();
        GT_Recipe.reInit();
        try {
            for (Iterator i$ = GregTech_API.sItemStackMappings.iterator(); i$.hasNext(); ) {
                Map tMap = (Map) i$.next();
                GT_Utility.reMap(tMap);
            }
        } catch (Throwable e) {e.printStackTrace(GT_Log.err);}

    }
//  public void onIDChangingEvent(FMLModIdMappingEvent aEvent)
//  {
//    GT_Utility.reInit();
//    GT_Recipe.reInit();
//    Map<GT_ItemStack, ?> tMap;
//    for (Iterator i$ = GregTech_API.sItemStackMappings.iterator(); i$.hasNext(); ) {
//      tMap = (Map)i$.next();
//    }
//  }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent aEvent) {
        try {
            for (Runnable tRunnable : GregTech_API.sBeforeGTServerstop) {
                tRunnable.run();
            }
        } catch (Throwable e) {e.printStackTrace(GT_Log.err);}
        gregtechproxy.onServerStopping();
        try {
            if ((GT_Values.D1) || (GT_Log.out != System.out)) {
                GT_Log.out.println("*");
                GT_Log.out.println("Printing List of all registered Objects inside the OreDictionary, now with free extra Sorting:");
                GT_Log.out.println("*");
                GT_Log.out.println("*");
                GT_Log.out.println("*");

                String[] tList = OreDictionary.getOreNames();
                Arrays.sort(tList);
                for (String tOreName : tList) {
                    int tAmount = OreDictionary.getOres(tOreName).size();
                    if (tAmount > 0) {
                        GT_Log.out.println((tAmount < 10 ? " " : "") + tAmount + "x " + tOreName);
                    }
                }
                GT_Log.out.println("*");
                GT_Log.out.println("Printing List of all registered Objects inside the Fluid Registry, now with free extra Sorting:");
                GT_Log.out.println("*");
                GT_Log.out.println("*");
                GT_Log.out.println("*");

                tList = (String[]) FluidRegistry.getRegisteredFluids().keySet().toArray(new String[FluidRegistry.getRegisteredFluids().keySet().size()]);
                Arrays.sort(tList);
                for (String tFluidName : tList) {
                    GT_Log.out.println(tFluidName);
                }
                GT_Log.out.println("*");
                GT_Log.out.println("*");
                GT_Log.out.println("*");
                GT_Log.out.println("Outputting all the Names inside the Biomeslist");
                GT_Log.out.println("*");
                GT_Log.out.println("*");
                GT_Log.out.println("*");
                for (int i = 0; i < BiomeGenBase.getBiomeGenArray().length; i++) {
                    if (BiomeGenBase.getBiomeGenArray()[i] != null) {
                        GT_Log.out.println(BiomeGenBase.getBiomeGenArray()[i].biomeID + " = " + BiomeGenBase.getBiomeGenArray()[i].biomeName);
                    }
                }
                GT_Log.out.println("*");
                GT_Log.out.println("*");
                GT_Log.out.println("*");
                GT_Log.out.println("Printing List of generatable Materials");
                GT_Log.out.println("*");
                GT_Log.out.println("*");
                GT_Log.out.println("*");
                for (int i = 0; i < GregTech_API.sGeneratedMaterials.length; i++) {
                    if (GregTech_API.sGeneratedMaterials[i] == null) {
                        GT_Log.out.println("Index " + i + ":" + null);
                    } else {
                        GT_Log.out.println("Index " + i + ":" + GregTech_API.sGeneratedMaterials[i]);
                    }
                }
                GT_Log.out.println("*");
                GT_Log.out.println("*");
                GT_Log.out.println("*");
                GT_Log.out.println("END GregTech-Debug");
                GT_Log.out.println("*");
                GT_Log.out.println("*");
                GT_Log.out.println("*");
            }
        } catch (Throwable e) {
            if (GT_Values.D1) {
                e.printStackTrace(GT_Log.err);
            }
        }
        try {
            for (Runnable tRunnable : GregTech_API.sAfterGTServerstop) {
                tRunnable.run();
            }
        } catch (Throwable e) {e.printStackTrace(GT_Log.err);}
    }

    public boolean isServerSide() {
        return gregtechproxy.isServerSide();
    }

    public boolean isClientSide() {
        return gregtechproxy.isClientSide();
    }

    public boolean isBukkitSide() {
        return gregtechproxy.isBukkitSide();
    }

    public EntityPlayer getThePlayer() {
        return gregtechproxy.getThePlayer();
    }

    public int addArmor(String aArmorPrefix) {
        return gregtechproxy.addArmor(aArmorPrefix);
    }

    public void doSonictronSound(ItemStack aStack, World aWorld, double aX, double aY, double aZ) {
        gregtechproxy.doSonictronSound(aStack, aWorld, aX, aY, aZ);
    }
    
    private void addSolidFakeLargeBoilerFuels(){
        GT_Recipe.GT_Recipe_Map.sLargeBoilerFakeFuels.addSolidRecipes(
        		GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Charcoal, 1),
        		GT_OreDictUnificator.get(OrePrefixes.gem, Materials.Charcoal, 1),
        		GT_OreDictUnificator.get(OrePrefixes.block, Materials.Charcoal, 1),
        		GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Coal, 1),
        		GT_OreDictUnificator.get(OrePrefixes.gem, Materials.Coal, 1),
        		GT_OreDictUnificator.get(OrePrefixes.block, Materials.Coal, 1),
        		GT_OreDictUnificator.get(OrePrefixes.crushed, Materials.Coal, 1),
        		GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Lignite, 1),
        		GT_OreDictUnificator.get(OrePrefixes.gem, Materials.Lignite, 1),
        		GT_OreDictUnificator.get(OrePrefixes.block, Materials.Lignite, 1),
        		GT_OreDictUnificator.get(OrePrefixes.crushed, Materials.Lignite, 1),
        		GT_OreDictUnificator.get(OrePrefixes.log, Materials.Wood, 1),
        		GT_OreDictUnificator.get(OrePrefixes.plank, Materials.Wood, 1),
        		GT_OreDictUnificator.get(OrePrefixes.stick, Materials.Wood, 1),
        		GT_OreDictUnificator.get(OrePrefixes.slab, Materials.Wood, 1),
        		GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Wood, 1),
        		GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Sodium, 1),
        		GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Lithium, 1),
        		GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Caesium, 1),
        		GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Sulfur, 1),
        		GT_OreDictUnificator.get(ItemList.Block_SSFUEL.get(1, new Object[0])),
        		GT_OreDictUnificator.get(ItemList.Block_MSSFUEL.get(1, new Object[0])),
        		GT_OreDictUnificator.get(OrePrefixes.rod, Materials.Blaze, 1));
        if (Loader.isModLoaded("Thaumcraft")) {
        	GT_Recipe.GT_Recipe_Map.sLargeBoilerFakeFuels.addSolidRecipe(GT_ModHandler.getModItem("Thaumcraft", "ItemResource", 1));
        }
    }
    
    public static int calculateTotalGTVersion(int minorVersion){
    	return calculateTotalGTVersion(VERSION, minorVersion);
    }

    public static int calculateTotalGTVersion(int majorVersion, int minorVersion){
    	return majorVersion * 1000 + minorVersion;
    }
}
