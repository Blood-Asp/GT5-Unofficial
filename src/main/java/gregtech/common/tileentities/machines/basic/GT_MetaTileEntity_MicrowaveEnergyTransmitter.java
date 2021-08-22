package gregtech.common.tileentities.machines.basic;

import gregtech.api.GregTech_API;
import gregtech.api.enums.ConfigCategories;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IEnergyConnected;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.BaseMetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicTank;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Config;
import gregtech.api.util.GT_Utility;
import gregtech.common.gui.GT_Container_MicrowaveEnergyTransmitter;
import gregtech.common.gui.GT_GUIContainer_MicrowaveEnergyTransmitter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.FluidStack;

import static gregtech.api.enums.GT_Values.V;
import static gregtech.api.enums.Textures.BlockIcons.MACHINE_CASINGS;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_TELEPORTER;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_TELEPORTER_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_TELEPORTER_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_TELEPORTER_GLOW;

public class GT_MetaTileEntity_MicrowaveEnergyTransmitter extends GT_MetaTileEntity_BasicTank {

    private static boolean sInterDimensionalTeleportAllowed = true;
    private static int mMaxLoss = 50;
    private static int mMaxLossDistance = 10000;
    private static boolean mPassiveEnergyUse = true;
    public int mTargetX = 0;
    public int mTargetY = 0;
    public int mTargetZ = 0;
    public int mTargetD = 0;
    public boolean mDebug = false;
    public boolean hasBlock = false;
    public int tTargetX = 0;
    public int tTargetY = 0;
    public int tTargetZ = 0;
    public int tTargetD = 0;
    public TileEntity tTile = null;

    public GT_MetaTileEntity_MicrowaveEnergyTransmitter(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 3, new String[]{"Transmits Energy Wirelessly", "Use Nitrogen Plasma", "for Inter-dimensional transmission", "0.004EU Loss per 100 Blocks"});
    }

    public GT_MetaTileEntity_MicrowaveEnergyTransmitter(String aName, int aTier, String aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 3, aDescription, aTextures);
    }

    public GT_MetaTileEntity_MicrowaveEnergyTransmitter(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 3, aDescription, aTextures);
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) return true;
        this.hasBlock = checkForBlock();
        aBaseMetaTileEntity.openGUI(aPlayer);
        return true;
    }

    @Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_Container_MicrowaveEnergyTransmitter(aPlayerInventory, aBaseMetaTileEntity);
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MicrowaveEnergyTransmitter(aPlayerInventory, aBaseMetaTileEntity);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_MicrowaveEnergyTransmitter(this.mName, this.mTier, this.mDescriptionArray, this.mTextures);
    }

    @Override
    public String[] getInfoData() {
        return new String[]{
                "Coordinates:",
                "X: " + EnumChatFormatting.GREEN + GT_Utility.formatNumbers(this.mTargetX) + EnumChatFormatting.RESET,
                "Y: " + EnumChatFormatting.GREEN + GT_Utility.formatNumbers(this.mTargetY) + EnumChatFormatting.RESET,
                "Z: " + EnumChatFormatting.GREEN + GT_Utility.formatNumbers(this.mTargetZ) + EnumChatFormatting.RESET,
                "Dimension: " + EnumChatFormatting.GREEN+this.mTargetD+EnumChatFormatting.RESET,
                "Dimension Valid: " + (GT_Utility.isRealDimension(this.mTargetD) ? EnumChatFormatting.GREEN+"Yes"+EnumChatFormatting.RESET : EnumChatFormatting.RED+"No"+EnumChatFormatting.RESET),
                "Dimension Registered: " + (DimensionManager.isDimensionRegistered(this.mTargetD) ? EnumChatFormatting.GREEN+"Yes"+EnumChatFormatting.RESET : EnumChatFormatting.RED+"No"+EnumChatFormatting.RESET)
        };
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == 0) return new ITexture[]{MACHINE_CASINGS[mTier][aColorIndex + 1]};
        if (aActive) return new ITexture[]{
                MACHINE_CASINGS[mTier][aColorIndex + 1],
                TextureFactory.of(OVERLAY_TELEPORTER_ACTIVE),
                TextureFactory.builder().addIcon(OVERLAY_TELEPORTER_ACTIVE_GLOW).glow().build()};
        return new ITexture[]{
                MACHINE_CASINGS[mTier][aColorIndex + 1],
                TextureFactory.of(OVERLAY_TELEPORTER),
                TextureFactory.builder().addIcon(OVERLAY_TELEPORTER_GLOW).glow().build()};
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        if (mFluid != null) aNBT.setTag("mFluid", mFluid.writeToNBT(new NBTTagCompound()));
        aNBT.setInteger("mTargetX", this.mTargetX);
        aNBT.setInteger("mTargetY", this.mTargetY);
        aNBT.setInteger("mTargetZ", this.mTargetZ);
        aNBT.setInteger("mTargetD", this.mTargetD);
        aNBT.setBoolean("mDebug", this.mDebug);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        mFluid = FluidStack.loadFluidStackFromNBT(aNBT.getCompoundTag("mFluid"));
        this.mTargetX = aNBT.getInteger("mTargetX");
        this.mTargetY = aNBT.getInteger("mTargetY");
        this.mTargetZ = aNBT.getInteger("mTargetZ");
        this.mTargetD = aNBT.getInteger("mTargetD");
        this.mDebug = aNBT.getBoolean("mDebug");
    }

    @Override
    public void onConfigLoad(GT_Config aConfig) {
        sInterDimensionalTeleportAllowed = aConfig.get(ConfigCategories.machineconfig, "Teleporter.Interdimensional", true);
        mMaxLoss = Math.max(aConfig.get(ConfigCategories.machineconfig, "MicrowaveTransmitter.MaxLoss", 50), 11);
        mMaxLossDistance = aConfig.get(ConfigCategories.machineconfig, "MicrowaveTransmitter.MaxLossDistance", 10000);
        mPassiveEnergyUse = aConfig.get(ConfigCategories.machineconfig, "MicrowaveTransmitter.PassiveEnergy", true);
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        if (aBaseMetaTileEntity.isServerSide()) {
            if ((this.mTargetX == 0) && (this.mTargetY == 0) && (this.mTargetZ == 0) && (this.mTargetD == 0)) {
                this.mTargetX = aBaseMetaTileEntity.getXCoord();
                this.mTargetY = aBaseMetaTileEntity.getYCoord();
                this.mTargetZ = aBaseMetaTileEntity.getZCoord();
                this.mTargetD = aBaseMetaTileEntity.getWorld().provider.dimensionId;
            }
            this.hasBlock = checkForBlock();
        }
    }

    public boolean checkForBlock() {
        for (byte i = -5; i <= 5; i = (byte) (i + 1)) {
            for (byte j = -5; j <= 5; j = (byte) (j + 1)) {
                for (byte k = -5; k <= 5; k = (byte) (k + 1)) {
                    if (getBaseMetaTileEntity().getBlockOffset(i, j, k) ==  GregTech_API.sBlockMetal5 && getBaseMetaTileEntity().getMetaIDOffset(i, j, k) == 8) {//require osmiridium block
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean hasDimensionalTeleportCapability() {
        return this.mDebug ||
                (
                        sInterDimensionalTeleportAllowed &&
                                (
                                        this.hasBlock ||
                                                mFluid != null && mFluid.isFluidEqual(Materials.Nitrogen.getPlasma(1)) && mFluid.amount >= 1000

                                )
                )
        ;
    }

    public boolean isDimensionalTeleportAvailable() {
        return this.mDebug || (hasDimensionalTeleportCapability() && GT_Utility.isRealDimension(this.mTargetD) && GT_Utility.isRealDimension(getBaseMetaTileEntity().getWorld().provider.dimensionId));
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (mFluid == null) {
            mFluid = Materials.Nitrogen.getPlasma(0);
        }
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (getBaseMetaTileEntity().isServerSide()) {
            if (getBaseMetaTileEntity().getTimer() % 100L == 50L) {
                this.hasBlock = checkForBlock();
            }
            if ((getBaseMetaTileEntity().isAllowedToWork()) && (getBaseMetaTileEntity().getRedstone())) {
                if (getBaseMetaTileEntity().getStoredEU() > (V[mTier] * 16)) {
                    if (mPassiveEnergyUse) {
                        getBaseMetaTileEntity().decreaseStoredEnergyUnits(2L<<(mTier-1), false);
                    }
                    if (hasDimensionalTeleportCapability() && this.mTargetD != getBaseMetaTileEntity().getWorld().provider.dimensionId && mFluid.isFluidEqual(Materials.Nitrogen.getPlasma(1))) {
                        mFluid.amount--;
                        if (mFluid.amount < 1) {
                            mFluid = null;
                        }
                    }
                    if (tTargetD != mTargetD || tTargetX != mTargetX || tTargetY != mTargetY || tTargetZ != mTargetZ) {
                        tTargetD = mTargetD;
                        tTargetX = mTargetX;
                        tTargetY = mTargetY;
                        tTargetZ = mTargetZ;
                        if (this.mTargetD == getBaseMetaTileEntity().getWorld().provider.dimensionId) {
                            tTile = getBaseMetaTileEntity().getTileEntity(this.mTargetX, this.mTargetY, this.mTargetZ);
                        } else {
                            World tWorld = DimensionManager.getWorld(this.mTargetD);
                            if (tWorld != null) {
                                tTile = tWorld.getTileEntity(this.mTargetX, this.mTargetY, this.mTargetZ);
                            }
                        }
                    }
                    int tDistance = distanceCalculation();
                    if(tTile!=null) {
                        if (tTile instanceof IEnergyConnected) {
                            long packetSize=V[mTier];
                            if(tTile instanceof IGregTechTileEntity){
                                IMetaTileEntity mte=((IGregTechTileEntity) tTile).getMetaTileEntity();
                                if(mte instanceof BaseMetaTileEntity) {
                                    packetSize=((BaseMetaTileEntity) mte).getMaxSafeInput();
                                }
                            }
                            long energyUse = 10;
                            if (mMaxLossDistance != 0) {
                                energyUse = GT_Utility.safeInt(10L + (tDistance * Math.max(mMaxLoss - 10L, 0) / mMaxLossDistance));
                            }
                            energyUse=packetSize + ((V[mTier] * energyUse) / 100);
                            if (getBaseMetaTileEntity().isUniversalEnergyStored(energyUse)) {
                                if (((IEnergyConnected) tTile).injectEnergyUnits((byte) 6, packetSize, 1) > 0) {
                                    getBaseMetaTileEntity().decreaseStoredEnergyUnits(energyUse, false);
                                }
                            }
                        }
                    }
                }
                getBaseMetaTileEntity().setActive(true);
            } else {
                getBaseMetaTileEntity().setActive(false);
            }
        }
    }

    private int distanceCalculation() {
        return Math.abs(((this.mTargetD != getBaseMetaTileEntity().getWorld().provider.dimensionId) && (isDimensionalTeleportAvailable()) ? 100 : 1) * (int) Math.sqrt(Math.pow(getBaseMetaTileEntity().getXCoord() - this.mTargetX, 2.0D) + Math.pow(getBaseMetaTileEntity().getYCoord() - this.mTargetY, 2.0D) + Math.pow(getBaseMetaTileEntity().getZCoord() - this.mTargetZ, 2.0D)));
    }

    @Override
    public boolean isSimpleMachine() {
        return false;
    }

    @Override
    public boolean isOverclockerUpgradable() {
        return false;
    }

    @Override
    public boolean isTransformerUpgradable() {
        return false;
    }

    @Override
    public boolean isElectric() {
        return true;
    }

    @Override
    public boolean isFacingValid(byte aFacing) {
        return true;
    }

    @Override
    public boolean isEnetInput() {
        return true;
    }

    @Override
    public boolean isInputFacing(byte aSide) {
        return true;
    }

    @Override
    public boolean isOutputFacing(byte aSide) {
        return false;
    }

    @Override
    public boolean isTeleporterCompatible() {
        return false;
    }

    @Override
    public long getMinimumStoredEU() {
        return V[mTier] * 16;
    }

    @Override
    public long maxEUStore() {
        return V[mTier] * 256;
    }

    @Override
    public long maxEUInput() {
        return V[mTier];
    }

    @Override
    public long maxSteamStore() {
        return maxEUStore();
    }

    @Override
    public long maxAmperesIn() {
        return 3;
    }

    @Override
    public int getStackDisplaySlot() {
        return 2;
    }

    @Override
    public boolean isAccessAllowed(EntityPlayer aPlayer) {
        return true;
    }

    @Override
    public int getInputSlot() {
        return 0;
    }

    @Override
    public int getOutputSlot() {
        return 0;
    }

    @Override
    public int getCapacity() {
        return 64000;
    }

    @Override
    public boolean doesFillContainers() {
        return false;
    }

    @Override
    public boolean doesEmptyContainers() {
        return false;
    }

    @Override
    public boolean canTankBeFilled() {
        return true;
    }

    @Override
    public boolean canTankBeEmptied() {
        return true;
    }

    @Override
    public boolean displaysItemStack() {
        return false;
    }

    @Override
    public boolean displaysStackSize() {
        return false;
    }

    @Override
    public ITexture[][][] getTextureSet(ITexture[] aTextures) {
        return null;
    }

}
