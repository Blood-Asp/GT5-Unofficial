package gregtech.common.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IDebugableBlock;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.items.GT_Generic_Block;
import gregtech.api.metatileentity.BaseMetaPipeEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.BaseTileEntity;
import gregtech.api.util.GT_BaseCrop;
import gregtech.api.util.GT_Log;
import gregtech.api.util.GT_Utility;
import gregtech.common.render.GT_Renderer_Block;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static gregtech.GT_Mod.GT_FML_LOGGER;
import static gregtech.api.objects.XSTR.XSTR_INSTANCE;

public class GT_Block_Machines extends GT_Generic_Block implements IDebugableBlock, ITileEntityProvider {
    private static final ThreadLocal<IGregTechTileEntity> mTemporaryTileEntity = new ThreadLocal<>();
    private boolean renderAsNormalBlock;

    public GT_Block_Machines() {
        super(GT_Item_Machines.class, "gt.blockmachines", new GT_Material_Machines());
        GregTech_API.registerMachineBlock(this, -1);
        setHardness(1.0F);
        setResistance(10.0F);
        setStepSound(soundTypeMetal);
        setCreativeTab(GregTech_API.TAB_GREGTECH);
        this.isBlockContainer = true;
        this.renderAsNormalBlock = true;
        this.useNeighborBrightness = true;
    }

    @Override
    public String getHarvestTool(int aMeta) {
        if (aMeta >= 8 && aMeta <= 11) {
            return "cutter";
        }
        return "wrench";
    }

    @Override
    public int getHarvestLevel(int aMeta) {
        return aMeta % 4;
    }

    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

    @Override
    public void onNeighborChange(IBlockAccess aWorld, int aX, int aY, int aZ, int aTileX, int aTileY, int aTileZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity instanceof BaseTileEntity)) {
            ((BaseTileEntity) tTileEntity).onAdjacentBlockChange(aTileX, aTileY, aTileZ);
        }
    }

    @Override
    public void onNeighborBlockChange(World aWorld, int aX, int aY, int aZ, Block aBlock) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity instanceof BaseMetaPipeEntity)) {
            ((BaseMetaPipeEntity) tTileEntity).onNeighborBlockChange(aX, aY, aZ);
        }
    }

    @Override
    public void onBlockAdded(World aWorld, int aX, int aY, int aZ) {
        super.onBlockAdded(aWorld, aX, aY, aZ);
        if (GregTech_API.isMachineBlock(this, aWorld.getBlockMetadata(aX, aY, aZ))) {
            GregTech_API.causeMachineUpdate(aWorld, aX, aY, aZ);
        }
    }

    @Override
    public String getUnlocalizedName() {
        return "gt.blockmachines";
    }

    @Override
    public String getLocalizedName() {
        return StatCollector.translateToLocal(getUnlocalizedName() + ".name");
    }

    @Override
    public int getFlammability(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection face) {
        return 0;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection face) {
        return GregTech_API.sMachineFlammable && (aWorld.getBlockMetadata(aX, aY, aZ) == 0) ? 100 : 0;
    }

    @Override
    public int getRenderType() {
        if (GT_Renderer_Block.INSTANCE == null) {
            return super.getRenderType();
        }
        return GT_Renderer_Block.INSTANCE.mRenderID;
    }

    @Override
    public boolean isFireSource(World aWorld, int aX, int aY, int aZ, ForgeDirection side) {
        return GregTech_API.sMachineFlammable && (aWorld.getBlockMetadata(aX, aY, aZ) == 0);
    }

    @Override
    public boolean isFlammable(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection face) {
        return GregTech_API.sMachineFlammable && (aWorld.getBlockMetadata(aX, aY, aZ) == 0);
    }

    @Override
    public boolean canCreatureSpawn(EnumCreatureType type, IBlockAccess aWorld, int aX, int aY, int aZ) {
        return false;
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess var1, int var2, int var3, int var4, int var5) {
        return true;
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess aWorld, int aX, int aY, int aZ) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockAccess aWorld, int aX, int aY, int aZ) {
        return false;
    }

    @Override
    public boolean hasTileEntity(int aMeta) {
        return true;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return renderAsNormalBlock;
    }

    public GT_Block_Machines setRenderAsNormalBlock(boolean aBool) {
        renderAsNormalBlock = aBool;
        return this;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World aWorld, int aMeta) {
        return createTileEntity(aWorld, aMeta);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess aIBlockAccess, int aX, int aY, int aZ, int aSide) {
        return Textures.BlockIcons.MACHINE_LV_SIDE.getIcon();
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int aSide, int aMeta) {
        return Textures.BlockIcons.MACHINE_LV_SIDE.getIcon();
    }

    @Override
    public boolean onBlockEventReceived(World aWorld, int aX, int aY, int aZ, int aData1, int aData2) {
        super.onBlockEventReceived(aWorld, aX, aY, aZ, aData1, aData2);
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return tTileEntity != null && tTileEntity.receiveClientEvent(aData1, aData2);
    }

    @SuppressWarnings("unchecked") // Old API uses raw List type
    @Override
    public void addCollisionBoxesToList(
            World aWorld, int aX, int aY, int aZ, AxisAlignedBB inputAABB, List outputAABB, Entity collider) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof IGregTechTileEntity &&
                ((IGregTechTileEntity) tTileEntity).getMetaTileEntity() != null) {
            ((IGregTechTileEntity) tTileEntity)
                    .addCollisionBoxesToList(aWorld, aX, aY, aZ, inputAABB, outputAABB, collider);
            return;
        }
        super.addCollisionBoxesToList(aWorld, aX, aY, aZ, inputAABB, outputAABB, collider);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World aWorld, int aX, int aY, int aZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof IGregTechTileEntity &&
                ((IGregTechTileEntity) tTileEntity).getMetaTileEntity() != null) {
            return ((IGregTechTileEntity) tTileEntity).getCollisionBoundingBoxFromPool(aWorld, aX, aY, aZ);
        }
        return super.getCollisionBoundingBoxFromPool(aWorld, aX, aY, aZ);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World aWorld, int aX, int aY, int aZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof IGregTechTileEntity &&
                ((IGregTechTileEntity) tTileEntity).getMetaTileEntity() != null) {
            return ((IGregTechTileEntity) tTileEntity).getCollisionBoundingBoxFromPool(aWorld, aX, aY, aZ);
        }
        return super.getSelectedBoundingBoxFromPool(aWorld, aX, aY, aZ);
    }

    @Override  //THIS
    public void setBlockBoundsBasedOnState(IBlockAccess blockAccess, int aX, int aY, int aZ) {
        TileEntity tTileEntity = blockAccess.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof IGregTechTileEntity &&
                (((IGregTechTileEntity) tTileEntity).getMetaTileEntity() != null)) {
            AxisAlignedBB bbb = ((IGregTechTileEntity) tTileEntity)
                    .getCollisionBoundingBoxFromPool(
                            ((IGregTechTileEntity) tTileEntity).getWorld(), 0, 0, 0);
            minX = bbb.minX; //This essentially sets block bounds
            minY = bbb.minY;
            minZ = bbb.minZ;
            maxX = bbb.maxX;
            maxY = bbb.maxY;
            maxZ = bbb.maxZ;
            return;
        }
        super.setBlockBoundsBasedOnState(blockAccess, aX, aY, aZ);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        super.setBlockBounds(0, 0, 0, 1, 1, 1);
    }

    @Override
    public void onEntityCollidedWithBlock(World aWorld, int aX, int aY, int aZ, Entity collider) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof IGregTechTileEntity &&
                ((IGregTechTileEntity) tTileEntity).getMetaTileEntity() != null) {
            ((IGregTechTileEntity) tTileEntity).onEntityCollidedWithBlock(aWorld, aX, aY, aZ, collider);
            return;
        }
        super.onEntityCollidedWithBlock(aWorld, aX, aY, aZ, collider);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister aIconRegister) {
        if (!GregTech_API.sPostloadFinished) return;
        GT_Log.out.println("GT_Mod: Setting up Icon Register for Blocks");
        GregTech_API.setBlockIconRegister(aIconRegister);

        GT_Log.out.println("GT_Mod: Registering MetaTileEntity specific Textures");
        try {
            for (IMetaTileEntity tMetaTileEntity : GregTech_API.METATILEENTITIES) {
                if (tMetaTileEntity != null) {
                    tMetaTileEntity.registerIcons(aIconRegister);
                }
            }
        } catch (Exception e) {
            e.printStackTrace(GT_Log.err);
        }
        GT_Log.out.println("GT_Mod: Registering Crop specific Textures");
        try {
            for (GT_BaseCrop tCrop : GT_BaseCrop.sCropList) {
                tCrop.registerSprites(aIconRegister);
            }
        } catch (Exception e) {
            e.printStackTrace(GT_Log.err);
        }
        GT_Log.out.println("GT_Mod: Starting Block Icon Load Phase");
        GT_FML_LOGGER.info("GT_Mod: Starting Block Icon Load Phase");
        try {
            for (Runnable tRunnable : GregTech_API.sGTBlockIconload) {
                tRunnable.run();
            }
        } catch (Exception e) {
            e.printStackTrace(GT_Log.err);
        }
        GT_Log.out.println("GT_Mod: Finished Block Icon Load Phase");
        GT_FML_LOGGER.info("GT_Mod: Finished Block Icon Load Phase");
    }

    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        return tTileEntity instanceof BaseMetaTileEntity &&
                ((BaseMetaTileEntity) tTileEntity).privateAccess() &&
                !((BaseMetaTileEntity) tTileEntity).playerOwnsThis(aPlayer, true) ?
                -1.0F : super.getPlayerRelativeBlockHardness(aPlayer, aWorld, aX, aY, aZ);
    }

    @Override
    public boolean onBlockActivated(
            World aWorld, int aX, int aY, int aZ, EntityPlayer aPlayer, int aSide, float par1, float par2, float par3) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity == null) {
            return false;
        }
        if (aPlayer.isSneaking()) {
            ItemStack tCurrentItem = aPlayer.inventory.getCurrentItem();
            if (
                    tCurrentItem != null &&
                    !GT_Utility.isStackInList(tCurrentItem, GregTech_API.sScrewdriverList) &&
                    !GT_Utility.isStackInList(tCurrentItem, GregTech_API.sWrenchList) &&
                    !GT_Utility.isStackInList(tCurrentItem, GregTech_API.sWireCutterList) &&
                    !GT_Utility.isStackInList(tCurrentItem, GregTech_API.sSolderingToolList)
            ) return false;
        }
        if ((tTileEntity instanceof IGregTechTileEntity)) {
            if (((IGregTechTileEntity) tTileEntity).getTimer() < 50L) {
                return false;
            }
            if ((!aWorld.isRemote) && !((IGregTechTileEntity) tTileEntity).isUseableByPlayer(aPlayer)) {
                return true;
            }
            return ((IGregTechTileEntity) tTileEntity).onRightclick(aPlayer, (byte) aSide, par1, par2, par3);
        }
        return false;
    }

    @Override
    public void onBlockClicked(World aWorld, int aX, int aY, int aZ, EntityPlayer aPlayer) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof IGregTechTileEntity) {
            ((IGregTechTileEntity) tTileEntity).onLeftclick(aPlayer);
        }
    }

    @Override
    public int getDamageValue(World aWorld, int aX, int aY, int aZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof IGregTechTileEntity) {
            return ((IGregTechTileEntity) tTileEntity).getMetaTileID();
        }
        return 0;
    }

    @Override
    public void onBlockExploded(World aWorld, int aX, int aY, int aZ, Explosion aExplosion) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof BaseMetaTileEntity) {
            GT_Log.exp.printf("Explosion at : %d | %d | %d DIMID: %s due to near explosion!%n",
                    aX, aY, aZ, aWorld.provider.dimensionId);
            ((BaseMetaTileEntity) tTileEntity).doEnergyExplosion();
        }
        super.onBlockExploded(aWorld, aX, aY, aZ, aExplosion);
    }

    @Override
    public void breakBlock(World aWorld, int aX, int aY, int aZ, Block par5, int par6) {
        GregTech_API.causeMachineUpdate(aWorld, aX, aY, aZ);
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof IGregTechTileEntity) {
            IGregTechTileEntity tGregTechTileEntity = (IGregTechTileEntity) tTileEntity;
            mTemporaryTileEntity.set(tGregTechTileEntity);
            for (int i = 0; i < tGregTechTileEntity.getSizeInventory(); i++) {
                ItemStack tItem = tGregTechTileEntity.getStackInSlot(i);
                if ((tItem != null) && (tItem.stackSize > 0) && (tGregTechTileEntity.isValidSlot(i))) {
                    EntityItem tItemEntity = new EntityItem(aWorld,
                            aX + XSTR_INSTANCE.nextFloat() * 0.8F + 0.1F,
                            aY + XSTR_INSTANCE.nextFloat() * 0.8F + 0.1F,
                            aZ + XSTR_INSTANCE.nextFloat() * 0.8F + 0.1F,
                            new ItemStack(tItem.getItem(), tItem.stackSize, tItem.getItemDamage()));
                    if (tItem.hasTagCompound()) {
                        tItemEntity.getEntityItem().setTagCompound((NBTTagCompound) tItem.getTagCompound().copy());
                    }
                    tItemEntity.motionX = (XSTR_INSTANCE.nextGaussian() * 0.05D);
                    tItemEntity.motionY = (XSTR_INSTANCE.nextGaussian() * 0.25D);
                    tItemEntity.motionZ = (XSTR_INSTANCE.nextGaussian() * 0.05D);
                    aWorld.spawnEntityInWorld(tItemEntity);
                    tItem.stackSize = 0;
                    tGregTechTileEntity.setInventorySlotContents(i, null);
                }
            }
        }
        super.breakBlock(aWorld, aX, aY, aZ, par5, par6);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World aWorld, int aX, int aY, int aZ, int aMeta, int aFortune) {
        IGregTechTileEntity tGregTechTileEntity = mTemporaryTileEntity.get();
        mTemporaryTileEntity.remove();
        return tGregTechTileEntity == null ? new ArrayList<>() : tGregTechTileEntity.getDrops();
    }

    @Override
    public boolean removedByPlayer(World aWorld, EntityPlayer aPlayer, int aX, int aY, int aZ, boolean aWillHarvest) {
        // This delays deletion of the block until after getDrops
        return aWillHarvest || super.removedByPlayer(aWorld, aPlayer, aX, aY, aZ, false);
    }

    @Override
    public void harvestBlock(World aWorld, EntityPlayer aPlayer, int aX, int aY, int aZ, int aMeta) {
        super.harvestBlock(aWorld, aPlayer, aX, aY, aZ, aMeta);
        aWorld.setBlockToAir(aX, aY, aZ);
    }

    @Override
    public int getComparatorInputOverride(World aWorld, int aX, int aY, int aZ, int aSide) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof IGregTechTileEntity) {
            return ((IGregTechTileEntity) tTileEntity).getComparatorValue((byte) aSide);
        }
        return 0;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess aWorld, int aX, int aY, int aZ, int aSide) {
        if (aSide < 0 || aSide > 5) {
            return 0;
        }
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof IGregTechTileEntity) {
            return ((IGregTechTileEntity) tTileEntity).getOutputRedstoneSignal(GT_Utility.getOppositeSide(aSide));
        }
        return 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess aWorld, int aX, int aY, int aZ, int aSide) {
        if (aSide < 0 || aSide > 5) {
            return 0;
        }
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof IGregTechTileEntity) {
            return ((IGregTechTileEntity) tTileEntity).getStrongOutputRedstoneSignal(GT_Utility.getOppositeSide(aSide));
        }
        return 0;
    }

    @Override
    public void dropBlockAsItemWithChance(World aWorld, int aX, int aY, int aZ, int par5, float chance, int par7) {
        if (!aWorld.isRemote) {
            TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
            if (tTileEntity != null && (chance < 1.0F)) {
                if (tTileEntity instanceof BaseMetaTileEntity && (GregTech_API.sMachineNonWrenchExplosions)) {
                    GT_Log.exp.printf("Explosion at : %d | %d | %d DIMID: %s NonWrench picking/Rain!%n",
                            aX, aY, aZ, aWorld.provider.dimensionId);
                    ((BaseMetaTileEntity) tTileEntity).doEnergyExplosion();
                }
            } else {
                super.dropBlockAsItemWithChance(aWorld, aX, aY, aZ, par5, chance, par7);
            }
        }
    }

    @Override
    public boolean isSideSolid(IBlockAccess aWorld, int aX, int aY, int aZ, ForgeDirection aSide) {
        if (aWorld.getBlockMetadata(aX, aY, aZ) == 0) {
            return true;
        }
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity != null) {
            if (tTileEntity instanceof BaseMetaTileEntity) {
                return true;
            }
            if (tTileEntity instanceof BaseMetaPipeEntity &&
                    (((BaseMetaPipeEntity) tTileEntity).mConnections & 0xFFFFFFC0) != 0) {
                return true;
            }
            return tTileEntity instanceof ICoverable &&
                    ((ICoverable) tTileEntity).getCoverIDAtSide((byte) aSide.ordinal()) != 0;
        }
        return false;
    }

    @Override
    public boolean isBlockNormalCube() {
        return true;
    }

    /**
     * Returns the default ambient occlusion value based on block opacity
     */
    @SideOnly(Side.CLIENT)
    @Override
    public float getAmbientOcclusionLightValue()
    {
        return this.renderAsNormalBlock() ? 0.2F : 0.5F;
    }

    @Override
    public int getLightOpacity(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof IGregTechTileEntity) {
            return ((IGregTechTileEntity) tTileEntity).getLightOpacity();
        }
        return aWorld.getBlockMetadata(aX, aY, aZ) == 0 ? 255 : 0;
    }

    @Override
    public int getLightValue(IBlockAccess aWorld, int aX, int aY, int aZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof BaseMetaTileEntity) {
            return ((BaseMetaTileEntity) tTileEntity).getLightValue();
        }
        return 0;
    }

    @Override
    public TileEntity createTileEntity(World aWorld, int aMeta) {
        if (aMeta < 4) {
            return GregTech_API.constructBaseMetaTileEntity();
        }
        return new BaseMetaPipeEntity();
    }

    @Override
    public float getExplosionResistance(
            Entity par1Entity, World aWorld, int aX, int aY, int aZ,
            double explosionX, double explosionY, double explosionZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof IGregTechTileEntity) {
            return ((IGregTechTileEntity) tTileEntity).getBlastResistance((byte) 6);
        }
        return 10.0F;
    }

    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("unchecked") // Old API uses raw List type
    public void getSubBlocks(Item par1Item, CreativeTabs par2CreativeTabs, List par3List) {
        for (int i = 1; i < GregTech_API.METATILEENTITIES.length; i++) {
            if (GregTech_API.METATILEENTITIES[i] != null) {
                par3List.add(new ItemStack(par1Item, 1, i));
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World aWorld, int aX, int aY, int aZ, EntityLivingBase aPlayer, ItemStack aStack) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (!(tTileEntity instanceof IGregTechTileEntity)) {
            return;
        }
        IGregTechTileEntity iGregTechTileEntity = (IGregTechTileEntity) tTileEntity;
        if (aPlayer == null) {
            iGregTechTileEntity.setFrontFacing((byte) ForgeDirection.UP.ordinal());
            return;
        }
        int yawQuadrant = MathHelper.floor_double(aPlayer.rotationYaw * 4.0F / 360.0F + 0.5D) & 0x3;
        int pitch = Math.round(aPlayer.rotationPitch);
        if (pitch >= 65 && iGregTechTileEntity.isValidFacing((byte) ForgeDirection.UP.ordinal())) {
            iGregTechTileEntity.setFrontFacing((byte) ForgeDirection.UP.ordinal());
            return;
        }
        if (pitch <= -65 && iGregTechTileEntity.isValidFacing((byte) ForgeDirection.DOWN.ordinal())) {
            iGregTechTileEntity.setFrontFacing((byte) ForgeDirection.DOWN.ordinal());
            return;
        }
        switch (yawQuadrant) {
            case 0:
                iGregTechTileEntity.setFrontFacing((byte) ForgeDirection.NORTH.ordinal());
                break;
            case 1:
                iGregTechTileEntity.setFrontFacing((byte) ForgeDirection.EAST.ordinal());
                break;
            case 2:
                iGregTechTileEntity.setFrontFacing((byte) ForgeDirection.SOUTH.ordinal());
                break;
            case 3:
                iGregTechTileEntity.setFrontFacing((byte) ForgeDirection.WEST.ordinal());
                break;
            default:
                break;
        }
    }

    @Override
        public ArrayList<String> getDebugInfo(EntityPlayer aPlayer, int aX, int aY, int aZ, int aLogLevel) {
        TileEntity tTileEntity = aPlayer.worldObj.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof BaseMetaTileEntity) {
            return ((BaseMetaTileEntity) tTileEntity).getDebugInfo(aPlayer, aLogLevel);
        }
        if (tTileEntity instanceof BaseMetaPipeEntity) {
            return ((BaseMetaPipeEntity) tTileEntity).getDebugInfo(aPlayer, aLogLevel);
        }
        return (ArrayList<String>) Collections.<String>emptyList();
    }

    @Override
    public boolean recolourBlock(World aWorld, int aX, int aY, int aZ, ForgeDirection aSide, int aColor) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if (tTileEntity instanceof IGregTechTileEntity) {
            if (((IGregTechTileEntity) tTileEntity).getColorization() == (byte) ((~aColor) & 0xF)) {
                return false;
            }
            ((IGregTechTileEntity) tTileEntity).setColorization((byte) ((~aColor) & 0xF));
            return true;
        }
        return false;
    }
}
