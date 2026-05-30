package com.mihoyo.zen.modules.impl.render;

import com.mihoyo.zen.gui.svelte.HudEditorScreen;
import com.mihoyo.zen.modules.Category;
import com.mihoyo.zen.modules.Module;

public class HudEditor extends Module {
    public HudEditor() {
        super("HudEditor", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        mc.setScreen(new HudEditorScreen());
        this.setEnabled(false);
    }
}
