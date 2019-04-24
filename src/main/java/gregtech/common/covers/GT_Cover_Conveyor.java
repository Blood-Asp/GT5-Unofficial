package gregtech.common.covers;

import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.interfaces.tileentity.IMachineProgress;
import gregtech.api.util.GT_CoverBehavior;
import gregtech.api.util.GT_Utility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fluids.Fluid;

public class GT_Cover_Conveyor
        extends GT_CoverBehavior {

    public final int mTickRate;
    public final byte stacksByOperation;

    public GT_Cover_Conveyor(int aTickRate, byte aStacksByOperation) {
        this.mTickRate = aTickRate;
        this.stacksByOperation = aStacksByOperation;
    }

    public int doCoverThings(byte aSide, byte aInputRedstone, int aCoverID, int aCoverVariable, ICoverable aTileEntity, long aTimer) {
        if ((aCoverVariable % 6 > 1) && ((aTileEntity instanceof IMachineProgress))) {
            if (((IMachineProgress) aTileEntity).isAllowedToWork() != aCoverVariable % 6 < 4) {
                return aCoverVariable;
            }
        }
        TileEntity tTileEntity = aTileEntity.getTileEntityAtSide(aSide);
        //aTileEntity.decreaseStoredEnergyUnits(1L, true);

        if (((aCoverVariable % 2 == 0) || (aSide != 1)) && ((aCoverVariable % 2 != 0) || (aSide != 0)) && (aTileEntity.getUniversalEnergyCapacity() >= 128L)) {
            if (aTileEntity.isUniversalEnergyStored(256L)) {
                aTileEntity.decreaseStoredEnergyUnits(4 * GT_Utility.moveSomeItemStacks(aCoverVariable % 2 == 0 ? aTileEntity : tTileEntity, aCoverVariable % 2 != 0 ? aTileEntity : tTileEntity, aCoverVariable % 2 != 0 ? GT_Utility.getOppositeSide(aSide) : aSide, aCoverVariable % 2 == 0 ? GT_Utility.getOppositeSide(aSide) : aSide, null, false, (byte) 64, (byte) 1, (byte) 64, (byte) 1, stacksByOperation), true);
            }
        } else {
            GT_Utility.moveSomeItemStacks(aCoverVariable % 2 == 0 ? aTileEntity : tTileEntity, aCoverVariable % 2 != 0 ? aTileEntity : tTileEntity, aCoverVariable % 2 != 0 ? GT_Utility.getOppositeSide(aSide) : aSide, aCoverVariable % 2 == 0 ? GT_Utility.getOppositeSide(aSide) : aSide, null, false, (byte) 64, (byte) 1, (byte) 64, (byte) 1, stacksByOperation);
        }
        return aCoverVariable;
    }

    public int onCoverScrewdriverclick(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        aCoverVariable = (aCoverVariable + (aPlayer.isSneaking()? -1 : 1)) % 12;
        if(aCoverVariable <0){aCoverVariable = 11;}
        switch(aCoverVariable) {
            case 0: GT_Utility.sendChatToPlayer(aPlayer, trans("006", "Export")); break;
            case 1: GT_Utility.sendChatToPlayer(aPlayer, trans("007", "Import")); break;
            case 2: GT_Utility.sendChatToPlayer(aPlayer, trans("008", "Export (conditional)")); break;
            case 3: GT_Utility.sendChatToPlayer(aPlayer, trans("009", "Import (conditional)")); break;
            case 4: GT_Utility.sendChatToPlayer(aPlayer, trans("010", "Export (invert cond)")); break;
            case 5: GT_Utility.sendChatToPlayer(aPlayer, trans("011", "Import (invert cond)")); break;
            case 6: GT_Utility.sendChatToPlayer(aPlayer, trans("012", "Export allow Input")); break;
            case 7: GT_Utility.sendChatToPlayer(aPlayer, trans("013", "Import allow Output")); break;
            case 8: GT_Utility.sendChatToPlayer(aPlayer, trans("014", "Export allow Input (conditional)")); break;
            case 9: GT_Utility.sendChatToPlayer(aPlayer, trans("015", "Import allow Output (conditional)")); break;
            case 10: GT_Utility.sendChatToPlayer(aPlayer, trans("016", "Export allow Input (invert cond)")); break;
            case 11: GT_Utility.sendChatToPlayer(aPlayer, trans("017", "Import allow Output (invert cond)")); break;
        }
        return aCoverVariable;
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

    public boolean letsFluidIn(byte aSide, int aCoverID, int aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
        return true;
    }

    public boolean letsFluidOut(byte aSide, int aCoverID, int aCoverVariable, Fluid aFluid, ICoverable aTileEntity) {
        return true;
    }

    public boolean letsItemsIn(byte aSide, int aCoverID, int aCoverVariable, int aSlot, ICoverable aTileEntity) {
        return (aCoverVariable >= 6) || (aCoverVariable % 2 != 0);
    }

    public boolean letsItemsOut(byte aSide, int aCoverID, int aCoverVariable, int aSlot, ICoverable aTileEntity) {
        return (aCoverVariable >= 6) || (aCoverVariable % 2 == 0);
    }

    public boolean alwaysLookConnected(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return true;
    }

    public int getTickRate(byte aSide, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return this.mTickRate;
    }
}
