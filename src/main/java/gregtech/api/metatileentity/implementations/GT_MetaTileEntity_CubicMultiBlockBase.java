package gregtech.api.metatileentity.implementations;

import com.gtnewhorizon.structurelib.structure.IStructureDefinition;
import com.gtnewhorizon.structurelib.structure.IStructureElement;
import com.gtnewhorizon.structurelib.structure.StructureDefinition;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import net.minecraft.item.ItemStack;

import static com.gtnewhorizon.structurelib.structure.StructureUtility.lazy;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.ofChain;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.onElementPass;
import static com.gtnewhorizon.structurelib.structure.StructureUtility.transpose;
import static gregtech.api.util.GT_StructureUtility.ofHatchAdder;

/**
 * A simple 3x3x3 hollow cubic multiblock, that can be arbitrarily rotated, made of a single type of machine casing and accepts hatches everywhere.
 * Controller will be placed in front center of the structure.
 * <p>
 * Note: You cannot use different casing for the same Class. Make a new subclass for it. You also should not change the casing
 * dynamically, i.e. it should be a dumb method returning some sort of constant.
 * <p>
 * Implementation tips:
 * 1. To restrict hatches, override {@link #addDynamoToMachineList(IGregTechTileEntity, int)} and its cousins instead of overriding the whole
 * {@link #getStructureDefinition()} or change {@link #checkHatches(IGregTechTileEntity, ItemStack)}. The former is a total overkill, while the later cannot
 * stop the structure check early.
 * 2. To limit rotation, override {@link #getInitialAlignmentLimits()}
 *
 * @param <T>
 */
public abstract class GT_MetaTileEntity_CubicMultiBlockBase<T extends GT_MetaTileEntity_CubicMultiBlockBase<T>> extends GT_MetaTileEntity_EnhancedMultiBlockBase<T> {
	protected static final String STRUCTURE_PIECE_MAIN = "main";
	protected static final ClassValue<IStructureDefinition<GT_MetaTileEntity_CubicMultiBlockBase<?>>> STRUCTURE_DEFINITION = new ClassValue<IStructureDefinition<GT_MetaTileEntity_CubicMultiBlockBase<?>>>() {
		@Override
		protected IStructureDefinition<GT_MetaTileEntity_CubicMultiBlockBase<?>> computeValue(Class<?> type) {
			return StructureDefinition.<GT_MetaTileEntity_CubicMultiBlockBase<?>>builder()
					.addShape(STRUCTURE_PIECE_MAIN, transpose(new String[][]{
							{"hhh", "hhh", "hhh"},
							{"h~h", "h-h", "hhh"},
							{"hhh", "hhh", "hhh"},
					}))
					.addElement('h', ofChain(
							lazy(t -> ofHatchAdder(GT_MetaTileEntity_CubicMultiBlockBase::addToMachineList, t.getHatchTextureIndex(), 1)),
							onElementPass(
									GT_MetaTileEntity_CubicMultiBlockBase::onCorrectCasingAdded,
									lazy(GT_MetaTileEntity_CubicMultiBlockBase::getCasingElement)
							)
					))
					.build();
		}
	};
	private int mCasingAmount = 0;

	protected GT_MetaTileEntity_CubicMultiBlockBase(int aID, String aName, String aNameRegional) {
		super(aID, aName, aNameRegional);
	}

	protected GT_MetaTileEntity_CubicMultiBlockBase(String aName) {
		super(aName);
	}

	/**
	 * Create a simple 3x3x3 hollow cubic structure made of a single type of machine casing and accepts hatches everywhere.
	 * <p>
	 * The created definition contains a single piece named {@link #STRUCTURE_PIECE_MAIN}.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public IStructureDefinition<T> getStructureDefinition() {
		return (IStructureDefinition<T>) STRUCTURE_DEFINITION.get(getClass());
	}

	@Override
	public void construct(ItemStack aStack, boolean aHintsOnly) {
		buildPiece(STRUCTURE_PIECE_MAIN, aStack, aHintsOnly, 1, 1, 0);
	}

	@Override
	public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
		mCasingAmount = 0;
		return checkPiece(STRUCTURE_PIECE_MAIN, 1, 1, 0) &&
				mCasingAmount >= getRequiredCasingCount() &&
				checkHatches(aBaseMetaTileEntity, aStack);
	}

	/**
	 * Called by {@link #checkMachine(IGregTechTileEntity, ItemStack)} to check if all required hatches are present.
	 * <p>
	 * Default implementation requires EXACTLY ONE maintenance hatch to be present, and ignore all other conditions.
	 *
	 * @param aBaseMetaTileEntity the tile entity of self
	 * @param aStack              The item stack inside the controller
	 * @return true if the test passes, false otherwise
	 */
	protected boolean checkHatches(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
		return mMaintenanceHatches.size() == 1;
	}

	protected abstract IStructureElement<GT_MetaTileEntity_CubicMultiBlockBase<?>> getCasingElement();

	/**
	 * The hatch's texture index.
	 */
	protected abstract int getHatchTextureIndex();

	protected abstract int getRequiredCasingCount();

	protected void onCorrectCasingAdded() {
		mCasingAmount++;
	}
}
