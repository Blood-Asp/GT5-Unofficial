package gregtech.common.items;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.items.GT_MetaGenerated_Item;
import gregtech.api.util.GT_LanguageManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** This class holds cells for non-GT fluids. */
public class GT_MetaGenerated_Item_98 extends GT_MetaGenerated_Item {
    public static GT_MetaGenerated_Item_98 INSTANCE;

    /**
     * Map of internal fluid name to cell type to register for that fluid.
     *
     * <p>The fluid at index {@code i} (in entry set iteration order) will be assigned ID {@code i}.
     *
     * <p>When adding a fluid, don't forget to make sure that GregTech loads after the mod that adds
     * that fluid!
     *
     * <p>In order to avoid breaking existing worlds, the entries in this list must not be
     * re-ordered or removed! The only safe modification that can be made to this list is adding new
     * entries to the end. To remove an entry, pass {@code null} in for the fluid name.
     */
    private static final ImmutableMap<String, CellType> FLUIDS =
            ImmutableMap.<String, CellType>builder()
                    .put("steam", CellType.REGULAR)
                    .put("bacterialsludge", CellType.REGULAR)
                    .put("mutagen", CellType.REGULAR)
                    .put("ender", CellType.REGULAR)
                    .put("endergoo", CellType.REGULAR)
                    .build();

    /**
     * We support adding two different types of cells.
     *
     * <p>Regular cells have capacity 1000 and use the regular cell icon. Molten cells have capacity
     * 144 and use the molten cell icon.
     */
    private enum CellType {
        REGULAR(1_000, OrePrefixes.cell),
        MOLTEN(144, OrePrefixes.cellMolten);
        // We could also add plasma cells (cellPlasma) here if we need to.
        // Plasma cells look like molten cells, but have 1000 capacity.

        private final int capacity;
        private final OrePrefixes prefix;

        CellType(int capacity, OrePrefixes prefix) {
            this.capacity = capacity;
            this.prefix = prefix;
        }
    }

    /** Struct class holding data that we need to properly handle a registered fluid cell item. */
    private static class RegisteredFluidData {
        private final Fluid fluid;
        private final short[] rgba;
        private final IIconContainer iconContainer;

        private RegisteredFluidData(Fluid fluid, short[] rgba, IIconContainer iconContainer) {
            this.fluid = fluid;
            this.rgba = rgba;
            this.iconContainer = iconContainer;
        }
    }

    /**
     * Map of ID to registered fluid data.
     *
     * <p>Only contains IDs that were successfully registered.
     */
    private final Map<Integer, RegisteredFluidData> registeredFluidDataMap;

    public GT_MetaGenerated_Item_98() {
        // For some reason, fluid cells will be rendered only if the metadata ID is less than the
        // offset. So we will specify maximum offset here.
        // See: GT_MetaGenerated_Item_Renderer.java
        super("metaitem.98", (short) 32766, (short) FLUIDS.size());

        INSTANCE = this;
        registeredFluidDataMap = new HashMap<>();

        int i = -1;
        for (Map.Entry<String, CellType> entry : FLUIDS.entrySet()) {
            i++;  // Increment first so that we don't accidentally skip doing so with continue
            String fluidName = entry.getKey();
            CellType cellType = entry.getValue();
            if (fluidName == null) {
                continue;
            }

            Fluid fluid = FluidRegistry.getFluid(fluidName);
            if (fluid == null) {
                // Fluid is not guaranteed to exist.
                // These fluids are non-GT fluids, so the mod may not be present.
                continue;
            }

            ItemStack itemStack = new ItemStack(this, 1, i);
            FluidStack fluidStack = new FluidStack(fluid, cellType.capacity);

            FluidContainerRegistry.registerFluidContainer(
                    new FluidContainerRegistry.FluidContainerData(
                            fluidStack, itemStack, ItemList.Cell_Empty.get(1L)));

            GT_LanguageManager.addStringLocalization(
                    getUnlocalizedName(itemStack) + ".name",
                    cellType.prefix.mLocalizedMaterialPre + fluid.getLocalizedName(fluidStack) + cellType.prefix.mLocalizedMaterialPost);

            int color = fluid.getColor();
            short[] rgba = new short[4];
            rgba[0] = (short) ((color & 0x00FF0000) >> 16);
            rgba[1] = (short) ((color & 0x0000FF00) >> 8);
            rgba[2] = (short) (color & 0x000000FF);
            rgba[3] = (short) ((color & 0xFF000000) >> 24);

            // We'll just steal the icons from Water. They are all the same anyway (except _NULL is broken for cells).
            IIconContainer iconContainer = Materials.Water.mIconSet.mTextures[cellType.prefix.mTextureIndex];
            registeredFluidDataMap.put(i, new RegisteredFluidData(fluid, rgba, iconContainer));
        }

        // We're not going to use these BitSets, so clear them to save memory.
        mEnabledItems.clear();
        mVisibleItems.clear();
    }

    @Override
    public short[] getRGBa(ItemStack aStack) {
        RegisteredFluidData fluidData = registeredFluidDataMap.get(aStack.getItemDamage());
        if (fluidData == null) {
            return Materials._NULL.mRGBa;
        }

        return fluidData.rgba;
    }

    @Override
    public ItemStack getContainerItem(ItemStack aStack) {
        return ItemList.Cell_Empty.get(1L);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item var1, CreativeTabs aCreativeTab, List aList) {
        registeredFluidDataMap.keySet().stream()
                .map(i -> new ItemStack(this, 1, i))
                .forEach(aList::add);
    }

    @Override
    public final IIcon getIconFromDamage(int aMetaData) {
        IIconContainer iconContainer = getIconContainer(aMetaData);
        if (iconContainer != null) {
            return iconContainer.getIcon();
        }
        return null;
    }

    @Override
    public IIconContainer getIconContainer(int aMetaData) {
        RegisteredFluidData fluidData = registeredFluidDataMap.get(aMetaData);
        if (fluidData == null) {
            return null;
        }
        return fluidData.iconContainer;
    }

    @Override
    public int getItemStackLimit(ItemStack aStack) {
        return 64;
    }
}
