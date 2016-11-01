package gregtech.api.metatileentity.implementations;

import gregtech.GT_Mod;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.objects.XSTR;
import gregtech.common.GT_Pollution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class GT_MetaTileEntity_Hatch_Muffler extends GT_MetaTileEntity_Hatch {
    public GT_MetaTileEntity_Hatch_Muffler(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 0, "Outputs the Pollution (Might cause ... things)");
    }

    public GT_MetaTileEntity_Hatch_Muffler(String aName, int aTier, String aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 0, aDescription, aTextures);
    }

    @Override
    public String[] getDescription() {
        return new String[]{mDescription, "DO NOT OBSTRUCT THE OUTPUT!","Reduces Pollution to "+calculatePollutionReduction(100)+"%"};
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_MUFFLER)};
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_MUFFLER)};
    }

    @Override
    public boolean isSimpleMachine() {
        return true;
    }

    @Override
    public boolean isFacingValid(byte aFacing) {
        return true;
    }

    @Override
    public boolean isAccessAllowed(EntityPlayer aPlayer) {
        return true;
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        return false;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_Hatch_Muffler(mName, mTier, mDescription, mTextures);
    }

    public boolean polluteEnvironment() {
    	if(getBaseMetaTileEntity().getAirAtSide(getBaseMetaTileEntity().getFrontFacing())){
    		GT_Pollution.addPollution(new ChunkPosition(this.getBaseMetaTileEntity().getXCoord(), this.getBaseMetaTileEntity().getYCoord(), this.getBaseMetaTileEntity().getZCoord()), calculatePollutionReduction(10000));
    		return true;
    		}
        return false;
    }
    
    public int calculatePollutionReduction(int aPollution){
    	return (int) (aPollution *(Math.pow(0.85F, mTier-1)));
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return false;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return false;
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if(this.getBaseMetaTileEntity().isActive())
            pollutionParticles(this.getBaseMetaTileEntity().getWorld());
    }

    public void pollutionParticles(World aWorld){
        XSTR floatGen=new XSTR();
        boolean chk1,chk2,chk3;
        float ran1=floatGen.nextFloat(),ran2=0,ran3=0;
        chk1=ran1*100<calculatePollutionReduction(100);
        if(GT_Pollution.getPollutionAtCoords(this.getBaseMetaTileEntity().getXCoord(), this.getBaseMetaTileEntity().getZCoord())>= GT_Mod.gregtechproxy.mPollutionSmogLimit){
            ran2=floatGen.nextFloat();
            ran3=floatGen.nextFloat();
            chk2=ran2*100<calculatePollutionReduction(100);
            chk3=ran3*100<calculatePollutionReduction(100);
            if(!(chk1||chk2||chk3))return;
        }else{
            if(!chk1)return;
            chk2=chk3=false;
        }

        IGregTechTileEntity aMuffler=this.getBaseMetaTileEntity();
        ForgeDirection aDir=ForgeDirection.getOrientation(aMuffler.getFrontFacing());
        float xPos=aDir.offsetX*0.76F+aMuffler.getXCoord()+0.25F;
        float yPos=aDir.offsetY*0.76F+aMuffler.getYCoord()+0.25F;
        float zPos=aDir.offsetZ*0.76F+aMuffler.getZCoord()+0.25F;

        float ySpd=aDir.offsetY*0.1F+0.2F+0.1F*floatGen.nextFloat();
        float xSpd;
        float zSpd;

        if(aDir.offsetY==-1){
            float temp=floatGen.nextFloat()*2*(float)Math.PI;
            xSpd=(float)Math.sin(temp)*0.1F;
            zSpd=(float)Math.cos(temp)*0.1F;
        }else{
            xSpd=aDir.offsetX*(0.1F+0.2F*floatGen.nextFloat());
            zSpd=aDir.offsetZ*(0.1F+0.2F*floatGen.nextFloat());
        }

        if(chk1)
            aWorld.spawnParticle("largesmoke", xPos + ran1*0.5F, yPos + floatGen.nextFloat()*0.5F, zPos + floatGen.nextFloat()*0.5F, xSpd, ySpd, zSpd);

        if(chk2)
            aWorld.spawnParticle("largesmoke", xPos + ran2*0.5F, yPos + floatGen.nextFloat()*0.5F, zPos + floatGen.nextFloat()*0.5F, xSpd, ySpd, zSpd);

        if(chk3)
            aWorld.spawnParticle("largesmoke", xPos + ran3*0.5F, yPos + floatGen.nextFloat()*0.5F, zPos + floatGen.nextFloat()*0.5F, xSpd, ySpd, zSpd);
    }
}
