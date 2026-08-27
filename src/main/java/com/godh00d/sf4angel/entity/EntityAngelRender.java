package com.godh00d.sf4angel.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAngelRender extends RenderLiving<EntityAngel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("sf4angel", "textures/entity/angel.png");
    private static final Logger LOGGER = LogManager.getLogger("sf4angel");

    public EntityAngelRender(RenderManager renderManager) {
        super(renderManager, new ModelAngel(), 0.0F);
        LOGGER.info("Constructed active EntityAngelRender with ModelAngel");
    }

    @Override
    protected void renderLivingAt(EntityAngel angel, double x, double y, double z) {
        int transitionTicks = angel.getVisualState() == EntityAngel.STATE_DESPAWNING
            ? EntityAngel.DESPAWN_TRANSITION_TICKS - 1 : EntityAngel.TRANSITION_TICKS - 1;
        float progress = Math.min(1.0F, (float) angel.getStateTimer() / transitionTicks);
        double transitionY = 0.0D;
        if (angel.getAnimationType() == EntityAngel.ANIM_SKY) {
            if (angel.getVisualState() == EntityAngel.STATE_SPAWNING) {
                transitionY = (1.0F - progress) * EntityAngel.FLY_AWAY_HEIGHT;
            }
            if (angel.getVisualState() == EntityAngel.STATE_DESPAWNING) {
                transitionY = progress * EntityAngel.FLY_AWAY_HEIGHT;
            }
        }
        super.renderLivingAt(angel, x, y + transitionY, z);
    }

    @Override
    protected void preRenderCallback(EntityAngel angel, float partialTickTime) {
        float scale = angel.getRenderScale();
        GlStateManager.scale(scale, scale, scale);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityAngel entity) {
        return TEXTURE;
    }
}
