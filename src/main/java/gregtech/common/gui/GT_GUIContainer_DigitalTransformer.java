package gregtech.common.gui;

import gregtech.api.gui.GT_GUIContainerMetaTile_Machine;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Transformer;
import net.minecraft.entity.player.InventoryPlayer;

import static gregtech.api.enums.GT_Values.*;

public class GT_GUIContainer_DigitalTransformer extends GT_GUIContainerMetaTile_Machine {
    public GT_GUIContainer_DigitalTransformer(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity) {
        super(new GT_Container_DigitalTransformer(aInventoryPlayer, aTileEntity), RES_PATH_GUI + "DigitalTransformer.png");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        GT_Container_DigitalTransformer dpg = (GT_Container_DigitalTransformer) mContainer;
        fontRendererObj.drawString("IN", 12, 18, 16448255);
        fontRendererObj.drawString("V: " + VN[((GT_MetaTileEntity_Transformer) dpg.mTileEntity.getMetaTileEntity()).mTier] + " (" + formatNumbers(V[((GT_MetaTileEntity_Transformer) dpg.mTileEntity.getMetaTileEntity()).mTier]) + ")", 36, 9, 16448255);
        fontRendererObj.drawString("A: " + dpg.iAmp, 36, 27, 16448255);
        fontRendererObj.drawString("OUT", 12, 18 + 36, 16448255);
        fontRendererObj.drawString("V: " + VN[dpg.oTier] + " (" + formatNumbers(V[dpg.oTier]) + ")", 36, 9 + 36, 16448255);
        fontRendererObj.drawString("A: " + dpg.oAMP, 36, 27 + 36, 16448255);
    }

    private String formatNumbers(long num) {
        if (num >= 1000000) {
            return num / 1000000 + "M";
        }
        if (num >= 1000) {
            return num / 1000 + "K";
        }
        return num + "";
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        super.drawGuiContainerBackgroundLayer(par1, par2, par3);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
