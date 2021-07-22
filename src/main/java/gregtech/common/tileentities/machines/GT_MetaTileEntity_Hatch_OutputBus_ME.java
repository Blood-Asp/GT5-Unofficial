package gregtech.common.tileentities.machines;

import appeng.api.AEApi;
import appeng.api.networking.GridFlags;
import appeng.api.networking.energy.IEnergySource;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.MachineSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.me.GridAccessException;
import appeng.me.helpers.AENetworkProxy;
import appeng.me.helpers.IGridProxyable;
import appeng.util.Platform;
import cpw.mods.fml.common.Optional;
import gregtech.api.GregTech_API;
import gregtech.api.enums.ItemList;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_OutputBus;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_Utility;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_ME_HATCH;

public class GT_MetaTileEntity_Hatch_OutputBus_ME extends GT_MetaTileEntity_Hatch_OutputBus {
    private BaseActionSource requestSource = null;
    private AENetworkProxy gridProxy = null;
    ItemStack cachedStack = null;
    long lastOutputTick = 0;
    long tickCounter = 0;
    boolean lastOutputFailed = false;

    public GT_MetaTileEntity_Hatch_OutputBus_ME(int aID, String aName, String aNameRegional) {
        super(aID, aName, aNameRegional, 1, new String[]{
            "Item Output for Multiblocks", "Stores directly into ME"}, 0);
    }

    public GT_MetaTileEntity_Hatch_OutputBus_ME(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, aDescription, 0, aTextures);
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_Hatch_OutputBus_ME(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_ME_HATCH)};
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_ME_HATCH)};
    }

    @Override
    public void onFirstTick(IGregTechTileEntity aBaseMetaTileEntity) {
        super.onFirstTick(aBaseMetaTileEntity);
        getProxy();
    }

    @Override
    public boolean storeAll(ItemStack aStack) {
        if (!GregTech_API.mAE2)
            return false;
        aStack.stackSize = store(aStack);
        return aStack.stackSize == 0;
    }

    /**
     * Attempt to store items in connected ME network. Returns how many items did not fit (if the network was down e.g.)
     *
     * @param stack  input stack
     * @return amount of items left over
     */
    @Optional.Method(modid = "appliedenergistics2")
    public int store(final ItemStack stack) {
        if (stack == null)
            return 0;
        try {
            AENetworkProxy proxy = getProxy();
            if (proxy == null)
            {
                lastOutputFailed = true;
                int cacheSize = cachedStack == null ? 0 : cachedStack.stackSize;
                cachedStack = null;
                return stack.stackSize + cacheSize;
            }
            if (lastOutputFailed) // if last output failed, don't buffer
            {
                IMEMonitor<IAEItemStack> sg = proxy.getStorage().getItemInventory();
                IAEItemStack toStore = AEApi.instance().storage().createItemStack(stack);
                IAEItemStack rest = Platform.poweredInsert(proxy.getEnergy(), sg, toStore, getRequest());
                if (rest != null && rest.getStackSize() > 0)
                    return (int) rest.getStackSize();
                else
                    lastOutputFailed = false;
            }
            else if (cachedStack != null && ((tickCounter > (lastOutputTick+20)) || !cachedStack.isItemEqual(stack)))
            {
                lastOutputTick = tickCounter;
                boolean sameStack = cachedStack.isItemEqual(stack);
                if (sameStack)
                    cachedStack.stackSize += stack.stackSize;
                IMEMonitor<IAEItemStack> sg = proxy.getStorage().getItemInventory();
                IAEItemStack toStore = AEApi.instance().storage().createItemStack(cachedStack);
                IAEItemStack rest = Platform.poweredInsert(proxy.getEnergy(), sg, toStore, getRequest());
                if (rest != null && rest.getStackSize() > 0)
                {
                    lastOutputFailed = true;
                    cachedStack.stackSize = (int)rest.getStackSize();
                    if (sameStack) // return all that was cached to sender
                    {
                        cachedStack = null;
                        return (int) rest.getStackSize();
                    }
                    else // leave the cache, and return input to sender
                    {
                        cachedStack.stackSize = (int)rest.getStackSize();
                        return stack.stackSize;
                    }
                }
                else
                {
                    if (!sameStack)
                        cachedStack = stack.copy();
                    else
                        cachedStack = null;
                    return 0;
                }
            }
            else
            {
                if (cachedStack == null)
                    cachedStack = stack.copy();
                else
                    cachedStack.stackSize += stack.stackSize;
            }
            return 0;
        }
        catch( final GridAccessException ignored )
        {
            lastOutputFailed = true;
        }
        return stack.stackSize;
    }

    @Optional.Method(modid = "appliedenergistics2")
    private BaseActionSource getRequest() {
        if (requestSource == null)
            requestSource = new MachineSource((IActionHost)getBaseMetaTileEntity());
        return requestSource;
    }

    @Override
    @Optional.Method(modid = "appliedenergistics2")
    public AECableType getCableConnectionType(ForgeDirection forgeDirection) {
        return isOutputFacing((byte)forgeDirection.ordinal()) ? AECableType.SMART : AECableType.NONE;
    }

    @Override
    public boolean onRightclick(IGregTechTileEntity aBaseMetaTileEntity, EntityPlayer aPlayer) {
        return false;
    }

    @Override
    @Optional.Method(modid = "appliedenergistics2")
    public AENetworkProxy getProxy() {
        if (gridProxy == null) {
            if (getBaseMetaTileEntity() instanceof IGridProxyable) {
                gridProxy = new AENetworkProxy((IGridProxyable)getBaseMetaTileEntity(), "proxy", ItemList.Hatch_Output_Bus_ME.get(1), true);
                gridProxy.onReady();
                gridProxy.setFlags(GridFlags.REQUIRE_CHANNEL);
            }
        }
        return this.gridProxy;
    }

    @Override
    @Optional.Method(modid = "appliedenergistics2")
    public void gridChanged() {
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        tickCounter = aTick;
        super.onPostTick(aBaseMetaTileEntity, aTick);
    }

    @Override
    public void saveNBTData(NBTTagCompound aNBT)
    {
        super.saveNBTData(aNBT);
        if (cachedStack != null) {
            NBTTagCompound tTag = new NBTTagCompound();
            cachedStack.writeToNBT(tTag);
            aNBT.setTag("cachedStack", tTag);
        }
    }

    @Override
    public void loadNBTData(NBTTagCompound aNBT) {
        super.loadNBTData(aNBT);
        NBTBase t = aNBT.getTag("cachedStack");
        if (t instanceof NBTTagCompound)
            cachedStack = GT_Utility.loadItem((NBTTagCompound)t);
    }

    public boolean isLastOutputFailed() {
        return lastOutputFailed;
    }
}
