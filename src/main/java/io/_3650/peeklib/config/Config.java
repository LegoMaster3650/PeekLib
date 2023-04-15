package io._3650.peeklib.config;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class Config {
	
	public static class Client {
		
		public final BooleanValue enablePeek;
		public final BooleanValue peekMainhand;
		public final BooleanValue peekOffhand;
		public final IntValue offsetY;
		public final IntValue offsetX;
		
		public final BooleanValue enableDebugTests;
		
		Client(ForgeConfigSpec.Builder builder) {
			builder.push("general");
			
			enablePeek = builder.comment("Enable PeekLib previews","[Default: true]").define("enablePeek", true);
			peekMainhand = builder.comment("Shows PeekLib previews for items in the main hand","[Default: true]").define("peekMainhand", true);
			peekOffhand = builder.comment("Shows PeekLib previews for items in the offhand","[Default: true]").define("peekOffhand", true);
			offsetX = builder.comment("Horizontal offset of the preview (+FURTHER,-CLOSER)", "Useful for aligning with mods that move the offhand and/or get in the way", "[Default: 0]").defineInRange("offsetX", 0, -100, 200);
			offsetY = builder.comment("Vertical offset of the preview (+UP,-DOWN)", "Useful for aligning with mods that raise the hotbar and/or get in the way","[Default: 0]").defineInRange("offsetY", 0, -100, 200);
			
			builder.pop();
			
			builder.push("debug");
			
			enableDebugTests = builder.comment("Enable debug test overlays if you want to get a feel for the mod or something.", ">> REQUIRES RESTART <<", "[Default: false]").define("enableDebugTests", false);
			
			builder.pop();
		}
		
	}
	
	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final Client CLIENT;
	
	static {
		final Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = clientSpecPair.getRight();
		CLIENT = clientSpecPair.getLeft();
	}
	
}