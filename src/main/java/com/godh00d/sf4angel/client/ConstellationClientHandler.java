package com.godh00d.sf4angel.client;

import com.godh00d.sf4angel.constellation.AchievementConstellationCatalog;
import com.godh00d.sf4angel.constellation.ConstellationDimension;
import com.godh00d.sf4angel.constellation.ConstellationManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.opengl.GL11;

public final class ConstellationClientHandler {

    private static final double CUBE_RADIUS = 0.34D;
    private static String hoveredTitle;

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld().isRemote && event.getWorld().provider.getDimension()
            != ConstellationDimension.getDimensionId()) {
            ConstellationClientState.clear();
            hoveredTitle = null;
        }
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        byte[] states = ConstellationClientState.states();
        Minecraft minecraft = Minecraft.getMinecraft();
        if (!isConstellation() || states.length != AchievementConstellationCatalog.COUNT
            || minecraft.getRenderViewEntity() == null) {
            hoveredTitle = null;
            return;
        }

        AchievementConstellationCatalog.Node[] nodes = AchievementConstellationCatalog.nodes();
        Entity camera = minecraft.getRenderViewEntity();
        float partial = event.getPartialTicks();
        double cameraX = camera.lastTickPosX + (camera.posX - camera.lastTickPosX) * partial;
        double cameraY = camera.lastTickPosY + (camera.posY - camera.lastTickPosY) * partial;
        double cameraZ = camera.lastTickPosZ + (camera.posZ - camera.lastTickPosZ) * partial;

        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(-cameraX, -cameraY, -cameraZ);
            GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();
            GlStateManager.disableCull();
            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO);
            GlStateManager.depthMask(false);
            GlStateManager.glLineWidth(1.5F);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
            for (int i = 0; i < nodes.length; i++) {
                if (states[i] == ConstellationManager.ABSENT) continue;
                for (String parentId : nodes[i].parents) {
                    Integer parent = AchievementConstellationCatalog.indexes().get(parentId);
                    if (parent == null || states[parent] == ConstellationManager.ABSENT) continue;
                    buffer.pos(nodes[parent].x, nodes[parent].y, nodes[parent].z).color(0.2F, 0.62F, 0.78F, 0.9F).endVertex();
                    buffer.pos(nodes[i].x, nodes[i].y, nodes[i].z).color(0.35F, 0.9F, 1.0F, 0.9F).endVertex();
                }
            }
            tessellator.draw();

            for (int i = 0; i < nodes.length; i++) {
                if (states[i] == ConstellationManager.ABSENT) continue;
                float red = states[i] == ConstellationManager.COMPLETED ? 1.0F
                    : states[i] == ConstellationManager.AVAILABLE ? 0.12F : 0.55F;
                float green = states[i] == ConstellationManager.COMPLETED ? 0.72F
                    : states[i] == ConstellationManager.AVAILABLE ? 0.9F : 0.62F;
                float blue = states[i] == ConstellationManager.COMPLETED ? 0.12F
                    : states[i] == ConstellationManager.AVAILABLE ? 1.0F : 0.72F;
                float alpha = states[i] == ConstellationManager.MYSTERY ? 0.45F : 0.95F;
                AxisAlignedBB box = box(nodes[i]);
                drawSolidBox(buffer, tessellator, box, red, green, blue, alpha);
                if (states[i] == ConstellationManager.COMPLETED) {
                    RenderGlobal.drawSelectionBoundingBox(box, 0.5F, 0.28F, 0.02F, 1.0F);
                } else if (states[i] == ConstellationManager.AVAILABLE) {
                    RenderGlobal.drawSelectionBoundingBox(box, 0.0F, 0.36F, 0.52F, 1.0F);
                } else {
                    RenderGlobal.drawSelectionBoundingBox(box, 0.22F, 0.26F, 0.34F, 0.75F);
                }
            }

            GlStateManager.enableTexture2D();
            for (int i = 0; i < nodes.length; i++) {
                if (states[i] == ConstellationManager.MYSTERY) renderLabel("?", nodes[i]);
            }
        } finally {
            try {
                GlStateManager.popMatrix();
            } finally {
                restoreRenderState();
            }
        }
        updateHover(nodes, states, partial);
    }

    @SubscribeEvent
    public void onOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL || !isConstellation()) return;
        Minecraft minecraft = Minecraft.getMinecraft();
        ScaledResolution resolution = event.getResolution();
        String heading = ConstellationClientState.states().length == AchievementConstellationCatalog.COUNT
            ? "Achievement Constellation" : "Loading Achievement Constellation...";
        drawCentered(minecraft, resolution, heading, 10, 0xE8F7FF);
        drawCentered(minecraft, resolution,
            "Fly forward to explore | Aim at a cube for its name",
            22, 0xA9C8D8);
        drawCentered(minecraft, resolution, "Right-click the angel to return", 34, 0xA9C8D8);
        if (hoveredTitle != null) {
            drawCentered(minecraft, resolution, hoveredTitle,
                resolution.getScaledHeight() / 2 + 12, 0xFFFFFF);
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        ConstellationClientState.clear();
        hoveredTitle = null;
    }

    private static void updateHover(AchievementConstellationCatalog.Node[] nodes, byte[] states, float partial) {
        Minecraft minecraft = Minecraft.getMinecraft();
        Entity viewer = minecraft.getRenderViewEntity();
        if (viewer == null) return;
        Vec3d start = viewer.getPositionEyes(partial);
        Vec3d end = start.add(viewer.getLook(partial).scale(96.0D));
        double nearest = Double.MAX_VALUE;
        String title = null;
        for (int i = 0; i < nodes.length; i++) {
            if (states[i] == ConstellationManager.ABSENT) continue;
            RayTraceResult hit = box(nodes[i]).grow(0.18D).calculateIntercept(start, end);
            if (hit == null) continue;
            double distance = start.squareDistanceTo(hit.hitVec);
            if (distance < nearest) {
                nearest = distance;
                title = states[i] == ConstellationManager.MYSTERY ? "?" : nodes[i].title;
            }
        }
        hoveredTitle = title;
    }

    private static AxisAlignedBB box(AchievementConstellationCatalog.Node node) {
        return new AxisAlignedBB(node.x - CUBE_RADIUS, node.y - CUBE_RADIUS, node.z - CUBE_RADIUS,
            node.x + CUBE_RADIUS, node.y + CUBE_RADIUS, node.z + CUBE_RADIUS);
    }

    private static void drawSolidBox(BufferBuilder buffer, Tessellator tessellator, AxisAlignedBB box,
                                     float red, float green, float blue, float alpha) {
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        vertex(buffer, box.minX, box.minY, box.minZ, red, green, blue, alpha); vertex(buffer, box.maxX, box.minY, box.minZ, red, green, blue, alpha);
        vertex(buffer, box.maxX, box.maxY, box.minZ, red, green, blue, alpha); vertex(buffer, box.minX, box.maxY, box.minZ, red, green, blue, alpha);
        vertex(buffer, box.minX, box.minY, box.maxZ, red, green, blue, alpha); vertex(buffer, box.minX, box.maxY, box.maxZ, red, green, blue, alpha);
        vertex(buffer, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha); vertex(buffer, box.maxX, box.minY, box.maxZ, red, green, blue, alpha);
        vertex(buffer, box.minX, box.minY, box.minZ, red, green, blue, alpha); vertex(buffer, box.minX, box.minY, box.maxZ, red, green, blue, alpha);
        vertex(buffer, box.maxX, box.minY, box.maxZ, red, green, blue, alpha); vertex(buffer, box.maxX, box.minY, box.minZ, red, green, blue, alpha);
        vertex(buffer, box.minX, box.maxY, box.minZ, red, green, blue, alpha); vertex(buffer, box.maxX, box.maxY, box.minZ, red, green, blue, alpha);
        vertex(buffer, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha); vertex(buffer, box.minX, box.maxY, box.maxZ, red, green, blue, alpha);
        vertex(buffer, box.minX, box.minY, box.minZ, red, green, blue, alpha); vertex(buffer, box.minX, box.maxY, box.minZ, red, green, blue, alpha);
        vertex(buffer, box.minX, box.maxY, box.maxZ, red, green, blue, alpha); vertex(buffer, box.minX, box.minY, box.maxZ, red, green, blue, alpha);
        vertex(buffer, box.maxX, box.minY, box.minZ, red, green, blue, alpha); vertex(buffer, box.maxX, box.minY, box.maxZ, red, green, blue, alpha);
        vertex(buffer, box.maxX, box.maxY, box.maxZ, red, green, blue, alpha); vertex(buffer, box.maxX, box.maxY, box.minZ, red, green, blue, alpha);
        tessellator.draw();
    }

    private static void vertex(BufferBuilder buffer, double x, double y, double z,
                               float red, float green, float blue, float alpha) {
        buffer.pos(x, y, z).color(red, green, blue, alpha).endVertex();
    }

    private static void renderLabel(String text, AchievementConstellationCatalog.Node node) {
        Minecraft minecraft = Minecraft.getMinecraft();
        GlStateManager.pushMatrix();
        try {
            GlStateManager.translate(node.x, node.y + 0.55D, node.z);
            GlStateManager.rotate(-minecraft.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(minecraft.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
            float scale = 0.025F;
            GlStateManager.scale(-scale, -scale, scale);
            minecraft.fontRenderer.drawString(text,
                -minecraft.fontRenderer.getStringWidth(text) / 2, 0, 0xEAF8FF);
        } finally {
            GlStateManager.popMatrix();
        }
    }

    private static boolean isConstellation() {
        Minecraft minecraft = Minecraft.getMinecraft();
        return minecraft.world != null && minecraft.world.provider.getDimension()
            == ConstellationDimension.getDimensionId();
    }

    private static void drawCentered(Minecraft minecraft, ScaledResolution resolution,
                                     String text, int y, int color) {
        int width = minecraft.fontRenderer.getStringWidth(text);
        minecraft.fontRenderer.drawString(text, (resolution.getScaledWidth() - width) / 2, y, color);
    }

    private static void restoreRenderState() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.glLineWidth(1.0F);
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableCull();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableTexture2D();
        GlStateManager.enableFog();
    }

}
