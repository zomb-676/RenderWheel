package com.github.zomb.forge;

import com.github.zomb.ParticleStormCommon;
import lombok.SneakyThrows;
import net.minecraftforge.fml.common.Mod;

@Mod(ParticleStormCommon.MOD_ID)
public class ParticleStormForge {



	public ParticleStormForge() {
		try {
			CommonEvent.register();
		} catch (Throwable e) {
			ParticleStormCommon.LOGGER.error("failed to construct mod entry class", e);

			throw e;
		}
	}

}


