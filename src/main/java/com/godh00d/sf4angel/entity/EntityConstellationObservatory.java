package com.godh00d.sf4angel.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public final class EntityConstellationObservatory extends EntityAngel {

    public static final double SCENE_OFFSET_X = 5.0D;
    public static final double SCENE_OFFSET_Z = -2.5D;
    private static final DataParameter<Float> SCENE_X = EntityDataManager.createKey(
        EntityConstellationObservatory.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SCENE_Y = EntityDataManager.createKey(
        EntityConstellationObservatory.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SCENE_Z = EntityDataManager.createKey(
        EntityConstellationObservatory.class, DataSerializers.FLOAT);

    public EntityConstellationObservatory(World world) {
        super(world);
        setConstellationAnchor(true);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        dataManager.register(SCENE_X, 0.0F);
        dataManager.register(SCENE_Y, 0.0F);
        dataManager.register(SCENE_Z, 0.0F);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (world.isRemote || isDead) return;
        EntityPlayer owner = getOwnerEntity();
        if (owner == null) return;

        double targetX = owner.posX + 1.8D;
        double targetY = owner.posY + owner.getEyeHeight() - 0.5D;
        double targetZ = owner.posZ + 1.2D;
        double dx = targetX - posX;
        double dy = targetY - posY;
        double dz = targetZ - posZ;
        double distanceSquared = dx * dx + dy * dy + dz * dz;
        double smoothing = distanceSquared > 64.0D ? 1.0D : 0.18D;
        setPosition(posX + dx * smoothing, posY + dy * smoothing, posZ + dz * smoothing);
        setLookTarget(owner);
    }

    public void setSceneCenter(double x, double y, double z) {
        dataManager.set(SCENE_X, (float) x);
        dataManager.set(SCENE_Y, (float) y);
        dataManager.set(SCENE_Z, (float) z);
    }

    public double getSceneX() {
        return dataManager.get(SCENE_X);
    }

    public double getSceneY() {
        return dataManager.get(SCENE_Y);
    }

    public double getSceneZ() {
        return dataManager.get(SCENE_Z);
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 320.0D * 320.0D;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setDouble("SceneX", getSceneX());
        compound.setDouble("SceneY", getSceneY());
        compound.setDouble("SceneZ", getSceneZ());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setSceneCenter(compound.getDouble("SceneX"), compound.getDouble("SceneY"),
            compound.getDouble("SceneZ"));
    }
}
