package gregtech.api.render;

import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.ITextureBuilder;
import gregtech.common.render.GT_TextureBuilder;
import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * This class contains a collection of static factory methods to help to use the New Texture API
 * <p>The {@link #of} methods directly returns ready-to-use instances of {@link ITexture} implementations.</p>
 * <p>To get more specific implementations of {@link ITexture} instances, use the {@link #builder()} method.</p>
 * <p>Example of the {@link #builder()}:</p>
 * <pre>{@code
 *     // Texture that glows in the dark
 *     TextureFactory.builder().addIcon(OVERLAY_FUSION1_GLOW).glow().build());
 *
 *     // Texture with same bottom flipped orientation as vanilla
 *     TextureFactory.builder().addIcon(GRANITE_RED_STONE).stdOrient().build();
 * }</pre>
 * See: the {@link ITextureBuilder} interface
 */
@SuppressWarnings("unused")
public final class TextureFactory {
    private TextureFactory() {
        throw new AssertionError("Non instantiable class");
    }

    /**
     * Multi-layered {@link ITexture} factory
     * @param textures The layers of {@link ITexture} from bottom to top
     * @return The instance of an {@link ITexture} implementation
     */
    public static ITexture of(final ITexture... textures) {
        return builder().addLayer(textures).build();
    }

    /**
     * All-Sided {@link ITexture} factory
     * @param bottomIconContainers The {@link IIconContainer} Icon for the Bottom Side.
     * @param topIconContainers The {@link IIconContainer} Icon for the Top Side.
     * @param northIconContainers The {@link IIconContainer} Icon for the North Side.
     * @param southIconContainers The {@link IIconContainer} Icon for the South Side.
     * @param westIconContainers The {@link IIconContainer} Icon for the West Side.
     * @param eastIconContainers The {@link IIconContainer} Icon for the East Side.
     * @param rgba The {@code short[]} tint for all sides.
     * @return The instance of an {@link ITexture} implementation
     */
    public static ITexture of(final IIconContainer bottomIconContainers,
                              final IIconContainer topIconContainers,
                              final IIconContainer northIconContainers,
                              final IIconContainer southIconContainers,
                              final IIconContainer westIconContainers,
                              final IIconContainer eastIconContainers,
                              final short[] rgba) {
        return builder().addIcon(bottomIconContainers,
                topIconContainers,
                northIconContainers,
                southIconContainers,
                westIconContainers,
                eastIconContainers)
                .setRGBA(rgba)
                .setAllowAlpha(true)
                .build();
    }

    /**
     * Bottom-Top-Sides-Sided {@link ITexture} factory
     * @param aBottomIconContainers The {@link IIconContainer} Icon for the Bottom Side.
     * @param aTopIconContainers The {@link IIconContainer} Icon for the Top Side.
     * @param aSideIconContainers The {@link IIconContainer} Icon for the North, South, West and East Sides.
     * @param rgba The {@code short[]} tint for all sides.
     * @return The instance of an {@link ITexture} implementation
     */
    public static ITexture of(IIconContainer aBottomIconContainers,
                              IIconContainer aTopIconContainers,
                              IIconContainer aSideIconContainers, short[] rgba) {
        return builder().addIcon(aBottomIconContainers,
                aTopIconContainers,
                aSideIconContainers,
                aSideIconContainers,
                aSideIconContainers,
                aSideIconContainers)
                .setRGBA(rgba)
                .setAllowAlpha(true)
                .build();
    }

    /**
     * Rendered {@link ITexture} factory
     * @param iconContainer The {@link IIconContainer} to render
     * @param rgba The {@code short[]} tint for the texture.
     * @param allowAlpha Determine if texture will use alpha blending (Not yet implemented)
     * @return The instance of an {@link ITexture} implementation
     */
    public static ITexture of(IIconContainer iconContainer, short[] rgba, boolean allowAlpha) {
        return builder().addIcon(iconContainer).setRGBA(rgba).setAllowAlpha(allowAlpha).build();
    }

    public static ITexture of(IIconContainer iconContainer, short[] rgba) {
        return builder().addIcon(iconContainer).setRGBA(rgba).build();
    }

    public static ITexture of(IIconContainer iconContainer) {
        return builder().addIcon(iconContainer).build();
    }

    /**
     * Copied-Block {@link ITexture} factory
     * that will render a texture copied from the side of a {@link Block}.
     *
     * @param block The {@link Block} that will provide the texture
     * @param meta  The meta value for the Block
     * @param side  The {@link ForgeDirection} side providing the texture
     * @param rgba  The RGBA tint to apply
     * @return The instance of an {@link ITexture} implementation
     */
    public static ITexture of(Block block, int meta, ForgeDirection side, short[] rgba) {
        return builder().setFromBlock(block, meta)
                .setFromSide(side)
                .setRGBA(rgba)
                .build();
    }

    public static ITexture of(Block block, int meta, ForgeDirection side) {
        return builder().setFromBlock(block, meta)
                .setFromSide(side)
                .build();
    }

    public static ITexture of(Block block, int meta) {
        return builder().setFromBlock(block, meta).build();
    }

    public static ITexture of(Block block) {
        return of(block, 0);
    }

    /**
     * {@link ITextureBuilder} factory
     * @return An instance of the {@link ITextureBuilder} implementation
     */
    public static ITextureBuilder builder() {
        return new GT_TextureBuilder();
    }
}
