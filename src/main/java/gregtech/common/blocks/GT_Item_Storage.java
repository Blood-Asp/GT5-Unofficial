package gregtech.common.blocks;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.List;

public class GT_Item_Storage extends ItemBlock {
    public GT_Item_Storage(Block par1) {
        super(par1);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(GregTech_API.TAB_GREGTECH_MATERIALS);
    }

    public String getUnlocalizedName(ItemStack aStack) {
        return this.field_150939_a.getUnlocalizedName() + "." + getDamage(aStack);
    }

    public String getItemStackDisplayName(ItemStack aStack) {
    	String aName = super.getItemStackDisplayName(aStack);
    	if (this.field_150939_a instanceof GT_Block_Metal) {
    		int aDamage = getDamage(aStack);
    		if (aDamage >= 0 && aDamage < ((GT_Block_Metal) this.field_150939_a).mMats.length){
    			Materials aMaterial = ((GT_Block_Metal) this.field_150939_a).mMats[aDamage];
    			if (aMaterial != null)
    				return aMaterial.getLocalizedNameForItem(aName);
    		}
    	}
    	return aName;
    }

    public int getMetadata(int aMeta) {
        return aMeta;
    }

    public void addInformation(ItemStack aStack, EntityPlayer aPlayer, List aList, boolean aF3_H) {
        super.addInformation(aStack, aPlayer, aList, aF3_H);
    }
}