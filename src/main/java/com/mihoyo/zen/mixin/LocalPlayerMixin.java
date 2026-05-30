package com.mihoyo.zen.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.event.impl.GameTickEvent;
import com.mihoyo.zen.event.impl.MotionEvent;
import com.mihoyo.zen.event.impl.SlowdownEvent;
import com.mihoyo.zen.event.impl.SprintEvent;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin {
    @Unique
    private MotionEvent zen$currentMotionEvent;

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    private boolean zen$onSlowDown(LocalPlayer player) {
        boolean slow = player.isUsingItem();
        SlowdownEvent event = new SlowdownEvent(slow);
        if (ZenClient.isReady()) {
            ZenClient.instance.getEventBus().call(event);
        }
        return event.isSlowDown();
    }

    @Inject(
            method = "tick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/AbstractClientPlayer;tick()V")
    )
    private void zen$onTick(CallbackInfo ci) {
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(new SprintEvent());
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void zen$onAiStep(CallbackInfo ci) {
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(new GameTickEvent());
        }
    }

    @Inject(method = "sendPosition", at = @At("HEAD"))
    private void zen$onSendPositionPre(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        zen$currentMotionEvent = new MotionEvent(false, player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.onGround());
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(zen$currentMotionEvent);
        }
    }

    @Inject(method = "sendPosition", at = @At("TAIL"))
    private void zen$onSendPositionPost(CallbackInfo ci) {
        LocalPlayer player = (LocalPlayer) (Object) this;
        MotionEvent post = new MotionEvent(true, player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.onGround());
        if (zen$currentMotionEvent != null && zen$currentMotionEvent.isPost()) {
            zen$currentMotionEvent.setPre(true);
        }
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(post);
        }
        zen$currentMotionEvent = null;
    }

    @Redirect(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getX()D"))
    private double zen$getMotionX(Entity entity) {
        return zen$currentMotionEvent != null ? zen$currentMotionEvent.x : entity.getX();
    }

    @Redirect(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getY()D"))
    private double zen$getMotionY(Entity entity) {
        return zen$currentMotionEvent != null ? zen$currentMotionEvent.y : entity.getY();
    }

    @Redirect(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getZ()D"))
    private double zen$getMotionZ(Entity entity) {
        return zen$currentMotionEvent != null ? zen$currentMotionEvent.z : entity.getZ();
    }

    @Redirect(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getYRot()F"))
    private float zen$getMotionYaw(Entity entity) {
        return zen$currentMotionEvent != null ? zen$currentMotionEvent.yaw : entity.getYRot();
    }

    @Redirect(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getXRot()F"))
    private float zen$getMotionPitch(Entity entity) {
        return zen$currentMotionEvent != null ? zen$currentMotionEvent.pitch : entity.getXRot();
    }

    @Redirect(method = "sendPosition", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;onGround()Z"))
    private boolean zen$getMotionOnGround(Entity entity) {
        return zen$currentMotionEvent != null ? zen$currentMotionEvent.onGround : entity.onGround();
    }
}
