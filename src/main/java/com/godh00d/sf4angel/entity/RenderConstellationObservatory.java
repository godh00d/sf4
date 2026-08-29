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
    private static final double ROOM_RADIUS = 112.0D;
    private static final float NODE_RADIUS = 0.62F;

    public RenderConstellationObservatory(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityAngel angel, double x, double y, double z, float entityYaw, float partialTicks) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        UUID owner = angel.getOwnerId();
        if (player == null || owner == null || !owner.equals(player.getUniqueID())) return;

        byte[] states = ConstellationClientState.states();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + EntityConstellationObservatory.SCENE_OFFSET_X, y,
            z + EntityConstellationObservatory.SCENE_OFFSET_Z);
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawRoom();
        drawEdges(states);
        drawNodes(states);
        GL11.glPopAttrib();
        GlStateManager.popMatrix();

        super.doRender(angel, x, y, z, entityYaw, partialTicks);
        int hovered = hoveredNode(angel, player, states, partialTicks);
        if (hovered >= 0) renderNodeLabel(angel, NODES[hovered], x, y, z);
    }

    private static void drawRoom() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        double r = ROOM_RADIUS;
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        quad(buffer, -r, -r, -r, -r, r, -r, r, r, -r, r, -r, -r);
        quad(buffer, r, -r, r, r, r, r, -r, r, r, -r, -r, r);
        quad(buffer, -r, -r, r, -r, -r, -r, r, -r, -r, r, -r, r);
        quad(buffer, -r, r, -r, -r, r, r, r, r, r, r, r, -r);
        quad(buffer, -r, -r, r, -r, r, r, -r, r, -r, -r, -r, -r);
        quad(buffer, r, -r, -r, r, r, -r, r, r, r, r, -r, r);
        tessellator.draw();
    }

    private static void quad(BufferBuilder buffer, double x1, double y1, double z1,
                             double x2, double y2, double z2, double x3, double y3, double z3,
                             double x4, double y4, double z4) {
        vertex(buffer, x1, y1, z1, 242, 246, 255, 255);
        vertex(buffer, x2, y2, z2, 242, 246, 255, 255);
        vertex(buffer, x3, y3, z3, 242, 246, 255, 255);
        vertex(buffer, x4, y4, z4, 242, 246, 255, 255);
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
                nodeVertex(buffer, parent, 55, 72, 96, 190);
                nodeVertex(buffer, node, 55, 72, 96, 190);
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

    private static boolean visible(byte[] states, int index) {
        return states.length != NODES.length || states[index] != ConstellationManager.ABSENT;
    }

    private static void nodeVertex(BufferBuilder buffer, AchievementConstellationCatalog.Node node,
                                   int red, int green, int blue, int alpha) {
        vertex(buffer, localX(node), localY(node), localZ(node), red, green, blue, alpha);
    }

    private static void box(BufferBuilder buffer, double x, double y, double z, double radius,
                            int red, int green, int blue) {
        double x0 = x - radius;
        double x1 = x + radius;
        double y0 = y - radius;
        double y1 = y + radius;
        double z0 = z - radius;
        double z1 = z + radius;
        face(buffer, x0, y0, z0, x1, y1, z0, red, green, blue);
        face(buffer, x1, y0, z1, x0, y1, z1, red, green, blue);
        face(buffer, x0, y0, z1, x0, y1, z0, red, green, blue);
        face(buffer, x1, y0, z0, x1, y1, z1, red, green, blue);
        faceHorizontal(buffer, x0, y0, z1, x1, z0, red, green, blue);
        faceHorizontal(buffer, x0, y1, z0, x1, z1, red, green, blue);
    }

    private static void face(BufferBuilder buffer, double x0, double y0, double z0,
                             double x1, double y1, double z1, int red, int green, int blue) {
        vertex(buffer, x0, y0, z0, red, green, blue, 255);
        vertex(buffer, x1, y0, z1, red, green, blue, 255);
        vertex(buffer, x1, y1, z1, red, green, blue, 255);
        vertex(buffer, x0, y1, z0, red, green, blue, 255);
    }

    private static void faceHorizontal(BufferBuilder buffer, double x0, double y, double z0,
                                       double x1, double z1, int red, int green, int blue) {
        vertex(buffer, x0, y, z0, red, green, blue, 255);
        vertex(buffer, x1, y, z0, red, green, blue, 255);
        vertex(buffer, x1, y, z1, red, green, blue, 255);
        vertex(buffer, x0, y, z1, red, green, blue, 255);
    }

    private static void vertex(BufferBuilder buffer, double x, double y, double z,
                               int red, int green, int blue, int alpha) {
        buffer.pos(x, y, z).color(red, green, blue, alpha).endVertex();
    }

    private static double localX(AchievementConstellationCatalog.Node node) {
        return node.x - CATALOG_CENTER_X;
    }

    private static double localY(AchievementConstellationCatalog.Node node) {
        return node.y - CATALOG_CENTER_Y;
    }

    private static double localZ(AchievementConstellationCatalog.Node node) {
        return node.z - CATALOG_CENTER_Z;
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
            if (missDistance < 1.1D && missDistance < nearestDistance) {
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
