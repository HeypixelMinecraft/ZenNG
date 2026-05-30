package shit.zen.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shit.zen.ClientBase;
import shit.zen.ZenClient;
import shit.zen.event.impl.RenderEntityEvent;
import shit.zen.event.impl.RotationAnimationEvent;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void zen$onRenderPre(LivingEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (!ZenClient.isReady()) return;
        RenderEntityEvent.Post pre = new RenderEntityEvent.Post((LivingEntityRenderer<?, ?>) (Object) this, entity, poseStack, bufferSource, partialTick, packedLight);
        ZenClient.getInstance().getEventBus().call(pre);
        if (pre.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void zen$onRenderPost(LivingEntity entity, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (!ZenClient.isReady()) return;
        RenderEntityEvent.Pre post = new RenderEntityEvent.Pre((LivingEntityRenderer<?, ?>) (Object) this, entity, poseStack, bufferSource, partialTick, packedLight);
        ZenClient.getInstance().getEventBus().call(post);
    }

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;rotLerp(FFF)F", ordinal = 1)
    )
    private float zen$onRenderHeadYawLerp(float delta, float start, float end, LivingEntity entity) {
        RotationAnimationEvent event = new RotationAnimationEvent(end, start, 0.0f, 0.0f);
        if (ZenClient.isReady() && entity == ClientBase.mc.player) {
            ZenClient.getInstance().getEventBus().call(event);
        }
        return Mth.rotLerp(delta, event.getLastYaw(), event.getYaw());
    }

    @Redirect(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;lerp(FFF)F", ordinal = 0)
    )
    private float zen$onRenderPitchLerp(float delta, float start, float end, LivingEntity entity) {
        RotationAnimationEvent event = new RotationAnimationEvent(0.0f, 0.0f, end, start);
        if (ZenClient.isReady() && entity == ClientBase.mc.player) {
            ZenClient.getInstance().getEventBus().call(event);
        }
        return Mth.lerp(delta, event.getLastPitch(), event.getPitch());
    }
}
