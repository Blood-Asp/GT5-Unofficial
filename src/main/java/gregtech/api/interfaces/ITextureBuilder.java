package gregtech.api.interfaces;

import gregtech.api.render.TextureFactory;
import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * <p>This Interface defines operations to configure and build instances of the {@link ITexture} implementations</p>
 * <p>Use the {@link TextureFactory#builder()} method to get an instance of the {@link ITextureBuilder} implementation.</p>
 */
public interface ITextureBuilder {
    /**
     * Build the {@link ITexture}
     *
     * @return The built {@link ITexture}
     */
    ITexture build();

    /**
     * @param block The {@link Block}
     * @param meta The meta value for the Block
     * @return {@link ITextureBuilder} for chaining
     */
    ITextureBuilder setFromBlock(Block block, int meta);

    /**
     * @param side <p>The {@link ForgeDirection} side providing the texture</p>
     *             <p>Default is {@link ForgeDirection#UNKNOWN} to use same side as rendered</p>
     * @return {@link ITextureBuilder} for chaining
     */
    ITextureBuilder setFromSide(ForgeDirection side);

    /**
     * @param iconContainers The {@link IIconContainer}s to add
     * @return {@link ITextureBuilder} for chaining
     */
    ITextureBuilder addIcon(IIconContainer... iconContainers);

    /**
     * @param rgba The RGBA tint for this {@link ITexture}
     * @return {@link ITextureBuilder} for chaining
     */
    ITextureBuilder setRGBA(short[] rgba);

    /**
     * @param iTextures The {@link ITexture} layers to add
     * @return {@link ITextureBuilder} for chaining
     */
    ITextureBuilder addLayer(ITexture... iTextures);

    /**
     * Set alpha blending
     * @param allowAlpha to set
     *
     * @return {@link ITextureBuilder} for chaining
     */
    ITextureBuilder setAllowAlpha(boolean allowAlpha);

    /**
     * Texture will render with same orientation as with vanilla blocks
     *
     * @return {@link ITextureBuilder} for chaining
     */
    ITextureBuilder stdOrient();

    /**
     * Texture always render with full brightness to glow in the dark
     *
     * @return {@link ITextureBuilder} for chaining
     */
    ITextureBuilder glow();
}
