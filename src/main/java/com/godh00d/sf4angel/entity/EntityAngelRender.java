package com.godh00d.sf4angel.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class EntityAngelRender extends Render<EntityAngel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("sf4angel", "textures/entity/angel.png");
    private static final Random RANDOM = new Random();

    private static final float CUBE_SIZE = 0.7F;
    private static final float HALO_Y = 0.55F;
    private static final float HALO_SIZE = 0.55F;
    private static final int HALO_SEGMENTS = 32;

    public EntityAngelRender(RenderManager renderManager) {
        super(renderManager);
        this.shadowOpaque = 0.0F;
    }

    @Override
    public void doRender(EntityAngel angel, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        float scale = angel.getRenderScale();
        float spin = angel.getRenderSpin();
        int animType = angel.getAnimationType();
        int state = angel.getVisualState();

        // DESCEND: slide down from above; ASCEND: slide up
        float yOffset = 0.5F;
        if (state == EntityAngel.STATE_SPAWNING && animType == EntityAngel.ANIM_DESCEND) {
            float progress = (float) angel.getStateTimer() / 30.0F;
            yOffset += (1.0F - progress) * 3.0F;
        }
        if (state == EntityAngel.STATE_DESPAWNING && animType == EntityAngel.ANIM_ASCEND) {
            float progress = (float) angel.getStateTimer() / 30.0F;
            yOffset += progress * 3.0F;
        }

        GlStateManager.translate(0, yOffset, 0);
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.rotate(spin, 0, 1, 0);

        float alpha = getAlpha(angel);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();

        renderBody(angel, alpha);
        renderHalo(angel, alpha);
        renderBeams(angel, alpha);

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();

        spawnParticles(angel);
    }

    private float getAlpha(EntityAngel angel) {
        int state = angel.getVisualState();
        if (state == EntityAngel.STATE_SPAWNING) {
            return 0.1F + angel.getRenderScale() * 0.8F;
        }
        if (state == EntityAngel.STATE_DESPAWNING) {
            return angel.getRenderScale();
        }
        return 0.9F;
    }

    private void renderBody(EntityAngel angel, float alpha) {
        float timer = (float) angel.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
        float bob = MathHelper.sin(timer * 0.05F) * 0.08F;

        GlStateManager.translate(0, bob, 0);

        this.bindTexture(TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

        float s = CUBE_SIZE;
        float hs = s / 2.0F;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        // Front
        buf.pos(-hs, -hs, hs).tex(0, 0).endVertex();
        buf.pos(hs, -hs, hs).tex(1, 0).endVertex();
        buf.pos(hs, hs, hs).tex(1, 1).endVertex();
        buf.pos(-hs, hs, hs).tex(0, 1).endVertex();
        // Back
        buf.pos(hs, -hs, -hs).tex(0, 0).endVertex();
        buf.pos(-hs, -hs, -hs).tex(1, 0).endVertex();
        buf.pos(-hs, hs, -hs).tex(1, 1).endVertex();
        buf.pos(hs, hs, -hs).tex(0, 1).endVertex();
        // Left
        buf.pos(-hs, -hs, -hs).tex(0, 0).endVertex();
        buf.pos(-hs, -hs, hs).tex(1, 0).endVertex();
        buf.pos(-hs, hs, hs).tex(1, 1).endVertex();
        buf.pos(-hs, hs, -hs).tex(0, 1).endVertex();
        // Right
        buf.pos(hs, -hs, hs).tex(0, 0).endVertex();
        buf.pos(hs, -hs, -hs).tex(1, 0).endVertex();
        buf.pos(hs, hs, -hs).tex(1, 1).endVertex();
        buf.pos(hs, hs, hs).tex(0, 1).endVertex();
        // Top
        buf.pos(-hs, hs, hs).tex(0, 0).endVertex();
        buf.pos(hs, hs, hs).tex(1, 0).endVertex();
        buf.pos(hs, hs, -hs).tex(1, 1).endVertex();
        buf.pos(-hs, hs, -hs).tex(0, 1).endVertex();
        // Bottom
        buf.pos(-hs, -hs, -hs).tex(0, 0).endVertex();
        buf.pos(hs, -hs, -hs).tex(1, 0).endVertex();
        buf.pos(hs, -hs, hs).tex(1, 1).endVertex();
        buf.pos(-hs, -hs, hs).tex(0, 1).endVertex();

        tess.draw();

        GlStateManager.translate(0, -bob, 0);
    }

    private void renderHalo(EntityAngel angel, float alpha) {
        GlStateManager.pushMatrix();

        float timer = (float) angel.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
        float bob = MathHelper.sin(timer * 0.05F) * 0.08F;
        float haloAngle = angel.getHaloAngle();

        GlStateManager.translate(0, bob + HALO_Y, 0);
        GlStateManager.rotate((float) Math.toDegrees(haloAngle), 0, 1, 0);

        GlStateManager.disableTexture2D();
        GlStateManager.color(1.0F, 0.85F, 0.0F, alpha);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        float hs = HALO_SIZE;

        buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buf.pos(-hs, 0.0F, -hs).endVertex();
        buf.pos(hs, 0.0F, -hs).endVertex();
        buf.pos(hs, 0.0F, hs).endVertex();
        buf.pos(-hs, 0.0F, hs).endVertex();
        buf.pos(-hs, 0.0F, -hs).endVertex();
        tess.draw();

        buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        buf.pos(-hs, 0.02F, -hs).endVertex();
        buf.pos(hs, 0.02F, -hs).endVertex();
        buf.pos(hs, 0.02F, hs).endVertex();
        buf.pos(-hs, 0.02F, hs).endVertex();
        buf.pos(-hs, 0.02F, -hs).endVertex();
        tess.draw();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void renderBeams(EntityAngel angel, float alpha) {
        GlStateManager.pushMatrix();

        float timer = (float) angel.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
        float bob = MathHelper.sin(timer * 0.05F) * 0.08F;
        float haloAngle = angel.getHaloAngle();

        GlStateManager.translate(0, bob, 0);
        GlStateManager.rotate((float) Math.toDegrees(haloAngle), 0, 1, 0);

        GlStateManager.disableTexture2D();
        GlStateManager.color(1.0F, 0.85F, 0.0F, alpha * 0.7F);

        float hs = HALO_SIZE;
        float beamTop = HALO_Y;
        float beamBottom = CUBE_SIZE / 2.0F + 0.05F;
        float inset = 0.4F;

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

        buf.pos(-hs, beamTop, -hs).endVertex();
        buf.pos(-hs * inset, beamBottom, -hs * inset).endVertex();

        buf.pos(hs, beamTop, -hs).endVertex();
        buf.pos(hs * inset, beamBottom, -hs * inset).endVertex();

        buf.pos(hs, beamTop, hs).endVertex();
        buf.pos(hs * inset, beamBottom, hs * inset).endVertex();

        buf.pos(-hs, beamTop, hs).endVertex();
        buf.pos(-hs * inset, beamBottom, hs * inset).endVertex();

        tess.draw();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }

    private void spawnParticles(EntityAngel angel) {
        if (!angel.world.isRemote) return;
        if (angel.getVisualState() != EntityAngel.STATE_VISIBLE) return;

        int count = 3 + RANDOM.nextInt(4);
        for (int i = 0; i < count; i++) {
            double px = angel.posX + (RANDOM.nextDouble() - 0.5) * 1.2;
            double py = angel.posY + 0.5 + RANDOM.nextDouble() * 1.5;
            double pz = angel.posZ + (RANDOM.nextDouble() - 0.5) * 1.2;

            int type = RANDOM.nextInt(5);
            switch (type) {
                case 0:
                    angel.world.spawnParticle(
                        net.minecraft.util.EnumParticleTypes.END_ROD,
                        px, py, pz, 0, 0.03, 0
                    );
                    break;
                case 1:
                    angel.world.spawnParticle(
                        net.minecraft.util.EnumParticleTypes.ENCHANTMENT_TABLE,
                        px, py, pz,
                        (RANDOM.nextDouble() - 0.5) * 0.3,
                        0.05,
                        (RANDOM.nextDouble() - 0.5) * 0.3
                    );
                    break;
                case 2:
                    angel.world.spawnParticle(
                        net.minecraft.util.EnumParticleTypes.VILLAGER_HAPPY,
                        px, py, pz, 0, 0.02, 0
                    );
                    break;
                case 3:
                    angel.world.spawnParticle(
                        net.minecraft.util.EnumParticleTypes.CLOUD,
                        px, py, pz,
                        (RANDOM.nextDouble() - 0.5) * 0.05,
                        0.03,
                        (RANDOM.nextDouble() - 0.5) * 0.05
                    );
                    break;
                case 4:
                    angel.world.spawnParticle(
                        net.minecraft.util.EnumParticleTypes.FIREWORKS_SPARK,
                        px, py, pz,
                        (RANDOM.nextDouble() - 0.5) * 0.2,
                        0.05,
                        (RANDOM.nextDouble() - 0.5) * 0.2
                    );
                    break;
            }
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityAngel entity) {
        return TEXTURE;
    }
}
