package gregtech.api.objects;

import gregtech.api.enums.Dyes;
import gregtech.api.interfaces.IColorModulationContainer;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;

public class GT_SidedTexture implements ITexture, IColorModulationContainer {
    private final ITexture[] mTextures;
    /**
     * DO NOT MANIPULATE THE VALUES INSIDE THIS ARRAY!!!
     * <p/>
     * Just set this variable to another different Array instead.
     * Otherwise some colored things will get Problems.
     */
    public short[] mRGBa;

    public GT_SidedTexture(IIconContainer aIcon0, IIconContainer aIcon1, IIconContainer aIcon2, IIconContainer aIcon3, IIconContainer aIcon4, IIconContainer aIcon5, short[] aRGBa, boolean aAllowAlpha) {
        if (aRGBa.length != 4) throw new IllegalArgumentException("RGBa doesn't have 4 Values @ GT_RenderedTexture");
        mTextures = new ITexture[]{
            new GT_RenderedTexture(aIcon0, aRGBa, aAllowAlpha),
            new GT_RenderedTexture(aIcon1, aRGBa, aAllowAlpha),
            new GT_RenderedTexture(aIcon2, aRGBa, aAllowAlpha),
            new GT_RenderedTexture(aIcon3, aRGBa, aAllowAlpha),
            new GT_RenderedTexture(aIcon4, aRGBa, aAllowAlpha),
            new GT_RenderedTexture(aIcon5, aRGBa, aAllowAlpha)
        };
        mRGBa = aRGBa;
    }

    public GT_SidedTexture(IIconContainer aIcon0, IIconContainer aIcon1, IIconContainer aIcon2, IIconContainer aIcon3, IIconContainer aIcon4, IIconContainer aIcon5, short[] aRGBa) {
        this(aIcon0, aIcon1, aIcon2, aIcon3, aIcon4, aIcon5, aRGBa, true);
    }

    public GT_SidedTexture(IIconContainer aIcon0, IIconContainer aIcon1, IIconContainer aIcon2, IIconContainer aIcon3, IIconContainer aIcon4, IIconContainer aIcon5) {
        this(aIcon0, aIcon1, aIcon2, aIcon3, aIcon4, aIcon5, Dyes._NULL.mRGBa);
    }

    public GT_SidedTexture(IIconContainer aBottom, IIconContainer aTop, IIconContainer aSides, short[] aRGBa) {
        this(aBottom, aTop, aSides, aSides, aSides, aSides, aRGBa);
    }

    public GT_SidedTexture(IIconContainer aBottom, IIconContainer aTop, IIconContainer aSides) {
        this(aBottom, aTop, aSides, Dyes._NULL.mRGBa);
    }

    @Override
    public void renderXPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        mTextures[5].renderXPos(aRenderer, aBlock, aX ,aY, aZ);
    }

    @Override
    public void renderXNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        mTextures[4].renderXNeg(aRenderer, aBlock, aX ,aY, aZ);
    }

    @Override
    public void renderYPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        mTextures[1].renderYPos(aRenderer, aBlock, aX ,aY, aZ);
    }

    @Override
    public void renderYNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        mTextures[0].renderYNeg(aRenderer, aBlock, aX ,aY, aZ);
    }

    @Override
    public void renderZPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        mTextures[3].renderZPos(aRenderer, aBlock, aX ,aY, aZ);
    }

    @Override
    public void renderZNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        mTextures[2].renderZNeg(aRenderer, aBlock, aX ,aY, aZ);
    }

    @Override
    public short[] getRGBA() {
        return mRGBa;
    }

    @Override
    public boolean isValidTexture() {
        for (ITexture renderedTexture : mTextures) {
            if (!renderedTexture.isValidTexture()) return false;
        }
        return true;
    }
}