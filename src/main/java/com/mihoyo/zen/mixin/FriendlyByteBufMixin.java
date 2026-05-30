package com.mihoyo.zen.mixin;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.mihoyo.zen.modules.impl.render.NameProtect;

@Mixin(FriendlyByteBuf.class)
public class FriendlyByteBufMixin {
    @Redirect(
            method = "readComponent",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/network/chat/Component$Serializer;fromJson(Ljava/lang/String;)Lnet/minecraft/network/chat/MutableComponent;")
    )
    private MutableComponent zen$readUtfWithNameProtection(String json) {
        String filtered = NameProtect.replacePlayerName(json);
        return Component.Serializer.fromJson(filtered);
    }
}
