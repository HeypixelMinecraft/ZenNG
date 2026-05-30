package shit.zen.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import shit.zen.ClientBase;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Redirect(
            method = "tickNonPassenger",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V", ordinal = 1)
    )
    private void zen$onTickEntity(Entity entity) {
        if (!ClientBase.delayPackets.isEmpty() && entity == ClientBase.mc.player) {
            Runnable delayed = ClientBase.delayPackets.poll();
            if (delayed != null) {
                delayed.run();
            }
        } else {
            entity.tick();
        }
    }
}
