package com.godh00d.sf4angel.constellation;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.gen.IChunkGenerator;

public class WorldProviderConstellation extends WorldProvider {

    @Override
    public DimensionType getDimensionType() {
        return ConstellationDimension.getDimensionType();
    }

    @Override
    public IChunkGenerator createChunkGenerator() {
        return new ChunkGeneratorConstellation(world);
    }

    @Override
    public boolean isSurfaceWorld() {
        return false;
    }

    @Override
    public boolean canRespawnHere() {
        return false;
    }

    @Override
    public boolean canDoRainSnowIce(net.minecraft.world.chunk.Chunk chunk) {
        return false;
    }

    @Override
    public float calculateCelestialAngle(long worldTime, float partialTicks) {
        return 0.0F;
    }

    @Override
    public int getMoonPhase(long worldTime) {
        return 0;
    }
}
