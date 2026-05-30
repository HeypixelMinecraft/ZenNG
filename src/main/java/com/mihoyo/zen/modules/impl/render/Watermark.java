package com.mihoyo.zen.modules.impl.render;

import com.mihoyo.zen.event.impl.GlRenderEvent;
import com.mihoyo.zen.event.impl.Render2DEvent;
import com.mihoyo.zen.hud.DynamicIsland;
import com.mihoyo.zen.hud.NeverloseWatermark;
import com.mihoyo.zen.modules.Category;
import com.mihoyo.zen.modules.Module;
import com.mihoyo.zen.settings.impl.ModeSetting;
import com.mihoyo.zen.event.EventTarget;

public class Watermark extends Module {
    final ModeSetting styleSetting = new ModeSetting("Style", "Neverlose", "DynamicIsland").withDefault("DynamicIsland");
    private final DynamicIsland dynamicIsland = new DynamicIsland();
    private final NeverloseWatermark neverloseWatermark = new NeverloseWatermark();

    public Watermark() {
        super("Watermark", Category.RENDER);
    }

    @EventTarget
    public void onRender2D(Render2DEvent render2DEvent) {
        if (!this.isEnabled()) {
            return;
        }
        switch (this.styleSetting.getValue()) {
            case "Neverlose":
                this.neverloseWatermark.onRender2D(render2DEvent);
                break;
            case "DynamicIsland":
                this.dynamicIsland.onRender2D(render2DEvent);
                break;
        }
    }

    @EventTarget
    public void onGlRender(GlRenderEvent glRenderEvent) {
        if (!this.isEnabled()) {
            return;
        }
        if ("Neverlose".equals(this.styleSetting.getValue())) {
            this.neverloseWatermark.onGlRender(glRenderEvent);
        }
    }
}
