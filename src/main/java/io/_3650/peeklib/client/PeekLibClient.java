package io._3650.peeklib.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import io._3650.peeklib.PeekLib;
import io._3650.peeklib.api.client.IPeekRenderer;
import io._3650.peeklib.api.client.PeekLibApi;
import io._3650.peeklib.api.client.PeekRendererOverride;
import io._3650.peeklib.api.client.simple.FluidPeekRenderer;
import io._3650.peeklib.api.client.simple.ItemPeekRenderer;
import io._3650.peeklib.config.Config;
import io._3650.peeklib.imc.RegisterPeekOverrideIMC;
import io._3650.peeklib.imc.RegisterPeekRendererIMC;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.InterModComms.IMCMessage;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

public class PeekLibClient {
	
	private static final Logger LOGGER = LogUtils.getLogger();
	
	private static final Map<Item, IPeekRenderer> renderers = new HashMap<>();
	private static final List<PeekRendererOverride> overrides = new ArrayList<>();
	
	public static IPeekRenderer getRenderer(Item item) {
		return renderers.get(item);
	}
	
	public static List<PeekRendererOverride> getOverrides() {
		return Collections.unmodifiableList(overrides);
	}
	
	public PeekLibClient() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::registerOverlay);
		bus.addListener(this::enqueueIMC);
		bus.addListener(this::processIMC);
	}
	
	private void registerOverlay(RegisterGuiOverlaysEvent event) {
		event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), PeekLib.MOD_ID, PeekOverlay.INSTANCE);
	}
	
	private void enqueueIMC(InterModEnqueueEvent event) {
		if (Config.CLIENT.enableDebugTests.get()) {
			PeekLibApi.registerRenderer(Items.BRICK, new ItemPeekRenderer() {
				@Override
				public ItemStack getItem(ForgeGui gui, ItemStack stack, InteractionHand hand) {
					return new ItemStack(Items.DIAMOND, 65);
				}
			});
			PeekLibApi.registerRenderer(Items.EMERALD, new ItemPeekRenderer(true, true) {
				@SuppressWarnings("resource")
				@Override
				public ItemStack getItem(ForgeGui gui, ItemStack stack, InteractionHand hand) {
					return new ItemStack(Items.GRASS_BLOCK, gui.getMinecraft().player.blockPosition().getY());
				}
			});
			PeekLibApi.registerRenderer(Items.WATER_BUCKET, new FluidPeekRenderer(1000, 1000, true) {
				@SuppressWarnings("resource")
				@Override
				public FluidStack getFluid(ForgeGui gui, ItemStack stack, InteractionHand hand) {
					return new FluidStack(Fluids.WATER, 100 * gui.getMinecraft().player.blockPosition().getY());
				}
			});
			PeekLibApi.registerRenderer(Items.LAVA_BUCKET, new FluidPeekRenderer(false) {
				@Override
				public FluidStack getFluid(ForgeGui gui, ItemStack stack, InteractionHand hand) {
					return new FluidStack(Fluids.LAVA, 900);
				}
			});
			PeekLibApi.registerOverride((stack) -> {
				if (stack.getItem() == Items.BRICK && stack.hasCustomHoverName()) return new ItemPeekRenderer() {
					@Override
					public ItemStack getItem(ForgeGui gui, ItemStack stack, InteractionHand hand) {
						return new ItemStack(Items.BEDROCK);
					}
				};
				else return null;
			});
		}
	}
	
	private void processIMC(InterModProcessEvent event) {
		InterModComms.getMessages(PeekLib.MOD_ID, s -> s.equals(RegisterPeekRendererIMC.METHOD))
				.filter(PeekLibClient::filterRendererIMC)
				.map(PeekLibClient::castRendererIMC)
				.forEach(PeekLibClient::checkedRegisterRenderer);
		InterModComms.getMessages(PeekLib.MOD_ID, s -> s.equals(RegisterPeekOverrideIMC.METHOD))
				.map(msg -> msg.messageSupplier().get())
				.filter(RegisterPeekOverrideIMC.class::isInstance)
				.map(RegisterPeekOverrideIMC.class::cast)
				.forEach(msg -> overrides.add(msg.override));
	}
	
	public static boolean filterRendererIMC(IMCMessage msg) {
		return msg.messageSupplier().get() instanceof RegisterPeekRendererIMC;
	}
	
	public static Pair<String, RegisterPeekRendererIMC> castRendererIMC(IMCMessage msg) {
		return Pair.of(msg.senderModId(), (RegisterPeekRendererIMC) msg.messageSupplier().get());
	}
	
	private static void checkedRegisterRenderer(Pair<String, RegisterPeekRendererIMC> ctx) {
		RegisterPeekRendererIMC msg = ctx.getRight();
		if (renderers.containsKey(msg.item)) LOGGER.error("Mod {} is attempting to override an existing peek renderer for {}", ctx.getLeft(), ForgeRegistries.ITEMS.getKey(msg.item));
		else renderers.put(msg.item, msg.renderer);
	}
	
}