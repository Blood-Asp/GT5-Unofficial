package gregtech.api.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import gregtech.api.enums.GT_Values;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class GT_UO_Fluid {
	public String Registry = "null";
	public int MaxAmount = 0;
	public int MinAmount = 0;
	public int Chance = 0;
	public int DecreasePerOperationAmount = 5;

	public GT_UO_Fluid(ConfigCategory aConfigCategory) {//TODO CONFIGURE
		if (aConfigCategory.containsKey("Registry"))
		{
			aConfigCategory.get("Registry").comment = "Fluid registry";
			Registry = aConfigCategory.get("Registry").getString();
		}
		if (aConfigCategory.containsKey("MaxAmount"))
		{
			aConfigCategory.get("MaxAmount").comment = "Max amount generation (per operation Amount)";
			MaxAmount = aConfigCategory.get("MaxAmount").getInt(0);
		}
		if (aConfigCategory.containsKey("MinAmount"))
		{
			aConfigCategory.get("MinAmount").comment = "Max amount generation (per operation Amount)";
			MinAmount = aConfigCategory.get("MinAmount").getInt(0);
		}
		if (aConfigCategory.containsKey("Chance"))
		{
			aConfigCategory.get("Chance").comment = "Chance generating";
			Chance = aConfigCategory.get("Chance").getInt(0);
		}
		if (aConfigCategory.containsKey("DecreasePerOperationAmount"))
		{
			aConfigCategory.get("DecreasePerOperationAmount").comment = "Decrease per operation Amount (X/5000L per operation)";
			DecreasePerOperationAmount = aConfigCategory.get("DecreasePerOperationAmount").getInt(5);
		}
		//System.out.println("GT UO "+aConfigCategory.getName()+" Fluid:"+Registry+" Max:"+MaxAmount+" Min:"+MinAmount+" Chance:"+Chance);
	}
	
	public Fluid getFluid(){
		try {
			return FluidRegistry.getFluid(this.Registry);
		} catch (Exception e) {
			return null;
		}
	}
	
	public int getRandomAmount(Random aRandom){
		int r1 = (int)Math.round(Math.pow((MaxAmount-MinAmount)*500000.d, 0.2));
		int r2 = (int)Math.floor(Math.pow(MinAmount*500000.d, 0.2));
        double amount = aRandom.nextInt(r1)+r2+aRandom.nextDouble();
        return (int) (Math.pow(amount, 5) / 100);
	}

}