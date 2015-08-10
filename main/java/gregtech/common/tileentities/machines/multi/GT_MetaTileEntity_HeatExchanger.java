package gregtech.common.tileentities.machines.multi;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import gregtech.GT_Mod;
import gregtech.api.GregTech_API;
import gregtech.api.enums.Materials;
import gregtech.api.enums.Textures;
import gregtech.api.gui.GT_GUIContainer_MultiMachine;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.metatileentity.IMetaTileEntity;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Input;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_InputBus;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_Output;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_Hatch_OutputBus;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_ModHandler;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;

public class GT_MetaTileEntity_HeatExchanger extends GT_MetaTileEntity_MultiBlockBase{
	  public GT_MetaTileEntity_HeatExchanger(int aID, String aName, String aNameRegional)
	  {
	    super(aID, aName, aNameRegional);
	  }
	  
	  public GT_MetaTileEntity_HeatExchanger(String aName)
	  {
	    super(aName);
	  }
	  
	  public String[] getDescription()
	  {
	    return new String[] { "Controller Block for the Heat Exchanger", "Size: 3x3x4", "Controller (front middle at bottom)", "3x3x4 of Stable Titanium Casing (hollow, Min 24!)", "2 Titanium Pipe Casing Blocks inside the Hollow Casing", "1x Distillated Water Input (one of the Casings)","min 1 Steam Output (one of the Casings)", "1x Maintenance Hatch (one of the Casings)", "1x Hot Fluid Input (botton Center)", "1x Cold Fluid Output (top Center)" };
	  }
	  
		@Override
		public void loadNBTData(NBTTagCompound aNBT) {
	    	superheated = aNBT.getBoolean("superheated");
	    	super.loadNBTData(aNBT);}
		
		@Override
		public void saveNBTData(NBTTagCompound aNBT) {
	    	aNBT.setBoolean("superheated", superheated);
	    	super.saveNBTData(aNBT);}
	
	public GT_MetaTileEntity_Hatch_Input mInputHotFluidHatch;
	public GT_MetaTileEntity_Hatch_Output mOutputColdFluidHatch;
	public boolean superheated=false;
	  
	  public ITexture[] getTexture(IGregTechTileEntity aBaseMetaTileEntity, byte aSide, byte aFacing, byte aColorIndex, boolean aActive, boolean aRedstone)
	  {
	    if (aSide == aFacing) {
	      return new ITexture[] { Textures.BlockIcons.CASING_BLOCKS[50], new GT_RenderedTexture(aActive ? Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER_ACTIVE : Textures.BlockIcons.OVERLAY_FRONT_LARGE_BOILER) };
	    }
	    return new ITexture[] { Textures.BlockIcons.CASING_BLOCKS[50] };
	  }
	  
	  public Object getClientGUI(int aID, InventoryPlayer aPlayerInventory, IGregTechTileEntity aBaseMetaTileEntity)
	  {
	    return new GT_GUIContainer_MultiMachine(aPlayerInventory, aBaseMetaTileEntity, getLocalName(), "LargeBoiler.png");
	  }
	  
	  public boolean isCorrectMachinePart(ItemStack aStack)
	  {
	    return true;
	  }
	  
	  public boolean isFacingValid(byte aFacing)
	  {
	    return aFacing > 1;
	  }
	  
	  public boolean checkRecipe(ItemStack aStack)
	  {
		if(GT_ModHandler.isLava(mInputHotFluidHatch.getFluid())){
			int fluidAmount = mInputHotFluidHatch.getFluidAmount();
			if(fluidAmount >= 500){superheated=true;}else{superheated=false;}
			if(fluidAmount>1000){fluidAmount=1000;}
			mInputHotFluidHatch.drain(fluidAmount, true);
			mOutputColdFluidHatch.fill(FluidRegistry.getFluidStack("ic2pahoehoelava", fluidAmount), true);
			
			
			           this.mMaxProgresstime = 20;
			           this.mEUt = fluidAmount*4;
			           this.mEfficiencyIncrease = 80;
		return true;	
		}
		
		if(mInputHotFluidHatch.getFluid().isFluidEqual(FluidRegistry.getFluidStack("ic2hotcoolant", 1))){
			int fluidAmount = mInputHotFluidHatch.getFluidAmount();
			if(fluidAmount >= 4000){superheated=true;}else{superheated=false;}
			if(fluidAmount>8000){fluidAmount=8000;}
			mInputHotFluidHatch.drain(fluidAmount, true);
			mOutputColdFluidHatch.fill(FluidRegistry.getFluidStack("ic2coolant", fluidAmount), true);
			
			
			           this.mMaxProgresstime = 20;
			           this.mEUt = fluidAmount/2;
			           this.mEfficiencyIncrease = 20;
		return true;	
		}
		return false;}
	  
	   private float water;
	   private int useWater(float input){
		   water = water + input;
		   int usage = (int)water;
		   water = water - (int)usage;
		   return  usage;
	   }
	  
	  public boolean onRunningTick(ItemStack aStack)
	  { 
	      int tGeneratedEU = (int)(this.mEUt * 2L * this.mEfficiency / 10000L);  // APPROXIMATELY how much steam to generate.
	      if (tGeneratedEU > 0) 
	      {
	    	if (superheated)
	    		tGeneratedEU /= 2;
	    	
	    	int distilledConsumed = (int) Math.round(tGeneratedEU / 160f);  // how much distilled water to consume
	    	tGeneratedEU = distilledConsumed * 160;  // EXACTLY how much steam to generate, producing a perfect 1:160 ratio with distilled water consumption
	    	FluidStack distilledStack = GT_ModHandler.getDistilledWater(useWater(distilledConsumed));
  	        if (depleteInput(distilledStack)) // Consume the distilled water
	        {
		  if(superheated)
		  {
			  addOutput(FluidRegistry.getFluidStack("ic2superheatedsteam", tGeneratedEU));  // Generate superheated steam
		  }
		  else
		  {
			  addOutput(GT_ModHandler.getSteam(tGeneratedEU)); // Generate regular steam
          	  }
	        } 
	        else 
	        {
	          explodeMultiblock(); // Generate crater
	        }
	      }
	      return true;
	    }
	    return true;
	  }
	  private static boolean controller;
	  public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack aStack)
	  {
	    int xDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetX;int zDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetZ;
	    
	    int tCasingAmount = 0;int tFireboxAmount = 0;controller=false;
	    for (int i = -1; i < 2; i++) {
	      for (int j = -1; j < 2; j++) {
	        if ((i != 0) || (j != 0))
	        {
	          for (int k = 0; k <= 3; k++) {
	          if (!addOutputToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir + i, k, zDir + j), 50)&&!addInputToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir + i, k, zDir + j), 50)&&!addMaintenanceToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir + i, k, zDir + j), 50)&&!ignoreController(aBaseMetaTileEntity.getBlockOffset(xDir + i, k, zDir + j)))
	            {
	              if (aBaseMetaTileEntity.getBlockOffset(xDir + i, k, zDir + j) != getCasingBlock()) {
	                return false;
	              }
	              if (aBaseMetaTileEntity.getMetaIDOffset(xDir + i, k, zDir + j) != getCasingMeta()) {
	                return false;
	              }
	              tCasingAmount++;
	            }
							}
	          }else{
								if(!addHotFluidInputToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir + i, 0, zDir + j), 50)){
									return false;
								}
								if(!addColdFluidOutputToMachineList(aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir + i, 3, zDir + j), 50)){
									return false;
								}
		            if (aBaseMetaTileEntity.getBlockOffset(xDir + i, 1, zDir + j) != getPipeBlock()) {
			              return false;
			            }
			            if (aBaseMetaTileEntity.getMetaIDOffset(xDir + i, 1, zDir + j) != getPipeMeta()) {
			              return false;
			            }
		
			            if (aBaseMetaTileEntity.getBlockOffset(xDir + i, 2, zDir + j) != getPipeBlock()) {
				              return false;
				            }
				            if (aBaseMetaTileEntity.getMetaIDOffset(xDir + i, 2, zDir + j) != getPipeMeta()) {
				              return false;
				            }
	        }
	      }
	    }
	    return (tCasingAmount >= 24);
	  }
	
	public boolean ignoreController(Block tTileEntity){
		if(!controller&&tTileEntity == GregTech_API.sBlockMachines){return true;}
		return false;
	}
	
	public boolean addColdFluidOutputToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
		if (aTileEntity == null) return false;
		IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
		if (aMetaTileEntity == null) return false;
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Output) {
			((GT_MetaTileEntity_Hatch)aMetaTileEntity).mMachineBlock = (byte)aBaseCasingIndex;
			mOutputColdFluidHatch = (GT_MetaTileEntity_Hatch_Output)aMetaTileEntity;
			return true;
		}
		return false;
	}
	
	public boolean addHotFluidInputToMachineList(IGregTechTileEntity aTileEntity, int aBaseCasingIndex) {
		if (aTileEntity == null) return false;
		IMetaTileEntity aMetaTileEntity = aTileEntity.getMetaTileEntity();
		if (aMetaTileEntity == null) return false;
		if (aMetaTileEntity instanceof GT_MetaTileEntity_Hatch_Input) {
			((GT_MetaTileEntity_Hatch)aMetaTileEntity).mMachineBlock = (byte)aBaseCasingIndex;
			((GT_MetaTileEntity_Hatch_Input)aMetaTileEntity).mRecipeMap = getRecipeMap();
			mInputHotFluidHatch = (GT_MetaTileEntity_Hatch_Input)aMetaTileEntity;
			return true;
		}
		return false;
	}
	
	  public Block getCasingBlock()
	  {
	    return GregTech_API.sBlockCasings4;
	  }
	  
	  public byte getCasingMeta()
	  {
	    return 2;
	  }
	  
	  public byte getCasingTextureIndex()
	  {
	    return 50;
	  }
	  
	  public Block getPipeBlock()
	  {
	    return GregTech_API.sBlockCasings2;
	  }
	  
	  public byte getPipeMeta()
	  {
	    return 14;
	  }
	  
	  public int getMaxEfficiency(ItemStack aStack)
	  {
	    return 10000;
	  }
	  
	  public int getPollutionPerTick(ItemStack aStack)
	  {
	    return 0;
	  }
	  
	  public int getDamageToComponent(ItemStack aStack)
	  {
	    return 0;
	  }
	  
	  public int getAmountOfOutputs()
	  {
	    return 1;
	  }
	  
	  public boolean explodesOnComponentBreak(ItemStack aStack)
	  {
	    return false;
	  }
	
	  public IMetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity)
	  {
	    return new GT_MetaTileEntity_HeatExchanger(this.mName);
	  }
	}
