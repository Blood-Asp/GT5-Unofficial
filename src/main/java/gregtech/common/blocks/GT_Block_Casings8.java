package gregtech.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_LanguageManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class GT_Block_Casings8 extends GT_Block_Casings_Abstract {

    //WATCH OUT FOR TEXTURE ID's
    public GT_Block_Casings8() {
        super(GT_Item_Casings8.class, "gt.blockcasings8", GT_Material_Casings.INSTANCE);
        for (int i = 0; i < 8; i = (i + 1)) {
            Textures.BlockIcons.casingTexturePages[1][i+48] = TextureFactory.of(this, i);
        }
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".0.name", "Chemically Inert Machine Casing");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".1.name", "PTFE Pipe Casing");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".2.name", "Mining Neutronium Casing");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".3.name", "Mining Black Plutonium Casing");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".4.name", "Extreme Engine Intake Casing");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".5.name", "Europium Reinforced Radiation Proof Machine Casing");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".6.name", "Advanced Rhodium Plated Palladium Machine Casing");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + ".7.name", "Advanced Iridium Plated Machine Casing");

        ItemList.Casing_Chemically_Inert.set(new ItemStack(this, 1, 0));
        ItemList.Casing_Pipe_Polytetrafluoroethylene.set(new ItemStack(this, 1, 1));
        ItemList.Casing_MiningNeutronium.set(new ItemStack(this, 1, 2));
        ItemList.Casing_MiningBlackPlutonium.set(new ItemStack(this, 1, 3));
        ItemList.Casing_ExtremeEngineIntake.set(new ItemStack(this, 1, 4));
        ItemList.Casing_AdvancedRadiationProof.set(new ItemStack(this, 1, 5));
        ItemList.Casing_Advanced_Rhodium_Palladium.set(new ItemStack(this, 1, 6));
        ItemList.Casing_Advanced_Iridium.set(new ItemStack(this, 1, 7));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int aSide, int aMeta) {
        switch (aMeta) {
        case 0:
            return Textures.BlockIcons.MACHINE_CASING_CHEMICALLY_INERT.getIcon();
        case 1:
            return Textures.BlockIcons.MACHINE_CASING_PIPE_POLYTETRAFLUOROETHYLENE.getIcon();
        case 2:
            return Textures.BlockIcons.MACHINE_CASING_MINING_NEUTRONIUM.getIcon();
        case 3:
            return Textures.BlockIcons.MACHINE_CASING_MINING_BLACKPLUTONIUM.getIcon();
        case 4:
            return Textures.BlockIcons.MACHINE_CASING_EXTREME_ENGINE_INTAKE.getIcon();//changed color in a terrible way
        case 5:
            return Textures.BlockIcons.MACHINE_CASING_ADVANCEDRADIATIONPROOF.getIcon();
        case 6:
            return Textures.BlockIcons.MACHINE_CASING_RHODIUM_PALLADIUM.getIcon();
        case 7:
            return Textures.BlockIcons.MACHINE_CASING_IRIDIUM.getIcon();
        }
        return Textures.BlockIcons.MACHINE_CASING_ROBUST_TUNGSTENSTEEL.getIcon();
    }
}
