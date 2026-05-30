package com.mihoyo.zen.mixin;

import java.util.List;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mihoyo.zen.ClientBase;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.event.impl.ChatReceiveEvent;
import com.mihoyo.zen.hud.TabListInfo;
import com.mihoyo.zen.modules.impl.render.Watermark;

@Mixin(PlayerTabOverlay.class)
public class PlayerTabOverlayMixin  {
    @Unique
    private static final ThreadLocal<Boolean> zen$renderState = ThreadLocal.withInitial(() -> false);

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;split(Lnet/minecraft/network/chat/FormattedText;I)Ljava/util/List;", ordinal = 0)
    )
    private List<FormattedCharSequence> zen$hookHeader(Font font, FormattedText text, int width) {
        Component component = (Component) text;
        ChatReceiveEvent event = new ChatReceiveEvent(ChatReceiveEvent.MessageType.SYSTEM, component);
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(event);
        }
        return font.split(event.getComponent(), width);
    }

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Font;split(Lnet/minecraft/network/chat/FormattedText;I)Ljava/util/List;", ordinal = 1)
    )
    private List<FormattedCharSequence> zen$hookFooter(Font font, FormattedText text, int width) {
        Component component = (Component) text;
        ChatReceiveEvent event = new ChatReceiveEvent(ChatReceiveEvent.MessageType.CHAT, component);
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(event);
        }
        return font.split(event.getComponent(), width);
    }

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/PlayerTabOverlay;getNameForDisplay(Lnet/minecraft/client/multiplayer/PlayerInfo;)Lnet/minecraft/network/chat/Component;")
    )
    private Component zen$hookName(PlayerTabOverlay overlay, PlayerInfo info) {
        Component displayName = overlay.getNameForDisplay(info);
        ChatReceiveEvent event = new ChatReceiveEvent(ChatReceiveEvent.MessageType.NAME, displayName);
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(event);
        }
        return event.getComponent();
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void zen$onRenderPre(GuiGraphics graphics, int width, Scoreboard scoreboard, Objective objective, CallbackInfo ci) {
        PlayerTabOverlay overlay = (PlayerTabOverlay) (Object) this;
        PlayerTabOverlayAccessor accessor = (PlayerTabOverlayAccessor) overlay;
        TabListInfo.header = accessor.zen$getHeader();
        TabListInfo.footer = accessor.zen$getFooter();
        zen$renderState.set(false);
        if (!ZenClient.isReady() || ZenClient.getInstance().getModuleManager() == null) return;
        Watermark watermark = ZenClient.getInstance().getModuleManager().getModule(Watermark.class);
        if (watermark == null || !watermark.isEnabled() || !ClientBase.mc.options.keyPlayerList.isDown()) return;
        if ("DynamicIsland".equals(watermark.getStyle())) {
            ci.cancel();
        } else {
            zen$renderState.set(true);
            graphics.pose().pushPose();
            graphics.pose().translate(0.0f, 30.0f, 0.0f);
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void zen$onRenderPost(GuiGraphics graphics, int width, Scoreboard scoreboard, Objective objective, CallbackInfo ci) {
        if (zen$renderState.get()) {
            graphics.pose().popPose();
        }
    }
}
