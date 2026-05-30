package com.mihoyo.zen.gui.svelte;

import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.hud.HudElement;
import com.mihoyo.zen.modules.Module;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class HudEditorScreen extends Screen {
    private HudElement dragging;
    private float dragOffsetX;
    private float dragOffsetY;

    public HudEditorScreen() {
        super(Component.literal("Zen HUD Editor"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.fill(0, 0, width, height, 0x33000000);
        graphics.drawString(font, "Zen HUD Editor - drag panels, ESC to save", 8, 8, 0xFFFFFFFF, true);

        for (HudElement element : ZenClient.getInstance().getHudManager().getHudElements().stream().filter(Module::isEnabled).toList()) {
            int x = Math.round(element.getX());
            int y = Math.round(element.getY());
            int w = Math.max(72, Math.round(element.getWidth()));
            int h = Math.max(22, Math.round(element.getHeight()));
            boolean hovered = isHovered(mouseX, mouseY, x, y, w, h);
            int border = hovered || element == dragging ? 0xFFFFCC4A : 0xAAFFFFFF;
            graphics.fill(x, y, x + w, y + h, hovered ? 0x44FFCC4A : 0x22000000);
            graphics.fill(x, y, x + w, y + 1, border);
            graphics.fill(x, y + h - 1, x + w, y + h, border);
            graphics.fill(x, y, x + 1, y + h, border);
            graphics.fill(x + w - 1, y, x + w, y + h, border);
            graphics.drawString(font, element.getName(), x + 5, y + 7, 0xFFFFFFFF, true);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) {
            return super.mouseClicked(mouseX, mouseY, button);
        }
        for (HudElement element : ZenClient.getInstance().getHudManager().getHudElements().stream().filter(Module::isEnabled).toList()) {
            int w = Math.max(72, Math.round(element.getWidth()));
            int h = Math.max(22, Math.round(element.getHeight()));
            if (isHovered(mouseX, mouseY, element.getX(), element.getY(), w, h)) {
                dragging = element;
                dragOffsetX = (float)mouseX - element.getX();
                dragOffsetY = (float)mouseY - element.getY();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (button == 0 && dragging != null) {
            dragging.setX(clamp((float)mouseX - dragOffsetX, 0.0f, width - Math.max(72.0f, dragging.getWidth())));
            dragging.setY(clamp((float)mouseY - dragOffsetY, 0.0f, height - Math.max(22.0f, dragging.getHeight())));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            dragging = null;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        ZenClient.getInstance().getConfigManager().saveAll();
        super.onClose();
    }

    private static boolean isHovered(double mouseX, double mouseY, float x, float y, float width, float height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
}
