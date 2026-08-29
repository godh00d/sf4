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
    private static final int TENDRIL_SEGMENTS = 20;
    private static final int TENDRIL_SIDES = 8;
    private static final double[] TENDRIL_COS = tendrilCircle(true);
    private static final double[] TENDRIL_SIN = tendrilCircle(false);

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
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawSkyShell();
        drawStars(animationTime);
        drawShootingStars(animationTime);
        GlStateManager.pushMatrix();
        GlStateManager.translate(sceneX, sceneY, sceneZ);
        double cameraX = -sceneX;
        double cameraY = -sceneY;
        double cameraZ = -sceneZ;

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(true);
        GL11.glDisable(GL11.GL_BLEND);
        drawTendrilTubes(states, animationTime, 0.30D, 208, 246, 255, 255);

        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawTendrilTubes(states, animationTime, 1.0D, 76, 188, 246, 112);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        drawTendrilTubes(states, animationTime, 1.52D, 255, 126, 46, 22);
        GL11.glDisable(GL11.GL_CULL_FACE);
        drawPlasmaMotes(animationTime);
        drawStarGlows(states, animationTime, cameraX, cameraY, cameraZ);

        GL11.glDepthMask(true);
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
        int red = (int) (2.0D + height * 7.0D);
        int green = (int) (5.0D + height * 15.0D);
        int blue = (int) (12.0D + height * 26.0D);
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
            boolean warm = i % 13 == 0;
            vertex(buffer, Math.cos(angle) * radius * (SKY_RADIUS - 0.2D),
                height * (SKY_RADIUS - 0.2D), Math.sin(angle) * radius * (SKY_RADIUS - 0.2D),
                warm ? 255 : brightness - 18, warm ? brightness : Math.min(255, brightness + 8),
                warm ? brightness - 72 : 255, alpha);
        }
        tessellator.draw();
    }

    private static void drawShootingStars(float animationTime) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 5; i++) {
            double cycle = (animationTime * 0.0017D + i * 0.217D) % 1.0D;
            if (cycle > 0.16D) continue;
            double progress = cycle / 0.16D;
            double fade = Math.sin(progress * Math.PI);
            double startX = Math.sin(i * 8.31D + 0.7D) * 8.5D;
            double startY = 7.5D - i % 3 * 3.2D;
            double startZ = Math.cos(i * 5.17D + 1.1D) * 8.5D;
            double directionX = 0.72D - i % 2 * 1.34D;
            double directionY = -0.34D - i % 3 * 0.07D;
            double directionZ = 0.58D - i % 4 * 0.37D;
            double directionLength = Math.sqrt(directionX * directionX
                + directionY * directionY + directionZ * directionZ);
            directionX /= directionLength;
            directionY /= directionLength;
            directionZ /= directionLength;
            double headX = startX + directionX * progress * 8.0D;
            double headY = startY + directionY * progress * 8.0D;
            double headZ = startZ + directionZ * progress * 8.0D;
            double tailLength = 1.3D + fade * 1.4D;
            double tailX = headX - directionX * tailLength;
            double tailY = headY - directionY * tailLength;
            double tailZ = headZ - directionZ * tailLength;
            double sideX = directionY * -headZ - directionZ * -headY;
            double sideY = directionZ * -headX - directionX * -headZ;
            double sideZ = directionX * -headY - directionY * -headX;
            double sideLength = Math.sqrt(sideX * sideX + sideY * sideY + sideZ * sideZ);
            if (sideLength < 0.0001D) continue;
            sideX /= sideLength;
            sideY /= sideLength;
            sideZ /= sideLength;
            double headWidth = 0.075D * fade;
            double tailWidth = 0.012D * fade;
            int headAlpha = (int) (235.0D * fade);
            vertex(buffer, tailX + sideX * tailWidth, tailY + sideY * tailWidth,
                tailZ + sideZ * tailWidth, 112, 190, 255, 0);
            vertex(buffer, tailX - sideX * tailWidth, tailY - sideY * tailWidth,
                tailZ - sideZ * tailWidth, 112, 190, 255, 0);
            vertex(buffer, headX - sideX * headWidth, headY - sideY * headWidth,
                headZ - sideZ * headWidth, 240, 250, 255, headAlpha);
            vertex(buffer, headX + sideX * headWidth, headY + sideY * headWidth,
                headZ + sideZ * headWidth, 240, 250, 255, headAlpha);
        }
        tessellator.draw();
    }

    private static void drawTendrilTubes(byte[] states, float animationTime, double radiusScale,
                                         int red, int green, int blue, int alpha) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        int pointCount = TENDRIL_SEGMENTS + 1;
        double[] points = new double[pointCount * 3];
        double[] tangents = new double[pointCount * 3];
        double[] normals = new double[pointCount * 3];
        double[] binormals = new double[pointCount * 3];
        double[] distances = new double[pointCount];
        double[] energies = new double[pointCount];
        for (int childIndex = 0; childIndex < NODES.length; childIndex++) {
            if (!visible(states, childIndex)) continue;
            for (String parentId : NODES[childIndex].parents) {
                Integer parentIndex = INDEXES.get(parentId);
                if (parentIndex == null || !visible(states, parentIndex)) continue;
                boolean structural = parentIndex == LAYOUT_PARENTS[childIndex];
                double radius = structural ? 0.19D : 0.072D;
                int edgeAlpha = structural ? alpha : (int) (alpha * 0.46D);
                int edgeRed = structural ? red : (int) (red * 0.72D);
                int edgeGreen = structural ? green : (int) (green * 0.78D);
                int edgeBlue = structural ? blue : (int) (blue * 0.88D);
                if (states[childIndex] == ConstellationManager.MYSTERY) {
                    edgeRed *= 0.44D;
                    edgeGreen *= 0.44D;
                    edgeBlue *= 0.44D;
                    edgeAlpha *= 0.48D;
                }
                appendTendrilTube(buffer, parentIndex, childIndex, animationTime,
                    radius * radiusScale, edgeRed, edgeGreen, edgeBlue, edgeAlpha,
                    points, tangents, normals, binormals, distances, energies);
            }
        }
        tessellator.draw();
    }

    private static void appendTendrilTube(BufferBuilder buffer, int parentIndex, int childIndex,
                                           float animationTime, double baseRadius,
                                           int red, int green, int blue, int alpha,
                                           double[] points, double[] tangents, double[] normals,
                                           double[] binormals, double[] distances, double[] energies) {
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
        for (int ring = 0; ring <= TENDRIL_SEGMENTS; ring++) {
            sampleTendril(points, ring * 3, ring / (double) TENDRIL_SEGMENTS,
                startX, startY, startZ, endX, endY, endZ,
                bendX, bendY, bendZ, seed, animationTime);
            if (ring > 0) {
                distances[ring] = distances[ring - 1] + distance(points, ring * 3, (ring - 1) * 3);
            } else {
                distances[ring] = 0.0D;
            }
        }
        for (int ring = 0; ring <= TENDRIL_SEGMENTS; ring++) {
            int before = Math.max(0, ring - 1) * 3;
            int after = Math.min(TENDRIL_SEGMENTS, ring + 1) * 3;
            setNormalized(tangents, ring * 3,
                points[after] - points[before], points[after + 1] - points[before + 1],
                points[after + 2] - points[before + 2]);
        }
        initializeFrame(tangents, normals, binormals, seed);
        for (int ring = 1; ring <= TENDRIL_SEGMENTS; ring++) {
            transportFrame(tangents, normals, binormals, ring);
        }
        double totalLength = distances[TENDRIL_SEGMENTS];
        for (int ring = 0; ring <= TENDRIL_SEGMENTS; ring++) {
            energies[ring] = pulseEnergy(distances[ring], distances, totalLength, animationTime, seed);
        }
        for (int segment = 0; segment < TENDRIL_SEGMENTS; segment++) {
            for (int side = 0; side < TENDRIL_SIDES; side++) {
                int nextSide = (side + 1) % TENDRIL_SIDES;
                tubeVertex(buffer, points, normals, binormals, energies,
                    segment, side, baseRadius, seed, animationTime, red, green, blue, alpha);
                tubeVertex(buffer, points, normals, binormals, energies,
                    segment, nextSide, baseRadius, seed, animationTime, red, green, blue, alpha);
                tubeVertex(buffer, points, normals, binormals, energies,
                    segment + 1, nextSide, baseRadius, seed, animationTime, red, green, blue, alpha);
                tubeVertex(buffer, points, normals, binormals, energies,
                    segment + 1, side, baseRadius, seed, animationTime, red, green, blue, alpha);
            }
        }
    }

    private static void sampleTendril(double[] result, int offset, double progress,
                                      double startX, double startY, double startZ,
                                      double endX, double endY, double endZ,
                                      double bendX, double bendY, double bendZ, double seed,
                                      float animationTime) {
        double arc = Math.sin(progress * Math.PI);
        double ripple = Math.sin(progress * Math.PI * 2.0D - animationTime * 0.035D + seed)
            * 0.14D * arc;
        result[offset] = startX + (endX - startX) * progress + bendX * arc + ripple * bendZ;
        result[offset + 1] = startY + (endY - startY) * progress + bendY * arc
            + Math.sin(progress * Math.PI * 3.0D - animationTime * 0.025D + seed) * 0.09D * arc;
        result[offset + 2] = startZ + (endZ - startZ) * progress + bendZ * arc - ripple * bendX;
    }

    private static void initializeFrame(double[] tangents, double[] normals, double[] binormals,
                                        double seed) {
        double helperX = Math.sin(seed * 1.17D);
        double helperY = Math.cos(seed * 0.83D);
        double helperZ = Math.sin(seed * 0.61D + 1.3D);
        double dot = helperX * tangents[0] + helperY * tangents[1] + helperZ * tangents[2];
        double normalX = helperX - tangents[0] * dot;
        double normalY = helperY - tangents[1] * dot;
        double normalZ = helperZ - tangents[2] * dot;
        if (normalX * normalX + normalY * normalY + normalZ * normalZ < 0.0001D) {
            helperX = Math.abs(tangents[1]) < 0.9D ? 0.0D : 1.0D;
            helperY = Math.abs(tangents[1]) < 0.9D ? 1.0D : 0.0D;
            helperZ = 0.0D;
            dot = helperX * tangents[0] + helperY * tangents[1];
            normalX = helperX - tangents[0] * dot;
            normalY = helperY - tangents[1] * dot;
            normalZ = -tangents[2] * dot;
        }
        setNormalized(normals, 0, normalX, normalY, normalZ);
        setNormalized(binormals, 0,
            tangents[1] * normals[2] - tangents[2] * normals[1],
            tangents[2] * normals[0] - tangents[0] * normals[2],
            tangents[0] * normals[1] - tangents[1] * normals[0]);
    }

    private static void transportFrame(double[] tangents, double[] normals, double[] binormals, int ring) {
        int previous = (ring - 1) * 3;
        int current = ring * 3;
        double axisX = tangents[previous + 1] * tangents[current + 2]
            - tangents[previous + 2] * tangents[current + 1];
        double axisY = tangents[previous + 2] * tangents[current]
            - tangents[previous] * tangents[current + 2];
        double axisZ = tangents[previous] * tangents[current + 1]
            - tangents[previous + 1] * tangents[current];
        double sine = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
        double cosine = Math.max(-1.0D, Math.min(1.0D,
            tangents[previous] * tangents[current]
                + tangents[previous + 1] * tangents[current + 1]
                + tangents[previous + 2] * tangents[current + 2]));
        double normalX = normals[previous];
        double normalY = normals[previous + 1];
        double normalZ = normals[previous + 2];
        if (sine > 0.00001D) {
            axisX /= sine;
            axisY /= sine;
            axisZ /= sine;
            double axisDotNormal = axisX * normalX + axisY * normalY + axisZ * normalZ;
            double crossX = axisY * normalZ - axisZ * normalY;
            double crossY = axisZ * normalX - axisX * normalZ;
            double crossZ = axisX * normalY - axisY * normalX;
            normalX = normalX * cosine + crossX * sine + axisX * axisDotNormal * (1.0D - cosine);
            normalY = normalY * cosine + crossY * sine + axisY * axisDotNormal * (1.0D - cosine);
            normalZ = normalZ * cosine + crossZ * sine + axisZ * axisDotNormal * (1.0D - cosine);
        }
        double tangentDotNormal = tangents[current] * normalX
            + tangents[current + 1] * normalY + tangents[current + 2] * normalZ;
        setNormalized(normals, current,
            normalX - tangents[current] * tangentDotNormal,
            normalY - tangents[current + 1] * tangentDotNormal,
            normalZ - tangents[current + 2] * tangentDotNormal);
        setNormalized(binormals, current,
            tangents[current + 1] * normals[current + 2] - tangents[current + 2] * normals[current + 1],
            tangents[current + 2] * normals[current] - tangents[current] * normals[current + 2],
            tangents[current] * normals[current + 1] - tangents[current + 1] * normals[current]);
    }

    private static void tubeVertex(BufferBuilder buffer, double[] points, double[] normals,
                                   double[] binormals, double[] energies,
                                   int ring, int side, double baseRadius, double seed,
                                   float animationTime, int red, int green, int blue, int alpha) {
        int offset = ring * 3;
        double progress = ring / (double) TENDRIL_SEGMENTS;
        double radialX = normals[offset] * TENDRIL_COS[side] + binormals[offset] * TENDRIL_SIN[side];
        double radialY = normals[offset + 1] * TENDRIL_COS[side]
            + binormals[offset + 1] * TENDRIL_SIN[side];
        double radialZ = normals[offset + 2] * TENDRIL_COS[side]
            + binormals[offset + 2] * TENDRIL_SIN[side];
        double energy = energies[ring];
        double breathing = 0.975D + Math.sin(animationTime * 0.045D - progress * 8.0D + seed) * 0.025D;
        double envelope = 0.55D + Math.sin(progress * Math.PI) * 0.45D;
        double surfaceRipple = 1.0D + Math.sin(side * Math.PI * 0.75D - progress * 11.0D
            + animationTime * 0.055D + seed) * 0.025D;
        double radius = baseRadius * envelope * (breathing + energy * 0.06D) * surfaceRipple;
        double light = Math.max(0.0D, radialX * 0.32D + radialY * 0.81D + radialZ * 0.49D);
        double spiral = Math.sin(side * Math.PI * 0.5D - progress * 13.0D
            + animationTime * 0.075D + seed) * 0.5D + 0.5D;
        double brightness = 0.45D + light * 0.30D + spiral * 0.10D + energy * 0.48D;
        int vertexRed = Math.min(255, (int) (red * brightness + (255 - red) * energy * 0.76D));
        int vertexGreen = Math.min(255, (int) (green * brightness + (255 - green) * energy * 0.76D));
        int vertexBlue = Math.min(255, (int) (blue * brightness + (255 - blue) * energy * 0.76D));
        int vertexAlpha = Math.min(255, (int) (alpha * (0.68D + energy * 0.32D)));
        vertex(buffer, points[offset] + radialX * radius, points[offset + 1] + radialY * radius,
            points[offset + 2] + radialZ * radius, vertexRed, vertexGreen, vertexBlue, vertexAlpha);
    }

    private static double pulseEnergy(double distance, double[] distances, double totalLength,
                                      float animationTime, double seed) {
        if (totalLength < 0.0001D) return 0.0D;
        double sigma = Math.max(0.42D, totalLength * 0.09D);
        double strongest = 0.0D;
        for (int pulse = 0; pulse < 2; pulse++) {
            double sample = pulseProgress(animationTime, seed, pulse) * TENDRIL_SEGMENTS;
            int ring = Math.min(TENDRIL_SEGMENTS - 1, (int) sample);
            double center = distances[ring]
                + (distances[ring + 1] - distances[ring]) * (sample - ring);
            double separation = Math.abs(distance - center);
            double endpointFade = Math.min(1.0D, Math.min(sample, TENDRIL_SEGMENTS - sample) * 0.5D);
            strongest = Math.max(strongest,
                Math.exp(-(separation * separation) / (2.0D * sigma * sigma)) * endpointFade);
        }
        return strongest;
    }

    private static double pulseProgress(float animationTime, double seed, int pulse) {
        double progress = animationTime * 0.0055D + seed * 0.137D + pulse * 0.5D;
        return progress - Math.floor(progress);
    }

    private static void drawPlasmaMotes(float animationTime) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 90; i++) {
            double cycle = (animationTime * 0.0018D + i * 0.61803398875D) % 1.0D;
            double curl = cycle * Math.PI * 2.0D + animationTime * 0.006D + i * 0.71D;
            double x = Math.sin(i * 12.9898D) * 68.0D + Math.sin(curl) * 0.65D;
            double y = -54.0D + cycle * 108.0D;
            double z = Math.cos(i * 7.233D) * 44.0D + Math.cos(curl) * 0.65D;
            int alpha = (int) (Math.sin(cycle * Math.PI) * 108.0D);
            double size = (0.032D + (i % 5) * 0.008D)
                * (0.85D + Math.sin(animationTime * 0.055D + i * 1.9D) * 0.15D);
            if (i % 5 == 0) {
                star(buffer, x, y, z, size, 255, 142, 52, alpha);
            } else {
                star(buffer, x, y, z, size, 116, 218, 255, alpha);
            }
        }
        tessellator.draw();
    }

    private static double distance(double[] points, int first, int second) {
        double x = points[first] - points[second];
        double y = points[first + 1] - points[second + 1];
        double z = points[first + 2] - points[second + 2];
        return Math.sqrt(x * x + y * y + z * z);
    }

    private static void setNormalized(double[] result, int offset, double x, double y, double z) {
        double length = Math.sqrt(x * x + y * y + z * z);
        if (length < 0.000001D) {
            result[offset] = 1.0D;
            result[offset + 1] = result[offset + 2] = 0.0D;
            return;
        }
        result[offset] = x / length;
        result[offset + 1] = y / length;
        result[offset + 2] = z / length;
    }

    private static double[] tendrilCircle(boolean cosine) {
        double[] values = new double[TENDRIL_SIDES];
        for (int i = 0; i < values.length; i++) {
            double angle = i * Math.PI * 2.0D / TENDRIL_SIDES;
            values[i] = cosine ? Math.cos(angle) : Math.sin(angle);
        }
        return values;
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
