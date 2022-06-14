package gregtech.common.covers;

import gregtech.api.enums.GT_Values;
import gregtech.api.gui.GT_GUICover;
import gregtech.api.gui.widgets.GT_GuiIcon;
import gregtech.api.gui.widgets.GT_GuiIconCheckButton;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IBatteryContainer;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.items.GT_MetaBase_Item;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicBatteryBuffer;
import gregtech.api.net.GT_Packet_TileEntityCover;
import gregtech.api.util.GT_CoverBehavior;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_Utility;
import ic2.api.item.IElectricItem;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;

public class GT_Cover_EUMeter extends GT_CoverBehavior {
    @Override
    public boolean isRedstoneSensitive(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity, long aTimer) {
        return false;
    }

    @Override
    public int doCoverThings(byte aSide, byte aInputRedstone, int aCoverID, int aCoverVariable, ICoverable aTileEntity, long aTimer) {
        long tScale = 0L;
        if (aCoverVariable < 2) {
            tScale = aTileEntity.getUniversalEnergyCapacity() / 15L;
            if (tScale > 0L) {
                aTileEntity.setOutputRedstoneSignal(aSide, aCoverVariable % 2 == 0 ? (byte) (int) (aTileEntity.getUniversalEnergyStored() / tScale) : (byte) (int) (15L - aTileEntity.getUniversalEnergyStored() / tScale));
            } else {
                aTileEntity.setOutputRedstoneSignal(aSide, (byte) (aCoverVariable % 2 == 0 ? 0 : 15));
            }
        } else if (aCoverVariable < 4) {
            tScale = aTileEntity.getEUCapacity() / 15L;
            if (tScale > 0L) {
                aTileEntity.setOutputRedstoneSignal(aSide, aCoverVariable % 2 == 0 ? (byte) (int) (aTileEntity.getStoredEU() / tScale) : (byte) (int) (15L - aTileEntity.getStoredEU() / tScale));
            } else {
                aTileEntity.setOutputRedstoneSignal(aSide, (byte) (aCoverVariable % 2 == 0 ? 0 : 15));
            }
        } else if (aCoverVariable < 6) {
            tScale = aTileEntity.getSteamCapacity() / 15L;
            if (tScale > 0L) {
                aTileEntity.setOutputRedstoneSignal(aSide, aCoverVariable % 2 == 0 ? (byte) (int) (aTileEntity.getStoredSteam() / tScale) : (byte) (int) (15L - aTileEntity.getStoredSteam() / tScale));
            } else {
                aTileEntity.setOutputRedstoneSignal(aSide, (byte) (aCoverVariable % 2 == 0 ? 0 : 15));
            }
        } else if (aCoverVariable < 8) {
            tScale = aTileEntity.getInputVoltage() * aTileEntity.getInputAmperage() / 15L;
            if (tScale > 0L) {
                aTileEntity.setOutputRedstoneSignal(aSide, aCoverVariable % 2 == 0 ? (byte) (int) (aTileEntity.getAverageElectricInput() / tScale) : (byte) (int) (15L - aTileEntity.getAverageElectricInput() / tScale));
            } else {
                aTileEntity.setOutputRedstoneSignal(aSide, (byte) (aCoverVariable % 2 == 0 ? 0 : 15));
            }
        } else if (aCoverVariable < 10) {
            tScale = aTileEntity.getOutputVoltage() * aTileEntity.getOutputAmperage() / 15L;
            if (tScale > 0L) {
                aTileEntity.setOutputRedstoneSignal(aSide, aCoverVariable % 2 == 0 ? (byte) (int) (aTileEntity.getAverageElectricOutput() / tScale) : (byte) (int) (15L - aTileEntity.getAverageElectricOutput() / tScale));
            } else {
                aTileEntity.setOutputRedstoneSignal(aSide, (byte) (aCoverVariable % 2 == 0 ? 0 : 15));
            }
        } else if (aCoverVariable < 12) {
            tScale = aTileEntity.getEUCapacity();
            long tStored = aTileEntity.getStoredEU();
            if (aTileEntity instanceof IGregTechTileEntity) {
                IGregTechTileEntity tTileEntity = (IGregTechTileEntity) aTileEntity;
                IMetaTileEntity mTileEntity = tTileEntity.getMetaTileEntity();
                if (mTileEntity instanceof IBatteryContainer) {
                    tScale += ((IBatteryContainer) mTileEntity).getBatteryCapacity();
                    tStored += ((IBatteryContainer) mTileEntity).getBatteryEnergyStored();
                }
            }
            tScale = tScale / 15L;
            if (tScale > 0L) {
                aTileEntity.setOutputRedstoneSignal(aSide, aCoverVariable % 2 == 0 ? (byte) (int) (tStored / tScale) : (byte) (int) (15L - tStored / tScale));
            } else {
                aTileEntity.setOutputRedstoneSignal(aSide, (byte) (aCoverVariable % 2 == 0 ? 0 : 15));
            }
        } else if (aCoverVariable < 14) {
            long tStored = aTileEntity.getStoredEU();
            if (aTileEntity instanceof IGregTechTileEntity) {
                IGregTechTileEntity tTileEntity = (IGregTechTileEntity) aTileEntity;
                IMetaTileEntity mTileEntity = tTileEntity.getMetaTileEntity();
                if (mTileEntity instanceof IBatteryContainer) {
                    tScale = ((IBatteryContainer) mTileEntity).getBatteryCapacity();
                    tStored = ((IBatteryContainer) mTileEntity).getBatteryEnergyStored();
                }
            }
            tScale = tScale / 15L;
            if (tScale > 0L) {
                aTileEntity.setOutputRedstoneSignal(aSide, aCoverVariable % 2 == 0 ? (byte) (int) (tStored / tScale) : (byte) (int) (15L - tStored / tScale));
            } else {
                aTileEntity.setOutputRedstoneSignal(aSide, (byte) (aCoverVariable % 2 == 0 ? 0 : 15));
            }
        }
        return aCoverVariable;
    }

    @Override
    public int onCoverScrewdriverclick(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        aCoverVariable = (aCoverVariable + (aPlayer.isSneaking()? -1 : 1)) % 14;
        if(aCoverVariable <0){aCoverVariable = 13;}
        switch(aCoverVariable) {
            case 0: GT_Utility.sendChatToPlayer(aPlayer, trans("031", "Normal Universal Storage")); break;
            case 1: GT_Utility.sendChatToPlayer(aPlayer, trans("032", "Inverted Universal Storage")); break;
            case 2: GT_Utility.sendChatToPlayer(aPlayer, trans("033", "Normal Electricity Storage")); break;
            case 3: GT_Utility.sendChatToPlayer(aPlayer, trans("034", "Inverted Electricity Storage")); break;
            case 4: GT_Utility.sendChatToPlayer(aPlayer, trans("035", "Normal Steam Storage")); break;
            case 5: GT_Utility.sendChatToPlayer(aPlayer, trans("036", "Inverted Steam Storage")); break;
            case 6: GT_Utility.sendChatToPlayer(aPlayer, trans("037", "Normal Average Electric Input")); break;
            case 7: GT_Utility.sendChatToPlayer(aPlayer, trans("038", "Inverted Average Electric Input")); break;
            case 8: GT_Utility.sendChatToPlayer(aPlayer, trans("039", "Normal Average Electric Output")); break;
            case 9: GT_Utility.sendChatToPlayer(aPlayer, trans("040", "Inverted Average Electric Output")); break;
            case 10: GT_Utility.sendChatToPlayer(aPlayer, trans("041", "Normal Electricity Storage(Including Batteries)")); break;
            case 11: GT_Utility.sendChatToPlayer(aPlayer, trans("042", "Inverted Electricity Storage(Including Batteries)")); break;
            case 12: GT_Utility.sendChatToPlayer(aPlayer, trans("043", "Normal Electricity Storage(Only Batteries)")); break;
            case 13: GT_Utility.sendChatToPlayer(aPlayer, trans("044", "Inverted Electricity Storage(Only Batteries)")); break;
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
    public boolean manipulatesSidedRedstoneOutput(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return true;
    }

    @Override
    public int getTickRate(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return 20;
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

            GuiButton b;
            b = new GT_GuiIconCheckButton(this, 0, startX + spaceX*0, startY+spaceY*1, GT_GuiIcon.CHECKMARK, null)
                    .setTooltipText(trans("256", "Universal Storage"));
            b = new GT_GuiIconCheckButton(this, 1, startX + spaceX*0, startY+spaceY*0, GT_GuiIcon.CHECKMARK, null)
                    .setTooltipText(trans("257", "Electricity Storage"));
            b = new GT_GuiIconCheckButton(this, 2, startX + spaceX*0, startY+spaceY*2, GT_GuiIcon.CHECKMARK, null)
                    .setTooltipText(trans("258", "Steam Storage"));
            b = new GT_GuiIconCheckButton(this, 3, startX + spaceX*4, startY+spaceY*1, GT_GuiIcon.CHECKMARK, null)
                    .setTooltipText(trans("259", "Average Electric Input"));
            b = new GT_GuiIconCheckButton(this, 4, startX + spaceX*4, startY+spaceY*2, GT_GuiIcon.CHECKMARK, null)
                    .setTooltipText(trans("260", "Average Electric Output"));
            b = new GT_GuiIconCheckButton(this, 5, startX + spaceX*4, startY+spaceY*0, GT_GuiIcon.CHECKMARK, null)
                    .setTooltipText(trans("261", "Electricity Storage(Including Batteries)"));
            b = new GT_GuiIconCheckButton(this, 6, startX + spaceX*0, startY+spaceY*3, GT_GuiIcon.CHECKMARK, null)
                    .setTooltipText(trans("262", "Electricity Storage(Only Batteries)"));
            b = new GT_GuiIconCheckButton(this, 7, startX + spaceX*4, startY+spaceY*3+4, GT_GuiIcon.REDSTONE_ON, GT_GuiIcon.REDSTONE_OFF);
        }

        @Override
        public void drawExtras(int mouseX, int mouseY, float parTicks) {
            super.drawExtras(mouseX, mouseY, parTicks);
            String s2;
            if ((coverVariable & 0x1) > 0)
                s2 = trans("INVERTED","Inverted");
            else
                s2 = trans("NORMAL","Normal");
            this.fontRendererObj.drawString(s2,  startX + spaceX*5, 8+startY+spaceY*3, 0xFF555555);

            this.fontRendererObj.drawString("Universal",
                    startX + spaceX*1, 4+startY+spaceY*1, 0xFF555555);
            this.fontRendererObj.drawString("Int. EU",
                    startX + spaceX*1, 4+startY+spaceY*0, 0xFF555555);
            this.fontRendererObj.drawString("Steam",
                    startX + spaceX*1, 4+startY+spaceY*2, 0xFF555555);
            this.fontRendererObj.drawString("Avg. Input",
                    startX + spaceX*5, 4+startY+spaceY*1, 0xFF555555);
            this.fontRendererObj.drawString("Avg. Output",
                    startX + spaceX*5, 4+startY+spaceY*2, 0xFF555555);
            this.fontRendererObj.drawString("EU stored",
                    startX + spaceX*5, 4+startY+spaceY*0, 0xFF555555);
            this.fontRendererObj.drawString("Bat stored",
                    startX + spaceX*1, 4+startY+spaceY*3, 0xFF555555);
        }

        @Override
        protected void onInitGui(int guiLeft, int guiTop, int gui_width, int gui_height) {
            updateButtons();
        }

        @Override
        public void buttonClicked(GuiButton btn){
            if (btn.id == 7 || !isEnabled(btn.id)){
                coverVariable = getNewCoverVariable(btn.id, ((GT_GuiIconCheckButton) btn).isChecked());
                GT_Values.NW.sendToServer(new GT_Packet_TileEntityCover(side, coverID, coverVariable, tile));
            }
            updateButtons();
        }

        private void updateButtons(){
            for (Object o : buttonList)
                ((GT_GuiIconCheckButton) o).setChecked(isEnabled(((GT_GuiIconCheckButton) o).id));
        }

        private int getNewCoverVariable(int id, boolean checked) {
            if (id == 7) {
                if (checked)
                    return coverVariable & ~0x1;
                else
                    return coverVariable | 0x1;
            }
            return (coverVariable & 0x1) | (id << 1) ;
        }

        private boolean isEnabled(int id) {
            if (id == 7)
                return (coverVariable & 0x1) > 0;
            return (coverVariable >> 1) == id;
        }
    }
}
