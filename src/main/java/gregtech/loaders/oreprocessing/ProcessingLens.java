package gregtech.loaders.oreprocessing;

import gregtech.api.GregTech_API;
import gregtech.api.enums.GT_Values;
import gregtech.api.enums.Materials;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.enums.Textures;
import gregtech.api.objects.GT_MultiTexture;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_OreDictUnificator;
import net.minecraft.item.ItemStack;

public class ProcessingLens implements gregtech.api.interfaces.IOreRecipeRegistrator {
    public ProcessingLens() {
        OrePrefixes.lens.add(this);
    }

    public void registerOre(OrePrefixes aPrefix, Materials aMaterial, String aOreDictName, String aModName, ItemStack aStack) {
        GT_Values.RA.addLatheRecipe(GT_OreDictUnificator.get(OrePrefixes.plate, aMaterial, 1L), GT_OreDictUnificator.get(OrePrefixes.lens, aMaterial, 1L), GT_OreDictUnificator.get(OrePrefixes.dustSmall, aMaterial, 1L), (int) Math.max(aMaterial.getMass() / 2L, 1L), 480);
        GT_Values.RA.addLatheRecipe(GT_OreDictUnificator.get(OrePrefixes.gemExquisite, aMaterial, 1L), GT_OreDictUnificator.get(OrePrefixes.lens, aMaterial, 1L), GT_OreDictUnificator.get(OrePrefixes.dust, aMaterial, 2L), (int) Math.max(aMaterial.getMass() , 1L), 24);
        GregTech_API.registerCover(aStack, new GT_MultiTexture(new gregtech.api.interfaces.ITexture[]{Textures.BlockIcons.MACHINE_CASINGS[2][0], new GT_RenderedTexture(Textures.BlockIcons.OVERLAY_LENS, aMaterial.mRGBa, false)}), new gregtech.common.covers.GT_Cover_Lens(aMaterial.mColor.mIndex));
    }
}
