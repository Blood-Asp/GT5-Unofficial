package gregtech.common.tileentities.machines.multi;

import gregtech.api.GregTech_API;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.render.GT_MultiTexture;
import gregtech.api.render.GT_RenderedGlowTexture;
import gregtech.api.render.GT_RenderedTexture;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import net.minecraft.block.Block;
import org.lwjgl.input.Keyboard;

import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FUSION2;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FUSION2_GLOW;

public class GT_MetaTileEntity_FusionComputer2 extends GT_MetaTileEntity_FusionComputer {

    private static final ITexture textureOverlay = new GT_MultiTexture(
            new GT_RenderedTexture(OVERLAY_FUSION2),
            new GT_RenderedGlowTexture(OVERLAY_FUSION2_GLOW));

    public GT_MetaTileEntity_FusionComputer2(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 6);
    }

    public GT_MetaTileEntity_FusionComputer2(String aName) {
        super(aName);
    }

    @Override
    public int tier() {
        return 7;
    }

    @Override
    public long maxEUStore() {
        return 320006000L * (Math.min(16, this.mEnergyHatches.size())) / 16L;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_FusionComputer2(mName);
    }

    @Override
    public Block getCasing() {
        return GregTech_API.sBlockCasings4;
    }

    @Override
    public int getCasingMeta() {
        return 6;
    }

    @Override
    public Block getFusionCoil() {
        return GregTech_API.sBlockCasings4;
    }

    @Override
    public int getFusionCoilMeta() {
        return 7;
    }

    @Override
    public String[] getDescription() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Fusion Reactor")
                .addInfo("It's over 9000!!!")
                .addInfo("Controller block for the Fusion Reactor Mk II")
                .addInfo("4096EU/t and 20M EU capacity per Energy Hatch")
                .addInfo("If the recipe has a startup cost greater than the")
                .addInfo("number of energy hatches * cap, you can't do it")
                .addSeparator()
                .beginStructureBlock(15, 3, 15, false)
                .addController("See diagram when placed")
                .addCasingInfo("Fusion Machine Casing", 79)
                .addStructureInfo("Cover the coils with casing")
                .addOtherStructurePart("Fusion Coil Block", "Center part of the ring")
                .addEnergyHatch("1-16, Specified casings")
                .addInputHatch("2-16, Specified casings")
                .addOutputHatch("1-16, Specified casings")
                .addStructureInfo("ALL Hatches must be ZPM or better")
                .toolTipFinisher("Gregtech");
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            return tt.getStructureInformation();
        } else {
            return tt.getInformation();
        }
    }

    @Override
    public ITexture getTextureOverlay() {
        return textureOverlay;
    }

    @Override
    public int tierOverclock() {
        return 2;
    }
}
