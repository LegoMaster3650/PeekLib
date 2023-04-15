package io._3650.peeklib.client;

import com.mojang.blaze3d.vertex.PoseStack;

import io._3650.peeklib.api.client.IPeekRenderer;
import io._3650.peeklib.api.client.PeekLibApi;
import io._3650.peeklib.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class PeekOverlay implements IGuiOverlay {
	
	public static final PeekOverlay INSTANCE = new PeekOverlay();
	
	@Override
	public void render(ForgeGui gui, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
		if (!Config.CLIENT.enablePeek.get()) return;
		Minecraft minecraft = gui.getMinecraft();
		if (minecraft.options.hideGui) return;
		LocalPlayer player = minecraft.player;
		
		ItemStack mainhand = player.getMainHandItem();
		ItemStack offhand = player.getOffhandItem();
		
		boolean renderMainhand = Config.CLIENT.peekMainhand.get();
		boolean renderOffhand = Config.CLIENT.peekOffhand.get();
		
		IPeekRenderer mainhandRenderer = renderMainhand ? PeekLibApi.getPeekRenderer(mainhand) : null;
		IPeekRenderer offhandRenderer = renderOffhand ? PeekLibApi.getPeekRenderer(offhand) : null;
		
		renderMainhand = renderMainhand && mainhandRenderer != null && mainhandRenderer.shouldRender(gui, mainhand, InteractionHand.MAIN_HAND);
		renderOffhand = renderOffhand && offhandRenderer != null && offhandRenderer.shouldRender(gui, offhand, InteractionHand.OFF_HAND);
		
		if (!renderMainhand && !renderOffhand) return; //exit early if no rendering will occur
		
		HumanoidArm mainhandArm = player.getMainArm();
		HumanoidArm offhandArm = mainhandArm.getOpposite();
		
		int width = screenWidth / 2;
		int y = (screenHeight - 42) - Config.CLIENT.offsetY.get();
		int xMH = getOffhandPos(width, mainhandArm);;
		int xOH = getOffhandPos(width, offhandArm);
		
		//background
		if (renderMainhand) {
			mainhandRenderer.renderBackground(gui, poseStack, mainhand, xMH, y, partialTick, InteractionHand.MAIN_HAND, mainhandArm);
		}
		if (renderOffhand) {
			offhandRenderer.renderBackground(gui, poseStack, offhand, xOH, y, partialTick, InteractionHand.OFF_HAND, offhandArm);
		}
		//foreground
		if (renderMainhand) {
			mainhandRenderer.renderForeground(gui, poseStack, mainhand, xMH, y, partialTick, InteractionHand.MAIN_HAND, mainhandArm);
		}
		if (renderOffhand) {
			offhandRenderer.renderForeground(gui, poseStack, offhand, xOH, y, partialTick, InteractionHand.OFF_HAND, offhandArm);
		}
		
	}
	
	private static int getOffhandPos(int width, HumanoidArm arm) {
		int configOffset = Config.CLIENT.offsetX.get();
		return arm == HumanoidArm.LEFT ? width - 91 - 26 - configOffset : width + 91 + 10 + configOffset;
	}
	
}