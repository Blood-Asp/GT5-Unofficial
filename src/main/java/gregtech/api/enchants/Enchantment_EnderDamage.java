package gregtech.api.enchants;

import gregtech.api.enums.Materials;
import gregtech.api.util.GT_LanguageManager;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class Enchantment_EnderDamage extends EnchantmentDamage {
    public static Enchantment_EnderDamage INSTANCE;

    public Enchantment_EnderDamage() {
        super(Rarity.UNCOMMON, 2);
        //GT_Config.addIDConfig(ConfigCategories.IDs.enchantments, "Disjunction", 15)
        GT_LanguageManager.addStringLocalization(getName(), "Disjunction");
        Materials.Silver.setEnchantmentForTools(this, 2);
        Materials.Mercury.setEnchantmentForTools(this, 3);
        Materials.Electrum.setEnchantmentForTools(this, 3);
        Materials.SterlingSilver.setEnchantmentForTools(this, 4);
        Materials.AstralSilver.setEnchantmentForTools(this, 5);
        INSTANCE = this;
    }

    @Override
    public int getMinEnchantability(int aLevel) {
        return 5 + (aLevel - 1) * 8;
    }

    @Override
    public int getMaxEnchantability(int aLevel) {
        return this.getMinEnchantability(aLevel) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public void onEntityDamaged(EntityLivingBase aHurtEntity, Entity aDamagingEntity, int aLevel) {
        if ((aHurtEntity instanceof EntityEnderman ||
                aHurtEntity instanceof EntityDragon ||
                (aHurtEntity.getClass().getName().contains(".") &&
                        aHurtEntity.getClass().getName()
                                .substring(aHurtEntity.getClass().getName().lastIndexOf(".")).contains("Ender")))) {

            // Weakness causes Endermen to not be able to teleport with GT being installed.
            aHurtEntity.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, aLevel * 200, Math.max(1, (5 * aLevel) / 7)));
            // They also get Poisoned. If you have this Enchant on an Arrow, you can kill the Ender Dragon easier.
            aHurtEntity.addPotionEffect(new PotionEffect(MobEffects.POISON, aLevel * 200, Math.max(1, (5 * aLevel) / 7)));
        }
    }

    @Override
    public String getName() {
        return "enchantment.damage.endermen";
    }
}