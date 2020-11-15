package gregtech.common.tileentities.machines.multi;

import java.util.ArrayList;
import java.util.Collection;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Dynamo;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Muffler;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

public class GT_MetaTileEntity_ExtremeDieselEngine extends GT_MetaTileEntity_DieselEngine {
	protected int fuelConsumption = 0;
    protected int fuelValue = 0;
    protected int fuelRemaining = 0;
    protected boolean boostEu = false;

    public GT_MetaTileEntity_ExtremeDieselEngine(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }
    
    public GT_MetaTileEntity_ExtremeDieselEngine(String aName) {
        super(aName);
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Controller Block for the Extreme Combustion Engine",
                "Size(WxHxD): 3x3x4, Controller (front centered)",
                "3x3x4 of Robust Tungstensteel Machine Casing (hollow, Min 16!)",
                "2x Titanium Gear Box Machine Casing inside the Hollow Casing",//todo
                "8x Extreme Engine Intake Casing (around controller)",
                "2x Input Hatch (HOG/Lubricant) (one of the Casings next to a Gear Box)",
                "1x Input Hatch (Optional, for Liquid Oxygen) (one of the Casings next to a Gear Box)",
                "1x Maintenance Hatch (one of the Casings next to a Gear Box)",
                "1x Muffler Hatch (top middle back, above the rear Gear Box)",
                "1x Dynamo Hatch (back centered)",
                "Engine Intake Casings must not be obstructed in front (only air blocks)",
                "Supply High Octane Gasoline and 8000L of Lubricant per hour to run.",
                "Supply 320L of Liquid Oxygen per second to boost output (optional).",
                "Default: Produces 8192EU/t at 100% efficiency",
                "Boosted: Produces 32768EU/t at 400% efficiency",
                "Causes " + 20 * getPollutionPerTick(null) + " Pollution per second"};
    }

    @Override
    protected GT_Recipe.GT_Recipe_Map_Fuel getFuelMap() {
        return GT_Recipe.GT_Recipe_Map.sExtremeDieselFuels;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            return new ITexture[]{Textures.BlockIcons.casingTexturePages[0][60], new GT_RenderedTexture(aActive ? Textures.BlockIcons.OVERLAY_FRONT_EXTREME_DIESEL_ENGINE_ACTIVE : Textures.BlockIcons.OVERLAY_FRONT_EXTREME_DIESEL_ENGINE)};
        }
        return new ITexture[]{Textures.BlockIcons.casingTexturePages[0][60]};//controller texture? where do I find this? Copied plasma turbine
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "LargeExtremeDieselEngine.png");//change
    }

    @Override
    public Block getCasingBlock() {//changed to RTSMC
        return GregTech_API.sBlockCasings4;
    }

    @Override
    public byte getCasingMeta() {//same
        return 0;
    }

    @Override
    public Block getIntakeBlock() {
        return GregTech_API.sBlockCasings8;//added new
    }

    @Override
    public byte getIntakeMeta() {//same
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
    public byte getCasingTextureIndex() {//should be what hatches/busses change to?
        return 60;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_ExtremeDieselEngine(this.mName);
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return boostEu ? 40000 : 10000;//4x output if boosted instead of x3
    }

    @Override
    public int getPollutionPerTick(ItemStack aStack) {
        return super.getPollutionPerTick(aStack) * 8;//x8
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
                EnumChatFormatting.BLUE+"Extreme Diesel Engine"+EnumChatFormatting.RESET,
                StatCollector.translateToLocal("GT5U.multiblock.energy")+": " +
                EnumChatFormatting.GREEN + Long.toString(storedEnergy) + EnumChatFormatting.RESET +" EU / "+
                EnumChatFormatting.YELLOW + Long.toString(maxEnergy) + EnumChatFormatting.RESET +" EU",
                getIdealStatus() == getRepairStatus() ?
                EnumChatFormatting.GREEN+StatCollector.translateToLocal("GT5U.turbine.maintenance.false")+EnumChatFormatting.RESET :
                EnumChatFormatting.RED+StatCollector.translateToLocal("GT5U.turbine.maintenance.true")+EnumChatFormatting.RESET,
                StatCollector.translateToLocal("GT5U.engine.output")+": " +EnumChatFormatting.RED+(-mEUt*mEfficiency/10000)+EnumChatFormatting.RESET+" EU/t",
                StatCollector.translateToLocal("GT5U.engine.consumption")+": " +EnumChatFormatting.YELLOW+fuelConsumption+EnumChatFormatting.RESET+" L/t",
                StatCollector.translateToLocal("GT5U.engine.value")+": " +EnumChatFormatting.YELLOW+fuelValue+EnumChatFormatting.RESET+" EU/L",
                StatCollector.translateToLocal("GT5U.turbine.fuel")+": " +EnumChatFormatting.GOLD+fuelRemaining+EnumChatFormatting.RESET+" L",
                StatCollector.translateToLocal("GT5U.engine.efficiency")+": " +EnumChatFormatting.YELLOW+(mEfficiency/100F)+EnumChatFormatting.YELLOW+" %",
                StatCollector.translateToLocal("GT5U.multiblock.pollution")+": " + EnumChatFormatting.GREEN + mPollutionReduction+ EnumChatFormatting.RESET+" %"

        };
    }
}
