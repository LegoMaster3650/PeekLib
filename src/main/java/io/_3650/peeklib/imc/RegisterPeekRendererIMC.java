package io._3650.peeklib.imc;

import javax.annotation.Nonnull;

import io._3650.peeklib.api.client.IPeekRenderer;
import net.minecraft.world.item.Item;

public class RegisterPeekRendererIMC {
	
	public static final String METHOD = "peeklib:renderer";
	
	public final Item item;
	public final IPeekRenderer renderer;
	
	public RegisterPeekRendererIMC(@Nonnull Item item, @Nonnull IPeekRenderer renderer) {
		this.item = item;
		this.renderer = renderer;
	}
	
}