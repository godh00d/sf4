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

    public ModelAngel() {
        textureWidth = 16;
        textureHeight = 16;

        body = part(0.0F, 12.0F, 0.0F, -6.0F, -6.0F, -6.0F, 12, 12, 12);
        bodyGlow = part(0.0F, 12.0F, 0.0F, -6.5F, -6.5F, -6.5F, 13, 13, 13);

        halo = squareHalo(11.0F, 2.0F, 2.5F);
        haloGlow = squareHalo(12.0F, 3.0F, 2.5F);

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

        float partialTicks = MathHelper.clamp(ageInTicks - angel.ticksExisted, 0.0F, 1.0F);
        float eyeYaw = angel.getClientPupilYaw(partialTicks);
        float eyePitch = angel.getClientPupilPitch(partialTicks);

        color(1.0F, 1.0F, 1.0F);
        body.render(scale);
        renderGlow(mood, scale);

        color(1.0F, mood == EntityAngel.MOOD_IRRITATED ? 0.4F : 0.88F, 0.32F);
        halo.render(scale);
        renderEye(mood, eyeYaw, eyePitch, scale);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void renderEye(int mood, float yawDegrees, float pitchDegrees, float scale) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(body.rotationPointX * scale, body.rotationPointY * scale,
            body.rotationPointZ * scale);
        if (body.rotateAngleZ != 0.0F) GlStateManager.rotate(body.rotateAngleZ * 57.295776F, 0.0F, 0.0F, 1.0F);
        if (body.rotateAngleY != 0.0F) GlStateManager.rotate(body.rotateAngleY * 57.295776F, 0.0F, 1.0F, 0.0F);
        if (body.rotateAngleX != 0.0F) GlStateManager.rotate(body.rotateAngleX * 57.295776F, 1.0F, 0.0F, 0.0F);

        float[] basis = inverseBodyBasis(createEyeBasis(yawDegrees, pitchDegrees));
        EyePlacement placement = placeEye(basis, 2.0F);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glColor4f(1.0F, 0.58F, 0.02F, 0.08F);
        drawFaceSquare(placement, 6.1F, 2.35F, scale);

        GL11.glDepthMask(true);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 0.7F, 0.05F, 1.0F);
        drawFaceSquare(placement, 6.06F, 2.0F, scale);
        if (mood == EntityAngel.MOOD_IRRITATED) {
            GL11.glColor4f(0.28F, 0.0F, 0.0F, 1.0F);
        } else {
            GL11.glColor4f(0.015F, 0.02F, 0.025F, 1.0F);
        }
        drawFaceSquare(placement, 6.11F, 0.9F, scale);
        GL11.glPopAttrib();
        GlStateManager.popMatrix();
    }

    private float[] createEyeBasis(float yawDegrees, float pitchDegrees) {
        double yaw = Math.toRadians(yawDegrees);
        double pitch = Math.toRadians(pitchDegrees);
        float sinYaw = (float) Math.sin(yaw);
        float cosYaw = (float) Math.cos(yaw);
        float sinPitch = (float) Math.sin(pitch);
        float cosPitch = (float) Math.cos(pitch);
        return new float[] {
            sinYaw * cosPitch, sinPitch, -cosYaw * cosPitch,
            cosYaw, 0.0F, sinYaw,
            -sinYaw * sinPitch, cosPitch, cosYaw * sinPitch
        };
    }

    private float[] inverseBodyBasis(float[] basis) {
        float[] transformed = new float[basis.length];
        for (int i = 0; i < basis.length; i += 3) {
            inverseBodyVector(basis[i], basis[i + 1], basis[i + 2], transformed, i);
        }
        return transformed;
    }

    private void inverseBodyVector(float x, float y, float z, float[] output, int offset) {
        float cosZ = MathHelper.cos(body.rotateAngleZ);
        float sinZ = MathHelper.sin(body.rotateAngleZ);
        float transformedX = cosZ * x + sinZ * y;
        float transformedY = -sinZ * x + cosZ * y;
        float cosY = MathHelper.cos(body.rotateAngleY);
        float sinY = MathHelper.sin(body.rotateAngleY);
        float transformedZ = sinY * transformedX + cosY * z;
        transformedX = cosY * transformedX - sinY * z;
        float cosX = MathHelper.cos(body.rotateAngleX);
        float sinX = MathHelper.sin(body.rotateAngleX);
        float finalY = cosX * transformedY + sinX * transformedZ;
        float finalZ = -sinX * transformedY + cosX * transformedZ;

        output[offset] = transformedX;
        output[offset + 1] = finalY;
        output[offset + 2] = finalZ;
    }

    private EyePlacement placeEye(float[] basis, float halfSize) {
        float x = basis[0];
        float y = basis[1];
        float z = basis[2];
        float absX = Math.abs(x);
        float absY = Math.abs(y);
        float absZ = Math.abs(z);
        float dominant = Math.max(absX, Math.max(absY, absZ));
        float distance = 6.06F / Math.max(0.001F, dominant);
        float centerX = x * distance;
        float centerY = y * distance;
        float centerZ = z * distance;
        float limit = 6.0F - halfSize;
        int face;

        if (absY >= absX && absY >= absZ) {
            face = y > 0.0F ? 2 : 3;
            centerX = MathHelper.clamp(centerX, -limit, limit);
            centerZ = MathHelper.clamp(centerZ, -limit, limit);
        } else if (absX >= absZ) {
            face = x > 0.0F ? 0 : 1;
            centerY = MathHelper.clamp(centerY, -limit, limit);
            centerZ = MathHelper.clamp(centerZ, -limit, limit);
        } else {
            face = z > 0.0F ? 4 : 5;
            centerX = MathHelper.clamp(centerX, -limit, limit);
            centerY = MathHelper.clamp(centerY, -limit, limit);
        }
        return new EyePlacement(face, centerX, centerY, centerZ);
    }

    private void drawFaceSquare(EyePlacement placement, float radius, float halfSize, float scale) {
        float x = placement.x;
        float y = placement.y;
        float z = placement.z;
        GL11.glBegin(GL11.GL_QUADS);
        if (placement.face == 0 || placement.face == 1) {
            x = placement.face == 0 ? radius : -radius;
            vertex(x, y - halfSize, z - halfSize, scale);
            vertex(x, y + halfSize, z - halfSize, scale);
            vertex(x, y + halfSize, z + halfSize, scale);
            vertex(x, y - halfSize, z + halfSize, scale);
        } else if (placement.face == 2 || placement.face == 3) {
            y = placement.face == 2 ? radius : -radius;
            vertex(x - halfSize, y, z - halfSize, scale);
            vertex(x + halfSize, y, z - halfSize, scale);
            vertex(x + halfSize, y, z + halfSize, scale);
            vertex(x - halfSize, y, z + halfSize, scale);
        } else {
            z = placement.face == 4 ? radius : -radius;
            vertex(x - halfSize, y - halfSize, z, scale);
            vertex(x + halfSize, y - halfSize, z, scale);
            vertex(x + halfSize, y + halfSize, z, scale);
            vertex(x - halfSize, y + halfSize, z, scale);
        }
        GL11.glEnd();
    }

    private void vertex(float x, float y, float z, float scale) {
        GL11.glVertex3f(x * scale, y * scale, z * scale);
    }

    private static class EyePlacement {
        final int face;
        final float x;
        final float y;
        final float z;

        EyePlacement(int face, float x, float y, float z) {
            this.face = face;
            this.x = x;
            this.y = y;
            this.z = z;
        }
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
