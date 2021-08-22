package gregtech.common.tileentities.machines.multi;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;

public class GT_MetaTileEntity_OilDrill3 extends GT_MetaTileEntity_OilDrillBase {
    public GT_MetaTileEntity_OilDrill3(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_OilDrill3(String aName) {
        super(aName);
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        return createTooltip("III");
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_OilDrill3(mName);
    }

    @Override
    protected ItemList getCasingBlockItem() {
        return ItemList.Casing_RobustTungstenSteel;
    }

    @Override
    protected Materials getFrameMaterial() {
        return Materials.TungstenSteel;
    }

    @Override
    protected int getCasingTextureIndex() {
        return 48;
    }

    @Override
    protected int getRangeInChunks() {
        return 8;
    }

    @Override
    protected int getMinTier() {
        return 4;
    }
}
