package com.godh00d.sf4angel.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

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
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDepthMask(false);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glColor4f(1.0F, 0.58F, 0.02F, 0.08F);
        drawSurfaceShape(basis, 6.1F, scale, 0.37F);

        GL11.glDepthMask(true);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0F, 0.7F, 0.05F, 1.0F);
        drawSurfaceShape(basis, 6.06F, scale, 0.32F);
        if (mood == EntityAngel.MOOD_IRRITATED) {
            GL11.glColor4f(0.28F, 0.0F, 0.0F, 1.0F);
        } else {
            GL11.glColor4f(0.015F, 0.02F, 0.025F, 1.0F);
        }
        drawSurfaceShape(basis, 6.11F, scale, 0.14F);
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

    private void drawSurfaceShape(float[] basis, float radius, float scale, float halfSize) {
        List<EyePoint> shape = new ArrayList<>(4);
        shape.add(new EyePoint(-halfSize, -halfSize));
        shape.add(new EyePoint(halfSize, -halfSize));
        shape.add(new EyePoint(halfSize, halfSize));
        shape.add(new EyePoint(-halfSize, halfSize));

        for (int face = 0; face < 6; face++) {
            List<EyePoint> clipped = shape;
            for (int constraint = 0; constraint < 5 && !clipped.isEmpty(); constraint++) {
                clipped = clipToFace(clipped, basis, face, constraint);
            }
            if (clipped.size() < 3) continue;

            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
            for (EyePoint point : clipped) {
                emitFaceVertex(basis, point, face, radius, scale);
            }
            GL11.glEnd();
        }
    }

    private List<EyePoint> clipToFace(List<EyePoint> input, float[] basis,
                                      int face, int constraint) {
        List<EyePoint> output = new ArrayList<>(input.size() + 2);
        EyePoint previous = input.get(input.size() - 1);
        float previousValue = faceConstraint(basis, previous, face, constraint);
        for (EyePoint current : input) {
            float currentValue = faceConstraint(basis, current, face, constraint);
            boolean previousInside = previousValue >= 0.0F;
            boolean currentInside = currentValue >= 0.0F;
            if (previousInside != currentInside) {
                float amount = previousValue / (previousValue - currentValue);
                output.add(new EyePoint(previous.u + (current.u - previous.u) * amount,
                    previous.v + (current.v - previous.v) * amount));
            }
            if (currentInside) output.add(current);
            previous = current;
            previousValue = currentValue;
        }
        return output;
    }

    private float faceConstraint(float[] basis, EyePoint point, int face, int constraint) {
        float x = basis[0] + basis[3] * point.u + basis[6] * point.v;
        float y = basis[1] + basis[4] * point.u + basis[7] * point.v;
        float z = basis[2] + basis[5] * point.u + basis[8] * point.v;
        float primary;
        float firstOther;
        float secondOther;
        switch (face) {
            case 0: primary = x; firstOther = y; secondOther = z; break;
            case 1: primary = -x; firstOther = y; secondOther = z; break;
            case 2: primary = y; firstOther = x; secondOther = z; break;
            case 3: primary = -y; firstOther = x; secondOther = z; break;
            case 4: primary = z; firstOther = x; secondOther = y; break;
            default: primary = -z; firstOther = x; secondOther = y; break;
        }
        switch (constraint) {
            case 0: return primary;
            case 1: return primary - firstOther;
            case 2: return primary + firstOther;
            case 3: return primary - secondOther;
            default: return primary + secondOther;
        }
    }

    private void emitFaceVertex(float[] basis, EyePoint point, int face,
                                float radius, float scale) {
        float x = basis[0] + basis[3] * point.u + basis[6] * point.v;
        float y = basis[1] + basis[4] * point.u + basis[7] * point.v;
        float z = basis[2] + basis[5] * point.u + basis[8] * point.v;
        float primary;
        switch (face) {
            case 0: primary = x; break;
            case 1: primary = -x; break;
            case 2: primary = y; break;
            case 3: primary = -y; break;
            case 4: primary = z; break;
            default: primary = -z; break;
        }
        float distance = radius / Math.max(0.001F, primary);
        GL11.glVertex3f(x * distance * scale, y * distance * scale, z * distance * scale);
    }

    private static class EyePoint {
        final float u;
        final float v;

        EyePoint(float u, float v) {
            this.u = u;
            this.v = v;
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
