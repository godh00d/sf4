package com.godh00d.sf4angel.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelAngel extends ModelBase {

    private final ModelRenderer body;
    private final ModelRenderer halo;

    public ModelAngel() {
        this.textureWidth = 16;
        this.textureHeight = 16;

        body = new ModelRenderer(this, 0, 0);
        body.setRotationPoint(0.0F, 12.0F, 0.0F);
        body.addBox(-6.0F, -6.0F, -6.0F, 12, 12, 12);

        halo = new ModelRenderer(this, 0, 0);
        halo.setRotationPoint(0.0F, 2.5F, 0.0F);
        halo.addBox(-11.0F, -1.0F, -11.0F, 22, 2, 2);
        halo.addBox(-11.0F, -1.0F, 9.0F, 22, 2, 2);
        halo.addBox(-11.0F, -1.0F, -9.0F, 2, 2, 18);
        halo.addBox(9.0F, -1.0F, -9.0F, 2, 2, 18);
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        EntityAngel angel = (EntityAngel) entity;
        float sway = MathHelper.sin(ageInTicks * 0.065F) * 0.075F;
        float forwardLean = (float) MathHelper.clamp(angel.motionZ * 0.8D, -0.12D, 0.12D);
        float sideLean = (float) MathHelper.clamp(-angel.motionX * 0.9D, -0.16D, 0.16D);

        body.rotateAngleY = (float) Math.toRadians(getBodySpin(angel, ageInTicks));
        body.rotateAngleX = forwardLean + MathHelper.sin(ageInTicks * 0.043F) * 0.025F;
        body.rotateAngleZ = sway + sideLean;
        body.rotationPointX = MathHelper.sin(ageInTicks * 0.035F) * 0.35F;

        float delayedTime = ageInTicks - 3.0F;
        halo.rotateAngleY = (float) Math.toRadians(delayedTime * 3.5F);
        halo.rotateAngleX = forwardLean * 0.45F;
        halo.rotateAngleZ = MathHelper.sin(delayedTime * 0.065F) * 0.055F + sideLean * 0.45F;
        halo.rotationPointX = MathHelper.sin(delayedTime * 0.035F) * 0.25F;

        GlStateManager.color(0.95F, 0.98F, 1.0F, 1.0F);
        body.render(scale);
        GlStateManager.color(1.0F, 0.62F, 0.02F, 1.0F);
        halo.render(scale);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private float getBodySpin(EntityAngel angel, float ageInTicks) {
        if (angel.getVisualState() != EntityAngel.STATE_VISIBLE && angel.getAnimationType() == EntityAngel.ANIM_SPIN) {
            float direction = angel.getVisualState() == EntityAngel.STATE_SPAWNING ? 1.0F : -1.0F;
            return direction * ageInTicks * 18.0F;
        }
        return -ageInTicks * 3.5F;
    }
}
