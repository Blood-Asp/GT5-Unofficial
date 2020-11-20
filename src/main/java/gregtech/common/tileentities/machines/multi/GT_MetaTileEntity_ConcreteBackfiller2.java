package gregtech.common.tileentities.machines.multi;

import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;

import static gregtech.api.enums.GT_Values.VN;

import org.lwjgl.input.Keyboard;

public class GT_MetaTileEntity_ConcreteBackfiller2 extends GT_MetaTileEntity_ConcreteBackfillerBase {
    public GT_MetaTileEntity_ConcreteBackfiller2(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_ConcreteBackfiller2(String aName) {
        super(aName);
    }

    @Override
    public String[] getDescription() {
        String casings = getCasingBlockItem().get(0).getDisplayName();
        
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
		tt.addMachineType("Concrete Backfiller")
		.addInfo("Controller Block for the Advanced Concrete Backfiller")
		.addInfo("Will fill in areas below it with light concrete. This goes through walls")
		.addInfo("Use it to remove any spawning locations beneath your base to reduce lag")
		.addInfo("Will pull back the pipes after it finishes that layer")
		.addInfo("Put Programmed Circuits into a Data Access Hatch to config radius. Buggy")
		.addInfo("Radius = (total config value)x2 blocks. Default 64, Maximum 128")//broken
		.addSeparator()
		.beginStructureBlock(3, 7, 3, false)
		.addController("Front bottom")
		.addStructureInfo(casings + " form the 3x1x3 Base")
		.addOtherStructurePart(casings, " 1x3x1 pillar above the center of the base (2 minimum total)")
		.addOtherStructurePart(getFrameMaterial().mName + " Frame Boxes", "Each pillar's side and 1x3x1 on top")
		.addEnergyHatch(VN[getMinTier()] + "+, Any base casing")
		.addMaintenanceHatch("Any base casing")
		.addStructureInfo("Data Access Hatch: Any base casing")
		.addInputBus("Mining Pipes, optional, any base casing")
		.addInputHatch("GT Concrete, any base casing")
		.addOutputBus("Mining Pipes, optional, any base casing")
		.toolTipFinisher("Gregtech");
		if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			return tt.getInformation();
		} else {
			return tt.getStructureInformation();
		}
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_ConcreteBackfiller2(mName);
    }

    @Override
    protected ItemList getCasingBlockItem() {
        return ItemList.Casing_StableTitanium;
    }

    @Override
    protected Materials getFrameMaterial() {
        return Materials.Titanium;
    }

    @Override
    protected int getCasingTextureIndex() {
        return 50;
    }

    @Override
    protected int getRadius() {
        int tConfig = getTotalConfigValue() * 2;
        return tConfig >= 128 ? 128 : tConfig <= 0 ? 64 : tConfig;
    }

    @Override
    protected int getMinTier() {
        return 4;
    }
}