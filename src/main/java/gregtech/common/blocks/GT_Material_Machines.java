package gregtech.common.blocks;

import net.minecraft.block.material.MapColour;
import net.minecraft.block.material.Material;

public class GT_Material_Machines
        extends Material {
    public GT_Material_Machines() {
        super(MapColour.ironColour);
        setRequiresTool();
        setImmovableMobility();
        setAdventureModeExempt();
    }

    public boolean isOpaque() {
        return false;
    }
}
