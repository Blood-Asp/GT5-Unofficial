package gregtech.common.tileentities.boilers;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.objects.GT_RenderedGlowTexture;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.common.gui.GT_Container_Boiler;
import gregtech.common.gui.GT_GUIContainer_Boiler;
import net.minecraft.entity.player.InventoryPlayer;

import static gregtech.api.enums.Textures.BlockIcons.BOILER_FRONT;
import static gregtech.api.enums.Textures.BlockIcons.BOILER_FRONT_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.BOILER_FRONT_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.MACHINE_STEELBRICKS_BOTTOM;
import static gregtech.api.enums.Textures.BlockIcons.MACHINE_STEELBRICKS_SIDE;
import static gregtech.api.enums.Textures.BlockIcons.MACHINE_STEELBRICKS_TOP;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_PIPE;

public class GT_MetaTileEntity_Boiler_Steel extends GT_MetaTileEntity_Boiler_Bronze {


    public GT_MetaTileEntity_Boiler_Steel(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, new String[]{
                "Faster than the Bronze Boiler",
                "Produces 300L of Steam per second",
                "Causes 30 Pollution per second"});
    }

    public GT_MetaTileEntity_Boiler_Steel(String aName, int aTier, String aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    public GT_MetaTileEntity_Boiler_Steel(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    public ITexture[][][] getTextureSet(ITexture[] aTextures) {
        ITexture[][][] rTextures = new ITexture[5][17][];
        final ITexture[]
                texBottom = {new GT_RenderedTexture(MACHINE_STEELBRICKS_BOTTOM)},
                texTop = {new GT_RenderedTexture(MACHINE_STEELBRICKS_TOP), new GT_RenderedTexture(OVERLAY_PIPE)},
                texSide = {new GT_RenderedTexture(MACHINE_STEELBRICKS_SIDE), new GT_RenderedTexture(OVERLAY_PIPE)},
                texFront = {new GT_RenderedTexture(MACHINE_STEELBRICKS_SIDE), new GT_RenderedTexture(BOILER_FRONT)},
                texFrontActive = {
                        new GT_RenderedTexture(MACHINE_STEELBRICKS_SIDE),
                        new GT_RenderedTexture(BOILER_FRONT_ACTIVE),
                        new GT_RenderedGlowTexture(BOILER_FRONT_ACTIVE_GLOW)};
        for (int i = 0; i < 17; i++) {
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
        return new GT_MetaTileEntity_Boiler_Steel(this.mName, this.mTier, this.mDescriptionArray, this.mTextures);
    }

    @Override
    protected int getPollution() {
        return 30;
    }

    @Override
    public int getCapacity() {
        return 32000;
    }

    @Override
    protected int getProductionPerSecond() {
        return 300;
    }

    @Override
    protected int getMaxTemperature() {
        return 1000;
    }

    @Override
    protected int getEnergyConsumption() {
        return 2;
    }

    @Override
    protected int getCooldownInterval() {
        return 40;
    }
}
