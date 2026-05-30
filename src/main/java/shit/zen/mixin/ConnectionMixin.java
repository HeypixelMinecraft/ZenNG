package shit.zen.mixin;

import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shit.zen.ClientBase;
import shit.zen.ZenClient;
import shit.zen.event.impl.PacketEvent;
import shit.zen.event.impl.PrePacketEvent;
import shit.zen.utils.misc.PacketUtil;

@Mixin(Connection.class)
public class ConnectionMixin {
    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void zen$onPacketReceive(ChannelHandlerContext context, Packet<?> packet, CallbackInfo ci) {
        if (ClientBase.mc == null || ClientBase.mc.level == null || ClientBase.mc.player == null || packet == null || !ZenClient.isReady()) {
            return;
        }
        PrePacketEvent prePacket = new PrePacketEvent(packet);
        ZenClient.getInstance().getEventBus().call(prePacket);
        if (prePacket.isCancelled()) {
            ci.cancel();
            return;
        }
        PacketEvent event = new PacketEvent(prePacket.getPacket(), false);
        ZenClient.getInstance().getEventBus().call(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void zen$onPacketSend(Packet<?> packet, PacketSendListener listener, CallbackInfo ci) {
        if (ClientBase.mc == null || ClientBase.mc.level == null || ClientBase.mc.player == null || packet == null || !ZenClient.isReady()) {
            return;
        }
        if (PacketUtil.shouldBypass((Packet) packet)) {
            return;
        }
        PacketEvent event = new PacketEvent(packet, true);
        ZenClient.getInstance().getEventBus().call(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
