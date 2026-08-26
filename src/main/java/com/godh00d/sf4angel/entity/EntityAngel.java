package com.godh00d.sf4angel.entity;

import com.godh00d.sf4angel.handler.TickHandler;
import com.godh00d.sf4angel.knowledge.AngelOracle;
import com.godh00d.sf4angel.knowledge.ChestScanner;
import com.godh00d.sf4angel.personality.AngelPersonality;
import com.godh00d.sf4angel.typewriter.TypewriterHandler;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.List;
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

    public static final int STATE_HIDDEN = 0;
    public static final int STATE_SPAWNING = 1;
    public static final int STATE_VISIBLE = 2;
    public static final int STATE_DESPAWNING = 3;

    public static final int ANIM_DESCEND = 0;
    public static final int ANIM_SMOKE_FORM = 1;
    public static final int ANIM_SPIN_GROW = 2;
    public static final int ANIM_SMOKE = 3;
    public static final int ANIM_ASCEND = 4;
    public static final int ANIM_SPIN_SHRINK = 5;

    private static final int SPAWN_TICKS = 30;
    private static final int DESPAWN_TICKS = 30;

    private UUID ownerId;
    private boolean despawnQueued = false;
    private int tickCounter = 0;

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

        if (timer >= SPAWN_TICKS) {
            setVisualState(STATE_VISIBLE);
            setStateTimer(0);
        }
    }

    private void updateVisible() {
        int timer = getStateTimer() + 1;
        setStateTimer(timer);

        checkDespawn();
    }

    private void updateDespawning() {
        int timer = getStateTimer() + 1;
        setStateTimer(timer);

        if (timer >= DESPAWN_TICKS) {
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
        this.dataManager.set(ANIMATION_TYPE, getRandomDespawnAnim());
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source == DamageSource.OUT_OF_WORLD) return false;
        if (source == DamageSource.IN_WALL) return false;
        if (source.isProjectile()) return false;

        if (source.getTrueSource() instanceof EntityPlayer) {
            EntityPlayer attacker = (EntityPlayer) source.getTrueSource();
            attacker.attackEntityFrom(DamageSource.causeMobDamage(this), 4.0F);

            WorldServer ws = (WorldServer) attacker.world;
            ws.addScheduledTask(() -> {
                attacker.world.addWeatherEffect(
                    new net.minecraft.entity.effect.EntityLightningBolt(
                        attacker.world, attacker.posX, attacker.posY, attacker.posZ, false
                    )
                );
            });

            if (attacker instanceof EntityPlayerMP) {
                String response = AngelPersonality.getRandomAttackResponse();
                TypewriterHandler.queueMessage((EntityPlayerMP) attacker, response, 0, 0);
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

    public UUID getOwnerId() {
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
        int[] anims = {ANIM_DESCEND, ANIM_SMOKE_FORM, ANIM_SPIN_GROW};
        return anims[world.rand.nextInt(anims.length)];
    }

    private int getRandomDespawnAnim() {
        int[] anims = {ANIM_SMOKE, ANIM_ASCEND, ANIM_SPIN_SHRINK};
        return anims[world.rand.nextInt(anims.length)];
    }

    private void updateClientVisuals() {
        float halo = getHaloAngle() + 0.05F;
        if (halo > 6.283F) halo -= 6.283F;
        setHaloAngle(halo);
    }

    // ---- Scale for renderer ----

    public float getRenderScale() {
        int state = getVisualState();
        int timer = getStateTimer();

        if (state == STATE_SPAWNING) {
            float progress = (float) timer / SPAWN_TICKS;
            return 0.1F + progress * 0.9F;
        }
        if (state == STATE_DESPAWNING) {
            float progress = (float) timer / DESPAWN_TICKS;
            return 1.0F - progress * 0.9F;
        }
        return 1.0F;
    }

    public float getRenderSpin() {
        int anim = getAnimationType();
        if (anim == ANIM_SPIN_GROW || anim == ANIM_SPIN_SHRINK) {
            return tickCounter * 0.2F;
        }
        return 0.0F;
    }

    // ---- NBT ----

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setInteger("VisualState", getVisualState());
        compound.setInteger("AnimationType", getAnimationType());
        compound.setInteger("StateTimer", getStateTimer());
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
        if (compound.hasKey("OwnerId")) {
            try {
                ownerId = UUID.fromString(compound.getString("OwnerId"));
            } catch (Exception e) {
            }
        }
    }
}
