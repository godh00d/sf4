package com.godh00d.sf4angel.client;

import com.godh00d.sf4angel.SF4Angel;
import com.godh00d.sf4angel.constellation.AchievementConstellationCatalog;
import com.godh00d.sf4angel.network.MessageConstellationProgress;
import net.minecraft.client.Minecraft;

public final class ConstellationClientState {

    private static byte[] states = new byte[0];
    private static Integer previousCloudSetting;

    private ConstellationClientState() {
    }

    public static void accept(MessageConstellationProgress message) {
        if (message.getStates().length == 0) {
            clear();
            return;
        }
        if (message.getCatalogCount() != AchievementConstellationCatalog.COUNT
            || !AchievementConstellationCatalog.HASH.equals(message.getCatalogHash())
            || message.getStates().length != AchievementConstellationCatalog.COUNT) {
            SF4Angel.logger.error("Rejected constellation snapshot for catalog {}/{} (local {}/{})",
                message.getCatalogCount(), message.getCatalogHash(), AchievementConstellationCatalog.COUNT,
                AchievementConstellationCatalog.HASH);
            clear();
            return;
        }
        states = message.getStates().clone();
        hideOverworldAtmosphere();
    }

    public static byte[] states() {
        return states;
    }

    public static void clear() {
        states = new byte[0];
        restoreOverworldAtmosphere();
    }

    public static void maintainAtmosphere() {
        if (states.length > 0) hideOverworldAtmosphere();
    }

    private static void hideOverworldAtmosphere() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.gameSettings == null) return;
        if (previousCloudSetting == null) previousCloudSetting = minecraft.gameSettings.clouds;
        minecraft.gameSettings.clouds = 0;
        if (minecraft.world == null) return;
        minecraft.world.setRainStrength(0.0F);
        minecraft.world.setThunderStrength(0.0F);
    }

    private static void restoreOverworldAtmosphere() {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (previousCloudSetting != null && minecraft.gameSettings != null) {
            minecraft.gameSettings.clouds = previousCloudSetting;
            minecraft.gameSettings.saveOptions();
        }
        previousCloudSetting = null;
    }
}
