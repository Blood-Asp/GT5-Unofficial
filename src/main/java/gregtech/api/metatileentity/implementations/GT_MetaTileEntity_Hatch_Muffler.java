package gregtech.api.metatileentity.implementations;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.GT_Mod;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.render.TextureFactory;
import gregtech.api.util.GT_LanguageManager;
import gregtech.api.util.WorldSpawnedEventBuilder;
import gregtech.common.GT_Pollution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Arrays;

import static gregtech.api.enums.Textures.BlockIcons.OVERLAY_MUFFLER;
import static gregtech.api.objects.XSTR.XSTR_INSTANCE;

@SuppressWarnings("unused") // Unused API is expected within scope
public class GT_MetaTileEntity_Hatch_Muffler extends GT_MetaTileEntity_Hatch {
    private static final String localizedDescFormat = GT_LanguageManager.addStringLocalization(
            "gt.blockmachines.hatch.muffler.desc.format",
            "Do not obstruct the output.%n" +
                    "Reduces Pollution to %d%%%n");
    private final int pollutionReduction = calculatePollutionReduction(100);
    private final int pollutionRecover = 100 - pollutionReduction;
    private final String[] description = String.format(localizedDescFormat, pollutionReduction, pollutionRecover)
            .split("\\R");
    private final boolean[] facings = new boolean[ForgeDirection.VALID_DIRECTIONS.length];

    public GT_MetaTileEntity_Hatch_Muffler(int aID, String aName, String aNameRegional, int aTier) {
        super(aID, aName, aNameRegional, aTier, 0, "");
    }

    public GT_MetaTileEntity_Hatch_Muffler(String aName, int aTier, String aDescription, ITexture[][][] aTextures) {
        this(aName, aTier, new String[]{aDescription}, aTextures);
    }

    public GT_MetaTileEntity_Hatch_Muffler(String aName, int aTier, String[] aDescription, ITexture[][][] aTextures) {
        super(aName, aTier, 0, aDescription, aTextures);
        setInValidFacings(ForgeDirection.DOWN);
    }

    @Override
    public String[] getDescription() {
        return description;
    }

    @Override
    public ITexture[] getTexturesActive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_MUFFLER)};
    }

    @Override
    public ITexture[] getTexturesInactive(ITexture aBaseTexture) {
        return new ITexture[]{aBaseTexture, TextureFactory.of(OVERLAY_MUFFLER)};
    }

    @Override
    public boolean isSimpleMachine() {
        return true;
    }

    @Override
    public boolean isValidSlot(int aIndex) {
        return false;
    }

    @Override
    public boolean allowPullStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return false;
    }

    @Override
    public boolean allowPutStack(IGregTechTileEntity aBaseMetaTileEntity, int aIndex, byte aSide, ItemStack aStack) {
        return false;
    }

    @Override
    public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
        return new GT_MetaTileEntity_Hatch_Muffler(mName, mTier, mDescriptionArray, mTextures);
    }

    @Override
    public void onPostTick(IGregTechTileEntity aBaseMetaTileEntity, long aTick) {
        super.onPostTick(aBaseMetaTileEntity, aTick);
        if (aBaseMetaTileEntity.isClientSide() && this.getBaseMetaTileEntity().isActive()) {
            pollutionParticles(this.getBaseMetaTileEntity().getWorld(), "largesmoke");
        }
    }

    @Override
    public boolean isFacingValid(byte aFacing) {
        return facings[aFacing];
    }

    @Override
    public boolean isAccessAllowed(EntityPlayer aPlayer) {
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void pollutionParticles(World aWorld, String name) {
        boolean chk1, chk2, chk3;
        float ran1 = XSTR_INSTANCE.nextFloat(), ran2, ran3;
        chk1 = ran1 * 100 < calculatePollutionReduction(100);
        if (GT_Pollution.getPollution(getBaseMetaTileEntity()) >= GT_Mod.gregtechproxy.mPollutionSmogLimit) {
            ran2 = XSTR_INSTANCE.nextFloat();
            ran3 = XSTR_INSTANCE.nextFloat();
            chk2 = ran2 * 100 < calculatePollutionReduction(100);
            chk3 = ran3 * 100 < calculatePollutionReduction(100);
            if (!(chk1 || chk2 || chk3)) return;
        } else {
            if (!chk1) return;
            ran2 = ran3 = 0.0F;
            chk2 = chk3 = false;
        }

        IGregTechTileEntity aMuffler = this.getBaseMetaTileEntity();
        ForgeDirection aDir = ForgeDirection.getOrientation(aMuffler.getFrontFacing());
        float xPos = aDir.offsetX * 0.76F + aMuffler.getXCoord() + 0.25F;
        float yPos = aDir.offsetY * 0.76F + aMuffler.getYCoord() + 0.25F;
        float zPos = aDir.offsetZ * 0.76F + aMuffler.getZCoord() + 0.25F;

        float ySpd = aDir.offsetY * 0.1F + 0.2F + 0.1F * XSTR_INSTANCE.nextFloat();
        float xSpd;
        float zSpd;

        if (aDir.offsetY == -1) {
            float temp = XSTR_INSTANCE.nextFloat() * 2 * (float) Math.PI;
            xSpd = (float) Math.sin(temp) * 0.1F;
            zSpd = (float) Math.cos(temp) * 0.1F;
        } else {
            xSpd = aDir.offsetX * (0.1F + 0.2F * XSTR_INSTANCE.nextFloat());
            zSpd = aDir.offsetZ * (0.1F + 0.2F * XSTR_INSTANCE.nextFloat());
        }

        WorldSpawnedEventBuilder.ParticleEventBuilder events = new WorldSpawnedEventBuilder.ParticleEventBuilder()
                .setIdentifier(name)
                .setWorld(aWorld)
                .setMotion(xSpd, ySpd, zSpd);

        if (chk1) {
            events.setPosition(xPos + ran1 * 0.5F, yPos + XSTR_INSTANCE.nextFloat() * 0.5F, zPos + XSTR_INSTANCE.nextFloat() * 0.5F)
                  .run();
        }
        if (chk2) {
            events.setPosition(xPos + ran2 * 0.5F, yPos + XSTR_INSTANCE.nextFloat() * 0.5F, zPos + XSTR_INSTANCE.nextFloat() * 0.5F)
                  .run();
        }
        if (chk3) {
            events.setPosition(xPos + ran3 * 0.5F, yPos + XSTR_INSTANCE.nextFloat() * 0.5F, zPos + XSTR_INSTANCE.nextFloat() * 0.5F)
                  .run();
        }
    }

    public int calculatePollutionReduction(int aPollution) {
        if (mTier < 2) {
            return aPollution;
        }
        return (int) ((float) aPollution * ((100F - 12.5F * ((float) mTier - 1F)) / 100F));
    }

    /**
     * @return pollution success
     * @deprecated replaced by {@link .polluteEnvironment(MetaTileEntity)}
     */
    @Deprecated
    public boolean polluteEnvironment() {
        return polluteEnvironment(null);
    }

    /**
     * @param mte The multi-block controller's {@link MetaTileEntity}
     *            MetaTileEntity is passed so newer muffler hatches can do wacky things with the multis
     * @return pollution success
     */
    public boolean polluteEnvironment(MetaTileEntity mte) {
        if (getBaseMetaTileEntity().getAirAtSide(getBaseMetaTileEntity().getFrontFacing())) {
            GT_Pollution.addPollution(getBaseMetaTileEntity(), calculatePollutionReduction(10000));
            return true;
        }
        return false;
    }

    /**
     * @param aFacings the {@link ForgeDirection} invalid facings
     * @apiNote API Code, BartWorks/TecTech based EBF relies on this. It's marked here, not anywhere else.
     */
    public void setInValidFacings(ForgeDirection... aFacings) {
        Arrays.fill(facings, true);
        Arrays.stream(aFacings).forEach(face -> facings[face.ordinal()] = false);
    }
}
