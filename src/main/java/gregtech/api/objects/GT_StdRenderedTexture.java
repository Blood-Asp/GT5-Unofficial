package gregtech.api.objects;

import gregtech.api.enums.Dyes;
import gregtech.api.interfaces.IIconContainer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;

/**
 * This ITexture implementation extends the GT_RenderedTexture class
 * to render with bottom side flipped as with dumb blocks rendering.
 * It is used in Ore blocks rendering so they better blends with dumb block ores
 * from vanilla or other mods, when seen from bottom.
 */
public class GT_StdRenderedTexture extends GT_RenderedTexture{

    @SuppressWarnings("unused")
    public GT_StdRenderedTexture(IIconContainer aIcon, short[] aRGBa, boolean aAllowAlpha) {
        super(aIcon, aRGBa, aAllowAlpha);
    }

    public GT_StdRenderedTexture(IIconContainer aIcon, short[] aRGBa) {
        super(aIcon, aRGBa, true);
    }

    @SuppressWarnings("unused")
    public GT_StdRenderedTexture(IIconContainer aIcon) {
        super(aIcon, Dyes._NULL.mRGBa);
    }

    @Override
    public void renderYNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        final Tessellator tessellator = Tessellator.instance;
        tessellator.setColorRGBA((int) (mRGBa[0] * 0.5F), (int) (mRGBa[1] * 0.5F), (int) (mRGBa[2] * 0.5F), mAllowAlpha ? 255 - mRGBa[3] : 255);
        aRenderer.renderFaceYNeg(aBlock, aX, aY, aZ, mIconContainer.getIcon());
        if (mIconContainer.getOverlayIcon() != null) {
            tessellator.setColorRGBA(128, 128, 128, 255);
            aRenderer.renderFaceYNeg(aBlock, aX, aY, aZ, mIconContainer.getOverlayIcon());
        }
    }
}
