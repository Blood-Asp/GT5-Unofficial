package gregtech.common.blocks;

import gregtech.api.capability.internal.IGregTechTileEntity;
import gregtech.api.metatileentity.IMetaTileEntity;
import gregtech.api.metatileentity.PaintableMetaTileEntity;
import gregtech.api.unification.material.type.DustMaterial;
import gregtech.api.unification.material.type.Material;
import gregtech.api.unification.ore.OrePrefix;
import gregtech.api.unification.ore.StoneType;
import gregtech.api.unification.ore.StoneTypes;
import gregtech.api.util.GTLog;
import gregtech.common.metatileentities.MetaTileEntities;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MetaBlocks {

    private MetaBlocks() {}

    public static BlockMachine MACHINE;

    public static BlockBoilerCasing BOILER_CASING;
    public static BlockMetalCasing METAL_CASING;
    public static BlockTurbineCasing TURBINE_CASING;
    public static BlockMachineCasing MACHINE_CASING;
    public static BlockMultiblockCasing MUTLIBLOCK_CASING;
    public static BlockWireCoil WIRE_COIL;
    public static BlockWarningSign WARNING_SIGN;

    public static BlockGranite GRANITE;
    public static BlockMineral MINERAL;
    public static BlockConcrete CONCRETE;

    public static HashMap<DustMaterial, BlockCompressed> COMPRESSED;
    public static HashMap<DustMaterial, HashMap<StoneType, BlockOre>> OREMAP;
    public static Collection<BlockOre> ORES;

    private static final IBlockColor COMPRESSED_BLOCK_COLOR = (IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) ->
        state.getValue(((BlockCompressed) state.getBlock()).variantProperty).materialRGB;

    private static final IItemColor COMPRESSED_ITEM_COLOR = (stack, tintIndex) -> {
        BlockCompressed block = (BlockCompressed) ((ItemBlock) stack.getItem()).getBlock();
        IBlockState state = block.getStateFromMeta(stack.getItemDamage());
        return state.getValue(block.variantProperty).materialRGB;
    };

    private static final IBlockColor ORE_BLOCK_COLOR = (IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) ->
        tintIndex == 1 ? ((BlockOre) state.getBlock()).material.materialRGB : 0xFFFFFF;

    private static final IItemColor ORE_ITEM_COLOR = (stack, tintIndex) ->
        tintIndex == 1 ? ((BlockOre) ((ItemBlock) stack.getItem()).getBlock()).material.materialRGB : 0xFFFFFF;

    private static final IBlockColor MACHINE_BLOCK_COLOR = (IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) -> {
        IGregTechTileEntity tileEntity = (IGregTechTileEntity) worldIn.getTileEntity(pos);
        if (tileEntity != null) {
            IMetaTileEntity metaTileEntity = tileEntity.getMetaTileEntity();
            if (metaTileEntity instanceof PaintableMetaTileEntity) {
                return ((PaintableMetaTileEntity) metaTileEntity).getColor().getColorValue();
            }
        }
        return 0xFFFFFF;
    };

    private static final IItemColor MACHINE_ITEM_COLOR = (stack, tintIndex) -> {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Color")) {
            return EnumDyeColor.byMetadata(stack.getTagCompound().getInteger("Color")).getColorValue();
        }
        return 0xFFFFFF;
    };

    public static void init() {
        BOILER_CASING = new BlockBoilerCasing();
        BOILER_CASING.setRegistryName("boiler_casing");
        METAL_CASING = new BlockMetalCasing();
        METAL_CASING.setRegistryName("metal_casing");
        TURBINE_CASING = new BlockTurbineCasing();
        TURBINE_CASING.setRegistryName("turbine_casing");
        MACHINE_CASING = new BlockMachineCasing();
        MACHINE_CASING.setRegistryName("machine_casing");
        MUTLIBLOCK_CASING = new BlockMultiblockCasing();
        MUTLIBLOCK_CASING.setRegistryName("multiblock_casing");
        WIRE_COIL = new BlockWireCoil();
        WIRE_COIL.setRegistryName("wire_coil");
        WARNING_SIGN = new BlockWarningSign();
        WARNING_SIGN.setRegistryName("warning_sign");
        GRANITE = new BlockGranite();
        GRANITE.setRegistryName("granite");
        MINERAL = new BlockMineral();
        MINERAL.setRegistryName("mineral");
        CONCRETE = new BlockConcrete();
        CONCRETE.setRegistryName("concrete");

        COMPRESSED = new HashMap<>();
        OREMAP = new HashMap<>();
        ORES = new ArrayList<>();
        StoneType.init();
        ArrayList<DustMaterial> materialBuffer = new ArrayList<>();
        int generationIndex = 0;
        for(Material material : Material.MATERIAL_REGISTRY.getObjectsWithIds()) {
            if(material instanceof DustMaterial) {
                materialBuffer.add((DustMaterial) material);
                if(materialBuffer.size() == 16) {
                    createCompressedBlock(materialBuffer, generationIndex);
                    materialBuffer.clear();
                    generationIndex++;
                }
                if(material.hasFlag(DustMaterial.MatFlags.GENERATE_ORE)) {
                    createOreBlock((DustMaterial) material);
                }
            }
        }
        if(!materialBuffer.isEmpty()) {
            createCompressedBlock(materialBuffer, generationIndex);
        }

        MetaTileEntities.init();

        MACHINE = new BlockMachine();
        MACHINE.setRegistryName("machine");
    }

    private static void createCompressedBlock(Collection<DustMaterial> materials, int index) {
        materials.removeIf(OrePrefix.block::isIgnored);
        BlockCompressed block = new BlockCompressed(materials);
        block.setRegistryName("compressed_" + index);
        materials.forEach(material -> COMPRESSED.put(material, block));
    }

    private static void createOreBlock(DustMaterial material) {
        StoneType[] stoneTypeBuffer = new StoneType[8];
        Arrays.fill(stoneTypeBuffer, StoneTypes._NULL);
        int generationIndex = 0;
        for (StoneType stoneType : StoneType.STONE_TYPE_REGISTRY) {
            int id = StoneType.STONE_TYPE_REGISTRY.getIDForObject(stoneType), index = id / 8;
            if (index > generationIndex) {
                createOreBlock(material, stoneTypeBuffer, generationIndex);
                Arrays.fill(stoneTypeBuffer, StoneTypes._NULL);
            }
            stoneTypeBuffer[id % 8] = stoneType;
            generationIndex = index;
        }
        createOreBlock(material, stoneTypeBuffer, generationIndex);
    }

    private static void createOreBlock(DustMaterial material, StoneType[] stoneTypes, int index) {
        BlockOre block = new BlockOre(material, stoneTypes);
        block.setRegistryName("ore_" + material + "_" + index);
        for (StoneType stoneType : stoneTypes) {
            OREMAP.computeIfAbsent(material, m -> new HashMap<>()).put(stoneType, block);
        }
        ORES.add(block);
    }

    @SideOnly(Side.CLIENT)
    public static void registerItemModels() {
        registerItemModel(BOILER_CASING);
        registerItemModel(METAL_CASING);
        registerItemModel(TURBINE_CASING);
        registerItemModel(MACHINE_CASING);
        registerItemModel(MUTLIBLOCK_CASING);
        registerItemModel(WIRE_COIL);
        registerItemModel(WARNING_SIGN);
        registerItemModel(GRANITE);
        registerItemModel(MINERAL);
        registerItemModel(CONCRETE);
        MACHINE.registerItemModel();

        COMPRESSED.values().stream().distinct().forEach(MetaBlocks::registerItemModel);
        ORES.stream().distinct().forEach(MetaBlocks::registerItemModel);
    }

    @SideOnly(Side.CLIENT)
    private static void registerItemModel(Block block) {
        for (IBlockState state : block.getBlockState().getValidStates()) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),
                block.getMetaFromState(state),
                new ModelResourceLocation(block.getRegistryName(), statePropertiesToString(state.getProperties())));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void registerStateMappers() {
        MetaBlocks.MACHINE.registerStateMapper();
    }

    @SideOnly(Side.CLIENT)
    public static void registerColors() {
        MetaBlocks.COMPRESSED.values().stream().distinct().forEach(block -> {
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(COMPRESSED_BLOCK_COLOR, block);
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(COMPRESSED_ITEM_COLOR, block);
        });

        MetaBlocks.ORES.stream().distinct().forEach(block -> {
            Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(ORE_BLOCK_COLOR, block);
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ORE_ITEM_COLOR, block);
        });

        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(MACHINE_BLOCK_COLOR, MetaBlocks.MACHINE);
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(MACHINE_ITEM_COLOR, MetaBlocks.MACHINE);
    }

    private static String statePropertiesToString(Map<IProperty<?>, Comparable<?>> properties) {
        StringBuilder stringbuilder = new StringBuilder();

        for (Map.Entry<IProperty<?>, Comparable<?>> entry : properties.entrySet()) {
            if (stringbuilder.length() != 0) {
                stringbuilder.append(",");
            }

            IProperty<?> property = entry.getKey();
            stringbuilder.append(property.getName());
            stringbuilder.append("=");
            stringbuilder.append(getPropertyName(property, entry.getValue()));
        }

        if (stringbuilder.length() == 0) {
            stringbuilder.append("normal");
        }

        return stringbuilder.toString();
    }

    @SuppressWarnings("unchecked")
    private static <T extends Comparable<T>> String getPropertyName(IProperty<T> property, Comparable<?> value) {
        return property.getName((T) value);
    }
}
