package gregtech.common.render;

import gregtech.api.interfaces.IIconContainer;
import gregtech.api.items.GT_MetaGenerated_Item;
import gregtech.api.util.GT_Utility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

public class GT_MetaGenerated_Item_Renderer implements IItemRenderer {
    public GT_MetaGenerated_Item_Renderer() {
        for (GT_MetaGenerated_Item item : GT_MetaGenerated_Item.sInstances.values()) {
            MinecraftForgeClient.registerItemRenderer(item, this);
        }
    }

    @Override
    public boolean handleRenderType(ItemStack aStack, IItemRenderer.ItemRenderType aType) {
        if ((GT_Utility.isStackInvalid(aStack)) || (aStack.getItemDamage() < 0)) {
            return false;
        }
        return (aType == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) || (aType == IItemRenderer.ItemRenderType.INVENTORY) || (aType == IItemRenderer.ItemRenderType.EQUIPPED) || (aType == IItemRenderer.ItemRenderType.ENTITY);
    }

    @Override
    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType aType, ItemStack aStack, IItemRenderer.ItemRendererHelper aHelper) {
        if (GT_Utility.isStackInvalid(aStack)) {
            return false;
        }
        return aType == IItemRenderer.ItemRenderType.ENTITY;
    }

    @Override
    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack aStack, Object... data) {
        if (GT_Utility.isStackInvalid(aStack)) {
            return;
        }
        short aMetaData = (short) aStack.getItemDamage();
        if (aMetaData < 0) {
            return;
        }
        GT_MetaGenerated_Item aItem = (GT_MetaGenerated_Item) aStack.getItem();


        GL11.glEnable(GL11.GL_BLEND);
        if (type == IItemRenderer.ItemRenderType.ENTITY) {
            if (RenderItem.renderInFrame) {
                GL11.glScalef(0.85F, 0.85F, 0.85F);
                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                GL11.glTranslated(-0.5D, -0.42D, 0.0D);
            } else {
                GL11.glTranslated(-0.5D, -0.42D, 0.0D);
            }
        }
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        if (aMetaData < aItem.mOffset) {
            IIconContainer aIcon = aItem.getIconContainer(aMetaData);
            IIcon tOverlay = null;
            IIcon fluidIcon = null;
            IIcon tIcon;
            if (aIcon == null) {
                tIcon = aStack.getIconIndex();
            } else {
                tIcon = aIcon.getIcon();
                tOverlay = aIcon.getOverlayIcon();
            }
            if (tIcon == null) {
                return;
            }
            FluidStack tFluid = GT_Utility.getFluidForFilledItem(aStack, true);
            if (tOverlay != null && tFluid != null && tFluid.getFluid() != null) {
                fluidIcon = tFluid.getFluid().getIcon(tFluid);
            }
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            if (fluidIcon == null) {
                short[] tModulation = aItem.getRGBa(aStack);
                GL11.glColor3f(tModulation[0] / 255.0F, tModulation[1] / 255.0F, tModulation[2] / 255.0F);
            }
            if (type.equals(IItemRenderer.ItemRenderType.INVENTORY)) {
                GT_RenderUtil.renderItemIcon(tIcon, 16.0D, 0.001D, 0.0F, 0.0F, -1.0F);
            } else {
                ItemRenderer.renderItemIn2D(Tessellator.instance, tIcon.getMaxU(), tIcon.getMinV(), tIcon.getMinU(), tIcon.getMaxV(), tIcon.getIconWidth(), tIcon.getIconHeight(), 0.0625F);
            }
            if (fluidIcon != null) {
                int tColor = tFluid.getFluid().getColor(tFluid);
                GL11.glColor3f((tColor >> 16 & 0xFF) / 255.0F, (tColor >> 8 & 0xFF) / 255.0F, (tColor & 0xFF) / 255.0F);
                Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                GL11.glDepthFunc(GL11.GL_EQUAL);
                if (type.equals(IItemRenderer.ItemRenderType.INVENTORY)) {
                    GT_RenderUtil.renderItemIcon(fluidIcon, 16.0D, 0.001D, 0.0F, 0.0F, -1.0F);
                } else {
                    ItemRenderer.renderItemIn2D(Tessellator.instance, fluidIcon.getMaxU(), fluidIcon.getMinV(), fluidIcon.getMinU(), fluidIcon.getMaxV(), fluidIcon.getIconWidth(), fluidIcon.getIconHeight(), 0.0625F);
                }
                GL11.glDepthFunc(GL11.GL_LEQUAL);
            }
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            if (tOverlay != null) {
                Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                if (type.equals(IItemRenderer.ItemRenderType.INVENTORY)) {
                    GT_RenderUtil.renderItemIcon(tOverlay, 16.0D, 0.001D, 0.0F, 0.0F, -1.0F);
                } else {
                    ItemRenderer.renderItemIn2D(Tessellator.instance, tOverlay.getMaxU(), tOverlay.getMinV(), tOverlay.getMinU(), tOverlay.getMaxV(), tOverlay.getIconWidth(), tOverlay.getIconHeight(), 0.0625F);
                }
            }
        } else {
            IIcon tIcon;
            if (aItem.mIconList[(aMetaData - aItem.mOffset)].length > 1) {
                Long[] tStats = aItem.mElectricStats.get(aMetaData);

                if ((tStats != null) && (tStats[3] < 0L)) {
                    long tCharge = aItem.getRealCharge(aStack);

                    if (tCharge <= 0L) {
                        tIcon = aItem.mIconList[(aMetaData - aItem.mOffset)][1];
                    } else {

                        if (tCharge >= tStats[0]) {
                            tIcon = aItem.mIconList[(aMetaData - aItem.mOffset)][8];
                        } else {
                            tIcon = aItem.mIconList[(aMetaData - aItem.mOffset)][(7 - (int) Math.max(0L, Math.min(5L, (tStats[0] - tCharge) * 6L / tStats[0])))];
                        }
                    }
                } else {
                    tIcon = aItem.mIconList[(aMetaData - aItem.mOffset)][0];
                }
            } else {
                tIcon = aItem.mIconList[(aMetaData - aItem.mOffset)][0];
            }
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            if (type.equals(IItemRenderer.ItemRenderType.INVENTORY)) {
                GT_RenderUtil.renderItemIcon(tIcon, 16.0D, 0.001D, 0.0F, 0.0F, -1.0F);
            } else {
                ItemRenderer.renderItemIn2D(Tessellator.instance, tIcon.getMaxU(), tIcon.getMinV(), tIcon.getMinU(), tIcon.getMaxV(), tIcon.getIconWidth(), tIcon.getIconHeight(), 0.0625F);
            }
        }
        GL11.glDisable(GL11.GL_BLEND);
    }
}
