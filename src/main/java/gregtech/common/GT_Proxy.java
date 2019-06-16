package gregtech.common;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTech_API;
import gregtech.api.enums.*;
import gregtech.api.enums.TC_Aspects.TC_AspectStack;
import gregtech.api.interfaces.IBlockOnWalkOver;
import gregtech.api.interfaces.IProjectileItem;
import gregtech.api.interfaces.internal.IGT_Mod;
import gregtech.api.interfaces.internal.IThaumcraftCompat;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.items.GT_MetaGenerated_Item;
import gregtech.api.items.GT_MetaGenerated_Tool;
import gregtech.api.net.GT_Packet_Pollution;
import gregtech.api.objects.*;
import gregtech.api.util.*;
import gregtech.common.entities.GT_Entity_Arrow;
import gregtech.common.gui.GT_ContainerVolumetricFlask;
import gregtech.common.gui.GT_GUIContainerVolumetricFlask;
import gregtech.common.items.GT_MetaGenerated_Tool_01;
import gregtech.common.items.armor.ModularArmor_Item;
import gregtech.common.items.armor.gui.*;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.terraingen.OreGenEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.io.File;
import java.text.DateFormat;
import java.util.*;

public abstract class GT_Proxy implements IGT_Mod, IGuiHandler, IFuelHandler {
    private static final EnumSet<OreGenEvent.GenerateMinable.EventType> PREVENTED_ORES = EnumSet.of(OreGenEvent.GenerateMinable.EventType.COAL,
            new OreGenEvent.GenerateMinable.EventType[]{OreGenEvent.GenerateMinable.EventType.IRON, OreGenEvent.GenerateMinable.EventType.GOLD,
                    OreGenEvent.GenerateMinable.EventType.DIAMOND, OreGenEvent.GenerateMinable.EventType.REDSTONE, OreGenEvent.GenerateMinable.EventType.LAPIS,
                    OreGenEvent.GenerateMinable.EventType.QUARTZ});
    public final HashSet<ItemStack> mRegisteredOres = new HashSet<ItemStack>(10000);
    public final ArrayList<String> mSoundNames = new ArrayList<String>();
    public final ArrayList<ItemStack> mSoundItems = new ArrayList<ItemStack>();
    public final ArrayList<Integer> mSoundCounts = new ArrayList<Integer>();
    private final Collection<OreDictEventContainer> mEvents = new HashSet<OreDictEventContainer>();
    private final Collection<String> mIgnoredItems = new HashSet<String>(Arrays.asList(new String[]{"itemGhastTear", "itemFlint", "itemClay", "itemBucketSaltWater",
            "itemBucketFreshWater", "itemBucketWater", "itemRock", "itemReed", "itemArrow", "itemSaw", "itemKnife", "itemHammer", "itemChisel", "itemRubber",
            "itemEssence", "itemIlluminatedPanel", "itemSkull", "itemRawRubber", "itemBacon", "itemJetpackAccelerator", "itemLazurite", "itemIridium",
            "itemTear", "itemClaw", "itemFertilizer", "itemTar", "itemSlimeball", "itemCoke", "itemBeeswax", "itemBeeQueen", "itemForcicium", "itemForcillium",
            "itemRoyalJelly", "itemHoneydew", "itemHoney", "itemPollen", "itemReedTypha", "itemSulfuricAcid", "itemPotash", "itemCompressedCarbon",
            "itemBitumen", "itemBioFuel", "itemCokeSugar", "itemCokeCactus", "itemCharcoalSugar", "itemCharcoalCactus", "itemSludge", "itemEnrichedAlloy",
            "itemQuicksilver", "itemMercury", "itemOsmium", "itemUltimateCircuit", "itemEnergizedStar", "itemAntimatterMolecule", "itemAntimatterGlob",
            "itemCoal", "itemBoat", "itemHerbalMedicineCake", "itemCakeSponge", "itemFishandPumpkinCakeSponge", "itemSoulCleaver", "itemInstantCake",
            "itemWhippingCream", "itemGlisteningWhippingCream", "itemCleaver", "itemHerbalMedicineWhippingCream", "itemStrangeWhippingCream",
            "itemBlazeCleaver", "itemBakedCakeSponge", "itemMagmaCake", "itemGlisteningCake", "itemOgreCleaver", "itemFishandPumpkinCake",
            "itemMagmaWhippingCream", "itemMultimeter", "itemSuperconductor"}));
    private final Collection<String> mIgnoredNames = new HashSet<String>(Arrays.asList(new String[]{"grubBee", "chainLink", "candyCane", "bRedString", "bVial",
            "bFlask", "anorthositeSmooth", "migmatiteSmooth", "slateSmooth", "travertineSmooth", "limestoneSmooth", "orthogneissSmooth", "marbleSmooth",
            "honeyDrop", "lumpClay", "honeyEqualssugar", "flourEqualswheat", "bluestoneInsulated", "blockWaterstone", "blockSand", "blockTorch",
            "blockPumpkin", "blockClothRock", "blockStainedHardenedClay", "blockQuartzPillar", "blockQuartzChiselled", "blockSpawner", "blockCloth", "mobHead",
            "mobEgg", "enderFlower", "enderChest", "clayHardened", "dayGemMaterial", "nightGemMaterial", "snowLayer", "bPlaceholder", "hardenedClay",
            "eternalLifeEssence", "sandstone", "wheatRice", "transdimBlock", "bambooBasket", "lexicaBotania", "livingwoodTwig", "redstoneCrystal",
            "pestleAndMortar", "glowstone", "whiteStone", "stoneSlab", "transdimBlock", "clayBowl", "clayPlate", "ceramicBowl", "ceramicPlate", "ovenRack",
            "clayCup", "ceramicCup", "batteryBox", "transmutationStone", "torchRedstoneActive", "coal", "charcoal", "cloth", "cobblestoneSlab",
            "stoneBrickSlab", "cobblestoneWall", "stoneBrickWall", "cobblestoneStair", "stoneBrickStair", "blockCloud", "blockDirt", "blockTyrian",
            "blockCarpet", "blockFft", "blockLavastone", "blockHolystone", "blockConcrete", "sunnariumPart", "brSmallMachineCyaniteProcessor", "meteoriteCoal",
            "blockCobble", "pressOreProcessor", "crusherOreProcessor", "grinderOreProcessor", "blockRubber", "blockHoney", "blockHoneydew", "blockPeat",
            "blockRadioactive", "blockSlime", "blockCocoa", "blockSugarCane", "blockLeather", "blockClayBrick", "solarPanelHV", "cableRedNet", "stoneBowl",
            "crafterWood", "taintedSoil", "brickXyEngineering", "breederUranium", "wireMill", "chunkLazurite", "aluminumNatural", "aluminiumNatural",
            "naturalAluminum", "naturalAluminium", "antimatterMilligram", "antimatterGram", "strangeMatter", "coalGenerator", "electricFurnace",
            "unfinishedTank", "valvePart", "aquaRegia", "leatherSeal", "leatherSlimeSeal", "hambone", "slimeball", "clay", "enrichedUranium", "camoPaste",
            "antiBlock", "burntQuartz", "salmonRaw", "blockHopper", "blockEnderObsidian", "blockIcestone", "blockMagicWood", "blockEnderCore", "blockHeeEndium",
            "oreHeeEndPowder", "oreHeeStardust", "oreHeeIgneousRock", "oreHeeInstabilityOrb", "crystalPureFluix", "shardNether", "gemFluorite",
            "stickObsidian", "caveCrystal", "shardCrystal", "dyeCrystal","shardFire","shardWater","shardAir","shardEarth","ingotRefinedIron","blockMarble","ingotUnstable"}));
    private final Collection<String> mInvalidNames = new HashSet<String>(Arrays.asList(new String[]{"diamondShard", "redstoneRoot", "obsidianStick", "bloodstoneOre",
            "universalCable", "bronzeTube", "ironTube", "netherTube", "obbyTube", "infiniteBattery", "eliteBattery", "advancedBattery", "10kEUStore",
            "blueDye", "MonazitOre", "quartzCrystal", "whiteLuminiteCrystal", "darkStoneIngot", "invisiumIngot", "demoniteOrb", "enderGem", "starconiumGem",
            "osmoniumIngot", "tapaziteGem", "zectiumIngot", "foolsRubyGem", "rubyGem", "meteoriteGem", "adamiteShard", "sapphireGem", "copperIngot",
            "ironStick", "goldStick", "diamondStick", "reinforcedStick", "draconicStick", "emeraldStick", "copperStick", "tinStick", "silverStick",
            "bronzeStick", "steelStick", "leadStick", "manyullynStick", "arditeStick", "cobaltStick", "aluminiumStick", "alumiteStick", "oilsandsOre",
            "copperWire", "superconductorWire", "sulfuricAcid", "conveyorBelt", "ironWire", "aluminumWire", "aluminiumWire", "silverWire", "tinWire",
            "dustSiliconSmall", "AluminumOre", "plateHeavyT2", "blockWool", "alloyPlateEnergizedHardened", "gasWood", "alloyPlateEnergized", "SilverOre",
            "LeadOre", "TinOre", "CopperOre", "silverOre", "leadOre", "tinOre", "copperOre", "bauxiteOre", "HSLivingmetalIngot", "oilMoving", "oilStill",
            "oilBucket", "petroleumOre", "dieselFuel", "diamondNugget", "planks", "wood", "stick", "sticks", "naquadah", "obsidianRod", "stoneRod",
            "thaumiumRod", "steelRod", "netherrackRod", "woodRod", "ironRod", "cactusRod", "flintRod", "copperRod", "cobaltRod", "alumiteRod", "blueslimeRod",
            "arditeRod", "manyullynRod", "bronzeRod", "boneRod", "slimeRod", "redalloyBundled", "bluestoneBundled", "infusedteslatiteInsulated",
            "redalloyInsulated", "infusedteslatiteBundled"}));
    private final DateFormat mDateFormat = DateFormat.getInstance();
    public ArrayList<String> mBufferedPlayerActivity = new ArrayList();
    public boolean mHardcoreCables = false;
    public boolean mSmallLavaBoilerEfficiencyLoss = true;
    public boolean mDisableVanillaOres = true;
    public boolean mNerfStorageBlocks = true;
    public boolean mHardMachineCasings = true;
    public boolean mAllowSmallBoilerAutomation = false;
    public boolean mNerfDustCrafting = true;
    public boolean mSortToTheEnd = true;
    public boolean mCraftingUnification = true;
    public boolean mInventoryUnification = true;
    public boolean mIncreaseDungeonLoot = true;
    public boolean mAxeWhenAdventure = true;
    public boolean mSurvivalIntoAdventure = false;
    public boolean mNerfedWoodPlank = true;
    public boolean mNerfedVanillaTools = true;
    public boolean mHardRock = false;
    public boolean mHungerEffect = true;
    public boolean mOnline = true;
    public boolean mIgnoreTcon = true;
    public boolean mDisableIC2Cables = false;
    public boolean mAchievements = true;
    public boolean mAE2Integration = true;
    public boolean mAE2Tunnel = true;
    public boolean mArcSmeltIntoAnnealed = true;
    public boolean mMagneticraftRecipes = true;
    public boolean mImmersiveEngineeringRecipes = true;
    private boolean isFirstServerWorldTick = true;
    private boolean mOreDictActivated = false;
    public boolean mChangeHarvestLevels=false;
    public boolean mNerfedCombs = true;
    public boolean mNerfedCrops = true;
    public boolean mGTBees = true;
    public boolean mHideUnusedOres = true;
    public boolean mHideRecyclingRecipes = true;
    public boolean mPollution = true;
    public boolean mExplosionItemDrop = false;
    public int mSkeletonsShootGTArrows = 16;
    public int mMaxEqualEntitiesAtOneSpot = 3;
    public int mFlintChance = 30;
    public int mItemDespawnTime = 6000;
    public int mUpgradeCount = 4;
    public int[] mHarvestLevel= new int[1000];
    public int mGraniteHavestLevel=3;
    public int mMaxHarvestLevel=7;
    public int mWireHeatingTicks = 4;
    public int mPollutionSmogLimit = 500000;
    public int mPollutionPoisonLimit = 750000;
    public int mPollutionVegetationLimit = 1000000;
    public int mPollutionSourRainLimit = 2000000;
    public final GT_UO_DimensionList mUndergroundOil = new GT_UO_DimensionList();
    public int mTicksUntilNextCraftSound = 0;
    public double mMagneticraftBonusOutputPercent = 100.0d;
    private World mUniverse = null;
    private final String aTextThermalExpansion = "ThermalExpansion";
    private final String aTextRailcraft = "Railcraft";
    private final String aTextTwilightForest = "TwilightForest";
    private final String aTextForestry = "Forestry";
    private final String aTextArsmagica2 = "arsmagica2";
    public boolean mTEMachineRecipes = false;
    public boolean mEnableAllMaterials = false;
    public boolean mEnableAllComponents = false;
    public boolean mAddGTRecipesToIC2Machines = true;
	public boolean mEnableCleanroom = true;
    public boolean mLowGravProcessing = false;
    public boolean mAprilFool = false;
    public boolean mCropNeedBlock = true;
    public boolean mDisableOldChemicalRecipes = false;
    public boolean mAMHInteraction = true;
    public boolean mForceFreeFace = false;
    public boolean mEasierIVPlusCables = false;
    public boolean mBrickedBlastFurnace = true;
    public boolean mMixedOreOnlyYieldsTwoThirdsOfPureOre = false;
    public boolean enableBlackGraniteOres = true;
    public boolean enableRedGraniteOres = true;
    public boolean enableMarbleOres = true;
    public boolean enableBasaltOres = true;
    public boolean enableGCOres = true;
    public boolean enableUBOres = true;
    public boolean gt6Pipe = true;
    public boolean gt6Cable = true;
    public boolean ic2EnergySourceCompat = true;
    public boolean costlyCableConnection = false;
    public boolean mMoreComplicatedChemicalRecipes = false;
    
    public GT_Proxy() {
        GameRegistry.registerFuelHandler(this);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.ORE_GEN_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
        GregTech_API.sThaumcraftCompat = (IThaumcraftCompat) GT_Utility.callConstructor("gregtech.common.GT_ThaumcraftCompat", 0, null, GT_Values.D1,
                new Object[0]);
        for (FluidContainerRegistry.FluidContainerData tData : FluidContainerRegistry.getRegisteredFluidContainerData()) {
            onFluidContainerRegistration(new FluidContainerRegistry.FluidContainerRegisterEvent(tData));
        }
        try {
            for (String tOreName : OreDictionary.getOreNames()) {
                ItemStack tOreStack;
                for (Iterator i$ = OreDictionary.getOres(tOreName).iterator(); i$.hasNext(); registerOre(new OreDictionary.OreRegisterEvent(tOreName, tOreStack))) {
                    tOreStack = (ItemStack) i$.next();
                }
            }
        } catch (Throwable e) {e.printStackTrace(GT_Log.err);}
    }

    private static final void registerRecipes(OreDictEventContainer aOre) {
        if ((aOre.mEvent.Ore == null) || (aOre.mEvent.Ore.getItem() == null)) {
            return;
        }
        if (aOre.mEvent.Ore.stackSize != 1) {
            aOre.mEvent.Ore.stackSize = 1;
        }
        if (aOre.mPrefix != null) {
            if (!aOre.mPrefix.isIgnored(aOre.mMaterial)) {
                aOre.mPrefix.processOre(aOre.mMaterial == null ? Materials._NULL : aOre.mMaterial, aOre.mEvent.Name, aOre.mModID,
                        GT_Utility.copyAmount(1L, new Object[]{aOre.mEvent.Ore}));
            }
        } else {
//			System.out.println("Thingy Name: "+ aOre.mEvent.Name+ " !!!Unknown 'Thingy' detected!!! This Object seems to probably not follow a valid OreDictionary Convention, or I missed a Convention. Please report to GregTech Intergalactical for additional compatiblity. This is not an Error, an Issue nor a Lag Source, it is just an Information, which you should pass to me.");
        }
    }

    public void onPreLoad() {
        GT_Log.out.println("GT_Mod: Preload-Phase started!");
        GT_Log.ore.println("GT_Mod: Preload-Phase started!");

        GregTech_API.sPreloadStarted = true;
        this.mIgnoreTcon = GregTech_API.sOPStuff.get(ConfigCategories.general, "ignoreTConstruct", true);
        this.mWireHeatingTicks = GregTech_API.sOPStuff.get(ConfigCategories.general, "WireHeatingTicks", 4);
        NetworkRegistry.INSTANCE.registerGuiHandler(GT_Values.GT, this);
        for (FluidContainerRegistry.FluidContainerData tData : FluidContainerRegistry.getRegisteredFluidContainerData()) {
            if ((tData.filledContainer.getItem() == Items.potionitem) && (tData.filledContainer.getItemDamage() == 0)) {
                tData.fluid.amount = 0;
                break;
            }
        }
        GT_Log.out.println("GT_Mod: Getting required Items of other Mods.");
        ItemList.TE_Slag.set(GT_ModHandler.getModItem(aTextThermalExpansion, "slag", 1L));
        ItemList.TE_Slag_Rich.set(GT_ModHandler.getModItem(aTextThermalExpansion, "slagRich", 1L));
        ItemList.TE_Rockwool.set(GT_ModHandler.getModItem(aTextThermalExpansion, "rockwool", 1L));
        ItemList.TE_Hardened_Glass.set(GT_ModHandler.getModItem(aTextThermalExpansion, "glassHardened", 1L));

        ItemList.RC_ShuntingWire.set(GT_ModHandler.getModItem(aTextRailcraft, "tile.railcraft.machine.delta", 1L, 0));
        ItemList.RC_ShuntingWireFrame.set(GT_ModHandler.getModItem(aTextRailcraft, "tile.railcraft.frame", 1L, 0));
        ItemList.RC_Rail_Standard.set(GT_ModHandler.getModItem(aTextRailcraft, "part.rail", 1L, 0));
        ItemList.RC_Rail_Adv.set(GT_ModHandler.getModItem(aTextRailcraft, "part.rail", 1L, 1));
        ItemList.RC_Rail_Wooden.set(GT_ModHandler.getModItem(aTextRailcraft, "part.rail", 1L, 2));
        ItemList.RC_Rail_HS.set(GT_ModHandler.getModItem(aTextRailcraft, "part.rail", 1L, 3));
        ItemList.RC_Rail_Reinforced.set(GT_ModHandler.getModItem(aTextRailcraft, "part.rail", 1L, 4));
        ItemList.RC_Rail_Electric.set(GT_ModHandler.getModItem(aTextRailcraft, "part.rail", 1L, 5));
        ItemList.RC_Tie_Wood.set(GT_ModHandler.getModItem(aTextRailcraft, "part.tie", 1L, 0));
        ItemList.RC_Tie_Stone.set(GT_ModHandler.getModItem(aTextRailcraft, "part.tie", 1L, 1));
        ItemList.RC_Bed_Wood.set(GT_ModHandler.getModItem(aTextRailcraft, "part.railbed", 1L, 0));
        ItemList.RC_Bed_Stone.set(GT_ModHandler.getModItem(aTextRailcraft, "part.railbed", 1L, 1));
        ItemList.RC_Rebar.set(GT_ModHandler.getModItem(aTextRailcraft, "part.rebar", 1L));
        ItemList.Tool_Sword_Steel.set(GT_ModHandler.getModItem(aTextRailcraft, "tool.steel.sword", 1L));
        ItemList.Tool_Pickaxe_Steel.set(GT_ModHandler.getModItem(aTextRailcraft, "tool.steel.pickaxe", 1L));
        ItemList.Tool_Shovel_Steel.set(GT_ModHandler.getModItem(aTextRailcraft, "tool.steel.shovel", 1L));
        ItemList.Tool_Axe_Steel.set(GT_ModHandler.getModItem(aTextRailcraft, "tool.steel.axe", 1L));
        ItemList.Tool_Hoe_Steel.set(GT_ModHandler.getModItem(aTextRailcraft, "tool.steel.hoe", 1L));

        ItemList.TF_LiveRoot.set(GT_ModHandler.getModItem(aTextTwilightForest, "item.liveRoot", 1L, 0));
        ItemList.TF_Vial_FieryBlood.set(GT_ModHandler.getModItem(aTextTwilightForest, "item.fieryBlood", 1L));
        ItemList.TF_Vial_FieryTears.set(GT_ModHandler.getModItem(aTextTwilightForest, "item.fieryTears", 1L));

        ItemList.FR_Lemon.set(GT_ModHandler.getModItem(aTextForestry, "fruits", 1L, 3));
        ItemList.FR_Mulch.set(GT_ModHandler.getModItem(aTextForestry, "mulch", 1L));
        ItemList.FR_Fertilizer.set(GT_ModHandler.getModItem(aTextForestry, "fertilizerCompound", 1L));
        ItemList.FR_Compost.set(GT_ModHandler.getModItem(aTextForestry, "fertilizerBio", 1L));
        ItemList.FR_Silk.set(GT_ModHandler.getModItem(aTextForestry, "craftingMaterial", 1L, 2));
        ItemList.FR_Wax.set(GT_ModHandler.getModItem(aTextForestry, "beeswax", 1L));
        ItemList.FR_WaxCapsule.set(GT_ModHandler.getModItem(aTextForestry, "waxCapsule", 1L));
        ItemList.FR_RefractoryWax.set(GT_ModHandler.getModItem(aTextForestry, "refractoryWax", 1L));
        ItemList.FR_RefractoryCapsule.set(GT_ModHandler.getModItem(aTextForestry, "refractoryEmpty", 1L));
        ItemList.FR_Bee_Drone.set(GT_ModHandler.getModItem(aTextForestry, "beeDroneGE", 1L));
        ItemList.FR_Bee_Princess.set(GT_ModHandler.getModItem(aTextForestry, "beePrincessGE", 1L));
        ItemList.FR_Bee_Queen.set(GT_ModHandler.getModItem(aTextForestry, "beeQueenGE", 1L));
        ItemList.FR_Tree_Sapling.set(GT_ModHandler.getModItem(aTextForestry, "sapling", 1L, GT_ModHandler.getModItem(aTextForestry, "saplingGE", 1L)));
        ItemList.FR_Butterfly.set(GT_ModHandler.getModItem(aTextForestry, "butterflyGE", 1L));
        ItemList.FR_Larvae.set(GT_ModHandler.getModItem(aTextForestry, "beeLarvaeGE", 1L));
        ItemList.FR_Serum.set(GT_ModHandler.getModItem(aTextForestry, "serumGE", 1L));
        ItemList.FR_Caterpillar.set(GT_ModHandler.getModItem(aTextForestry, "caterpillarGE", 1L));
        ItemList.FR_PollenFertile.set(GT_ModHandler.getModItem(aTextForestry, "pollenFertile", 1L));
        ItemList.FR_Stick.set(GT_ModHandler.getModItem(aTextForestry, "oakStick", 1L));
        ItemList.FR_Casing_Impregnated.set(GT_ModHandler.getModItem(aTextForestry, "impregnatedCasing", 1L));
        ItemList.FR_Casing_Sturdy.set(GT_ModHandler.getModItem(aTextForestry, "sturdyMachine", 1L));
        ItemList.FR_Casing_Hardened.set(GT_ModHandler.getModItem(aTextForestry, "hardenedMachine", 1L));

        ItemList.Bottle_Empty.set(new ItemStack(Items.glass_bottle, 1));

        ItemList.Cell_Universal_Fluid.set(GT_ModHandler.getIC2Item("FluidCell", 1L));
        ItemList.Cell_Empty.set(GT_ModHandler.getIC2Item("cell", 1L, GT_ModHandler.getIC2Item("cellEmpty", 1L, GT_ModHandler.getIC2Item("emptyCell", 1L))));
        ItemList.Cell_Water.set(GT_ModHandler.getIC2Item("waterCell", 1L, GT_ModHandler.getIC2Item("cellWater", 1L)));
        ItemList.Cell_Lava.set(GT_ModHandler.getIC2Item("lavaCell", 1L, GT_ModHandler.getIC2Item("cellLava", 1L)));
        ItemList.Cell_Air.set(GT_ModHandler.getIC2Item("airCell", 1L, GT_ModHandler.getIC2Item("cellAir", 1L, GT_ModHandler.getIC2Item("cellOxygen", 1L))));

        ItemList.IC2_Item_Casing_Iron.set(GT_ModHandler.getIC2Item("casingiron", 1L));
        ItemList.IC2_Item_Casing_Gold.set(GT_ModHandler.getIC2Item("casinggold", 1L));
        ItemList.IC2_Item_Casing_Bronze.set(GT_ModHandler.getIC2Item("casingbronze", 1L));
        ItemList.IC2_Item_Casing_Copper.set(GT_ModHandler.getIC2Item("casingcopper", 1L));
        ItemList.IC2_Item_Casing_Tin.set(GT_ModHandler.getIC2Item("casingtin", 1L));
        ItemList.IC2_Item_Casing_Lead.set(GT_ModHandler.getIC2Item("casinglead", 1L));
        ItemList.IC2_Item_Casing_Steel.set(GT_ModHandler.getIC2Item("casingadviron", 1L));
        ItemList.IC2_Spray_WeedEx.set(GT_ModHandler.getIC2Item("weedEx", 1L));
        ItemList.IC2_Fuel_Can_Empty.set(GT_ModHandler.getIC2Item("fuelCan", 1L, GT_ModHandler.getIC2Item("fuelCanEmpty", 1L, GT_ModHandler.getIC2Item("emptyFuelCan", 1L))));
        ItemList.IC2_Fuel_Can_Filled.set(GT_ModHandler.getIC2Item("filledFuelCan", 1L));
        ItemList.IC2_Mixed_Metal_Ingot.set(GT_ModHandler.getIC2Item("mixedMetalIngot", 1L));
        ItemList.IC2_Fertilizer.set(GT_ModHandler.getIC2Item("fertilizer", 1L));
        ItemList.IC2_CoffeeBeans.set(GT_ModHandler.getIC2Item("coffeeBeans", 1L));
        ItemList.IC2_CoffeePowder.set(GT_ModHandler.getIC2Item("coffeePowder", 1L));
        ItemList.IC2_Hops.set(GT_ModHandler.getIC2Item("hops", 1L));
        ItemList.IC2_Resin.set(GT_ModHandler.getIC2Item("resin", 1L));
        ItemList.IC2_Plantball.set(GT_ModHandler.getIC2Item("plantBall", 1L));
        ItemList.IC2_PlantballCompressed.set(GT_ModHandler.getIC2Item("compressedPlantBall", 1L, ItemList.IC2_Plantball.get(1L, new Object[0])));
        ItemList.IC2_Crop_Seeds.set(GT_ModHandler.getIC2Item("cropSeed", 1L));
        ItemList.IC2_Grin_Powder.set(GT_ModHandler.getIC2Item("grinPowder", 1L));
        ItemList.IC2_Energium_Dust.set(GT_ModHandler.getIC2Item("energiumDust", 1L));
        ItemList.IC2_Scrap.set(GT_ModHandler.getIC2Item("scrap", 1L));
        ItemList.IC2_Scrapbox.set(GT_ModHandler.getIC2Item("scrapBox", 1L));
        ItemList.IC2_Fuel_Rod_Empty.set(GT_ModHandler.getIC2Item("fuelRod", 1L));
        ItemList.IC2_Food_Can_Empty.set(GT_ModHandler.getIC2Item("tinCan", 1L));
        ItemList.IC2_Food_Can_Filled.set(GT_ModHandler.getIC2Item("filledTinCan", 1L, 0));
        ItemList.IC2_Food_Can_Spoiled.set(GT_ModHandler.getIC2Item("filledTinCan", 1L, 1));
        ItemList.IC2_Industrial_Diamond.set(GT_ModHandler.getIC2Item("industrialDiamond", 1L, new ItemStack(Items.diamond, 1)));
        ItemList.IC2_Compressed_Coal_Ball.set(GT_ModHandler.getIC2Item("compressedCoalBall", 1L));
        ItemList.IC2_Compressed_Coal_Chunk.set(GT_ModHandler.getIC2Item("coalChunk", 1L));
        ItemList.IC2_ShaftIron.set(GT_ModHandler.getIC2Item("ironshaft", 1L));
        ItemList.IC2_ShaftSteel.set(GT_ModHandler.getIC2Item("steelshaft", 1L));

        ItemList.IC2_SuBattery.set(GT_ModHandler.getIC2Item("suBattery", 1L));
        ItemList.IC2_ReBattery.set(GT_ModHandler.getIC2Item("reBattery", 1L));
        ItemList.IC2_AdvBattery.set(GT_ModHandler.getIC2Item("advBattery", 1L));
        ItemList.IC2_EnergyCrystal.set(GT_ModHandler.getIC2Item("energyCrystal", 1L));
        ItemList.IC2_LapotronCrystal.set(GT_ModHandler.getIC2Item("lapotronCrystal", 1L));

        ItemList.IC2_LapotronCrystal.set(GT_ModHandler.getIC2Item("lapotronCrystal", 1L));
        ItemList.IC2_LapotronCrystal.set(GT_ModHandler.getIC2Item("lapotronCrystal", 1L));
        ItemList.IC2_LapotronCrystal.set(GT_ModHandler.getIC2Item("lapotronCrystal", 1L));

        ItemList.Tool_Sword_Bronze.set(GT_ModHandler.getIC2Item("bronzeSword", 1L));
        ItemList.Tool_Pickaxe_Bronze.set(GT_ModHandler.getIC2Item("bronzePickaxe", 1L));
        ItemList.Tool_Shovel_Bronze.set(GT_ModHandler.getIC2Item("bronzeShovel", 1L));
        ItemList.Tool_Axe_Bronze.set(GT_ModHandler.getIC2Item("bronzeAxe", 1L));
        ItemList.Tool_Hoe_Bronze.set(GT_ModHandler.getIC2Item("bronzeHoe", 1L));
        ItemList.IC2_ForgeHammer.set(GT_ModHandler.getIC2Item("ForgeHammer", 1L));
        ItemList.IC2_WireCutter.set(GT_ModHandler.getIC2Item("cutter", 1L));

        ItemList.Credit_Iron.set(GT_ModHandler.getIC2Item("coin", 1L));

        ItemList.Circuit_Basic.set(GT_ModHandler.getIC2Item("electronicCircuit", 1L));
        ItemList.Circuit_Advanced.set(GT_ModHandler.getIC2Item("advancedCircuit", 1L));

        ItemList.Upgrade_Overclocker.set(GT_ModHandler.getIC2Item("overclockerUpgrade", 1L));
        ItemList.Upgrade_Battery.set(GT_ModHandler.getIC2Item("energyStorageUpgrade", 1L));

        ItemList.Dye_Bonemeal.set(new ItemStack(Items.dye, 1, 15));
        ItemList.Dye_SquidInk.set(new ItemStack(Items.dye, 1, 0));
        ItemList.Dye_Cocoa.set(new ItemStack(Items.dye, 1, 3));

        ItemList.Book_Written_00.set(new ItemStack(Items.written_book, 1, 0));

        ItemList.Food_Baked_Bread.set(new ItemStack(Items.bread, 1, 0));
        ItemList.Food_Raw_Potato.set(new ItemStack(Items.potato, 1, 0));
        ItemList.Food_Baked_Potato.set(new ItemStack(Items.baked_potato, 1, 0));
        ItemList.Food_Poisonous_Potato.set(new ItemStack(Items.poisonous_potato, 1, 0));

        OrePrefixes.bottle.mContainerItem = ItemList.Bottle_Empty.get(1L, new Object[0]);
        OrePrefixes.bucket.mContainerItem = new ItemStack(Items.bucket, 1);
        OrePrefixes.cellPlasma.mContainerItem = ItemList.Cell_Empty.get(1L, new Object[0]);
        OrePrefixes.cell.mContainerItem = ItemList.Cell_Empty.get(1L, new Object[0]);

        GregTech_API.sFrostHazmatList.add(GT_ModHandler.getIC2Item("hazmatHelmet", 1L, 32767));
        GregTech_API.sFrostHazmatList.add(GT_ModHandler.getIC2Item("hazmatChestplate", 1L, 32767));
        GregTech_API.sFrostHazmatList.add(GT_ModHandler.getIC2Item("hazmatLeggings", 1L, 32767));
        GregTech_API.sFrostHazmatList.add(GT_ModHandler.getIC2Item("hazmatBoots", 1L, 32767));

        GregTech_API.sHeatHazmatList.add(GT_ModHandler.getIC2Item("hazmatHelmet", 1L, 32767));
        GregTech_API.sHeatHazmatList.add(GT_ModHandler.getIC2Item("hazmatChestplate", 1L, 32767));
        GregTech_API.sHeatHazmatList.add(GT_ModHandler.getIC2Item("hazmatLeggings", 1L, 32767));
        GregTech_API.sHeatHazmatList.add(GT_ModHandler.getIC2Item("hazmatBoots", 1L, 32767));

        GregTech_API.sBioHazmatList.add(GT_ModHandler.getIC2Item("hazmatHelmet", 1L, 32767));
        GregTech_API.sBioHazmatList.add(GT_ModHandler.getIC2Item("hazmatChestplate", 1L, 32767));
        GregTech_API.sBioHazmatList.add(GT_ModHandler.getIC2Item("hazmatLeggings", 1L, 32767));
        GregTech_API.sBioHazmatList.add(GT_ModHandler.getIC2Item("hazmatBoots", 1L, 32767));

        GregTech_API.sGasHazmatList.add(GT_ModHandler.getIC2Item("hazmatHelmet", 1L, 32767));
        GregTech_API.sGasHazmatList.add(GT_ModHandler.getIC2Item("hazmatChestplate", 1L, 32767));
        GregTech_API.sGasHazmatList.add(GT_ModHandler.getIC2Item("hazmatLeggings", 1L, 32767));
        GregTech_API.sGasHazmatList.add(GT_ModHandler.getIC2Item("hazmatBoots", 1L, 32767));

        GregTech_API.sRadioHazmatList.add(GT_ModHandler.getIC2Item("hazmatHelmet", 1L, 32767));
        GregTech_API.sRadioHazmatList.add(GT_ModHandler.getIC2Item("hazmatChestplate", 1L, 32767));
        GregTech_API.sRadioHazmatList.add(GT_ModHandler.getIC2Item("hazmatLeggings", 1L, 32767));
        GregTech_API.sRadioHazmatList.add(GT_ModHandler.getIC2Item("hazmatBoots", 1L, 32767));

        GregTech_API.sElectroHazmatList.add(GT_ModHandler.getIC2Item("hazmatHelmet", 1L, 32767));
        GregTech_API.sElectroHazmatList.add(GT_ModHandler.getIC2Item("hazmatChestplate", 1L, 32767));
        GregTech_API.sElectroHazmatList.add(GT_ModHandler.getIC2Item("hazmatLeggings", 1L, 32767));
        GregTech_API.sElectroHazmatList.add(GT_ModHandler.getIC2Item("hazmatBoots", 1L, 32767));
        GregTech_API.sElectroHazmatList.add(new ItemStack(Items.chainmail_helmet, 1, 32767));
        GregTech_API.sElectroHazmatList.add(new ItemStack(Items.chainmail_chestplate, 1, 32767));
        GregTech_API.sElectroHazmatList.add(new ItemStack(Items.chainmail_leggings, 1, 32767));
        GregTech_API.sElectroHazmatList.add(new ItemStack(Items.chainmail_boots, 1, 32767));

        GT_ModHandler.sNonReplaceableItems.add(new ItemStack(Items.bow, 1, 32767));
        GT_ModHandler.sNonReplaceableItems.add(new ItemStack(Items.fishing_rod, 1, 32767));
        GT_ModHandler.sNonReplaceableItems.add(ItemList.IC2_ForgeHammer.getWithDamage(1L, 32767L, new Object[0]));
        GT_ModHandler.sNonReplaceableItems.add(ItemList.IC2_WireCutter.getWithDamage(1L, 32767L, new Object[0]));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("painter", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("blackPainter", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("redPainter", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("greenPainter", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("brownPainter", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("bluePainter", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("purplePainter", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("cyanPainter", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("lightGreyPainter", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("darkGreyPainter", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("pinkPainter", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("limePainter", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("yellowPainter", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("cloudPainter", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("magentaPainter", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("orangePainter", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("whitePainter", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("cfPack", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("jetpack", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("treetap", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("weedEx", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("staticBoots", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("compositeArmor", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("hazmatHelmet", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("hazmatChestplate", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("hazmatLeggings", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getIC2Item("hazmatBoots", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextRailcraft, "part.turbine.disk", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextRailcraft, "part.turbine.blade", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextRailcraft, "part.turbine.rotor", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextRailcraft, "borehead.diamond", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextRailcraft, "borehead.steel", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextRailcraft, "borehead.iron", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextTwilightForest, "item.plateNaga", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextTwilightForest, "item.legsNaga", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextTwilightForest, "item.arcticHelm", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextTwilightForest, "item.arcticPlate", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextTwilightForest, "item.arcticLegs", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextTwilightForest, "item.arcticBoots", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextTwilightForest, "item.yetiHelm", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextTwilightForest, "item.yetiPlate", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextTwilightForest, "item.yetiLegs", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextTwilightForest, "item.yetiBoots", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem("appliedenergistics2", "item.ToolCertusQuartzCuttingKnife", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem("appliedenergistics2", "item.ToolNetherQuartzCuttingKnife", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextForestry, "apiaristHelmet", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextForestry, "apiaristChest", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextForestry, "apiaristLegs", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextForestry, "apiaristBoots", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextForestry, "frameUntreated", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextForestry, "frameImpregnated", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextForestry, "frameProven", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem(aTextForestry, "waxCast", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem("GalacticraftCore", "item.sensorGlasses", 1L, 32767));
        GT_ModHandler.sNonReplaceableItems.add(GT_ModHandler.getModItem("IC2NuclearControl", "ItemToolThermometer", 1L, 32767));

        RecipeSorter.register("gregtech:shaped", GT_Shaped_Recipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped before:minecraft:shapeless");
        RecipeSorter.register("gregtech:shapeless", GT_Shapeless_Recipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
    }

    public void onLoad() {
        GT_Log.out.println("GT_Mod: Beginning Load-Phase.");
        GT_Log.ore.println("GT_Mod: Beginning Load-Phase.");
        GT_OreDictUnificator.registerOre("cropChilipepper", GT_ModHandler.getModItem("magicalcrops", "magicalcrops_CropProduce", 1L, 2));
        GT_OreDictUnificator.registerOre("cropTomato", GT_ModHandler.getModItem("magicalcrops", "magicalcrops_CropProduce", 1L, 8));
        GT_OreDictUnificator.registerOre("cropGrape", GT_ModHandler.getModItem("magicalcrops", "magicalcrops_CropProduce", 1L, 4));
        GT_OreDictUnificator.registerOre("cropTea", GT_ModHandler.getModItem("ganyssurface", "teaLeaves", 1L, 0));

        GregTech_API.sLoadStarted = true;
        for (FluidContainerRegistry.FluidContainerData tData : FluidContainerRegistry.getRegisteredFluidContainerData()) {
            if ((tData.filledContainer.getItem() == Items.potionitem) && (tData.filledContainer.getItemDamage() == 0)) {
                tData.fluid.amount = 0;
                break;
            }
        }
        GT_LanguageManager.writePlaceholderStrings();
    }

    public static long tBits = GT_ModHandler.RecipeBits.DO_NOT_CHECK_FOR_COLLISIONS | GT_ModHandler.RecipeBits.BUFFERED | GT_ModHandler.RecipeBits.ONLY_ADD_IF_RESULT_IS_NOT_NULL | GT_ModHandler.RecipeBits.NOT_REMOVABLE;
    public void onPostLoad() {
        GT_Log.out.println("GT_Mod: Beginning PostLoad-Phase.");
        GT_Log.ore.println("GT_Mod: Beginning PostLoad-Phase.");
        if (GT_Log.pal != null) {
            new Thread(new GT_PlayerActivityLogger()).start();
        }
        GregTech_API.sPostloadStarted = true;
        GT_OreDictUnificator.addItemData(new ItemStack(Items.iron_door, 1), new ItemData(Materials.Iron, 21772800L, new MaterialStack[0]));
        GT_OreDictUnificator.addItemData(new ItemStack(Items.wooden_door, 1, 32767), new ItemData(Materials.Wood, 21772800L, new MaterialStack[0]));
        for (FluidContainerRegistry.FluidContainerData tData : FluidContainerRegistry.getRegisteredFluidContainerData()) {
            if ((tData.filledContainer.getItem() == Items.potionitem) && (tData.filledContainer.getItemDamage() == 0)) {
                tData.fluid.amount = 0;
                break;
            }
        }
        GT_Log.out.println("GT_Mod: Adding Configs specific for MetaTileEntities");
        try {
            for (int i = 1; i < GregTech_API.METATILEENTITIES.length; i++) {
                for (; i < GregTech_API.METATILEENTITIES.length; i++) {
                    if (GregTech_API.METATILEENTITIES[i] != null) {
                        GregTech_API.METATILEENTITIES[i].onConfigLoad(GregTech_API.sMachineFile);
                    }
                }
            }
        } catch (Throwable e) {e.printStackTrace(GT_Log.err);}
        GT_Log.out.println("GT_Mod: Adding Tool Usage Crafting Recipes for OreDict Items.");
        for (Materials aMaterial : Materials.values()) {
            if ((aMaterial.mUnificatable) && (aMaterial.mMaterialInto == aMaterial)) {
                GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, aMaterial.mMacerateInto, 1L), tBits, new Object[]{"h", "X",
                        'X', OrePrefixes.crushedCentrifuged.get(aMaterial)});
                GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, aMaterial.mMacerateInto, 1L), tBits, new Object[]{"h", "X",
                        'X', OrePrefixes.crystalline.get(aMaterial)});
                GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, aMaterial.mMacerateInto, 1L), tBits, new Object[]{"h", "X",
                        'X', OrePrefixes.crystal.get(aMaterial)});
                GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.dustPure, aMaterial.mMacerateInto, 1L), tBits, new Object[]{"h", "X",
                        'X', OrePrefixes.crushedPurified.get(aMaterial)});
                GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.dustPure, aMaterial.mMacerateInto, 1L), tBits, new Object[]{"h", "X",
                        'X', OrePrefixes.cleanGravel.get(aMaterial)});
                GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.dustPure, aMaterial.mMacerateInto, 1L), tBits, new Object[]{"h", "X",
                        'X', OrePrefixes.reduced.get(aMaterial)});
                GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.dustImpure, aMaterial.mMacerateInto, 1L), tBits, new Object[]{"h", "X",
                        'X', OrePrefixes.clump.get(aMaterial)});
                GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.dustImpure, aMaterial.mMacerateInto, 1L), tBits, new Object[]{"h", "X",
                        'X', OrePrefixes.shard.get(aMaterial)});
                GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.dustImpure, aMaterial.mMacerateInto, 1L), tBits, new Object[]{"h", "X",
                        'X', OrePrefixes.crushed.get(aMaterial)});
                GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.dustImpure, aMaterial.mMacerateInto, 1L), tBits, new Object[]{"h", "X",
                        'X', OrePrefixes.dirtyGravel.get(aMaterial)});
                GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.dustSmall, aMaterial, 4L), tBits,
                        new Object[]{" X", "  ", 'X', OrePrefixes.dust.get(aMaterial)});
                GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.dustTiny, aMaterial, 9L), tBits,
                        new Object[]{"X ", "  ", 'X', OrePrefixes.dust.get(aMaterial)});
                GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, aMaterial, 1L), tBits,
                        new Object[]{"XX", "XX", 'X', OrePrefixes.dustSmall.get(aMaterial)});
                GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.dust, aMaterial, 1L), tBits,
                        new Object[]{"XXX", "XXX", "XXX", 'X', OrePrefixes.dustTiny.get(aMaterial)});
            }
        }
    }

    public void onServerStarting() {
        GT_Log.out.println("GT_Mod: ServerStarting-Phase started!");
        GT_Log.ore.println("GT_Mod: ServerStarting-Phase started!");

        this.mUniverse = null;
        this.isFirstServerWorldTick = true;
        for (FluidContainerRegistry.FluidContainerData tData : FluidContainerRegistry.getRegisteredFluidContainerData()) {
            if ((tData.filledContainer.getItem() == Items.potionitem) && (tData.filledContainer.getItemDamage() == 0)) {
                tData.fluid.amount = 0;
                break;
            }
        }
        try {
            for (int i = 1; i < GregTech_API.METATILEENTITIES.length; i++) {
                for (; i < GregTech_API.METATILEENTITIES.length; i++) {
                    if (GregTech_API.METATILEENTITIES[i] != null) {
                        GregTech_API.METATILEENTITIES[i].onServerStart();
                    }
                }
            }
        } catch (Throwable e) {e.printStackTrace(GT_Log.err);}

        dimensionWiseChunkData.clear();//!!! IMPORTANT for map switching...
        dimensionWisePollution.clear();//!!! IMPORTANT for map switching...
    }

    public void onServerStarted() {
        GregTech_API.sWirelessRedstone.clear();
        GT_FluidStack.fixAllThoseFuckingFluidIDs();

        GT_Log.out.println("GT_Mod: Cleaning up all OreDict Crafting Recipes, which have an empty List in them, since they are never meeting any Condition.");
        List tList = CraftingManager.getInstance().getRecipeList();
        for (int i = 0; i < tList.size(); i++) {
            if ((tList.get(i) instanceof ShapedOreRecipe)) {
                for (Object tObject : ((ShapedOreRecipe) tList.get(i)).getInput()) {
                    if (((tObject instanceof List)) && (((List) tObject).isEmpty())) {
                        tList.remove(i--);
                        break;
                    }
                }
            } else if ((tList.get(i) instanceof ShapelessOreRecipe)) {
                for (Object tObject : ((ShapelessOreRecipe) tList.get(i)).getInput()) {
                    if (((tObject instanceof List)) && (((List) tObject).isEmpty())) {
                        tList.remove(i--);
                        break;
                    }
                }
            }
        }
    }

    public void onServerStopping() {
        File tSaveDirectory = getSaveDirectory();
        GregTech_API.sWirelessRedstone.clear();
        if (tSaveDirectory != null) {
            try {
                for (int i = 1; i < GregTech_API.METATILEENTITIES.length; i++) {
                    for (; i < GregTech_API.METATILEENTITIES.length; i++) {
                        if (GregTech_API.METATILEENTITIES[i] != null) {
                            GregTech_API.METATILEENTITIES[i].onWorldSave(tSaveDirectory);
                        }
                    }
                }
            } catch (Throwable e) {e.printStackTrace(GT_Log.err);}
        }
        this.mUniverse = null;
    }

    @SubscribeEvent
    public void onClientConnectedToServerEvent(FMLNetworkEvent.ClientConnectedToServerEvent aEvent) {
    }

    @SubscribeEvent
    public void onArrowNockEvent(ArrowNockEvent aEvent) {
        if ((!aEvent.isCanceled()) && (GT_Utility.isStackValid(aEvent.result))
                && (GT_Utility.getProjectile(SubTag.PROJECTILE_ARROW, aEvent.entityPlayer.inventory) != null)) {
            aEvent.entityPlayer.setItemInUse(aEvent.result, aEvent.result.getItem().getMaxItemUseDuration(aEvent.result));
            aEvent.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onArrowLooseEvent(ArrowLooseEvent aEvent) {
        ItemStack aArrow = GT_Utility.getProjectile(SubTag.PROJECTILE_ARROW, aEvent.entityPlayer.inventory);
        if ((!aEvent.isCanceled()) && (GT_Utility.isStackValid(aEvent.bow)) && (aArrow != null) && ((aEvent.bow.getItem() instanceof ItemBow))) {
            float tSpeed = aEvent.charge / 20.0F;
            tSpeed = (tSpeed * tSpeed + tSpeed * 2.0F) / 3.0F;
            if (tSpeed < 0.1D) {
                return;
            }
            if (tSpeed > 1.0D) {
                tSpeed = 1.0F;
            }
            EntityArrow tArrowEntity = ((IProjectileItem) aArrow.getItem()).getProjectile(SubTag.PROJECTILE_ARROW, aArrow, aEvent.entityPlayer.worldObj,
                    aEvent.entityPlayer, tSpeed * 2.0F);
            if (tSpeed >= 1.0F) {
                tArrowEntity.setIsCritical(true);
            }
            int tLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, aEvent.bow);
            if (tLevel > 0) {
                tArrowEntity.setDamage(tArrowEntity.getDamage() + tLevel * 0.5D + 0.5D);
            }
            tLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, aEvent.bow);
            if (tLevel > 0) {
                tArrowEntity.setKnockbackStrength(tLevel);
            }
            tLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, aEvent.bow);
            if (tLevel > 0) {
                tArrowEntity.setFire(tLevel * 100);
            }
            aEvent.bow.damageItem(1, aEvent.entityPlayer);
            aEvent.bow.getItem();
            aEvent.entityPlayer.worldObj.playSoundAtEntity(aEvent.entityPlayer, "random.bow", 1.0F, 0.64893958288F + tSpeed
                    * 0.5F);

            tArrowEntity.canBePickedUp = 1;
            if (!aEvent.entityPlayer.capabilities.isCreativeMode) {
                aArrow.stackSize -= 1;
            }
            if (aArrow.stackSize == 0) {
                GT_Utility.removeNullStacksFromInventory(aEvent.entityPlayer.inventory);
            }
            if (!aEvent.entityPlayer.worldObj.isRemote) {
                aEvent.entityPlayer.worldObj.spawnEntityInWorld(tArrowEntity);
            }
            aEvent.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEndermanTeleportEvent(EnderTeleportEvent aEvent) {
        if (((aEvent.entityLiving instanceof EntityEnderman)) && (aEvent.entityLiving.getActivePotionEffect(Potion.weakness) != null)) {
            aEvent.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEntitySpawningEvent(EntityJoinWorldEvent aEvent) {
        if ((aEvent.entity != null) && (!aEvent.entity.worldObj.isRemote)) {
            if ((aEvent.entity instanceof EntityItem)) {
                ((EntityItem) aEvent.entity).setEntityItemStack(GT_OreDictUnificator.get(((EntityItem) aEvent.entity).getEntityItem()));
            }
            if ((this.mSkeletonsShootGTArrows > 0) && (aEvent.entity.getClass() == EntityArrow.class)
                    && (aEvent.entity.worldObj.rand.nextInt(this.mSkeletonsShootGTArrows) == 0)
                    && ((((EntityArrow) aEvent.entity).shootingEntity instanceof EntitySkeleton))) {
                aEvent.entity.worldObj.spawnEntityInWorld(new GT_Entity_Arrow((EntityArrow) aEvent.entity, (ItemStack) OrePrefixes.arrowGtWood.mPrefixedItems
                        .get(aEvent.entity.worldObj.rand.nextInt(OrePrefixes.arrowGtWood.mPrefixedItems.size()))));
                aEvent.entity.setDead();
            }
        }
    }

    @SubscribeEvent
    public void onOreGenEvent(OreGenEvent.GenerateMinable aGenerator) {
        if ((this.mDisableVanillaOres) && ((aGenerator.generator instanceof WorldGenMinable)) && (PREVENTED_ORES.contains(aGenerator.type))) {
            aGenerator.setResult(Result.DENY);
        }
    }

    private String getDataAndTime() {
        return this.mDateFormat.format(new Date());
    }

    @SubscribeEvent
    public void onPlayerInteraction(PlayerInteractEvent aEvent) {
        if ((aEvent.entityPlayer == null) || (aEvent.entityPlayer.worldObj == null) || (aEvent.action == null) || (aEvent.world.provider == null)) {
            return;
        }
        if ((!aEvent.entityPlayer.worldObj.isRemote) && (aEvent.action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
                && (GT_Log.pal != null)) {
            this.mBufferedPlayerActivity.add(getDataAndTime() + ";" + aEvent.action.name() + ";" + aEvent.entityPlayer.getDisplayName() + ";DIM:"
                    + aEvent.world.provider.dimensionId + ";" + aEvent.x + ";" + aEvent.y + ";" + aEvent.z + ";|;" + aEvent.x / 10 + ";" + aEvent.y / 10 + ";"
                    + aEvent.z / 10);
        }
        ItemStack aStack = aEvent.entityPlayer.getCurrentEquippedItem();
        if ((aStack != null) && (aEvent.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) && (aStack.getItem() == Items.flint_and_steel)) {
            if ((!aEvent.world.isRemote) && (!aEvent.entityPlayer.capabilities.isCreativeMode) && (aEvent.world.rand.nextInt(100) >= this.mFlintChance)) {
                aEvent.setCanceled(true);
                aStack.damageItem(1, aEvent.entityPlayer);
                if (aStack.getItemDamage() >= aStack.getMaxDamage()) {
                    aStack.stackSize -= 1;
                }
                if (aStack.stackSize <= 0) {
                    ForgeEventFactory.onPlayerDestroyItem(aEvent.entityPlayer, aStack);
                }
            }
            return;
        }
    }

    @SubscribeEvent
    public void onBlockHarvestingEvent(BlockEvent.HarvestDropsEvent aEvent) {
        if (aEvent.harvester != null) {
            if ((!aEvent.world.isRemote) && (GT_Log.pal != null)) {
                this.mBufferedPlayerActivity.add(getDataAndTime() + ";HARVEST_BLOCK;" + aEvent.harvester.getDisplayName() + ";DIM:"
                        + aEvent.world.provider.dimensionId + ";" + aEvent.x + ";" + aEvent.y + ";" + aEvent.z + ";|;" + aEvent.x / 10 + ";" + aEvent.y / 10
                        + ";" + aEvent.z / 10);
            }
            ItemStack aStack = aEvent.harvester.getCurrentEquippedItem();
            if (aStack != null) {
                if ((aStack.getItem() instanceof GT_MetaGenerated_Tool)) {
                    ((GT_MetaGenerated_Tool) aStack.getItem()).onHarvestBlockEvent(aEvent.drops, aStack, aEvent.harvester, aEvent.block, aEvent.x, aEvent.y,
                            aEvent.z, (byte) aEvent.blockMetadata, aEvent.fortuneLevel, aEvent.isSilkTouching, aEvent);
                }
                if (EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, aStack) > 2) {
                    try {
                        for (ItemStack tDrop : aEvent.drops) {
                            ItemStack tSmeltingOutput = GT_ModHandler.getSmeltingOutput(tDrop, false, null);
                            if (tSmeltingOutput != null) {
                                tDrop.stackSize *= tSmeltingOutput.stackSize;
                                tSmeltingOutput.stackSize = tDrop.stackSize;
                                GT_Utility.setStack(tDrop, tSmeltingOutput);
                            }
                        }
                    } catch (Throwable e) {e.printStackTrace(GT_Log.err);}
                }
            }
        }
    }

    @SubscribeEvent
    public void registerOre(OreDictionary.OreRegisterEvent aEvent) {
        ModContainer tContainer = Loader.instance().activeModContainer();
        String aMod = tContainer == null ? "UNKNOWN" : tContainer.getModId();
        String aOriginalMod = aMod;
        if (GT_OreDictUnificator.isRegisteringOres()) {
            aMod = "gregtech";
        } else if (aMod.equals("gregtech")) {
            aMod = "UNKNOWN";
        }
        if ((aEvent == null) || (aEvent.Ore == null) || (aEvent.Ore.getItem() == null) || (aEvent.Name == null) || (aEvent.Name.isEmpty())
                || (aEvent.Name.replaceAll("_", "").length() - aEvent.Name.length() == 9)) {
            if (aOriginalMod.equals("gregtech")) {
                aOriginalMod = "UNKNOWN";
            }
            GT_Log.ore
                    .println(aOriginalMod
                            + " did something very bad! The registration is too invalid to even be shown properly. This happens only if you register null, invalid Items, empty Strings or even nonexisting Events to the OreDict.");
            throw new IllegalArgumentException(
                    aOriginalMod
                            + " did something very bad! The registration is too invalid to even be shown properly. This happens only if you register null, invalid Items, empty Strings or even nonexisting Events to the OreDict.");
        }
        try {
            aEvent.Ore.stackSize = 1;
            if (this.mIgnoreTcon || aEvent.Ore.getUnlocalizedName().startsWith("item.oreberry")) {
                if ((aOriginalMod.toLowerCase(Locale.ENGLISH).contains("xycraft")) || (aOriginalMod.toLowerCase(Locale.ENGLISH).contains("tconstruct"))
                        || ((aOriginalMod.toLowerCase(Locale.ENGLISH).contains("natura")) && (!aOriginalMod.toLowerCase(Locale.ENGLISH).contains("natural")))) {
                    if (GT_Values.D1) {
                        GT_Log.ore.println(aMod + " -> " + aEvent.Name + " is getting ignored, because of racism. :P");
                    }
                    return;
                }
            }
            String tModToName = aMod + " -> " + aEvent.Name;
            if ((this.mOreDictActivated) || (GregTech_API.sPostloadStarted) || ((this.mSortToTheEnd) && (GregTech_API.sLoadFinished))) {
                tModToName = aOriginalMod + " --Late--> " + aEvent.Name;
            }
            if (((aEvent.Ore.getItem() instanceof ItemBlock)) || (GT_Utility.getBlockFromStack(aEvent.Ore) != Blocks.air)) {
                GT_OreDictUnificator.addToBlacklist(aEvent.Ore);
            }
            this.mRegisteredOres.add(aEvent.Ore);
            if (this.mIgnoredItems.contains(aEvent.Name)) {
                if ((aEvent.Name.startsWith("item"))) {
                    GT_Log.ore.println(tModToName);
                    if (aEvent.Name.equals("itemCopperWire")) {
                        GT_OreDictUnificator.registerOre(OreDictNames.craftingWireCopper, aEvent.Ore);
                    }
                    if (aEvent.Name.equals("itemRubber")) {
                        GT_OreDictUnificator.registerOre(OrePrefixes.ingot, Materials.Rubber, aEvent.Ore);
                    }
                    return;
                }
            }else if(this.mIgnoredNames.contains(aEvent.Name)){
                GT_Log.ore.println(tModToName + " is getting ignored via hardcode.");
                return;
            }
            else if (aEvent.Name.equals("stone")) {
                GT_OreDictUnificator.registerOre("stoneSmooth", aEvent.Ore);
                return;
            }
            else if (aEvent.Name.equals("cobblestone")) {
                GT_OreDictUnificator.registerOre("stoneCobble", aEvent.Ore);
                return;
            }
            else if ((aEvent.Name.contains("|")) || (aEvent.Name.contains("*")) || (aEvent.Name.contains(":")) || (aEvent.Name.contains("."))
                    || (aEvent.Name.contains("$"))) {
                GT_Log.ore.println(tModToName + " is using a private Prefix and is therefor getting ignored properly.");
                return;
            }
            else if (aEvent.Name.equals("copperWire")) {
                GT_OreDictUnificator.registerOre(OreDictNames.craftingWireCopper, aEvent.Ore);
            }
            else if (aEvent.Name.equals("oreHeeEndrium")) {
                GT_OreDictUnificator.registerOre(OrePrefixes.ore, Materials.Endium, aEvent.Ore);
            }
            else if (aEvent.Name.equals("sheetPlastic")) {
                GT_OreDictUnificator.registerOre(OrePrefixes.plate, Materials.Plastic, aEvent.Ore);
            }
            else if (aEvent.Name.startsWith("shard")) {
                if (aEvent.Name.equals("shardAir")) {
                    GT_OreDictUnificator.registerOre(OrePrefixes.gem, Materials.InfusedAir, aEvent.Ore);
                    return;
                }
                else if (aEvent.Name.equals("shardWater")) {
                    GT_OreDictUnificator.registerOre(OrePrefixes.gem, Materials.InfusedWater, aEvent.Ore);
                    return;
                }
                else if (aEvent.Name.equals("shardFire")) {
                    GT_OreDictUnificator.registerOre(OrePrefixes.gem, Materials.InfusedFire, aEvent.Ore);
                    return;
                }
                else if (aEvent.Name.equals("shardEarth")) {
                    GT_OreDictUnificator.registerOre(OrePrefixes.gem, Materials.InfusedEarth, aEvent.Ore);
                    return;
                }
                else if (aEvent.Name.equals("shardOrder")) {
                    GT_OreDictUnificator.registerOre(OrePrefixes.gem, Materials.InfusedOrder, aEvent.Ore);
                    return;
                }
                else if (aEvent.Name.equals("shardEntropy")) {
                    GT_OreDictUnificator.registerOre(OrePrefixes.gem, Materials.InfusedEntropy, aEvent.Ore);
                    return;
                }
            } else if (aEvent.Name.equals("fieryIngot")) {
                GT_OreDictUnificator.registerOre(OrePrefixes.ingot, Materials.FierySteel, aEvent.Ore);
                return;
            }
            else if (aEvent.Name.equals("ironwood")) {
                GT_OreDictUnificator.registerOre(OrePrefixes.ingot, Materials.IronWood, aEvent.Ore);
                return;
            }
            else if (aEvent.Name.equals("steeleaf")) {
                GT_OreDictUnificator.registerOre(OrePrefixes.ingot, Materials.Steeleaf, aEvent.Ore);
                return;
            }
            else if (aEvent.Name.equals("knightmetal")) {
                GT_OreDictUnificator.registerOre(OrePrefixes.ingot, Materials.Knightmetal, aEvent.Ore);
                return;
            }
            else if (aEvent.Name.equals("compressedAluminum")) {
                GT_OreDictUnificator.registerOre(OrePrefixes.compressed, Materials.Aluminium, aEvent.Ore);
                return;
            }
            else if (aEvent.Name.contains(" ")) {
                GT_Log.ore.println(tModToName + " is getting re-registered because the OreDict Name containing invalid spaces.");
                GT_OreDictUnificator.registerOre(aEvent.Name.replaceAll(" ", ""), GT_Utility.copyAmount(1L, new Object[]{aEvent.Ore}));
                aEvent.Ore.setStackDisplayName("Invalid OreDictionary Tag");
                return;
            }
            else if (this.mInvalidNames.contains(aEvent.Name)) {
                GT_Log.ore.println(tModToName + " is wrongly registered and therefor getting ignored.");

                return;
            }
            OrePrefixes aPrefix = OrePrefixes.getOrePrefix(aEvent.Name);
            Materials aMaterial = Materials._NULL;
            if ((aPrefix == OrePrefixes.nugget) && (aMod.equals("Thaumcraft")) && (aEvent.Ore.getItem().getUnlocalizedName().contains("ItemResource"))) {
                return;
            }
            if (aPrefix == null) {
                if (aEvent.Name.toLowerCase().equals(aEvent.Name)) {
                    GT_Log.ore.println(tModToName + " is invalid due to being solely lowercased.");
                    return;
                }else if (aEvent.Name.toUpperCase().equals(aEvent.Name)) {
                    GT_Log.ore.println(tModToName + " is invalid due to being solely uppercased.");
                    return;
                } else if (Character.isUpperCase(aEvent.Name.charAt(0))) {
                    GT_Log.ore.println(tModToName + " is invalid due to the first character being uppercased.");
                }
            } else {
                if (aPrefix.mDontUnificateActively) {
                    GT_OreDictUnificator.addToBlacklist(aEvent.Ore);
                }
                if (aPrefix != aPrefix.mPrefixInto) {
                    String tNewName = aEvent.Name.replaceFirst(aPrefix.toString(), aPrefix.mPrefixInto.toString());
                    if (!GT_OreDictUnificator.isRegisteringOres()) {
                        GT_Log.ore.println(tModToName + " uses a depricated Prefix, and is getting re-registered as " + tNewName);
                    }
                    GT_OreDictUnificator.registerOre(tNewName, aEvent.Ore);
                    return;
                }
                String tName = aEvent.Name.replaceFirst(aPrefix.toString(), "");
                if (tName.length() > 0) {
                    char firstChar = tName.charAt(0);
                    if (Character.isUpperCase(firstChar) || Character.isLowerCase(firstChar) || firstChar == '_') {
                        if (aPrefix.mIsMaterialBased) {
                            aMaterial = Materials.get(tName);
                            if (aMaterial != aMaterial.mMaterialInto) {
                                GT_OreDictUnificator.registerOre(aPrefix, aMaterial.mMaterialInto, aEvent.Ore);
                                if (!GT_OreDictUnificator.isRegisteringOres()) {
                                    GT_Log.ore.println(tModToName + " uses a deprecated Material and is getting re-registered as "
                                            + aPrefix.get(aMaterial.mMaterialInto));
                                }
                                return;
                            }
                            if (!aPrefix.isIgnored(aMaterial)) {
                                aPrefix.add(GT_Utility.copyAmount(1L, new Object[]{aEvent.Ore}));
                            }
                            if (aMaterial != Materials._NULL) {
                                Materials tReRegisteredMaterial;
                                for (Iterator i$ = aMaterial.mOreReRegistrations.iterator(); i$.hasNext(); GT_OreDictUnificator.registerOre(aPrefix,
                                        tReRegisteredMaterial, aEvent.Ore)) {
                                    tReRegisteredMaterial = (Materials) i$.next();
                                }
                                aMaterial.add(GT_Utility.copyAmount(1L, new Object[]{aEvent.Ore}));

                                if (GregTech_API.sThaumcraftCompat != null && aPrefix.doGenerateItem(aMaterial) && !aPrefix.isIgnored(aMaterial)) {
                                    List<TC_AspectStack> tAspects = new ArrayList<TC_AspectStack>();
                                    for (TC_AspectStack tAspect : aPrefix.mAspects) tAspect.addToAspectList(tAspects);
                                    if (aPrefix.mMaterialAmount >= 3628800 || aPrefix.mMaterialAmount < 0) for (TC_AspectStack tAspect : aMaterial.mAspects) tAspect.addToAspectList(tAspects);
                                    GregTech_API.sThaumcraftCompat.registerThaumcraftAspectsToItem(GT_Utility.copyAmount(1, aEvent.Ore), tAspects, aEvent.Name);
                                }

                                switch (aPrefix) {
                                    case crystal:
                                        if ((aMaterial == Materials.CertusQuartz) || (aMaterial == Materials.NetherQuartz) || (aMaterial == Materials.Fluix)) {
                                            GT_OreDictUnificator.registerOre(OrePrefixes.gem, aMaterial, aEvent.Ore);
                                        }
                                        break;
                                    case gem:
                                        if (aMaterial == Materials.Lapis || aMaterial == Materials.Sodalite) {
                                            GT_OreDictUnificator.registerOre(Dyes.dyeBlue, aEvent.Ore);
                                        } else if (aMaterial == Materials.Lazurite) {
                                            GT_OreDictUnificator.registerOre(Dyes.dyeCyan, aEvent.Ore);
                                        } else if (aMaterial == Materials.InfusedAir || aMaterial == Materials.InfusedWater || aMaterial == Materials.InfusedFire || aMaterial == Materials.InfusedEarth || aMaterial == Materials.InfusedOrder || aMaterial == Materials.InfusedEntropy) {
                                            GT_OreDictUnificator.registerOre(aMaterial.mName.replaceFirst("Infused", "shard"), aEvent.Ore);
                                        } else if (aMaterial == Materials.Chocolate) {
                                            GT_OreDictUnificator.registerOre(Dyes.dyeBrown, aEvent.Ore);
                                        } else if (aMaterial == Materials.CertusQuartz || aMaterial == Materials.NetherQuartz) {
                                            GT_OreDictUnificator.registerOre(OrePrefixes.item.get(aMaterial), aEvent.Ore);
                                            GT_OreDictUnificator.registerOre(OrePrefixes.crystal, aMaterial, aEvent.Ore);
                                            GT_OreDictUnificator.registerOre(OreDictNames.craftingQuartz, aEvent.Ore);
                                        } else if (aMaterial == Materials.Fluix || aMaterial == Materials.Quartz || aMaterial == Materials.Quartzite) {
                                            GT_OreDictUnificator.registerOre(OrePrefixes.crystal, aMaterial, aEvent.Ore);
                                            GT_OreDictUnificator.registerOre(OreDictNames.craftingQuartz, aEvent.Ore);
                                        }
                                        break;
                                    case cableGt01:
                                        if (aMaterial == Materials.Tin) {
                                            GT_OreDictUnificator.registerOre(OreDictNames.craftingWireTin, aEvent.Ore);
                                        } else if (aMaterial == Materials.Copper) {
                                            GT_OreDictUnificator.registerOre(OreDictNames.craftingWireCopper, aEvent.Ore);
                                        } else if (aMaterial == Materials.Gold) {
                                            GT_OreDictUnificator.registerOre(OreDictNames.craftingWireGold, aEvent.Ore);
                                        } else if (aMaterial == Materials.Iron) {
                                            GT_OreDictUnificator.registerOre(OreDictNames.craftingWireIron, aEvent.Ore);
                                        }
                                        break;
                                    case lens:
                                        if ((aMaterial.contains(SubTag.TRANSPARENT)) && (aMaterial.mColor != Dyes._NULL)) {
                                            GT_OreDictUnificator.registerOre("craftingLens" + aMaterial.mColor.toString().replaceFirst("dye", ""), aEvent.Ore);
                                        }
                                        break;
                                    case plate:
                                        if ((aMaterial == Materials.Plastic) || (aMaterial == Materials.Rubber)) {
                                            GT_OreDictUnificator.registerOre(OrePrefixes.sheet, aMaterial, aEvent.Ore);
                                        } else if (aMaterial == Materials.Silicon) {
                                            GT_OreDictUnificator.registerOre(OrePrefixes.item, aMaterial, aEvent.Ore);
                                        } else if (aMaterial == Materials.Wood) {
                                            GT_OreDictUnificator.addToBlacklist(aEvent.Ore);
                                            GT_OreDictUnificator.registerOre(OrePrefixes.plank, aMaterial, aEvent.Ore);
                                        }
                                        break;
                                    case cell:
                                        if (aMaterial == Materials.Empty) {
                                            GT_OreDictUnificator.addToBlacklist(aEvent.Ore);
                                        }
                                        break;
                                    case gearGt:
                                        GT_OreDictUnificator.registerOre(OrePrefixes.gear, aMaterial, aEvent.Ore);
                                        break;
                                    case stick:
                                        if (!GT_RecipeRegistrator.sRodMaterialList.contains(aMaterial)) {
                                            GT_RecipeRegistrator.sRodMaterialList.add(aMaterial);
                                        } else if (aMaterial == Materials.Wood) {
                                            GT_OreDictUnificator.addToBlacklist(aEvent.Ore);
                                        } else if ((aMaterial == Materials.Tin) || (aMaterial == Materials.Lead) || (aMaterial == Materials.SolderingAlloy)) {
                                            GT_OreDictUnificator.registerOre(ToolDictNames.craftingToolSolderingMetal, aEvent.Ore);
                                        }
                                        break;
                                    case dust:
                                        if (aMaterial == Materials.Salt) {
                                            GT_OreDictUnificator.registerOre("itemSalt", aEvent.Ore);
                                        } else if (aMaterial == Materials.Wood) {
                                            GT_OreDictUnificator.registerOre("pulpWood", aEvent.Ore);
                                        } else if (aMaterial == Materials.Wheat) {
                                            GT_OreDictUnificator.registerOre("foodFlour", aEvent.Ore);
                                        } else if (aMaterial == Materials.Lapis) {
                                            GT_OreDictUnificator.registerOre(Dyes.dyeBlue, aEvent.Ore);
                                        } else if (aMaterial == Materials.Lazurite) {
                                            GT_OreDictUnificator.registerOre(Dyes.dyeCyan, aEvent.Ore);
                                        } else if (aMaterial == Materials.Sodalite) {
                                            GT_OreDictUnificator.registerOre(Dyes.dyeBlue, aEvent.Ore);
                                        } else if (aMaterial == Materials.Cocoa) {
                                            GT_OreDictUnificator.registerOre(Dyes.dyeBrown, aEvent.Ore);
                                            GT_OreDictUnificator.registerOre("foodCocoapowder", aEvent.Ore);
                                        } else if (aMaterial == Materials.Coffee) {
                                            GT_OreDictUnificator.registerOre(Dyes.dyeBrown, aEvent.Ore);
                                        } else if (aMaterial == Materials.BrownLimonite) {
                                            GT_OreDictUnificator.registerOre(Dyes.dyeBrown, aEvent.Ore);
                                        } else if (aMaterial == Materials.YellowLimonite) {
                                            GT_OreDictUnificator.registerOre(Dyes.dyeYellow, aEvent.Ore);
                                        }
                                        break;
                                    case ingot:
                                        if (aMaterial == Materials.Rubber) {
                                            GT_OreDictUnificator.registerOre("itemRubber", aEvent.Ore);
                                        } else if (aMaterial == Materials.FierySteel) {
                                            GT_OreDictUnificator.registerOre("fieryIngot", aEvent.Ore);
                                        } else if (aMaterial == Materials.IronWood) {
                                            GT_OreDictUnificator.registerOre("ironwood", aEvent.Ore);
                                        } else if (aMaterial == Materials.Steeleaf) {
                                            GT_OreDictUnificator.registerOre("steeleaf", aEvent.Ore);
                                        } else if (aMaterial == Materials.Knightmetal) {
                                            GT_OreDictUnificator.registerOre("knightmetal", aEvent.Ore);
                                        } else if ((aMaterial == Materials.Brass) && (aEvent.Ore.getItemDamage() == 2)
                                                && (aEvent.Ore.getUnlocalizedName().equals("item.ingotBrass"))
                                                && (new ItemStack(aEvent.Ore.getItem(), 1, 0).getUnlocalizedName().contains("red"))) {
                                            GT_OreDictUnificator.set(OrePrefixes.ingot, Materials.RedAlloy, new ItemStack(aEvent.Ore.getItem(), 1, 0));
                                            GT_OreDictUnificator.set(OrePrefixes.ingot, Materials.BlueAlloy, new ItemStack(aEvent.Ore.getItem(), 1, 1));
                                            GT_OreDictUnificator.set(OrePrefixes.ingot, Materials.Brass, new ItemStack(aEvent.Ore.getItem(), 1, 2));
                                            if (!mDisableIC2Cables) {
                                                GT_Values.RA.addWiremillRecipe(GT_ModHandler.getIC2Item("copperCableItem", 3L), new ItemStack(aEvent.Ore.getItem(), 1,
                                                        8), 400, 1);
                                                GT_Values.RA.addWiremillRecipe(GT_ModHandler.getIC2Item("ironCableItem", 6L),
                                                        new ItemStack(aEvent.Ore.getItem(), 1, 9), 400, 2);
                                            }
                                            GT_Values.RA.addCutterRecipe(new ItemStack(aEvent.Ore.getItem(), 1, 3), new ItemStack(aEvent.Ore.getItem(), 16, 4),
                                                    null, 400, 8);
                                        }
                                        break;
                                    default:
                                        break;
                                }
                                if (aPrefix.mIsUnificatable && !aMaterial.mUnificatable) {
                                    return;
                                }
                            } else {
                                for (Dyes tDye : Dyes.VALUES) {
                                    if (aEvent.Name.endsWith(tDye.name().replaceFirst("dye", ""))) {
                                        GT_OreDictUnificator.addToBlacklist(aEvent.Ore);
                                        GT_Log.ore.println(tModToName + " Oh man, why the fuck would anyone need a OreDictified Color for this, that is even too much for GregTech... do not report this, this is just a random Comment about how ridiculous this is.");
                                        return;
                                    }
                                }
//								System.out.println("Material Name: "+aEvent.Name+ " !!!Unknown Material detected!!! Please report to GregTech Intergalactical for additional compatiblity. This is not an Error, an Issue nor a Lag Source, it is just an Information, which you should pass to me.");
//								GT_Log.ore.println(tModToName + " uses an unknown Material. Report this to GregTech.");
                                return;
                            }
                        } else {
                            aPrefix.add(GT_Utility.copyAmount(1L, new Object[]{aEvent.Ore}));
                        }
                    }
                } else if (aPrefix.mIsSelfReferencing) {
                    aPrefix.add(GT_Utility.copyAmount(1L, new Object[]{aEvent.Ore}));
                } else {
                    GT_Log.ore.println(tModToName + " uses a Prefix as full OreDict Name, and is therefor invalid.");
                    aEvent.Ore.setStackDisplayName("Invalid OreDictionary Tag");
                    return;
                }
                switch (aPrefix) {
                    case dye:
                        if (GT_Utility.isStringValid(tName)) {
                            GT_OreDictUnificator.registerOre(OrePrefixes.dye, aEvent.Ore);
                        }
                        break;
                    case stoneSmooth:
                        GT_OreDictUnificator.registerOre("stone", aEvent.Ore);
                        break;
                    case stoneCobble:
                        GT_OreDictUnificator.registerOre("cobblestone", aEvent.Ore);
                        break;
                    case plank:
                        if (tName.equals("Wood")) {
                            GT_OreDictUnificator.addItemData(aEvent.Ore, new ItemData(Materials.Wood, 3628800L, new MaterialStack[0]));
                        }
                        break;
                    case slab:
                        if (tName.equals("Wood")) {
                            GT_OreDictUnificator.addItemData(aEvent.Ore, new ItemData(Materials.Wood, 1814400L, new MaterialStack[0]));
                        }
                        break;
                    case sheet:
                        if (tName.equals("Plastic")) {
                            GT_OreDictUnificator.registerOre(OrePrefixes.plate, Materials.Plastic, aEvent.Ore);
                        } else if (tName.equals("Rubber")) {
                            GT_OreDictUnificator.registerOre(OrePrefixes.plate, Materials.Rubber, aEvent.Ore);
                        }
                        break;
                    case crafting:
                        if (tName.equals("ToolSolderingMetal")) {
                            GregTech_API.registerSolderingMetal(aEvent.Ore);
                        } else if (tName.equals("IndustrialDiamond")) {
                            GT_OreDictUnificator.addToBlacklist(aEvent.Ore);
                        } else if (tName.equals("WireCopper")) {
                            GT_OreDictUnificator.registerOre(OrePrefixes.wire, Materials.Copper, aEvent.Ore);
                        }
                        break;
                    case wood:
                        if (tName.equals("Rubber")) {
                            GT_OreDictUnificator.registerOre("logRubber", aEvent.Ore);
                        }
                        break;
                    case food:
                        if (tName.equals("Cocoapowder")) {
                            GT_OreDictUnificator.registerOre(OrePrefixes.dust, Materials.Cocoa, aEvent.Ore);
                        }
                        break;
                    default:
                        break;
                }
            }
            GT_Log.ore.println(tModToName);

            OreDictEventContainer tOre = new OreDictEventContainer(aEvent, aPrefix, aMaterial, aMod);
            if ((!this.mOreDictActivated) || (!GregTech_API.sUnificationEntriesRegistered)) {
                this.mEvents.add(tOre);
            } else {
                this.mEvents.clear();
            }
            if (this.mOreDictActivated) {
                registerRecipes(tOre);
            }
        } catch (Throwable e) {
            e.printStackTrace(GT_Log.err);
        }
    }
    
    @SubscribeEvent
    public void onLivingUpdate(LivingUpdateEvent aEvent) {
        if (aEvent.entityLiving.onGround) {
            int tX = MathHelper.floor_double(aEvent.entityLiving.posX), tY = MathHelper.floor_double(aEvent.entityLiving.boundingBox.minY-0.001F), tZ = MathHelper.floor_double(aEvent.entityLiving.posZ);
            Block tBlock = aEvent.entityLiving.worldObj.getBlock(tX, tY, tZ);
            if (tBlock instanceof IBlockOnWalkOver) ((IBlockOnWalkOver)tBlock).onWalkOver(aEvent.entityLiving, aEvent.entityLiving.worldObj, tX, tY, tZ);
        }
    }

    @SubscribeEvent
    public void onFluidContainerRegistration(FluidContainerRegistry.FluidContainerRegisterEvent aFluidEvent) {
        if ((aFluidEvent.data.filledContainer.getItem() == Items.potionitem) && (aFluidEvent.data.filledContainer.getItemDamage() == 0)) {
            aFluidEvent.data.fluid.amount = 0;
        }
        GT_OreDictUnificator.addToBlacklist(aFluidEvent.data.emptyContainer);
        GT_OreDictUnificator.addToBlacklist(aFluidEvent.data.filledContainer);
        GT_Utility.addFluidContainerData(aFluidEvent.data);
    }

    @SubscribeEvent
    public void onServerTickEvent(TickEvent.ServerTickEvent aEvent) {
    }

    @SubscribeEvent
    public void onWorldTickEvent(TickEvent.WorldTickEvent aEvent) {
    	if(aEvent.world.provider.dimensionId == 0)
            mTicksUntilNextCraftSound--;   
        if (aEvent.side.isServer()) {
            if (this.mUniverse == null) {
                this.mUniverse = aEvent.world;
            }         
            if (this.isFirstServerWorldTick) {
                File tSaveDiretory = getSaveDirectory();
                if (tSaveDiretory != null) {
                    this.isFirstServerWorldTick = false;
                    try {
                        for (IMetaTileEntity tMetaTileEntity : GregTech_API.METATILEENTITIES) {
                            if (tMetaTileEntity != null) {
                                tMetaTileEntity.onWorldLoad(tSaveDiretory);
                            }
                        }
                    } catch (Throwable e) {e.printStackTrace(GT_Log.err);}
                }
            }
            if ((aEvent.world.getTotalWorldTime() % 100L == 0L) && ((this.mItemDespawnTime != 6000) || (this.mMaxEqualEntitiesAtOneSpot > 0))) {
                for (int i = 0; i < aEvent.world.loadedEntityList.size(); i++) {
                    if ((aEvent.world.loadedEntityList.get(i) instanceof Entity)) {
                        Entity tEntity = (Entity) aEvent.world.loadedEntityList.get(i);
                        if (((tEntity instanceof EntityItem)) && (this.mItemDespawnTime != 6000) && (((EntityItem) tEntity).lifespan == 6000)) {
                            ((EntityItem) tEntity).lifespan = this.mItemDespawnTime;
                        } else if (((tEntity instanceof EntityLivingBase)) && (this.mMaxEqualEntitiesAtOneSpot > 0) && (!(tEntity instanceof EntityPlayer))
                                && (((EntityLivingBase) tEntity).canBePushed()) && (((EntityLivingBase) tEntity).getHealth() > 0.0F)) {
                            List tList = tEntity.worldObj.getEntitiesWithinAABBExcludingEntity(tEntity,
                                    tEntity.boundingBox.expand(0.20000000298023224D, 0.0D, 0.20000000298023224D));
                            Class tClass = tEntity.getClass();
                            int tEntityCount = 1;
                            if (tList != null) {
                                for (int j = 0; j < tList.size(); j++) {
                                    if ((tList.get(j) != null) && (tList.get(j).getClass() == tClass)) {
                                        tEntityCount++;
                                    }
                                }
                            }
                            if (tEntityCount > this.mMaxEqualEntitiesAtOneSpot) {
                                tEntity.attackEntityFrom(DamageSource.inWall, tEntityCount - this.mMaxEqualEntitiesAtOneSpot);
                            }
                        }
                    }
                }
            }

            GT_Pollution.onWorldTick(aEvent);
        }
    }

    @SubscribeEvent
    public void onPlayerTickEventServer(TickEvent.PlayerTickEvent aEvent) {
        if ((aEvent.side.isServer()) && (aEvent.phase == TickEvent.Phase.END) && (!aEvent.player.isDead)) {
            if ((aEvent.player.ticksExisted % 200 == 0) && (aEvent.player.capabilities.allowEdit) && (!aEvent.player.capabilities.isCreativeMode)
                    && (this.mSurvivalIntoAdventure)) {
                aEvent.player.setGameType(GameType.ADVENTURE);
                aEvent.player.capabilities.allowEdit = false;
                if (this.mAxeWhenAdventure) {
                    GT_Utility.sendChatToPlayer(aEvent.player, GT_LanguageManager.addStringLocalization("Interaction_DESCRIPTION_Index_097", "It's dangerous to go alone! Take this.", false));
                    aEvent.player.worldObj.spawnEntityInWorld(new EntityItem(aEvent.player.worldObj, aEvent.player.posX, aEvent.player.posY,
                            aEvent.player.posZ, GT_MetaGenerated_Tool_01.INSTANCE.getToolWithStats(GT_MetaGenerated_Tool_01.AXE, 1, Materials.Flint, Materials.Wood, null)));
                }
            }
            boolean tHungerEffect = (this.mHungerEffect) && (aEvent.player.ticksExisted % 2400 == 1200);
            if (aEvent.player.ticksExisted % 120 == 0) {
                int tCount = 64;
                for (int i = 0; i < 36; i++) {
                    ItemStack tStack;
                    if ((tStack = aEvent.player.inventory.getStackInSlot(i)) != null) {
                        if (!aEvent.player.capabilities.isCreativeMode) {
                            GT_Utility.applyRadioactivity(aEvent.player, GT_Utility.getRadioactivityLevel(tStack), tStack.stackSize);
                            float tHeat = GT_Utility.getHeatDamageFromItem(tStack);
                            if (tHeat != 0.0F) {
                                if (tHeat > 0.0F) {
                                    GT_Utility.applyHeatDamage(aEvent.player, tHeat);
                                } else {
                                    GT_Utility.applyFrostDamage(aEvent.player, -tHeat);
                                }
                            }
                        }
                        if (tHungerEffect) {
                            tCount += tStack.stackSize * 64 / Math.max(1, tStack.getMaxStackSize());
                        }
                        if (this.mInventoryUnification) {
                            GT_OreDictUnificator.setStack(true, tStack);
                        }
                    }
                }
                for (int i = 0; i < 4; i++) {
                    ItemStack tStack;
                    if ((tStack = aEvent.player.inventory.armorInventory[i]) != null) {
                        if (!aEvent.player.capabilities.isCreativeMode) {
                            GT_Utility.applyRadioactivity(aEvent.player, GT_Utility.getRadioactivityLevel(tStack), tStack.stackSize);
                            float tHeat = GT_Utility.getHeatDamageFromItem(tStack);
                            if (tHeat != 0.0F) {
                                if (tHeat > 0.0F) {
                                    GT_Utility.applyHeatDamage(aEvent.player, tHeat);
                                } else {
                                    GT_Utility.applyFrostDamage(aEvent.player, -tHeat);
                                }
                            }
                        }
                        if (tHungerEffect) {
                            tCount += 256;
                        }
                    }
                }
                if (tHungerEffect) {
                    aEvent.player.addExhaustion(Math.max(1.0F, tCount / 666.6F));
                }
            }
            if (aEvent.player.ticksExisted % 10 == 0) {
        	int tPollution = 0;
        	tPollution = GT_Pollution.getPollution(new ChunkCoordIntPair(aEvent.player.chunkCoordX,aEvent.player.chunkCoordZ), aEvent.player.dimension);
        	if(aEvent.player instanceof EntityPlayerMP)GT_Values.NW.sendToPlayer(new GT_Packet_Pollution(tPollution), (EntityPlayerMP) aEvent.player);
            }
        }
    }

    public Object getServerGuiElement(int aID, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ) {
        if(aID>=1000){
            int ID = aID-1000;
            switch(ID){
                case 0:
                    return new ContainerBasicArmor(aPlayer, new InventoryArmor(ModularArmor_Item.class, aPlayer.getCurrentEquippedItem()));
                case 1:
                    return new ContainerElectricArmor1(aPlayer, new InventoryArmor(ModularArmor_Item.class, aPlayer.getCurrentEquippedItem()));
                case 2:
                    return new ContainerElectricArmor1(aPlayer, new InventoryArmor(ModularArmor_Item.class, aPlayer.getCurrentEquippedItem()));
                case 10:
                    return new GT_ContainerVolumetricFlask(aPlayer.inventory);
                default:
                    return getRightItem(aPlayer, ID);
            }
        }
        if(aID>=100){
        	int tSlot = aID / 100;
            int ID = aID%100;
            switch(ID){
            case 0:
                return new ContainerBasicArmor(aPlayer, new InventoryArmor(ModularArmor_Item.class, aPlayer.getEquipmentInSlot(tSlot)));
            case 1:
                return new ContainerElectricArmor1(aPlayer, new InventoryArmor(ModularArmor_Item.class, aPlayer.getEquipmentInSlot(tSlot)));
            case 2:
                return new ContainerElectricArmor1(aPlayer, new InventoryArmor(ModularArmor_Item.class, aPlayer.getEquipmentInSlot(tSlot)));
            default:
                return getRightItem(aPlayer, ID);
        }
        }
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity instanceof IGregTechTileEntity)) {
            IMetaTileEntity tMetaTileEntity = ((IGregTechTileEntity) tTileEntity).getMetaTileEntity();
            if (tMetaTileEntity != null) {
                return tMetaTileEntity.getServerGUI(aID, aPlayer.inventory, (IGregTechTileEntity) tTileEntity);
            }
        }
        return null;
    }

    public Object getRightItem(EntityPlayer player, int ID){
        ItemStack mStack = player.getEquipmentInSlot(ID/100);
        if(mStack==null||!(mStack.getItem() instanceof ModularArmor_Item))return null;

        switch(ID % 100){
            case 0:
                return new ContainerBasicArmor(player, new InventoryArmor(ModularArmor_Item.class, mStack));
            case 1:
                return new ContainerElectricArmor1(player, new InventoryArmor(ModularArmor_Item.class, mStack));
            case 2:
                return new ContainerElectricArmor1(player, new InventoryArmor(ModularArmor_Item.class, mStack));
        }
        return null;

    }

    public Object getClientGuiElement(int aID, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ) {
        if(aID>=1000){
            int ID = aID-1000;
            switch(ID){
                case 0:
                    return new GuiModularArmor(new ContainerBasicArmor(aPlayer, new InventoryArmor(ModularArmor_Item.class, aPlayer.getCurrentEquippedItem())), aPlayer);
                case 1:
                    return new GuiElectricArmor1(new ContainerElectricArmor1(aPlayer, new InventoryArmor(ModularArmor_Item.class, aPlayer.getCurrentEquippedItem())), aPlayer);
                case 2:
                    return new GuiElectricArmor1(new ContainerElectricArmor1(aPlayer, new InventoryArmor(ModularArmor_Item.class, aPlayer.getCurrentEquippedItem())), aPlayer);
                case 10:
                    return new GT_GUIContainerVolumetricFlask(new GT_ContainerVolumetricFlask(aPlayer.inventory));
                default:
                    return getRightItemGui(aPlayer, ID);
            }
        }
        if(aID>=100){
        	int tSlot = aID / 100;
            int ID = aID%100;
            switch(ID){
            case 0:
                return new GuiModularArmor(new ContainerBasicArmor(aPlayer, new InventoryArmor(ModularArmor_Item.class, aPlayer.getEquipmentInSlot(tSlot))), aPlayer);
            case 1:
                return new GuiElectricArmor1(new ContainerElectricArmor1(aPlayer, new InventoryArmor(ModularArmor_Item.class, aPlayer.getEquipmentInSlot(tSlot))), aPlayer);
            case 2:
                return new GuiElectricArmor1(new ContainerElectricArmor1(aPlayer, new InventoryArmor(ModularArmor_Item.class, aPlayer.getEquipmentInSlot(tSlot))), aPlayer);
            default:
                return getRightItem(aPlayer, ID);
        }
        }
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity instanceof IGregTechTileEntity)) {
            IMetaTileEntity tMetaTileEntity = ((IGregTechTileEntity) tTileEntity).getMetaTileEntity();
            if (tMetaTileEntity != null) {
                return tMetaTileEntity.getClientGUI(aID, aPlayer.inventory, (IGregTechTileEntity) tTileEntity);
            }
        }
        return null;
    }

    public Object getRightItemGui(EntityPlayer player, int ID){
        ItemStack mStack = player.getEquipmentInSlot(ID/100);
        if(mStack==null||!(mStack.getItem() instanceof ModularArmor_Item))return null;

        switch(ID % 100){
            case 0:
                return new GuiModularArmor(new ContainerBasicArmor(player, new InventoryArmor(ModularArmor_Item.class, mStack)),player);
            case 1:
                return new GuiElectricArmor1(new ContainerElectricArmor1(player, new InventoryArmor(ModularArmor_Item.class, mStack)), player);
            case 2:
                return new GuiElectricArmor1(new ContainerElectricArmor1(player, new InventoryArmor(ModularArmor_Item.class, mStack)), player);
        }
        return null;

    }

    public int getBurnTime(ItemStack aFuel) {
        if ((aFuel == null) || (aFuel.getItem() == null)) {
            return 0;
        }
        int rFuelValue = 0;
        if ((aFuel.getItem() instanceof GT_MetaGenerated_Item)) {
            Short tFuelValue = (Short) ((GT_MetaGenerated_Item) aFuel.getItem()).mBurnValues.get(Short.valueOf((short) aFuel.getItemDamage()));
            if (tFuelValue != null) {
                rFuelValue = Math.max(rFuelValue, tFuelValue.shortValue());
            }
        }
        NBTTagCompound tNBT = aFuel.getTagCompound();
        if (tNBT != null) {
            short tValue = tNBT.getShort("GT.ItemFuelValue");
            rFuelValue = Math.max(rFuelValue, tValue);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "gemSodium")) {
            rFuelValue =   Math.max(rFuelValue, 4000);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "crushedSodium")) {
            rFuelValue =   Math.max(rFuelValue, 4000);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustImpureSodium")) {
            rFuelValue =   Math.max(rFuelValue, 4000);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustSodium")) {
            rFuelValue =   Math.max(rFuelValue, 400);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustSmallSodium")) {
            rFuelValue =   Math.max(rFuelValue, 100);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustTinySodium")) {
            rFuelValue =   Math.max(rFuelValue, 44);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustSulfur")) {
            rFuelValue =   Math.max(rFuelValue, 1600);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "gemLithium")) {
            rFuelValue =   Math.max(rFuelValue, 6000);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "crushedLithium")) {
            rFuelValue =   Math.max(rFuelValue, 6000);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustImpureLithium")) {
            rFuelValue =   Math.max(rFuelValue, 6000);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustLithium")) {
            rFuelValue =   Math.max(rFuelValue, 6000);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustSmallLithium")) {
            rFuelValue =   Math.max(rFuelValue, 2000);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustTinyLithium")) {
            rFuelValue =   Math.max(rFuelValue, 888);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "gemCaesium")) {
            rFuelValue =   Math.max(rFuelValue, 6000);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "crushedCaesium")) {
            rFuelValue =   Math.max(rFuelValue, 6000);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustImpureCaesium")) {
            rFuelValue =   Math.max(rFuelValue, 6000);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustCaesium")) {
            rFuelValue =   Math.max(rFuelValue, 6000);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustSmallCaesium")) {
            rFuelValue =   Math.max(rFuelValue, 2000);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustTinyCaesium")) {
            rFuelValue =   Math.max(rFuelValue, 888);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "gemLignite")) {
            rFuelValue =   Math.max(rFuelValue, 1200);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "crushedLignite")) {
            rFuelValue =   Math.max(rFuelValue, 1200);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustImpureLignite")) {
            rFuelValue =   Math.max(rFuelValue, 1200);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustLignite")) {
            rFuelValue =   Math.max(rFuelValue, 1200);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustSmallLignite")) {
            rFuelValue =   Math.max(rFuelValue, 375);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustTinyLignite")) {
            rFuelValue =   Math.max(rFuelValue, 166);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "gemCoal")) {
            rFuelValue =   Math.max(rFuelValue, 1600);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "crushedCoal")) {
            rFuelValue =   Math.max(rFuelValue, 1600);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustImpureCoal")) {
            rFuelValue =   Math.max(rFuelValue, 1600);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustCoal")) {
            rFuelValue =   Math.max(rFuelValue, 1600);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustSmallCoal")) {
            rFuelValue =   Math.max(rFuelValue, 400);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustTinyCoal")) {
            rFuelValue =   Math.max(rFuelValue, 177);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "gemCharcoal")) {
            rFuelValue =   Math.max(rFuelValue, 1600);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "crushedCharcoal")) {
            rFuelValue =   Math.max(rFuelValue, 1600);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustImpureCharcoal")) {
            rFuelValue =   Math.max(rFuelValue, 1600);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustCharcoal")) {
            rFuelValue =   Math.max(rFuelValue, 1600);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustSmallCharcoal")) {
            rFuelValue =   Math.max(rFuelValue, 400);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustTinyCharcoal")) {
            rFuelValue =   Math.max(rFuelValue, 177);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustWood")) {
            rFuelValue =   Math.max(rFuelValue, 100);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustSmallWood")) {
            rFuelValue =   Math.max(rFuelValue, 25);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "dustTinyWood")) {
            rFuelValue =   Math.max(rFuelValue, 11);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "plateWood")) {
            rFuelValue =   Math.min(rFuelValue, 300);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "blockLignite")) {
            rFuelValue =   Math.max(rFuelValue, 12000);
        } else if (GT_OreDictUnificator.isItemStackInstanceOf(aFuel, "blockCharcoal")) {
            rFuelValue =   Math.max(rFuelValue, 16000);
        } else if (GT_Utility.areStacksEqual(aFuel, new ItemStack(Blocks.wooden_button, 1))) {
            rFuelValue =   Math.max(rFuelValue, 150);
        } else if (GT_Utility.areStacksEqual(aFuel, new ItemStack(Blocks.ladder, 1))) {
            rFuelValue =   Math.max(rFuelValue, 100);
        } else if (GT_Utility.areStacksEqual(aFuel, new ItemStack(Items.sign, 1))) {
            rFuelValue =   Math.max(rFuelValue, 600);
        } else if (GT_Utility.areStacksEqual(aFuel, new ItemStack(Items.wooden_door, 1))) {
            rFuelValue =   Math.max(rFuelValue, 600);
        } else if (GT_Utility.areStacksEqual(aFuel, ItemList.Block_MSSFUEL.get(1, new Object[0]))) {
            rFuelValue = Math.max(rFuelValue, 150000);
        }if (GT_Utility.areStacksEqual(aFuel, ItemList.Block_SSFUEL.get(1, new Object[0]))) {
            rFuelValue = Math.max(rFuelValue, 100000);
        }

        return rFuelValue;
    }

    public Fluid addAutoGeneratedCorrespondingFluid(Materials aMaterial){
        return addFluid(aMaterial.mName.toLowerCase(Locale.ENGLISH), "molten.autogenerated", aMaterial.mDefaultLocalName, aMaterial, 
        		aMaterial.mRGBa, 1, aMaterial.getLiquidTemperature(), GT_OreDictUnificator.get(OrePrefixes.cell, aMaterial, 1L), ItemList.Cell_Empty.get(1L, new Object[0]), 1000);
    }

	public Fluid addAutoGeneratedCorrespondingGas(Materials aMaterial) {
		return addFluid(aMaterial.mName.toLowerCase(Locale.ENGLISH), "molten.autogenerated", aMaterial.mDefaultLocalName, aMaterial, 
        		aMaterial.mRGBa, 2, aMaterial.getGasTemperature(), GT_OreDictUnificator.get(OrePrefixes.cell, aMaterial, 1L), ItemList.Cell_Empty.get(1L, new Object[0]), 1000);
	}
    
    public Fluid addAutogeneratedMoltenFluid(Materials aMaterial) {
        return addFluid("molten." + aMaterial.mName.toLowerCase(Locale.ENGLISH), "molten.autogenerated", "Molten " + aMaterial.mDefaultLocalName, aMaterial,
                aMaterial.mMoltenRGBa, 4, aMaterial.mMeltingPoint <= 0 ? 1000 : aMaterial.mMeltingPoint, null, null, 0);
    }

    public Fluid addAutogeneratedPlasmaFluid(Materials aMaterial) {
        return addFluid("plasma." + aMaterial.mName.toLowerCase(Locale.ENGLISH), "plasma.autogenerated", aMaterial.mDefaultLocalName + " Plasma", aMaterial,
                aMaterial.mMoltenRGBa, 3, 10000, GT_OreDictUnificator.get(OrePrefixes.cellPlasma, aMaterial, 1L), ItemList.Cell_Empty.get(1L, new Object[0]), 1000);
    }
    
    public void addAutoGeneratedHydroCrackedFluid(Materials aMaterial){
    	GT_Fluid uncrackedFluid = null;
    	if (aMaterial.mFluid != null) {
    		uncrackedFluid = (GT_Fluid) aMaterial.mFluid;
    	} else if (aMaterial.mGas != null) {
    		uncrackedFluid = (GT_Fluid) aMaterial.mGas;
    	}
    	Fluid crackedFluid = addFluid("hydrocracked."+aMaterial.mName.toLowerCase(Locale.ENGLISH), uncrackedFluid.mTextureName,
    			"Hydro-Cracked " + aMaterial.mDefaultLocalName, null, aMaterial.mRGBa, 2, 775, null, null, 0);
		GT_Values.RA.addCrackingRecipe(1, new FluidStack(uncrackedFluid, 1000), Materials.Hydrogen.getGas(1000), 
				new FluidStack(crackedFluid, 1000), 20, 120);
    	aMaterial.setHydroCrackedFluid(crackedFluid);
    }
    
    public void addAutoGeneratedSteamCrackedFluid(Materials aMaterial){
    	GT_Fluid uncrackedFluid = null;
    	if (aMaterial.mFluid != null) {
    		uncrackedFluid = (GT_Fluid) aMaterial.mFluid;
    	} else if (aMaterial.mGas != null) {
    		uncrackedFluid = (GT_Fluid) aMaterial.mGas;
    	}
    	Fluid crackedFluid = addFluid("steamcracked."+aMaterial.mName.toLowerCase(Locale.ENGLISH), uncrackedFluid.mTextureName,
    			"Steam-Cracked " + aMaterial.mDefaultLocalName, null, aMaterial.mRGBa, 2, 775, null, null, 0);
		GT_Values.RA.addCrackingRecipe(2, new FluidStack(uncrackedFluid, 1000), GT_ModHandler.getSteam(1000), 
				new FluidStack(crackedFluid, 1000), 20, 240);
    	aMaterial.setSteamCrackedFluid(crackedFluid);
    }
    
    public Fluid addFluid(String aName, String aLocalized, Materials aMaterial, int aState, int aTemperatureK) {
        return addFluid(aName, aLocalized, aMaterial, aState, aTemperatureK, null, null, 0);
    }

    public Fluid addFluid(String aName, String aLocalized, Materials aMaterial, int aState, int aTemperatureK, ItemStack aFullContainer,
                          ItemStack aEmptyContainer, int aFluidAmount) {
        return addFluid(aName, aName.toLowerCase(Locale.ENGLISH), aLocalized, aMaterial, null, aState, aTemperatureK, aFullContainer, aEmptyContainer, aFluidAmount);
    }

    public Fluid addFluid(String aName, String aTexture, String aLocalized, Materials aMaterial, short[] aRGBa, int aState, int aTemperatureK,
                          ItemStack aFullContainer, ItemStack aEmptyContainer, int aFluidAmount) {
        aName = aName.toLowerCase(Locale.ENGLISH);
        Fluid rFluid = new GT_Fluid(aName, aTexture, aRGBa != null ? aRGBa : Dyes._NULL.getRGBA());
        GT_LanguageManager.addStringLocalization(rFluid.getUnlocalizedName(), aLocalized == null ? aName : aLocalized);
        if (FluidRegistry.registerFluid(rFluid)) {
            switch (aState) {
                case 0:
                    rFluid.setGaseous(false);
                    rFluid.setViscosity(10000);
                    break;
                case 1:
                case 4:
                    rFluid.setGaseous(false);
                    rFluid.setViscosity(1000);
                    break;
                case 2:
                    rFluid.setGaseous(true);
                    rFluid.setDensity(-100);
                    rFluid.setViscosity(200);
                    break;
                case 3:
                    rFluid.setGaseous(true);
                    rFluid.setDensity(55536);
                    rFluid.setViscosity(10);
                    rFluid.setLuminosity(15);
            }
        } else {
            rFluid = FluidRegistry.getFluid(aName);
        }
        if (rFluid.getTemperature() == new Fluid("test").getTemperature()) {
            rFluid.setTemperature(aTemperatureK);
        }
        if (aMaterial != null) {
            switch (aState) {
                case 0:
                    aMaterial.mSolid = rFluid;
                    break;
                case 1:
                    aMaterial.mFluid = rFluid;
                    break;
                case 2:
                    aMaterial.mGas = rFluid;
                    break;
                case 3:
                    aMaterial.mPlasma = rFluid;
                    break;
                case 4:
                    aMaterial.mStandardMoltenFluid = rFluid;
            }
        }
        if ((aFullContainer != null) && (aEmptyContainer != null)
                && (!FluidContainerRegistry.registerFluidContainer(new FluidStack(rFluid, aFluidAmount), aFullContainer, aEmptyContainer))) {
            GT_Values.RA.addFluidCannerRecipe(aFullContainer, GT_Utility.getContainerItem(aFullContainer, false), null, new FluidStack(rFluid, aFluidAmount));
        }
        return rFluid;
    }

    public File getSaveDirectory() {
        return this.mUniverse == null ? null : this.mUniverse.getSaveHandler().getWorldDirectory();
    }

    public void registerUnificationEntries() {
        GregTech_API.sUnification.mConfig.save();
        GregTech_API.sUnification.mConfig.load();
        GT_OreDictUnificator.resetUnificationEntries();
        for (OreDictEventContainer tOre : this.mEvents) {
            if ((!(tOre.mEvent.Ore.getItem() instanceof GT_MetaGenerated_Item)) && (tOre.mPrefix != null) && (tOre.mPrefix.mIsUnificatable) && (tOre.mMaterial != null)) {
                boolean chkmi = tOre.mModID != null;
                if (chkmi) {
                    if (tOre.mModID.equalsIgnoreCase("enderio") && tOre.mPrefix == OrePrefixes.ingot && tOre.mMaterial == Materials.DarkSteel) {
                        GT_OreDictUnificator.addAssociation(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, false);
                        GT_OreDictUnificator.set(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, (GregTech_API.sUnification.get(new StringBuilder().append(ConfigCategories.specialunificationtargets).append(".").append(tOre.mModID).toString(), tOre.mEvent.Name, true)), true);continue;
                    } else if (tOre.mModID.equalsIgnoreCase("thermalfoundation") && tOre.mPrefix == OrePrefixes.dust && tOre.mMaterial == Materials.Blizz) {
                        GT_OreDictUnificator.addAssociation(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, false);
                        GT_OreDictUnificator.set(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, (GregTech_API.sUnification.get(new StringBuilder().append(ConfigCategories.specialunificationtargets).append(".").append(tOre.mModID).toString(), tOre.mEvent.Name, true)), true);continue;
                    } else if (tOre.mModID.equalsIgnoreCase("thermalfoundation") && tOre.mPrefix == OrePrefixes.dust && tOre.mMaterial == Materials.Pyrotheum) {
                        GT_OreDictUnificator.addAssociation(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, false);
                        GT_OreDictUnificator.set(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, (GregTech_API.sUnification.get(new StringBuilder().append(ConfigCategories.specialunificationtargets).append(".").append(tOre.mModID).toString(), tOre.mEvent.Name, true)), true);continue;
                    } else if (tOre.mModID.equalsIgnoreCase(aTextArsmagica2) && tOre.mPrefix == OrePrefixes.dust && tOre.mMaterial == Materials.Vinteum) {
                        GT_OreDictUnificator.addAssociation(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, false);
                        GT_OreDictUnificator.set(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, (GregTech_API.sUnification.get(new StringBuilder().append(ConfigCategories.specialunificationtargets).append(".").append(tOre.mModID).toString(), tOre.mEvent.Name, true)), true);continue;
                    } else if (tOre.mModID.equalsIgnoreCase(aTextArsmagica2) && tOre.mPrefix == OrePrefixes.gem && tOre.mMaterial == Materials.BlueTopaz) {
                        GT_OreDictUnificator.addAssociation(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, false);
                        GT_OreDictUnificator.set(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, (GregTech_API.sUnification.get(new StringBuilder().append(ConfigCategories.specialunificationtargets).append(".").append(tOre.mModID).toString(), tOre.mEvent.Name, true)), true);continue;
                    } else if (tOre.mModID.equalsIgnoreCase(aTextArsmagica2) && tOre.mPrefix == OrePrefixes.gem && tOre.mMaterial == Materials.Chimerite) {
                        GT_OreDictUnificator.addAssociation(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, false);
                        GT_OreDictUnificator.set(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, (GregTech_API.sUnification.get(new StringBuilder().append(ConfigCategories.specialunificationtargets).append(".").append(tOre.mModID).toString(), tOre.mEvent.Name, true)), true);continue;
                    } else if (tOre.mModID.equalsIgnoreCase(aTextArsmagica2) && tOre.mPrefix == OrePrefixes.gem && tOre.mMaterial == Materials.Moonstone) {
                        GT_OreDictUnificator.addAssociation(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, false);
                        GT_OreDictUnificator.set(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, (GregTech_API.sUnification.get(new StringBuilder().append(ConfigCategories.specialunificationtargets).append(".").append(tOre.mModID).toString(), tOre.mEvent.Name, true)), true);continue;
                    } else if (tOre.mModID.equalsIgnoreCase(aTextArsmagica2) && tOre.mPrefix == OrePrefixes.gem && tOre.mMaterial == Materials.Sunstone) {
                        GT_OreDictUnificator.addAssociation(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, false);
                        GT_OreDictUnificator.set(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, (GregTech_API.sUnification.get(new StringBuilder().append(ConfigCategories.specialunificationtargets).append(".").append(tOre.mModID).toString(), tOre.mEvent.Name, true)), true);continue;
                    } else if (tOre.mModID.equalsIgnoreCase("rotarycraft") && tOre.mPrefix == OrePrefixes.ingot && tOre.mMaterial == Materials.HSLA) {
                        GT_OreDictUnificator.addAssociation(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, false);
                        GT_OreDictUnificator.set(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, (GregTech_API.sUnification.get(new StringBuilder().append(ConfigCategories.specialunificationtargets).append(".").append(tOre.mModID).toString(), tOre.mEvent.Name, true)), true);continue;
                    } else if (tOre.mModID.equalsIgnoreCase("appliedenergistics2") && tOre.mPrefix == OrePrefixes.gem && tOre.mMaterial == Materials.CertusQuartz) {
                        GT_OreDictUnificator.addAssociation(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, false);
                        GT_OreDictUnificator.set(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, (GregTech_API.sUnification.get(new StringBuilder().append(ConfigCategories.specialunificationtargets).append(".").append(tOre.mModID).toString(), tOre.mEvent.Name, true)), true);continue;
                    } else if (tOre.mModID.equalsIgnoreCase("appliedenergistics2") && tOre.mPrefix == OrePrefixes.dust && tOre.mMaterial == Materials.CertusQuartz) {
                        GT_OreDictUnificator.addAssociation(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, false);
                        GT_OreDictUnificator.set(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, (GregTech_API.sUnification.get(new StringBuilder().append(ConfigCategories.specialunificationtargets).append(".").append(tOre.mModID).toString(), tOre.mEvent.Name, true)), true);continue;
                    }
                }
                if (GT_OreDictUnificator.isBlacklisted(tOre.mEvent.Ore)) {
                    GT_OreDictUnificator.addAssociation(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, true);
                } else {
                    GT_OreDictUnificator.addAssociation(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, false);
                    GT_OreDictUnificator.set(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, (chkmi) && (GregTech_API.sUnification.get(new StringBuilder().append(ConfigCategories.specialunificationtargets).append(".").append(tOre.mModID).toString(), tOre.mEvent.Name, false)), true);
                }
            }
        }
        for (OreDictEventContainer tOre : this.mEvents) {
            if (((tOre.mEvent.Ore.getItem() instanceof GT_MetaGenerated_Item)) && (tOre.mPrefix != null) && (tOre.mPrefix.mIsUnificatable)
                    && (tOre.mMaterial != null)) {
                if (GT_OreDictUnificator.isBlacklisted(tOre.mEvent.Ore)) {
                    GT_OreDictUnificator.addAssociation(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, true);
                } else {
                    GT_OreDictUnificator.addAssociation(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, false);
                    GT_OreDictUnificator.set(tOre.mPrefix, tOre.mMaterial, tOre.mEvent.Ore, (tOre.mModID != null) &&
                            (GregTech_API.sUnification.get(new StringBuilder().append(ConfigCategories.specialunificationtargets).append(".").append(tOre.mModID), tOre.mEvent.Name, false)), true);
                }
            }
        }
        GregTech_API.sUnificationEntriesRegistered = true;
        GregTech_API.sUnification.mConfig.save();
        GT_Recipe.reInit();
    }

    public void activateOreDictHandler() {
        this.mOreDictActivated = true;
        ProgressManager.ProgressBar progressBar = ProgressManager.push("Register materials", mEvents.size());
        OreDictEventContainer tEvent;
        for (Iterator i$ = this.mEvents.iterator(); i$.hasNext(); registerRecipes(tEvent)) {
            tEvent = (OreDictEventContainer) i$.next();
            
            progressBar.step(tEvent.mMaterial == null ? "" : tEvent.mMaterial.toString());
        }
        ProgressManager.pop(progressBar);
    }

    public static final HashMap<Integer,HashMap<ChunkCoordIntPair,int []>> dimensionWiseChunkData = new HashMap<>(16);//stores chunk data that is loaded/saved
    public static final HashMap<Integer,GT_Pollution> dimensionWisePollution = new HashMap<>(16);//stores GT_Polluttors objects
	public static final byte GTOIL=3,GTOILFLUID=2,GTPOLLUTION=1,GTMETADATA=0,NOT_LOADED=0,LOADED=1;//consts
    //@Deprecated
	//public static final HashMap<ChunkPosition, int[]>  chunkData = new HashMap<>(0);

    private static final byte oilVer=(byte)20;//non zero plz

    //TO get default's fast
    public static int[] getDefaultChunkDataOnCreation(){
        return new int[]{NOT_LOADED,0,-1,-1};
    }
    public static int[] getDefaultChunkDataOnLoad(){
        return new int[]{LOADED,0,-1,-1};
    }

    @SubscribeEvent
    public void handleChunkSaveEvent(ChunkDataEvent.Save event) {//ALWAYS SAVE FROM THE HASH MAP DATA
        HashMap<ChunkCoordIntPair,int []> chunkData=dimensionWiseChunkData.get(event.world.provider.dimensionId);
        if(chunkData==null) return;//no dim info stored

        int[] tInts = chunkData.get(event.getChunk().getChunkCoordIntPair());
        if(tInts==null) return;//no chunk data stored
        //assuming len of this array 4
        if(tInts[3]>=0)event.getData().setInteger("GTOIL", tInts[GTOIL]);
        else event.getData().removeTag("GTOIL");
        if(tInts[2]>=0)event.getData().setInteger("GTOILFLUID", tInts[GTOILFLUID]);
        else event.getData().removeTag("GTOILFLUID");
        if(tInts[1]>0)event.getData().setInteger("GTPOLLUTION", tInts[GTPOLLUTION]);
        else event.getData().removeTag("GTPOLLUTION");
        event.getData().setByte("GTOILVER", oilVer);//version mark
    }

    @SubscribeEvent
    public void handleChunkLoadEvent(ChunkDataEvent.Load event) {
        final int worldID=event.world.provider.dimensionId;
        HashMap<ChunkCoordIntPair, int[]> chunkData = dimensionWiseChunkData.get(worldID);
        if (chunkData == null){
            chunkData=new HashMap<>(1024);
            dimensionWiseChunkData.put(worldID, chunkData);
        }
        if (dimensionWisePollution.get(worldID) == null)
            dimensionWisePollution.put(worldID, new GT_Pollution(event.world));

        int[] tInts = chunkData.get(event.getChunk().getChunkCoordIntPair());
        if (tInts == null) {
            //NOT LOADED and NOT PROCESSED by pollution algorithms
            //regular load
            tInts = getDefaultChunkDataOnLoad();

            if (event.getData().getByte("GTOILVER") == oilVer) {
                if (event.getData().hasKey("GTOIL"))
                    tInts[GTOIL] = event.getData().getInteger("GTOIL");
                if (event.getData().hasKey("GTOILFLUID"))
                    tInts[GTOILFLUID] = event.getData().getInteger("GTOILFLUID");
            }

            tInts[GTPOLLUTION] = event.getData().getInteger("GTPOLLUTION");//Defaults to 0

            //store in HASH MAP if has useful data
            if (tInts[GTPOLLUTION] > 0 || tInts[GTOIL] >= 0 || tInts[GTOILFLUID] >= 0)
                chunkData.put(event.getChunk().getChunkCoordIntPair(), tInts);
        } else if (tInts[GTMETADATA] == NOT_LOADED) {//was NOT loaded from chunk save game data
            //NOT LOADED but generated
            //append load
            if (event.getData().getByte("GTOILVER") == oilVer) {
                if (tInts[GTOIL] < 0 && event.getData().hasKey("GTOIL"))//if was not yet initialized
                    tInts[GTOIL] = event.getData().getInteger("GTOIL");

                if (tInts[GTOILFLUID] < 0 && event.getData().hasKey("GTOILFLUID"))//if was not yet initialized
                    tInts[GTOILFLUID] = event.getData().getInteger("GTOILFLUID");
            } else {
                tInts[GTOIL] = -1;
                tInts[GTOILFLUID] = -1;
            }

            tInts[GTPOLLUTION] += event.getData().getInteger("GTPOLLUTION");//Defaults to 0, add stored pollution to data
            tInts[GTMETADATA] = LOADED;//mark as = loaded
            //store in HASHMAP

            chunkData.put(event.getChunk().getChunkCoordIntPair(), tInts);
        }//else if(tInts[0]==1){
        ////Already loaded chunk data
        ////DO NOTHING - this chunk data was already loaded and stored in hash map
        //}
    }
    
    @SubscribeEvent
    public void onBlockBreakSpeedEvent(PlayerEvent.BreakSpeed aEvent)
    {
      if (aEvent.newSpeed > 0.0F)
      {
        if (aEvent.entityPlayer != null)
        {
          ItemStack aStack = aEvent.entityPlayer.getCurrentEquippedItem();
          if ((aStack != null) && ((aStack.getItem() instanceof GT_MetaGenerated_Tool))) {
            aEvent.newSpeed = ((GT_MetaGenerated_Tool)aStack.getItem()).onBlockBreakSpeedEvent(aEvent.newSpeed, aStack, aEvent.entityPlayer, aEvent.block, aEvent.x, aEvent.y, aEvent.z, (byte)aEvent.metadata, aEvent);
          }
        }
      }
    }

    public static class OreDictEventContainer {
        public final OreDictionary.OreRegisterEvent mEvent;
        public final OrePrefixes mPrefix;
        public final Materials mMaterial;
        public final String mModID;

        public OreDictEventContainer(OreDictionary.OreRegisterEvent aEvent, OrePrefixes aPrefix, Materials aMaterial, String aModID) {
            this.mEvent = aEvent;
            this.mPrefix = aPrefix;
            this.mMaterial = aMaterial;
            this.mModID = ((aModID == null) || (aModID.equals("UNKNOWN")) ? null : aModID);
        }
    }

    @SubscribeEvent
    public void onBlockEvent(BlockEvent event) {
    	if (event.block.getUnlocalizedName().equals("blockAlloyGlass"))
    		GregTech_API.causeMachineUpdate(event.world, event.x, event.y, event.z);
    }


}
