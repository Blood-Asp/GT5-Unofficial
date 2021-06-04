package gregtech.common.covers;

import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.gui.GT_GUICover;
import gregtech.api.gui.widgets.GT_GuiIcon;
import gregtech.api.gui.widgets.GT_GuiIconCheckButton;
import gregtech.api.gui.widgets.GT_GuiIntegerTextBox;
import gregtech.api.interfaces.IGuiScreen;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.net.GT_Packet_TileEntityCover;
import gregtech.api.util.GT_CoverBehavior;
import gregtech.api.util.GT_Utility;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fluids.Fluid;

public abstract class GT_Cover_RedstoneWirelessBase extends GT_CoverBehavior {

    private static int MAX_CHANNEL = 65535;

    @Override
    public boolean onCoverRemoval(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity, boolean aForced) {
        GregTech_API.sWirelessRedstone.put(Integer.valueOf(aCoverVariable), Byte.valueOf((byte) 0));
        return true;
    }

    @Override
    public boolean onCoverRightclick(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (((aX > 0.375D) && (aX < 0.625D)) || ((aSide > 3) && ((aY > 0.375D) && (aY < 0.625D)))) {
            GregTech_API.sWirelessRedstone.put(Integer.valueOf(aCoverVariable), Byte.valueOf((byte) 0));
            aCoverVariable = ((Integer)GT_Utility.stackToInt(aPlayer.inventory.getCurrentItem())).hashCode();

            int playerHash = aPlayer.getDisplayName().hashCode();
            aCoverVariable = playerHash & 0xffff0000 | aCoverVariable & 0x0000ffff;

            aTileEntity.setCoverDataAtSide(aSide, aCoverVariable);
            GT_Utility.sendChatToPlayer(aPlayer, trans("081", "Frequency: ") + aCoverVariable);
            return true;
        }
        return false;
    }

    @Override
    public int onCoverScrewdriverclick(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (((aX > 0.375D) && (aX < 0.625D)) || ((aSide <= 3) || (((aY > 0.375D) && (aY < 0.625D)) || ((((aZ <= 0.375D) || (aZ >= 0.625D))))))) {
            GregTech_API.sWirelessRedstone.put(Integer.valueOf(aCoverVariable), Byte.valueOf((byte) 0));
            float[] tCoords = GT_Utility.getClickedFacingCoords(aSide, aX, aY, aZ);

            short tAdjustVal = 0;

            switch ((byte) ((byte) (int) (tCoords[0] * 2.0F) + 2 * (byte) (int) (tCoords[1] * 2.0F))) {
                case 0:
                    tAdjustVal = -32;
                    break;
                case 1:
                    tAdjustVal = 32;
                    break;
                case 2:
                    tAdjustVal = -1024;
                    break;
                case 3:
                    tAdjustVal = 1024;
            }

            if ((aCoverVariable & 0x0000ffff) > Short.MAX_VALUE)
            {

            }
        }
        GT_Utility.sendChatToPlayer(aPlayer, trans("081", "Frequency: ") + aCoverVariable);
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
    public String getDescription(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return trans("081", "Frequency: ") + aCoverVariable;
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
        return new GUI(aSide, aCoverID, coverData, aTileEntity);
    }

    private class GUI extends GT_GUICover {
        private final byte side;
        private final int coverID;
        private GT_GuiIntegerTextBox fBox;
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

            fBox = new GT_GuiShortTextBox(this, 2,startX + spaceX*0,startY+spaceY*0 + 2, spaceX*4-3,12);
            fBox.setText(String.valueOf(coverVariable & 0x0000ffff));
            fBox.setMaxStringLength(12);

            GuiButton b;
            b = new GT_GuiIconCheckButton(this, 0, startX + spaceX * 0, startY + spaceY * 2, GT_GuiIcon.CHECKMARK, null);
        }

        @Override
        public void drawExtras(int mouseX, int mouseY, float parTicks) {
            super.drawExtras(mouseX, mouseY, parTicks);
            this.getFontRenderer().drawString(trans("246","Frequency" ),  startX + spaceX*4, 4+startY+spaceY*0, 0xFF555555);
            this.getFontRenderer().drawString(trans("601", "Use Private Frequency"), startX + spaceX * 1, startY + spaceY * 2 + 4, 0xFF555555);
        }

        @Override
        protected void onInitGui(int guiLeft, int guiTop, int gui_width, int gui_height) {
            fBox.setFocused(true);
        }

        @Override
        public void onMouseWheel(int x, int y, int delta) {
            for (GT_GuiIntegerTextBox box : textBoxes){
                if (box.isFocused()) {
                    int step = Math.max(1, Math.abs(delta / 120));
                    step = (isShiftKeyDown() ? 1000 : isCtrlKeyDown() ? 50 : 1) * (delta > 0 ? step : -step);
                    long i;
                    try {
                        i = Long.parseLong(box.getText());
                    } catch (NumberFormatException e) {
                        return;
                    }
                    i = i + step;
                    if (i > MAX_CHANNEL)
                        i = MAX_CHANNEL;
                    else if (i < 0)
                        i = 0;

                    box.setText(String.valueOf(i));
                    return;
                }
            }
        }

        @Override
        public void applyTextBox(GT_GuiIntegerTextBox box) {
            int i;
            String s = box.getText().trim();
            try {
                i = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                resetTextBox(box);
                return;
            }

            if (i > MAX_CHANNEL)
                i = MAX_CHANNEL;
            else if (i < 0)
                i = 0;

            coverVariable = (coverVariable & 0xffff0000) | (i & 0x0000ffff);
            fBox.setText(Integer.toString(coverVariable & 0x0000ffff));
            GT_Values.NW.sendToServer(new GT_Packet_TileEntityCover(side, coverID, coverVariable, tile));
        }

        @Override
        public void resetTextBox(GT_GuiIntegerTextBox box) {
            box.setText(String.valueOf(coverVariable & 0x0000ffff));
        }

        private class GT_GuiShortTextBox extends GT_GuiIntegerTextBox {

            public GT_GuiShortTextBox(IGuiScreen gui, int id, int x, int y, int width, int height) {
                super(gui, id, x, y, width, height);
            }

            @Override
            public boolean textboxKeyTyped(char c, int key) {
                String tText = getText().trim();
                int tValue = 0;

                super.textboxKeyTyped(c, key);

                int cursorPos = this.getCursorPosition();

                String newText = getText().trim();
                if (newText.length() > 0) {
                    try {
                        tValue = Integer.parseInt(newText);
                    } catch (NumberFormatException ignored) {}

                    if (tValue > MAX_CHANNEL)
                        setText(String.valueOf(MAX_CHANNEL));
                    else
                        setText(String.valueOf(tValue));

                    setCursorPosition(cursorPos);
                }

                return true;
            }
        }
    }
}
