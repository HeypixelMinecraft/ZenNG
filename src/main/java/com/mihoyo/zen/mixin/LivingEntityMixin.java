package com.mihoyo.zen.mixin;

import java.util.Map;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.mihoyo.zen.ClientBase;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.event.impl.EntityHurtEvent;
import com.mihoyo.zen.event.impl.FallFlyingEvent;
import com.mihoyo.zen.event.impl.JumpEvent;
import com.mihoyo.zen.event.impl.JumpMarkerEvent;
import com.mihoyo.zen.event.impl.RotationAnimationEvent;
import com.mihoyo.zen.modules.impl.movement.NoDelay;
import com.mihoyo.zen.modules.impl.movement.Scaffold;
import com.mihoyo.zen.modules.impl.render.FullBright;
import com.mihoyo.zen.utils.game.PlayerUtil;
import com.mihoyo.zen.utils.misc.ReflectionUtil;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    private Map<MobEffect, MobEffectInstance> activeEffects;

    @Inject(method = "aiStep", at = @At("HEAD"))
    private void zen$onAiStep(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (zen$shouldFastDig(entity)) {
            ReflectionUtil.setJumpDelay(entity, 0);
        }
    }

    private static boolean zen$shouldFastDig(LivingEntity entity) {
        if (!ZenClient.isReady() || entity != ClientBase.mc.player) return false;
        NoDelay noDelay = NoDelay.INSTANCE;
        if (noDelay == null || !noDelay.isEnabled() || !noDelay.fastDig.getValue()) return false;
        return Scaffold.INSTANCE == null || !Scaffold.INSTANCE.isEnabled();
    }

    /**
     * @author OpenZen
     * @reason Preserve FullBright night vision behavior from the old Patchify overwrite.
     */
    @Overwrite
    public boolean hasEffect(MobEffect effect) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (ClientBase.mc != null
                && entity == ClientBase.mc.player
                && effect == MobEffects.NIGHT_VISION
                && FullBright.INSTANCE != null
                && FullBright.INSTANCE.isEnabled()) {
            return true;
        }
        return activeEffects.containsKey(effect);
    }

    @Redirect(method = "tickHeadTurn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getYRot()F"))
    private float zen$onTickHeadTurn(Entity entity) {
        float currentYaw = entity.getYRot();
        RotationAnimationEvent event = new RotationAnimationEvent(currentYaw, 0, 0, 0);
        if (ZenClient.isReady() && entity == ClientBase.mc.player) {
            ZenClient.getInstance().getEventBus().call(event);
        }
        return event.getYaw();
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getYRot()F"))
    private float zen$onTickGetYRot(Entity entity) {
        return ClientBase.yaw;
    }

    @Redirect(method = "jumpFromGround", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getYRot()F"))
    private float zen$onJumpGetYRot(Entity entity) {
        float yaw = entity.getYRot();
        JumpMarkerEvent event = new JumpMarkerEvent(yaw);
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(event);
        }
        ClientBase.yaw = event.getYaw();
        return event.getYaw();
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    private void zen$onTravel(Vec3 movement, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity == null || entity != ClientBase.mc.player || !ZenClient.isReady()) return;
        JumpEvent event = new JumpEvent();
        ZenClient.getInstance().getEventBus().call(event);
        if (event.isCancelled()) {
            PlayerUtil.updateWalkAnim();
            ci.cancel();
        }
    }

    @Redirect(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getXRot()F"))
    private float zen$onTravelGetXRot(Entity entity) {
        float pitch = entity.getXRot();
        FallFlyingEvent event = new FallFlyingEvent(pitch);
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(event);
        }
        return event.getPitch();
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    private void zen$onHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(new EntityHurtEvent(entity, source, amount));
        }
    }
}
