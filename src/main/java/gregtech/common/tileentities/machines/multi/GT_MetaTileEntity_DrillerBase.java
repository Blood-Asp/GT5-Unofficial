package gregtech.common.tileentities.machines.multi;

import static gregtech.api.enums.GT_Values.W;

import java.util.ArrayList;

import gregtech.api.GregTech_API;
import gregtech.api.enums.ItemList;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_DataAccess;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Energy;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_Utility;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class GT_MetaTileEntity_DrillerBase extends GT_MetaTileEntity_MultiBlockBase {
    private static final ItemStack miningPipe = GT_ModHandler.getIC2Item("miningPipe", 0);
    private static final ItemStack miningPipeTip = GT_ModHandler.getIC2Item("miningPipeTip", 0);
    private static final Block miningPipeBlock = GT_Utility.getBlockFromStack(miningPipe);
    private static final Block miningPipeTipBlock = GT_Utility.getBlockFromStack(miningPipeTip);

    private Block casingBlock;
    private int casingMeta;
    private int frameMeta;
    private int casingTextureIndex;

    private ForgeDirection back;

    private int xDrill, yDrill, zDrill, xPipe, zPipe, yHead;
    protected boolean isPickingPipes;

    public GT_MetaTileEntity_DrillerBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        initFields();
    }

    public GT_MetaTileEntity_DrillerBase(String aName) {
        super(aName);
        initFields();
    }

    private void initFields() {
        casingBlock = getCasingBlockItem().getBlock();
        casingMeta = getCasingBlockItem().get(0).getItemDamage();
        int frameId = 4096 + getFrameMaterial().mMetaItemSubID;
        frameMeta = GregTech_API.METATILEENTITIES[frameId] != null ? GregTech_API.METATILEENTITIES[frameId].getTileEntityBaseType() : W;
        casingTextureIndex = getCasingTextureIndex();
        isPickingPipes = false;
    }

    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing)
            return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[casingTextureIndex],new GT_RenderedTexture(aActive ? Textures.BlockIcons.OVERLAY_FRONT_ORE_DRILL_ACTIVE : Textures.BlockIcons.OVERLAY_FRONT_ORE_DRILL)};
        return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[casingTextureIndex]};
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT) {
        super.saveNBTData(aNBT);
        aNBT.setBoolean("isPickingPipe", isPickingPipes);
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        isPickingPipes = aNBT.getBoolean("isPickingPipes");
    }

    protected boolean tryPickPipe() {
        if (yHead == yDrill) return false;
        if (tryOutputPipe()){
            if (checkBlockAndMeta(xPipe, yHead + 1, zPipe, miningPipeBlock, W))
                getBaseMetaTileEntity().getWorld().setBlock(xPipe, yHead + 1, zPipe, miningPipeTipBlock);
            getBaseMetaTileEntity().getWorld().setBlockToAir(xPipe, yHead, zPipe);
            return true;
        }
        return false;
    }

    protected boolean tryLowerPipe() {
        if (!isHasMiningPipes()) return false;

        if (yHead <= 0) return false;
        if (!canLowerPipe()) return false;

        getBaseMetaTileEntity().getWorld().setBlock(xPipe, yHead - 1, zPipe, miningPipeTipBlock);
        if (yHead != yDrill) getBaseMetaTileEntity().getWorld().setBlock(xPipe, yHead, zPipe, miningPipeBlock);

        getBaseMetaTileEntity().decrStackSize(1, 1);
        return true;
    }

    private void putMiningPipesFromInputsInController() {
        int maxPipes = miningPipe.getMaxStackSize();
        if (isHasMiningPipes(maxPipes)) return;

        ItemStack pipes = getStackInSlot(1);
        for (ItemStack storedItem : getStoredInputs()) {
            if (!storedItem.isItemEqual(miningPipe)) continue;

            if (pipes == null) {
                setInventorySlotContents(1, GT_Utility.copy(miningPipe));
                pipes = getStackInSlot(1);
            }

            if (pipes.stackSize == maxPipes) break;

            int needPipes = maxPipes - pipes.stackSize;
            int transferPipes = storedItem.stackSize < needPipes ? storedItem.stackSize : needPipes;

            pipes.stackSize += transferPipes;
            storedItem.stackSize -= transferPipes;
        }
        updateSlots();
    }

    private boolean tryOutputPipe(){
        if (!getBaseMetaTileEntity().addStackToSlot(1, GT_Utility.copyAmount(1, miningPipe)))
            mOutputItems = new ItemStack[] {GT_Utility.copyAmount(1, miningPipe)};
        return true;
    }

    protected boolean canLowerPipe(){
        return yHead > 0 && !checkBlockAndMeta(xPipe, yHead - 1, zPipe, Blocks.bedrock, W);
    }

    private boolean isHasMiningPipes() {
        return isHasMiningPipes(1);
    }

    private boolean isHasMiningPipes(int minCount) {
        ItemStack pipe = getStackInSlot(1);
        return pipe != null && pipe.stackSize > minCount - 1 && pipe.isItemEqual(miningPipe);
    }

    private boolean isEnergyEnough() {
        long requiredEnergy = 512 + getMaxInputVoltage() * 4;
        for (GT_MetaTileEntity_Hatch_Energy energyHatch : mEnergyHatches) {
            requiredEnergy -= energyHatch.getEUVar();
            if (requiredEnergy <= 0) return true;
        }
        return false;
    }

    protected boolean workingDownward(ItemStack aStack, int xDrill, int yDrill, int zDrill, int xPipe, int zPipe, int yHead, int oldYHead){
        if(!tryLowerPipe())
            if(waitForPipes()) return false;
        isPickingPipes = true;
        return true;
    }

    protected boolean workingUpward(ItemStack aStack, int xDrill, int yDrill, int zDrill, int xPipe, int zPipe, int yHead, int oldYHead) {
        if (tryPickPipe()) {
            return true;
        } else {
            isPickingPipes = false;
            stopMachine();
            return false;
        }
    }

    @Override
    public boolean checkRecipe(ItemStack aStack) {
        //Public pipe actions
        setElectricityStats();
        int oldYHead = yHead;
        if (!checkPipesAndSetYHead() || !isEnergyEnough()) {
            stopMachine();
            return false;
        }
        putMiningPipesFromInputsInController();
        if (!isPickingPipes)
            return workingDownward(aStack, xDrill, yDrill, zDrill, xPipe, zPipe, yHead, oldYHead);
        else
            return workingUpward(aStack, xDrill, yDrill, zDrill, xPipe, zPipe, yHead, oldYHead);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        updateCoordinates();
        //check base layer
        for (int xOff = -1 + back.offsetX; xOff <= 1 + back.offsetX; xOff++) {
            for (int zOff = -1 + back.offsetZ; zOff <= 1 + back.offsetZ; zOff++) {
                if (xOff == 0 && zOff == 0) continue;

                IGregTechTileEntity tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(xOff, 0, zOff);
                if (!checkCasingBlock(xOff, 0, zOff)
                        && !addMaintenanceToMachineList(tTileEntity, casingTextureIndex)
                        && !addInputToMachineList(tTileEntity, casingTextureIndex)
                        && !addOutputToMachineList(tTileEntity, casingTextureIndex)
                        && !addEnergyInputToMachineList(tTileEntity, casingTextureIndex)
                        && !addDataAccessToMachineList(tTileEntity, casingTextureIndex))
                    return false;
            }
        }
        if(!checkHatches()) return false;
        if (GT_Utility.getTier(getMaxInputVoltage()) < getMinTier()) return false;
        //check tower
        for (int yOff = 1; yOff < 4; yOff++) {
            if (!checkCasingBlock(back.offsetX, yOff, back.offsetZ)
                    || !checkFrameBlock(back.offsetX + 1, yOff, back.offsetZ)
                    || !checkFrameBlock(back.offsetX - 1, yOff, back.offsetZ)
                    || !checkFrameBlock(back.offsetX, yOff, back.offsetZ + 1)
                    || !checkFrameBlock(back.offsetX, yOff, back.offsetZ - 1)
                    || !checkFrameBlock(back.offsetX, yOff + 3, back.offsetZ))
                return false;
        }
        return true;
    }

    private void updateCoordinates() {
        xDrill = getBaseMetaTileEntity().getXCoord();
        yDrill = getBaseMetaTileEntity().getYCoord();
        zDrill = getBaseMetaTileEntity().getZCoord();
        back = ForgeDirection.getOrientation(getBaseMetaTileEntity().getBackFacing());
        xPipe = xDrill + back.offsetX;
        zPipe = zDrill + back.offsetZ;
    }

    private boolean checkPipesAndSetYHead() {
        yHead = yDrill - 1;
        while (checkBlockAndMeta(xPipe, yHead, zPipe, miningPipeBlock, W)) yHead--; //skip pipes
        //is pipe tip OR is controller layer
        if (checkBlockAndMeta(xPipe, yHead, zPipe, miningPipeTipBlock, W) || ++yHead == yDrill) return true;
        //pipe column is broken - try fix
        getBaseMetaTileEntity().getWorld().setBlock(xPipe, yHead, zPipe, miningPipeTipBlock);
        return true;
    }

    protected boolean checkCasingBlock(int xOff, int yOff, int zOff) {
        return checkBlockAndMetaOffset(xOff, yOff, zOff, casingBlock, casingMeta);
    }
    //meta of frame is getTileEntityBaseType; frame should be checked using its drops (possible a high weight operation)
    protected boolean checkFrameBlock(int xOff, int yOff, int zOff) {
        return checkBlockAndMetaOffset(xOff, yOff, zOff, GregTech_API.sBlockMachines, frameMeta);
    }

    protected boolean checkBlockAndMetaOffset(int xOff, int yOff, int zOff, Block block, int meta) {
        return checkBlockAndMeta(xDrill + xOff, yDrill + yOff, zDrill + zOff, block, meta);
    }

    private boolean checkBlockAndMeta(int x, int y, int z, Block block, int meta) {
        return (meta == W || getBaseMetaTileEntity().getMetaID(x, y, z) == meta)
                && getBaseMetaTileEntity().getBlock(x, y, z) == block;
    }

    protected boolean waitForPipes(){
        if (canLowerPipe()) {
            mMaxProgresstime = 0;
            return true;
        }
        return false;
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public int getMaxEfficiency(ItemStack aStack) {
        return 10000;
    }

    @Override
    public int getPollutionPerTick(ItemStack aStack) {
        return 0;
    }

    @Override
    public int getDamageToComponent(ItemStack aStack) {
        return 0;
    }

    @Override
    public boolean explodesOnComponentBreak(ItemStack aStack) {
        return false;
    }

    public abstract Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity);

    protected abstract ItemList getCasingBlockItem();

    protected abstract Materials getFrameMaterial();

    protected abstract int getCasingTextureIndex();

    protected abstract int getMinTier();

    protected abstract boolean checkHatches();

    protected abstract void setElectricityStats();

    public int getTotalConfigValue(){
        int config = 0;
        ArrayList<ItemStack> tCircuitList = getDataItems(1);
        for (ItemStack tCircuit : tCircuitList)
            config += tCircuit.getItemDamage();
        return config;
    }

    public ArrayList<GT_MetaTileEntity_Hatch_DataAccess> mDataAccessHatches = new ArrayList<GT_MetaTileEntity_Hatch_DataAccess>();

    /**
     * @param state using bitmask, 1 for IntegratedCircuit, 2 for DataStick, 4 for DataOrb
     */
    private boolean isCorrectDataItem(ItemStack aStack, int state){
        if ((state & 1) != 0 && ItemList.Circuit_Integrated.isStackEqual(aStack, true, true)) return true;
        if ((state & 2) != 0 && ItemList.Tool_DataStick.isStackEqual(aStack, false, true)) return true;
        if ((state & 4) != 0 && ItemList.Tool_DataOrb.isStackEqual(aStack, false, true)) return true;
        return false;
    }

    /**
     * @param state using bitmask, 1 for IntegratedCircuit, 2 for DataStick, 4 for DataOrb
     */
    public ArrayList<ItemStack> getDataItems(int state) {
        ArrayList<ItemStack> rList = new ArrayList<ItemStack>();
        if (GT_Utility.isStackValid(mInventory[1]) && isCorrectDataItem(mInventory[1], state)) {
            rList.add(mInventory[1]);
        }
        for (GT_MetaTileEntity_Hatch_DataAccess tHatch : mDataAccessHatches) {
            if (isValidMetaTileEntity(tHatch)) {
                for (int i = 0; i < tHatch.getBaseMetaTileEntity().getSizeInventory(); i++) {
                    if (tHatch.getBaseMetaTileEntity().getStackInSlot(i) != null
                            && isCorrectDataItem(tHatch.getBaseMetaTileEntity().getStackInSlot(i), state))
                        rList.add(tHatch.getBaseMetaTileEntity().getStackInSlot(i));
                }
            }
        }
        return rList;
    }

    public boolean addDataAccessToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
        if (aTileEntity == null) return false;
        IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
        if (aMetaTileEntity == null) return false;
        if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_DataAccess) {
            ((GT_MetaTileEntity_Hatch) aMetaTileEntity).mMachineBlock = (byte) aBaseCasingIndex;
            return mDataAccessHatches.add((GT_MetaTileEntity_Hatch_DataAccess) aMetaTileEntity);
        }
        return false;
    }

}