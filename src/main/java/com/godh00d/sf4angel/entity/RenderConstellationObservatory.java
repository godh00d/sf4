package com.godh00d.sf4angel.entity;

import com.godh00d.sf4angel.client.ConstellationClientState;
import com.godh00d.sf4angel.constellation.AchievementConstellationCatalog;
import com.godh00d.sf4angel.constellation.ConstellationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.UUID;

public final class RenderConstellationObservatory extends EntityAngelRender {

    private static final AchievementConstellationCatalog.Node[] NODES = AchievementConstellationCatalog.nodes();
    private static final Map<String, Integer> INDEXES = AchievementConstellationCatalog.indexes();
    private static final double CATALOG_CENTER_X = 41.0D;
    private static final double CATALOG_CENTER_Y = 88.0D;
    private static final double CATALOG_CENTER_Z = 0.0D;
    private static final double SCENE_SCALE = 0.15D;
    private static final double SKY_RADIUS = 14.0D;
    private static final float NODE_RADIUS = 0.30F;

    public RenderConstellationObservatory(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityAngel angel, double x, double y, double z, float entityYaw, float partialTicks) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        UUID owner = angel.getOwnerId();
        if (player == null || owner == null || !owner.equals(player.getUniqueID())) return;

        byte[] states = ConstellationClientState.states();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawSkyShell();
        drawStars();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + EntityConstellationObservatory.SCENE_OFFSET_X, y,
            z + EntityConstellationObservatory.SCENE_OFFSET_Z);
        drawEdges(states);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        drawNodeAuras(states);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawNodes(states);
        GlStateManager.popMatrix();
        GL11.glPopAttrib();

        super.doRender(angel, x, y, z, entityYaw, partialTicks);
        int hovered = hoveredNode(angel, player, states, partialTicks);
        if (hovered >= 0) renderNodeLabel(angel, NODES[hovered], x, y, z);
    }

    private static void drawSkyShell() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        double r = SKY_RADIUS;
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        skyQuad(buffer, -r, -r, -r, -r, r, -r, r, r, -r, r, -r, -r);
        skyQuad(buffer, r, -r, r, r, r, r, -r, r, r, -r, -r, r);
        skyQuad(buffer, -r, -r, r, -r, -r, -r, r, -r, -r, r, -r, r);
        skyQuad(buffer, -r, r, -r, -r, r, r, r, r, r, r, r, -r);
        skyQuad(buffer, -r, -r, r, -r, r, r, -r, r, -r, -r, -r, -r);
        skyQuad(buffer, r, -r, -r, r, r, -r, r, r, r, r, -r, r);
        tessellator.draw();
    }

    private static void skyQuad(BufferBuilder buffer, double x1, double y1, double z1,
                                double x2, double y2, double z2, double x3, double y3, double z3,
                                double x4, double y4, double z4) {
        skyVertex(buffer, x1, y1, z1);
        skyVertex(buffer, x2, y2, z2);
        skyVertex(buffer, x3, y3, z3);
        skyVertex(buffer, x4, y4, z4);
    }

    private static void skyVertex(BufferBuilder buffer, double x, double y, double z) {
        double height = (y / SKY_RADIUS + 1.0D) * 0.5D;
        int red = (int) (6.0D + height * 28.0D);
        int green = (int) (7.0D + height * 10.0D);
        int blue = (int) (24.0D + height * 48.0D);
        vertex(buffer, x, y, z, red, green, blue, 255);
    }

    private static void drawStars() {
        GL11.glPointSize(1.8F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 128; i++) {
            double height = -1.0D + 2.0D * (i + 0.5D) / 128.0D;
            double radius = Math.sqrt(1.0D - height * height);
            double angle = i * 2.399963229728653D;
            int brightness = 155 + i * 73 % 100;
            vertex(buffer, Math.cos(angle) * radius * (SKY_RADIUS - 0.2D),
                height * (SKY_RADIUS - 0.2D), Math.sin(angle) * radius * (SKY_RADIUS - 0.2D),
                brightness, brightness - 12, 255, 210);
        }
        tessellator.draw();
    }

    private static void drawEdges(byte[] states) {
        GL11.glLineWidth(1.7F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < NODES.length; i++) {
            if (!visible(states, i)) continue;
            AchievementConstellationCatalog.Node node = NODES[i];
            for (String parentId : node.parents) {
                Integer parentIndex = INDEXES.get(parentId);
                if (parentIndex == null || !visible(states, parentIndex)) continue;
                AchievementConstellationCatalog.Node parent = NODES[parentIndex];
                nodeVertex(buffer, parent, 130, 104, 218, 210);
                nodeVertex(buffer, node, 130, 104, 218, 210);
            }
        }
        tessellator.draw();
    }

    private static void drawNodes(byte[] states) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < NODES.length; i++) {
            if (!visible(states, i)) continue;
            int state = states.length == NODES.length ? states[i] : ConstellationManager.MYSTERY;
            int red = state == ConstellationManager.COMPLETED ? 45
                : state == ConstellationManager.AVAILABLE ? 238 : 66;
            int green = state == ConstellationManager.COMPLETED ? 183
                : state == ConstellationManager.AVAILABLE ? 177 : 153;
            int blue = state == ConstellationManager.COMPLETED ? 112
                : state == ConstellationManager.AVAILABLE ? 57 : 225;
            AchievementConstellationCatalog.Node node = NODES[i];
            box(buffer, localX(node), localY(node), localZ(node), NODE_RADIUS, red, green, blue);
        }
        tessellator.draw();
    }

    private static void drawNodeAuras(byte[] states) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < NODES.length; i++) {
            if (!visible(states, i)) continue;
            int state = states.length == NODES.length ? states[i] : ConstellationManager.MYSTERY;
            int red = state == ConstellationManager.COMPLETED ? 45
                : state == ConstellationManager.AVAILABLE ? 238 : 66;
            int green = state == ConstellationManager.COMPLETED ? 183
                : state == ConstellationManager.AVAILABLE ? 177 : 153;
            int blue = state == ConstellationManager.COMPLETED ? 112
                : state == ConstellationManager.AVAILABLE ? 57 : 225;
            AchievementConstellationCatalog.Node node = NODES[i];
            box(buffer, localX(node), localY(node), localZ(node), NODE_RADIUS * 2.2F,
                red, green, blue, 42);
        }
        tessellator.draw();
    }

    private static boolean visible(byte[] states, int index) {
        return states.length != NODES.length || states[index] != ConstellationManager.ABSENT;
    }

    private static void nodeVertex(BufferBuilder buffer, AchievementConstellationCatalog.Node node,
                                   int red, int green, int blue, int alpha) {
        vertex(buffer, localX(node), localY(node), localZ(node), red, green, blue, alpha);
    }

    private static void box(BufferBuilder buffer, double x, double y, double z, double radius,
                            int red, int green, int blue) {
        box(buffer, x, y, z, radius, red, green, blue, 255);
    }

    private static void box(BufferBuilder buffer, double x, double y, double z, double radius,
                            int red, int green, int blue, int alpha) {
        double x0 = x - radius;
        double x1 = x + radius;
        double y0 = y - radius;
        double y1 = y + radius;
        double z0 = z - radius;
        double z1 = z + radius;
        face(buffer, x0, y0, z0, x1, y1, z0, red, green, blue, alpha);
        face(buffer, x1, y0, z1, x0, y1, z1, red, green, blue, alpha);
        face(buffer, x0, y0, z1, x0, y1, z0, red, green, blue, alpha);
        face(buffer, x1, y0, z0, x1, y1, z1, red, green, blue, alpha);
        faceHorizontal(buffer, x0, y0, z1, x1, z0, red, green, blue, alpha);
        faceHorizontal(buffer, x0, y1, z0, x1, z1, red, green, blue, alpha);
    }

    private static void face(BufferBuilder buffer, double x0, double y0, double z0,
                             double x1, double y1, double z1, int red, int green, int blue, int alpha) {
        vertex(buffer, x0, y0, z0, red, green, blue, alpha);
        vertex(buffer, x1, y0, z1, red, green, blue, alpha);
        vertex(buffer, x1, y1, z1, red, green, blue, alpha);
        vertex(buffer, x0, y1, z0, red, green, blue, alpha);
    }

    private static void faceHorizontal(BufferBuilder buffer, double x0, double y, double z0,
                                       double x1, double z1, int red, int green, int blue, int alpha) {
        vertex(buffer, x0, y, z0, red, green, blue, alpha);
        vertex(buffer, x1, y, z0, red, green, blue, alpha);
        vertex(buffer, x1, y, z1, red, green, blue, alpha);
        vertex(buffer, x0, y, z1, red, green, blue, alpha);
    }

    private static void vertex(BufferBuilder buffer, double x, double y, double z,
                               int red, int green, int blue, int alpha) {
        buffer.pos(x, y, z).color(red, green, blue, alpha).endVertex();
    }

    private static double localX(AchievementConstellationCatalog.Node node) {
        return (node.x - CATALOG_CENTER_X) * SCENE_SCALE;
    }

    private static double localY(AchievementConstellationCatalog.Node node) {
        return (node.y - CATALOG_CENTER_Y) * SCENE_SCALE;
    }

    private static double localZ(AchievementConstellationCatalog.Node node) {
        return (node.z - CATALOG_CENTER_Z) * SCENE_SCALE;
    }

    private static int hoveredNode(EntityAngel angel, EntityPlayer player, byte[] states, float partialTicks) {
        Vec3d eye = player.getPositionEyes(partialTicks);
        Vec3d look = player.getLook(partialTicks);
        double entityX = angel.lastTickPosX + (angel.posX - angel.lastTickPosX) * partialTicks;
        double entityY = angel.lastTickPosY + (angel.posY - angel.lastTickPosY) * partialTicks;
        double entityZ = angel.lastTickPosZ + (angel.posZ - angel.lastTickPosZ) * partialTicks;
        int nearest = -1;
        double nearestDistance = Double.MAX_VALUE;
        for (int i = 0; i < NODES.length; i++) {
            if (!visible(states, i)) continue;
            AchievementConstellationCatalog.Node node = NODES[i];
            Vec3d offset = new Vec3d(
                entityX + EntityConstellationObservatory.SCENE_OFFSET_X + localX(node) - eye.x,
                entityY + localY(node) - eye.y,
                entityZ + EntityConstellationObservatory.SCENE_OFFSET_Z + localZ(node) - eye.z);
            double alongRay = offset.dotProduct(look);
            if (alongRay < 0.0D || alongRay > 180.0D) continue;
            double missDistance = offset.lengthSquared() - alongRay * alongRay;
            if (missDistance < 0.36D && missDistance < nearestDistance) {
                nearestDistance = missDistance;
                nearest = i;
            }
        }
        return nearest;
    }

    private void renderNodeLabel(EntityAngel angel, AchievementConstellationCatalog.Node node,
                                 double x, double y, double z) {
        FontRenderer font = getFontRendererFromRenderManager();
        float scale = 0.025F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + EntityConstellationObservatory.SCENE_OFFSET_X + localX(node),
            y + localY(node) + 1.15D,
            z + EntityConstellationObservatory.SCENE_OFFSET_Z + localZ(node));
        GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate((renderManager.options.thirdPersonView == 2 ? -1 : 1) * renderManager.playerViewX,
            1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO);
        int halfWidth = font.getStringWidth(node.title) / 2;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(-halfWidth - 2, -2, 0.0D).color(10, 18, 30, 190).endVertex();
        buffer.pos(-halfWidth - 2, 10, 0.0D).color(10, 18, 30, 190).endVertex();
        buffer.pos(halfWidth + 2, 10, 0.0D).color(10, 18, 30, 190).endVertex();
        buffer.pos(halfWidth + 2, -2, 0.0D).color(10, 18, 30, 190).endVertex();
        tessellator.draw();
        font.drawString(node.title, -halfWidth, 0, 0xFFF7FBFF);
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
