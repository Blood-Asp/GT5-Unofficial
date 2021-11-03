package gregtech.api.interfaces;

import gregtech.api.items.GT_MetaGenerated_Tool;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IAOETool {

    int getMaxAOESize();

    void onRightClick(ItemStack stack, EntityPlayer player);

    float getDamageMultiplyer();

    float getSpeedReduction();

    float getDigSpeed(float digSpeed, IToolStats stats, ItemStack stack);

    int onBlockDestroyed(ItemStack stack,IToolStats stats ,World world, Block block, int x, int y, int z, EntityLivingBase player);

    default void setAOE(NBTTagCompound statNbt, int value) {
        statNbt.setInteger("AOE", value);
    }

    default int getAOE(NBTTagCompound statNbt) {
        return statNbt.getInteger("AOE");
    }

    default int breakBlockAround(int side,int xStartPos ,int yStartPos,int xLength, int yLenghth, IToolStats stats, ItemStack stack, World world, int x, int y, int z,
                                       EntityPlayerMP player) {
        int harvsestleve = stack.getItem().getHarvestLevel(stack,"");

        int DX;
        int DY;
        int DZ;
        boolean downUp = side < 2;
        boolean northSouth = side < 4;
        float rotation = player.rotationYawHead < 0 ? 360f + player.rotationYawHead : player.rotationYawHead;

        BiFunction<Integer,Integer,Boolean> xCheck = (value,len) -> value<len;
        BiFunction<Integer,Integer,Boolean> yCheck = (value,len) -> value>len;

        int xIterate = 1;
        int yIterate = -1;

        if (downUp ) {
            boolean north = rotation > 135 && rotation < 225;// 180
            //boolean east = rotation >= 225 && rotation <= 315;//270
            boolean west = rotation >= 45 && rotation <= 135;// 90
            boolean south = rotation < 45 || rotation > 315;//0

            //x = easth-west
            //y = north-south

            if (south) {
                yLenghth *= -1;
                yLenghth -= yStartPos;
                yStartPos *= -1;
                if (side == 0) {
                    xLength += xStartPos;
                } else {
                    xCheck = (value,len) -> value>len;
                    xLength *= -1;
                    xLength -= xStartPos;
                    xIterate = -1;
                    xStartPos *=-1;
                }
            } else if (west) {
                xCheck = (value,len) -> value>len;
                xLength *= -1;
                xLength -= xStartPos;
                xIterate = -1;
                xStartPos *=-1;

                if (side == 0) {
                    yLenghth *= -1;
                    yLenghth -= yStartPos;
                    yStartPos *= -1;
                } else {
                    yCheck = (value,len) -> value<len;
                    yIterate = 1;
                    yLenghth += yStartPos;
                }
            } else if (north) {
                yCheck = (value,len) -> value<len;
                yIterate = 1;
                yLenghth += yStartPos;

                if (side == 0) {
                    xCheck = (value,len) -> value>len;
                    xLength *= -1;
                    xLength -= xStartPos;
                    xIterate = -1;
                    xStartPos *=-1;
                } else {
                    xLength += xStartPos;
                }
            } else {
                xLength += xStartPos;

                if (side == 0) {
                    yCheck = (value,len) -> value<len;
                    yIterate = 1;
                    yLenghth += yStartPos;
                } else {
                    yLenghth *= -1;
                    yLenghth -= yStartPos;
                    yStartPos *= -1;
                }
            }
        } else {
            yLenghth *= -1;
            yLenghth -= yStartPos;
            yStartPos *= -1;
            if (northSouth) {
                if (side ==2) {
                    xLength *= -1;
                    xLength -= xStartPos;
                    xIterate = -1;
                    xStartPos *=-1;
                    xCheck = (value,len) -> value>len;
                } else {
                    xLength += xStartPos;
                }
            } else {
                if (side == 5) {
                    xLength *= -1;
                    xLength -= xStartPos;
                    xIterate = -1;
                    xStartPos *=-1;
                    xCheck = (value,len) -> value>len;
                } else {
                    xLength += xStartPos;
                }
            }
        }
        int broken = 0;
        for (int Y = yStartPos; yCheck.apply(Y,yLenghth); Y+=yIterate) {
            for (int X = xStartPos;xCheck.apply(X,xLength); X+=xIterate) {
                if (Y != 0 || X != 0) {
                    if (downUp) {
                        DX = x + Y;
                        DY = y;
                        DZ = z + X;
                    } else if (northSouth) {
                        DX = x + X;
                        DY = y + Y;
                        DZ = z;
                    } else {
                        DX = x;
                        DY = y + Y;
                        DZ = z + X;
                    }
                    if (breakBlock(stack, stats, harvsestleve, world, DX, DY, DZ, player))
                        broken++;
                }
            }
        }
        return broken;
    }

    default boolean breakBlock(ItemStack aStack, IToolStats stats, int harvestLevel, World world, int x, int y, int z, EntityPlayerMP player) {
        // most of this code is stolen from TiC
        if (world.isAirBlock(x, y, z))
            return false;
        Block block = world.getBlock(x, y, z);
        int blockMeta = world.getBlockMetadata(x, y, z);
        if (!canHarvest(harvestLevel, block, blockMeta, stats)) return false;
        BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(world, player.theItemInWorldManager.getGameType(), player, x, y, z);
        if (event.isCanceled())
            return false;
        if (!world.isRemote) {
            block.onBlockHarvested(world, x, y, z, blockMeta, player);
            if (block.removedByPlayer(world, player, x, y, z, true)) {
                block.onBlockDestroyedByPlayer(world, x, y, z, blockMeta);
                block.harvestBlock(world, player, x, y, z, blockMeta);
                block.dropXpOnBlockBreak(world, x, y, z, event.getExpToDrop());
            }
            ((GT_MetaGenerated_Tool) aStack.getItem()).doDamage(aStack, (int) Math.max(1, block.getBlockHardness(world, x, y, z) * stats.getToolDamagePerBlockBreak()));
            player.playerNetServerHandler.sendPacket(new S23PacketBlockChange(x, y, z, world));
        }
        return true;
    }

    //a fast version of get digSpeed
    //and block stats dont matter for the get digSpeed mult or even matter for this function
    static boolean canHarvest(int toolHarvestLevel, Block block, int meta, IToolStats stats) {
        int blockHarvestLevel = block.getHarvestLevel(meta);
        if (toolHarvestLevel < blockHarvestLevel || blockHarvestLevel < 0) return false;
        boolean isMineble = stats.isMinableBlock(block, (byte) meta);
        if (!isMineble && blockHarvestLevel != 0) {
            return false;
        }
        return true;
    }
}
