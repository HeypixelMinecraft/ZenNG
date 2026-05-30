package com.mihoyo.zen.manager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import com.mihoyo.zen.ClientBase;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.event.impl.GlRenderEvent;
import com.mihoyo.zen.event.impl.Render2DEvent;
import com.mihoyo.zen.event.impl.TickEvent;
import com.mihoyo.zen.gui.svelte.SvelteHudOverlay;
import com.mihoyo.zen.gui.IntroAnimation;
import com.mihoyo.zen.hud.HudElement;
import com.mihoyo.zen.hud.KeyBindsHud;
import com.mihoyo.zen.hud.LieDetector;
import com.mihoyo.zen.hud.ModuleListHud;
import com.mihoyo.zen.hud.PlayerListHud;
import com.mihoyo.zen.hud.PotionEffectsHud;
import com.mihoyo.zen.hud.TargetHud;
import com.mihoyo.zen.event.EventTarget;

public class HudManager {
    private final Map<String, HudElement> hudElements = new HashMap<>();

    public HudManager() {
        this.init();
    }

    public void init() {
        this.registerHudElement(new TargetHud());
        this.registerHudElement(new KeyBindsHud());
        this.registerHudElement(new ModuleListHud());
        this.registerHudElement(new PlayerListHud());
        this.registerHudElement(new PotionEffectsHud());
        this.registerHudElement(new LieDetector());
    }

    private void registerHudElement(HudElement hudElement) {
        ZenClient.getInstance().getModuleManager().register(hudElement);
        this.hudElements.put(hudElement.getClass().getSimpleName(), hudElement);
    }

    public <T extends HudElement> T getHudElement(Class<T> clazz) {
        return clazz.cast(this.hudElements.get(clazz.getSimpleName()));
    }

    public HudElement getHudElementByName(String string) {
        return this.hudElements.values().stream().filter(hudElement -> hudElement.getName().equalsIgnoreCase(string)).findFirst().orElse(null);
    }

    public Collection<HudElement> getHudElements() {
        return this.hudElements.values();
    }

    @EventTarget
    public void onTick(TickEvent tickEvent) {
        if (ClientBase.mc.screen == null) {
            try {
                for (HudElement hudElement : ZenClient.getInstance().getHudManager().getHudElements()) {
                    hudElement.stopDragging();
                }
            } catch (Exception exception) {
                ClientBase.logger.error(exception);
                ClientBase.logger.error(exception.getMessage());
            }
        }
    }

    @EventTarget
    public void onRender2D(Render2DEvent render2DEvent) {
        if (IntroAnimation.isRunning()) {
            return;
        }
        if (SvelteHudOverlay.render(render2DEvent)) {
            return;
        }
        for (HudElement hudElement : this.getHudElements()) {
            if (!hudElement.isEnabled()) continue;
            hudElement.onRender2D(render2DEvent, hudElement.getX(), hudElement.getY());
        }
    }

    @EventTarget
    public void onGlRender(GlRenderEvent glRenderEvent) {
        if (IntroAnimation.isRunning()) {
            return;
        }
        if (SvelteHudOverlay.isActive()) {
            return;
        }
        for (HudElement hudElement : this.getHudElements()) {
            if (!hudElement.isEnabled()) continue;
            hudElement.onGlRender(glRenderEvent, hudElement.getX(), hudElement.getY());
        }
    }
}
