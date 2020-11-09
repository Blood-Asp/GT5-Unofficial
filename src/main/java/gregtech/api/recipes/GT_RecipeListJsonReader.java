package gregtech.api.recipes;

import com.google.gson.stream.JsonReader;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.enums.GT_Values;
import gregtech.api.util.GT_Log;
import gregtech.api.util.GT_OreDictUnificator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
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
        List<GT_MachineRecipe> rList = new ArrayList<>(100);
        try {
            aReader.beginArray();
            while (aReader.hasNext()) {
                rList.add(readRecipe(aReader));
            }
            aReader.endArray();
        } catch (IOException e) {
            e.printStackTrace(GT_Log.err);
        }
        return rList;
    }

    private static GT_MachineRecipe readRecipe(JsonReader aReader) throws IOException {
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
                            }
                        }
                        aReader.endObject();
                        if (FluidRegistry.isFluidRegistered(tFluidName)) {
                            tFluidInputs.add(new FluidStack(FluidRegistry.getFluid(tFluidName), tFluidAmount));
                        } else {
                            GT_Log.err.println("Unregistered fluid used in recipe: " + tFluidName);
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
                default:
                    throw new AssertionError();
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
            return rRecipe;
        }
        return null;
    }

    private static GT_RecipeInput readInput(JsonReader aReader) throws IOException {
        ItemStack tStack = null;
        List<ItemStack> tAlts = new ArrayList<>(5);
        String tOreName = null;
        int tCount = -1;
        String tNbtData = null;
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
                    tNbtData = aReader.nextString();
                    break;
                case "alts":
                    aReader.beginArray();
                    while (aReader.hasNext()) {
                        ItemStack tAltStack = null;
                        String tAltNbtData = null;
                        String tAltOreName = null;
                        aReader.beginObject();
                        while (aReader.hasNext()) {
                            String tAltEntryName = aReader.nextName();
                            if ("item".equals(tAltEntryName)) {
                                tAltStack = StringtoItemStack(aReader.nextString());
                            } else if ("ore".equals(tAltEntryName)) {
                                tAltOreName = aReader.nextString();
                            }else if ("nbt".equals(tAltEntryName)) {
                                tAltNbtData = aReader.nextString();
                            }
                        }
                        aReader.endObject();
                        if (tAltStack != null && tAltNbtData != null) {
                            try {
                                tAltStack.setTagCompound((NBTTagCompound) JsonToNBT.func_150315_a(tAltNbtData));
                            } catch (NBTException e) {
                                e.printStackTrace(GT_Log.err);
                            }
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
                    throw new AssertionError();
            }
        }
        aReader.endObject();
        if (tCount >= 0) {
            if (tOreName != null) {
                return new GT_RecipeInputOredict(tOreName, tCount);
            } else if (!tAlts.isEmpty()) {
                tAlts.get(0).stackSize = tCount;
                return new GT_RecipeInputAlts(tAlts.toArray(new ItemStack[0]));
            } else if (tStack != null) {
                tStack.stackSize = tCount;
                if (tNbtData != null) {
                    try {
                        tStack.setTagCompound((NBTTagCompound) JsonToNBT.func_150315_a(tNbtData));
                    } catch (NBTException e) {
                        e.printStackTrace(GT_Log.err);
                    }
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
            ItemStack rStack = GameRegistry.findItemStack(tPieces[0], tPieces[1], 1);
            rStack.setItemDamage(tMeta);
            return rStack;
        }
        return null;
    }
    
    private static GT_RecipeOutput readOutput(JsonReader aReader) throws IOException {
        ItemStack tStack = null;
        String tOreName = null;
        int tCount = -1;
        float tChance = 1.0f;
        String tNbtData = null;
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
                    tNbtData = aReader.nextString();
                    break;
                case "chance":
                    tChance = (float) aReader.nextDouble();
                    break;
                case "displayName":
                case "comment":
                    // cosmetic, to make recipe more readable, in case item's internal name is something like 
                    // gregtech:gt.metaitem.02:30500
                    break;
                default:
                    throw new AssertionError();
            }
        }
        aReader.endObject();
        if (tCount >= 0) {
            if (tOreName != null) {
                return new GT_RecipeOutput(tOreName, tCount);
            } else if (tStack != null) {
                tStack.stackSize = tCount;
                if (tNbtData != null) {
                    try {
                        tStack.setTagCompound((NBTTagCompound) JsonToNBT.func_150315_a(tNbtData));
                    } catch (NBTException e) {
                        e.printStackTrace(GT_Log.err);
                    }
                }
                return new GT_RecipeOutput(tStack, tChance);
            }
        }
        return null;
    }
    
}
