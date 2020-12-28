package gregtech.common.tileentities.machines.multi;

import gregtech.api.GregTech_API;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.interfaces.IHeatingCoil;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public abstract class GT_MetaTileEntity_AbstractMultiFurnace extends GT_MetaTileEntity_MultiBlockBase {

    private static final int CASING_INDEX = 11;

    public GT_MetaTileEntity_AbstractMultiFurnace(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_AbstractMultiFurnace(String aName) {
        super(aName);
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public boolean isFacingValid(byte aFacing) {
        return aFacing > 1;
    }

    protected HeatingCoilLevel getInitialHeatLevel(IGregTechTileEntity aBaseMetaTileEntity, int xDir, int zDir) {
        Block coil = aBaseMetaTileEntity.getBlockOffset(xDir + 1, 1, zDir);
        if (!(coil instanceof IHeatingCoil))
            return null;
        IHeatingCoil heatingCoil = (IHeatingCoil) coil;
        byte tUsedMeta = aBaseMetaTileEntity.getMetaIDOffset(xDir + 1, 1, zDir);
        return heatingCoil.getCoilHeat(tUsedMeta);
    }

    protected boolean checkStructure(HeatingCoilLevel heatingCap, int xDir, int zDir, IGregTechTileEntity aBaseMetaTileEntity){
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                if (!checkTopLayer(i, j, xDir, zDir, aBaseMetaTileEntity))
                    return false;

                if (!checkBottomLayer(i, j, xDir, zDir, aBaseMetaTileEntity))
                    return false;

                if (!checkCoils(heatingCap, i, j, xDir, zDir, aBaseMetaTileEntity))
                    return false;
            }
        }
        return true;
    }

    protected abstract boolean checkTopLayer(int i, int j, int xDir, int zDir, IGregTechTileEntity aBaseMetaTileEntity);
    protected abstract boolean checkCoils(HeatingCoilLevel heatingCap, int i, int j, int xDir, int zDir, IGregTechTileEntity aBaseMetaTileEntity);

    protected boolean checkBottomLayer(int i, int j, int xDir, int zDir, IGregTechTileEntity aBaseMetaTileEntity){
        if ((xDir + i == 0) && (zDir + j == 0))
            return true;
        IGregTechTileEntity tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir + i, 0, zDir + j);
        if (addMaintenanceToMachineList(tTileEntity, CASING_INDEX))
            return true;
        if (addInputToMachineList(tTileEntity, CASING_INDEX))
            return true;
        if (addOutputToMachineList(tTileEntity, CASING_INDEX))
            return true;
        if (addEnergyInputToMachineList(tTileEntity, CASING_INDEX))
            return true;

        if (aBaseMetaTileEntity.getBlockOffset(xDir + i, 0, zDir + j) != GregTech_API.sBlockCasings1)
            return false;
        return aBaseMetaTileEntity.getMetaIDOffset(xDir + i, 0, zDir + j) == CASING_INDEX;
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    @Override
    public int getPollutionPerTick(ItemStack aStack) {
        return 20;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    @Override
    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return false;
    }
}
