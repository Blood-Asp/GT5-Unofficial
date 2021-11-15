package gregtech.api.util;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Used by machines that are locked to a single recipe, for fast computation. */
public class GT_Single_Recipe_Check {
    private final GT_MetaTileEntity_MultiBlockBase multiBlockBase;
    private final GT_Recipe recipe;
    private final ImmutableMap<ItemId, Integer> itemCost;
    private final ImmutableMap<Fluid, Integer> fluidCost;
    private final int totalItemCost;
    private final int totalFluidCost;

    private GT_Single_Recipe_Check(
            GT_MetaTileEntity_MultiBlockBase multiBlockBase,
            GT_Recipe recipe,
            ImmutableMap<ItemId, Integer> itemCost,
            ImmutableMap<Fluid, Integer> fluidCost) {
        this.multiBlockBase = multiBlockBase;
        this.recipe = recipe;
        this.itemCost = itemCost;
        this.fluidCost = fluidCost;

        this.totalItemCost = itemCost.values().stream().mapToInt(Integer::intValue).sum();
        this.totalFluidCost = fluidCost.values().stream().mapToInt(Integer::intValue).sum();
    }

    public GT_Recipe getRecipe() {
        return recipe;
    }

    /**
     * Use this method if recipes cannot require more than a single stack of any item or fluid.
     * In particular, {@link GT_MetaTileEntity_MultiBlockBase#getCompactedInputs()} and
     * {@link GT_MetaTileEntity_MultiBlockBase#getCompactedFluids()} both enforce this single-stack
     * restriction, so any multi-block that calls those can use this method.
     */
    public boolean checkRecipeInputsSingleStack(boolean consumeInputs) {
        Map<ItemId, ItemStack> itemMap = null;
        if (totalItemCost > 0) {
            itemMap = new HashMap<>();
            for (ItemStack itemStack : multiBlockBase.getStoredInputs()) {
                itemMap.merge(
                        ItemId.createNoCopy(itemStack), itemStack,
                        (a, b) -> a.stackSize >= b.stackSize ? a : b);
            }

            for (Map.Entry<ItemId, Integer> entry : itemCost.entrySet()) {
                ItemStack itemStack = itemMap.get(entry.getKey());
                if (itemStack == null || itemStack.stackSize < entry.getValue()) {
                    return false;
                }
            }
        }

        Map<Fluid, FluidStack> fluidMap = null;
        if (totalFluidCost > 0) {
            fluidMap = new HashMap<>();
            for (FluidStack fluidStack : multiBlockBase.getStoredFluids()) {
                fluidMap.merge(
                        fluidStack.getFluid(), fluidStack,
                        (a, b) -> a.amount >= b.amount ? a : b);
            }

            for (Map.Entry<Fluid, Integer> entry : fluidCost.entrySet()) {
                FluidStack fluidStack = fluidMap.get(entry.getKey());
                if (fluidStack == null || fluidStack.amount < entry.getValue()) {
                    return false;
                }
            }
        }

        if (consumeInputs) {
            if (totalItemCost > 0) {
                for (Map.Entry<ItemId, Integer> entry : itemCost.entrySet()) {
                    itemMap.get(entry.getKey()).stackSize -= entry.getValue();
                }
            }

            if (totalFluidCost > 0) {
                for (Map.Entry<Fluid, Integer> entry : fluidCost.entrySet()) {
                    fluidMap.get(entry.getKey()).amount -= entry.getValue();
                }
            }
        }

        return true;
    }

    /**
     * Use this method if recipes can require more than a single stack of any item or fluid.
     * It is less efficient, though.
     */
    public boolean checkRecipeInputs(boolean consumeInputs) {
        List<ItemStack> items = null;
        if (totalItemCost > 0) {
            items = multiBlockBase.getStoredInputs();

            Map<ItemId, Integer> itemMap = new HashMap<>();
            for (ItemStack itemStack : items) {
                itemMap.merge(ItemId.createNoCopy(itemStack), itemStack.stackSize, Integer::sum);
            }

            for (Map.Entry<ItemId, Integer> entry : itemCost.entrySet()) {
                if (itemMap.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                    return false;
                }
            }
        }

        List<FluidStack> fluids = null;
        if (totalFluidCost > 0) {
            fluids = multiBlockBase.getStoredFluids();

            Map<Fluid, Integer> fluidMap = new HashMap<>();
            for (FluidStack fluidStack : fluids) {
                fluidMap.merge(fluidStack.getFluid(), fluidStack.amount, Integer::sum);
            }

            for (Map.Entry<Fluid, Integer> entry : fluidCost.entrySet()) {
                if (fluidMap.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                    return false;
                }
            }
        }

        if (consumeInputs) {
            if (totalItemCost > 0) {
                int remainingItemCost = totalItemCost;
                Map<ItemId, Integer> runningItemCost = Maps.newHashMap(itemCost);
                for (ItemStack itemStack : items) {
                    ItemId key = ItemId.createNoCopy(itemStack);
                    int runningCost = runningItemCost.getOrDefault(key, 0);
                    int paid = Math.min(itemStack.stackSize, runningCost);
                    itemStack.stackSize -= paid;
                    runningItemCost.put(key, runningCost - paid);

                    remainingItemCost -= paid;
                    if (remainingItemCost <= 0) {
                        break;
                    }
                }
            }

            if (totalFluidCost > 0) {
                int remainingFluidCost = totalFluidCost;
                Map<Fluid, Integer> runningFluidCost = Maps.newHashMap(fluidCost);
                for (FluidStack fluidStack : fluids) {
                    Fluid key = fluidStack.getFluid();
                    int runningCost = runningFluidCost.getOrDefault(key, 0);
                    int paid = Math.min(fluidStack.amount, runningCost);
                    fluidStack.amount -= paid;
                    runningFluidCost.put(key, runningCost - paid);

                    remainingFluidCost -= paid;
                    if (remainingFluidCost <= 0) {
                        break;
                    }
                }
            }
        }

        return true;
    }

    private static Map<ItemId, Integer> buildItemMap(
            GT_MetaTileEntity_MultiBlockBase multiBlockBase) {
        Map<ItemId, Integer> itemMap = new HashMap<>();
        for (ItemStack itemStack : multiBlockBase.getStoredInputs()) {
            itemMap.merge(ItemId.create(itemStack), itemStack.stackSize, Integer::sum);
        }
        return itemMap;
    }

    private static Map<Fluid, Integer> buildFluidMap(
            GT_MetaTileEntity_MultiBlockBase multiBlockBase) {
        Map<Fluid, Integer> fluidMap = new HashMap<>();
        for (FluidStack fluidStack : multiBlockBase.getStoredFluids()) {
            fluidMap.merge(fluidStack.getFluid(), fluidStack.amount, Integer::sum);
        }
        return fluidMap;
    }

    public static Builder builder(GT_MetaTileEntity_MultiBlockBase multiBlockBase) {
        return new Builder(multiBlockBase);
    }

    public static final class Builder {
        private final GT_MetaTileEntity_MultiBlockBase multiBlockBase;

        // In order to compute which items and fluids are consumed by the recipe, we compare the
        // multi-block's items and fluids before and after inputs are consumed by the recipe.
        private Map<ItemId, Integer> beforeItems;
        private Map<Fluid, Integer> beforeFluids;
        private Map<ItemId, Integer> afterItems;
        private Map<Fluid, Integer> afterFluids;

        private GT_Recipe recipe;

        private Builder(GT_MetaTileEntity_MultiBlockBase multiBlockBase) {
            this.multiBlockBase = multiBlockBase;
        }

        /** Call this before inputs are consumed by the recipe. */
        public Builder setBefore() {
            beforeItems = buildItemMap(multiBlockBase);
            beforeFluids = buildFluidMap(multiBlockBase);
            return this;
        }

        /** Call this after inputs are consumed by the recipe. */
        public Builder setAfter() {
            afterItems = buildItemMap(multiBlockBase);
            afterFluids = buildFluidMap(multiBlockBase);
            return this;
        }

        public Builder setRecipe(GT_Recipe recipe) {
            this.recipe = recipe;
            return this;
        }

        public GT_Single_Recipe_Check build() {
            ImmutableMap.Builder<ItemId, Integer> itemCostBuilder = ImmutableMap.builder();
            for (Map.Entry<ItemId, Integer> entry : beforeItems.entrySet()) {
                int cost = entry.getValue() - afterItems.getOrDefault(entry.getKey(), 0);
                if (cost > 0) {
                    itemCostBuilder.put(entry.getKey(), cost);
                }
            }

            ImmutableMap.Builder<Fluid, Integer> fluidCostBuilder = ImmutableMap.builder();
            for (Map.Entry<Fluid, Integer> entry : beforeFluids.entrySet()) {
                int cost = entry.getValue() - afterFluids.getOrDefault(entry.getKey(), 0);
                if (cost > 0) {
                    fluidCostBuilder.put(entry.getKey(), cost);
                }
            }

            return new GT_Single_Recipe_Check(
                    multiBlockBase, recipe, itemCostBuilder.build(), fluidCostBuilder.build());
        }
    }

    @AutoValue
    abstract static class ItemId {
        /** This method copies NBT, as it is mutable. */
        private static ItemId create(ItemStack itemStack) {
            NBTTagCompound nbt = itemStack.getTagCompound();
            if (nbt != null) {
                nbt = (NBTTagCompound) nbt.copy();
            }

            return new AutoValue_GT_Single_Recipe_Check_ItemId(
                    itemStack.getItem(), itemStack.getItemDamage(), nbt);
        }

        /** This method does not copy NBT in order to save time. Make sure not to mutate it! */
        private static ItemId createNoCopy(ItemStack itemStack) {
            return new AutoValue_GT_Single_Recipe_Check_ItemId(
                    itemStack.getItem(), itemStack.getItemDamage(), itemStack.getTagCompound());
        }

        abstract Item item();
        abstract int metaData();

        @Nullable
        abstract NBTTagCompound nbt();
    }
}
