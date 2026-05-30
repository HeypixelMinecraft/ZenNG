package com.mihoyo.zen.modules.impl.movement;

import java.util.ArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import com.mihoyo.zen.event.impl.MotionEvent;
import com.mihoyo.zen.event.impl.SneakEvent;
import com.mihoyo.zen.modules.Category;
import com.mihoyo.zen.modules.Module;
import com.mihoyo.zen.modules.impl.combat.KillAura;
import com.mihoyo.zen.settings.impl.BooleanSetting;
import com.mihoyo.zen.settings.impl.NumberSetting;
import com.mihoyo.zen.utils.animation.Timer;
import com.mihoyo.zen.utils.game.MovementUtil;
import com.mihoyo.zen.event.EventTarget;

public class TargetStrafe
extends Module {
    public static TargetStrafe INSTANCE;
    private final Timer collisionTimer = new Timer();
    private final BooleanSetting smartStrafe = new BooleanSetting("Jump Key Only", true);
    private final NumberSetting range = new NumberSetting("Range", 0.5f, 0.1f, 2.0f, 0.1f);
    private final NumberSetting switchDelay = new NumberSetting("Switch Delay", 1000, 100, 5000, 100);
    public static int strafeDirectionSign;
    public static Entity strafeTarget;
    private final Timer switchTimer = new Timer();

    public TargetStrafe() {
        super("TargetStrafe", Category.MOVEMENT);
        INSTANCE = this;
    }

    public static float getRange() {
        return TargetStrafe.INSTANCE.range.getValue().floatValue();
    }

    public static boolean isSmartStrafe() {
        return TargetStrafe.INSTANCE.smartStrafe.getValue();
    }

    @EventTarget
    public void onMotion(MotionEvent motionEvent) {
        if (motionEvent.isPost() && mc.player != null) {
            boolean aboveVoid;
            AABB playerBox;
            if (KillAura.target == null) {
                strafeTarget = null;
            } else if (this.switchTimer.hasPassed(this.switchDelay.getValue().intValue()) || strafeTarget == null) {
                ArrayList<Entity> sortedTargets = new ArrayList<>(KillAura.targetList);
                sortedTargets.sort((a, b) -> {
                    float distA = mc.player.distanceTo(a);
                    float distB = mc.player.distanceTo(b);
                    return Float.compare(distA, distB);
                });
                if (!sortedTargets.isEmpty()) {
                    strafeTarget = sortedTargets.get(0);
                    this.switchTimer.reset();
                }
            }
            playerBox = mc.player.getBoundingBox();
            aboveVoid = MovementUtil.isAboveVoid(playerBox.minX, playerBox.minY, playerBox.minZ) || MovementUtil.isAboveVoid(playerBox.minX, playerBox.minY, playerBox.maxZ) || MovementUtil.isAboveVoid(playerBox.maxX, playerBox.minY, playerBox.minZ) || MovementUtil.isAboveVoid(playerBox.maxX, playerBox.minY, playerBox.maxZ);
            if ((aboveVoid || mc.player.horizontalCollision) && this.collisionTimer.hasPassedDouble(500.0, true)) {
                strafeDirectionSign *= -1;
            }
        }
    }

    @EventTarget
    public void onSneak(SneakEvent sneakEvent) {
        if (!sneakEvent.isCancelled() && !FireballBlink.INSTANCE.isEnabled()) {
            sneakEvent.setCancelled(true);
        }
    }

    static {
        strafeDirectionSign = 1;
    }
}