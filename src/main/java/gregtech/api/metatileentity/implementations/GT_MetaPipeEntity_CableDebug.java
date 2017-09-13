package gregtech.api.metatileentity.implementations;

import cofh.api.energy.IEnergyReceiver;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.metatileentity.IMetaTileEntityCable;
import gregtech.api.interfaces.tileentity.IColoredTileEntity;
import gregtech.api.interfaces.tileentity.IEnergyConnected;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GT_Utility;
import gregtech.common.GT_Client;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Arrays;

public class GT_MetaPipeEntity_CableDebug extends GT_MetaPipeEntity_Cable implements IMetaTileEntityCable {
    EntityPlayer player;

    public GT_MetaPipeEntity_CableDebug(int aID, String aName, String aNameRegional, float aThickNess, Materials aMaterial, long aCableLossPerMeter, long aAmperage, long aVoltage, boolean aInsulated, boolean aCanShock) {
        super(aID, aName, aNameRegional, aThickNess, aMaterial, aCableLossPerMeter, aAmperage, aVoltage, aInsulated, aCanShock);
    }

    public GT_MetaPipeEntity_CableDebug(String aName, float aThickNess, Materials aMaterial, long aCableLossPerMeter, long aAmperage, long aVoltage, boolean aInsulated, boolean aCanShock) {
        super(aName, aThickNess, aMaterial, aCableLossPerMeter, aAmperage, aVoltage, aInsulated, aCanShock);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaPipeEntity_CableDebug(mName, mThickNess, mMaterial, mCableLossPerMeter, mAmperage, mVoltage, mInsulated, mCanShock);
    }

    @Override
    public long injectEnergyUnits(byte aSide, long aVoltage, long aAmperage) {
        if (!getBaseMetaTileEntity().getCoverBehaviorAtSide(aSide).letsEnergyIn(aSide, getBaseMetaTileEntity().getCoverIDAtSide(aSide), getBaseMetaTileEntity().getCoverDataAtSide(aSide), getBaseMetaTileEntity()))
            return 0;
        sendToPlayer("IEU "+aSide+" "+aVoltage+" "+aAmperage);
        return transferElectricity(aSide, aVoltage, aAmperage, new ArrayList<TileEntity>(Arrays.asList((TileEntity) getBaseMetaTileEntity())));
    }

    @Override
    public long transferElectricity(byte aSide, long aVoltage, long aAmperage, ArrayList<TileEntity> aAlreadyPassedTileEntityList) {

        sendToPlayer("TEl "+getBaseMetaTileEntity().getMetaTileID()+" "+ getBaseMetaTileEntity().getXCoord()+" "+ getBaseMetaTileEntity().getYCoord()+" "+ getBaseMetaTileEntity().getZCoord()+" ");
        for(TileEntity te:aAlreadyPassedTileEntityList)
            sendToPlayer(te.blockMetadata+" "+te.xCoord+" "+te.yCoord+" "+te.zCoord);

        long rUsedAmperes = 0;
        aVoltage -= mCableLossPerMeter;
        if (aVoltage > 0) for (byte i = 0; i < 6 && aAmperage > rUsedAmperes; i++)
            if (i != aSide && (mConnections & (1 << i)) != 0 && getBaseMetaTileEntity().getCoverBehaviorAtSide(i).letsEnergyOut(i, getBaseMetaTileEntity().getCoverIDAtSide(i), getBaseMetaTileEntity().getCoverDataAtSide(i), getBaseMetaTileEntity())) {
                TileEntity tTileEntity = getBaseMetaTileEntity().getTileEntityAtSide(i);
                sendToPlayer("TEn "+tTileEntity.blockMetadata+" "+tTileEntity.xCoord+" "+tTileEntity.yCoord+" "+tTileEntity.zCoord);
                if (!aAlreadyPassedTileEntityList.contains(tTileEntity)) {
                    aAlreadyPassedTileEntityList.add(tTileEntity);
                    if (tTileEntity instanceof IEnergyConnected) {
                        if (getBaseMetaTileEntity().getColorization() >= 0) {
                            byte tColor = ((IEnergyConnected) tTileEntity).getColorization();
                            if (tColor >= 0 && tColor != getBaseMetaTileEntity().getColorization()) continue;
                        }
                        if (tTileEntity instanceof IGregTechTileEntity && ((IGregTechTileEntity) tTileEntity).getMetaTileEntity() instanceof IMetaTileEntityCable && ((IGregTechTileEntity) tTileEntity).getCoverBehaviorAtSide(GT_Utility.getOppositeSide(i)).letsEnergyIn(GT_Utility.getOppositeSide(i), ((IGregTechTileEntity) tTileEntity).getCoverIDAtSide(GT_Utility.getOppositeSide(i)), ((IGregTechTileEntity) tTileEntity).getCoverDataAtSide(GT_Utility.getOppositeSide(i)), ((IGregTechTileEntity) tTileEntity))) {
                            if (((IGregTechTileEntity) tTileEntity).getTimer() > 50) {
                                sendToPlayer("TEc "+tTileEntity.blockMetadata+" "+tTileEntity.xCoord+" "+tTileEntity.yCoord+" "+tTileEntity.zCoord);
                                rUsedAmperes += ((IMetaTileEntityCable) ((IGregTechTileEntity) tTileEntity).getMetaTileEntity()).transferElectricity(GT_Utility.getOppositeSide(i), aVoltage, aAmperage - rUsedAmperes, aAlreadyPassedTileEntityList);
                            }
                        } else {
                            sendToPlayer("TEt "+tTileEntity.blockMetadata+" "+tTileEntity.xCoord+" "+tTileEntity.yCoord+" "+tTileEntity.zCoord);
                            rUsedAmperes += ((IEnergyConnected) tTileEntity).injectEnergyUnits(GT_Utility.getOppositeSide(i), aVoltage, aAmperage - rUsedAmperes);
                        }
//        		} else if (tTileEntity instanceof IEnergySink) {
//            		ForgeDirection tDirection = ForgeDirection.getOrientation(i).getOpposite();
//            		if (((IEnergySink)tTileEntity).acceptsEnergyFrom((TileEntity)getBaseMetaTileEntity(), tDirection)) {
//            			if (((IEnergySink)tTileEntity).demandedEnergyUnits() > 0 && ((IEnergySink)tTileEntity).injectEnergyUnits(tDirection, aVoltage) < aVoltage) rUsedAmperes++;
//            		}
                    } else if (tTileEntity instanceof IEnergySink) {
                        sendToPlayer("TEs "+tTileEntity.blockMetadata+" "+tTileEntity.xCoord+" "+tTileEntity.yCoord+" "+tTileEntity.zCoord);
                        ForgeDirection tDirection = ForgeDirection.getOrientation(i).getOpposite();
                        if (((IEnergySink) tTileEntity).acceptsEnergyFrom((TileEntity) getBaseMetaTileEntity(), tDirection)) {
                            if (((IEnergySink) tTileEntity).getDemandedEnergy() > 0 && ((IEnergySink) tTileEntity).injectEnergy(tDirection, aVoltage, aVoltage) < aVoltage)
                                rUsedAmperes++;
                        }
                    } else if (GregTech_API.mOutputRF && tTileEntity instanceof IEnergyReceiver) {
                        ForgeDirection tDirection = ForgeDirection.getOrientation(i).getOpposite();
                        long rfOUT = aVoltage * GregTech_API.mEUtoRF / 100;
                        int  rfOut = rfOUT>Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)rfOUT;
                        if (((IEnergyReceiver) tTileEntity).receiveEnergy(tDirection, rfOut, true) == rfOut) {
                            ((IEnergyReceiver) tTileEntity).receiveEnergy(tDirection, rfOut, false);
                            rUsedAmperes++;
                        } else if (((IEnergyReceiver) tTileEntity).receiveEnergy(tDirection, rfOut, true) > 0) {
                            if (mRestRF == 0) {
                                int RFtrans = ((IEnergyReceiver) tTileEntity).receiveEnergy(tDirection, (int) rfOut, false);
                                rUsedAmperes++;
                                mRestRF = rfOut - RFtrans;
                            } else {
                                int RFtrans = ((IEnergyReceiver) tTileEntity).receiveEnergy(tDirection, (int) mRestRF, false);
                                mRestRF = mRestRF - RFtrans;
                            }
                        }
                        if (GregTech_API.mRFExplosions && ((IEnergyReceiver) tTileEntity).getMaxEnergyStored(tDirection) < rfOut * 600) {
                            if (rfOut > 32 * GregTech_API.mEUtoRF / 100) this.doExplosion(rfOut);
                        }
                    }
                }
                sendToPlayer("TEf "+rUsedAmperes);
            }
        mTransferredVoltage=(Math.max(mTransferredVoltage,aVoltage));
        mTransferredAmperage += rUsedAmperes;
        mTransferredVoltageLast20 = (Math.max(mTransferredVoltageLast20, aVoltage));
        mTransferredAmperageLast20 = Math.max(mTransferredAmperageLast20, mTransferredAmperage);
        if (aVoltage > mVoltage || mTransferredAmperage > mAmperage) {
            if(mOverheat>mMaxOverheat)
                getBaseMetaTileEntity().setToFire();
            else
                mOverheat +=100;
            return aAmperage;
        }
        return rUsedAmperes;
        //Always return amount of used amperes, used all on overheat
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (aBaseMetaTileEntity.isServerSide()) {
            mTransferredVoltageOK=mTransferredVoltage;
            mTransferredVoltage=0;
            mTransferredAmperageOK=mTransferredAmperage;
            mTransferredAmperage = 0;

            sendToPlayer("OPT" + aTick + " "+ aBaseMetaTileEntity.getWorld().getTotalWorldTime());

            if(mOverheat>0)mOverheat--;
            if (aTick % 20 == 0) {
                mTransferredVoltageLast20OK=mTransferredVoltageLast20;
                mTransferredVoltageLast20 = 0;
                mTransferredAmperageLast20OK=mTransferredAmperageLast20;
                mTransferredAmperageLast20 = 0;
                mConnections = 0;
                for (byte i = 0, j = 0; i < 6; i++) {
                    j = GT_Utility.getOppositeSide(i);
                    if (aBaseMetaTileEntity.getCoverBehaviorAtSide(i).alwaysLookConnected(i, aBaseMetaTileEntity.getCoverIDAtSide(i), aBaseMetaTileEntity.getCoverDataAtSide(i), aBaseMetaTileEntity) || aBaseMetaTileEntity.getCoverBehaviorAtSide(i).letsEnergyIn(i, aBaseMetaTileEntity.getCoverIDAtSide(i), aBaseMetaTileEntity.getCoverDataAtSide(i), aBaseMetaTileEntity) || aBaseMetaTileEntity.getCoverBehaviorAtSide(i).letsEnergyOut(i, aBaseMetaTileEntity.getCoverIDAtSide(i), aBaseMetaTileEntity.getCoverDataAtSide(i), aBaseMetaTileEntity)) {
                        TileEntity tTileEntity = aBaseMetaTileEntity.getTileEntityAtSide(i);
                        if (tTileEntity instanceof IColoredTileEntity) {
                            if (aBaseMetaTileEntity.getColorization() >= 0) {
                                byte tColor = ((IColoredTileEntity) tTileEntity).getColorization();
                                if (tColor >= 0 && tColor != aBaseMetaTileEntity.getColorization()) continue;
                            }
                        }
                        if (tTileEntity instanceof IEnergyConnected && (((IEnergyConnected) tTileEntity).inputEnergyFrom(j) || ((IEnergyConnected) tTileEntity).outputsEnergyTo(j))) {
                            mConnections |= (1 << i);
                            continue;
                        }
                        if (tTileEntity instanceof IGregTechTileEntity && ((IGregTechTileEntity) tTileEntity).getMetaTileEntity() instanceof IMetaTileEntityCable) {
                            if (((IGregTechTileEntity) tTileEntity).getCoverBehaviorAtSide(j).alwaysLookConnected(j, ((IGregTechTileEntity) tTileEntity).getCoverIDAtSide(j), ((IGregTechTileEntity) tTileEntity).getCoverDataAtSide(j), ((IGregTechTileEntity) tTileEntity)) || ((IGregTechTileEntity) tTileEntity).getCoverBehaviorAtSide(j).letsEnergyIn(j, ((IGregTechTileEntity) tTileEntity).getCoverIDAtSide(j), ((IGregTechTileEntity) tTileEntity).getCoverDataAtSide(j), ((IGregTechTileEntity) tTileEntity)) || ((IGregTechTileEntity) tTileEntity).getCoverBehaviorAtSide(j).letsEnergyOut(j, ((IGregTechTileEntity) tTileEntity).getCoverIDAtSide(j), ((IGregTechTileEntity) tTileEntity).getCoverDataAtSide(j), ((IGregTechTileEntity) tTileEntity))) {
                                mConnections |= (1 << i);
                                continue;
                            }
                        }
                        if (tTileEntity instanceof IEnergySink && ((IEnergySink) tTileEntity).acceptsEnergyFrom((TileEntity) aBaseMetaTileEntity, ForgeDirection.getOrientation(j))) {
                            mConnections |= (1 << i);
                            continue;
                        }
                        if (GregTech_API.mOutputRF && tTileEntity instanceof IEnergyReceiver && ((IEnergyReceiver) tTileEntity).canConnectEnergy(ForgeDirection.getOrientation(j))) {
                            mConnections |= (1 << i);
                            continue;
                        }
                        /*
					    if (tTileEntity instanceof IEnergyEmitter && ((IEnergyEmitter)tTileEntity).emitsEnergyTo((TileEntity)aBaseMetaTileEntity, ForgeDirection.getOrientation(j))) {
				    		mConnections |= (1<<i);
				    		continue;
					    }*/
                    }
                }
            }
        }else if(aBaseMetaTileEntity.isClientSide() && GT_Client.changeDetected==4) aBaseMetaTileEntity.issueTextureUpdate();
    }

    @Override
    public String[] getInfoData() {
        return new String[]{
                //EnumChatFormatting.BLUE + mName + EnumChatFormatting.RESET,
                "Heat: "+
                        EnumChatFormatting.RED+ mOverheat +EnumChatFormatting.RESET+" / "+EnumChatFormatting.YELLOW+ mMaxOverheat + EnumChatFormatting.RESET,
                "Max Load (1t):",
                EnumChatFormatting.GREEN + Integer.toString(mTransferredAmperageOK) + EnumChatFormatting.RESET +" A / "+
                        EnumChatFormatting.YELLOW + Long.toString(mAmperage) + EnumChatFormatting.RESET +" A",
                "Max EU/p (1t):",
                EnumChatFormatting.GREEN + Long.toString(mTransferredVoltageOK) + EnumChatFormatting.RESET +" EU / "+
                        EnumChatFormatting.YELLOW + Long.toString(mVoltage) + EnumChatFormatting.RESET +" EU",
                "Max Load (20t): "+
                    EnumChatFormatting.GREEN + Integer.toString(mTransferredAmperageLast20OK) + EnumChatFormatting.RESET +" A",
                "Max EU/p (20t): "+
                    EnumChatFormatting.GREEN + Long.toString(mTransferredVoltageLast20OK) + EnumChatFormatting.RESET +" EU"
        };
    }

    @Override
    public void onScrewdriverRightClick(byte aSide, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if(getBaseMetaTileEntity().isServerSide()) {
            if(player==null) player=aPlayer;
            else player=null;
        }
    }

    private void sendToPlayer(String thing){
        if(player!=null) {
            player.addChatComponentMessage(new ChatComponentText(thing));
            System.out.println(thing);
        }
    }
}
