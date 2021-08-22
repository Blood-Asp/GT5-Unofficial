package gregtech.api.graphs.consumers;

import gregtech.api.interfaces.tileentity.IEnergyConnected;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import java.util.ArrayList;

//consumer for gt machines
public class NodeGTBaseMetaTile extends ConsumerNode {
    public NodeGTBaseMetaTile(int aNodeValue, BaseMetaTileEntity aTileEntity, byte aSide, ArrayList<ConsumerNode> aConsumers) {
        super(aNodeValue, aTileEntity, aSide, aConsumers);
    }

    @Override
    public int injectEnergy(int aVoltage, int aMaxAmps) {
        return (int)((IEnergyConnected) mTileEntity).injectEnergyUnits(mSide,aVoltage, aMaxAmps);
    }

    @Override
    public boolean needsEnergy() {
        BaseMetaTileEntity tTileEntity = (BaseMetaTileEntity) mTileEntity;
        return super.needsEnergy() && tTileEntity.getStoredEU() < tTileEntity.getEUCapacity();
    }
}
