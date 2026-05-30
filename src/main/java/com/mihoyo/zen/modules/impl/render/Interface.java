package com.mihoyo.zen.modules.impl.render;

import com.mihoyo.zen.modules.Category;
import com.mihoyo.zen.modules.Module;
import com.mihoyo.zen.settings.impl.BooleanSetting;

public class Interface
extends Module {
    public final BooleanSetting svelteHud = new BooleanSetting("Svelte HUD", true);

    public Interface() {
        super("Interface", Category.RENDER);
    }
}
