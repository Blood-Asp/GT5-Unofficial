package gregtech.common.tools;

import gregtech.api.interfaces.IIconContainer;
import gregtech.api.items.GT_MetaGenerated_Tool;
import gregtech.api.util.GT_ToolHarvestHelper;
import gregtech.common.items.behaviors.Behaviour_Sense;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.List;

public class GT_Tool_Sense extends GT_Tool {
    private ThreadLocal<Object> sIsHarvestingRightNow = new ThreadLocal();

    @Override
    public float getBaseDamage() {
        return 3.0F;
    }
    
    @Override
    public float getMaxDurabilityMultiplier() {
        return 4.0F;
    }


    @Override
    public boolean isMinableBlock(Block aBlock, byte aMetaData) {
        return GT_ToolHarvestHelper.isAppropriateTool(aBlock , aMetaData ,"sense" , "scythe")
                || GT_ToolHarvestHelper.isAppropriateMaterial(aBlock ,Material.plants ,Material.leaves);
    }

    @Override
    public int convertBlockDrops(List<ItemStack> aDrops, ItemStack aStack, EntityPlayer aPlayer, Block aBlock, int aX, int aY, int aZ, byte aMetaData, int aFortune, boolean aSilkTouch, BlockEvent.HarvestDropsEvent aEvent) {
        int rConversions = 0;
        if ((this.sIsHarvestingRightNow.get() == null) && ((aPlayer instanceof EntityPlayerMP))) {
            this.sIsHarvestingRightNow.set(this);
            for (int i = -2; i < 3; i++) {
                for (int j = -2; j < 3; j++) {
                    for (int k = -2; k < 3; k++) {
                        if (((i != 0) || (j != 0) || (k != 0)) 
                            && (aStack.getItem().getDigSpeed(aStack, aPlayer.worldObj.getBlock(aX + i, aY + j, aZ + k), aPlayer.worldObj.getBlockMetadata(aX + i, aY + j, aZ + k)) > 0.0F) 
                            && (((EntityPlayerMP) aPlayer).theItemInWorldManager.tryHarvestBlock(aX + i, aY + j, aZ + k))) {
                            rConversions++;
                        }
                    }
                }
            }
            this.sIsHarvestingRightNow.set(null);
        }
        return rConversions;
    }

    @Override
    public IIconContainer getIcon(boolean aIsToolHead, ItemStack aStack) {
        return aIsToolHead ? GT_MetaGenerated_Tool.getPrimaryMaterial(aStack).mIconSet.mTextures[gregtech.api.enums.OrePrefixes.toolHeadSense.mTextureIndex] : GT_MetaGenerated_Tool.getSecondaryMaterial(aStack).mIconSet.mTextures[gregtech.api.enums.OrePrefixes.stick.mTextureIndex];
    }

    @Override
    public short[] getRGBa(boolean aIsToolHead, ItemStack aStack) {
        return aIsToolHead ? GT_MetaGenerated_Tool.getPrimaryMaterial(aStack).mRGBa : GT_MetaGenerated_Tool.getSecondaryMaterial(aStack).mRGBa;
    }

    @Override
    public void onStatsAddedToTool(GT_MetaGenerated_Tool aItem, int aID) {
        aItem.addItemBehavior(aID, new Behaviour_Sense(getToolDamagePerBlockBreak()));
    }

    @Override
    public IChatComponent getDeathMessage(EntityLivingBase aPlayer, EntityLivingBase aEntity) {
        return new ChatComponentText(EnumChatFormatting.GREEN + aPlayer.getCommandSenderName() + EnumChatFormatting.WHITE + " has taken the Soul of " + EnumChatFormatting.RED + aEntity.getCommandSenderName() + EnumChatFormatting.WHITE);
    }
}
