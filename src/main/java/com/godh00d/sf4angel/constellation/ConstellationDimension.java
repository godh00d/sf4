package com.godh00d.sf4angel.constellation;

import com.godh00d.sf4angel.Reference;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.io.File;

public final class ConstellationDimension {

    private static final int DEFAULT_DIMENSION_ID = 42420;
    private static final int DEFAULT_TYPE_ID = 42421;
    private static int dimensionId = DEFAULT_DIMENSION_ID;
    private static DimensionType dimensionType;

    private ConstellationDimension() {
    }

    public static void register(File suggestedConfig, Logger logger) {
        Configuration config = new Configuration(suggestedConfig);
        try {
            config.load();
            dimensionId = config.getInt("dimensionId", "constellation", DEFAULT_DIMENSION_ID,
                2, Integer.MAX_VALUE, "Stable achievement constellation dimension ID.");
            int typeId = config.getInt("dimensionTypeId", "constellation", DEFAULT_TYPE_ID,
                2, Integer.MAX_VALUE, "Stable achievement constellation dimension type ID.");

            if (DimensionManager.isDimensionRegistered(dimensionId)) {
                throw new IllegalStateException("SF4 Angel constellation dimension ID " + dimensionId
                    + " is already registered; change constellation.dimensionId in " + suggestedConfig);
            }
            try {
                DimensionType.getById(typeId);
                throw new IllegalStateException("SF4 Angel constellation type ID " + typeId
                    + " is already registered; change constellation.dimensionTypeId in " + suggestedConfig);
            } catch (IllegalArgumentException expectedUnusedId) {
                // DimensionType reports unused IDs by throwing.
            }

            dimensionType = DimensionType.register(Reference.MOD_ID + "_constellation", "_constellation",
                typeId, WorldProviderConstellation.class, false);
            DimensionManager.registerDimension(dimensionId, dimensionType);
            logger.info("Registered achievement constellation dimension {} with type {}", dimensionId, typeId);
        } finally {
            if (config.hasChanged()) config.save();
        }
    }

    public static int getDimensionId() {
        return dimensionId;
    }

    public static DimensionType getDimensionType() {
        if (dimensionType == null) throw new IllegalStateException("Constellation dimension is not registered");
        return dimensionType;
    }
}
