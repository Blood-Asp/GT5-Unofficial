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
        return new String[]{mDescription, "DO NOT OBSTRUCT THE OUTPUT!"};
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
    	GT_Pollution.addPollution(new ChunkPosition(this.getBaseMetaTileEntity().getXCoord(), this.getBaseMetaTileEntity().getYCoord(), this.getBaseMetaTileEntity().getZCoord()), 10000 - (600*(mTier-1)));
        return (mTier > 1 && getBaseMetaTileEntity().getRandomNumber(mTier) != 0) || getBaseMetaTileEntity().getAirAtSide(getBaseMetaTileEntity().getFrontFacing());
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return false;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return false;
    }

    //@Override
    //public String[] getInfoData() {
    //    return new String[]{
    //            "Reduces pollution by: "+ EnumChatFormatting.GREEN + (6*(mTier-1))+ EnumChatFormatting.RESET+" %"
    //    };
    //}

    public int getPollutionReduction() {
        return 6*(mTier-1);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if(this.getBaseMetaTileEntity().isActive())
            pollutionParticles(this.getBaseMetaTileEntity().getWorld());
    }

    public void pollutionParticles(World aWorld){
        IGregTechTileEntity aMuffler=this.getBaseMetaTileEntity();
        ForgeDirection aDir=ForgeDirection.getOrientation(aMuffler.getFrontFacing());
        double xPos=aDir.offsetX+aMuffler.getXCoord()+0.25F;
        double yPos=aDir.offsetX+aMuffler.getYCoord()+0.05F;
        double zPos=aDir.offsetX+aMuffler.getZCoord()+0.25F;

        aWorld.spawnParticle("largesmoke", xPos + (new XSTR()).nextFloat()*0.5F, yPos, zPos + (new XSTR()).nextFloat()*0.5F, 0.0D, 0.3D, 0.0D);
        if(GT_Pollution.getPollutionAtCoords(this.getBaseMetaTileEntity().getXCoord(), this.getBaseMetaTileEntity().getZCoord())>= GT_Mod.gregtechproxy.mPollutionSmogLimit) {
            aWorld.spawnParticle("largesmoke", xPos + (new XSTR()).nextFloat()*0.5F, yPos, zPos + (new XSTR()).nextFloat()*0.5F, 0.0D, 0.45D, 0.0D);
            aWorld.spawnParticle("largesmoke", xPos + (new XSTR()).nextFloat()*0.5F, yPos, zPos + (new XSTR()).nextFloat()*0.5F, 0.0D, 0.6D, 0.0D);
        }
    }
}
