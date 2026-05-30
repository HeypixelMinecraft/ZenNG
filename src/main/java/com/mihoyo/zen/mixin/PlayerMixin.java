package com.mihoyo.zen.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mihoyo.zen.ClientBase;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.event.impl.EntityRemoveEvent;

@Mixin(Player.class)
public class PlayerMixin {
    @Redirect(method = "die", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getYRot()F"))
    private float zen$onDieGetYRot(Entity entity) {
        return ClientBase.yaw;
    }

    @Redirect(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getYRot()F"))
    private float zen$onDropGetYRot(Entity entity, ItemStack stack, boolean dropAll, boolean traceItem) {
        return ClientBase.yaw;
    }

    @Redirect(method = "attack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getYRot()F"))
    private float zen$onAttackGetYRot(Entity entity) {
        return ClientBase.yaw;
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void zen$onAttackPre(Entity target, CallbackInfo ci) {
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(new EntityRemoveEvent(false, target));
        }
    }

    @Inject(method = "attack", at = @At("TAIL"))
    private void zen$onAttackPost(Entity target, CallbackInfo ci) {
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(new EntityRemoveEvent(true, target));
        }
    }
}
