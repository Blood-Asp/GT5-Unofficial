package gregtech.common.items;

import com.google.common.collect.ImmutableMap;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import forestry.api.core.Tabs;
import forestry.api.recipes.RecipeManagers;
import gregtech.GT_Mod;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;
import gregtech.loaders.materialprocessing.ProcessingModSupport;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import static gregtech.api.enums.GT_Values.MOD_ID;

public class ItemDrop extends Item {
	@SideOnly(Side.CLIENT)
	private IIcon secondIcon;

	public ItemDrop() {
		super();
		this.setCreativeTab(Tabs.tabApiculture);
		this.setHasSubtypes(true);
		this.setUnlocalizedName("gt.drop");
		GameRegistry.registerItem(this, "gt.drop", MOD_ID);
	}

	public ItemStack getStackForType(DropType type) {
		return new ItemStack(this, 1, type.ordinal());
	}

	public ItemStack getStackForType(DropType type, int count) {
		return new ItemStack(this, count, type.ordinal());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tabs, List list) {
		for (DropType type : DropType.values()) {
			if (type.showInList) {
				list.add(this.getStackForType(type));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getRenderPasses(int meta) {
		return 2;
	}

	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		this.itemIcon = par1IconRegister.registerIcon("forestry:honeyDrop.0");
		this.secondIcon = par1IconRegister.registerIcon("forestry:honeyDrop.1");
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		return (pass == 0) ? itemIcon : secondIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		int meta = Math.max(0, Math.min(DropType.values().length - 1, stack.getItemDamage()));
		int colour = DropType.values()[meta].getColours()[0];

		if (pass >= 1) {
			colour = DropType.values()[meta].getColours()[1];
		}

		return colour;
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return DropType.values()[stack.getItemDamage()].getName();
	}
	public void initDropsRecipes() {
		ItemStack tDrop;

		tDrop = getStackForType(DropType.COOLANT);
		//addRecipe(tDrop, aOutput, aOutput2, aChance, aEUt);
	}

	public void addRecipe(ItemStack tDrop, FluidStack aOutput, ItemStack aOutput2, int aChance, int aEUt) {
		GT_Values.RA.addFluidExtractionRecipe(tDrop, aOutput2, aOutput, aChance, 40, aEUt);
	}

}