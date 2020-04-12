package gregtech.api.threads;

import gregtech.api.GregTech_API;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.interfaces.tileentity.IMachineBlockUpdateable;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_Cable;
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_Fluid;
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_Item;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import java.util.Set;
import java.util.HashSet;

public class GT_Runnable_MachineBlockUpdate implements Runnable {
    private final int mX, mY, mZ;
    private final World mWorld;
    private final Set<ChunkPosition> mVisited;

    private static final int MAX_UPDATE_DEPTH = 128;

    public GT_Runnable_MachineBlockUpdate(World aWorld, int aX, int aY, int aZ) {
        mWorld = aWorld;
        mX = aX;
        mY = aY;
        mZ = aZ;
        mVisited = new HashSet<ChunkPosition>(80);
    }

    private boolean shouldVisit(int aX, int aY, int aZ) {
        return !mVisited.contains(new ChunkPosition(aX, aY, aZ));
    }

    private boolean shouldUpdate(TileEntity aTileEntity) {
        // Stop recursion on cables and pipes
        if (aTileEntity == null || !(aTileEntity instanceof IGregTechTileEntity))
          return false;
        IMetaTileEntity tMetaTileEntity = ((IGregTechTileEntity) aTileEntity).getMetaTileEntity();
        return
            !(tMetaTileEntity instanceof GT_MetaPipeEntity_Cable) &&
            !(tMetaTileEntity instanceof GT_MetaPipeEntity_Fluid) &&
            !(tMetaTileEntity instanceof GT_MetaPipeEntity_Item);
    }

    private void stepToUpdateMachine(World aWorld, int aX, int aY, int aZ) {
        mVisited.add(new ChunkPosition(aX, aY, aZ));
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (!shouldUpdate(tTileEntity) || mVisited.size() > MAX_UPDATE_DEPTH)
            return;

        if (tTileEntity instanceof IMachineBlockUpdateable)
            ((IMachineBlockUpdateable) tTileEntity).onMachineBlockUpdate();

        if (mVisited.size() < 5 ||
            (tTileEntity instanceof IMachineBlockUpdateable) ||
            GregTech_API.isMachineBlock(aWorld.getBlock(aX, aY, aZ), aWorld.getBlockMetadata(aX, aY, aZ))) {
            if (shouldVisit(aX + 1, aY, aZ)) stepToUpdateMachine(aWorld, aX + 1, aY, aZ);
            if (shouldVisit(aX - 1, aY, aZ)) stepToUpdateMachine(aWorld, aX - 1, aY, aZ);
            if (shouldVisit(aX, aY + 1, aZ)) stepToUpdateMachine(aWorld, aX, aY + 1, aZ);
            if (shouldVisit(aX, aY - 1, aZ)) stepToUpdateMachine(aWorld, aX, aY - 1, aZ);
            if (shouldVisit(aX, aY, aZ + 1)) stepToUpdateMachine(aWorld, aX, aY, aZ + 1);
            if (shouldVisit(aX, aY, aZ - 1)) stepToUpdateMachine(aWorld, aX, aY, aZ - 1);
        }
    }

    @Override
    public void run() {
        try {
            stepToUpdateMachine(mWorld, mX, mY, mZ);
        } catch (Throwable e) {/**/}
    }
}
