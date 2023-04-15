package io._3650.peeklib.api.client;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;

/**
 * An item peek renderer, the heart of the mod.
 * @author LegoMaster3650
 */
public interface IPeekRenderer {
	
	/**
	 * Checks whether the given stack should try rendering at all
	 * @param gui The {@linkplain ForgeGui} being rendered from
	 * @param stack The {@linkplain ItemStack} being peeked at
	 * @param hand The {@linkplain InteractionHand} being rendered for
	 * @return Whether the given stack should try rendering at all
	 */
	public boolean shouldRender(ForgeGui gui, ItemStack stack, InteractionHand hand);
	
	/**
	 * Renders the peek background
	 * @param gui The {@linkplain ForgeGui} being rendered from
	 * @param poseStack The current rendering {@linkplain PoseStack}
	 * @param stack The {@linkplain ItemStack} being peeked at
	 * @param x The X coordinate to render at, pre-calculated to a state usable in {@linkplain ItemRenderer#renderAndDecorateItem(net.minecraft.world.item.ItemStack, int, int)}
	 * @param y The Y coordinate to render at, pre-calculated to a state usable in {@linkplain ItemRenderer#renderAndDecorateItem(net.minecraft.world.item.ItemStack, int, int)}
	 * @param partialTick The current partial tick
	 * @param hand The {@linkplain InteractionHand} being rendered for
	 * @param side The onscreen {@linkplain HumanoidArm} side being rendered for
	 */
	public void renderBackground(ForgeGui gui, PoseStack poseStack, ItemStack stack, int x, int y, float partialTick, InteractionHand hand, HumanoidArm side);
	
	/**
	 * Renders the peek foreground
	 * @param gui The {@linkplain ForgeGui} being rendered from
	 * @param poseStack The current rendering {@linkplain PoseStack}
	 * @param stack The {@linkplain ItemStack} being peeked at
	 * @param x The X coordinate to render at, pre-calculated to a state usable in {@linkplain ItemRenderer#renderAndDecorateItem(net.minecraft.world.item.ItemStack, int, int)}
	 * @param y The Y coordinate to render at, pre-calculated to a state usable in {@linkplain ItemRenderer#renderAndDecorateItem(net.minecraft.world.item.ItemStack, int, int)}
	 * @param partialTick The current partial tick
	 * @param hand The {@linkplain InteractionHand} being rendered for
	 * @param side The onscreen {@linkplain HumanoidArm} side being rendered for
	 */
	public void renderForeground(ForgeGui gui, PoseStack poseStack, ItemStack stack, int x, int y, float partialTick, InteractionHand hand, HumanoidArm side);
	
}