package gregtech.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.SubTag;
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
import static gregtech.api.enums.OrePrefixes.cellMolten;

public class GT_MetaGenerated_Item_99
        extends GT_MetaGenerated_Item {
    public static GT_MetaGenerated_Item_99 INSTANCE;

    private BitSet enabled=new BitSet();

    private void registerd(Materials tMaterial,int i){
        enabled.set(i);
        ItemStack tStack = addItem(i,getDefaultLocalizationFormat(cellMolten, tMaterial, i),tMaterial.getToolTip(cellMolten.mMaterialAmount / M));
        if (cellMolten.mIsUnificatable) {
            GT_OreDictUnificator.set(cellMolten, tMaterial, tStack);
        } else {
            GT_OreDictUnificator.registerOre(cellMolten.get(tMaterial), tStack);
        }
    }

    private void register(Materials tMaterial,int i){
        ItemStack tStack = new ItemStack(this, 1, i);
        enabled.set(i);
        GT_LanguageManager.addStringLocalization(getUnlocalizedName(tStack) + ".name", getDefaultLocalizationFormat(cellMolten, tMaterial, i));
        GT_LanguageManager.addStringLocalization(getUnlocalizedName(tStack) + ".tooltip", tMaterial.getToolTip(cellMolten.mMaterialAmount / M));
        if (cellMolten.mIsUnificatable) {
            GT_OreDictUnificator.set(cellMolten, tMaterial, tStack);
        } else {
            GT_OreDictUnificator.registerOre(cellMolten.get(tMaterial), tStack);
        }
    }
    //x32

    public GT_MetaGenerated_Item_99() {
        super("metaitem.99", (short) 1000, (short) 0);

        INSTANCE = this;

        for (Materials tMaterial:GregTech_API.sGeneratedMaterials) {
            if (tMaterial == null || tMaterial.mMetaItemSubID<0 || tMaterial.mMetaItemSubID>=1000) continue;
            //if (tMaterial.getcells(1)==null) {
            if ((tMaterial.contains(SubTag.SMELTING_TO_FLUID)) && (!tMaterial.contains(SubTag.NO_SMELTING))) {
                register(tMaterial,tMaterial.mMetaItemSubID);
                if (tMaterial.mSmeltInto != tMaterial) {
                    register(tMaterial.mSmeltInto,tMaterial.mSmeltInto.mMetaItemSubID);
                }
            }
            //}
        }
        mEnabledItems.clear();
        mVisibleItems.clear();
    }

	/* ---------- OVERRIDEABLE FUNCTIONS ---------- */

    /**
     * @return the Color Modulation the Material is going to be rendered with.
     */
    @Override
    public short[] getRGBa(ItemStack aStack) {
        Materials tMaterial = GregTech_API.sGeneratedMaterials[getDamage(aStack)];
        return tMaterial == null ? Materials._NULL.mMoltenRGBa : tMaterial.mMoltenRGBa;
    }

	/* ---------- OVERRIDEABLE FUNCTIONS ---------- */

    /**
     * @param aPrefix   the OreDict Prefix
     * @param aMaterial the Material
     * @param aMetaData a Index from [0 - 31999]
     * @return the Localized Name when default LangFiles are used.
     */
    @Deprecated
    public String getDefaultLocalization(OrePrefixes aPrefix, Materials aMaterial, int aMetaData) {
        return aPrefix.getDefaultLocalNameForItem(aMaterial);
    }

    /**
     * @param aPrefix   the OreDict Prefix
     * @param aMaterial the Material
     * @param aMetaData a Index from [0 - 31999]
     * @return the Localized Name Format when default LangFiles are used.
     */
    public String getDefaultLocalizationFormat(OrePrefixes aPrefix, Materials aMaterial, int aMetaData) {
        return aPrefix.getDefaultLocalNameFormatForItem(aMaterial);
    }

    /**
     * @param aPrefix         always != null
     * @param aMaterial       always != null
     * @param aDoShowAllItems this is the Configuration Setting of the User, if he wants to see all the Stuff like Tiny Dusts or Crushed Ores as well.
     * @return if this Item should be visible in NEI or Creative
     */
    public boolean doesShowInCreative(OrePrefixes aPrefix, Materials aMaterial, boolean aDoShowAllItems) {
        return true;
    }

	/* ---------- INTERNAL OVERRIDES ---------- */

    @Override
    public String getItemStackDisplayName(ItemStack aStack) {
        String aName = super.getItemStackDisplayName(aStack);
        int aDamage = aStack.getItemDamage();
        if (aDamage < 1000 && aDamage >= 0)
            return Materials.getLocalizedNameForItem(aName, aDamage);
        return aName;
    }

    @Override
    public ItemStack getContainerItem(ItemStack aStack) {
        int aDamage = aStack.getItemDamage();
        if (aDamage < 1000 && aDamage >= 0) {
            return cellMolten.mContainerItem;
        }
        return null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public final void getSubItems(Item var1, CreativeTabs aCreativeTab, List aList) {
        for (int i = 0; i < 1000; i++) {
            Materials aMaterial = GregTech_API.sGeneratedMaterials[i];
            if (aMaterial != null && enabled.get(i)) {
                ItemStack tStack = new ItemStack(this, 1, i);
                isItemStackUsable(tStack);
                aList.add(tStack);
            }
        }
        super.getSubItems(var1, aCreativeTab, aList);
    }

    @Override
    public final IIcon getIconFromDamage(int aMetaData) {
        if (aMetaData < 0) return null;
        if (aMetaData < 1000) {
            Materials tMaterial = GregTech_API.sGeneratedMaterials[aMetaData];
            if (tMaterial == null) return null;
            IIconContainer tIcon = getIconContainer(aMetaData);
            if (tIcon != null) return tIcon.getIcon();
            return null;
        }
        return null;
    }

    @Override
    public IIconContainer getIconContainer(int aMetaData) {
        if (aMetaData < 0) return null;
        if (aMetaData < 1000) {
            Materials tMaterial = GregTech_API.sGeneratedMaterials[aMetaData];
            if (tMaterial == null) return null;
            return tMaterial.mIconSet.mTextures[cellMolten.mTextureIndex];
        }
        return null;
    }

    @Override
    public int getItemStackLimit(ItemStack aStack) {
        return cellMolten.mDefaultStackSize;
    }
}