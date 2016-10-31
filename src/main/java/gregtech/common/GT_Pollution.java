	package gregtech.common;

import gregtech.GT_Mod;
import gregtech.api.objects.XSTR;
import gregtech.api.util.GT_Utility;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import java.util.ArrayList;
import java.util.List;

//import net.minecraft.entity.EntityLiving;

public class GT_Pollution {
	/**
	 * Pollution dispersion until effects start:
	 * Calculation: ((Limit * 0.01) + 2000) * (4 <- spreading rate)
	 * 
	 * SMOG(500k) 466.7 pollution/sec
	 * Poison(750k) 633,3 pollution/sec
	 * Dying Plants(1mio) 800 pollution/sec
	 * Sour Rain(1.5mio) 1133.3 pollution/sec
	 * 
	 * Pollution producers (pollution/sec)
	 * Bronze Boiler(20)
	 * Lava Boiler(20)
	 * High Pressure Boiler(20)
	 * Bronze Blast Furnace(50)
	 * Diesel Generator(14/28/75)
	 * Gas Turbine(7/14/37)
	 * Charcoal Pile(100)
	 * 
	 * Large Diesel Generator(300)
	 * Electric Blast Furnace(100)
	 * Implosion Compressor(2000)
	 * Large Boiler(240)
	 * Large Gas Turbine(160)
	 * Multi Smelter(100)
	 * Pyrolyse Oven(400)
	 * 
	 * Machine Explosion(100,000)
	 * 
	 * Muffler Hatch Pollution reduction:
	 * LV (0%), MV (30%), HV (52%), EV (66%), IV (76%), LuV (84%), ZPM (89%), UV (92%), MAX (95%)
	 */

	static List<ChunkPosition> tList = null;
	static int loops = 1;
	static XSTR tRan = new XSTR();

	public static void onWorldTick(World aWorld, int aTick){
		if(!GT_Mod.gregtechproxy.mPollution)return;
		if(aTick == 0 || (tList==null && GT_Proxy.chunkData!=null)){
			tList = new ArrayList<ChunkPosition>(GT_Proxy.chunkData.keySet());
			loops = (tList.size()/1200) + 1;
//			System.out.println("new Pollution loop"+aTick);
		}
		if(tList!=null && tList.size() > 0){
			int i = 0;
			for(; i < loops ; i++){
				if(tList.size()>0){
					ChunkPosition tPos = tList.get(0);
					tList.remove(0);
					if(tPos!=null && GT_Proxy.chunkData.containsKey(tPos)){
						int tPollution = GT_Proxy.chunkData.get(tPos)[1];
		//				System.out.println("process: "+tPos.chunkPosX+" "+tPos.chunkPosZ+" "+tPollution);
						//Reduce pollution in chunk
						tPollution = (int)(0.995f*tPollution);
						tPollution -= 2000;
						if(tPollution<=0){tPollution = 0;}else{
							//Spread Pollution
							if(tPollution>400000){
								List<ChunkPosition> tNeighbor = new ArrayList();
								tNeighbor.add(new ChunkPosition(tPos.chunkPosX+1, 1, tPos.chunkPosZ));
								tNeighbor.add(new ChunkPosition(tPos.chunkPosX-1, 1, tPos.chunkPosZ));
								tNeighbor.add(new ChunkPosition(tPos.chunkPosX, 1, tPos.chunkPosZ+1));
								tNeighbor.add(new ChunkPosition(tPos.chunkPosX, 1, tPos.chunkPosZ-1));
								for(ChunkPosition tNPos : tNeighbor){
									if(!GT_Proxy.chunkData.containsKey(tNPos)){
										GT_Utility.undergroundOil(aWorld,tNPos.chunkPosX,tNPos.chunkPosZ,false,0);
									}
									int tNPol = GT_Proxy.chunkData.get(tNPos)[1];
									if(tNPol*6 < tPollution*5){
										int tDiff = tPollution - tNPol;
										tDiff = tDiff/20;
										tNPol = GT_Utility.safeInt((long)tNPol+tDiff);//tNPol += tDiff;
										tPollution -= tDiff;
										GT_Proxy.chunkData.get(tNPos)[1] = tNPol;
									}
								}
							}
							int[] tArray = GT_Proxy.chunkData.get(tPos);
							tArray[1] = tPollution;
							GT_Proxy.chunkData.remove(tPos);
							GT_Proxy.chunkData.put(tPos, tArray);
							//Create Pollution effects
			//				Smog filter TODO
							if(tPollution > GT_Mod.gregtechproxy.mPollutionSmogLimit) {
								AxisAlignedBB chunk = AxisAlignedBB.getBoundingBox(tPos.chunkPosX << 4, 0, tPos.chunkPosZ << 4, (tPos.chunkPosX << 4) + 16, 256, (tPos.chunkPosZ << 4) + 16);
								List<EntityLivingBase> tEntitys = aWorld.getEntitiesWithinAABB(EntityLivingBase.class, chunk);
								for (EntityLivingBase tEnt : tEntitys) {
									if (!GT_Utility.isWearingFullGasHazmat(tEnt) && tRan.nextInt(tPollution / 25000) > 10) {
										tEnt.addPotionEffect(new PotionEffect(Potion.weakness.id, Math.min(tPollution / 1000, 1000), tPollution / 400000));
										tEnt.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, Math.min(tPollution / 1000, 1000), tPollution / 400000));
										tEnt.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, Math.min(tPollution / 1000, 1000), tPollution / 400000));
									}
								}
								//				Poison effects
								if (tPollution > GT_Mod.gregtechproxy.mPollutionPoisonLimit) {
									//AxisAlignedBB chunk = AxisAlignedBB.getBoundingBox(tPos.chunkPosX*16, 0, tPos.chunkPosZ*16, tPos.chunkPosX*16+16, 256, tPos.chunkPosZ*16+16);
									//List<EntityLiving> tEntitys = aWorld.getEntitiesWithinAABB(EntityLiving.class, chunk);
									for (EntityLivingBase tEnt : tEntitys) {
										if (!GT_Utility.isWearingFullGasHazmat(tEnt) && tRan.nextInt(tPollution / 25000) > 20) {
											tEnt.addPotionEffect(new PotionEffect(Potion.confusion.id, Math.min(tPollution / 2000, 1000), tPollution / 500000));
											tEnt.addPotionEffect(new PotionEffect(Potion.poison.id, Math.min(tPollution / 4000, 1000), 1));
											tEnt.addPotionEffect(new PotionEffect(Potion.blindness.id, Math.min(tPollution / 2000, 1000), 1));
										}
									}
									//				killing plants
									if (tPollution > GT_Mod.gregtechproxy.mPollutionVegetationLimit) {
										int f = 20;
										for (; f < (tPollution / 25000); f++) {
											int x = (tPos.chunkPosX << 4) + tRan.nextInt(16);
											int y = 60 + (-f + tRan.nextInt(f * 2 + 1));
											int z = (tPos.chunkPosZ << 4) + tRan.nextInt(16);
											damageBlock(aWorld, x, y, z, tPollution > GT_Mod.gregtechproxy.mPollutionSourRainLimit);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	public static void damageBlock(World aWorld, int x, int y, int z, boolean sourRain){
		//Teporary overriden
		//World world = DimensionManager.getWorld(aWorld.provider.dimensionId);
		World world = DimensionManager.getWorld(0);
		if (world.isRemote)	return;
		Block tBlock = world.getBlock(x, y, z);
		int tMeta = world.getBlockMetadata(x, y, z);
		if (tBlock == Blocks.air || tBlock == Blocks.stone || tBlock == Blocks.sand|| tBlock == Blocks.deadbush)return;
		
			if (tBlock == Blocks.leaves || tBlock == Blocks.leaves2 || tBlock.getMaterial() == Material.leaves)
				world.setBlockToAir(x, y, z);
			if (tBlock == Blocks.reeds) {
				tBlock.dropBlockAsItem(world, x, y, z, tMeta, 0);
				world.setBlockToAir(x, y, z);
			}
			if (tBlock == Blocks.tallgrass)
				world.setBlock(x, y, z, Blocks.deadbush);
			if (tBlock == Blocks.vine) {
				tBlock.dropBlockAsItem(world, x, y, z, tMeta, 0);
				world.setBlockToAir(x, y, z);
			}
			if (tBlock == Blocks.waterlily || tBlock == Blocks.wheat || tBlock == Blocks.cactus || 
				tBlock.getMaterial() == Material.cactus || tBlock == Blocks.melon_block || tBlock == Blocks.melon_stem) {
				tBlock.dropBlockAsItem(world, x, y, z, tMeta, 0);
				world.setBlockToAir(x, y, z);
			}
			if (tBlock == Blocks.red_flower || tBlock == Blocks.yellow_flower || tBlock == Blocks.carrots || 
				tBlock == Blocks.potatoes || tBlock == Blocks.pumpkin || tBlock == Blocks.pumpkin_stem) {
				tBlock.dropBlockAsItem(world, x, y, z, tMeta, 0);
				world.setBlockToAir(x, y, z);
			}
			if (tBlock == Blocks.sapling || tBlock.getMaterial() == Material.plants)
				world.setBlock(x, y, z, Blocks.deadbush);
			if (tBlock == Blocks.cocoa) {
				tBlock.dropBlockAsItem(world, x, y, z, tMeta, 0);
				world.setBlockToAir(x, y, z);
			}
			if (tBlock == Blocks.mossy_cobblestone)
				world.setBlock(x, y, z, Blocks.cobblestone);
			if (tBlock == Blocks.grass || tBlock.getMaterial() == Material.grass )
				world.setBlock(x, y, z, Blocks.dirt);	
			if(tBlock == Blocks.farmland || tBlock == Blocks.dirt){
				world.setBlock(x, y, z, Blocks.sand);					
			}
			
			if(sourRain && world.isRaining() && (tBlock == Blocks.stone || tBlock == Blocks.gravel || tBlock == Blocks.cobblestone) && 
				world.getBlock(x, y+1, z) == Blocks.air && world.canBlockSeeTheSky(x, y, z)){
				if(tBlock == Blocks.stone){world.setBlock(x, y, z, Blocks.cobblestone);	}
				else if(tBlock == Blocks.cobblestone){world.setBlock(x, y, z, Blocks.gravel);	}
				else if(tBlock == Blocks.gravel){world.setBlock(x, y, z, Blocks.sand);	}
			}
	}
	
	public static void addPollution(ChunkPosition aPos, int aPollution){
		if(!GT_Mod.gregtechproxy.mPollution)return;
		try{
			ChunkPosition tPos = new ChunkPosition(aPos.chunkPosX>>4, 1, aPos.chunkPosZ>>4);
	//		System.out.println("add pollution x: "+ tPos.chunkPosX +" z: " + tPos.chunkPosZ +" poll: "+aPollution);
			int[] tData = new int[2];
			if(GT_Proxy.chunkData.containsKey(tPos)){
				tData = GT_Proxy.chunkData.get(tPos);
				if(tData.length>1){
					tData[1]=GT_Utility.safeInt((long)tData[1]+aPollution);//tData[1] += aPollution;
				}
			}else{
				tData[1]=GT_Utility.safeInt((long)tData[1]+aPollution);//tData[1] += aPollution;
				GT_Proxy.chunkData.put(tPos, tData);
			}
		}catch(Exception e){}
	}

	public static int getPollutionAtCoords(int aX, int aZ){
		ChunkPosition tPos = new ChunkPosition(aX>>4, 1, aZ>>4);
		if(GT_Proxy.chunkData.containsKey(tPos)){
			int[] tPollution = GT_Proxy.chunkData.get(tPos);
			if(tPollution.length>1 && tPollution[1]>0){
				return tPollution[1];
			}else{
				return 0;
			}
		}
		return 0;
	}
}
