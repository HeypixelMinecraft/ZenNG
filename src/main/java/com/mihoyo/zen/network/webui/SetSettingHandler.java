package com.mihoyo.zen.network.webui;

import com.google.gson.Gson;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.exception.ModuleNotFoundException;
import com.mihoyo.zen.modules.Module;
import com.mihoyo.zen.modules.impl.world.WebUI;
import com.mihoyo.zen.settings.Setting;
import com.mihoyo.zen.utils.render.TextureUtil;
import com.sun.net.httpserver.HttpExchange;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SetSettingHandler extends AbstractHttpHandler {
    private static final Gson GSON = new Gson();

    @Override
    public int handleRequest(InputStream in, OutputStream out, HttpExchange exchange) throws Throwable {
        Map<String, String> query = TextureUtil.parseQueryString(exchange.getRequestURI().getQuery());
        Map<String, Object> response = new HashMap<>();
        boolean success = false;
        String reason = null;
        Object result = null;
        if (query.containsKey("module") && query.containsKey("name") && query.containsKey("value")) {
            try {
                Module module = lookupModule(query.get("module"));
                if (module == null) {
                    reason = "Module not found";
                } else if (module instanceof WebUI) {
                    reason = "WebUI cannot be edited from WebUI";
                } else {
                    String settingName = query.get("name");
                    success = GuiStateHandler.applySetting(module, settingName, query.get("value"));
                    if (success) {
                        result = module.getSettings().stream()
                                .filter(setting -> setting.getName().equals(settingName))
                                .findFirst()
                                .map(Setting::getValue)
                                .orElse(null);
                    } else {
                        reason = "Invalid setting or value";
                    }
                }
            } catch (Throwable throwable) {
                success = false;
                reason = throwable.toString();
            }
        } else {
            result = false;
            reason = "Missing module, name, or value parameter";
        }
        response.put("success", success);
        response.put("reason", reason);
        response.put("result", result);
        out.write(GSON.toJson(response).getBytes(StandardCharsets.UTF_8));
        return 200;
    }

    private static Module lookupModule(String name) {
        try {
            return ZenClient.getInstance().getModuleManager().getModule(name);
        } catch (ModuleNotFoundException e) {
            return null;
        }
    }
}
