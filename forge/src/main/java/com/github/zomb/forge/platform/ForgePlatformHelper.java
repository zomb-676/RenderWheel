package com.github.zomb.forge.platform;

import com.github.zomb.ParticleStormCommon;
import com.github.zomb.platform.IPlatformHelper;
import com.github.zomb.platform.PlatformEnum;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.loading.FMLLoader;

public class ForgePlatformHelper implements IPlatformHelper {
	@Override
	public PlatformEnum getPlatform() {
		return PlatformEnum.FORGE;
	}

	@Override
	public boolean isDevelopmentEnviroment() {
		return !FMLLoader.isProduction();
	}

	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public boolean isLoadingStateValid() {
		return ModLoader.isLoadingStateValid();
	}

	@Override
	public boolean promoteGlVersion() {
		if (isModLoaded(ParticleStormCommon.MOD_ID_MODERN_UI)){
			return false;
		}
		return true;
	}

}
