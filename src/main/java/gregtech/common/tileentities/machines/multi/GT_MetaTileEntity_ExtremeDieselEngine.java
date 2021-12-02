package gregtech.common.tileentities.machines.multi;

import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Dynamo;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Muffler;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_EXTREME_DIESEL_ENGINE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_EXTREME_DIESEL_ENGINE_ACTIVE;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_EXTREME_DIESEL_ENGINE_ACTIVE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_FRONT_EXTREME_DIESEL_ENGINE_GLOW;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;

public class GT_MetaTileEntity_ExtremeDieselEngine extends GT_MetaTileEntity_DieselEngine {

    public GT_MetaTileEntity_ExtremeDieselEngine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }
    
    public GT_MetaTileEntity_ExtremeDieselEngine(String aName) {
        super(aName);
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Combustion Generator")
                .addInfo("Controller block for the Extreme Combustion Engine")
                .addInfo("Supply High Octane Gasoline and 8000L of Lubricant per hour to run")
                .addInfo("Supply 320L/s of Liquid Oxygen to boost output (optional)")
                .addInfo("Default: Produces 8192EU/t at 100% fuel efficiency")
                .addInfo("Boosted: Produces 32768EU/t at 400% fuel efficiency")
                .addInfo("You need to wait for it to reach 400% to output full power")
                .addPollutionAmount(getPollutionPerSecond(null))
                .addSeparator()
                .beginStructureBlock(3, 3, 4, false)
                .addController("Front center")
                .addCasingInfo("Robust Tungstensteel Machine Casing", 16)
                .addOtherStructurePart("Titanium Gear Box Machine Casing", "Inner 2 blocks")
                .addOtherStructurePart("Extreme Engine Intake Machine Casing", "8x, ring around controller")
                .addStructureInfo("Extreme Engine Intake Casings must not be obstructed in front (only air blocks)")
                .addDynamoHatch("Back center", 2)
                .addMaintenanceHatch("One of the casings next to a Gear Box", 1)
                .addMufflerHatch("Top middle back, above the rear Gear Box", 1)
                .addInputHatch("HOG, next to a Gear Box", 1)
                .addInputHatch("Lubricant, next to a Gear Box", 1)
                .addInputHatch("Liquid Oxygen, optional, next to a Gear Box", 1)
                .toolTipFinisher("Gregtech");
        return tt;
    }

    @Override
    protected GT_Recipe.GT_Recipe_Map_Fuel getFuelMap() {
        return GT_Recipe.GT_Recipe_Map.sExtremeDieselFuels;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            if (aActive) return new ITexture[]{
                    casingTexturePages[0][60],
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_EXTREME_DIESEL_ENGINE_ACTIVE).extFacing().build(),
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_EXTREME_DIESEL_ENGINE_ACTIVE_GLOW).extFacing().glow().build()};
            return new ITexture[]{
                    casingTexturePages[0][60],
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_EXTREME_DIESEL_ENGINE).extFacing().build(),
                    TextureFactory.builder().addIcon(OVERLAY_FRONT_EXTREME_DIESEL_ENGINE_GLOW).extFacing().glow().build()};
        }
        return new ITexture[]{casingTexturePages[0][60]};
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "LargeExtremeDieselEngine.png");
    }

    @Override
    public Block getCasingBlock() {
        return GregTech_API.sBlockCasings4;
    }

    @Override
    public byte getCasingMeta() {
        return 0;
    }

    @Override
    public Block getIntakeBlock() {
        return GregTech_API.sBlockCasings8;
    }

    @Override
    public byte getIntakeMeta() {
        return 4;
    }

    @Override
    public Block getGearboxBlock() {
        return GregTech_API.sBlockCasings2;
    }

    @Override
    public byte getGearboxMeta() {
        return 4;
    }

    @Override
    public byte getCasingTextureIndex() {
        return 60;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_ExtremeDieselEngine(this.mName);
    }

    @Override
    protected int getNominalOutput() {
        return 8192;
    }

    @Override
    protected int getBoostFactor() {
        return 4;
    }

    @Override
    protected Materials getBooster() {
        return Materials.LiquidOxygen;
    }

    @Override
    protected int getAdditiveFactor() {
        return 8;
    }

    @Override
    protected int getEfficiencyIncrease() {
        return 20;
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return boostEu ? 40000 : 10000;
    }

    @Override
    public int getPollutionPerSecond(ItemStack aStack) {
        return GT_Mod.gregtechproxy.mPollutionExtremeCombustionEnginePerSecond;
    }

    @Override
    public String[] getInfoData() {
        int mPollutionReduction=0;
        for (GT_MetaTileEntity_Hatch_Muffler tHatch : mMufflerHatches) {
            if (isValidMetaTileEntity(tHatch)) {
                mPollutionReduction=Math.max(tHatch.calculatePollutionReduction(100),mPollutionReduction);
            }
        }

        long storedEnergy=0;
        long maxEnergy=0;
        for(GT_MetaTileEntity_Hatch_Dynamo tHatch : mDynamoHatches) {
            if (isValidMetaTileEntity(tHatch)) {
                storedEnergy+=tHatch.getBaseMetaTileEntity().getStoredEU();
                maxEnergy+=tHatch.getBaseMetaTileEntity().getEUCapacity();
            }
        }
      
        return new String[]{
                EnumChatFormatting.BLUE + "Extreme Diesel Engine" + EnumChatFormatting.RESET,
                StatCollector.translateToLocal("GT5U.multiblock.energy") + ": " +
                        EnumChatFormatting.GREEN + GT_Utility.formatNumbers(storedEnergy) + EnumChatFormatting.RESET + " EU / " +
                        EnumChatFormatting.YELLOW + GT_Utility.formatNumbers(maxEnergy) + EnumChatFormatting.RESET + " EU",
                getIdealStatus() == getRepairStatus() ?
                        EnumChatFormatting.GREEN + StatCollector.translateToLocal("GT5U.turbine.maintenance.false") + EnumChatFormatting.RESET :
                        EnumChatFormatting.RED + StatCollector.translateToLocal("GT5U.turbine.maintenance.true") + EnumChatFormatting.RESET,
                StatCollector.translateToLocal("GT5U.engine.output") + ": " +
                        EnumChatFormatting.RED + GT_Utility.formatNumbers(-mEUt*mEfficiency/10000) + EnumChatFormatting.RESET + " EU/t",
                StatCollector.translateToLocal("GT5U.engine.consumption") + ": " +
                        EnumChatFormatting.YELLOW + GT_Utility.formatNumbers(fuelConsumption) + EnumChatFormatting.RESET + " L/t",
                StatCollector.translateToLocal("GT5U.engine.value") + ": " +
                        EnumChatFormatting.YELLOW + GT_Utility.formatNumbers(fuelValue) + EnumChatFormatting.RESET + " EU/L",
                StatCollector.translateToLocal("GT5U.turbine.fuel") + ": " +
                        EnumChatFormatting.GOLD + GT_Utility.formatNumbers(fuelRemaining) + EnumChatFormatting.RESET + " L",
                StatCollector.translateToLocal("GT5U.engine.efficiency") + ": " +
                        EnumChatFormatting.YELLOW + (mEfficiency/100F) + EnumChatFormatting.YELLOW + " %",
                StatCollector.translateToLocal("GT5U.multiblock.pollution") + ": " +
                        EnumChatFormatting.GREEN + mPollutionReduction + EnumChatFormatting.RESET + " %"
        };
    }
}
