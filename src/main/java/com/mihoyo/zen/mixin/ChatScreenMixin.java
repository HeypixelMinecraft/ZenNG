package com.mihoyo.zen.mixin;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mihoyo.zen.ClientBase;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.hud.HudElement;
import com.mihoyo.zen.modules.Module;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void zen$onRender(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        try {
            for (HudElement element : ZenClient.getInstance().getHudManager().getHudElements().stream().filter(Module::isEnabled).toList()) {
                if (!element.isDragging()) continue;
                element.mouseDragged(mouseX, mouseY);
                boolean leftDown = GLFW.glfwGetMouseButton(ClientBase.mc.getWindow().getWindow(), 0) == 1;
                if (!leftDown) {
                    element.setDragging(false);
                }
            }
        } catch (Exception exception) {
            ClientBase.logger.error(exception);
            ClientBase.logger.error(exception.getMessage());
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"))
    private void zen$onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        try {
            for (HudElement element : ZenClient.getInstance().getHudManager().getHudElements().stream().filter(Module::isEnabled).toList()) {
                if (element.mousePressed((int) mouseX, (int) mouseY, button)) {
                    break;
                }
            }
        } catch (Exception exception) {
            ClientBase.logger.error(exception);
            ClientBase.logger.error(exception.getMessage());
        }
    }
}
