package shit.zen.mixin;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketUtils;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.util.thread.BlockableEventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shit.zen.ZenClient;
import shit.zen.network.PacketHandlerUtil;

@Mixin(PacketUtils.class)
public class PacketUtilsMixin {
    private static final Logger ZEN_LOGGER = LoggerFactory.getLogger(PacketUtils.class);

    @Inject(
            method = "ensureRunningOnSameThread(Lnet/minecraft/network/protocol/Packet;Lnet/minecraft/network/PacketListener;Lnet/minecraft/util/thread/BlockableEventLoop;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private static <T extends PacketListener> void zen$onEnsureRunningOnSameThread(Packet<T> packet, T listener, BlockableEventLoop<?> loop, CallbackInfo ci) throws RunningOnDifferentThreadException {
        if (ZenClient.isReady()) {
            ci.cancel();
            PacketHandlerUtil.processPacket(ZEN_LOGGER, packet, listener, loop);
        }
    }
}
