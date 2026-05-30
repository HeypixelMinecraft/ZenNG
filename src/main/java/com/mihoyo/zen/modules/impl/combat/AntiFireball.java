package com.mihoyo.zen.modules.impl.combat;

import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Fireball;
import com.mihoyo.zen.event.impl.MotionEvent;
import com.mihoyo.zen.modules.Category;
import com.mihoyo.zen.modules.Module;
import com.mihoyo.zen.utils.misc.ChatUtil;
import com.mihoyo.zen.event.EventTarget;

public class AntiFireball
extends Module {
    public AntiFireball() {
        super("AntiFireball", Category.COMBAT);
    }

    @EventTarget
    public void onMotion(MotionEvent motionEvent) {
        if (!motionEvent.isPost()) {
            return;
        }
        if (mc.player == null || mc.level == null || mc.gameMode == null) {
            return;
        }
        Stream<Entity> stream = StreamSupport.stream(mc.level.entitiesForRendering().spliterator(), false);
        Optional<Fireball> optional = stream.filter(entity -> entity instanceof Fireball && (double)mc.player.distanceTo(entity) < 6.0).map(entity -> (Fireball)entity).findFirst();
        if (!optional.isPresent()) {
            return;
        }
        Fireball fireball = optional.get();
        ChatUtil.print("§c[AntiFireball] Attacking fireball...");
        mc.gameMode.attack(mc.player, fireball);
        mc.player.swing(InteractionHand.MAIN_HAND);
    }
}