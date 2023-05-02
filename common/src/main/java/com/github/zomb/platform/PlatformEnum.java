package com.github.zomb.platform;

/**
 * the enum class indicates all possible platforms
 */
public enum PlatformEnum {
	FABRIC("fabric"), FORGE("forge");

	public final String platformString;

	PlatformEnum(String platformName) {
		this.platformString = platformName;
	}
}
