package io._3650.peeklib.api.client;

import net.minecraft.world.item.ItemStack;

/**
 * A itemstack-sensitive override to put renderers
 * @author LegoMaster3650
 */
@FunctionalInterface
public interface PeekRendererOverride {
	
	/**
	 * Attempts to get a renderer from the override
	 * @param stack The {@linkplain ItemStack} to look for an override for
	 * @return A {@linkplain IPeekRenderer} if the override matches or <code>null</code> if not
	 */
	public IPeekRenderer get(ItemStack stack);
	
}