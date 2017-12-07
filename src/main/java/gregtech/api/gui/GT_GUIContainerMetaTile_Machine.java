package gregtech.api.gui;

import gregtech.api.GregTech_API;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemDye;
import org.lwjgl.opengl.GL11;

/**
 * NEVER INCLUDE THIS FILE IN YOUR MOD!!!
 * <p/>
 * The GUI-Container I use for all my MetaTileEntities
 */
public class GT_GUIContainerMetaTile_Machine extends GT_GUIContainer {

    public final GT_ContainerMetaTile_Machine mContainer;

    public GT_GUIContainerMetaTile_Machine(GT_ContainerMetaTile_Machine aContainer, String aGUIbackground) {
        super(aContainer, aGUIbackground);
        mContainer = aContainer;
    }

    public GT_GUIContainerMetaTile_Machine(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity, String aGUIbackground) {
        this(new GT_ContainerMetaTile_Machine(aInventoryPlayer, aTileEntity), aGUIbackground);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        super.drawGuiContainerBackgroundLayer(par1, par2, par3);
        if (GregTech_API.sColouredGUI && mContainer != null && mContainer.mTileEntity != null) {
            int tColour = mContainer.mTileEntity.getColourization() & 15;
            if (tColour < ItemDye.field_150922_c.length) {
                tColour = ItemDye.field_150922_c[tColour];
                GL11.glColour4f(((tColour >> 16) & 255) / 255.0F, ((tColour >> 8) & 255) / 255.0F, (tColour & 255) / 255.0F, 1.0F);
            } else GL11.glColour4f(1.0F, 1.0F, 1.0F, 1.0F);
        } else GL11.glColour4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}