package com.godh00d.sf4angel.constellation;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/** Dependency-free, fail-closed access to the current GameStages player data. */
final class GameStageAccess {

    private static final Logger LOGGER = LogManager.getLogger("sf4angel");
    private static Method hasStage;
    private static boolean resolved;
    private static boolean warned;

    private GameStageAccess() {
    }

    static boolean hasStage(EntityPlayer player, String stage) {
        if (!Loader.isModLoaded("gamestages")) return false;
        Method method = resolve();
        if (method == null) return false;
        try {
            Object result = method.invoke(null, player, stage);
            return result instanceof Boolean && (Boolean) result;
        } catch (ReflectiveOperationException | LinkageError | RuntimeException exception) {
            warn(exception);
            return false;
        }
    }

    private static Method resolve() {
        if (resolved) return hasStage;
        resolved = true;
        try {
            Class<?> helper = Class.forName("net.darkhax.gamestages.GameStageHelper");
            for (Method method : helper.getMethods()) {
                Class<?>[] parameters = method.getParameterTypes();
                if (method.getName().equals("hasStage") && Modifier.isStatic(method.getModifiers())
                    && parameters.length == 2 && parameters[0].isAssignableFrom(EntityPlayer.class)
                    && parameters[1] == String.class) {
                    method.setAccessible(true);
                    hasStage = method;
                    break;
                }
            }
            if (hasStage == null) throw new NoSuchMethodException("GameStageHelper.hasStage(EntityPlayer, String)");
        } catch (ReflectiveOperationException | LinkageError | RuntimeException exception) {
            warn(exception);
        }
        return hasStage;
    }

    private static void warn(Throwable exception) {
        if (warned) return;
        warned = true;
        LOGGER.warn("Current GameStages ownership is unavailable; gated constellation nodes are hidden: {}",
            exception.toString());
    }
}
