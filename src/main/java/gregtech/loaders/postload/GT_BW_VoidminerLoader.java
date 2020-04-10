package gregtech.loaders.postload;


import com.github.bartimaeusnek.bartworks.system.material.Werkstoff;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.interfaces.ISubTagContainer;

import java.util.Arrays;

import static gregtech.api.enums.Materials.values;
import static gregtech.api.util.GT_BartWorks_Compat.addVoidMinerDropsToDimension;

public class GT_BW_VoidminerLoader {

    private GT_BW_VoidminerLoader() {
    }

    private static final int DEEPDARK_ID = 100;

    private static boolean hasOres(Materials materials){
        return (materials.mTypes & 8) != 0;
    }

    private static boolean hasOres(Werkstoff materials){
        return materials.hasItemType(OrePrefixes.ore);
    }

    private static void addVoidDimerDrops(ISubTagContainer materials){
        addVoidMinerDropsToDimension(DEEPDARK_ID, materials, 1f);
    }

    public static void initDeepDark() {
        Arrays.stream(values()).filter(GT_BW_VoidminerLoader::hasOres).forEach(GT_BW_VoidminerLoader::addVoidDimerDrops);
        Werkstoff.werkstoffHashSet.stream().filter(GT_BW_VoidminerLoader::hasOres).forEach(GT_BW_VoidminerLoader::addVoidDimerDrops);
    }

}