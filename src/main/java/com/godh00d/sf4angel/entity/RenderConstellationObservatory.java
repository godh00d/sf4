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
    private static final double CATALOG_CENTER_X = 190.0D;
    private static final double CATALOG_CENTER_Y = 184.0D;
    private static final double CATALOG_CENTER_Z = -32.0D;
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
        double cameraX = -sceneX;
        double cameraY = -sceneY;
        double cameraZ = -sceneZ;
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        drawTendrilRibbons(states, animationTime, cameraX, cameraY, cameraZ,
            0.34D, 78, 48, 156, 32, true);
        drawTendrilRibbons(states, animationTime, cameraX, cameraY, cameraZ,
            0.15D, 92, 72, 168, 15, false);
        drawTendrilRibbons(states, animationTime, cameraX, cameraY, cameraZ,
            0.105D, 188, 150, 255, 195, true);
        drawTendrilRibbons(states, animationTime, cameraX, cameraY, cameraZ,
            0.045D, 138, 118, 196, 88, false);
        drawMagicParticles(states, animationTime, cameraX, cameraY, cameraZ);
        drawStarGlows(states, animationTime, cameraX, cameraY, cameraZ);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawNodes(states, animationTime);
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

    private static void drawTendrilRibbons(byte[] states, float animationTime,
                                            double cameraX, double cameraY, double cameraZ,
                                            double width, int red, int green, int blue, int alpha,
                                            boolean layoutEdges) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int childIndex = 0; childIndex < NODES.length; childIndex++) {
            if (!visible(states, childIndex)) continue;
            for (String parentId : NODES[childIndex].parents) {
                Integer parentIndex = INDEXES.get(parentId);
                if (parentIndex == null || !visible(states, parentIndex)) continue;
                if ((parentIndex == LAYOUT_PARENTS[childIndex]) != layoutEdges) continue;
                int edgeAlpha = states[childIndex] == ConstellationManager.MYSTERY
                    ? (int) (alpha * 0.48D) : alpha;
                appendTendrilRibbon(buffer, parentIndex, childIndex, animationTime,
                    cameraX, cameraY, cameraZ, width, red, green, blue, edgeAlpha);
            }
        }
        tessellator.draw();
    }

    private static void appendTendrilRibbon(BufferBuilder buffer, int parentIndex, int childIndex,
                                             float animationTime, double cameraX, double cameraY,
                                             double cameraZ, double width,
                                             int red, int green, int blue, int alpha) {
        double startX = nodeX(parentIndex, animationTime);
        double startY = nodeY(parentIndex, animationTime);
        double startZ = nodeZ(parentIndex, animationTime);
        double endX = nodeX(childIndex, animationTime);
        double endY = nodeY(childIndex, animationTime);
        double endZ = nodeZ(childIndex, animationTime);
        double seed = parentIndex * 1.731D + childIndex * 0.917D;
        double bendX = Math.sin(seed) * 0.92D;
        double bendY = 0.55D + Math.cos(seed * 0.71D) * 0.38D;
        double bendZ = Math.cos(seed) * 0.92D;
        double[] first = new double[3];
        double[] second = new double[3];
        for (int segment = 0; segment < 16; segment++) {
            double firstProgress = segment / 16.0D;
            double secondProgress = (segment + 1) / 16.0D;
            sampleTendril(first, firstProgress, startX, startY, startZ, endX, endY, endZ,
                bendX, bendY, bendZ, seed, animationTime);
            sampleTendril(second, secondProgress, startX, startY, startZ, endX, endY, endZ,
                bendX, bendY, bendZ, seed, animationTime);

            double tangentX = second[0] - first[0];
            double tangentY = second[1] - first[1];
            double tangentZ = second[2] - first[2];
            double tangentLength = Math.sqrt(tangentX * tangentX + tangentY * tangentY + tangentZ * tangentZ);
            if (tangentLength < 0.0001D) continue;
            tangentX /= tangentLength;
            tangentY /= tangentLength;
            tangentZ /= tangentLength;
            double middleX = (first[0] + second[0]) * 0.5D;
            double middleY = (first[1] + second[1]) * 0.5D;
            double middleZ = (first[2] + second[2]) * 0.5D;
            double cameraDirectionX = cameraX - middleX;
            double cameraDirectionY = cameraY - middleY;
            double cameraDirectionZ = cameraZ - middleZ;
            double cameraLength = Math.sqrt(cameraDirectionX * cameraDirectionX
                + cameraDirectionY * cameraDirectionY + cameraDirectionZ * cameraDirectionZ);
            if (cameraLength > 0.0001D) {
                cameraDirectionX /= cameraLength;
                cameraDirectionY /= cameraLength;
                cameraDirectionZ /= cameraLength;
            }
            double sideX = tangentY * cameraDirectionZ - tangentZ * cameraDirectionY;
            double sideY = tangentZ * cameraDirectionX - tangentX * cameraDirectionZ;
            double sideZ = tangentX * cameraDirectionY - tangentY * cameraDirectionX;
            double sideLength = Math.sqrt(sideX * sideX + sideY * sideY + sideZ * sideZ);
            if (sideLength < 0.0001D) {
                sideX = -tangentZ;
                sideY = 0.0D;
                sideZ = tangentX;
                sideLength = Math.sqrt(sideX * sideX + sideZ * sideZ);
            }
            if (sideLength < 0.0001D) {
                sideX = 1.0D;
                sideY = sideZ = 0.0D;
                sideLength = 1.0D;
            }
            sideX /= sideLength;
            sideY /= sideLength;
            sideZ /= sideLength;
            double firstWidth = width * (0.30D + Math.sin(firstProgress * Math.PI) * 0.70D);
            double secondWidth = width * (0.30D + Math.sin(secondProgress * Math.PI) * 0.70D);
            int firstAlpha = flowingAlpha(alpha, firstProgress, animationTime, seed);
            int secondAlpha = flowingAlpha(alpha, secondProgress, animationTime, seed);
            vertex(buffer, first[0] + sideX * firstWidth, first[1] + sideY * firstWidth,
                first[2] + sideZ * firstWidth, red, green, blue, firstAlpha);
            vertex(buffer, first[0] - sideX * firstWidth, first[1] - sideY * firstWidth,
                first[2] - sideZ * firstWidth, red, green, blue, firstAlpha);
            vertex(buffer, second[0] - sideX * secondWidth, second[1] - sideY * secondWidth,
                second[2] - sideZ * secondWidth, red, green, blue, secondAlpha);
            vertex(buffer, second[0] + sideX * secondWidth, second[1] + sideY * secondWidth,
                second[2] + sideZ * secondWidth, red, green, blue, secondAlpha);
        }
    }

    private static void sampleTendril(double[] result, double progress,
                                      double startX, double startY, double startZ,
                                      double endX, double endY, double endZ,
                                      double bendX, double bendY, double bendZ, double seed,
                                      float animationTime) {
        double arc = Math.sin(progress * Math.PI);
        double ripple = Math.sin(progress * Math.PI * 2.0D + animationTime * 0.018D + seed)
            * 0.14D * arc;
        result[0] = startX + (endX - startX) * progress + bendX * arc + ripple * bendZ;
        result[1] = startY + (endY - startY) * progress + bendY * arc
            + Math.sin(progress * Math.PI * 3.0D + seed) * 0.09D * arc;
        result[2] = startZ + (endZ - startZ) * progress + bendZ * arc - ripple * bendX;
    }

    private static int flowingAlpha(int alpha, double progress, float animationTime, double seed) {
        return (int) (alpha * (0.84D
            + Math.sin(animationTime * 0.012D - progress * 5.0D + seed) * 0.14D));
    }

    private static void drawMagicParticles(byte[] states, float animationTime,
                                           double cameraX, double cameraY, double cameraZ) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
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
            double[] point = new double[3];
            sampleTendril(point, progress, startX, startY, startZ, endX, endY, endZ,
                Math.sin(seed) * 0.92D, 0.55D + Math.cos(seed * 0.71D) * 0.38D,
                Math.cos(seed) * 0.92D, seed, animationTime);
            appendBillboardDiamond(buffer, point[0], point[1], point[2], cameraX, cameraY, cameraZ,
                0.16D, 232, 214, 255, 225);
        }
        for (int i = 0; i < 90; i++) {
            double cycle = (animationTime * 0.0012D + i * 0.61803398875D) % 1.0D;
            double x = Math.sin(i * 12.9898D) * 70.0D;
            double y = -54.0D + cycle * 108.0D;
            double z = Math.cos(i * 7.233D) * 46.0D;
            int alpha = (int) (Math.sin(cycle * Math.PI) * 92.0D);
            double size = 0.045D + (i % 5) * 0.012D;
            appendBillboardDiamond(buffer, x, y, z, cameraX, cameraY, cameraZ,
                size, 166, 124, 244, alpha);
        }
        tessellator.draw();
    }

    private static void appendBillboardDiamond(BufferBuilder buffer, double x, double y, double z,
                                                double cameraX, double cameraY, double cameraZ,
                                                double size, int red, int green, int blue, int alpha) {
        double directionX = cameraX - x;
        double directionY = cameraY - y;
        double directionZ = cameraZ - z;
        double directionLength = Math.sqrt(directionX * directionX
            + directionY * directionY + directionZ * directionZ);
        if (directionLength < 0.0001D) return;
        directionX /= directionLength;
        directionY /= directionLength;
        directionZ /= directionLength;
        double rightX = -directionZ;
        double rightY = 0.0D;
        double rightZ = directionX;
        double rightLength = Math.sqrt(rightX * rightX + rightZ * rightZ);
        if (rightLength < 0.0001D) {
            rightX = 1.0D;
            rightZ = 0.0D;
            rightLength = 1.0D;
        }
        rightX /= rightLength;
        rightZ /= rightLength;
        double upX = rightY * directionZ - rightZ * directionY;
        double upY = rightZ * directionX - rightX * directionZ;
        double upZ = rightX * directionY - rightY * directionX;
        vertex(buffer, x + upX * size, y + upY * size, z + upZ * size, red, green, blue, alpha);
        vertex(buffer, x + rightX * size, y + rightY * size, z + rightZ * size, red, green, blue, alpha);
        vertex(buffer, x - upX * size, y - upY * size, z - upZ * size, red, green, blue, alpha);
        vertex(buffer, x - rightX * size, y - rightY * size, z - rightZ * size, red, green, blue, alpha);
    }

    private static void drawStarGlows(byte[] states, float animationTime,
                                      double cameraX, double cameraY, double cameraZ) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < NODES.length; i++) {
            if (!visible(states, i) || states[i] == ConstellationManager.MYSTERY) continue;
            int state = states[i];
            double x = nodeX(i, animationTime);
            double y = nodeY(i, animationTime);
            double z = nodeZ(i, animationTime);
            double directionX = cameraX - x;
            double directionY = cameraY - y;
            double directionZ = cameraZ - z;
            double directionLength = Math.sqrt(directionX * directionX
                + directionY * directionY + directionZ * directionZ);
            if (directionLength < 0.0001D) continue;
            directionX /= directionLength;
            directionY /= directionLength;
            directionZ /= directionLength;
            double rightX = -directionZ;
            double rightY = 0.0D;
            double rightZ = directionX;
            double rightLength = Math.sqrt(rightX * rightX + rightZ * rightZ);
            if (rightLength < 0.0001D) {
                rightX = 1.0D;
                rightZ = 0.0D;
                rightLength = 1.0D;
            }
            rightX /= rightLength;
            rightZ /= rightLength;
            double upX = rightY * directionZ - rightZ * directionY;
            double upY = rightZ * directionX - rightX * directionZ;
            double upZ = rightX * directionY - rightY * directionX;
            int red = starRed(state);
            int green = starGreen(state);
            int blue = starBlue(state);
            double pulse = starPulse(state, i, animationTime) * starScale(state);
            double haloRadius = NODE_RADIUS * 3.2D * pulse;
            int centerAlpha = state == ConstellationManager.COMPLETED ? 56 : 42;
            for (int wedge = 0; wedge < 16; wedge++) {
                double firstAngle = wedge * Math.PI * 2.0D / 16.0D;
                double secondAngle = (wedge + 1) * Math.PI * 2.0D / 16.0D;
                vertex(buffer, x, y, z, red, green, blue, centerAlpha);
                billboardVertex(buffer, x, y, z, rightX, rightY, rightZ, upX, upY, upZ,
                    firstAngle, haloRadius, red, green, blue, 0);
                billboardVertex(buffer, x, y, z, rightX, rightY, rightZ, upX, upY, upZ,
                    secondAngle, haloRadius, red, green, blue, 0);
            }
            double rotation = animationTime * 0.004D + i * 0.47D;
            for (int rayIndex = 0; rayIndex < 10; rayIndex++) {
                double angle = rotation + rayIndex * Math.PI * 2.0D / 10.0D;
                double length = NODE_RADIUS * pulse * (rayIndex % 2 == 0 ? 4.2D : 2.8D);
                double base = NODE_RADIUS * pulse * 0.18D;
                double directionRight = Math.cos(angle);
                double directionUp = Math.sin(angle);
                double perpendicularRight = -directionUp;
                double perpendicularUp = directionRight;
                billboardOffsetVertex(buffer, x, y, z, rightX, rightY, rightZ, upX, upY, upZ,
                    perpendicularRight * base, perpendicularUp * base, red, green, blue, 118);
                billboardOffsetVertex(buffer, x, y, z, rightX, rightY, rightZ, upX, upY, upZ,
                    -perpendicularRight * base, -perpendicularUp * base, red, green, blue, 118);
                billboardOffsetVertex(buffer, x, y, z, rightX, rightY, rightZ, upX, upY, upZ,
                    directionRight * length, directionUp * length, red, green, blue, 0);
            }
        }
        tessellator.draw();
    }

    private static void billboardVertex(BufferBuilder buffer, double x, double y, double z,
                                        double rightX, double rightY, double rightZ,
                                        double upX, double upY, double upZ,
                                        double angle, double radius,
                                        int red, int green, int blue, int alpha) {
        billboardOffsetVertex(buffer, x, y, z, rightX, rightY, rightZ, upX, upY, upZ,
            Math.cos(angle) * radius, Math.sin(angle) * radius, red, green, blue, alpha);
    }

    private static void billboardOffsetVertex(BufferBuilder buffer, double x, double y, double z,
                                              double rightX, double rightY, double rightZ,
                                              double upX, double upY, double upZ,
                                              double rightOffset, double upOffset,
                                              int red, int green, int blue, int alpha) {
        vertex(buffer, x + rightX * rightOffset + upX * upOffset,
            y + rightY * rightOffset + upY * upOffset,
            z + rightZ * rightOffset + upZ * upOffset, red, green, blue, alpha);
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

    private static void drawNodes(byte[] states, float animationTime) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < NODES.length; i++) {
            if (!visible(states, i)) continue;
            int state = states[i];
            int red = starRed(state);
            int green = starGreen(state);
            int blue = starBlue(state);
            double pulse = starPulse(state, i, animationTime);
            double coreRadius = NODE_RADIUS * 0.62D * pulse * starScale(state);
            star(buffer, nodeX(i, animationTime), nodeY(i, animationTime), nodeZ(i, animationTime),
                coreRadius, red, green, blue,
                state == ConstellationManager.MYSTERY ? 165 : 255);
            if (state != ConstellationManager.MYSTERY) {
                star(buffer, nodeX(i, animationTime), nodeY(i, animationTime), nodeZ(i, animationTime),
                    coreRadius * 0.34D, Math.min(255, red + 85), Math.min(255, green + 85),
                    Math.min(255, blue + 85), 245);
            }
        }
        tessellator.draw();
    }

    private static double starPulse(int state, int index, float animationTime) {
        if (state == ConstellationManager.MYSTERY) return 0.90D;
        double strength = state == ConstellationManager.COMPLETED ? 1.0D : 0.65D;
        return 1.0D + Math.sin(animationTime * 0.040D + index * 0.73D) * 0.10D * strength
            + Math.sin(animationTime * 0.017D + index * 1.31D) * 0.04D * strength;
    }

    private static double starScale(int state) {
        if (state == ConstellationManager.AVAILABLE) return 0.72D;
        if (state == ConstellationManager.MYSTERY) return 0.58D;
        return 1.0D;
    }

    private static int starRed(int state) {
        if (state == ConstellationManager.COMPLETED) return 255;
        if (state == ConstellationManager.AVAILABLE) return 72;
        return 104;
    }

    private static int starGreen(int state) {
        if (state == ConstellationManager.COMPLETED) return 190;
        if (state == ConstellationManager.AVAILABLE) return 158;
        return 108;
    }

    private static int starBlue(int state) {
        if (state == ConstellationManager.COMPLETED) return 58;
        if (state == ConstellationManager.AVAILABLE) return 255;
        return 116;
    }

    private static boolean visible(byte[] states, int index) {
        if (states.length != NODES.length || states[index] == ConstellationManager.ABSENT) return false;
        return NODES[index].id.startsWith("sf4angel:core/") || states[index] == ConstellationManager.COMPLETED;
    }

    private static void star(BufferBuilder buffer, double x, double y, double z, double radius,
                             int red, int green, int blue, int alpha) {
        double tip = radius * 1.65D;
        double waist = radius * 1.25D;
        triangle(buffer, x, y + tip, z, x + waist, y, z, x, y, z + waist,
            red, green, blue, alpha, 1.00D);
        triangle(buffer, x, y + tip, z, x, y, z + waist, x - waist, y, z,
            red, green, blue, alpha, 0.82D);
        triangle(buffer, x, y + tip, z, x - waist, y, z, x, y, z - waist,
            red, green, blue, alpha, 0.68D);
        triangle(buffer, x, y + tip, z, x, y, z - waist, x + waist, y, z,
            red, green, blue, alpha, 0.90D);
        triangle(buffer, x, y - tip, z, x, y, z + waist, x + waist, y, z,
            red, green, blue, alpha, 0.72D);
        triangle(buffer, x, y - tip, z, x - waist, y, z, x, y, z + waist,
            red, green, blue, alpha, 0.62D);
        triangle(buffer, x, y - tip, z, x, y, z - waist, x - waist, y, z,
            red, green, blue, alpha, 0.78D);
        triangle(buffer, x, y - tip, z, x + waist, y, z, x, y, z - waist,
            red, green, blue, alpha, 0.86D);
    }

    private static void triangle(BufferBuilder buffer, double x1, double y1, double z1,
                                 double x2, double y2, double z2, double x3, double y3, double z3,
                                 int red, int green, int blue, int alpha, double shade) {
        int shadedRed = (int) (red * shade);
        int shadedGreen = (int) (green * shade);
        int shadedBlue = (int) (blue * shade);
        vertex(buffer, x1, y1, z1, shadedRed, shadedGreen, shadedBlue, alpha);
        vertex(buffer, x2, y2, z2, shadedRed, shadedGreen, shadedBlue, alpha);
        vertex(buffer, x3, y3, z3, shadedRed, shadedGreen, shadedBlue, alpha);
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
