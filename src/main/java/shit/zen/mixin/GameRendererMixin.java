package shit.zen.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shit.zen.ZenClient;
import shit.zen.event.impl.GlRenderEvent;
import shit.zen.event.impl.Render2DEvent;
import shit.zen.modules.impl.render.AspectRatio;
import shit.zen.modules.impl.render.FullBright;
import shit.zen.modules.impl.render.NoHurtCam;
import shit.zen.render.Renderer;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    /**
     * @author OpenZen
     * @reason Preserve FullBright night-vision scaling behavior.
     */
    @Overwrite
    public static float getNightVisionScale(LivingEntity entity, float partial) {
        if (FullBright.INSTANCE != null && FullBright.INSTANCE.isEnabled()) {
            return FullBright.INSTANCE.brightnessSetting.getValue().floatValue() / 100.0f;
        }
        return entity.hasEffect(MobEffects.NIGHT_VISION) ? 1.0f : 0.0f;
    }

    @Inject(
            method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;F)V", shift = At.Shift.AFTER)
    )
    private void zen$onRender(float partialTick, long nanoTime, boolean renderLevel, CallbackInfo ci) {
        GameRenderer gameRenderer = (GameRenderer) (Object) this;
        GuiGraphics graphics = new GuiGraphics(gameRenderer.getMinecraft(), gameRenderer.getMinecraft().renderBuffers().bufferSource());
        Render2DEvent event = new Render2DEvent(graphics.pose(), graphics, partialTick);
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(event);
            graphics.pose().pushPose();
            Renderer.render(graphics, glEvent -> {
                GlRenderEvent glRender = new GlRenderEvent(graphics, graphics.pose(), glEvent);
                ZenClient.getInstance().getEventBus().call(glRender);
            });
            graphics.pose().popPose();
        }
    }

    @Redirect(
            method = "getProjectionMatrix",
            at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4f;setPerspective(FFFF)Lorg/joml/Matrix4f;", remap = false)
    )
    private Matrix4f zen$onGetProjectionMatrix(Matrix4f matrix, float fovy, float aspect, float zNear, float zFar, double fov) {
        GameRenderer gameRenderer = (GameRenderer) (Object) this;
        if (!ZenClient.isReady() || AspectRatio.INSTANCE == null || !AspectRatio.INSTANCE.isEnabled()) {
            return matrix.setPerspective(fovy, aspect, zNear, zFar);
        }
        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().identity();
        GameRendererAccessor accessor = (GameRendererAccessor) gameRenderer;
        float zoom = accessor.zen$getZoom();
        float zoomX = accessor.zen$getZoomX();
        float zoomY = accessor.zen$getZoomY();
        if (zoom != 1.0f) {
            poseStack.translate(zoomX, -zoomY, 0.0f);
            poseStack.scale(zoom, zoom, 1.0f);
        }
        poseStack.last().pose().mul(new Matrix4f().setPerspective(
                (float) (fov * (float) (Math.PI / 180.0)),
                AspectRatio.INSTANCE.ratioSetting.getValue().floatValue(),
                0.05f,
                gameRenderer.getDepthFar()));
        return poseStack.last().pose();
    }

    @Inject(method = "bobHurt", at = @At("HEAD"), cancellable = true)
    private void zen$onBobHurt(PoseStack poseStack, float partial, CallbackInfo ci) {
        if (ZenClient.isReady() && NoHurtCam.INSTANCE != null && NoHurtCam.INSTANCE.isEnabled()) {
            ci.cancel();
        }
    }
}
