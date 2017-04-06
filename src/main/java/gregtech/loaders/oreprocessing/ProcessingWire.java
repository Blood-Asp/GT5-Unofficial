package gregtech.loaders.oreprocessing;

import gregtech.api.enums.*;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.api.util.GT_Utility;
import gregtech.common.GT_Proxy;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ProcessingWire implements gregtech.api.interfaces.IOreRecipeRegistrator {
    public ProcessingWire() {
        OrePrefixes.wireGt01.add(this);
        OrePrefixes.wireGt02.add(this);
        OrePrefixes.wireGt04.add(this);
        OrePrefixes.wireGt08.add(this);
        OrePrefixes.wireGt12.add(this);
        OrePrefixes.wireGt16.add(this);
    }

    public void registerOre(OrePrefixes aPrefix, Materials aMaterial, String aOreDictName, String aModName, ItemStack aStack) {
        switch (aPrefix) {
            case wireGt01:
                switch (aMaterial.mName){
                    case "Cobalt": case "Lead": case "Tin": case "Zinc":case "SolderingAlloy":
                        GT_ModHandler.addShapelessCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt01, aMaterial, 1L), new Object[]{aOreDictName, new ItemStack(Blocks.CARPET, 1, 15), new ItemStack(Items.STRING, 1)});
                        GT_Values.RA.addBoxingRecipe(GT_Utility.copyAmount(1L, aStack), new ItemStack(Blocks.CARPET, 1, 15), GT_OreDictUnificator.get(OrePrefixes.cableGt01, aMaterial, 1L), 100, 8);
                        GT_Values.RA.addUnboxingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt01, aMaterial, 1L), GT_Utility.copyAmount(1L, aStack), new ItemStack(Blocks.CARPET, 1, 15), 100, 8);
                        break;
                    case "RedAlloy":
                        GT_ModHandler.addShapelessCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt01, aMaterial, 1L), new Object[]{aOreDictName, OrePrefixes.plate.get(Materials.Paper)});
                        GT_Values.RA.addBoxingRecipe(GT_Utility.copyAmount(1L, aStack), GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Paper, 1L), GT_OreDictUnificator.get(OrePrefixes.cableGt01, aMaterial, 1L), 100, 8);
                        GT_Values.RA.addUnboxingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt01, aMaterial, 1L), GT_Utility.copyAmount(1L, aStack), GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Paper, 1L), 100, 8);
                        break;
                    default:
                        GT_Values.RA.addAssemblerRecipe(aStack, ItemList.Circuit_Integrated.getWithDamage(0L, 24L), Materials.Rubber.getMolten(144L), GT_OreDictUnificator.get(OrePrefixes.cableGt01, aMaterial, 1L), 100, 8);
                        GT_Values.RA.addUnboxingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt01, aMaterial, 1L), GT_Utility.copyAmount(1L, aStack), GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 1L), 100, 8);
                }
                if (!aMaterial.contains(gregtech.api.enums.SubTag.NO_SMASHING)) {
                    GT_Values.RA.addBenderRecipe(GT_Utility.copyAmount(1L, aStack), GT_OreDictUnificator.get(OrePrefixes.springSmall, aMaterial, 2L), 100, 8);
                    GT_Values.RA.addWiremillRecipe(GT_Utility.copyAmount(1L, aStack), GT_OreDictUnificator.get(OrePrefixes.wireFine, aMaterial, 4L), 200, 8);
                    GT_Values.RA.addWiremillRecipe(GT_OreDictUnificator.get(OrePrefixes.ingot, aMaterial, 1L), GT_Utility.copy(GT_Utility.copyAmount(2L, aStack), GT_OreDictUnificator.get(OrePrefixes.wireFine, aMaterial, 8L)), 100, 4);
                    GT_Values.RA.addWiremillRecipe(GT_OreDictUnificator.get(OrePrefixes.stick, aMaterial, 1L), GT_Utility.copy(aStack, GT_OreDictUnificator.get(OrePrefixes.wireFine, aMaterial, 4L)), 50, 4);
                }
                if (aMaterial.mUnificatable && (aMaterial.mMaterialInto == aMaterial) && !aMaterial.contains(SubTag.NO_WORKING))
                    GT_ModHandler.addCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.wireGt01, aMaterial, 1L), GT_Proxy.tBits, new Object[]{"Xx", Character.valueOf('X'), OrePrefixes.plate.get(aMaterial)});
                GT_Values.RA.addAssemblerRecipe(GT_Utility.copyAmount(2L, aStack), ItemList.Circuit_Integrated.getWithDamage(0L, 2L), GT_OreDictUnificator.get(OrePrefixes.wireGt02, aMaterial, 1L), 150, 8);
                GT_Values.RA.addAssemblerRecipe(GT_Utility.copyAmount(4L, aStack), ItemList.Circuit_Integrated.getWithDamage(0L, 4L), GT_OreDictUnificator.get(OrePrefixes.wireGt04, aMaterial, 1L), 200, 8);
                GT_Values.RA.addAssemblerRecipe(GT_Utility.copyAmount(8L, aStack), ItemList.Circuit_Integrated.getWithDamage(0L, 8L), GT_OreDictUnificator.get(OrePrefixes.wireGt08, aMaterial, 1L), 300, 8);
                GT_Values.RA.addAssemblerRecipe(GT_Utility.copyAmount(12L, aStack), ItemList.Circuit_Integrated.getWithDamage(0L, 12L), GT_OreDictUnificator.get(OrePrefixes.wireGt12, aMaterial, 1L), 400, 8);
                GT_Values.RA.addAssemblerRecipe(GT_Utility.copyAmount(16L, aStack), ItemList.Circuit_Integrated.getWithDamage(0L, 16L), GT_OreDictUnificator.get(OrePrefixes.wireGt16, aMaterial, 1L), 500, 8);

//                TODO
//                if (GT_Mod.gregtechproxy.mAE2Integration) {
//                    Api.INSTANCE.registries().p2pTunnel().addNewAttunement(aStack, TunnelType.IC2_POWER);
//                    Api.INSTANCE.registries().p2pTunnel().addNewAttunement(GT_OreDictUnificator.get(OrePrefixes.cableGt01, aMaterial, 1L), TunnelType.IC2_POWER);
//                }
                break;
            case wireGt02:
                switch (aMaterial.mName){
                    case "Cobalt": case "Lead": case "Tin": case "Zinc":case "SolderingAlloy":
                        GT_ModHandler.addShapelessCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt02, aMaterial, 1L), new Object[]{aOreDictName, new ItemStack(Blocks.CARPET, 1, 15), new ItemStack(Items.STRING, 1)});
                        GT_Values.RA.addBoxingRecipe(GT_Utility.copyAmount(1L, aStack), new ItemStack(Blocks.CARPET, 1, 15), GT_OreDictUnificator.get(OrePrefixes.cableGt02, aMaterial, 1L), 100, 8);
                        GT_Values.RA.addUnboxingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt02, aMaterial, 1L), GT_Utility.copyAmount(1L, aStack), new ItemStack(Blocks.CARPET, 1, 15), 100, 8);
                        break;
                    case "RedAlloy":
                        GT_ModHandler.addShapelessCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt02, aMaterial, 1L), new Object[]{aOreDictName, OrePrefixes.plate.get(Materials.Paper)});
                        GT_Values.RA.addBoxingRecipe(GT_Utility.copyAmount(1L, aStack), GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Paper, 1L), GT_OreDictUnificator.get(OrePrefixes.cableGt02, aMaterial, 1L), 100, 8);
                        GT_Values.RA.addUnboxingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt02, aMaterial, 1L), GT_Utility.copyAmount(1L, aStack), GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Paper, 1L), 100, 8);
                        break;
                    default:
                        GT_Values.RA.addAssemblerRecipe(aStack, ItemList.Circuit_Integrated.getWithDamage(0L, 24L), Materials.Rubber.getMolten(144L), GT_OreDictUnificator.get(OrePrefixes.cableGt02, aMaterial, 1L), 100, 8);
                        GT_Values.RA.addUnboxingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt02, aMaterial, 1L), GT_Utility.copyAmount(1L, aStack), GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 1L), 100, 8);
                }
                GT_ModHandler.addShapelessCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.wireGt01, aMaterial, 2L), new Object[]{aOreDictName});
                GT_ModHandler.addShapelessCraftingRecipe(GT_Utility.copyAmount(1L, aStack), new Object[]{OrePrefixes.wireGt01.get(aMaterial), OrePrefixes.wireGt01.get(aMaterial)});
//                TODO
//                if (GT_Mod.gregtechproxy.mAE2Integration) {
//                    Api.INSTANCE.registries().p2pTunnel().addNewAttunement(aStack, TunnelType.IC2_POWER);
//                    Api.INSTANCE.registries().p2pTunnel().addNewAttunement(GT_OreDictUnificator.get(OrePrefixes.cableGt02, aMaterial, 1L), TunnelType.IC2_POWER);
//                }
                break;
            case wireGt04:
                switch (aMaterial.mName){
                    case "Cobalt": case "Lead": case "Tin": case "Zinc":case "SolderingAlloy":
                        GT_ModHandler.addShapelessCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt04, aMaterial, 1L), new Object[]{aOreDictName, new ItemStack(Blocks.CARPET, 1, 15), new ItemStack(Blocks.CARPET, 1, 15), new ItemStack(Items.STRING, 1)});
                        GT_Values.RA.addBoxingRecipe(GT_Utility.copyAmount(1L, aStack), new ItemStack(Blocks.CARPET, 2, 15), GT_OreDictUnificator.get(OrePrefixes.cableGt04, aMaterial, 1L), 100, 8);
                        GT_Values.RA.addUnboxingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt04, aMaterial, 1L), GT_Utility.copyAmount(1L, aStack), new ItemStack(Blocks.CARPET, 2, 15), 100, 8);
                        break;
                    case "RedAlloy":
                        GT_ModHandler.addShapelessCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt04, aMaterial, 1L), new Object[]{aOreDictName, OrePrefixes.plate.get(Materials.Paper), OrePrefixes.plate.get(Materials.Paper)});
                        GT_Values.RA.addBoxingRecipe(GT_Utility.copyAmount(1L, aStack), GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Paper, 2L), GT_OreDictUnificator.get(OrePrefixes.cableGt04, aMaterial, 1L), 100, 8);
                        GT_Values.RA.addUnboxingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt04, aMaterial, 1L), GT_Utility.copyAmount(1L, aStack), GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Paper, 2L), 100, 8);
                        break;
                    default:
                        GT_Values.RA.addAssemblerRecipe(aStack, ItemList.Circuit_Integrated.getWithDamage(0L, 24L), Materials.Rubber.getMolten(288L), GT_OreDictUnificator.get(OrePrefixes.cableGt04, aMaterial, 1L), 100, 8);
                        GT_Values.RA.addUnboxingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt04, aMaterial, 1L), GT_Utility.copyAmount(1L, aStack), GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 2L), 100, 8);
                }
                GT_ModHandler.addShapelessCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.wireGt01, aMaterial, 4L), new Object[]{aOreDictName});
                GT_ModHandler.addShapelessCraftingRecipe(GT_Utility.copyAmount(1L, aStack), new Object[]{OrePrefixes.wireGt02.get(aMaterial), OrePrefixes.wireGt02.get(aMaterial)});
//                TODO
//                if (GT_Mod.gregtechproxy.mAE2Integration) {
//                    Api.INSTANCE.registries().p2pTunnel().addNewAttunement(aStack, TunnelType.IC2_POWER);
//                    Api.INSTANCE.registries().p2pTunnel().addNewAttunement(GT_OreDictUnificator.get(OrePrefixes.cableGt04, aMaterial, 1L), TunnelType.IC2_POWER);
//                }
                break;
            case wireGt08:
                switch (aMaterial.mName){
                    case "Cobalt": case "Lead": case "Tin": case "Zinc":case "SolderingAlloy":
                        GT_ModHandler.addShapelessCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt08, aMaterial, 1L), new Object[]{aOreDictName, new ItemStack(Blocks.CARPET, 1, 15), new ItemStack(Blocks.CARPET, 1, 15), new ItemStack(Blocks.CARPET, 1, 15), new ItemStack(Items.STRING, 1)});
                        GT_Values.RA.addBoxingRecipe(GT_Utility.copyAmount(1L, aStack), new ItemStack(Blocks.CARPET, 3, 15), GT_OreDictUnificator.get(OrePrefixes.cableGt08, aMaterial, 1L), 100, 8);
                        GT_Values.RA.addUnboxingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt08, aMaterial, 1L), GT_Utility.copyAmount(1L, aStack), new ItemStack(Blocks.CARPET, 3, 15), 100, 8);
                        break;
                    case "RedAlloy":
                        GT_ModHandler.addShapelessCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt08, aMaterial, 1L), new Object[]{aOreDictName, OrePrefixes.plate.get(Materials.Paper), OrePrefixes.plate.get(Materials.Paper), OrePrefixes.plate.get(Materials.Paper)});
                        GT_Values.RA.addBoxingRecipe(GT_Utility.copyAmount(1L, aStack), GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Paper, 3L), GT_OreDictUnificator.get(OrePrefixes.cableGt08, aMaterial, 1L), 100, 8);
                        GT_Values.RA.addUnboxingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt08, aMaterial, 1L), GT_Utility.copyAmount(1L, aStack), GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Paper, 3L), 100, 8);
                        break;
                    default:
                        GT_Values.RA.addAssemblerRecipe(aStack, ItemList.Circuit_Integrated.getWithDamage(0L, 24L), Materials.Rubber.getMolten(432L), GT_OreDictUnificator.get(OrePrefixes.cableGt08, aMaterial, 1L), 100, 8);
                        GT_Values.RA.addUnboxingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt08, aMaterial, 1L), GT_Utility.copyAmount(1L, aStack), GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 3L), 100, 8);
                }
                GT_ModHandler.addShapelessCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.wireGt01, aMaterial, 8L), new Object[]{aOreDictName});
                GT_ModHandler.addShapelessCraftingRecipe(GT_Utility.copyAmount(1L, aStack), new Object[]{OrePrefixes.wireGt04.get(aMaterial), OrePrefixes.wireGt04.get(aMaterial)});
//                TODO
//                if (GT_Mod.gregtechproxy.mAE2Integration) {
//                    Api.INSTANCE.registries().p2pTunnel().addNewAttunement(aStack, TunnelType.IC2_POWER);
//                    Api.INSTANCE.registries().p2pTunnel().addNewAttunement(GT_OreDictUnificator.get(OrePrefixes.cableGt08, aMaterial, 1L), TunnelType.IC2_POWER);
//                }
                break;
            case wireGt12:
                switch (aMaterial.mName){
                    case "Cobalt": case "Lead": case "Tin": case "Zinc":case "SolderingAlloy":
                        GT_ModHandler.addShapelessCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt12, aMaterial, 1L), new Object[]{aOreDictName, new ItemStack(Blocks.CARPET, 1, 15), new ItemStack(Blocks.CARPET, 1, 15), new ItemStack(Blocks.CARPET, 1, 15), new ItemStack(Blocks.CARPET, 1, 15), new ItemStack(Items.STRING, 1)});
                        GT_Values.RA.addBoxingRecipe(GT_Utility.copyAmount(1L, aStack), new ItemStack(Blocks.CARPET, 4, 15), GT_OreDictUnificator.get(OrePrefixes.cableGt12, aMaterial, 1L), 100, 8);
                        GT_Values.RA.addUnboxingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt12, aMaterial, 1L), GT_Utility.copyAmount(1L, aStack), new ItemStack(Blocks.CARPET, 4, 15), 100, 8);
                        break;
                    case "RedAlloy":
                        GT_ModHandler.addShapelessCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt12, aMaterial, 1L), new Object[]{aOreDictName, OrePrefixes.plate.get(Materials.Paper), OrePrefixes.plate.get(Materials.Paper), OrePrefixes.plate.get(Materials.Paper), OrePrefixes.plate.get(Materials.Paper)});
                        GT_Values.RA.addBoxingRecipe(GT_Utility.copyAmount(1L, aStack), GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Paper, 4L), GT_OreDictUnificator.get(OrePrefixes.cableGt12, aMaterial, 1L), 100, 8);
                        GT_Values.RA.addUnboxingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt12, aMaterial, 1L), GT_Utility.copyAmount(1L, aStack), GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Paper, 4L), 100, 8);
                        break;
                    default:
                        GT_Values.RA.addAssemblerRecipe(aStack, ItemList.Circuit_Integrated.getWithDamage(0L, 24L), Materials.Rubber.getMolten(576L), GT_OreDictUnificator.get(OrePrefixes.cableGt12, aMaterial, 1L), 100, 8);
                        GT_Values.RA.addUnboxingRecipe(GT_OreDictUnificator.get(OrePrefixes.cableGt12, aMaterial, 1L), GT_Utility.copyAmount(1L, aStack), GT_OreDictUnificator.get(OrePrefixes.plate, Materials.Rubber, 4L), 100, 8);
                }
                GT_ModHandler.addShapelessCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.wireGt01, aMaterial, 12L), new Object[]{aOreDictName});
                GT_ModHandler.addShapelessCraftingRecipe(GT_Utility.copyAmount(1L, aStack), new Object[]{OrePrefixes.wireGt08.get(aMaterial), OrePrefixes.wireGt04.get(aMaterial)});
//                TODO
//                if (GT_Mod.gregtechproxy.mAE2Integration) {
//                    Api.INSTANCE.registries().p2pTunnel().addNewAttunement(aStack, TunnelType.IC2_POWER);
//                    Api.INSTANCE.registries().p2pTunnel().addNewAttunement(GT_OreDictUnificator.get(OrePrefixes.cableGt12, aMaterial, 1L), TunnelType.IC2_POWER);
//                }
                break;
            case wireGt16:
                GT_ModHandler.addShapelessCraftingRecipe(GT_OreDictUnificator.get(OrePrefixes.wireGt01, aMaterial, 16L), new Object[]{aOreDictName});
                GT_ModHandler.addShapelessCraftingRecipe(GT_Utility.copyAmount(1L, aStack), new Object[]{OrePrefixes.wireGt08.get(aMaterial), OrePrefixes.wireGt08.get(aMaterial)});
                GT_ModHandler.addShapelessCraftingRecipe(GT_Utility.copyAmount(1L, aStack), new Object[]{OrePrefixes.wireGt12.get(aMaterial), OrePrefixes.wireGt04.get(aMaterial)});
//                TODO
//                if (GT_Mod.gregtechproxy.mAE2Integration) {
//                    Api.INSTANCE.registries().p2pTunnel().addNewAttunement(aStack, TunnelType.IC2_POWER);
//                }
                break;
        }
    }
}
