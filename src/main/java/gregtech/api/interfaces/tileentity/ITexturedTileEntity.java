package gregtech.api.interfaces.tileentity;

import gregtech.api.interfaces.ITexture;
import net.minecraft.block.Block;

public interface ITexturedTileEntity {
    /**
     * @return the Textures rendered by the GT Rendering
     */
	ITexture[] getTexture(Block aBlock, byte aSide);
}