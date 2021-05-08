package gregtech.api.render;

import gregtech.GT_Mod;
import gregtech.api.enums.Dyes;
import gregtech.api.enums.Textures.BlockIcons;
import gregtech.api.interfaces.IColorModulationContainer;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

import static gregtech.api.util.LightingHelper.MAX_BRIGHTNESS;

public class GT_RenderedGlowTexture implements ITexture, IColorModulationContainer {
    final IIconContainer mIconContainer;
    final boolean mAllowAlpha;
    /**
     * DO NOT MANIPULATE THE VALUES INSIDE THIS ARRAY!!!
     * <p/>
     * Just set this variable to another different Array instead.
     * Otherwise some colored things will get Problems.
     */
    public short[] mRGBa;

    public GT_RenderedGlowTexture(IIconContainer aIcon) {
        this(aIcon, Dyes._NULL.mRGBa);
    }

    public GT_RenderedGlowTexture(IIconContainer aIcon, short[] aRGBa) {
        this(aIcon, aRGBa, true);
    }

    public GT_RenderedGlowTexture(IIconContainer aIcon, short[] aRGBa, boolean aAllowAlpha) {
        if (aRGBa.length != 4) throw new IllegalArgumentException("RGBa doesn't have 4 Values @ GT_RenderedTexture");
        mIconContainer = GT_Mod.gregtechproxy.mRenderGlowTextures ? aIcon : BlockIcons.VOID;
        mAllowAlpha = aAllowAlpha;
        mRGBa = aRGBa;
    }

    @Override
    public short[] getRGBA() {
        return mRGBa;
    }

    @Override
    public void renderXPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        if (!GT_Mod.gregtechproxy.mRenderGlowTextures) return;
        aRenderer.field_152631_f = true;
        final boolean enableAO = aRenderer.enableAO;
        aRenderer.enableAO = false;
        startDrawingQuads(aRenderer, 1.0f, 0.0f, 0.0f);
        Tessellator.instance.setBrightness(MAX_BRIGHTNESS);
        Tessellator.instance.setColorOpaque(mRGBa[0], mRGBa[1], mRGBa[2]);
        aRenderer.renderFaceXPos(aBlock, aX, aY, aZ, mIconContainer.getIcon());
        if (mIconContainer.getOverlayIcon() != null) {
            Tessellator.instance.setColorOpaque(255, 255, 255);
            aRenderer.renderFaceXPos(aBlock, aX, aY, aZ, mIconContainer.getOverlayIcon());
        }
        draw(aRenderer);
        aRenderer.field_152631_f = false;
        aRenderer.enableAO = enableAO;
    }

    @Override
    public void renderXNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        if (!GT_Mod.gregtechproxy.mRenderGlowTextures) return;
        final boolean enableAO = aRenderer.enableAO;
        aRenderer.enableAO = false;
        startDrawingQuads(aRenderer, -1.0f, 0.0f, 0.0f);
        Tessellator.instance.setBrightness(MAX_BRIGHTNESS);
        Tessellator.instance.setColorOpaque(mRGBa[0], mRGBa[1], mRGBa[2]);
        aRenderer.renderFaceXNeg(aBlock, aX, aY, aZ, mIconContainer.getIcon());
        if (mIconContainer.getOverlayIcon() != null) {
            Tessellator.instance.setColorOpaque(255, 255, 255);
            aRenderer.renderFaceXNeg(aBlock, aX, aY, aZ, mIconContainer.getOverlayIcon());
        }
        draw(aRenderer);
        aRenderer.enableAO = enableAO;
    }

    @Override
    public void renderYPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        if (!GT_Mod.gregtechproxy.mRenderGlowTextures) return;
        final boolean enableAO = aRenderer.enableAO;
        aRenderer.enableAO = false;
        startDrawingQuads(aRenderer, 0.0f, 1.0f, 0.0f);
        Tessellator.instance.setBrightness(MAX_BRIGHTNESS);
        Tessellator.instance.setColorOpaque(mRGBa[0], mRGBa[1], mRGBa[2]);
        aRenderer.renderFaceYPos(aBlock, aX, aY, aZ, mIconContainer.getIcon());
        if (mIconContainer.getOverlayIcon() != null) {
            Tessellator.instance.setColorOpaque(255, 255, 255);
            aRenderer.renderFaceYPos(aBlock, aX, aY, aZ, mIconContainer.getOverlayIcon());
        }
        draw(aRenderer);
        aRenderer.enableAO = enableAO;
    }

    @Override
    public void renderYNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        if (!GT_Mod.gregtechproxy.mRenderGlowTextures) return;
        final boolean enableAO = aRenderer.enableAO;
        startDrawingQuads(aRenderer, 0.0f, -1.0f, 0.0f);
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

        Tessellator.instance.setBrightness(MAX_BRIGHTNESS);
        Tessellator.instance.setColorOpaque(mRGBa[0], mRGBa[1], mRGBa[2]);

        tessellator.addVertexWithUV(minX, minY, maxZ, maxU, maxV);
        tessellator.addVertexWithUV(minX, minY, minZ, maxU, minV);
        tessellator.addVertexWithUV(maxX, minY, minZ, minU, minV);
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

            minX = aX + (float) aRenderer.renderMinX;
            maxX = aX + (float) aRenderer.renderMaxX;
            minY = aY + (float) aRenderer.renderMinY;
            minZ = aZ + (float) aRenderer.renderMinZ;
            maxZ = aZ + (float) aRenderer.renderMaxZ;

            Tessellator.instance.setColorOpaque(255, 255, 255);
            tessellator.addVertexWithUV(minX, minY, maxZ, maxU, maxV);
            tessellator.addVertexWithUV(minX, minY, minZ, maxU, minV);
            tessellator.addVertexWithUV(maxX, minY, minZ, minU, minV);
            tessellator.addVertexWithUV(maxX, minY, maxZ, minU, maxV);
        }
        draw(aRenderer);
        aRenderer.enableAO = enableAO;
    }

    @Override
    public void renderZPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        if (!GT_Mod.gregtechproxy.mRenderGlowTextures) return;
        final boolean enableAO = aRenderer.enableAO;
        aRenderer.enableAO = false;
        startDrawingQuads(aRenderer, 0.0f, 0.0f, 1.0f);
        Tessellator.instance.setBrightness(MAX_BRIGHTNESS);
        Tessellator.instance.setColorOpaque(mRGBa[0], mRGBa[1], mRGBa[2]);
        aRenderer.renderFaceZPos(aBlock, aX, aY, aZ, mIconContainer.getIcon());
        if (mIconContainer.getOverlayIcon() != null) {
            aRenderer.renderFaceZPos(aBlock, aX, aY, aZ, mIconContainer.getOverlayIcon());
        }
        draw(aRenderer);
        aRenderer.enableAO = enableAO;
    }

    @Override
    public void renderZNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        if (!GT_Mod.gregtechproxy.mRenderGlowTextures) return;
        final boolean enableAO = aRenderer.enableAO;
        aRenderer.enableAO = false;
        aRenderer.field_152631_f = true;
        startDrawingQuads(aRenderer, 0.0f, 0.0f, -1.0f);
        Tessellator.instance.setBrightness(MAX_BRIGHTNESS);
        Tessellator.instance.setColorOpaque(mRGBa[0], mRGBa[1], mRGBa[2]);
        aRenderer.renderFaceZNeg(aBlock, aX, aY, aZ, mIconContainer.getIcon());
        if (mIconContainer.getOverlayIcon() != null) {
            Tessellator.instance.setColorOpaque(255, 255, 255);
            aRenderer.renderFaceZNeg(aBlock, aX, aY, aZ, mIconContainer.getOverlayIcon());
        }
        draw(aRenderer);
        aRenderer.field_152631_f = false;
        aRenderer.enableAO = enableAO;
    }

    @Override
    public boolean isValidTexture() {
        return mIconContainer != null;
    }
}
