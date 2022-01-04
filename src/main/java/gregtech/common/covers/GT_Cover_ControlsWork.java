package gregtech.common.covers;

import gregtech.api.enums.GT_Values;
import gregtech.api.gui.GT_GUICover;
import gregtech.api.gui.widgets.GT_GuiIcon;
import gregtech.api.gui.widgets.GT_GuiIconButton;
import gregtech.api.gui.widgets.GT_GuiIconCheckButton;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.interfaces.tileentity.IMachineProgress;
import gregtech.api.net.GT_Packet_TileEntityCover;
import gregtech.api.util.GT_CoverBehavior;
import gregtech.api.util.GT_Utility;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.Fluid;

public class GT_Cover_ControlsWork extends GT_CoverBehavior {
    @Override
    public int doCoverThings(byte aSide, byte aInputRedstone, int aCoverID, int aCoverVariable, ICoverable aTileEntity, long aTimer) {
        if (aTileEntity instanceof IMachineProgress) {
            IMachineProgress machine = (IMachineProgress) aTileEntity;
            if (aCoverVariable < 2) {
                if ((aInputRedstone > 0) == (aCoverVariable == 0)) {
                    if (!machine.isAllowedToWork())
                        machine.enableWorking();
                } else if (machine.isAllowedToWork())
                    machine.disableWorking();
                machine.setWorkDataValue(aInputRedstone);
            } else if (aCoverVariable == 2 && machine.isAllowedToWork()) {
                machine.disableWorking();
            } else {
                if (machine.wasShutdown()) {
                    machine.disableWorking();
                    GT_Utility.sendChatToPlayer(lastPlayer, aTileEntity.getInventoryName() + "at " + String.format("(%d,%d,%d)", aTileEntity.getXCoord(), aTileEntity.getYCoord(), aTileEntity.getZCoord()) + " shut down.");
                    return 2;
                } else {
                    return 3 + doCoverThings(aSide, aInputRedstone, aCoverID, aCoverVariable - 3, aTileEntity, aTimer);
                }
            }
        }
        return aCoverVariable;
    }

    @Override
    public boolean letsEnergyIn(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return true;
    }

    @Override
    public boolean letsEnergyOut(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return true;
    }

    @Override
    public boolean letsFluidIn(byte aSide, int aCoverID, int aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
        return true;
    }

    @Override
    public boolean letsFluidOut(byte aSide, int aCoverID, int aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
        return true;
    }

    @Override
    public boolean letsItemsIn(byte aSide, int aCoverID, int aCoverVariable, int aSlot, ICoverable aTileEntity) {
        return true;
    }

    @Override
    public boolean letsItemsOut(byte aSide, int aCoverID, int aCoverVariable, int aSlot, ICoverable aTileEntity) {
        return true;
    }

    @Override
    public boolean onCoverRemoval(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity, boolean aForced) {
        if ((aTileEntity instanceof IMachineProgress)) {
            ((IMachineProgress) aTileEntity).enableWorking();
            ((IMachineProgress) aTileEntity).setWorkDataValue((byte) 0);
        }
        return true;
    }

    @Override
    public int onCoverScrewdriverclick(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        aCoverVariable = (aCoverVariable + (aPlayer.isSneaking()? -1 : 1)) % 5;
        if(aCoverVariable <0){aCoverVariable = 2;}
        if (aCoverVariable == 0) {
            GT_Utility.sendChatToPlayer(aPlayer, trans("003", "Enable with Signal"));
        }
        if (aCoverVariable == 1) {
            GT_Utility.sendChatToPlayer(aPlayer, trans("004", "Disable with Signal"));
        }
        if (aCoverVariable == 2) {
            GT_Utility.sendChatToPlayer(aPlayer, trans("005", "Disabled"));
        }
        if (aCoverVariable == 3) {
            GT_Utility.sendChatToPlayer(aPlayer, trans("505", "Enable with Signal (Safe)"));
        }
        if (aCoverVariable == 4) {
            GT_Utility.sendChatToPlayer(aPlayer, trans("506", "Disable with Signal (Safe)"));
        }
        // TODO: Set lastPlayer
        return aCoverVariable;
    }

    @Override
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
        return new GT_Cover_ControlsWork.GUI(aSide, aCoverID, coverData, aTileEntity);
    }

    private class GUI extends GT_GUICover {
        private final byte side;
        private final int coverID;
        private int coverVariable;

        private static final int startX = 10;
        private static final int startY = 25;
        private static final int spaceX = 18;
        private static final int spaceY = 18;

        public GUI(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
            super(aTileEntity, 176, 107, GT_Utility.intToStack(aCoverID));
            this.side = aSide;
            this.coverID = aCoverID;
            this.coverVariable = aCoverVariable;

            new GT_GuiIconButton(this, 0, startX + spaceX * 0, startY + spaceY * 0, GT_GuiIcon.REDSTONE_ON);
            new GT_GuiIconButton(this, 1, startX + spaceX * 0, startY + spaceY * 1, GT_GuiIcon.REDSTONE_OFF);
            new GT_GuiIconButton(this, 2, startX + spaceX * 0, startY + spaceY * 2, GT_GuiIcon.CROSS);

            new GT_GuiIconCheckButton(this, 3, startX + spaceX * 0, startY + spaceY * 3, GT_GuiIcon.CHECKMARK, GT_GuiIcon.CROSS).setChecked(aCoverVariable > 2);
        }

        @Override
        public void drawExtras(int mouseX, int mouseY, float parTicks) {
            super.drawExtras(mouseX, mouseY, parTicks);
            this.fontRendererObj.drawString(trans("243", "Enable with Redstone"), 3+startX + spaceX*1, 4+startY+spaceY*0, 0xFF555555);
            this.fontRendererObj.drawString(trans("244", "Disable with Redstone"),3+startX + spaceX*1, 4+startY+spaceY*1, 0xFF555555);
            this.fontRendererObj.drawString(trans("245", "Disable machine"),              3+startX + spaceX*1, 4+startY+spaceY*2, 0xFF555555);
            this.fontRendererObj.drawString(trans("507", "Safe Mode"), 3+startX + spaceX*1, 4+startY+spaceY*3, 0xFF555555);
        }

        @Override
        protected void onInitGui(int guiLeft, int guiTop, int gui_width, int gui_height) {
            updateButtons();
        }

        @Override
        public void buttonClicked(GuiButton btn) {
            if (getClickable(btn.id)) {
                int bID = btn.id;
                if (bID == 3) {
                    ((GT_GuiIconCheckButton) btn).setChecked(!((GT_GuiIconCheckButton) btn).isChecked());
                } else {
                    coverVariable = getNewCoverVariable(bID);
                }
                adjustCoverVariable();
                // TODO: Set lastPlayer;
                GT_Values.NW.sendToServer(new GT_Packet_TileEntityCover(side, coverID, coverVariable, tile));
            }
            updateButtons();
        }

        private void updateButtons() {
            GuiButton b;
            for (Object o : buttonList) {
                b = (GuiButton) o;
                b.enabled = getClickable(b.id);
            }
        }

        private int getNewCoverVariable(int id) {
            return id;
        }

        private boolean getClickable(int id) {
            return ((id != coverVariable && id != coverVariable - 3) || id == 3);
        }

        private void adjustCoverVariable() {
            boolean safeMode = ((GT_GuiIconCheckButton) buttonList.get(3)).isChecked();
            if (safeMode && coverVariable < 2) {
                coverVariable += 3;
            }
            if (!safeMode && coverVariable > 2) {
                coverVariable -= 3;
            }
        }

    }
}
