package gregtech.common.blocks;

import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.metatileentity.IConnectable;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_Cable;
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_Fluid;
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_Frame;
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_Item;
import gregtech.api.util.GT_ItsNotMyFaultException;
import gregtech.api.util.GT_LanguageManager;
import gregtech.api.util.GT_Log;
import gregtech.api.util.GT_Utility;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import java.util.List;

public class GT_Item_Machines extends ItemBlock {

    private static final String[] directionNames = {"Bottom", "Top", "North", "South", "West", "East"};

    public GT_Item_Machines(Block par1) {
        super(par1);
        setMaxDamage(0);
        setHasSubtypes(true);
        setCreativeTab(GregTech_API.TAB_GREGTECH);
    }

    @Override
    public void addInformation(ItemStack aStack, EntityPlayer aPlayer, List aList, boolean par4) {
        try {
            int tDamage = getDamage(aStack);
            if ((tDamage <= 0) || (tDamage >= GregTech_API.METATILEENTITIES.length)) {
                return;
            }

            if (GregTech_API.METATILEENTITIES[tDamage] != null) {
                IGregTechTileEntity tTileEntity = GregTech_API.METATILEENTITIES[tDamage].getBaseMetaTileEntity();
                if (tTileEntity.getDescription() != null) {
                    int i = 0;
                    IMetaTileEntity metaTileEntity = tTileEntity.getMetaTileEntity();
                    String suffix = (metaTileEntity instanceof MetaTileEntity && ((MetaTileEntity) metaTileEntity).isDisplaySecondaryDescription()) ? "Secondary_" : "";
                    for (String tDescription : tTileEntity.getDescription()) {
                        if (GT_Utility.isStringValid(tDescription)) {
                            if(tDescription.contains("%%%")){
                                String[] tString = tDescription.split("%%%");
                                if(tString.length>=2){
                                    StringBuilder tBuffer = new StringBuilder();
                                    String[] tRep = new String[tString.length / 2];
                                    for (int j = 0; j < tString.length; j++)
                                        if (j % 2 == 0) tBuffer.append(tString[j]);
                                        else {tBuffer.append(" %s"); tRep[j / 2] = tString[j];}
                                    aList.add(String.format(GT_LanguageManager.addStringLocalization("TileEntity_" + suffix + "DESCRIPTION_" + tDamage + "_Index_" + i++, tBuffer.toString(), !GregTech_API.sPostloadFinished ), (Object[]) tRep));
                                }
                            }else{String tTranslated = GT_LanguageManager.addStringLocalization("TileEntity_" + suffix + "DESCRIPTION_" + tDamage + "_Index_" + i++, tDescription, !GregTech_API.sPostloadFinished );
                                aList.add(tTranslated.equals("") ? tDescription : tTranslated);}
                        }else i++;
                    }
                }
                if (tTileEntity.getEUCapacity() > 0L) {
                    if (tTileEntity.getInputVoltage() > 0L) {
                        int inputTier = GT_Utility.getTier(tTileEntity.getInputVoltage());
                        aList.add(GT_LanguageManager.addStringLocalization("TileEntity_EUp_IN", "Voltage IN: ", !GregTech_API.sPostloadFinished ) + EnumChatFormatting.GREEN + GT_Utility.formatNumbers(tTileEntity.getInputVoltage()) + " (" + GT_Values.TIER_COLORS[inputTier] + GT_Values.VN[inputTier] + EnumChatFormatting.GREEN +")" + EnumChatFormatting.GRAY);
                    }
                    if (tTileEntity.getOutputVoltage() > 0L) {
                        int outputTier = GT_Utility.getTier(tTileEntity.getOutputVoltage());
                        aList.add(GT_LanguageManager.addStringLocalization("TileEntity_EUp_OUT", "Voltage OUT: ", !GregTech_API.sPostloadFinished ) + EnumChatFormatting.GREEN + GT_Utility.formatNumbers(tTileEntity.getOutputVoltage()) + " (" + GT_Values.TIER_COLORS[outputTier] + GT_Values.VN[outputTier] + EnumChatFormatting.GREEN + ")" + EnumChatFormatting.GRAY);
                    }
                    if (tTileEntity.getOutputAmperage() > 1L) {
                        aList.add(GT_LanguageManager.addStringLocalization("TileEntity_EUp_AMOUNT", "Amperage: ", !GregTech_API.sPostloadFinished ) + EnumChatFormatting.YELLOW + GT_Utility.formatNumbers(tTileEntity.getOutputAmperage()) + EnumChatFormatting.GRAY);
                    }
                    aList.add(GT_LanguageManager.addStringLocalization("TileEntity_EUp_STORE", "Capacity: ", !GregTech_API.sPostloadFinished ) + EnumChatFormatting.BLUE + GT_Utility.formatNumbers(tTileEntity.getEUCapacity()) + EnumChatFormatting.GRAY);
                }
            }
            NBTTagCompound aNBT = aStack.getTagCompound();
            if (aNBT != null) {
                if (aNBT.getBoolean("mMuffler")) {
                    aList.add(GT_LanguageManager.addStringLocalization("GT_TileEntity_MUFFLER", "has Muffler Upgrade", !GregTech_API.sPostloadFinished ));
                }
                if (aNBT.getBoolean("mSteamConverter")) {
                    aList.add(GT_LanguageManager.addStringLocalization("GT_TileEntity_STEAMCONVERTER", "has Steam Upgrade", !GregTech_API.sPostloadFinished ));
                }
                int tAmount = 0;
                if ((tAmount = aNBT.getByte("mSteamTanks")) > 0) {
                    aList.add(tAmount + " " + GT_LanguageManager.addStringLocalization("GT_TileEntity_STEAMTANKS", "Steam Tank Upgrades", !GregTech_API.sPostloadFinished ));
                }

                addInstalledCoversInformation(aNBT, aList);
            }
        } catch (Throwable e) {
            e.printStackTrace(GT_Log.err);
        }
    }

    private void addInstalledCoversInformation(NBTTagCompound aNBT, List<String> aList) {
        if (aNBT.hasKey("mCoverSides")){
            int[] mCoverSides = aNBT.getIntArray("mCoverSides");
            if (mCoverSides != null && mCoverSides.length == 6) {
                for (byte i = 0; i < 6; i++) {
                    int coverId = mCoverSides[i];
                    ItemStack coverStack = GT_Utility.intToStack(coverId);
                    if (coverStack != null) {
                        aList.add(String.format("Cover on %s side: %s", directionNames[i], coverStack.getDisplayName()));
                    }
                }
            }
        }
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public String getUnlocalizedName(ItemStack aStack) {
        short tDamage = (short) getDamage(aStack);
        if ((tDamage < 0) || (tDamage >= GregTech_API.METATILEENTITIES.length)) {
            return "";
        }
        if (GregTech_API.METATILEENTITIES[tDamage] != null) {
            return getUnlocalizedName() + "." + GregTech_API.METATILEENTITIES[tDamage].getMetaName();
        }
        return "";
    }

    @Override
    public String getItemStackDisplayName(ItemStack aStack) {
    	String aName = super.getItemStackDisplayName(aStack);
    	short aDamage = (short) getDamage(aStack);
    	if (aDamage >= 0 && aDamage < GregTech_API.METATILEENTITIES.length && GregTech_API.METATILEENTITIES[aDamage] != null) {
            Materials aMaterial = null;
            if (GregTech_API.METATILEENTITIES[aDamage] instanceof GT_MetaPipeEntity_Item) {
            	aMaterial = ((GT_MetaPipeEntity_Item) GregTech_API.METATILEENTITIES[aDamage]).mMaterial;
            } else if (GregTech_API.METATILEENTITIES[aDamage] instanceof GT_MetaPipeEntity_Fluid) {
            	aMaterial = ((GT_MetaPipeEntity_Fluid) GregTech_API.METATILEENTITIES[aDamage]).mMaterial;
            } else if (GregTech_API.METATILEENTITIES[aDamage] instanceof GT_MetaPipeEntity_Cable) {
            	aMaterial = ((GT_MetaPipeEntity_Cable) GregTech_API.METATILEENTITIES[aDamage]).mMaterial;
            } else if (GregTech_API.METATILEENTITIES[aDamage] instanceof GT_MetaPipeEntity_Frame) {
            	aMaterial = ((GT_MetaPipeEntity_Frame) GregTech_API.METATILEENTITIES[aDamage]).mMaterial;
            }
            if (aMaterial != null) {
            	aName = aMaterial.getLocalizedNameForItem(aName);
            }
        }
    	return aName;
    }

    @Override
    public void onCreated(ItemStack aStack, World aWorld, EntityPlayer aPlayer) {
        super.onCreated(aStack, aWorld, aPlayer);
        short tDamage = (short) getDamage(aStack);
        if ((tDamage < 0) || ((tDamage >= GregTech_API.METATILEENTITIES.length) && (GregTech_API.METATILEENTITIES[tDamage] != null))) {
            GregTech_API.METATILEENTITIES[tDamage].onCreated(aStack, aWorld, aPlayer);
        }
    }

    @Override
    public boolean placeBlockAt(ItemStack aStack, EntityPlayer aPlayer, World aWorld, int aX, int aY, int aZ, int side, float hitX, float hitY, float hitZ, int aMeta) {
        short tDamage = (short) getDamage(aStack);
        if (tDamage > 0) {
            if (GregTech_API.METATILEENTITIES[tDamage] == null) {
                return false;
            }
            int tMetaData = GregTech_API.METATILEENTITIES[tDamage].getTileEntityBaseType();
            if (!aWorld.setBlock(aX, aY, aZ, this.field_150939_a, tMetaData, 3)) {
                return false;
            }
            if (aWorld.getBlock(aX, aY, aZ) != this.field_150939_a) {
                throw new GT_ItsNotMyFaultException("Failed to place Block even though World.setBlock returned true. It COULD be MCPC/Bukkit causing that. In case you really have that installed, don't report this Bug to me, I don't know how to fix it.");
            }
            if (aWorld.getBlockMetadata(aX, aY, aZ) != tMetaData) {
                throw new GT_ItsNotMyFaultException("Failed to set the MetaValue of the Block even though World.setBlock returned true. It COULD be MCPC/Bukkit causing that. In case you really have that installed, don't report this Bug to me, I don't know how to fix it.");
            }
            IGregTechTileEntity tTileEntity = (IGregTechTileEntity) aWorld.getTileEntity(aX, aY, aZ);
            if (tTileEntity != null) {
                tTileEntity.setInitialValuesAsNBT(tTileEntity.isServerSide() ? aStack.getTagCompound() : null, tDamage);
                if (aPlayer != null) {
                    tTileEntity.setOwnerName(aPlayer.getDisplayName());
                    tTileEntity.setOwnerUuid(aPlayer.getUniqueID());
                }
                tTileEntity.getMetaTileEntity().initDefaultModes(aStack.getTagCompound());
                final byte aSide = GT_Utility.getOppositeSide(side);
                if (tTileEntity.getMetaTileEntity() instanceof IConnectable) {
                    // If we're connectable, try connecting to whatever we're up against
                	((IConnectable) tTileEntity.getMetaTileEntity()).connect(aSide);
                } else if (aPlayer != null && aPlayer.isSneaking()) {
                    // If we're being placed against something that is connectable, try telling it to connect to us
                    IGregTechTileEntity aTileEntity = tTileEntity.getIGregTechTileEntityAtSide(aSide);
                    if (aTileEntity != null && aTileEntity.getMetaTileEntity() instanceof IConnectable) {
                        ((IConnectable) aTileEntity.getMetaTileEntity()).connect((byte)side);
                    }
                }
            }
        } else if (!aWorld.setBlock(aX, aY, aZ, this.field_150939_a, tDamage, 3)) {
            return false;
        }
        if (aWorld.getBlock(aX, aY, aZ) == this.field_150939_a) {
            this.field_150939_a.onBlockPlacedBy(aWorld, aX, aY, aZ, aPlayer, aStack);
            this.field_150939_a.onPostBlockPlaced(aWorld, aX, aY, aZ, tDamage);
        }
        return true;
    }
}
