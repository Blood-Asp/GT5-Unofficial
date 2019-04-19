package gregtech.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.objects.GT_CopiedBlockTexture;
import gregtech.api.util.GT_LanguageManager;
import gregtech.api.util.GT_Utility;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class GT_Block_Casings6
        extends GT_Block_Casings_Abstract {
    public GT_Block_Casings6() {
        super(GT_Item_Casings6.class, "gt.blockcasings6", GT_Material_Casings.INSTANCE);
        GT_Utility.addTexturePage((byte) 1);
        for (byte i = 0; i < 1; i = (byte) (i + 1)) {
            Textures.BlockIcons.casingTexturePages[1][i+16] = new GT_CopiedBlockTexture(this, 6, i);
        }
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".0.name", "Robust HSS-G Machine Casing");

        ItemList.Casing_RobustHSSG.set(new ItemStack(this,1,0));
    }
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int aSide, int aMeta) {
        switch (aMeta) {

        }
        return Textures.BlockIcons.MACHINE_CASING_ROBUST_HSSG.getIcon();
    }
}
