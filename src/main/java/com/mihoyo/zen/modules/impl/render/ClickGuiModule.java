package com.mihoyo.zen.modules.impl.render;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mihoyo.zen.gui.NewClickGui;
import com.mihoyo.zen.gui.OldClickGui;
import com.mihoyo.zen.gui.PanelClickGui;
import com.mihoyo.zen.gui.svelte.SvelteClickGui;
import com.mihoyo.zen.modules.Category;
import com.mihoyo.zen.modules.Module;
import com.mihoyo.zen.settings.impl.ModeSetting;
import com.mihoyo.zen.utils.misc.ChatUtil;

public class ClickGuiModule
extends Module {
    public static final Logger LOGGER = LogManager.getLogger(ClickGuiModule.class);
    public final ModeSetting styleSetting = new ModeSetting("Mode", "Svelte", "Old", "Panel", "New").withDefault("Svelte");

    public ClickGuiModule() {
        super("ClickGui", Category.RENDER, 344);
    }

    @Override
    protected void onEnable() {
        try {
            if (this.styleSetting.is("Svelte")) {
                if (SvelteClickGui.canOpen()) {
                    mc.setScreen(new SvelteClickGui());
                } else {
                    LOGGER.warn("Svelte ClickGUI is unavailable. Install/load MCEF and wait until it finishes initializing.");
                    ChatUtil.print("Svelte ClickGUI unavailable: MCEF is not loaded or not initialized.");
                    mc.setScreen(null);
                }
            } else if (this.styleSetting.is("Old")) {
                mc.setScreen(new OldClickGui());
            } else if (this.styleSetting.is("Panel")) {
                mc.setScreen(PanelClickGui.panelClickGui);
            } else {
                mc.setScreen(new NewClickGui());
            }
            LOGGER.info("ClickGUI opened successfully");
        } catch (Exception exception) {
            LOGGER.error("Error opening ClickGUI", exception);
        } finally {
            this.setEnabled(false);
        }
    }
}
