package com.mihoyo.zen.network.webui;

import com.mihoyo.zen.ClientBase;
import com.mihoyo.zen.utils.misc.ChatUtil;
import com.sun.net.httpserver.HttpServer;
import java.awt.Desktop;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

public final class WebUiServer {
    public static final int PORT = 8089;
    public static final String BASE_URL = "http://127.0.0.1:" + PORT;
    public static final String SVELTE_GUI_URL = BASE_URL + "/svelte-gui/index.html";

    private static HttpServer httpServer;

    private WebUiServer() {
    }

    public static synchronized boolean ensureStarted(boolean openExternalBrowser) {
        if (httpServer != null) {
            if (openExternalBrowser) {
                openExternalBrowser(BASE_URL);
            }
            return true;
        }
        try {
            httpServer = createHttpServer();
            if (openExternalBrowser) {
                openExternalBrowser(BASE_URL);
            }
            return true;
        } catch (IOException exception) {
            ClientBase.logger.error("Failed to start WebUI server", exception);
            return false;
        }
    }

    public static synchronized boolean isStarted() {
        return httpServer != null;
    }

    public static synchronized void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            httpServer = null;
        }
    }

    private static HttpServer createHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/api/modulesList", new ModulesHandler());
        server.createContext("/api/categoriesList", new CategoriesHandler());
        server.createContext("/api/setStatus", new ToggleModuleHandler());
        server.createContext("/api/setModuleSettingValue", new SetSettingHandler());
        server.createContext("/api/getModuleSetting", new SettingsHandler());
        server.createContext("/api/gui/state", new GuiStateHandler());
        server.createContext("/svelte-gui", new StaticFileHandler("/svelte-gui", "/svelte-gui"));
        server.createContext("/", new StaticFileHandler("/webui", "/"));
        server.start();
        return server;
    }

    private static void openExternalBrowser(String url) {
        try {
            System.setProperty("java.awt.headless", "false");
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (URISyntaxException | IOException exception) {
            ChatUtil.print("Failed to open browser: " + exception.getMessage());
        }
    }
}
