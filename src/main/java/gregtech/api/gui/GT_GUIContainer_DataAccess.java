package gregtech.api.gui;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import static gregtech.api.enums.GT_Values.RES_PATH_GUI;

public class GT_GUIContainer_DataAccess extends GT_GUIContainer_4by4 {

	public GT_GUIContainer_DataAccess(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity, String aName) {
        super(aInventoryPlayer, aTileEntity, aName);
        mGUIbackground = new ResourceLocation(mGUIbackgroundPath = RES_PATH_GUI + "DataAccess.png");
    }
}
