package com.mihoyo.zen.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.modules.impl.render.NameTags;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {
    @Inject(method = "renderNameTag", at = @At("HEAD"), cancellable = true)
    private void zen$onRenderNameTag(Entity entity, Component component, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (entity instanceof LivingEntity && ZenClient.isReady() && NameTags.INSTANCE != null && NameTags.INSTANCE.isEnabled()) {
            ci.cancel();
        }
    }
}
