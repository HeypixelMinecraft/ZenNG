package com.mihoyo.zen.gui.svelte;

import com.mihoyo.zen.ClientBase;
import java.lang.reflect.Method;

final class McefBrowserBridge {
    private final Object browser;

    private McefBrowserBridge(Object browser) {
        this.browser = browser;
    }

    static boolean isReady() {
        try {
            Class<?> mcefClass = Class.forName("com.cinemamod.mcef.MCEF");
            return Boolean.TRUE.equals(mcefClass.getMethod("isInitialized").invoke(null));
        } catch (Throwable throwable) {
            return false;
        }
    }

    static McefBrowserBridge create(String url, boolean transparent) {
        try {
            Class<?> mcefClass = Class.forName("com.cinemamod.mcef.MCEF");
            Object browser = mcefClass.getMethod("createBrowser", String.class, boolean.class)
                    .invoke(null, url, transparent);
            return new McefBrowserBridge(browser);
        } catch (Throwable throwable) {
            ClientBase.logger.error("Failed to create MCEF browser", throwable);
            return null;
        }
    }

    int getTextureId() {
        try {
            Object renderer = call("getRenderer");
            Object texture = renderer.getClass().getMethod("getTextureID").invoke(renderer);
            return texture instanceof Number number ? number.intValue() : 0;
        } catch (Throwable throwable) {
            return 0;
        }
    }

    void resize(int width, int height) {
        invoke("resize", new Class<?>[]{int.class, int.class}, width, height);
    }

    void close() {
        invoke("close", new Class<?>[0]);
    }

    void focus(boolean focused) {
        invoke("setFocus", new Class<?>[]{boolean.class}, focused);
    }

    void mousePress(int x, int y, int button) {
        invoke("sendMousePress", new Class<?>[]{int.class, int.class, int.class}, x, y, button);
    }

    void mouseRelease(int x, int y, int button) {
        invoke("sendMouseRelease", new Class<?>[]{int.class, int.class, int.class}, x, y, button);
    }

    void mouseMove(int x, int y) {
        invoke("sendMouseMove", new Class<?>[]{int.class, int.class}, x, y);
    }

    void mouseWheel(int x, int y, double delta) {
        invoke("sendMouseWheel", new Class<?>[]{int.class, int.class, double.class, int.class}, x, y, delta, 0);
    }

    void keyPress(int keyCode, int scanCode, int modifiers) {
        invoke("sendKeyPress", new Class<?>[]{int.class, int.class, int.class}, keyCode, scanCode, modifiers);
    }

    void keyRelease(int keyCode, int scanCode, int modifiers) {
        invoke("sendKeyRelease", new Class<?>[]{int.class, int.class, int.class}, keyCode, scanCode, modifiers);
    }

    void keyTyped(char codePoint, int modifiers) {
        invoke("sendKeyTyped", new Class<?>[]{char.class, int.class}, codePoint, modifiers);
    }

    private Object call(String name) throws ReflectiveOperationException {
        return browser.getClass().getMethod(name).invoke(browser);
    }

    private void invoke(String name, Class<?>[] parameterTypes, Object... args) {
        try {
            Method method = browser.getClass().getMethod(name, parameterTypes);
            method.invoke(browser, args);
        } catch (Throwable throwable) {
            ClientBase.logger.debug("MCEF browser call failed: " + name, throwable);
        }
    }
}
