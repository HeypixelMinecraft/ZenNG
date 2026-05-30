package com.mihoyo.zen.gui.svelte;

import com.mihoyo.zen.ClientBase;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.event.impl.Render2DEvent;
import com.mihoyo.zen.modules.impl.render.Interface;
import com.mihoyo.zen.network.webui.WebUiServer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;

public final class SvelteHudOverlay {
    private static McefBrowserBridge browser;
    private static long browserCreatedAt;
    private static long lastRenderAt;
    private static int lastWidth;
    private static int lastHeight;

    private SvelteHudOverlay() {
    }

    public static boolean shouldUseWebHud() {
        try {
            Interface interfaceModule = ZenClient.getInstance().getModuleManager().getModule(Interface.class);
            return interfaceModule.svelteHud.getValue();
        } catch (Throwable throwable) {
            return false;
        }
    }

    public static boolean isActive() {
        return shouldUseWebHud() && System.currentTimeMillis() - lastRenderAt < 250L;
    }

    static boolean preload() {
        if (!WebUiServer.ensureStarted(false) || !McefBrowserBridge.isReady()) {
            return false;
        }
        Minecraft mc = ClientBase.mc;
        if (mc == null || mc.getWindow() == null) {
            return false;
        }
        if (browser == null) {
            browser = McefBrowserBridge.create(WebUiServer.SVELTE_HUD_URL, true);
            if (browser == null) {
                return false;
            }
            browser.focus(false);
            browserCreatedAt = System.currentTimeMillis();
            lastWidth = 0;
            lastHeight = 0;
        }
        int browserWidth = Math.max(1, mc.getWindow().getGuiScaledWidth());
        int browserHeight = Math.max(1, mc.getWindow().getGuiScaledHeight());
        if (browserWidth != lastWidth || browserHeight != lastHeight) {
            browser.resize(browserWidth, browserHeight);
            lastWidth = browserWidth;
            lastHeight = browserHeight;
        }
        return true;
    }

    public static boolean render(Render2DEvent event) {
        if (!shouldUseWebHud() || !WebUiServer.ensureStarted(false) || !McefBrowserBridge.isReady()) {
            lastRenderAt = 0L;
            return false;
        }
        Minecraft mc = ClientBase.mc;
        if (mc == null || mc.getWindow() == null) {
            return false;
        }
        if (!preload()) {
            lastRenderAt = 0L;
            return false;
        }

        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        int browserWidth = Math.max(1, width);
        int browserHeight = Math.max(1, height);
        if (browserWidth != lastWidth || browserHeight != lastHeight) {
            browser.resize(browserWidth, browserHeight);
            lastWidth = browserWidth;
            lastHeight = browserHeight;
        }

        int textureId = browser.getTextureId();
        if (textureId == 0 || System.currentTimeMillis() - browserCreatedAt < 120L) {
            lastRenderAt = 0L;
            return false;
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
        lastRenderAt = System.currentTimeMillis();
        return true;
    }
}
