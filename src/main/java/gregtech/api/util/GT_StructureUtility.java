package gregtech.api.util;

import com.gtnewhorizon.structurelib.StructureLibAPI;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.IStructureElementNoPlacement;
import gregtech.api.GregTech_API;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.interfaces.IHeatingCoil;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

import static com.gtnewhorizon.structurelib.StructureLibAPI.HINT_BLOCK_META_GENERIC_11;

public class GT_StructureUtility {
	public static final int HINT_COIL_DEFAULT_DOTS = HINT_BLOCK_META_GENERIC_11;

	private GT_StructureUtility() {
		throw new AssertionError("Not instantiable");
	}

	public static <T> IStructureElementNoPlacement<T> ofHatchAdder(IGT_HatchAdder<T> IGT_HatchAdder, int textureIndex, int dots) {
		return ofHatchAdder(IGT_HatchAdder, textureIndex, StructureLibAPI.getBlockHint(), dots - 1);
	}

	public static <T> IStructureElementNoPlacement<T> ofHatchAdder(IGT_HatchAdder<T> IGT_HatchAdder, int textureIndex, Block hintBlock, int hintMeta) {
		if (IGT_HatchAdder == null || hintBlock == null) {
			throw new IllegalArgumentException();
		}
		return new IStructureElementNoPlacement<T>() {
			@Override
			public boolean check(T t, World world, int x, int y, int z) {
				TileEntity tileEntity = world.getTileEntity(x, y, z);
				return tileEntity instanceof IGregTechTileEntity && IGT_HatchAdder.apply(t, (IGregTechTileEntity) tileEntity, (short) textureIndex);
			}

			@Override
			public boolean spawnHint(T t, World world, int x, int y, int z, ItemStack trigger) {
				StructureLibAPI.hintParticle(world, x, y, z, hintBlock, hintMeta);
				return true;
			}
		};
	}

	public static <T> IStructureElement<T> ofHatchAdderOptional(IGT_HatchAdder<T> IGT_HatchAdder, int textureIndex, int dots, Block placeCasing, int placeCasingMeta) {
		return ofHatchAdderOptional(IGT_HatchAdder, textureIndex, StructureLibAPI.getBlockHint(), dots - 1, placeCasing, placeCasingMeta);
	}

	public static <T> IStructureElement<T> ofHatchAdderOptional(IGT_HatchAdder<T> IGT_HatchAdder, int textureIndex, Block hintBlock, int hintMeta, Block placeCasing, int placeCasingMeta) {
		if (IGT_HatchAdder == null || hintBlock == null) {
			throw new IllegalArgumentException();
		}
		return new IStructureElement<T>() {
			@Override
			public boolean check(T t, World world, int x, int y, int z) {
				TileEntity tileEntity = world.getTileEntity(x, y, z);
				Block worldBlock = world.getBlock(x, y, z);
				return (tileEntity instanceof IGregTechTileEntity &&
						IGT_HatchAdder.apply(t, (IGregTechTileEntity) tileEntity, (short) textureIndex)) ||
						(worldBlock == placeCasing && worldBlock.getDamageValue(world, x, y, z) == placeCasingMeta);
			}

			@Override
			public boolean spawnHint(T t, World world, int x, int y, int z, ItemStack trigger) {
				StructureLibAPI.hintParticle(world, x, y, z, hintBlock, hintMeta);
				return true;
			}

			@Override
			public boolean placeBlock(T t, World world, int x, int y, int z, ItemStack trigger) {
				world.setBlock(x, y, z, placeCasing, placeCasingMeta, 2);
				return true;
			}
		};
	}

	/**
	 * Assume a default of LV coil. Assume all coils accepted. Assumes using {@link #HINT_COIL_DEFAULT_DOTS} as dots.
	 * @see #ofCoil(BiPredicate, Function, int, Block, int)
	 */
	public static <T> IStructureElement<T> ofCoil(BiConsumer<T, HeatingCoilLevel> heatingCoilSetter, Function<T, HeatingCoilLevel> heatingCoilGetter) {
		return ofCoil((t, l) -> {
			heatingCoilSetter.accept(t, l);
			return true;
		}, heatingCoilGetter, HINT_COIL_DEFAULT_DOTS, GregTech_API.sBlockCasings5, 0);
	}

	/**
	 * Assumes using {@link #HINT_COIL_DEFAULT_DOTS} as dots
	 * @see #ofCoil(BiPredicate, Function, int, Block, int)
	 */
	public static <T> IStructureElement<T> ofCoil(BiPredicate<T, HeatingCoilLevel> heatingCoilSetter, Function<T, HeatingCoilLevel> heatingCoilGetter, Block defaultCoil, int defaultMeta) {
		return ofCoil(heatingCoilSetter, heatingCoilGetter, HINT_COIL_DEFAULT_DOTS, defaultCoil, defaultMeta);
	}

	/**
	 * Heating coil structure element.
	 * @param heatingCoilSetter Notify the controller of this new coil.
	 *                            Got called exactly once per coil.
	 *                            Might be called less times if structure test fails.
	 *                            If the setter returns false then it assumes the coil is rejected.
	 * @param heatingCoilGetter Get the current heating level. Null means no coil recorded yet.
	 * @param dots The hinting dots
	 * @param defaultCoil The block to place when auto constructing
	 * @param defaultMeta The block meta to place when auto constructing
	 */
	public static <T> IStructureElement<T> ofCoil(BiPredicate<T, HeatingCoilLevel> heatingCoilSetter, Function<T, HeatingCoilLevel> heatingCoilGetter, int dots, Block defaultCoil, int defaultMeta) {
		if (heatingCoilSetter == null || heatingCoilGetter == null) {
			throw new IllegalArgumentException();
		}
		return new IStructureElement<T>() {
			@Override
			public boolean check(T t, World world, int x, int y, int z) {
				Block block = world.getBlock(x, y, z);
				if (!(block instanceof IHeatingCoil))
					return false;
				HeatingCoilLevel existingLevel = heatingCoilGetter.apply(t),
						newLevel = ((IHeatingCoil) block).getCoilHeat(world.getBlockMetadata(x, y, z));
				if (existingLevel == null) {
					return heatingCoilSetter.test(t, newLevel);
				} else {
					return newLevel == existingLevel;
				}
			}

			@Override
			public boolean spawnHint(T t, World world, int x, int y, int z, ItemStack trigger) {
				StructureLibAPI.hintParticle(world, x, y, z, StructureLibAPI.getBlockHint(), dots);
				return true;
			}

			@Override
			public boolean placeBlock(T t, World world, int x, int y, int z, ItemStack trigger) {
				return world.setBlock(x, y, z, defaultCoil, defaultMeta, 3);
			}
		};
	}
}
