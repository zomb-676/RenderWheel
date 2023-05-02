package com.github.zomb;

import lombok.experimental.UtilityClass;
import lombok.val;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@UtilityClass
public class ParticleStormCommon {
	public static final String MOD_ID = "particle_storm";
	public static final String MOD_NAME = "ParticleStorm";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);


	//mod id
	public static final String MOD_ID_MODERN_UI = "modernui";

	public static void a(){
		FluidStorage.ITEM.registerForItems((itemstack,context) -> {
			return null;
		});
		Class<CustomIngredient> aClass = CustomIngredient.class;
		val fabricItemClass = FabricItem.class;
		Class<BlockApiLookup> blockApiLookupClass = BlockApiLookup.class;

	}
}
