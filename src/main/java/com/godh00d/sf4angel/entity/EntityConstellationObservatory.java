package com.godh00d.sf4angel.entity;

import net.minecraft.world.World;

public final class EntityConstellationObservatory extends EntityAngel {

    public static final double SCENE_OFFSET_X = 57.0D;
    public static final double SCENE_OFFSET_Z = -2.5D;

    public EntityConstellationObservatory(World world) {
        super(world);
        setConstellationAnchor(true);
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 320.0D * 320.0D;
    }
}
