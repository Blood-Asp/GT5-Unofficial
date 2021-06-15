package gregtech.api.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

/**
 * NEVER INCLUDE THIS FILE IN YOUR MOD!!!
 * <p/>
 * Main GUI-Container-Class which basically contains the Code needed to prevent crashes from improperly Coded Items.
 */
public class GT_GUIContainer extends GuiContainer {

    public boolean mCrashed = false;

    public ResourceLocation mGUIbackground;

    public String mGUIbackgroundPath;
    
    public static ResourceLocation mGUITitleBackground;
    static { mGUITitleBackground = new ResourceLocation(gregtech.api.enums.GT_Values.RES_PATH_GUI + "GUITitle.png");};

    public GT_GUIContainer(Container aContainer, String aGUIbackground) {
        super(aContainer);
        mGUIbackground = new ResourceLocation(mGUIbackgroundPath = aGUIbackground);
    }

    public int getLeft() {
        return guiLeft;
    }

    public int getTop() {
        return guiTop;
    }
    
    protected void drawGuiContainerTitleBackground() {
    	int x = (width - xSize) / 2;
    	int y = (height - ySize) / 2;
    	GL11.glColor3ub((byte) 255, (byte) 255, (byte) 255);
    	mc.renderEngine.bindTexture(mGUITitleBackground);
    	drawTexturedModalRect(x, y - 17, 0, 0, xSize, 17);   
    }
    
    protected void drawGuiContainerTitleForeground(String mName, int xOffset, int yOffset, int colorDec) {
    	fontRendererObj.drawString(mName, xOffset, yOffset, colorDec);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        //
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        mc.renderEngine.bindTexture(mGUIbackground);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        try {
            super.drawScreen(par1, par2, par3);
        } catch (Throwable e) {
            try {
                Tessellator.instance.draw();
            } catch (Throwable f) {
                //
            }
        }
    }
    /*
    @Override
    protected void drawSlotInventory(Slot par1Slot) {
        try {
        	super.drawSlotInventory(par1Slot);
        } catch(Throwable e) {
            try {
            	Tessellator.instance.draw();
            } catch(Throwable f) {}
        	if (!mCrashed) {
        		GT_Log.out.println("Clientside Slot drawing Crash prevented. Seems one Itemstack causes Problems with negative Damage Values or the Wildcard Damage Value. This is absolutely NOT a Bug of the GregTech-Addon, so don't even think about reporting it to me, it's a Bug of the Mod, which belongs to the almost-crash-causing Item, so bug that Mods Author and not me! Did you hear it? NOT ME!!!");
        		e.printStackTrace();
            	mCrashed = true;
        	}
        }
    }*/
}
