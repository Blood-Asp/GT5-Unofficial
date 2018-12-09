package gregtech.loaders.misc;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.objects.GT_CopiedBlockTexture;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_ModHandler;
import gregtech.common.covers.GT_Cover_Vent;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class GT_CoverLoader
        implements Runnable {
    public void run() {
        for (byte i = 0; i < 16; i = (byte) (i + 1)) {
            GregTech_API.registerCover(new ItemStack(Blocks.carpet, 1, i), new GT_CopiedBlockTexture(Blocks.wool, 0, i), null);
        }
        GregTech_API.registerCover(GT_ModHandler.getIC2Item("reactorVent", 1L, 1), new GT_RenderedTexture(Textures.BlockIcons.VENT_NORMAL), new GT_Cover_Vent(1));
        GregTech_API.registerCover(GT_ModHandler.getIC2Item("reactorVentCore", 1L, 1), new GT_RenderedTexture(Textures.BlockIcons.VENT_NORMAL), new GT_Cover_Vent(1));
        GregTech_API.registerCover(GT_ModHandler.getIC2Item("reactorVentGold", 1L, 1), new GT_RenderedTexture(Textures.BlockIcons.VENT_ADVANCED), new GT_Cover_Vent(2));
        GregTech_API.registerCover(GT_ModHandler.getIC2Item("reactorVentSpread", 1L), new GT_RenderedTexture(Textures.BlockIcons.VENT_NORMAL), new GT_Cover_Vent(2));
        GregTech_API.registerCover(GT_ModHandler.getIC2Item("reactorVentDiamond", 1L, 1), new GT_RenderedTexture(Textures.BlockIcons.VENT_ADVANCED), new GT_Cover_Vent(3));
    }
}
