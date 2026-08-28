package com.godh00d.sf4angel.client;

import com.godh00d.sf4angel.SF4Angel;
import com.godh00d.sf4angel.constellation.AchievementConstellationCatalog;
import com.godh00d.sf4angel.network.MessageConstellationProgress;

public final class ConstellationClientState {

    private static byte[] states = new byte[0];

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
    }

    public static byte[] states() {
        return states;
    }

    public static void clear() {
        states = new byte[0];
    }
}
