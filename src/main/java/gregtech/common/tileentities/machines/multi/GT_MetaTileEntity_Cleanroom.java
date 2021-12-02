package gregtech.common.tileentities.machines.multi;

import java.util.HashMap;
import java.util.Map;

import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMachineCallback;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_BasicHull;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Log;
import gregtech.api.util.GT_Multiblock_Tooltip_Builder;
import gregtech.api.util.GT_Recipe;
import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;
import org.lwjgl.input.Keyboard;

import static gregtech.api.enums.GT_Values.debugCleanroom;
import static gregtech.api.enums.Textures.BlockIcons.*;

public class GT_MetaTileEntity_Cleanroom extends GT_MetaTileEntity_MultiBlockBase {
    private int mHeight = -1;

    public GT_MetaTileEntity_Cleanroom(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional);
    }

    public GT_MetaTileEntity_Cleanroom(String aName) {
        super(aName);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_Cleanroom(mName);
    }

    @Override
    public String[] getDescription() {
        final GT_Multiblock_Tooltip_Builder tt = new GT_Multiblock_Tooltip_Builder();
        tt.addMachineType("Cleanroom")
                .addInfo("Controller block for the Cleanroom")
                .addInfo("Consumes 40 EU/t when first turned on")
                .addInfo("and 4 EU/t once at 100% efficiency when not overclocked")//?
                .addInfo("An energy hatch accepts up to 2A, so you can use 2A LV or 1A MV")
                .addInfo("2 LV batteries + 1 LV generator or 1 MV generator")//?
                .addInfo("Time required to reach full efficiency is proportional to")
                .addInfo("the height of empty space within")
                .addInfo("Make sure your Energy Hatch matches! ?")
                .addSeparator()
                .beginVariableStructureBlock(3, 15, 4, 15, 3, 15, true)
                .addController("Top center")
                .addCasingInfo("Plascrete", 20)
                .addStructureInfo(GT_Values.cleanroomGlass + "% of the Plascrete can be replaced with Reinforced Glass")//check
                .addStructureInfo("Other material can be used in place of Plascrete. See config for detail")//check
                .addOtherStructurePart("Filter Machine Casing", "Top besides controller and edges")
                .addEnergyHatch("Any casing. Exactly one.")//check
                .addMaintenanceHatch("Any casing")
                .addStructureInfo("1x Reinforced Door (keep closed or efficiency will reduce)")
                .addStructureInfo("Up to 10 Machine Hulls for Item & Energy transfer through walls")
                .addStructureInfo("You can also use Diodes for more power")
                .addStructureInfo("Diodes also count towards 10 Machine Hulls count limit")
                .toolTipFinisher("Gregtech");
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            return tt.getStructureInformation();
        } else {
            return tt.getInformation();
        }
    }

    @Override
    public boolean checkRecipe(ItemStack aStack) {
        mEfficiencyIncrease = 100;
        // use the standard overclock mechanism to determine duration and estimate a maximum consumption
        calculateOverclockedNessMulti(40, 45 * Math.max(1, mHeight - 1), 1, getMaxInputVoltage());
        // negate it to trigger the special energy consumption function. divide by 10 to get the actual final consumption.
        mEUt /= -10;
        return true;
    }

    @Override
    public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
        int x = 1;
        int z = 1;
        int y = 1;
        int mDoorCount = 0;
        int mHullCount = 0;
        int mPlascreteCount = 0;
        HashMap<String, Integer> otherBlocks = new HashMap<>();
        boolean doorState = false;
        this.mUpdate = 100;

        if (debugCleanroom) {
            GT_Log.out.println(
                    "Cleanroom: Checking machine"
            );
        }
        for (int i = 1; i < 8; i++) {
            Block tBlock = aBaseMetaTileEntity.getBlockOffset(i, 0, 0);
            int tMeta = aBaseMetaTileEntity.getMetaIDOffset(i, 0, 0);
            if (tBlock != GregTech_API.sBlockCasings3 || tMeta != 11) {
                if (tBlock == GregTech_API.sBlockReinforced || tMeta == 2) {
                    x = i;
                    break;
                } else {
                    if (debugCleanroom) {
                        GT_Log.out.println("Cleanroom: Unable to detect room X edge?");
                    }
                    return false;
                }
            }
        }
        for (int i = 1; i < 8; i++) {
            Block tBlock = aBaseMetaTileEntity.getBlockOffset(0, 0, i);
            int tMeta = aBaseMetaTileEntity.getMetaIDOffset(0, 0, i);
            if (tBlock != GregTech_API.sBlockCasings3 || tMeta != 11) {
                if (tBlock == GregTech_API.sBlockReinforced || tMeta == 2) {
                    z = i;
                    break;
                } else {
                    if (debugCleanroom) {
                        GT_Log.out.println("Cleanroom: Unable to detect room Z edge?");
                    }
                    return false;
                }
            }
        }
        //detect room square for filters
        for (int i = -x + 1; i < x; i++) {
            for (int j = -z + 1; j < z; j++) {
                if (i == 0 && j == 0) continue;
                Block tBlock = aBaseMetaTileEntity.getBlockOffset(j, 0, i);
                int tMeta = aBaseMetaTileEntity.getMetaIDOffset(j, 0, i);
                if (tBlock != GregTech_API.sBlockCasings3 && tMeta != 11) {
                    return false;
                }
            }
        }

        for (int i = -1; i > -16; i--) {
            Block tBlock = aBaseMetaTileEntity.getBlockOffset(x, i, z);
            int tMeta = aBaseMetaTileEntity.getMetaIDOffset(x, i, z);
            if (tBlock != GregTech_API.sBlockReinforced || tMeta != 2) {
                y = i + 1;
                break;
            }
        }
        if (y > -2) {
            if (debugCleanroom) {
                GT_Log.out.println(
                        "Cleanroom: Room not tall enough?"
                );
            }
            return false;
        }
        for (int dX = -x; dX <= x; dX++) {
            for (int dZ = -z; dZ <= z; dZ++) {
                for (int dY = 0; dY >= y; dY--) {
                    if (dX == -x || dX == x || dY == 0 || dY == y || dZ == -z || dZ == z) {
                        Block tBlock = aBaseMetaTileEntity.getBlockOffset(dX, dY, dZ);
                        int tMeta = aBaseMetaTileEntity.getMetaIDOffset(dX, dY, dZ);
                        if (dY == 0) {                                            // TOP
                            if (dX == -x || dX == x || dZ == -z || dZ == z) {    // Top Border
                                if (tBlock != GregTech_API.sBlockReinforced || tMeta != 2) {
                                    if (debugCleanroom) {
                                        GT_Log.out.println(
                                                "Cleanroom: Non reinforced block on top edge? tMeta != 2"
                                        );
                                    }
                                    return false;
                                }
                            } else if (dX != 0 || dZ != 0) {                     // Top Inner exclude center
                                if (tBlock != GregTech_API.sBlockCasings3 || tMeta != 11) {
                                    if (debugCleanroom) {
                                        GT_Log.out.println(
                                                "Cleanroom: Non reinforced block on top face interior? tMeta != 11"
                                        );
                                    }
                                    return false;
                                }
                            }
                        } else if (tBlock == GregTech_API.sBlockReinforced && tMeta == 2) {
                            mPlascreteCount++;
                        } else {
                            IGregTechTileEntity tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(dX, dY, dZ);
                            if ((!this.addMaintenanceToMachineList(tTileEntity, 210)) && (!this.addEnergyInputToMachineList(tTileEntity, 210))) {
                                if (tBlock instanceof ic2.core.block.BlockIC2Door) {
                                    if ((tMeta & 8) == 0) {
                                        // let's not fiddle with bits anymore.
                                        if (Math.abs(dZ) < z) // on side parallel to z axis
                                            doorState = tMeta == 1 || tMeta == 3 || tMeta == 4 || tMeta == 6;
                                        else if (Math.abs(dX) < x) // on side parallel to x axis
                                            doorState = tMeta == 0 || tMeta == 2 || tMeta == 5 || tMeta == 7;
                                        // corners ignored
                                    }
                                    mDoorCount++;
                                } else {
                                    if (tTileEntity != null) {
                                        IMetaTileEntity aMetaTileEntity = tTileEntity.getMetaTileEntity();
                                        if (aMetaTileEntity == null) {
                                            if (debugCleanroom) {
                                                GT_Log.out.println(
                                                    "Cleanroom: Missing block? Not a aMetaTileEntity"
                                                );
                                            }
                                            return false;
                                        }
                                        if (aMetaTileEntity instanceof GT_MetaTileEntity_BasicHull) {
                                            mHullCount++;
                                        }
                                        else {
                                            if (debugCleanroom) {
                                                GT_Log.out.println(
                                                    "Cleanroom: Incorrect GT block? " + tBlock.getUnlocalizedName()
                                                );
                                            }
                                            return false;
                                        }
                                    }
                                    else {
                                        String key = tBlock.getUnlocalizedName() + ":"+ tMeta;
                                        if (config.containsKey(key)) { // check with meta first
                                            otherBlocks.compute(key, (k, v) -> v == null ? 1 : v + 1);
                                        }
                                        else {
                                            key = tBlock.getUnlocalizedName();
                                            if (config.containsKey(key)) {
                                                otherBlocks.compute(key, (k, v) -> v == null ? 1 : v + 1);
                                            }
                                            else {
                                                if (debugCleanroom)
                                                    GT_Log.out.println("Cleanroom: not allowed block " + tBlock.getUnlocalizedName());
                                                return false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (this.mMaintenanceHatches.size() != 1 || this.mEnergyHatches.size() != 1 || mDoorCount > 2 || mHullCount > 10) {
            return false;
        }
        if (mPlascreteCount < 20)
            return false;
        float ratio = (((float) mPlascreteCount) / 100f);
        for (Map.Entry<String, Integer> e : otherBlocks.entrySet()) {
            ConfigEntry ce = config.get(e.getKey());
            if (ce.allowedCount > 0) { // count has priority
                if (e.getValue() > ce.allowedCount)
                    return false;
            } else if (e.getValue() > ratio * ce.percentage)
                return false;
        }

        setCallbacks(x, y, z, aBaseMetaTileEntity);

        if (doorState) {
            this.mEfficiency = Math.max(0, this.mEfficiency - 200);
        }
        for (byte i = 0; i < 6; i++) {
            byte t = (byte) Math.max(1, (byte) (15 / (10000f / this.mEfficiency)));
            aBaseMetaTileEntity.setInternalOutputRedstoneSignal(i, t);
        }
        this.mHeight = -y;
        return true;
    }

    private void setCallbacks(int x, int y, int z, IGregTechTileEntity aBaseMetaTileEntity) {
        for (int dX = -x + 1; dX <= x - 1; dX++)
            for (int dZ = -z + 1; dZ <= z - 1; dZ++)
                for (int dY = -1; dY >= y + 1; dY--) {
                    TileEntity tTileEntity = aBaseMetaTileEntity.getTileEntityOffset(dX, dY, dZ);

                    if (tTileEntity instanceof IGregTechTileEntity) {
                        IMetaTileEntity iMetaTileEntity = ((IGregTechTileEntity) tTileEntity).getMetaTileEntity();

                        if (iMetaTileEntity instanceof IMachineCallback<?>)
                            checkAndSetCallback((IMachineCallback<?>) iMetaTileEntity);

                    } else if (tTileEntity instanceof IMachineCallback<?>)
                        checkAndSetCallback((IMachineCallback<?>) tTileEntity);
                }
    }

    @SuppressWarnings("unchecked")
    private void checkAndSetCallback(IMachineCallback<?> iMachineCallback) {
        if (debugCleanroom)
            GT_Log.out.println(
                    "Cleanroom: IMachineCallback detected, checking for cleanroom: " + (iMachineCallback.getType() == this.getClass())
            );
        if (iMachineCallback.getType() == this.getClass())
            ((IMachineCallback<GT_MetaTileEntity_Cleanroom>) iMachineCallback).setCallbackBase(this);
    }

    @Override
    public boolean allowGeneralRedstoneOutput() {
        return true;
    }

    @Override
    public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone) {
        if (aSide == ForgeDirection.DOWN.ordinal() || aSide == ForgeDirection.UP.ordinal()) {
            return new ITexture[]{
                    TextureFactory.of(BLOCK_PLASCRETE),
                    aActive ?
                            TextureFactory.of(
                                    TextureFactory.of(OVERLAY_TOP_CLEANROOM_ACTIVE),
                                    TextureFactory.builder().addIcon(OVERLAY_TOP_CLEANROOM_ACTIVE_GLOW).glow().build()) :
                            TextureFactory.of(
                                    TextureFactory.of(OVERLAY_TOP_CLEANROOM),
                                    TextureFactory.builder().addIcon(OVERLAY_TOP_CLEANROOM_GLOW).glow().build())};
        }
        return new ITexture[]{TextureFactory.of(BLOCK_PLASCRETE)};
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, this.getLocalName(), "MultiblockDisplay.png");
    }

    @Override
    public GT_Recipe.GT_Recipe_Map getRecipeMap() {
        return null;
    }

    @Override
    public boolean isCorrectMachinePart(ItemStack aStack) {
        return true;
    }

    @Override
    public boolean isFacingValid(byte aFacing) {
        return aFacing > 1;
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

    private static class ConfigEntry {
        int percentage;
        int allowedCount;
        int meta;
        ConfigEntry(int percentage, int count, int meta) {
            this.percentage = percentage;
            this.allowedCount = count;
            this.meta = meta;
        }
    }
    private final static HashMap<String, ConfigEntry> config = new HashMap<>();

    private static final String category = "cleanroom_allowed_blocks";
    private static final int wildcard_meta = Short.MAX_VALUE;

    private static void setDefaultConfigValues(Configuration cfg) {
        cfg.get("cleanroom_allowed_blocks.reinforced_glass", "Name","blockAlloyGlass");
        cfg.get("cleanroom_allowed_blocks.reinforced_glass", "Percentage",5);
        cfg.get("cleanroom_allowed_blocks.bw_reinforced_glass_0", "Name","BW_GlasBlocks");
        cfg.get("cleanroom_allowed_blocks.bw_reinforced_glass_0", "Percentage",50);
        cfg.get("cleanroom_allowed_blocks.bw_reinforced_glass_0", "Meta",0);
        cfg.get("cleanroom_allowed_blocks.bw_reinforced_glass", "Name","BW_GlasBlocks");
        cfg.get("cleanroom_allowed_blocks.bw_reinforced_glass", "Percentage",100);
        cfg.get("cleanroom_allowed_blocks.elevator", "Name","tile.openblocks.elevator");
        cfg.get("cleanroom_allowed_blocks.elevator", "Count",1);
        cfg.get("cleanroom_allowed_blocks.travel_anchor", "Name","tile.blockTravelAnchor");
        cfg.get("cleanroom_allowed_blocks.travel_anchor", "Count",1);
        cfg.get("cleanroom_allowed_blocks.warded_glass", "Name","tile.blockCosmeticOpaque");
        cfg.get("cleanroom_allowed_blocks.warded_glass", "Meta",2);
        cfg.get("cleanroom_allowed_blocks.warded_glass", "Percentage",50);
    }
    public static void loadConfig(Configuration cfg) {
        if (!cfg.hasCategory(category))
            setDefaultConfigValues(cfg);
        for (ConfigCategory cc : cfg.getCategory(category).getChildren()) {
            String name = cc.get("Name").getString();
            if (cc.containsKey("Count")) {
                if (cc.containsKey("Meta"))
                    config.put(name+":"+cc.get("Meta").getInt(), new ConfigEntry(0, cc.get("Count").getInt(), cc.get("Meta").getInt()));
                else
                    config.put(name, new ConfigEntry(0, cc.get("Count").getInt(), wildcard_meta));
            }
            else if (cc.containsKey("Percentage")) {
                if (cc.containsKey("Meta"))
                    config.put(name+":"+cc.get("Meta").getInt(), new ConfigEntry(cc.get("Percentage").getInt(), 0, cc.get("Meta").getInt()));
                else
                    config.put(name, new ConfigEntry(cc.get("Percentage").getInt(), 0, wildcard_meta));
            }
        }
    }
}
