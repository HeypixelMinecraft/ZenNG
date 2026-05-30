package com.mihoyo.zen.mixin;

import net.minecraft.client.player.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.event.impl.StrafeEvent;

@Mixin(KeyboardInput.class)
public class KeyboardInputMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void zen$onTick(boolean isSneaking, float sneakMultiplier, CallbackInfo ci) {
        KeyboardInput input = (KeyboardInput) (Object) this;
        input.forwardImpulse = input.up == input.down ? 0.0f : (input.up ? 1.0f : -1.0f);
        input.leftImpulse = input.left == input.right ? 0.0f : (input.left ? 1.0f : -1.0f);
        StrafeEvent event = new StrafeEvent(input.forwardImpulse, input.leftImpulse, input.jumping);
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(event);
        }
        double sneakFactor = 0.3;
        input.forwardImpulse = event.getForward();
        input.leftImpulse = event.getStrafe();
        input.jumping = event.isSprinting();
        if (isSneaking) {
            input.leftImpulse = (float) (input.leftImpulse * sneakFactor);
            input.forwardImpulse = (float) (input.forwardImpulse * sneakFactor);
        }
    }
}
