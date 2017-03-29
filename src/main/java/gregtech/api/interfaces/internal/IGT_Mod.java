package gregtech.api.interfaces.internal;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Interface used by the Mods Main Class to reference to internals.
 * <p/>
 * Don't even think about including this File in your Mod.
 */
public interface IGT_Mod {
    /**
     * This means that Server specific Basefiles are definitely existing! Not if the World is actually server side or not!
     */
	boolean isServerSide();

    /**
     * This means that Client specific Basefiles are definitely existing! Not if the World is actually client side or not!
     */
	boolean isClientSide();

    /**
     * This means that Bukkit specific Basefiles are definitely existing! Not if the World is actually bukkit server or not!
     */
	boolean isBukkitSide();

    /**
     * works only ClientSide otherwise returns null
     */
	EntityPlayer getThePlayer();

    //---------- Internal Usage Only ----------
    /**
     * Plays the Sonictron Sound for the ItemStack on the Client Side
     */
	void doSonictronSound(ItemStack aStack, World aWorld, double aX, double aY, double aZ);

}