package gregtech.api.threads;

import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.interfaces.tileentity.IMachineBlockUpdateable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class GT_Runnable_MachineBlockUpdate implements Runnable {
    //used by runner thread
    private final int x, y, z;
    private final World world;
    private final Set<ChunkPosition> visited = new HashSet<>(80);

    //Threading
    private static final ThreadFactory THREAD_FACTORY = r -> {
        Thread thread = new Thread(r);
        thread.setName("GT_MachineBlockUpdate");
        return thread;
    };
    private static ExecutorService EXECUTOR_SERVICE;

    //This class should never be initiated outside of this class!
    private GT_Runnable_MachineBlockUpdate(World aWorld, int aX, int aY, int aZ) {
        this.world = aWorld;
        this.x = aX;
        this.y = aY;
        this.z = aZ;
    }

    public static boolean isEnabled() {
        return isEnabled;
    }

    public static void setEnabled() {
        GT_Runnable_MachineBlockUpdate.isEnabled = true;
    }

    public static void setDisabled() {
        GT_Runnable_MachineBlockUpdate.isEnabled = false;
    }

    public static void setEnabled(boolean isEnabled) {
        GT_Runnable_MachineBlockUpdate.isEnabled = isEnabled;
    }

    private static boolean isEnabled = true;

    public static void setMachineUpdateValues(World aWorld, int aX, int aY, int aZ) {
        if (isEnabled)
            EXECUTOR_SERVICE.submit(new GT_Runnable_MachineBlockUpdate(aWorld, aX, aY, aZ));
    }

    public static void initExecutorService() {
        EXECUTOR_SERVICE = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), THREAD_FACTORY);
        //Executors.newSingleThreadExecutor(THREAD_FACTORY);
        //Executors.newCachedThreadPool(THREAD_FACTORY);
        //Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),THREAD_FACTORY);
    }

    public static void shutdownExecutorService() {
        try {
            GT_Mod.GT_FML_LOGGER.info("Shutting down Machine block update executor service");
            EXECUTOR_SERVICE.shutdown(); // Disable new tasks from being submitted
            // Wait a while for existing tasks to terminate
            if (!EXECUTOR_SERVICE.awaitTermination(60, TimeUnit.SECONDS)) {
                EXECUTOR_SERVICE.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!EXECUTOR_SERVICE.awaitTermination(60, TimeUnit.SECONDS)) {
                    GT_Mod.GT_FML_LOGGER.error("Well this didn't terminated well... GT_Runnable_MachineBlockUpdate.shutdownExecutorService");
                }
            }
        } catch (InterruptedException ie) {
            GT_Mod.GT_FML_LOGGER.error("Well this interruption got interrupted...", ie);
            // (Re-)Cancel if current thread also interrupted
            EXECUTOR_SERVICE.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }catch (Exception e){
            GT_Mod.GT_FML_LOGGER.error("Well this didn't terminated well...",e);
            // (Re-)Cancel in case
            EXECUTOR_SERVICE.shutdownNow();
        }finally {
            GT_Mod.GT_FML_LOGGER.info("Leaving... GT_Runnable_MachineBlockUpdate.shutdownExecutorService");
        }
    }

    private boolean shouldRecurse(TileEntity aTileEntity, int aX, int aY, int aZ) {
        //no check on IGregTechTileEntity as it should call the underlying meta tile isMachineBlockUpdateRecursive
        //if (aTileEntity instanceof IGregTechTileEntity) {
        //    return ((IGregTechTileEntity) aTileEntity).isMachineBlockUpdateRecursive();
        //}
        return (aTileEntity instanceof IMachineBlockUpdateable &&
                ((IMachineBlockUpdateable) aTileEntity).isMachineBlockUpdateRecursive()) ||
                GregTech_API.isMachineBlock(world.getBlock(aX, aY, aZ), world.getBlockMetadata(aX, aY, aZ));
    }

    private void causeUpdate(TileEntity tileEntity) {
        //no check for IGregTechTileEntity as it should call the underlying meta tile onMachineBlockUpdate
        if (tileEntity instanceof IMachineBlockUpdateable) {
            ((IMachineBlockUpdateable) tileEntity).onMachineBlockUpdate();
        }
    }

    private void stepToUpdateMachine(int aX, int aY, int aZ) {
        if (!visited.add(new ChunkPosition(aX, aY, aZ)))
            return;
        TileEntity tTileEntity = world.getTileEntity(aX, aY, aZ);

        causeUpdate(tTileEntity);

        if (visited.size() < 5 || shouldRecurse(tTileEntity, aX, aY, aZ)) {
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
        try {
            stepToUpdateMachine(x, y, z);
        } catch (Exception e) {
            GT_Mod.GT_FML_LOGGER.error("Well this update was broken... " + new Coordinates(x, y, z, world), e);
        }
    }

    public static class Coordinates {
        public final int mX;
        public final int mY;
        public final int mZ;
        public final World mWorld;

        public Coordinates(int mX, int mY, int mZ, World mWorld) {
            this.mX = mX;
            this.mY = mY;
            this.mZ = mZ;
            this.mWorld = mWorld;
        }

        @Override
        public String toString() {
            return "Coordinates{" +
                    "mX=" + mX +
                    ", mY=" + mY +
                    ", mZ=" + mZ +
                    ", mWorld=" + mWorld.getProviderName() + " @dimId " + mWorld.provider.dimensionId +
                    '}';
        }
    }
}
