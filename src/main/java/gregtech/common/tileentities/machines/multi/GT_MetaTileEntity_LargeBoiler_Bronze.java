package gregtech.common.tileentities.machines.multi;

import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class GT_MetaTileEntity_LargeBoiler_Bronze extends GT_MetaTileEntity_LargeBoiler {
    public GT_MetaTileEntity_LargeBoiler_Bronze(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_LargeBoiler_Bronze(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_LargeBoiler_Bronze(this.mName);
    }

    @Override
    public int getPollutionPerTick(ItemStack aStack) {

        int integratedCircuitConfig = getIntegratedCircuitConfig();

        /**
         * This is the coefficient reducing the pollution based on the throttle applied via the circuit.
         * 25 is the equivalent of EU/t removed by a throttle of -1000L/s (25 EU/t * 2 L/EU * 20 ticks = 1000 L/s)
         * so 25/getEUt() is the normalized quantity removed by each increment in the throttle
         */

        int integratedCircuitReduction = (1-integratedCircuitConfig*25/getEUt());
        /**
         * max here to clamp it to one in case the integratedCircuitReduction goes negative to ensure 1 gibbl/t
         * of pollution.
         */
        return Math.max(1, GT_Mod.gregtechproxy.mPollutionLargeBronzeBoiler*integratedCircuitReduction);

    }
    
    @Override
    public String getCasingMaterial(){
    	return "Bronze";
    }

    @Override
    public String getCasingBlockType() {
        return "Plated Bricks";
    }

    @Override
    public Block getCasingBlock() {
        return GregTech_API.sBlockCasings1;
    }

    @Override
    public byte getCasingMeta() {
        return 10;
    }

    @Override
    public byte getCasingTextureIndex() {
        return 10;
    }

    @Override
    public Block getPipeBlock() {
        return GregTech_API.sBlockCasings2;
    }

    @Override
    public byte getPipeMeta() {
        return 12;
    }

    @Override
    public Block getFireboxBlock() {
        return GregTech_API.sBlockCasings3;
    }

    @Override
    public byte getFireboxMeta() {
        return 13;
    }

    @Override
    public byte getFireboxTextureIndex() {
        return 45;
    }

    @Override
    public int getEUt() {
        return 400;
    }

    @Override
    public int getEfficiencyIncrease() {
        return 16;
    }

    @Override
    int runtimeBoost(int mTime) {
        return mTime * 2;
    }
}
