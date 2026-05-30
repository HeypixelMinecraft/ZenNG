package shit.zen.mixin;

import net.minecraft.client.KeyboardHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shit.zen.ZenClient;
import shit.zen.event.impl.KeyEvent;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    private void zen$onKeyPress(long window, int keyCode, int scanCode, int action, int modifiers, CallbackInfo ci) {
        if (!ZenClient.isReady()) return;
        KeyEvent event = new KeyEvent(keyCode, action != 0);
        ZenClient.getInstance().getEventBus().call(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
