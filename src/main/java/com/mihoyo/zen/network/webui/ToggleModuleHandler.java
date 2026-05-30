package com.mihoyo.zen.network.webui;

import com.google.gson.Gson;
import com.mihoyo.zen.ClientBase;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.exception.ModuleNotFoundException;
import com.mihoyo.zen.modules.Module;
import com.mihoyo.zen.modules.impl.render.ClickGuiModule;
import com.mihoyo.zen.modules.impl.world.WebUI;
import com.mihoyo.zen.utils.render.TextureUtil;
import com.sun.net.httpserver.HttpExchange;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ToggleModuleHandler extends AbstractHttpHandler {
    private static final Gson GSON = new Gson();

    @Override
    public int handleRequest(InputStream in, OutputStream out, HttpExchange exchange) throws Throwable {
        Map<String, String> query = TextureUtil.parseQueryString(exchange.getRequestURI().getQuery());
        Map<String, Object> response = new HashMap<>();
        String reason = null;
        boolean state;
        boolean success;
        if (query.containsKey("module") && query.containsKey("state")) {
            try {
                Module module = lookupModule(query.get("module"));
                if (module == null) {
                    state = false;
                    success = false;
                    reason = "Module not found";
                } else if (module instanceof WebUI || module instanceof ClickGuiModule) {
                    state = module.isEnabled();
                    success = true;
                    reason = "This module cannot be toggled from WebUI";
                } else {
                    boolean requestedState = Boolean.parseBoolean(query.get("state"));
                    setEnabledOnClientThread(module, requestedState);
                    state = module.isEnabled();
                    success = true;
                }
            } catch (Throwable throwable) {
                state = false;
                success = false;
                reason = throwable.toString();
            }
        } else {
            state = false;
            success = false;
            reason = "Missing module or state parameter";
        }
        response.put("success", success);
        response.put("reason", reason);
        response.put("result", state);
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

    private static void setEnabledOnClientThread(Module module, boolean state) {
        if (ClientBase.mc == null || ClientBase.mc.isSameThread()) {
            module.setEnabled(state);
            return;
        }
        CompletableFuture<Void> future = new CompletableFuture<>();
        ClientBase.mc.execute(() -> {
            try {
                module.setEnabled(state);
                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });
        future.join();
    }
}
