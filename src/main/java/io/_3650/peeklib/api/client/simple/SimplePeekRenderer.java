package io._3650.peeklib.api.client.simple;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import io._3650.peeklib.api.client.IPeekRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;

/**
 * Simple base for rendering a peek with an offhand-style widget background
 * @author LegoMaster3650
 */
public abstract class SimplePeekRenderer implements IPeekRenderer {
	
	private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
	
	@Override
	public void renderBackground(ForgeGui gui, PoseStack poseStack, ItemStack stack, int x, int y, float partialTick, InteractionHand hand, HumanoidArm side) {
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderTexture(0, WIDGETS);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
		gui.blit(poseStack, x - 3, y - 3, side == HumanoidArm.LEFT ? 24 : 60, 23, 22, 22);
	}
	
}