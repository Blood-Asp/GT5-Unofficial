package gregtech.api.interfaces;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;

public interface ITexture {
    void renderXPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ);

    void renderXNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ);

    void renderYPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ);

    void renderYNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ);

    void renderZPos(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ);

    void renderZNeg(RenderBlocks aRenderer, Block aBlock, int aX, int aY, int aZ);

    boolean isValidTexture();
}