package gregtech.common.blocks;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.items.GT_Generic_Block;
import gregtech.api.util.GT_LanguageManager;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.common.render.GT_Renderer_Block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class GT_Block_Ores
        extends GT_Generic_Block
        implements ITileEntityProvider {
    public static ThreadLocal<GT_TileEntity_Ores> mTemporaryTileEntity = new ThreadLocal();
    public static boolean FUCKING_LOCK = false;

    public GT_Block_Ores() {
        super(GT_Item_Ores.class, "gt.blockores", Material.rock);
        this.isBlockContainer = true;
        setStepSound(soundTypeStone);
        setCreativeTab(GregTech_API.TAB_GREGTECH_ORES);
        for (int i = 0; i < 16; i++) {
            GT_ModHandler.addValuableOre(this, i, 1);
        }
        for (int i = 1; i < GregTech_API.sGeneratedMaterials.length; i++) {
            if (GregTech_API.sGeneratedMaterials[i] != null) {
                GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + i + ".name", getLocalizedName(GregTech_API.sGeneratedMaterials[i]));
                GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + (i + 1000) + ".name", getLocalizedName(GregTech_API.sGeneratedMaterials[i]));
                GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + (i + 2000) + ".name", getLocalizedName(GregTech_API.sGeneratedMaterials[i]));
                GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + (i + 3000) + ".name", getLocalizedName(GregTech_API.sGeneratedMaterials[i]));
                GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + (i + 4000) + ".name", getLocalizedName(GregTech_API.sGeneratedMaterials[i]));
                GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + (i + 16000) + ".name", "Small " + getLocalizedName(GregTech_API.sGeneratedMaterials[i]));
                GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + (i + 17000) + ".name", "Small " + getLocalizedName(GregTech_API.sGeneratedMaterials[i]));
                GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + (i + 18000) + ".name", "Small " + getLocalizedName(GregTech_API.sGeneratedMaterials[i]));
                GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + (i + 19000) + ".name", "Small " + getLocalizedName(GregTech_API.sGeneratedMaterials[i]));
                GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + (i + 20000) + ".name", "Small " + getLocalizedName(GregTech_API.sGeneratedMaterials[i]));
                if ((GregTech_API.sGeneratedMaterials[i].mTypes & 0x8) != 0) {
                    GT_OreDictUnificator.registerOre(OrePrefixes.ore.get(GregTech_API.sGeneratedMaterials[i]), new ItemStack(this, 1, i));
                    GT_OreDictUnificator.registerOre(OrePrefixes.oreNetherrack.get(GregTech_API.sGeneratedMaterials[i]), new ItemStack(this, 1, i + 1000));
                    GT_OreDictUnificator.registerOre(OrePrefixes.oreEndstone.get(GregTech_API.sGeneratedMaterials[i]), new ItemStack(this, 1, i + 2000));
                    GT_OreDictUnificator.registerOre(OrePrefixes.oreBlackgranite.get(GregTech_API.sGeneratedMaterials[i]), new ItemStack(this, 1, i + 3000));
                    GT_OreDictUnificator.registerOre(OrePrefixes.oreRedgranite.get(GregTech_API.sGeneratedMaterials[i]), new ItemStack(this, 1, i + 4000));
                }
            }
        }
    }

    public void onNeighborChange(IBlockAccess aWorld, int aX, int aY, int aZ, int aTileX, int aTileY, int aTileZ) {
        if (!FUCKING_LOCK) {
            FUCKING_LOCK = true;
            TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
            if ((tTileEntity instanceof GT_TileEntity_Ores)) {
                ((GT_TileEntity_Ores) tTileEntity).onUpdated();
            }
        }
        FUCKING_LOCK = false;
    }

    public void onNeighborBlockChange(World aWorld, int aX, int aY, int aZ, Block aBlock) {
        if (!FUCKING_LOCK) {
            FUCKING_LOCK = true;
            TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
            if ((tTileEntity instanceof GT_TileEntity_Ores)) {
                ((GT_TileEntity_Ores) tTileEntity).onUpdated();
            }
        }
        FUCKING_LOCK = false;
    }

    public String getLocalizedName(Materials aMaterial) {
        switch (aMaterial) {
            case InfusedAir:
            case InfusedDull:
            case InfusedEarth:
            case InfusedEntropy:
            case InfusedFire:
            case InfusedOrder:
            case InfusedVis:
            case InfusedWater:
                return aMaterial.mDefaultLocalName + " Infused Stone";
            case Vermiculite:
            case Bentonite:
            case Kaolinite:
            case Talc:
            case BasalticMineralSand:
            case GraniticMineralSand:
            case GlauconiteSand:
            case CassiteriteSand:
            case GarnetSand:
            case QuartzSand:
            case Pitchblende:
            case FullersEarth:
                return aMaterial.mDefaultLocalName;
            default:
                return aMaterial.mDefaultLocalName + OrePrefixes.ore.mLocalizedMaterialPost;
        }

    }

    public boolean onBlockEventReceived(World p_149696_1_, int p_149696_2_, int p_149696_3_, int p_149696_4_, int p_149696_5_, int p_149696_6_) {
        super.onBlockEventReceived(p_149696_1_, p_149696_2_, p_149696_3_, p_149696_4_, p_149696_5_, p_149696_6_);
        TileEntity tileentity = p_149696_1_.getTileEntity(p_149696_2_, p_149696_3_, p_149696_4_);
        return tileentity != null ? tileentity.receiveClientEvent(p_149696_5_, p_149696_6_) : false;
    }

    public boolean canEntityDestroy(IBlockAccess world, int x, int y, int z, Entity entity) {
        return (!(entity instanceof EntityDragon)) && (super.canEntityDestroy(world, x, y, z, entity));
    }

    public String getHarvestTool(int aMeta) {
        return aMeta < 8 ? "pickaxe" : "shovel";
    }

    public int getHarvestLevel(int aMeta) {
        return aMeta % 8;
    }

    public float getBlockHardness(World aWorld, int aX, int aY, int aZ) {
        return 1.0F + getHarvestLevel(aWorld.getBlockMetadata(aX, aY, aZ)) * 1.0F;
    }

    public float getExplosionResistance(Entity par1Entity, World aWorld, int aX, int aY, int aZ, double explosionX, double explosionY, double explosionZ) {
        return 1.0F + getHarvestLevel(aWorld.getBlockMetadata(aX, aY, aZ)) * 1.0F;
    }

    protected boolean canSilkHarvest() {
        return false;
    }

    public String getUnlocalizedName() {
        return "gt.blockores";
    }

    public String getLocalizedName() {
        return StatCollector.translateToLocal(getUnlocalizedName() + ".name");
    }

    public int getRenderType() {
        if (GT_Renderer_Block.INSTANCE == null) {
            return super.getRenderType();
        }
        return GT_Renderer_Block.INSTANCE.mRenderID;
    }

    public boolean canBeReplacedByLeaves(IBlockAccess aWorld, int aX, int aY, int aZ) {
        return false;
    }

    public boolean isNormalCube(IBlockAccess aWorld, int aX, int aY, int aZ) {
        return true;
    }

    public boolean hasTileEntity(int aMeta) {
        return true;
    }

    public boolean renderAsNormalBlock() {
        return true;
    }

    public boolean isOpaqueCube() {
        return true;
    }

    public TileEntity createNewTileEntity(World aWorld, int aMeta) {
        return createTileEntity(aWorld, aMeta);
    }

    public IIcon getIcon(IBlockAccess aIBlockAccess, int aX, int aY, int aZ, int aSide) {
        return Blocks.stone.getIcon(0, 0);
    }

    public IIcon getIcon(int aSide, int aMeta) {
        return Blocks.stone.getIcon(0, 0);
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister aIconRegister) {
    }

    public int getDamageValue(World aWorld, int aX, int aY, int aZ) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity != null) && ((tTileEntity instanceof GT_TileEntity_Ores))) {
            return ((GT_TileEntity_Ores) tTileEntity).getMetaData();
        }
        return 0;
    }

    public void breakBlock(World aWorld, int aX, int aY, int aZ, Block par5, int par6) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity instanceof GT_TileEntity_Ores)) {
            mTemporaryTileEntity.set((GT_TileEntity_Ores) tTileEntity);
        }
        super.breakBlock(aWorld, aX, aY, aZ, par5, par6);
        aWorld.removeTileEntity(aX, aY, aZ);
    }

    public ArrayList<ItemStack> getDrops(World aWorld, int aX, int aY, int aZ, int aMeta, int aFortune) {
        TileEntity tTileEntity = aWorld.getTileEntity(aX, aY, aZ);
        if ((tTileEntity instanceof GT_TileEntity_Ores)) {
            return ((GT_TileEntity_Ores) tTileEntity).getDrops(aFortune);
        }
        return mTemporaryTileEntity.get() == null ? new ArrayList() : ((GT_TileEntity_Ores) mTemporaryTileEntity.get()).getDrops(aFortune);
    }

    public TileEntity createTileEntity(World aWorld, int aMeta) {
        return new GT_TileEntity_Ores();
    }

    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item aItem, CreativeTabs aTab, List aList) {
        for (int i = 0; i < GregTech_API.sGeneratedMaterials.length; i++) {
            Materials tMaterial = GregTech_API.sGeneratedMaterials[i];
            if ((tMaterial != null) && ((tMaterial.mTypes & 0x8) != 0)) {
                aList.add(new ItemStack(aItem, 1, i));
                aList.add(new ItemStack(aItem, 1, i + 1000));
                aList.add(new ItemStack(aItem, 1, i + 2000));
                aList.add(new ItemStack(aItem, 1, i + 3000));
                aList.add(new ItemStack(aItem, 1, i + 4000));
                aList.add(new ItemStack(aItem, 1, i + 16000));
                aList.add(new ItemStack(aItem, 1, i + 17000));
                aList.add(new ItemStack(aItem, 1, i + 18000));
                aList.add(new ItemStack(aItem, 1, i + 19000));
                aList.add(new ItemStack(aItem, 1, i + 20000));
            }
        }
    }
}
