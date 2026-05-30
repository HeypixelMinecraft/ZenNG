package com.mihoyo.zen.network.webui;

import com.google.gson.Gson;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.exception.ModuleNotFoundException;
import com.mihoyo.zen.modules.Category;
import com.mihoyo.zen.modules.Module;
import com.mihoyo.zen.settings.Setting;
import com.mihoyo.zen.settings.impl.BooleanSetting;
import com.mihoyo.zen.settings.impl.ModeSetting;
import com.mihoyo.zen.settings.impl.MultiSelectSetting;
import com.mihoyo.zen.settings.impl.NumberSetting;
import com.mihoyo.zen.utils.render.TextureUtil;
import com.sun.net.httpserver.HttpExchange;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuiStateHandler extends AbstractHttpHandler {
    private static final Gson GSON = new Gson();

    @Override
    public int handleRequest(InputStream in, OutputStream out, HttpExchange exchange) throws Throwable {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        Map<String, String> query = TextureUtil.parseQueryString(exchange.getRequestURI().getQuery());
        Module selected = lookupModule(query.get("module"));
        if (selected == null) {
            selected = ZenClient.getInstance().getModuleManager().getModules().stream().findFirst().orElse(null);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("categories", categories());
        response.put("modules", modules());
        response.put("selectedModule", selected == null ? null : moduleEntry(selected));
        response.put("settings", selected == null ? List.of() : settings(selected));
        out.write(GSON.toJson(response).getBytes(StandardCharsets.UTF_8));
        return 200;
    }

    public static List<Map<String, Object>> categories() {
        List<Map<String, Object>> categories = new ArrayList<>();
        for (Category category : Category.values()) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", category.name());
            entry.put("name", category.displayName);
            categories.add(entry);
        }
        return categories;
    }

    public static List<Map<String, Object>> modules() {
        List<Map<String, Object>> modules = new ArrayList<>();
        for (Module module : ZenClient.getInstance().getModuleManager().getModules()) {
            modules.add(moduleEntry(module));
        }
        return modules;
    }

    public static Map<String, Object> moduleEntry(Module module) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("name", module.getName());
        entry.put("category", module.getCategory().name());
        entry.put("categoryName", module.getCategory().displayName);
        entry.put("enabled", module.isEnabled());
        entry.put("bind", module.getBind().getName());
        entry.put("key", module.getKey());
        entry.put("hasSettings", module.getSettings().stream().anyMatch(GuiStateHandler::isVisible));
        return entry;
    }

    public static List<Map<String, Object>> settings(Module module) {
        List<Map<String, Object>> settings = new ArrayList<>();
        for (Setting<?> setting : module.getSettings()) {
            if (!isVisible(setting)) {
                continue;
            }
            settings.add(settingEntry(setting));
        }
        return settings;
    }

    public static Map<String, Object> settingEntry(Setting<?> setting) {
        Map<String, Object> entry = new HashMap<>();
        entry.put("name", setting.getName());
        entry.put("displayName", setting.getName());
        entry.put("value", setting.getValue());
        if (setting instanceof NumberSetting numberSetting) {
            entry.put("type", "number");
            entry.put("min", numberSetting.getMin());
            entry.put("max", numberSetting.getMax());
            entry.put("step", numberSetting.getStep());
        } else if (setting instanceof BooleanSetting) {
            entry.put("type", "boolean");
        } else if (setting instanceof ModeSetting modeSetting) {
            entry.put("type", "mode");
            entry.put("values", modeSetting.getModes());
        } else if (setting instanceof MultiSelectSetting multiSelectSetting) {
            entry.put("type", "multi");
            entry.put("values", multiSelectSetting.getOptions());
        } else {
            entry.put("type", "text");
        }
        return entry;
    }

    public static boolean applySetting(Module module, String settingName, String rawValue) {
        for (Setting<?> setting : module.getSettings()) {
            if (!setting.getName().equals(settingName)) {
                continue;
            }
            return applySetting(setting, rawValue);
        }
        return false;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static boolean applySetting(Setting setting, String rawValue) {
        if (setting instanceof NumberSetting) {
            try {
                setting.setValue(Double.valueOf(rawValue));
                return true;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }
        if (setting instanceof BooleanSetting) {
            setting.setValue(Boolean.valueOf(rawValue));
            return true;
        }
        if (setting instanceof ModeSetting modeSetting) {
            if (Arrays.asList(modeSetting.getModes()).contains(rawValue)) {
                modeSetting.setValue(rawValue);
                return true;
            }
            return false;
        }
        if (setting instanceof MultiSelectSetting multiSelectSetting) {
            List<String> selected = new ArrayList<>();
            if (!rawValue.isBlank()) {
                for (String value : rawValue.split(",")) {
                    String trimmed = value.trim();
                    if (multiSelectSetting.getOptions().contains(trimmed)) {
                        selected.add(trimmed);
                    }
                }
            }
            multiSelectSetting.setValue(selected);
            return true;
        }
        return false;
    }

    private static boolean isVisible(Setting<?> setting) {
        return setting.getVisibility() == null || setting.getVisibility().displayable();
    }

    private static Module lookupModule(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }
        try {
            return ZenClient.getInstance().getModuleManager().getModule(name);
        } catch (ModuleNotFoundException e) {
            return null;
        }
    }
}
