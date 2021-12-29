package gregtech.common.tools;

import gregtech.GT_Mod;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class GT_Tool_Drill_HV extends GT_Tool_Drill_LV {
    @Override
    public int getToolDamagePerBlockBreak() {
        return GT_Mod.gregtechproxy.mHardRock ? 250 : 400;
    }

    @Override
    public int getToolDamagePerDropConversion() {
        return 1600;
    }

    @Override
    public int getToolDamagePerContainerCraft() {
        return 12800;
    }

    @Override
    public int getToolDamagePerEntityAttack() {
        return 3200;
    }

    @Override
    public int getBaseQuality() {
        return 1;
    }

    @Override
    public float getBaseDamage() {
        return 3.0F;
    }

    @Override
    public float getSpeedMultiplier() {
        return 9.0F;
    }

    @Override
    public float getMaxDurabilityMultiplier() {
        return 4.0F;
    }

    @Override
    public int getMaxAOESize() {
        return 6;
    }

    @Override
    public void onToolCrafted(ItemStack aStack, EntityPlayer aPlayer) {
        super.onToolCrafted(aStack, aPlayer);
        try {
            GT_Mod.instance.achievements.issueAchievement(aPlayer, "highpowerdrill");
            GT_Mod.instance.achievements.issueAchievement(aPlayer, "buildDDrill");
        } catch (Exception ignored) {
        }
    }

    @Override
    public IIconContainer getIcon(boolean aIsToolHead, ItemStack aStack) {
        return aIsToolHead ? gregtech.api.items.GT_MetaGenerated_Tool.getPrimaryMaterial(aStack).mIconSet.mTextures[gregtech.api.enums.OrePrefixes.toolHeadDrill.mTextureIndex] : Textures.ItemIcons.POWER_UNIT_HV;
    }
}
