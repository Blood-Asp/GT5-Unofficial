/*
 * LightingHelper - Derived and adapted from @Mineshopper / carpentersblocks
 * Copyright (c) 2013-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package gregtech.api.util;

import com.github.basdxz.apparatus.defenition.managed.IParaBlock;
import com.github.basdxz.apparatus.defenition.managed.IParaTileEntity;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class LightingHelper {
    public static boolean apparatusLoaded = Loader.isModLoaded("apparatus");

    public static final int NORMAL_BRIGHTNESS = 0xff00ff;
    public static final int MAX_BRIGHTNESS = 0xf000f0;
    public static final float NO_Z_FIGHT_OFFSET = 1.0F / 1024.0F;
    protected static final float[] LIGHTNESS = {0.5F, 1.0F, 0.8F, 0.8F, 0.6F, 0.6F};
    private final RenderBlocks renderBlocks;
    /**
     * Brightness for side.
     */
    private int brightness;
    /**
     * Ambient occlusion values for all four corners of side.
     */
    private float aoTopLeft, aoBottomLeft, aoBottomRight, aoTopRight;
    private boolean hasLightnessOverride;
    private float lightnessOverride;
    private boolean hasBrightnessOverride;
    private int brightnessOverride;
    private boolean hasColorOverride;
    private int colorOverride = 0xffffff;

    /**
     * Class constructor specifying the {@link RenderBlocks}.
     *
     * @param renderBlocks the {@link RenderBlocks}
     */
    public LightingHelper(RenderBlocks renderBlocks) {
        this.renderBlocks = renderBlocks;
        if (renderBlocks.useInventoryTint) {
            // Block will be rendered in an inventory, so it needs its lightness maxed
            setLightnessOverride(1.0F);
        }
    }

    /**
     * Gets average brightness from two brightness values.
     *
     * @param brightnessA the first brightness value
     * @param brightnessB the second brightness value
     * @return the mixed brightness
     */
    public static int getAverageBrightness(int brightnessA, int brightnessB) {
        int sectionA1 = brightnessA >> 16 & 0xff;
        int sectionA2 = brightnessA & 255;

        int sectionB1 = brightnessB >> 16 & 0xff;
        int sectionB2 = brightnessB & 255;

        int difference1 = (int) ((sectionA1 + sectionB1) / 2.0F);
        int difference2 = (int) ((sectionA2 + sectionB2) / 2.0F);

        return difference1 << 16 | difference2;
    }

    /**
     * Gets rgb color from RGBA array.
     *
     * @param color the integer color
     * @return a float array with rgb values
     */
    public static float[] getRGB(short[] color) {
        float red = color[0] / 255.0F;
        float green = color[1] / 255.0F;
        float blue = color[2] / 255.0F;

        return new float[]{red, green, blue};
    }

    /**
     * Clears brightness override.
     */
    public void clearBrightnessOverride() {
        hasBrightnessOverride = false;
    }

    /**
     * Clears color override.
     */
    public void clearColorOverride() {
        hasColorOverride = false;
    }

    /**
     * Clears lightness override.
     */
    public void clearLightnessOverride() {
        hasLightnessOverride = false;
    }

    /**
     * @return the Ambient Occlusion for Bottom-Left corner
     */
    public float getAoBottomLeft() {
        return aoBottomLeft;
    }

    /**
     * @return the Ambient Occlusion for Bottom-Right corner
     */
    public float getAoBottomRight() {
        return aoBottomRight;
    }

    /**
     * @return the Ambient Occlusion for Top-Left corner
     */
    public float getAoTopLeft() {
        return aoTopLeft;
    }

    /**
     * @return the Ambient Occlusion for Top-Right corner
     */
    public float getAoTopRight() {
        return aoTopRight;
    }

    /**
     * Sets brightness override.
     *
     * @param brightness the brightness override
     * @return the {@link LightingHelper}
     */
    public LightingHelper setBrightnessOverride(int brightness) {
        hasBrightnessOverride = true;
        brightnessOverride = brightness;
        return this;
    }

    public LightingHelper setColorOverride(short[] color) {
        return setColorOverride(getColor(color));
    }

    /**
     * Sets color override.
     *
     * @param color the color override
     * @return the {@link LightingHelper}
     */
    public LightingHelper setColorOverride(int color) {
        hasColorOverride = true;
        colorOverride = color;
        return this;
    }

    /**
     * Gets int color from RGBA array.
     *
     * @param rgba the short RGBA color array
     * @return int color
     */
    public static int getColor(short[] rgba) {
        return (rgba[2] & 0xff) | (rgba[1] & 0xff) << 8 | (rgba[0] & 0xff) << 16;
    }

    /**
     * Sets lightness override.
     *
     * @param lightness the lightness override
     * @return the {@link LightingHelper}
     */
    public LightingHelper setLightnessOverride(float lightness) {
        hasLightnessOverride = true;
        lightnessOverride = lightness;
        return this;
    }

    /**
     * Sets up the color using lightness, brightness, and the primary color
     * value (usually the dye color) for the side.
     *
     * @param side the side
     * @param rgba the primary short[] RGBA color array
     */
    public void setupColor(int side, short[] rgba) {
        setupColor(side, getColor(rgba));
    }

    /**
     * Sets up the color using lightness, brightness, and the primary color
     * value (usually the dye color) for the side.
     *
     * @param side     the side
     * @param hexColor the primary color
     */
    public void setupColor(int side, int hexColor) {
        Tessellator tessellator = Tessellator.instance;
        float lightness = hasLightnessOverride ? lightnessOverride : LIGHTNESS[side];
        float[] rgb = getRGB(hexColor);

        if (hasColorOverride && !renderBlocks.hasOverrideBlockTexture()) {
            rgb = getRGB(colorOverride);
        }

        applyAnaglyph(rgb);

        if (renderBlocks.enableAO) {
            tessellator.setBrightness(hasBrightnessOverride ? brightnessOverride : brightness);

            if (renderBlocks.hasOverrideBlockTexture()) {

                renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = rgb[0];
                renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = rgb[1];
                renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = rgb[2];

            } else {

                renderBlocks.colorRedTopLeft = renderBlocks.colorRedBottomLeft = renderBlocks.colorRedBottomRight = renderBlocks.colorRedTopRight = rgb[0] * lightness;
                renderBlocks.colorGreenTopLeft = renderBlocks.colorGreenBottomLeft = renderBlocks.colorGreenBottomRight = renderBlocks.colorGreenTopRight = rgb[1] * lightness;
                renderBlocks.colorBlueTopLeft = renderBlocks.colorBlueBottomLeft = renderBlocks.colorBlueBottomRight = renderBlocks.colorBlueTopRight = rgb[2] * lightness;

                renderBlocks.colorRedTopLeft *= aoTopLeft;
                renderBlocks.colorGreenTopLeft *= aoTopLeft;
                renderBlocks.colorBlueTopLeft *= aoTopLeft;
                renderBlocks.colorRedBottomLeft *= aoBottomLeft;
                renderBlocks.colorGreenBottomLeft *= aoBottomLeft;
                renderBlocks.colorBlueBottomLeft *= aoBottomLeft;
                renderBlocks.colorRedBottomRight *= aoBottomRight;
                renderBlocks.colorGreenBottomRight *= aoBottomRight;
                renderBlocks.colorBlueBottomRight *= aoBottomRight;
                renderBlocks.colorRedTopRight *= aoTopRight;
                renderBlocks.colorGreenTopRight *= aoTopRight;
                renderBlocks.colorBlueTopRight *= aoTopRight;
            }

        } else {

            if (hasBrightnessOverride) tessellator.setBrightness(brightnessOverride);
            tessellator.setColorOpaque_F(rgb[0] * lightness, rgb[1] * lightness, rgb[2] * lightness);

        }
    }

    /**
     * Gets rgb color from integer.
     *
     * @param color the integer color
     * @return a float array with rgb values
     */
    public static float[] getRGB(int color) {
        float red = (color >> 16 & 0xff) / 255.0F;
        float green = (color >> 8 & 0xff) / 255.0F;
        float blue = (color & 0xff) / 255.0F;

        return new float[]{red, green, blue};
    }

    /**
     * Will apply anaglyph color multipliers to RGB float array.
     * <p>
     * If {@link EntityRenderer#anaglyphEnable} is false,
     * will do nothing.
     *
     * @param rgb array containing red, green and blue float values
     */
    public void applyAnaglyph(float[] rgb) {
        if (EntityRenderer.anaglyphEnable) {
            rgb[0] = (rgb[0] * 30.0F + rgb[1] * 59.0F + rgb[2] * 11.0F) / 100.0F;
            rgb[1] = (rgb[0] * 30.0F + rgb[1] * 70.0F) / 100.0F;
            rgb[2] = (rgb[0] * 30.0F + rgb[2] * 70.0F) / 100.0F;
        }
    }

    /**
     * Gets mixed ambient occlusion value from two inputs, with a
     * ratio applied to the final result.
     *
     * @param x     the x coordinate of the first block
     * @param y     the y coordinate of the first block
     * @param z     the z coordinate of the first block
     * @param x2    the x coordinate of the second block
     * @param y2    the y coordinate of the second block
     * @param z2    the z coordinate of the second block
     * @param ratio the ratio for mixing
     * @return the mixed red, green, blue float values
     */
    public float getMixedAo(int x, int y, int z, int x2, int y2, int z2, double ratio) {
        return getMixedAo(
                getAmbientOcclusionLightValue(x, y, z),
                getAmbientOcclusionLightValue(x2, y2, z2),
                ratio);
    }

    /**
     * Gets ambient occlusion value of a block at a set position.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param z the z coordinate
     * @return the ambient occlusion value
     */
    public float getAmbientOcclusionLightValue(int x, int y, int z) {
        val block = renderBlocks.blockAccess.getBlock(x, y, z);
        if (apparatusLoaded)
            getAmbientOcclusionLightValueParaTileRedirect(block, x, y, z);
        return block.getAmbientOcclusionLightValue();
    }

    /**
     * Gets ambient occlusion value of an IParaTile at a location,
     * falling back to vanilla methods if the block is not instance of IParaBlock.
     *
     * @param block the block {@link Block}
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param z     the z coordinate
     * @return the ambient occlusion value
     */
    public float getAmbientOcclusionLightValueParaTileRedirect(Block block, int x, int y, int z) {
        if (block instanceof IParaBlock) {
            val tileEntity = renderBlocks.blockAccess.getTileEntity(x, y, z);
            if (tileEntity instanceof IParaTileEntity)
                return ((IParaTileEntity) tileEntity).paraTile().getAmbientOcclusionLightValue();
        }
        return block.getAmbientOcclusionLightValue();
    }

    /**
     * Gets mixed ambient occlusion value from two inputs, with a
     * ratio applied to the final result.
     *
     * @param ao1   the first ambient occlusion value
     * @param ao2   the second ambient occlusion value
     * @param ratio the ratio for mixing
     * @return the mixed red, green, blue float values
     */
    public static float getMixedAo(float ao1, float ao2, double ratio) {
        float diff = (float) (Math.abs(ao1 - ao2) * (1.0F - ratio));

        return ao1 > ao2 ? ao1 - diff : ao1 + diff;
    }

    /**
     * Sets up lighting for the West face and returns the {@link LightingHelper}.
     * <p>
     * This is a consolidated <code>method</code> that sets side shading
     * with respect to the following attributes:
     * <p>
     * <ul>
     *   <li>{@link RenderBlocks#enableAO}</li>
     *   <li>{@link RenderBlocks#partialRenderBounds}</li>
     * </ul>
     *
     * @param block the block {@link Block}
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param z     the z coordinate
     * @return the {@link LightingHelper}
     */
    public LightingHelper setupLightingXNeg(Block block, int x, int y, int z) {

        if (renderBlocks.enableAO) {

            int xOffset = renderBlocks.renderMinX > 0.0F + NO_Z_FIGHT_OFFSET ? x : x - 1;

            int mixedBrightness = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y, z);
            brightness = mixedBrightness;

            float ratio = (float) (1.0F - renderBlocks.renderMinX);
            float aoLightValue = getAmbientOcclusionLightValue(x - 1, y, z);

            renderBlocks.aoBrightnessXYNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y - 1, z);
            renderBlocks.aoBrightnessXZNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y, z - 1);
            renderBlocks.aoBrightnessXZNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y, z + 1);
            renderBlocks.aoBrightnessXYNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y + 1, z);
            renderBlocks.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y - 1, z - 1);
            renderBlocks.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y - 1, z + 1);
            renderBlocks.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y + 1, z - 1);
            renderBlocks.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y + 1, z + 1);
            renderBlocks.aoLightValueScratchXYNN = getMixedAo(x - 1, y - 1, z, x, y - 1, z, ratio);
            renderBlocks.aoLightValueScratchXZNN = getMixedAo(x - 1, y, z - 1, x, y, z - 1, ratio);
            renderBlocks.aoLightValueScratchXZNP = getMixedAo(x - 1, y, z + 1, x, y, z + 1, ratio);
            renderBlocks.aoLightValueScratchXYNP = getMixedAo(x - 1, y + 1, z, x, y + 1, z, ratio);
            renderBlocks.aoLightValueScratchXYZNNN = getMixedAo(x - 1, y - 1, z - 1, x, y - 1, z - 1, ratio);
            renderBlocks.aoLightValueScratchXYZNNP = getMixedAo(x - 1, y - 1, z + 1, x, y - 1, z + 1, ratio);
            renderBlocks.aoLightValueScratchXYZNPN = getMixedAo(x - 1, y + 1, z - 1, x, y + 1, z - 1, ratio);
            renderBlocks.aoLightValueScratchXYZNPP = getMixedAo(x - 1, y + 1, z + 1, x, y + 1, z + 1, ratio);

            int brightnessMixedXYZNPN = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessXYNP, mixedBrightness);
            int brightnessMixedXYZNNN = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessXZNN, mixedBrightness);
            int brightnessMixedXYZNNP = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessXZNP, mixedBrightness);
            int brightnessMixedXYZNPP = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNP, renderBlocks.aoBrightnessXYNP, renderBlocks.aoBrightnessXYZNPP, mixedBrightness);

            float aoMixedXYZNPN = (renderBlocks.aoLightValueScratchXZNN + aoLightValue + renderBlocks.aoLightValueScratchXYZNPN + renderBlocks.aoLightValueScratchXYNP) / 4.0F;
            float aoMixedXYZNNN = (renderBlocks.aoLightValueScratchXYZNNN + renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXZNN + aoLightValue) / 4.0F;
            float aoMixedXYZNNP = (renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXYZNNP + aoLightValue + renderBlocks.aoLightValueScratchXZNP) / 4.0F;
            float aoMixedXYZNPP = (aoLightValue + renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchXYZNPP) / 4.0F;

            aoTopLeft = (float) (aoMixedXYZNPP * renderBlocks.renderMaxY * renderBlocks.renderMaxZ + aoMixedXYZNPN * renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMaxZ) + aoMixedXYZNNN * (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMaxZ) + aoMixedXYZNNP * (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMaxZ);
            aoBottomLeft = (float) (aoMixedXYZNPP * renderBlocks.renderMaxY * renderBlocks.renderMinZ + aoMixedXYZNPN * renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMinZ) + aoMixedXYZNNN * (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMinZ) + aoMixedXYZNNP * (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMinZ);
            aoBottomRight = (float) (aoMixedXYZNPP * renderBlocks.renderMinY * renderBlocks.renderMinZ + aoMixedXYZNPN * renderBlocks.renderMinY * (1.0D - renderBlocks.renderMinZ) + aoMixedXYZNNN * (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMinZ) + aoMixedXYZNNP * (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMinZ);
            aoTopRight = (float) (aoMixedXYZNPP * renderBlocks.renderMinY * renderBlocks.renderMaxZ + aoMixedXYZNPN * renderBlocks.renderMinY * (1.0D - renderBlocks.renderMaxZ) + aoMixedXYZNNN * (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMaxZ) + aoMixedXYZNNP * (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMaxZ);

            renderBlocks.brightnessTopLeft = renderBlocks.mixAoBrightness(brightnessMixedXYZNPP, brightnessMixedXYZNPN, brightnessMixedXYZNNN, brightnessMixedXYZNNP, renderBlocks.renderMaxY * renderBlocks.renderMaxZ, renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMaxZ), (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMaxZ), (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMaxZ);
            renderBlocks.brightnessBottomLeft = renderBlocks.mixAoBrightness(brightnessMixedXYZNPP, brightnessMixedXYZNPN, brightnessMixedXYZNNN, brightnessMixedXYZNNP, renderBlocks.renderMaxY * renderBlocks.renderMinZ, renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMinZ), (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMinZ), (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMinZ);
            renderBlocks.brightnessBottomRight = renderBlocks.mixAoBrightness(brightnessMixedXYZNPP, brightnessMixedXYZNPN, brightnessMixedXYZNNN, brightnessMixedXYZNNP, renderBlocks.renderMinY * renderBlocks.renderMinZ, renderBlocks.renderMinY * (1.0D - renderBlocks.renderMinZ), (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMinZ), (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMinZ);
            renderBlocks.brightnessTopRight = renderBlocks.mixAoBrightness(brightnessMixedXYZNPP, brightnessMixedXYZNPN, brightnessMixedXYZNNN, brightnessMixedXYZNNP, renderBlocks.renderMinY * renderBlocks.renderMaxZ, renderBlocks.renderMinY * (1.0D - renderBlocks.renderMaxZ), (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMaxZ), (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMaxZ);

        }

        return this;
    }

    /**
     * Sets up lighting for the East face and returns the {@link LightingHelper}.
     * <p>
     * This is a consolidated <code>method</code> that sets side shading
     * with respect to the following attributes:
     * <p>
     * <ul>
     *   <li>{@link RenderBlocks#enableAO}</li>
     *   <li>{@link RenderBlocks#partialRenderBounds}</li>
     * </ul>
     *
     * @param block the block {@link Block}
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param z     the z coordinate
     * @return the {@link LightingHelper}
     */
    public LightingHelper setupLightingXPos(Block block, int x, int y, int z) {

        if (renderBlocks.enableAO) {

            int xOffset = renderBlocks.renderMaxX < 1.0F - NO_Z_FIGHT_OFFSET ? x : x + 1;

            int mixedBrightness = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y, z);
            brightness = mixedBrightness;

            float aoLightValue = getAmbientOcclusionLightValue(x + 1, y, z);

            renderBlocks.aoBrightnessXYPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y - 1, z);
            renderBlocks.aoBrightnessXZPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y, z - 1);
            renderBlocks.aoBrightnessXZPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y, z + 1);
            renderBlocks.aoBrightnessXYPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y + 1, z);
            renderBlocks.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y - 1, z - 1);
            renderBlocks.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y - 1, z + 1);
            renderBlocks.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y + 1, z - 1);
            renderBlocks.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, xOffset, y + 1, z + 1);
            renderBlocks.aoLightValueScratchXYPN = getMixedAo(x + 1, y - 1, z, x, y - 1, z, renderBlocks.renderMaxX);
            renderBlocks.aoLightValueScratchXZPN = getMixedAo(x + 1, y, z - 1, x, y, z - 1, renderBlocks.renderMaxX);
            renderBlocks.aoLightValueScratchXZPP = getMixedAo(x + 1, y, z + 1, x, y, z + 1, renderBlocks.renderMaxX);
            renderBlocks.aoLightValueScratchXYPP = getMixedAo(x + 1, y + 1, z, x, y + 1, z, renderBlocks.renderMaxX);
            renderBlocks.aoLightValueScratchXYZPNN = getMixedAo(x + 1, y - 1, z - 1, x, y - 1, z - 1, renderBlocks.renderMaxX);
            renderBlocks.aoLightValueScratchXYZPNP = getMixedAo(x + 1, y - 1, z + 1, x, y - 1, z + 1, renderBlocks.renderMaxX);
            renderBlocks.aoLightValueScratchXYZPPN = getMixedAo(x + 1, y + 1, z - 1, x, y + 1, z - 1, renderBlocks.renderMaxX);
            renderBlocks.aoLightValueScratchXYZPPP = getMixedAo(x + 1, y + 1, z + 1, x, y + 1, z + 1, renderBlocks.renderMaxX);

            int brightnessMixedXYZPPP = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZPP, renderBlocks.aoBrightnessXYPP, renderBlocks.aoBrightnessXYZPPP, mixedBrightness);
            int brightnessMixedXYZPNP = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYPN, renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessXZPP, mixedBrightness);
            int brightnessMixedXYZPNN = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZPNN, renderBlocks.aoBrightnessXYPN, renderBlocks.aoBrightnessXZPN, mixedBrightness);
            int brightnessMixedXYZPPN = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZPN, renderBlocks.aoBrightnessXYZPPN, renderBlocks.aoBrightnessXYPP, mixedBrightness);

            float aoMixedXYZPPP = (aoLightValue + renderBlocks.aoLightValueScratchXZPP + renderBlocks.aoLightValueScratchXYPP + renderBlocks.aoLightValueScratchXYZPPP) / 4.0F;
            float aoMixedXYZPNP = (renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXYZPNP + aoLightValue + renderBlocks.aoLightValueScratchXZPP) / 4.0F;
            float aoMixedXYZPNN = (renderBlocks.aoLightValueScratchXYZPNN + renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXZPN + aoLightValue) / 4.0F;
            float aoMixedXYZPPN = (renderBlocks.aoLightValueScratchXZPN + aoLightValue + renderBlocks.aoLightValueScratchXYZPPN + renderBlocks.aoLightValueScratchXYPP) / 4.0F;

            aoTopLeft = (float) (aoMixedXYZPNP * (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMaxZ + aoMixedXYZPNN * (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMaxZ) + aoMixedXYZPPN * renderBlocks.renderMinY * (1.0D - renderBlocks.renderMaxZ) + aoMixedXYZPPP * renderBlocks.renderMinY * renderBlocks.renderMaxZ);
            aoBottomLeft = (float) (aoMixedXYZPNP * (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMinZ + aoMixedXYZPNN * (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMinZ) + aoMixedXYZPPN * renderBlocks.renderMinY * (1.0D - renderBlocks.renderMinZ) + aoMixedXYZPPP * renderBlocks.renderMinY * renderBlocks.renderMinZ);
            aoBottomRight = (float) (aoMixedXYZPNP * (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMinZ + aoMixedXYZPNN * (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMinZ) + aoMixedXYZPPN * renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMinZ) + aoMixedXYZPPP * renderBlocks.renderMaxY * renderBlocks.renderMinZ);
            aoTopRight = (float) (aoMixedXYZPNP * (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMaxZ + aoMixedXYZPNN * (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMaxZ) + aoMixedXYZPPN * renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMaxZ) + aoMixedXYZPPP * renderBlocks.renderMaxY * renderBlocks.renderMaxZ);

            renderBlocks.brightnessTopLeft = renderBlocks.mixAoBrightness(brightnessMixedXYZPNP, brightnessMixedXYZPNN, brightnessMixedXYZPPN, brightnessMixedXYZPPP, (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMaxZ, (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMaxZ), renderBlocks.renderMinY * (1.0D - renderBlocks.renderMaxZ), renderBlocks.renderMinY * renderBlocks.renderMaxZ);
            renderBlocks.brightnessBottomLeft = renderBlocks.mixAoBrightness(brightnessMixedXYZPNP, brightnessMixedXYZPNN, brightnessMixedXYZPPN, brightnessMixedXYZPPP, (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMinZ, (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMinZ), renderBlocks.renderMinY * (1.0D - renderBlocks.renderMinZ), renderBlocks.renderMinY * renderBlocks.renderMinZ);
            renderBlocks.brightnessBottomRight = renderBlocks.mixAoBrightness(brightnessMixedXYZPNP, brightnessMixedXYZPNN, brightnessMixedXYZPPN, brightnessMixedXYZPPP, (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMinZ, (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMinZ), renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMinZ), renderBlocks.renderMaxY * renderBlocks.renderMinZ);
            renderBlocks.brightnessTopRight = renderBlocks.mixAoBrightness(brightnessMixedXYZPNP, brightnessMixedXYZPNN, brightnessMixedXYZPPN, brightnessMixedXYZPPP, (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMaxZ, (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMaxZ), renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMaxZ), renderBlocks.renderMaxY * renderBlocks.renderMaxZ);

        }

        return this;
    }

    /**
     * Sets up lighting for the bottom face and returns the {@link LightingHelper}.
     * <p>
     * This is a consolidated <code>method</code> that sets side shading
     * with respect to the following attributes:
     * <p>
     * <ul>
     *   <li>{@link RenderBlocks#enableAO}</li>
     *   <li>{@link RenderBlocks#partialRenderBounds}</li>
     * </ul>
     *
     * @param block the block {@link Block}
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param z     the z coordinate
     * @return the {@link LightingHelper}
     */
    public LightingHelper setupLightingYNeg(Block block, int x, int y, int z) {

        if (renderBlocks.enableAO) {

            int yOffset = renderBlocks.renderMinY > 0.0F + NO_Z_FIGHT_OFFSET ? y : y - 1;

            int mixedBrightness = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, yOffset, z);
            brightness = mixedBrightness;

            float ratio = (float) (1.0F - renderBlocks.renderMinY);
            float aoLightValue = getAmbientOcclusionLightValue(x, y - 1, z);

            renderBlocks.aoBrightnessXYNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, yOffset, z);
            renderBlocks.aoBrightnessYZNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, yOffset, z - 1);
            renderBlocks.aoBrightnessYZNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, yOffset, z + 1);
            renderBlocks.aoBrightnessXYPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, yOffset, z);
            renderBlocks.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, yOffset, z - 1);
            renderBlocks.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, yOffset, z + 1);
            renderBlocks.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, yOffset, z - 1);
            renderBlocks.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, yOffset, z + 1);
            renderBlocks.aoLightValueScratchXYNN = getMixedAo(x - 1, y - 1, z, x - 1, y, z, ratio);
            renderBlocks.aoLightValueScratchYZNN = getMixedAo(x, y - 1, z - 1, x, y, z - 1, ratio);
            renderBlocks.aoLightValueScratchYZNP = getMixedAo(x, y - 1, z + 1, x, y, z + 1, ratio);
            renderBlocks.aoLightValueScratchXYPN = getMixedAo(x + 1, y - 1, z, x + 1, y, z, ratio);
            renderBlocks.aoLightValueScratchXYZNNN = getMixedAo(x - 1, y - 1, z - 1, x - 1, y, z - 1, ratio);
            renderBlocks.aoLightValueScratchXYZNNP = getMixedAo(x - 1, y - 1, z + 1, x - 1, y, z + 1, ratio);
            renderBlocks.aoLightValueScratchXYZPNN = getMixedAo(x + 1, y - 1, z - 1, x + 1, y, z - 1, ratio);
            renderBlocks.aoLightValueScratchXYZPNP = getMixedAo(x + 1, y - 1, z + 1, x + 1, y, z + 1, ratio);

            int brightnessMixedXYZPNP = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNP, renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessXYPN, mixedBrightness);
            int brightnessMixedXYZPNN = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNN, renderBlocks.aoBrightnessXYPN, renderBlocks.aoBrightnessXYZPNN, mixedBrightness);
            int brightnessMixedXYZNNN = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessYZNN, mixedBrightness);
            int brightnessMixedXYZNNP = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessXYNN, renderBlocks.aoBrightnessYZNP, mixedBrightness);

            float aoMixedXYZPNP = (renderBlocks.aoLightValueScratchYZNP + aoLightValue + renderBlocks.aoLightValueScratchXYZPNP + renderBlocks.aoLightValueScratchXYPN) / 4.0F;
            float aoMixedXYZPNN = (aoLightValue + renderBlocks.aoLightValueScratchYZNN + renderBlocks.aoLightValueScratchXYPN + renderBlocks.aoLightValueScratchXYZPNN) / 4.0F;
            float aoMixedXYZNNN = (renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchXYZNNN + aoLightValue + renderBlocks.aoLightValueScratchYZNN) / 4.0F;
            float aoMixedXYZNNP = (renderBlocks.aoLightValueScratchXYZNNP + renderBlocks.aoLightValueScratchXYNN + renderBlocks.aoLightValueScratchYZNP + aoLightValue) / 4.0F;

            aoTopLeft = (float) (aoMixedXYZNNP * renderBlocks.renderMaxZ * (1.0D - renderBlocks.renderMinX) + aoMixedXYZPNP * renderBlocks.renderMaxZ * renderBlocks.renderMinX + aoMixedXYZPNN * (1.0D - renderBlocks.renderMaxZ) * renderBlocks.renderMinX + aoMixedXYZNNN * (1.0D - renderBlocks.renderMaxZ) * (1.0D - renderBlocks.renderMinX));
            aoBottomLeft = (float) (aoMixedXYZNNP * renderBlocks.renderMinZ * (1.0D - renderBlocks.renderMinX) + aoMixedXYZPNP * renderBlocks.renderMinZ * renderBlocks.renderMinX + aoMixedXYZPNN * (1.0D - renderBlocks.renderMinZ) * renderBlocks.renderMinX + aoMixedXYZNNN * (1.0D - renderBlocks.renderMinZ) * (1.0D - renderBlocks.renderMinX));
            aoBottomRight = (float) (aoMixedXYZNNP * renderBlocks.renderMinZ * (1.0D - renderBlocks.renderMaxX) + aoMixedXYZPNP * renderBlocks.renderMinZ * renderBlocks.renderMaxX + aoMixedXYZPNN * (1.0D - renderBlocks.renderMinZ) * renderBlocks.renderMaxX + aoMixedXYZNNN * (1.0D - renderBlocks.renderMinZ) * (1.0D - renderBlocks.renderMaxX));
            aoTopRight = (float) (aoMixedXYZNNP * renderBlocks.renderMaxZ * (1.0D - renderBlocks.renderMaxX) + aoMixedXYZPNP * renderBlocks.renderMaxZ * renderBlocks.renderMaxX + aoMixedXYZPNN * (1.0D - renderBlocks.renderMaxZ) * renderBlocks.renderMaxX + aoMixedXYZNNN * (1.0D - renderBlocks.renderMaxZ) * (1.0D - renderBlocks.renderMaxX));

            renderBlocks.brightnessTopLeft = renderBlocks.mixAoBrightness(brightnessMixedXYZNNP, brightnessMixedXYZPNP, brightnessMixedXYZPNN, brightnessMixedXYZNNN, renderBlocks.renderMaxZ * (1.0D - renderBlocks.renderMinX), renderBlocks.renderMaxZ * renderBlocks.renderMinX, (1.0D - renderBlocks.renderMaxZ) * renderBlocks.renderMinX, (1.0D - renderBlocks.renderMaxZ) * (1.0D - renderBlocks.renderMinX));
            renderBlocks.brightnessBottomLeft = renderBlocks.mixAoBrightness(brightnessMixedXYZNNP, brightnessMixedXYZPNP, brightnessMixedXYZPNN, brightnessMixedXYZNNN, renderBlocks.renderMinZ * (1.0D - renderBlocks.renderMinX), renderBlocks.renderMinZ * renderBlocks.renderMinX, (1.0D - renderBlocks.renderMinZ) * renderBlocks.renderMinX, (1.0D - renderBlocks.renderMinZ) * (1.0D - renderBlocks.renderMinX));
            renderBlocks.brightnessBottomRight = renderBlocks.mixAoBrightness(brightnessMixedXYZNNP, brightnessMixedXYZPNP, brightnessMixedXYZPNN, brightnessMixedXYZNNN, renderBlocks.renderMinZ * (1.0D - renderBlocks.renderMaxX), renderBlocks.renderMinZ * renderBlocks.renderMaxX, (1.0D - renderBlocks.renderMinZ) * renderBlocks.renderMaxX, (1.0D - renderBlocks.renderMinZ) * (1.0D - renderBlocks.renderMaxX));
            renderBlocks.brightnessTopRight = renderBlocks.mixAoBrightness(brightnessMixedXYZNNP, brightnessMixedXYZPNP, brightnessMixedXYZPNN, brightnessMixedXYZNNN, renderBlocks.renderMaxZ * (1.0D - renderBlocks.renderMaxX), renderBlocks.renderMaxZ * renderBlocks.renderMaxX, (1.0D - renderBlocks.renderMaxZ) * renderBlocks.renderMaxX, (1.0D - renderBlocks.renderMaxZ) * (1.0D - renderBlocks.renderMaxX));

        }

        return this;
    }

    /**
     * Sets up lighting for the top face and returns the {@link LightingHelper}.
     * <p>
     * This is a consolidated <code>method</code> that sets side shading
     * with respect to the following attributes:
     * <p>
     * <ul>
     *   <li>{@link RenderBlocks#enableAO}</li>
     *   <li>{@link RenderBlocks#partialRenderBounds}</li>
     * </ul>
     *
     * @param block the block {@link Block}
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param z     the z coordinate
     * @return the {@link LightingHelper}
     */
    public LightingHelper setupLightingYPos(Block block, int x, int y, int z) {

        if (renderBlocks.enableAO) {

            int yOffset = renderBlocks.renderMaxY < 1.0F - NO_Z_FIGHT_OFFSET ? y : y + 1;

            int mixedBrightness = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, yOffset, z);
            brightness = mixedBrightness;

            float aoLightValue = getAmbientOcclusionLightValue(x, y + 1, z);

            renderBlocks.aoBrightnessXYNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, yOffset, z);
            renderBlocks.aoBrightnessXYPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, yOffset, z);
            renderBlocks.aoBrightnessYZPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, yOffset, z - 1);
            renderBlocks.aoBrightnessYZPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, yOffset, z + 1);
            renderBlocks.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, yOffset, z - 1);
            renderBlocks.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, yOffset, z - 1);
            renderBlocks.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, yOffset, z + 1);
            renderBlocks.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, yOffset, z + 1);
            renderBlocks.aoLightValueScratchXYNP = getMixedAo(x - 1, y + 1, z, x - 1, y, z, renderBlocks.renderMaxY);
            renderBlocks.aoLightValueScratchXYPP = getMixedAo(x + 1, y + 1, z, x + 1, y, z, renderBlocks.renderMaxY);
            renderBlocks.aoLightValueScratchYZPN = getMixedAo(x, y + 1, z - 1, x, y, z - 1, renderBlocks.renderMaxY);
            renderBlocks.aoLightValueScratchYZPP = getMixedAo(x, y + 1, z + 1, x, y, z + 1, renderBlocks.renderMaxY);
            renderBlocks.aoLightValueScratchXYZNPN = getMixedAo(x - 1, y + 1, z - 1, x - 1, y, z - 1, renderBlocks.renderMaxY);
            renderBlocks.aoLightValueScratchXYZPPN = getMixedAo(x + 1, y + 1, z - 1, x + 1, y, z - 1, renderBlocks.renderMaxY);
            renderBlocks.aoLightValueScratchXYZNPP = getMixedAo(x - 1, y + 1, z + 1, x - 1, y, z + 1, renderBlocks.renderMaxY);
            renderBlocks.aoLightValueScratchXYZPPP = getMixedAo(x + 1, y + 1, z + 1, x + 1, y, z + 1, renderBlocks.renderMaxY);

            int brightnessMixedXYZPPP = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPP, renderBlocks.aoBrightnessXYZPPP, renderBlocks.aoBrightnessXYPP, mixedBrightness);
            int brightnessMixedXYZPPN = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPN, renderBlocks.aoBrightnessXYPP, renderBlocks.aoBrightnessXYZPPN, mixedBrightness);
            int brightnessMixedXYZNPN = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYNP, renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessYZPN, mixedBrightness);
            int brightnessMixedXYZNPP = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNPP, renderBlocks.aoBrightnessXYNP, renderBlocks.aoBrightnessYZPP, mixedBrightness);

            float aoMixedXYZPPP = (renderBlocks.aoLightValueScratchYZPP + aoLightValue + renderBlocks.aoLightValueScratchXYZPPP + renderBlocks.aoLightValueScratchXYPP) / 4.0F;
            float aoMixedXYZPPN = (aoLightValue + renderBlocks.aoLightValueScratchYZPN + renderBlocks.aoLightValueScratchXYPP + renderBlocks.aoLightValueScratchXYZPPN) / 4.0F;
            float aoMixedXYZNPN = (renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchXYZNPN + aoLightValue + renderBlocks.aoLightValueScratchYZPN) / 4.0F;
            float aoMixedXYZNPP = (renderBlocks.aoLightValueScratchXYZNPP + renderBlocks.aoLightValueScratchXYNP + renderBlocks.aoLightValueScratchYZPP + aoLightValue) / 4.0F;

            aoTopLeft     /*SE*/ = (float) (aoMixedXYZNPP * renderBlocks.renderMaxZ * (1.0D - renderBlocks.renderMaxX) + aoMixedXYZPPP * renderBlocks.renderMaxZ * renderBlocks.renderMaxX + aoMixedXYZPPN * (1.0D - renderBlocks.renderMaxZ) * renderBlocks.renderMaxX + aoMixedXYZNPN * (1.0D - renderBlocks.renderMaxZ) * (1.0D - renderBlocks.renderMaxX));
            aoBottomLeft  /*NE*/ = (float) (aoMixedXYZNPP * renderBlocks.renderMinZ * (1.0D - renderBlocks.renderMaxX) + aoMixedXYZPPP * renderBlocks.renderMinZ * renderBlocks.renderMaxX + aoMixedXYZPPN * (1.0D - renderBlocks.renderMinZ) * renderBlocks.renderMaxX + aoMixedXYZNPN * (1.0D - renderBlocks.renderMinZ) * (1.0D - renderBlocks.renderMaxX));
            aoBottomRight /*NW*/ = (float) (aoMixedXYZNPP * renderBlocks.renderMinZ * (1.0D - renderBlocks.renderMinX) + aoMixedXYZPPP * renderBlocks.renderMinZ * renderBlocks.renderMinX + aoMixedXYZPPN * (1.0D - renderBlocks.renderMinZ) * renderBlocks.renderMinX + aoMixedXYZNPN * (1.0D - renderBlocks.renderMinZ) * (1.0D - renderBlocks.renderMinX));
            aoTopRight    /*SW*/ = (float) (aoMixedXYZNPP * renderBlocks.renderMaxZ * (1.0D - renderBlocks.renderMinX) + aoMixedXYZPPP * renderBlocks.renderMaxZ * renderBlocks.renderMinX + aoMixedXYZPPN * (1.0D - renderBlocks.renderMaxZ) * renderBlocks.renderMinX + aoMixedXYZNPN * (1.0D - renderBlocks.renderMaxZ) * (1.0D - renderBlocks.renderMinX));

            renderBlocks.brightnessTopLeft = renderBlocks.mixAoBrightness(brightnessMixedXYZNPP, brightnessMixedXYZPPP, brightnessMixedXYZPPN, brightnessMixedXYZNPN, renderBlocks.renderMaxZ * (1.0D - renderBlocks.renderMaxX), renderBlocks.renderMaxZ * renderBlocks.renderMaxX, (1.0D - renderBlocks.renderMaxZ) * renderBlocks.renderMaxX, (1.0D - renderBlocks.renderMaxZ) * (1.0D - renderBlocks.renderMaxX));
            renderBlocks.brightnessBottomLeft = renderBlocks.mixAoBrightness(brightnessMixedXYZNPP, brightnessMixedXYZPPP, brightnessMixedXYZPPN, brightnessMixedXYZNPN, renderBlocks.renderMinZ * (1.0D - renderBlocks.renderMaxX), renderBlocks.renderMinZ * renderBlocks.renderMaxX, (1.0D - renderBlocks.renderMinZ) * renderBlocks.renderMaxX, (1.0D - renderBlocks.renderMinZ) * (1.0D - renderBlocks.renderMaxX));
            renderBlocks.brightnessBottomRight = renderBlocks.mixAoBrightness(brightnessMixedXYZNPP, brightnessMixedXYZPPP, brightnessMixedXYZPPN, brightnessMixedXYZNPN, renderBlocks.renderMinZ * (1.0D - renderBlocks.renderMinX), renderBlocks.renderMinZ * renderBlocks.renderMinX, (1.0D - renderBlocks.renderMinZ) * renderBlocks.renderMinX, (1.0D - renderBlocks.renderMinZ) * (1.0D - renderBlocks.renderMinX));
            renderBlocks.brightnessTopRight = renderBlocks.mixAoBrightness(brightnessMixedXYZNPP, brightnessMixedXYZPPP, brightnessMixedXYZPPN, brightnessMixedXYZNPN, renderBlocks.renderMaxZ * (1.0D - renderBlocks.renderMinX), renderBlocks.renderMaxZ * renderBlocks.renderMinX, (1.0D - renderBlocks.renderMaxZ) * renderBlocks.renderMinX, (1.0D - renderBlocks.renderMaxZ) * (1.0D - renderBlocks.renderMinX));
        }

        return this;
    }

    /**
     * Sets up lighting for the North face and returns the {@link LightingHelper}.
     * <p>
     * This is a consolidated <code>method</code> that sets side shading
     * with respect to the following attributes:
     * <p>
     * <ul>
     *   <li>{@link RenderBlocks#enableAO}</li>
     *   <li>{@link RenderBlocks#partialRenderBounds}</li>
     * </ul>
     *
     * @param block the block {@link Block}
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param z     the z coordinate
     * @return the {@link LightingHelper}
     */
    public LightingHelper setupLightingZNeg(Block block, int x, int y, int z) {

        if (renderBlocks.enableAO) {

            int zOffset = renderBlocks.renderMinZ > 0.0F + NO_Z_FIGHT_OFFSET ? z : z - 1;

            int mixedBrightness = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, zOffset);
            brightness = mixedBrightness;

            float ratio = (float) (1.0F - renderBlocks.renderMinZ);
            float aoLightValue = getAmbientOcclusionLightValue(x, y, z - 1);

            renderBlocks.aoBrightnessXZNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, zOffset);
            renderBlocks.aoBrightnessYZNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, zOffset);
            renderBlocks.aoBrightnessYZPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, zOffset);
            renderBlocks.aoBrightnessXZPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, zOffset);
            renderBlocks.aoBrightnessXYZNNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y - 1, zOffset);
            renderBlocks.aoBrightnessXYZNPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y + 1, zOffset);
            renderBlocks.aoBrightnessXYZPNN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y - 1, zOffset);
            renderBlocks.aoBrightnessXYZPPN = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y + 1, zOffset);
            renderBlocks.aoLightValueScratchXZNN = getMixedAo(x - 1, y, z - 1, x - 1, y, z, ratio);
            renderBlocks.aoLightValueScratchYZNN = getMixedAo(x, y - 1, z - 1, x, y - 1, z, ratio);
            renderBlocks.aoLightValueScratchYZPN = getMixedAo(x, y + 1, z - 1, x, y + 1, z, ratio);
            renderBlocks.aoLightValueScratchXZPN = getMixedAo(x + 1, y, z - 1, x + 1, y, z, ratio);
            renderBlocks.aoLightValueScratchXYZNNN = getMixedAo(x - 1, y - 1, z - 1, x - 1, y - 1, z, ratio);
            renderBlocks.aoLightValueScratchXYZNPN = getMixedAo(x - 1, y + 1, z - 1, x - 1, y + 1, z, ratio);
            renderBlocks.aoLightValueScratchXYZPNN = getMixedAo(x + 1, y - 1, z - 1, x + 1, y - 1, z, ratio);
            renderBlocks.aoLightValueScratchXYZPPN = getMixedAo(x + 1, y + 1, z - 1, x + 1, y + 1, z, ratio);

            int brightnessMixedXYZPPN = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPN, renderBlocks.aoBrightnessXZPN, renderBlocks.aoBrightnessXYZPPN, mixedBrightness);
            int brightnessMixedXYZPNN = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNN, renderBlocks.aoBrightnessXYZPNN, renderBlocks.aoBrightnessXZPN, mixedBrightness);
            int brightnessMixedXYZNNN = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNN, renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessYZNN, mixedBrightness);
            int brightnessMixedXYZNPN = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNN, renderBlocks.aoBrightnessXYZNPN, renderBlocks.aoBrightnessYZPN, mixedBrightness);

            float aoMixedXYZPPN = (aoLightValue + renderBlocks.aoLightValueScratchYZPN + renderBlocks.aoLightValueScratchXZPN + renderBlocks.aoLightValueScratchXYZPPN) / 4.0F;
            float aoMixedXYZPNN = (renderBlocks.aoLightValueScratchYZNN + aoLightValue + renderBlocks.aoLightValueScratchXYZPNN + renderBlocks.aoLightValueScratchXZPN) / 4.0F;
            float aoMixedXYZNNN = (renderBlocks.aoLightValueScratchXYZNNN + renderBlocks.aoLightValueScratchXZNN + renderBlocks.aoLightValueScratchYZNN + aoLightValue) / 4.0F;
            float aoMixedXYZNPN = (renderBlocks.aoLightValueScratchXZNN + renderBlocks.aoLightValueScratchXYZNPN + aoLightValue + renderBlocks.aoLightValueScratchYZPN) / 4.0F;

            aoTopLeft = (float) (aoMixedXYZNPN * renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMinX) + aoMixedXYZPPN * renderBlocks.renderMaxY * renderBlocks.renderMinX + aoMixedXYZPNN * (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMinX + aoMixedXYZNNN * (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMinX));
            aoBottomLeft = (float) (aoMixedXYZNPN * renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMaxX) + aoMixedXYZPPN * renderBlocks.renderMaxY * renderBlocks.renderMaxX + aoMixedXYZPNN * (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMaxX + aoMixedXYZNNN * (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMaxX));
            aoBottomRight = (float) (aoMixedXYZNPN * renderBlocks.renderMinY * (1.0D - renderBlocks.renderMaxX) + aoMixedXYZPPN * renderBlocks.renderMinY * renderBlocks.renderMaxX + aoMixedXYZPNN * (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMaxX + aoMixedXYZNNN * (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMaxX));
            aoTopRight = (float) (aoMixedXYZNPN * renderBlocks.renderMinY * (1.0D - renderBlocks.renderMinX) + aoMixedXYZPPN * renderBlocks.renderMinY * renderBlocks.renderMinX + aoMixedXYZPNN * (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMinX + aoMixedXYZNNN * (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMinX));

            renderBlocks.brightnessTopLeft = renderBlocks.mixAoBrightness(brightnessMixedXYZNPN, brightnessMixedXYZPPN, brightnessMixedXYZPNN, brightnessMixedXYZNNN, renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMinX), renderBlocks.renderMaxY * renderBlocks.renderMinX, (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMinX, (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMinX));
            renderBlocks.brightnessBottomLeft = renderBlocks.mixAoBrightness(brightnessMixedXYZNPN, brightnessMixedXYZPPN, brightnessMixedXYZPNN, brightnessMixedXYZNNN, renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMaxX), renderBlocks.renderMaxY * renderBlocks.renderMaxX, (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMaxX, (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMaxX));
            renderBlocks.brightnessBottomRight = renderBlocks.mixAoBrightness(brightnessMixedXYZNPN, brightnessMixedXYZPPN, brightnessMixedXYZPNN, brightnessMixedXYZNNN, renderBlocks.renderMinY * (1.0D - renderBlocks.renderMaxX), renderBlocks.renderMinY * renderBlocks.renderMaxX, (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMaxX, (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMaxX));
            renderBlocks.brightnessTopRight = renderBlocks.mixAoBrightness(brightnessMixedXYZNPN, brightnessMixedXYZPPN, brightnessMixedXYZPNN, brightnessMixedXYZNNN, renderBlocks.renderMinY * (1.0D - renderBlocks.renderMinX), renderBlocks.renderMinY * renderBlocks.renderMinX, (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMinX, (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMinX));

        }

        return this;
    }

    /**
     * Sets up lighting for the South face and returns the {@link LightingHelper}.
     * <p>
     * This is a consolidated <code>method</code> that sets side shading
     * with respect to the following attributes:
     * <p>
     * <ul>
     *   <li>{@link RenderBlocks#enableAO}</li>
     *   <li>{@link RenderBlocks#partialRenderBounds}</li>
     * </ul>
     *
     * @param block the block {@link Block}
     * @param x     the x coordinate
     * @param y     the y coordinate
     * @param z     the z coordinate
     * @return the {@link LightingHelper}
     */
    public LightingHelper setupLightingZPos(Block block, int x, int y, int z) {

        if (renderBlocks.enableAO) {

            int zOffset = renderBlocks.renderMaxZ < 1.0F - NO_Z_FIGHT_OFFSET ? z : z + 1;

            int mixedBrightness = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y, zOffset);
            brightness = mixedBrightness;

            float aoLightValue = getAmbientOcclusionLightValue(x, y, z + 1);

            renderBlocks.aoBrightnessXZNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y, zOffset);
            renderBlocks.aoBrightnessXZPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y, zOffset);
            renderBlocks.aoBrightnessYZNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y - 1, zOffset);
            renderBlocks.aoBrightnessYZPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x, y + 1, zOffset);
            renderBlocks.aoBrightnessXYZNNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y - 1, zOffset);
            renderBlocks.aoBrightnessXYZNPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x - 1, y + 1, zOffset);
            renderBlocks.aoBrightnessXYZPNP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y - 1, zOffset);
            renderBlocks.aoBrightnessXYZPPP = block.getMixedBrightnessForBlock(renderBlocks.blockAccess, x + 1, y + 1, zOffset);
            renderBlocks.aoLightValueScratchXZNP = getMixedAo(x - 1, y, z + 1, x - 1, y, z, renderBlocks.renderMaxZ);
            renderBlocks.aoLightValueScratchXZPP = getMixedAo(x + 1, y, z + 1, x + 1, y, z, renderBlocks.renderMaxZ);
            renderBlocks.aoLightValueScratchYZNP = getMixedAo(x, y - 1, z + 1, x, y - 1, z, renderBlocks.renderMaxZ);
            renderBlocks.aoLightValueScratchYZPP = getMixedAo(x, y + 1, z + 1, x, y + 1, z, renderBlocks.renderMaxZ);
            renderBlocks.aoLightValueScratchXYZNNP = getMixedAo(x - 1, y - 1, z + 1, x - 1, y - 1, z, renderBlocks.renderMaxZ);
            renderBlocks.aoLightValueScratchXYZNPP = getMixedAo(x - 1, y + 1, z + 1, x - 1, y + 1, z, renderBlocks.renderMaxZ);
            renderBlocks.aoLightValueScratchXYZPNP = getMixedAo(x + 1, y - 1, z + 1, x + 1, y - 1, z, renderBlocks.renderMaxZ);
            renderBlocks.aoLightValueScratchXYZPPP = getMixedAo(x + 1, y + 1, z + 1, x + 1, y + 1, z, renderBlocks.renderMaxZ);

            int brightnessMixedXYZNPP = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXZNP, renderBlocks.aoBrightnessXYZNPP, renderBlocks.aoBrightnessYZPP, mixedBrightness);
            int brightnessMixedXYZNNP = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessXYZNNP, renderBlocks.aoBrightnessXZNP, renderBlocks.aoBrightnessYZNP, mixedBrightness);
            int brightnessMixedXYZPNP = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZNP, renderBlocks.aoBrightnessXYZPNP, renderBlocks.aoBrightnessXZPP, mixedBrightness);
            int brightnessMixedXYZPPP = renderBlocks.getAoBrightness(renderBlocks.aoBrightnessYZPP, renderBlocks.aoBrightnessXZPP, renderBlocks.aoBrightnessXYZPPP, mixedBrightness);

            float aoMixedXYZNPP = (renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchXYZNPP + aoLightValue + renderBlocks.aoLightValueScratchYZPP) / 4.0F;
            float aoMixedXYZNNP = (renderBlocks.aoLightValueScratchXYZNNP + renderBlocks.aoLightValueScratchXZNP + renderBlocks.aoLightValueScratchYZNP + aoLightValue) / 4.0F;
            float aoMixedXYZPNP = (renderBlocks.aoLightValueScratchYZNP + aoLightValue + renderBlocks.aoLightValueScratchXYZPNP + renderBlocks.aoLightValueScratchXZPP) / 4.0F;
            float aoMixedXYZPPP = (aoLightValue + renderBlocks.aoLightValueScratchYZPP + renderBlocks.aoLightValueScratchXZPP + renderBlocks.aoLightValueScratchXYZPPP) / 4.0F;

            aoTopLeft = (float) (aoMixedXYZNPP * renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMinX) + aoMixedXYZPPP * renderBlocks.renderMaxY * renderBlocks.renderMinX + aoMixedXYZPNP * (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMinX + aoMixedXYZNNP * (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMinX));
            aoBottomLeft = (float) (aoMixedXYZNPP * renderBlocks.renderMinY * (1.0D - renderBlocks.renderMinX) + aoMixedXYZPPP * renderBlocks.renderMinY * renderBlocks.renderMinX + aoMixedXYZPNP * (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMinX + aoMixedXYZNNP * (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMinX));
            aoBottomRight = (float) (aoMixedXYZNPP * renderBlocks.renderMinY * (1.0D - renderBlocks.renderMaxX) + aoMixedXYZPPP * renderBlocks.renderMinY * renderBlocks.renderMaxX + aoMixedXYZPNP * (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMaxX + aoMixedXYZNNP * (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMaxX));
            aoTopRight = (float) (aoMixedXYZNPP * renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMaxX) + aoMixedXYZPPP * renderBlocks.renderMaxY * renderBlocks.renderMaxX + aoMixedXYZPNP * (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMaxX + aoMixedXYZNNP * (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMaxX));

            renderBlocks.brightnessTopLeft = renderBlocks.mixAoBrightness(brightnessMixedXYZNPP, brightnessMixedXYZNNP, brightnessMixedXYZPNP, brightnessMixedXYZPPP, renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMinX), (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMinX), (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMinX, renderBlocks.renderMaxY * renderBlocks.renderMinX);
            renderBlocks.brightnessBottomLeft = renderBlocks.mixAoBrightness(brightnessMixedXYZNPP, brightnessMixedXYZNNP, brightnessMixedXYZPNP, brightnessMixedXYZPPP, renderBlocks.renderMinY * (1.0D - renderBlocks.renderMinX), (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMinX), (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMinX, renderBlocks.renderMinY * renderBlocks.renderMinX);
            renderBlocks.brightnessBottomRight = renderBlocks.mixAoBrightness(brightnessMixedXYZNPP, brightnessMixedXYZNNP, brightnessMixedXYZPNP, brightnessMixedXYZPPP, renderBlocks.renderMinY * (1.0D - renderBlocks.renderMaxX), (1.0D - renderBlocks.renderMinY) * (1.0D - renderBlocks.renderMaxX), (1.0D - renderBlocks.renderMinY) * renderBlocks.renderMaxX, renderBlocks.renderMinY * renderBlocks.renderMaxX);
            renderBlocks.brightnessTopRight = renderBlocks.mixAoBrightness(brightnessMixedXYZNPP, brightnessMixedXYZNNP, brightnessMixedXYZPNP, brightnessMixedXYZPPP, renderBlocks.renderMaxY * (1.0D - renderBlocks.renderMaxX), (1.0D - renderBlocks.renderMaxY) * (1.0D - renderBlocks.renderMaxX), (1.0D - renderBlocks.renderMaxY) * renderBlocks.renderMaxX, renderBlocks.renderMaxY * renderBlocks.renderMaxX);
        }

        return this;
    }
}
