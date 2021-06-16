package gregtech.api.graphs.consumers;

import gregtech.api.interfaces.tileentity.IEnergyConnected;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import java.util.ArrayList;

//consumer for gt machines
public class NodeGTBaseMetaTile extends ConsumerNode {
    public NodeGTBaseMetaTile(int aNodeValue, BaseMetaTileEntity aTileEntity, int aSide, ArrayList<ConsumerNode> aConsumers) {
        super(aNodeValue, aTileEntity, aSide, aConsumers);
    }

    @Override
    public int injectEnergy(int aVoltage, int aMaxApms) {
        return (int)((IEnergyConnected) mTileEntity).injectEnergyUnits((byte) mSide,aVoltage, aMaxApms);
    }

    @Override
    public boolean needsEnergy() {
        BaseMetaTileEntity tTileEntity = (BaseMetaTileEntity) mTileEntity;
        return super.needsEnergy() && tTileEntity.getStoredEU() < tTileEntity.getEUCapacity();
    }
}
