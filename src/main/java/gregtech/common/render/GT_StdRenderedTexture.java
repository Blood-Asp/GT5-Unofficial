package gregtech.common.render;

import gregtech.api.interfaces.IIconContainer;
import gregtech.api.util.LightingHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * This ITexture implementation extends the GT_RenderedTexture class
 * to render with bottom side flipped as with dumb blocks rendering.
 * It is used in Ore blocks rendering so they better blends with dumb block ores
 * from vanilla or other mods, when seen from bottom.
 */
class GT_StdRenderedTexture extends GT_RenderedTexture{

    GT_StdRenderedTexture(IIconContainer aIcon, short[] aRGBa, boolean allowAlpha) {
        super(aIcon, aRGBa, allowAlpha);
    }

    @Override
    public void renderYNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        // TODO: Uncomment this once all addons have migrated to the new Texture API
        //startDrawingQuads(aRenderer, 0.0f, -1.0f, 0.0f);
        LightingHelper lighting = new LightingHelper(aRenderer);
        lighting.setupLightingYNeg(aBlock, aX, aY, aZ)
                .setupColor(ForgeDirection.DOWN.ordinal(), getRGBA());
        aRenderer.renderFaceYNeg(aBlock, aX, aY, aZ, mIconContainer.getIcon());
        if (mIconContainer.getOverlayIcon() != null) {
            lighting.setupColor(ForgeDirection.DOWN.ordinal(), 0xffffff);
            aRenderer.renderFaceYNeg(aBlock, aX, aY, aZ, mIconContainer.getOverlayIcon());
        }
        // TODO: Uncomment this once all addons have migrated to the new Texture API
        //draw(aRenderer);
    }
}
