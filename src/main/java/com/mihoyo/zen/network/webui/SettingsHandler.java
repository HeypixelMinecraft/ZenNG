package com.mihoyo.zen.network.webui;

import com.google.gson.Gson;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.exception.ModuleNotFoundException;
import com.mihoyo.zen.modules.Module;
import com.mihoyo.zen.utils.render.TextureUtil;
import com.sun.net.httpserver.HttpExchange;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class SettingsHandler extends AbstractHttpHandler {
    private static final Gson GSON = new Gson();

    @Override
    public int handleRequest(InputStream in, OutputStream out, HttpExchange exchange) throws Throwable {
        Map<String, String> query = TextureUtil.parseQueryString(exchange.getRequestURI().getQuery());
        Map<String, Object> response = new HashMap<>();
        boolean success = false;
        String reason = null;
        if (query.containsKey("module")) {
            try {
                Module module = lookupModule(query.get("module"));
                if (module == null) {
                    reason = "Module not found";
                } else {
                    response.put("result", GuiStateHandler.settings(module));
                    success = true;
                }
            } catch (Throwable throwable) {
                success = false;
                reason = throwable.toString();
            }
        } else {
            reason = "Missing module parameter";
        }
        response.put("success", success);
        response.put("reason", reason);
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
