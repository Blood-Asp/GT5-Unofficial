package gregtech;

import com.google.common.base.Stopwatch;
import com.gtnewhorizon.structurelib.StructureLib;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTech_API;
import gregtech.api.enchants.Enchantment_EnderDamage;
import gregtech.api.enchants.Enchantment_Radioactivity;
import gregtech.api.enums.*;
import gregtech.api.interfaces.internal.IGT_Mod;
import gregtech.api.objects.ItemData;
import gregtech.api.objects.ReverseShapedRecipe;
import gregtech.api.objects.ReverseShapelessRecipe;
import gregtech.api.objects.XSTR;
import gregtech.api.threads.GT_Runnable_MachineBlockUpdate;
import gregtech.api.util.*;
import gregtech.common.GT_DummyWorld;
import gregtech.common.GT_Network;
import gregtech.common.GT_Proxy;
import gregtech.common.GT_RecipeAdder;
import gregtech.common.entities.GT_Entity_Arrow;
import gregtech.common.entities.GT_Entity_Arrow_Potion;
import gregtech.common.items.GT_MetaGenerated_Tool_01;
import gregtech.common.items.behaviors.Behaviour_DataOrb;
import gregtech.common.misc.GT_Command;
import gregtech.common.tileentities.machines.basic.GT_MetaTileEntity_Massfabricator;
import gregtech.common.tileentities.machines.long_distance.GT_MetaTileEntity_LongDistancePipelineBase;
import gregtech.common.tileentities.machines.multi.GT_MetaTileEntity_Cleanroom;
import gregtech.common.tileentities.storage.GT_MetaTileEntity_DigitalChestBase;
import gregtech.loaders.ExtraIcons;
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
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static gregtech.api.enums.GT_Values.MOD_ID_AE;
import static gregtech.api.enums.GT_Values.MOD_ID_FR;

@SuppressWarnings("ALL")
@Mod(modid = "gregtech", name = "GregTech5-Unofficial", version = "MC1710", useMetadata = false,
        dependencies = " required-after:IC2;" +
                " required-after:structurelib;" +
                " after:dreamcraft;" +
                " after:Forestry;" +
                " after:PFAAGeologica;" +
                " after:Thaumcraft;" +
                " after:Railcraft;" +
                " after:appliedenergistics2;" +
                " after:ThermalExpansion;" +
                " after:TwilightForest;" +
                " after:harvestcraft;" +
                " after:magicalcrops;" +
                " after:BuildCraft|Transport;" +
                " after:BuildCraft|Silicon;" +
                " after:BuildCraft|Factory;" +
                " after:BuildCraft|Energy;" +
                " after:BuildCraft|Core;" +
                " after:BuildCraft|Builders;" +
                " after:GalacticraftCore;" +
                " after:GalacticraftMars;" +
                " after:GalacticraftPlanets;" +
                " after:ThermalExpansion|Transport;" +
                " after:ThermalExpansion|Energy;" +
                " after:ThermalExpansion|Factory;" +
                " after:RedPowerCore;" +
                " after:RedPowerBase;" +
                " after:RedPowerMachine;" +
                " after:RedPowerCompat;" +
                " after:RedPowerWiring;" +
                " after:RedPowerLogic;" +
                " after:RedPowerLighting;" +
                " after:RedPowerWorld;" +
                " after:RedPowerControl;" +
                " after:UndergroundBiomes;" +
                " after:TConstruct;" +
                " after:Translocator;")
public class GT_Mod implements IGT_Mod {

    @Deprecated // Keep for use in BaseMetaTileEntity
    public static final int VERSION = 509, SUBVERSION = 40;
    @Deprecated
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
    public static final Logger GT_FML_LOGGER = LogManager.getLogger("GregTech GTNH");


    static {
        if ((509 != GregTech_API.VERSION) || (509 != GT_ModHandler.VERSION) || (509 != GT_OreDictUnificator.VERSION) || (509 != GT_Recipe.VERSION) || (509 != GT_Utility.VERSION) || (509 != GT_RecipeRegistrator.VERSION) || (509 != Element.VERSION) || (509 != Materials.VERSION) || (509 != OrePrefixes.VERSION)) {
            throw new GT_ItsNotMyFaultException("One of your Mods included GregTech-API Files inside it's download, mention this to the Mod Author, who does this bad thing, and tell him/her to use reflection. I have added a Version check, to prevent Authors from breaking my Mod that way.");
        }
    }

    public GT_Mod() {
        try {
            Class.forName("ic2.core.IC2").getField("enableOreDictCircuit").set(null, Boolean.FALSE);
        } catch (Throwable ignored) {
        }
        try {
            Class.forName("ic2.core.IC2").getField("enableCraftingBucket").set(null, Boolean.FALSE);
        } catch (Throwable ignored) {
        }
        try {
            Class.forName("ic2.core.IC2").getField("enableEnergyInStorageBlockItems").set(null, Boolean.FALSE);
        } catch (Throwable ignored) {
        }
        GT_Values.GT = this;
        GT_Values.DW = new GT_DummyWorld();
        GT_Values.NW = new GT_Network();
        GregTech_API.sRecipeAdder = GT_Values.RA = new GT_RecipeAdder();

        Textures.BlockIcons.VOID.name();
        Textures.ItemIcons.VOID.name();
    }

    public static int calculateTotalGTVersion(int minorVersion) {
        return calculateTotalGTVersion(VERSION, minorVersion);
    }

    public static int calculateTotalGTVersion(int majorVersion, int minorVersion) {
        return majorVersion * 1000 + minorVersion;

    }

    @Mod.EventHandler
    public void onPreLoad(FMLPreInitializationEvent aEvent) {
        Locale.setDefault(Locale.ENGLISH);
        if (GregTech_API.sPreloadStarted) {
            return;
        }

        for (Runnable tRunnable : GregTech_API.sBeforeGTPreload) {
            try {
                tRunnable.run();
            } catch (Throwable e) {
                e.printStackTrace(GT_Log.err);
            }
        }

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            MinecraftForge.EVENT_BUS.register(new ExtraIcons());
        }

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

        GregTech_API.sClientDataFile = new GT_Config(new Configuration(new File(new File(aEvent.getModConfigurationDirectory(), "GregTech"), "Client.cfg")));
        GregTech_API.mIC2Classic = Loader.isModLoaded("IC2-Classic-Spmod");
        GregTech_API.mGTPlusPlus = Loader.isModLoaded("miscutils");
        GregTech_API.mTranslocator = Loader.isModLoaded("Translocator");
        GregTech_API.mTConstruct = Loader.isModLoaded("TConstruct");
        GregTech_API.mGalacticraft = Loader.isModLoaded("GalacticraftCore");
        GregTech_API.mAE2 = Loader.isModLoaded(MOD_ID_AE);
        GT_Log.out.println("GT_Mod: Are you there Translocator? " + GregTech_API.mTranslocator);
        GT_Log.out.println("GT_Mod: Are you there TConstruct? " + GregTech_API.mTConstruct);
        GT_Log.out.println("GT_Mod: Are you there GalacticraftCore? " + GregTech_API.mGalacticraft);

        GT_Log.mLogFile = new File(aEvent.getModConfigurationDirectory().getParentFile(), "logs/GregTech.log");
        if (!GT_Log.mLogFile.exists()) {
            try {
                GT_Log.mLogFile.createNewFile();
            } catch (Throwable ignored) {
            }
        }
        try {
            GT_Log.out = GT_Log.err = new PrintStream(GT_Log.mLogFile);
        } catch (FileNotFoundException ignored) {
        }
        GT_Log.mOreDictLogFile = new File(aEvent.getModConfigurationDirectory().getParentFile(), "logs/OreDict.log");
        if (!GT_Log.mOreDictLogFile.exists()) {
            try {
                GT_Log.mOreDictLogFile.createNewFile();
            } catch (Throwable ignored) {
            }
        }
        if (tMainConfig.get(aTextGeneral, "LoggingExplosions", true).getBoolean(true)) {
            GT_Log.mExplosionLog = new File(aEvent.getModConfigurationDirectory().getParentFile(), "logs/Explosion.log");
            if (!GT_Log.mExplosionLog.exists()) {
                try {
                    GT_Log.mExplosionLog.createNewFile();
                } catch (Throwable ignored) {
                }
            }
            try {
                GT_Log.exp = new PrintStream(GT_Log.mExplosionLog);
            } catch (Throwable ignored) {
            }
        }

        if (tMainConfig.get(aTextGeneral, "LoggingPlayerActivity", true).getBoolean(true)) {
            GT_Log.mPlayerActivityLogFile = new File(aEvent.getModConfigurationDirectory().getParentFile(), "logs/PlayerActivity.log");
            if (!GT_Log.mPlayerActivityLogFile.exists()) {
                try {
                    GT_Log.mPlayerActivityLogFile.createNewFile();
                } catch (Throwable ignored) {
                }
            }
            try {
                GT_Log.pal = new PrintStream(GT_Log.mPlayerActivityLogFile);
            } catch (Throwable ignored) {
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
            tList.forEach(GT_Log.ore::println);
        } catch (Throwable ignored) {
        }
        gregtechproxy.onPreLoad();

        GT_Log.out.println("GT_Mod: Setting Configs");

        GT_Values.D1 = tMainConfig.get(aTextGeneral, "Debug", false).getBoolean(false);
        GT_Values.D2 = tMainConfig.get(aTextGeneral, "Debug2", false).getBoolean(false);
        GT_Values.hideAssLineRecipes = tMainConfig.get(aTextGeneral, "hide assline recipes", false).getBoolean(false);
        GT_Values.updateFluidDisplayItems = tMainConfig.get(aTextGeneral, "update fluid display items", true).getBoolean(true);
        GT_Values.allow_broken_recipemap = tMainConfig.get(aTextGeneral, "debug allow broken recipemap", false).getBoolean(false);
        GT_Values.debugCleanroom = tMainConfig.get(aTextGeneral, "debugCleanroom", false).getBoolean(false);
        GT_Values.debugDriller = tMainConfig.get(aTextGeneral, "debugDriller", false).getBoolean(false);
        GT_Values.debugWorldGen = tMainConfig.get(aTextGeneral, "debugWorldGen", false).getBoolean(false);
        GT_Values.debugOrevein = tMainConfig.get(aTextGeneral, "debugOrevein", false).getBoolean(false);
        GT_Values.debugSmallOres = tMainConfig.get(aTextGeneral, "debugSmallOres", false).getBoolean(false);
        GT_Values.debugStones = tMainConfig.get(aTextGeneral, "debugStones", false).getBoolean(false);
        GT_Values.debugBlockMiner = tMainConfig.get(aTextGeneral, "debugBlockMiner", false).getBoolean(false);
        GT_Values.debugBlockPump = tMainConfig.get(aTextGeneral, "debugBlockPump", false).getBoolean(false);
        GT_Values.debugEntityCramming = tMainConfig.get(aTextGeneral, "debugEntityCramming", false).getBoolean(false);
        GT_Values.debugWorldData = tMainConfig.get(aTextGeneral, "debugWorldData", false).getBoolean(false);
        GT_Values.oreveinPercentage = tMainConfig.get(aTextGeneral, "oreveinPercentage_100", 100).getInt(100);
        GT_Values.oreveinAttempts = tMainConfig.get(aTextGeneral, "oreveinAttempts_64", 64).getInt(64);
        GT_Values.oreveinMaxPlacementAttempts = tMainConfig.get(aTextGeneral, "oreveinMaxPlacementAttempts_8", 8).getInt(8);
        GT_Values.oreveinPlacerOres = tMainConfig.get(aTextGeneral, "oreveinPlacerOres", true).getBoolean(true);
        GT_Values.oreveinPlacerOresMultiplier = tMainConfig.get(aTextGeneral, "oreveinPlacerOresMultiplier", 2).getInt(2);
        //GT_Values.oreveinMaxSize = tMainConfig.get(aTextGeneral, "oreveinMaxSize_64",64).getInt(64);
        GT_Values.ticksBetweenSounds = tMainConfig.get("machines", "TicksBetweenSounds", 30).getInt(30);
        GT_Values.cleanroomGlass = (float) tMainConfig.get("machines", "ReinforcedGlassPercentageForCleanroom", 5D).getDouble(5D);
        GT_Values.enableChunkloaders = tMainConfig.get("machines", "enableChunkloaders", true).getBoolean(true);
        GT_Values.alwaysReloadChunkloaders = tMainConfig.get("machines", "alwaysReloadChunkloaders", false).getBoolean(false);
        GT_Values.debugChunkloaders = tMainConfig.get("machines", "debugChunkloaders", false).getBoolean(false);
        GT_Values.disableDigitalChestsExternalAccess = tMainConfig.get("machines", "disableDigitalChestsExternalAccess", false).getBoolean(false);
        GregTech_API.TICKS_FOR_LAG_AVERAGING = tMainConfig.get(aTextGeneral, "TicksForLagAveragingWithScanner", 25).getInt(25);
        GregTech_API.MILLISECOND_THRESHOLD_UNTIL_LAG_WARNING = tMainConfig.get(aTextGeneral, "MillisecondsPassedInGTTileEntityUntilLagWarning", 100).getInt(100);
        if (tMainConfig.get(aTextGeneral, "disable_STDOUT", false).getBoolean(false)) {
            GT_FML_LOGGER.info("Disableing Console Messages.");
            GT_FML_LOGGER.exit();
            System.out.close();
            System.err.close();
        }
        /*if (tMainConfig.get(aTextGeneral, "disable_STDERR", false).getBoolean(false)) {
            System.err.close();
        }*/
        GregTech_API.sMachineExplosions = tMainConfig.get("machines", "machines_explosion_damage", true).getBoolean(false);
        GregTech_API.sMachineFlammable = tMainConfig.get("machines", "machines_flammable", true).getBoolean(false);
        GregTech_API.sMachineNonWrenchExplosions = tMainConfig.get("machines", "explosions_on_nonwrenching", true).getBoolean(false);
        GregTech_API.sMachineWireFire = tMainConfig.get("machines", "wirefire_on_explosion", true).getBoolean(false);
        GregTech_API.sMachineFireExplosions = tMainConfig.get("machines", "fire_causes_explosions", true).getBoolean(false);
        GregTech_API.sMachineRainExplosions = tMainConfig.get("machines", "rain_causes_explosions", true).getBoolean(false);
        GregTech_API.sMachineThunderExplosions = tMainConfig.get("machines", "lightning_causes_explosions", true).getBoolean(false);
        GregTech_API.sConstantEnergy = tMainConfig.get("machines", "constant_need_of_energy", true).getBoolean(false);
        GregTech_API.sColoredGUI = tMainConfig.get("machines", "colored_guis_when_painted", true).getBoolean(false);
        GregTech_API.sMachineMetalGUI = tMainConfig.get("machines", "guis_in_consistent_machine_metal_color", false).getBoolean(false);

        // Implementation for this is actually handled in NewHorizonsCoreMod in MainRegistry.java!
        GregTech_API.sUseMachineMetal = tMainConfig.get("machines", "use_machine_metal_tint", true).getBoolean(true);

        GregTech_API.sTimber = tMainConfig.get(aTextGeneral, "timber_axe", true).getBoolean(true);
        GregTech_API.sDrinksAlwaysDrinkable = tMainConfig.get(aTextGeneral, "drinks_always_drinkable", false).getBoolean(false);
        GregTech_API.sDoShowAllItemsInCreative = tMainConfig.get(aTextGeneral, "show_all_metaitems_in_creative_and_NEI", false).getBoolean(false);
        GregTech_API.sMultiThreadedSounds = tMainConfig.get(aTextGeneral, "sound_multi_threading", false).getBoolean(false);
        String SBdye0 = "ColorModulation.";
        Arrays.stream(Dyes.values()).filter(tDye -> (tDye != Dyes._NULL) && (tDye.mIndex < 0)).forEach(tDye -> {
                    String SBdye1 = SBdye0 + tDye;
                    tDye.mRGBa[0] = ((short) Math.min(255, Math.max(0, GregTech_API.sClientDataFile.get(SBdye1, "R", tDye.mRGBa[0]))));
                    tDye.mRGBa[1] = ((short) Math.min(255, Math.max(0, GregTech_API.sClientDataFile.get(SBdye1, "G", tDye.mRGBa[1]))));
                    tDye.mRGBa[2] = ((short) Math.min(255, Math.max(0, GregTech_API.sClientDataFile.get(SBdye1, "B", tDye.mRGBa[2]))));
                }
        );
        gregtechproxy.mRenderTileAmbientOcclusion = GregTech_API.sClientDataFile.get("render", "TileAmbientOcclusion", true);
        gregtechproxy.mRenderGlowTextures = GregTech_API.sClientDataFile.get("render", "GlowTextures", true);
        gregtechproxy.mRenderFlippedMachinesFlipped = GregTech_API.sClientDataFile.get("render", "RenderFlippedMachinesFlipped", true);
        gregtechproxy.mRenderIndicatorsOnHatch = GregTech_API.sClientDataFile.get("render", "RenderIndicatorsOnHatch", true);
        gregtechproxy.mRenderDirtParticles =  GregTech_API.sClientDataFile.get("render", "RenderDirtParticles", true);
        gregtechproxy.mRenderPollutionFog = GregTech_API.sClientDataFile.get("render", "RenderPollutionFog", true);

        gregtechproxy.mMaxEqualEntitiesAtOneSpot = tMainConfig.get(aTextGeneral, "MaxEqualEntitiesAtOneSpot", 3).getInt(3);
        gregtechproxy.mSkeletonsShootGTArrows = tMainConfig.get(aTextGeneral, "SkeletonsShootGTArrows", 16).getInt(16);
        gregtechproxy.mFlintChance = tMainConfig.get(aTextGeneral, "FlintAndSteelChance", 30).getInt(30);
        gregtechproxy.mItemDespawnTime = tMainConfig.get(aTextGeneral, "ItemDespawnTime", 6000).getInt(6000);
        gregtechproxy.mAllowSmallBoilerAutomation = tMainConfig.get(aTextGeneral, "AllowSmallBoilerAutomation", false).getBoolean(false);
        gregtechproxy.mHardMachineCasings = tMainConfig.get(aTextGeneral, "HardMachineCasings", true).getBoolean(true);
        gregtechproxy.mDisableVanillaOres = tMainConfig.get(aTextGeneral, "DisableVanillaOres", true).getBoolean(true);
        gregtechproxy.mNerfDustCrafting = tMainConfig.get(aTextGeneral, "NerfDustCrafting", true).getBoolean(true);
        gregtechproxy.mIncreaseDungeonLoot = tMainConfig.get(aTextGeneral, "IncreaseDungeonLoot", true).getBoolean(true);
        gregtechproxy.mAxeWhenAdventure = tMainConfig.get(aTextGeneral, "AdventureModeStartingAxe", true).getBoolean(true);
        gregtechproxy.mHardcoreCables = tMainConfig.get(aTextGeneral, "HardCoreCableLoss", false).getBoolean(false);
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
        gregtechproxy.mAE2Integration = GregTech_API.sSpecialFile.get(ConfigCategories.general, "EnableAE2Integration", GregTech_API.mAE2);
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

        //Pollution: edit GT_Proxy.java to change default values
        gregtechproxy.mPollution = tMainConfig.get("Pollution", "EnablePollution", gregtechproxy.mPollution).getBoolean(gregtechproxy.mPollution);
        gregtechproxy.mPollutionSmogLimit = tMainConfig.get("Pollution", "SmogLimit", gregtechproxy.mPollutionSmogLimit).getInt(gregtechproxy.mPollutionSmogLimit);
        gregtechproxy.mPollutionPoisonLimit = tMainConfig.get("Pollution", "PoisonLimit", gregtechproxy.mPollutionPoisonLimit).getInt(gregtechproxy.mPollutionPoisonLimit);
        gregtechproxy.mPollutionVegetationLimit = tMainConfig.get("Pollution", "VegetationLimit", gregtechproxy.mPollutionVegetationLimit).getInt(gregtechproxy.mPollutionVegetationLimit);
        gregtechproxy.mPollutionSourRainLimit = tMainConfig.get("Pollution", "SourRainLimit", gregtechproxy.mPollutionSourRainLimit).getInt(gregtechproxy.mPollutionSourRainLimit);
        gregtechproxy.mPollutionOnExplosion = tMainConfig.get("Pollution", "SourRainLimit", gregtechproxy.mPollutionOnExplosion).getInt(gregtechproxy.mPollutionOnExplosion);
        gregtechproxy.mExplosionItemDrop = tMainConfig.get("general", "ExplosionItemDrops", gregtechproxy.mExplosionItemDrop).getBoolean(gregtechproxy.mExplosionItemDrop);
        gregtechproxy.mPollutionPrimitveBlastFurnacePerSecond = tMainConfig.get("Pollution", "PollutionPrimitiveBlastFurnace", gregtechproxy.mPollutionPrimitveBlastFurnacePerSecond).getInt(gregtechproxy.mPollutionPrimitveBlastFurnacePerSecond);
        gregtechproxy.mPollutionCharcoalPitPerSecond = tMainConfig.get("Pollution", "PollutionCharcoalPit", gregtechproxy.mPollutionCharcoalPitPerSecond).getInt(gregtechproxy.mPollutionCharcoalPitPerSecond);
        gregtechproxy.mPollutionEBFPerSecond = tMainConfig.get("Pollution", "PollutionEBF", gregtechproxy.mPollutionEBFPerSecond).getInt(gregtechproxy.mPollutionEBFPerSecond);
        gregtechproxy.mPollutionLargeCombustionEnginePerSecond = tMainConfig.get("Pollution", "PollutionLargeCombustionEngine",gregtechproxy.mPollutionLargeCombustionEnginePerSecond).getInt(gregtechproxy.mPollutionLargeCombustionEnginePerSecond);
        gregtechproxy.mPollutionExtremeCombustionEnginePerSecond = tMainConfig.get("Pollution", "PollutionExtremeCombustionEngine",gregtechproxy.mPollutionExtremeCombustionEnginePerSecond).getInt(gregtechproxy.mPollutionExtremeCombustionEnginePerSecond);
        gregtechproxy.mPollutionImplosionCompressorPerSecond = tMainConfig.get("Pollution", "PollutionImplosionCompressor", gregtechproxy.mPollutionImplosionCompressorPerSecond).getInt(gregtechproxy.mPollutionImplosionCompressorPerSecond);
        gregtechproxy.mPollutionLargeBronzeBoilerPerSecond = tMainConfig.get("Pollution", "PollutionLargeBronzeBoiler", gregtechproxy.mPollutionLargeBronzeBoilerPerSecond).getInt(gregtechproxy.mPollutionLargeBronzeBoilerPerSecond);
        gregtechproxy.mPollutionLargeSteelBoilerPerSecond = tMainConfig.get("Pollution", "PollutionLargeSteelBoiler", gregtechproxy.mPollutionLargeSteelBoilerPerSecond).getInt(gregtechproxy.mPollutionLargeSteelBoilerPerSecond);
        gregtechproxy.mPollutionLargeTitaniumBoilerPerSecond = tMainConfig.get("Pollution", "PollutionLargeTitaniumBoiler", gregtechproxy.mPollutionLargeTitaniumBoilerPerSecond ).getInt(gregtechproxy.mPollutionLargeTitaniumBoilerPerSecond );
        gregtechproxy.mPollutionLargeTungstenSteelBoilerPerSecond = tMainConfig.get("Pollution", "PollutionLargeTungstenSteelBoiler", gregtechproxy.mPollutionLargeTungstenSteelBoilerPerSecond).getInt(gregtechproxy.mPollutionLargeTungstenSteelBoilerPerSecond);
        gregtechproxy.mPollutionReleasedByThrottle = tMainConfig.get("Pollution", "PollutionReleasedByThrottle", gregtechproxy.mPollutionReleasedByThrottle).getDouble(gregtechproxy.mPollutionReleasedByThrottle);
        gregtechproxy.mPollutionLargeGasTurbinePerSecond = tMainConfig.get("Pollution", "PollutionLargeGasTurbine",  gregtechproxy.mPollutionLargeGasTurbinePerSecond).getInt( gregtechproxy.mPollutionLargeGasTurbinePerSecond);
        gregtechproxy.mPollutionMultiSmelterPerSecond = tMainConfig.get("Pollution", "PollutionMultiSmelter", gregtechproxy.mPollutionMultiSmelterPerSecond).getInt(gregtechproxy.mPollutionMultiSmelterPerSecond);
        gregtechproxy.mPollutionPyrolyseOvenPerSecond = tMainConfig.get("Pollution", "PollutionPyrolyseOven", gregtechproxy.mPollutionPyrolyseOvenPerSecond).getInt(gregtechproxy.mPollutionPyrolyseOvenPerSecond);
        gregtechproxy.mPollutionSmallCoalBoilerPerSecond = tMainConfig.get("Pollution", "PollutionSmallCoalBoiler", gregtechproxy.mPollutionSmallCoalBoilerPerSecond).getInt(gregtechproxy.mPollutionSmallCoalBoilerPerSecond);
        gregtechproxy.mPollutionHighPressureLavaBoilerPerSecond = tMainConfig.get("Pollution", "PollutionHighPressureLavaBoiler", gregtechproxy.mPollutionHighPressureLavaBoilerPerSecond).getInt(gregtechproxy.mPollutionHighPressureLavaBoilerPerSecond);
        gregtechproxy.mPollutionHighPressureCoalBoilerPerSecond = tMainConfig.get("Pollution", "PollutionHighPressureCoalBoiler", gregtechproxy.mPollutionHighPressureCoalBoilerPerSecond).getInt(gregtechproxy.mPollutionHighPressureCoalBoilerPerSecond);
        gregtechproxy.mPollutionBaseDieselGeneratorPerSecond = tMainConfig.get("Pollution", "PollutionBaseDieselGenerator",gregtechproxy.mPollutionBaseDieselGeneratorPerSecond).getInt(gregtechproxy.mPollutionBaseDieselGeneratorPerSecond);
        gregtechproxy.mPollutionDieselGeneratorReleasedByTier = tMainConfig.get("Pollution", "PollutionReleasedByTierDieselGenerator", gregtechproxy.mPollutionDieselGeneratorReleasedByTier).getDoubleList();
        gregtechproxy.mPollutionBaseGasTurbinePerSecond = tMainConfig.get("Pollution", "PollutionBaseGasTurbineGenerator", gregtechproxy.mPollutionBaseGasTurbinePerSecond).getInt(gregtechproxy.mPollutionBaseGasTurbinePerSecond);
        gregtechproxy.mPollutionGasTurbineReleasedByTier = tMainConfig.get("Pollution", "PollutionReleasedByTierGasTurbineGenerator", gregtechproxy.mPollutionGasTurbineReleasedByTier).getDoubleList();

        gregtechproxy.mUndergroundOil.getConfig(tMainConfig, "undergroundfluid");
        gregtechproxy.mEnableCleanroom = tMainConfig.get("general", "EnableCleanroom", true).getBoolean(true);
        if (gregtechproxy.mEnableCleanroom)
            GT_MetaTileEntity_Cleanroom.loadConfig(tMainConfig);
        gregtechproxy.mLowGravProcessing = Loader.isModLoaded(GT_Values.MOD_ID_GC_CORE) && tMainConfig.get("general", "LowGravProcessing", true).getBoolean(true);
        gregtechproxy.mUseGreatlyShrukenReplacementList = tMainConfig.get("general", "GTNH Optimised Material Loading", true).getBoolean(true);
        Calendar now = Calendar.getInstance();
        gregtechproxy.mAprilFool = GregTech_API.sSpecialFile.get(ConfigCategories.general, "AprilFool", now.get(Calendar.MONTH) == Calendar.APRIL && now.get(Calendar.DAY_OF_MONTH) == 1);
        gregtechproxy.mCropNeedBlock = tMainConfig.get("general", "CropNeedBlockBelow", true).getBoolean(true);
        gregtechproxy.mDisableOldChemicalRecipes = tMainConfig.get("general", "DisableOldChemicalRecipes", false).getBoolean(false);
        gregtechproxy.mAMHInteraction = tMainConfig.get("general", "AllowAutoMaintenanceHatchInteraction", false).getBoolean(false);
        GregTech_API.mOutputRF = GregTech_API.sOPStuff.get(ConfigCategories.general, "OutputRF", true);
        GregTech_API.mInputRF = GregTech_API.sOPStuff.get(ConfigCategories.general, "InputRF", false);
        GregTech_API.mEUtoRF = GregTech_API.sOPStuff.get(ConfigCategories.general, "100EUtoRF", 360);
        GregTech_API.mRFtoEU = GregTech_API.sOPStuff.get(ConfigCategories.general, "100RFtoEU", 20);
        GregTech_API.mRFExplosions = GregTech_API.sOPStuff.get(ConfigCategories.general, "RFExplosions", false);
        GregTech_API.meIOLoaded = Loader.isModLoaded("EnderIO");
        gregtechproxy.mForceFreeFace = GregTech_API.sMachineFile.get(ConfigCategories.machineconfig, "forceFreeFace", true);
        gregtechproxy.mBrickedBlastFurnace = tMainConfig.get("general", "BrickedBlastFurnace", true).getBoolean(true);
        gregtechproxy.mEasierIVPlusCables = tMainConfig.get("general", "EasierEVPlusCables", false).getBoolean(false);
        gregtechproxy.mMixedOreOnlyYieldsTwoThirdsOfPureOre = tMainConfig.get("general", "MixedOreOnlyYieldsTwoThirdsOfPureOre", false).getBoolean(false);
        gregtechproxy.enableBlackGraniteOres = GregTech_API.sWorldgenFile.get("general", "enableBlackGraniteOres", gregtechproxy.enableBlackGraniteOres);
        gregtechproxy.enableRedGraniteOres = GregTech_API.sWorldgenFile.get("general", "enableRedGraniteOres", gregtechproxy.enableRedGraniteOres);
        gregtechproxy.enableMarbleOres = GregTech_API.sWorldgenFile.get("general", "enableMarbleOres", gregtechproxy.enableMarbleOres);
        gregtechproxy.enableBasaltOres = GregTech_API.sWorldgenFile.get("general", "enableBasaltOres", gregtechproxy.enableBasaltOres);
        gregtechproxy.gt6Pipe = tMainConfig.get("general", "GT6StyledPipesConnection", true).getBoolean(true);
        gregtechproxy.gt6Cable = tMainConfig.get("general", "GT6StyledWiresConnection", true).getBoolean(true);
        gregtechproxy.ic2EnergySourceCompat = tMainConfig.get("general", "Ic2EnergySourceCompat", true).getBoolean(true);
        gregtechproxy.costlyCableConnection = tMainConfig.get("general", "CableConnectionRequiresSolderingMaterial", false).getBoolean(false);
        GT_LanguageManager.i18nPlaceholder = tMainConfig.get("general", "EnablePlaceholderForMaterialNamesInLangFile", true).getBoolean(true);
        GT_MetaTileEntity_LongDistancePipelineBase.minimalDistancePoints = tMainConfig.get("general", "LongDistancePipelineMinimalDistancePoints", 64).getInt(64);

        GregTech_API.mUseOnlyGoodSolderingMaterials = GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.harderrecipes, "useonlygoodsolderingmaterials", GregTech_API.mUseOnlyGoodSolderingMaterials);
        gregtechproxy.mChangeHarvestLevels = GregTech_API.sMaterialProperties.get("havestLevel", "activateHarvestLevelChange", false);//TODO CHECK
        if (gregtechproxy.mChangeHarvestLevels) {
            gregtechproxy.mGraniteHavestLevel = GregTech_API.sMaterialProperties.get("havestLevel", "graniteHarvestLevel", 3);
            gregtechproxy.mMaxHarvestLevel = Math.min(15, GregTech_API.sMaterialProperties.get("havestLevel", "maxLevel", 7));
            Materials.getMaterialsMap().values().parallelStream().filter(tMaterial -> tMaterial != null && tMaterial.mToolQuality > 0 && tMaterial.mMetaItemSubID < gregtechproxy.mHarvestLevel.length && tMaterial.mMetaItemSubID >= 0).forEach(
                    tMaterial -> gregtechproxy.mHarvestLevel[tMaterial.mMetaItemSubID] = GregTech_API.sMaterialProperties.get("materialHavestLevel", tMaterial.mDefaultLocalName, tMaterial.mToolQuality)
            );
        }

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
        if (GT_LanguageManager.sEnglishFile.get("EnableLangFile", "UseThisFileAsLanguageFile", false).getBoolean(false)) {
            GT_LanguageManager.sLanguage = GT_LanguageManager.sEnglishFile.get("EnableLangFile", "Language", "en_US").getString();
        }

        Materials.getMaterialsMap().values().parallelStream().filter(Objects::nonNull).forEach(aMaterial -> aMaterial.mLocalizedName = GT_LanguageManager.addStringLocalization("Material." + aMaterial.mName.toLowerCase(), aMaterial.mDefaultLocalName));

        GT_Log.out.println("GT_Mod: Removing all original Scrapbox Drops.");
        try {
            GT_Utility.getField("ic2.core.item.ItemScrapbox$Drop", "topChance", true, true).set(null, 0);
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

        GT_FML_LOGGER.info("preReader");
        List<String> oreTags = new ArrayList<>();
        if (Loader.isModLoaded("MineTweaker3")) {
            File globalDir = new File("scripts");
            if (globalDir.exists()) {
                List<String> scripts = new ArrayList<>();
                for (File file : globalDir.listFiles()) {
                    if (file.getName().endsWith(".zs")) {
                        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                            String line;
                            while ((line = br.readLine()) != null) {
                                scripts.add(line);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                String pattern1 = "<";
                String pattern2 = ">";

                Pattern p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
                for (String text : scripts) {
                    Matcher m = p.matcher(text);
                    while (m.find()) {
                        String hit = m.group(1);
                        if (hit.startsWith("ore:")) {
                            hit = hit.substring(4);
                            if (!oreTags.contains(hit)) oreTags.add(hit);
                        } else if (hit.startsWith("gregtech:gt.metaitem.0")) {
                            hit = hit.substring(22);
                            int mIt = Integer.parseInt(hit.substring(0, 1));
                            if (mIt > 0) {
                                int meta = 0;
                                try {
                                    hit = hit.substring(2);
                                    meta = Integer.parseInt(hit);
                                } catch (Exception e) {
                                    GT_FML_LOGGER.info("parseError: " + hit);
                                }
                                if (meta > 0 && meta < 32000) {
                                    int prefix = meta / 1000;
                                    int material = meta % 1000;
                                    String tag = "";
                                    String[] tags = new String[]{};
                                    if (mIt == 1)
                                        tags = new String[]{"dustTiny", "dustSmall", "dust", "dustImpure", "dustPure", "crushed", "crushedPurified", "crushedCentrifuged", "gem", "nugget", null, "ingot", "ingotHot", "ingotDouble", "ingotTriple", "ingotQuadruple", "ingotQuintuple", "plate", "plateDouble", "plateTriple", "plateQuadruple", "plateQuintuple", "plateDense", "stick", "lens", "round", "bolt", "screw", "ring", "foil", "cell", "cellPlasma", "cellMolten"};
                                    if (mIt == 2)
                                        tags = new String[]{"toolHeadSword", "toolHeadPickaxe", "toolHeadShovel", "toolHeadAxe", "toolHeadHoe", "toolHeadHammer", "toolHeadFile", "toolHeadSaw", "toolHeadDrill", "toolHeadChainsaw", "toolHeadWrench", "toolHeadUniversalSpade", "toolHeadSense", "toolHeadPlow", "toolHeadArrow", "toolHeadBuzzSaw", "turbineBlade", null, null, "wireFine", "gearGtSmall", "rotor", "stickLong", "springSmall", "spring", "arrowGtWood", "arrowGtPlastic", "gemChipped", "gemFlawed", "gemFlawless", "gemExquisite", "gearGt"};
                                    if (mIt == 3)
                                        tags = new String[]{"crateGtDust", "crateGtIngot", "crateGtGem", "crateGtPlate", "itemCasing"};
                                    if (tags.length > prefix) tag = tags[prefix];
                                    if (GregTech_API.sGeneratedMaterials[material] != null) {
                                        tag += GregTech_API.sGeneratedMaterials[material].mName;
                                        if (!oreTags.contains(tag)) oreTags.add(tag);
                                    } else if (material > 0) {
                                        GT_FML_LOGGER.info("MaterialDisabled: " + material + " " + m.group(1));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        String[] preS = new String[]{"dustTiny", "dustSmall", "dust", "dustImpure", "dustPure", "crushed", "crushedPurified", "crushedCentrifuged", "gem", "nugget", "ingot", "ingotHot", "ingotDouble", "ingotTriple", "ingotQuadruple", "ingotQuintuple", "plate", "plateDouble", "plateTriple", "plateQuadruple", "plateQuintuple", "plateDense", "stick", "lens", "round", "bolt", "screw", "ring", "foil", "cell", "cellPlasma", "toolHeadSword", "toolHeadPickaxe", "toolHeadShovel", "toolHeadAxe", "toolHeadHoe", "toolHeadHammer", "toolHeadFile", "toolHeadSaw", "toolHeadDrill", "toolHeadChainsaw", "toolHeadWrench", "toolHeadUniversalSpade", "toolHeadSense", "toolHeadPlow", "toolHeadArrow", "toolHeadBuzzSaw", "turbineBlade", "wireFine", "gearGtSmall", "rotor", "stickLong", "springSmall", "spring", "arrowGtWood", "arrowGtPlastic", "gemChipped", "gemFlawed", "gemFlawless", "gemExquisite", "gearGt", "crateGtDust", "crateGtIngot", "crateGtGem", "crateGtPlate", "cellMolten"};

        List<String> mMTTags = new ArrayList<>();
        oreTags.stream()
                .filter(test -> StringUtils.startsWithAny(test, preS))
                .forEach(test -> {
                    mMTTags.add(test);
                    if (GT_Values.D1)
                        GT_FML_LOGGER.info("oretag: " + test);
                });

        GT_FML_LOGGER.info("reenableMetaItems");

        for (String reEnable : mMTTags) {
            OrePrefixes tPrefix = OrePrefixes.getOrePrefix(reEnable);
            if (tPrefix != null) {
                Materials tName = Materials.get(reEnable.replaceFirst(tPrefix.toString(), ""));
                if (tName != null) {
                    tPrefix.mDisabledItems.remove(tName);
                    tPrefix.mGeneratedItems.add(tName);
                    if (tPrefix == OrePrefixes.screw) {
                        OrePrefixes.bolt.mDisabledItems.remove(tName);
                        OrePrefixes.bolt.mGeneratedItems.add(tName);
                        OrePrefixes.stick.mDisabledItems.remove(tName);
                        OrePrefixes.stick.mGeneratedItems.add(tName);
                    }
                    if (tPrefix == OrePrefixes.round) {
                        OrePrefixes.nugget.mDisabledItems.remove(tName);
                        OrePrefixes.nugget.mGeneratedItems.add(tName);
                    }
                    if (tPrefix == OrePrefixes.spring) {
                        OrePrefixes.stickLong.mDisabledItems.remove(tName);
                        OrePrefixes.stickLong.mGeneratedItems.add(tName);
                        OrePrefixes.stick.mDisabledItems.remove(tName);
                        OrePrefixes.stick.mGeneratedItems.add(tName);
                    }
                    if (tPrefix == OrePrefixes.springSmall) {
                        OrePrefixes.stick.mDisabledItems.remove(tName);
                        OrePrefixes.stick.mGeneratedItems.add(tName);
                    }
                    if (tPrefix == OrePrefixes.stickLong) {
                        OrePrefixes.stick.mDisabledItems.remove(tName);
                        OrePrefixes.stick.mGeneratedItems.add(tName);
                    }
                    if (tPrefix == OrePrefixes.rotor) {
                        OrePrefixes.ring.mDisabledItems.remove(tName);
                        OrePrefixes.ring.mGeneratedItems.add(tName);
                    }
                } else {
                    GT_FML_LOGGER.info("noMaterial " + reEnable);
                }
            } else {
                GT_FML_LOGGER.info("noPrefix " + reEnable);
            }
        }

        new GT_Loader_OreProcessing().run();
        new GT_Loader_OreDictionary().run();
        new GT_Loader_ItemData().run();
        new GT_Loader_Item_Block_And_Fluid().run();
        new GT_Loader_MetaTileEntities().run();

        new GT_Loader_CircuitBehaviors().run();
        new GT_CoverBehaviorLoader().run();
        new GT_SonictronLoader().run();
        new GT_SpawnEventHandler();

        GT_Values.RA.addCentrifugeRecipe(Materials.Stone.getDust(1), GT_Values.NI, GT_Values.NF, GT_Values.NF,
                Materials.Quartzite.getDustSmall(1), Materials.PotassiumFeldspar.getDustSmall(1), Materials.Marble.getDustTiny(2),
                Materials.Biotite.getDustTiny(1), Materials.MetalMixture.getDustTiny(1), Materials.Sodalite.getDustTiny(1),
                new int[]{10000, 10000, 10000, 10000, 7500, 5000}, 480, 120);
        GT_Values.RA.addCentrifugeRecipe(Materials.MetalMixture.getDust(1), GT_Values.NI, GT_Values.NF, GT_Values.NF,
                Materials.BandedIron.getDustSmall(1), Materials.Bauxite.getDustSmall(1), Materials.Pyrolusite.getDustTiny(2),
                Materials.Barite.getDustTiny(1), Materials.Chromite.getDustTiny(1), Materials.Ilmenite.getDustTiny(1),
                new int[]{10000, 10000, 10000, 10000, 7500, 5000}, 1000, 900);

        double outputMultiplier = Math.min(GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "StoneDustOutputMultiplier", 0.6), 1.0);
        if (GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "StoneDustCentrifugation", false)) {
            int fullChance = (int) (10000 * outputMultiplier);
            int[] chances = new int[]{fullChance, fullChance, fullChance, fullChance, fullChance, (int) (5500 * outputMultiplier)};
            GT_Values.RA.addCentrifugeRecipe(Materials.Stone.getDust(1), GT_Values.NI, GT_Values.NF, GT_Values.NF,
                    Materials.Quartzite.getDustSmall(1), Materials.PotassiumFeldspar.getDustSmall(1), Materials.Marble.getDustTiny(2),
                    Materials.Biotite.getDustTiny(1), Materials.MetalMixture.getDustTiny(1), Materials.Sodalite.getDustTiny(1),
                    chances, 600, 120);
            GT_Values.RA.addCentrifugeRecipe(Materials.MetalMixture.getDust(1), GT_Values.NI, GT_Values.NF, GT_Values.NF,
                    Materials.BandedIron.getDustSmall(1), Materials.Bauxite.getDustSmall(1), Materials.Pyrolusite.getDustTiny(2),
                    Materials.Barite.getDustTiny(1), Materials.Chromite.getDustTiny(1), Materials.Ilmenite.getDustTiny(1),
                    new int[]{10000, 10000, 10000, 10000, 10000, 6000}, 600, 1920);
        }
        double ashOutputMultiplier = Math.min(GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "AshOutputMultiplier", 1.0), 1.0);
        if (GregTech_API.sOPStuff.get(ConfigCategories.Recipes.gregtechrecipes, "AshYieldsAlkaliMetalOxides", true)) {
            int[] outputChances = new int[]{(int) (9900 * ashOutputMultiplier), (int) (6400 * ashOutputMultiplier),
                    (int) (6000 * ashOutputMultiplier), (int) (500 * ashOutputMultiplier),
                    (int) (5000 * ashOutputMultiplier), (int) (2500 * ashOutputMultiplier)};
            GT_Values.RA.addCentrifugeRecipe(Materials.Ash.getDust(1), GT_Values.NI, GT_Values.NF, GT_Values.NF,
                    Materials.Quicklime.getDustSmall(2), Materials.Potash.getDustSmall(1), Materials.Magnesia.getDustSmall(1),
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
                List<ModContainer> tNewModsList = new ArrayList<>();
                ModContainer tGregTech = null;
                short tModList_sS = (short) tModList.size();
                for (short i = 0; i < tModList_sS; i = (short) (i + 1)) {
                    ModContainer tMod = tModList.get(i);
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

        for (Runnable tRunnable : GregTech_API.sAfterGTPreload) {
            try {
                tRunnable.run();
            } catch (Throwable e) {
                e.printStackTrace(GT_Log.err);
            }
        }

        if (FMLCommonHandler.instance().getEffectiveSide().isServer())
            GT_Assemblyline_Server.fillMap(aEvent);
    }

    @Mod.EventHandler
    public void onLoad(FMLInitializationEvent aEvent) {
        if (GregTech_API.sLoadStarted) {
            return;
        }

        for (Runnable tRunnable : GregTech_API.sBeforeGTLoad) {
            try {
                tRunnable.run();
            } catch (Throwable e) {
                e.printStackTrace(GT_Log.err);
            }
        }

        if (Loader.isModLoaded(MOD_ID_FR))
            new GT_Bees();

        //Disable Low Grav regardless of config if Cleanroom is disabled.
        if (!gregtechproxy.mEnableCleanroom) {
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

        for (Runnable tRunnable : GregTech_API.sAfterGTLoad) {
            try {
                tRunnable.run();
            } catch (Throwable e) {
                e.printStackTrace(GT_Log.err);
            }
        }

    }

    @Mod.EventHandler
    public void onPostLoad(FMLPostInitializationEvent aEvent) {
        if (GregTech_API.sPostloadStarted) {
            return;
        }

        for (Runnable tRunnable : GregTech_API.sBeforeGTPostload) {
            try {
                tRunnable.run();
            } catch (Throwable e) {
                e.printStackTrace(GT_Log.err);
            }
        }

        gregtechproxy.onPostLoad();


        for (int i = 1; i < GregTech_API.METATILEENTITIES.length; i++) {
            if (i >= GregTech_API.METATILEENTITIES.length)
                break;
            if (GregTech_API.METATILEENTITIES[i] != null) {
                GT_Log.out.println("META " + i + " " + GregTech_API.METATILEENTITIES[i].getMetaName());
            }
        }
        
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

        GT_RecipeRegistrator.registerUsagesForMaterials(null, false, new ItemStack(Blocks.planks, 1), new ItemStack(Blocks.cobblestone, 1), new ItemStack(Blocks.stone, 1), new ItemStack(Items.leather, 1));

        GT_OreDictUnificator.addItemData(GT_ModHandler.getRecipeOutput(null, GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Tin, 1L), null, GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Tin, 1L), null, GT_OreDictUnificator.get(OrePrefixes.ingot, Materials.Tin, 1L), null, null, null), new ItemData(Materials.Tin, 10886400L));
        if (!GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.storageblockcrafting, "tile.glowstone", false)) {
            GT_ModHandler.removeRecipe(new ItemStack(Items.glowstone_dust, 1), new ItemStack(Items.glowstone_dust, 1), null, new ItemStack(Items.glowstone_dust, 1), new ItemStack(Items.glowstone_dust, 1));
        }
        GT_ModHandler.removeRecipeDelayed(new ItemStack(Blocks.wooden_slab, 1, 0), new ItemStack(Blocks.wooden_slab, 1, 1), new ItemStack(Blocks.wooden_slab, 1, 2));
        GT_ModHandler.addCraftingRecipe(new ItemStack(Blocks.wooden_slab, 6, 0), GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.BUFFERED, new Object[]{"WWW", 'W', new ItemStack(Blocks.planks, 1, 0)});

        // Save a copy of these list before activateOreDictHandler(), then loop over them.
        Map<IRecipeInput, RecipeOutput> aMaceratorRecipeList = GT_ModHandler.getMaceratorRecipeList();
        Map<IRecipeInput, RecipeOutput> aCompressorRecipeList = GT_ModHandler.getCompressorRecipeList();
        Map<IRecipeInput, RecipeOutput> aExtractorRecipeList = GT_ModHandler.getExtractorRecipeList();
        Map<IRecipeInput, RecipeOutput> aOreWashingRecipeList = GT_ModHandler.getOreWashingRecipeList();
        Map<IRecipeInput, RecipeOutput> aThermalCentrifugeRecipeList = GT_ModHandler.getThermalCentrifugeRecipeList();

        GT_Log.out.println("GT_Mod: Activating OreDictionary Handler, this can take some time, as it scans the whole OreDictionary");
        GT_FML_LOGGER.info("If your Log stops here, you were too impatient. Wait a bit more next time, before killing Minecraft with the Task Manager.");

        Stopwatch stopwatch = Stopwatch.createStarted();
        gregtechproxy.activateOreDictHandler();
        GT_FML_LOGGER.info("Congratulations, you have been waiting long enough (" + stopwatch.stop() + "). Have a Cake.");
        GT_Log.out.println("GT_Mod: List of Lists of Tool Recipes: " + GT_ModHandler.sSingleNonBlockDamagableRecipeList_list.toString());
        GT_Log.out.println("GT_Mod: Vanilla Recipe List -> Outputs null or stackSize <=0: " + GT_ModHandler.sVanillaRecipeList_warntOutput.toString());
        GT_Log.out.println("GT_Mod: Single Non Block Damagable Recipe List -> Outputs null or stackSize <=0: " + GT_ModHandler.sSingleNonBlockDamagableRecipeList_warntOutput.toString());

        Set<Materials> replaceVanillaItemsSet = gregtechproxy.mUseGreatlyShrukenReplacementList ? Arrays.stream(Materials.values()).filter(GT_RecipeRegistrator::hasVanillaRecipes).collect(Collectors.toSet()) : new HashSet<>(Arrays.asList(Materials.values()));

        stopwatch.reset();
        stopwatch.start();
        GT_FML_LOGGER.info("Replacing Vanilla Materials in recipes, please wait.");

        ProgressManager.ProgressBar progressBar = ProgressManager.push("Register materials", replaceVanillaItemsSet.size());
        if (GT_Values.cls_enabled){
            try {
                GT_CLS_Compat.doActualRegistrationCLS(progressBar,replaceVanillaItemsSet);
                GT_CLS_Compat.pushToDisplayProgress();
            } catch (InvocationTargetException | IllegalAccessException e) {
                GT_Mod.GT_FML_LOGGER.catching(e);
            }
        }
        else {
            replaceVanillaItemsSet.forEach(m -> {
                progressBar.step(m.mDefaultLocalName);
                doActualRegistration(m);
            });
        }
        ProgressManager.pop(progressBar);
        GT_FML_LOGGER.info("Replaced Vanilla Materials (" + stopwatch.stop() + "). Have a Cake.");


        stopwatch.reset();
        stopwatch.start();
        // remove gemIridium exploit
        ItemStack iridiumOre = GT_ModHandler.getIC2Item("iridiumOre", 1);
        aCompressorRecipeList.entrySet().parallelStream()
                .filter(e -> e.getKey().getInputs().size() == 1 && e.getKey().getInputs().get(0).isItemEqual(iridiumOre))
                .findAny()
                .ifPresent(e -> aCompressorRecipeList.remove(e.getKey()));
        //Add default IC2 recipe to GT
        GT_ModHandler.addIC2RecipesToGT(aMaceratorRecipeList, GT_Recipe.GT_Recipe_Map.sMaceratorRecipes, true, true, true);
        GT_ModHandler.addIC2RecipesToGT(aCompressorRecipeList, GT_Recipe.GT_Recipe_Map.sCompressorRecipes, true, true, true);
        GT_ModHandler.addIC2RecipesToGT(aExtractorRecipeList, GT_Recipe.GT_Recipe_Map.sExtractorRecipes, true, true, true);
        GT_ModHandler.addIC2RecipesToGT(aOreWashingRecipeList, GT_Recipe.GT_Recipe_Map.sOreWasherRecipes, false, true, true);
        GT_ModHandler.addIC2RecipesToGT(aThermalCentrifugeRecipeList, GT_Recipe.GT_Recipe_Map.sThermalCentrifugeRecipes, true, true, true);
        GT_FML_LOGGER.info("IC2 Removal (" + stopwatch.stop() + "). Have a Cake.");
        
        
        if (GT_Values.D1) {
            GT_ModHandler.sSingleNonBlockDamagableRecipeList.forEach(iRecipe -> GT_Log.out.println("=> " + iRecipe.getRecipeOutput().getDisplayName()));
        }
        new GT_CraftingRecipeLoader().run();
        if (GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, "ic2forgehammer", true)) {
            GT_ModHandler.removeRecipeByOutput(ItemList.IC2_ForgeHammer.getWildcard(1L));
        }
        GT_ModHandler.removeRecipeByOutput(GT_ModHandler.getIC2Item("machine", 1L));
        GT_ModHandler.addCraftingRecipe(GT_ModHandler.getIC2Item("machine", 1L), GT_ModHandler.RecipeBits.BUFFERED | GT_ModHandler.RecipeBits.NOT_REMOVABLE | GT_ModHandler.RecipeBits.REVERSIBLE, new Object[]{"RRR", "RwR", "RRR", 'R', OrePrefixes.plate.get(Materials.Iron)});
        ItemStack ISdata0 = new ItemStack(Items.potionitem, 1, 0);
        ItemStack ILdata0 = ItemList.Bottle_Empty.get(1L);
        for (FluidContainerRegistry.FluidContainerData tData : FluidContainerRegistry.getRegisteredFluidContainerData()) {
            if ((tData.filledContainer.getItem() == Items.potionitem) && (tData.filledContainer.getItemDamage() == 0)) {
                GT_Recipe.GT_Recipe_Map.sFluidCannerRecipes.addRecipe(true, new ItemStack[]{ILdata0}, new ItemStack[]{ISdata0}, null, new FluidStack[]{Materials.Water.getFluid(250L)}, null, 4, 1, 0);
                GT_Recipe.GT_Recipe_Map.sFluidCannerRecipes.addRecipe(true, new ItemStack[]{ISdata0}, new ItemStack[]{ILdata0}, null, null, null, 4, 1, 0);
            } else {
                GT_Recipe.GT_Recipe_Map.sFluidCannerRecipes.addRecipe(true, new ItemStack[]{tData.emptyContainer}, new ItemStack[]{tData.filledContainer}, null, new FluidStack[]{tData.fluid}, null, tData.fluid.amount / 62, 1, 0);
                GT_Recipe.GT_Recipe_Map.sFluidCannerRecipes.addRecipe(true, new ItemStack[]{tData.filledContainer}, new ItemStack[]{GT_Utility.getContainerItem(tData.filledContainer, true)}, null, null, new FluidStack[]{tData.fluid}, tData.fluid.amount / 62, 1, 0);
            }
        }

        if (Loader.isModLoaded(MOD_ID_FR)) {
            GT_Forestry_Compat.transferCentrifugeRecipes();
            GT_Forestry_Compat.transferSqueezerRecipes();
        }
        if (GregTech_API.mAE2)
            GT_MetaTileEntity_DigitalChestBase.registerAEIntegration();


        Arrays.stream(new String[]{
                "blastfurnace", "blockcutter", "inductionFurnace", "generator", "windMill", "waterMill", "solarPanel", "centrifuge", "electrolyzer", "compressor",
                "electroFurnace", "extractor", "macerator", "recycler", "metalformer", "orewashingplant", "massFabricator", "replicator",
            })
           .filter(tName -> GregTech_API.sRecipeFile.get(ConfigCategories.Recipes.disabledrecipes, aTextIC2 + tName, true))
           .map(tName -> GT_ModHandler.getIC2Item(tName, 1L)).forEach(GT_ModHandler::removeRecipeByOutputDelayed);
        

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
        new GT_ExtremeDieselFuelLoader().run();
        
        
        /* 
         * Until this point most crafting recipe additions, and removals, have been buffered.
         * Go through, execute the removals in bulk, and then any deferred additions.  The bulk removals in particular significantly speed up the recipe list
         * modifications.
         */

        stopwatch.reset();
        stopwatch.start();
        GT_Log.out.println("GT_Mod: Adding buffered Recipes.");
        GT_ModHandler.stopBufferingCraftingRecipes();
        GT_FML_LOGGER.info("Executed delayed Crafting Recipes (" + stopwatch.stop() + "). Have a Cake.");
        
        GT_Log.out.println("GT_Mod: Saving Lang File.");
        GT_LanguageManager.sEnglishFile.save();
        GregTech_API.sPostloadFinished = true;
        GT_Log.out.println("GT_Mod: PostLoad-Phase finished!");
        GT_Log.ore.println("GT_Mod: PostLoad-Phase finished!");
        for (Runnable tRunnable : GregTech_API.sAfterGTPostload) {
            try {
                tRunnable.run();
            } catch (Throwable e) {
                e.printStackTrace(GT_Log.err);
            }
        }
        GT_Log.out.println("GT_Mod: Adding Fake Recipes for NEI");

        if (Loader.isModLoaded(MOD_ID_FR))
            GT_Forestry_Compat.populateFakeNeiRecipes();

        if (ItemList.IC2_Crop_Seeds.get(1L) != null) {
            GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.IC2_Crop_Seeds.getWildcard(1L)}, new ItemStack[]{ItemList.IC2_Crop_Seeds.getWithName(1L, "Scanned Seeds")}, null, null, null, 160, 8, 0);
        }
        GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{new ItemStack(Items.written_book, 1, 32767)}, new ItemStack[]{ItemList.Tool_DataStick.getWithName(1L, "Scanned Book Data")}, ItemList.Tool_DataStick.getWithName(1L, "Stick to save it to"), null, null, 128, 30, 0);
        GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{new ItemStack(Items.filled_map, 1, 32767)}, new ItemStack[]{ItemList.Tool_DataStick.getWithName(1L, "Scanned Map Data")}, ItemList.Tool_DataStick.getWithName(1L, "Stick to save it to"), null, null, 128, 30, 0);
        GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.Tool_DataOrb.getWithName(1L, "Orb to overwrite")}, new ItemStack[]{ItemList.Tool_DataOrb.getWithName(1L, "Copy of the Orb")}, ItemList.Tool_DataOrb.getWithName(0L, "Orb to copy"), null, null, 512, 30, 0);
        GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.Tool_DataStick.getWithName(1L, "Stick to overwrite")}, new ItemStack[]{ItemList.Tool_DataStick.getWithName(1L, "Copy of the Stick")}, ItemList.Tool_DataStick.getWithName(0L, "Stick to copy"), null, null, 128, 30, 0);
        GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.Tool_DataStick.getWithName(1L, "Raw Prospection Data")}, new ItemStack[]{ItemList.Tool_DataStick.getWithName(1L, "Analyzed Prospection Data")}, null, null, null, 1000, 30, 0);
        if (Loader.isModLoaded("GalacticraftCore")) {
            GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{GT_ModHandler.getModItem("GalacticraftCore", "item.schematic", 1, Short.MAX_VALUE).setStackDisplayName("Any Schematic")}, new ItemStack[]{ItemList.Tool_DataStick.getWithName(1L, "Scanned Schematic")}, ItemList.Tool_DataStick.getWithName(1L, "Stick to save it to"), null, null, 36000, 480, 0);
            if (Loader.isModLoaded("GalacticraftMars"))
                GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{GT_ModHandler.getModItem("GalacticraftMars", "item.schematic", 1, Short.MAX_VALUE).setStackDisplayName("Any Schematic")}, new ItemStack[]{ItemList.Tool_DataStick.getWithName(1L, "Scanned Schematic")}, ItemList.Tool_DataStick.getWithName(1L, "Stick to save it to"), null, null, 36000, 480, 0);
            if (Loader.isModLoaded("GalaxySpace")) {
                for (int i = 4; i < 9; i++) {
                    GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, new ItemStack[]{GT_ModHandler.getModItem("GalaxySpace", "item.SchematicTier" + i, 1).setStackDisplayName("Any Schematic")}, new ItemStack[]{ItemList.Tool_DataStick.getWithName(1L, "Scanned Schematic")}, ItemList.Tool_DataStick.getWithName(1L, "Stick to save it to"), null, null, 36000, 480, 0);
                }
            }
        }
        Materials.getMaterialsMap().values().stream().forEach(tMaterial -> {
            if ((tMaterial.mElement != null) && (!tMaterial.mElement.mIsIsotope) && (tMaterial != Materials.Magic) && (tMaterial.getMass() > 0L)) {
                ItemStack tOutput = ItemList.Tool_DataOrb.get(1L);
                Behaviour_DataOrb.setDataTitle(tOutput, "Elemental-Scan");
                Behaviour_DataOrb.setDataName(tOutput, tMaterial.mElement.name());
                ItemStack tInput = GT_OreDictUnificator.get(OrePrefixes.dust, tMaterial, 1L);
                ItemStack[] ISmat0 = new ItemStack[]{tInput};
                ItemStack[] ISmat1 = new ItemStack[]{tOutput};
                if (tInput != null) {
                    GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, ISmat0, ISmat1, ItemList.Tool_DataOrb.get(1L), null, null, (int) (tMaterial.getMass() * 8192L), 30, 0);
                    GT_Recipe.GT_Recipe_Map.sReplicatorFakeRecipes.addFakeRecipe(false, null, ISmat0, ISmat1, new FluidStack[]{Materials.UUMatter.getFluid(tMaterial.getMass())}, null, (int) (tMaterial.getMass() * 512L), 30, 0);

                }
                tInput = GT_OreDictUnificator.get(OrePrefixes.cell, tMaterial, 1L);
                ISmat0 = new ItemStack[]{tInput};
                if (tInput != null) {
                    GT_Recipe.GT_Recipe_Map.sScannerFakeRecipes.addFakeRecipe(false, ISmat0, ISmat1, ItemList.Tool_DataOrb.get(1L), null, null, (int) (tMaterial.getMass() * 8192L), 30, 0);
                    GT_Recipe.GT_Recipe_Map.sReplicatorFakeRecipes.addFakeRecipe(false, null, ISmat0, ISmat1, new FluidStack[]{Materials.UUMatter.getFluid(tMaterial.getMass())}, null, (int) (tMaterial.getMass() * 512L), 30, 0);
                }
            }
        });

        if (!GT_MetaTileEntity_Massfabricator.sRequiresUUA)
            GT_Recipe.GT_Recipe_Map.sMassFabFakeRecipes.addFakeRecipe(false, null, null, null, null, new FluidStack[]{Materials.UUMatter.getFluid(1L)}, GT_MetaTileEntity_Massfabricator.sDurationMultiplier, 256, 0);
        GT_Recipe.GT_Recipe_Map.sMassFabFakeRecipes.addFakeRecipe(false, null, null, null, new FluidStack[]{Materials.UUAmplifier.getFluid(GT_MetaTileEntity_Massfabricator.sUUAperUUM)}, new FluidStack[]{Materials.UUMatter.getFluid(1L)}, GT_MetaTileEntity_Massfabricator.sDurationMultiplier / GT_MetaTileEntity_Massfabricator.sUUASpeedBonus, 256, 0);
        GT_Recipe.GT_Recipe_Map.sRockBreakerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.Display_ITS_FREE.getWithName(0L, "Place Lava on Side")}, new ItemStack[]{new ItemStack(Blocks.cobblestone, 1)}, null, null, null, 16, 30, 0);
        GT_Recipe.GT_Recipe_Map.sRockBreakerFakeRecipes.addFakeRecipe(false, new ItemStack[]{ItemList.Display_ITS_FREE.getWithName(0L, "Place Lava on Top")}, new ItemStack[]{new ItemStack(Blocks.stone, 1)}, null, null, null, 16, 30, 0);
        GT_Recipe.GT_Recipe_Map.sRockBreakerFakeRecipes.addFakeRecipe(false, new ItemStack[]{GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Redstone, 1L)}, new ItemStack[]{new ItemStack(Blocks.obsidian, 1)}, null, null, null, 128, 30, 0);

        if (GregTech_API.mOutputRF || GregTech_API.mInputRF) {
            GT_Utility.checkAvailabilities();
            if (!GT_Utility.RF_CHECK) {
                GregTech_API.mOutputRF = false;
                GregTech_API.mInputRF = false;
            }
        }

        addSolidFakeLargeBoilerFuels();
        identifyAnySteam();

        achievements = new GT_Achievements();

        ReverseShapedRecipe.runReverseRecipes();
        ReverseShapelessRecipe.runReverseRecipes();

        GT_Recipe.GTppRecipeHelper = true;
        GT_Log.out.println("GT_Mod: Loading finished, deallocating temporary Init Variables.");
        GregTech_API.sBeforeGTPreload = null;
        GregTech_API.sAfterGTPreload = null;
        GregTech_API.sBeforeGTLoad = null;
        GregTech_API.sAfterGTLoad = null;
        GregTech_API.sBeforeGTPostload = null;
        GregTech_API.sAfterGTPostload = null;


        CreativeTabs mainTab = new CreativeTabs("GTtools") {
            @SideOnly(Side.CLIENT)
            @Override
            public ItemStack getIconItemStack() {
                return ItemList.Tool_Cheat.get(1, new ItemStack(Blocks.iron_block, 1));
            }

            @SideOnly(Side.CLIENT)
            @Override
            public Item getTabIconItem() {
                return ItemList.Circuit_Integrated.getItem();
            }

            @Override
            public void displayAllReleventItems(List aList) {
                for (int i = 0; i < 32766; i += 2) {
                    if (GT_MetaGenerated_Tool_01.INSTANCE.getToolStats(new ItemStack(GT_MetaGenerated_Tool_01.INSTANCE, 1, i)) != null) {
                        ItemStack tStack = new ItemStack(GT_MetaGenerated_Tool_01.INSTANCE, 1, i);
                        GT_MetaGenerated_Tool_01.INSTANCE.isItemStackUsable(tStack);
                        aList.add(GT_MetaGenerated_Tool_01.INSTANCE.getToolWithStats(i, 1, Materials.Lead, Materials.Lead, null));
                        aList.add(GT_MetaGenerated_Tool_01.INSTANCE.getToolWithStats(i, 1, Materials.Nickel, Materials.Nickel, null));
                        aList.add(GT_MetaGenerated_Tool_01.INSTANCE.getToolWithStats(i, 1, Materials.Cobalt, Materials.Cobalt, null));
                        aList.add(GT_MetaGenerated_Tool_01.INSTANCE.getToolWithStats(i, 1, Materials.Osmium, Materials.Osmium, null));
                        aList.add(GT_MetaGenerated_Tool_01.INSTANCE.getToolWithStats(i, 1, Materials.Adamantium, Materials.Adamantium, null));
                        aList.add(GT_MetaGenerated_Tool_01.INSTANCE.getToolWithStats(i, 1, Materials.Neutronium, Materials.Neutronium, null));
                    }
                }
                super.displayAllReleventItems(aList);
            }
        };
    }

    public static void doActualRegistration(Materials m){
        String platename = OrePrefixes.plate.get(m).toString();
        boolean noSmash = !m.contains(SubTag.NO_SMASHING);
        if ((m.mTypes & 2) != 0)
            GT_RecipeRegistrator.registerUsagesForMaterials(platename, noSmash, m.getIngots(1));
        if ((m.mTypes & 4) != 0)
            GT_RecipeRegistrator.registerUsagesForMaterials(platename, noSmash, m.getGems(1));
        if (m.getBlocks(1) != null)
            GT_RecipeRegistrator.registerUsagesForMaterials(null, noSmash, m.getBlocks(1));
    }

    @Mod.EventHandler
    public void onServerStarted(FMLServerStartedEvent aEvent) {
        gregtechproxy.onServerStarted();
    }

    @Mod.EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent aEvent) {
        gregtechproxy.onServerAboutToStart();
    }

    @Mod.EventHandler
    public void onServerStarting(FMLServerStartingEvent aEvent) {

        for (Runnable tRunnable : GregTech_API.sBeforeGTServerstart) {
            try {
                tRunnable.run();
            } catch (Throwable e) {
                e.printStackTrace(GT_Log.err);
            }
        }

        gregtechproxy.onServerStarting();
        //Check for more IC2 recipes on ServerStart to also catch MineTweaker additions
        GT_ModHandler.addIC2RecipesToGT(GT_ModHandler.getMaceratorRecipeList(), GT_Recipe.GT_Recipe_Map.sMaceratorRecipes, true, true, true);
        GT_ModHandler.addIC2RecipesToGT(GT_ModHandler.getCompressorRecipeList(), GT_Recipe.GT_Recipe_Map.sCompressorRecipes, true, true, true);
        GT_ModHandler.addIC2RecipesToGT(GT_ModHandler.getExtractorRecipeList(), GT_Recipe.GT_Recipe_Map.sExtractorRecipes, true, true, true);
        GT_ModHandler.addIC2RecipesToGT(GT_ModHandler.getOreWashingRecipeList(), GT_Recipe.GT_Recipe_Map.sOreWasherRecipes, false, true, true);
        GT_ModHandler.addIC2RecipesToGT(GT_ModHandler.getThermalCentrifugeRecipeList(), GT_Recipe.GT_Recipe_Map.sThermalCentrifugeRecipes, true, true, true);
        GT_Log.out.println("GT_Mod: Unificating outputs of all known Recipe Types.");
        ArrayList<ItemStack> tStacks = new ArrayList<>(10000);
        GT_Log.out.println("GT_Mod: IC2 Machines");

        ic2.api.recipe.Recipes.cannerBottle.getRecipes().values().stream().map(t -> t.items).forEach(tStacks::addAll);
        ic2.api.recipe.Recipes.centrifuge.getRecipes().values().stream().map(t -> t.items).forEach(tStacks::addAll);
        ic2.api.recipe.Recipes.compressor.getRecipes().values().stream().map(t -> t.items).forEach(tStacks::addAll);
        ic2.api.recipe.Recipes.extractor.getRecipes().values().stream().map(t -> t.items).forEach(tStacks::addAll);
        ic2.api.recipe.Recipes.macerator.getRecipes().values().stream().map(t -> t.items).forEach(tStacks::addAll);
        ic2.api.recipe.Recipes.metalformerCutting.getRecipes().values().stream().map(t -> t.items).forEach(tStacks::addAll);
        ic2.api.recipe.Recipes.metalformerExtruding.getRecipes().values().stream().map(t -> t.items).forEach(tStacks::addAll);
        ic2.api.recipe.Recipes.metalformerRolling.getRecipes().values().stream().map(t -> t.items).forEach(tStacks::addAll);
        ic2.api.recipe.Recipes.matterAmplifier.getRecipes().values().stream().map(t -> t.items).forEach(tStacks::addAll);
        ic2.api.recipe.Recipes.oreWashing.getRecipes().values().stream().map(t -> t.items).forEach(tStacks::addAll);

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

        FurnaceRecipes.smelting().getSmeltingList().values().forEach(k -> tStacks.add((ItemStack) k));

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
                GT_FML_LOGGER.error("GT-ERR-01: @ " + tOutput.getUnlocalizedName() + "   " + tOutput.getDisplayName());
                GT_FML_LOGGER.error("A Recipe used an OreDict Item as Output directly, without copying it before!!! This is a typical CallByReference/CallByValue Error");
                GT_FML_LOGGER.error("Said Item will be renamed to make the invalid Recipe visible, so that you can report it properly.");
                GT_FML_LOGGER.error("Please check all Recipes outputting this Item, and report the Recipes to their Owner.");
                GT_FML_LOGGER.error("The Owner of the ==>RECIPE<==, NOT the Owner of the Item, which has been mentioned above!!!");
                GT_FML_LOGGER.error("And ONLY Recipes which are ==>OUTPUTTING<== the Item, sorry but I don't want failed Bug Reports.");
                GT_FML_LOGGER.error("GregTech just reports this Error to you, so you can report it to the Mod causing the Problem.");
                GT_FML_LOGGER.error("Even though I make that Bug visible, I can not and will not fix that for you, that's for the causing Mod to fix.");
                GT_FML_LOGGER.error("And speaking of failed Reports:");
                GT_FML_LOGGER.error("Both IC2 and GregTech CANNOT be the CAUSE of this Problem, so don't report it to either of them.");
                GT_FML_LOGGER.error("I REPEAT, BOTH, IC2 and GregTech CANNOT be the source of THIS BUG. NO MATTER WHAT.");
                GT_FML_LOGGER.error("Asking in the IC2 Forums, which Mod is causing that, won't help anyone, since it is not possible to determine, which Mod it is.");
                GT_FML_LOGGER.error("If it would be possible, then I would have had added the Mod which is causing it to the Message already. But it is not possible.");
                GT_FML_LOGGER.error("Sorry, but this Error is serious enough to justify this Wall-O-Text and the partially allcapsed Language.");
                GT_FML_LOGGER.error("Also it is a Ban Reason on the IC2-Forums to post this seriously.");
                tOutput.setStackDisplayName("ERROR! PLEASE CHECK YOUR LOG FOR 'GT-ERR-01'!");
            } else {
                GT_OreDictUnificator.setStack(tOutput);
            }
        }
        GregTech_API.mServerStarted = true;
        GT_Log.out.println("GT_Mod: ServerStarting-Phase finished!");
        GT_Log.ore.println("GT_Mod: ServerStarting-Phase finished!");

        for (Runnable tRunnable : GregTech_API.sAfterGTServerstart) {
            try {
                tRunnable.run();
            } catch (Throwable e) {
                e.printStackTrace(GT_Log.err);
            }
        }

        aEvent.registerServerCommand(new GT_Command());
        //Sets a new Machine Block Update Thread everytime a world is loaded
        GT_Runnable_MachineBlockUpdate.initExecutorService();
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

    @Mod.EventHandler
    public void onIDChangingEvent(FMLModIdMappingEvent aEvent) {
        GT_Utility.reInit();
        GT_Recipe.reInit();
        try {
            for (Map<gregtech.api.objects.GT_ItemStack, ?> gt_itemStackMap : GregTech_API.sItemStackMappings) {
                GT_Utility.reMap((Map) gt_itemStackMap);
            }
        } catch (Throwable e) {
            e.printStackTrace(GT_Log.err);
        }

    }

    @Mod.EventHandler
    public void onServerStopping(FMLServerStoppingEvent aEvent) {
        for (Runnable tRunnable : GregTech_API.sBeforeGTServerstop) {
            try {
                tRunnable.run();
            } catch (Throwable e) {
                e.printStackTrace(GT_Log.err);
            }
        }

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

                tList = FluidRegistry.getRegisteredFluids().keySet().toArray(new String[0]);
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
        for (Runnable tRunnable : GregTech_API.sAfterGTServerstop) {
            try {
                tRunnable.run();
            } catch (Throwable e) {
                e.printStackTrace(GT_Log.err);
            }
        }
        //Interrupt IDLE Threads to close down cleanly
        GT_Runnable_MachineBlockUpdate.shutdownExecutorService();
    }

    private void addSolidFakeLargeBoilerFuels() {
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
                GT_OreDictUnificator.get(ItemList.Block_SSFUEL.get(1)),
                GT_OreDictUnificator.get(ItemList.Block_MSSFUEL.get(1)),
                GT_OreDictUnificator.get(OrePrefixes.rod, Materials.Blaze, 1));
        if (Loader.isModLoaded("Thaumcraft")) {
            GT_Recipe.GT_Recipe_Map.sLargeBoilerFakeFuels.addSolidRecipe(GT_ModHandler.getModItem("Thaumcraft", "ItemResource", 1));
        }
    }

    private void identifyAnySteam() {
        final String[] steamCandidates = {"steam", "ic2steam"};
        final String[] superHeatedSteamCandidates = {"ic2superheatedsteam"};

        GT_ModHandler.sAnySteamFluidIDs = Arrays.stream(steamCandidates).map(FluidRegistry::getFluid).filter(Objects::nonNull)
            .map(FluidRegistry::getFluidID).collect(Collectors.toList());
        GT_ModHandler.sSuperHeatedSteamFluidIDs = Arrays.stream(superHeatedSteamCandidates).map(FluidRegistry::getFluid).filter(Objects::nonNull)
            .map(FluidRegistry::getFluidID).collect(Collectors.toList());
    }
}
