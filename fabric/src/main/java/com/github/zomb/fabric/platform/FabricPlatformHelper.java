package com.github.zomb.fabric.platform;

import com.github.zomb.platform.IPlatformHelper;
import com.github.zomb.platform.PlatformEnum;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformHelper implements IPlatformHelper {
	@Override
	public PlatformEnum getPlatform() {
		return PlatformEnum.FABRIC;
	}

	@Override
	public boolean isDevelopmentEnviroment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	@Override
	public boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public boolean isLoadingStateValid() {
		return true;
	}

	@Override
	public boolean promoteGlVersion() {
		return true;
	}
}
