package gregtech.api.recipes;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SubTag;
import gregtech.api.util.GT_Log;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

/**
 * 
 * Reads a list of GregTech machine recipes from JSON.
 */
public class GT_RecipeListJsonReader {

    private GT_RecipeListJsonReader() {
        // Utility class, private constructor.
    }
    
    private static class GT_Json_Recipe extends GT_Recipe {
        GT_Json_Recipe(boolean aOptimize, ItemStack[] aInputs, ItemStack[] aOutputs, Object aSpecialItems, int[] aChances, FluidStack[] aFluidInputs, FluidStack[] aFluidOutputs, int aDuration, int aEUt, int aSpecialValue) {
            super(aOptimize, aInputs, aOutputs, aSpecialItems, aChances, aFluidInputs, aFluidOutputs, aDuration, aEUt, aSpecialValue);
        }
    }
    
    private static class ItemStackWithChance {
        public ItemStack mStack;
        public float mChance;
        ItemStackWithChance(ItemStack aStack, float aChance) {
            mStack = aStack;
            mChance = aChance;
        }
    }
    
    private static ItemStack[] getOutputItems(List<ItemStackWithChance> aList) {
        ItemStack[] rItems = new ItemStack[aList.size()];
        for (int i = 0; i < aList.size(); i++) {
            if (aList.get(i) != null) {
                rItems[i] = aList.get(i).mStack;
            } else {
                rItems[i] = null;
            }
        }
        return rItems;
    }
    
    private static int[] getOutputChancesAsInts(List<ItemStackWithChance> aList) {
        int[] rChances = new int[aList.size()];
        for (int i = 0; i < aList.size(); i++) {
            if (aList.get(i) != null) {
                rChances[i] = (int)(aList.get(i).mChance * 10000);
            } else {
                rChances[i] = 0;
            }
        }
        return rChances;
    }
    
    public static List<GT_Recipe> readRecipes(JsonReader aReader) {
        aReader.setLenient(true);
        List<GT_Recipe> rList = new ArrayList<>(100);
        try {
            aReader.beginArray();
            while (aReader.hasNext()) {
                List<GT_Recipe> tRecipe = readSingleRecipe(aReader);
                if (tRecipe != null) {
                    rList.addAll(tRecipe);
                }
            }
            aReader.endArray();
        } catch (IOException e) {
            e.printStackTrace(GT_Log.err);
        }
        return rList;
    }

    // Reads one recipe object from the json, but some convenience features may cause a single json recipe
    // to translate into multiple GT_Recipe objects.
    private static List<GT_Recipe> readSingleRecipe(JsonReader aReader) throws IOException {
        List<ItemStack> tInputs = new ArrayList<>(10);
        List<ItemStackWithChance> tOutputs = new ArrayList<>(10);
        List<FluidStack> tFluidInputs = new ArrayList<>(10);
        List<FluidStack> tFluidOutputs = new ArrayList<>(10);
        List<FluidStack> tFluidsPerCircuit = new ArrayList<>(10);
        int tDuration = 0;
        int tEUt = 0;
        int tSpecialValue = 0;
        boolean tEnabled = true;
        String tEnableCondition = null;
        boolean tInvertCondition = false;
        boolean tHidden = false;
        boolean tUseSolderFluids = false;
        boolean tUseCuttingFluids = false;
        boolean tOptimize = true;
        int tSolderAmount = 0;
        aReader.beginObject();
        while (aReader.hasNext()) {
            String tName = aReader.nextName();
            switch (tName) {
                case "inputs":
                    aReader.beginArray();
                    while (aReader.hasNext()) {
                        tInputs.add(readInput(aReader));
                    }
                    aReader.endArray();
                    break;
                case "outputs":
                    aReader.beginArray();
                    while (aReader.hasNext()) {
                        tOutputs.add(readOutput(aReader));
                    }
                    aReader.endArray();
                    break;
                case "inputFluids":
                case "fluidInputs":
                    aReader.beginArray();
                    while (aReader.hasNext()) {
                        String tFluidName = null;
                        int tFluidAmount = 0;
                        FluidStack tMaterialFluid = null;
                        aReader.beginObject();
                        while (aReader.hasNext()) {
                            String tSubName = aReader.nextName();
                            if ("fluid".equals(tSubName)) {
                                tFluidName = aReader.nextString();
                            } else if ("amount".equals(tSubName)) {
                                tFluidAmount = aReader.nextInt();
                            } else if ("solderFluids".equals(tSubName)) {
                                tUseSolderFluids = true;
                                tSolderAmount = aReader.nextInt();
                            } else if ("gas".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tMaterialFluid = Materials.get(tMaterialName).getGas(1L);
                            } else if ("plasma".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tMaterialFluid = Materials.get(tMaterialName).getPlasma(1L);
                            } else if ("molten".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tMaterialFluid = Materials.get(tMaterialName).getMolten(1L);
                            } else if ("materialFluid".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tMaterialFluid = Materials.get(tMaterialName).getFluid(1L);
                            }
                        }
                        aReader.endObject();
                        if (!tUseSolderFluids) {
                            if (tMaterialFluid != null) {
                                tMaterialFluid.amount = tFluidAmount;
                                tFluidInputs.add(tMaterialFluid);
                            } else if (tFluidName != null) {
                                if ("ic2distilledwater".equals(tFluidName)) {
                                    tFluidInputs.add(GT_ModHandler.getDistilledWater(tFluidAmount));
                                } else if (FluidRegistry.isFluidRegistered(tFluidName)) {
                                    tFluidInputs.add(new FluidStack(FluidRegistry.getFluid(tFluidName), tFluidAmount));
                                } else {
                                    GT_Log.err.println("Unregistered fluid used in recipe: " + tFluidName);
                                }
                            }
                        }
                    }
                    aReader.endArray();
                    break;
                case "outputFluids":
                case "fluidOutputs":
                    aReader.beginArray();
                    while (aReader.hasNext()) {
                        String tFluidName = null;
                        int tFluidAmount = 0;
                        FluidStack tMaterialFluid = null;
                        aReader.beginObject();
                        while (aReader.hasNext()) {
                            String tSubName = aReader.nextName();
                            if ("fluid".equals(tSubName)) {
                                tFluidName = aReader.nextString();
                            } else if ("amount".equals(tSubName)) {
                                tFluidAmount = aReader.nextInt();
                            } else if ("gas".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tMaterialFluid = Materials.get(tMaterialName).getGas(1L);
                            } else if ("plasma".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tMaterialFluid = Materials.get(tMaterialName).getPlasma(1L);
                            } else if ("molten".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tMaterialFluid = Materials.get(tMaterialName).getMolten(1L);
                            } else if ("materialFluid".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tMaterialFluid = Materials.get(tMaterialName).getFluid(1L);
                            }
                        }
                        aReader.endObject();
                        if (tMaterialFluid != null) {
                            tMaterialFluid.amount = tFluidAmount;
                            tFluidOutputs.add(tMaterialFluid);
                        } else if (tFluidName != null) {
                            if ("ic2distilledwater".equals(tFluidName)) {
                                tFluidOutputs.add(GT_ModHandler.getDistilledWater(tFluidAmount));
                            } else if (FluidRegistry.isFluidRegistered(tFluidName)) {
                                tFluidOutputs.add(new FluidStack(FluidRegistry.getFluid(tFluidName), tFluidAmount));
                            } else {
                                GT_Log.err.println("Unregistered fluid used in recipe: " + tFluidName);
                            }
                        }
                    }
                    aReader.endArray();
                    break;
                case "fluidsPerCircuit":
                    aReader.beginArray();
                    while (aReader.hasNext()) {
                        String tFluidName = null;
                        int tFluidAmount = 0;
                        FluidStack tMaterialFluid = null;
                        aReader.beginObject();
                        while (aReader.hasNext()) {
                            String tSubName = aReader.nextName();
                            if ("fluid".equals(tSubName)) {
                                tFluidName = aReader.nextString();
                            } else if ("amount".equals(tSubName)) {
                                tFluidAmount = aReader.nextInt();
                            } else if ("gas".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tMaterialFluid = Materials.get(tMaterialName).getGas(1L);
                            } else if ("plasma".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tMaterialFluid = Materials.get(tMaterialName).getPlasma(1L);
                            } else if ("molten".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tMaterialFluid = Materials.get(tMaterialName).getMolten(1L);
                            } else if ("materialFluid".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tMaterialFluid = Materials.get(tMaterialName).getFluid(1L);
                            }
                        }
                        aReader.endObject();
                        if (tMaterialFluid != null) {
                            tMaterialFluid.amount = tFluidAmount;
                            tFluidsPerCircuit.add(tMaterialFluid);
                        } else if (tFluidName != null) {
                            if ("ic2distilledwater".equals(tFluidName)) {
                                tFluidsPerCircuit.add(GT_ModHandler.getDistilledWater(tFluidAmount));
                            } else if (FluidRegistry.isFluidRegistered(tFluidName)) {
                                tFluidsPerCircuit.add(new FluidStack(FluidRegistry.getFluid(tFluidName), tFluidAmount));
                            } else {
                                GT_Log.err.println("Unregistered fluid used in recipe: " + tFluidName);
                            }
                        }
                    }
                    aReader.endArray();
                    break;
                case "duration":
                    tDuration = aReader.nextInt();
                    break;
                case "EUt":
                    tEUt = aReader.nextInt();
                    break;
                case "specialValue":
                    JsonToken tToken = aReader.peek();
                    if (tToken == JsonToken.STRING) {
                        String tSpecialValueString = aReader.nextString();
                        if ("lowgravity".equals(tSpecialValueString)) {
                            tSpecialValue = -100;
                        } else if ("cleanroom".equals(tSpecialValueString)) {
                            tSpecialValue = -200;
                        } else {
                            tSpecialValue = Integer.valueOf(tSpecialValueString);
                        }
                    } else if (tToken == JsonToken.NUMBER) {
                        tSpecialValue = aReader.nextInt();
                    } else {
                        throw new AssertionError("Invalid token in specialValue: " + tToken);
                    }
                    break;
                case "blastTemp":
                case "startingCost":
                    tSpecialValue = aReader.nextInt();
                    break;
                case "enabled":
                    tEnabled = aReader.nextBoolean();
                    break;
                case "enableCondition":
                    tEnableCondition = aReader.nextString();
                    if (tEnableCondition.startsWith("!")) {
                        tInvertCondition = true;
                        tEnableCondition = tEnableCondition.substring(1);
                    }
                    break;
                case "hidden":
                    tHidden = aReader.nextBoolean();
                    break;
                case "useCuttingFluids":
                    tUseCuttingFluids = aReader.nextBoolean();
                    break;
                case "optimize":
                    tOptimize = aReader.nextBoolean();
                    break;
                default:
                    throw new AssertionError("Invalid recipe specifier: " + tName);
            }
        }
        aReader.endObject();
        // Recipes disabled by condition should not be added.
        if (tEnableCondition != null && !(GT_RecipeConditions.getConditionValue(tEnableCondition) ^ tInvertCondition)) {
            return null;
        }
        // Clear input/output lists of nulls, which could be from unregistered items/fluids, or items from unloaded mods.
        // if this results in completely empty inputs or outputs, that usually means the recipe should not be added.
        // (except for things like fuels and some fake recipes which are not currently handled in json)
        while (tInputs.remove(null)) { /* do nothing else */ }
        while (tOutputs.remove(null)) { /* do nothing else */ }
        while (tFluidInputs.remove(null)) { /* do nothing else */ }
        while (tFluidOutputs.remove(null)) { /* do nothing else */ }
        if (tEUt <= 0 || (tInputs.isEmpty() && tFluidInputs.isEmpty()) || (tOutputs.isEmpty() && tFluidOutputs.isEmpty() && tFluidsPerCircuit.isEmpty())) {
            return null;
        }
        if (!tFluidsPerCircuit.isEmpty()) {
            tInputs.add(GT_Utility.getIntegratedCircuit(0));
            List<GT_Recipe> tList = new ArrayList<>(tFluidsPerCircuit.size());
            for (int i = 0; i < tFluidsPerCircuit.size(); i++) {
                tInputs.set(tInputs.size() - 1, GT_Utility.getIntegratedCircuit(i + 1));
                GT_Recipe rRecipe = new GT_Json_Recipe(tOptimize, tInputs.toArray(new ItemStack[0]), getOutputItems(tOutputs), null,
                    getOutputChancesAsInts(tOutputs), tFluidInputs.toArray(new FluidStack[0]), new FluidStack[]{tFluidsPerCircuit.get(i)}, tDuration, tEUt, tSpecialValue);
                rRecipe.mEnabled = tEnabled;
                rRecipe.mHidden = tHidden;
                tList.add(rRecipe);
            }
            return tList;
        }
        if (!tOutputs.isEmpty() || !tFluidOutputs.isEmpty()) {
            GT_Recipe rRecipe = new GT_Json_Recipe(tOptimize, tInputs.toArray(new ItemStack[0]), getOutputItems(tOutputs), null,
                    getOutputChancesAsInts(tOutputs), tFluidInputs.toArray(new FluidStack[0]), tFluidOutputs.toArray(new FluidStack[0]), tDuration, tEUt, tSpecialValue);
            rRecipe.mEnabled = tEnabled;
            rRecipe.mHidden = tHidden;
            if (tUseSolderFluids && (tSolderAmount > 0)) {
                FluidStack[] tSolderFluids = getSolderFluids();
                List<GT_Recipe> tList = new ArrayList<>(tSolderFluids.length);
                for (FluidStack tSolderFluid : tSolderFluids) {
                    GT_Recipe tRecipe = rRecipe.copy();
                    tRecipe.mFluidInputs = new FluidStack[]{tSolderFluid.copy()};
                    tRecipe.mFluidInputs[0].amount *= tSolderAmount;
                    tList.add(tRecipe);
                }
                return tList;
            } else if (tUseCuttingFluids) {
                List<GT_Recipe> tList = new ArrayList<>(3);
                GT_Recipe tRecipe = rRecipe.copy();
                tRecipe.mFluidInputs = new FluidStack[]{Materials.Lubricant.getFluid(Math.max(1, Math.min(250, tDuration * tEUt / 1280)))};
                tList.add(tRecipe);
                tRecipe = rRecipe.copy();
                tRecipe.mFluidInputs = new FluidStack[]{GT_ModHandler.getDistilledWater(Math.max(3, Math.min(750, tDuration * tEUt / 426)))};
                tRecipe.mDuration = tDuration * 2;
                tList.add(tRecipe);
                tRecipe = rRecipe.copy();
                tRecipe.mFluidInputs = new FluidStack[]{Materials.Water.getFluid(Math.max(4, Math.min(1000, tDuration * tEUt / 320)))};
                tRecipe.mDuration = tDuration * 2;
                tList.add(tRecipe);
                return tList;
            }
            return Collections.singletonList(rRecipe);
        }
        return null;
    }

    private static ItemStack readInput(JsonReader aReader) throws IOException {
        ItemStack tStack = null;
        String tOreName = null;
        int tCount = -1;
        NBTTagCompound tNbtData = null;
        aReader.beginObject();
        while (aReader.hasNext()) {
            String tEntryName = aReader.nextName();
            switch (tEntryName) {
                case "item":
                    String tItemFullName = aReader.nextString();
                    tStack = StringtoItemStack(tItemFullName);
                    break;
                case "ore":
                case "oredict":
                    tOreName = aReader.nextString();
                    break;
                case "count":
                    tCount = aReader.nextInt();
                    break;
                case "nbt":
                    tNbtData = readNbt(aReader);
                    break;
                case "circuit":
                    int tCircuitConfig = aReader.nextInt();
                    tStack = GT_Utility.getIntegratedCircuit(tCircuitConfig);
                    tCount = 0;
                    break;
                case "displayName":
                case "comment":
                    aReader.nextString();
                    // cosmetic, to make recipe more readable, in case item's internal name is something like 
                    // gregtech:gt.metaitem.02:30500
                    break;
                default:
                    throw new AssertionError("Invalid input specifier: " + tEntryName);
            }
        }
        aReader.endObject();
        if (tCount >= 0) {
            if (tOreName != null) {
                return GT_OreDictUnificator.getFirstOre(tOreName, tCount);
            } else if (tStack != null) {
                tStack.stackSize = tCount;
                if (tNbtData != null) {
                    tStack.setTagCompound(tNbtData);
                }
                return tStack;
            }
        }
        return null;
    }

    private static ItemStack StringtoItemStack(String aItemString) {
        String[] tPieces = aItemString.split(":");
        int tMeta = 0;
        if (tPieces.length >= 3) {
            if ("*".equals(tPieces[2])) {
                tMeta = GT_Values.W;
            } else {
                tMeta = Integer.valueOf(tPieces[2]);
            }
        }
        if (tPieces.length >= 2) {
            ItemStack rStack = null;
            if ("GTItemList".equals(tPieces[0])) {
                try {
                    rStack = ItemList.valueOf(tPieces[1]).get(1, new Object[0]);
                    return rStack;
                } catch (Throwable e) {
                    e.printStackTrace(GT_Log.err);
                }
            } else if ("IC2ItemList".equals(tPieces[0])) {
                if (tPieces.length >= 3) {
                    rStack = GT_ModHandler.getIC2Item(tPieces[1], 1, tMeta);
                } else {
                    rStack = GT_ModHandler.getIC2Item(tPieces[1], 1);
                }
                return rStack;
            } else {
                rStack = GameRegistry.findItemStack(tPieces[0], tPieces[1], 1);
            }
            if (rStack != null) {
                rStack.setItemDamage(tMeta);
            }
            return rStack;
        }
        return null;
    }
    
    private static ItemStackWithChance readOutput(JsonReader aReader) throws IOException {
        ItemStack tStack = null;
        String tOreName = null;
        int tCount = -1;
        float tChance = 1.0f;
        NBTTagCompound tNbtData = null;
        aReader.beginObject();
        while (aReader.hasNext()) {
            String tEntryName = aReader.nextName();
            switch (tEntryName) {
                case "item":
                    String tItemFullName = aReader.nextString();
                    tStack = StringtoItemStack(tItemFullName);
                    break;
                case "ore":
                case "oredict":
                    tOreName = aReader.nextString();
                    break;
                case "count":
                    tCount = aReader.nextInt();
                    break;
                case "nbt":
                    tNbtData = readNbt(aReader);
                    break;
                case "chance":
                    tChance = (float) aReader.nextDouble();
                    break;
                case "displayName":
                case "comment":
                    aReader.nextString();
                    // cosmetic, to make recipe more readable, in case item's internal name is something like 
                    // gregtech:gt.metaitem.02:30500
                    break;
                default:
                    throw new AssertionError("Invalid output specifier: " + tEntryName);
            }
        }
        aReader.endObject();
        if (tCount >= 0) {
            if (tOreName != null) {
                tStack = GT_OreDictUnificator.getFirstOre(tOreName, tCount);
            }
            if (tStack != null) {
                tStack.stackSize = tCount;
                if (tNbtData != null) {
                    tStack.setTagCompound(tNbtData);
                }
                return new ItemStackWithChance(tStack, tChance);
            }
        }
        return null;
    }
    
    private static NBTTagCompound readNbt(JsonReader aReader) throws IOException {
        NBTTagCompound rNBT = new NBTTagCompound();
        aReader.beginArray();
        while (aReader.hasNext()) {
            String tTagName = null;
            String tType = null;
            String tValue = null;
            aReader.beginObject();
            while (aReader.hasNext()) {
                String tEntryName = aReader.nextName();
                switch (tEntryName) {
                    case "tag":
                    case "tagName":
                    case "name":
                    case "key":
                        tTagName = aReader.nextString();
                        break;
                    case "type":
                        tType = aReader.nextString();
                        break;
                    case "value":
                        tValue = aReader.nextString();
                        break;
                    default:
                        throw new AssertionError("Invalid NBT specifier: " + tEntryName);
                }
            }
            aReader.endObject();
            if (tTagName != null && tType != null && tValue != null) {
                switch (tType.toLowerCase()) {
                    case "boolean":
                        rNBT.setBoolean(tTagName, Boolean.valueOf(tValue));
                        break;
                    case "byte":
                        rNBT.setByte(tTagName, Byte.valueOf(tValue));
                        break;
                    case "double":
                        rNBT.setDouble(tTagName, Double.valueOf(tValue));
                        break;
                    case "float":
                        rNBT.setFloat(tTagName, Float.valueOf(tValue));
                        break;
                    case "int":
                        rNBT.setInteger(tTagName, Integer.valueOf(tValue));
                        break;
                    case "long":
                        rNBT.setLong(tTagName, Long.valueOf(tValue));
                        break;
                    case "short":
                        rNBT.setShort(tTagName, Short.valueOf(tValue));
                        break;
                    case "string":
                        rNBT.setString(tTagName, tValue);
                        break;
                    default:
                        throw new AssertionError("Invalid NBT tag type: " + tType);
                }
            }
        }
        aReader.endArray();
        return rNBT;
    }

    private static FluidStack[] sSolderFluids = null;
    
    private static FluidStack[] getSolderFluids() {
        if (sSolderFluids != null) {
            return sSolderFluids;
        }
        List<FluidStack> tSolderFluids = new ArrayList<>(3);
        for (Materials tMat : Materials.values()) {
            if (tMat.mStandardMoltenFluid != null && tMat.contains(SubTag.SOLDERING_MATERIAL) && !(GregTech_API.mUseOnlyGoodSolderingMaterials && !tMat.contains(SubTag.SOLDERING_MATERIAL_GOOD))) {
                int tMultiplier = tMat.contains(SubTag.SOLDERING_MATERIAL_GOOD) ? 1 : tMat.contains(SubTag.SOLDERING_MATERIAL_BAD) ? 4 : 2;
                tSolderFluids.add(tMat.getMolten(tMultiplier));
            }
        }
        sSolderFluids = tSolderFluids.toArray(new FluidStack[0]);
        return sSolderFluids;
    }
    
}
