package net.anoltongi;

import net.anoltongi.data.OreXPData;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/*
Hello! I hope you're having an awesome day <3
This mod allows data pack and mod pack creators to make any block give the xp they want, in a very simple way! Or if you're not interested in that,
you can always use the mod without any other changes, it'll just add xp to current existing ores.
 */
public class UniversalOreXP implements ModInitializer {
	public static final String MOD_ID = "universal_ore_xp";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Registering CustomOreXp listener");
		OreXPData.register();
		CustomOreXP.registerXP();
	}
}