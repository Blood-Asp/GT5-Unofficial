package gregtech.loaders.preload;

import gregtech.api.util.GT_Log;
import gregtech.common.redstonecircuits.GT_Circuit_BasicLogic;
import gregtech.common.redstonecircuits.GT_Circuit_BitAnd;
import gregtech.common.redstonecircuits.GT_Circuit_CombinationLock;
import gregtech.common.redstonecircuits.GT_Circuit_Equals;
import gregtech.common.redstonecircuits.GT_Circuit_Pulser;
import gregtech.common.redstonecircuits.GT_Circuit_Randomizer;
import gregtech.common.redstonecircuits.GT_Circuit_RedstoneMeter;
import gregtech.common.redstonecircuits.GT_Circuit_Repeater;
import gregtech.common.redstonecircuits.GT_Circuit_Timer;

public class GT_Loader_CircuitBehaviors
        implements Runnable {
    public void run() {
        GT_Log.out.println("GT_Mod: Register Redstone Circuit behaviours.");
        new GT_Circuit_Timer(0);
        new GT_Circuit_BasicLogic(1);
        new GT_Circuit_Repeater(2);
        new GT_Circuit_Pulser(3);
        new GT_Circuit_RedstoneMeter(4);

        new GT_Circuit_Randomizer(8);

        new GT_Circuit_CombinationLock(16);
        new GT_Circuit_BitAnd(17);
        new GT_Circuit_Equals(18);
    }
}
