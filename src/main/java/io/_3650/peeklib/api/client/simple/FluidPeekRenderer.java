package io._3650.peeklib.api.client.simple;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import io._3650.peeklib.api.client.renderer.FluidRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

/**
 * Further simplified renderer that peeks at a singular fluid value
 * @author LegoMaster3650
 */
public abstract class FluidPeekRenderer extends SimplePeekRenderer {
	
	public final boolean decorate;
	public final int capacity;
	public final int unit;
	public final boolean forceRender;
	public final boolean allowDecimal;
	
	/**
	 * Constructs an {@linkplain ItemPeekRenderer} with:<br>
	 * Decorations enabled<br>
	 * Capacity of 1000<br>
	 * Unit of 1000<br>
	 * Force Rendering disabled
	 */
	public FluidPeekRenderer() {
		this(true, FluidType.BUCKET_VOLUME, FluidType.BUCKET_VOLUME, false);
	}
	/**
	 * Constructs an {@linkplain ItemPeekRenderer} with:<br>
	 * Decorations specified<br>
	 * Capacity of 1000<br>
	 * Unit of 1000<br>
	 * Force Rendering disabled
	 * @param decorate Whether to enable decorations
	 */
	public FluidPeekRenderer(boolean decorate) {
		this(decorate, FluidType.BUCKET_VOLUME, FluidType.BUCKET_VOLUME, false);
	}
	/**
	 * Constructs an {@linkplain ItemPeekRenderer} with:<br>
	 * Decorations specified<br>
	 * Capacity specified<br>
	 * Unit of 1000<br>
	 * Force Rendering specified
	 * @param decorate Whether to enable decorations
	 * @param capacity The maximum "capacity" of the fluid render
	 * @param forceRender Whether to force rendering even if no valid item is found
	 */
	public FluidPeekRenderer(boolean decorate, int capacity, boolean forceRender) {
		this(decorate, capacity, FluidType.BUCKET_VOLUME, forceRender);
	}
	/**
	 * Constructs an {@linkplain ItemPeekRenderer} with:<br>
	 * Decorations enabled<br>
	 * Capacity specified<br>
	 * Unit specified<br>
	 * Force Rendering disabled
	 * @param capacity The maximum "capacity" of the fluid render
	 * @param unit The unit to divide the amount by for display
	 */
	public FluidPeekRenderer(int capacity, int unit) {
		this(true, capacity, unit, false);
	}
	/**
	 * Constructs an {@linkplain ItemPeekRenderer} with:<br>
	 * Decorations enabled<br>
	 * Capacity specified<br>
	 * Unit specified<br>
	 * Force Rendering specified
	 * @param capacity The maximum "capacity" of the fluid render
	 * @param unit The unit to divide the amount by for display
	 * @param forceRender Whether to force rendering even if no valid item is found
	 */
	public FluidPeekRenderer(int capacity, int unit, boolean forceRender) {
		this(true, capacity, unit, forceRender);
	}
	private FluidPeekRenderer(boolean decorate, int capacity, int unit, boolean forceRender) {
		this.decorate = decorate;
		this.capacity = capacity;
		this.unit = unit;
		this.forceRender = forceRender;
		this.allowDecimal = unit >= FluidType.BUCKET_VOLUME;
	}
	
	/**
	 * Gets the fluid to render.<br>
	 * Fired twice per render, once to check (unless force rendering is enabled) and once to render
	 * @param gui The {@linkplain ForgeGui} being rendered from
	 * @param stack The {@linkplain ItemStack} to check
	 * @param hand The {@linkplain InteractionHand} being rendered for
	 * @return The fluid to render, or {@linkplain FluidStack#EMPTY EMPTY} if not
	 */
	public abstract @Nonnull FluidStack getFluid(ForgeGui gui, ItemStack stack, InteractionHand hand);
	
	@Override
	public boolean shouldRender(ForgeGui gui, ItemStack stack, InteractionHand hand) {
		return forceRender || !getFluid(gui, stack, hand).isEmpty();
	}
	
	@Override
	public void renderForeground(ForgeGui gui, PoseStack poseStack, ItemStack stack, int x, int y, float partialTick, InteractionHand hand, HumanoidArm side) {
		FluidStack target = getFluid(gui, stack, hand);
		FluidRenderer.renderGuiFluid(poseStack, target, capacity, x, y, 16, 16, false, gui.getBlitOffset());
		if (decorate) FluidRenderer.renderGuiFluidAmount(gui.getFont(), target, unit, x, y, gui.getBlitOffset(), allowDecimal);
	}
	
}