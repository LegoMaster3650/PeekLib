package io._3650.peeklib;

import com.mojang.logging.LogUtils;

import io._3650.peeklib.client.PeekLibClient;
import io._3650.peeklib.config.Config;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;

import org.slf4j.Logger;

@Mod(PeekLib.MOD_ID)
public class PeekLib {
	
	public static final String MOD_ID = "peeklib";
	
	public static final Logger LOGGER = LogUtils.getLogger();
	
	public PeekLib() {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> PeekLibClient::new);
		
		ModLoadingContext.get().registerConfig(Type.CLIENT, Config.CLIENT_SPEC, "peeklib-client.toml");
	}
	
}