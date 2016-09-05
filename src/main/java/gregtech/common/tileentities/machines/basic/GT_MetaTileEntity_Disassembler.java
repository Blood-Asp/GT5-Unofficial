package gregtech.common.tileentities.machines.basic;

import gregtech.api.enums.GT_Values;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicMachine;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Utility;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GT_MetaTileEntity_Disassembler
        extends GT_MetaTileEntity_BasicMachine {
    public GT_MetaTileEntity_Disassembler(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 1, "Disassembles Machines at " + Math.max(50 + 10 * aTier,100) + "% Efficiency", 1, 9, "Disassembler.png", "", new ITexture[]{new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_SIDE_DISASSEMBLER_ACTIVE), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_SIDE_DISASSEMBLER), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_FRONT_DISASSEMBLER_ACTIVE), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_FRONT_DISASSEMBLER), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_TOP_DISASSEMBLER_ACTIVE), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_TOP_DISASSEMBLER), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_BOTTOM_DISASSEMBLER_ACTIVE), new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_BOTTOM_DISASSEMBLER)});
    }

    public GT_MetaTileEntity_Disassembler(String aName, int aTier, String aDescription, ITexture[][][] aTextures, String aGUIName, String aNEIName) {
        super(aName, aTier, 1, aDescription, aTextures, 1, 9, aGUIName, aNEIName);
    }

    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_Disassembler(this.mName, this.mTier, this.mDescription, this.mTextures, this.mGUIName, this.mNEIName);
    }

    public int checkRecipe() {
        if ((getInputAt(0) != null) && (isOutputEmpty())) {
            NBTTagCompound tNBT = getInputAt(0).getTagCompound();
            if (tNBT != null) {
                tNBT = tNBT.getCompoundTag("GT.CraftingComponents");
                if (tNBT != null) {
                    boolean isAnyOutput=false;
                    calculateOverclockedNessDisassembler(16);
                    this.mMaxProgresstime = 80;
                    //In case recipe is too OP for that machine
                    if (mEUt == Integer.MAX_VALUE - 1)//&& mMaxProgresstime==Integer.MAX_VALUE-1
                        return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
                    for (int i = 0; i < this.mOutputItems.length; i++) {
                        if (getBaseMetaTileEntity().getRandomNumber(100) < 50 + 10 * this.mTier) {
                            this.mOutputItems[i] = GT_Utility.loadItem(tNBT, "Ingredient." + i);
                            if (this.mOutputItems[i] != null) {
                                this.mMaxProgresstime *= (mTier>5 ? 1.7F-(mTier/10.0F) : 1.7F);
                                isAnyOutput=true;
                            }
                        }
                    }
                    if(!isAnyOutput){
                    	return 0;
                    }
                    getInputAt(0).stackSize -= 1;
                    return 2;
                }
            }
        }
        return 0;
    }

    private void calculateOverclockedNessDisassembler(int aEUt) {
        if(mTier==0){
            mEUt=aEUt/4;
        }else{
            //Long EUt calculation
            long xEUt=aEUt;
            //Isnt too low EUt check?
            long tempEUt = xEUt<GT_Values.V[1] ? GT_Values.V[1] : xEUt;

            while (tempEUt <= GT_Values.V[mTier -1] * (long)mAmperage) {
                tempEUt *= 4;//this actually controls overclocking
                xEUt *= 4;//this is effect of overclocking
            }
            if(xEUt>Integer.MAX_VALUE-1){
                mEUt = Integer.MAX_VALUE-1;
            }else{
                mEUt = (int)xEUt;
                mEUt = mEUt == 0 ? 1 : mEUt;
            }
        }
    }

    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return (super.allowPutStack(aBaseMetaTileEntity, aIndex, aSide, aStack)) && (aStack.getTagCompound() != null) && (aStack.getTagCompound().getCompoundTag("GT.CraftingComponents") != null);
    }
}
