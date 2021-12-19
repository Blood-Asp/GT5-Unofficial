package gregtech.api.metatileentity;

import static gregtech.GT_Mod.GT_FML_LOGGER;
import static gregtech.api.enums.GT_Values.NW;
import static gregtech.api.metatileentity.BaseMetaTileEntity.COVER_DATA_NBT_KEYS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.enums.Textures.BlockIcons;
import gregtech.api.graphs.Node;
import gregtech.api.graphs.paths.NodePath;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IConnectable;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.interfaces.tileentity.IPipeRenderedTileEntity;
import gregtech.api.net.GT_Packet_TileEntity;
import gregtech.api.objects.GT_ItemStack;
import gregtech.api.util.GT_CoverBehavior;
import gregtech.api.util.GT_CoverBehaviorBase;
import gregtech.api.util.GT_Log;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;
import gregtech.api.util.ISerializableObject;
import gregtech.common.GT_Client;
import gregtech.common.covers.GT_Cover_Fluidfilter;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

/**
 * NEVER INCLUDE THIS FILE IN YOUR MOD!!!
 * <p/>
 * This is the main TileEntity for EVERYTHING.
 */
public class BaseMetaPipeEntity extends BaseTileEntity implements IGregTechTileEntity, IPipeRenderedTileEntity {
    private final GT_CoverBehaviorBase<?>[] mCoverBehaviors = new GT_CoverBehaviorBase<?>[]{GregTech_API.sNoBehavior, GregTech_API.sNoBehavior, GregTech_API.sNoBehavior, GregTech_API.sNoBehavior, GregTech_API.sNoBehavior, GregTech_API.sNoBehavior};
    public byte mConnections = IConnectable.NO_CONNECTION;
    protected MetaPipeEntity mMetaTileEntity;
    private byte[] mSidedRedstone = new byte[]{0, 0, 0, 0, 0, 0};
    private int[] mCoverSides = new int[]{0, 0, 0, 0, 0, 0}, mTimeStatistics = new int[GregTech_API.TICKS_FOR_LAG_AVERAGING];
    private ISerializableObject[] mCoverData = new ISerializableObject[6];
    private boolean mInventoryChanged = false, mWorkUpdate = false, mWorks = true, mNeedsUpdate = true, mNeedsBlockUpdate = true, mSendClientData = false;
    private final boolean mCheckConnections = false;
    private byte mColor = 0, oColor = 0, mStrongRedstone = 0, oStrongRedstone = 0, oRedstoneData = 63, oTextureData = 0, oUpdateData = 0, mLagWarningCount = 0;
    private int oX = 0, oY = 0, oZ = 0, mTimeStatisticsIndex = 0;
    private short mID = 0;
    private long mTickTimer = 0;
    protected Node node;
    protected NodePath nodePath;

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public NodePath getNodePath() {
        return nodePath;
    }

    public void setNodePath(NodePath nodePath) {
        this.nodePath = nodePath;
    }


    public BaseMetaPipeEntity() {
    }

    @Override
    public void writeToNBT(NBTTagCompound aNBT) {
        try {
            super.writeToNBT(aNBT);
        } catch (Throwable e) {
            GT_Log.err.println("Encountered CRITICAL ERROR while saving MetaTileEntity, the Chunk whould've been corrupted by now, but I prevented that. Please report immediately to GregTech Intergalactical!!!");
            e.printStackTrace(GT_Log.err);
        }
        try {
            aNBT.setInteger("mID", mID);
            for (int i = 0; i < mCoverData.length; i++) {
                if (mCoverData[i] != null)
                    aNBT.setTag(COVER_DATA_NBT_KEYS[i], mCoverData[i].saveDataToNBT());
            }
            aNBT.setIntArray("mCoverSides", mCoverSides);
            aNBT.setByteArray("mRedstoneSided", mSidedRedstone);
            aNBT.setByte("mConnections", mConnections);
            aNBT.setByte("mColor", mColor);
            aNBT.setByte("mStrongRedstone", mStrongRedstone);
            aNBT.setBoolean("mWorks", !mWorks);
        } catch (Throwable e) {
            GT_Log.err.println("Encountered CRITICAL ERROR while saving MetaTileEntity, the Chunk would've been corrupted by now, but I prevented that. Please report immediately to GregTech Intergalactical!!!");
            e.printStackTrace(GT_Log.err);
        }
        try {
            if (hasValidMetaTileEntity()) {
                NBTTagList tItemList = new NBTTagList();
                for (int i = 0; i < mMetaTileEntity.getRealInventory().length; i++) {
                    ItemStack tStack = mMetaTileEntity.getRealInventory()[i];
                    if (tStack != null) {
                        NBTTagCompound tTag = new NBTTagCompound();
                        tTag.setInteger("IntSlot", i);
                        tStack.writeToNBT(tTag);
                        tItemList.appendTag(tTag);
                    }
                }
                aNBT.setTag("Inventory", tItemList);

                try {
                    mMetaTileEntity.saveNBTData(aNBT);
                } catch (Throwable e) {
                    GT_Log.err.println("Encountered CRITICAL ERROR while saving MetaTileEntity, the Chunk whould've been corrupted by now, but I prevented that. Please report immediately to GregTech Intergalactical!!!");
                    e.printStackTrace(GT_Log.err);
                }
            }
        } catch (Throwable e) {
            GT_Log.err.println("Encountered CRITICAL ERROR while saving MetaTileEntity, the Chunk whould've been corrupted by now, but I prevented that. Please report immediately to GregTech Intergalactical!!!");
            e.printStackTrace(GT_Log.err);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound aNBT) {
        super.readFromNBT(aNBT);
        setInitialValuesAsNBT(aNBT, (short) 0);
    }

    @Override
    public void setInitialValuesAsNBT(NBTTagCompound aNBT, short aID) {
        if (aNBT == null) {
            if (aID > 0) mID = aID;
            else mID = mID > 0 ? mID : 0;
            if (mID != 0) createNewMetatileEntity(mID);
        } else {
            if (aID <= 0) mID = (short) aNBT.getInteger("mID");
            else mID = aID;
            mCoverSides = aNBT.getIntArray("mCoverSides");
            mSidedRedstone = aNBT.getByteArray("mRedstoneSided");
            mConnections = aNBT.getByte("mConnections");
            mColor = aNBT.getByte("mColor");
            mStrongRedstone = aNBT.getByte("mStrongRedstone");
            mWorks = !aNBT.getBoolean("mWorks");

            if (mCoverSides.length != 6) mCoverSides = new int[]{0, 0, 0, 0, 0, 0};
            // check old form of data
            mCoverData = null;
            if (aNBT.hasKey("mCoverData", 11)) {
                int[] tOldData = aNBT.getIntArray("mCoverData");
                if (tOldData.length == 6)
                    mCoverData = Arrays.stream(tOldData).mapToObj(ISerializableObject.LegacyCoverData::new).toArray(ISerializableObject[]::new);
            }
            // if no old data
            if (mCoverData == null) {
                mCoverData = new ISerializableObject[6];
                for (byte i = 0; i<6; i++) {
                    GT_CoverBehaviorBase<?> tBehavior = getCoverBehaviorAtSideNew(i);
                    if (tBehavior == null)
                        continue;
                    mCoverData[i] = tBehavior.createDataObject();
                    if (aNBT.hasKey(COVER_DATA_NBT_KEYS[i]))
                        mCoverData[i].loadDataFromNBT(aNBT.getTag(COVER_DATA_NBT_KEYS[i]));
                }
            }
            if (mSidedRedstone.length != 6) mSidedRedstone = new byte[]{0, 0, 0, 0, 0, 0};

            for (byte i = 0; i < 6; i++) mCoverBehaviors[i] = GregTech_API.getCoverBehaviorNew(mCoverSides[i]);

            // check old form of data
            mCoverData = new ISerializableObject[6];
            if (aNBT.hasKey("mCoverData", 11) && aNBT.getIntArray("mCoverData").length == 6) {
                int[] tOldData = aNBT.getIntArray("mCoverData");
                for (int i = 0; i < tOldData.length; i++) {
                    if(mCoverBehaviors[i] instanceof GT_Cover_Fluidfilter) {
                        final String filterKey = String.format("fluidFilter%d", i);
                        if (aNBT.hasKey(filterKey)) {
                            mCoverData[i] = mCoverBehaviors[i].createDataObject((tOldData[i] & 7) | (FluidRegistry.getFluidID(aNBT.getString(filterKey)) << 3));
                        }
                    } else if (mCoverBehaviors[i] != null && mCoverBehaviors[i] != GregTech_API.sNoBehavior) {
                        mCoverData[i] = mCoverBehaviors[i].createDataObject(tOldData[i]);
                    }
                }
            } else {
                // no old data
                for (byte i = 0; i<6; i++) {
                    if (mCoverBehaviors[i] == null)
                        continue;
                    if (aNBT.hasKey(COVER_DATA_NBT_KEYS[i]))
                        mCoverData[i] = mCoverBehaviors[i].createDataObject(aNBT.getTag(COVER_DATA_NBT_KEYS[i]));
                    else
                        mCoverData[i] = mCoverBehaviors[i].createDataObject();
                }
            }
            if (mID != 0 && createNewMetatileEntity(mID)) {
                NBTTagList tItemList = aNBT.getTagList("Inventory", 10);
                for (int i = 0; i < tItemList.tagCount(); i++) {
                    NBTTagCompound tTag = tItemList.getCompoundTagAt(i);
                    int tSlot = tTag.getInteger("IntSlot");
                    if (tSlot >= 0 && tSlot < mMetaTileEntity.getRealInventory().length) {
                        mMetaTileEntity.getRealInventory()[tSlot] = GT_Utility.loadItem(tTag);
                    }
                }

                try {
                    mMetaTileEntity.loadNBTData(aNBT);
                } catch (Throwable e) {
                    GT_Log.err.println("Encountered Exception while loading MetaTileEntity, the Server should've crashed now, but I prevented that. Please report immediately to GregTech Intergalactical!!!");
                    e.printStackTrace(GT_Log.err);
                }
            }
        }

        if (mCoverData == null || mCoverData.length != 6) mCoverData = new ISerializableObject[6];
        if (mCoverSides.length != 6) mCoverSides = new int[]{0, 0, 0, 0, 0, 0};
        if (mSidedRedstone.length != 6) mSidedRedstone = new byte[]{0, 0, 0, 0, 0, 0};

        for (byte i = 0; i < 6; i++) mCoverBehaviors[i] = GregTech_API.getCoverBehaviorNew(mCoverSides[i]);
    }

    private boolean createNewMetatileEntity(short aID) {
        if (aID <= 0 || aID >= GregTech_API.METATILEENTITIES.length || GregTech_API.METATILEENTITIES[aID] == null) {
            GT_Log.err.println("MetaID " + aID + " not loadable => locking TileEntity!");
        } else {
            if (hasValidMetaTileEntity()) mMetaTileEntity.setBaseMetaTileEntity(null);
            GregTech_API.METATILEENTITIES[aID].newMetaEntity(this).setBaseMetaTileEntity(this);
            mTickTimer = 0;
            mID = aID;
            return true;
        }
        return false;
    }

    @Override
    public void updateEntity() {
        super.updateEntity();

        if (!hasValidMetaTileEntity()) {
            if (mMetaTileEntity == null) return;
            mMetaTileEntity.setBaseMetaTileEntity(this);
        }

        long tTime = System.nanoTime();
        int tCode = 0;

        try { for (tCode = 0; hasValidMetaTileEntity() && tCode >= 0; ) {
            switch (tCode) {
                case 0:
                    tCode++;
                    if (mTickTimer++ == 0) {
                        oX = xCoord;
                        oY = yCoord;
                        oZ = zCoord;
                        if (isServerSide()) for (byte i = 0; i < 6; i++)
                            if (getCoverIDAtSide(i) != 0)
                                if (!mMetaTileEntity.allowCoverOnSide(i, new GT_ItemStack(getCoverIDAtSide(i))))
                                    dropCover(i, i, true);
                        worldObj.markTileEntityChunkModified(xCoord, yCoord, zCoord, this);
                        mMetaTileEntity.onFirstTick(this);
                        if (!hasValidMetaTileEntity()) return;
                    }
                case 1:
                    tCode++;
                    if (isClientSide()) {
                        if (mColor != oColor) {
                            mMetaTileEntity.onColorChangeClient(oColor = mColor);
                            issueTextureUpdate();
                        }

                            if (mNeedsUpdate) {
                                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                                //worldObj.func_147479_m(xCoord, yCoord, zCoord);
                                mNeedsUpdate = false;
                            }
                        }
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                        if (isServerSide() && mTickTimer > 10) {
                            for (byte i = (byte) (tCode - 2); i < 6; i++)
                                if (getCoverIDAtSide(i) != 0) {
                                    tCode++;
                                    GT_CoverBehaviorBase<?> tCover = getCoverBehaviorAtSideNew(i);
                                    int tCoverTickRate = tCover.getTickRate(i, getCoverIDAtSide(i), mCoverData[i], this);
                                    if (tCoverTickRate > 0 && mTickTimer % tCoverTickRate == 0) {
                                        byte tRedstone = tCover.isRedstoneSensitive(i, getCoverIDAtSide(i), mCoverData[i], this, mTickTimer) ? getInputRedstoneSignal(i) : 0;
                                        mCoverData[i] = tCover.doCoverThings(i, tRedstone, getCoverIDAtSide(i), mCoverData[i], this, mTickTimer);
                                        if (!hasValidMetaTileEntity()) return;
                                    }
                                }
                            byte oldConnections =  mConnections;
                            // Mask-out connection direction bits to keep only Foam related connections
                            mConnections = (byte) (mMetaTileEntity.mConnections | (mConnections & ~IConnectable.CONNECTED_ALL));
                            // If foam not hardened, tries roll chance to harden
                            if ((mConnections & IConnectable.HAS_FOAM) == IConnectable.HAS_FRESHFOAM && getRandomNumber(1000) == 0) {
                                mConnections = (byte) ((mConnections & ~IConnectable.HAS_FRESHFOAM) | IConnectable.HAS_HARDENEDFOAM);
                            }
                            if (mTickTimer > 12 && oldConnections != mConnections)
                                GregTech_API.causeCableUpdate(worldObj,xCoord,yCoord,zCoord);
                        }
                    case 8:
                        tCode = 9;
                        mMetaTileEntity.onPreTick(this, mTickTimer);
                        if (!hasValidMetaTileEntity()) return;
                    case 9:
                        tCode++;
                        if (isServerSide()) {
                            if (mTickTimer == 10) {
                                for (byte i = 0; i < 6; i++)
                                    mCoverBehaviors[i] = GregTech_API.getCoverBehaviorNew(mCoverSides[i]);
                                issueBlockUpdate();
                                joinEnet();
                            }

                            if (xCoord != oX || yCoord != oY || zCoord != oZ) {
                                oX = xCoord;
                                oY = yCoord;
                                oZ = zCoord;
                                issueClientUpdate();
                                clearTileEntityBuffer();
                            }
                        }
                    case 10:
                        tCode++;
                        mMetaTileEntity.onPostTick(this, mTickTimer);
                        if (!hasValidMetaTileEntity()) return;
                    case 11:
                        tCode++;
                        if (isServerSide()) {
                            if (mTickTimer % 10 == 0) {
                                if (mSendClientData) {
                                    NW.sendPacketToAllPlayersInRange(worldObj, new GT_Packet_TileEntity(xCoord, (short) yCoord, zCoord, mID, mCoverSides[0], mCoverSides[1], mCoverSides[2], mCoverSides[3], mCoverSides[4], mCoverSides[5], oTextureData = mConnections, oUpdateData = hasValidMetaTileEntity() ? mMetaTileEntity.getUpdateData() : 0, oRedstoneData = (byte) (((mSidedRedstone[0] > 0) ? 1 : 0) | ((mSidedRedstone[1] > 0) ? 2 : 0) | ((mSidedRedstone[2] > 0) ? 4 : 0) | ((mSidedRedstone[3] > 0) ? 8 : 0) | ((mSidedRedstone[4] > 0) ? 16 : 0) | ((mSidedRedstone[5] > 0) ? 32 : 0)), oColor = mColor), xCoord, zCoord);
                                    mSendClientData = false;
                                }
                            }

                            if (mTickTimer > 10) {
                                if (mConnections != oTextureData) sendBlockEvent((byte) 0, oTextureData = mConnections);
                                byte tData = mMetaTileEntity.getUpdateData();
                                if (tData != oUpdateData) sendBlockEvent((byte) 1, oUpdateData = tData);
                                if (mColor != oColor) sendBlockEvent((byte) 2, oColor = mColor);
                                tData = (byte) (((mSidedRedstone[0] > 0) ? 1 : 0) | ((mSidedRedstone[1] > 0) ? 2 : 0) | ((mSidedRedstone[2] > 0) ? 4 : 0) | ((mSidedRedstone[3] > 0) ? 8 : 0) | ((mSidedRedstone[4] > 0) ? 16 : 0) | ((mSidedRedstone[5] > 0) ? 32 : 0));
                                if (tData != oRedstoneData) sendBlockEvent((byte) 3, oRedstoneData = tData);
                            }

                            if (mNeedsBlockUpdate) {
                                updateNeighbours(mStrongRedstone, oStrongRedstone);
                                oStrongRedstone = mStrongRedstone;
                                mNeedsBlockUpdate = false;
                            }
                        }
                default:
                    tCode = -1;
                    break;
            }
        }
        } catch (Throwable e) {
            //GT_Log.err.println("Encountered Exception while ticking MetaTileEntity in Step " + (tCode - 1) + ". The Game should've crashed now, but I prevented that. Please report immidietly to GregTech Intergalactical!!!");
            e.printStackTrace(GT_Log.err);
        }

        if (isServerSide() && hasValidMetaTileEntity()) {
            tTime = System.nanoTime() - tTime;
            if (mTimeStatistics.length > 0)
                mTimeStatistics[mTimeStatisticsIndex = (mTimeStatisticsIndex + 1) % mTimeStatistics.length] = (int) tTime;
            if (tTime > 0 && tTime > (GregTech_API.MILLISECOND_THRESHOLD_UNTIL_LAG_WARNING*1000000) && mTickTimer > 1000 && getMetaTileEntity().doTickProfilingMessageDuringThisTick() && mLagWarningCount++ < 10)
                GT_FML_LOGGER.warn("WARNING: Possible Lag Source at ["+xCoord+","+yCoord+","+zCoord+"] in Dimension "+worldObj.provider.dimensionId+" with "+tTime+" ns caused by an instance of "+getMetaTileEntity().getClass());
        }

        mWorkUpdate = mInventoryChanged = false;
    }

    @Override
    public Packet getDescriptionPacket() {
        issueClientUpdate();
        return null;
    }

    public final void receiveMetaTileEntityData(short aID, int aCover0, int aCover1, int aCover2, int aCover3, int aCover4, int aCover5, byte aTextureData, byte aUpdateData, byte aRedstoneData, byte aColorData) {
        issueTextureUpdate();
        if (aID > 0 && mID != aID) {
            mID = aID;
            createNewMetatileEntity(mID);
        }

        mCoverSides[0] = aCover0;
        mCoverSides[1] = aCover1;
        mCoverSides[2] = aCover2;
        mCoverSides[3] = aCover3;
        mCoverSides[4] = aCover4;
        mCoverSides[5] = aCover5;

        for (byte i = 0; i < 6; i++) mCoverBehaviors[i] = GregTech_API.getCoverBehaviorNew(mCoverSides[i]);

        receiveClientEvent(0, aTextureData);
        receiveClientEvent(1, aUpdateData);
        receiveClientEvent(2, aColorData);
        receiveClientEvent(3, aRedstoneData);
    }

    @Override
    public boolean receiveClientEvent(int aEventID, int aValue) {
        super.receiveClientEvent(aEventID, aValue);

        if (hasValidMetaTileEntity()) {
            try {
                mMetaTileEntity.receiveClientEvent((byte) aEventID, (byte) aValue);
            } catch (Throwable e) {
                GT_Log.err.println("Encountered Exception while receiving Data from the Server, the Client should've been crashed by now, but I prevented that. Please report immediately to GregTech Intergalactical!!!");
                e.printStackTrace(GT_Log.err);
            }
        }

        if (isClientSide()) {
            issueTextureUpdate();
            switch (aEventID) {
                case 0:
                    mConnections = (byte) aValue;
                    break;
                case 1:
                    if (hasValidMetaTileEntity()) mMetaTileEntity.onValueUpdate((byte) aValue);
                    break;
                case 2:
                    if (aValue > 16 || aValue < 0) aValue = 0;
                    mColor = (byte) aValue;
                    break;
                case 3:
                    mSidedRedstone[0] = (byte) ((aValue & 1) == 1 ? 15 : 0);
                    mSidedRedstone[1] = (byte) ((aValue & 2) == 2 ? 15 : 0);
                    mSidedRedstone[2] = (byte) ((aValue & 4) == 4 ? 15 : 0);
                    mSidedRedstone[3] = (byte) ((aValue & 8) == 8 ? 15 : 0);
                    mSidedRedstone[4] = (byte) ((aValue & 16) == 16 ? 15 : 0);
                    mSidedRedstone[5] = (byte) ((aValue & 32) == 32 ? 15 : 0);
                    break;
                case 4:
                    if (hasValidMetaTileEntity() && mTickTimer > 20)
                        mMetaTileEntity.doSound((byte) aValue, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
                    break;
                case 5:
                    if (hasValidMetaTileEntity() && mTickTimer > 20)
                        mMetaTileEntity.startSoundLoop((byte) aValue, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
                    break;
                case 6:
                    if (hasValidMetaTileEntity() && mTickTimer > 20)
                        mMetaTileEntity.stopSoundLoop((byte) aValue, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
                    break;
            }
        }
        return true;
    }

    public ArrayList<String> getDebugInfo(EntityPlayer aPlayer, int aLogLevel) {
        ArrayList<String> tList = new ArrayList<String>();
        if (aLogLevel > 2) {
            tList.add("Meta-ID: " + EnumChatFormatting.BLUE+ mID +EnumChatFormatting.RESET + (hasValidMetaTileEntity() ? EnumChatFormatting.GREEN+" valid"+EnumChatFormatting.RESET : EnumChatFormatting.RED+" invalid"+EnumChatFormatting.RESET) + (mMetaTileEntity == null ? EnumChatFormatting.RED+" MetaTileEntity == null!"+EnumChatFormatting.RESET : " "));
        }
        if (aLogLevel > 1) {
            if (mTimeStatistics.length > 0) {
                double tAverageTime = 0;
                double tWorstTime = 0;
                for (int tTime : mTimeStatistics) {
                    tAverageTime += tTime;
                    if (tTime > tWorstTime) {
                        tWorstTime = tTime;
                    }
                }
                tList.add("Average CPU-load of ~" + (tAverageTime / mTimeStatistics.length) + "ns since " + mTimeStatistics.length + " ticks with worst time of " + tWorstTime + "ns.");
            }
            if (mLagWarningCount > 0) {
                tList.add("Caused " + (mLagWarningCount >= 10 ? "more than 10" : mLagWarningCount) + " Lag Spike Warnings (anything taking longer than " + GregTech_API.MILLISECOND_THRESHOLD_UNTIL_LAG_WARNING + "ms) on the Server.");
            }
            tList.add("Is" + (mMetaTileEntity.isAccessAllowed(aPlayer) ? " " : EnumChatFormatting.RED+" not "+EnumChatFormatting.RESET) + "accessible for you");
        }
        if(joinedIc2Enet)
            tList.add("Joined IC2 ENet");

        return mMetaTileEntity.getSpecialDebugInfo(this, aPlayer, aLogLevel, tList);
    }

    @Override
    public void issueTextureUpdate() {
        mNeedsUpdate = true;
    }

    @Override
    public void issueBlockUpdate() {
        mNeedsBlockUpdate = true;
    }

    @Override
    public void issueClientUpdate() {
        mSendClientData = true;
    }

    @Override
    public void issueCoverUpdate(byte aSide) {
        issueClientUpdate();
    }

    @Override
    public void receiveCoverData(byte coverSide, int coverID, int coverData) {
        if ((coverSide >= 0 && coverSide < 6) && (mCoverSides[coverSide] == coverID))
            setCoverDataAtSide(coverSide, coverData);
    }

    @Override
    public void receiveCoverData(byte aCoverSide, int aCoverID, ISerializableObject aCoverData, EntityPlayerMP aPlayer) {
        if ((aCoverSide >= 0 && aCoverSide < 6) && (mCoverSides[aCoverSide] == aCoverID))
            setCoverDataAtSide(aCoverSide, aCoverData);
    }

    @Override
    public byte getStrongestRedstone() {
        return (byte) Math.max(getInternalInputRedstoneSignal((byte) 0), Math.max(getInternalInputRedstoneSignal((byte) 1), Math.max(getInternalInputRedstoneSignal((byte) 2), Math.max(getInternalInputRedstoneSignal((byte) 3), Math.max(getInternalInputRedstoneSignal((byte) 4), getInternalInputRedstoneSignal((byte) 5))))));
    }

    @Override
    public boolean getRedstone() {
        return getRedstone((byte) 0) || getRedstone((byte) 1) || getRedstone((byte) 2) || getRedstone((byte) 3) || getRedstone((byte) 4) || getRedstone((byte) 5);
    }

    @Override
    public boolean getRedstone(byte aSide) {
        return getInternalInputRedstoneSignal(aSide) > 0;
    }

    public ITexture getCoverTexture(byte aSide) {
        if (getCoverIDAtSide(aSide) == 0) return null;
        if (GT_Mod.instance.isClientSide() && (GT_Client.hideValue & 0x1) != 0) {
            return BlockIcons.HIDDEN_TEXTURE[0]; // See through
        }
        return GregTech_API.sCovers.get(new GT_ItemStack(getCoverIDAtSide(aSide)));
    }

    @Override
    public boolean isGivingInformation() {
        if (canAccessData()) return mMetaTileEntity.isGivingInformation();
        return false;
    }

    @Override
    public boolean isValidFacing(byte aSide) {
        if (canAccessData()) return mMetaTileEntity.isFacingValid(aSide);
        return false;
    }

    @Override
    public byte getBackFacing() {
        return GT_Utility.getOppositeSide(getFrontFacing());
    }

    @Override
    public byte getFrontFacing() {
        return 6;
    }

    @Override
    public void setFrontFacing(byte aFacing) {
        doEnetUpdate();
    }

    @Override
    public int getSizeInventory() {
        if (canAccessData()) return mMetaTileEntity.getSizeInventory();
        return 0;
    }

    @Override
    public ItemStack getStackInSlot(int aIndex) {
        if (canAccessData()) return mMetaTileEntity.getStackInSlot(aIndex);
        return null;
    }

    @Override
    public void setInventorySlotContents(int aIndex, ItemStack aStack) {
        mInventoryChanged = true;
        if (canAccessData())
            mMetaTileEntity.setInventorySlotContents(aIndex, worldObj.isRemote ? aStack : GT_OreDictUnificator.setStack(true, aStack));
    }

    @Override
    public String getInventoryName() {
        if (canAccessData()) return mMetaTileEntity.getInventoryName();
        if (GregTech_API.METATILEENTITIES[mID] != null) return GregTech_API.METATILEENTITIES[mID].getInventoryName();
        return "";
    }

    @Override
    public int getInventoryStackLimit() {
        if (canAccessData()) return mMetaTileEntity.getInventoryStackLimit();
        return 64;
    }

    @Override
    public void openInventory() {/*Do nothing*/}

    @Override
    public void closeInventory() {/*Do nothing*/}

    @Override
    public boolean isUseableByPlayer(EntityPlayer aPlayer) {
        return hasValidMetaTileEntity() && mTickTimer > 40 && getTileEntityOffset(0, 0, 0) == this && aPlayer.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64 && mMetaTileEntity.isAccessAllowed(aPlayer);
    }

    @Override
    public void validate() {
        super.validate();
        mTickTimer = 0;
    }

    @Override
    public void invalidate() {
        tileEntityInvalid = false;
        if (hasValidMetaTileEntity()) {
            mMetaTileEntity.onRemoval();
            mMetaTileEntity.setBaseMetaTileEntity(null);
        }
        leaveEnet();
        super.invalidate();
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        ItemStack stack = getStackInSlot(slot);
        if (stack != null) setInventorySlotContents(slot, null);
        return stack;
    }

    /**
     * Checks validity of meta tile and delegates to it
     */
    @Override
    public void onMachineBlockUpdate() {
        if (canAccessData()) mMetaTileEntity.onMachineBlockUpdate();
    }

    /**
     * Checks validity of meta tile and delegates to it
     */
    @Override
    public boolean isMachineBlockUpdateRecursive() {
        return canAccessData() && mMetaTileEntity.isMachineBlockUpdateRecursive();
    }

    @Override
    public int getProgress() {
        return canAccessData() ? mMetaTileEntity.getProgresstime() : 0;
    }

    @Override
    public int getMaxProgress() {
        return canAccessData() ? mMetaTileEntity.maxProgresstime() : 0;
    }

    @Override
    public boolean increaseProgress(int aProgressAmountInTicks) {
        return canAccessData() && mMetaTileEntity.increaseProgress(aProgressAmountInTicks) != aProgressAmountInTicks;
    }

    @Override
    public boolean hasThingsToDo() {
        return getMaxProgress() > 0;
    }

    @Override
    public void enableWorking() {
        if (!mWorks) mWorkUpdate = true;
        mWorks = true;
    }

    @Override
    public void disableWorking() {
        mWorks = false;
    }

    @Override
    public boolean isAllowedToWork() {
        return mWorks;
    }

    @Override
    public boolean hasWorkJustBeenEnabled() {
        return mWorkUpdate;
    }

    @Override
    public byte getWorkDataValue() {
        return 0;
    }

    @Override
    public void setWorkDataValue(byte aValue) {/*Do nothing*/}

    @Override
    public int getMetaTileID() {
        return mID;
    }

    @Override
    public int setMetaTileID(short aID) {
        return mID = aID;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void setActive(boolean aActive) {/*Do nothing*/}

    @Override
    public long getTimer() {
        return mTickTimer;
    }

    @Override
    public boolean decreaseStoredEnergyUnits(long aEnergy, boolean aIgnoreTooLessEnergy) {
        return false;
    }

    @Override
    public boolean increaseStoredEnergyUnits(long aEnergy, boolean aIgnoreTooMuchEnergy) {
        return false;
    }

    @Override
    public boolean inputEnergyFrom(byte aSide) {
        return false;
    }

    @Override
    public boolean inputEnergyFrom(byte aSide, boolean waitForActive) {
        return false;
    }

    @Override
    public boolean outputsEnergyTo(byte aSide) {
        return false;
    }

    @Override
    public boolean outputsEnergyTo(byte aSide, boolean waitForActive) {
        return false;
    }

    @Override
    public long getOutputAmperage() {
        return 0;
    }

    @Override
    public long getOutputVoltage() {
        return 0;
    }

    @Override
    public long getInputAmperage() {
        return 0;
    }

    @Override
    public long getInputVoltage() {
        return 0;
    }

    @Override
    public boolean increaseStoredSteam(long aEnergy, boolean aIgnoreTooMuchEnergy) {
        return false;
    }

    @Override
    public String[] getDescription() {
        if (canAccessData()) return mMetaTileEntity.getDescription();
        return new String[0];
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        if (canAccessData()) return mMetaTileEntity.isValidSlot(aIndex);
        return false;
    }

    @Override
    public long getUniversalEnergyStored() {
        return Math.max(getStoredEU(), getStoredSteam());
    }

    @Override
    public long getUniversalEnergyCapacity() {
        return Math.max(getEUCapacity(), getSteamCapacity());
    }

    @Override
    public long getStoredEU() {
        return 0;
    }

    @Override
    public long getEUCapacity() {
        return 0;
    }

    @Override
    public long getStoredSteam() {
        return 0;
    }

    @Override
    public long getSteamCapacity() {
        return 0;
    }

    @Override
    public ITexture[] getTexture(Block aBlock, byte aSide) {
        ITexture rIcon = getCoverTexture(aSide);
        if (rIcon != null) return new ITexture[]{rIcon};
        return getTextureUncovered(aSide);
    }

    @Override
    public ITexture[] getTextureCovered(byte aSide) {
        ITexture coverTexture = getCoverTexture(aSide);
        ITexture[] textureUncovered = getTextureUncovered(aSide);
        ITexture[] textureCovered;
        if (coverTexture != null) {
            textureCovered = Arrays.copyOf(textureUncovered, textureUncovered.length + 1);
            textureCovered[textureUncovered.length] = coverTexture;
            return textureCovered;
        } else {
            return textureUncovered;
        }
    }

    @Override
    public ITexture[] getTextureUncovered(byte aSide) {
        if ((mConnections & IConnectable.HAS_FRESHFOAM) != 0) return Textures.BlockIcons.FRESHFOAM;
        if ((mConnections & IConnectable.HAS_HARDENEDFOAM) != 0) return Textures.BlockIcons.HARDENEDFOAMS[mColor];
        if ((mConnections & IConnectable.HAS_FOAM) != 0) return Textures.BlockIcons.ERROR_RENDERING;
        byte tConnections = mConnections;
        if (tConnections == IConnectable.CONNECTED_WEST || tConnections == IConnectable.CONNECTED_EAST) tConnections = (byte) (IConnectable.CONNECTED_WEST | IConnectable.CONNECTED_EAST);
        else if (tConnections == IConnectable.CONNECTED_DOWN || tConnections == IConnectable.CONNECTED_UP) tConnections = (byte) (IConnectable.CONNECTED_DOWN | IConnectable.CONNECTED_UP);
        else if (tConnections == IConnectable.CONNECTED_NORTH || tConnections == IConnectable.CONNECTED_SOUTH) tConnections = (byte) (IConnectable.CONNECTED_NORTH | IConnectable.CONNECTED_SOUTH);
        if (hasValidMetaTileEntity())
            return mMetaTileEntity.getTexture(this, aSide, tConnections, (byte) (mColor - 1),
                    tConnections == 0 || (tConnections & (1 << aSide)) != 0,
                    getOutputRedstoneSignal(aSide) > 0);
        return Textures.BlockIcons.ERROR_RENDERING;
    }

    protected boolean hasValidMetaTileEntity() {
        return mMetaTileEntity != null && mMetaTileEntity.getBaseMetaTileEntity() == this;
    }

    protected boolean canAccessData() {
        return hasValidMetaTileEntity() && !isDead;
    }

    @Override
    public void doExplosion(long aAmount) {
        if (canAccessData()) {
            mMetaTileEntity.onExplosion();
            mMetaTileEntity.doExplosion(aAmount);
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops() {
        ItemStack rStack = new ItemStack(GregTech_API.sBlockMachines, 1, mID);
        NBTTagCompound tNBT = new NBTTagCompound();
        if (mStrongRedstone > 0) tNBT.setByte("mStrongRedstone", mStrongRedstone);
        boolean hasCover = false;
        for (byte i = 0; i < mCoverSides.length; i++) {
            if (mCoverSides[i] != 0) {
                if (mCoverData[i] != null) // this really shouldn't be null if a cover is there already, but whatever
                    tNBT.setTag(COVER_DATA_NBT_KEYS[i], mCoverData[i].saveDataToNBT());
                hasCover = true;
            }
        }
        if (hasCover)
            tNBT.setIntArray("mCoverSides", mCoverSides);
        if (hasValidMetaTileEntity()) mMetaTileEntity.setItemNBT(tNBT);
        if (!tNBT.hasNoTags()) rStack.setTagCompound(tNBT);
        return new ArrayList<ItemStack>(Arrays.asList(rStack));
    }

    @Override
    public boolean onRightclick(EntityPlayer aPlayer, byte aSide, float aX, float aY, float aZ) {
        if (isClientSide()) {
            //Configure Cover, sneak can also be: screwdriver, wrench, side cutter, soldering iron
            if (aPlayer.isSneaking()) {
                byte tSide = (getCoverIDAtSide(aSide) == 0) ? GT_Utility.determineWrenchingSide(aSide, aX, aY, aZ) : aSide;
                return (getCoverBehaviorAtSide(tSide).hasCoverGUI());
            } else if (getCoverBehaviorAtSide(aSide).onCoverRightclickClient(aSide, this, aPlayer, aX, aY, aZ)) {
                return true;
            }
        }
        if (isServerSide()) {
            ItemStack tCurrentItem = aPlayer.inventory.getCurrentItem();
            if (tCurrentItem != null) {
                if (getColorization() >= 0 && GT_Utility.areStacksEqual(new ItemStack(Items.water_bucket, 1), tCurrentItem)) {
                    tCurrentItem.func_150996_a(Items.bucket);
                    setColorization((byte) -1);
                    return true;
                }
                byte tSide = GT_Utility.determineWrenchingSide(aSide, aX, aY, aZ);
                if (GT_Utility.isStackInList(tCurrentItem, GregTech_API.sWrenchList)) {
                    if (mMetaTileEntity.onWrenchRightClick(aSide, tSide, aPlayer, aX, aY, aZ)) {
                        GT_ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, aPlayer);
                        GT_Utility.sendSoundToPlayers(worldObj, GregTech_API.sSoundList.get(100), 1.0F, -1, xCoord, yCoord, zCoord);
                    }
                    return true;
                }
                if (GT_Utility.isStackInList(tCurrentItem, GregTech_API.sScrewdriverList)) {
                    if (getCoverIDAtSide(aSide) == 0 && getCoverIDAtSide(tSide) != 0) {
                        if (GT_ModHandler.damageOrDechargeItem(tCurrentItem, 1, 200, aPlayer)) {
                            setCoverDataAtSide(tSide, getCoverBehaviorAtSideNew(tSide).onCoverScrewdriverClick(tSide, getCoverIDAtSide(tSide), getComplexCoverDataAtSide(tSide), this, aPlayer, 0.5F, 0.5F, 0.5F));
                            mMetaTileEntity.onScrewdriverRightClick(tSide, aPlayer, aX, aY, aZ);
                            GT_Utility.sendSoundToPlayers(worldObj, GregTech_API.sSoundList.get(100), 1.0F, -1, xCoord, yCoord, zCoord);
                        }
                    } else {
                        if (GT_ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, aPlayer)) {
                            setCoverDataAtSide(aSide, getCoverBehaviorAtSideNew(aSide).onCoverScrewdriverClick(aSide, getCoverIDAtSide(aSide), getComplexCoverDataAtSide(aSide), this, aPlayer, aX, aY, aZ));
                            mMetaTileEntity.onScrewdriverRightClick(aSide, aPlayer, aX, aY, aZ);
                            GT_Utility.sendSoundToPlayers(worldObj, GregTech_API.sSoundList.get(100), 1.0F, -1, xCoord, yCoord, zCoord);
                        }
                    }
                    return true;
                }

                if (GT_Utility.isStackInList(tCurrentItem, GregTech_API.sHardHammerList)) {
                    //if (GT_ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, aPlayer)) {
                    //	GT_Utility.sendSoundToPlayers(worldObj, GregTech_API.sSoundList.get(1), 1.0F, -1, xCoord, yCoord, zCoord);
                    //}
                    return true;
                }

                if (GT_Utility.isStackInList(tCurrentItem, GregTech_API.sSoftHammerList)) {
                    if (GT_ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, aPlayer)) {
                        if (mWorks) disableWorking();
                        else enableWorking();
                        GT_Utility.sendChatToPlayer(aPlayer, trans("090","Machine Processing: ") + (isAllowedToWork() ? trans("088","Enabled") : trans("087","Disabled")));
                        GT_Utility.sendSoundToPlayers(worldObj, GregTech_API.sSoundList.get(101), 1.0F, -1, xCoord, yCoord, zCoord);
                    }
                    return true;
                }

                if (GT_Utility.isStackInList(tCurrentItem, GregTech_API.sWireCutterList)) {
                    if (mMetaTileEntity.onWireCutterRightClick(aSide, tSide, aPlayer, aX, aY, aZ)) {
                        //logic handled internally
                        GT_Utility.sendSoundToPlayers(worldObj, GregTech_API.sSoundList.get(100), 1.0F, -1, xCoord, yCoord, zCoord);
                    }
                    doEnetUpdate();
                    return true;
                }

                if (GT_Utility.isStackInList(tCurrentItem, GregTech_API.sSolderingToolList)) {
                	if (mMetaTileEntity.onSolderingToolRightClick(aSide, tSide, aPlayer, aX, aY, aZ)) {
                	    //logic handled internally
                        GT_Utility.sendSoundToPlayers(worldObj, GregTech_API.sSoundList.get(103), 1.0F, -1, xCoord, yCoord, zCoord);
                    } else if (GT_ModHandler.useSolderingIron(tCurrentItem, aPlayer)) {
                        mStrongRedstone ^= (1 << tSide);
                        GT_Utility.sendChatToPlayer(aPlayer, trans("091","Redstone Output at Side ") + tSide + trans("092"," set to: ") + ((mStrongRedstone & (1 << tSide)) != 0 ? trans("093","Strong") : trans("094","Weak")));
                        GT_Utility.sendSoundToPlayers(worldObj, GregTech_API.sSoundList.get(103), 3.0F, -1, xCoord, yCoord, zCoord);
                        issueBlockUpdate();
                    }
                    doEnetUpdate();
                    return true;
                }

                byte coverSide = aSide;
                if (getCoverIDAtSide(aSide) == 0) coverSide = tSide;

                if (getCoverIDAtSide(coverSide) == 0) {
                    if (GregTech_API.sCovers.containsKey(new GT_ItemStack(tCurrentItem))) {
                        if (GregTech_API.getCoverBehaviorNew(tCurrentItem).isCoverPlaceable(coverSide, new GT_ItemStack(tCurrentItem), this) &&
                            mMetaTileEntity.allowCoverOnSide(coverSide, new GT_ItemStack(tCurrentItem)))
                        {
                            setCoverItemAtSide(coverSide, tCurrentItem);
                            if (!aPlayer.capabilities.isCreativeMode) tCurrentItem.stackSize--;
                            GT_Utility.sendSoundToPlayers(worldObj, GregTech_API.sSoundList.get(100), 1.0F, -1, xCoord, yCoord, zCoord);
                        }
                        return true;
                    }
                } else {
                    if (GT_Utility.isStackInList(tCurrentItem, GregTech_API.sCrowbarList)) {
                        if (GT_ModHandler.damageOrDechargeItem(tCurrentItem, 1, 1000, aPlayer)) {
                            GT_Utility.sendSoundToPlayers(worldObj, GregTech_API.sSoundList.get(0), 1.0F, -1, xCoord, yCoord, zCoord);
                            dropCover(coverSide, aSide, false);
                        }
                        return true;
                    }
                }
            }
            else if (aPlayer.isSneaking()) { //Sneak click, no tool -> open cover config or turn back.
                aSide = (getCoverIDAtSide(aSide) == 0) ? GT_Utility.determineWrenchingSide(aSide, aX, aY, aZ) : aSide;
                return getCoverIDAtSide(aSide) > 0 && getCoverBehaviorAtSideNew(aSide).onCoverShiftRightClick(aSide, getCoverIDAtSide(aSide), getComplexCoverDataAtSide(aSide), this, aPlayer);
            }

            if (getCoverBehaviorAtSideNew(aSide).onCoverRightClick(aSide, getCoverIDAtSide(aSide), getComplexCoverDataAtSide(aSide), this, aPlayer, aX, aY, aZ))
                return true;
        }

        if (!getCoverBehaviorAtSideNew(aSide).isGUIClickable(aSide, getCoverIDAtSide(aSide), getComplexCoverDataAtSide(aSide), this))
            return false;

        try {
            if (!aPlayer.isSneaking() && hasValidMetaTileEntity()) return mMetaTileEntity.onRightclick(this, aPlayer, aSide, aX, aY, aZ);
        } catch (Throwable e) {
            GT_Log.err.println("Encountered Exception while rightclicking TileEntity, the Game should've crashed now, but I prevented that. Please report immediately to GregTech Intergalactical!!!");
            e.printStackTrace(GT_Log.err);
        }

        return false;
    }

    @Override
    public void onLeftclick(EntityPlayer aPlayer) {
        try {
            if (aPlayer != null && hasValidMetaTileEntity()) mMetaTileEntity.onLeftclick(this, aPlayer);
        } catch (Throwable e) {
            GT_Log.err.println("Encountered Exception while leftclicking TileEntity, the Game should've crashed now, but I prevented that. Please report immediately to GregTech Intergalactical!!!");
            e.printStackTrace(GT_Log.err);
        }
    }

    @Override
    public boolean isDigitalChest() {
        return false;
    }

    @Override
    public ItemStack[] getStoredItemData() {
        return null;
    }

    @Override
    public void setItemCount(int aCount) {
        //
    }

    @Override
    public int getMaxItemCount() {
        return 0;
    }

    /**
     * Can put aStack into Slot
     */
    @Override
    public boolean isItemValidForSlot(int aIndex, ItemStack aStack) {
        return canAccessData() && mMetaTileEntity.isItemValidForSlot(aIndex, aStack);
    }

    /**
     * returns all valid Inventory Slots, no matter which Side (Unless it's covered).
     * The Side Stuff is done in the following two Functions.
     */
    @Override
    public int[] getAccessibleSlotsFromSide(int aSide) {
        if (canAccessData() && (getCoverBehaviorAtSideNew((byte) aSide).letsItemsOut((byte) aSide, getCoverIDAtSide((byte) aSide), getComplexCoverDataAtSide((byte) aSide), -1, this) || getCoverBehaviorAtSide((byte) aSide).letsItemsIn((byte) aSide, getCoverIDAtSide((byte) aSide), getComplexCoverDataAtSide((byte) aSide), -1, this)))
            return mMetaTileEntity.getAccessibleSlotsFromSide(aSide);
        return new int[0];
    }

    /**
     * Can put aStack into Slot at Side
     */
    @Override
    public boolean canInsertItem(int aIndex, ItemStack aStack, int aSide) {
        return canAccessData() && getCoverBehaviorAtSide((byte) aSide).letsItemsIn((byte) aSide, getCoverIDAtSide((byte) aSide), getComplexCoverDataAtSide((byte) aSide), aIndex, this) && mMetaTileEntity.canInsertItem(aIndex, aStack, aSide);
    }

    /**
     * Can pull aStack out of Slot from Side
     */
    @Override
    public boolean canExtractItem(int aIndex, ItemStack aStack, int aSide) {
        return canAccessData() && getCoverBehaviorAtSide((byte) aSide).letsItemsOut((byte) aSide, getCoverIDAtSide((byte) aSide), getComplexCoverDataAtSide((byte) aSide), aIndex, this) && mMetaTileEntity.canExtractItem(aIndex, aStack, aSide);
    }

    @Override
    public boolean isUpgradable() {
        return false;
    }

    @Override
    public byte getInternalInputRedstoneSignal(byte aSide) {
        return (byte) (getCoverBehaviorAtSide(aSide).getRedstoneInput(aSide, getInputRedstoneSignal(aSide), getCoverIDAtSide(aSide), getComplexCoverDataAtSide(aSide), this) & 15);
    }

    @Override
    public byte getInputRedstoneSignal(byte aSide) {
        return (byte) (worldObj.getIndirectPowerLevelTo(getOffsetX(aSide, 1), getOffsetY(aSide, 1), getOffsetZ(aSide, 1), aSide) & 15);
    }

    @Override
    public byte getOutputRedstoneSignal(byte aSide) {
        return (byte) (getCoverBehaviorAtSideNew(aSide).manipulatesSidedRedstoneOutput(aSide, getCoverIDAtSide(aSide), getComplexCoverDataAtSide(aSide), this) || (getCoverBehaviorAtSideNew(aSide).letsRedstoneGoOut(aSide, getCoverIDAtSide(aSide), getComplexCoverDataAtSide(aSide), this)) ? mSidedRedstone[aSide] & 15 : 0);
    }

    @Override
    public void setInternalOutputRedstoneSignal(byte aSide, byte aStrength) {
        if (!getCoverBehaviorAtSideNew(aSide).manipulatesSidedRedstoneOutput(aSide, getCoverIDAtSide(aSide), getComplexCoverDataAtSide(aSide), this))
            setOutputRedstoneSignal(aSide, aStrength);
    }

    @Override
    public void setOutputRedstoneSignal(byte aSide, byte aStrength) {
        aStrength = (byte) Math.min(Math.max(0, aStrength), 15);
        if (aSide >= 0 && aSide < 6 && mSidedRedstone[aSide] != aStrength) {
            mSidedRedstone[aSide] = aStrength;
            issueBlockUpdate();
        }
    }

    @Override
    public boolean isSteamEngineUpgradable() {
        return isUpgradable() && !hasSteamEngineUpgrade() && getSteamCapacity() > 0;
    }

    @Override
    public boolean addSteamEngineUpgrade() {
        if (isSteamEngineUpgradable()) {
            issueBlockUpdate();
            return true;
        }
        return false;
    }

    @Override
    public boolean hasSteamEngineUpgrade() {
        return false;
    }

    @Override
    public boolean hasInventoryBeenModified() {
        return mInventoryChanged;
    }

    @Override
    public void setGenericRedstoneOutput(boolean aOnOff) {
        //
    }

    @Override
    public int getErrorDisplayID() {
        return 0;
    }

    @Override
    public void setErrorDisplayID(int aErrorID) {
        //
    }

    @Override
    public IMetaTileEntity getMetaTileEntity() {
        return hasValidMetaTileEntity() ? mMetaTileEntity : null;
    }

    @Override
    public void setMetaTileEntity(IMetaTileEntity aMetaTileEntity) {
        mMetaTileEntity = (MetaPipeEntity) aMetaTileEntity;
    }

    @Override
    @Deprecated
    public GT_CoverBehavior getCoverBehaviorAtSide(byte aSide) {
        if (aSide >= 0 && aSide < mCoverBehaviors.length && mCoverBehaviors[aSide] instanceof GT_CoverBehavior)
            return (GT_CoverBehavior) mCoverBehaviors[aSide];
        return GregTech_API.sNoBehavior;
    }

    @Override
    public void setCoverIDAtSide(byte aSide, int aID) {
        if (aSide >= 0 && aSide < 6) {
            mCoverSides[aSide] = aID;
            mCoverBehaviors[aSide] = GregTech_API.getCoverBehaviorNew(aID);
            mCoverData[aSide] = mCoverBehaviors[aSide].createDataObject();
            issueCoverUpdate(aSide);
            issueBlockUpdate();
        }
    }

    @Override
    public void setCoverItemAtSide(byte aSide, ItemStack aCover) {
        GregTech_API.getCoverBehaviorNew(aCover).placeCover(aSide, aCover, this);
    }

    @Override
    public int getCoverIDAtSide(byte aSide) {
        if (aSide >= 0 && aSide < 6) return mCoverSides[aSide];
        return 0;
    }

    @Override
    public ItemStack getCoverItemAtSide(byte aSide) {
        return GT_Utility.intToStack(getCoverIDAtSide(aSide));
    }

    @Override
    public boolean canPlaceCoverIDAtSide(byte aSide, int aID) {
        return getCoverIDAtSide(aSide) == 0;
    }

    @Override
    public boolean canPlaceCoverItemAtSide(byte aSide, ItemStack aCover) {
        return getCoverIDAtSide(aSide) == 0;
    }

    @Override
    @Deprecated
    public void setCoverDataAtSide(byte aSide, int aData) {
        if (aSide >= 0 && aSide < 6 && mCoverData[aSide] instanceof ISerializableObject.LegacyCoverData)
            mCoverData[aSide] = new ISerializableObject.LegacyCoverData(aData);
    }

    @Override
    @Deprecated
    public int getCoverDataAtSide(byte aSide) {
        if (aSide >= 0 && aSide < 6 && mCoverData[aSide] instanceof ISerializableObject.LegacyCoverData)
            return ((ISerializableObject.LegacyCoverData) mCoverData[aSide]).get();
        return 0;
    }

    @Override
    public void setCoverDataAtSide(byte aSide, ISerializableObject aData) {
        if (aSide >= 0 && aSide < 6 && getCoverBehaviorAtSideNew(aSide) != null && getCoverBehaviorAtSideNew(aSide).cast(aData) != null)
            mCoverData[aSide] = aData;
    }

    @Override
    public ISerializableObject getComplexCoverDataAtSide(byte aSide) {
        if (aSide >= 0 && aSide < 6 && getCoverBehaviorAtSideNew(aSide) != null)
            return mCoverData[aSide];
        return GregTech_API.sNoBehavior.createDataObject();
    }

    @Override
    public GT_CoverBehaviorBase<?> getCoverBehaviorAtSideNew(byte aSide) {
        if (aSide >= 0 && aSide < 6)
            return mCoverBehaviors[aSide];
        return GregTech_API.sNoBehavior;
    }

    @Override
    public void setLightValue(byte aLightValue) {
        //
    }

    @Override
    public long getAverageElectricInput() {
        return 0;
    }

    @Override
    public long getAverageElectricOutput() {
        return 0;
    }

    @Override
    public boolean dropCover(byte aSide, byte aDroppedSide, boolean aForced) {
        if (getCoverBehaviorAtSideNew(aSide).onCoverRemoval(aSide, getCoverIDAtSide(aSide), mCoverData[aSide], this, aForced) || aForced) {
            ItemStack tStack = getCoverBehaviorAtSideNew(aSide).getDrop(aSide, getCoverIDAtSide(aSide), getComplexCoverDataAtSide(aSide), this);
            if (tStack != null) {
                tStack.setTagCompound(null);
                EntityItem tEntity = new EntityItem(worldObj, getOffsetX(aDroppedSide, 1) + 0.5, getOffsetY(aDroppedSide, 1) + 0.5, getOffsetZ(aDroppedSide, 1) + 0.5, tStack);
                tEntity.motionX = 0;
                tEntity.motionY = 0;
                tEntity.motionZ = 0;
                worldObj.spawnEntityInWorld(tEntity);
            }

            setCoverIDAtSide(aSide, 0);
            setOutputRedstoneSignal(aSide, (byte) 0);
            return true;
        }
        return false;
    }

    @Override
    public String getOwnerName() {
        return "Player";
    }

    @Override
    public String setOwnerName(String aName) {
        return "Player";
    }

    @Override
    public UUID getOwnerUuid() {
        return GT_Utility.defaultUuid;
    }

    @Override
    public void setOwnerUuid(UUID uuid) {

    }

    @Override
    public byte getComparatorValue(byte aSide) {
        return canAccessData() ? mMetaTileEntity.getComparatorValue(aSide) : 0;
    }

    @Override
    public byte getStrongOutputRedstoneSignal(byte aSide) {
        return aSide >= 0 && aSide < 6 && (mStrongRedstone & (1 << aSide)) != 0 ? (byte) (mSidedRedstone[aSide] & 15) : 0;
    }

    @Override
    public void setStrongOutputRedstoneSignal(byte aSide, byte aStrength) {
        mStrongRedstone |= (1 << aSide);
        setOutputRedstoneSignal(aSide, aStrength);
    }

    @Override
    public ItemStack decrStackSize(int aIndex, int aAmount) {
        if (canAccessData()) {
            mInventoryChanged = true;
            return mMetaTileEntity.decrStackSize(aIndex, aAmount);
        }
        return null;
    }

    @Override
    public long injectEnergyUnits(byte aSide, long aVoltage, long aAmperage) {
        if (canAccessData()) return mMetaTileEntity.injectEnergyUnits(aSide, aVoltage, aAmperage);
        return 0;
    }

    @Override
    public boolean drainEnergyUnits(byte aSide, long aVoltage, long aAmperage) {
        return false;
    }

    @Override
    public boolean acceptsRotationalEnergy(byte aSide) {
        if (!canAccessData() || getCoverIDAtSide(aSide) != 0) return false;
        return mMetaTileEntity.acceptsRotationalEnergy(aSide);
    }

    @Override
    public boolean injectRotationalEnergy(byte aSide, long aSpeed, long aEnergy) {
        if (!canAccessData() || getCoverIDAtSide(aSide) != 0) return false;
        return mMetaTileEntity.injectRotationalEnergy(aSide, aSpeed, aEnergy);
    }
    
    private boolean canMoveFluidOnSide(ForgeDirection aSide, Fluid aFluid, boolean isFill) {
        if (aSide == ForgeDirection.UNKNOWN)
            return true;

        IFluidHandler tTileEntity = getITankContainerAtSide((byte) aSide.ordinal());
        // Only require a connection if there's something to connect to - Allows fluid cells & buckets to interact with the pipe
        if (tTileEntity != null && !mMetaTileEntity.isConnectedAtSide((byte) aSide.ordinal()))
            return false;

        if(isFill && mMetaTileEntity.isLiquidInput((byte) aSide.ordinal())
                && getCoverBehaviorAtSide((byte) aSide.ordinal()).letsFluidIn((byte) aSide.ordinal(), getCoverIDAtSide((byte) aSide.ordinal()), getComplexCoverDataAtSide((byte) aSide.ordinal()), aFluid, this))
            return true;

        if (!isFill && mMetaTileEntity.isLiquidOutput((byte) aSide.ordinal())
                && getCoverBehaviorAtSide((byte) aSide.ordinal()).letsFluidOut((byte) aSide.ordinal(), getCoverIDAtSide((byte) aSide.ordinal()), getComplexCoverDataAtSide((byte) aSide.ordinal()),aFluid, this))
            return true;

        return false;
    }

    @Override
    public int fill(ForgeDirection aSide, FluidStack aFluidStack, boolean doFill) {
        if (mTickTimer > 5 && canAccessData() && canMoveFluidOnSide(aSide, aFluidStack == null ? null : aFluidStack.getFluid(), true))
            return mMetaTileEntity.fill(aSide, aFluidStack, doFill);
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection aSide, int maxDrain, boolean doDrain) {
        if (mTickTimer > 5 && canAccessData() && canMoveFluidOnSide(aSide, mMetaTileEntity.getFluid() == null ? null : mMetaTileEntity.getFluid().getFluid(), false))
            return mMetaTileEntity.drain(aSide, maxDrain, doDrain);
        return null;
    }

    @Override
    public FluidStack drain(ForgeDirection aSide, FluidStack aFluidStack, boolean doDrain) {
        if (mTickTimer > 5 && canAccessData() && canMoveFluidOnSide(aSide, aFluidStack == null ? null : aFluidStack.getFluid(), false))
            return mMetaTileEntity.drain(aSide, aFluidStack, doDrain);
        return null;
    }

    @Override
    public boolean canFill(ForgeDirection aSide, Fluid aFluid) {
        if (mTickTimer > 5 && canAccessData() && canMoveFluidOnSide(aSide, aFluid, true))
            return mMetaTileEntity.canFill(aSide, aFluid);
        return false;
    }

    @Override
    public boolean canDrain(ForgeDirection aSide, Fluid aFluid) {
        if (mTickTimer > 5 && canAccessData() && canMoveFluidOnSide(aSide, aFluid, false))
            return mMetaTileEntity.canDrain(aSide, aFluid);
        return false;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection aSide) {
        if (canAccessData()
            && (aSide == ForgeDirection.UNKNOWN 
                || (mMetaTileEntity.isLiquidInput((byte) aSide.ordinal())
                    && getCoverBehaviorAtSide((byte) aSide.ordinal()).letsFluidIn((byte) aSide.ordinal(), getCoverIDAtSide((byte) aSide.ordinal()), getComplexCoverDataAtSide((byte) aSide.ordinal()), null, this))
                || (mMetaTileEntity.isLiquidOutput((byte) aSide.ordinal()) && getCoverBehaviorAtSide((byte) aSide.ordinal()).letsFluidOut((byte) aSide.ordinal(), getCoverIDAtSide((byte) aSide.ordinal()), getComplexCoverDataAtSide((byte) aSide.ordinal()), null, this))
                    // Doesn't need to be connected to get Tank Info -- otherwise things can't connect
               )
            )
            return mMetaTileEntity.getTankInfo(aSide);
        return new FluidTankInfo[]{};
    }

    @Override
    public boolean isInvalidTileEntity() {
        return isInvalid();
    }

    @Override
    public boolean addStackToSlot(int aIndex, ItemStack aStack) {
        if (GT_Utility.isStackInvalid(aStack)) return true;
        if (aIndex < 0 || aIndex >= getSizeInventory()) return false;
        ItemStack tStack = getStackInSlot(aIndex);
        if (GT_Utility.isStackInvalid(tStack)) {
            setInventorySlotContents(aIndex, aStack);
            return true;
        }
        aStack = GT_OreDictUnificator.get(aStack);
        if (GT_Utility.areStacksEqual(tStack, aStack) && tStack.stackSize + aStack.stackSize <= Math.min(aStack.getMaxStackSize(), getInventoryStackLimit())) {
            tStack.stackSize += aStack.stackSize;
            return true;
        }
        return false;
    }

    @Override
    public boolean addStackToSlot(int aIndex, ItemStack aStack, int aAmount) {
        return addStackToSlot(aIndex, GT_Utility.copyAmount(aAmount, aStack));
    }

    @Override
    public byte getColorization() {
        return (byte) (mColor - 1);
    }

    @Override
    public byte setColorization(byte aColor) {
        if (aColor > 15 || aColor < -1) aColor = -1;
        mColor = (byte) (aColor + 1);
        if (canAccessData()) mMetaTileEntity.onColorChangeServer(aColor);
        return mColor;
    }

    @Override
    public float getThickNess() {
        if (canAccessData()) return mMetaTileEntity.getThickNess();
        return 1.0F;
    }

    public boolean renderInside(byte aSide) {
        if (canAccessData()) return mMetaTileEntity.renderInside(aSide);
        return false;
    }

    @Override
    public float getBlastResistance(byte aSide) {
        return (mConnections & IConnectable.HAS_FOAM) != 0 ? 50.0F : 5.0F;
    }

    @Override
    public boolean isMufflerUpgradable() {
        return false;
    }

    @Override
    public boolean addMufflerUpgrade() {
        return false;
    }

    @Override
    public boolean hasMufflerUpgrade() {
        return false;
    }

    @Override
    public boolean isUniversalEnergyStored(long aEnergyAmount) {
        return getUniversalEnergyStored() >= aEnergyAmount;
    }

    @Override
    public String[] getInfoData() {
        {
            if (canAccessData()) return getMetaTileEntity().getInfoData();
            return new String[]{};
        }
    }

    @Override
    public byte getConnections() {
        return mConnections;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        mInventoryChanged = true;
    }

    public void onNeighborBlockChange(int aX, int aY, int aZ) {
        if (canAccessData()) {
            final IMetaTileEntity meta = getMetaTileEntity();
            if (meta instanceof MetaPipeEntity) {
                // Trigger a checking of connections in case someone placed down a block that the pipe/wire shouldn't be connected to.
                // However; don't do it immediately in case the world isn't finished loading
                //  (This caused issues with AE2 GTEU p2p connections.
                ((MetaPipeEntity) meta).setCheckConnections();
            }
        }
    }

    @Override
    public int getLightOpacity() {
        return mMetaTileEntity == null ? 0 : mMetaTileEntity.getLightOpacity();
    }

    @Override
    public void addCollisionBoxesToList(World aWorld, int aX, int aY, int aZ, AxisAlignedBB inputAABB, List<AxisAlignedBB> outputAABB, Entity collider) {
        mMetaTileEntity.addCollisionBoxesToList(aWorld, aX, aY, aZ, inputAABB, outputAABB, collider);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World aWorld, int aX, int aY, int aZ) {
        return mMetaTileEntity.getCollisionBoundingBoxFromPool(aWorld, aX, aY, aZ);
    }

    @Override
    public void onEntityCollidedWithBlock(World aWorld, int aX, int aY, int aZ, Entity collider) {
        mMetaTileEntity.onEntityCollidedWithBlock(aWorld, aX, aY, aZ, collider);
    }
}
