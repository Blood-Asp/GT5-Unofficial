package gregtech.api.threads;

import gregtech.api.GregTech_API;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.interfaces.tileentity.IMachineBlockUpdateable;
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_Cable;
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_Fluid;
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class GT_Runnable_MachineBlockUpdate implements Runnable {

    private static int mX;
    private static int mY;
    private static int mZ;
    private static World mWorld;
    private static final Set<ChunkPosition> mVisited = new HashSet<>(80);

    //Threading
    private static boolean allowedToRun; //makes if this thread is idle
    private static final Queue<Coordinates> toUpdate = new ConcurrentLinkedQueue<>(); //blocks added while this thread ran
    private static final Thread INSTANCETHREAD = new Thread(new GT_Runnable_MachineBlockUpdate(), "Machine Block Updates"); //Instance of this thread

    //This class should never be initiated outside of this class!
    private GT_Runnable_MachineBlockUpdate() {
    }

    public static synchronized void setmX(int mX) {
        GT_Runnable_MachineBlockUpdate.mX = mX;
    }

    public static synchronized void setmY(int mY) {
        GT_Runnable_MachineBlockUpdate.mY = mY;
    }

    public static synchronized void setmZ(int mZ) {
        GT_Runnable_MachineBlockUpdate.mZ = mZ;
    }

    public static synchronized void setmWorld(World mWorld) {
        GT_Runnable_MachineBlockUpdate.mWorld = mWorld;
    }

    /**
     * Clears the mVisited HashSet
     */
    public static synchronized void resetVisited() {
        synchronized (GT_Runnable_MachineBlockUpdate.mVisited) {
            GT_Runnable_MachineBlockUpdate.mVisited.clear();
        }
    }

    /**
     *  Never call this Method without checking if the thead is NOT allowed to run!
     */
    private static void setMachineUpdateValuesUnsafe(World aWorld, int aX, int aY, int aZ){
        GT_Runnable_MachineBlockUpdate.setmZ(aZ);
        GT_Runnable_MachineBlockUpdate.setmY(aY);
        GT_Runnable_MachineBlockUpdate.setmX(aX);
        GT_Runnable_MachineBlockUpdate.setmWorld(aWorld);
        GT_Runnable_MachineBlockUpdate.resetVisited();
        GT_Runnable_MachineBlockUpdate.setAllowedToRun(true);
        synchronized (toUpdate) {
            if (GT_Runnable_MachineBlockUpdate.INSTANCETHREAD.getState() == Thread.State.WAITING)
                GT_Runnable_MachineBlockUpdate.toUpdate.notify();
        }
    }

    /**
     * If the thread is idleing, sets new values and remove the idle flag, otherwise, queue the cooridinates.
     */
    public static synchronized void setMachineUpdateValues(World aWorld, int aX, int aY, int aZ) {
        if (GT_Runnable_MachineBlockUpdate.isAllowedToRun()) {
            toUpdate.add(new Coordinates(aX, aY, aZ, aWorld));
        } else {
            GT_Runnable_MachineBlockUpdate.setMachineUpdateValuesUnsafe(aWorld, aX, aY, aZ);
        }
    }

    private static class Coordinates {

        private final int mX;
        private final int mY;
        private final int mZ;
        private final World mWorld;

        public Coordinates(int mX, int mY, int mZ, World mWorld) {
            this.mX = mX;
            this.mY = mY;
            this.mZ = mZ;
            this.mWorld = mWorld;
        }

        /**
         * Updated the Main Update Thread while its Idle
         */
        public void update() {
            GT_Runnable_MachineBlockUpdate.setMachineUpdateValues(this.mWorld,this.mX,this.mY,this.mZ);
        }
    }

    public static synchronized boolean isAllowedToRun() {
        return allowedToRun;
    }

    public static synchronized void setAllowedToRun(boolean unlocked) {
        GT_Runnable_MachineBlockUpdate.allowedToRun = unlocked;
    }

    public static Thread getINSTANCETHREAD() {
        return INSTANCETHREAD;
    }

    private boolean shouldRecurse(TileEntity aTileEntity, int aX, int aY, int aZ) {
        if (aTileEntity instanceof IGregTechTileEntity) {
            // Stop recursion on GregTech cables, item pipes, and fluid pipes
            IMetaTileEntity tMetaTileEntity = ((IGregTechTileEntity) aTileEntity).getMetaTileEntity();
            if ((tMetaTileEntity instanceof GT_MetaPipeEntity_Cable) ||
                (tMetaTileEntity instanceof GT_MetaPipeEntity_Fluid) ||
                (tMetaTileEntity instanceof GT_MetaPipeEntity_Item))
                return false;
        }

        return (aTileEntity instanceof IMachineBlockUpdateable) ||
            GregTech_API.isMachineBlock(mWorld.getBlock(aX, aY, aZ), mWorld.getBlockMetadata(aX, aY, aZ));
    }

    private void stepToUpdateMachine(int aX, int aY, int aZ) {
        if (!mVisited.add(new ChunkPosition(aX, aY, aZ)))
            return;

        TileEntity tTileEntity = mWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof IMachineBlockUpdateable)
            ((IMachineBlockUpdateable) tTileEntity).onMachineBlockUpdate();

        if (mVisited.size() < 5 || shouldRecurse(tTileEntity, aX, aY, aZ)) {
            stepToUpdateMachine(aX + 1, aY, aZ);
            stepToUpdateMachine(aX - 1, aY, aZ);
            stepToUpdateMachine(aX, aY + 1, aZ);
            stepToUpdateMachine(aX, aY - 1, aZ);
            stepToUpdateMachine(aX, aY, aZ + 1);
            stepToUpdateMachine(aX, aY, aZ - 1);
        }

    }

    @Override
    public void run() {
        for(;;) { //infinite loop
            if (isAllowedToRun()) {//Are we ready to work?
                try {
                    stepToUpdateMachine(mX, mY, mZ);
                } catch (Throwable e) {/**/}
                setAllowedToRun(false); //Work is finished, wait for new Coords.
            } else {
                //Checkes if the Update Queue has members
                //DO NOT USE OPTIONALS HERE!
                synchronized (toUpdate) {
                    Coordinates coordinates = toUpdate.poll();
                    if (coordinates != null) {
                        coordinates.update();
                    } else {
                        try {
                            toUpdate.wait();
                        } catch (InterruptedException ignored) {
                            return;
                        }
                    }
                }
            }
        }
    }
}