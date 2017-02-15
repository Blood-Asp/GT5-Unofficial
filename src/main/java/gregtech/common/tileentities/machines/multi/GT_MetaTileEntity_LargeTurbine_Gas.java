package gregtech.common.tileentities.machines.multi;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Recipe.GT_Recipe_Map;
import gregtech.api.util.GT_Utility;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;

public class GT_MetaTileEntity_LargeTurbine_Gas extends GT_MetaTileEntity_LargeTurbine {

    public GT_MetaTileEntity_LargeTurbine_Gas(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_LargeTurbine_Gas(String aName) {
        super(aName);
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        return new ITexture[]{Textures.BlockIcons.MACHINE_CASINGS[1][aColorIndex + 1], aFacing == aSide ? aActive ? new GT_RenderedTexture(Textures.BlockIcons.LARGETURBINE_SS_ACTIVE5) : new GT_RenderedTexture(Textures.BlockIcons.LARGETURBINE_SS5) : Textures.BlockIcons.CASING_BLOCKS[58]};
    }


    public String[] getDescription() {
        return new String[]{
                "Controller Block for the Large Gas Turbine",
                "Size(WxHxD): 3x3x4 (Hollow), Controller (Front centered)",
                "1x Input Hatch (Side centered)",
                "1x Maintenance Hatch (Side centered)",
                "1x Muffler Hatch (Side centered)",
                "1x Dynamo Hatch (Back centered)",
                "Stainless Steel Turbine Casings for the rest (24 at least!)",
                "Needs a Turbine Item (Inside controller GUI)",
                "Output depending on Rotor: 102-6720EU/t"};
    }

    public int getFuelValue(FluidStack aLiquid) {
        if (aLiquid == null || GT_Recipe_Map.sTurbineFuels == null) return 0;
        FluidStack tLiquid;
        Collection<GT_Recipe> tRecipeList = GT_Recipe_Map.sTurbineFuels.mRecipeList;
        if (tRecipeList != null) for (GT_Recipe tFuel : tRecipeList)
            if ((tLiquid = GT_Utility.getFluidForFilledItem(tFuel.getRepresentativeInput(0), true)) != null)
                if (aLiquid.isFluidEqual(tLiquid)) return tFuel.mSpecialValue;
        return 0;
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_LargeTurbine_Gas(mName);
    }

    @Override
    public Block getCasingBlock() {
        return GregTech_API.sBlockCasings4;
    }

    @Override
    public byte getCasingMeta() {
        return 10;
    }

    @Override
    public byte getCasingTextureIndex() {
        return 58;
    }

    @Override
    public int getPollutionPerTick(ItemStack aStack) {
        return 8;
    }

    @Override
    int fluidIntoPower(ArrayList<FluidStack> aFluids, int aOptFlow, int aBaseEff) {
        int tEU = 0;

        int actualOptimalFlow = 0;

        if (aFluids.size() >= 1) {
            FluidStack firstFuelType = new FluidStack(aFluids.get(0), 0); // Identify a SINGLE type of fluid to process.  Doesn't matter which one. Ignore the rest!
            int fuelValue = getFuelValue(firstFuelType);
            actualOptimalFlow = (int) (aOptFlow / fuelValue);
            this.realOptFlow = actualOptimalFlow;

            int remainingFlow = (int) (actualOptimalFlow * 1.25f); // Allowed to use up to 125% of optimal flow.  Variable required outside of loop for multi-hatch scenarios.
            int flow = 0;
            int totalFlow = 0;

            int aFluids_sS=aFluids.size();
            for (int i = 0; i < aFluids_sS; i++) {
                if (aFluids.get(i).isFluidEqual(firstFuelType)) {
                    flow = aFluids.get(i).amount; // Get all (steam) in hatch
                    flow = Math.min(flow, Math.min(remainingFlow, (int) (actualOptimalFlow * 1.25f))); // try to use up to 125% of optimal flow w/o exceeding remainingFlow
                    depleteInput(new FluidStack(aFluids.get(i), flow)); // deplete that amount
                    this.storedFluid = aFluids.get(i).amount;
                    remainingFlow -= flow; // track amount we're allowed to continue depleting from hatches
                    totalFlow += flow; // track total input used
                }
            }

            tEU = (int) (Math.min((float) actualOptimalFlow, totalFlow) * fuelValue);

            if (totalFlow != actualOptimalFlow) {
                float efficiency = 1.0f - Math.abs(((totalFlow - (float) actualOptimalFlow) / actualOptimalFlow));
                if(totalFlow>actualOptimalFlow){efficiency = 1.0f;}
                if (efficiency < 0)
                    efficiency = 0; // Can happen with really ludicrously poor inefficiency.
                tEU *= efficiency;
                tEU = Math.max(1, (int)((long)tEU * (long)aBaseEff / 10000L));
            } else {
                tEU = (int)((long)tEU * (long)aBaseEff / 10000L);
            }

            return tEU;

        }
        return 0;
    }


}
