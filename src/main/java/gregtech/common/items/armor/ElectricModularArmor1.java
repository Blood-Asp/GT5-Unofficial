package gregtech.common.items.armor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ElectricModularArmor1 extends ModularArmor_Item{
	
	public boolean mChargeProvider=false;

	public ElectricModularArmor1(int aArmorIndex, int aType, String name,int gui) {
		super(aArmorIndex, aType, name,gui);
	}
	
	public boolean canProvideEnergy(ItemStack aStack) {
		return mChargeProvider;
	}
	
	public Item getChargedItem(ItemStack aStack) {
		return this;
	}
	
	public Item getEmptyItem(ItemStack aStack) {
		return this;
	}
	
	public int getMaxCharge(ItemStack aStack) {
		return data.charge;
	}
	
	public int getTier(ItemStack aStack) {
		return 2;
	}
	
	public int getTransferLimit(ItemStack aStack) {
		return openGuiNr==1?128:512;
	}
}
