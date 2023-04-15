package io._3650.peeklib.imc;

import javax.annotation.Nonnull;

import io._3650.peeklib.api.client.PeekRendererOverride;

public class RegisterPeekOverrideIMC {
	
	public static final String METHOD = "peeklib:override";
	
	public final PeekRendererOverride override;
	
	public RegisterPeekOverrideIMC(@Nonnull PeekRendererOverride override) {
		this.override = override;
	}
	
}