package gregtech.common.tileentities.machines.basic;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Transformer;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.common.gui.GT_Container_DigitalTransformer;
import gregtech.common.gui.GT_GUIContainer_DigitalTransformer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import static gregtech.api.enums.GT_Values.V;


public class GT_MetaTileEntity_Digital_Transformer extends GT_MetaTileEntity_Transformer {
    public int oTier, oAmp, iAmp;

    public GT_MetaTileEntity_Digital_Transformer(int aID, String aName, String aNameRegional, int aTier, String aDescription) {
        super(aID, aName, aNameRegional, aTier, aDescription);
    }

    public GT_MetaTileEntity_Digital_Transformer(String aName, int aTier, String aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_Digital_Transformer(mName, mTier, mDescription, mTextures);
    }

    public ITexture[][][] getTextureSet(ITexture[] aTextures) {
        ITexture[][][] rTextures = new ITexture[12][17][];
        for (byte i = -1; i < 16; i++) {
            rTextures[0][i + 1] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.DIGITAL_TRANSFORMER[mTier]), Textures.BlockIcons.OVERLAYS_ENERGY_OUT[mTier]};
            rTextures[1][i + 1] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.DIGITAL_TRANSFORMER[mTier]), Textures.BlockIcons.OVERLAYS_ENERGY_OUT[mTier]};
            rTextures[2][i + 1] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.DIGITAL_TRANSFORMER[mTier]), Textures.BlockIcons.OVERLAYS_ENERGY_OUT[mTier]};
            rTextures[3][i + 1] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.DIGITAL_TRANSFORMER[mTier]), Textures.BlockIcons.OVERLAYS_ENERGY_IN_MULTI[mTier]};
            rTextures[4][i + 1] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.DIGITAL_TRANSFORMER[mTier]), Textures.BlockIcons.OVERLAYS_ENERGY_IN_MULTI[mTier]};
            rTextures[5][i + 1] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.DIGITAL_TRANSFORMER[mTier]), Textures.BlockIcons.OVERLAYS_ENERGY_IN_MULTI[mTier]};
            rTextures[6][i + 1] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.DIGITAL_TRANSFORMER[mTier]), Textures.BlockIcons.OVERLAYS_ENERGY_IN[mTier]};
            rTextures[7][i + 1] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.DIGITAL_TRANSFORMER[mTier]), Textures.BlockIcons.OVERLAYS_ENERGY_IN[mTier]};
            rTextures[8][i + 1] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.DIGITAL_TRANSFORMER[mTier]), Textures.BlockIcons.OVERLAYS_ENERGY_IN[mTier]};
            rTextures[9][i + 1] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.DIGITAL_TRANSFORMER[mTier]), Textures.BlockIcons.OVERLAYS_ENERGY_OUT_MULTI[mTier]};
            rTextures[10][i + 1] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.DIGITAL_TRANSFORMER[mTier]), Textures.BlockIcons.OVERLAYS_ENERGY_OUT_MULTI[mTier]};
            rTextures[11][i + 1] = new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.DIGITAL_TRANSFORMER[mTier]), Textures.BlockIcons.OVERLAYS_ENERGY_OUT_MULTI[mTier]};
        }
        return rTextures;
    }

    @Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_Container_DigitalTransformer(aPlayerInventory, aBaseMetaTileEntity);
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_DigitalTransformer(aPlayerInventory, aBaseMetaTileEntity);
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity iGregTechTileEntity, int i, byte b, ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity iGregTechTileEntity, int i, byte b, ItemStack itemStack) {
        return false;
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        aNBT.setInteger("tier", oTier);
        aNBT.setInteger("oAmp", oAmp);
        aNBT.setInteger("iAmp", iAmp);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        oTier = aNBT.getInteger("tier");
        oAmp = aNBT.getInteger("oAmp");
        iAmp = aNBT.getInteger("iAmp");
    }

    @Override
    public long maxEUInput() {
        return V[mTier];
    }

    @Override
    public long maxEUOutput() {
        return V[oTier];
    }

    @Override
    public long maxAmperesOut() {
        return oAmp;
    }

    @Override
    public long maxAmperesIn() {
        return iAmp;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) {
            return true;
        }
        aBaseMetaTileEntity.openGUI(aPlayer);
        return true;
    }

    @Override
    public String[] getDescription() {
        return new String[]{"Accepts " + V[mTier] + "V"};
    }

    @Override
    public boolean isElectric() {
        return true;
    }

    @Override
    public long maxEUStore() {
        return 512L + V[mTier + 1] * 64L;
    }
}
