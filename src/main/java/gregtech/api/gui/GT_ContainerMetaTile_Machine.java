package gregtech.api.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GT_LanguageManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;

import java.util.Iterator;

/**
 * NEVER INCLUDE THIS FILE IN YOUR MOD!!!
 * <p/>
 * The Container I use for all my MetaTileEntities
 */
public class GT_ContainerMetaTile_Machine extends GT_Container {

    public int mActive = 0, mMaxProgressTime = 0, mProgressTime = 0, mEnergy = 0, mSteam = 0, mSteamStorage = 0, mStorage = 0, mOutput = 0, mInput = 0, mID = 0, mDisplayErrorCode = 0;
    private int oActive = 0, oMaxProgressTime = 0, oProgressTime = 0, oEnergy = 0, oSteam = 0, oSteamStorage = 0, oStorage = 0, oOutput = 0, oInput = 0, oID = 0, oDisplayErrorCode = 0, mTimer = 0;


    public GT_ContainerMetaTile_Machine(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity) {
        super(aInventoryPlayer, aTileEntity);

        mTileEntity = aTileEntity;

        if (mTileEntity != null && mTileEntity.getMetaTileEntity() != null) {
            addSlots(aInventoryPlayer);
            if (doesBindPlayerInventory()) bindPlayerInventory(aInventoryPlayer);
            detectAndSendChanges();
        } else {
            aInventoryPlayer.player.openContainer = aInventoryPlayer.player.inventoryContainer;
        }
    }
    public GT_ContainerMetaTile_Machine(InventoryPlayer aInventoryPlayer, IGregTechTileEntity aTileEntity, boolean doesBindInventory) {
        super(aInventoryPlayer, aTileEntity);
        mTileEntity = aTileEntity;

        if (mTileEntity != null && mTileEntity.getMetaTileEntity() != null) {
            addSlots(aInventoryPlayer);
            if (doesBindPlayerInventory() && doesBindInventory) bindPlayerInventory(aInventoryPlayer);
            detectAndSendChanges();
        } else {
            aInventoryPlayer.player.openContainer = aInventoryPlayer.player.inventoryContainer;
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if (mTileEntity.isClientSide() || mTileEntity.getMetaTileEntity() == null) return;
        mStorage = (int) Math.min(Integer.MAX_VALUE, mTileEntity.getEUCapacity());
        mEnergy = (int) Math.min(Integer.MAX_VALUE, mTileEntity.getStoredEU());
        mSteamStorage = (int) Math.min(Integer.MAX_VALUE, mTileEntity.getSteamCapacity());
        mSteam = (int) Math.min(Integer.MAX_VALUE, mTileEntity.getStoredSteam());
        mOutput = (int) Math.min(Integer.MAX_VALUE, mTileEntity.getOutputVoltage());
        mInput = (int) Math.min(Integer.MAX_VALUE, mTileEntity.getInputVoltage());
        mDisplayErrorCode = mTileEntity.getErrorDisplayID();
        mProgressTime = mTileEntity.getProgress();
        mMaxProgressTime = mTileEntity.getMaxProgress();
        mActive = mTileEntity.isActive() ? 1 : 0;
        mTimer++;

        Iterator var2 = this.crafters.iterator();
        while (var2.hasNext()) {
            ICrafting var1 = (ICrafting) var2.next();
            if (mTimer % 500 == 10 || oEnergy != mEnergy) {
                var1.sendProgressBarUpdate(this, 0, mEnergy & 65535);
                var1.sendProgressBarUpdate(this, 1, mEnergy >>> 16);
            }
            if (mTimer % 500 == 10 || oStorage != mStorage) {
                var1.sendProgressBarUpdate(this, 2, mStorage & 65535);
                var1.sendProgressBarUpdate(this, 3, mStorage >>> 16);
            }
            if (mTimer % 500 == 10 || oOutput != mOutput) {
                var1.sendProgressBarUpdate(this, 4, mOutput);
            }
            if (mTimer % 500 == 10 || oInput != mInput) {
                var1.sendProgressBarUpdate(this, 5, mInput);
            }
            if (mTimer % 500 == 10 || oDisplayErrorCode != mDisplayErrorCode) {
                var1.sendProgressBarUpdate(this, 6, mDisplayErrorCode);
            }
            if (mTimer % 500 == 10 || oProgressTime != mProgressTime) {
                var1.sendProgressBarUpdate(this, 11, mProgressTime & 65535);
                var1.sendProgressBarUpdate(this, 12, mProgressTime >>> 16);
            }
            if (mTimer % 500 == 10 || oMaxProgressTime != mMaxProgressTime) {
                var1.sendProgressBarUpdate(this, 13, mMaxProgressTime & 65535);
                var1.sendProgressBarUpdate(this, 14, mMaxProgressTime >>> 16);
            }
            if (mTimer % 500 == 10 || oID != mID) {
                var1.sendProgressBarUpdate(this, 15, mID);
            }
            if (mTimer % 500 == 10 || oActive != mActive) {
                var1.sendProgressBarUpdate(this, 16, mActive);
            }
            if (mTimer % 500 == 10 || oSteam != mSteam) {
                var1.sendProgressBarUpdate(this, 17, mSteam & 65535);
                var1.sendProgressBarUpdate(this, 18, mSteam >>> 16);
            }
            if (mTimer % 500 == 10 || oSteamStorage != mSteamStorage) {
                var1.sendProgressBarUpdate(this, 19, mSteamStorage & 65535);
                var1.sendProgressBarUpdate(this, 20, mSteamStorage >>> 16);
            }
        }

        oID = mID;
        oSteam = mSteam;
        oInput = mInput;
        oActive = mActive;
        oOutput = mOutput;
        oEnergy = mEnergy;
        oStorage = mStorage;
        oSteamStorage = mSteamStorage;
        oProgressTime = mProgressTime;
        oMaxProgressTime = mMaxProgressTime;
        oDisplayErrorCode = mDisplayErrorCode;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int par1, int par2) {
        super.updateProgressBar(par1, par2);
        switch (par1) {
            case 0:
                mEnergy = mEnergy & -65536 | par2;
                break;
            case 1:
                mEnergy = mEnergy & 65535 | par2 << 16;
                break;
            case 2:
                mStorage = mStorage & -65536 | par2;
                break;
            case 3:
                mStorage = mStorage & 65535 | par2 << 16;
                break;
            case 4:
                mOutput = par2;
                break;
            case 5:
                mInput = par2;
                break;
            case 6:
                mDisplayErrorCode = par2;
                break;
            case 11:
                mProgressTime = mProgressTime & -65536 | par2;
                break;
            case 12:
                mProgressTime = mProgressTime & 65535 | par2 << 16;
                break;
            case 13:
                mMaxProgressTime = mMaxProgressTime & -65536 | par2;
                break;
            case 14:
                mMaxProgressTime = mMaxProgressTime & 65535 | par2 << 16;
                break;
            case 15:
                mID = par2;
                break;
            case 16:
                mActive = par2;
                break;
            case 17:
                mSteam = mSteam & -65536 | par2;
                break;
            case 18:
                mSteam = mSteam & 65535 | par2 << 16;
                break;
            case 19:
                mSteamStorage = mSteamStorage & -65536 | par2;
                break;
            case 20:
                mSteamStorage = mSteamStorage & 65535 | par2 << 16;
                break;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return mTileEntity.isUseableByPlayer(player);
    }
    
    public String trans(String aKey, String aEnglish){
    	return GT_LanguageManager.addStringLocalization("Interaction_DESCRIPTION_Index_"+aKey, aEnglish, false);
    }
}