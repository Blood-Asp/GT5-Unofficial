package gregtech.common.tileentities.boilers;

import gregtech.GT_Mod;
import gregtech.api.enums.Dyes;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;
import gregtech.common.GT_Pollution;
import gregtech.common.gui.GT_Container_Boiler;
import gregtech.common.gui.GT_GUIContainer_Boiler;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class GT_MetaTileEntity_Boiler_Lava
        extends GT_MetaTileEntity_Boiler {
    public GT_MetaTileEntity_Boiler_Lava(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, new String[]{
                "A Boiler running off Lava",
                "Produces 600L of Steam per second",
                "Drops to 200L of Steam per second as byproduct slot fills",
                "Clean out byproducts to keep efficiency high",
                "Causes 20 Pollution per second"});
    }

    public GT_MetaTileEntity_Boiler_Lava(String aName, int aTier, String aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    public GT_MetaTileEntity_Boiler_Lava(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    public ITexture[][][] getTextureSet(ITexture[] aTextures) {
        ITexture[][][] rTextures = new ITexture[5][17][];
        for (byte i = -1; i < 16; i++) {
            rTextures[0][(i + 1)] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.MACHINE_STEELBRICKS_BOTTOM, Dyes.getModulation(i, Dyes._NULL.mRGBa))};
            rTextures[1][(i + 1)] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.MACHINE_STEELBRICKS_TOP, Dyes.getModulation(i, Dyes._NULL.mRGBa)), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_PIPE)};
            rTextures[2][(i + 1)] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.MACHINE_STEELBRICKS_SIDE, Dyes.getModulation(i, Dyes._NULL.mRGBa)), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_PIPE)};
            rTextures[3][(i + 1)] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.MACHINE_STEELBRICKS_SIDE, Dyes.getModulation(i, Dyes._NULL.mRGBa)), new GT_RenderedTexture(Textures.BlockIcons.BOILER_LAVA_FRONT)};
            rTextures[4][(i + 1)] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.MACHINE_STEELBRICKS_SIDE, Dyes.getModulation(i, Dyes._NULL.mRGBa)), new GT_RenderedTexture(Textures.BlockIcons.BOILER_LAVA_FRONT_ACTIVE)};
        }
        return rTextures;
    }

    public int maxProgresstime() {
        return 1000;
    }

    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_Container_Boiler(aPlayerInventory, aBaseMetaTileEntity, 32000);
    }

    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_Boiler(aPlayerInventory, aBaseMetaTileEntity, "SteelBoiler.png", 32000);
    }

    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_Boiler_Lava(this.mName, this.mTier, this.mDescriptionArray, this.mTextures);
    }

    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if ((aBaseMetaTileEntity.isServerSide()) && (aTick > 20L)) {
            if (this.mTemperature <= 20) {
                this.mTemperature = 20;
                this.mLossTimer = 0;
            }
            if (++this.mLossTimer > 20) {
                this.mTemperature -= 1;
                this.mLossTimer = 0;
            }
            for (byte i = 1; (this.mSteam != null) && (i < 6); i = (byte) (i + 1)) {
                if (i != aBaseMetaTileEntity.getFrontFacing()) {
                    IFluidHandler tTileEntity = aBaseMetaTileEntity.getITankContainerAtSide(i);
                    if (tTileEntity != null) {
                        FluidStack tDrained = aBaseMetaTileEntity.drain(ForgeDirection.getOrientation(i), Math.max(1, this.mSteam.amount / 2), false);
                        if (tDrained != null) {
                            int tFilledAmount = tTileEntity.fill(ForgeDirection.getOrientation(i).getOpposite(), tDrained, false);
                            if (tFilledAmount > 0) {
                                tTileEntity.fill(ForgeDirection.getOrientation(i).getOpposite(), aBaseMetaTileEntity.drain(ForgeDirection.getOrientation(i), tFilledAmount, true), true);
                            }
                        }
                    }
                }
            }
            if (aTick % 10L == 0L) {
                if (this.mTemperature > 100) {
                    if ((this.mFluid == null) || (!GT_ModHandler.isWater(this.mFluid)) || (this.mFluid.amount <= 0)) {
                        this.mHadNoWater = true;
                    } else {
                        if (this.mHadNoWater) {
                            aBaseMetaTileEntity.doExplosion(2048L);
                            return;
                        }
                        
                        int maxOutput = 300;
                        int minOutput = 100;
                        
                        double efficiency = 1.0;

						if (GT_Mod.gregtechproxy.mSmallLavaBoilerEfficiencyLoss) {
							ItemStack byproductStack = aBaseMetaTileEntity.getStackInSlot(3);

							if (byproductStack != null && !(GT_Utility.isStackInvalid(byproductStack))) {
								// Efficiency drops from 100% when there is no byproduct, 
								// to 0% when there is a full stack of byproduct
								efficiency = 1.0 - (double) byproductStack.stackSize / (double) byproductStack.getMaxStackSize();
							}
						}
                        
                        //Decrease water amount in proportion to steam production
                        //Can't decrease by a fraction, as fluid amounts are integers, so need to decrease randomly so expected amount consumed is correct
                        if(aBaseMetaTileEntity.getRandomNumber(100) < Math.round(efficiency * 100.0)) {
                        	this.mFluid.amount -= 1;
                        }
                        
                        //Steam output drops from maxOutput when efficiency is 100% to minOutput when efficiency is 0%
                        long output = (minOutput + Math.round((double) (maxOutput - minOutput) * efficiency));

                        if (this.mSteam == null) {
                            this.mSteam = GT_ModHandler.getSteam(output);
                        } else if (GT_ModHandler.isSteam(this.mSteam)) {
                            this.mSteam.amount += output;
                        } else {
                            this.mSteam = GT_ModHandler.getSteam(output);
                        }
                    }
                } else {
                    this.mHadNoWater = false;
                }
            }
            if ((this.mSteam != null) &&
                    (this.mSteam.amount > 32000)) {
                sendSound((byte) 1);
                this.mSteam.amount = 24000;
            }
            if ((this.mProcessingEnergy <= 0) && (aBaseMetaTileEntity.isAllowedToWork()) &&
                    (GT_OreDictUnificator.isItemStackInstanceOf(this.mInventory[2], OrePrefixes.bucket.get(Materials.Lava)))) {
                this.mProcessingEnergy += 1000;
                //Either we only had one bucket of lava in the input, and this is fine, or we had a stack of lava buckets.
                //Those can only stack when cheating, so we don't care about voiding them as we can just cheat in more
                aBaseMetaTileEntity.setInventorySlotContents(2, GT_OreDictUnificator.get(OrePrefixes.bucket, Materials.Empty, 1L));
            }
            if ((this.mTemperature < 1000) && (this.mProcessingEnergy > 0) && (aTick % 8L == 0L)) {
                this.mProcessingEnergy -= 3;
                this.mTemperature += 1;
                if (aBaseMetaTileEntity.getRandomNumber(333) == 0) {
                    //Produce one byproduct on average every one bucket of lava
                    if(!aBaseMetaTileEntity.addStackToSlot(3, GT_OreDictUnificator.get(OrePrefixes.dustImpure, Materials.Stone, 1L))) {
                        //If the output slot had something in it already, stick one dust in:
                        aBaseMetaTileEntity.setInventorySlotContents(3, GT_OreDictUnificator.get(OrePrefixes.dustImpure, Materials.Stone, 1));
                    }
                }
            }

            if (this.mProcessingEnergy > 0 && (aTick % 20L == 0L)) {
                GT_Pollution.addPollution(getBaseMetaTileEntity(), 20);
            }
            aBaseMetaTileEntity.setActive(this.mProcessingEnergy > 0);
        }
    }

    public final int fill(FluidStack aFluid, boolean doFill) {
        if ((GT_ModHandler.isLava(aFluid)) && (this.mProcessingEnergy < 50)) {
            int tFilledAmount = Math.min(50, aFluid.amount);
            if (doFill) {
                this.mProcessingEnergy += tFilledAmount;
            }
            return tFilledAmount;
        }
        return super.fill(aFluid, doFill);
    }
}
