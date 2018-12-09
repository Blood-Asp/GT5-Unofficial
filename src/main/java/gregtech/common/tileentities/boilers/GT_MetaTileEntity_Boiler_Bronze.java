package gregtech.common.tileentities.boilers;

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

public class GT_MetaTileEntity_Boiler_Bronze
        extends GT_MetaTileEntity_Boiler {
    public GT_MetaTileEntity_Boiler_Bronze(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, new String[]{
                "An early way to get Steam Power",
                "Produces 120L of Steam per second",
                "Causes 20 Pollution per second"});
    }

    public GT_MetaTileEntity_Boiler_Bronze(String aName, int aTier, String aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    public GT_MetaTileEntity_Boiler_Bronze(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    public ITexture[][][] getTextureSet(ITexture[] aTextures) {
        ITexture[][][] rTextures = new ITexture[5][17][];
        for (byte i = -1; i < 16; i++) {
            rTextures[0][(i + 1)] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.MACHINE_BRONZEBRICKS_BOTTOM, Dyes.getModulation(i, Dyes._NULL.mRGBa))};
            rTextures[1][(i + 1)] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.MACHINE_BRONZEBRICKS_TOP, Dyes.getModulation(i, Dyes._NULL.mRGBa)), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_PIPE)};
            rTextures[2][(i + 1)] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.MACHINE_BRONZEBRICKS_SIDE, Dyes.getModulation(i, Dyes._NULL.mRGBa)), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_PIPE)};
            rTextures[3][(i + 1)] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.MACHINE_BRONZEBRICKS_SIDE, Dyes.getModulation(i, Dyes._NULL.mRGBa)), new GT_RenderedTexture(Textures.BlockIcons.BOILER_FRONT)};
            rTextures[4][(i + 1)] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.MACHINE_BRONZEBRICKS_SIDE, Dyes.getModulation(i, Dyes._NULL.mRGBa)), new GT_RenderedTexture(Textures.BlockIcons.BOILER_FRONT_ACTIVE)};
        }
        return rTextures;
    }

    public int maxProgresstime() {
        return 500;
    }

    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_Container_Boiler(aPlayerInventory, aBaseMetaTileEntity, 16000);
    }

    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_Boiler(aPlayerInventory, aBaseMetaTileEntity, "BronzeBoiler.png", 16000);
    }

    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_Boiler_Bronze(this.mName, this.mTier, this.mDescriptionArray, this.mTextures);
    }

    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if ((aBaseMetaTileEntity.isServerSide()) && (aTick > 20L)) {
            if (this.mTemperature <= 20) {
                this.mTemperature = 20;
                this.mLossTimer = 0;
            }
            if (++this.mLossTimer > 45) {
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
            if (aTick % 25L == 0L) {
                if (this.mTemperature > 100) {
                    if ((this.mFluid == null) || (!GT_ModHandler.isWater(this.mFluid)) || (this.mFluid.amount <= 0)) {
                        this.mHadNoWater = true;
                    } else {
                        if (this.mHadNoWater) {
                            aBaseMetaTileEntity.doExplosion(2048L);
                            return;
                        }
                        this.mFluid.amount -= 1;
                        if (this.mSteam == null) {
                            this.mSteam = GT_ModHandler.getSteam(150L);
                        } else if (GT_ModHandler.isSteam(this.mSteam)) {
                            this.mSteam.amount += 150;
                        } else {
                            this.mSteam = GT_ModHandler.getSteam(150L);
                        }
                    }
                } else {
                    this.mHadNoWater = false;
                }
            }
            if ((this.mSteam != null) &&
                    (this.mSteam.amount > 16000)) {
                sendSound((byte) 1);
                this.mSteam.amount = 12000;
            }
            
            //Check the boiler has not been choked by output and can keep burning fuel
            boolean byproductStuffed = false;
            
            ItemStack byproductStack = aBaseMetaTileEntity.getStackInSlot(3);
            
            if(byproductStack != null && !(GT_Utility.isStackInvalid(byproductStack)) && byproductStack.stackSize == byproductStack.getMaxStackSize()) {
                byproductStuffed = true;
            }
            
            if ((this.mProcessingEnergy <= 0) && (aBaseMetaTileEntity.isAllowedToWork()) &&
                    (this.mInventory[2] != null) && !byproductStuffed) {
                int fuelEnergy = 0, byproductChance = 1;
                Object byproduct = Materials.Ash;
                boolean validFuel = false;
                if ((GT_OreDictUnificator.isItemStackInstanceOf(this.mInventory[2], OrePrefixes.gem.get(Materials.Coal))) || 
                    (GT_OreDictUnificator.isItemStackInstanceOf(this.mInventory[2], OrePrefixes.dust.get(Materials.Coal))) || 
                    (GT_OreDictUnificator.isItemStackInstanceOf(this.mInventory[2], OrePrefixes.dustImpure.get(Materials.Coal))) || 
                    (GT_OreDictUnificator.isItemStackInstanceOf(this.mInventory[2], OrePrefixes.crushed.get(Materials.Coal)))) {
                    fuelEnergy = 160;
                    byproductChance = 3;
                    byproduct = Materials.DarkAsh;
                    validFuel = true;
                } else if ((GT_OreDictUnificator.isItemStackInstanceOf(this.mInventory[2], OrePrefixes.gem.get(Materials.Charcoal))) || 
                           (GT_OreDictUnificator.isItemStackInstanceOf(this.mInventory[2], OrePrefixes.dust.get(Materials.Charcoal))) || 
                           (GT_OreDictUnificator.isItemStackInstanceOf(this.mInventory[2], OrePrefixes.dustImpure.get(Materials.Charcoal))) || 
                           (GT_OreDictUnificator.isItemStackInstanceOf(this.mInventory[2], OrePrefixes.crushed.get(Materials.Charcoal)))) {
                    fuelEnergy = 160;
                    byproductChance = 3;
                    byproduct = Materials.Ash;
                    validFuel = true;
                } else if (GT_OreDictUnificator.isItemStackInstanceOf(this.mInventory[2], "fuelCoke")) {
                    fuelEnergy = 640;
                    byproductChance = 2;
                    byproduct = Materials.Ash;
                    validFuel = true;
                } else if ((GT_OreDictUnificator.isItemStackInstanceOf(this.mInventory[2], OrePrefixes.gem.get(Materials.Lignite))) || 
                           (GT_OreDictUnificator.isItemStackInstanceOf(this.mInventory[2], OrePrefixes.dust.get(Materials.Lignite))) || 
                           (GT_OreDictUnificator.isItemStackInstanceOf(this.mInventory[2], OrePrefixes.dustImpure.get(Materials.Lignite))) || 
                           (GT_OreDictUnificator.isItemStackInstanceOf(this.mInventory[2], OrePrefixes.crushed.get(Materials.Lignite)))) {
                    fuelEnergy = 120;
                    byproductChance = 8;
                    byproduct = Materials.DarkAsh;
                    validFuel = true;
                }
                
                if(validFuel) {
                    this.mProcessingEnergy += fuelEnergy;
                    aBaseMetaTileEntity.decrStackSize(2, 1);
                    if (aBaseMetaTileEntity.getRandomNumber(byproductChance) == 0) {
                        if(!aBaseMetaTileEntity.addStackToSlot(3, GT_OreDictUnificator.get(OrePrefixes.dustTiny, byproduct, 1L))) {
                            //We can only get here if the output wasn't stuffed, so if the add fails, it can only be because the byproduct types don't match
                            //Have attempted to mix ash types, so add one to the size of the stack and convert all ash to light ashes
                            aBaseMetaTileEntity.setInventorySlotContents(3, GT_OreDictUnificator.get(OrePrefixes.dustTiny, Materials.Ash, byproductStack.stackSize + 1));
                        }
                    }
                }
            }
            if ((this.mTemperature < 500) && (this.mProcessingEnergy > 0) && (aTick % 12L == 0L)) {
                this.mProcessingEnergy -= 1;
                this.mTemperature += 1;
            }
            if (this.mProcessingEnergy > 0 && (aTick % 20L == 0L)) {
                GT_Pollution.addPollution(getBaseMetaTileEntity(), 20);
            }
            aBaseMetaTileEntity.setActive(this.mProcessingEnergy > 0);
        }
    }
}
