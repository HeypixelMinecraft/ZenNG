package com.mihoyo.zen.modules.impl.render;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.mihoyo.zen.gui.svelte.SvelteClickGui;
import com.mihoyo.zen.modules.Category;
import com.mihoyo.zen.modules.Module;
import com.mihoyo.zen.utils.misc.ChatUtil;

public class ClickGuiModule
extends Module {
    public static final Logger LOGGER = LogManager.getLogger(ClickGuiModule.class);

    public ClickGuiModule() {
        super("ClickGui", Category.RENDER, 344);
    }

    @Override
    protected void onEnable() {
        try {
            if (SvelteClickGui.canOpen()) {
                mc.setScreen(new SvelteClickGui());
            } else {
                LOGGER.warn("Svelte ClickGUI is unavailable. Install/load MCEF and wait until it finishes initializing.");
                ChatUtil.print("Svelte ClickGUI unavailable: MCEF is not loaded or not initialized.");
                mc.setScreen(null);
            }
            LOGGER.info("ClickGUI opened successfully");
        } catch (Exception exception) {
            LOGGER.error("Error opening ClickGUI", exception);
        } finally {
            this.setEnabled(false);
        }
    }
}
