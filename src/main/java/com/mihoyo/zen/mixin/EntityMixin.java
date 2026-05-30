package com.mihoyo.zen.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mihoyo.zen.ClientBase;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.event.impl.PreTickEvent;
import com.mihoyo.zen.event.impl.RayTraceEvent;
import com.mihoyo.zen.event.impl.RotationEvent;
import com.mihoyo.zen.event.impl.SneakEvent;
import com.mihoyo.zen.event.impl.StuckInBlockEvent;
import com.mihoyo.zen.modules.impl.movement.MoveFix;
import com.mihoyo.zen.utils.misc.ReflectionUtil;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "makeStuckInBlock", at = @At("TAIL"))
    private void zen$onMakeStuckInBlock(BlockState state, Vec3 motion, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (ClientBase.mc.player != entity) return;
        StuckInBlockEvent event = new StuckInBlockEvent(state, motion);
        ZenClient.getInstance().getEventBus().call(event);
        if (event.isCancelled()) {
            ReflectionUtil.setInstanceField(entity, Vec3.ZERO, "stuckSpeedMultiplier", "net/minecraft/world/entity/Entity");
            return;
        }
        ReflectionUtil.setInstanceField(entity, event.getMotion(), "stuckSpeedMultiplier", "net/minecraft/world/entity/Entity");
    }

    @Inject(method = "push(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
    private void zen$onPush(Entity pushedEntity, CallbackInfo ci) {
        Entity entity = (Entity) (Object) this;
        if (!ZenClient.isReady() || entity != ClientBase.mc.player || entity.isInWater()) return;
        SneakEvent event = new SneakEvent();
        ZenClient.getInstance().getEventBus().call(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    /**
     * @author OpenZen
     * @reason Preserve rotation event hook from the old Patchify overwrite.
     */
    @Overwrite
    public void moveRelative(float speed, Vec3 movement) {
        Entity entity = (Entity) (Object) this;
        RotationEvent event = new RotationEvent(entity.getYRot(), speed);
        if (ZenClient.isReady() && entity == ClientBase.mc.player) {
            ZenClient.getInstance().getEventBus().call(event);
        }
        if (MoveFix.INSTANCE != null && MoveFix.INSTANCE.handleMoveRelative(entity, speed, movement, event.getYaw())) {
            if (ZenClient.isReady() && entity == ClientBase.mc.player) {
                ZenClient.getInstance().getEventBus().call(new PreTickEvent());
            }
            return;
        }
        Vec3 result = zen$applyRotation(movement, speed, event.getYaw());
        entity.setDeltaMovement(entity.getDeltaMovement().add(result));
        if (ZenClient.isReady() && entity == ClientBase.mc.player) {
            ZenClient.getInstance().getEventBus().call(new PreTickEvent());
        }
    }

    /**
     * @author OpenZen
     * @reason Preserve ray trace rotation event hook from the old Patchify overwrite.
     */
    @Overwrite
    public Vec3 calculateViewVector(float pitch, float yaw) {
        Entity entity = (Entity) (Object) this;
        RayTraceEvent event = new RayTraceEvent(entity, yaw, pitch);
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(event);
        }
        yaw = event.getYaw();
        pitch = event.getPitch();
        float pitchRad = pitch * (float) (Math.PI / 180.0);
        float yawRad = -yaw * (float) (Math.PI / 180.0);
        float cosYaw = Mth.cos(yawRad);
        float sinYaw = Mth.sin(yawRad);
        float cosPitch = Mth.cos(pitchRad);
        float sinPitch = Mth.sin(pitchRad);
        return new Vec3(sinYaw * cosPitch, -sinPitch, cosYaw * cosPitch);
    }

    private static Vec3 zen$applyRotation(Vec3 movement, float speed, float yaw) {
        double lengthSq = movement.lengthSqr();
        if (lengthSq < 1.0e-7) {
            return Vec3.ZERO;
        }
        Vec3 normalized = (lengthSq > 1.0 ? movement.normalize() : movement).scale(speed);
        float sinYaw = Mth.sin(yaw * (float) (Math.PI / 180.0));
        float cosYaw = Mth.cos(yaw * (float) (Math.PI / 180.0));
        return new Vec3(normalized.x * cosYaw - normalized.z * sinYaw,
                normalized.y,
                normalized.z * cosYaw + normalized.x * sinYaw);
    }
}
