package gregtech.common.tileentities.machines.multi;

import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class GT_MetaTileEntity_LargeBoiler_Steel extends GT_MetaTileEntity_LargeBoiler {
    public GT_MetaTileEntity_LargeBoiler_Steel(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_LargeBoiler_Steel(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_LargeBoiler_Steel(this.mName);
    }

    @Override
    public int getPollutionPerSecond(ItemStack aStack) {
        int integratedCircuitConfig = getIntegratedCircuitConfig();
        return Math.max(1, (int) (GT_Mod.gregtechproxy.mPollutionLargeSteelBoilerPerSecond/(GT_Mod.gregtechproxy.mPollutionReleasedByThrottle * integratedCircuitConfig)));
    }

    @Override
    public int getPollutionPerTick(ItemStack aStack){
        return getPollutionPerSecond(aStack)/20;
    }

    @Override
    public String getCasingMaterial(){
    	return "Steel";
    }

    @Override
    public String getCasingBlockType() {
        return "Machine Casings";
    }

    @Override
    public Block getCasingBlock() {
        return GregTech_API.sBlockCasings2;
    }

    @Override
    public byte getCasingMeta() {
        return 0;
    }

    @Override
    public byte getCasingTextureIndex() {
        return 16;
    }

    @Override
    public Block getPipeBlock() {
        return GregTech_API.sBlockCasings2;
    }

    @Override
    public byte getPipeMeta() {
        return 13;
    }

    @Override
    public Block getFireboxBlock() {
        return GregTech_API.sBlockCasings3;
    }

    @Override
    public byte getFireboxMeta() {
        return 14;
    }

    @Override
    public byte getFireboxTextureIndex() {
        return 46;
    }

    @Override
    public int getEUt() {
        return 600;
    }

    @Override
    public int getEfficiencyIncrease() {
        return 12;
    }

    @Override
    int runtimeBoost(int mTime) {
        return mTime * 150 / 100;
    }
}
