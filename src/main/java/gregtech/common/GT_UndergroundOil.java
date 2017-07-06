package gregtech.common;

import gregtech.GT_Mod;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.objects.GT_UO_Fluid;
import gregtech.api.objects.XSTR;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.util.HashMap;

import static gregtech.common.GT_Proxy.*;

/**
 * Created by Tec on 29.04.2017.
 */
public class GT_UndergroundOil {
    public static final short DIVIDER=5000;

    public static FluidStack undergroundOil(IGregTechTileEntity te, float drainSpeedCoefficient){
        return undergroundOil(te.getWorld().getChunkFromBlockCoords(te.getXCoord(),te.getZCoord()),drainSpeedCoefficient);
    }

    //Returns whole content for information purposes -> when drainSpeedCoeff < 0
    //Else returns extracted fluidStack if amount > 0, or null otherwise
    public static FluidStack undergroundOil(Chunk chunk, float drainSpeedCoefficient) {
        if (GT_Mod.gregtechproxy.mUndergroundOil.CheckBlackList(chunk.worldObj.provider.dimensionId)) return null;
        World aWorld = chunk.worldObj;

        //Read hash map
        HashMap<ChunkCoordIntPair, int[]> chunkData = dimensionWiseChunkData.get(aWorld.provider.dimensionId);
        if(chunkData==null){
            chunkData=new HashMap<>(1024);
            dimensionWiseChunkData.put(aWorld.provider.dimensionId,chunkData);
        }

        int[] tInts = chunkData.get(chunk.getChunkCoordIntPair());

        if(tInts==null) tInts=getDefaultChunkDataOnCreation();//init if null
        else if(tInts[GTOIL]==0){//FAST stop
            //can return 0 amount stack for info :D
            return drainSpeedCoefficient>=0 ? null : new FluidStack(FluidRegistry.getFluid(tInts[GTOILFLUID]),0);
        }

        //GEN IT TO GET OBJECT...
        XSTR tRandom = new XSTR( (aWorld.getSeed() + aWorld.provider.dimensionId * 2 + ((int)Math.floor((double)chunk.getChunkCoordIntPair().chunkXPos/(double)96)) + (7 * ((int)Math.floor((double)chunk.getChunkCoordIntPair().chunkZPos/96)))));
        GT_UO_Fluid uoFluid = GT_Mod.gregtechproxy.mUndergroundOil.GetDimension(aWorld.provider.dimensionId).getRandomFluid(tRandom);

        //Fluid stack holder
        FluidStack fluidInChunk;

        //Set fluidstack from uoFluid
        if (uoFluid == null || uoFluid.getFluid()==null){
            tInts[GTOILFLUID]=Integer.MAX_VALUE;//null fluid pointer... kindof
            tInts[GTOIL]=0;
            chunkData.put(chunk.getChunkCoordIntPair(),tInts);//update hash map
            return null;
        } else {
            if(tInts[GTOILFLUID]== uoFluid.getFluid().getID()){//if stored fluid matches uoFluid
                fluidInChunk = new FluidStack(uoFluid.getFluid(),tInts[GTOIL]);
            }else{
                fluidInChunk  = new FluidStack(uoFluid.getFluid(), uoFluid.getRandomAmount(tRandom));
                tRandom=new XSTR();
                fluidInChunk.amount=(int)((float)fluidInChunk.amount*(0.75f+(tRandom.nextFloat()/2f)));//Randomly change amounts by +/- 25%
            }
            tInts[GTOIL]=fluidInChunk.amount;
            tInts[GTOILFLUID]=fluidInChunk.getFluidID();
        }

        //do stuff on it if needed
        if(drainSpeedCoefficient>=0){
            if(fluidInChunk.amount<DIVIDER){
                fluidInChunk=null;
                tInts[GTOIL]=0;//so in next access it will stop way above
            }else{
                fluidInChunk.amount = (int)(fluidInChunk.amount*(double)drainSpeedCoefficient/DIVIDER);//give appropriate amount
                tInts[GTOIL]-=uoFluid.DecreasePerOperationAmount;//diminish amount
            }
        }else{//just get info
            if(fluidInChunk.amount<DIVIDER){
                fluidInChunk.amount=0;//return informative stack
                tInts[GTOIL]=0;//so in next access it will stop way above
            }else{
                fluidInChunk.amount=fluidInChunk.amount/DIVIDER;//give moderate extraction speed
            }
        }

        chunkData.put(chunk.getChunkCoordIntPair(),tInts);//update hash map
        return fluidInChunk;
    }
}
