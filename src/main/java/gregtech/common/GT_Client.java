// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GT_Client.java

package gregtech.common;

import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Translation;
import com.gtnewhorizon.structurelib.alignment.IAlignment;
import com.gtnewhorizon.structurelib.alignment.IAlignmentProvider;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.IHasFluidDisplayItem;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.interfaces.tileentity.ITurnable;
import gregtech.api.metatileentity.BaseMetaPipeEntity;
import gregtech.api.net.GT_Packet_ClientPreference;
import gregtech.api.objects.GT_ItemStack;
import gregtech.api.util.GT_ClientPreference;
import gregtech.api.util.GT_Log;
import gregtech.api.util.GT_PlayedSound;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import gregtech.api.util.WorldSpawnedEventBuilder;
import gregtech.common.entities.GT_Entity_Arrow;
import gregtech.common.entities.GT_Entity_Arrow_Potion;
import gregtech.common.net.MessageUpdateFluidDisplayItem;
import gregtech.common.render.GT_CapeRenderer;
import gregtech.common.render.GT_FlaskRenderer;
import gregtech.common.render.GT_FluidDisplayStackRenderer;
import gregtech.common.render.GT_MetaGenerated_Item_Renderer;
import gregtech.common.render.GT_MetaGenerated_Tool_Renderer;
import gregtech.common.render.GT_PollutionRenderer;
import gregtech.common.render.GT_Renderer_Block;
import gregtech.common.render.GT_Renderer_Entity_Arrow;
import ic2.api.tile.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.oredict.OreDictionary;
import org.lwjgl.opengl.GL11;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.lwjgl.opengl.GL11.GL_LINE_LOOP;

// Referenced classes of package gregtech.common:
//            GT_Proxy

public class GT_Client extends GT_Proxy
        implements Runnable {

    public static final String GTMEGA_CAPE_LIST_URL = "https://raw.githubusercontent.com/GTMEGA/CustomGTCapeHook-Cape-List/master/capes.txt";
    public static final String GT_CAPE_LIST_URL = "http://gregtech.overminddl1.com/com/gregoriust/gregtech/supporterlist.txt";
    private static final List<Block> ROTATABLE_VANILLA_BLOCKS;

    private static final int[][] GRID_SWITCH_TABLE = new int[][]{
            {0, 5, 3, 1, 2, 4},
            {5, 0, 1, 3, 2, 4},
            {1, 3, 0, 5, 2, 4},
            {3, 1, 5, 0, 2, 4},
            {4, 2, 3, 1, 0, 5},
            {2, 4, 3, 1, 5, 0},
    };

    // don't ask. these "just works"
    private static final Transformation ROTATION_MARKER_TRANSFORM_CENTER = new Scale(0.5);
    private static final Transformation[] ROTATION_MARKER_TRANSFORMS_SIDES_TRANSFORMS = {
            new Scale(0.25).with(new Translation(0, 0, 0.375)).compile(),
            new Scale(0.25).with(new Translation(0.375, 0, 0)).compile(),
            new Scale(0.25).with(new Translation(0, 0, -0.375)).compile(),
            new Scale(0.25).with(new Translation(-0.375, 0, 0)).compile(),
    };
    private static final int[] ROTATION_MARKER_TRANSFORMS_SIDES = {
            -1, -1, 2, 0, 3, 1,
            -1, -1, 0, 2, 3, 1,
            0, 2, -1, -1, 3, 1,
            2, 0, -1, -1, 3, 1,
            1, 3, 2, 0, -1, -1,
            3, 1, 2, 0, -1, -1
    };
    private static final Transformation[] ROTATION_MARKER_TRANSFORMS_CORNER = {
            new Scale(0.25).with(new Translation(0.375, 0, 0.375)).compile(),
            new Scale(0.25).with(new Translation(-0.375, 0, 0.375)).compile(),
            new Scale(0.25).with(new Translation(0.375, 0, -0.375)).compile(),
            new Scale(0.25).with(new Translation(-0.375, 0, -0.375)).compile(),
    };
    private static int rotationMarkerDisplayList;
    private static boolean rotationMarkerDisplayListCompiled = false;

    static {
        ROTATABLE_VANILLA_BLOCKS = Arrays.asList(Blocks.piston, Blocks.sticky_piston, Blocks.furnace, Blocks.lit_furnace, Blocks.dropper, Blocks.dispenser, Blocks.chest, Blocks.trapped_chest, Blocks.ender_chest, Blocks.hopper,
                Blocks.pumpkin, Blocks.lit_pumpkin);
    }

    private final HashSet<String> mCapeList = new HashSet<>();
    public static final GT_PollutionRenderer mPollutionRenderer = new GT_PollutionRenderer();
    private final GT_CapeRenderer mCapeRenderer;
    private final List<Materials> mPosR;
    private final List<Materials> mPosG;
    private final List<Materials> mPosB;
    private final List<Materials> mPosA = Collections.emptyList();
    private final List<Materials> mNegR;
    private final List<Materials> mNegG;
    private final List<Materials> mNegB;
    private final List<Materials> mNegA = Collections.emptyList();
    private final List<Materials> mMoltenPosR;
    private final List<Materials> mMoltenPosG;
    private final List<Materials> mMoltenPosB;
    private final List<Materials> mMoltenPosA = Collections.emptyList();
    private final List<Materials> mMoltenNegR;
    private final List<Materials> mMoltenNegG;
    private final List<Materials> mMoltenNegB;
    private final List<Materials> mMoltenNegA = Collections.emptyList();
    private long mAnimationTick;
    /**
     * This is the place to def the value used below
     **/
    private long afterSomeTime;
    private boolean mAnimationDirection;
    private int mLastUpdatedBlockX;
    private int mLastUpdatedBlockY;
    private int mLastUpdatedBlockZ;
    private GT_ClientPreference mPreference;
    private boolean mFirstTick = false;
    public static final int ROTATION_MARKER_RESOLUTION = 120;

    public GT_Client() {
        mCapeRenderer = new GT_CapeRenderer(mCapeList);
        mAnimationTick = 0L;
        mAnimationDirection = false;
        mPosR = Arrays.asList(Materials.Enderium, Materials.Vinteum, Materials.Uranium235, Materials.InfusedGold, Materials.Plutonium241, Materials.NaquadahEnriched, Materials.Naquadria, Materials.InfusedOrder, Materials.Force,
                Materials.Pyrotheum, Materials.Sunnarium, Materials.Glowstone, Materials.Thaumium, Materials.InfusedVis, Materials.InfusedAir, Materials.InfusedFire, Materials.FierySteel, Materials.Firestone);
        mPosG = Arrays.asList(Materials.Enderium, Materials.Vinteum, Materials.Uranium235, Materials.InfusedGold, Materials.Plutonium241, Materials.NaquadahEnriched, Materials.Naquadria, Materials.InfusedOrder, Materials.Force,
                Materials.Pyrotheum, Materials.Sunnarium, Materials.Glowstone, Materials.InfusedAir, Materials.InfusedEarth);
        mPosB = Arrays.asList(Materials.Enderium, Materials.Vinteum, Materials.Uranium235, Materials.InfusedGold, Materials.Plutonium241, Materials.NaquadahEnriched, Materials.Naquadria, Materials.InfusedOrder, Materials.InfusedVis,
                Materials.InfusedWater, Materials.Thaumium);
        mNegR = Arrays.asList(Materials.InfusedEntropy, Materials.NetherStar);
        mNegG = Arrays.asList(Materials.InfusedEntropy, Materials.NetherStar);
        mNegB = Arrays.asList(Materials.InfusedEntropy, Materials.NetherStar);
        mMoltenPosR = Arrays.asList(Materials.Enderium, Materials.NetherStar, Materials.Vinteum, Materials.Uranium235, Materials.InfusedGold, Materials.Plutonium241, Materials.NaquadahEnriched, Materials.Naquadria, Materials.InfusedOrder, Materials.Force,
                Materials.Pyrotheum, Materials.Sunnarium, Materials.Glowstone, Materials.Thaumium, Materials.InfusedVis, Materials.InfusedAir, Materials.InfusedFire, Materials.FierySteel, Materials.Firestone);
        mMoltenPosG = Arrays.asList(Materials.Enderium, Materials.NetherStar, Materials.Vinteum, Materials.Uranium235, Materials.InfusedGold, Materials.Plutonium241, Materials.NaquadahEnriched, Materials.Naquadria, Materials.InfusedOrder, Materials.Force,
                Materials.Pyrotheum, Materials.Sunnarium, Materials.Glowstone, Materials.InfusedAir, Materials.InfusedEarth);
        mMoltenPosB = Arrays.asList(Materials.Enderium, Materials.NetherStar, Materials.Vinteum, Materials.Uranium235, Materials.InfusedGold, Materials.Plutonium241, Materials.NaquadahEnriched, Materials.Naquadria, Materials.InfusedOrder, Materials.InfusedVis,
                Materials.InfusedWater, Materials.Thaumium);
        mMoltenNegR = Collections.singletonList(Materials.InfusedEntropy);
        mMoltenNegG = Collections.singletonList(Materials.InfusedEntropy);
        mMoltenNegB = Collections.singletonList(Materials.InfusedEntropy);
    }


    private static boolean checkedForChicken = false;

    private static void drawGrid(DrawBlockHighlightEvent aEvent, boolean showCoverConnections, boolean aIsWrench, boolean aIsSneaking) {
        if (!checkedForChicken) {
            try {
                Class.forName("codechicken.lib.vec.Rotation");
            } catch (Throwable e) {
                if (GT_Values.D1) {
                    e.printStackTrace(GT_Log.err);
                }
                return;
            }
            checkedForChicken = true;
        }

        GL11.glPushMatrix();
        GL11.glTranslated(-(aEvent.player.lastTickPosX + (aEvent.player.posX - aEvent.player.lastTickPosX) * (double) aEvent.partialTicks), -(aEvent.player.lastTickPosY + (aEvent.player.posY - aEvent.player.lastTickPosY) * (double) aEvent.partialTicks), -(aEvent.player.lastTickPosZ + (aEvent.player.posZ - aEvent.player.lastTickPosZ) * (double) aEvent.partialTicks));
        GL11.glTranslated((float) aEvent.target.blockX + 0.5F, (float) aEvent.target.blockY + 0.5F, (float) aEvent.target.blockZ + 0.5F);
        int tSideHit = aEvent.target.sideHit;
        Rotation.sideRotations[tSideHit].glApply();
        // draw grid
        GL11.glTranslated(0.0D, -0.501D, 0.0D);
        GL11.glLineWidth(2.0F);
        GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.5F);
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex3d(+.50D, .0D, -.25D);
        GL11.glVertex3d(-.50D, .0D, -.25D);
        GL11.glVertex3d(+.50D, .0D, +.25D);
        GL11.glVertex3d(-.50D, .0D, +.25D);
        GL11.glVertex3d(+.25D, .0D, -.50D);
        GL11.glVertex3d(+.25D, .0D, +.50D);
        GL11.glVertex3d(-.25D, .0D, -.50D);
        GL11.glVertex3d(-.25D, .0D, +.50D);
        TileEntity tTile = aEvent.player.worldObj.getTileEntity(aEvent.target.blockX, aEvent.target.blockY, aEvent.target.blockZ);

        // draw connection indicators
        byte tConnections = 0;
        if (tTile instanceof ICoverable) {
            if (showCoverConnections) {
                for (byte i = 0; i < 6; i++) {
                    if (((ICoverable) tTile).getCoverIDAtSide(i) > 0)
                        tConnections = (byte) (tConnections + (1 << i));
                }
            } else if (tTile instanceof BaseMetaPipeEntity)
                tConnections = ((BaseMetaPipeEntity) tTile).mConnections;
        }

        if (tConnections > 0) {
            for (byte i = 0; i < 6; i++) {
                if ((tConnections & (1 << i)) != 0) {
                    switch (GRID_SWITCH_TABLE[aEvent.target.sideHit][i]) {
                        case 0:
                            GL11.glVertex3d(+.25D, .0D, +.25D);
                            GL11.glVertex3d(-.25D, .0D, -.25D);
                            GL11.glVertex3d(-.25D, .0D, +.25D);
                            GL11.glVertex3d(+.25D, .0D, -.25D);
                            break;
                        case 1:
                            GL11.glVertex3d(-.25D, .0D, +.50D);
                            GL11.glVertex3d(+.25D, .0D, +.25D);
                            GL11.glVertex3d(-.25D, .0D, +.25D);
                            GL11.glVertex3d(+.25D, .0D, +.50D);
                            break;
                        case 2:
                            GL11.glVertex3d(-.50D, .0D, -.25D);
                            GL11.glVertex3d(-.25D, .0D, +.25D);
                            GL11.glVertex3d(-.50D, .0D, +.25D);
                            GL11.glVertex3d(-.25D, .0D, -.25D);
                            break;
                        case 3:
                            GL11.glVertex3d(-.25D, .0D, -.50D);
                            GL11.glVertex3d(+.25D, .0D, -.25D);
                            GL11.glVertex3d(-.25D, .0D, -.25D);
                            GL11.glVertex3d(+.25D, .0D, -.50D);
                            break;
                        case 4:
                            GL11.glVertex3d(+.50D, .0D, -.25D);
                            GL11.glVertex3d(+.25D, .0D, +.25D);
                            GL11.glVertex3d(+.50D, .0D, +.25D);
                            GL11.glVertex3d(+.25D, .0D, -.25D);
                            break;
                        case 5:
                            GL11.glVertex3d(+.50D, .0D, +.50D);
                            GL11.glVertex3d(+.25D, .0D, +.25D);
                            GL11.glVertex3d(+.50D, .0D, +.25D);
                            GL11.glVertex3d(+.25D, .0D, +.50D);
                            GL11.glVertex3d(+.50D, .0D, -.50D);
                            GL11.glVertex3d(+.25D, .0D, -.25D);
                            GL11.glVertex3d(+.50D, .0D, -.25D);
                            GL11.glVertex3d(+.25D, .0D, -.50D);
                            GL11.glVertex3d(-.50D, .0D, +.50D);
                            GL11.glVertex3d(-.25D, .0D, +.25D);
                            GL11.glVertex3d(-.50D, .0D, +.25D);
                            GL11.glVertex3d(-.25D, .0D, +.50D);
                            GL11.glVertex3d(-.50D, .0D, -.50D);
                            GL11.glVertex3d(-.25D, .0D, -.25D);
                            GL11.glVertex3d(-.50D, .0D, -.25D);
                            GL11.glVertex3d(-.25D, .0D, -.50D);
                            break;
                    }
                }
            }
        }
        GL11.glEnd();
        // draw turning indicator
        if (aIsWrench && tTile instanceof IGregTechTileEntity &&
                ((IGregTechTileEntity) tTile).getMetaTileEntity() instanceof IAlignmentProvider) {
            IAlignment tAlignment = ((IAlignmentProvider) ((IGregTechTileEntity) tTile).getMetaTileEntity()).getAlignment();
            if (tAlignment != null) {
                ForgeDirection direction = tAlignment.getDirection();
                if (direction.ordinal() == tSideHit)
                    drawExtendedRotationMarker(ROTATION_MARKER_TRANSFORM_CENTER, aIsSneaking, false);
                else if (direction.getOpposite().ordinal() == tSideHit) {
                    for (Transformation t : ROTATION_MARKER_TRANSFORMS_CORNER) {
                        drawExtendedRotationMarker(t, aIsSneaking, true);
                    }
                } else {
                    drawExtendedRotationMarker(ROTATION_MARKER_TRANSFORMS_SIDES_TRANSFORMS[ROTATION_MARKER_TRANSFORMS_SIDES[tSideHit * 6 + direction.ordinal()]], aIsSneaking, true);
                }
            }
        }
        GL11.glPopMatrix(); // get back to player center
    }

    private static void drawExtendedRotationMarker(Transformation transform, boolean sneaking, boolean small) {
        if (sneaking)
            drawFlipMarker(transform);
        else
            drawRotationMarker(transform);
    }

    private static void drawRotationMarker(Transformation transform) {
        if (!rotationMarkerDisplayListCompiled) {
            rotationMarkerDisplayList = GLAllocation.generateDisplayLists(1);
            compileRotationMarkerDisplayList(rotationMarkerDisplayList);
            rotationMarkerDisplayListCompiled = true;
        }
        GL11.glPushMatrix();
        transform.glApply();
        GL11.glCallList(rotationMarkerDisplayList);
        GL11.glPopMatrix();
    }

    private static void compileRotationMarkerDisplayList(int displayList) {
        GL11.glNewList(displayList, GL11.GL_COMPILE);
        GL11.glBegin(GL_LINE_LOOP);
        for (int i = 0; i <= ROTATION_MARKER_RESOLUTION; i++) {
            GL11.glVertex3d(Math.cos(i * Math.PI * 1.75 / ROTATION_MARKER_RESOLUTION) * 0.4, 0, Math.sin(i * Math.PI * 1.75 / ROTATION_MARKER_RESOLUTION) * 0.4);
        }
        for (int i = ROTATION_MARKER_RESOLUTION; i >=0; i--) {
            GL11.glVertex3d(Math.cos(i * Math.PI * 1.75 / ROTATION_MARKER_RESOLUTION) * 0.24, 0, Math.sin(i * Math.PI * 1.75 / ROTATION_MARKER_RESOLUTION) * 0.24);
        }
        GL11.glVertex3d(0.141114561800, 0, 0);
        GL11.glVertex3d(0.32, 0, -0.178885438199);
        GL11.glVertex3d(0.498885438199, 0, 0);
        GL11.glEnd();
        GL11.glEndList();
    }

    private static void drawFlipMarker(Transformation transform) {
        GL11.glPushMatrix();
        transform.glApply();
        Tessellator t = Tessellator.instance;
        // right shape
        GL11.glLineStipple(4, (short) 0xAAAA);
        GL11.glEnable(GL11.GL_LINE_STIPPLE);
        t.startDrawing(GL11.GL_LINE_STRIP);
        t.addVertex(0.1d, 0d, 0.04d);
        t.addVertex(0.1d, 0d, 0.2d);
        t.addVertex(0.35d, 0d, 0.35d);
        t.addVertex(0.35d, 0d, -0.35d);
        t.addVertex(0.1d, 0d, -0.2d);
        t.addVertex(0.1d, 0d, -0.04d);
        t.draw();
        GL11.glDisable(GL11.GL_LINE_STIPPLE);
        // left shape
        t.startDrawing(GL11.GL_LINE_STRIP);
        t.addVertex(-0.1d, 0d, 0.04d);
        t.addVertex(-0.1d, 0d, 0.2d);
        t.addVertex(-0.35d, 0d, 0.35d);
        t.addVertex(-0.35d, 0d, -0.35d);
        t.addVertex(-0.1d, 0d, -0.2d);
        t.addVertex(-0.1d, 0d, -0.04d);
        t.draw();
        // arrow
        t.startDrawing(GL11.GL_LINE_LOOP);
        t.addVertex(0.15d, 0d, -0.04d);
        t.addVertex(0.15d, 0d, -0.1d);
        t.addVertex(0.25d, 0d, 0.d);
        t.addVertex(0.15d, 0d, 0.1d);
        t.addVertex(0.15d, 0d, 0.04d);
        t.addVertex(-0.15d, 0d, 0.04d);
        t.addVertex(-0.15d, 0d, 0.1d);
        t.addVertex(-0.25d, 0d, 0.d);
        t.addVertex(-0.15d, 0d, -0.1d);
        t.addVertex(-0.15d, 0d, -0.04d);
        t.draw();
        GL11.glPopMatrix();
    }

    @Override
    public boolean isServerSide() {
        return true;
    }

    @Override
    public boolean isClientSide() {
        return true;
    }

    @Override
    public boolean isBukkitSide() {
        return false;
    }

    @Override
    public EntityPlayer getThePlayer() {
        return Minecraft.getMinecraft().thePlayer;
    }

    @Override
    public int addArmor(String aPrefix) {
        return RenderingRegistry.addNewArmourRendererPrefix(aPrefix);
    }

    @Override
    public void onPreLoad() {
        super.onPreLoad();
        String[] arr = {
                "renadi", "hanakocz", "MysteryDump", "Flaver4", "x_Fame", "Peluche321", "Goshen_Ithilien", "manf", "Bimgo", "leagris",
                "IAmMinecrafter02", "Cerous", "Devilin_Pixy", "Bkarlsson87", "BadAlchemy", "CaballoCraft", "melanclock", "Resursator", "demanzke", "AndrewAmmerlaan",
                "Deathlycraft", "Jirajha", "Axlegear", "kei_kouma", "Dracion", "dungi", "Dorfschwein", "Zero Tw0", "mattiagraz85", "sebastiank30",
                "Plem", "invultri", "grillo126", "malcanteth", "Malevolence_", "Nicholas_Manuel", "Sirbab", "kehaan", "bpgames123", "semig0d",
                "9000bowser", "Sovereignty89", "Kris1432", "xander_cage_", "samuraijp", "bsaa", "SpwnX", "tworf", "Kadah", "kanni",
                "Stute", "Hegik", "Onlyme", "t3hero", "Hotchi", "jagoly", "Nullav", "BH5432", "Sibmer", "inceee",
                "foxxx0", "Hartok", "TMSama", "Shlnen", "Carsso", "zessirb", "meep310", "Seldron", "yttr1um", "hohounk",
                "freebug", "Sylphio", "jmarler", "Saberawr", "r00teniy", "Neonbeta", "yinscape", "voooon24", "Quintine", "peach774",
                "lepthymo", "bildeman", "Kremnari", "Aerosalo", "OndraSter", "oscares91", "mr10movie", "Daxx367x2", "EGERTRONx", "aka13_404",
                "Abouttabs", "Johnstaal", "djshiny99", "megatronp", "DZCreeper", "Kane_Hart", "Truculent", "vidplace7", "simon6689", "MomoNasty",
                "UnknownXLV", "goreacraft", "Fluttermine", "Daddy_Cecil", "MrMaleficus", "TigersFangs", "cublikefoot", "chainman564", "NikitaBuker", "Misha999777",
                "25FiveDetail", "AntiCivilBoy", "michaelbrady", "xXxIceFirexXx", "Speedynutty68", "GarretSidzaka", "HallowCharm977", "mastermind1919", "The_Hypersonic", "diamondguy2798",
                "zF4ll3nPr3d4t0r", "CrafterOfMines57", "XxELIT3xSNIP3RxX", "SuterusuKusanagi", "xavier0014", "adamros", "alexbegt"
        };
        for (String tName : arr) {
            mCapeList.add(tName.toLowerCase());
        }
        new Thread(this).start();

        mPollutionRenderer.preLoad();

        mPreference = new GT_ClientPreference(GregTech_API.sClientDataFile);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        new GT_Renderer_Block();
        new GT_MetaGenerated_Item_Renderer();
        new GT_MetaGenerated_Tool_Renderer();
        new GT_Renderer_Entity_Arrow(GT_Entity_Arrow.class, "arrow");
        new GT_Renderer_Entity_Arrow(GT_Entity_Arrow_Potion.class, "arrow_potions");
        new GT_FlaskRenderer();
        new GT_FluidDisplayStackRenderer();
    }

    @Override
    public void onPostLoad() {
        super.onPostLoad();
        try {
            label0:
            for (int i = 1; i < GregTech_API.METATILEENTITIES.length; i++)
                do {
                    if (i >= GregTech_API.METATILEENTITIES.length)
                        continue label0;
                    if (GregTech_API.METATILEENTITIES[i] != null) {
                        GregTech_API.METATILEENTITIES[i].getStackForm(1L).getTooltip(null, true);
                        GT_Log.out.println("META " + i + " " + GregTech_API.METATILEENTITIES[i].getMetaName());
                    }
                    i++;
                } while (true);
        } catch (Throwable e) {
            e.printStackTrace(GT_Log.err);
        }


//        super.onPostLoad();
//
//            for (int i = 1; i < GregTech_API.METATILEENTITIES.length; i++) {
//              try {
//                for (; i < GregTech_API.METATILEENTITIES.length; i++) if (GregTech_API.METATILEENTITIES[i] != null) GregTech_API.METATILEENTITIES[i].getStackForm(1L).getTooltip(null, true);
//              } catch (Throwable e) {
//                e.printStackTrace(GT_Log.err);
//              }   //weak lmao
//            }
    }

    @Override
    public void run() {
        GT_Log.out.println("GT_Mod: Downloading Cape List.");
        try (Scanner tScanner = new Scanner(new URL(GT_CAPE_LIST_URL).openStream())) {
            while (tScanner.hasNextLine()) {
                this.mCapeList.add(tScanner.nextLine().toLowerCase());
            }
        } catch (Throwable e) {
            e.printStackTrace(GT_Log.err);
        }
        GT_Log.out.println("GT New Horizons: Downloading Cape List.");
        try (Scanner tScanner = new Scanner(new URL(GTMEGA_CAPE_LIST_URL).openStream())) {
            while (tScanner.hasNextLine()) {
                String tName = tScanner.nextLine().toLowerCase();
                if (tName.contains(":")) {
                    if (!this.mCapeList.contains(tName.substring(0, tName.indexOf(":")))) {
                        this.mCapeList.add(tName);
                    }
                } else {
                    this.mCapeList.add(tName);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace(GT_Log.err);
        }
    }

    @Override
    @SubscribeEvent
    public void onClientConnectedToServerEvent(FMLNetworkEvent.ClientConnectedToServerEvent aEvent) {
        mFirstTick = true;
    }

    @SubscribeEvent
    public void receiveRenderSpecialsEvent(net.minecraftforge.client.event.RenderPlayerEvent.Specials.Pre aEvent) {
        mCapeRenderer.receiveRenderSpecialsEvent(aEvent);
    }

    @SubscribeEvent
    public void onPlayerTickEventClient(TickEvent.PlayerTickEvent aEvent) {
        if ((aEvent.side.isClient()) && (aEvent.phase == TickEvent.Phase.END) && (!aEvent.player.isDead)) {
            if (mFirstTick) {
                mFirstTick = false;
                GT_Values.NW.sendToServer(new GT_Packet_ClientPreference(mPreference));
            }
            afterSomeTime++;
            if (afterSomeTime >= 100L) {
                afterSomeTime = 0;
                StatFileWriter sfw = Minecraft.getMinecraft().thePlayer.getStatFileWriter();
                try {
                    for (GT_Recipe recipe : GT_Recipe.GT_Recipe_Map.sAssemblylineVisualRecipes.mRecipeList) {
                        recipe.mHidden = GT_Values.hideAssLineRecipes && !sfw.hasAchievementUnlocked(GT_Mod.achievements.getAchievement(recipe.getOutput(0).getUnlocalizedName()));
                    }
                } catch (Exception ignored) {
                }
            }
            for (Iterator<Map.Entry<GT_PlayedSound, Integer>> iterator = GT_Utility.sPlayedSoundMap.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<GT_PlayedSound, Integer> tEntry = iterator.next();
                if (tEntry.getValue() < 0) {
                    iterator.remove();
                } else {
                    tEntry.setValue(tEntry.getValue() - 1);
                }
            }
            if (!GregTech_API.mServerStarted) GregTech_API.mServerStarted = true;
            if (GT_Values.updateFluidDisplayItems) {
                MovingObjectPosition trace = Minecraft.getMinecraft().objectMouseOver;
                if (trace != null && trace.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK &&
                        (mLastUpdatedBlockX != trace.blockX &&
                                mLastUpdatedBlockY != trace.blockY &&
                                mLastUpdatedBlockZ != trace.blockZ || afterSomeTime % 10 == 0)) {
                    mLastUpdatedBlockX = trace.blockX;
                    mLastUpdatedBlockY = trace.blockY;
                    mLastUpdatedBlockZ = trace.blockZ;
                    TileEntity tileEntity = aEvent.player.worldObj.getTileEntity(trace.blockX, trace.blockY, trace.blockZ);
                    if (tileEntity instanceof IGregTechTileEntity) {
                        IGregTechTileEntity gtTile = (IGregTechTileEntity) tileEntity;
                        if (gtTile.getMetaTileEntity() instanceof IHasFluidDisplayItem) {
                            GT_Values.NW.sendToServer(new MessageUpdateFluidDisplayItem(trace.blockX, trace.blockY, trace.blockZ, gtTile.getWorld().provider.dimensionId));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onDrawBlockHighlight(DrawBlockHighlightEvent aEvent) {
        Block aBlock = aEvent.player.worldObj.getBlock(aEvent.target.blockX, aEvent.target.blockY, aEvent.target.blockZ);
        TileEntity aTileEntity = aEvent.player.worldObj.getTileEntity(aEvent.target.blockX, aEvent.target.blockY, aEvent.target.blockZ);

        if (GT_Utility.isStackInList(aEvent.currentItem, GregTech_API.sWrenchList))
        {
            if (aTileEntity instanceof ITurnable || ROTATABLE_VANILLA_BLOCKS.contains(aBlock) || aTileEntity instanceof IWrenchable)
                drawGrid(aEvent, false, true, aEvent.player.isSneaking());
            return;
        }

        if (!(aTileEntity instanceof ICoverable))
            return;

        if (GT_Utility.isStackInList(aEvent.currentItem, GregTech_API.sWireCutterList) ||
                GT_Utility.isStackInList(aEvent.currentItem, GregTech_API.sSolderingToolList)) {
            if (((ICoverable) aTileEntity).getCoverIDAtSide((byte) aEvent.target.sideHit) == 0)
                drawGrid(aEvent, false, false, aEvent.player.isSneaking());
            return;
        }

        if ((aEvent.currentItem == null && aEvent.player.isSneaking()) ||
                GT_Utility.isStackInList(aEvent.currentItem, GregTech_API.sCrowbarList) ||
                GT_Utility.isStackInList(aEvent.currentItem, GregTech_API.sScrewdriverList)) {
            if (((ICoverable) aTileEntity).getCoverIDAtSide((byte) aEvent.target.sideHit) == 0)
                for (byte i = 0; i < 6; i++)
                    if (((ICoverable) aTileEntity).getCoverIDAtSide(i) > 0) {
                        drawGrid(aEvent, true, false, true);
                        return;
                    }
            return;
        }

        if (GT_Utility.isStackInList(aEvent.currentItem, GregTech_API.sCovers.keySet())) {
            if (((ICoverable) aTileEntity).getCoverIDAtSide((byte) aEvent.target.sideHit) == 0)
                drawGrid(aEvent, true, false, aEvent.player.isSneaking());
        }
    }

    @SubscribeEvent
    public void receiveRenderEvent(net.minecraftforge.client.event.RenderPlayerEvent.Pre aEvent) {
        if (GT_Utility.getFullInvisibility(aEvent.entityPlayer)) {
            aEvent.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onClientTickEvent(cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent aEvent) {
        if (aEvent.phase == cpw.mods.fml.common.gameevent.TickEvent.Phase.END) {
            if (changeDetected > 0) changeDetected--;
            int newHideValue = shouldHeldItemHideThings();
            if (newHideValue != hideValue) {
                hideValue = newHideValue;
                changeDetected = 5;
            }
            mAnimationTick++;
            if (mAnimationTick % 50L == 0L) {
                mAnimationDirection = !mAnimationDirection;
            }
            int tDirection = mAnimationDirection ? 1 : -1;
            for (Object o : mPosR) {
                Materials tMaterial = (Materials) o;
                tMaterial.mRGBa[0] += tDirection;
            }

            for (Materials tMaterial : mPosG) {
                tMaterial.mRGBa[1] += tDirection;
            }

            for (Materials tMaterial : mPosB) {
                tMaterial.mRGBa[2] += tDirection;
            }

            for (Materials tMaterial : mPosA) {
                tMaterial.mRGBa[3] += tDirection;
            }

            for (Materials tMaterial : mNegR) {
                tMaterial.mRGBa[0] -= tDirection;
            }

            for (Materials tMaterial : mNegG) {
                tMaterial.mRGBa[1] -= tDirection;
            }

            for (Materials tMaterial : mNegB) {
                tMaterial.mRGBa[2] -= tDirection;
            }

            for (Materials tMaterial : mNegA) {
                tMaterial.mRGBa[3] -= tDirection;
            }

            for (Materials tMaterial : mMoltenPosR) {
                tMaterial.mMoltenRGBa[0] += tDirection;
            }

            for (Materials tMaterial : mMoltenPosG) {
                tMaterial.mMoltenRGBa[1] += tDirection;
            }

            for (Materials tMaterial : mMoltenPosB) {
                tMaterial.mMoltenRGBa[2] += tDirection;
            }

            for (Materials tMaterial : mMoltenPosA) {
                tMaterial.mMoltenRGBa[3] += tDirection;
            }

            for (Materials tMaterial : mMoltenNegR) {
                tMaterial.mMoltenRGBa[0] -= tDirection;
            }

            for (Materials tMaterial : mMoltenNegG) {
                tMaterial.mMoltenRGBa[1] -= tDirection;
            }

            for (Materials tMaterial : mMoltenNegB) {
                tMaterial.mMoltenRGBa[2] -= tDirection;
            }

            for (Materials tMaterial : mMoltenNegA) {
                tMaterial.mMoltenRGBa[3] -= tDirection;
            }

        }
    }

    @Override
    public void doSonictronSound(ItemStack aStack, World aWorld, double aX, double aY, double aZ) {
        if (GT_Utility.isStackInvalid(aStack))
            return;
        String tString = "note.harp";
        int i = 0;
        int j = mSoundItems.size();
        do {
            if (i >= j)
                break;
            if (GT_Utility.areStacksEqual(mSoundItems.get(i), aStack)) {
                tString = mSoundNames.get(i);
                break;
            }
            i++;
        } while (true);
        if (tString.startsWith("random.explode"))
            if (aStack.stackSize == 3)
                tString = "random.fuse";
            else if (aStack.stackSize == 2)
                tString = "random.old_explode";
        if (tString.startsWith("streaming."))
            switch (aStack.stackSize) {
                case 1: // '\001'
                    tString = (new StringBuilder()).append(tString).append("13").toString();
                    break;

                case 2: // '\002'
                    tString = (new StringBuilder()).append(tString).append("cat").toString();
                    break;

                case 3: // '\003'
                    tString = (new StringBuilder()).append(tString).append("blocks").toString();
                    break;

                case 4: // '\004'
                    tString = (new StringBuilder()).append(tString).append("chirp").toString();
                    break;

                case 5: // '\005'
                    tString = (new StringBuilder()).append(tString).append("far").toString();
                    break;

                case 6: // '\006'
                    tString = (new StringBuilder()).append(tString).append("mall").toString();
                    break;

                case 7: // '\007'
                    tString = (new StringBuilder()).append(tString).append("mellohi").toString();
                    break;

                case 8: // '\b'
                    tString = (new StringBuilder()).append(tString).append("stal").toString();
                    break;

                case 9: // '\t'
                    tString = (new StringBuilder()).append(tString).append("strad").toString();
                    break;

                case 10: // '\n'
                    tString = (new StringBuilder()).append(tString).append("ward").toString();
                    break;

                case 11: // '\013'
                    tString = (new StringBuilder()).append(tString).append("11").toString();
                    break;

                case 12: // '\f'
                    tString = (new StringBuilder()).append(tString).append("wait").toString();
                    break;

                default:
                    tString = (new StringBuilder()).append(tString).append("wherearewenow").toString();
                    break;
            }
        if (tString.startsWith("streaming.")) {
            new WorldSpawnedEventBuilder.RecordEffectEventBuilder()
                    .setIdentifier(tString.substring(10))
                    .setPosition(aX, aY, aZ)
                    .run();
        } else {
            new WorldSpawnedEventBuilder.SoundEventBuilder()
                    .setVolume(3f)
                    .setPitch(tString.startsWith("note.") ? (float) Math.pow(2D, (double) (aStack.stackSize - 13) / 12D) : 1.0F)
                    .setIdentifier(tString)
                    .setPosition(aX, aY, aZ)
                    .run();
        }
    }

    public static int hideValue = 0;

    /**
     * <p>Client tick counter that is set to 5 on hiding pipes and covers.</p>
     * <p>It triggers a texture update next client tick when reaching 4, with provision for 3 more update tasks,
     * spreading client change detection related work and network traffic on different ticks, until it reaches 0.</p>
     */
    public static int changeDetected = 0;

    private static int shouldHeldItemHideThings() {
        try {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            if (player == null) return 0;
            ItemStack tCurrentItem = player.getCurrentEquippedItem();
            if (tCurrentItem == null) return 0;
            int[] ids = OreDictionary.getOreIDs(tCurrentItem);
            int hide = 0;
            for (int i : ids) {
                if (OreDictionary.getOreName(i).equals("craftingToolSolderingIron")) {
                    hide |= 0x1;
                    break;
                }
            }
            if (GT_Utility.isStackInList(tCurrentItem, GregTech_API.sWrenchList)
                    || GT_Utility.isStackInList(tCurrentItem, GregTech_API.sScrewdriverList)
                    || GT_Utility.isStackInList(tCurrentItem, GregTech_API.sHardHammerList)
                    || GT_Utility.isStackInList(tCurrentItem, GregTech_API.sSoftHammerList)
                    || GT_Utility.isStackInList(tCurrentItem, GregTech_API.sWireCutterList)
                    || GT_Utility.isStackInList(tCurrentItem, GregTech_API.sSolderingToolList)
                    || GT_Utility.isStackInList(tCurrentItem, GregTech_API.sCrowbarList)
                    || GregTech_API.sCovers.containsKey(new GT_ItemStack(tCurrentItem))) {
                hide |= 0x2;
            }
            return hide;
        } catch (Exception e) {
            return 0;
        }
    }

    public static void recieveChunkPollutionPacket(ChunkCoordIntPair chunk, int pollution) {
        mPollutionRenderer.processPacket(chunk, pollution);
    }
}
