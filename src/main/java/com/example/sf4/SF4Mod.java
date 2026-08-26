package com.example.sf4;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SF4Mod implements ModInitializer {
    public static final String MOD_ID = "sf4";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("SF4 Mod is initializing!");
    }
}