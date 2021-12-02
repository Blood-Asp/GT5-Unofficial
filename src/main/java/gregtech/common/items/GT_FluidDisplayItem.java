package gregtech.common.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@SuppressWarnings({"rawtypes","unchecked"})
public class GT_FluidDisplayItem extends GT_Generic_Item {

    private static final Map<Fluid, String> sFluidTooltips = new HashMap<>();

    public GT_FluidDisplayItem() {
        super("GregTech_FluidDisplay", "Fluid Display", null);
        ItemList.Display_Fluid.set(this);
    }

    @Override
    protected void addAdditionalToolTips(List aList, ItemStack aStack, EntityPlayer aPlayer) {
        if (FluidRegistry.getFluid(aStack.getItemDamage()) != null) {
            String tChemicalFormula = getChemicalFormula(new FluidStack(FluidRegistry.getFluid(aStack.getItemDamage()), 1));
            if (!tChemicalFormula.isEmpty()) aList.add(EnumChatFormatting.YELLOW + tChemicalFormula + EnumChatFormatting.RESET);
        }
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

    @SideOnly(Side.CLIENT)
    public String getChemicalFormula(FluidStack aRealFluid) {
        return sFluidTooltips.computeIfAbsent(aRealFluid.getFluid(),
                fluid -> {
                    for(ItemStack tContainer : GT_Utility.getContainersFromFluid(aRealFluid)) {
                        if (isCell(tContainer)) {
                            Materials tMaterial = getMaterialFromCell(tContainer);
                            if (!tMaterial.equals(Materials._NULL)) {
                                if (tMaterial.mChemicalFormula.equals("?")) {
                                    return "";
                                }
                                else {
                                    return tMaterial.mChemicalFormula;
                                }
                            }
                            else {
                                // For GT++ Fluid Display
                                // GT++ didn't register a Material in GT, so I have too find the Chemical Formula in its cell's tooltip
                                List tTooltip = tContainer.getTooltip(null, true);
                                for (Object tInfo : tTooltip) {
                                    if (!((String) tInfo).contains(" ") && !((String) tInfo).contains(":") && tTooltip.indexOf(tInfo) != 0) {
                                        return (String) tInfo;
                                    }
                                }
                            }
                        }
                    }
                    return "";
        });
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

    public static boolean isCell(ItemStack tItemStack) {
        for (int tOreDict : OreDictionary.getOreIDs(tItemStack)) {
            String tOreDictName = OreDictionary.getOreName(tOreDict);
            if (tOreDictName.startsWith("cell")) return true;
        }
        return false;
    }

    public static Materials getMaterialFromCell(ItemStack tItemStack) {
        for (int tOreDict : OreDictionary.getOreIDs(tItemStack)) {
            String tOreDictName = OreDictionary.getOreName(tOreDict);
            if (tOreDictName.startsWith("cell")) {
                return Materials.getRealMaterial(
                        tOreDictName.replace("cell", "")
                                .replace("Molten", "")
                                .replace("Plasma", "")
                );
            }
        }
        return Materials._NULL;
    }
}
