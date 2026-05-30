package shit.zen.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import shit.zen.ZenClient;
import shit.zen.event.impl.UseItemRayTraceEvent;

@Mixin(Item.class)
public class ItemMixin {
    @Redirect(
            method = "getPlayerPOVHitResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getXRot()F")
    )
    private static float zen$onGetPOVHitXRot(Entity entity, Level level, Player player, ClipContext.Fluid fluidContext) {
        UseItemRayTraceEvent event = new UseItemRayTraceEvent(player.getYRot(), player.getXRot());
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(event);
        }
        return event.getPitch();
    }

    @Redirect(
            method = "getPlayerPOVHitResult",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getYRot()F")
    )
    private static float zen$onGetPOVHitYRot(Entity entity, Level level, Player player, ClipContext.Fluid fluidContext) {
        UseItemRayTraceEvent event = new UseItemRayTraceEvent(player.getYRot(), player.getXRot());
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(event);
        }
        return event.getYaw();
    }
}
