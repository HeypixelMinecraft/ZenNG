package com.mihoyo.zen.modules.impl.world;

import com.sun.net.httpserver.HttpServer;
import java.awt.Desktop;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import com.mihoyo.zen.modules.Category;
import com.mihoyo.zen.modules.Module;
import com.mihoyo.zen.network.webui.CategoriesHandler;
import com.mihoyo.zen.network.webui.ModulesHandler;
import com.mihoyo.zen.network.webui.SetSettingHandler;
import com.mihoyo.zen.network.webui.SettingsHandler;
import com.mihoyo.zen.network.webui.StaticFileHandler;
import com.mihoyo.zen.network.webui.ToggleModuleHandler;
import com.mihoyo.zen.utils.misc.ChatUtil;

public class WebUI extends Module {
    private HttpServer httpServer;

    public WebUI() {
        super("WebUI", Category.WORLD);
        setEnabled(false);
    }

    @Override
    public void onEnable() {
        try {
            this.httpServer = this.createHttpServer();
            ChatUtil.print("WebUI started at http://127.0.0.1:8089");
            try {
                System.setProperty("java.awt.headless", "false");
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(new URI("http://127.0.0.1:8089"));
                }
            } catch (URISyntaxException | IOException ex) {
                ChatUtil.print("Failed to open browser: " + ex.getMessage());
            }
        } catch (IOException ioException) {
            ChatUtil.print("Failed to start http server because " + ioException.getMessage());
            ioException.printStackTrace();
            this.setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        if (this.httpServer != null) {
            this.httpServer.stop(0);
            this.httpServer = null;
            ChatUtil.print("WebUI stopped");
        }
    }

    private HttpServer createHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8089), 0);
        server.createContext("/api/modulesList", new ModulesHandler());
        server.createContext("/api/categoriesList", new CategoriesHandler());
        server.createContext("/api/setStatus", new ToggleModuleHandler());
        server.createContext("/api/setModuleSettingValue", new SetSettingHandler());
        server.createContext("/api/getModuleSetting", new SettingsHandler());
        server.createContext("/", new StaticFileHandler("/webui", "/"));
        server.start();
        return server;
    }
}
