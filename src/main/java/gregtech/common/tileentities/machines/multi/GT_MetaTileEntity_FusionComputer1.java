package gregtech.common.tileentities.machines.multi;

import org.lwjgl.input.Keyboard;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.IIconContainer;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import net.minecraft.block.Block;

public class GT_MetaTileEntity_FusionComputer1 extends GT_MetaTileEntity_FusionComputer {

    public GT_MetaTileEntity_FusionComputer1(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 6);
    }

    public GT_MetaTileEntity_FusionComputer1(String aName) {
        super(aName);
    }

    @Override
    public int tier() {
        return 6;
    }

    @Override
    public long maxEUStore() {
        return 160003000L * (Math.min(16, this.mEnergyHatches.size())) / 16L;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_FusionComputer1(mName);
    }

    @Override
    public int getCasingMeta() {
        return 6;
    }

    @Override
    public Block getFusionCoil() {
        return GregTech_API.sBlockCasings1;
    }

    @Override
    public int getFusionCoilMeta() {
        return 15;
    }

    public String[] getDescription() {
    	final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
		tt.addMachineType("Fusion Reactor")
		.addInfo("It's over 9000!!!")
		.addInfo("Controller block for the Fusion Reactor Mk I")
		.addInfo("2048EU/t and 10M EU capacity per Energy Hatch")
		.addInfo("If the recipe has a startup cost greater than the")
		.addInfo("number of energy hatches * cap, you can't do it")
		.addSeparator()
		.beginStructureBlock(15, 3, 15, false)
		.addController("See diagram when placed")
		.addCasingInfo("LuV Machine Casing", 79)
		.addStructureInfo("Cover the coils with casing")
		.addOtherStructurePart("Superconducting Coil Block", "Center part of the ring")
		.addEnergyHatch("1-16, Specified casings")
		.addInputHatch("2-16, Specified casings")
		.addOutputHatch("1-16, Specified casings")
		.addStructureInfo("ALL Hatches must be LuV or better")
		.toolTipFinisher("Gregtech");
		if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			return tt.getInformation();
		} else {
			return tt.getStructureInformation();
		}
    }

    @Override
    public int tierOverclock() {
        return 1;
    }

    @Override
    public Block getCasing() {
        return GregTech_API.sBlockCasings1;
    }

    @Override
    public IIconContainer getIconOverlay() {
        return Textures.BlockIcons.OVERLAY_FUSION1;
    }

}
