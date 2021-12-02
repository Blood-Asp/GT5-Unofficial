package gregtech.common.tileentities.machines.multi;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.items.GT_MetaGenerated_Tool;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Dynamo;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Muffler;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Recipe.GT_Recipe_Map;
import gregtech.api.util.GT_Utility;
import gregtech.common.items.GT_MetaGenerated_Tool_01;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;

import static gregtech.api.enums.Textures.BlockIcons.LARGETURBINE_TU_ACTIVE5;
import static gregtech.api.enums.Textures.BlockIcons.MACHINE_CASINGS;
import static gregtech.api.enums.Textures.BlockIcons.casingTexturePages;

public class GT_MetaTileEntity_LargeTurbine_Plasma extends GT_MetaTileEntity_LargeTurbine {

    public GT_MetaTileEntity_LargeTurbine_Plasma(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_LargeTurbine_Plasma(String aName) {
        super(aName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        return new ITexture[]{MACHINE_CASINGS[1][aColorIndex + 1], aFacing == aSide ? aActive ? TextureFactory.builder().addIcon(LARGETURBINE_TU_ACTIVE5).extFacing().build() : TextureFactory.builder().addIcon(Textures.BlockIcons.LARGETURBINE_TU5).extFacing().build() : casingTexturePages[0][60]};
    }

    @Override
    protected GT_Multiblock_Tooltip_Builder createTooltip() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Plasma Turbine")
                .addInfo("Controller block for the Large Plasma Generator")
                .addInfo("Needs a Turbine, place inside controller")
                .addInfo("Use your Fusion Reactor to produce the Plasma")
                .addSeparator()
                .beginStructureBlock(3, 3, 4, true)
                .addController("Front center")
                .addCasingInfo("Tungstensteel Turbine Casing", 24)
                .addDynamoHatch("Back center", 1)
                .addMaintenanceHatch("Side centered", 2)
                .addInputHatch("Plasma Fluid, Side centered", 2)
                .addOutputHatch("Molten Fluid, optional, Side centered", 2)
                .toolTipFinisher("Gregtech");
        return tt;
    }

    public int getFuelValue(FluidStack aLiquid) {
        if (aLiquid == null) return 0;
        GT_Recipe tFuel = GT_Recipe_Map.sPlasmaFuels.findFuel(aLiquid);
        if (tFuel != null) return tFuel.mSpecialValue;
        return 0;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_LargeTurbine_Plasma(mName);
    }

    @Override
    public Block getCasingBlock() {
        return GregTech_API.sBlockCasings4;
    }

    @Override
    public byte getCasingMeta() {
        return 12;
    }

    @Override
    public byte getCasingTextureIndex() {
        return 60;
    }

    @Override
    int fluidIntoPower(ArrayList<FluidStack> aFluids, int aOptFlow, int aBaseEff) {
        if (aFluids.size() >= 1) {
            aOptFlow *= 800;//CHANGED THINGS HERE, check recipe runs once per 20 ticks
            int tEU = 0;

            int actualOptimalFlow = 0;

            FluidStack firstFuelType = new FluidStack(aFluids.get(0), 0); // Identify a SINGLE type of fluid to process.  Doesn't matter which one. Ignore the rest!
            int fuelValue = getFuelValue(firstFuelType);
            actualOptimalFlow = GT_Utility.safeInt((long) Math.ceil((double) aOptFlow / (double) fuelValue));
            this.realOptFlow = actualOptimalFlow; // For scanner info

            int remainingFlow = GT_Utility.safeInt((long) (actualOptimalFlow * 1.25f)); // Allowed to use up to 125% of optimal flow.  Variable required outside of loop for multi-hatch scenarios.
            int flow = 0;
            int totalFlow = 0;

            storedFluid = 0;
            for (FluidStack aFluid : aFluids) {
                if (aFluid.isFluidEqual(firstFuelType)) {
                    flow = Math.min(aFluid.amount, remainingFlow); // try to use up w/o exceeding remainingFlow
                    depleteInput(new FluidStack(aFluid, flow)); // deplete that amount
                    this.storedFluid += aFluid.amount;
                    remainingFlow -= flow; // track amount we're allowed to continue depleting from hatches
                    totalFlow += flow; // track total input used
                }
            }
            String fn = FluidRegistry.getFluidName(firstFuelType);
            String[] nameSegments = fn.split("\\.", 2);
            if (nameSegments.length == 2) {
                String outputName = nameSegments[1];
                FluidStack output = FluidRegistry.getFluidStack(outputName, totalFlow);
                if (output == null) {
                    output = FluidRegistry.getFluidStack("molten." + outputName, totalFlow);
                }
                if (output != null) {
                    addOutput(output);
                }
            }
            if (totalFlow <= 0) return 0;
            tEU = GT_Utility.safeInt((long) ((fuelValue / 20D) * (double) totalFlow));

            //GT_FML_LOGGER.info(totalFlow+" : "+fuelValue+" : "+aOptFlow+" : "+actualOptimalFlow+" : "+tEU);

            if (totalFlow == actualOptimalFlow) {
                tEU = GT_Utility.safeInt((long) (aBaseEff / 10000D * tEU));
            } else {
                double efficiency = 1.0D - Math.abs((totalFlow - actualOptimalFlow) / (float) actualOptimalFlow);

                tEU = (int) (tEU * efficiency);
                tEU = GT_Utility.safeInt((long) (aBaseEff / 10000D * tEU));
            }

            return tEU;

        }
        return 0;
    }

    @Override
    public boolean checkRecipe(ItemStack aStack) {
        if ((counter & 7) == 0 && (aStack == null || !(aStack.getItem() instanceof GT_MetaGenerated_Tool) || aStack.getItemDamage() < 170 || aStack.getItemDamage() > 179)) {
            stopMachine();
            return false;
        }
        ArrayList<FluidStack> tFluids = getStoredFluids();
        if (!tFluids.isEmpty()) {
            if (baseEff == 0 || optFlow == 0 || counter >= 512 || this.getBaseMetaTileEntity().hasWorkJustBeenEnabled()
                    || this.getBaseMetaTileEntity().hasInventoryBeenModified()) {
                counter = 0;
                baseEff = GT_Utility.safeInt((long) ((5F + ((GT_MetaGenerated_Tool) aStack.getItem()).getToolCombatDamage(aStack)) * 1000F));
                optFlow = GT_Utility.safeInt((long) Math.max(Float.MIN_NORMAL,
                        ((GT_MetaGenerated_Tool) aStack.getItem()).getToolStats(aStack).getSpeedMultiplier()
                                * ((GT_MetaGenerated_Tool) aStack.getItem()).getPrimaryMaterial(aStack).mToolSpeed
                                * 50));
            } else {
                counter++;
            }
        }

        if (optFlow <= 0 || baseEff <= 0) {
            stopMachine();//in case the turbine got removed
            return false;
        }

        int newPower = fluidIntoPower(tFluids, optFlow, baseEff);  // How much the turbine should be producing with this flow

        int difference = newPower - this.mEUt; // difference between current output and new output

        // Magic numbers: can always change by at least 10 eu/t, but otherwise by at most 1 percent of the difference in power level (per tick)
        // This is how much the turbine can actually change during this tick
        int maxChangeAllowed = Math.max(200, GT_Utility.safeInt((long) Math.abs(difference) / 5));

        if (Math.abs(difference) > maxChangeAllowed) { // If this difference is too big, use the maximum allowed change
            int change = maxChangeAllowed * (difference > 0 ? 1 : -1); // Make the change positive or negative.
            this.mEUt += change; // Apply the change
        } else
            this.mEUt = newPower;

        if (this.mEUt <= 0) {
            //stopMachine();
            this.mEUt = 0;
            this.mEfficiency = 0;
            return false;
        } else {
            this.mMaxProgresstime = 20;
            this.mEfficiencyIncrease = 200;
            // Overvoltage is handled inside the MultiBlockBase when pushing out to dynamos.  no need to do it here.

            return true;
        }
    }

	    @Override
    public String[] getInfoData() {
        int mPollutionReduction=0;
        for (GT_MetaTileEntity_Hatch_Muffler tHatch : mMufflerHatches) {
            if (isValidMetaTileEntity(tHatch)) {
                mPollutionReduction=Math.max(tHatch.calculatePollutionReduction(100),mPollutionReduction);
            }
        }

        String tRunning = mMaxProgresstime>0 ?

                EnumChatFormatting.GREEN+StatCollector.translateToLocal("GT5U.turbine.running.true")+EnumChatFormatting.RESET :
                EnumChatFormatting.RED+StatCollector.translateToLocal("GT5U.turbine.running.false")+EnumChatFormatting.RESET;
        String tMaintainance = getIdealStatus() == getRepairStatus() ?
                EnumChatFormatting.GREEN+StatCollector.translateToLocal("GT5U.turbine.maintenance.false")+EnumChatFormatting.RESET :
                EnumChatFormatting.RED+StatCollector.translateToLocal("GT5U.turbine.maintenance.true")+EnumChatFormatting.RESET ;
        int tDura = 0;

        if (mInventory[1] != null && mInventory[1].getItem() instanceof GT_MetaGenerated_Tool_01) {
            tDura = GT_Utility.safeInt((long)(100.0f / GT_MetaGenerated_Tool.getToolMaxDamage(mInventory[1]) * (GT_MetaGenerated_Tool.getToolDamage(mInventory[1]))+1));
        }

        long storedEnergy=0;
        long maxEnergy=0;
        for(GT_MetaTileEntity_Hatch_Dynamo tHatch : mDynamoHatches) {
            if (isValidMetaTileEntity(tHatch)) {
                storedEnergy+=tHatch.getBaseMetaTileEntity().getStoredEU();
                maxEnergy+=tHatch.getBaseMetaTileEntity().getEUCapacity();
            }
        }
        String[] ret = new String[]{
                // 8 Lines available for information panels
                tRunning + ": " + EnumChatFormatting.RED + GT_Utility.formatNumbers(mEUt) + EnumChatFormatting.RESET + " EU/t", /* 1 */
                tMaintainance, /* 2 */
                StatCollector.translateToLocal("GT5U.turbine.efficiency") + ": " +
                        EnumChatFormatting.YELLOW + (mEfficiency/100F) + EnumChatFormatting.RESET + "%", /* 2 */
                StatCollector.translateToLocal("GT5U.multiblock.energy") + ": " +
                        EnumChatFormatting.GREEN + GT_Utility.formatNumbers(storedEnergy) + EnumChatFormatting.RESET + " EU / " + /* 3 */
                        EnumChatFormatting.YELLOW + GT_Utility.formatNumbers(maxEnergy) + EnumChatFormatting.RESET + " EU",
                StatCollector.translateToLocal("GT5U.turbine.flow") + ": " +
                        EnumChatFormatting.YELLOW + GT_Utility.formatNumbers(GT_Utility.safeInt((long)realOptFlow)) + EnumChatFormatting.RESET + " L/s" + /* 4 */
                        EnumChatFormatting.YELLOW + " (" + (looseFit?StatCollector.translateToLocal("GT5U.turbine.loose"):StatCollector.translateToLocal("GT5U.turbine.tight")) + ")", /* 5 */
                StatCollector.translateToLocal("GT5U.turbine.fuel") + ": " +
                        EnumChatFormatting.GOLD + GT_Utility.formatNumbers(storedFluid) + EnumChatFormatting.RESET + "L", /* 6 */
                StatCollector.translateToLocal("GT5U.turbine.dmg") + ": " +
                        EnumChatFormatting.RED + Integer.toString(tDura) + EnumChatFormatting.RESET + "%", /* 7 */
                StatCollector.translateToLocal("GT5U.multiblock.pollution") + ": " +
                        EnumChatFormatting.GREEN + mPollutionReduction + EnumChatFormatting.RESET + " %" /* 8 */
        };
        if (!this.getClass().getName().contains("Steam"))
        	ret[4]=StatCollector.translateToLocal("GT5U.turbine.flow")+": "+EnumChatFormatting.YELLOW+GT_Utility.safeInt((long)realOptFlow)+EnumChatFormatting.RESET+" L/s";
        return ret;
    }
}
