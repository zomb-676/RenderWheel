package com.github.zomb.platform;

/**
 * the interface used to do platform specific operation at common<br>
 * constructed by {@link Services}
 */
public interface IPlatformHelper {
	PlatformEnum getPlatform();

	boolean isDevelopmentEnviroment();

	boolean isModLoaded(String modId);

	boolean isLoadingStateValid();

	boolean promoteGlVersion();
}
