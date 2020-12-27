package gregtech.api.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTech_API;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.util.GT_LanguageManager;
import gregtech.common.blocks.GT_Item_LongDistancePipe;
import gregtech.common.blocks.GT_Material_Machines;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class GT_Block_LongDistancePipe extends GT_Generic_Block {
    public IIconContainer[] mIcons;
    public GT_Block_LongDistancePipe() {
        super(GT_Item_LongDistancePipe.class, "gt.block.longdistancepipe", new GT_Material_Machines());
        setStepSound(soundTypeMetal);
        setCreativeTab(GregTech_API.TAB_GREGTECH);
        GregTech_API.registerMachineBlock(this, -1);
        
        GT_LanguageManager.addStringLocalization(getUnlocalizedName()+".0.name", "Long Distance Fluid Pipeline Pipe");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName()+".1.name", "Long Distance Item Pipeline Pipe");
        GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + 32767 + ".name", "Any Sub Block of this");
        
        ItemList.Long_Distance_Pipeline_Fluid_Pipe.set(new ItemStack(this, 1, 0));
        ItemList.Long_Distance_Pipeline_Item_Pipe.set(new ItemStack(this, 1, 1));
        mIcons = new IIconContainer[]{Textures.BlockIcons.LONG_DISTANCE_PIPE_FLUID, Textures.BlockIcons.LONG_DISTANCE_PIPE_ITEM};
    }
    public void onBlockAdded(World aWorld, int aX, int aY, int aZ) {
        super.onBlockAdded(aWorld, aX, aY, aZ);
        if (GregTech_API.isMachineBlock(this, aWorld.getBlockMetadata(aX, aY, aZ))) {
            GregTech_API.causeMachineUpdate(aWorld, aX, aY, aZ);
        }
    }
    public void breakBlock(World aWorld, int aX, int aY, int aZ, Block par5, int par6) {
        GregTech_API.causeMachineUpdate(aWorld, aX, aY, aZ);
        super.breakBlock(aWorld, aX, aY, aZ, par5, par6);
    }
    public String getHarvestTool(int aMeta) {
        return "wrench";
    }

    public int getHarvestLevel(int aMeta) {
        return 2;
    }

    public String getUnlocalizedName() {
        return this.mUnlocalizedName;
    }

    public String getLocalizedName() {
        return StatCollector.translateToLocal(this.mUnlocalizedName + ".name");
    }
    
    public IIcon getIcon(int aSide, int aMeta) {
        return mIcons[aMeta % mIcons.length].getIcon();
    }

    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess world, int x, int y, int z) {
        return false;
    }

    public int quantityDropped(Random par1Random) {
        return 1;
    }

    public Item getItemDropped(int par1, Random par2Random, int par3) {
        return Item.getItemFromBlock(this);
    }

    public int damageDropped(int par1) {
        return par1;
    }

    public int getDamageValue(World par1World, int par2, int par3, int par4) {
        return par1World.getBlockMetadata(par2, par3, par4);
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item aItem, CreativeTabs par2CreativeTabs, List aList) {
        for (int i = 0; i < 3; i++) {
            ItemStack aStack = new ItemStack(aItem, 1, i);
            if (!aStack.getDisplayName().contains(".name")) aList.add(aStack);
        }
    }
}
