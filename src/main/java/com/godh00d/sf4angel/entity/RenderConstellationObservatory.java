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
    private static final int[] LAYOUT_PARENTS = layoutParents();
    private static final double CATALOG_CENTER_X = 111.0D;
    private static final double CATALOG_CENTER_Y = 120.0D;
    private static final double CATALOG_CENTER_Z = -22.5D;
    private static final double SCENE_SCALE = 0.41D;
    private static final double SKY_RADIUS = 14.0D;
    private static final float NODE_RADIUS = 0.48F;

    public RenderConstellationObservatory(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(EntityAngel angel, double x, double y, double z, float entityYaw, float partialTicks) {
        EntityPlayer player = Minecraft.getMinecraft().player;
        UUID owner = angel.getOwnerId();
        if (player == null || owner == null || !owner.equals(player.getUniqueID())) return;

        EntityConstellationObservatory observatory = (EntityConstellationObservatory) angel;
        byte[] states = ConstellationClientState.states();
        float animationTime = angel.ticksExisted + partialTicks;
        double entityX = angel.lastTickPosX + (angel.posX - angel.lastTickPosX) * partialTicks;
        double entityY = angel.lastTickPosY + (angel.posY - angel.lastTickPosY) * partialTicks;
        double entityZ = angel.lastTickPosZ + (angel.posZ - angel.lastTickPosZ) * partialTicks;
        double sceneX = x + observatory.getSceneX() - entityX;
        double sceneY = y + observatory.getSceneY() - entityY;
        double sceneZ = z + observatory.getSceneZ() - entityZ;
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawSkyShell();
        drawStars(animationTime);
        GlStateManager.pushMatrix();
        GlStateManager.translate(sceneX, sceneY, sceneZ);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        drawEdges(states, animationTime, 7.0F, 78, 48, 156, 54, true);
        drawEdges(states, animationTime, 2.5F, 92, 72, 168, 24, false);
        drawTendrilSparks(states, animationTime);
        drawMotes(animationTime);
        drawNodeAuras(states, animationTime);
        drawStarRays(states, animationTime, 5.0F, 1.45D, 48);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawEdges(states, animationTime, 2.0F, 164, 132, 238, 225, true);
        drawEdges(states, animationTime, 0.8F, 126, 108, 184, 105, false);
        drawNodes(states, animationTime);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        drawStarRays(states, animationTime, 1.5F, 1.0D, 210);
        GlStateManager.popMatrix();
        GL11.glPopAttrib();

        super.doRender(angel, x, y, z, entityYaw, partialTicks);
        int hovered = hoveredNode(observatory, player, states, partialTicks, animationTime);
        if (hovered >= 0) {
            String title = states[hovered] == ConstellationManager.MYSTERY
                ? "Unrevealed achievement" : NODES[hovered].title;
            renderNodeLabel(title, hovered, sceneX, sceneY, sceneZ, animationTime);
        }
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

    private static void drawStars(float animationTime) {
        GL11.glPointSize(1.35F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 320; i++) {
            double height = -1.0D + 2.0D * (i + 0.5D) / 320.0D;
            double radius = Math.sqrt(1.0D - height * height);
            double angle = i * 2.399963229728653D;
            double flicker = Math.sin(animationTime * (0.009D + i % 7 * 0.0013D) + i * 0.91D);
            int brightness = 145 + i * 73 % 60 + (int) ((flicker + 1.0D) * 22.0D);
            int alpha = 145 + (int) ((flicker + 1.0D) * 42.0D);
            vertex(buffer, Math.cos(angle) * radius * (SKY_RADIUS - 0.2D),
                height * (SKY_RADIUS - 0.2D), Math.sin(angle) * radius * (SKY_RADIUS - 0.2D),
                brightness, brightness - 12, 255, alpha);
        }
        tessellator.draw();
    }

    private static void drawEdges(byte[] states, float animationTime, float width,
                                  int red, int green, int blue, int alpha, boolean layoutEdges) {
        GL11.glLineWidth(width);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < NODES.length; i++) {
            if (!visible(states, i)) continue;
            AchievementConstellationCatalog.Node node = NODES[i];
            for (String parentId : node.parents) {
                Integer parentIndex = INDEXES.get(parentId);
                if (parentIndex == null || !visible(states, parentIndex)) continue;
                if ((parentIndex == LAYOUT_PARENTS[i]) != layoutEdges) continue;
                appendTendril(buffer, parentIndex, i, animationTime, red, green, blue, alpha);
            }
        }
        tessellator.draw();
    }

    private static int[] layoutParents() {
        int[] parents = new int[NODES.length];
        for (int i = 0; i < parents.length; i++) {
            parents[i] = -1;
            int highestY = Integer.MIN_VALUE;
            for (String parentId : NODES[i].parents) {
                Integer parentIndex = INDEXES.get(parentId);
                if (parentIndex != null && NODES[parentIndex].y > highestY) {
                    parents[i] = parentIndex;
                    highestY = NODES[parentIndex].y;
                }
            }
        }
        return parents;
    }

    private static void drawTendrilSparks(byte[] states, float animationTime) {
        GL11.glPointSize(2.4F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
        for (int childIndex = 0; childIndex < NODES.length; childIndex++) {
            int parentIndex = LAYOUT_PARENTS[childIndex];
            if (parentIndex < 0 || !visible(states, parentIndex) || !visible(states, childIndex)) continue;
            double seed = parentIndex * 1.731D + childIndex * 0.917D;
            double progress = (animationTime * 0.0045D + seed * 0.137D) % 1.0D;
            double startX = nodeX(parentIndex, animationTime);
            double startY = nodeY(parentIndex, animationTime);
            double startZ = nodeZ(parentIndex, animationTime);
            double endX = nodeX(childIndex, animationTime);
            double endY = nodeY(childIndex, animationTime);
            double endZ = nodeZ(childIndex, animationTime);
            double bendX = Math.sin(seed) * 0.72D;
            double bendY = 0.38D + Math.cos(seed * 0.71D) * 0.30D;
            double bendZ = Math.cos(seed) * 0.72D;
            double arc = Math.sin(progress * Math.PI);
            double ripple = Math.sin(progress * Math.PI * 2.0D + animationTime * 0.018D + seed)
                * 0.11D * arc;
            double x = startX + (endX - startX) * progress + bendX * arc + ripple * bendZ;
            double y = startY + (endY - startY) * progress + bendY * arc
                + Math.sin(progress * Math.PI * 3.0D + seed) * 0.07D * arc;
            double z = startZ + (endZ - startZ) * progress + bendZ * arc - ripple * bendX;
            vertex(buffer, x, y, z, 225, 205, 255, 210);
        }
        tessellator.draw();
    }

    private static void appendTendril(BufferBuilder buffer, int parentIndex, int childIndex,
                                      float animationTime, int red, int green, int blue, int alpha) {
        double startX = nodeX(parentIndex, animationTime);
        double startY = nodeY(parentIndex, animationTime);
        double startZ = nodeZ(parentIndex, animationTime);
        double endX = nodeX(childIndex, animationTime);
        double endY = nodeY(childIndex, animationTime);
        double endZ = nodeZ(childIndex, animationTime);
        double seed = parentIndex * 1.731D + childIndex * 0.917D;
        double bendX = Math.sin(seed) * 0.72D;
        double bendY = 0.38D + Math.cos(seed * 0.71D) * 0.30D;
        double bendZ = Math.cos(seed) * 0.72D;

        for (int segment = 0; segment < 16; segment++) {
            appendTendrilVertex(buffer, segment / 16.0D, startX, startY, startZ, endX, endY, endZ,
                bendX, bendY, bendZ, seed, animationTime, red, green, blue, alpha);
            appendTendrilVertex(buffer, (segment + 1) / 16.0D, startX, startY, startZ, endX, endY, endZ,
                bendX, bendY, bendZ, seed, animationTime, red, green, blue, alpha);
        }
    }

    private static void appendTendrilVertex(BufferBuilder buffer, double progress,
                                             double startX, double startY, double startZ,
                                             double endX, double endY, double endZ,
                                             double bendX, double bendY, double bendZ, double seed,
                                             float animationTime, int red, int green, int blue, int alpha) {
        double arc = Math.sin(progress * Math.PI);
        double ripple = Math.sin(progress * Math.PI * 2.0D + animationTime * 0.018D + seed) * 0.11D * arc;
        double x = startX + (endX - startX) * progress + bendX * arc + ripple * bendZ;
        double y = startY + (endY - startY) * progress + bendY * arc
            + Math.sin(progress * Math.PI * 3.0D + seed) * 0.07D * arc;
        double z = startZ + (endZ - startZ) * progress + bendZ * arc - ripple * bendX;
        int flowingAlpha = (int) (alpha * (0.86D
            + Math.sin(animationTime * 0.012D - progress * 4.0D + seed) * 0.10D));
        vertex(buffer, x, y, z, red, green, blue, flowingAlpha);
    }

    private static void drawNodes(byte[] states, float animationTime) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < NODES.length; i++) {
            if (!visible(states, i)) continue;
            int state = states[i];
            int red = state == ConstellationManager.COMPLETED ? 45
                : state == ConstellationManager.AVAILABLE ? 238 : 66;
            int green = state == ConstellationManager.COMPLETED ? 183
                : state == ConstellationManager.AVAILABLE ? 177 : 153;
            int blue = state == ConstellationManager.COMPLETED ? 112
                : state == ConstellationManager.AVAILABLE ? 57 : 225;
            double pulse = starPulse(i, animationTime);
            star(buffer, nodeX(i, animationTime), nodeY(i, animationTime), nodeZ(i, animationTime),
                NODE_RADIUS * 0.62D * pulse, red, green, blue, 255);
        }
        tessellator.draw();
    }

    private static void drawNodeAuras(byte[] states, float animationTime) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < NODES.length; i++) {
            if (!visible(states, i)) continue;
            int state = states[i];
            int red = state == ConstellationManager.COMPLETED ? 45
                : state == ConstellationManager.AVAILABLE ? 238 : 66;
            int green = state == ConstellationManager.COMPLETED ? 183
                : state == ConstellationManager.AVAILABLE ? 177 : 153;
            int blue = state == ConstellationManager.COMPLETED ? 112
                : state == ConstellationManager.AVAILABLE ? 57 : 225;
            double breath = (2.0D + Math.sin(animationTime * 0.025D + i * 0.67D) * 0.16D)
                * starPulse(i, animationTime);
            star(buffer, nodeX(i, animationTime), nodeY(i, animationTime), nodeZ(i, animationTime),
                NODE_RADIUS * breath,
                red, green, blue, 42);
        }
        tessellator.draw();
    }

    private static void drawStarRays(byte[] states, float animationTime, float width,
                                     double sizeMultiplier, int alpha) {
        GL11.glLineWidth(width);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < NODES.length; i++) {
            if (!visible(states, i)) continue;
            int state = states[i];
            int red = state == ConstellationManager.COMPLETED ? 76
                : state == ConstellationManager.AVAILABLE ? 255 : 116;
            int green = state == ConstellationManager.COMPLETED ? 226
                : state == ConstellationManager.AVAILABLE ? 214 : 190;
            int blue = state == ConstellationManager.COMPLETED ? 154
                : state == ConstellationManager.AVAILABLE ? 104 : 255;
            double x = nodeX(i, animationTime);
            double y = nodeY(i, animationTime);
            double z = nodeZ(i, animationTime);
            double radius = NODE_RADIUS * 2.45D * starPulse(i, animationTime) * sizeMultiplier;
            ray(buffer, x, y, z, radius, 1.0D, 0.0D, 0.0D, red, green, blue, alpha);
            ray(buffer, x, y, z, radius, 0.0D, 1.0D, 0.0D, red, green, blue, alpha);
            ray(buffer, x, y, z, radius, 0.0D, 0.0D, 1.0D, red, green, blue, alpha);
            double diagonal = 0.5773502691896258D;
            ray(buffer, x, y, z, radius, diagonal, diagonal, diagonal, red, green, blue, alpha);
            ray(buffer, x, y, z, radius, diagonal, diagonal, -diagonal, red, green, blue, alpha);
            ray(buffer, x, y, z, radius, diagonal, -diagonal, diagonal, red, green, blue, alpha);
            ray(buffer, x, y, z, radius, -diagonal, diagonal, diagonal, red, green, blue, alpha);
        }
        tessellator.draw();
    }

    private static void ray(BufferBuilder buffer, double x, double y, double z, double radius,
                            double directionX, double directionY, double directionZ,
                            int red, int green, int blue, int alpha) {
        vertex(buffer, x - directionX * radius, y - directionY * radius, z - directionZ * radius,
            red, green, blue, alpha);
        vertex(buffer, x + directionX * radius, y + directionY * radius, z + directionZ * radius,
            red, green, blue, alpha);
    }

    private static double starPulse(int index, float animationTime) {
        return 1.0D + Math.sin(animationTime * 0.040D + index * 0.73D) * 0.10D
            + Math.sin(animationTime * 0.017D + index * 1.31D) * 0.04D;
    }

    private static boolean visible(byte[] states, int index) {
        return states.length == NODES.length && states[index] != ConstellationManager.ABSENT;
    }

    private static void drawMotes(float animationTime) {
        GL11.glPointSize(2.2F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_POINTS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 56; i++) {
            double cycle = (animationTime * 0.0015D + i * 0.61803398875D) % 1.0D;
            double x = Math.sin(i * 12.9898D) * 30.0D;
            double y = -30.0D + cycle * 60.0D;
            double z = Math.cos(i * 7.233D) * 22.0D;
            int alpha = (int) (Math.sin(cycle * Math.PI) * 105.0D);
            vertex(buffer, x, y, z, 158, 116, 244, alpha);
        }
        tessellator.draw();
    }

    private static void star(BufferBuilder buffer, double x, double y, double z, double radius,
                             int red, int green, int blue, int alpha) {
        double tip = radius * 1.65D;
        double waist = radius * 1.25D;
        triangle(buffer, x, y + tip, z, x + waist, y, z, x, y, z + waist, red, green, blue, alpha);
        triangle(buffer, x, y + tip, z, x, y, z + waist, x - waist, y, z, red, green, blue, alpha);
        triangle(buffer, x, y + tip, z, x - waist, y, z, x, y, z - waist, red, green, blue, alpha);
        triangle(buffer, x, y + tip, z, x, y, z - waist, x + waist, y, z, red, green, blue, alpha);
        triangle(buffer, x, y - tip, z, x, y, z + waist, x + waist, y, z, red, green, blue, alpha);
        triangle(buffer, x, y - tip, z, x - waist, y, z, x, y, z + waist, red, green, blue, alpha);
        triangle(buffer, x, y - tip, z, x, y, z - waist, x - waist, y, z, red, green, blue, alpha);
        triangle(buffer, x, y - tip, z, x + waist, y, z, x, y, z - waist, red, green, blue, alpha);
    }

    private static void triangle(BufferBuilder buffer, double x1, double y1, double z1,
                                 double x2, double y2, double z2, double x3, double y3, double z3,
                                 int red, int green, int blue, int alpha) {
        vertex(buffer, x1, y1, z1, red, green, blue, alpha);
        vertex(buffer, x2, y2, z2, red, green, blue, alpha);
        vertex(buffer, x3, y3, z3, red, green, blue, alpha);
    }

    private static void vertex(BufferBuilder buffer, double x, double y, double z,
                               int red, int green, int blue, int alpha) {
        buffer.pos(x, y, z).color(red, green, blue, alpha).endVertex();
    }

    private static double nodeX(int index, float animationTime) {
        AchievementConstellationCatalog.Node node = NODES[index];
        return (node.x - CATALOG_CENTER_X) * SCENE_SCALE
            + Math.sin(animationTime * 0.011D + index * 2.17D) * 0.05D;
    }

    private static double nodeY(int index, float animationTime) {
        AchievementConstellationCatalog.Node node = NODES[index];
        return (node.y - CATALOG_CENTER_Y) * SCENE_SCALE
            + Math.sin(animationTime * 0.008D + index * 1.37D) * 0.07D;
    }

    private static double nodeZ(int index, float animationTime) {
        AchievementConstellationCatalog.Node node = NODES[index];
        return (node.z - CATALOG_CENTER_Z) * SCENE_SCALE
            + Math.cos(animationTime * 0.010D + index * 1.79D) * 0.05D;
    }

    private static int hoveredNode(EntityConstellationObservatory observatory, EntityPlayer player,
                                   byte[] states, float partialTicks, float animationTime) {
        Vec3d eye = player.getPositionEyes(partialTicks);
        Vec3d look = player.getLook(partialTicks);
        int nearest = -1;
        double nearestDistance = Double.MAX_VALUE;
        for (int i = 0; i < NODES.length; i++) {
            if (!visible(states, i)) continue;
            Vec3d offset = new Vec3d(
                observatory.getSceneX() + nodeX(i, animationTime) - eye.x,
                observatory.getSceneY() + nodeY(i, animationTime) - eye.y,
                observatory.getSceneZ() + nodeZ(i, animationTime) - eye.z);
            double alongRay = offset.dotProduct(look);
            if (alongRay < 0.0D || alongRay > 180.0D) continue;
            double missDistance = offset.lengthSquared() - alongRay * alongRay;
            if (missDistance < 0.64D && missDistance < nearestDistance) {
                nearestDistance = missDistance;
                nearest = i;
            }
        }
        return nearest;
    }

    private void renderNodeLabel(String title, int index,
                                 double sceneX, double sceneY, double sceneZ, float animationTime) {
        FontRenderer font = getFontRendererFromRenderManager();
        float scale = 0.025F;
        GlStateManager.pushMatrix();
        GlStateManager.translate(sceneX + nodeX(index, animationTime),
            sceneY + nodeY(index, animationTime) + 1.05D,
            sceneZ + nodeZ(index, animationTime));
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
        int halfWidth = font.getStringWidth(title) / 2;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(-halfWidth - 2, -2, 0.0D).color(10, 18, 30, 190).endVertex();
        buffer.pos(-halfWidth - 2, 10, 0.0D).color(10, 18, 30, 190).endVertex();
        buffer.pos(halfWidth + 2, 10, 0.0D).color(10, 18, 30, 190).endVertex();
        buffer.pos(halfWidth + 2, -2, 0.0D).color(10, 18, 30, 190).endVertex();
        tessellator.draw();
        font.drawString(title, -halfWidth, 0, 0xFFF7FBFF);
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
