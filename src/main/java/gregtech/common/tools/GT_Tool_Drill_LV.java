package gregtech.common.tools;

import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IAOETool;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.IToolStats;
import gregtech.api.items.GT_MetaGenerated_Tool;
import gregtech.api.util.GT_ToolHarvestHelper;
import gregtech.api.util.GT_Utility;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.*;
import net.minecraft.world.World;

import java.util.List;

public class GT_Tool_Drill_LV extends GT_Tool implements IAOETool {
    @Override
    public int getToolDamagePerBlockBreak() {
        return GT_Mod.gregtechproxy.mHardRock ? 15 : 35;
    }

    @Override
    public int getToolDamagePerDropConversion() {
        return 100;
    }

    @Override
    public int getToolDamagePerContainerCraft() {
        return 100;
    }

    @Override
    public int getToolDamagePerEntityAttack() {
        return 200;
    }

    @Override
    public int getBaseQuality() {
        return 0;
    }

    @Override
    public float getBaseDamage() {
        return 2.0F;
    }

    @Override
    public float getSpeedMultiplier() {
        return 3.0F;
    }

    @Override
    public float getMaxDurabilityMultiplier() {
        return 1.0F;
    }

    @Override
    public String getCraftingSound() {
        return (String) GregTech_API.sSoundList.get(106);
    }

    @Override
    public String getEntityHitSound() {
        return (String) GregTech_API.sSoundList.get(106);
    }

    @Override
    public String getBreakingSound() {
        return (String) GregTech_API.sSoundList.get(106);
    }

    @Override
    public String getMiningSound() {
        return (String) GregTech_API.sSoundList.get(106);
    }

    @Override
    public boolean canBlock() {
        return false;
    }

    @Override
    public boolean isCrowbar() {
        return false;
    }

    @Override
    public boolean isMinableBlock(Block aBlock, byte aMetaData) {
        return GT_ToolHarvestHelper.isAppropriateTool(aBlock, aMetaData, "pickaxe", "shovel")
                || GT_ToolHarvestHelper.isAppropriateMaterial(aBlock,
                Material.rock,
                Material.iron,
                Material.anvil,
                Material.sand,
                Material.grass,
                Material.ground,
                Material.snow,
                Material.clay,
                Material.glass
        );

    }

    @Override
    public ItemStack getBrokenItem(ItemStack aStack) {
        return null;
    }

    @Override
    public IIconContainer getIcon(boolean aIsToolHead, ItemStack aStack) {
        return aIsToolHead ? GT_MetaGenerated_Tool.getPrimaryMaterial(aStack).mIconSet.mTextures[gregtech.api.enums.OrePrefixes.toolHeadDrill.mTextureIndex] : Textures.ItemIcons.POWER_UNIT_LV;
    }

    @Override
    public short[] getRGBa(boolean aIsToolHead, ItemStack aStack) {
        return aIsToolHead ? GT_MetaGenerated_Tool.getPrimaryMaterial(aStack).mRGBa : GT_MetaGenerated_Tool.getSecondaryMaterial(aStack).mRGBa;
    }

    @Override
    public void onStatsAddedToTool(GT_MetaGenerated_Tool aItem, int aID) {
    }

    @Override
    public void onToolCrafted(ItemStack aStack, EntityPlayer aPlayer) {
        super.onToolCrafted(aStack, aPlayer);
        aPlayer.triggerAchievement(AchievementList.buildPickaxe);
        aPlayer.triggerAchievement(AchievementList.buildBetterPickaxe);
        try {
            GT_Mod.instance.achievements.issueAchievement(aPlayer, "driltime");
            GT_Mod.instance.achievements.issueAchievement(aPlayer, "buildDrill");
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onRightClick(ItemStack stack, EntityPlayer player) {
        if (player.isSneaking()) {
            NBTTagCompound stats = GT_MetaGenerated_Tool.getStatNbt(stack);
            int aoe = getAOE(stats) + 1;
            if (aoe > getMaxAOESize()) aoe = 0;
            GT_Utility.sendChatToPlayer(player, StatCollector.translateToLocal("GT5U.tool.drill.area") + " " + xLen[aoe] + "x" + yLen[aoe]);
            setAOE(stats, aoe);
        }
    }

    @Override
    public float getDigSpeed(float digSpeed, IToolStats stats, ItemStack stack) {
        NBTTagCompound nbtStats = GT_MetaGenerated_Tool.getStatNbt(stack);
        int aoe = getAOE(nbtStats);
        if (aoe > 0) return digSpeed / (getSpeedReduction() * aoe);
        return digSpeed;
    }

    @Override
    public float getDamageMultiplyer() {
        return 1.25f;
    }

    @Override
    public float getSpeedReduction() {
        return 1.5f;
    }

    @Override
    public int getMaxAOESize() {
        return 3;
    }

    static int xStart[] = {0, 0, 0, -1, -1, -2, -2};
    static int yStart[] = {0, 0, 0, -1, -2, -3, -4};
    static int xLen[] = {1, 1, 2, 3, 4, 5, 6};
    static int yLen[] = {1, 2, 2, 3, 4, 5, 6};

    @Override
    public float onBlockDestroyed(ItemStack stack, IToolStats stats, float damagePerBlock, float timeToTakeCenter, float digSpeed, World world, Block block, int x, int y, int z, EntityLivingBase player) {
        if (!(player instanceof EntityPlayerMP))
            return 0;
        MovingObjectPosition mop = GT_Utility.raytraceFromEntity(player.worldObj, player, false, 5);
        if (mop == null) return 0;
        int side = mop.sideHit;
        EntityPlayerMP playerMP = (EntityPlayerMP) player;
        NBTTagCompound nbt = GT_MetaGenerated_Tool.getStatNbt(stack);
        int aoe = getAOE(nbt);
        if (aoe == 0) return 0;
        damagePerBlock *= getDamageMultiplyer();
        float broken = breakBlockAround(side, xStart[aoe], yStart[aoe], xLen[aoe], yLen[aoe], stats, stack, world, x, y,
                z, playerMP, damagePerBlock, timeToTakeCenter, digSpeed);
        return broken;
    }

    @Override
    public IChatComponent getDeathMessage(EntityLivingBase aPlayer, EntityLivingBase aEntity) {
        return new ChatComponentText(EnumChatFormatting.RED + aEntity.getCommandSenderName() + EnumChatFormatting.WHITE + " got the Drill! (by " + EnumChatFormatting.GREEN + aPlayer.getCommandSenderName() + EnumChatFormatting.WHITE + ")");
    }

    @Override
    public boolean onItemUse(ItemStack stack, World world, int x, int y, int z, int sidehit, EntityPlayer playerEntity, float hitX, float hitY, float hitZ) {
        if (playerEntity.isSneaking()) {
            return false;
        } else {
            return placeSideBlock(stack, world, x, y, z, sidehit, playerEntity, hitX, hitY, hitZ);
        }
    }

    @Override
    public void toolTip(List aList, ItemStack aStack, EntityPlayer aPlayer, IToolStats stats) {
        NBTTagCompound nbtStats = GT_MetaGenerated_Tool.getStatNbt(aStack);
        if (nbtStats != null) {
            int aoe = ((GT_Tool_Drill_LV) stats).getAOE(nbtStats);
            aList.add(EnumChatFormatting.GRAY + "Shift + RMB to change AOE size.");
            aList.add(EnumChatFormatting.WHITE + "AOE: " + EnumChatFormatting.GREEN + xLen[aoe] + "X" + yLen[aoe]);
        }
    }
}
