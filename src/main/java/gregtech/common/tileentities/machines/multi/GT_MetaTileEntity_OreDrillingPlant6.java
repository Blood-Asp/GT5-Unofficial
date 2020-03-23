package gregtech.common.tileentities.machines.multi;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

public class GT_MetaTileEntity_OreDrillingPlant6 extends GT_MetaTileEntity_OreDrillingPlantBase {
    public GT_MetaTileEntity_OreDrillingPlant6(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        mTier=5;
    }

    public GT_MetaTileEntity_OreDrillingPlant6(String aName) {
        super(aName);
        mTier=5;
    }

    @Override
    public String[] getDescription() {
        return getDescriptionInternal("ZPM");
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_OreDrillingPlant6(mName);
    }

    @Override
    protected ItemList getCasingBlockItem() {
        return ItemList.Casing_MiningBlackPlutonium;
    }

    @Override
    protected Materials getFrameMaterial() {
        return Materials.BlackPlutonium;
    }

    @Override
    protected int getCasingTextureIndex() {
        return 51;
    }

    @Override
    protected int getRadiusInChunks() {
        return 15;
    }

    @Override
    protected int getMinTier() {
        return 7;
    }

    @Override
    protected int getBaseProgressTime() {
        return 160;
    }
}
