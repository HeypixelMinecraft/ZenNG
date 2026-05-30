package com.mihoyo.zen.modules.impl.world;

import com.mihoyo.zen.modules.Category;
import com.mihoyo.zen.modules.Module;
import com.mihoyo.zen.network.webui.WebUiServer;
import com.mihoyo.zen.utils.misc.ChatUtil;

public class WebUI extends Module {
    public WebUI() {
        super("WebUI", Category.WORLD);
        setEnabled(false);
    }

    @Override
    public void onEnable() {
        if (WebUiServer.ensureStarted(true)) {
            ChatUtil.print("WebUI started at http://127.0.0.1:8089");
        } else {
            ChatUtil.print("Failed to start WebUI");
            this.setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        if (WebUiServer.isStarted()) {
            WebUiServer.stop();
            ChatUtil.print("WebUI stopped");
        }
    }
}
