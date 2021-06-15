package gregtech.common.gui;

import gregtech.api.gui.GT_GUIContainerMetaTile_Machine;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GT_LanguageManager;
import net.minecraft.entity.player.InventoryPlayer;

public class GT_GUIContainer_SuperBuffer extends GT_GUIContainerMetaTile_Machine {
	String unlocalisedName;
    public GT_GUIContainer_SuperBuffer(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity) {
        super(new GT_Container_SuperBuffer(aInventoryPlayer, aTileEntity), "gregtech:textures/gui/SuperBuffer.png");
        unlocalisedName = aTileEntity.getMetaTileEntity().getMetaName();
    }
    
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
    	String mName = GT_LanguageManager.getTranslation("gt.blockmachines." + unlocalisedName + ".name");
    	super.drawGuiContainerTitleForeground(mName, 5, -12, 4210752);
    }


    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    	super.drawGuiContainerTitleBackground();
        super.drawGuiContainerBackgroundLayer(par1, par2, par3);
        int x = (this.width - this.xSize) / 2;
        int y = (this.height - this.ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, this.xSize, this.ySize);
    }
}
