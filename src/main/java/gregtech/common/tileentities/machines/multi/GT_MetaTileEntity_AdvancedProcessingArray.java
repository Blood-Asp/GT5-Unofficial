package gregtech.common.tileentities.machines.multi;

import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Recipe;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static gregtech.api.enums.GT_Values.V;
import static gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicMachine.isValidForLowGravity;

public class GT_MetaTileEntity_AdvancedProcessingArray extends GT_MetaTileEntity_MultiBlockBase {

    GT_Recipe mLastRecipe;

    public GT_MetaTileEntity_AdvancedProcessingArray(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_AdvancedProcessingArray(String aName) {
        super(aName);
    }

    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_AdvancedProcessingArray(this.mName);
    }

    public String[] getDescription() {
        return new String[]{
                "Controller Block for the Advanced Processing Array",
                "Is an improved version of basic processing Array",
                "Runs supplied machines as if placed in the world",
                "Size(WxHxD): 5x5x5 (Hollow), Controller (Front  Second Layer centered)",
                "1x Input Hatch/Bus (Any casing)",
                "1x Output Hatch/Bus (Any casing)",
                "1x Maintenance Hatch (Any casing)",
                "1x Energy Hatch (Any casing)",
                "Robust HSS-G Machine Casings for the rest (70 at least!)",
                "Place up to 64 Single Block GT Machines into the Controller Inventory"};
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            return new ITexture[] {
                    Textures.BlockIcons.casingTexturePages[1][50],
                    new GT_RenderedTexture(aActive ? Textures.BlockIcons.OVERLAY_FRONT_PROCESSING_ARRAY_ACTIVE
                            : Textures.BlockIcons.OVERLAY_FRONT_PROCESSING_ARRAY) };
        }
        return new ITexture[] { Textures.BlockIcons.casingTexturePages[1][50] };
    }

    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "AdvancedProcessingArray.png");
    }

    public GT_Recipe.GT_Recipe_Map getRecipeMap() {
        if (mInventory[1] == null) return null;
        String tmp = mInventory[1].getUnlocalizedName().replaceAll("gt\\.blockmachines\\.basicmachine\\.", "");
        if (tmp.startsWith("centrifuge")) {
            return GT_Recipe.GT_Recipe_Map.sCentrifugeRecipes;
        } else if (tmp.startsWith("electrolyzer")) {
            return GT_Recipe.GT_Recipe_Map.sElectrolyzerRecipes;
        } else if (tmp.startsWith("alloysmelter")) {
            return GT_Recipe.GT_Recipe_Map.sAlloySmelterRecipes;
        } else if (tmp.startsWith("assembler")) {
            return GT_Recipe.GT_Recipe_Map.sAssemblerRecipes;
        } else if (tmp.startsWith("compressor")) {
            return GT_Recipe.GT_Recipe_Map.sCompressorRecipes;
        } else if (tmp.startsWith("extractor")) {
            return GT_Recipe.GT_Recipe_Map.sExtractorRecipes;
        } else if (tmp.startsWith("macerator")) {
            return GT_Recipe.GT_Recipe_Map.sMaceratorRecipes;
        } else if (tmp.startsWith("recycler")) {
            return GT_Recipe.GT_Recipe_Map.sRecyclerRecipes;
        } else if (tmp.startsWith("thermalcentrifuge")) {
            return GT_Recipe.GT_Recipe_Map.sThermalCentrifugeRecipes;
        } else if (tmp.startsWith("orewasher")) {
            return GT_Recipe.GT_Recipe_Map.sOreWasherRecipes;
        } else if (tmp.startsWith("chemicalreactor")) {
            return GT_Recipe.GT_Recipe_Map.sChemicalRecipes;
        } else if (tmp.startsWith("chemicalbath")) {
            return GT_Recipe.GT_Recipe_Map.sChemicalBathRecipes;
        } else if (tmp.startsWith("electromagneticseparator")) {
            return GT_Recipe.GT_Recipe_Map.sElectroMagneticSeparatorRecipes;
        } else if (tmp.startsWith("autoclave")) {
            return GT_Recipe.GT_Recipe_Map.sAutoclaveRecipes;
        } else if (tmp.startsWith("mixer")) {
            return GT_Recipe.GT_Recipe_Map.sMixerRecipes;
        } else if (tmp.startsWith("hammer")) {
            return GT_Recipe.GT_Recipe_Map.sHammerRecipes;
        } else if (tmp.startsWith("sifter")) {
            return GT_Recipe.GT_Recipe_Map.sSifterRecipes;
        } else if (tmp.startsWith("extruder")) {
            return GT_Recipe.GT_Recipe_Map.sExtruderRecipes;
        } else if (tmp.startsWith("laserengraver")) {
            return GT_Recipe.GT_Recipe_Map.sLaserEngraverRecipes;
        } else if (tmp.startsWith("bender")) {
            return GT_Recipe.GT_Recipe_Map.sBenderRecipes;
        } else if (tmp.startsWith("wiremill")) {
            return GT_Recipe.GT_Recipe_Map.sWiremillRecipes;
        } else if (tmp.startsWith("arcfurnace")) {
            return GT_Recipe.GT_Recipe_Map.sArcFurnaceRecipes;
        } else if (tmp.startsWith("brewery")) {
            return GT_Recipe.GT_Recipe_Map.sBrewingRecipes;
        } else if (tmp.startsWith("canner")) {
            return GT_Recipe.GT_Recipe_Map.sCannerRecipes;
        } else if (tmp.startsWith("cutter")) {
            return GT_Recipe.GT_Recipe_Map.sCutterRecipes;
        } else if (tmp.startsWith("fermenter")) {
            return GT_Recipe.GT_Recipe_Map.sFermentingRecipes;
        } else if (tmp.startsWith("fluidextractor")) {
            return GT_Recipe.GT_Recipe_Map.sFluidExtractionRecipes;
        } else if (tmp.startsWith("fluidsolidifier")) {
            return GT_Recipe.GT_Recipe_Map.sFluidSolidficationRecipes;
        } else if (tmp.startsWith("lathe")) {
            return GT_Recipe.GT_Recipe_Map.sLatheRecipes;
        } else if (tmp.startsWith("boxinator")) {
            return GT_Recipe.GT_Recipe_Map.sBoxinatorRecipes;
        } else if (tmp.startsWith("unboxinator")) {
            return GT_Recipe.GT_Recipe_Map.sUnboxinatorRecipes;
        } else if (tmp.startsWith("polarizer")) {
            return GT_Recipe.GT_Recipe_Map.sPolarizerRecipes;
        } else if(tmp.startsWith("press")){
            return GT_Recipe.GT_Recipe_Map.sPressRecipes;
        } else if (tmp.startsWith("plasmaarcfurnace")) {
            return GT_Recipe.GT_Recipe_Map.sPlasmaArcFurnaceRecipes;
        } else if (tmp.startsWith("printer")) {
            return GT_Recipe.GT_Recipe_Map.sPrinterRecipes;
        } else if (tmp.startsWith("press")) {
            return GT_Recipe.GT_Recipe_Map.sPressRecipes;
        } else if (tmp.startsWith("fluidcanner")) {
            return GT_Recipe.GT_Recipe_Map.sFluidCannerRecipes;
        } else if (tmp.startsWith("fluidheater")) {
            return GT_Recipe.GT_Recipe_Map.sFluidHeaterRecipes;
        } else if (tmp.startsWith("distillery")) {
            return GT_Recipe.GT_Recipe_Map.sDistilleryRecipes;
        } else if (tmp.startsWith("slicer")) {
            return GT_Recipe.GT_Recipe_Map.sSlicerRecipes;
        } else if (tmp.startsWith("amplifier")) {
            return GT_Recipe.GT_Recipe_Map.sAmplifiers;
        } else if (tmp.startsWith("circuitassembler")) {
            return GT_Recipe.GT_Recipe_Map.sCircuitAssemblerRecipes;
        }
        return null;
    }

    public boolean isCorrectMachinePart(ItemStack aStack) {
        if (aStack != null && aStack.getUnlocalizedName().startsWith("gt.blockmachines.basicmachine.")) {
            return true;
        }
        return false;
    }

    public boolean isFacingValid(byte aFacing) {
        return aFacing > 1;
    }

    public String mMachine = "";
    public boolean checkRecipe(ItemStack aStack) {
        if (!isCorrectMachinePart(mInventory[1])) {
            return false;
        }
        GT_Recipe.GT_Recipe_Map map = getRecipeMap();
        if (map == null) return false;
        ArrayList<ItemStack> tInputList = getStoredInputs();
        int tTier = 0;
        if (mInventory[1].getUnlocalizedName().endsWith("1")) {
            tTier = 1;
        }else if (mInventory[1].getUnlocalizedName().endsWith("2")) {
            tTier = 2;
        }else if (mInventory[1].getUnlocalizedName().endsWith("3")) {
            tTier = 3;
        }else if (mInventory[1].getUnlocalizedName().endsWith("4")) {
            tTier = 4;
        }else if (mInventory[1].getUnlocalizedName().endsWith("5")) {
            tTier = 5;
        }else if (mInventory[1].getUnlocalizedName().endsWith("6")) {
            tTier = 6;
        }else if (mInventory[1].getUnlocalizedName().endsWith("7")) {
            tTier = 7;
        }else if (mInventory[1].getUnlocalizedName().endsWith("8")) {
            tTier = 8;
        }

        if(!mMachine.equals(mInventory[1].getUnlocalizedName()))mLastRecipe=null;
        mMachine = mInventory[1].getUnlocalizedName();
        ItemStack[] tInputs = (ItemStack[]) tInputList.toArray(new ItemStack[tInputList.size()]);

        ArrayList<FluidStack> tFluidList = getStoredFluids();

        FluidStack[] tFluids = (FluidStack[]) tFluidList.toArray(new FluidStack[tFluidList.size()]);
        if (tInputList.size() > 0 || tFluids.length > 0) {
            GT_Recipe tRecipe = map.findRecipe(getBaseMetaTileEntity(), mLastRecipe, false, gregtech.api.enums.GT_Values.V[tTier], tFluids, tInputs);
            if (tRecipe != null) {
                if (GT_Mod.gregtechproxy.mLowGravProcessing && tRecipe.mSpecialValue == -100 &&
                        !isValidForLowGravity(tRecipe,getBaseMetaTileEntity().getWorld().provider.dimensionId))
                    return false;

                mLastRecipe = tRecipe;
                this.mEUt = 0;
                this.mOutputItems = null;
                this.mOutputFluids = null;
                int machines = Math.min(64, mInventory[1].stackSize);
                int i = 0;
                for (; i < machines; i++) {
                    if (!tRecipe.isRecipeInputEqual(true, tFluids, tInputs)) {
                        if (i == 0) {
                            return false;
                        }
                        break;
                    }
                }
                this.mMaxProgresstime = tRecipe.mDuration;
                this.mEfficiency = (10000 - (getIdealStatus() - getRepairStatus()) * 1000);
                this.mEfficiencyIncrease = 10000;
                this.mEUt = tRecipe.mEUt;
                this.mMaxProgresstime = tRecipe.mDuration;
                while (this.mEUt <= V[tTier - 1] * map.mAmperage) {
                    this.mEUt *= 4;
                    this.mMaxProgresstime /= 2;
                }
                this.mEUt *= i;
                if (this.mEUt > 0) {
                    this.mEUt = (-this.mEUt);
                }
                ItemStack[] tOut = new ItemStack[tRecipe.mOutputs.length];
                for (int h = 0; h < tRecipe.mOutputs.length; h++) {
                    if(tRecipe.getOutput(h)!=null){
                        tOut[h] = tRecipe.getOutput(h).copy();
                        tOut[h].stackSize = 0;}
                }
                FluidStack tFOut = null;
                if (tRecipe.getFluidOutput(0) != null) tFOut = tRecipe.getFluidOutput(0).copy();
                for (int f = 0; f < tOut.length; f++) {
                    if (tRecipe.mOutputs[f] != null && tOut[f] != null) {
                        for (int g = 0; g < i; g++) {
                            if (getBaseMetaTileEntity().getRandomNumber(10000) < tRecipe.getOutputChance(f))
                                tOut[f].stackSize += tRecipe.mOutputs[f].stackSize;
                        }
                    }
                }
                if (tFOut != null) {
                    int tSize = tFOut.amount;
                    tFOut.amount = tSize * i;
                }
                tOut = clean(tOut);
                this.mMaxProgresstime = Math.max(1, this.mMaxProgresstime);
                List<ItemStack> overStacks = new ArrayList<ItemStack>();
                for (int f = 0; f < tOut.length; f++) {
                    while (tOut[f].getMaxStackSize() < tOut[f].stackSize) {
                        if(tOut[f]!=null){
                            ItemStack tmp = tOut[f].copy();
                            tmp.stackSize = tmp.getMaxStackSize();
                            tOut[f].stackSize = tOut[f].stackSize - tOut[f].getMaxStackSize();
                            overStacks.add(tmp);}
                    }
                }
                if (overStacks.size() > 0) {
                    ItemStack[] tmp = new ItemStack[overStacks.size()];
                    tmp = overStacks.toArray(tmp);
                    tOut = ArrayUtils.addAll(tOut, tmp);
                }
                List<ItemStack> tSList = new ArrayList<ItemStack>();
                for (ItemStack tS : tOut) {
                    if (tS.stackSize > 0) tSList.add(tS);
                }
                tOut = tSList.toArray(new ItemStack[tSList.size()]);
                this.mOutputItems = tOut;
                this.mOutputFluids = new FluidStack[]{tFOut};
                updateSlots();
                return true;
            }
        }
        return false;
    }

    public static ItemStack[] clean(final ItemStack[] v) {
        List<ItemStack> list = new ArrayList<ItemStack>(Arrays.asList(v));
        list.removeAll(Collections.singleton(null));
        return list.toArray(new ItemStack[list.size()]);
    }

    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        int xDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetX*2;
        int zDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetZ*2;
        int tAmount = 0;
        for (int i = -2; i < 3; i++) {
            for (int j = -2; j < 3; j++) {
                for (int h = -1; h < 4; h++) {
                    IGregTechTileEntity tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir + i, h, zDir + j);
                    if (i != 2 && i != -2 && j != 2 && j != -2) {
                        if(h == -1||h==3){
                            if ((!addMaintenanceToMachineList(tTileEntity, 178)) && (!addInputToMachineList(tTileEntity, 178)) && (!addOutputToMachineList(tTileEntity, 178)) && (!addEnergyInputToMachineList(tTileEntity, 178))) {
                                if (aBaseMetaTileEntity.getBlockOffset(xDir + i, h, zDir + j) != GregTech_API.sBlockCasings8) {
                                    return false;
                                }
                                if (aBaseMetaTileEntity.getMetaIDOffset(xDir + i, h, zDir + j) != 2) {
                                    return false;
                                }
                                tAmount++;
                            }
                        }
                        else{
                            if (!aBaseMetaTileEntity.getAirOffset(xDir + i, h, zDir + j)) {
                                return false;
                            }
                        }
                    }
                    else {
                        if(!(i+xDir==0&&j+zDir==0&&h==0)){
                            if ((!addMaintenanceToMachineList(tTileEntity, 178)) && (!addInputToMachineList(tTileEntity, 178)) && (!addOutputToMachineList(tTileEntity, 178)) && (!addEnergyInputToMachineList(tTileEntity, 178))) {
                                if (aBaseMetaTileEntity.getBlockOffset(xDir + i, h, zDir + j) != GregTech_API.sBlockCasings8) {
                                    return false;
                                }
                                if (aBaseMetaTileEntity.getMetaIDOffset(xDir + i, h, zDir + j) != 2) {
                                    return false;
                                }
                                tAmount++;
                            }
                        }
                    }

                }
            }
        }
        return tAmount >=70;
    }

    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    public int getPollutionPerTick(ItemStack aStack) {
        return 0;
    }

    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return false;
    }

}
