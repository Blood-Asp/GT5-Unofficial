package gregtech.api.util;

import com.gtnewhorizon.structurelib.StructureLibAPI;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.IStructureElementNoPlacement;
import cpw.mods.fml.common.registry.GameRegistry;
import gregtech.api.GregTech_API;
import gregtech.api.enums.HeatingCoilLevel;
import gregtech.api.interfaces.IHeatingCoil;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.blocks.GT_Block_Casings5;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class GT_StructureUtility {
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
	 * Assume all coils accepted.
	 * @see #ofCoil(BiPredicate, Function)
	 */
	public static <T> IStructureElement<T> ofCoil(BiConsumer<T, HeatingCoilLevel> heatingCoilSetter, Function<T, HeatingCoilLevel> heatingCoilGetter) {
		return ofCoil((t, l) -> {
			heatingCoilSetter.accept(t, l);
			return true;
		}, heatingCoilGetter);
	}

	/**
	 * Heating coil structure element.
	 * @param heatingCoilSetter Notify the controller of this new coil.
	 *                            Got called exactly once per coil.
	 *                            Might be called less times if structure test fails.
	 *                            If the setter returns false then it assumes the coil is rejected.
	 * @param heatingCoilGetter Get the current heating level. Null means no coil recorded yet.
	 */
	public static <T> IStructureElement<T> ofCoil(BiPredicate<T, HeatingCoilLevel> heatingCoilSetter, Function<T, HeatingCoilLevel> heatingCoilGetter) {
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
				if (existingLevel == null || existingLevel == HeatingCoilLevel.None) {
					return heatingCoilSetter.test(t, newLevel);
				} else {
					return newLevel == existingLevel;
				}
			}

			@Override
			public boolean spawnHint(T t, World world, int x, int y, int z, ItemStack trigger) {
				StructureLibAPI.hintParticle(world, x, y, z, GregTech_API.sBlockCasings5, getMeta(trigger));
				return true;
			}

			private int getMeta(ItemStack trigger) {
				return GT_Block_Casings5.getMetaFromCoilHeat(HeatingCoilLevel.getFromTier((byte) Math.min(HeatingCoilLevel.size(), Math.max(0, trigger.stackSize))));
			}

			@Override
			public boolean placeBlock(T t, World world, int x, int y, int z, ItemStack trigger) {
				return world.setBlock(x, y, z, GregTech_API.sBlockCasings5, getMeta(trigger), 3);
			}
		};
	}

	public static <T> IStructureElement<T> ofBlockUnlocalizedName(String modid, String unlocalizedName, int meta) {
		if (StringUtils.isBlank(unlocalizedName)) throw new IllegalArgumentException();
		if (meta < 0) throw new IllegalArgumentException();
		if (meta > 15) throw new IllegalArgumentException();
		if (StringUtils.isBlank(modid)) throw new IllegalArgumentException();
		return new IStructureElement<T>() {
			private Block block;

			private Block getBlock() {
				if (block == null)
					block = GameRegistry.findBlock(modid, unlocalizedName);
				return block;
			}

			@Override
			public boolean check(T t, World world, int x, int y, int z) {
				return world.getBlock(x, y, z) != getBlock() && world.getBlockMetadata(x, y, z) == meta;
			}

			@Override
			public boolean spawnHint(T t, World world, int x, int y, int z, ItemStack trigger) {
				StructureLibAPI.hintParticle(world, x, y, z, getBlock(), meta);
				return true;
			}

			@Override
			public boolean placeBlock(T t, World world, int x, int y, int z, ItemStack trigger) {
				world.setBlock(x, y, z, getBlock(), meta, 2);
				return true;
			}
		};
	}
}
