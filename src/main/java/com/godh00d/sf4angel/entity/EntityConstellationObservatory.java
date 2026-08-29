package com.godh00d.sf4angel.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

public final class EntityConstellationObservatory extends EntityAngel {

    private static final DataParameter<Float> SCENE_X = EntityDataManager.createKey(
        EntityConstellationObservatory.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SCENE_Y = EntityDataManager.createKey(
        EntityConstellationObservatory.class, DataSerializers.FLOAT);
    private static final DataParameter<Float> SCENE_Z = EntityDataManager.createKey(
        EntityConstellationObservatory.class, DataSerializers.FLOAT);
    private double trailX;
    private double trailZ = 1.0D;
    private double followVelocityX;
    private double followVelocityY;
    private double followVelocityZ;
    private double previousOwnerX;
    private double previousOwnerZ;
    private boolean ownerPositionKnown;

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

        if (!ownerPositionKnown) {
            previousOwnerX = owner.posX;
            previousOwnerZ = owner.posZ;
            ownerPositionKnown = true;
        }
        double movementX = owner.posX - previousOwnerX;
        double movementZ = owner.posZ - previousOwnerZ;
        previousOwnerX = owner.posX;
        previousOwnerZ = owner.posZ;
        double movementLength = Math.sqrt(movementX * movementX + movementZ * movementZ);
        if (movementLength > 0.025D) {
            trailX = movementX / movementLength;
            trailZ = movementZ / movementLength;
        }
        double targetX = owner.posX - trailX * 2.0D;
        double targetY = owner.posY + owner.getEyeHeight() - 0.5D;
        double targetZ = owner.posZ - trailZ * 2.0D;
        double dx = targetX - posX;
        double dy = targetY - posY;
        double dz = targetZ - posZ;
        double distanceSquared = dx * dx + dy * dy + dz * dz;
        followVelocityX = followVelocityX * 0.82D + dx * 0.045D;
        followVelocityY = followVelocityY * 0.82D + dy * 0.045D;
        followVelocityZ = followVelocityZ * 0.82D + dz * 0.045D;
        double velocity = Math.sqrt(followVelocityX * followVelocityX
            + followVelocityY * followVelocityY + followVelocityZ * followVelocityZ);
        double maxVelocity = movementLength + 0.38D
            + Math.min(0.82D, Math.sqrt(distanceSquared) * 0.055D);
        if (velocity > maxVelocity) {
            double scale = maxVelocity / velocity;
            followVelocityX *= scale;
            followVelocityY *= scale;
            followVelocityZ *= scale;
        }
        double nextX = posX + followVelocityX;
        double nextY = posY + followVelocityY;
        double nextZ = posZ + followVelocityZ;
        double ownerCenterY = owner.posY + owner.getEyeHeight() - 0.5D;
        double ownerDx = nextX - owner.posX;
        double ownerDy = nextY - ownerCenterY;
        double ownerDz = nextZ - owner.posZ;
        double ownerDistance = Math.sqrt(ownerDx * ownerDx + ownerDy * ownerDy + ownerDz * ownerDz);
        if (ownerDistance > 2.75D) {
            double leash = 2.75D / ownerDistance;
            nextX = owner.posX + ownerDx * leash;
            nextY = ownerCenterY + ownerDy * leash;
            nextZ = owner.posZ + ownerDz * leash;
        }
        setPosition(nextX, nextY, nextZ);
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
