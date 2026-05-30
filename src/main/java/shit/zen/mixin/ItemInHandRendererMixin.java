package shit.zen.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shit.zen.ClientBase;
import shit.zen.ZenClient;
import shit.zen.event.impl.UpdateHeldItemEvent;
import shit.zen.modules.impl.render.OldHitting;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getMainHandItem()Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack zen$onGetMainHandItem(LivingEntity entity) {
        UpdateHeldItemEvent event = new UpdateHeldItemEvent(InteractionHand.MAIN_HAND, ClientBase.mc.player.getMainHandItem());
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(event);
        }
        return event.getItemStack();
    }

    @Inject(method = "renderArmWithItem", at = @At("HEAD"), cancellable = true)
    private void zen$onRenderArmWithItem(AbstractClientPlayer player, float partialTicks, float pitch, InteractionHand hand, float swingProgress, ItemStack stack, float equippedProgress, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, CallbackInfo ci) {
        if (!ZenClient.isReady() || OldHitting.INSTANCE == null || !OldHitting.INSTANCE.isEnabled()) {
            return;
        }
        boolean useKeyHeld = ClientBase.mc.options.keyUse.isDown() && ClientBase.mc.player.getOffhandItem().isEmpty();
        boolean killAuraAttacking = OldHitting.INSTANCE.isKillAuraAttacking();
        if (hand != InteractionHand.MAIN_HAND
                || !(stack.getItem() instanceof SwordItem)
                || (!useKeyHeld && !killAuraAttacking)) {
            return;
        }
        ci.cancel();
        OldHitting.INSTANCE.applyHitAnimation(poseStack, swingProgress, player.getMainArm(), equippedProgress);
        boolean rightHand = player.getMainArm() == HumanoidArm.RIGHT;
        ((ItemInHandRenderer) (Object) this).renderItem(
                player, stack,
                rightHand ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
                !rightHand, poseStack, bufferSource, packedLight);
    }
}
