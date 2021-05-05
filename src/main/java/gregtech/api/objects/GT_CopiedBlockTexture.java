package gregtech.api.objects;

import gregtech.api.enums.Dyes;
import gregtech.api.interfaces.ITexture;
import gregtech.api.util.LightingHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;

public class GT_CopiedBlockTexture implements ITexture {
    private final Block mBlock;
    private final byte mSide, mMeta;
    private final boolean mAllowAlpha;
    /**
     * DO NOT MANIPULATE THE VALUES INSIDE THIS ARRAY!!!
     * <p/>
     * Just set this variable to another different Array instead.
     * Otherwise some colored things will get Problems.
     */
    public short[] mRGBa;

    public GT_CopiedBlockTexture(Block aBlock, int aSide, int aMeta, short[] aRGBa, boolean aAllowAlpha) {
        if (aRGBa.length != 4) throw new IllegalArgumentException("RGBa doesn't have 4 Values @ GT_CopiedBlockTexture");
        mBlock = aBlock;
        mRGBa = aRGBa;
        mSide = (byte) aSide;
        mMeta = (byte) aMeta;
        mAllowAlpha = aAllowAlpha;
    }

    public GT_CopiedBlockTexture(Block aBlock, int aSide, int aMeta, short[] aRGBa) {
        this(aBlock, aSide, aMeta, aRGBa, true);
    }

    public GT_CopiedBlockTexture(Block aBlock, int aSide, int aMeta) {
        this(aBlock, aSide, aMeta, Dyes._NULL.mRGBa);
    }

    private IIcon getIcon(int aSide) {
        if (mSide == 6) return mBlock.getIcon(aSide, mMeta);
        return mBlock.getIcon(mSide, mMeta);
    }

    @Override
    public void renderXPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        IIcon aIcon = getIcon(ForgeDirection.EAST.ordinal());
        aRenderer.field_152631_f = true;
        startDrawingQuads(aRenderer, 1.0f, 0.0f, 0.0f);
        new LightingHelper(aRenderer)
                .setupLightingXPos(aBlock, aX, aY, aZ)
                .setupColor(ForgeDirection.EAST.ordinal(), 0xffffff);
        aRenderer.renderFaceXPos(aBlock, aX, aY, aZ, aIcon);
        draw(aRenderer);
        aRenderer.field_152631_f = false;
    }

    @Override
    public void renderXNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        startDrawingQuads(aRenderer, -1.0f, 0.0f, 0.0f);
        IIcon aIcon = getIcon(ForgeDirection.WEST.ordinal());
        new LightingHelper(aRenderer)
                .setupLightingXNeg(aBlock, aX, aY, aZ)
                .setupColor(ForgeDirection.WEST.ordinal(), 0xffffff);
        aRenderer.renderFaceXNeg(aBlock, aX, aY, aZ, aIcon);
        draw(aRenderer);
    }

    @Override
    public void renderYPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        startDrawingQuads(aRenderer, 0.0f, 1.0f, 0.0f);
        IIcon aIcon = getIcon(ForgeDirection.UP.ordinal());
            new LightingHelper(aRenderer)
                    .setupLightingYPos(aBlock, aX, aY, aZ)
                    .setupColor(ForgeDirection.UP.ordinal(), 0xffffff);
        aRenderer.renderFaceYPos(aBlock, aX, aY, aZ, aIcon);
        draw(aRenderer);
    }

    @Override
    public void renderYNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        startDrawingQuads(aRenderer, 0.0f, -1.0f, 0.0f);
        IIcon aIcon = getIcon(ForgeDirection.DOWN.ordinal());
        new LightingHelper(aRenderer)
                .setupLightingYNeg(aBlock, aX, aY, aZ)
                .setupColor(ForgeDirection.DOWN.ordinal(), 0xffffff);
        aRenderer.renderFaceYNeg(aBlock, aX, aY, aZ, aIcon);
        draw(aRenderer);
    }

    @Override
    public void renderZPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        startDrawingQuads(aRenderer, 0.0f, 0.0f, 1.0f);
        IIcon aIcon = getIcon(ForgeDirection.SOUTH.ordinal());
        new LightingHelper(aRenderer)
                .setupLightingZPos(aBlock, aX, aY, aZ)
                .setupColor(ForgeDirection.SOUTH.ordinal(), 0xffffff);
        aRenderer.renderFaceZPos(aBlock, aX, aY, aZ, aIcon);
        draw(aRenderer);
    }

    @Override
    public void renderZNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ) {
        startDrawingQuads(aRenderer, 0.0f, 0.0f, -1.0f);
        IIcon aIcon = getIcon(ForgeDirection.NORTH.ordinal());
        aRenderer.field_152631_f = true;
        new LightingHelper(aRenderer)
                .setupLightingZNeg(aBlock, aX, aY, aZ)
                .setupColor(ForgeDirection.NORTH.ordinal(), 0xffffff);
        aRenderer.renderFaceZNeg(aBlock, aX, aY, aZ, aIcon);
        draw(aRenderer);
        aRenderer.field_152631_f = false;
    }

    @Override
    public boolean isValidTexture() {
        return mBlock != null;
    }

    public Block getBlock() {
        return mBlock;
    }

    public byte getMeta() {
        return mMeta;
    }
}
