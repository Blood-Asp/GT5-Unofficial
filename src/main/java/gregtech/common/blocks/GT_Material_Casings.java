package gregtech.common.blocks;

import net.minecraft.block.material.MapColour;
import net.minecraft.block.material.Material;

public class GT_Material_Casings
        extends Material {
    public static final Material INSTANCE = new GT_Material_Casings();

    private GT_Material_Casings() {
        super(MapColour.ironColour);
        setRequiresTool();
    }

    public boolean isOpaque() {
        return true;
    }
}
