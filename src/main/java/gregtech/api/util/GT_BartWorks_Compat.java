package gregtech.api.util;

import com.github.bartimaeusnek.bartworks.API.VoidMinerDropAdder;
import com.github.bartimaeusnek.bartworks.API.WerkstoffAPI;
import com.github.bartimaeusnek.bartworks.system.material.Werkstoff;
import gregtech.api.enums.Materials;
import gregtech.api.interfaces.ISubTagContainer;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class GT_BartWorks_Compat {

    public static Materials getBartWorksMaterialByVarName(String name) {
        Materials materials = Materials._NULL;
        try {
            materials = new WerkstoffAPI().getWerkstoff(name).getBridgeMaterial();
        } catch (NoSuchFieldException | IllegalAccessException exception) {
            exception.printStackTrace();
        }
        return materials;
    }

    public static Materials getBartWorksMaterialByIGNName(String name) {
        Optional<Werkstoff> material = Werkstoff.werkstoffHashSet.stream().filter(e -> e.getDefaultName().equals(name)).findFirst();
        if (material.isPresent()) {
            return material.get().getBridgeMaterial();
        }
        return Materials._NULL;
    }

    public static Materials getBartWorksMaterialByID(int id) {
        return Optional.ofNullable(Optional.ofNullable(Werkstoff.werkstoffHashMap.get((short) id)).orElse(Werkstoff.default_null_Werkstoff).getBridgeMaterial()).orElse(Materials._NULL);
    }

    public static void addVoidMinerDropsToDimension(int dimID, ISubTagContainer material, float chance){
        try {
            VoidMinerDropAdder.addDropsToDim(dimID, material, chance);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}