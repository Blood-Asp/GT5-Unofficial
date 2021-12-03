package gregtech.loaders.misc;

import gregtech.common.net.MessageSetIntegratedCircuit;

public class NetworkDispatcher extends eu.usrv.yamcore.network.PacketDispatcher{
    public static NetworkDispatcher INSTANCE;
    public NetworkDispatcher() {
        super("gregtech");
        INSTANCE = this;
        registerPackets();
    }

    @Override
    public void registerPackets() {
        registerMessage(MessageSetIntegratedCircuit.clientHandler.class,MessageSetIntegratedCircuit.class);
        registerMessage(MessageSetIntegratedCircuit.ServerHandler.class,MessageSetIntegratedCircuit.class);
    }
}
