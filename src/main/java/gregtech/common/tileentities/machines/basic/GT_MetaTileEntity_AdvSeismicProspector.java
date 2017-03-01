package gregtech.common.tileentities.machines.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import gregtech.api.GregTech_API;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicMachine;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.objects.ItemData;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;
import gregtech.common.blocks.GT_Block_Ores_Abstract;
import gregtech.common.blocks.GT_TileEntity_Ores;
import ic2.core.Ic2Items;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.fluids.FluidStack;

public class GT_MetaTileEntity_AdvSeismicProspector extends GT_MetaTileEntity_BasicMachine {    
    boolean ready = false;
    int radius;
    int near;
    int middle;
    int step;

    public GT_MetaTileEntity_AdvSeismicProspector(int aID, String aName, String aNameRegional, int aTier, int aRadius, int aStep) {
        super(aID, aName, aNameRegional, aTier, 1, // amperage
                "Place, activate with explosives ("
                + "8 Glyceryl, "
                + "32 TNT or "
                + "16 ITNT), use Data Stick",
                1, // input slot count
                1, // output slot count
                "Default.png", // GUI name
                "", // NEI name
                new ITexture[] { new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_SIDE_ROCK_BREAKER_ACTIVE),
                        new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_SIDE_ROCK_BREAKER),
                        new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_TOP_ROCK_BREAKER_ACTIVE),
                        new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_TOP_ROCK_BREAKER),
                        new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_FRONT_ROCK_BREAKER_ACTIVE),
                        new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_FRONT_ROCK_BREAKER),
                        new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_BOTTOM_ROCK_BREAKER_ACTIVE),
                        new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_BOTTOM_ROCK_BREAKER) });
        radius = aRadius;
        near = radius / 3;
        near = near + near % 2; // making near value even;
        middle = near * 2;
        step = aStep;
    }

    protected GT_MetaTileEntity_AdvSeismicProspector(String aName, int aTier, String aDescription, ITexture[][][] aTextures,
            String aGUIName, String aNEIName, int aNear, int aMiddle, int aRadius, int aStep) {
        super(aName, aTier, 1, aDescription, aTextures, 1, 1, aGUIName, aNEIName);
        radius = aRadius;
        near = aNear;
        middle = aMiddle;
        step = aStep;
    }

    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_AdvSeismicProspector(this.mName, this.mTier, this.mDescription, this.mTextures,
                this.mGUIName, this.mNEIName, this.near, this.middle, this.radius, this.step);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isServerSide()) {
            ItemStack aStack = aPlayer.getCurrentEquippedItem();

            if (!ready && (GT_Utility.consumeItems(aPlayer, aStack, Item.getItemFromBlock(Blocks.tnt), 32)
                    || GT_Utility.consumeItems(aPlayer, aStack, Ic2Items.industrialTnt.getItem(), 16)
                    || GT_Utility.consumeItems(aPlayer, aStack, Materials.Glyceryl, 8))) {

                this.ready = true;
                this.mMaxProgresstime = (aPlayer.capabilities.isCreativeMode ? 20 : 800);

            } else if (ready && mMaxProgresstime == 0
                    && aStack != null && aStack.stackSize == 1
                    && aStack.getItem() == ItemList.Tool_DataStick.getItem()) {
                this.ready = false;

                // prospecting ores
                HashMap<String, Integer> tNearOres = new HashMap<String, Integer>();
                HashMap<String, Integer> tMiddleOres = new HashMap<String, Integer>();
                HashMap<String, Integer> tFarOres = new HashMap<String, Integer>();
                prospectOres(tNearOres, tMiddleOres, tFarOres);

                // prospecting oils
                HashMap<String, Integer> tOils = new HashMap<String, Integer>();
                prospectOils(tOils);

                GT_Utility.ItemNBT.setAdvancedProspectionData(mTier,
                    aStack,
                    this.getBaseMetaTileEntity().getXCoord(),
                    this.getBaseMetaTileEntity().getYCoord(),
                    this.getBaseMetaTileEntity().getZCoord(),
                    this.getBaseMetaTileEntity().getWorld().provider.dimensionId,
                    GT_Utility.sortByValueToList(tOils),
                    GT_Utility.sortByValueToList(tNearOres),
                    GT_Utility.sortByValueToList(tMiddleOres),
                    GT_Utility.sortByValueToList(tFarOres),
                    near, middle, radius);
            }
        }

        return true;
    }

    private void prospectOils(HashMap<String, Integer> aOils) {

        int tLeftXBound = GT_Utility.getScaleCoordinates(this.getBaseMetaTileEntity().getXCoord() - radius, 16);
        int tRightXBound = GT_Utility.getScaleCoordinates(this.getBaseMetaTileEntity().getXCoord() + radius, 16);

        int tLeftZBound = GT_Utility.getScaleCoordinates(this.getBaseMetaTileEntity().getZCoord() - radius, 16);
        int tRightZBound = GT_Utility.getScaleCoordinates(this.getBaseMetaTileEntity().getZCoord() + radius, 16);

        HashMap<ChunkPosition, FluidStack> tFluids = new HashMap<ChunkPosition, FluidStack>();

        try {
            for (int x = tLeftXBound; x <= tRightXBound; ++x)
                for (int z = tLeftZBound; z <= tRightZBound; ++z) 
                {
                	ChunkPosition tPos = new ChunkPosition(GT_Utility.getScaleCoordinates(x*16,96), 0, GT_Utility.getScaleCoordinates(z*16,96));
                	FluidStack tFluid = GT_Utility.getUndergroundOil(getBaseMetaTileEntity().getWorld(), x*16, z*16);
            		if (tFluid != null)
	                	if (tFluids.containsKey(tPos))
	                	{
	                		if (tFluids.get(tPos).amount<tFluid.amount)
	                			tFluids.get(tPos).amount = tFluid.amount;
	                	} else if (tFluid.amount / 5000 > 0)
	                        tFluids.put(tPos, tFluid);
                }
			
    		for (HashMap.Entry<ChunkPosition, FluidStack> fl : tFluids.entrySet()) {
    			aOils.put(fl.getKey().chunkPosX + "," + fl.getKey().chunkPosZ + "," + (fl.getValue().amount / 5000) + "," + fl.getValue().getLocalizedName(), fl.getValue().amount / 5000);
    		}
		} catch (Exception e) {
			// TODO: handle exception
		}        
    }

    private void prospectOres(Map<String, Integer> aNearOres, Map<String, Integer> aMiddleOres, Map<String, Integer> aFarOres) {        
        int tLeftXBound = this.getBaseMetaTileEntity().getXCoord() - radius;
        int tRightXBound = tLeftXBound + 2*radius;

        int tLeftZBound = this.getBaseMetaTileEntity().getZCoord() - radius;
        int tRightZBound = tLeftZBound + 2*radius;

        for (int i = tLeftXBound; i <= tRightXBound; i += step)
            for (int k = tLeftZBound; k <= tRightZBound; k += step) {
                int di = Math.abs(i - this.getBaseMetaTileEntity().getXCoord());
                int dk = Math.abs(k - this.getBaseMetaTileEntity().getZCoord());

                if (di <= near && dk <= near)
                    prospectHole(i, k, aNearOres);
                else if (di <= middle && dk <= middle)
                    prospectHole(i, k, aMiddleOres);
                else
                    prospectHole(i, k, aFarOres);
            } 
    }

    private void prospectHole(
            int i, int k, Map<String, Integer> aOres) {

        String tFoundOre = null;
        for (int j = this.getBaseMetaTileEntity().getYCoord(); j > 0; j--) {
            tFoundOre = checkForOre(i, j, k);                            
            if (tFoundOre == null)
                continue;

            countOre(aOres, tFoundOre);
        }
    }

    private String checkForOre(int x, int y, int z) {
        Block tBlock = this.getBaseMetaTileEntity().getBlock(x, y, z);

        if (tBlock instanceof GT_Block_Ores_Abstract) {
            TileEntity tTileEntity = getBaseMetaTileEntity().getWorld().getTileEntity(x, y, z);

            if ((tTileEntity instanceof GT_TileEntity_Ores)
                && (((GT_TileEntity_Ores) tTileEntity).mMetaData < 16000)) { // Filtering small ores
                Materials tMaterial
                    = GregTech_API.sGeneratedMaterials[((GT_TileEntity_Ores) tTileEntity).mMetaData % 1000];

                if ((tMaterial != null) && (tMaterial != Materials._NULL))
                    return tMaterial.mDefaultLocalName;
            }
        } else {
            int tMetaID = getBaseMetaTileEntity().getWorld().getBlockMetadata(x, y, z);
            ItemData tAssotiation = GT_OreDictUnificator.getAssociation(new ItemStack(tBlock, 1, tMetaID));

            if ((tAssotiation != null) && (tAssotiation.mPrefix.toString().startsWith("ore")))
                return tAssotiation.mMaterial.mMaterial.mDefaultLocalName;
        }

        return null;
    }

    private static void countOre(Map<String, Integer> map, String ore) {
        Integer oldCount = map.get(ore);
        oldCount = (oldCount == null) ? 0 : oldCount;

        map.put(ore, oldCount + 1);
    }
}
