package io._3650.peeklib.api.client.renderer;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.annotation.Nonnull;
import org.joml.Matrix4f;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;

/**
 * 
 * @author LegoMaster3650
 */
public class FluidRenderer {
	
	private static final DecimalFormat LARGE_FORMAT = Util.make(new DecimalFormat("0.0"), (format) -> {
		format.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
	});
	
	/**
	 * Quickly convert pixels to MB (assuming 1MB = 16 pixels)
	 * @param pixels The number of pixels
	 * @return The equivalent in MB
	 */
	public static int pixelsToMB(int pixels) {
		return FluidType.BUCKET_VOLUME * pixels / 16;
	}
	
	/**
	 * Render a fluidstack in a gui (or anything)
	 * @param poseStack The {@linkplain PoseStack} to use
	 * @param stack The {@linkplain FluidStack} to render
	 * @param capacity The maximum "capacity" of the fluid render
	 * @param x The X value you'd use in {@linkplain ItemRenderer#renderAndDecorateItem(net.minecraft.world.item.ItemStack, int, int)}
	 * @param y The Y value you'd use in {@linkplain ItemRenderer#renderAndDecorateItem(net.minecraft.world.item.ItemStack, int, int)}
	 * @param width The width of the render
	 * @param height The height of the render
	 * @param flowing Whether to use the still or flowing fluid sprite (<code>false</code> = still, <code>true</code> = flowing)
	 * @param blitOffset The blit offset (z value) to render at
	 */
	public static void renderGuiFluid(PoseStack poseStack, @Nonnull FluidStack stack, int capacity, int x, int y, int width, int height, boolean flowing, float blitOffset) {
		if (stack.isEmpty()) return;
		
		RenderSystem.setShaderTexture(0, InventoryMenu.BLOCK_ATLAS);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		
		Minecraft mc = Minecraft.getInstance();
		Fluid fluid = stack.getFluid();
		IClientFluidTypeExtensions fluidType = IClientFluidTypeExtensions.of(fluid);
		TextureAtlasSprite tex = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(flowing ? fluidType.getFlowingTexture(stack) : fluidType.getStillTexture(stack));
		
		int tint = fluidType.getTintColor(stack);
		RenderSystem.setShaderColor((tint >> 16 & 0xFF) / 255.0F, (tint >> 8 & 0xFF) / 255.0F, (tint & 0xFF) / 255.0F, ((tint >> 24) & 0xFF) / 255.0F);
		
		int amount = stack.getAmount();
		int scaledAmt = (amount * height) / capacity;
		if (amount > 0 && scaledAmt < 1) scaledAmt = 1;
		if (scaledAmt > height) scaledAmt = height;
		
		Matrix4f matrix = poseStack.last().pose();
		
		final int xTiles = width / 16;
		final int xRemainder = width - (xTiles * 16);
		final int yTiles = scaledAmt / 16;
		final int yRemainder = scaledAmt - (yTiles * 16);
		
		for (int xTile = 0; xTile <= xTiles; xTile++) {
			for (int yTile = 0; yTile <= yTiles; yTile++) {
				final int drawWidth = (xTile == xTiles) ? xRemainder : 16;
				final int drawHeight = (yTile == yTiles) ? yRemainder : 16;
				final int drawX = x + (xTile * 16);
				final int drawY = y - (yTile * 16);
				if (drawWidth > 0 && drawHeight > 0) {
					final int maskRight = 16 - drawWidth;
					final int maskBottom = 16 - drawHeight;
					
					float uMin = tex.getU0();
					float uMax = tex.getU1();
					float vMin = tex.getV0();
					float vMax = tex.getV1();
					uMax = uMax - (maskRight / 16.0F * (uMax - uMin));
					vMin = vMin + (maskBottom / 16.0F * (vMax - vMin));
					
					Tesselator tesselator = Tesselator.getInstance();
					BufferBuilder bufferBuilder = tesselator.getBuilder();
					bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
					bufferBuilder.vertex(matrix, drawX, drawY + 16, blitOffset).uv(uMin, vMax).endVertex();
					bufferBuilder.vertex(matrix, drawX + 16 - maskRight, drawY + 16, blitOffset).uv(uMax, vMax).endVertex();
					bufferBuilder.vertex(matrix, drawX + 16 - maskRight, drawY + maskBottom, blitOffset).uv(uMax, vMin).endVertex();
					bufferBuilder.vertex(matrix, drawX, drawY + maskBottom, blitOffset).uv(uMin, vMin).endVertex();
					tesselator.end();
				}
			}
		}
		
	}
	
	/**
	 * Render a number for an amount of fluid
	 * @param font The default {@linkplain Font} to use
	 * @param stack The {@linkplain FluidStack} to display the amount of
	 * @param unit The unit to divide the amount by for display
	 * @param x The X value you'd use in {@linkplain ItemRenderer#renderGuiItemDecorations(Font, net.minecraft.world.item.ItemStack, int, int)}
	 * @param y The Y value you'd use in {@linkplain ItemRenderer#renderGuiItemDecorations(Font, net.minecraft.world.item.ItemStack, int, int)}
	 * @param blitOffset The blit offset (z value) to render at
	 * @param allowDecimal Whether to allow one decimal place to be displayed for display values under 100
	 */
	public static void renderGuiFluidAmount(Font font, FluidStack stack, int unit, int x, int y, float blitOffset, boolean allowDecimal) {
		int amount = stack.getAmount();
		if (amount > 0) {
			PoseStack poseStack = new PoseStack();
			String s;
			if (allowDecimal) {
				double div = Math.floorDiv(amount * 10, unit) / 10.0;
				if (div >= 100.0) s = String.valueOf(Mth.floor(div));
				else s = LARGE_FORMAT.format(div);
			} else s = String.valueOf(Math.floorDiv(amount, unit));
			poseStack.translate(0.0F, 0.0F, blitOffset + 200.0F);
			MultiBufferSource.BufferSource source = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
			font.drawInBatch(s, (float)(x + 19 - 2 - font.width(s)), (float)(y + 6 + 3), 16777215, true, poseStack.last().pose(), source, false, 0, 15728880);
			source.endBatch();
		}
	}
	
}