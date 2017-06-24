package gregtech.common.tileentities.automation;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Buffer;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Utility;
import gregtech.common.gui.GT_Container_ChestBuffer;
import gregtech.common.gui.GT_GUIContainer_ChestBuffer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class GT_MetaTileEntity_ItemDistributor extends GT_MetaTileEntity_Buffer {
	private byte[] weights = {1, 1, 1, 1, 1, 1};
	boolean onlyOutputToInventories = true;
	
	public GT_MetaTileEntity_ItemDistributor(int aID, String aName, String aNameRegional, int aTier) {
		super(aID, aName, aNameRegional, aTier, 28, "Buffering lots of incoming Items");
	}

	public GT_MetaTileEntity_ItemDistributor(int aID, String aName, String aNameRegional, int aTier, int aInvSlotCount,
			String aDescription) {
		super(aID, aName, aNameRegional, aTier, aInvSlotCount, aDescription);
	}

	public GT_MetaTileEntity_ItemDistributor(String aName, int aTier, int aInvSlotCount, String aDescription, ITexture[][][] aTextures) {
		super(aName, aTier, aInvSlotCount, aDescription, aTextures);
	}

	public GT_MetaTileEntity_ItemDistributor(String aName, int aTier, int aInvSlotCount, String[] aDescription, ITexture[][][] aTextures) {
		super(aName, aTier, aInvSlotCount, aDescription, aTextures);
	}

	public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
		return new GT_MetaTileEntity_ItemDistributor(this.mName, this.mTier, this.mInventory.length, this.mDescriptionArray, this.mTextures);
	}

	public ITexture getOverlayIcon() {
		return new GT_RenderedTexture(Textures.BlockIcons.AUTOMATION_CHESTBUFFER);
	}

	public boolean isValidSlot(int aIndex) {
		return aIndex < this.mInventory.length - 1;
	}

	protected void moveItems(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
		fillStacksIntoFirstSlots();
		int tCost = 0;
		for (byte side = 0; side < 6; side++) {
			TileEntity adjacentTileEntity = aBaseMetaTileEntity.getTileEntityAtSide(side);
			if(!onlyOutputToInventories || adjacentTileEntity != null){
		        tCost = GT_Utility.moveOneItemStack(aBaseMetaTileEntity, adjacentTileEntity, 
		        		side, GT_Utility.getOppositeSide(side), null, false, (byte) 64, (byte) 1, weights[side], weights[side]);				
			}
		}
        if (tCost > 0 || aBaseMetaTileEntity.hasInventoryBeenModified()) {
            mSuccess = 50;
            aBaseMetaTileEntity.decreaseStoredEnergyUnits(Math.abs(tCost), true);
        }
		fillStacksIntoFirstSlots();
	}

	protected void fillStacksIntoFirstSlots() {
		for (int i = 0; i < this.mInventory.length - 1; i++) {
			for (int j = i + 1; j < this.mInventory.length - 1; j++) {
				if ((this.mInventory[j] != null)
						&& ((this.mInventory[i] == null) || (GT_Utility.areStacksEqual(this.mInventory[i], this.mInventory[j])))) {
					GT_Utility.moveStackFromSlotAToSlotB(getBaseMetaTileEntity(), getBaseMetaTileEntity(), j, i, (byte) 64, (byte) 1,
							(byte) 64, (byte) 1);
				}
			}
		}
	}

	public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
		return new GT_Container_ChestBuffer(aPlayerInventory, aBaseMetaTileEntity);
	}

	public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
		return new GT_GUIContainer_ChestBuffer(aPlayerInventory, aBaseMetaTileEntity);
	}
}
