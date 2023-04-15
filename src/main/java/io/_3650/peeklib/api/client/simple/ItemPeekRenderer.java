package io._3650.peeklib.api.client.simple;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;

/**
 * Further simplified renderer that peeks at a singular item value, only rendering if a valid item is found
 * @author LegoMaster3650
 */
public abstract class ItemPeekRenderer extends SimplePeekRenderer {
	
	public final boolean decorate;
	public final boolean forceRender;
	
	/**
	 * Constructs an {@linkplain ItemPeekRenderer} with decorations enabled and forced rendering disabled
	 */
	public ItemPeekRenderer() {
		this(true, false);
	}
	/**
	 * Constructs an {@linkplain ItemPeekRenderer} with decorations specified and forced rendering disabled
	 * @param decorate Whether to enable decorations
	 */
	public ItemPeekRenderer(boolean decorate) {
		this(decorate, false);
	}
	/**
	 * Constructs an {@linkplain ItemPeekRenderer} with decorations and forced rendering specified
	 * @param decorate Whether to enable decorations
	 * @param forceRender Whether to force rendering even if no valid item is found
	 */
	public ItemPeekRenderer(boolean decorate, boolean forceRender) {
		this.decorate = decorate;
		this.forceRender = forceRender;
	}
	
	/**
	 * Gets the item to render.<br>
	 * Fired twice per render, once to check (unless force rendering is enabled) and once to render
	 * @param gui The {@linkplain ForgeGui} being rendered from
	 * @param stack The {@linkplain ItemStack} to check
	 * @param hand The {@linkplain InteractionHand} being rendered for
	 * @return The item to render, or {@linkplain ItemStack#EMPTY EMPTY} if not
	 */
	public abstract @Nonnull ItemStack getItem(ForgeGui gui, ItemStack stack, InteractionHand hand);
	
	@Override
	public boolean shouldRender(ForgeGui gui, ItemStack stack, InteractionHand hand) {
		return forceRender || !getItem(gui, stack, hand).isEmpty();
	}
	
	@Override
	public void renderForeground(ForgeGui gui, PoseStack poseStack, ItemStack stack, int x, int y, float partialTick, InteractionHand hand, HumanoidArm side) {
		ItemStack target = getItem(gui, stack, hand);
		ItemRenderer itemRenderer = gui.getMinecraft().getItemRenderer();
		itemRenderer.renderAndDecorateItem(target, x, y);
		if (decorate) itemRenderer.renderGuiItemDecorations(gui.getFont(), target, x, y);
	}
}