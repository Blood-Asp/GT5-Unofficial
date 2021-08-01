package gregtech.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.items.GT_MetaGenerated_Item;
import gregtech.api.util.GT_LanguageManager;
import gregtech.api.util.GT_OreDictUnificator;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.BitSet;
import java.util.List;

import static gregtech.api.enums.GT_Values.M;

/** Generates cells for cracked fluids. */
public class GT_MetaGenerated_Item_04 extends GT_MetaGenerated_Item {
    public static GT_MetaGenerated_Item_04 INSTANCE;

    private static final int NUM_CRACKED_CELL_TYPES = OrePrefixes.CRACKED_CELL_TYPES.size();

    private BitSet enabled = new BitSet();

    public GT_MetaGenerated_Item_04() {
        super("metaitem.04", (short) (NUM_CRACKED_CELL_TYPES * 1000), (short) 0);
        INSTANCE = this;

        for (Materials tMaterial : GregTech_API.sGeneratedMaterials) {
            if (tMaterial == null || !tMaterial.canBeCracked() || tMaterial.mMetaItemSubID < 0 || tMaterial.mMetaItemSubID >= 1000) {
                continue;
            }
            register(tMaterial, tMaterial.mMetaItemSubID);
        }

        // We're not going to use these BitSets, so clear them to save memory.
        mEnabledItems.clear();
        mVisibleItems.clear();
    }

    private void register(Materials tMaterial, int i) {
        int currIndex = NUM_CRACKED_CELL_TYPES * i;
        for (OrePrefixes type : OrePrefixes.CRACKED_CELL_TYPES) {
            ItemStack tStack = new ItemStack(this, 1, currIndex);
            GT_LanguageManager.addStringLocalization(getUnlocalizedName(tStack) + ".name", type.getDefaultLocalNameFormatForItem(tMaterial));
            GT_LanguageManager.addStringLocalization(getUnlocalizedName(tStack) + ".tooltip", tMaterial.getToolTip(type.mMaterialAmount / M));

            if (type.mIsUnificatable) {
                GT_OreDictUnificator.set(type, tMaterial, tStack);
            } else {
                GT_OreDictUnificator.registerOre(type.get(tMaterial), tStack);
            }

            enabled.set(currIndex);
            currIndex++;
        }
    }

    private int getMaterialIndex(int damage) {
        return damage / NUM_CRACKED_CELL_TYPES;
    }

    /** Returns null for item damage out of bounds. */
    private OrePrefixes getOrePrefix(int damage) {
        if (damage < 0 || damage >= NUM_CRACKED_CELL_TYPES * 1000) {
            return null;
        }
        return OrePrefixes.CRACKED_CELL_TYPES.get(damage % NUM_CRACKED_CELL_TYPES);
    }

    @Override
    public short[] getRGBa(ItemStack aStack) {
        Materials tMaterial = GregTech_API.sGeneratedMaterials[getMaterialIndex(aStack.getItemDamage())];
        return tMaterial == null ? Materials._NULL.mRGBa : tMaterial.mRGBa;
    }

    @Override
    public String getItemStackDisplayName(ItemStack aStack) {
        String aName = super.getItemStackDisplayName(aStack);

        int index = getMaterialIndex(aStack.getItemDamage());
        if (index >= 0 && index < 1000) {
            return Materials.getLocalizedNameForItem(aName, index);
        }

        return aName;
    }

    @Override
    public ItemStack getContainerItem(ItemStack aStack) {
        OrePrefixes prefix = getOrePrefix(aStack.getItemDamage());
        if (prefix != null) {
            return prefix.mContainerItem;
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item var1, CreativeTabs aCreativeTab, List aList) {
        enabled.stream()
                .mapToObj(i -> new ItemStack(this, 1, i))
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
        int index = getMaterialIndex(aMetaData);
        if (index < 0 || index >= 1000) {
            return null;
        }

        Materials material = GregTech_API.sGeneratedMaterials[index];
        OrePrefixes prefix = getOrePrefix(aMetaData);
        if (material != null && prefix != null) {
            return material.mIconSet.mTextures[prefix.mTextureIndex];
        }

        return null;
    }

    @Override
    public int getItemStackLimit(ItemStack aStack) {
        OrePrefixes prefix = getOrePrefix(aStack.getItemDamage());
        if (prefix != null) {
            return prefix.mDefaultStackSize;
        } else {
            // If we're here, then something went wrong. Use cell.mDefaultStackSize as a fallback.
            return OrePrefixes.cell.mDefaultStackSize;
        }
    }
}
