package gregtech.common.tileentities.machines.multi;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;

public class GT_MetaTileEntity_OreDrillingPlant1 extends GT_MetaTileEntity_OreDrillingPlantBase {
    public GT_MetaTileEntity_OreDrillingPlant1(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_OreDrillingPlant1(String aName) {
        super(aName);
    }

    @Override
    public String[] getDescription() {
        return getDescriptionInternal("I");
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_OreDrillingPlant1(mName);
    }

    @Override
    protected ItemList getCasingBlockItem() {
        return ItemList.Casing_SolidSteel;
    }

    @Override
    protected Materials getFrameMaterial() {
        return Materials.Steel;
    }

    @Override
    protected int getCasingTextureIndex() {
        return 16;
    }

    @Override
    protected int getRadiusInChunks() {
        return 3;
    }

    @Override
    protected int getMinTier() {
        return 2;
    }

    @Override
    protected int getBaseProgressTime() {
        return 960;
    }
}
