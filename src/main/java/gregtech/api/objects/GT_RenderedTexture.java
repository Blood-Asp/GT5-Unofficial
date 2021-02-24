package gregtech.api.objects;

import gregtech.api.enums.Dyes;
import gregtech.api.interfaces.IColorModulationContainer;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.util.LightingHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class GT_RenderedTexture implements ITexture, IColorModulationContainer {
    final IIconContainer mIconContainer;
    final boolean mAllowAlpha;
    /**
     * DO NOT MANIPULATE THE VALUES INSIDE THIS ARRAY!!!
     * <p/>
     * Just set this variable to another different Array instead.
     * Otherwise some colored things will get Problems.
     */
    public short[] mRGBa;

    public GT_RenderedTexture(IIconContainer aIcon, short[] aRGBa, boolean aAllowAlpha) {
        if (aRGBa.length != 4) throw new IllegalArgumentException("RGBa doesn't have 4 Values @ GT_RenderedTexture");
        mIconContainer = aIcon;
        mAllowAlpha = aAllowAlpha;
        mRGBa = aRGBa;
    }

    public GT_RenderedTexture(IIconContainer aIcon, short[] aRGBa) {
        this(aIcon, aRGBa, true);
    }

    public GT_RenderedTexture(IIconContainer aIcon) {
        this(aIcon, Dyes._NULL.mRGBa);
    }

    @Override
    public void renderXPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        aRenderer.field_152631_f = true;
        LightingHelper lighting = new LightingHelper(aRenderer);
        lighting.setupLightingXPos(aBlock, aX, aY, aZ)
                .setupColor(ForgeDirection.EAST.ordinal(), mRGBa);
        aRenderer.renderFaceXPos(aBlock, aX, aY, aZ, mIconContainer.getIcon());
        if (mIconContainer.getOverlayIcon() != null) {
            lighting.setupColor(ForgeDirection.EAST.ordinal(), 0xffffff);
            aRenderer.renderFaceXPos(aBlock, aX, aY, aZ, mIconContainer.getOverlayIcon());
        }
        aRenderer.field_152631_f = false;
    }

    @Override
    public void renderXNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        LightingHelper lighting = new LightingHelper(aRenderer);
        lighting.setupLightingXNeg(aBlock, aX, aY, aZ)
                .setupColor(ForgeDirection.WEST.ordinal(), mRGBa);
        aRenderer.renderFaceXNeg(aBlock, aX, aY, aZ, mIconContainer.getIcon());
        if (mIconContainer.getOverlayIcon() != null) {
            lighting.setupColor(ForgeDirection.WEST.ordinal(), 0xffffff);
            aRenderer.renderFaceXNeg(aBlock, aX, aY, aZ, mIconContainer.getOverlayIcon());
        }
    }

    @Override
    public void renderYPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        LightingHelper lighting = new LightingHelper(aRenderer);
        lighting.setupLightingYPos(aBlock, aX, aY, aZ)
                .setupColor(ForgeDirection.UP.ordinal(), mRGBa);
        aRenderer.renderFaceYPos(aBlock, aX, aY, aZ, mIconContainer.getIcon());
        if (mIconContainer.getOverlayIcon() != null) {
            lighting.setupColor(ForgeDirection.UP.ordinal(), 0xffffff);
            aRenderer.renderFaceYPos(aBlock, aX, aY, aZ, mIconContainer.getOverlayIcon());
        }
    }

    @Override
    public void renderYNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        final Tessellator tessellator = Tessellator.instance;
        IIcon aIcon = mIconContainer.getIcon();

        float minU = aIcon.getInterpolatedU((1.0D - aRenderer.renderMaxX) * 16.0D);
        float maxU = aIcon.getInterpolatedU((1.0D - aRenderer.renderMinX) * 16.0D);
        float minV = aIcon.getInterpolatedV(aRenderer.renderMinZ * 16.0D);
        float maxV = aIcon.getInterpolatedV(aRenderer.renderMaxZ * 16.0D);

        if (aRenderer.renderMinX < 0.0D || aRenderer.renderMaxX > 1.0D) {
            minU = 16.0F - aIcon.getMaxU();
            maxU = 16.0F - aIcon.getMinU();
        }

        if (aRenderer.renderMinZ < 0.0D || aRenderer.renderMaxZ > 1.0D) {
            minV = aIcon.getMinV();
            maxV = aIcon.getMaxV();
        }

        double minX = aX + aRenderer.renderMinX;
        double maxX = aX + aRenderer.renderMaxX;
        double minY = aY + aRenderer.renderMinY;
        double minZ = aZ + aRenderer.renderMinZ;
        double maxZ = aZ + aRenderer.renderMaxZ;

        LightingHelper lighting = new LightingHelper(aRenderer);
        lighting.setupLightingYNeg(aBlock, aX, aY, aZ)
                .setupColor(ForgeDirection.DOWN.ordinal(), mRGBa);

        if (aRenderer.enableAO) {
            tessellator.setColorOpaque_F(aRenderer.colorRedTopLeft, aRenderer.colorGreenTopLeft, aRenderer.colorBlueTopLeft);
            tessellator.setBrightness(aRenderer.brightnessTopLeft);
            tessellator.addVertexWithUV(minX, minY, maxZ, maxU, maxV);
            tessellator.setColorOpaque_F(aRenderer.colorRedBottomLeft, aRenderer.colorGreenBottomLeft, aRenderer.colorBlueBottomLeft);
            tessellator.setBrightness(aRenderer.brightnessBottomLeft);
            tessellator.addVertexWithUV(minX, minY, minZ, maxU, minV);
            tessellator.setColorOpaque_F(aRenderer.colorRedBottomRight, aRenderer.colorGreenBottomRight, aRenderer.colorBlueBottomRight);
            tessellator.setBrightness(aRenderer.brightnessBottomRight);
            tessellator.addVertexWithUV(maxX, minY, minZ, minU, minV);
            tessellator.setColorOpaque_F(aRenderer.colorRedTopRight, aRenderer.colorGreenTopRight, aRenderer.colorBlueTopRight);
            tessellator.setBrightness(aRenderer.brightnessTopRight);
        } else {
            tessellator.addVertexWithUV(minX, minY, maxZ, maxU, maxV);
            tessellator.addVertexWithUV(minX, minY, minZ, maxU, minV);
            tessellator.addVertexWithUV(maxX, minY, minZ, minU, minV);
        }
        tessellator.addVertexWithUV(maxX, minY, maxZ, minU, maxV);

        if (mIconContainer.getOverlayIcon() != null) {
            minU = aIcon.getInterpolatedU((1.0D - aRenderer.renderMaxX) * 16.0D);
            maxU = aIcon.getInterpolatedU((1.0D - aRenderer.renderMinX) * 16.0D);
            minV = aIcon.getInterpolatedV(aRenderer.renderMinZ * 16.0D);
            maxV = aIcon.getInterpolatedV(aRenderer.renderMaxZ * 16.0D);

            if (aRenderer.renderMinX < 0.0D || aRenderer.renderMaxX > 1.0D) {
                minU = 16.0F - aIcon.getMaxU();
                maxU = 16.0F - aIcon.getMinU();
            }

            if (aRenderer.renderMinZ < 0.0D || aRenderer.renderMaxZ > 1.0D) {
                minV = aIcon.getMinV();
                maxV = aIcon.getMaxV();
            }

            minX = aX + (float)aRenderer.renderMinX;
            maxX = aX + (float)aRenderer.renderMaxX;
            minY = aY + (float)aRenderer.renderMinY;
            minZ = aZ + (float)aRenderer.renderMinZ;
            maxZ = aZ + (float)aRenderer.renderMaxZ;

            lighting.setupColor(ForgeDirection.DOWN.ordinal(), 0xffffff);

            if (aRenderer.enableAO) {
                tessellator.setColorOpaque_F(aRenderer.colorRedTopLeft, aRenderer.colorGreenTopLeft, aRenderer.colorBlueTopLeft);
                tessellator.setBrightness(aRenderer.brightnessTopLeft);
                tessellator.addVertexWithUV(minX, minY, maxZ, maxU, maxV);
                tessellator.setColorOpaque_F(aRenderer.colorRedBottomLeft, aRenderer.colorGreenBottomLeft, aRenderer.colorBlueBottomLeft);
                tessellator.setBrightness(aRenderer.brightnessBottomLeft);
                tessellator.addVertexWithUV(minX, minY, minZ, maxU, minV);
                tessellator.setColorOpaque_F(aRenderer.colorRedBottomRight, aRenderer.colorGreenBottomRight, aRenderer.colorBlueBottomRight);
                tessellator.setBrightness(aRenderer.brightnessBottomRight);
                tessellator.addVertexWithUV(maxX, minY, minZ, minU, minV);
                tessellator.setColorOpaque_F(aRenderer.colorRedTopRight, aRenderer.colorGreenTopRight, aRenderer.colorBlueTopRight);
                tessellator.setBrightness(aRenderer.brightnessTopRight);
            } else {
                tessellator.addVertexWithUV(minX, minY, maxZ, maxU, maxV);
                tessellator.addVertexWithUV(minX, minY, minZ, maxU, minV);
                tessellator.addVertexWithUV(maxX, minY, minZ, minU, minV);
            }
            tessellator.addVertexWithUV(maxX, minY, maxZ, minU, maxV);
        }
    }

    @Override
    public void renderZPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        LightingHelper lighting = new LightingHelper(aRenderer);
        lighting.setupLightingZPos(aBlock, aX, aY, aZ)
                .setupColor(ForgeDirection.SOUTH.ordinal(), mRGBa);
        aRenderer.renderFaceZPos(aBlock, aX, aY, aZ, mIconContainer.getIcon());
        if (mIconContainer.getOverlayIcon() != null) {
            lighting.setupColor(ForgeDirection.SOUTH.ordinal(), 0xffffff);
            aRenderer.renderFaceZPos(aBlock, aX, aY, aZ, mIconContainer.getOverlayIcon());
        }
    }

    @Override
    public void renderZNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        aRenderer.field_152631_f = true;
        LightingHelper lighting = new LightingHelper(aRenderer);
        lighting.setupLightingZNeg(aBlock, aX, aY, aZ)
                .setupColor(ForgeDirection.NORTH.ordinal(), mRGBa);
        aRenderer.renderFaceZNeg(aBlock, aX, aY, aZ, mIconContainer.getIcon());
        if (mIconContainer.getOverlayIcon() != null) {
            lighting.setupColor(ForgeDirection.NORTH.ordinal(), 0xffffff);
            aRenderer.renderFaceZNeg(aBlock, aX, aY, aZ, mIconContainer.getOverlayIcon());
        }
        aRenderer.field_152631_f = false;
    }

    @Override
    public short[] getRGBA() {
        return mRGBa;
    }

    @Override
    public boolean isValidTexture() {
        return mIconContainer != null;
    }
}