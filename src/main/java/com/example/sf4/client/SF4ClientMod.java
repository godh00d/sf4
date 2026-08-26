package com.example.sf4.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SF4ClientMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("sf4-client");

    @Override
    public void onInitializeClient() {
        LOGGER.info("SF4 Client Mod is initializing!");
    }
}