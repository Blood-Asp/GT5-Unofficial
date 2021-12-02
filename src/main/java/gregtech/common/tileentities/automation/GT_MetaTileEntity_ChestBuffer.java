package gregtech.common.tileentities.automation;

import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Buffer;
import gregtech.api.render.TextureFactory;
import gregtech.common.gui.GT_Container_ChestBuffer;
import gregtech.common.gui.GT_GUIContainer_ChestBuffer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Comparator;

import static gregtech.api.enums.Textures.BlockIcons.AUTOMATION_CHESTBUFFER;
import static gregtech.api.enums.Textures.BlockIcons.AUTOMATION_CHESTBUFFER_GLOW;

public class GT_MetaTileEntity_ChestBuffer extends GT_MetaTileEntity_Buffer {

    private static final int[] tickRate = {400, 200, 100, 20, 4, 1, 1, 1, 1,  1,  1,  1,   1};
    private static final int[] maxStacks = { 1,   1,   1,  1, 1, 1, 2, 4, 8, 16, 32, 64, 128};


    public GT_MetaTileEntity_ChestBuffer(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 28, new String[]{
                "Buffers up to 27 Item Stacks",
                "Use Screwdriver to regulate output stack size",
                "Does not consume energy to move Item",
                getTickRateDesc(aTier)});
    }

    public GT_MetaTileEntity_ChestBuffer(int aID, String aName, String aNameRegional, int aTier, int aInvSlotCount, String aDescription) {
        super(aID, aName, aNameRegional, aTier, aInvSlotCount, aDescription);
    }

    public GT_MetaTileEntity_ChestBuffer(int aID, String aName, String aNameRegional, int aTier, int aInvSlotCount, String[] aDescription) {
                super(aID, aName, aNameRegional, aTier, aInvSlotCount, aDescription);
    }

    public GT_MetaTileEntity_ChestBuffer(String aName, int aTier, int aInvSlotCount, String aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aInvSlotCount, aDescription, aTextures);
    }
    
    public GT_MetaTileEntity_ChestBuffer(String aName, int aTier, int aInvSlotCount, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aInvSlotCount, aDescription, aTextures);
    }

    @Override
    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_ChestBuffer(this.mName, this.mTier, this.mInventory.length, this.mDescriptionArray, this.mTextures);
    }

    @Override
    public ITexture getOverlayIcon() {
        return TextureFactory.of(
                TextureFactory.of(AUTOMATION_CHESTBUFFER),
                TextureFactory.builder().addIcon(AUTOMATION_CHESTBUFFER_GLOW).glow().build());
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        return aIndex < this.mInventory.length - 1;
    }

    @Override
    protected void moveItems(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        if (aTimer % tickRate[mTier] > 0) return;

        if(this.bSortStacks && aBaseMetaTileEntity.hasInventoryBeenModified()) {
            fillStacksIntoFirstSlots();
        }
        // mSuccess will be negative if the call is caused by the %200 aTimer, always try to push. Otherwise it will be positive.
        // For the first 6 ticks after a successful move (49->44), push every tick. Then go to every 5 ticks.
        if ( (mSuccess <= 0 ) || (mSuccess > 43) || ((mSuccess % 5) == 0 )){
            super.moveItems(aBaseMetaTileEntity, aTimer, Math.min(MAX, maxStacks[mTier]));
        }

        if(mSuccess < 0) {
            mSuccess = 0;
        }
    }

// Implementation using Java built in sort algorithm
// Uses terribad string comparison to sort against.  Would be better if we did something else?
    protected void sortStacks() {
        Arrays.sort(this.mInventory, new Comparator<ItemStack>() {
                @Override
                // Taken from https://gist.github.com/Choonster/876acc3217229e172e46
                public int compare(ItemStack o1, ItemStack o2) {
                    if( o2 == null )
                        return -1;
                    if( o1 == null )
                        return 1;
                    Item item1 = o1.getItem();
                    Item item2 = o2.getItem();

                    if(item1 instanceof ItemBlock) {
                        if (!(item2 instanceof ItemBlock))
                            return -1; // If item1 is a block and item2 isn't, sort item1 before item2
                    } else if (item2 instanceof ItemBlock)
                        return 1; // If item2 is a block and item1 isn't, sort item1 after item2

                    int id1 = Item.getIdFromItem( item1 );
                    int id2 = Item.getIdFromItem( item2 );
                    if ( id1 < id2 ) {
                        return -1;
                    }
                    if ( id1 > id2 ) {
                        return 1;
                    }

                    id1 = o1.getItemDamage();
                    id2 = o2.getItemDamage();

                    return Integer.compare(id1, id2);
                }
            });
    }

    @Override
    protected void fillStacksIntoFirstSlots() {
        sortStacks();
        super.fillStacksIntoFirstSlots();
    }

    @Override
    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_Container_ChestBuffer(aPlayerInventory, aBaseMetaTileEntity);
    }

    @Override
    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_ChestBuffer(aPlayerInventory, aBaseMetaTileEntity);
    }

    protected static String getTickRateDesc(int tier){
        int tickRate = getTickRate(tier);
        String timeStr = "";
        String numStr = "";
        if (maxStacks[tier] > 1) {
            numStr = maxStacks[tier] + " items";
        } else {
            numStr = "1 item";
        }
        if (tickRate < 20)
            timeStr = "1/" + 20/tickRate + " ";
        else if (tickRate > 20) {
            timeStr = (tickRate / 20) + "th ";
        }
        return "Moves " + numStr + " every " + timeStr + "second";
    }

    protected static int getTickRate(int tier) {
        if (tier > 9)
            return 1;
        return tickRate[tier];
    }

    protected static int getMaxStacks(int tier) {
        // Included higher tiers on the off chance they actually work without blowing things up lmao
        return tier > 9 ? MAX : Math.min(maxStacks[tier], MAX);
    }
}
