package gregtech.common.items.behaviors;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.items.GT_MetaBase_Item;
import gregtech.api.items.GT_MetaGenerated_Tool;
import gregtech.api.objects.ItemData;
import gregtech.api.objects.XSTR;
import gregtech.api.util.GT_LanguageManager;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;
import gregtech.common.blocks.GT_Block_Ores_Abstract;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.IFluidBlock;

import java.util.List;
import java.util.Random;

public class Behaviour_Prospecting
        extends Behaviour_None {
    private final int mVanillaCosts;
    private final int mEUCosts;
    private final String mTooltip = GT_LanguageManager.addStringLocalization("gt.behaviour.prospecting", "Usable for Prospecting");

    public Behaviour_Prospecting(int aVanillaCosts, int aEUCosts) {
        this.mVanillaCosts = aVanillaCosts;
        this.mEUCosts = aEUCosts;
    }

    public boolean onItemUseFirst(GT_MetaBase_Item aItem, ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ, int aSide, float hitX, float hitY, float hitZ) {
        if (aWorld.isRemote) {
            return false;
        }
      Block aBlock = aWorld.getBlock(aX, aY, aZ);
      if (aBlock == null) {
          return false;
      }
      byte aMeta = (byte) aWorld.getBlockMetadata(aX, aY, aZ);
        ItemData tAssotiation = GT_OreDictUnificator.getAssociation(new ItemStack(aBlock, 1, aMeta));
        if ((tAssotiation != null) && (tAssotiation.mPrefix.toString().startsWith("ore"))){
        	GT_Utility.sendChatToPlayer(aPlayer, trans("100","This is ") + tAssotiation.mMaterial.mMaterial.mDefaultLocalName + trans("101"," Ore."));
        	GT_Utility.sendSoundToPlayers(aWorld, (String) GregTech_API.sSoundList.get(Integer.valueOf(1)), 1.0F, -1.0F, aX, aY, aZ);	
        	return true;
	    	}
	    	
		    if (aBlock.isReplaceableOreGen(aWorld, aX, aY, aZ, Blocks.stone) || 
		    	aBlock.isReplaceableOreGen(aWorld, aX, aY, aZ, Blocks.netherrack) || 
		    	aBlock.isReplaceableOreGen(aWorld, aX, aY, aZ, Blocks.end_stone) ||
		    	aBlock.isReplaceableOreGen(aWorld, aX, aY, aZ, GregTech_API.sBlockStones) ||
		    	aBlock.isReplaceableOreGen(aWorld, aX, aY, aZ, GregTech_API.sBlockGranites) ||
		    	(aBlock == GregTech_API.sBlockOresUb1)  || 
		    	(aBlock == GregTech_API.sBlockOresUb2)  ||  
		    	(aBlock == GregTech_API.sBlockOresUb3)  || 
		    	(aBlock == GregTech_API.sBlockOres1)  ||
		    	(aBlock == GregTech_API.sBlockOresGC)){
	            if (GT_ModHandler.damageOrDechargeItem(aStack, this.mVanillaCosts, this.mEUCosts, aPlayer)) {
              GT_Utility.sendSoundToPlayers(aWorld, (String) GregTech_API.sSoundList.get(Integer.valueOf(1)), 1.0F, -1.0F, aX, aY, aZ);
              int tMetaID = 0;
              int tQuality = (aItem instanceof GT_MetaGenerated_Tool) ? ((GT_MetaGenerated_Tool) aItem).getHarvestLevel(aStack, "") : 0;
		    	int tX = aX, tY = aY, tZ = aZ;
				Block tBlock;
		        for (int i = 0, j = (int)(6 + tQuality); i < j; i++) {
                  tX -= ForgeDirection.getOrientation(aSide).offsetX;
                  tY -= ForgeDirection.getOrientation(aSide).offsetY;
                  tZ -= ForgeDirection.getOrientation(aSide).offsetZ;
		        	
					tBlock = aWorld.getBlock(tX, tY, tZ);
			    	if (tBlock == Blocks.lava || tBlock == Blocks.flowing_lava) {
			    		GT_Utility.sendChatToPlayer(aPlayer, trans("102","There is Lava behind this Rock."));
				    	break;
			    	}
			    	if (tBlock instanceof BlockLiquid || tBlock instanceof IFluidBlock) {
			    		GT_Utility.sendChatToPlayer(aPlayer, trans("103","There is a Liquid behind this Rock."));
				    	break;
					}
			    	if (tBlock == Blocks.monster_egg || !GT_Utility.hasBlockHitBox(aWorld, tX, tY, tZ)) {
			    		GT_Utility.sendChatToPlayer(aPlayer, trans("104","There is an Air Pocket behind this Rock."));
				    	break;
			    	}
			    	if (tBlock != aBlock) {
			    		if (i < 4) GT_Utility.sendChatToPlayer(aPlayer, trans("105","Material is changing behind this Rock."));
				    	break;
			    	}
			    }
		        
				Random tRandom = new XSTR(aX^aY^aZ^aSide);
				for (int i = 0, j = (int)(9+2*tQuality); i < j; i++) {
					tX = aX-4-tQuality+tRandom.nextInt(j);
					tY = aY-4-tQuality+tRandom.nextInt(j);
					tZ = aZ-4-tQuality+tRandom.nextInt(j);
					tBlock = aWorld.getBlock(tX, tY, tZ);
                    if ((tBlock instanceof GT_Block_Ores_Abstract)) {
                        TileEntity tTileEntity = aWorld.getTileEntity(tX, tY, tZ);
                        if ((tTileEntity instanceof GT_TileEntity_Ores)) {
                            Materials tMaterial = GregTech_API.sGeneratedMaterials[(((GT_TileEntity_Ores) tTileEntity).mMetaData % 1000)];
                            if ((tMaterial != null) && (tMaterial != Materials._NULL)) {
                                GT_Utility.sendChatToPlayer(aPlayer, trans("106","Found traces of ") + tMaterial.mDefaultLocalName + trans("101"," Ore."));
                                return true;
                            }
                        }
                    } else {
                        tMetaID = aWorld.getBlockMetadata(tX, tY, tZ);
                        tAssotiation = GT_OreDictUnificator.getAssociation(new ItemStack(tBlock, 1, tMetaID));
                        if ((tAssotiation != null) && (tAssotiation.mPrefix.toString().startsWith("ore"))) {
                            GT_Utility.sendChatToPlayer(aPlayer, trans("106","Found traces of ") + tAssotiation.mMaterial.mMaterial.mDefaultLocalName + trans("101"," Ore."));
                            return true;
                        }
                    }
				}
				GT_Utility.sendChatToPlayer(aPlayer, trans("107","No Ores found."));
	    		return true;
		    }
        }
	return false;
    }

    public List<String> getAdditionalToolTips(GT_MetaBase_Item aItem, List<String> aList, ItemStack aStack) {
        aList.add(this.mTooltip);
        return aList;
    }
}
