package gregtech.loaders.oreprocessing;

import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.*;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;
import gregtech.common.blocks.GT_Block_Ores;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static gregtech.api.enums.GT_Values.MOD_ID;

public class ProcessingOre implements gregtech.api.interfaces.IOreRecipeRegistrator {
    private ArrayList<Materials> mAlreadyListedOres = new ArrayList(1000);
    private static ArrayList<GT_Block_Ores> mPretreatOres = new ArrayList<>();
    private HashMap<GT_Block_Ores, ItemStack[]> mPretreatMap = new HashMap<>();
    private boolean mPretreatInit = false;

    public ProcessingOre() {
        for (OrePrefixes tPrefix : OrePrefixes.values())
            if ((tPrefix.name().startsWith("ore")) && (tPrefix != OrePrefixes.orePoor) && (tPrefix != OrePrefixes.oreSmall) && (tPrefix != OrePrefixes.oreRich) && (tPrefix != OrePrefixes.oreNormal))
                tPrefix.add(this);
    }

    public void registerOre(OrePrefixes aPrefix, Materials aMaterial, String aOreDictName, String aModName, ItemStack aStack) {
    	if (!mPretreatInit) {
    		this.pretreatInit();
    		mPretreatInit = true;
    	}
    	ItemStack[] tList = mPretreatMap.get(Block.getBlockFromItem(aStack.getItem()));
		int tMeta = aStack.getItemDamage() / 1000;
		if (tList != null && tMeta >= 0 && tMeta < 8 && tList[tMeta] != null) {
			this.registerOre(null, aMaterial, aOreDictName, aModName, aStack, tList[tMeta]);
			return;
		}
        this.registerOre(aPrefix, aMaterial, aOreDictName, aModName, aStack, null);
    }

    private void registerOre(OrePrefixes aPrefix, Materials aMaterial, String aOreDictName, String aModName, ItemStack aStack, ItemStack aReplacement) {
    	boolean tIsRich = (aPrefix == OrePrefixes.oreNether) || (aPrefix == OrePrefixes.oreEnd) || (aPrefix == OrePrefixes.oreDense);

        if (aMaterial == Materials.Oilsands) {
            GT_Values.RA.addCentrifugeRecipe(GT_Utility.copyAmount(1L, new Object[]{aStack}), null, null, Materials.Oil.getFluid(tIsRich ? 1000L : 500L), new ItemStack(net.minecraft.init.Blocks.sand, 1, 0), null, null, null, null, null, new int[]{tIsRich ? '?' : '?'}, tIsRich ? 2000 : 1000, 5);
        } else {
            registerStandardOreRecipes(aPrefix, aMaterial, GT_Utility.copyAmount(1L, new Object[]{aStack}), Math.max(1, gregtech.api.GregTech_API.sOPStuff.get(gregtech.api.enums.ConfigCategories.Materials.oreprocessingoutputmultiplier, aMaterial.toString(), 1)) * (tIsRich ? 2 : 1), aReplacement);
        }
    }

    private boolean registerStandardOreRecipes(OrePrefixes aPrefix, Materials aMaterial, ItemStack aOreStack, int aMultiplier, ItemStack aReplacement) {
        if ((aOreStack == null) || (aMaterial == null)) return false;
        GT_ModHandler.addValuableOre(GT_Utility.getBlockFromStack(aOreStack), aOreStack.getItemDamage(), aMaterial.mOreValue);
        Materials tMaterial = aMaterial.mOreReplacement;
        Materials tPrimaryByMaterial = null;
        aMultiplier = Math.max(1, aMultiplier);
        aOreStack = GT_Utility.copyAmount(1L, new Object[]{aOreStack});
        aOreStack.stackSize = 1;


        ItemStack tIngot = GT_OreDictUnificator.get(OrePrefixes.ingot, aMaterial.mDirectSmelting, 1L);
        ItemStack tGem = GT_OreDictUnificator.get(OrePrefixes.gem, tMaterial, 1L);
        ItemStack tSmeltInto = tIngot == null ? null : aMaterial.contains(SubTag.SMELTING_TO_GEM) ? GT_OreDictUnificator.get(OrePrefixes.gem, tMaterial.mDirectSmelting, GT_OreDictUnificator.get(OrePrefixes.crystal, tMaterial.mDirectSmelting, GT_OreDictUnificator.get(OrePrefixes.gem, tMaterial, GT_OreDictUnificator.get(OrePrefixes.crystal, tMaterial, 1L), 1L), 1L), 1L) : tIngot;

        ItemStack tDust = GT_OreDictUnificator.get(OrePrefixes.dust, tMaterial, tGem, 1L);
        ItemStack tCleaned = GT_OreDictUnificator.get(OrePrefixes.crushedPurified, tMaterial, tDust, 1L);
        ItemStack tCrushed = GT_OreDictUnificator.get(OrePrefixes.crushed, tMaterial, aMaterial.mOreMultiplier * aMultiplier);
        ItemStack tPrimaryByProduct = null;

        if (tCrushed == null) {
            tCrushed = GT_OreDictUnificator.get(OrePrefixes.dustImpure, tMaterial, GT_Utility.copyAmount(aMaterial.mOreMultiplier * aMultiplier, new Object[]{tCleaned, tDust, tGem}), aMaterial.mOreMultiplier * aMultiplier);
        }

        ArrayList<ItemStack> tByProductStacks = new ArrayList();

        for (Materials tMat : aMaterial.mOreByProducts) {
            ItemStack tByProduct = GT_OreDictUnificator.get(OrePrefixes.dust, tMat, 1L);
            if (tByProduct != null) tByProductStacks.add(tByProduct);
            if (tPrimaryByProduct == null) {
                tPrimaryByMaterial = tMat;
                tPrimaryByProduct = GT_OreDictUnificator.get(OrePrefixes.dust, tMat, 1L);
                if (GT_OreDictUnificator.get(OrePrefixes.dustSmall, tMat, 1L) == null)
                    GT_OreDictUnificator.get(OrePrefixes.dustTiny, tMat, GT_OreDictUnificator.get(OrePrefixes.nugget, tMat, 2L), 2L);
            }
            GT_OreDictUnificator.get(OrePrefixes.dust, tMat, 1L);
            if (GT_OreDictUnificator.get(OrePrefixes.dustSmall, tMat, 1L) == null)
                GT_OreDictUnificator.get(OrePrefixes.dustTiny, tMat, GT_OreDictUnificator.get(OrePrefixes.nugget, tMat, 2L), 2L);
        }
        if ((!tByProductStacks.isEmpty()) && (!this.mAlreadyListedOres.contains(aMaterial))) {
            this.mAlreadyListedOres.add(aMaterial);
            gregtech.api.util.GT_Recipe.GT_Recipe_Map.sByProductList.addFakeRecipe(false, new ItemStack[]{GT_OreDictUnificator.get(OrePrefixes.ore, aMaterial, aOreStack, 1L)}, (ItemStack[]) tByProductStacks.toArray(new ItemStack[tByProductStacks.size()]), null, null, null, null, 0, 0, 0);
        }

        if (tPrimaryByMaterial == null) tPrimaryByMaterial = tMaterial;
        if (tPrimaryByProduct == null) tPrimaryByProduct = tDust;
        boolean tHasSmelting = false;

        if (tSmeltInto != null) {
            if ((aMaterial.mBlastFurnaceRequired) || (aMaterial.mDirectSmelting.mBlastFurnaceRequired)) {
                GT_ModHandler.removeFurnaceSmelting(aOreStack);
            } else {
                if (GT_Mod.gregtechproxy.mTEMachineRecipes) {
                    GT_ModHandler.addInductionSmelterRecipe(aOreStack, new ItemStack(net.minecraft.init.Blocks.sand, 1), GT_Utility.mul(aMultiplier * (aMaterial.contains(SubTag.INDUCTIONSMELTING_LOW_OUTPUT) ? 1 : 2) * aMaterial.mSmeltingMultiplier, new Object[]{tSmeltInto}), ItemList.TE_Slag_Rich.get(1L, new Object[0]), 300 * aMultiplier, 10 * aMultiplier);
                    GT_ModHandler.addInductionSmelterRecipe(aOreStack, ItemList.TE_Slag_Rich.get(aMultiplier, new Object[0]), GT_Utility.mul(aMultiplier * (aMaterial.contains(SubTag.INDUCTIONSMELTING_LOW_OUTPUT) ? 2 : 3) * aMaterial.mSmeltingMultiplier, new Object[]{tSmeltInto}), ItemList.TE_Slag.get(aMultiplier, new Object[0]), 300 * aMultiplier, 95);
                }
                tHasSmelting = GT_ModHandler.addSmeltingRecipe(aOreStack, GT_Utility.copyAmount(aMultiplier * aMaterial.mSmeltingMultiplier, new Object[]{tSmeltInto}));
            }

            if (aMaterial.contains(SubTag.BLASTFURNACE_CALCITE_TRIPLE)) {
                GT_Values.RA.addBlastRecipe(aOreStack, GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Calcite,   aMultiplier), null, null, GT_Utility.mul(aMultiplier * 3 * aMaterial.mSmeltingMultiplier, new Object[]{tSmeltInto}), ItemList.TE_Slag.get(1L, new Object[]{GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.DarkAsh, 1L)}), tSmeltInto.stackSize * 500, 120, 1500);
                GT_Values.RA.addBlastRecipe(aOreStack, GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Quicklime, aMultiplier), null, null, GT_Utility.mul(aMultiplier * 3 * aMaterial.mSmeltingMultiplier, new Object[]{tSmeltInto}), ItemList.TE_Slag.get(1L, new Object[]{GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.DarkAsh, 1L)}), tSmeltInto.stackSize * 500, 120, 1500);
            } else if (aMaterial.contains(SubTag.BLASTFURNACE_CALCITE_DOUBLE)) {
                GT_Values.RA.addBlastRecipe(aOreStack, GT_OreDictUnificator.get(OrePrefixes.dust, Materials.Calcite, aMultiplier), null, null, GT_Utility.mul(aMultiplier * (GT_Mod.gregtechproxy.mMixedOreOnlyYieldsTwoThirdsOfPureOre ? 2 : 3) * aMaterial.mSmeltingMultiplier, new Object[]{tSmeltInto}), ItemList.TE_Slag.get(1L, new Object[]{GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.DarkAsh, 1L)}), tSmeltInto.stackSize * 500, 120, 1500);
                GT_Values.RA.addBlastRecipe(aOreStack, GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Quicklime, aMultiplier * 3), null, null, GT_Utility.mul(aMultiplier * 2 * aMaterial.mSmeltingMultiplier, new Object[]{tSmeltInto}), ItemList.TE_Slag.get(1L, new Object[]{GT_OreDictUnificator.get(OrePrefixes.dustSmall, Materials.DarkAsh, 1L)}), tSmeltInto.stackSize * 500, 120, 1500);
            }
        }

        if (!tHasSmelting) {
            GT_ModHandler.addSmeltingRecipe(aOreStack, GT_OreDictUnificator.get(OrePrefixes.gem, tMaterial.mDirectSmelting, Math.max(1, aMultiplier * aMaterial.mSmeltingMultiplier / 2)));
        }

        if (tCrushed != null) {
            GT_Values.RA.addForgeHammerRecipe(aOreStack, GT_Utility.copy(new Object[]{GT_Utility.copyAmount(tCrushed.stackSize, new Object[]{tGem}), tCrushed}), 16, 10);
            GT_ModHandler.addPulverisationRecipe(aOreStack, GT_Utility.mul(2L, new Object[]{tCrushed}), tMaterial.contains(SubTag.PULVERIZING_CINNABAR) ? GT_OreDictUnificator.get(OrePrefixes.crystal, Materials.Cinnabar, GT_OreDictUnificator.get(OrePrefixes.gem, tPrimaryByMaterial, GT_Utility.copyAmount(1L, new Object[]{tPrimaryByProduct}), 1L), 1L) : GT_OreDictUnificator.get(OrePrefixes.gem, tPrimaryByMaterial, GT_Utility.copyAmount(1L, new Object[]{tPrimaryByProduct}), 1L), tPrimaryByProduct == null ? 0 : tPrimaryByProduct.stackSize * 10 * aMultiplier * aMaterial.mByProductMultiplier, aPrefix == null ? aReplacement : GT_OreDictUnificator.getDust(aPrefix.mSecondaryMaterial), 50, true);
        }
        return true;
    }

    public static void addToPretreatList(GT_Block_Ores aOre) {
    	mPretreatOres.add(aOre);
    }

    private void pretreatInit() {
    	for (GT_Block_Ores aOre : mPretreatOres) {
    		boolean[] aEnabled = aOre.getEnabledMetas();
    		Object[] aDroppedDusts = aOre.getDroppedDusts();
    		OrePrefixes[] aPrefixes = aOre.getProcessingPrefix();
    		ItemStack[] tCustomedItems = new ItemStack[8];
    		for (int i = 0; i < 8; i++) {
    			if (!aEnabled[i]) continue;
    			ItemStack dust = aDroppedDusts[i] instanceof Materials ? OrePrefixes.dust.doGenerateItem((Materials) aDroppedDusts[i]) ? GT_OreDictUnificator.get(OrePrefixes.dust, (Materials) aDroppedDusts[i], 1L) : null : aDroppedDusts[i] instanceof ItemStack ? ((ItemStack) aDroppedDusts[i]) : null;
            	ItemStack dust1 = aPrefixes[i] != null ? GT_OreDictUnificator.getDust(aPrefixes[i].mSecondaryMaterial) : null;
            	if (aPrefixes[i] != null && GT_Utility.areStacksEqual(dust, dust1)) {
            		tCustomedItems[i] = null;
            	} else {
            		tCustomedItems[i] = dust;
            	}
    		}
    		mPretreatMap.put(aOre, tCustomedItems);
    	}
    }
}
