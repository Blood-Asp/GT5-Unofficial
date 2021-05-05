package gregtech.common.tileentities.boilers;

import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.objects.GT_RenderedGlowTexture;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.common.gui.GT_Container_Boiler;
import gregtech.common.gui.GT_GUIContainer_Boiler;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraftforge.fluids.FluidStack;

import static gregtech.api.enums.Textures.BlockIcons.BOILER_LAVA_FRONT;
import static gregtech.api.enums.Textures.BlockIcons.BOILER_LAVA_FRONT_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.BOILER_LAVA_FRONT_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.MACHINE_STEELBRICKS_BOTTOM;
import static gregtech.api.enums.Textures.BlockIcons.MACHINE_STEELBRICKS_SIDE;
import static gregtech.api.enums.Textures.BlockIcons.MACHINE_STEELBRICKS_TOP;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_PIPE;

public class GT_MetaTileEntity_Boiler_Lava extends GT_MetaTileEntity_Boiler {
    public GT_MetaTileEntity_Boiler_Lava(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, new String[]{
                "A Boiler running off Lava",
                "Produces 600L of Steam per second",
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
        final ITexture[]
                texBottom = {new GT_RenderedTexture(MACHINE_STEELBRICKS_BOTTOM)},
                texTop = {new GT_RenderedTexture(MACHINE_STEELBRICKS_TOP), new GT_RenderedTexture(OVERLAY_PIPE)},
                texSide = {new GT_RenderedTexture(MACHINE_STEELBRICKS_SIDE), new GT_RenderedTexture(OVERLAY_PIPE)},
                texFront = {
                        new GT_RenderedTexture(MACHINE_STEELBRICKS_SIDE),
                        new GT_RenderedTexture(BOILER_LAVA_FRONT)},
                texFrontActive = {
                        new GT_RenderedTexture(MACHINE_STEELBRICKS_SIDE),
                        new GT_RenderedTexture(BOILER_LAVA_FRONT_ACTIVE),
                        new GT_RenderedGlowTexture(BOILER_LAVA_FRONT_ACTIVE_GLOW)};
        for (byte i = 0; i < 17; i++) {
            rTextures[0][i] = texBottom;
            rTextures[1][i] = texTop;
            rTextures[2][i] = texSide;
            rTextures[3][i] = texFront;
            rTextures[4][i] = texFrontActive;
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

    @Override
    protected int getPollution() {
        return 20;
    }

    @Override
    protected int getProductionPerSecond() {
        return 600;
    }

    @Override
    protected int getMaxTemperature() {
        return 1000;
    }

    @Override
    protected int getEnergyConsumption() {
        return 3;
    }

    @Override
    protected int getCooldownInterval() {
        return 20;
    }

    @Override
    protected void updateFuel(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        if (GT_OreDictUnificator.isItemStackInstanceOf(this.mInventory[2], OrePrefixes.bucket.get(Materials.Lava))) {
            this.mProcessingEnergy += 1000;
            aBaseMetaTileEntity.decrStackSize(2, 1);
            aBaseMetaTileEntity.addStackToSlot(3, GT_OreDictUnificator.get(OrePrefixes.bucket, Materials.Empty, 1L));
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
