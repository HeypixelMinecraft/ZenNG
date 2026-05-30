package com.mihoyo.zen.modules.impl.render;

import com.mihoyo.zen.event.impl.GlRenderEvent;
import com.mihoyo.zen.event.impl.Render2DEvent;
import com.mihoyo.zen.gui.svelte.SvelteHudOverlay;
import com.mihoyo.zen.hud.DynamicIsland;
import com.mihoyo.zen.modules.Category;
import com.mihoyo.zen.modules.Module;
import com.mihoyo.zen.settings.impl.ModeSetting;
import com.mihoyo.zen.event.EventTarget;

public class Watermark extends Module {
    final ModeSetting styleSetting = new ModeSetting("Style", "Neverlose", "DynamicIsland").withDefault("DynamicIsland");
    private final DynamicIsland dynamicIsland = new DynamicIsland();

    public Watermark() {
        super("Watermark", Category.RENDER);
    }

    public String getStyle() {
        return this.styleSetting.getValue();
    }

    @EventTarget
    public void onRender2D(Render2DEvent render2DEvent) {
        if (SvelteHudOverlay.isActive()) {
            return;
        }
        if (!this.isEnabled()) {
            return;
        }
        switch (this.styleSetting.getValue()) {
            case "DynamicIsland":
                this.dynamicIsland.onRender2D(render2DEvent);
                break;
        }
    }

    @EventTarget
    public void onGlRender(GlRenderEvent glRenderEvent) {
        if (SvelteHudOverlay.isActive()) {
            return;
        }
        if (!this.isEnabled()) {
            return;
        }
    }
}
