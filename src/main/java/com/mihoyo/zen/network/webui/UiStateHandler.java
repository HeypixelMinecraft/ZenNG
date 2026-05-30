package com.mihoyo.zen.network.webui;

import com.google.gson.Gson;
import com.mihoyo.zen.ClientBase;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.hud.HudElement;
import com.mihoyo.zen.modules.Module;
import com.mihoyo.zen.modules.impl.combat.KillAura;
import com.mihoyo.zen.modules.impl.movement.Scaffold;
import com.mihoyo.zen.modules.impl.render.Interface;
import com.mihoyo.zen.modules.impl.render.NameProtect;
import com.mihoyo.zen.modules.impl.render.Watermark;
import com.mihoyo.zen.modules.impl.world.AutoPlay;
import com.mihoyo.zen.utils.game.BlockUtil;
import com.mihoyo.zen.utils.game.MovementUtil;
import com.sun.net.httpserver.HttpExchange;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UiStateHandler extends AbstractHttpHandler {
    private static final Gson GSON = new Gson();
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

    @Override
    public int handleRequest(InputStream in, OutputStream out, HttpExchange exchange) throws Throwable {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("screen", screen());
        response.put("hud", hud());
        response.put("modules", modules());
        response.put("keybinds", keybinds());
        response.put("effects", effects());
        response.put("target", target());
        response.put("players", players());
        response.put("scaffold", scaffold());
        response.put("dynamicIsland", dynamicIsland());
        response.put("watermark", watermark());
        out.write(GSON.toJson(response).getBytes(StandardCharsets.UTF_8));
        return 200;
    }

    private static Map<String, Object> screen() {
        Minecraft mc = ClientBase.mc;
        Map<String, Object> screen = new HashMap<>();
        screen.put("width", mc.getWindow().getGuiScaledWidth());
        screen.put("height", mc.getWindow().getGuiScaledHeight());
        screen.put("fps", mc.getFps());
        return screen;
    }

    private static List<Map<String, Object>> hud() {
        List<Map<String, Object>> hud = new ArrayList<>();
        for (HudElement element : ZenClient.getInstance().getHudManager().getHudElements()) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", element.getName());
            entry.put("enabled", element.isEnabled());
            entry.put("x", element.getX());
            entry.put("y", element.getY());
            entry.put("width", element.getWidth());
            entry.put("height", element.getHeight());
            hud.add(entry);
        }
        return hud;
    }

    private static List<Map<String, Object>> modules() {
        List<Map<String, Object>> modules = new ArrayList<>();
        for (Module module : ZenClient.getInstance().getModuleManager().getModules()) {
            if (!module.isEnabled() || module instanceof Interface) {
                continue;
            }
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", module.getName());
            entry.put("category", module.getCategory().displayName);
            modules.add(entry);
        }
        modules.sort((a, b) -> String.valueOf(b.get("name")).length() - String.valueOf(a.get("name")).length());
        return modules;
    }

    private static List<Map<String, Object>> keybinds() {
        List<Map<String, Object>> keybinds = new ArrayList<>();
        for (Module module : ZenClient.getInstance().getModuleManager().getModules()) {
            if (!module.isEnabled() || module.getKey() <= 0 || "Interface".equals(module.getName()) || "ClickGui".equals(module.getName())) {
                continue;
            }
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", module.getName());
            entry.put("key", module.getBind().getName());
            entry.put("enabled", true);
            keybinds.add(entry);
        }
        return keybinds;
    }

    private static List<Map<String, Object>> effects() {
        List<Map<String, Object>> effects = new ArrayList<>();
        if (ClientBase.mc.player == null) {
            return effects;
        }
        for (MobEffectInstance effect : ClientBase.mc.player.getActiveEffects()) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("name", effect.getEffect().getDisplayName().getString());
            entry.put("amplifier", amplifier(effect.getAmplifier() + 1));
            entry.put("duration", effect.isInfiniteDuration() || effect.getDuration() > 72000 ? "∞" : MobEffectUtil.formatDuration(effect, 1.0f).getString());
            entry.put("color", String.format("#%06X", effect.getEffect().getColor() & 0xFFFFFF));
            effects.add(entry);
        }
        return effects;
    }

    private static Map<String, Object> target() {
        Map<String, Object> target = new HashMap<>();
        if (KillAura.aimingTarget instanceof LivingEntity entity) {
            float maxHealth = Math.max(1.0f, Math.min(entity.getMaxHealth(), 20.0f));
            float health = Math.max(0.0f, Math.min(entity.getHealth(), 20.0f));
            target.put("visible", true);
            target.put("name", NameProtect.replacePlayerName(entity.getName().getString()));
            target.put("health", health);
            target.put("maxHealth", maxHealth);
            target.put("hurtTime", entity.hurtTime);
            target.put("distance", ClientBase.mc.player == null ? 0.0 : ClientBase.mc.player.distanceTo(entity));
        } else {
            target.put("visible", false);
        }
        return target;
    }

    private static List<Map<String, Object>> players() {
        List<Map<String, Object>> players = new ArrayList<>();
        if (ClientBase.mc.level == null || ClientBase.mc.player == null) {
            return players;
        }
        ClientBase.mc.level.players().stream()
                .filter(player -> player != ClientBase.mc.player)
                .limit(8)
                .forEach(player -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("name", NameProtect.replacePlayerName(player.getName().getString()));
                    entry.put("distance", ClientBase.mc.player.distanceTo(player));
                    entry.put("health", player.getHealth());
                    players.add(entry);
                });
        return players;
    }

    private static Map<String, Object> scaffold() {
        Map<String, Object> scaffold = new HashMap<>();
        int blocks = blockCount();
        boolean enabled = Scaffold.INSTANCE != null && Scaffold.INSTANCE.isEnabled();
        scaffold.put("enabled", enabled);
        scaffold.put("blocks", blocks);
        scaffold.put("speed", ClientBase.mc.player == null ? 0.0 : MovementUtil.getSpeedBps());
        scaffold.put("visible", enabled && blocks > 0);
        return scaffold;
    }

    private static Map<String, Object> dynamicIsland() {
        Map<String, Object> island = new HashMap<>();
        Watermark watermark = watermarkModule();
        if (watermark == null || !watermark.isEnabled() || !"DynamicIsland".equals(watermark.getStyle())) {
            island.put("visible", false);
            island.put("type", "hidden");
            island.put("data", Map.of());
            return island;
        }
        Map<String, Object> size = new HashMap<>();
        size.put("alignment", "top");
        if (ClientBase.mc.options.keyPlayerList.isDown()) {
            size.put("width", 200.0);
            size.put("height", 30.0);
            island.put("type", "tablist");
            island.put("visible", true);
            island.put("size", size);
            island.put("data", Map.of("title", "Player List", "count", players().size() + 1));
            return island;
        }
        Map<String, Object> scaffold = scaffold();
        if (Boolean.TRUE.equals(scaffold.get("visible"))) {
            size.put("width", 260.0);
            size.put("height", 30.0);
            size.put("alignment", "center");
            island.put("type", "scaffold");
            island.put("visible", true);
            island.put("size", size);
            island.put("data", scaffold);
            return island;
        }
        if (AutoPlay.instance != null && AutoPlay.instance.isEnabled() && AutoPlay.instance.pendingDisconnect) {
            Map<String, Object> autoPlay = new HashMap<>();
            long elapsed = System.currentTimeMillis() - AutoPlay.instance.disconnectTime;
            double delayMs = AutoPlay.instance.getDelay().getValue().doubleValue() * 1000.0;
            autoPlay.put("progress", Math.max(0.0, Math.min(1.0, elapsed / delayMs)));
            size.put("width", 260.0);
            size.put("height", 40.0);
            size.put("alignment", "center");
            island.put("type", "autoplay");
            island.put("visible", true);
            island.put("size", size);
            island.put("data", autoPlay);
            return island;
        }
        Map<String, String> serverInfo = serverInfo();
        size.put("width", 118.0 + Math.max(serverInfo.get("line1").length(), serverInfo.get("line2").length()) * 5.0);
        size.put("height", 25.0);
        island.put("type", "watermark");
        island.put("visible", true);
        island.put("size", size);
        island.put("data", Map.of("line1", serverInfo.get("line1"), "line2", serverInfo.get("line2")));
        return island;
    }

    private static Map<String, Object> watermark() {
        Map<String, Object> data = new HashMap<>();
        Watermark watermark = watermarkModule();
        boolean enabled = watermark != null && watermark.isEnabled();
        String style = watermark == null ? "DynamicIsland" : watermark.getStyle();
        data.put("enabled", enabled);
        data.put("style", style);
        data.put("visible", enabled && "Neverlose".equals(style));
        data.put("username", ClientBase.mc.player != null ? ClientBase.mc.player.getGameProfile().getName() : "Player");
        data.put("config", "Default Config");
        data.put("ping", pingText());
        data.put("fps", ClientBase.mc.getFps() + "fps");
        data.put("server", serverName());
        data.put("time", TIME_FORMAT.format(new Date()));
        return data;
    }

    private static Watermark watermarkModule() {
        try {
            return ZenClient.getInstance().getModuleManager().getModule(Watermark.class);
        } catch (Throwable throwable) {
            return null;
        }
    }

    private static Map<String, String> serverInfo() {
        if (ClientBase.mc.isSingleplayer()) {
            return Map.of("line1", "Singleplayer", "line2", "1ms");
        }
        ServerData serverData = ClientBase.mc.getCurrentServer();
        String serverIp = serverData != null ? serverData.ip : "Multiplayer";
        int ping = 0;
        if (ClientBase.mc.getConnection() != null && ClientBase.mc.player != null) {
            PlayerInfo playerInfo = ClientBase.mc.getConnection().getPlayerInfo(ClientBase.mc.player.getUUID());
            if (playerInfo != null) {
                ping = Math.max(0, Math.min(9999, playerInfo.getLatency()));
            }
        }
        return Map.of("line1", serverIp, "line2", ping + "ms");
    }

    private static String serverName() {
        ServerData serverData = ClientBase.mc.getCurrentServer();
        return serverData != null ? serverData.ip : "Singleplayer";
    }

    private static String pingText() {
        if (ClientBase.mc.player == null || ClientBase.mc.player.connection == null) {
            return "0ms";
        }
        PlayerInfo playerInfo = ClientBase.mc.player.connection.getPlayerInfo(ClientBase.mc.player.getUUID());
        return playerInfo == null ? "0ms" : playerInfo.getLatency() + "ms";
    }

    private static int blockCount() {
        if (ClientBase.mc.player == null) {
            return 0;
        }
        int total = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = ClientBase.mc.player.getInventory().getItem(i);
            if (stack.getItem() instanceof BlockItem && BlockUtil.isPlaceable(stack)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private static String amplifier(int amplifier) {
        return switch (amplifier) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(amplifier);
        };
    }
}
