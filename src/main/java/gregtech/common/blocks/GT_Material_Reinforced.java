package gregtech.common.blocks;

import net.minecraft.block.material.MapColour;
import net.minecraft.block.material.Material;

public class GT_Material_Reinforced
        extends Material {
    public GT_Material_Reinforced() {
        super(MapColour.stoneColour);
        setRequiresTool();
        setAdventureModeExempt();
    }

    public boolean isOpaque() {
        return true;
    }
}
