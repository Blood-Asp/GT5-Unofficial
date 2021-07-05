package gregtech.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.items.GT_Generic_Item;
import gregtech.api.util.GT_Utility;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@SuppressWarnings({"rawtypes","unchecked"})
public class GT_FluidDisplayItem extends GT_Generic_Item {
    public GT_FluidDisplayItem() {
        super("GregTech_FluidDisplay", "Fluid Display", null);
        ItemList.Display_Fluid.set(this);
    }

    @Override
    protected void addAdditionalToolTips(List aList, ItemStack aStack, EntityPlayer aPlayer) {
        NBTTagCompound aNBT = aStack.getTagCompound();
        if (GT_Values.D1) {
            Fluid tFluid = FluidRegistry.getFluid(aStack.getItemDamage());
            if (tFluid != null) {
                aList.add("Registry: " + tFluid.getName());
            }
        }
        if (aNBT != null) {
            long tToolTipAmount = aNBT.getLong("mFluidDisplayAmount");
            if (tToolTipAmount > 0L) {
            	aList.add(EnumChatFormatting.BLUE + String.format(trans("016", "Amount: %s L"), "" + tToolTipAmount) + EnumChatFormatting.GRAY);
            }
            aList.add(EnumChatFormatting.RED + String.format(trans("017", "Temperature: %s K"), "" + aNBT.getLong("mFluidDisplayHeat")) + EnumChatFormatting.GRAY);
            aList.add(EnumChatFormatting.GREEN + String.format(trans("018", "State: %s"), aNBT.getBoolean("mFluidState") ? "Gas" : "Liquid") + EnumChatFormatting.GRAY);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister aIconRegister) {
    }

    @Override
    public IIcon getIconFromDamage(int aMeta) {
        return Stream.of(FluidRegistry.getFluid(aMeta), FluidRegistry.WATER)
                .filter(Objects::nonNull)
                .map(Fluid::getStillIcon)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(IllegalStateException::new);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack aStack, int aRenderPass) {
        Fluid tFluid = FluidRegistry.getFluid(aStack.getItemDamage());
        return tFluid == null ? 16777215 : tFluid.getColor();
    }

    @Override
    public int getSpriteNumber() {
        return 0;
    }

    @Override
    public String getUnlocalizedName(ItemStack aStack) {
        if (aStack != null) {
            return GT_Utility.getFluidName(FluidRegistry.getFluid(aStack.getItemDamage()), false);
        }
        return "";
    }

    @Override
    public String getItemStackDisplayName(ItemStack aStack) {
        if (aStack != null) {
            return GT_Utility.getFluidName(FluidRegistry.getFluid(aStack.getItemDamage()), true);
        }
        return "";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item aItem, CreativeTabs aTab, List aList) {
        if (GT_Values.D1) {
            int i = 0;
            for (int j = FluidRegistry.getMaxID(); i < j; i++) {
                ItemStack tStack = GT_Utility.getFluidDisplayStack(FluidRegistry.getFluid(i));
                if (tStack != null) {
                    aList.add(tStack);
                }
            }
        }
    }
}
