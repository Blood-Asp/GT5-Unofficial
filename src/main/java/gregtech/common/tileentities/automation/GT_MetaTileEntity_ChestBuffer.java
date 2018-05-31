package gregtech.common.tileentities.automation;

import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Buffer;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Utility;
import gregtech.common.gui.GT_Container_ChestBuffer;
import gregtech.common.gui.GT_GUIContainer_ChestBuffer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Comparator;

public class GT_MetaTileEntity_ChestBuffer
        extends GT_MetaTileEntity_Buffer {
    public GT_MetaTileEntity_ChestBuffer(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 28, new String[]{
                        		"Buffers up to 27 Item Stacks",
                        		"Use Screwdriver to regulate output stack size",
                        		"Consumes 3EU per moved Item"});
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

    public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_ChestBuffer(this.mName, this.mTier, this.mInventory.length, this.mDescriptionArray, this.mTextures);
    }

    public ITexture getOverlayIcon() {
        return new GT_RenderedTexture(Textures.BlockIcons.AUTOMATION_CHESTBUFFER);
    }

    public boolean isValidSlot(int aIndex) {
        return aIndex < this.mInventory.length - 1;
    }

    protected void moveItems(IGregTechTileEntity aBaseMetaTileEntity, long aTimer) {
        if(aBaseMetaTileEntity.hasInventoryBeenModified()) {
            fillStacksIntoFirstSlots();
        }
        // mSuccess will be negative if the call is caused by the %200 aTimer. Otherwise it will be positive, and only every other tick is a push attempted.
        if ( (mSuccess <= 0 ) || (( mSuccess > 0 ) && ( (mSuccess % 2) == 0 )) ){
            super.moveItems(aBaseMetaTileEntity, aTimer);
        }
        // mSuccesss is set to 50 on a successful move
        if(mSuccess == 50) {
            fillStacksIntoFirstSlots();
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
                    if( o2 == null )
                        return -1;
                    if( o1 == null )
                        return 1;
                    Item item1 = o1.getItem();
                    Item item2 = o2.getItem();
                
                    // If item1 is a block and item2 isn't, sort item1 before item2
                    if (((item1 instanceof ItemBlock)) && (!(item2 instanceof ItemBlock))) {
                        return -1;
                    }
                
                    // If item2 is a block and item1 isn't, sort item1 after item2
                    if (((item2 instanceof ItemBlock)) && (!(item1 instanceof ItemBlock))) {
                        return 1;
                    }
                
                    String displayName1 = o1.getDisplayName();
                    String displayName2 = o2.getDisplayName();
                
                    int result = displayName1.compareToIgnoreCase(displayName2);
                    //System.out.println("sorter: " + displayName1 + " " + displayName2 + " " + result);
                    return result;
                }
            });
    }

// Implementation of insertion sort 
// Worst case time of a 2.8ms
/*
    public int compare(ItemStack o1, ItemStack o2) {
        if( o2 == null )
            return -1;
        if( o1 == null )
            return 1;
        Item item1 = o1.getItem();
        Item item2 = o2.getItem();
    
        // If item1 is a block and item2 isn't, sort item1 before item2
        if (((item1 instanceof ItemBlock)) && (!(item2 instanceof ItemBlock))) {
            return -1;
        }
    
        // If item2 is a block and item1 isn't, sort item1 after item2
        if (((item2 instanceof ItemBlock)) && (!(item1 instanceof ItemBlock))) {
            return 1;
        }
    
        String displayName1 = o1.getDisplayName();
        String displayName2 = o2.getDisplayName();
    
        int result = displayName1.compareToIgnoreCase(displayName2);
        //System.out.println("sorter: " + displayName1 + " " + displayName2 + " " + result);
        return result;
    }

    protected void sortStacks() {
        int i = 1, j;
        while( i < this.mInventory.length ){
            j = i;
            while( (j > 0) && ( compare(this.mInventory[j-1], this.mInventory[j]) == -1) ) {
                GT_Utility.moveStackFromSlotAToSlotB(getBaseMetaTileEntity(), getBaseMetaTileEntity(), j, j-1, (byte) 64, (byte) 1, (byte) 64, (byte) 1);
                j--;
            }
            i++;
        }
    }
*/

    protected void fillStacksIntoFirstSlots() {
        sortStacks();
        // Merge small stacks together
        for (int i = 0; i < this.mInventory.length-1;) {
            //System.out.println( (this.mInventory[i] == null) ? "Slot empty " + i : "Slot " + i + " holds " + this.mInventory[i].getDisplayName());
            for (int j = i + 1; j < this.mInventory.length; j++) {
                if ((this.mInventory[j] != null) && ((GT_Utility.areStacksEqual(this.mInventory[i], this.mInventory[j])))) {
                    GT_Utility.moveStackFromSlotAToSlotB(getBaseMetaTileEntity(), getBaseMetaTileEntity(), j, i, (byte) 64, (byte) 1, (byte) 64, (byte) 1);
                    //System.out.println( "Moving slot " + j + " into slot " +  i );
                }
                else {
                    i=j;
                    break; // No more matching items for this i, do next i
                }
            }
        }
    }

    public Object getServerGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_Container_ChestBuffer(aPlayerInventory, aBaseMetaTileEntity);
    }

    public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity) {
        return new GT_GUIContainer_ChestBuffer(aPlayerInventory, aBaseMetaTileEntity);
    }
}
