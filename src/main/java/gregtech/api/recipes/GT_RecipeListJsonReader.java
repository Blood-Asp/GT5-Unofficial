package gregtech.api.recipes;

import com.google.gson.stream.JsonReader;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.SubTag;
import gregtech.api.util.GT_Log;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
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
    
    public static List<GT_MachineRecipe> readRecipes(JsonReader aReader) {
        aReader.setLenient(true);
        List<GT_MachineRecipe> rList = new ArrayList<>(100);
        try {
            aReader.beginArray();
            while (aReader.hasNext()) {
                List<GT_MachineRecipe> tRecipe = readRecipe(aReader);
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

    private static List<GT_MachineRecipe> readRecipe(JsonReader aReader) throws IOException {
        List<GT_RecipeInput> tInputs = new ArrayList<>(10);
        List<GT_RecipeOutput> tOutputs = new ArrayList<>(10);
        List<FluidStack> tFluidInputs = new ArrayList<>(10);
        List<FluidStack> tFluidOutputs = new ArrayList<>(10);
        int tDuration = 0;
        int tEUt = 0;
        int tSpecialValue = 0;
        boolean tEnabled = true;
        String tEnableCondition = null;
        boolean tInvertCondition = false;
        boolean tHidden = false;
        boolean tUseSolderFluids = false;
        boolean tUseCuttingFluids = false;
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
                                tFluidName = Materials.get(tMaterialName).getGas(1L).getFluid().getName();
                            } else if ("plasma".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tFluidName = Materials.get(tMaterialName).getPlasma(1L).getFluid().getName();
                            } else if ("molten".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tFluidName = Materials.get(tMaterialName).getMolten(1L).getFluid().getName();
                            } else if ("materialFluid".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tFluidName = Materials.get(tMaterialName).getFluid(1L).getFluid().getName();
                            }
                        }
                        aReader.endObject();
                        if (!tUseSolderFluids) {
                            if (FluidRegistry.isFluidRegistered(tFluidName)) {
                                tFluidInputs.add(new FluidStack(FluidRegistry.getFluid(tFluidName), tFluidAmount));
                            } else {
                                GT_Log.err.println("Unregistered fluid used in recipe: " + tFluidName);
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
                        aReader.beginObject();
                        while (aReader.hasNext()) {
                            String tSubName = aReader.nextName();
                            if ("fluid".equals(tSubName)) {
                                tFluidName = aReader.nextString();
                            } else if ("amount".equals(tSubName)) {
                                tFluidAmount = aReader.nextInt();
                            } else if ("gas".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tFluidName = Materials.get(tMaterialName).getGas(1L).getFluid().getName();
                            } else if ("plasma".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tFluidName = Materials.get(tMaterialName).getPlasma(1L).getFluid().getName();
                            } else if ("molten".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tFluidName = Materials.get(tMaterialName).getMolten(1L).getFluid().getName();
                            } else if ("materialFluid".equals(tSubName)) {
                                String tMaterialName = aReader.nextString();
                                tFluidName = Materials.get(tMaterialName).getFluid(1L).getFluid().getName();
                            }
                        }
                        aReader.endObject();
                        if (FluidRegistry.isFluidRegistered(tFluidName)) {
                            tFluidOutputs.add(new FluidStack(FluidRegistry.getFluid(tFluidName), tFluidAmount));
                        } else {
                            GT_Log.err.println("Unregistered fluid used in recipe: " + tFluidName);
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
                    String tSpecialValueString = aReader.nextString();
                    if ("lowgravity".equals(tSpecialValueString)) {
                        tSpecialValue = -100;
                    } else if ("cleanroom".equals(tSpecialValueString)) {
                        tSpecialValue = -200;
                    } else {
                        tSpecialValue = Integer.valueOf(tSpecialValueString);
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
                default:
                    throw new AssertionError("Invalid recipe specifier");
            }
        }
        aReader.endObject();
        if (!tOutputs.isEmpty() || !tFluidOutputs.isEmpty()) {
            GT_MachineRecipe rRecipe = new GT_MachineRecipe(tInputs.toArray(new GT_RecipeInput[0]), tOutputs.toArray(new GT_RecipeOutput[0]), 
                    tFluidInputs.toArray(new FluidStack[0]), tFluidOutputs.toArray(new FluidStack[0]));
            rRecipe.setDuration(tDuration);
            rRecipe.setEUt(tEUt);
            rRecipe.setSpecialValue(tSpecialValue);
            rRecipe.setEnabled(tEnabled);
            rRecipe.setEnableCondition(tEnableCondition);
            rRecipe.setInvertCondition(tInvertCondition);
            rRecipe.setHidden(tHidden);
            if (rRecipe.isValidRecipe()) {
                if (tUseSolderFluids && (tSolderAmount > 0)) {
                    FluidStack[] tSolderFluids = getSolderFluids();
                    List<GT_MachineRecipe> tList = new ArrayList<>(tSolderFluids.length);
                    for (FluidStack tSolderFluid : tSolderFluids) {
                        GT_MachineRecipe tRecipe = rRecipe.copy();
                        tRecipe.mFluidInputs = new FluidStack[]{tSolderFluid.copy()};
                        tRecipe.mFluidInputs[0].amount *= tSolderAmount;
                        tList.add(tRecipe);
                    }
                    return tList;
                } else if (tUseCuttingFluids) {
                    List<GT_MachineRecipe> tList = new ArrayList<>(3);
                    GT_MachineRecipe tRecipe = rRecipe.copy();
                    tRecipe.mFluidInputs = new FluidStack[]{Materials.Lubricant.getFluid(Math.max(1, Math.min(250, tDuration * tEUt / 1280)))};
                    tList.add(tRecipe);
                    tRecipe = rRecipe.copy();
                    tRecipe.mFluidInputs = new FluidStack[]{GT_ModHandler.getDistilledWater(Math.max(3, Math.min(750, tDuration * tEUt / 426)))};
                    tRecipe.setDuration(tDuration * 2);
                    tList.add(tRecipe);
                    tRecipe = rRecipe.copy();
                    tRecipe.mFluidInputs = new FluidStack[]{Materials.Water.getFluid(Math.max(4, Math.min(1000, tDuration * tEUt / 320)))};
                    tRecipe.setDuration(tDuration * 2);
                    tList.add(tRecipe);
                    return tList;
                }
                return Collections.singletonList(rRecipe);
            }
        }
        return null;
    }

    private static GT_RecipeInput readInput(JsonReader aReader) throws IOException {
        ItemStack tStack = null;
        List<ItemStack> tAlts = new ArrayList<>(5);
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
                case "alts":
                    aReader.beginArray();
                    while (aReader.hasNext()) {
                        ItemStack tAltStack = null;
                        NBTTagCompound tAltNbtData = null;
                        String tAltOreName = null;
                        aReader.beginObject();
                        while (aReader.hasNext()) {
                            String tAltEntryName = aReader.nextName();
                            if ("item".equals(tAltEntryName)) {
                                tAltStack = StringtoItemStack(aReader.nextString());
                            } else if ("ore".equals(tAltEntryName) || "oredict".equals(tAltEntryName)) {
                                tAltOreName = aReader.nextString();
                            }else if ("nbt".equals(tAltEntryName)) {
                                tAltNbtData = readNbt(aReader);
                            }
                        }
                        aReader.endObject();
                        if (tAltStack != null && tAltNbtData != null) {
                            tAltStack.setTagCompound(tAltNbtData);
                        }
                        if (tAltOreName != null) {
                            tAlts.addAll(GT_OreDictUnificator.getOres(tAltOreName));
                        } else {
                            tAlts.add(tAltStack);
                        }
                    }
                    aReader.endArray();
                    break;
                case "displayName":
                case "comment":
                    aReader.nextString();
                    // cosmetic, to make recipe more readable, in case item's internal name is something like 
                    // gregtech:gt.metaitem.02:30500
                    break;
                default:
                    throw new AssertionError("Invalid input specifier");
            }
        }
        aReader.endObject();
        if (tCount >= 0) {
            if (tOreName != null) {
                return new GT_RecipeInputOredict(tOreName, tCount);
            } else if (!tAlts.isEmpty()) {
                for (ItemStack tAlt : tAlts) {
                    tAlt.stackSize = tCount;
                }
                GT_RecipeInputAlts rInput = new GT_RecipeInputAlts(tAlts.toArray(new ItemStack[0]));
                rInput.setCount(tCount);
                return rInput;
            } else if (tStack != null) {
                tStack.stackSize = tCount;
                if (tNbtData != null) {
                    tStack.setTagCompound(tNbtData);
                }
                return new GT_RecipeInput(tStack);
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
                rStack = GT_ModHandler.getIC2Item(tPieces[0], 1);
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
    
    private static GT_RecipeOutput readOutput(JsonReader aReader) throws IOException {
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
                    throw new AssertionError("Invalid output specifier");
            }
        }
        aReader.endObject();
        if (tCount >= 0) {
            if (tOreName != null) {
                return new GT_RecipeOutput(tOreName, tCount, tChance);
            } else if (tStack != null) {
                tStack.stackSize = tCount;
                if (tNbtData != null) {
                    tStack.setTagCompound(tNbtData);
                }
                return new GT_RecipeOutput(tStack, tChance);
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
                        throw new AssertionError("Invalid NBT specifier");
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
                        throw new AssertionError();
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
