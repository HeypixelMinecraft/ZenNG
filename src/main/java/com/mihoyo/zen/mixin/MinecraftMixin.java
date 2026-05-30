package com.mihoyo.zen.mixin;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mihoyo.zen.ClientBase;
import com.mihoyo.zen.ZenClient;
import com.mihoyo.zen.event.impl.DisconnectEvent;
import com.mihoyo.zen.event.impl.PostMotionEvent;
import com.mihoyo.zen.event.impl.PreMotionEvent;
import com.mihoyo.zen.event.impl.TickEvent;
import com.mihoyo.zen.modules.impl.movement.NoSlow;
import com.mihoyo.zen.modules.impl.render.ESP;
import com.mihoyo.zen.render.Renderer;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Unique
    private static volatile boolean zen$initialized = false;
    @Unique
    private static HitResult zen$savedHitResult;

    @Inject(method = "tick", at = @At("HEAD"))
    private void zen$onTick(CallbackInfo ci) {
        Minecraft minecraft = (Minecraft) (Object) this;
        if (!zen$initialized) {
            synchronized (MinecraftMixin.class) {
                if (!zen$initialized) {
                    ClientBase.mc = ZenClient.getMcInstance();
                    ClientBase.isLoading = true;
                    ModList.get().getMods().removeIf(modInfo -> modInfo.getModId().equals("hey"));
                    List<IModFileInfo> toRemove = new ArrayList<>();
                    for (IModFileInfo modFile : ModList.get().getModFiles()) {
                        for (IModInfo modInfo : modFile.getMods()) {
                            if (modInfo.getModId().equals("hey")) {
                                toRemove.add(modFile);
                            }
                        }
                    }
                    ModList.get().getModFiles().removeAll(toRemove);
                    new ZenClient();
                    zen$initialized = true;
                }
            }
        }
        if (ZenClient.isReady()) {
            ZenClient.serverTickRate = 1.0f;
            ClientBase.yaw = minecraft.player.getYRot();
            ZenClient.instance.getEventBus().call(new TickEvent());
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void zen$onTickPost(CallbackInfo ci) {
        if (ZenClient.isReady()) {
            ZenClient.instance.getEventBus().call(new PostMotionEvent());
        }
    }

    @Inject(method = "close", at = @At("HEAD"))
    private void zen$onClose(CallbackInfo ci) {
        ZenClient.getInstance().shutdown();
    }

    @Inject(method = "setLevel", at = @At("HEAD"))
    private void zen$onSetLevel(ClientLevel level, CallbackInfo ci) {
        if (ZenClient.isReady()) {
            ZenClient.getInstance().getEventBus().call(new DisconnectEvent());
        }
    }

    @Inject(method = "handleKeybinds", at = @At("HEAD"), cancellable = true)
    private void zen$onHandleKeybinds(CallbackInfo ci) {
        if (ZenClient.isReady()) {
            PreMotionEvent event = new PreMotionEvent();
            ZenClient.getInstance().getEventBus().call(event);
            if (event.isCancelled()) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "startUseItem", at = @At("HEAD"))
    private void zen$onStartUseItemPre(CallbackInfo ci) {
        Minecraft minecraft = (Minecraft) (Object) this;
        if (NoSlow.isBlocking(minecraft)) {
            zen$savedHitResult = minecraft.hitResult;
            Vec3 location = zen$savedHitResult.getLocation();
            minecraft.hitResult = BlockHitResult.miss(location, Direction.DOWN, BlockPos.containing(location));
        }
    }

    @Inject(method = "startUseItem", at = @At("TAIL"))
    private void zen$onStartUseItemPost(CallbackInfo ci) {
        Minecraft minecraft = (Minecraft) (Object) this;
        if (zen$savedHitResult != null) {
            minecraft.hitResult = zen$savedHitResult;
            zen$savedHitResult = null;
        }
    }

    @Redirect(
            method = "shouldEntityAppearGlowing",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isCurrentlyGlowing()Z")
    )
    private boolean zen$onShouldEntityGlow(Entity entity) {
        if (ZenClient.isReady()
                && ZenClient.instance.getModuleManager() != null
                && ESP.INSTANCE != null
                && ESP.INSTANCE.isGlowing(entity)) {
            return true;
        }
        return entity.isCurrentlyGlowing();
    }

    @Inject(method = "resizeDisplay", at = @At("TAIL"))
    private void zen$onResizeDisplay(CallbackInfo ci) {
        Minecraft minecraft = (Minecraft) (Object) this;
        Renderer.setGuiScaleVerified((float) minecraft.getWindow().getGuiScale());
    }
}
