package io._3650.peeklib.api.client;

import java.util.List;

import io._3650.peeklib.PeekLib;
import io._3650.peeklib.client.PeekLibClient;
import io._3650.peeklib.imc.RegisterPeekOverrideIMC;
import io._3650.peeklib.imc.RegisterPeekRendererIMC;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

/**
 * The main PeekLib API<br>
 * Just use {@link #registerRenderer(Item, IPeekRenderer) registerRenderer} and {@link #registerOverride(PeekRendererOverride) registerOverride}
 * @author LegoMaster3650
 */
public class PeekLibApi {
	
	/**
	 * Registers a peek renderer for the mod<br>
	 * It is recommended but not enforced to not register a renderer for any vanilla items<br>
	 * <b>MUST BE USED DURING {@linkplain InterModEnqueueEvent}</b>
	 * @param item The {@linkplain Item} to register this renderer for
	 * @param renderer The {@linkplain IPeekRenderer} to register
	 */
	public static void registerRenderer(Item item, IPeekRenderer renderer) {
		final var imc = new RegisterPeekRendererIMC(item, renderer);
		InterModComms.sendTo(PeekLib.MOD_ID, RegisterPeekRendererIMC.METHOD, () -> imc);
	}
	
	/**
	 * Registers a peek override for the mod<br>
	 * <b>MUST BE USED DURING {@linkplain InterModEnqueueEvent}</b>
	 * @param override The {@linkplain PeekRendererOverride} to register
	 */
	public static void registerOverride(PeekRendererOverride override) {
		final var imc = new RegisterPeekOverrideIMC(override);
		InterModComms.sendTo(PeekLib.MOD_ID, RegisterPeekOverrideIMC.METHOD, () -> imc);
	}
	
	/**
	 * Gets the renderer for the given stack
	 * @param stack The {@linkplain ItemStack} to look for a renderer for
	 * @return The {@linkplain IPeekRenderer} for the given item, if present, or <code>null</code> if not
	 */
	public static IPeekRenderer getPeekRenderer(ItemStack stack) {
		for (var override : PeekLibClient.getOverrides()) {
			IPeekRenderer cantidate = override.get(stack);
			if (cantidate != null) return cantidate;
		}
		return PeekLibClient.getRenderer(stack.getItem());
	}
	
	/**
	 * Gets the renderer for the given item, WITHOUT APPLYING OVERRIDES
	 * @param item The {@linkplain Item} to look for a renderer for
	 * @return The {@linkplain IPeekRenderer} for the given item, if present, or <code>null</code> if not
	 * @see {@linkplain #getPeekRenderer(ItemStack)} for the method you probably want
	 */
	public static IPeekRenderer getDirectRenderer(Item item) {
		return PeekLibClient.getRenderer(item);
	}
	
	/**
	 * Gets an unmodifiable list of all renderer overrides
	 * @return An unmodifiable list of all {@linkplain PeekRendererOverride PeekRendererOverrides}
	 */
	public static List<PeekRendererOverride> getOverrides() {
		return PeekLibClient.getOverrides();
	}
	
}