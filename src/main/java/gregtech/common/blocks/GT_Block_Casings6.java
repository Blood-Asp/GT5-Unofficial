package gregtech.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.objects.GT_CopiedBlockTexture;
import gregtech.api.util.GT_LanguageManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class GT_Block_Casings6
        extends GT_Block_Casings_Abstract {
    public GT_Block_Casings6() {
        super(GT_Item_Casings6.class, "gt.blockcasings6", GT_Material_Casings.INSTANCE);
        for (byte i = 0; i < 16; i = (byte) (i + 1)) {
            Textures.BlockIcons.CASING_BLOCKS[(i + 64)] = new GT_CopiedBlockTexture(this, 9, i);
        }
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".0.name", "Tank Casing I");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".1.name", "Tank Casing II");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".2.name", "Tank Casing III");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".3.name", "Tank Casing IV");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".4.name", "Tank Casing V");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".5.name", "Tank Casing VI");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".6.name", "Tank Casing VII");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".7.name", "Tank Casing VIII");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".8.name", "Tank Casing IX");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".9.name", "Tank Casing X");

        ItemList.Casing_Tank_1.set(new ItemStack(this, 1, 0));
        ItemList.Casing_Tank_2.set(new ItemStack(this, 1, 1));
        ItemList.Casing_Tank_3.set(new ItemStack(this, 1, 2));
        ItemList.Casing_Tank_4.set(new ItemStack(this, 1, 3));
        ItemList.Casing_Tank_5.set(new ItemStack(this, 1, 4));
        ItemList.Casing_Tank_6.set(new ItemStack(this, 1, 5));
        ItemList.Casing_Tank_7.set(new ItemStack(this, 1, 6));
        ItemList.Casing_Tank_8.set(new ItemStack(this, 1, 7));
        ItemList.Casing_Tank_9.set(new ItemStack(this, 1, 8));
        ItemList.Casing_Tank_10.set(new ItemStack(this, 1, 9));
    }
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int aSide, int aMeta) {
        switch (aMeta) {
            case 0:
                return Textures.BlockIcons.MACHINE_CASING_TANK_1.getIcon();
            case 1:
                return Textures.BlockIcons.MACHINE_CASING_TANK_2.getIcon();
            case 2:
                return Textures.BlockIcons.MACHINE_CASING_TANK_3.getIcon();
            case 3:
                return Textures.BlockIcons.MACHINE_CASING_TANK_4.getIcon();
            case 4:
                return Textures.BlockIcons.MACHINE_CASING_TANK_5.getIcon();
            case 5:
                return Textures.BlockIcons.MACHINE_CASING_TANK_6.getIcon();
            case 6:
                return Textures.BlockIcons.MACHINE_CASING_TANK_7.getIcon();
            case 7:
                return Textures.BlockIcons.MACHINE_CASING_TANK_8.getIcon();
            case 8:
                return Textures.BlockIcons.MACHINE_CASING_TANK_9.getIcon();
            case 9:
                return Textures.BlockIcons.MACHINE_CASING_TANK_10.getIcon();
        }
        return Textures.BlockIcons.MACHINE_CASING_TANK_1.getIcon();
    }
}
