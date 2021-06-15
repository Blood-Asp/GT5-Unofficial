package gregtech.api.gui;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GT_LanguageManager;
import net.minecraft.entity.player.InventoryPlayer;

import static gregtech.api.enums.GT_Values.RES_PATH_GUI;

public class GT_GUIContainer_MaintenanceHatch extends GT_GUIContainerMetaTile_Machine {
	String unlocalisedName;
    public GT_GUIContainer_MaintenanceHatch(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity) {
        super(new GT_Container_MaintenanceHatch(aInventoryPlayer, aTileEntity), RES_PATH_GUI + "Maintenance.png");
        unlocalisedName = aTileEntity.getMetaTileEntity().getMetaName();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
    	String mName = GT_LanguageManager.getTranslation("gt.blockmachines." + unlocalisedName + ".name");
    	super.drawGuiContainerTitleForeground(mName, 5, -12, 4210752);
        fontRendererObj.drawString("Click with Tool to repair.", 5, 5, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
    	super.drawGuiContainerTitleBackground();
        super.drawGuiContainerBackgroundLayer(par1, par2, par3);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
