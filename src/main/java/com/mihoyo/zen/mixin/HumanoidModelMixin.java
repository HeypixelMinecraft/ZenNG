package com.mihoyo.zen.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.mihoyo.zen.ClientBase;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.event.impl.CameraPitchEvent;

@Mixin(HumanoidModel.class)
public class HumanoidModelMixin {
    @ModifyVariable(method = "setupAnim", at = @At("HEAD"), argsOnly = true, ordinal = 4)
    private float zen$onPitchRender(float pitch, LivingEntity entity) {
        if (ZenClient.isReady() && entity == ClientBase.mc.player && ClientBase.mc.level != null) {
            CameraPitchEvent event = (CameraPitchEvent) ZenClient.instance.getEventBus().call(new CameraPitchEvent(pitch));
            return event.getPitch();
        }
        return pitch;
    }
}
