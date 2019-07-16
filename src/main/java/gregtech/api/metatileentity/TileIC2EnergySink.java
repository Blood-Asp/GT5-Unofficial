package gregtech.api.metatileentity;

import com.google.common.collect.Sets;
import gregtech.api.interfaces.metatileentity.IConnectable;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.metatileentity.IMetaTileEntityCable;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaPipeEntity_Cable;
import gregtech.api.util.GT_Utility;
import ic2.api.energy.tile.IEnergySink;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class TileIC2EnergySink extends TileEntity implements IEnergySink {
    private IGregTechTileEntity myMeta;
    private GT_MetaPipeEntity_Cable cableMeta = null;

    public TileIC2EnergySink(IGregTechTileEntity meta) {
        if (meta == null) throw new NullPointerException("no null metas");
        myMeta = meta;
        final IMetaTileEntity metaTile = myMeta.getMetaTileEntity();
        if (metaTile instanceof IMetaTileEntityCable) {
            cableMeta = (GT_MetaPipeEntity_Cable) metaTile;
        }
    }
    /*
     *
     * IC2 enet compat - IEnergySink
     *
     */

    /**
     * Determine how much energy the sink accepts.
     *
     * Make sure that injectEnergy() does accepts energy if demandsEnergy() returns anything > 0.
     *
     * @note Modifying the energy net from this method is disallowed.
     *
     * @return max accepted input in eu
     */
    @Override
    public double getDemandedEnergy() {
        if(cableMeta != null) {
            return (cableMeta.mVoltage * cableMeta.mAmperage);
        }
        else
            return myMeta.getEUCapacity() - myMeta.getStoredEU();
    }



    /**
     * Determine the tier of this energy sink.
     * 1 = LV, 2 = MV, 3 = HV, 4 = EV etc.
     * @note Return Integer.MAX_VALUE to allow any voltage.
     *
     * @return tier of this energy sink
     */
    @Override
    public int getSinkTier() {
        return GT_Utility.getTier(cableMeta != null ? cableMeta.mVoltage : myMeta.getInputVoltage());
    }

    /**
     * Transfer energy to the sink.
     *
     * It's highly recommended to accept all energy by letting the internal buffer overflow to
     * increase the performance and accuracy of the distribution simulation.
     *
     * @param directionFrom direction from which the energy comes from
     * @param amount energy to be transferred
     * @return Energy not consumed (leftover)
     */
    @Override
    public double injectEnergy(ForgeDirection directionFrom, double amount, double voltage) {

        final long amps =(long)Math.max(amount / (cableMeta != null ? cableMeta.mVoltage : myMeta.getInputVoltage() * 1.0), 1.0);
        final long euPerAmp = (long)(amount / (amps * 1.0));

        final IMetaTileEntity metaTile = myMeta.getMetaTileEntity();
        if (metaTile == null) return amount;

        final long usedAmps;
        if(cableMeta != null) {
            usedAmps = ((IMetaTileEntityCable) metaTile).transferElectricity((byte) directionFrom.ordinal(), Math.min(euPerAmp, cableMeta.mVoltage), amps, Sets.newHashSet((TileEntity) myMeta));

            // [23:17:00] [Client thread/INFO]: [CHAT] §9(§aAOE §aSafe §aMode §ais §aenabled§9) §cOperation §cCanceled §cbecause §ca §ctile §centity §cwas §cdetected
            //[23:17:01] [Server thread/ERROR] [FML]: Exception caught during firing event cpw.mods.fml.common.gameevent.TickEvent$WorldTickEvent@1b066533:
            //java.lang.NullPointerException
            //	at gregtech.api.metatileentity.TileIC2EnergySink.injectEnergy(TileIC2EnergySink.java:83) ~[TileIC2EnergySink.class:?]
            //	at ic2.core.energy.EnergyNetLocal.processChanges(EnergyNetLocal.java:704) ~[EnergyNetLocal.class:?]
            //	at ic2.core.energy.EnergyNetLocal.onTickEnd(EnergyNetLocal.java:389) ~[EnergyNetLocal.class:?]
            //	at ic2.core.energy.EnergyNetGlobal.onTickEnd(EnergyNetGlobal.java:69) ~[EnergyNetGlobal.class:?]
            //	at ic2.core.TickHandler.onWorldTick(TickHandler.java:79) ~[TickHandler.class:?]
            //	at cpw.mods.fml.common.eventhandler.ASMEventHandler_85_TickHandler_onWorldTick_WorldTickEvent.invoke(.dynamic) ~[?:?]
            //	at cpw.mods.fml.common.eventhandler.ASMEventHandler.invoke(ASMEventHandler.java:54) ~[ASMEventHandler.class:?]
            //	at cpw.mods.fml.common.eventhandler.EventBus.post(EventBus.java:140) ~[EventBus.class:?]
            //	at cpw.mods.fml.common.FMLCommonHandler.onPostWorldTick(FMLCommonHandler.java:255) ~[FMLCommonHandler.class:?]
            //	at net.minecraft.server.MinecraftServer.func_71190_q(MinecraftServer.java:645) ~[MinecraftServer.class:?]
            //	at net.minecraft.server.MinecraftServer.func_71217_p(MinecraftServer.java:547) ~[MinecraftServer.class:?]
            //	at net.minecraft.server.integrated.IntegratedServer.func_71217_p(IntegratedServer.java:111) ~[bsx.class:?]
            //	at net.minecraft.server.MinecraftServer.run(MinecraftServer.java:396) [MinecraftServer.class:?]
            //	at net.minecraft.server.MinecraftServer$2.run(MinecraftServer.java:685) [?:?]
        }
        else
            usedAmps = myMeta.injectEnergyUnits((byte) directionFrom.ordinal(), Math.min(euPerAmp, myMeta.getInputVoltage()), amps);
        return amount - ( usedAmps * euPerAmp);

        // transferElectricity for cables
    }

    /**
     * Determine if this acceptor can accept current from an adjacent emitter in a direction.
     *
     * The TileEntity in the emitter parameter is what was originally added to the energy net,
     * which may be normal in-world TileEntity, a delegate or an IMetaDelegate.
     *
     * @param emitter energy emitter, may also be null or an IMetaDelegate
     * @param direction direction the energy is being received from
     */
    @Override
    public boolean acceptsEnergyFrom(TileEntity emitter, ForgeDirection direction) {
        final IMetaTileEntity metaTile = myMeta.getMetaTileEntity();
        if(metaTile instanceof IMetaTileEntityCable && (direction == ForgeDirection.UNKNOWN || ((IConnectable)metaTile).isConnectedAtSide(direction.ordinal())))
            return true;
        else
            return myMeta.inputEnergyFrom((byte) direction.ordinal(), false);

    }

}
