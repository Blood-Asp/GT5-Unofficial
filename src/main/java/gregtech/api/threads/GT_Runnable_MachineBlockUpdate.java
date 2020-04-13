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
    private static final Thread INSTANCETHREAD = new Thread(new GT_Runnable_MachineBlockUpdate()); //Instance of this thread

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

    private static void stepToUpdateMachine(World aWorld, int aX, int aY, int aZ, ArrayList<ChunkPosition> aList) {
        aList.add(new ChunkPosition(aX, aY, aZ));
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof IMachineBlockUpdateable)
            ((IMachineBlockUpdateable) tTileEntity).onMachineBlockUpdate();
        if (aList.size() < 5 || (tTileEntity instanceof IMachineBlockUpdateable) || GregTech_API.isMachineBlock(aWorld.getBlock(aX, aY, aZ), aWorld.getBlockMetadata(aX, aY, aZ))) {
            if (!aList.contains(new ChunkPosition(aX + 1, aY, aZ))) stepToUpdateMachine(aWorld, aX + 1, aY, aZ, aList);
            if (!aList.contains(new ChunkPosition(aX - 1, aY, aZ))) stepToUpdateMachine(aWorld, aX - 1, aY, aZ, aList);
            if (!aList.contains(new ChunkPosition(aX, aY + 1, aZ))) stepToUpdateMachine(aWorld, aX, aY + 1, aZ, aList);
            if (!aList.contains(new ChunkPosition(aX, aY - 1, aZ))) stepToUpdateMachine(aWorld, aX, aY - 1, aZ, aList);
            if (!aList.contains(new ChunkPosition(aX, aY, aZ + 1))) stepToUpdateMachine(aWorld, aX, aY, aZ + 1, aList);
            if (!aList.contains(new ChunkPosition(aX, aY, aZ - 1))) stepToUpdateMachine(aWorld, aX, aY, aZ - 1, aList);
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
                    if (coordinates != null)
                        coordinates.update();
                }
            }
        }
    }
}