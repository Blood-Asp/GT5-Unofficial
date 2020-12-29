package gregtech.common.covers;

import gregtech.api.enums.GT_Values;
import gregtech.api.gui.GT_GUICover;
import gregtech.api.gui.widgets.GT_GuiIcon;
import gregtech.api.gui.widgets.GT_GuiIconButton;
import gregtech.api.gui.widgets.GT_GuiIntegerTextBox;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.net.GT_Packet_TileEntityCover;
import gregtech.api.util.GT_CoverBehavior;
import gregtech.api.util.GT_Utility;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class GT_Cover_FluidRegulator extends GT_CoverBehavior {
	public final int mTransferRate;

	public GT_Cover_FluidRegulator(int aTransferRate) {
		this.mTransferRate = aTransferRate;
	}

	public int doCoverThings(byte aSide, byte aInputRedstone, int aCoverID, int aCoverVariable, ICoverable aTileEntity,
			long aTimer) {
		if (aCoverVariable == 0) {
			return aCoverVariable;
		}
		if ((aTileEntity instanceof IFluidHandler)) {
			IFluidHandler tTank1;
			IFluidHandler tTank2;
            ForgeDirection directionFrom;
            ForgeDirection directionTo;
			if (aCoverVariable > 0) {
				tTank2 = aTileEntity.getITankContainerAtSide(aSide);
				tTank1 = (IFluidHandler) aTileEntity;
                directionFrom = ForgeDirection.UNKNOWN;
                directionTo = ForgeDirection.getOrientation(aSide).getOpposite();
			} else {
				tTank1 = aTileEntity.getITankContainerAtSide(aSide);
				tTank2 = (IFluidHandler) aTileEntity;
                directionFrom = ForgeDirection.getOrientation(aSide).getOpposite();
                directionTo = ForgeDirection.UNKNOWN;
			}
			if (tTank1 != null && tTank2 != null) {
				FluidStack tLiquid = tTank1.drain(directionFrom, Math.abs(aCoverVariable), false);
				if (tLiquid != null) {
					tLiquid = tLiquid.copy();
					tLiquid.amount = tTank2.fill(directionTo, tLiquid, false);
					if (tLiquid.amount > 0) {
						tTank2.fill(directionTo, tTank1.drain(directionFrom, tLiquid.amount, true), true);
					}
				}
			}
		}
		return aCoverVariable;
	}

	public int onCoverScrewdriverclick(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity,
			EntityPlayer aPlayer, float aX, float aY, float aZ) {
		if (GT_Utility.getClickedFacingCoords(aSide, aX, aY, aZ)[0] >= 0.5F) {
			aCoverVariable += aPlayer.isSneaking() ? 256 : 16;
		} else {
			aCoverVariable -= aPlayer.isSneaking() ? 256 : 16;
		}
		if (aCoverVariable > mTransferRate) {
			aCoverVariable = mTransferRate;
		}
		if (aCoverVariable < (0 - mTransferRate)) {
			aCoverVariable = (0 - mTransferRate);
		}
		GT_Utility.sendChatToPlayer(aPlayer,
				trans("048", "Pump speed: ") + aCoverVariable + trans("049", "L/tick ") + aCoverVariable * 20 + trans("050", "L/sec"));
		return aCoverVariable;
	}

	public boolean onCoverRightclick(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity,
			EntityPlayer aPlayer, float aX, float aY, float aZ) {
		if (GT_Utility.getClickedFacingCoords(aSide, aX, aY, aZ)[0] >= 0.5F) {
			aCoverVariable++;
		} else {
			aCoverVariable--;
		}
		if (aCoverVariable > mTransferRate) {
			aCoverVariable = mTransferRate;
		}
		if (aCoverVariable < (0 - mTransferRate)) {
			aCoverVariable = (0 - mTransferRate);
		}
		GT_Utility.sendChatToPlayer(aPlayer,
				trans("048", "Pump speed: ") + aCoverVariable + trans("049", "L/tick ") + aCoverVariable * 20 + trans("050", "L/sec"));
		aTileEntity.setCoverDataAtSide(aSide, aCoverVariable);
		return true;
	}

    public boolean letsRedstoneGoIn(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return true;
    }

    public boolean letsRedstoneGoOut(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return true;
    }

    public boolean letsEnergyIn(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return true;
    }

    public boolean letsEnergyOut(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return true;
    }

    public boolean letsItemsIn(byte aSide, int aCoverID, int aCoverVariable, int aSlot, ICoverable aTileEntity) {
        return true;
    }

    public boolean letsItemsOut(byte aSide, int aCoverID, int aCoverVariable, int aSlot, ICoverable aTileEntity) {
        return true;
    }

    public boolean letsFluidIn(byte aSide, int aCoverID, int aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
    	return false;
    }

    public boolean letsFluidOut(byte aSide, int aCoverID, int aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
    	return false;
    }

    public boolean alwaysLookConnected(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return true;
    }

    public int getTickRate(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return 1;
    }

	/**
	 * GUI Stuff
	 */

	@Override
	public boolean hasCoverGUI() {
		return true;
	}

	@Override
	public Object getClientGUI(byte aSide, int aCoverID, int coverData, ICoverable aTileEntity)  {
		return new GT_Cover_FluidRegulator.GUI(aSide, aCoverID, coverData, aTileEntity);
	}

	private class GUI extends GT_GUICover {
		private final byte side;
		private final int coverID;
		private GT_GuiIntegerTextBox tBox, lBox;
		private int coverVariable;

		private final static int startX = 10;
		private final static int startY = 25;
		private final static int spaceX = 18;
		private final static int spaceY = 18;

		private int speed;
		private boolean export;

		public GUI(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
			super(aTileEntity, 176, 107, GT_Utility.intToStack(aCoverID));
			this.side = aSide;
			this.coverID = aCoverID;
			this.coverVariable = aCoverVariable;

			this.speed = Math.abs(coverVariable);
			this.export = coverVariable >= 0;
			new GT_GuiIconButton(this, 0,startX + spaceX*0,startY+spaceY*0, GT_GuiIcon.EXPORT).setTooltipText(trans("006","Export"));
			new GT_GuiIconButton(this, 1,startX + spaceX*1,startY+spaceY*0, GT_GuiIcon.IMPORT).setTooltipText(trans("007","Import"));

			tBox = new GT_GuiIntegerTextBox(this, 2,startX + spaceX*0,startY+spaceY*1 + 2, spaceX*4-3,12);
			tBox.setText(String.valueOf(speed));
			tBox.setMaxStringLength(10);

			lBox = new GT_GuiIntegerTextBox(this, 3,startX + spaceX*0,startY+spaceY*2 + 2, spaceX*4-3,12);
			lBox.setText(String.valueOf(speed*20L));
			lBox.setMaxStringLength(10);
		}

		@Override
		public void drawExtras(int mouseX, int mouseY, float parTicks) {
			super.drawExtras(mouseX, mouseY, parTicks);
			this.getFontRenderer().drawString(trans("229","Import/Export" ),  startX + spaceX*4, 4+startY+spaceY*0, 0xFF555555);
			this.getFontRenderer().drawString(trans("049", "L/tick "),  startX + spaceX*4, 4+startY+spaceY*1, 0xFF555555);
			this.getFontRenderer().drawString(trans("050", "L/sec"),   startX + spaceX*4, 4+startY+spaceY*2, 0xFF555555);
		}

		@Override
		protected void onInitGui(int guiLeft, int guiTop, int gui_width, int gui_height) {
			updateButtons();
			tBox.setFocused(true);
		}

		public void buttonClicked(GuiButton btn){
			if (getClickable(btn.id)){
				coverVariable = getNewCoverVariable(btn.id);
				GT_Values.NW.sendToServer(new GT_Packet_TileEntityCover(side, coverID, coverVariable, tile));
			}
			updateButtons();
		}

		@Override
		public void onMouseWheel(int x, int y, int delta) {
			for (GT_GuiIntegerTextBox box : textBoxes){
				if (box.isFocused()) {
					int step = Math.max(1, Math.abs(delta / 120));
					step = (isShiftKeyDown() ? 50 : isCtrlKeyDown() ? 5 : 1) * (delta > 0 ? step : -step);
					long i;
					try {
						i = Long.parseLong(box.getText());
					} catch (NumberFormatException e) {
						return;
					}
					if (i > (Long.MAX_VALUE-1000))
						break;

					i = i + step;
					if (i <= 0)
						i = 0;
					box.setText(String.valueOf(i));
					break;
				}
			}
		}

		@Override
		public void applyTextBox(GT_GuiIntegerTextBox box) {
			long i;
			String s = box.getText().trim();
			try {
				i = Long.parseLong(s);
			} catch (NumberFormatException e) {
				resetTextBox(box);
				return;
			}

			if (box.id == 3)
				i = i / 20;

			if (i > mTransferRate)
				i = mTransferRate;
			else if (i <= 0)
				i = 0;

			speed = (int) i;

			tBox.setText(String.valueOf(speed));
			lBox.setText(String.valueOf(speed*20));

			coverVariable = getNewCoverVariable(2);
			GT_Values.NW.sendToServer(new GT_Packet_TileEntityCover(side, coverID, coverVariable, tile));
		}

		@Override
		public void resetTextBox(GT_GuiIntegerTextBox box) {
			box.setText(String.valueOf(speed));
		}

		private void updateButtons(){
			GuiButton b;
			for (Object o : buttonList) {
				b = (GuiButton) o;
				b.enabled = getClickable(b.id);
			}
		}

		private int getNewCoverVariable(int id) {
			switch (id) {
				case 0:
					export = true;
					return speed;
				case 1:
					export = false;
					return -speed;
				case 2:
					if (export)
						return speed;
					else
						return -speed;
			}
			return coverVariable;
		}

		private boolean getClickable(int id) {
			return (id == 0) ^ (export);
		}
	}
}
