package gregtech.common.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.ITexture;
import gregtech.api.items.GT_Generic_Block;
import gregtech.api.objects.GT_CopiedBlockTexture;
import gregtech.api.util.GT_LanguageManager;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.common.render.GT_Renderer_Block;
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
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class GT_Block_Ores extends GT_Generic_Block implements ITileEntityProvider {
    public static ThreadLocal<GT_TileEntity_Ores> mTemporaryTileEntity = new ThreadLocal();
    public static boolean FUCKING_LOCK = false;
    public static boolean tHideOres;
    private final String aTextName = ".name";
    private final String aTextSmall = "Small ";
    private static Boolean avoidTileEntityCreation = false;

    private final int mDropState;
    private final boolean[] mEnabled = new boolean[8];
    private final OrePrefixes[] mPrefixes = new OrePrefixes[8];
    private final Object[] mDroppedDusts = new Object[8];
    private final ITexture[] mTextures = new ITexture[16];
    private final double[] mBaseHardness = new double[16];
    
    public static final HashMap<String, Integer> sBlockReplacementMap = new HashMap<>();
    
    public static class OreBlockProp {
    	public static final OreBlockProp NONE = new OreBlockProp("NULL", OrePrefixes.ore, null, false);
    	private static final ITexture TEXTURE_STONE = new GT_CopiedBlockTexture(Blocks.stone, 0, 0);

    	public String mBlockName, mOrePrefix, mDroppedDust, mHarvestTool;
    	public boolean mEnabled;
    	public double mBaseHardness;
    	private Block mBlock;
    	private int mBlockMeta;
    	private ITexture mTexture = null;

    	public OreBlockProp(Block aBlock, int aMeta, OrePrefixes aPrefix, Materials aMaterial, boolean aEnabled) {
    		this(Block.blockRegistry.getNameForObject(aBlock) + ":" + aMeta, aPrefix, aMaterial, aEnabled);
    	}

    	public OreBlockProp(String aBlockConfig, OrePrefixes aPrefix, Materials aMaterial, boolean aEnabled) {
    		this(aBlockConfig, aPrefix.name(), "Material:" + (aMaterial == null ? Materials._NULL.mName : aMaterial.mName), aEnabled);
    	}

    	public OreBlockProp(String aBlockConfig, String aPrefixConfig, String aDroppedDustConfig, boolean aEnabled) {
    		this(aBlockConfig, aPrefixConfig, aDroppedDustConfig, "pickaxe", 1.0D, aEnabled);
    	}

    	public OreBlockProp(String aBlockConfig, String aPrefixConfig, String aDroppedDustConfig, String aTool, double aHardness, boolean aEnabled) {
    		mBlockName = aBlockConfig;
    		mOrePrefix = aPrefixConfig;
    		mDroppedDust = aDroppedDustConfig;
    		mHarvestTool = aTool;
    		mBaseHardness = aHardness;
    		try {mBlock = Block.getBlockFromName(mBlockName.substring(0, mBlockName.lastIndexOf(":")));}
    		catch (Exception e) {mBlock = null;}
    		try {mBlockMeta = Integer.parseInt(mBlockName.substring(mBlockName.lastIndexOf(":") + 1));}
    		catch (Exception e) {mBlockMeta = 0;}
    		mEnabled = aEnabled && isValid();
    	}

    	public OreBlockProp setTexture(ITexture aTexture) {
    		mTexture = aTexture;
    		return this;
    	}
    	
    	public String getBlockName() {
    		try {return mBlockName.substring(0, mBlockName.lastIndexOf(":"));}
    		catch (Exception e) {return "NULL";}
    	}

    	public Block getBlock() {
    		return mBlock;
    	}

    	public int getBlockMeta() {
    		return mBlockMeta;
    	}

    	public OrePrefixes getPrefix() {
    		return OrePrefixes.getOrePrefix(mOrePrefix);
    	}

    	public Object getDustDrop() {
    		if (mDroppedDust.startsWith("Material:"))
    			return Materials.get(mDroppedDust.substring(9));
    		else if (mDroppedDust.startsWith("ItemStack:"))
    			return GT_OreDictUnificator.get(mDroppedDust.substring(10), 1L);
    		return null;
    	}

    	public ITexture getTexture() {
    		return mTexture == null ? (mTexture = (isValid() ? new GT_CopiedBlockTexture(mBlock, 0, mBlockMeta) : TEXTURE_STONE)) : mTexture; 
    	}

    	public int getHarvestLevel() {
    		if (isValid())
    			return mBlock.getHarvestLevel(mBlockMeta);
    		return 0;
    	}

    	public boolean isValid() {
    		return !(mBlock == null || mBlockMeta < 0 || mBlockMeta >= 16);
    	}

    	public static OreBlockProp[] getEmptyList() {
    		return new OreBlockProp[]{NONE, NONE, NONE, NONE, NONE, NONE, NONE, NONE};
    	}
    }

    /**
     * 
     * @param aUnlocalizedName
     * @param aHideFirstMeta
     * @param aMaterial Block Material
     * @param aOreBlocks List of the Blocks which will be replaced by ore. Must have 8 entries.
     * @param aDropState -1 for vanilla GT ores, -2 for GC ores, -3, -4, -5 for UB ores; positive value for custom ores; 0 for not available.
     */
    public GT_Block_Ores(String aUnlocalizedName, boolean aHideFirstMeta, short aDropState, OreBlockProp... aOreBlocks) {
        super(GT_Item_Ores.class, aUnlocalizedName, Material.rock);
        this.isBlockContainer = true;
        setStepSound(soundTypeStone);
        setCreativeTab(GregTech_API.TAB_GREGTECH_ORES);
        tHideOres = Loader.isModLoaded("NotEnoughItems") && GT_Mod.gregtechproxy.mHideUnusedOres;
        
        assert aOreBlocks.length == 8;
        
        this.mDropState = aDropState;
        for (int i = 0; i < 8; i++) {
        	this.mEnabled[i] = aOreBlocks[i].mEnabled;
        	this.mPrefixes[i] = aOreBlocks[i].getPrefix();
        	this.mDroppedDusts[i] = aOreBlocks[i].getDustDrop();
        	this.mBaseHardness[i] = aOreBlocks[i].mBaseHardness;
        	this.mBaseHardness[i + 8] = aOreBlocks[i].mBaseHardness;
        	this.mTextures[i] = aOreBlocks[i].getTexture();
        	this.mTextures[i + 8] = aOreBlocks[i].getTexture();
        	this.setHarvestLevel(aOreBlocks[i].mHarvestTool, aOreBlocks[i].getHarvestLevel(), i);
        	this.setHarvestLevel(aOreBlocks[i].mHarvestTool, aOreBlocks[i].getHarvestLevel(), i + 8);
        	sBlockReplacementMap.put(aOreBlocks[i].mBlockName, (this.mDropState << 16) |  i);
        	GT_ModHandler.addValuableOre(this, i, 1);
        	GT_ModHandler.addValuableOre(this, i + 8, 1);
        }
        
        for (int i = 1; i < GregTech_API.sGeneratedMaterials.length; i++) {
            if (GregTech_API.sGeneratedMaterials[i] != null) {
                for (int j = 0; j < 8; j++) {
                    if (!this.mEnabled[j]) continue;
                    GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + (i + (j * 1000)) + aTextName, getLocalizedName(GregTech_API.sGeneratedMaterials[i]));
                    GT_LanguageManager.addStringLocalization(getUnlocalizedName() + "." + ((i + 16000) + (j * 1000)) + aTextName, aTextSmall + getLocalizedName(GregTech_API.sGeneratedMaterials[i]));
                    if ((GregTech_API.sGeneratedMaterials[i].mTypes & 0x8) != 0) {
                        GT_OreDictUnificator.registerOre(this.mPrefixes[j] != null ? this.mPrefixes[j].get(GregTech_API.sGeneratedMaterials[i]) : "", new ItemStack(this, 1, i + (j * 1000)));
                        if (tHideOres) {
                            if (!(j == 0 && !aHideFirstMeta)) {
                                codechicken.nei.api.API.hideItem(new ItemStack(this, 1, i + (j * 1000)));
                            }
                            codechicken.nei.api.API.hideItem(new ItemStack(this, 1, (i + 16000) + (j * 1000)));
                        }
                    }
                }
            }
        }
    }

    public OrePrefixes[] getProcessingPrefix() { //Must have 8 entries; an entry can be null to disable automatic recipes.
    	return this.mPrefixes;
    }

    public boolean[] getEnabledMetas() { //Must have 8 entries.
    	return this.mEnabled;
    }

    public Block getDroppedBlock() {
    	return getDroppedBlock(this.mDropState);
    }

    public static Block getDroppedBlock(int aDropState) {
    	switch (aDropState) {
    	case 0: return null;
    	case -1: return GregTech_API.sBlockOres1;
    	case -2: return GregTech_API.sBlockOresGC;
    	case -3: return GregTech_API.sBlockOresUb1;
    	case -4: return GregTech_API.sBlockOresUb2;
    	case -5: return GregTech_API.sBlockOresUb3;
    	default:
    		if (aDropState > 0 && aDropState <= GregTech_API.sBlockOresCustom.length)
        		return GregTech_API.sBlockOresCustom[aDropState - 1];
    		return GregTech_API.sBlockOres1;
    	}
    }

    public static int getBlockReplaceData(String aBlockName) {
    	Integer data;
    	if ((data = sBlockReplacementMap.get(aBlockName)) != null)
    		return data;
    	return 0;
    }

    public Object[] getDroppedDusts() { //Must have 8 entries; can be null.
    	return this.mDroppedDusts;
    }

    public ITexture[] getTextureSet() { //Must have 16 entries.
    	return this.mTextures;
    }

    public int getBaseBlockHarvestLevel(int aMeta) {
    	return super.getHarvestLevel(aMeta);
    }

    public String getUnlocalizedName() {
    	return this.mUnlocalizedName;
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
        switch (aMaterial.mName) {
            case "InfusedAir":
            case "InfusedDull":
            case "InfusedEarth":
            case "InfusedEntropy":
            case "InfusedFire":
            case "InfusedOrder":
            case "InfusedVis":
            case "InfusedWater":
                return aMaterial.mDefaultLocalName + " Infused Stone";
            case "Vermiculite":
            case "Bentonite":
            case "Kaolinite":
            case "Talc":
            case "BasalticMineralSand":
            case "GraniticMineralSand":
            case "GlauconiteSand":
            case "CassiteriteSand":
            case "GarnetSand":
            case "QuartzSand":
            case "Pitchblende":
            case "FullersEarth":
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

    public int getHarvestLevel(int aMeta) {
        return aMeta;
    }

    public float getBlockHardness(World aWorld, int aX, int aY, int aZ) {
    	int aMeta = aWorld.getBlockMetadata(aX, aY, aZ);
        return (float) (mBaseHardness[aMeta] < 0 ? mBaseHardness[aMeta] : Math.max(mBaseHardness[aMeta], 1.0D + getHarvestLevel(aMeta) * 1.0D));
    }

    public float getExplosionResistance(Entity par1Entity, World aWorld, int aX, int aY, int aZ, double explosionX, double explosionY, double explosionZ) {
    	int aMeta = aWorld.getBlockMetadata(aX, aY, aZ);
        return (float) (mBaseHardness[aMeta] < 0 ? 6000000.0F : Math.max(mBaseHardness[aMeta], 1.0D + getHarvestLevel(aMeta) * 1.0D));
    }

    protected boolean canSilkHarvest() {
        return false;
    }

    public String getLocalizedName() {
        return StatCollector.translateToLocal(getUnlocalizedName() + aTextName);
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
        return !avoidTileEntityCreation;
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
        if (((tTileEntity instanceof GT_TileEntity_Ores))) {
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
            return ((GT_TileEntity_Ores) tTileEntity).getDrops(getDroppedBlock(), aFortune);
        }
        return mTemporaryTileEntity.get() == null ? new ArrayList() : ((GT_TileEntity_Ores) mTemporaryTileEntity.get()).getDrops(getDroppedBlock(), aFortune);
    }

    public TileEntity createTileEntity(World aWorld, int aMeta) {
        return new GT_TileEntity_Ores();
    }

    @Override
    public void getSubBlocks(Item aItem, CreativeTabs aTab, List aList) {
        for (int i = 0; i < GregTech_API.sGeneratedMaterials.length; i++) {
            Materials tMaterial = GregTech_API.sGeneratedMaterials[i];
            if ((tMaterial != null) && ((tMaterial.mTypes & 0x8) != 0)) {
                if (!(new ItemStack(aItem, 1, i).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i));
                if (!(new ItemStack(aItem, 1, i + 1000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 1000));
                if (!(new ItemStack(aItem, 1, i + 2000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 2000));
                if (!(new ItemStack(aItem, 1, i + 3000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 3000));
                if (!(new ItemStack(aItem, 1, i + 4000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 4000));
                if (!(new ItemStack(aItem, 1, i + 5000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 5000));
                if (!(new ItemStack(aItem, 1, i + 6000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 6000));
                if (!(new ItemStack(aItem, 1, i + 7000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 7000));
                if (!(new ItemStack(aItem, 1, i + 16000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 16000));
                if (!(new ItemStack(aItem, 1, i + 17000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 17000));
                if (!(new ItemStack(aItem, 1, i + 18000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 18000));
                if (!(new ItemStack(aItem, 1, i + 19000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 19000));
                if (!(new ItemStack(aItem, 1, i + 20000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 20000));
                if (!(new ItemStack(aItem, 1, i + 21000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 21000));
                if (!(new ItemStack(aItem, 1, i + 22000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 22000));
                if (!(new ItemStack(aItem, 1, i + 23000).getDisplayName().contains(aTextName))) aList.add(new ItemStack(aItem, 1, i + 23000));
            }
        }
    }

    public static void setOreBlock(World aWorld, int aX, int aY, int aZ, GT_Block_Ores aOreBlock, int aOreMeta) {
    	synchronized (avoidTileEntityCreation) {
        	avoidTileEntityCreation = true;
        	int tOreBlockMeta = GT_TileEntity_Ores.getHarvestData((short) aOreMeta, aOreBlock.getBaseBlockHarvestLevel(aOreMeta % 16000 / 1000));
        	aWorld.setBlock(aX, aY, aZ, aOreBlock, tOreBlockMeta, 0);
        	GT_TileEntity_Ores aTileEntity = new GT_TileEntity_Ores();
        	aTileEntity.setWorldObj(aWorld);
        	aTileEntity.xCoord = aX;
        	aTileEntity.yCoord = aY;
        	aTileEntity.zCoord = aZ;
        	aTileEntity.blockType = aOreBlock;
        	aTileEntity.blockMetadata = tOreBlockMeta;
        	aTileEntity.mMetaData = ((short) aOreMeta);
        	aTileEntity.mNatural = true;
        	Chunk tChunk = aWorld.getChunkFromChunkCoords(aX >> 4, aZ >> 4);
            if (tChunk != null) {
            	ChunkPosition tChunkPosition = new ChunkPosition(aX & 15, aY, aZ & 15);
                if (tChunk.chunkTileEntityMap.containsKey(tChunkPosition))
                	((TileEntity) tChunk.chunkTileEntityMap.get(tChunkPosition)).invalidate();
                aTileEntity.validate();
                tChunk.chunkTileEntityMap.put(tChunkPosition, aTileEntity);
            }
        	avoidTileEntityCreation = false;
        }
    }
}