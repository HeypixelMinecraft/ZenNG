package com.mihoyo.zen.gui.svelte;

import com.mihoyo.zen.ClientBase;
import com.mihoyo.zen.gui.PanelClickGui;
import com.mihoyo.zen.network.webui.WebUiServer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

public class SvelteClickGui extends Screen {
    private static McefBrowserBridge sharedBrowser;
    private static long sharedBrowserCreatedAt;

    private McefBrowserBridge browser;

    public SvelteClickGui() {
        super(Component.literal("Zen ClickGUI"));
    }

    public static boolean canOpen() {
        return WebUiServer.ensureStarted(false) && McefBrowserBridge.isReady();
    }

    @Override
    protected void init() {
        super.init();
        if (!WebUiServer.ensureStarted(false) || !McefBrowserBridge.isReady()) {
            ClientBase.logger.warn("MCEF is not ready, falling back to PanelClickGui");
            minecraft.setScreen(PanelClickGui.panelClickGui);
            return;
        }
        if (sharedBrowser == null) {
            sharedBrowser = McefBrowserBridge.create(WebUiServer.SVELTE_GUI_URL + "?ingame=1", true);
            if (sharedBrowser == null) {
                minecraft.setScreen(PanelClickGui.panelClickGui);
                return;
            }
            sharedBrowserCreatedAt = System.currentTimeMillis();
        }
        browser = sharedBrowser;
        browser.focus(true);
        resizeBrowser();
    }

    private int browserX(double x) {
        return (int)(x * minecraft.getWindow().getGuiScale());
    }

    private int browserY(double y) {
        return (int)(y * minecraft.getWindow().getGuiScale());
    }

    private void resizeBrowser() {
        if (browser != null && width > 0 && height > 0) {
            double scale = minecraft.getWindow().getGuiScale();
            browser.resize(Math.max(1, (int)(width * scale)), Math.max(1, (int)(height * scale)));
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        resizeBrowser();
    }

    @Override
    public void onClose() {
        if (browser != null) {
            browser.focus(false);
        }
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (browser == null) {
            super.render(guiGraphics, mouseX, mouseY, partialTick);
            return;
        }
        int textureId = browser.getTextureId();
        if (textureId == 0 || System.currentTimeMillis() - sharedBrowserCreatedAt < 120L) {
            super.render(guiGraphics, mouseX, mouseY, partialTick);
            return;
        }
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, textureId);
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        buffer.vertex(0.0, height, 0.0).uv(0.0f, 1.0f).color(255, 255, 255, 255).endVertex();
        buffer.vertex(width, height, 0.0).uv(1.0f, 1.0f).color(255, 255, 255, 255).endVertex();
        buffer.vertex(width, 0.0, 0.0).uv(1.0f, 0.0f).color(255, 255, 255, 255).endVertex();
        buffer.vertex(0.0, 0.0, 0.0).uv(0.0f, 0.0f).color(255, 255, 255, 255).endVertex();
        tesselator.end();
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (browser != null) {
            browser.mousePress(browserX(mouseX), browserY(mouseY), button);
            browser.focus(true);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (browser != null) {
            browser.mouseRelease(browserX(mouseX), browserY(mouseY), button);
            browser.focus(true);
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (browser != null) {
            browser.mouseMove(browserX(mouseX), browserY(mouseY));
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (browser != null) {
            browser.mouseWheel(browserX(mouseX), browserY(mouseY), delta);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (browser != null) {
            browser.keyPress(keyCode, scanCode, modifiers);
            browser.focus(true);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (browser != null) {
            browser.keyRelease(keyCode, scanCode, modifiers);
            browser.focus(true);
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (browser != null && codePoint != 0) {
            browser.keyTyped(codePoint, modifiers);
            browser.focus(true);
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }
}
