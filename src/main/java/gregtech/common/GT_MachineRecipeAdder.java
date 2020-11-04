package gregtech.common;

import cpw.mods.fml.common.Loader;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.internal.IGT_RecipeAdder;
import gregtech.api.objects.GT_FluidStack;
import gregtech.api.objects.ItemData;
import gregtech.api.recipes.GT_AssemblyLineRecipe;
import gregtech.api.recipes.GT_MachineRecipe;
import gregtech.api.recipes.GT_RecipeInput;
import gregtech.api.recipes.GT_RecipeInputAlts;
import gregtech.api.recipes.GT_RecipeInputOredict;
import gregtech.api.recipes.GT_RecipeMap;
import gregtech.api.recipes.GT_RecipeOutput;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;
import gregtech.common.items.GT_IntegratedCircuit_Item;
import java.util.Arrays;
import mods.railcraft.common.blocks.aesthetics.cube.EnumCube;
import mods.railcraft.common.items.RailcraftToolItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class GT_MachineRecipeAdder implements IGT_RecipeAdder {

    @Override
    @Deprecated
    public boolean addFusionReactorRecipe(ItemStack aInput1, ItemStack aInput2, ItemStack aOutput1, int aFusionDurationInTicks, int aFusionEnergyPerTick, int aEnergyNeededForStartingFusion) {
        return false;
    }

    @Override
    public boolean addFusionReactorRecipe(FluidStack aInput1, FluidStack aInput2, FluidStack aOutput1, int aDuration, int aEUt, int aStartEU) {
        if (aInput1 == null || aInput2 == null || aOutput1 == null || aDuration < 1 || aEUt < 1 || aStartEU < 1) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("fusion", aOutput1.getFluid().getName(), aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sFusionRecipes.addRecipe(null, new FluidStack[]{aInput1, aInput2}, new FluidStack[]{aOutput1}, tDuration, aEUt, aStartEU);
        return true;
    }

    @Override
    public boolean addCentrifugeRecipe(ItemStack aInput1, int aInput2, ItemStack aOutput1, ItemStack aOutput2, ItemStack aOutput3, ItemStack aOutput4, ItemStack aOutput5, ItemStack aOutput6, int aDuration) {
        ItemStack tInput2 = null;
        if (aInput2 < 0) {
            tInput2 = ItemList.IC2_Fuel_Can_Empty.get(-aInput2, new Object[0]);
        } else if (aInput2 > 0) {
            tInput2 = ItemList.Cell_Empty.get(aInput2, new Object[0]);
        }
        return addCentrifugeRecipe(aInput1, tInput2, GT_Values.NF, GT_Values.NF, aOutput1, aOutput2, aOutput3, aOutput4, aOutput5, aOutput6, null, aDuration, 5);
    }

    @Override
    public boolean addCentrifugeRecipe(ItemStack aInput1, ItemStack aInput2, FluidStack aFluidInput, FluidStack aFluidOutput, ItemStack aOutput1, ItemStack aOutput2, ItemStack aOutput3, ItemStack aOutput4, ItemStack aOutput5, ItemStack aOutput6, int[] aChances, int aDuration, int aEUt) {
        if (((aInput1 == null) && (aFluidInput == null)) || ((aOutput1 == null) && (aFluidOutput == null))) {
            return false;
        }
        int tDuration = aDuration;
        if (aInput1 != null) {
            tDuration = GregTech_API.sRecipeFile.get("centrifuge", aInput1, aDuration);
            if (tDuration <= 0) {
                return false;
            }
        }
        if (aFluidInput != null) {
            tDuration = GregTech_API.sRecipeFile.get("centrifuge", aFluidInput.getFluid().getName(), aDuration);
            if (tDuration <= 0) {
                return false;
            }
        }
        GT_RecipeMap.sCentrifugeRecipes.addRecipe(true, new ItemStack[]{aInput1, aInput2}, new ItemStack[]{aOutput1, aOutput2, aOutput3, aOutput4, aOutput5, aOutput6}, null, aChances, new FluidStack[]{aFluidInput}, new FluidStack[]{aFluidOutput}, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addCompressorRecipe(ItemStack aInput1, ItemStack aOutput1, int aDuration, int aEUt) {
        if ((aInput1 == null) || (aOutput1 == null)) {
                return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("compressor", aInput1, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sCompressorRecipes.addRecipe(true, new ItemStack[]{aInput1}, new ItemStack[]{aOutput1}, null, null, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addElectrolyzerRecipe(ItemStack aInput1, int aInput2, ItemStack aOutput1, ItemStack aOutput2, ItemStack aOutput3, ItemStack aOutput4, ItemStack aOutput5, ItemStack aOutput6, int aDuration, int aEUt) {
        ItemStack tInput2 = null;
        if (aInput2 < 0) {
            tInput2 = ItemList.IC2_Fuel_Can_Empty.get(-aInput2, new Object[0]);
        } else if (aInput2 > 0) {
            tInput2 = ItemList.Cell_Empty.get(aInput2, new Object[0]);
        }
        return addElectrolyzerRecipe(aInput1, tInput2, GT_Values.NF, GT_Values.NF, aOutput1, aOutput2, aOutput3, aOutput4, aOutput5, aOutput6, null, aDuration, aEUt);
    }

    @Override
    public boolean addElectrolyzerRecipe(ItemStack aInput1, ItemStack aInput2, FluidStack aFluidInput, FluidStack aFluidOutput, ItemStack aOutput1, ItemStack aOutput2, ItemStack aOutput3, ItemStack aOutput4, ItemStack aOutput5, ItemStack aOutput6, int[] aChances, int aDuration, int aEUt) {
        if (((aInput1 == null) && (aFluidInput == null)) || ((aOutput1 == null) && (aFluidOutput == null))) {
            return false;
        }
        int tDuration = aDuration;
        if (aInput1 != null) {
            tDuration = GregTech_API.sRecipeFile.get("electrolyzer", aInput1, aDuration);
            if (tDuration <= 0) {
                return false;
            }
        }
        if (aFluidInput != null) {
            tDuration = GregTech_API.sRecipeFile.get("electrolyzer", aFluidInput.getFluid().getName(), aDuration);
            if (tDuration <= 0) {
                return false;
            }
        }
        GT_RecipeMap.sElectrolyzerRecipes.addRecipe(true, new ItemStack[]{aInput1, aInput2}, new ItemStack[]{aOutput1, aOutput2, aOutput3, aOutput4, aOutput5, aOutput6}, null, aChances, new FluidStack[]{aFluidInput}, new FluidStack[]{aFluidOutput}, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addChemicalRecipe(ItemStack aInput1, ItemStack aInput2, ItemStack aOutput, int aDuration) {
        return addChemicalRecipe(aInput1, aInput2, GT_Values.NF, GT_Values.NF, aOutput, aDuration);
    }

    @Override
    public boolean addChemicalRecipe(ItemStack aInput1, ItemStack aInput2, FluidStack aFluidInput, FluidStack aFluidOutput, ItemStack aOutput, int aDuration) {
        return addChemicalRecipe(aInput1, aInput2, aFluidInput, aFluidOutput, aOutput, aDuration, 30);
    }

    @Override
    public boolean addChemicalRecipe(ItemStack aInput1, ItemStack aInput2, FluidStack aFluidInput, FluidStack aFluidOutput, ItemStack aOutput, ItemStack aOutput2, int aDuration) {
        return addChemicalRecipe(aInput1, aInput2, aFluidInput, aFluidOutput, aOutput, aOutput2, aDuration, 30);
    }

    @Override
    public boolean addChemicalRecipeForBasicMachineOnly(ItemStack aInput1, ItemStack aInput2, FluidStack aFluidInput, FluidStack aFluidOutput, ItemStack aOutput, ItemStack aOutput2, int aDuration, int aEUtick) {
        if (((aInput1 == null) && (aFluidInput == null)) || ((aOutput == null) && (aOutput2 == null) && (aFluidOutput == null))) {
            return false;
        }
        int tDuration = aDuration;
        if (aOutput != null || aOutput2 != null) {
            tDuration = GregTech_API.sRecipeFile.get("chemicalreactor", aOutput, aDuration);
            if (tDuration <= 0) {
                return false;
            }
        }
        if (aFluidOutput != null) {
            tDuration = GregTech_API.sRecipeFile.get("chemicalreactor", aFluidOutput.getFluid().getName(), aDuration);
            if (tDuration <= 0) {
                return false;
            }
        }
        if (aEUtick <= 0) {
            return false;
        }
        GT_RecipeMap.sChemicalRecipes.addRecipe(true, new ItemStack[]{aInput1, aInput2}, new ItemStack[]{aOutput, aOutput2}, null, null, new FluidStack[]{aFluidInput}, new FluidStack[]{aFluidOutput}, tDuration, aEUtick, 0);
        return true;
    }

    @Override
    public void addDefaultPolymerizationRecipes(Fluid aBasicMaterial, ItemStack aBasicMaterialCell, Fluid aPolymer) {
    	//Oxygen/Titaniumtetrafluoride -> +50% Output each
        addChemicalRecipe(ItemList.Cell_Air.get(1, new Object[0]), GT_Utility.getIntegratedCircuit(1), new GT_FluidStack(aBasicMaterial, 288), new GT_FluidStack(aPolymer, 288),  Materials.Empty.getCells(1), 320);
        addChemicalRecipe(Materials.Oxygen.getCells(1),            GT_Utility.getIntegratedCircuit(1), new GT_FluidStack(aBasicMaterial, 144), new GT_FluidStack(aPolymer, 216),  Materials.Empty.getCells(1), 160);
        addChemicalRecipe(aBasicMaterialCell,                      GT_Utility.getIntegratedCircuit(1), Materials.Air.getGas(14000),            new GT_FluidStack(aPolymer, 1000), Materials.Empty.getCells(1), 1120);
        addChemicalRecipe(aBasicMaterialCell,  	                   GT_Utility.getIntegratedCircuit(1), Materials.Oxygen.getGas(7000),          new GT_FluidStack(aPolymer, 1500), Materials.Empty.getCells(1), 1120);
        addMultiblockChemicalRecipe(new ItemStack[]{GT_Utility.getIntegratedCircuit(2)}, 
        		new FluidStack[]{new GT_FluidStack(aBasicMaterial, 2160), Materials.Air.getGas(15000),   Materials.Titaniumtetrachloride.getFluid(100)}, 
        		new FluidStack[]{new GT_FluidStack(aPolymer, 3240)}, null, 800, 30);
        addMultiblockChemicalRecipe(new ItemStack[]{GT_Utility.getIntegratedCircuit(2)}, 
        		new FluidStack[]{new GT_FluidStack(aBasicMaterial, 2160), Materials.Oxygen.getGas(7500), Materials.Titaniumtetrachloride.getFluid(100)}, 
        		new FluidStack[]{new GT_FluidStack(aPolymer, 4320)}, null, 800, 30);
    }

    @Override
    public boolean addChemicalRecipe(ItemStack aInput1, ItemStack aInput2, FluidStack aFluidInput, FluidStack aFluidOutput, ItemStack aOutput, int aDuration, int aEUtick) {
        return addChemicalRecipe(aInput1, aInput2, aFluidInput, aFluidOutput, aOutput, GT_Values.NI, aDuration, aEUtick);
    }

    @Override
    public boolean addChemicalRecipe(ItemStack aInput1, ItemStack aInput2, FluidStack aFluidInput, FluidStack aFluidOutput, ItemStack aOutput, ItemStack aOutput2, int aDuration, int aEUtick) {
        if (((aInput1 == null) && (aFluidInput == null)) || ((aOutput == null) && (aOutput2 == null) && (aFluidOutput == null))) {
            return false;
        }
        int tDuration = aDuration;
        if (aOutput != null) {
            tDuration = GregTech_API.sRecipeFile.get("chemicalreactor", aOutput, aDuration);
            if (tDuration <= 0) {
                return false;
            }
        }
        if (aFluidOutput != null) {
            tDuration = GregTech_API.sRecipeFile.get("chemicalreactor", aFluidOutput.getFluid().getName(), aDuration);
            if (tDuration <= 0) {
                return false;
            }
        }
        if (aEUtick <= 0) {
            return false;
        }
        GT_RecipeMap.sChemicalRecipes.addRecipe(true, new ItemStack[]{aInput1, aInput2}, new ItemStack[]{aOutput, aOutput2}, null, null, new FluidStack[]{aFluidInput}, new FluidStack[]{aFluidOutput}, tDuration, aEUtick, mAddingDeprecatedRecipes ? -300 : 0);
        if (!(aInput1 != null && aInput1.getItem() instanceof GT_IntegratedCircuit_Item && aInput1.getItemDamage() >= 10)
        		&& !(aInput2 != null && aInput2.getItem() instanceof GT_IntegratedCircuit_Item && aInput2.getItemDamage() >= 10)) {
            GT_RecipeMap.sMultiblockChemicalRecipes.addRecipe(false, new ItemStack[]{aInput1, aInput2}, new ItemStack[]{aOutput, aOutput2}, null, null, new FluidStack[]{aFluidInput}, new FluidStack[]{aFluidOutput}, tDuration, aEUtick, mAddingDeprecatedRecipes ? -300 : 0);        	
        }
        return true;
    }

    @Override
    public boolean addMultiblockChemicalRecipe(ItemStack[] aInputs, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, ItemStack[] aOutputs, int aDuration, int aEUtick) {
    	if (areItemsAndFluidsBothNull(aInputs, aFluidInputs) || areItemsAndFluidsBothNull(aOutputs, aFluidOutputs)) {
    		return false;
    	}
    	if (aEUtick <= 0) {
    		return false;
    	}
        GT_RecipeMap.sMultiblockChemicalRecipes.addRecipe(false, aInputs, aOutputs, null, null, aFluidInputs, aFluidOutputs, aDuration, aEUtick, 0);
    	return true;
    }

    @Override
    public boolean addBlastRecipe(ItemStack aInput1, ItemStack aInput2, ItemStack aOutput1, ItemStack aOutput2, int aDuration, int aEUt, int aLevel) {
        return addBlastRecipe(aInput1, aInput2, GT_Values.NF, GT_Values.NF, aOutput1, aOutput2, aDuration, aEUt, aLevel);
    }

    @Override
    public boolean addBlastRecipe(ItemStack aInput1, ItemStack aInput2, FluidStack aFluidInput, FluidStack aFluidOutput, ItemStack aOutput1, ItemStack aOutput2, int aDuration, int aEUt, int aLevel) {
        if ((aInput1 == null) || (aOutput1 == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("blastfurnace", aInput1, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sBlastRecipes.addRecipe(true, new ItemStack[]{aInput1, aInput2}, new ItemStack[]{aOutput1, aOutput2}, null, null, 
        		new FluidStack[]{aFluidInput}, new FluidStack[]{aFluidOutput}, tDuration, aEUt, aLevel);
        return true;
    }

    @Override
    public boolean addPrimitiveBlastRecipe(ItemStack aInput1, ItemStack aInput2, int aCoalAmount, ItemStack aOutput1, ItemStack aOutput2, int aDuration) {
        if ((aInput1 == null && aInput2 == null) || (aOutput1 == null && aOutput2 == null)) {
            return false;
        }
        if (aCoalAmount <= 0) {
        	return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("primitiveblastfurnace", aInput1, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        Materials[] coals = new Materials[]{Materials.Coal, Materials.Charcoal};
        for (Materials coal : coals) {
        	GT_RecipeMap.sPrimitiveBlastRecipes.addRecipe(true, new ItemStack[]{aInput1, aInput2, coal.getGems(aCoalAmount)}, new ItemStack[]{aOutput1, aOutput2, Materials.DarkAsh.getDustTiny(aCoalAmount)}, null, null, null, null, tDuration, 0, 0);
        	GT_RecipeMap.sPrimitiveBlastRecipes.addRecipe(true, new ItemStack[]{aInput1, aInput2, coal.getDust(aCoalAmount)}, new ItemStack[]{aOutput1, aOutput2, Materials.DarkAsh.getDustTiny(aCoalAmount)}, null, null, null, null, tDuration, 0, 0);
        }        
        if (Loader.isModLoaded("Railcraft")) { 
        	GT_RecipeMap.sPrimitiveBlastRecipes.addRecipe(true, new ItemStack[]{aInput1, aInput2, RailcraftToolItems.getCoalCoke(aCoalAmount / 2)}, new ItemStack[]{aOutput1, aOutput2, Materials.Ash.getDustTiny(aCoalAmount / 2)}, null, null, null, null, tDuration * 2 / 3, 0, 0);
        }
        if ((aInput1 == null || aInput1.stackSize <= 6) && (aInput2 == null || aInput2.stackSize <= 6)
                && (aOutput1 == null || aOutput1.stackSize <= 6) && (aOutput2 == null || aOutput2.stackSize <= 6)) {
            ItemStack tInput1 = aInput1 == null ? null : GT_Utility.copyAmount(aInput1.stackSize * 10, aInput1);
            ItemStack tInput2 = aInput2 == null ? null : GT_Utility.copyAmount(aInput2.stackSize * 10, aInput2);
            ItemStack tOutput1 = aOutput1 == null ? null : GT_Utility.copyAmount(aOutput1.stackSize * 10, aOutput1);
            ItemStack tOutput2 = aOutput2 == null ? null : GT_Utility.copyAmount(aOutput2.stackSize * 10, aOutput2);
            for (Materials coal : coals) {
                GT_RecipeMap.sPrimitiveBlastRecipes.addRecipe(true, new ItemStack[]{tInput1, tInput2, coal.getBlocks(aCoalAmount)}, new ItemStack[]{tOutput1, tOutput2, Materials.DarkAsh.getDust(aCoalAmount)}, null, null, null, null, tDuration * 10, 0, 0);
                GT_RecipeMap.sPrimitiveBlastRecipes.addRecipe(true, new ItemStack[]{tInput1, tInput2, coal.getBlocks(aCoalAmount)}, new ItemStack[]{tOutput1, tOutput2, Materials.DarkAsh.getDust(aCoalAmount)}, null, null, null, null, tDuration * 10, 0, 0);
            }
            if (Loader.isModLoaded("Railcraft")) {
                GT_RecipeMap.sPrimitiveBlastRecipes.addRecipe(true, new ItemStack[]{tInput1, tInput2, EnumCube.COKE_BLOCK.getItem(aCoalAmount / 2)}, new ItemStack[]{tOutput1, tOutput2, Materials.Ash.getDust(aCoalAmount / 2)}, null, null, null, null, tDuration * 20 / 3, 0, 0);
            }
        }
        return true;
    }

    @Override
    public boolean addCannerRecipe(ItemStack aInput1, ItemStack aInput2, ItemStack aOutput1, ItemStack aOutput2, int aDuration, int aEUt) {
        if ((aInput1 == null) || (aOutput1 == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("canning", aInput1, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sCannerRecipes.addRecipe(false, new ItemStack[]{aInput1, aInput2}, new ItemStack[]{aOutput1, aOutput2}, null, null, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addAlloySmelterRecipe(ItemStack aInput1, ItemStack aInput2, ItemStack aOutput1, int aDuration, int aEUt) {
        return addAlloySmelterRecipe(aInput1, aInput2, aOutput1, aDuration, aEUt, false);
    }

    @Override
    public boolean addAlloySmelterRecipe(ItemStack aInput1, ItemStack aInput2, ItemStack aOutput1, int aDuration, int aEUt, boolean hidden) {
        if ((aInput1 == null) || (aOutput1 == null || Materials.Graphite.contains(aInput1))) {
            return false;
        }
        if ((aInput2 == null) && ((OrePrefixes.ingot.contains(aInput1)) || (OrePrefixes.dust.contains(aInput1)) || (OrePrefixes.gem.contains(aInput1)))) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("alloysmelting", aInput2 == null ? aInput1 : aOutput1, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_MachineRecipe tRecipe = new GT_MachineRecipe(new ItemStack[]{aInput1, aInput2}, new ItemStack[]{aOutput1}, new FluidStack[0], new FluidStack[0]).setDuration(tDuration).setEUt(aEUt).setHidden(hidden);
        GT_RecipeMap.sAlloySmelterRecipes.addRecipe(tRecipe);
        return true;
    }

    @Override
    @Deprecated
    public boolean addCNCRecipe(ItemStack aInput1, ItemStack aOutput1, int aDuration, int aEUt) {
        // relevant machine doesn't seem to actually exist, and old recipe map for it wasn't actually invoked.
        return false;
    }

    @Override
    public boolean addAssemblerRecipe(ItemStack aInput1, ItemStack aInput2, ItemStack aOutput1, int aDuration, int aEUt) {
        if ((aInput1 == null) || (aOutput1 == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("assembling", aOutput1, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sAssemblerRecipes.addRecipe(true, new ItemStack[]{aInput1, aInput2 == null ? aInput1 : aInput2}, new ItemStack[]{aOutput1}, null, null, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addAssemblerRecipe(ItemStack aInput1, ItemStack aInput2, FluidStack aFluidInput, ItemStack aOutput1, int aDuration, int aEUt) {
        return addAssemblerRecipe(new ItemStack[]{aInput1, aInput2}, aFluidInput, aOutput1, aDuration, aEUt);
    }

    @Override
    public boolean addAssemblerRecipe(ItemStack[] aInputs, FluidStack aFluidInput, ItemStack aOutput1, int aDuration, int aEUt) {
    	if (areItemsAndFluidsBothNull(aInputs, new FluidStack[]{aFluidInput})) {
    		return false;
    	}
    	if (aOutput1 == null) {
    		return false;
    	}
        int tDuration = GregTech_API.sRecipeFile.get("assembling", aOutput1, aDuration);
    	if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sAssemblerRecipes.addRecipe(true, aInputs, new ItemStack[]{aOutput1}, null, new FluidStack[]{aFluidInput}, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addAssemblerRecipe(ItemStack aInput1, Object aOreDict, int aAmount, FluidStack aFluidInput, ItemStack aOutput1, int aDuration, int aEUt) {
        int tDuration = GregTech_API.sRecipeFile.get("assembling", aOutput1, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeInput[] tInputs = new GT_RecipeInput[]{new GT_RecipeInput(aInput1), new GT_RecipeInputOredict(aOreDict.toString(), aAmount)};
        GT_RecipeOutput[] tOutputs = new GT_RecipeOutput[]{new GT_RecipeOutput(aOutput1)};
        GT_RecipeMap.sAssemblerRecipes.addRecipe(new GT_MachineRecipe(tInputs, tOutputs, new FluidStack[]{aFluidInput}, new FluidStack[0]).setDuration(tDuration).setEUt(aEUt));
        return true;
    }

    @Override
    public boolean addAssemblerRecipe(ItemStack[] aInputs, Object aOreDict, int aAmount, FluidStack aFluidInput, ItemStack aOutput1, int aDuration, int aEUt) {
        int tDuration = GregTech_API.sRecipeFile.get("assembling", aOutput1, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeInput[] tInputs = new GT_RecipeInput[aInputs.length + 1];
        for (int i = 0; i < aInputs.length; i++) {
            tInputs[i] = new GT_RecipeInput(aInputs[i]);
        }
        tInputs[aInputs.length] = new GT_RecipeInputOredict(aOreDict.toString(), aAmount);
        GT_RecipeOutput[] tOutputs = new GT_RecipeOutput[]{new GT_RecipeOutput(aOutput1)};
        GT_RecipeMap.sAssemblerRecipes.addRecipe(new GT_MachineRecipe(tInputs, tOutputs, new FluidStack[]{aFluidInput}, new FluidStack[0]).setDuration(tDuration).setEUt(aEUt));
        return true;
    }

    @Override
    public boolean addCircuitAssemblerRecipe(ItemStack[] aInputs, FluidStack aFluidInput, ItemStack aOutput, int aDuration, int aEUt) {
        if ((aInputs == null) || (aOutput == null) || aInputs.length > 6 || aInputs.length < 1) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("circuitassembler", aOutput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sCircuitAssemblerRecipes.addRecipe(true, aInputs, new ItemStack[]{aOutput}, null, null, new FluidStack[]{aFluidInput}, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addAssemblylineRecipe(ItemStack aResearchItem, int aResearchTime, ItemStack[] aInputs, FluidStack[] aFluidInputs, ItemStack aOutput1, int aDuration, int aEUt) {
        return addAssemblylineRecipe(aResearchItem, aResearchTime, (Object[])aInputs, aFluidInputs, aOutput1, aDuration, aEUt);
    }

    @Override
    public boolean addAssemblylineRecipe(ItemStack aResearchItem, int aResearchTime, Object[] aInputs, FluidStack[] aFluidInputs, ItemStack aOutput, int aDuration, int aEUt) {
        if ((aResearchItem==null)||(aResearchTime<=0)||(aInputs == null) || (aOutput == null) || aInputs.length>15 || aInputs.length<4) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("assemblingline", aOutput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeInput[] tInputs = new GT_RecipeInput[aInputs.length];
        for (int i = 0; i < aInputs.length; i++) {
            if (aInputs[i] instanceof ItemStack) {
                tInputs[i] = new GT_RecipeInput((ItemStack)aInputs[i]);
            } else if (aInputs[i] instanceof ItemStack[]) {
                ItemStack[] tStacks = (ItemStack[])aInputs[i];
                tInputs[i] = new GT_RecipeInputAlts(Arrays.copyOf(tStacks, tStacks.length));
            } else if (aInputs[i] instanceof Object[]) {
                Object[] objs = (Object[])aInputs[i];
                if (objs.length == 2 && objs[1] instanceof Number) {
                    int tAmount = ((Number)objs[1]).intValue();
                    tInputs[i] = new GT_RecipeInputOredict(objs[0].toString(), tAmount);
                }
            } else {
                System.out.println("addAssemblingLineRecipe "+aResearchItem.getDisplayName()+" --> "+aOutput.getUnlocalizedName()+" there is some null item in that recipe");
                return false;
            }
        }
        GT_AssemblyLineRecipe tRecipe = (GT_AssemblyLineRecipe) new GT_AssemblyLineRecipe(tInputs, new GT_RecipeOutput[]{new GT_RecipeOutput(aOutput)}, aFluidInputs, new FluidStack[0]).setDuration(tDuration).setEUt(aEUt).setSpecialItems(new ItemStack[]{ItemList.Tool_DataStick.getWithName(1L, "Reads Research result", new Object[0])});
        GT_AssemblyLineRecipe.sAssemblyLineRecipes.add(tRecipe);
        GT_MachineRecipe tFakeRecipe = new GT_MachineRecipe(new ItemStack[]{aResearchItem}, new ItemStack[]{aOutput}, null, null).setSpecialItems(new ItemStack[]{ItemList.Tool_DataStick.getWithName(1L, "Writes Research result", new Object[0])}).setDuration(aResearchTime).setEUt(30).setSpecialValue(-201);
        GT_RecipeMap.sScannerFakeRecipes.addFakeRecipe(false, tFakeRecipe);
        GT_RecipeMap.sAssemblyLineVisualRecipes.addFakeRecipe(false,tRecipe);
        return true;
    }

    @Override
    public boolean addForgeHammerRecipe(ItemStack aInput1, ItemStack aOutput1, int aDuration, int aEUt) {
        if ((aInput1 == null) || (aOutput1 == null)) {
            return false;
        }
        if (!GregTech_API.sRecipeFile.get("forgehammer", aOutput1, true)) {
            return false;
        }
        GT_RecipeMap.sHammerRecipes.addRecipe(true, new ItemStack[]{aInput1}, new ItemStack[]{aOutput1}, null, null, null, aDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addWiremillRecipe(ItemStack aInput, ItemStack aOutput, int aDuration, int aEUt) {
        if ((aInput == null) || (aOutput == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("wiremill", aInput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sWiremillRecipes.addRecipe(true, new ItemStack[]{aInput}, new ItemStack[]{aOutput}, null, null, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addPolarizerRecipe(ItemStack aInput, ItemStack aOutput, int aDuration, int aEUt) {
        if ((aInput == null) || (aOutput == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("polarizer", aInput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sPolarizerRecipes.addRecipe(true, new ItemStack[]{aInput}, new ItemStack[]{aOutput}, null, null, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addBenderRecipe(ItemStack aInput, ItemStack aOutput, int aDuration, int aEUt) {
        if ((aInput == null) || (aOutput == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("polarizer", aInput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sBenderRecipes.addRecipe(true, new ItemStack[]{aInput, ItemList.Circuit_Integrated.getWithDamage(0, aInput.stackSize)}, new ItemStack[]{aOutput}, null, null, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addExtruderRecipe(ItemStack aInput, ItemStack aShape, ItemStack aOutput, int aDuration, int aEUt) {
        if ((aInput == null) || (aShape == null) || (aOutput == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("extruder", aOutput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sExtruderRecipes.addRecipe(true, new ItemStack[]{aInput, aShape}, new ItemStack[]{aOutput}, null, null, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addSlicerRecipe(ItemStack aInput, ItemStack aShape, ItemStack aOutput, int aDuration, int aEUt) {
        if ((aInput == null) || (aShape == null) || (aOutput == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("slicer", aOutput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sSlicerRecipes.addRecipe(true, new ItemStack[]{aInput, aShape}, new ItemStack[]{aOutput}, null, null, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addOreWasherRecipe(ItemStack aInput, ItemStack aOutput1, ItemStack aOutput2, ItemStack aOutput3, FluidStack aFluidInput, int aDuration, int aEUt) {
        if ((aInput == null) || (aFluidInput == null) || ((aOutput1 == null) || (aOutput2 == null) || (aOutput3 == null))) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("orewasher", aInput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sOreWasherRecipes.addRecipe(true, new ItemStack[]{aInput}, new ItemStack[]{aOutput1, aOutput2, aOutput3}, null, new FluidStack[]{aFluidInput}, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addImplosionRecipe(ItemStack aInput1, int aInput2, ItemStack aOutput1, ItemStack aOutput2) {
        if ((aInput1 == null) || (aOutput1 == null)) {
            return false;
        }
        int tInput2 = GregTech_API.sRecipeFile.get("implosion", aInput1, aInput2);
        if (tInput2 <= 0) {
            return false;
        }
        int tExplosives = Math.max(1, Math.min(tInput2, 64));
        int tGunpowder = tExplosives * 2;
        int tDynamite = Math.max(1, tExplosives / 2);
        int tTNT = Math.max(1, tExplosives / 2);
        int tITNT = Math.max(1, tExplosives / 4);
        if(tGunpowder <= 64){
        	GT_RecipeMap.sImplosionRecipes.addRecipe(true, new ItemStack[]{aInput1, ItemList.Block_Powderbarrel.get(tGunpowder, new Object[0])}, new ItemStack[]{aOutput1, aOutput2}, null, null, null, null, 20, 30, 0);
        }
        if(tDynamite <= 16){
        	GT_RecipeMap.sImplosionRecipes.addRecipe(true, new ItemStack[]{aInput1, GT_ModHandler.getIC2Item("dynamite", tDynamite, null)}, new ItemStack[]{aOutput1, aOutput2}, null, null, null, null, 20, 30, 0);
        }
        GT_RecipeMap.sImplosionRecipes.addRecipe(true, new ItemStack[]{aInput1, new ItemStack(Blocks.tnt,tTNT)}, new ItemStack[]{aOutput1, aOutput2}, null, null, null, null, 20, 30, 0);
        GT_RecipeMap.sImplosionRecipes.addRecipe(true, new ItemStack[]{aInput1, GT_ModHandler.getIC2Item("industrialTnt", tITNT, null)}, new ItemStack[]{aOutput1, aOutput2}, null, null, null, null, 20, 30, 0);
        
        return true;
    }

    @Override
    @Deprecated
    public boolean addGrinderRecipe(ItemStack aInput1, ItemStack aInput2, ItemStack aOutput1, ItemStack aOutput2, ItemStack aOutput3, ItemStack aOutput4) {
        return false;
    }

    @Override
    public boolean addDistillationTowerRecipe(FluidStack aInput, FluidStack[] aOutputs, ItemStack aOutput2, int aDuration, int aEUt) {
        if (aInput == null || aOutputs == null || aOutputs.length < 1 || aOutputs.length > 11) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("distillation", aInput.getUnlocalizedName(), aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sDistillationRecipes.addRecipe(false, null, new ItemStack[]{aOutput2}, null, new FluidStack[]{aInput}, aOutputs, tDuration, Math.max(1, aEUt), 0);
        return true;
    }

    @Override
    public boolean addSimpleArcFurnaceRecipe(ItemStack aInput, FluidStack aFluidInput, ItemStack[] aOutputs, int[] aChances, int aDuration, int aEUt) {
        // Nothing in standard GT uses this, but GTTweaker allows recipes to be added to the Oxygen Arc Furnace while excluding the Plasma Arc Furnace.
        if ((aInput == null) || (aOutputs == null) || aFluidInput == null) {
            return false;
        }
        for (ItemStack tStack : aOutputs) {
            if (tStack != null) {
                int tDuration = GregTech_API.sRecipeFile.get("arcfurnace", aInput, aDuration);
                if (tDuration <= 0) {
                    return false;
                }
                GT_RecipeMap.sArcFurnaceRecipes.addRecipe(true, new ItemStack[]{aInput}, aOutputs, null, aChances, new FluidStack[]{aFluidInput}, null, tDuration, Math.max(1, aEUt), 0);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addPlasmaArcFurnaceRecipe(ItemStack aInput, FluidStack aFluidInput, ItemStack[] aOutputs, int[] aChances, int aDuration, int aEUt) {
        if ((aInput == null) || (aOutputs == null) || aFluidInput == null) {
            return false;
        }
        for (ItemStack tStack : aOutputs) {
            if (tStack != null) {
                int tDuration = GregTech_API.sRecipeFile.get("arcfurnace", aInput, aDuration);
                if (tDuration <= 0) {
                    return false;
                }
                GT_RecipeMap.sPlasmaArcFurnaceRecipes.addRecipe(true, new ItemStack[]{aInput}, aOutputs, null, aChances, new FluidStack[]{aFluidInput}, null, tDuration, Math.max(1, aEUt), 0);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addPlasmaArcFurnaceRecipe(ItemStack aInput, FluidStack aFluidInput, ItemStack[] aOutputs, FluidStack aFluidOutput, int[] aChances, int aDuration, int aEUt) {
        if ((aInput == null) || (aOutputs == null) || aFluidInput == null) {
            return false;
        }
        for (ItemStack tStack : aOutputs) {
            if (tStack != null) {
                int tDuration = GregTech_API.sRecipeFile.get("arcfurnace", aInput, aDuration);
                if (tDuration <= 0) {
                    return false;
                }
                GT_RecipeMap.sPlasmaArcFurnaceRecipes.addRecipe(true, new ItemStack[]{aInput}, aOutputs, null, aChances, new FluidStack[]{aFluidInput}, new FluidStack[]{aFluidOutput}, tDuration, Math.max(1, aEUt), 0);
                return true;
            }
        }
        return false;
    }

    @Override
    @Deprecated
    public boolean addDistillationRecipe(ItemStack aInput1, int aInput2, ItemStack aOutput1, ItemStack aOutput2, ItemStack aOutput3, ItemStack aOutput4, int aDuration, int aEUt) {
        return false;
    }

    @Override
    public boolean addLatheRecipe(ItemStack aInput1, ItemStack aOutput1, ItemStack aOutput2, int aDuration, int aEUt) {
        if ((aInput1 == null) || (aOutput1 == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("lathe", aInput1, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sLatheRecipes.addRecipe(true, new ItemStack[]{aInput1}, new ItemStack[]{aOutput1, aOutput2}, null, new FluidStack[0], new FluidStack[0], tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addCutterRecipe(ItemStack aInput, FluidStack aLubricant, ItemStack aOutput1, ItemStack aOutput2, int aDuration, int aEUt) {
        if ((aInput == null) || (aLubricant == null) || (aOutput1 == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("cutting", aInput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sCutterRecipes.addRecipe(true, new ItemStack[]{aInput}, new ItemStack[]{aOutput1, aOutput2}, null, new FluidStack[]{aLubricant}, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addCutterRecipe(ItemStack aInput, ItemStack aOutput1, ItemStack aOutput2, int aDuration, int aEUt) {
        if ((aInput == null) || (aOutput1 == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("cutting", aInput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sCutterRecipes.addRecipe(true, new ItemStack[]{aInput}, new ItemStack[]{aOutput1, aOutput2}, null, new FluidStack[]{Materials.Water.getFluid(Math.max(4, Math.min(1000, tDuration * aEUt / 320)))}, null, tDuration * 2, aEUt, 0);
        GT_RecipeMap.sCutterRecipes.addRecipe(true, new ItemStack[]{aInput}, new ItemStack[]{aOutput1, aOutput2}, null, new FluidStack[]{GT_ModHandler.getDistilledWater(Math.max(3, Math.min(750, tDuration * aEUt / 426)))}, null, tDuration * 2, aEUt, 0);
        GT_RecipeMap.sCutterRecipes.addRecipe(true, new ItemStack[]{aInput}, new ItemStack[]{aOutput1, aOutput2}, null, new FluidStack[]{Materials.Lubricant.getFluid(Math.max(1, Math.min(250, tDuration * aEUt / 1280)))}, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addBoxingRecipe(ItemStack aContainedItem, ItemStack aEmptyBox, ItemStack aFullBox, int aDuration, int aEUt) {
        if ((aContainedItem == null) || (aFullBox == null)) {
            return false;
        }
        if (!GregTech_API.sRecipeFile.get("boxing", aFullBox, true)) {
            return false;
        }
        GT_RecipeMap.sBoxinatorRecipes.addRecipe(true, new ItemStack[]{aContainedItem, aEmptyBox}, new ItemStack[]{aFullBox}, null, null, null, aDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addThermalCentrifugeRecipe(ItemStack aInput, ItemStack aOutput1, ItemStack aOutput2, ItemStack aOutput3, int aDuration, int aEUt) {
        if ((aInput == null) || (aOutput1 == null)) {
            return false;
        }
        if (!GregTech_API.sRecipeFile.get("thermalcentrifuge", aInput, true)) {
            return false;
        }
        GT_RecipeMap.sThermalCentrifugeRecipes.addRecipe(true, new ItemStack[]{aInput}, new ItemStack[]{aOutput1, aOutput2, aOutput3}, null, null, null, aDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addUnboxingRecipe(ItemStack aFullBox, ItemStack aContainedItem, ItemStack aEmptyBox, int aDuration, int aEUt) {
        if ((aFullBox == null) || (aContainedItem == null)) {
            return false;
        }
        if (!GregTech_API.sRecipeFile.get("unboxing", aFullBox, true)) {
            return false;
        }
        GT_RecipeMap.sUnboxinatorRecipes.addRecipe(true, new ItemStack[]{aFullBox}, new ItemStack[]{aContainedItem, aEmptyBox}, null, null, null, aDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addVacuumFreezerRecipe(ItemStack aInput1, ItemStack aOutput1, int aDuration) {
        if ((aInput1 == null) || (aOutput1 == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("vacuumfreezer", aInput1, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sVacuumRecipes.addRecipe(true, new ItemStack[]{aInput1}, new ItemStack[]{aOutput1}, null, new FluidStack[0], new FluidStack[0], tDuration, 120, 0);
        return true;
    }

    @Override
    public boolean addFuel(ItemStack aInput1, ItemStack aOutput1, int aEU, int aType) {
        if (aInput1 == null || aEU <= 0) {
            return false;
        }
        GT_RecipeMap[] tFuelTypeMaps = {
            GT_RecipeMap.sDieselFuels,
            GT_RecipeMap.sTurbineFuels,
            GT_RecipeMap.sHotFuels,
            GT_RecipeMap.sDenseLiquidFuels,
            GT_RecipeMap.sPlasmaFuels,
            GT_RecipeMap.sMagicFuels
        };
        int tType = aType;
        if (tType < 0 || tType > 5) {
            tType = 3;
        }
        GT_MachineRecipe tRecipe = new GT_MachineRecipe( new ItemStack[]{aInput1}, new ItemStack[]{aOutput1}, new FluidStack[0], new FluidStack[0]).setSpecialValue(aEU);
        tFuelTypeMaps[tType].addRecipe(tRecipe);
        if (tType == 0) {
            GT_RecipeMap.sLargeBoilerFakeFuels.addDieselRecipe(tRecipe);
        } else if (tType == 3) {
            GT_RecipeMap.sLargeBoilerFakeFuels.addDenseLiquidRecipe(tRecipe);
        }
        return true;
    }

    @Override
    public boolean addAmplifier(ItemStack aAmplifierItem, int aDuration, int aAmplifierAmountOutputted) {
        if ((aAmplifierItem == null) || (aAmplifierAmountOutputted <= 0)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("amplifier", aAmplifierItem, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sAmplifiers.addRecipe(true, new ItemStack[]{aAmplifierItem}, null, null, null, new FluidStack[]{Materials.UUAmplifier.getFluid(aAmplifierAmountOutputted)}, tDuration, 32, 0);
        return true;
    }

    @Override
    public boolean addBrewingRecipe(ItemStack aIngredient, Fluid aInput, Fluid aOutput, boolean aHidden) {
        if ((aIngredient == null) || (aInput == null) || (aOutput == null)) {
            return false;
        }
        if (!GregTech_API.sRecipeFile.get("brewing", aOutput.getUnlocalizedName(), true)) {
            return false;
        }
        GT_MachineRecipe tRecipe = GT_RecipeMap.sBrewingRecipes.addRecipe(false, new ItemStack[]{aIngredient}, null, null, new FluidStack[]{new FluidStack(aInput, 750)}, new FluidStack[]{new FluidStack(aOutput, 750)}, 128, 4, 0);
        if ((aHidden) && (tRecipe != null)) {
            tRecipe.mHidden = true;
        }
        return true;
    }

    @Override
    public boolean addFermentingRecipe(FluidStack aInput, FluidStack aOutput, int aDuration, boolean aHidden) {
        if ((aInput == null) || (aOutput == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("fermenting", aOutput.getFluid().getUnlocalizedName(), aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_MachineRecipe tRecipe = GT_RecipeMap.sFermentingRecipes.addRecipe(false, null, null, null, new FluidStack[]{aInput}, new FluidStack[]{aOutput}, tDuration, 2, 0);
        if ((aHidden) && (tRecipe != null)) {
            tRecipe.mHidden = true;
        }
        return true;
    }

    @Override
    public boolean addFluidHeaterRecipe(ItemStack aCircuit, FluidStack aInput, FluidStack aOutput, int aDuration, int aEUt) {
        if ((aInput == null) || (aOutput == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("fluidheater", aOutput.getFluid().getUnlocalizedName(), aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sFluidHeaterRecipes.addRecipe(true, new ItemStack[]{aCircuit}, null, null, new FluidStack[]{aInput}, new FluidStack[]{aOutput}, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addDistilleryRecipe(ItemStack aCircuit, FluidStack aInput, FluidStack aOutput, ItemStack aSolidOutput, int aDuration, int aEUt, boolean aHidden) {
        if ((aInput == null) || (aOutput == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("distillery", aOutput.getFluid().getUnlocalizedName(), aDuration);
        if (tDuration <= 0) {
            return false;
        }
        //reduce the batch size if fluid amount is exceeding 
        int tScale = (Math.max(aInput.amount, aOutput.amount) + 999) / 1000;
        if (tScale <= 0) {
            tScale = 1;
        }
        FluidStack tInput = aInput;
        FluidStack tOutput = aOutput;
        ItemStack tSolidOutput = aSolidOutput;
        if (tScale > 1){
        	//trying to find whether there is a better factor
        	for (int i = tScale; i <= 5; i++) {
        		if (aInput.amount % i == 0 && tDuration % i == 0) {
        			tScale = i;
        			break;
        		}
        	}
        	for (int i = tScale; i <= 5; i++) {
        		if (aInput.amount % i == 0 && tDuration % i == 0 && aOutput.amount % i == 0) {
        			tScale = i;
        			break;
        		}
        	}
                tInput = new FluidStack(aInput.getFluid(), (aInput.amount + tScale - 1) / tScale);
                tOutput = new FluidStack(aOutput.getFluid(), aOutput.amount / tScale);
                tSolidOutput = aSolidOutput;
        	if (tSolidOutput != null) {
        		ItemData tData = GT_OreDictUnificator.getItemData(tSolidOutput);
        		if (tData != null && (tData.mPrefix == OrePrefixes.dust || OrePrefixes.dust.mFamiliarPrefixes.contains(tData.mPrefix))) {
                            tSolidOutput = GT_OreDictUnificator.getDust(tData.mMaterial.mMaterial, tData.mMaterial.mAmount * tSolidOutput.stackSize / tScale);
                        } else {
        			if (tSolidOutput.stackSize / tScale == 0) {
                                    tSolidOutput = GT_Values.NI;
                                } else {
                                    tSolidOutput = new ItemStack(aSolidOutput.getItem(), aSolidOutput.stackSize / tScale);
                                }
        		}
        	}
        	tDuration = (tDuration + tScale - 1) / tScale;
        }
        GT_MachineRecipe tRecipe = GT_RecipeMap.sDistilleryRecipes.addRecipe(true, new ItemStack[]{aCircuit}, new ItemStack[]{tSolidOutput}, null, new FluidStack[]{tInput}, new FluidStack[]{tOutput}, tDuration, aEUt, 0);
        if ((aHidden) && (tRecipe != null)) {
            tRecipe.mHidden = true;
        }
        return true;
    }

    @Override
    public boolean addDistilleryRecipe(ItemStack aCircuit, FluidStack aInput, FluidStack aOutput, int aDuration, int aEUt, boolean aHidden) {
    	return addDistilleryRecipe(aCircuit, aInput, aOutput, null, aDuration, aEUt, aHidden);
    }

    @Override
    public boolean addDistilleryRecipe(int circuitConfig, FluidStack aInput, FluidStack aOutput, ItemStack aSolidOutput, int aDuration, int aEUt, boolean aHidden) {
    	return addDistilleryRecipe(GT_Utility.getIntegratedCircuit(circuitConfig), aInput, aOutput, aSolidOutput, aDuration, aEUt, aHidden);
    }

    @Override
    public boolean addDistilleryRecipe(int circuitConfig, FluidStack aInput, FluidStack aOutput, int aDuration, int aEUt, boolean aHidden) {
    	return addDistilleryRecipe(GT_Utility.getIntegratedCircuit(circuitConfig), aInput, aOutput, aDuration, aEUt, aHidden);
    }

    @Override
    public boolean addFluidSolidifierRecipe(ItemStack aMold, FluidStack aInput, ItemStack aOutput, int aDuration, int aEUt) {
        if ((aMold == null) || (aInput == null) || (aOutput == null)) {
            return false;
        }
        FluidStack tInput = aInput;
        if (aInput.isFluidEqual(Materials.PhasedGold.getMolten(144))) {
            tInput = Materials.VibrantAlloy.getMolten(aInput.amount);
        }
        if (aInput.isFluidEqual(Materials.PhasedIron.getMolten(144))) {
            tInput = Materials.PulsatingIron.getMolten(aInput.amount);
        }
        int tDuration = GregTech_API.sRecipeFile.get("fluidsolidifier", aOutput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sFluidSolidificationRecipes.addRecipe(true, new ItemStack[]{aMold}, new ItemStack[]{aOutput}, null, new FluidStack[]{tInput}, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addFluidSmelterRecipe(ItemStack aInput, ItemStack aRemains, FluidStack aOutput, int aChance, int aDuration, int aEUt) {
        return addFluidSmelterRecipe(aInput, aRemains, aOutput, aChance, aDuration, aEUt, false);
    }

    @Override
    public boolean addFluidSmelterRecipe(ItemStack aInput, ItemStack aRemains, FluidStack aOutput, int aChance, int aDuration, int aEUt, boolean hidden) {
        if ((aInput == null) || (aOutput == null)) {
            return false;
        }
        FluidStack tOutput = aOutput;
        if (aOutput.isFluidEqual(Materials.PhasedGold.getMolten(1))) {
            tOutput = Materials.VibrantAlloy.getMolten(aOutput.amount);
        }
        if (aOutput.isFluidEqual(Materials.PhasedIron.getMolten(1))) {
            tOutput = Materials.PulsatingIron.getMolten(aOutput.amount);
        }
        int tDuration = GregTech_API.sRecipeFile.get("fluidsmelter", aInput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_MachineRecipe tRecipe = GT_RecipeMap.sFluidExtractionRecipes.addRecipe(true, new ItemStack[]{aInput}, new ItemStack[]{aRemains}, null, new int[]{aChance}, null, new FluidStack[]{tOutput}, tDuration, aEUt, 0);
        if ((hidden) && (tRecipe != null)) {
           tRecipe.mHidden = true;
        }
        return true;
    }

    @Override
    public boolean addFluidExtractionRecipe(ItemStack aInput, ItemStack aRemains, FluidStack aOutput, int aChance, int aDuration, int aEUt) {
        return addFluidSmelterRecipe(aInput, aRemains, aOutput, aChance, aDuration, aEUt, false);
    }

    @Override
    public boolean addFluidCannerRecipe(ItemStack aInput, ItemStack aOutput, FluidStack aFluidInput, FluidStack aFluidOutput) {
        if ((aInput != null) && (aOutput != null)) {
            if ((aFluidInput == null) == (aFluidOutput == null)) {
                return false;
            }
        } else {
            return false;
        }
        if (!GregTech_API.sRecipeFile.get("fluidcanner", aOutput, true)) {
            return false;
        }
        GT_RecipeMap.sFluidCannerRecipes.addRecipe(true, new ItemStack[]{aInput}, new ItemStack[]{aOutput}, null, new FluidStack[]{aFluidInput}, new FluidStack[]{aFluidOutput}, aFluidOutput == null ? aFluidInput.amount / 62 : aFluidOutput.amount / 62, 1, 0);
        return true;
    }

    @Override
    public boolean addChemicalBathRecipe(ItemStack aInput, FluidStack aBathingFluid, ItemStack aOutput1, ItemStack aOutput2, ItemStack aOutput3, int[] aChances, int aDuration, int aEUt) {
        if ((aInput == null) || (aBathingFluid == null) || (aOutput1 == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("chemicalbath", aInput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sChemicalBathRecipes.addRecipe(true, new ItemStack[]{aInput}, new ItemStack[]{aOutput1, aOutput2, aOutput3}, null, aChances, new FluidStack[]{aBathingFluid}, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addElectromagneticSeparatorRecipe(ItemStack aInput, ItemStack aOutput1, ItemStack aOutput2, ItemStack aOutput3, int[] aChances, int aDuration, int aEUt) {
        if ((aInput == null) || (aOutput1 == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("electromagneticseparator", aInput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sElectroMagneticSeparatorRecipes.addRecipe(true, new ItemStack[]{aInput}, new ItemStack[]{aOutput1, aOutput2, aOutput3}, null, aChances, null, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addExtractorRecipe(ItemStack aInput, ItemStack aOutput, int aDuration, int aEUt) {
        if ((aInput == null) || (aOutput == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("extractor", aInput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sExtractorRecipes.addRecipe(true, new ItemStack[]{aInput}, new ItemStack[]{aOutput}, null, null, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addPrinterRecipe(ItemStack aInput, FluidStack aFluid, ItemStack aSpecialSlot, ItemStack aOutput, int aDuration, int aEUt) {
        if ((aInput == null) || (aFluid == null) || (aOutput == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("printer", aInput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sPrinterRecipes.addRecipe(true, new ItemStack[]{aInput}, new ItemStack[]{aOutput}, aSpecialSlot, null, new FluidStack[]{aFluid}, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addAutoclaveRecipe(ItemStack aInput, FluidStack aFluid, ItemStack aOutput, int aChance, int aDuration, int aEUt) {
    	return addAutoclaveRecipe(aInput, aFluid, aOutput, aChance, aDuration, aEUt, false);
    }

    @Override
    public boolean addAutoclaveRecipe(ItemStack aInput, FluidStack aFluid, ItemStack aOutput, int aChance, int aDuration, int aEUt, boolean aCleanroom) {
        if ((aInput == null) || (aFluid == null) || (aOutput == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("autoclave", aInput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        boolean tCleanroom = aCleanroom;
        if (!GT_Mod.gregtechproxy.mEnableCleanroom) {
            tCleanroom = false;
        }
        GT_RecipeMap.sAutoclaveRecipes.addRecipe(true, new ItemStack[]{aInput}, new ItemStack[]{aOutput}, null, new int[]{aChance}, new FluidStack[]{aFluid}, null, tDuration, aEUt, tCleanroom ? -100 : 0);
        return true;
    }

    @Override
    public boolean addMixerRecipe(ItemStack aInput1, ItemStack aInput2, ItemStack aInput3, ItemStack aInput4, FluidStack aFluidInput, FluidStack aFluidOutput, ItemStack aOutput, int aDuration, int aEUt) {
        if (((aInput1 == null) && (aFluidInput == null)) || ((aOutput == null) && (aFluidOutput == null))) {
            return false;
        }
        int tDuration = aDuration;
        if (aOutput != null) {
            tDuration = GregTech_API.sRecipeFile.get("mixer", aOutput, aDuration);
            if (tDuration <= 0) {
                return false;
            }
        }
        if (aFluidOutput != null) {
            tDuration = GregTech_API.sRecipeFile.get("mixer", aFluidOutput.getFluid().getName(), aDuration);
            if (tDuration <= 0) {
                return false;
            }
        }
        GT_RecipeMap.sMixerRecipes.addRecipe(true, new ItemStack[]{aInput1, aInput2, aInput3, aInput4}, new ItemStack[]{aOutput}, null, null, new FluidStack[]{aFluidInput}, new FluidStack[]{aFluidOutput}, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addLaserEngraverRecipe(ItemStack aItemToEngrave, ItemStack aLens, ItemStack aEngravedItem, int aDuration, int aEUt) {
    	return addLaserEngraverRecipe( aItemToEngrave, aLens, aEngravedItem, aDuration, aEUt, false);
    }

    @Override
    public boolean addLaserEngraverRecipe(ItemStack aItemToEngrave, ItemStack aLens, ItemStack aEngravedItem, int aDuration, int aEUt, boolean aCleanroom) {
        if ((aItemToEngrave == null) || (aLens == null) || (aEngravedItem == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("laserengraving", aEngravedItem, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        boolean tCleanroom = aCleanroom;
        if (!GT_Mod.gregtechproxy.mEnableCleanroom) {
            tCleanroom = false;
        }
        GT_RecipeMap.sLaserEngraverRecipes.addRecipe(true, new ItemStack[]{aItemToEngrave, aLens}, new ItemStack[]{aEngravedItem}, null, null, null, tDuration, aEUt, tCleanroom ? -200 : 0);
        return true;
    }

    @Override
    public boolean addFormingPressRecipe(ItemStack aItemToImprint, ItemStack aForm, ItemStack aImprintedItem, int aDuration, int aEUt) {
        if ((aItemToImprint == null) || (aForm == null) || (aImprintedItem == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("press", aImprintedItem, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sPressRecipes.addRecipe(true, new ItemStack[]{aItemToImprint, aForm}, new ItemStack[]{aImprintedItem}, null, null, null, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addSifterRecipe(ItemStack aItemToSift, ItemStack[] aSiftedItems, int[] aChances, int aDuration, int aEUt) {
        if ((aItemToSift == null) || (aSiftedItems == null)) {
            return false;
        }
        for (ItemStack tStack : aSiftedItems) {
            if (tStack != null) {
                int tDuration = GregTech_API.sRecipeFile.get("sifter", aItemToSift, aDuration);
                if (tDuration <= 0) {
                    return false;
                }
                GT_RecipeMap.sSifterRecipes.addRecipe(true, new ItemStack[]{aItemToSift}, aSiftedItems, null, aChances, null, null, tDuration, aEUt, 0);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addArcFurnaceRecipe(ItemStack aInput, ItemStack[] aOutputs, int[] aChances, int aDuration, int aEUt) {
        return addArcFurnaceRecipe(aInput, aOutputs, aChances, aDuration, aEUt,	false);
    }

    @Override
    public boolean addArcFurnaceRecipe(ItemStack aInput, ItemStack[] aOutputs, int[] aChances, int aDuration, int aEUt, boolean hidden) {
        if ((aInput == null) || (aOutputs == null)) {
            return false;
        }
        for (ItemStack tStack : aOutputs) {
            if (tStack != null) {
                int tDuration = GregTech_API.sRecipeFile.get("arcfurnace", aInput, aDuration);
                if (tDuration <= 0) {
                    return false;
                }
                GT_MachineRecipe sRecipe = GT_RecipeMap.sArcFurnaceRecipes.addRecipe(true, new ItemStack[]{aInput}, aOutputs, null, aChances, new FluidStack[]{Materials.Oxygen.getGas(aDuration)}, null, tDuration, Math.max(1, aEUt), 0);
                if ((hidden) && (sRecipe != null)) {
                   sRecipe.mHidden = true;
                }
                for (Materials tMaterial : new Materials[]{Materials.Argon, Materials.Nitrogen}) {
                    if (tMaterial.mPlasma != null) {
                        int tPlasmaAmount = (int) Math.max(1L, tDuration / (tMaterial.getMass() * 16L));
                        GT_MachineRecipe tRecipe = GT_RecipeMap.sPlasmaArcFurnaceRecipes.addRecipe(true, new ItemStack[]{aInput}, aOutputs, null, aChances, new FluidStack[]{tMaterial.getPlasma(tPlasmaAmount)}, new FluidStack[]{tMaterial.getGas(tPlasmaAmount)}, Math.max(1, tDuration / 16), Math.max(1, aEUt / 3), 0);
                        if ((hidden) && (tRecipe != null)) {
                           tRecipe.mHidden = true;
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addPulveriserRecipe(ItemStack aInput, ItemStack[] aOutputs, int[] aChances, int aDuration, int aEUt) {
        return addPulveriserRecipe(aInput, aOutputs, aChances, aDuration, aEUt, false);
    }

    @Override
    public boolean addPulveriserRecipe(ItemStack aInput, ItemStack[] aOutputs, int[] aChances, int aDuration, int aEUt, boolean hidden) {
        if ((aInput == null) || (aOutputs == null)) {
            return false;
        }
        for (ItemStack tStack : aOutputs) {
            if (tStack != null) {
                int tDuration = GregTech_API.sRecipeFile.get("pulveriser", aInput, aDuration);
                if (tDuration <= 0) {
                    return false;
                }
                GT_MachineRecipe tRecipe = GT_RecipeMap.sMaceratorRecipes.addRecipe(true, new ItemStack[]{aInput}, aOutputs, null, aChances, null, null, tDuration, aEUt, 0);
                if ((hidden) && (tRecipe != null)) {
                   tRecipe.mHidden = true;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean addUniversalDistillationRecipe(FluidStack aInput, FluidStack[] aOutputs, ItemStack aOutput2, int aDuration, int aEUt) {
    	for (int i = 0; i < Math.min(aOutputs.length, 11); i++) {
    		addDistilleryRecipe(i + 1, aInput, aOutputs[i], aOutput2, aDuration * 2, aEUt / 4, false);
    	}
        return addDistillationTowerRecipe(aInput, aOutputs, aOutput2, aDuration, aEUt);
    }

    @Override
    public boolean addPyrolyseRecipe(ItemStack aInput, FluidStack aFluidInput, int intCircuit, ItemStack aOutput, FluidStack aFluidOutput, int aDuration, int aEUt) {
        if (aInput == null) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("pyrolyse", aInput, aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sPyrolyseRecipes.addRecipe(false, new ItemStack[]{aInput, ItemList.Circuit_Integrated.getWithDamage(0L, intCircuit, new Object[0])}, new ItemStack[]{aOutput}, null, null, new FluidStack[]{aFluidInput}, new FluidStack[]{aFluidOutput}, tDuration, aEUt, 0);
        return true;
    }

    @Override
    @Deprecated
    public boolean addCrackingRecipe(FluidStack aInput, FluidStack aOutput, int aDuration, int aEUt) {
        return false;
    }

    @Override
    public boolean addCrackingRecipe(int circuitConfig, FluidStack aInput, FluidStack aInput2, FluidStack aOutput, int aDuration, int aEUt) {
        if (aInput == null || aInput2 == null || (aOutput == null)) {
            return false;
        }
        int tDuration = GregTech_API.sRecipeFile.get("cracking", aInput.getUnlocalizedName(), aDuration);
        if (tDuration <= 0) {
            return false;
        }
        GT_RecipeMap.sCrackingRecipes.addRecipe(false, new ItemStack[]{GT_Utility.getIntegratedCircuit(circuitConfig)}, null, null, null, 
        		new FluidStack[]{aInput, aInput2}, new FluidStack[]{aOutput}, tDuration, aEUt, 0);
        return true;
    }

    @Override
    public boolean addSonictronSound(ItemStack aItemStack, String aSoundName) {
        // Unclear if this is fully implemented; no usages found.
        if ((aItemStack == null) || (aSoundName == null) || (aSoundName.isEmpty())) {
            return false;
        }
        GT_Mod.gregtechproxy.mSoundItems.add(aItemStack);
        GT_Mod.gregtechproxy.mSoundNames.add(aSoundName);
        if (aSoundName.startsWith("note.")) {
            GT_Mod.gregtechproxy.mSoundCounts.add(25);
        } else {
            GT_Mod.gregtechproxy.mSoundCounts.add(1);
        }
        return true;
    }

    private boolean mAddingDeprecatedRecipes = false;
    
    private boolean areItemsAndFluidsBothNull(ItemStack[] items, FluidStack[] fluids) {
        boolean itemsNull = true;
        if (items != null) {
            for (ItemStack itemStack : items) {
                if (itemStack != null) {
                    itemsNull = false;
                    break;
                }
            }
        }
        boolean fluidsNull = true;
        if (fluids != null) {
            for (FluidStack fluidStack : fluids) {
                if (fluidStack != null) {
                    fluidsNull = false;
                    break;
                }
            }
        }
        return itemsNull && fluidsNull;
    }

    @Override
    public boolean isAddingDeprecatedRecipes() {
        return mAddingDeprecatedRecipes;
    }

    @Override
    public void setIsAddingDeprecatedRecipes(boolean isAddingDeprecatedRecipes) {
        mAddingDeprecatedRecipes = isAddingDeprecatedRecipes;
    }
    
}
