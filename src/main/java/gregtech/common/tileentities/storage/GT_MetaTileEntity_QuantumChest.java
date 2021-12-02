package gregtech.common.tileentities.storage;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import net.minecraft.item.ItemStack;

public class GT_MetaTileEntity_QuantumChest extends GT_MetaTileEntity_DigitalChestBase {
    public int mItemCount = 0;
    public ItemStack mItemStack = null;

    public GT_MetaTileEntity_QuantumChest(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier);
    }

    public GT_MetaTileEntity_QuantumChest(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_QuantumChest(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    protected String chestName() {
        return "Quantum Chest";
    }

    @Override
    public int getItemCount() {
        return mItemCount;
    }

    @Override
    public void setItemCount(int aCount) {
        mItemCount = aCount;
    }

    @Override
    public ItemStack getItemStack() {
        return mItemStack;
    }

    @Override
    public void setItemStack(ItemStack s) {
        mItemStack = s;
    }
}
