package com.godh00d.sf4angel.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class ModelAngel extends ModelBase {

    private final ModelRenderer body;
    private final ModelRenderer bodyGlow;
    private final ModelRenderer halo;
    private final ModelRenderer haloGlow;
    private final ModelRenderer pupil;

    public ModelAngel() {
        textureWidth = 16;
        textureHeight = 16;

        body = part(0.0F, 12.0F, 0.0F, -6.0F, -6.0F, -6.0F, 12, 12, 12);
        bodyGlow = part(0.0F, 12.0F, 0.0F, -6.5F, -6.5F, -6.5F, 13, 13, 13);

        halo = squareHalo(11.0F, 2.0F, 2.5F);
        haloGlow = squareHalo(12.0F, 3.0F, 2.5F);

        pupil = part(0.0F, 0.0F, -6.35F, -1.0F, -1.0F, -1.0F, 2, 2, 2);
    }

    private ModelRenderer part(float x, float y, float z, float boxX, float boxY, float boxZ,
                               int width, int height, int depth) {
        ModelRenderer renderer = new ModelRenderer(this, 0, 0);
        renderer.setRotationPoint(x, y, z);
        renderer.addBox(boxX, boxY, boxZ, width, height, depth);
        return renderer;
    }

    private ModelRenderer squareHalo(float radius, float thickness, float y) {
        ModelRenderer renderer = new ModelRenderer(this, 0, 0);
        renderer.setRotationPoint(0.0F, y, 0.0F);
        int width = Math.round(radius * 2.0F);
        int depth = Math.max(1, Math.round(thickness));
        float inner = radius - thickness;
        renderer.addBox(-radius, -thickness * 0.5F, -radius, width, depth, depth);
        renderer.addBox(-radius, -thickness * 0.5F, inner, width, depth, depth);
        renderer.addBox(-radius, -thickness * 0.5F, -inner, depth, depth,
            Math.max(1, Math.round(inner * 2.0F)));
        renderer.addBox(inner, -thickness * 0.5F, -inner, depth, depth,
            Math.max(1, Math.round(inner * 2.0F)));
        return renderer;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
                       float netHeadYaw, float headPitch, float scale) {
        EntityAngel angel = (EntityAngel) entity;
        int mood = angel.getMood();
        float speed = MathHelper.sqrt(angel.motionX * angel.motionX + angel.motionZ * angel.motionZ);
        float inertia = MathHelper.clamp(speed * 1.8F, 0.0F, 0.24F);
        float breath = MathHelper.sin(ageInTicks * 0.075F);
        float delayedBreath = MathHelper.sin((ageInTicks - 3.0F) * 0.075F);

        body.rotateAngleY = getTransitionSpin(angel, ageInTicks);
        body.rotateAngleX = (float) MathHelper.clamp(-angel.motionY * 0.75D, -0.16D, 0.16D);
        body.rotateAngleZ = breath * 0.035F + MathHelper.sin(ageInTicks * 0.041F) * inertia;
        body.rotationPointX = MathHelper.sin(ageInTicks * 0.035F) * (0.15F + inertia * 1.5F);
        body.rotationPointY = 12.0F + breath * 0.18F;

        bodyGlow.rotateAngleY = body.rotateAngleY;
        bodyGlow.rotateAngleX = body.rotateAngleX;
        bodyGlow.rotateAngleZ = body.rotateAngleZ;
        bodyGlow.rotationPointX = body.rotationPointX;
        bodyGlow.rotationPointY = body.rotationPointY;

        float haloSpeed = mood == EntityAngel.MOOD_PROUD ? 8.0F
            : mood == EntityAngel.MOOD_IRRITATED ? 11.0F
            : mood == EntityAngel.MOOD_CONCERNED ? 2.2F : 4.0F;
        halo.rotateAngleY = (float) Math.toRadians(ageInTicks * haloSpeed);
        halo.rotateAngleX = body.rotateAngleX * 0.35F;
        halo.rotateAngleZ = delayedBreath * 0.035F - body.rotateAngleZ * 0.3F;
        halo.rotationPointY = 2.5F + delayedBreath * 0.12F;
        haloGlow.rotateAngleY = halo.rotateAngleY;
        haloGlow.rotateAngleX = halo.rotateAngleX;
        haloGlow.rotateAngleZ = halo.rotateAngleZ;
        haloGlow.rotationPointY = halo.rotationPointY;

        placePupil(angel.getClientPupilYaw(), angel.getClientPupilPitch());

        color(1.0F, 1.0F, 1.0F);
        body.render(scale);
        renderGlow(mood, scale);

        color(1.0F, mood == EntityAngel.MOOD_IRRITATED ? 0.4F : 0.88F, 0.32F);
        halo.render(scale);
        setPupilColor(mood);
        renderPupil(scale);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void placePupil(float yawDegrees, float pitchDegrees) {
        double yaw = Math.toRadians(yawDegrees);
        double pitch = Math.toRadians(pitchDegrees);
        float horizontal = (float) Math.cos(pitch);
        float x = (float) Math.sin(yaw) * horizontal;
        float y = (float) Math.sin(pitch);
        float z = (float) -Math.cos(yaw) * horizontal;
        float dominant = Math.max(Math.abs(x), Math.max(Math.abs(y), Math.abs(z)));
        float distance = 6.35F / Math.max(0.001F, dominant);

        pupil.rotationPointX = x * distance;
        pupil.rotationPointY = y * distance;
        pupil.rotationPointZ = z * distance;
        pupil.rotateAngleX = 0.0F;
        pupil.rotateAngleY = 0.0F;
        pupil.rotateAngleZ = 0.0F;
    }

    private void renderPupil(float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(body.rotationPointX * scale, body.rotationPointY * scale,
            body.rotationPointZ * scale);
        if (body.rotateAngleZ != 0.0F) GlStateManager.rotate(body.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
        if (body.rotateAngleY != 0.0F) GlStateManager.rotate(body.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
        if (body.rotateAngleX != 0.0F) GlStateManager.rotate(body.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);
        pupil.render(scale);
        GlStateManager.popMatrix();
    }

    private void renderGlow(int mood, float scale) {
        GL11.glPushAttrib(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_LIGHTING_BIT);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glColor4f(0.62F, 0.9F, 1.0F, 0.2F);
        bodyGlow.render(scale);
        GL11.glColor4f(1.0F, mood == EntityAngel.MOOD_IRRITATED ? 0.08F : 0.55F, 0.04F, 0.34F);
        haloGlow.render(scale);
        GL11.glPopAttrib();
    }

    private void setPupilColor(int mood) {
        if (mood == EntityAngel.MOOD_IRRITATED) color(0.35F, 0.0F, 0.01F);
        else if (mood == EntityAngel.MOOD_CONCERNED) color(0.01F, 0.12F, 0.28F);
        else color(0.015F, 0.025F, 0.04F);
    }

    private void color(float red, float green, float blue) {
        GlStateManager.color(red, green, blue, 1.0F);
    }

    private float getTransitionSpin(EntityAngel angel, float ageInTicks) {
        if (angel.getVisualState() != EntityAngel.STATE_VISIBLE
            && angel.getAnimationType() == EntityAngel.ANIM_SPIN) {
            float direction = angel.getVisualState() == EntityAngel.STATE_SPAWNING ? 1.0F : -1.0F;
            return (float) Math.toRadians(direction * ageInTicks * 18.0F);
        }
        return 0.0F;
    }
}
