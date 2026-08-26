package com.godh00d.sf4angel.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Random;

public class EntityAngelRender extends Render<EntityAngel> {

    private static final ResourceLocation TEXTURE = new ResourceLocation("sf4angel", "textures/entity/angel.png");
    private static final Random RANDOM = new Random();

    public EntityAngelRender(RenderManager renderManager) {
        super(renderManager);
        this.shadowOpaque = 0.0F;
    }

    @Override
    public void doRender(EntityAngel angel, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y + 0.5, z);

        float scale = angel.getRenderScale();
        float spin = angel.getRenderSpin();

        GlStateManager.scale(scale, scale, scale);
        GlStateManager.rotate(spin, 0, 1, 0);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableCull();

        renderBody(angel);
        renderHalo(angel);
        spawnParticles(angel);

        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderBody(EntityAngel angel) {
        float timer = (float) angel.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
        float bob = MathHelper.sin(timer * 0.05F) * 0.1F;

        GlStateManager.translate(0, bob, 0);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();

        float r = 1.0F, g = 1.0F, b = 1.0F, a = 0.9F;

        int state = angel.getVisualState();
        if (state == EntityAngel.STATE_SPAWNING || state == EntityAngel.STATE_DESPAWNING) {
            a = 0.5F + angel.getRenderScale() * 0.4F;
        }

        GlStateManager.color(r, g, b, a);

        float s = 0.7F;
        float hs = s / 2.0F;

        buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        // Front face
        buf.pos(-hs, -hs, hs).tex(0, 0).endVertex();
        buf.pos(hs, -hs, hs).tex(1, 0).endVertex();
        buf.pos(hs, hs, hs).tex(1, 1).endVertex();
        buf.pos(-hs, hs, hs).tex(0, 1).endVertex();

        // Back face
        buf.pos(hs, -hs, -hs).tex(0, 0).endVertex();
        buf.pos(-hs, -hs, -hs).tex(1, 0).endVertex();
        buf.pos(-hs, hs, -hs).tex(1, 1).endVertex();
        buf.pos(hs, hs, -hs).tex(0, 1).endVertex();

        // Left face
        buf.pos(-hs, -hs, -hs).tex(0, 0).endVertex();
        buf.pos(-hs, -hs, hs).tex(1, 0).endVertex();
        buf.pos(-hs, hs, hs).tex(1, 1).endVertex();
        buf.pos(-hs, hs, -hs).tex(0, 1).endVertex();

        // Right face
        buf.pos(hs, -hs, hs).tex(0, 0).endVertex();
        buf.pos(hs, -hs, -hs).tex(1, 0).endVertex();
        buf.pos(hs, hs, -hs).tex(1, 1).endVertex();
        buf.pos(hs, hs, hs).tex(0, 1).endVertex();

        // Top face
        buf.pos(-hs, hs, hs).tex(0, 0).endVertex();
        buf.pos(hs, hs, hs).tex(1, 0).endVertex();
        buf.pos(hs, hs, -hs).tex(1, 1).endVertex();
        buf.pos(-hs, hs, -hs).tex(0, 1).endVertex();

        // Bottom face
        buf.pos(-hs, -hs, -hs).tex(0, 0).endVertex();
        buf.pos(hs, -hs, -hs).tex(1, 0).endVertex();
        buf.pos(hs, -hs, hs).tex(1, 1).endVertex();
        buf.pos(-hs, -hs, hs).tex(0, 1).endVertex();

        tess.draw();

        GlStateManager.translate(0, -bob, 0);
    }

    private void renderHalo(EntityAngel angel) {
        float haloAngle = angel.getHaloAngle();
        float timer = (float) angel.ticksExisted + Minecraft.getMinecraft().getRenderPartialTicks();
        float bob = MathHelper.sin(timer * 0.05F) * 0.1F;

        GlStateManager.translate(0, bob + 0.45F, 0);
        GlStateManager.rotate(90, 1, 0, 0);
        GlStateManager.rotate((float) Math.toDegrees(haloAngle), 0, 0, 1);

        GlStateManager.disableTexture2D();
        GlStateManager.color(1.0F, 0.85F, 0.0F, 0.9F);

        Tessellator tess = Tessellator.getInstance();
        BufferBuilder buf = tess.getBuffer();
        buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);

        float radius = 0.45F;
        int segments = 32;

        for (int i = 0; i <= segments; i++) {
            float angle = (float) (2.0 * Math.PI * i / segments);
            float hx = MathHelper.cos(angle) * radius;
            float hy = MathHelper.sin(angle) * radius;
            buf.pos(hx, hy, 0).endVertex();
        }

        tess.draw();

        // Second ring for thickness
        buf.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION);
        float radius2 = 0.42F;
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (2.0 * Math.PI * i / segments);
            float hx = MathHelper.cos(angle) * radius2;
            float hy = MathHelper.sin(angle) * radius2;
            buf.pos(hx, hy, 0).endVertex();
        }
        tess.draw();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
    }

    private void spawnParticles(EntityAngel angel) {
        if (!angel.world.isRemote) return;
        if (angel.getVisualState() != EntityAngel.STATE_VISIBLE) return;

        int count = RANDOM.nextInt(3);
        for (int i = 0; i < count; i++) {
            double px = angel.posX + (RANDOM.nextDouble() - 0.5) * 2;
            double py = angel.posY + RANDOM.nextDouble() * 2;
            double pz = angel.posZ + (RANDOM.nextDouble() - 0.5) * 2;

            int type = RANDOM.nextInt(5);
            switch (type) {
                case 0:
                    angel.world.spawnParticle(
                        net.minecraft.util.EnumParticleTypes.END_ROD,
                        px, py, pz, 0, 0.02, 0
                    );
                    break;
                case 1:
                    angel.world.spawnParticle(
                        net.minecraft.util.EnumParticleTypes.ENCHANTMENT_TABLE,
                        px, py, pz,
                        (RANDOM.nextDouble() - 0.5) * 0.5,
                        0.1,
                        (RANDOM.nextDouble() - 0.5) * 0.5
                    );
                    break;
                case 2:
                    angel.world.spawnParticle(
                        net.minecraft.util.EnumParticleTypes.VILLAGER_HAPPY,
                        px, py, pz, 0, 0, 0
                    );
                    break;
                case 3:
                    angel.world.spawnParticle(
                        net.minecraft.util.EnumParticleTypes.CLOUD,
                        px, py, pz,
                        (RANDOM.nextDouble() - 0.5) * 0.1,
                        0.05,
                        (RANDOM.nextDouble() - 0.5) * 0.1
                    );
                    break;
                case 4:
                    angel.world.spawnParticle(
                        net.minecraft.util.EnumParticleTypes.FIREWORKS_SPARK,
                        px, py, pz,
                        (RANDOM.nextDouble() - 0.5) * 0.3,
                        0.1,
                        (RANDOM.nextDouble() - 0.5) * 0.3
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
