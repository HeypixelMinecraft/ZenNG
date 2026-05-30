package com.mihoyo.zen.gui.svelte;

import com.mihoyo.zen.ClientBase;
import com.mihoyo.zen.network.webui.WebUiServer;

public final class SveltePreloader {
    private static long lastAttemptAt;
    private static boolean completed;

    private SveltePreloader() {
    }

    public static boolean preloadAll() {
        if (completed) {
            return true;
        }
        long now = System.currentTimeMillis();
        if (now - lastAttemptAt < 1000L) {
            return false;
        }
        lastAttemptAt = now;
        if (!WebUiServer.ensureStarted(false) || !McefBrowserBridge.isReady()) {
            return false;
        }
        boolean clickGuiReady = SvelteClickGui.preload();
        boolean hudReady = SvelteHudOverlay.preload();
        completed = clickGuiReady && hudReady;
        if (completed) {
            ClientBase.logger.info("Preloaded Svelte/MCEF ClickGUI and HUD pages.");
        }
        return completed;
    }
}
