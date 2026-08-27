package com.godh00d.sf4angel.entity;

import com.godh00d.sf4angel.handler.TickHandler;
import com.godh00d.sf4angel.knowledge.AngelOracle;
import com.godh00d.sf4angel.knowledge.ChestScanner;
import com.godh00d.sf4angel.personality.AngelPersonality;
import com.godh00d.sf4angel.typewriter.TypewriterHandler;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.UUID;

public class EntityAngel extends EntityCreature {

    public static final DataParameter<Integer> VISUAL_STATE =
        EntityDataManager.createKey(EntityAngel.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> ANIMATION_TYPE =
        EntityDataManager.createKey(EntityAngel.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> STATE_TIMER =
        EntityDataManager.createKey(EntityAngel.class, DataSerializers.VARINT);
    public static final DataParameter<Float> HALO_ANGLE =
        EntityDataManager.createKey(EntityAngel.class, DataSerializers.FLOAT);
    public static final DataParameter<Integer> OWNER_ID =
        EntityDataManager.createKey(EntityAngel.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> MOOD =
        EntityDataManager.createKey(EntityAngel.class, DataSerializers.VARINT);
    public static final DataParameter<Integer> LOOK_TARGET_ID =
        EntityDataManager.createKey(EntityAngel.class, DataSerializers.VARINT);

    public static final int STATE_HIDDEN = 0;
    public static final int STATE_SPAWNING = 1;
    public static final int STATE_VISIBLE = 2;
    public static final int STATE_DESPAWNING = 3;

    public static final int ANIM_SMOKE = 0;
    public static final int ANIM_SPIN = 1;
    public static final int ANIM_SKY = 2;

    public static final int MOOD_CALM = 0;
    public static final int MOOD_CURIOUS = 1;
    public static final int MOOD_PROUD = 2;
    public static final int MOOD_CONCERNED = 3;
    public static final int MOOD_IRRITATED = 4;

    public static final int TRANSITION_TICKS = 24;
    public static final int DESPAWN_TRANSITION_TICKS = 40;
    public static final double FLY_AWAY_HEIGHT = 24.0D;

    private UUID ownerId;
    private boolean despawnQueued = false;
    private int tickCounter = 0;
    private int moodTimer;
    private float previousClientPupilYaw;
    private float previousClientPupilPitch;
    private float clientPupilYaw;
    private float clientPupilPitch;

    public EntityAngel(World world) {
        super(world);
        this.setSize(1.0F, 1.0F);
        this.noClip = true;
        this.setNoGravity(true);

    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(VISUAL_STATE, STATE_SPAWNING);
        this.dataManager.register(ANIMATION_TYPE, getRandomSpawnAnim());
        this.dataManager.register(STATE_TIMER, 0);
        this.dataManager.register(HALO_ANGLE, 0.0F);
        this.dataManager.register(OWNER_ID, -1);
        this.dataManager.register(MOOD, MOOD_CALM);
        this.dataManager.register(LOOK_TARGET_ID, -1);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(9999.0D);
        this.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).setBaseValue(1.0D);
    }

    @Override
    protected void updateAITasks() {
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (world.isRemote) {
            updateClientVisuals();
            return;
        }

        tickCounter++;

        switch (getVisualState()) {
            case STATE_SPAWNING:
                updateSpawning();
                break;
            case STATE_VISIBLE:
                updateVisible();
                break;
            case STATE_DESPAWNING:
                updateDespawning();
                break;
        }

        float halo = getHaloAngle() + 0.05F;
        if (halo > 6.283F) halo -= 6.283F;
        setHaloAngle(halo);
    }

    private void updateSpawning() {
        int timer = getStateTimer() + 1;
        setStateTimer(timer);

        if (timer >= TRANSITION_TICKS) {
            setVisualState(STATE_VISIBLE);
            setStateTimer(0);
        }
    }

    private void updateVisible() {
        int timer = getStateTimer() + 1;
        setStateTimer(timer);

        updatePersonality();
        checkDespawn();
    }

    private void updatePersonality() {
        EntityPlayer owner = getOwnerEntity();
        if (owner == null) return;
        if (getLookTarget() == null) setLookTarget(owner);

        int moodTimer = getMoodTimer();
        if (moodTimer > 0) {
            setMoodTimer(moodTimer - 1);
        }

        if (getMood() != MOOD_IRRITATED && owner.getHealth() < owner.getMaxHealth() * 0.3F) {
            setMood(MOOD_CONCERNED, 30);
            setLookTarget(owner);
            return;
        }

        if (getMoodTimer() > 0) return;

        setMood(MOOD_CALM, 0);
        setLookTarget(owner);
    }

    private void updateDespawning() {
        int timer = getStateTimer() + 1;
        setStateTimer(timer);

        if (timer >= DESPAWN_TRANSITION_TICKS) {
            setDead();
        }
    }

    private void checkDespawn() {
        EntityPlayer owner = getOwnerEntity();
        if (owner == null || despawnQueued) {
            if (owner instanceof EntityPlayerMP) {
                if (TypewriterHandler.shouldDespawn((EntityPlayerMP) owner)) {
                    startDespawn(0);
                }
            } else if (owner == null) {
                startDespawn(0);
            }
        }
    }

    public void startDespawn(int delay) {
        if (getVisualState() == STATE_DESPAWNING || getVisualState() == STATE_HIDDEN) return;
        setVisualState(STATE_DESPAWNING);
        setStateTimer(0);
        this.dataManager.set(ANIMATION_TYPE, ANIM_SKY);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (world.isRemote) return false;
        if (source == DamageSource.OUT_OF_WORLD) return false;
        if (source == DamageSource.IN_WALL) return false;
        if (source.isProjectile()) return false;

        if (source.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) source.getTrueSource();
            setMood(MOOD_IRRITATED, 100);
            setLookTarget(attacker);
            attacker.attackEntityFrom(DamageSource.causeMobDamage(this), 4.0F);

            if (attacker.world instanceof WorldServer) {
                WorldServer ws = (WorldServer) attacker.world;
                ws.addScheduledTask(() -> {
                    attacker.world.addWeatherEffect(
                        new net.minecraft.entity.effect.EntityLightningBolt(
                            attacker.world, attacker.posX, attacker.posY, attacker.posZ, false
                        )
                    );
                });
            }

            if (attacker instanceof EntityPlayerMP) {
                String response = AngelPersonality.getRandomAttackResponse();
                TypewriterHandler.queueRedMessage((EntityPlayerMP) attacker, response, 0, 0);
            }
        }

        return false;
    }

    @Override
    public boolean isEntityInvulnerable(DamageSource source) {
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public void onStruckByLightning(net.minecraft.entity.effect.EntityLightningBolt bolt) {
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public boolean isSilent() {
        return true;
    }

    @Override
    public int getBrightnessForRender() {
        return 15728880;
    }

    @Override
    public float getBrightness() {
        return 1.0F;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ENDEREYE_LAUNCH;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ENDERDRAGON_FLAP;
    }

    // ---- DataParameter getters/setters ----

    public int getVisualState() {
        return this.dataManager.get(VISUAL_STATE);
    }

    public void setVisualState(int state) {
        this.dataManager.set(VISUAL_STATE, state);
    }

    public int getAnimationType() {
        return this.dataManager.get(ANIMATION_TYPE);
    }

    public void setAnimationType(int type) {
        this.dataManager.set(ANIMATION_TYPE, type);
    }

    public int getStateTimer() {
        return this.dataManager.get(STATE_TIMER);
    }

    public void setStateTimer(int timer) {
        this.dataManager.set(STATE_TIMER, timer);
    }

    public float getHaloAngle() {
        return this.dataManager.get(HALO_ANGLE);
    }

    public void setHaloAngle(float angle) {
        this.dataManager.set(HALO_ANGLE, angle);
    }

    public int getMood() {
        return this.dataManager.get(MOOD);
    }

    public int getMoodTimer() {
        return moodTimer;
    }

    public void setMood(int mood, int ticks) {
        this.dataManager.set(MOOD, mood);
        moodTimer = Math.max(0, ticks);
    }

    public void setMoodTimer(int ticks) {
        moodTimer = Math.max(0, ticks);
    }

    public void setLookTarget(@Nullable EntityLivingBase target) {
        this.dataManager.set(LOOK_TARGET_ID, target == null ? -1 : target.getEntityId());
    }

    @Nullable
    public net.minecraft.entity.Entity getLookTarget() {
        int id = this.dataManager.get(LOOK_TARGET_ID);
        return id < 0 ? null : world.getEntityByID(id);
    }

    public float getClientPupilYaw(float partialTicks) {
        return previousClientPupilYaw
            + MathHelper.wrapDegrees(clientPupilYaw - previousClientPupilYaw) * partialTicks;
    }

    public float getClientPupilPitch(float partialTicks) {
        return previousClientPupilPitch + (clientPupilPitch - previousClientPupilPitch) * partialTicks;
    }

    public UUID getOwnerId() {
        if (ownerId != null) return ownerId;
        int id = this.dataManager.get(OWNER_ID);
        if (id == -1) return null;
        if (world.playerEntities.isEmpty()) return null;
        for (Object obj : world.playerEntities) {
            EntityPlayer p = (EntityPlayer) obj;
            if (p.getEntityId() == id) return p.getUniqueID();
        }
        return null;
    }

    public void setOwnerId(UUID uuid) {
        for (Object obj : world.playerEntities) {
            EntityPlayer p = (EntityPlayer) obj;
            if (p.getUniqueID().equals(uuid)) {
                this.dataManager.set(OWNER_ID, p.getEntityId());
                setLookTarget(p);
                this.ownerId = uuid;
                return;
            }
        }
    }

    @Nullable
    public EntityPlayer getOwnerEntity() {
        UUID id = getOwnerId();
        if (id == null) return null;
        for (Object obj : world.playerEntities) {
            EntityPlayer p = (EntityPlayer) obj;
            if (p.getUniqueID().equals(id)) return p;
        }
        return null;
    }

    // ---- Animation helpers ----

    private int getRandomSpawnAnim() {
        return world.rand.nextInt(3);
    }

    private void updateClientVisuals() {
        float halo = getHaloAngle() + 0.05F;
        if (halo > 6.283F) halo -= 6.283F;
        setHaloAngle(halo);

        if (getVisualState() != STATE_HIDDEN) {
            updateClientGaze();
            spawnClientParticles();
        }
    }

    private void updateClientGaze() {
        previousClientPupilYaw = clientPupilYaw;
        previousClientPupilPitch = clientPupilPitch;
        net.minecraft.entity.Entity target = getLookTarget();
        float targetYawOffset = 0.0F;
        float targetPitch = 0.0F;
        if (target != null) {
            double dx = target.posX - posX;
            double dz = target.posZ - posZ;
            double lookY = target instanceof EntityLivingBase
                ? target.posY + ((EntityLivingBase) target).getEyeHeight()
                : target.posY + target.height * 0.5D;
            double horizontal = Math.sqrt(dx * dx + dz * dz);
            float targetYaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
            targetYawOffset = MathHelper.wrapDegrees(renderYawOffset - targetYaw);
            targetPitch = (float) -(Math.atan2(lookY - (posY + 0.75D), horizontal)
                * 180.0D / Math.PI);
        }

        int mood = getMood();
        int dartInterval = mood == MOOD_CURIOUS || mood == MOOD_IRRITATED ? 42
            : mood == MOOD_PROUD ? 55 : mood == MOOD_CONCERNED ? 68 : 90;
        int dartTime = (ticksExisted + getEntityId() * 7) % dartInterval;
        int dartCycle = (ticksExisted + getEntityId() * 7) / dartInterval;
        float dartYaw = 0.0F;
        float dartPitch = 0.0F;
        if (dartTime < 5) {
            float yawRange = mood == MOOD_CURIOUS ? 14.0F : mood == MOOD_IRRITATED ? 18.0F : 7.0F;
            float pitchRange = mood == MOOD_CURIOUS ? 9.0F : mood == MOOD_IRRITATED ? 11.0F : 4.0F;
            dartYaw = MathHelper.sin((dartCycle * 19 + getEntityId()) * 1.73F) * yawRange;
            dartPitch = MathHelper.cos((dartCycle * 13 + getEntityId()) * 1.31F) * pitchRange;
        }
        float desiredPupilYaw = MathHelper.wrapDegrees(targetYawOffset + dartYaw);
        float desiredPupilPitch = MathHelper.clamp(targetPitch + dartPitch, -82.0F, 82.0F);
        float pupilSpeed = mood == MOOD_CONCERNED ? 0.18F
            : mood == MOOD_CURIOUS || mood == MOOD_IRRITATED ? 0.38F : 0.29F;
        clientPupilYaw += MathHelper.wrapDegrees(desiredPupilYaw - clientPupilYaw) * pupilSpeed;
        clientPupilPitch += (desiredPupilPitch - clientPupilPitch) * pupilSpeed;
    }

    private void spawnClientParticles() {
        boolean transitioning = getVisualState() == STATE_SPAWNING || getVisualState() == STATE_DESPAWNING;
        if (!transitioning && ticksExisted % 6 != 0) return;
        int count = transitioning ? 16 : 1;
        int transitionTicks = getVisualState() == STATE_DESPAWNING
            ? DESPAWN_TRANSITION_TICKS - 1 : TRANSITION_TICKS - 1;
        float progress = Math.min(1.0F, (float) getStateTimer() / transitionTicks);
        double transitionY = 0.0D;
        if (transitioning && getAnimationType() == ANIM_SKY) {
            transitionY = (getVisualState() == STATE_SPAWNING ? 1.0F - progress : progress)
                * FLY_AWAY_HEIGHT;
        }
        for (int i = 0; i < count; i++) {
            double direction = getVisualState() == STATE_SPAWNING ? -1.0D : 1.0D;
            double angle = direction * ticksExisted * (getAnimationType() == ANIM_SPIN ? 0.45D : 0.13D)
                + i * Math.PI * 2.0D / count;
            double radius = transitioning ? 0.2D + getRenderScale() * 0.8D : 0.55D + world.rand.nextDouble() * 0.35D;
            double px = posX + Math.cos(angle) * radius;
            double py = posY + transitionY + 0.35D + world.rand.nextDouble() * 1.1D;
            double pz = posZ + Math.sin(angle) * radius;
            double mx = (world.rand.nextDouble() - 0.5D) * 0.015D;
            double my = 0.015D + world.rand.nextDouble() * 0.025D;
            double mz = (world.rand.nextDouble() - 0.5D) * 0.015D;

            EnumParticleTypes particle;
            if (transitioning && getAnimationType() == ANIM_SMOKE) {
                particle = i % 2 == 0 ? EnumParticleTypes.SMOKE_LARGE : EnumParticleTypes.CLOUD;
            } else if (transitioning && getAnimationType() == ANIM_SKY) {
                particle = i % 2 == 0 ? EnumParticleTypes.END_ROD : EnumParticleTypes.FIREWORKS_SPARK;
            } else {
                particle = ticksExisted % 12 == 0
                    ? EnumParticleTypes.END_ROD : EnumParticleTypes.ENCHANTMENT_TABLE;
            }
            world.spawnParticle(particle, px, py, pz, mx, my, mz);
        }
    }

    // ---- Scale for renderer ----

    public float getRenderScale() {
        int state = getVisualState();
        int timer = getStateTimer();

        if (state == STATE_SPAWNING) {
            float progress = (float) timer / (TRANSITION_TICKS - 1);
            if (getAnimationType() != ANIM_SPIN) return 1.0F;
            return 0.05F + progress * 0.95F;
        }
        if (state == STATE_DESPAWNING) {
            float progress = (float) timer / DESPAWN_TRANSITION_TICKS;
            if (getAnimationType() != ANIM_SPIN) return 1.0F;
            return Math.max(0.05F, 1.0F - progress * 0.95F);
        }
        return 1.0F;
    }

    public float getRenderSpin() {
        int anim = getAnimationType();
        if (getVisualState() != STATE_VISIBLE) {
            float direction = getVisualState() == STATE_SPAWNING ? -1.0F : 1.0F;
            if (anim == ANIM_SPIN) return direction * ticksExisted * 18.0F;
        }
        return -ticksExisted * 2.0F;
    }

    // ---- NBT ----

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("VisualState", getVisualState());
        compound.setInteger("AnimationType", getAnimationType());
        compound.setInteger("StateTimer", getStateTimer());
        compound.setInteger("Mood", getMood());
        compound.setInteger("MoodTimer", getMoodTimer());
        if (ownerId != null) {
            compound.setString("OwnerId", ownerId.toString());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setVisualState(compound.getInteger("VisualState"));
        setAnimationType(compound.getInteger("AnimationType"));
        setStateTimer(compound.getInteger("StateTimer"));
        if (compound.hasKey("Mood")) setMood(compound.getInteger("Mood"), compound.getInteger("MoodTimer"));
        if (compound.hasKey("OwnerId")) {
            try {
                ownerId = UUID.fromString(compound.getString("OwnerId"));
            } catch (Exception e) {
            }
        }
    }
}
