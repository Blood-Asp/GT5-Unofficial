package gregtech.common.tileentities.machines.multi;

import gregtech.api.GregTech_API;
import gregtech.api.enums.*;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.objects.ItemData;
import gregtech.api.util.*;
import gregtech.common.blocks.GT_Block_Ores_Abstract;
import gregtech.common.blocks.GT_TileEntity_Ores;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.ChunkPosition;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;

import static gregtech.api.enums.GT_Values.V;
import static gregtech.api.enums.GT_Values.VN;
import static gregtech.api.enums.GT_Values.W;

public abstract class GT_MetaTileEntity_OreDrillingPlantBase extends GT_MetaTileEntity_MultiBlockBase {
    private static final ItemStack miningPipe = GT_ModHandler.getIC2Item("miningPipe", 0);
    private static final ItemStack miningPipeTip = GT_ModHandler.getIC2Item("miningPipeTip", 0);
    private static final Block miningPipeBlock = GT_Utility.getBlockFromStack(miningPipe);
    private static final Block miningPipeTipBlock = GT_Utility.getBlockFromStack(miningPipeTip);

    private final ArrayList<ChunkPosition> oreBlockPositions = new ArrayList<>();
    private Block casingBlock;

    private int casingMeta;
    private int frameMeta;
    private int casingTextureIndex;

    private ForgeDirection back;
    private int xDrill, yDrill, zDrill, xCenter, zCenter, yHead;
    private boolean isPickingPipes;

    public GT_MetaTileEntity_OreDrillingPlantBase(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
        //initFields();
    }

    public GT_MetaTileEntity_OreDrillingPlantBase(String aName) {
        super(aName);
        initFields();
    }

    private void initFields() {
        xDrill = getBaseMetaTileEntity().getXCoord();
        yDrill = getBaseMetaTileEntity().getYCoord();
        zDrill = getBaseMetaTileEntity().getZCoord();
        back = ForgeDirection.getOrientation(getBaseMetaTileEntity().getBackFacing());
        xCenter = xDrill + back.offsetX;
        zCenter = zDrill + back.offsetZ;
        isPickingPipes = false;
        casingBlock = getCasingBlockItem().getBlock();
        casingMeta = getCasingBlockItem().get(0).getItemDamage();
        frameMeta = 4096 + getFrameMaterial().mMetaItemSubID;
        casingTextureIndex = getCasingTextureIndex();
    }

    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == aFacing) {
            return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[16], new GT_RenderedTexture(aActive ? Textures.BlockIcons.OVERLAY_FRONT_ADVMINER2_ACTIVE : Textures.BlockIcons.OVERLAY_FRONT_ADVMINER2)};
        }
        return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[16]};
    }

    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "OreDrillingPlant.png");
    }

    @Override
    public boolean checkRecipe(ItemStack aStack) {
        if (isPickingPipes) {
            if (tryPickPipe()) {
                mOutputItems = new ItemStack[] {GT_Utility.copyAmount(1, miningPipe)};
                setElectricityStats();
                return true;
            } else {
                stopMachine();
                return false;
            }
        }
        putMiningPipesFromInputsInController();
        if (!tryConsumeDrillingFluid()) return false;

        fillMineListIfEmpty();
        if (oreBlockPositions.isEmpty()) {
            boolean isMoved = moveOneDown();
            if (!isMoved) {
                isPickingPipes = true;
                setElectricityStats();
                return true;
            }
        }

        ChunkPosition oreBlockPos = null;
        Block oreBlock = null;

        while ((oreBlock == null || oreBlock == Blocks.air) && !oreBlockPositions.isEmpty()) {
            oreBlockPos = oreBlockPositions.remove(0);
            oreBlock = getBaseMetaTileEntity().getBlock(oreBlockPos.chunkPosX, oreBlockPos.chunkPosY, oreBlockPos.chunkPosZ);
        }

        if (oreBlock != null && oreBlock != Blocks.air) {
            ArrayList<ItemStack> oreBlockDrops = getBlockDrops(oreBlock, oreBlockPos.chunkPosX, oreBlockPos.chunkPosY, oreBlockPos.chunkPosZ);
            getBaseMetaTileEntity().getWorld().setBlockToAir(oreBlockPos.chunkPosX, oreBlockPos.chunkPosY, oreBlockPos.chunkPosZ);
            mOutputItems = getOutputByDrops(oreBlockDrops);
        }

        setElectricityStats();
        return true;
    }

    private boolean tryPickPipe() {
        if (yHead == yDrill) return false;
        if (checkBlockAndMeta(xCenter, yHead + 1, zCenter, miningPipeBlock, W))
            getBaseMetaTileEntity().getWorld().setBlock(xCenter, yHead + 1, zCenter, miningPipeTipBlock);
        getBaseMetaTileEntity().getWorld().setBlockToAir(xCenter, yHead, zCenter);
        return true;
    }

    private void setElectricityStats() {
        this.mEfficiency = getCurrentEfficiency(null);
        this.mEfficiencyIncrease = 10000;
        //T1 = 12; T2 = 48; T3 = 192; T4 = 768
        this.mEUt = 3 * (1 << (getMinTier() << 1));
        //T1 = 960; T2 = 480; T3 = 240; T4 = 120
        this.mMaxProgresstime = 1920 / (1 << getMinTier());

        long voltage = getMaxInputVoltage();
        long overclockEu = V[Math.max(1, GT_Utility.getTier(voltage)) - 1];
        while (this.mEUt <= overclockEu) {
            this.mEUt *= 4;
            this.mMaxProgresstime /= 2;
        }

        this.mEUt = -this.mEUt;
        this.mMaxProgresstime = Math.max(1, this.mMaxProgresstime);
    }

    private ItemStack[] getOutputByDrops(ArrayList<ItemStack> oreBlockDrops) {
        long voltage = getMaxInputVoltage();
        ArrayList<ItemStack> outputItems = new ArrayList<>();
        while (!oreBlockDrops.isEmpty()) {
            ItemStack currentItem = oreBlockDrops.remove(0).copy();
            if (!doUseMaceratorRecipe(currentItem)) {
                multiplyStackSize(currentItem);
                outputItems.add(currentItem);
                continue;
            }

            GT_Recipe tRecipe = GT_Recipe.GT_Recipe_Map.sMaceratorRecipes.findRecipe(getBaseMetaTileEntity(), false, voltage, null, currentItem);
            if (tRecipe == null) {
                outputItems.add(currentItem);
                continue;
            }

            for (int i = 0; i < tRecipe.mOutputs.length; i++) {
                ItemStack recipeOutput = tRecipe.mOutputs[i].copy();
                if (getBaseMetaTileEntity().getRandomNumber(10000) < tRecipe.getOutputChance(i))
                    multiplyStackSize(recipeOutput);
                outputItems.add(recipeOutput);
            }
        }
        return GT_Utility.listToArray(outputItems);
    }

    private boolean doUseMaceratorRecipe(ItemStack currentItem) {
        ItemData itemData = GT_OreDictUnificator.getItemData(currentItem);
        return itemData == null
                || itemData.mPrefix != OrePrefixes.crushed
                && itemData.mPrefix != OrePrefixes.dustImpure
                && itemData.mPrefix != OrePrefixes.dust
                && itemData.mMaterial.mMaterial != Materials.Oilsands;
    }

    private void multiplyStackSize(ItemStack itemStack) {
        itemStack.stackSize *= getBaseMetaTileEntity().getRandomNumber(4) + 1;
    }

    private ArrayList<ItemStack> getBlockDrops(final Block oreBlock, int posX, int posY, int posZ) {
        final int blockMeta = getBaseMetaTileEntity().getMetaID(posX, posY, posZ);
        if (oreBlock.canSilkHarvest(getBaseMetaTileEntity().getWorld(), null, posX, posY, posZ, blockMeta)) {
            return new ArrayList<ItemStack>() {{
                add(new ItemStack(oreBlock, 1, blockMeta));
            }};
        } else {
            return oreBlock.getDrops(getBaseMetaTileEntity().getWorld(), posX, posY, posZ, blockMeta, 1);
        }
    }

    private boolean tryConsumeDrillingFluid() {
        return depleteInput(new FluidStack(ItemList.sDrillingFluid, 2000));
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

    private void fillMineListIfEmpty() {
        if (!oreBlockPositions.isEmpty()) return;

        tryAddOreBlockToMineList(xCenter, yHead - 1, zCenter);
        if (yHead == yDrill) return; //skip controller block layer

        int radius = getRadiusInChunks() << 4;
        for (int xOff = -radius; xOff <= radius; xOff++) {
            for (int zOff = -radius; zOff <= radius; zOff++) {
                tryAddOreBlockToMineList(xDrill + xOff, yHead, zDrill + zOff);
            }
        }
    }

    private void tryAddOreBlockToMineList(int x, int y, int z) {
        Block block = getBaseMetaTileEntity().getBlock(x, y, z);
        int blockMeta = getBaseMetaTileEntity().getMetaID(x, y, z);
        ChunkPosition blockPos = new ChunkPosition(x, y, z);
        if (block instanceof GT_Block_Ores_Abstract) {
            TileEntity tTileEntity = getBaseMetaTileEntity().getTileEntity(x, y, z);
            if (tTileEntity != null && tTileEntity instanceof GT_TileEntity_Ores && ((GT_TileEntity_Ores) tTileEntity).mNatural && !oreBlockPositions.contains(blockPos)) {
                oreBlockPositions.add(blockPos);
            }
        } else {
            ItemData association = GT_OreDictUnificator.getAssociation(new ItemStack(block, 1, blockMeta));
            if (association != null && association.mPrefix.toString().startsWith("ore") && !oreBlockPositions.contains(blockPos)) {
                oreBlockPositions.add(blockPos);
            }
        }
    }

    private boolean moveOneDown() {
        if (!isHasMiningPipes()) return false;

        if (yHead <= 0) return false;
        if (checkBlockAndMeta(xCenter, yHead - 1, zCenter, Blocks.bedrock, 0)) return false;

        if (!getBaseMetaTileEntity().getWorld().setBlock(xCenter, yHead - 1, zCenter, miningPipeTipBlock)) return false;
        if (yHead != yDrill) getBaseMetaTileEntity().getWorld().setBlock(xCenter, yHead, zCenter, miningPipeBlock);

        getBaseMetaTileEntity().decrStackSize(1, 1);
        return true;
    }

    private boolean isHasMiningPipes() {
        return isHasMiningPipes(1);
    }

    private boolean isHasMiningPipes(int minCount) {
        ItemStack pipe = getStackInSlot(1);
        return pipe != null && pipe.stackSize > minCount - 1 && pipe.isItemEqual(miningPipe);
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        //check base layer
        for (int xOff = -1 + back.offsetX; xOff <= 1 + back.offsetX; xOff++) {
            for (int zOff = -1 + back.offsetZ; zOff <= 1 + back.offsetZ; zOff++) {
                if (xOff == 0 && zOff == 0) continue;

                IGregTechTileEntity tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(xOff, 0, zOff);
                if (addMaintenanceToMachineList(tTileEntity, casingTextureIndex)
                        || addInputToMachineList(tTileEntity, casingTextureIndex)
                        || addOutputToMachineList(tTileEntity, casingTextureIndex)
                        || addEnergyInputToMachineList(tTileEntity, casingTextureIndex))
                    continue;

                if (!checkCasingBlock(xOff, 0, zOff)) return false;
            }
        }
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
        return checkPipesAndSetYHead();
    }

    private boolean checkPipesAndSetYHead() {
        yHead = yDrill - 1;
        while (checkBlockAndMeta(xCenter, yHead, zCenter, miningPipeBlock, W)) yHead--;
        if (checkBlockAndMeta(xCenter, yHead, zCenter, miningPipeTipBlock, W)) return true;
        yHead++;
        return yHead == yDrill || getBaseMetaTileEntity().getWorld().setBlock(xCenter, yHead, zCenter, miningPipeTipBlock);
    }

    private boolean checkCasingBlock(int xOff, int yOff, int zOff) {
        return checkBlockAndMetaOffset(xOff, yOff, zOff, casingBlock, casingMeta);
    }

    private boolean checkFrameBlock(int xOff, int yOff, int zOff) {
        return checkBlockAndMetaOffset(xOff, yOff, zOff, GregTech_API.sBlockMachines, frameMeta);
    }

    private boolean checkBlockAndMetaOffset(int xOff, int yOff, int zOff, Block block, int meta) {
        return checkBlockAndMeta(xDrill + xOff, yDrill + yOff, zDrill + zOff, block, meta);
    }

    private boolean checkBlockAndMeta(int x, int y, int z, Block block, int meta) {
        return (meta == W || getBaseMetaTileEntity().getMetaID(x, y, z) == meta)
                && getBaseMetaTileEntity().getBlock(x, y, z) == block;
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

    protected abstract ItemList getCasingBlockItem();

    protected abstract Materials getFrameMaterial();

    protected abstract int getCasingTextureIndex();

    protected abstract int getRadiusInChunks();

    protected abstract int getMinTier();

    protected String[] getDescriptionInternal(String tierSuffix) {
        String casings = getCasingBlockItem().get(0).getDisplayName();
        return new String[]{
                "Controller Block for the Ore Drilling Plant " + (tierSuffix != null ? tierSuffix : ""),
                "Size(WxHxD): 3x7x3, Controller (Front middle bottom)",
                "3x1x3 Base of " + casings,
                "1x3x1 " + casings + " pillar (Center of base)",
                "1x3x1 " + getFrameMaterial().mName + " Frame Boxes (Each pillar side and on top)",
                "1x Input Hatch (Any bottom layer casing)",
                "1x Output Bus (Any bottom layer casing)",
                "1x Maintenance Hatch (Any bottom layer casing)",
                "1x " + VN[getMinTier()] + "+ Energy Hatch (Any bottom layer casing)"};
    }
}
