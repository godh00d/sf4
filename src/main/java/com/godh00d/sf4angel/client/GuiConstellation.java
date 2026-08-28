package com.godh00d.sf4angel.client;

import com.godh00d.sf4angel.constellation.AchievementConstellationCatalog;
import com.godh00d.sf4angel.constellation.ConstellationManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.Map;

public final class GuiConstellation extends GuiScreen {

    private AchievementConstellationCatalog.Node[] nodes;
    private Map<String, Integer> indexes;
    private byte[] states;
    private int[] screenX;
    private int[] screenY;
    private int hovered = -1;

    @Override
    public void initGui() {
        nodes = AchievementConstellationCatalog.nodes();
        indexes = AchievementConstellationCatalog.indexes();
        states = ConstellationClientState.states();
        screenX = new int[nodes.length];
        screenY = new int[nodes.length];
        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 - 50, height - 25, 100, 20, "Return"));
        projectTree();
    }

    private void projectTree() {
        double minHorizontal = Double.MAX_VALUE;
        double maxHorizontal = -Double.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (AchievementConstellationCatalog.Node node : nodes) {
            double horizontal = node.z + (node.x - 40) * 0.35D;
            minHorizontal = Math.min(minHorizontal, horizontal);
            maxHorizontal = Math.max(maxHorizontal, horizontal);
            minY = Math.min(minY, node.y);
            maxY = Math.max(maxY, node.y);
        }
        double horizontalRange = Math.max(1.0D, maxHorizontal - minHorizontal);
        double verticalRange = Math.max(1.0D, maxY - minY);
        double scale = Math.min((width - 36.0D) / horizontalRange, (height - 86.0D) / verticalRange);
        double center = (minHorizontal + maxHorizontal) * 0.5D;
        for (int i = 0; i < nodes.length; i++) {
            double horizontal = nodes[i].z + (nodes[i].x - 40) * 0.35D;
            screenX[i] = (int) Math.round(width * 0.5D + (horizontal - center) * scale);
            screenY[i] = (int) Math.round(height - 39.0D - (nodes[i].y - minY) * scale);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawGradientRect(0, 0, width, height, 0xFF080817, 0xFF11102B);
        hovered = findNode(mouseX, mouseY);
        drawBranches();
        for (int i = 0; i < nodes.length; i++) drawNode(i);

        drawCenteredString(fontRenderer, "Achievement Constellation", width / 2, 9, 0xE8F7FF);
        drawCenteredString(fontRenderer, "Every star is at least five blocks apart in the 3D catalog", width / 2, 21, 0x8FB5C8);
        if (hovered >= 0) {
            String state = stateName(stateAt(hovered));
            drawCenteredString(fontRenderer, nodes[hovered].title + "  |  " + state,
                width / 2, height - 36, 0xFFFFFF);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawBranches() {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
            GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
            GlStateManager.DestFactor.ZERO);
        GlStateManager.glLineWidth(1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
        for (int child = 0; child < nodes.length; child++) {
            for (String parentId : nodes[child].parents) {
                Integer parent = indexes.get(parentId);
                if (parent == null) continue;
                boolean highlighted = hovered == child || hovered == parent;
                float alpha = highlighted ? 0.95F : 0.34F;
                buffer.pos(screenX[parent], screenY[parent], 0).color(0.28F, 0.72F, 0.82F, alpha).endVertex();
                buffer.pos(screenX[child], screenY[child], 0).color(0.48F, 0.94F, 1.0F, alpha).endVertex();
            }
        }
        tessellator.draw();
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private void drawNode(int index) {
        byte state = stateAt(index);
        int color = state == ConstellationManager.COMPLETED ? 0xFFFFB52E
            : state == ConstellationManager.AVAILABLE ? 0xFF55EAF2
            : state == ConstellationManager.MYSTERY ? 0xFFB48AEE : 0xFF3D4259;
        int radius = hovered == index ? 4 : 2;
        drawRect(screenX[index] - radius, screenY[index] - radius,
            screenX[index] + radius + 1, screenY[index] + radius + 1, color);
        if (hovered == index) {
            drawRect(screenX[index] - 6, screenY[index] - 6, screenX[index] + 7, screenY[index] - 5, 0xFFFFFFFF);
            drawRect(screenX[index] - 6, screenY[index] + 6, screenX[index] + 7, screenY[index] + 7, 0xFFFFFFFF);
            drawRect(screenX[index] - 6, screenY[index] - 5, screenX[index] - 5, screenY[index] + 6, 0xFFFFFFFF);
            drawRect(screenX[index] + 6, screenY[index] - 5, screenX[index] + 7, screenY[index] + 6, 0xFFFFFFFF);
        }
    }

    private int findNode(int mouseX, int mouseY) {
        int nearest = -1;
        int nearestDistance = 49;
        for (int i = 0; i < nodes.length; i++) {
            int dx = screenX[i] - mouseX;
            int dy = screenY[i] - mouseY;
            int distance = dx * dx + dy * dy;
            if (distance < nearestDistance) {
                nearest = i;
                nearestDistance = distance;
            }
        }
        return nearest;
    }

    private byte stateAt(int index) {
        return states.length == nodes.length ? states[index] : ConstellationManager.MYSTERY;
    }

    private static String stateName(byte state) {
        if (state == ConstellationManager.COMPLETED) return "Completed";
        if (state == ConstellationManager.AVAILABLE) return "Available";
        if (state == ConstellationManager.MYSTERY) return "Mystery";
        return "Hidden";
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) mc.displayGuiScreen(null);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true;
    }
}
