package gregtech.api.objects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import gregtech.api.enums.GT_Values;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class GT_UO_Fluid {
	public String Registry;
	public int MaxAmount;
	public int MinAmount;
	public int Chance;

	public GT_UO_Fluid(ConfigCategory aConfigCategory) { 
		Registry = aConfigCategory.get("Registry").getString();;
		MaxAmount = aConfigCategory.get("MaxAmount").getInt();
		MinAmount = aConfigCategory.get("MinAmount").getInt();
		Chance = aConfigCategory.get("Chance").getInt();
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