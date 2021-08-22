package gregtech.api.graphs.consumers;

import ic2.api.energy.tile.IEnergySink;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;

//consumer for IC2 machines
public class NodeEnergySink extends ConsumerNode {
    public NodeEnergySink(int nodeValue, IEnergySink tileEntity, byte side, ArrayList<ConsumerNode> consumers) {
        super(nodeValue, (TileEntity) tileEntity, side, consumers);
    }

    @Override
    public boolean needsEnergy() {
        return super.needsEnergy() && ((IEnergySink) mTileEntity).getDemandedEnergy() > 0;
    }

    @Override
    public int injectEnergy(int aVoltage, int aMaxAmps) {
        int tUsedAmps = 0;
        while (aMaxAmps > tUsedAmps && ((IEnergySink) mTileEntity).getDemandedEnergy() > 0 &&
                ((IEnergySink) mTileEntity).injectEnergy(ForgeDirection.getOrientation(mSide), aVoltage, aVoltage) < aVoltage)
            tUsedAmps++;
        return tUsedAmps;
    }
}
