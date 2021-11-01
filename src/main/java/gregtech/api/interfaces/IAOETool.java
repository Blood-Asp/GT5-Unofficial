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

public interface IAOETool {

    int getMaxAOESize();

    void onRightClick(ItemStack stack, EntityPlayer player);

    int getDamageMultiplyer();

    float getSpeedReduction();

    float getDigSpeed(float digSpeed, IToolStats stats, ItemStack stack);

    int onBlockDestroyed(ItemStack stack,IToolStats stats ,World world, Block block, int x, int y, int z, EntityLivingBase player);

    default void setAOE(NBTTagCompound statNbt, int value) {
        statNbt.setInteger("AOE", value);
    }

    default int getAOE(NBTTagCompound statNbt) {
        return statNbt.getInteger("AOE");
    }

    default int breakBlockAround(int side, int xRadius, int yRadius, IToolStats stats, ItemStack stack, World world, int x, int y, int z,
                                       EntityPlayerMP player) {
        int harvsestleve = stack.getItem().getHarvestLevel(stack,"");

        float rotation = player.rotationYawHead < 0 ? 360f + player.rotationYawHead : player.rotationYawHead;

        int DX;
        int DY;
        int DZ;
        boolean downUp = side < 2;
        boolean northSouth = side < 4;

        if (downUp && !(rotation < 45 || rotation > 315 || (rotation > 135 && rotation < 225 ))) {
            int yt = yRadius;
            yRadius = xRadius;
            xRadius = yt;
        }

        int broken = 0;
        for (int i = -1*yRadius; i <= yRadius; i++) {
            for (int j = -1*xRadius; j <= xRadius; j++) {
                if (i != 0 || j != 0) {
                    if (downUp) {
                        DX = x + i;
                        DY = y;
                        DZ = z + j;
                    } else if (northSouth) {
                        DX = x + i;
                        DY = y + j;
                        DZ = z;
                    } else {
                        DX = x;
                        DY = y + j;
                        DZ = z + i;
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
        if (toolHarvestLevel < blockHarvestLevel) return false;
        boolean isMineble = stats.isMinableBlock(block, (byte) meta);
        if (!isMineble && blockHarvestLevel != 0) {
            return false;
        }
        return true;
    }
}
