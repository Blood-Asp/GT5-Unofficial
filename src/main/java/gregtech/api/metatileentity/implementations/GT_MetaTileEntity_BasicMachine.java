package gregtech.api.metatileentity.implementations;

import com.enderio.core.common.util.BlockCoord;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.ItemList;
import gregtech.api.gui.GT_Container_BasicMachine;
import gregtech.api.gui.GT_GUIContainer_BasicMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMachineCallback;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.objects.GT_ItemStack;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.*;
import gregtech.api.util.GT_Recipe.GT_Recipe_Map;
import gregtech.common.tileentities.machines.multi.GT_MetaTileEntity_Cleanroom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.Arrays;
import java.util.List;

import static gregtech.api.enums.GT_Values.V;
import static gregtech.api.enums.GT_Values.debugCleanroom;
import static gregtech.api.enums.Textures.BlockIcons.MACHINE_CASINGS;
import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_PIPE_OUT;
import static gregtech.api.util.GT_Utility.moveMultipleItemStacks;

/**
 * NEVER INCLUDE THIS FILE IN YOUR MOD!!!
 * <p/>
 * This is the main construct for my Basic Machines such as the Automatic Extractor
 * Extend this class to make a simple Machine
 */
public abstract class GT_MetaTileEntity_BasicMachine extends GT_MetaTileEntity_BasicTank implements IMachineCallback<GT_MetaTileEntity_Cleanroom> {

    /**
     * return values for checkRecipe()
     */
    protected static final int
            DID_NOT_FIND_RECIPE = 0,
            FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS = 1,
            FOUND_AND_SUCCESSFULLY_USED_RECIPE = 2;
    public static final int OTHER_SLOT_COUNT = 4;
    public final ItemStack[] mOutputItems;
    public final int mInputSlotCount, mAmperage;
    public boolean mAllowInputFromOutputSide = false, mFluidTransfer = false, mItemTransfer = false, mHasBeenUpdated = false, mStuttering = false, mCharge = false, mDecharge = false;
    public boolean mDisableFilter = true;
    public boolean mDisableMultiStack = true;
    public int mMainFacing = -1, mProgresstime = 0, mMaxProgresstime = 0, mEUt = 0, mOutputBlocked = 0;
    public FluidStack mOutputFluid;
    public String mGUIName, mNEIName;


    @Deprecated
    public GT_MetaTileEntity_Cleanroom mCleanroom;
    /**
     * Contains the Recipe which has been previously used, or null if there was no previous Recipe, which could have been buffered
     */
    protected GT_Recipe mLastRecipe = null;
    private FluidStack mFluidOut;

    /**
     * @param aOverlays 0 = SideFacingActive
     *                  1 = SideFacingInactive
     *                  2 = FrontFacingActive
     *                  3 = FrontFacingInactive
     *                  4 = TopFacingActive
     *                  5 = TopFacingInactive
     *                  6 = BottomFacingActive
     *                  7 = BottomFacingInactive
     *                  ----- Not all Array Elements have to be initialised, you can also just use 8 Parameters for the Default Pipe Texture Overlays -----
     *                  8 = BottomFacingPipeActive
     *                  9 = BottomFacingPipeInactive
     *                  10 = TopFacingPipeActive
     *                  11 = TopFacingPipeInactive
     *                  12 = SideFacingPipeActive
     *                  13 = SideFacingPipeInactive
     */
    public GT_MetaTileEntity_BasicMachine(int aID, String aName, String aNameRegional, int aTier, int aAmperage, String aDescription, int aInputSlotCount, int aOutputSlotCount, String aGUIName, String aNEIName, ITexture... aOverlays) {
        super(aID, aName, aNameRegional, aTier, OTHER_SLOT_COUNT + aInputSlotCount + aOutputSlotCount + 1, aDescription, aOverlays);
        mInputSlotCount = Math.max(0, aInputSlotCount);
        mOutputItems = new ItemStack[Math.max(0, aOutputSlotCount)];
        mAmperage = aAmperage;
        mGUIName = aGUIName;
        mNEIName = aNEIName;
    }
    public GT_MetaTileEntity_BasicMachine(int aID, String aName, String aNameRegional, int aTier, int aAmperage, String[] aDescription, int aInputSlotCount, int aOutputSlotCount, String aGUIName, String aNEIName, ITexture... aOverlays) {
        super(aID, aName, aNameRegional, aTier, OTHER_SLOT_COUNT + aInputSlotCount + aOutputSlotCount + 1, aDescription, aOverlays);
        mInputSlotCount = Math.max(0, aInputSlotCount);
        mOutputItems = new ItemStack[Math.max(0, aOutputSlotCount)];
        mAmperage = aAmperage;
        mGUIName = aGUIName;
        mNEIName = aNEIName;
    }
    public GT_MetaTileEntity_BasicMachine(String aName, int aTier, int aAmperage, String aDescription, ITexture[][][] aTextures, int aInputSlotCount, int aOutputSlotCount, String aGUIName, String aNEIName) {
        super(aName, aTier, OTHER_SLOT_COUNT + aInputSlotCount + aOutputSlotCount + 1, aDescription, aTextures);
        mInputSlotCount = Math.max(0, aInputSlotCount);
        mOutputItems = new ItemStack[Math.max(0, aOutputSlotCount)];
        mAmperage = aAmperage;
        mGUIName = aGUIName;
        mNEIName = aNEIName;
    }
    
    public GT_MetaTileEntity_BasicMachine(String aName, int aTier, int aAmperage, String[] aDescription, ITexture[][][] aTextures, int aInputSlotCount, int aOutputSlotCount, String aGUIName, String aNEIName) {
        super(aName, aTier, OTHER_SLOT_COUNT + aInputSlotCount + aOutputSlotCount + 1, aDescription, aTextures);
        mInputSlotCount = Math.max(0, aInputSlotCount);
        mOutputItems = new ItemStack[Math.max(0, aOutputSlotCount)];
        mAmperage = aAmperage;
        mGUIName = aGUIName;
        mNEIName = aNEIName;
    }

    protected boolean isValidMainFacing(byte aSide) {
    	return aSide > 1;
    }
    
    public boolean setMainFacing(byte aSide){
    	if (!isValidMainFacing(aSide)) return false;
    	mMainFacing = aSide;
    	if(getBaseMetaTileEntity().getFrontFacing() == mMainFacing){
    		getBaseMetaTileEntity().setFrontFacing(GT_Utility.getOppositeSide(aSide));
    	}
        onFacingChange();
        onMachineBlockUpdate();
    	return true;
    }

    @Override
    public GT_MetaTileEntity_Cleanroom getCallbackBase() {
        return this.mCleanroom;
    }

    @Override
    public void setCallbackBase(GT_MetaTileEntity_Cleanroom callback) {
        this.mCleanroom = callback;
    }

    @Override
    public Class<GT_MetaTileEntity_Cleanroom> getType() {
        return GT_MetaTileEntity_Cleanroom.class;
    }

    @Override
    public ITexture[][][] getTextureSet(ITexture[] aTextures) {
        ITexture[][][] rTextures = new ITexture[14][17][];
        aTextures = Arrays.copyOf(aTextures, 14);

        for (int i = 0; i < aTextures.length; i++)
            if (aTextures[i] != null) for (byte c = -1; c < 16; c++) {
                if (rTextures[i][c + 1] == null)
                    rTextures[i][c + 1] = new ITexture[]{MACHINE_CASINGS[mTier][c + 1], aTextures[i]};
            }

        for (byte c = -1; c < 16; c++) {
            if (rTextures[0][c + 1] == null) rTextures[0][c + 1] = getSideFacingActive(c);
            if (rTextures[1][c + 1] == null) rTextures[1][c + 1] = getSideFacingInactive(c);
            if (rTextures[2][c + 1] == null) rTextures[2][c + 1] = getFrontFacingActive(c);
            if (rTextures[3][c + 1] == null) rTextures[3][c + 1] = getFrontFacingInactive(c);
            if (rTextures[4][c + 1] == null) rTextures[4][c + 1] = getTopFacingActive(c);
            if (rTextures[5][c + 1] == null) rTextures[5][c + 1] = getTopFacingInactive(c);
            if (rTextures[6][c + 1] == null) rTextures[6][c + 1] = getBottomFacingActive(c);
            if (rTextures[7][c + 1] == null) rTextures[7][c + 1] = getBottomFacingInactive(c);
            if (rTextures[8][c + 1] == null) rTextures[8][c + 1] = getBottomFacingPipeActive(c);
            if (rTextures[9][c + 1] == null) rTextures[9][c + 1] = getBottomFacingPipeInactive(c);
            if (rTextures[10][c + 1] == null) rTextures[10][c + 1] = getTopFacingPipeActive(c);
            if (rTextures[11][c + 1] == null) rTextures[11][c + 1] = getTopFacingPipeInactive(c);
            if (rTextures[12][c + 1] == null) rTextures[12][c + 1] = getSideFacingPipeActive(c);
            if (rTextures[13][c + 1] == null) rTextures[13][c + 1] = getSideFacingPipeInactive(c);
        }
        return rTextures;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        return mTextures[mMainFacing < 2 ? aSide == aFacing ? aActive ? 2 : 3 : aSide == 0 ? aActive ? 6 : 7 : aSide == 1 ? aActive ? 4 : 5 : aActive ? 0 : 1 : aSide == mMainFacing ? aActive ? 2 : 3 : (showPipeFacing() && aSide == aFacing) ? aSide == 0 ? aActive ? 8 : 9 : aSide == 1 ? aActive ? 10 : 11 : aActive ? 12 : 13 : aSide == 0 ? aActive ? 6 : 7 : aSide == 1 ? aActive ? 4 : 5 : aActive ? 0 : 1][aColorIndex + 1];
    }

    @Override
    public boolean isSimpleMachine() {
        return false;
    }

    @Override
    public boolean isOverclockerUpgradable() {
        return false;
    }

    @Override
    public boolean isTransformerUpgradable() {
        return false;
    }

    @Override
    public boolean isElectric() {
        return true;
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        return aIndex > 0 && super.isValidSlot(aIndex) && aIndex != OTHER_SLOT_COUNT + mInputSlotCount + mOutputItems.length;
    }

    @Override
    public boolean isFacingValid(byte aFacing) {
        return mMainFacing > 1 || aFacing > 1;
    }

    @Override
    public boolean isEnetInput() {
        return true;
    }

    @Override
    public boolean isInputFacing(byte aSide) {
        return aSide != mMainFacing;
    }

    @Override
    public boolean isOutputFacing(byte aSide) {
        return false;
    }

    @Override
    public boolean isTeleporterCompatible() {
        return false;
    }

    @Override
    public boolean isLiquidInput(byte aSide) {
        return aSide != mMainFacing && (mAllowInputFromOutputSide || aSide != getBaseMetaTileEntity().getFrontFacing());
    }

    @Override
    public boolean isLiquidOutput(byte aSide) {
        return aSide != mMainFacing;
    }

    @Override
    public long getMinimumStoredEU() {
        return V[mTier] * 16L;
    }

    @Override
    public long maxEUStore() {
        return V[mTier] * 64L;
    }

    @Override
    public long maxEUInput() {
        return V[mTier];
    }

    @Override
    public long maxSteamStore() {
        return maxEUStore();
    }

    @Override
    public long maxAmperesIn() {
        return ((long)mEUt * 2L) / V[mTier] + 1L;
    }

    @Override
    public int getInputSlot() {
        return OTHER_SLOT_COUNT;
    }

    @Override
    public int getOutputSlot() {
        return OTHER_SLOT_COUNT + mInputSlotCount;
    }

    @Override
    public int getStackDisplaySlot() {
        return 2;
    }

    @Override
    public int rechargerSlotStartIndex() {
        return 1;
    }

    @Override
    public int dechargerSlotStartIndex() {
        return 1;
    }

    @Override
    public int rechargerSlotCount() {
        return mCharge ? 1 : 0;
    }

    @Override
    public int dechargerSlotCount() {
        return mDecharge ? 1 : 0;
    }

    @Override
    public boolean isAccessAllowed(EntityPlayer aPlayer) {
        return true;
    }

    @Override
    public int getProgresstime() {
        return mProgresstime;
    }

    @Override
    public int maxProgresstime() {
        return mMaxProgresstime;
    }

    @Override
    public int increaseProgress(int aProgress) {
        mProgresstime += aProgress;
        return mMaxProgresstime - mProgresstime;
    }

    @Override
    public boolean isFluidInputAllowed(FluidStack aFluid) {
        return getFillableStack() != null || (getRecipeList() != null && getRecipeList().containsInput(aFluid));
    }

    @Override
    public boolean isFluidChangingAllowed() {
        return true;
    }

    @Override
    public boolean doesFillContainers() {
        return false;
    }

    @Override
    public boolean doesEmptyContainers() {
        return false;
    }

    @Override
    public boolean canTankBeFilled() {
        return true;
    }

    @Override
    public boolean canTankBeEmptied() {
        return true;
    }

    @Override
    public boolean displaysItemStack() {
        return true;
    }

    @Override
    public boolean displaysStackSize() {
        return true;
    }

    @Override
    public FluidStack getDisplayedFluid() {
        return displaysOutputFluid() ? getDrainableStack() : null;
    }

    @Override
    public FluidStack getDrainableStack() {
        return mFluidOut;
    }

    @Override
    public FluidStack setDrainableStack(FluidStack aFluid) {
        mFluidOut = aFluid;
        return mFluidOut;
    }

    @Override
    public boolean isDrainableStackSeparate() {
        return true;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        if (aBaseMetaTileEntity.isClientSide()) return true;
        if(!GT_Mod.gregtechproxy.mForceFreeFace) {
        	aBaseMetaTileEntity.openGUI(aPlayer);
        	return true;
        }
        for(byte i=0;i < 6; i++){
        	if(aBaseMetaTileEntity.getAirAtSide(i)){
        		aBaseMetaTileEntity.openGUI(aPlayer);
        		return true;
        	}        	
        }
        GT_Utility.sendChatToPlayer(aPlayer,"No free Side!");        
        return true;
    }

    @Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_Container_BasicMachine(aPlayerInventory, aBaseMetaTileEntity);
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_BasicMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), mGUIName, GT_Utility.isStringValid(mNEIName) ? mNEIName : getRecipeList() != null ? getRecipeList().mUnlocalizedName : "");
    }

    @Override
    public void initDefaultModes(NBTTagCompound aNBT) {
        mMainFacing = -1;
        if (!getBaseMetaTileEntity().getWorld().isRemote) {
            GT_ClientPreference tPreference = GT_Mod.gregtechproxy.getClientPreference(getBaseMetaTileEntity().getOwnerUuid());
            if (tPreference != null) {
                mDisableFilter = !tPreference.isSingleBlockInitialFilterEnabled();
                mDisableMultiStack = !tPreference.isSingleBlockInitialMultiStackEnabled();
            }
        }
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("mFluidTransfer", mFluidTransfer);
        aNBT.setBoolean("mItemTransfer", mItemTransfer);
        aNBT.setBoolean("mHasBeenUpdated", mHasBeenUpdated);
        aNBT.setBoolean("mAllowInputFromOutputSide", mAllowInputFromOutputSide);
        aNBT.setBoolean("mDisableFilter", mDisableFilter);
        aNBT.setBoolean("mDisableMultiStack", mDisableMultiStack);
        aNBT.setInteger("mEUt", mEUt);
        aNBT.setInteger("mMainFacing", mMainFacing);
        aNBT.setInteger("mProgresstime", mProgresstime);
        aNBT.setInteger("mMaxProgresstime", mMaxProgresstime);
        if (mOutputFluid != null) aNBT.setTag("mOutputFluid", mOutputFluid.writeToNBT(new NBTTagCompound()));
        if (mFluidOut != null) aNBT.setTag("mFluidOut", mFluidOut.writeToNBT(new NBTTagCompound()));

        for (int i = 0; i < mOutputItems.length; i++)
            if (mOutputItems[i] != null)
                aNBT.setTag("mOutputItem" + i, mOutputItems[i].writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        mFluidTransfer = aNBT.getBoolean("mFluidTransfer");
        mItemTransfer = aNBT.getBoolean("mItemTransfer");
        mHasBeenUpdated = aNBT.getBoolean("mHasBeenUpdated");
        mAllowInputFromOutputSide = aNBT.getBoolean("mAllowInputFromOutputSide");
        mDisableFilter = aNBT.getBoolean("mDisableFilter");
        mDisableMultiStack = aNBT.getBoolean("mDisableMultiStack");
        mEUt = aNBT.getInteger("mEUt");
        mMainFacing = aNBT.getInteger("mMainFacing");
        mProgresstime = aNBT.getInteger("mProgresstime");
        mMaxProgresstime = aNBT.getInteger("mMaxProgresstime");
        mOutputFluid = FluidStack.loadFluidStackFromNBT(aNBT.getCompoundTag("mOutputFluid"));
        mFluidOut = FluidStack.loadFluidStackFromNBT(aNBT.getCompoundTag("mFluidOut"));

        for (int i = 0; i < mOutputItems.length; i++) mOutputItems[i] = GT_Utility.loadItem(aNBT, "mOutputItem" + i);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);

        if (aBaseMetaTileEntity.isServerSide()) {
            mCharge = aBaseMetaTileEntity.getStoredEU() / 2 > aBaseMetaTileEntity.getEUCapacity() / 3;
            mDecharge = aBaseMetaTileEntity.getStoredEU() < aBaseMetaTileEntity.getEUCapacity() / 3;

            doDisplayThings();

            boolean tSucceeded = false;

            if (mMaxProgresstime > 0 && (mProgresstime >= 0 || aBaseMetaTileEntity.isAllowedToWork())) {
                aBaseMetaTileEntity.setActive(true);
                if (mProgresstime < 0 || drainEnergyForProcess(mEUt)) {
                    if (++mProgresstime >= mMaxProgresstime) {
                        for (int i = 0; i < mOutputItems.length; i++)
                            for (int j = 0; j < mOutputItems.length; j++)
                                if (aBaseMetaTileEntity.addStackToSlot(getOutputSlot() + ((j + i) % mOutputItems.length), mOutputItems[i]))
                                    break;
                        if (mOutputFluid != null)
                            if (getDrainableStack() == null) setDrainableStack(mOutputFluid.copy());
                            else if (mOutputFluid.isFluidEqual(getDrainableStack()))
                                getDrainableStack().amount += mOutputFluid.amount;
                        Arrays.fill(mOutputItems, null);
                        mOutputFluid = null;
                        mEUt = 0;
                        mProgresstime = 0;
                        mMaxProgresstime = 0;
                        mStuttering = false;
                        tSucceeded = true;
                        endProcess();
                    }
                    if (mProgresstime > 5) mStuttering = false;
                    //XSTR aXSTR = new XSTR();
                    //if(GT_Mod.gregtechproxy.mAprilFool && aXSTR.nextInt(5000)==0)GT_Utility.sendSoundToPlayers(aBaseMetaTileEntity.getWorld(), GregTech_API.sSoundList.get(5), 10.0F, -1.0F, aBaseMetaTileEntity.getXCoord(), aBaseMetaTileEntity.getYCoord(),aBaseMetaTileEntity.getZCoord());
                } else {
                    if (!mStuttering) {
                        stutterProcess();
                        if (canHaveInsufficientEnergy()) mProgresstime = -100;
                        mStuttering = true;
                    }
                }
            } else {
                aBaseMetaTileEntity.setActive(false);
            }

            boolean tRemovedOutputFluid = false;

            if (doesAutoOutputFluids() && getDrainableStack() != null && aBaseMetaTileEntity.getFrontFacing() != mMainFacing && (tSucceeded || aTick % 20 == 0)) {
                IFluidHandler tTank = aBaseMetaTileEntity.getITankContainerAtSide(aBaseMetaTileEntity.getFrontFacing());
                if (tTank != null) {
                    FluidStack tDrained = drain(1000, false);
                    if (tDrained != null) {
                        int tFilledAmount = tTank.fill(ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()), tDrained, false);
                        if (tFilledAmount > 0)
                            tTank.fill(ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()), drain(tFilledAmount, true), true);
                    }
                }
                if (getDrainableStack() == null) tRemovedOutputFluid = true;
            }

            if (doesAutoOutput() && !isOutputEmpty() && aBaseMetaTileEntity.getFrontFacing() != mMainFacing && (tSucceeded || mOutputBlocked % 300 == 1 || aBaseMetaTileEntity.hasInventoryBeenModified() || aTick % 600 == 0)) {
                TileEntity tTileEntity2 = aBaseMetaTileEntity.getTileEntityAtSide(aBaseMetaTileEntity.getFrontFacing());
                long tStoredEnergy = aBaseMetaTileEntity.getUniversalEnergyStored();
                int tMaxStacks = (int)(tStoredEnergy/64l);
                if (tMaxStacks > mOutputItems.length)
                    tMaxStacks = mOutputItems.length;

                moveMultipleItemStacks(aBaseMetaTileEntity, tTileEntity2, aBaseMetaTileEntity.getFrontFacing(), aBaseMetaTileEntity.getBackFacing(), null, false, (byte) 64, (byte) 1, (byte) 64, (byte) 1,tMaxStacks);
//                for (int i = 0, tCosts = 1; i < mOutputItems.length && tCosts > 0 && aBaseMetaTileEntity.isUniversalEnergyStored(128); i++) {
//                    tCosts = GT_Utility.moveOneItemStack(aBaseMetaTileEntity, tTileEntity2, aBaseMetaTileEntity.getFrontFacing(), aBaseMetaTileEntity.getBackFacing(), null, false, (byte) 64, (byte) 1, (byte) 64, (byte) 1);
//                    if (tCosts > 0) aBaseMetaTileEntity.decreaseStoredEnergyUnits(tCosts, true);
//                }
            }

            if (mOutputBlocked != 0) if (isOutputEmpty()) mOutputBlocked = 0;
            else mOutputBlocked++;

            if (allowToCheckRecipe()) {
                if (mMaxProgresstime <= 0 && aBaseMetaTileEntity.isAllowedToWork() && (tRemovedOutputFluid || tSucceeded || aBaseMetaTileEntity.hasInventoryBeenModified() || aTick % 600 == 0 || aBaseMetaTileEntity.hasWorkJustBeenEnabled()) && hasEnoughEnergyToCheckRecipe()) {
                    if (checkRecipe() == FOUND_AND_SUCCESSFULLY_USED_RECIPE) {
                        if (mInventory[3] != null && mInventory[3].stackSize <= 0) mInventory[3] = null;
                        for (int i = getInputSlot(), j = i + mInputSlotCount; i < j; i++)
                            if (mInventory[i] != null && mInventory[i].stackSize <= 0) mInventory[i] = null;
                        for (int i = 0; i < mOutputItems.length; i++) {
                            mOutputItems[i] = GT_Utility.copyOrNull(mOutputItems[i]);
                            if (mOutputItems[i] != null && mOutputItems[i].stackSize > 64)
                                mOutputItems[i].stackSize = 64;
                            mOutputItems[i] = GT_OreDictUnificator.get(true, mOutputItems[i]);
                        }
                        if (mFluid != null && mFluid.amount <= 0) mFluid = null;
                        mMaxProgresstime = Math.max(1, mMaxProgresstime);
                        if (GT_Utility.isDebugItem(mInventory[dechargerSlotStartIndex()])) {
                            mEUt = mMaxProgresstime = 1;
                        }
                        startProcess();
                    } else {
                        mMaxProgresstime = 0;
                        Arrays.fill(mOutputItems, null);
                        mOutputFluid = null;
                    }
                }
            } else {
                if (!mStuttering) {
                    stutterProcess();
                    mStuttering = true;
                }
            }
        }
    }

    protected void doDisplayThings() {
        if (mMainFacing < 2 && getBaseMetaTileEntity().getFrontFacing() > 1) {
            mMainFacing = getBaseMetaTileEntity().getFrontFacing();
        }
        if (mMainFacing >= 2 && !mHasBeenUpdated) {
            mHasBeenUpdated = true;
            getBaseMetaTileEntity().setFrontFacing(getBaseMetaTileEntity().getBackFacing());
        }
    }

    @Override
    public void updateFluidDisplayItem() {
        super.updateFluidDisplayItem();
        if (displaysInputFluid()) {
            int tDisplayStackSlot = OTHER_SLOT_COUNT + mInputSlotCount + mOutputItems.length;
            if (getFillableStack() == null) {
                if (ItemList.Display_Fluid.isStackEqual(mInventory[tDisplayStackSlot], true, true))
                    mInventory[tDisplayStackSlot] = null;
            } else {
                mInventory[tDisplayStackSlot] = GT_Utility.getFluidDisplayStack(getFillableStack(), true, !displaysStackSize());
            }
        }
    }

    protected boolean hasEnoughEnergyToCheckRecipe() {
        return getBaseMetaTileEntity().isUniversalEnergyStored(getMinimumStoredEU() / 2);
    }

    protected boolean drainEnergyForProcess(long aEUt) {
        return getBaseMetaTileEntity().decreaseStoredEnergyUnits(aEUt, false);
    }

    protected void calculateOverclockedNess(GT_Recipe aRecipe) {
        calculateOverclockedNess(aRecipe.mEUt, aRecipe.mDuration);
    }

    /**
     * Calcualtes overclocked ness using long integers
     * @param aEUt          - recipe EUt
     * @param aDuration     - recipe Duration
     */
    protected void calculateOverclockedNess(int aEUt, int aDuration) {
        if(mTier==0){
            //Long time calculation
            long xMaxProgresstime = ((long)aDuration)<<1;
            if(xMaxProgresstime>Integer.MAX_VALUE-1){
                //make impossible if too long
                mEUt=Integer.MAX_VALUE-1;
                mMaxProgresstime=Integer.MAX_VALUE-1;
            }else{
                mEUt=aEUt>>2;
                mMaxProgresstime=(int)xMaxProgresstime;
            }
        }else{
            //Long EUt calculation
            long xEUt=aEUt;
            //Isnt too low EUt check?
            long tempEUt = Math.max(xEUt, V[1]);

            mMaxProgresstime = aDuration;

            while (tempEUt <= V[mTier -1] * (long)mAmperage) {
                tempEUt<<=2;//this actually controls overclocking
                //xEUt *= 4;//this is effect of everclocking
                mMaxProgresstime>>=1;//this is effect of overclocking
                xEUt = mMaxProgresstime==0 ? xEUt>>1 : xEUt<<2;//U know, if the time is less than 1 tick make the machine use 2x less power
            }
            if(xEUt>Integer.MAX_VALUE-1){
                mEUt = Integer.MAX_VALUE-1;
                mMaxProgresstime = Integer.MAX_VALUE-1;
            }else{
                mEUt = (int)xEUt;
                if(mEUt==0)
                    mEUt = 1;
                if(mMaxProgresstime==0)
                    mMaxProgresstime = 1;//set time to 1 tick
            }
        }
    }

    protected ItemStack getSpecialSlot() {
        return mInventory[3];
    }

    protected ItemStack getOutputAt(int aIndex) {
        return mInventory[getOutputSlot() + aIndex];
    }

    protected ItemStack[] getAllOutputs() {
        ItemStack[] rOutputs = new ItemStack[mOutputItems.length];
        for (int i = 0; i < mOutputItems.length; i++) rOutputs[i] = getOutputAt(i);
        return rOutputs;
    }

    protected boolean canOutput(GT_Recipe aRecipe) {
        return aRecipe != null && (aRecipe.mNeedsEmptyOutput ? isOutputEmpty() && getDrainableStack() == null : canOutput(aRecipe.getFluidOutput(0)) && canOutput(aRecipe.mOutputs));
    }

    protected boolean canOutput(ItemStack... aOutputs) {
        if (aOutputs == null) return true;
        ItemStack[] tOutputSlots = getAllOutputs();
        for (int i = 0; i < tOutputSlots.length && i < aOutputs.length; i++)
            if (tOutputSlots[i] != null && aOutputs[i] != null && (!GT_Utility.areStacksEqual(tOutputSlots[i], aOutputs[i], false) || tOutputSlots[i].stackSize + aOutputs[i].stackSize > tOutputSlots[i].getMaxStackSize())) {
                mOutputBlocked++;
                return false;
            }
        return true;
    }

    protected boolean canOutput(FluidStack aOutput) {
        return getDrainableStack() == null || aOutput == null || (getDrainableStack().isFluidEqual(aOutput) && (getDrainableStack().amount <= 0 || getDrainableStack().amount + aOutput.amount <= getCapacity()));
    }

    protected ItemStack getInputAt(int aIndex) {
        return mInventory[getInputSlot() + aIndex];
    }

    protected ItemStack[] getAllInputs() {
        ItemStack[] rInputs = new ItemStack[mInputSlotCount];
        for (int i = 0; i < mInputSlotCount; i++) rInputs[i] = getInputAt(i);
        return rInputs;
    }

    protected boolean isOutputEmpty() {
        boolean rIsEmpty = true;
        for (ItemStack tOutputSlotContent : getAllOutputs())
            if (tOutputSlotContent != null) {
                rIsEmpty = false;
                break;
            }
        return rIsEmpty;
    }

    protected boolean displaysInputFluid() {
        return true;
    }

    protected boolean displaysOutputFluid() {
        return true;
    }

    @Override
    public void onValueUpdate(byte aValue) {
        mMainFacing = aValue;
    }

    @Override
    public byte getUpdateData() {
        return (byte) mMainFacing;
    }

    @Override
    public void doSound(byte aIndex, double aX, double aY, double aZ) {
        super.doSound(aIndex, aX, aY, aZ);
        if (aIndex == 8) GT_Utility.doSoundAtClient(GregTech_API.sSoundList.get(210), 100, 1.0F, aX, aY, aZ);
    }

    public boolean doesAutoOutput() {
        return mItemTransfer;
    }

    public boolean doesAutoOutputFluids() {
        return mFluidTransfer;
    }

    public boolean allowToCheckRecipe() {
        return true;
    }

    public boolean showPipeFacing() {
        return true;
    }

    /**
     * Called whenever the Machine successfully started a Process, useful for Sound Effects
     */
    public void startProcess() {
        //
    }

    /**
     * Called whenever the Machine successfully finished a Process, useful for Sound Effects
     */
    public void endProcess() {
        //
    }

    /**
     * Called whenever the Machine aborted a Process, useful for Sound Effects
     */
    public void abortProcess() {
        //
    }

    /**
     * Called whenever the Machine aborted a Process but still works on it, useful for Sound Effects
     */
    public void stutterProcess() {
        if (useStandardStutterSound()) sendSound((byte) 8);
    }

    /**
     * If this Machine can have the Insufficient Energy Line Problem
     */
    public boolean canHaveInsufficientEnergy() {
        return true;
    }

    public boolean useStandardStutterSound() {
        return true;
    }

    @Override
    public String[] getInfoData() {
        return new String[]{
                EnumChatFormatting.BLUE + mNEIName + EnumChatFormatting.RESET,
                "Progress:",
                EnumChatFormatting.GREEN + GT_Utility.formatNumbers((mProgresstime/20)) + EnumChatFormatting.RESET +" s / " +
                EnumChatFormatting.YELLOW + GT_Utility.formatNumbers(mMaxProgresstime / 20) + EnumChatFormatting.RESET + " s",
                "Stored Energy:",
                EnumChatFormatting.GREEN + GT_Utility.formatNumbers(getBaseMetaTileEntity().getStoredEU()) + EnumChatFormatting.RESET + " EU / " +
                EnumChatFormatting.YELLOW + GT_Utility.formatNumbers(getBaseMetaTileEntity().getEUCapacity()) + EnumChatFormatting.RESET + " EU",
                "Probably uses: " +
                        EnumChatFormatting.RED + GT_Utility.formatNumbers(mEUt) + EnumChatFormatting.RESET + " EU/t at " +
                        EnumChatFormatting.RED + GT_Utility.formatNumbers(mEUt == 0 ? 0 : mAmperage) + EnumChatFormatting.RESET +" A"
        };
    }

    @Override
    public boolean isGivingInformation() {
        return true;
    }

    @Override
    public void onScrewdriverRightClick(byte aSide, EntityPlayer aPlayer, float aX, float aY, float aZ) {
        if (aSide == getBaseMetaTileEntity().getFrontFacing() || aSide == mMainFacing) {
            if (aPlayer.isSneaking()){
                int tMode = mDisableFilter ? 0 : 2;
                tMode += mDisableMultiStack ? 0 : 1;

                switch (tMode) {
                    case 0: mDisableFilter = true;
                        mDisableMultiStack = false;
                        GT_Utility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("GT5U.hatch.disableFilter." + mDisableFilter));
                        GT_Utility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("GT5U.hatch.disableMultiStack." + mDisableMultiStack));
                        break;
                    case 1: mDisableFilter = false;
                        mDisableMultiStack = true;
                        GT_Utility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("GT5U.hatch.disableFilter." + mDisableFilter));
                        GT_Utility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("GT5U.hatch.disableMultiStack." + mDisableMultiStack));
                        break;
                    case 2: mDisableFilter = false;
                        mDisableMultiStack = false;
                        GT_Utility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("GT5U.hatch.disableFilter." + mDisableFilter));
                        GT_Utility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("GT5U.hatch.disableMultiStack." + mDisableMultiStack));
                        break;
                    case 3: mDisableFilter = true;
                        mDisableMultiStack = true;
                        GT_Utility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("GT5U.hatch.disableFilter." + mDisableFilter));
                        GT_Utility.sendChatToPlayer(aPlayer, StatCollector.translateToLocal("GT5U.hatch.disableMultiStack." + mDisableMultiStack));
                        break;
                }
            } else {
                mAllowInputFromOutputSide = !mAllowInputFromOutputSide;
                GT_Utility.sendChatToPlayer(aPlayer, mAllowInputFromOutputSide ? trans("095", "Input from Output Side allowed") : trans("096", "Input from Output Side forbidden"));
            }
        }
    }

    @Override
    public boolean allowCoverOnSide(byte aSide, GT_ItemStack aCoverID) {
        if (aSide != mMainFacing) return true;
        GT_CoverBehaviorBase<?> tBehavior = GregTech_API.getCoverBehaviorNew(aCoverID.toStack());
        return tBehavior.isGUIClickable(aSide, GT_Utility.stackToInt(aCoverID.toStack()), tBehavior.createDataObject(), getBaseMetaTileEntity());}

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return aSide != mMainFacing && aIndex >= getOutputSlot() && aIndex < getOutputSlot() + mOutputItems.length;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        if (aSide == mMainFacing || aIndex < getInputSlot() || aIndex >= getInputSlot() + mInputSlotCount || (!mAllowInputFromOutputSide && aSide == aBaseMetaTileEntity.getFrontFacing()))
            return false;
        for (int i = getInputSlot(), j = i + mInputSlotCount; i < j; i++)
            if (GT_Utility.areStacksEqual(GT_OreDictUnificator.get(aStack), mInventory[i]) && (mDisableMultiStack || mInventory[i].stackSize + aStack.stackSize <= aStack.getMaxStackSize())) return i == aIndex;
        return mDisableFilter || allowPutStackValidated(aBaseMetaTileEntity, aIndex, aSide, aStack);
    }

    /**
     * Test if given stack can be inserted into specified slot.
     * Before execution of this method it is ensured there is no such kind of item inside any input slots already.
     */
    protected boolean allowPutStackValidated(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return mInventory[aIndex] == null;
    }

    /**
     * @return the Recipe List which is used for this Machine, this is a useful Default Handler
     */
    public GT_Recipe_Map getRecipeList() {
        return null;
    }

    /**
     * Override this to check the Recipes yourself, super calls to this could be useful if you just want to add a special case
     * <p/>
     * I thought about Enum too, but Enum doesn't add support for people adding other return Systems.
     * <p/>
     * Funny how Eclipse marks the word Enum as not correctly spelled.
     *
     * @return see constants above
     */
    public int checkRecipe() {
        return checkRecipe(false);
    }

    public static boolean isValidForLowGravity(GT_Recipe tRecipe, int dimId){
        return //TODO check or get a better solution
                DimensionManager.getProvider(dimId).getClass().getName().contains("Orbit") ||
                DimensionManager.getProvider(dimId).getClass().getName().endsWith("Space") ||
                DimensionManager.getProvider(dimId).getClass().getName().endsWith("Asteroids") ||
                DimensionManager.getProvider(dimId).getClass().getName().endsWith("SS") ||
                DimensionManager.getProvider(dimId).getClass().getName().contains("SpaceStation");
    }


    /**
     *
     * @param skipOC disables OverclockedNess calculation and check - if you do you must implement your own method...
     * @return DID_NOT_FIND_RECIPE = 0,
     *         FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS = 1,
     *         FOUND_AND_SUCCESSFULLY_USED_RECIPE = 2;
     */
    public int checkRecipe(boolean skipOC){
        GT_Recipe_Map tMap = getRecipeList();
        if (tMap == null) return DID_NOT_FIND_RECIPE;
        GT_Recipe tRecipe = tMap.findRecipe(getBaseMetaTileEntity(), mLastRecipe, false, V[mTier], new FluidStack[]{getFillableStack()}, getSpecialSlot(), getAllInputs());
        if (tRecipe == null) return DID_NOT_FIND_RECIPE;

        if (GT_Mod.gregtechproxy.mLowGravProcessing && (tRecipe.mSpecialValue == -100 || tRecipe.mSpecialValue == -300) &&
                !isValidForLowGravity(tRecipe,getBaseMetaTileEntity().getWorld().provider.dimensionId))
            return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
        if (tRecipe.mCanBeBuffered) mLastRecipe = tRecipe;
        if (!canOutput(tRecipe)) {
            mOutputBlocked++;
            return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
        }
        if (tRecipe.mSpecialValue == -200 && (getCallbackBase() == null || getCallbackBase().mEfficiency == 0))
            return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
        if (!tRecipe.isRecipeInputEqual(true, new FluidStack[]{getFillableStack()}, getAllInputs()))
            return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
        for (int i = 0; i < mOutputItems.length; i++)
            if (getBaseMetaTileEntity().getRandomNumber(10000) < tRecipe.getOutputChance(i))
                mOutputItems[i] = tRecipe.getOutput(i);
        if (tRecipe.mSpecialValue == -200 || tRecipe.mSpecialValue == -300)
            for (int i = 0; i < mOutputItems.length; i++)
                if (mOutputItems[i] != null && getBaseMetaTileEntity().getRandomNumber(10000) > getCallbackBase().mEfficiency)
                {
					if (debugCleanroom) {
						GT_Log.out.println(
							"BasicMachine: Voiding output due to efficiency failure. mEfficiency = " + 
							getCallbackBase().mEfficiency
						);
					}
                    mOutputItems[i] = null;
                }
        mOutputFluid = tRecipe.getFluidOutput(0);
        if (!skipOC) {
            calculateOverclockedNess(tRecipe);
            //In case recipe is too OP for that machine
            if (mMaxProgresstime == Integer.MAX_VALUE - 1 && mEUt == Integer.MAX_VALUE - 1)
                return FOUND_RECIPE_BUT_DID_NOT_MEET_REQUIREMENTS;
        }
        return FOUND_AND_SUCCESSFULLY_USED_RECIPE;
    }

    @Override
    public void getWailaBody(ItemStack stack, List<String> currentTip, MovingObjectPosition pos, NBTTagCompound tag, int side) {
        super.getWailaBody(stack, currentTip, pos, tag, side);
        currentTip.add(String.format("Progress: %d s / %d s", tag.getInteger("progressSingleBlock"), tag.getInteger("maxProgressSingleBlock")));
    }

    @Override
    public void getWailaNBT(NBTTagCompound tag, World world, BlockCoord pos) {
        super.getWailaNBT(tag, world, pos);
        final int progressSingleBlock = mProgresstime / 20;
        final int maxProgressSingleBlock = mMaxProgresstime / 20;
        tag.setInteger("progressSingleBlock", progressSingleBlock);
        tag.setInteger("maxProgressSingleBlock", maxProgressSingleBlock);
    }

    public ITexture[] getSideFacingActive(byte aColor) {
        return new ITexture[]{MACHINE_CASINGS[mTier][aColor + 1]};
    }

    public ITexture[] getSideFacingInactive(byte aColor) {
        return new ITexture[]{MACHINE_CASINGS[mTier][aColor + 1]};
    }

    public ITexture[] getFrontFacingActive(byte aColor) {
        return new ITexture[]{MACHINE_CASINGS[mTier][aColor + 1]};
    }

    public ITexture[] getFrontFacingInactive(byte aColor) {
        return new ITexture[]{MACHINE_CASINGS[mTier][aColor + 1]};
    }

    public ITexture[] getTopFacingActive(byte aColor) {
        return new ITexture[]{MACHINE_CASINGS[mTier][aColor + 1]};
    }

    public ITexture[] getTopFacingInactive(byte aColor) {
        return new ITexture[]{MACHINE_CASINGS[mTier][aColor + 1]};
    }

    public ITexture[] getBottomFacingActive(byte aColor) {
        return new ITexture[]{MACHINE_CASINGS[mTier][aColor + 1]};
    }

    public ITexture[] getBottomFacingInactive(byte aColor) {
        return new ITexture[]{MACHINE_CASINGS[mTier][aColor + 1]};
    }

    public ITexture[] getBottomFacingPipeActive(byte aColor) {
        return new ITexture[]{MACHINE_CASINGS[mTier][aColor + 1], TextureFactory.of(OVERLAY_PIPE_OUT)};
    }

    public ITexture[] getBottomFacingPipeInactive(byte aColor) {
        return new ITexture[]{MACHINE_CASINGS[mTier][aColor + 1], TextureFactory.of(OVERLAY_PIPE_OUT)};
    }

    public ITexture[] getTopFacingPipeActive(byte aColor) {
        return new ITexture[]{MACHINE_CASINGS[mTier][aColor + 1], TextureFactory.of(OVERLAY_PIPE_OUT)};
    }

    public ITexture[] getTopFacingPipeInactive(byte aColor) {
        return new ITexture[]{MACHINE_CASINGS[mTier][aColor + 1], TextureFactory.of(OVERLAY_PIPE_OUT)};
    }

    public ITexture[] getSideFacingPipeActive(byte aColor) {
        return new ITexture[]{MACHINE_CASINGS[mTier][aColor + 1], TextureFactory.of(OVERLAY_PIPE_OUT)};
    }

    public ITexture[] getSideFacingPipeInactive(byte aColor) {
        return new ITexture[]{MACHINE_CASINGS[mTier][aColor + 1], TextureFactory.of(OVERLAY_PIPE_OUT)};
    }
}
