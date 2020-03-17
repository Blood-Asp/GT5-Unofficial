package gregtech.api.interfaces;

import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

public interface IIconContainer {
    /**
     * @return A regular Icon.
     */
    IIcon getIcon();

    /**
     * @return Icon of the Overlay (or null if there is no Icon)
     */
    IIcon getOverlayIcon();

    /**
     * @return the Default Texture File for this Icon.
     */
    ResourceLocation getTextureFile();
}
