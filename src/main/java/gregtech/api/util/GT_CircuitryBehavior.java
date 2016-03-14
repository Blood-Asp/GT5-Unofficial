package gregtech.api.util;

import gregtech.api.GregTech_API;
import gregtech.api.interfaces.IRedstoneCircuitBlock;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Redstone Circuit Control Code
 * <p/>
 * This should make everything possible what a Redstone Computer or BuildCraft Gate could do.
 * It is intended to use this similar to BC-Gates (for acquiring Data) and RP Logic Gates.
 * You could write an extremely specified and complex Logic Gate, which works only for you Setup, like
 * with ComputerCraft, but you would have to write an extra Mod to add that, as it doesn't work Ingame.
 * <p/>
 * One can make use of the fact, that ItemStacks can be stored as Integer, so that you can scan
 * Inventories for specific Items using that. Luckily the Buttons in the GUI enable Copy/Paste of
 * ItemID+MetaData to Integer, including the WildCard Damage Value when you use rightclick to place it.
 * You just need to use @GT_Utility.stackToInt(ItemStack aStack) to get it.
 * <p/>
 * All Functions run usually in a seperate try/catch Block, so that failed Logic won't crash the TileEntity.
 */
public abstract class GT_CircuitryBehavior {
    /**
     * @param aIndex 0 - 1023 are my own Indices, so use other Numbers!
     */
    public GT_CircuitryBehavior(int aIndex) {
        GregTech_API.sCircuitryBehaviors.put(aIndex, this);
    }

    /**
     * returns if there is Redstone applied to any of the valid Inputs (OR)
     */
    public static final boolean getAnyRedstone(IRedstoneCircuitBlock aRedstoneCircuitBlock) {
        for (byte i = 0; i < 6; i++) {
            if (i != aRedstoneCircuitBlock.getOutputFacing() && aRedstoneCircuitBlock.getCover(i).letsRedstoneGoIn(i, aRedstoneCircuitBlock.getCoverID(i), aRedstoneCircuitBlock.getCoverVariable(i), aRedstoneCircuitBlock.getOwnTileEntity())) {
                if (aRedstoneCircuitBlock.getInputRedstone(i) > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * returns if there is Redstone applied to all the valid Inputs (AND)
     */
    public static final boolean getAllRedstone(IRedstoneCircuitBlock aRedstoneCircuitBlock) {
        for (byte i = 0; i < 6; i++) {
            if (i != aRedstoneCircuitBlock.getOutputFacing() && aRedstoneCircuitBlock.getCover(i).letsRedstoneGoIn(i, aRedstoneCircuitBlock.getCoverID(i), aRedstoneCircuitBlock.getCoverVariable(i), aRedstoneCircuitBlock.getOwnTileEntity())) {
                if (aRedstoneCircuitBlock.getInputRedstone(i) == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * returns if there is Redstone applied to exactly one of the valid Inputs (XOR)
     */
    public static final boolean getOneRedstone(IRedstoneCircuitBlock aRedstoneCircuitBlock) {
        int tRedstoneAmount = 0;
        for (byte i = 0; i < 6; i++) {
            if (i != aRedstoneCircuitBlock.getOutputFacing() && aRedstoneCircuitBlock.getCover(i).letsRedstoneGoIn(i, aRedstoneCircuitBlock.getCoverID(i), aRedstoneCircuitBlock.getCoverVariable(i), aRedstoneCircuitBlock.getOwnTileEntity())) {
                if (aRedstoneCircuitBlock.getInputRedstone(i) > 0) {
                    tRedstoneAmount++;
                }
            }
        }
        return tRedstoneAmount == 1;
    }

    /**
     * returns the strongest incoming RS-Power
     */
    public static final byte getStrongestRedstone(IRedstoneCircuitBlock aRedstoneCircuitBlock) {
        byte tRedstoneAmount = 0;
        for (byte i = 0; i < 6; i++) {
            if (i != aRedstoneCircuitBlock.getOutputFacing() && aRedstoneCircuitBlock.getCover(i).letsRedstoneGoIn(i, aRedstoneCircuitBlock.getCoverID(i), aRedstoneCircuitBlock.getCoverVariable(i), aRedstoneCircuitBlock.getOwnTileEntity())) {
                tRedstoneAmount = (byte) Math.max(tRedstoneAmount, aRedstoneCircuitBlock.getInputRedstone(i));
            }
        }
        return tRedstoneAmount;
    }

    /*****************
     * GUI Functions *
     *****************/

    /**
     * returns the weakest incoming non-zero RS-Power
     */
    public static final byte getWeakestNonZeroRedstone(IRedstoneCircuitBlock aRedstoneCircuitBlock) {
        if (!getAnyRedstone(aRedstoneCircuitBlock)) return 0;
        byte tRedstoneAmount = 15;
        for (byte i = 0; i < 6; i++) {
            if (i != aRedstoneCircuitBlock.getOutputFacing() && aRedstoneCircuitBlock.getCover(i).letsRedstoneGoIn(i, aRedstoneCircuitBlock.getCoverID(i), aRedstoneCircuitBlock.getCoverVariable(i), aRedstoneCircuitBlock.getOwnTileEntity())) {
                if (aRedstoneCircuitBlock.getInputRedstone(i) > 0)
                    tRedstoneAmount = (byte) Math.min(tRedstoneAmount, aRedstoneCircuitBlock.getInputRedstone(i));
            }
        }
        return tRedstoneAmount;
    }

    /**
     * returns the weakest incoming RS-Power
     */
    public static final byte getWeakestRedstone(IRedstoneCircuitBlock aRedstoneCircuitBlock) {
        if (!getAnyRedstone(aRedstoneCircuitBlock)) return 0;
        byte tRedstoneAmount = 15;
        for (byte i = 0; i < 6; i++) {
            if (i != aRedstoneCircuitBlock.getOutputFacing() && aRedstoneCircuitBlock.getCover(i).letsRedstoneGoIn(i, aRedstoneCircuitBlock.getCoverID(i), aRedstoneCircuitBlock.getCoverVariable(i), aRedstoneCircuitBlock.getOwnTileEntity())) {
                tRedstoneAmount = (byte) Math.min(tRedstoneAmount, aRedstoneCircuitBlock.getInputRedstone(i));
            }
        }
        return tRedstoneAmount;
    }

    /**
     * Initializes the Parameters of this Circuit, all Parameters have been set to 0 right before calling this
     *
     * @param aCircuitData,          The Data Storage you can use (8 Slots)
     * @param aRedstoneCircuitBlock, The Circuit Block MetaTileEntity itself
     */
    public abstract void initParameters(int[] aCircuitData, IRedstoneCircuitBlock aRedstoneCircuitBlock);

    /**
     * Validates the Parameters of this Circuit when a value has been changed by the GUI
     * Also called right after @initParameters and when the Chunk reloads
     *
     * @param aCircuitData,          The Data Storage you can use (8 Slots and only the first 4 are User definable)
     * @param aRedstoneCircuitBlock, The Circuit Block MetaTileEntity itself
     */
    public abstract void validateParameters(int[] aCircuitData, IRedstoneCircuitBlock aRedstoneCircuitBlock);


    /****************************
     * Useful Utility Functions *
     ****************************/

    /**
     * Called every tick if the Block has enough Energy and if the Block is Active
     *
     * @param aCircuitData,          The Data Storage you can use (8 Slots)
     * @param aRedstoneCircuitBlock, The Circuit Block MetaTileEntity itself
     */
    public abstract void onTick(int[] aCircuitData, IRedstoneCircuitBlock aRedstoneCircuitBlock);

    /**
     * If the ItemStack should be displayed. Parameters are between 0 and 3.
     */
    public abstract boolean displayItemStack(int[] aCircuitData, IRedstoneCircuitBlock aRedstoneCircuitBlock, int aIndex);

    /**
     * The Name of the Gate for the GUI
     */
    @SideOnly(Side.CLIENT)
    public abstract String getName();

    /**
     * The Description of the Gate for the GUI
     */
    @SideOnly(Side.CLIENT)
    public abstract String getDescription();

    /**
     * The Description of the Data Field for the GUI
     */
    @SideOnly(Side.CLIENT)
    public abstract String getDataDescription(int[] aCircuitData, int aCircuitDataIndex);

    /**
     * How the Integer should be displayed in the GUI.
     * null means, that it just displays as regular Number.
     */
    @SideOnly(Side.CLIENT)
    public String getDataDisplay(int[] aCircuitData, int aCircuitDataIndex) {
        return null;
    }
}