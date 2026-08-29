package com.godh00d.sf4angel.entity;

import com.godh00d.sf4angel.client.ConstellationClientState;
import com.godh00d.sf4angel.constellation.AchievementConstellationCatalog;
import com.godh00d.sf4angel.constellation.ConstellationManager;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.Map;
import java.util.List;
import java.util.Random;
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
    private static final int SKY_LATITUDES = 18;
    private static final int SKY_LONGITUDES = 36;
    private static final int SKY_STAR_COUNT = 640;
    private static final double[] SKY_STARS = createSkyStars();
    private static final float NODE_RADIUS = 0.48F;
    private static final double[] ICOSAHEDRON_VERTICES = createIcosahedronVertices();
    private static final int[] ICOSAHEDRON_FACES = {
        0, 11, 5, 0, 5, 1, 0, 1, 7, 0, 7, 10, 0, 10, 11,
        1, 5, 9, 5, 11, 4, 11, 10, 2, 10, 7, 6, 7, 1, 8,
        3, 9, 4, 3, 4, 2, 3, 2, 6, 3, 6, 8, 3, 8, 9,
        4, 9, 5, 2, 4, 11, 6, 2, 10, 8, 6, 7, 9, 8, 1
    };
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
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
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
        drawTendrilTubes(states, animationTime, 0.30D, 0, 255);

        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawTendrilTubes(states, animationTime, 1.0D, 1, 112);

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        drawTendrilTubes(states, animationTime, 1.52D, 2, 22);
        GL11.glDisable(GL11.GL_CULL_FACE);
        drawPlasmaMotes(animationTime);
        drawStarGlows(states, animationTime, cameraX, cameraY, cameraZ);

        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        drawNodes(states, animationTime);
        GlStateManager.popMatrix();
        GL11.glPopAttrib();

        super.doRender(angel, x, y, z, entityYaw, partialTicks);
        int hovered = hoveredNode(observatory, player, states, partialTicks, animationTime);
        if (hovered >= 0) {
            boolean mystery = states[hovered] == ConstellationManager.MYSTERY;
            String title = mystery
                ? "Unrevealed achievement" : NODES[hovered].title;
            renderNodeLabel(title, nodeHint(hovered, mystery), hovered,
                sceneX, sceneY, sceneZ, animationTime);
        }
    }

    private static void drawSkyShell() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        for (int latitude = 0; latitude < SKY_LATITUDES; latitude++) {
            double firstLatitude = -Math.PI * 0.5D + Math.PI * latitude / SKY_LATITUDES;
            double secondLatitude = -Math.PI * 0.5D + Math.PI * (latitude + 1) / SKY_LATITUDES;
            for (int longitude = 0; longitude < SKY_LONGITUDES; longitude++) {
                double firstLongitude = Math.PI * 2.0D * longitude / SKY_LONGITUDES;
                double secondLongitude = longitude + 1 == SKY_LONGITUDES
                    ? 0.0D : Math.PI * 2.0D * (longitude + 1) / SKY_LONGITUDES;
                skySphereVertex(buffer, firstLatitude, firstLongitude);
                skySphereVertex(buffer, secondLatitude, firstLongitude);
                skySphereVertex(buffer, secondLatitude, secondLongitude);
                skySphereVertex(buffer, firstLatitude, firstLongitude);
                skySphereVertex(buffer, secondLatitude, secondLongitude);
                skySphereVertex(buffer, firstLatitude, secondLongitude);
            }
        }
        tessellator.draw();
    }

    private static void skySphereVertex(BufferBuilder buffer, double latitude, double longitude) {
        double horizontal = Math.cos(latitude) * SKY_RADIUS;
        skyVertex(buffer, Math.cos(longitude) * horizontal, Math.sin(latitude) * SKY_RADIUS,
            Math.sin(longitude) * horizontal);
    }

    private static void skyVertex(BufferBuilder buffer, double x, double y, double z) {
        double height = (y / SKY_RADIUS + 1.0D) * 0.5D;
        int red = (int) (2.0D + height * 7.0D);
        int green = (int) (5.0D + height * 15.0D);
        int blue = (int) (12.0D + height * 26.0D);
        vertex(buffer, x, y, z, red, green, blue, 255);
    }

    private static void drawStars(float animationTime) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        double[] basis = new double[6];
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < SKY_STAR_COUNT; i++) {
            int offset = i * 8;
            double wave = Math.sin(animationTime * SKY_STARS[offset + 5] + SKY_STARS[offset + 4]);
            boolean rare = SKY_STARS[offset + 6] > 0.5D;
            double blink = rare ? 0.38D + Math.pow(wave * 0.5D + 0.5D, 2.0D) * 0.62D
                : 0.82D + wave * 0.18D;
            int alpha = (int) ((rare ? 245.0D : 178.0D) * blink);
            setSkyStarBasis(offset, basis);
            appendSkyDiamond(buffer, offset, basis,
                SKY_STARS[offset + 3] * (rare ? 0.82D + blink * 0.18D : 1.0D), alpha);
        }
        tessellator.draw();

        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < SKY_STAR_COUNT; i++) {
            int offset = i * 8;
            if (SKY_STARS[offset + 6] < 0.5D) continue;
            double wave = Math.sin(animationTime * SKY_STARS[offset + 5] + SKY_STARS[offset + 4]);
            double blink = 0.38D + Math.pow(wave * 0.5D + 0.5D, 2.0D) * 0.62D;
            setSkyStarBasis(offset, basis);
            appendSkyGlow(buffer, offset, basis, SKY_STARS[offset + 3] * (2.4D + blink * 0.8D),
                (int) (92.0D * blink));
        }
        tessellator.draw();
    }

    private static void appendSkyDiamond(BufferBuilder buffer, int offset, double[] basis,
                                         double size, int alpha) {
        double radius = SKY_RADIUS - 0.18D;
        double x = SKY_STARS[offset] * radius;
        double y = SKY_STARS[offset + 1] * radius;
        double z = SKY_STARS[offset + 2] * radius;
        int red = SKY_STARS[offset + 7] > 0.5D ? 255 : 184;
        int green = SKY_STARS[offset + 7] > 0.5D ? 214 : 220;
        int blue = SKY_STARS[offset + 7] > 0.5D ? 152 : 255;
        vertex(buffer, x + basis[3] * size, y + basis[4] * size, z + basis[5] * size,
            red, green, blue, alpha);
        vertex(buffer, x + basis[0] * size, y + basis[1] * size, z + basis[2] * size,
            red, green, blue, alpha);
        vertex(buffer, x - basis[3] * size, y - basis[4] * size, z - basis[5] * size,
            red, green, blue, alpha);
        vertex(buffer, x - basis[0] * size, y - basis[1] * size, z - basis[2] * size,
            red, green, blue, alpha);
    }

    private static void appendSkyGlow(BufferBuilder buffer, int offset, double[] basis,
                                      double radius, int alpha) {
        double centerRadius = SKY_RADIUS - 0.2D;
        double x = SKY_STARS[offset] * centerRadius;
        double y = SKY_STARS[offset + 1] * centerRadius;
        double z = SKY_STARS[offset + 2] * centerRadius;
        for (int wedge = 0; wedge < 12; wedge++) {
            double first = wedge * Math.PI * 2.0D / 12.0D;
            double second = (wedge + 1) * Math.PI * 2.0D / 12.0D;
            vertex(buffer, x, y, z, 228, 240, 255, alpha);
            billboardOffsetVertex(buffer, x, y, z, basis[0], basis[1], basis[2],
                basis[3], basis[4], basis[5], Math.cos(first) * radius,
                Math.sin(first) * radius, 154, 202, 255, 0);
            billboardOffsetVertex(buffer, x, y, z, basis[0], basis[1], basis[2],
                basis[3], basis[4], basis[5], Math.cos(second) * radius,
                Math.sin(second) * radius, 154, 202, 255, 0);
        }
    }

    private static void setSkyStarBasis(int offset, double[] result) {
        double directionX = SKY_STARS[offset];
        double directionY = SKY_STARS[offset + 1];
        double directionZ = SKY_STARS[offset + 2];
        double rightX = Math.abs(directionY) < 0.9D ? directionZ : 0.0D;
        double rightY = Math.abs(directionY) < 0.9D ? 0.0D : -directionZ;
        double rightZ = Math.abs(directionY) < 0.9D ? -directionX : directionY;
        double rightLength = Math.sqrt(rightX * rightX + rightY * rightY + rightZ * rightZ);
        rightX /= rightLength;
        rightY /= rightLength;
        rightZ /= rightLength;
        double upX = directionY * rightZ - directionZ * rightY;
        double upY = directionZ * rightX - directionX * rightZ;
        double upZ = directionX * rightY - directionY * rightX;
        double rollCos = Math.cos(SKY_STARS[offset + 4]);
        double rollSin = Math.sin(SKY_STARS[offset + 4]);
        result[0] = rightX * rollCos + upX * rollSin;
        result[1] = rightY * rollCos + upY * rollSin;
        result[2] = rightZ * rollCos + upZ * rollSin;
        result[3] = upX * rollCos - rightX * rollSin;
        result[4] = upY * rollCos - rightY * rollSin;
        result[5] = upZ * rollCos - rightZ * rollSin;
    }

    private static double[] createSkyStars() {
        Random random = new Random(10842L);
        double[] stars = new double[SKY_STAR_COUNT * 8];
        for (int i = 0; i < SKY_STAR_COUNT; i++) {
            int offset = i * 8;
            double y = random.nextDouble() * 2.0D - 1.0D;
            double angle = random.nextDouble() * Math.PI * 2.0D;
            double horizontal = Math.sqrt(1.0D - y * y);
            boolean rare = random.nextDouble() < 0.018D;
            double sizeRoll = random.nextDouble();
            stars[offset] = Math.cos(angle) * horizontal;
            stars[offset + 1] = y;
            stars[offset + 2] = Math.sin(angle) * horizontal;
            stars[offset + 3] = rare ? 0.10D + sizeRoll * 0.08D
                : 0.018D + sizeRoll * sizeRoll * sizeRoll * 0.045D;
            stars[offset + 4] = random.nextDouble() * Math.PI * 2.0D;
            stars[offset + 5] = 0.025D + random.nextDouble() * 0.045D;
            stars[offset + 6] = rare ? 1.0D : 0.0D;
            stars[offset + 7] = random.nextDouble() < 0.12D ? 1.0D : 0.0D;
        }
        return stars;
    }

    private static void drawShootingStars(float animationTime) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 4; i++) {
            double progress = shootingProgress(animationTime, i);
            if (progress < 0.0D) continue;
            double[] configuration = shootingConfiguration(i);
            double fade = Math.sin(progress * Math.PI);
            appendShootingTrail(buffer, progress, fade, configuration, 0.13D, 58, 82, 154, 255);
            appendShootingTrail(buffer, progress, fade, configuration, 0.038D, 225, 224, 246, 255);
        }
        tessellator.draw();

        buffer.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i < 4; i++) {
            double progress = shootingProgress(animationTime, i);
            if (progress < 0.0D) continue;
            appendShootingHead(buffer, progress, Math.sin(progress * Math.PI), shootingConfiguration(i));
        }
        tessellator.draw();
    }

    private static double shootingProgress(float animationTime, int index) {
        double cycle = (animationTime * 0.00065D + index * 0.241D) % 1.0D;
        return cycle <= 0.022D ? cycle / 0.022D : -1.0D;
    }

    private static double[] shootingConfiguration(int index) {
        double y = -0.48D + index * 0.29D;
        double angle = index * 2.17D + 0.63D;
        double horizontal = Math.sqrt(1.0D - y * y);
        double startX = Math.cos(angle) * horizontal;
        double startZ = Math.sin(angle) * horizontal;
        double tangentX = startZ;
        double tangentY = 0.0D;
        double tangentZ = -startX;
        double tangentLength = Math.sqrt(tangentX * tangentX + tangentZ * tangentZ);
        tangentX /= tangentLength;
        tangentZ /= tangentLength;
        double bendX = y * tangentZ;
        double bendY = startZ * tangentX - startX * tangentZ;
        double bendZ = -y * tangentX;
        double roll = index * 1.31D + 0.4D;
        double rollCos = Math.cos(roll);
        double rollSin = Math.sin(roll);
        return new double[] {
            startX, y, startZ,
            tangentX * rollCos + bendX * rollSin,
            tangentY * rollCos + bendY * rollSin,
            tangentZ * rollCos + bendZ * rollSin,
            bendX * rollCos - tangentX * rollSin,
            bendY * rollCos - tangentY * rollSin,
            bendZ * rollCos - tangentZ * rollSin
        };
    }

    private static void appendShootingTrail(BufferBuilder buffer, double progress, double fade,
                                            double[] configuration, double maximumWidth,
                                            int maximumAlpha, int red, int green, int blue) {
        double tail = Math.max(0.0D, progress - 0.28D);
        double[] first = new double[3];
        double[] second = new double[3];
        for (int segment = 0; segment < 12; segment++) {
            double firstFraction = segment / 12.0D;
            double secondFraction = (segment + 1) / 12.0D;
            shootingPoint(first, tail + (progress - tail) * firstFraction, configuration);
            shootingPoint(second, tail + (progress - tail) * secondFraction, configuration);
            double tangentX = second[0] - first[0];
            double tangentY = second[1] - first[1];
            double tangentZ = second[2] - first[2];
            double radialX = (first[0] + second[0]) * 0.5D;
            double radialY = (first[1] + second[1]) * 0.5D;
            double radialZ = (first[2] + second[2]) * 0.5D;
            double sideX = radialY * tangentZ - radialZ * tangentY;
            double sideY = radialZ * tangentX - radialX * tangentZ;
            double sideZ = radialX * tangentY - radialY * tangentX;
            double sideLength = Math.sqrt(sideX * sideX + sideY * sideY + sideZ * sideZ);
            if (sideLength < 0.0001D) continue;
            sideX /= sideLength;
            sideY /= sideLength;
            sideZ /= sideLength;
            double firstWidth = maximumWidth * Math.pow(firstFraction, 0.68D) * fade;
            double secondWidth = maximumWidth * Math.pow(secondFraction, 0.68D) * fade;
            int firstAlpha = (int) (maximumAlpha * Math.pow(firstFraction, 1.35D) * fade);
            int secondAlpha = (int) (maximumAlpha * Math.pow(secondFraction, 1.35D) * fade);
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

    private static void appendShootingHead(BufferBuilder buffer, double progress, double fade,
                                           double[] configuration) {
        double[] center = new double[3];
        double[] before = new double[3];
        shootingPoint(center, progress, configuration);
        shootingPoint(before, Math.max(0.0D, progress - 0.01D), configuration);
        double upX = center[0] - before[0];
        double upY = center[1] - before[1];
        double upZ = center[2] - before[2];
        double upLength = Math.sqrt(upX * upX + upY * upY + upZ * upZ);
        if (upLength < 0.0001D) return;
        upX /= upLength;
        upY /= upLength;
        upZ /= upLength;
        double rightX = center[1] * upZ - center[2] * upY;
        double rightY = center[2] * upX - center[0] * upZ;
        double rightZ = center[0] * upY - center[1] * upX;
        double rightLength = Math.sqrt(rightX * rightX + rightY * rightY + rightZ * rightZ);
        if (rightLength < 0.0001D) return;
        rightX /= rightLength;
        rightY /= rightLength;
        rightZ /= rightLength;
        appendRadialGlow(buffer, center[0], center[1], center[2], rightX, rightY, rightZ,
            upX, upY, upZ, 0.34D * fade, 86, 154, 202, 255);
        appendRadialGlow(buffer, center[0], center[1], center[2], rightX, rightY, rightZ,
            upX, upY, upZ, 0.14D * fade, 225, 238, 248, 255);
    }

    private static void appendRadialGlow(BufferBuilder buffer, double x, double y, double z,
                                         double rightX, double rightY, double rightZ,
                                         double upX, double upY, double upZ,
                                         double radius, int alpha, int red, int green, int blue) {
        for (int wedge = 0; wedge < 12; wedge++) {
            double first = wedge * Math.PI * 2.0D / 12.0D;
            double second = (wedge + 1) * Math.PI * 2.0D / 12.0D;
            vertex(buffer, x, y, z, red, green, blue, alpha);
            billboardOffsetVertex(buffer, x, y, z, rightX, rightY, rightZ, upX, upY, upZ,
                Math.cos(first) * radius, Math.sin(first) * radius, red, green, blue, 0);
            billboardOffsetVertex(buffer, x, y, z, rightX, rightY, rightZ, upX, upY, upZ,
                Math.cos(second) * radius, Math.sin(second) * radius, red, green, blue, 0);
        }
    }

    private static void shootingPoint(double[] result, double progress, double[] configuration) {
        double pathLength = 0.58D;
        double bend = Math.sin(progress * Math.PI) * 0.065D;
        double cosine = Math.cos(pathLength * progress);
        double sine = Math.sin(pathLength * progress);
        double x = configuration[0] * cosine + configuration[3] * sine + configuration[6] * bend;
        double y = configuration[1] * cosine + configuration[4] * sine + configuration[7] * bend;
        double z = configuration[2] * cosine + configuration[5] * sine + configuration[8] * bend;
        double length = Math.sqrt(x * x + y * y + z * z);
        double radius = SKY_RADIUS - 0.34D;
        result[0] = x / length * radius;
        result[1] = y / length * radius;
        result[2] = z / length * radius;
    }

    private static void drawTendrilTubes(byte[] states, float animationTime, double radiusScale,
                                         int layer, int alpha) {
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
        int[] connectionCounts = visibleConnectionCounts(states);
        for (int childIndex = 0; childIndex < NODES.length; childIndex++) {
            if (!visible(states, childIndex)) continue;
            for (String parentId : NODES[childIndex].parents) {
                Integer parentIndex = INDEXES.get(parentId);
                if (parentIndex == null || !visible(states, parentIndex)) continue;
                boolean structural = parentIndex == LAYOUT_PARENTS[childIndex];
                double radius = structural ? 0.19D : 0.072D;
                int edgeAlpha = structural ? alpha : (int) (alpha * 0.46D);
                int edgeRed = tendrilRed(states[childIndex], layer);
                int edgeGreen = tendrilGreen(states[childIndex], layer);
                int edgeBlue = tendrilBlue(states[childIndex], layer);
                if (!structural) {
                    edgeRed *= 0.72D;
                    edgeGreen *= 0.78D;
                    edgeBlue *= 0.88D;
                }
                if (states[childIndex] == ConstellationManager.MYSTERY) edgeAlpha *= 0.82D;
                appendTendrilTube(buffer, parentIndex, childIndex, animationTime,
                    radius * radiusScale,
                    nodeVisualRadius(states[parentIndex], parentIndex, animationTime) * 0.92D,
                    nodeVisualRadius(states[childIndex], childIndex, animationTime) * 0.92D,
                    tendrilCollarRadius(states[parentIndex], parentIndex, connectionCounts[parentIndex],
                        structural, layer, animationTime),
                    tendrilCollarRadius(states[childIndex], childIndex, connectionCounts[childIndex],
                        structural, layer, animationTime),
                    edgeRed, edgeGreen, edgeBlue, edgeAlpha,
                    points, tangents, normals, binormals, distances, energies);
            }
        }
        tessellator.draw();
    }

    private static int tendrilRed(int state, int layer) {
        if (state == ConstellationManager.COMPLETED) return 255;
        if (state == ConstellationManager.AVAILABLE) return layer == 0 ? 206 : layer == 1 ? 68 : 38;
        return layer == 0 ? 150 : layer == 1 ? 96 : 74;
    }

    private static int tendrilGreen(int state, int layer) {
        if (state == ConstellationManager.COMPLETED) return layer == 0 ? 236 : layer == 1 ? 188 : 112;
        if (state == ConstellationManager.AVAILABLE) return layer == 0 ? 244 : layer == 1 ? 154 : 82;
        return layer == 0 ? 155 : layer == 1 ? 103 : 80;
    }

    private static int tendrilBlue(int state, int layer) {
        if (state == ConstellationManager.COMPLETED) return layer == 0 ? 166 : layer == 1 ? 60 : 24;
        if (state == ConstellationManager.AVAILABLE) return 255;
        return layer == 0 ? 166 : layer == 1 ? 118 : 94;
    }

    private static int[] visibleConnectionCounts(byte[] states) {
        int[] counts = new int[NODES.length];
        for (int childIndex = 0; childIndex < NODES.length; childIndex++) {
            if (!visible(states, childIndex)) continue;
            for (String parentId : NODES[childIndex].parents) {
                Integer parentIndex = INDEXES.get(parentId);
                if (parentIndex == null || !visible(states, parentIndex)) continue;
                counts[parentIndex]++;
                counts[childIndex]++;
            }
        }
        return counts;
    }

    private static double tendrilCollarRadius(int state, int nodeIndex, int connectionCount,
                                               boolean structural, int layer, float animationTime) {
        double stateScale = state == ConstellationManager.COMPLETED ? 0.78D
            : state == ConstellationManager.AVAILABLE ? 0.66D : 0.56D;
        double crowdScale = 1.0D / Math.sqrt(1.0D + Math.max(0, connectionCount - 1) * 0.22D);
        double edgeScale = structural ? 1.0D : 0.58D;
        double layerScale = layer == 0 ? 0.30D : layer == 1 ? 1.0D : 1.28D;
        return nodeVisualRadius(state, nodeIndex, animationTime)
            * stateScale * crowdScale * edgeScale * layerScale;
    }

    private static void appendTendrilTube(BufferBuilder buffer, int parentIndex, int childIndex,
                                           float animationTime, double baseRadius,
                                           double startSurfaceRadius, double endSurfaceRadius,
                                           double startCollarRadius, double endCollarRadius,
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
        }
        double startDirectionX = points[3] - points[0];
        double startDirectionY = points[4] - points[1];
        double startDirectionZ = points[5] - points[2];
        double startDirectionLength = Math.sqrt(startDirectionX * startDirectionX
            + startDirectionY * startDirectionY + startDirectionZ * startDirectionZ);
        int last = TENDRIL_SEGMENTS * 3;
        double endDirectionX = points[last] - points[last - 3];
        double endDirectionY = points[last + 1] - points[last - 2];
        double endDirectionZ = points[last + 2] - points[last - 1];
        double endDirectionLength = Math.sqrt(endDirectionX * endDirectionX
            + endDirectionY * endDirectionY + endDirectionZ * endDirectionZ);
        for (int ring = 0; ring <= TENDRIL_SEGMENTS; ring++) {
            double progress = ring / (double) TENDRIL_SEGMENTS;
            double startWeight = Math.pow(1.0D - progress, 3.0D);
            double endWeight = Math.pow(progress, 3.0D);
            int offset = ring * 3;
            points[offset] += startDirectionX / startDirectionLength * startSurfaceRadius * startWeight
                - endDirectionX / endDirectionLength * endSurfaceRadius * endWeight;
            points[offset + 1] += startDirectionY / startDirectionLength * startSurfaceRadius * startWeight
                - endDirectionY / endDirectionLength * endSurfaceRadius * endWeight;
            points[offset + 2] += startDirectionZ / startDirectionLength * startSurfaceRadius * startWeight
                - endDirectionZ / endDirectionLength * endSurfaceRadius * endWeight;
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
                    segment, side, baseRadius, startCollarRadius, endCollarRadius,
                    seed, animationTime, red, green, blue, alpha);
                tubeVertex(buffer, points, normals, binormals, energies,
                    segment, nextSide, baseRadius, startCollarRadius, endCollarRadius,
                    seed, animationTime, red, green, blue, alpha);
                tubeVertex(buffer, points, normals, binormals, energies,
                    segment + 1, nextSide, baseRadius, startCollarRadius, endCollarRadius,
                    seed, animationTime, red, green, blue, alpha);
                tubeVertex(buffer, points, normals, binormals, energies,
                    segment + 1, side, baseRadius, startCollarRadius, endCollarRadius,
                    seed, animationTime, red, green, blue, alpha);
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
                                   int ring, int side, double baseRadius,
                                   double startCollarRadius, double endCollarRadius, double seed,
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
        double startAttachment = Math.pow(1.0D - progress, 4.0D);
        double endAttachment = Math.pow(progress, 4.0D);
        double envelope = 0.78D + Math.sin(progress * Math.PI) * 0.22D;
        double surfaceRipple = 1.0D + Math.sin(side * Math.PI * 0.75D - progress * 11.0D
            + animationTime * 0.055D + seed) * 0.025D;
        double radius = baseRadius * envelope;
        radius += (startCollarRadius - baseRadius * 0.78D) * startAttachment
            + (endCollarRadius - baseRadius * 0.78D) * endAttachment;
        radius *= (breathing + energy * 0.06D) * surfaceRipple;
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
            if (!visible(states, i)) continue;
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
            double haloRadius = NODE_RADIUS * 3.9D * pulse;
            int centerAlpha = state == ConstellationManager.COMPLETED ? 82
                : state == ConstellationManager.AVAILABLE ? 64 : 24;
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
            int rayCount = state == ConstellationManager.MYSTERY ? 6 : 10;
            for (int rayIndex = 0; rayIndex < rayCount; rayIndex++) {
                double angle = rotation + rayIndex * Math.PI * 2.0D / rayCount;
                double length = NODE_RADIUS * pulse * (rayIndex % 2 == 0 ? 4.8D : 3.2D);
                double base = NODE_RADIUS * pulse * 0.18D;
                int rayAlpha = state == ConstellationManager.MYSTERY ? 42 : 142;
                double directionRight = Math.cos(angle);
                double directionUp = Math.sin(angle);
                double perpendicularRight = -directionUp;
                double perpendicularUp = directionRight;
                billboardOffsetVertex(buffer, x, y, z, rightX, rightY, rightZ, upX, upY, upZ,
                    perpendicularRight * base, perpendicularUp * base, red, green, blue, rayAlpha);
                billboardOffsetVertex(buffer, x, y, z, rightX, rightY, rightZ, upX, upY, upZ,
                    -perpendicularRight * base, -perpendicularUp * base, red, green, blue, rayAlpha);
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
            double coreRadius = nodeVisualRadius(state, i, animationTime);
            nodeStar(buffer, nodeX(i, animationTime), nodeY(i, animationTime), nodeZ(i, animationTime),
                coreRadius, red, green, blue,
                state == ConstellationManager.MYSTERY ? 165 : 255);
        }
        tessellator.draw();
    }

    private static double starPulse(int state, int index, float animationTime) {
        if (state == ConstellationManager.MYSTERY) return 0.90D;
        double strength = state == ConstellationManager.COMPLETED ? 1.0D : 0.65D;
        return 1.0D + Math.sin(animationTime * 0.040D + index * 0.73D) * 0.10D * strength
            + Math.sin(animationTime * 0.017D + index * 1.31D) * 0.04D * strength
            + Math.sin(animationTime * 0.155D + index * 2.43D) * 0.035D * strength;
    }

    private static double starScale(int state) {
        if (state == ConstellationManager.AVAILABLE) return 0.72D;
        if (state == ConstellationManager.MYSTERY) return 0.58D;
        return 1.0D;
    }

    private static double nodeVisualRadius(int state, int index, float animationTime) {
        return NODE_RADIUS * 1.10D * starPulse(state, index, animationTime) * starScale(state);
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

    private static void nodeStar(BufferBuilder buffer, double x, double y, double z, double radius,
                                 int red, int green, int blue, int alpha) {
        for (int face = 0; face < ICOSAHEDRON_FACES.length / 3; face++) {
            int first = ICOSAHEDRON_FACES[face * 3] * 3;
            int second = ICOSAHEDRON_FACES[face * 3 + 1] * 3;
            int third = ICOSAHEDRON_FACES[face * 3 + 2] * 3;
            double firstX = ICOSAHEDRON_VERTICES[first] * radius;
            double firstY = ICOSAHEDRON_VERTICES[first + 1] * radius;
            double firstZ = ICOSAHEDRON_VERTICES[first + 2] * radius;
            double secondX = ICOSAHEDRON_VERTICES[second] * radius;
            double secondY = ICOSAHEDRON_VERTICES[second + 1] * radius;
            double secondZ = ICOSAHEDRON_VERTICES[second + 2] * radius;
            double thirdX = ICOSAHEDRON_VERTICES[third] * radius;
            double thirdY = ICOSAHEDRON_VERTICES[third + 1] * radius;
            double thirdZ = ICOSAHEDRON_VERTICES[third + 2] * radius;
            double firstSecondLength = Math.sqrt((firstX + secondX) * (firstX + secondX)
                + (firstY + secondY) * (firstY + secondY) + (firstZ + secondZ) * (firstZ + secondZ));
            double secondThirdLength = Math.sqrt((secondX + thirdX) * (secondX + thirdX)
                + (secondY + thirdY) * (secondY + thirdY) + (secondZ + thirdZ) * (secondZ + thirdZ));
            double thirdFirstLength = Math.sqrt((thirdX + firstX) * (thirdX + firstX)
                + (thirdY + firstY) * (thirdY + firstY) + (thirdZ + firstZ) * (thirdZ + firstZ));
            double firstSecondX = (firstX + secondX) / firstSecondLength * radius;
            double firstSecondY = (firstY + secondY) / firstSecondLength * radius;
            double firstSecondZ = (firstZ + secondZ) / firstSecondLength * radius;
            double secondThirdX = (secondX + thirdX) / secondThirdLength * radius;
            double secondThirdY = (secondY + thirdY) / secondThirdLength * radius;
            double secondThirdZ = (secondZ + thirdZ) / secondThirdLength * radius;
            double thirdFirstX = (thirdX + firstX) / thirdFirstLength * radius;
            double thirdFirstY = (thirdY + firstY) / thirdFirstLength * radius;
            double thirdFirstZ = (thirdZ + firstZ) / thirdFirstLength * radius;
            int facet = face * 4;
            stellarTriangle(buffer, x, y, z, firstX, firstY, firstZ,
                firstSecondX, firstSecondY, firstSecondZ, thirdFirstX, thirdFirstY, thirdFirstZ,
                red, green, blue, alpha, facet);
            stellarTriangle(buffer, x, y, z, firstSecondX, firstSecondY, firstSecondZ,
                secondX, secondY, secondZ, secondThirdX, secondThirdY, secondThirdZ,
                red, green, blue, alpha, facet + 1);
            stellarTriangle(buffer, x, y, z, thirdFirstX, thirdFirstY, thirdFirstZ,
                secondThirdX, secondThirdY, secondThirdZ, thirdX, thirdY, thirdZ,
                red, green, blue, alpha, facet + 2);
            stellarTriangle(buffer, x, y, z, firstSecondX, firstSecondY, firstSecondZ,
                secondThirdX, secondThirdY, secondThirdZ, thirdFirstX, thirdFirstY, thirdFirstZ,
                red, green, blue, alpha, facet + 3);
        }
    }

    private static void stellarTriangle(BufferBuilder buffer, double centerX, double centerY, double centerZ,
                                        double firstX, double firstY, double firstZ,
                                        double secondX, double secondY, double secondZ,
                                        double thirdX, double thirdY, double thirdZ,
                                        int red, int green, int blue, int alpha, int facet) {
        double edgeOneX = secondX - firstX;
        double edgeOneY = secondY - firstY;
        double edgeOneZ = secondZ - firstZ;
        double edgeTwoX = thirdX - firstX;
        double edgeTwoY = thirdY - firstY;
        double edgeTwoZ = thirdZ - firstZ;
        double normalX = edgeOneY * edgeTwoZ - edgeOneZ * edgeTwoY;
        double normalY = edgeOneZ * edgeTwoX - edgeOneX * edgeTwoZ;
        double normalZ = edgeOneX * edgeTwoY - edgeOneY * edgeTwoX;
        double centroidX = firstX + secondX + thirdX;
        double centroidY = firstY + secondY + thirdY;
        double centroidZ = firstZ + secondZ + thirdZ;
        if (normalX * centroidX + normalY * centroidY + normalZ * centroidZ < 0.0D) {
            double swapX = secondX;
            double swapY = secondY;
            double swapZ = secondZ;
            secondX = thirdX;
            secondY = thirdY;
            secondZ = thirdZ;
            thirdX = swapX;
            thirdY = swapY;
            thirdZ = swapZ;
            normalX = -normalX;
            normalY = -normalY;
            normalZ = -normalZ;
        }
        double normalLength = Math.sqrt(normalX * normalX + normalY * normalY + normalZ * normalZ);
        double light = Math.max(0.0D, (normalX * 0.34D + normalY * 0.78D + normalZ * 0.52D)
            / normalLength);
        double shade = 0.34D + light * 0.58D + (facet % 5) * 0.016D;
        int facetRed = Math.min(255, (int) (red * shade));
        int facetGreen = Math.min(255, (int) (green * shade));
        int facetBlue = Math.min(255, (int) (blue * shade));
        vertex(buffer, centerX + firstX, centerY + firstY, centerZ + firstZ,
            facetRed, facetGreen, facetBlue, alpha);
        vertex(buffer, centerX + secondX, centerY + secondY, centerZ + secondZ,
            facetRed, facetGreen, facetBlue, alpha);
        vertex(buffer, centerX + thirdX, centerY + thirdY, centerZ + thirdZ,
            facetRed, facetGreen, facetBlue, alpha);
    }

    private static double[] createIcosahedronVertices() {
        double phi = (1.0D + Math.sqrt(5.0D)) * 0.5D;
        double scale = 1.0D / Math.sqrt(1.0D + phi * phi);
        return new double[] {
            -scale, phi * scale, 0.0D, scale, phi * scale, 0.0D,
            -scale, -phi * scale, 0.0D, scale, -phi * scale, 0.0D,
            0.0D, -scale, phi * scale, 0.0D, scale, phi * scale,
            0.0D, -scale, -phi * scale, 0.0D, scale, -phi * scale,
            phi * scale, 0.0D, -scale, phi * scale, 0.0D, scale,
            -phi * scale, 0.0D, -scale, -phi * scale, 0.0D, scale
        };
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

    private static String nodeHint(int index, boolean mystery) {
        if (mystery) return "Complete the blue achievement leading to this path.";
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.getConnection() == null) return "Continue this achievement branch.";
        Advancement advancement = minecraft.getConnection().getAdvancementManager().getAdvancementList()
            .getAdvancement(new ResourceLocation(NODES[index].id));
        if (advancement == null || advancement.getDisplay() == null) {
            return "Continue this achievement branch.";
        }
        String description = advancement.getDisplay().getDescription().getUnformattedText().trim();
        if (description.startsWith("First inventory acquisition")
            && !advancement.getDisplay().getIcon().isEmpty()) {
            return "Acquire " + advancement.getDisplay().getIcon().getDisplayName() + ".";
        }
        int clauseEnd = description.indexOf(';');
        if (clauseEnd > 0) description = description.substring(0, clauseEnd);
        if (description.length() > 108) {
            int breakAt = description.lastIndexOf(' ', 105);
            description = description.substring(0, breakAt > 40 ? breakAt : 105) + "...";
        }
        return description;
    }

    private void renderNodeLabel(String title, String hint, int index,
                                 double sceneX, double sceneY, double sceneZ, float animationTime) {
        FontRenderer font = getFontRendererFromRenderManager();
        float scale = 0.025F;
        List<String> hintLines = font.listFormattedStringToWidth(hint, 220);
        if (hintLines.size() > 3) hintLines = hintLines.subList(0, 3);
        int width = font.getStringWidth(title);
        for (String line : hintLines) width = Math.max(width, font.getStringWidth(line));
        int halfWidth = width / 2;
        int height = 12 + hintLines.size() * 9;
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
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        buffer.pos(-halfWidth - 2, -2, 0.0D).color(10, 18, 30, 190).endVertex();
        buffer.pos(-halfWidth - 2, height, 0.0D).color(10, 18, 30, 190).endVertex();
        buffer.pos(halfWidth + 2, height, 0.0D).color(10, 18, 30, 190).endVertex();
        buffer.pos(halfWidth + 2, -2, 0.0D).color(10, 18, 30, 190).endVertex();
        tessellator.draw();
        font.drawString(title, -font.getStringWidth(title) / 2, 0, 0xFFF7FBFF);
        for (int lineIndex = 0; lineIndex < hintLines.size(); lineIndex++) {
            String line = hintLines.get(lineIndex);
            font.drawString(line, -font.getStringWidth(line) / 2, 11 + lineIndex * 9, 0xFFB8C9DA);
        }
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.popMatrix();
    }
}
