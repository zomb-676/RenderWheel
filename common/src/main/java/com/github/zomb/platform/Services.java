package com.github.zomb.platform;

import com.github.zomb.ParticleStormCommon;
import net.minecraft.client.ClientBrandRetriever;

public class Services {
	private static final String PATH = "com.github.zomb";
	public static final IPlatformHelper PLANTFORM = setupPlatformHelper();

	public static IPlatformHelper setupPlatformHelper() {
		String loaderName = ClientBrandRetriever.getClientModName().toLowerCase().trim();
		var classLocation = switch (loaderName) {
			case "forge" -> PATH + ".forge.platform.ForgePlatformHelper";
			case "fabric" -> PATH + ".fabric.platform.FabricPlatformHelper";
			case "vanilla" -> throw new RuntimeException("run on vanilla?");
			case "quilt" -> throw new RuntimeException("quilt is not supported yet");
			default -> throw new RuntimeException("unknown loader " + loaderName);
		};
		ParticleStormCommon.LOGGER.debug("detect loader " + loaderName);
		IPlatformHelper loadedService;
		try {
			loadedService = (IPlatformHelper) Class.forName(classLocation).getConstructor().newInstance();
		} catch (Exception e) {
			ParticleStormCommon.LOGGER.error("failed to init PlatformHelper");
			throw new RuntimeException(e);
		}
		ParticleStormCommon.LOGGER.debug("Loaded {} for service", loadedService);
		return loadedService;
	}
}
