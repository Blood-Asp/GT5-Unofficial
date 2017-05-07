package gregtech.common.tileentities.machines.multi;

import gregtech.api.GregTech_API;
import gregtech.api.enums.Textures;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Log;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class GT_MetaTileEntity_LargeChemicalReactor extends GT_MetaTileEntity_MultiBlockBase {

	private static final int CASING_INDEX = 16;

	public GT_MetaTileEntity_LargeChemicalReactor(int aID, String aName, String aNameRegional) {
		super(aID, aName, aNameRegional);
	}

	public GT_MetaTileEntity_LargeChemicalReactor(String aName) {
		super(aName);
	}

	@Override
	public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity) {
		return new GT_MetaTileEntity_LargeChemicalReactor(this.mName);
	}

	@Override
	public String[] getDescription() {
		return new String[] {
				"Controller block for the Large Chemical Reactor",
				"Has the same recipes as the Chemical Reactor",
				"Does not lose efficiency when overclocked",
				"Accepts fluids instead of fluid cells",
				"Size(WxHxD): 3x2x3",
				"Controller (Lower front centered)",
				"1x Any Heating Coil (Bottom centered)",
				"1x PTFE Pipe Casing (Top centered)",
				"1x Input Bus/Hatch (Any edge casing)",
				"1x Output Bus/Hatch (Any edge casing)",
				"1x Maintenance Hatch (Any edge casing)",
				"1x Energy Hatch (Any edge casing)",
				"Chemically Inert Casings for the rest (10 at least!)" };
	}

	@Override
	public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive,
			boolean aRedstone) {
		if (aSide == aFacing) {
			return new ITexture[] {
					Textures.BlockIcons.CASING_BLOCKS[CASING_INDEX],
					new GT_RenderedTexture(aActive ? Textures.BlockIcons.OVERLAY_FRONT_IMPLOSION_COMPRESSOR_ACTIVE
							: Textures.BlockIcons.OVERLAY_FRONT_IMPLOSION_COMPRESSOR) };
		}
		return new ITexture[] { Textures.BlockIcons.CASING_BLOCKS[CASING_INDEX] };
	}

	@Override
	public boolean isCorrectMachinePart(ItemStack aStack) {
		return true;
	}

	@Override
	public boolean checkRecipe(ItemStack aStack) {
		return false;
	}

	@Override
	public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack) {
		int xDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetX;
		int zDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetZ;
		int casingAmount = 0;
		// i=width, j=depth, k=height
		for (int x = -1 + xDir; x <= xDir + 1; x++) {
			for (int z = -1 + zDir; z <= zDir + 1; z++) {
				for (int y = 0; y <= 1; y++) {
					IGregTechTileEntity tileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(x, y, z);
					Block block = aBaseMetaTileEntity.getBlockOffset(x, y, z);
					if (x == xDir && z == zDir) {
						if (y == 0 && block != GregTech_API.sBlockCasings5) {
							GT_Log.err.println("EPIK no Heating Coil");
							GT_Log.err.println("EPIK found " + block);
							return false;
						} else if (y == 1
								&& (block != GregTech_API.sBlockCasings4 || aBaseMetaTileEntity.getMetaIDOffset(x, y, z) != 15)) {
							GT_Log.err.println("EPIK no PTFE Pipe Casing");
							return false;
						}
					} else if (x != 0 || y != 0 || z != 0) {
						if (!addInputToMachineList(tileEntity, CASING_INDEX) && !addOutputToMachineList(tileEntity, CASING_INDEX)
								&& !addMaintenanceToMachineList(tileEntity, CASING_INDEX)
								&& !addEnergyInputToMachineList(tileEntity, CASING_INDEX)) {
							if (block == GregTech_API.sBlockCasings2 && aBaseMetaTileEntity.getMetaIDOffset(x, y, z) == 0) {
								casingAmount++;
								GT_Log.err.println("EPIK correct casing at " + x + " " + y + " " + z);								
							} else {
								GT_Log.err.println("EPIK wrong casing at " + x + " " + y + " " + z);
								GT_Log.err.println("Found " + tileEntity);
								return false;
							}
						}
					}

				}
			}

		}
		return casingAmount >= 10;
	}

	@Override
	public int getMaxEfficiency(ItemStack aStack) {
		return 10000;
	}

	@Override
	public int getPollutionPerTick(ItemStack aStack) {
		return 0;
	}

	@Override
	public int getDamageToComponent(ItemStack aStack) {
		return 0;
	}

	@Override
	public boolean explodesOnComponentBreak(ItemStack aStack) {
		return false;
	}

}
