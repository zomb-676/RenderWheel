package com.github.zomb.forge;

import net.minecraft.client.Minecraft;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.Bindings;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class CommonEvent {

	public static boolean enable = false;

	private CommonEvent() {
	}

	public static void register() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus forgeBus = Bindings.getForgeBus().get();
	}
}
