package com.mihoyo.zen.modules.impl.movement;

import com.mihoyo.zen.modules.Category;
import com.mihoyo.zen.modules.Module;
import com.mihoyo.zen.settings.impl.BooleanSetting;
import com.mihoyo.zen.utils.rotation.RotationHandler;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class MoveFix extends Module {
    public static MoveFix INSTANCE;

    public final BooleanSetting silent = new BooleanSetting("Silent", true);

    private boolean silentFix = true;
    private boolean doFix;
    private boolean overwritten;

    public MoveFix() {
        super("MoveFix", Category.MOVEMENT);
        INSTANCE = this;
        this.updateOverwrite();
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        if (!this.overwritten) {
            this.doFix = true;
            this.silentFix = this.silent.getValue();
        }
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        if (!this.overwritten) {
            this.doFix = false;
        }
    }

    public void applyForceStrafe(boolean silentFix, boolean doFix) {
        this.silentFix = silentFix;
        this.doFix = doFix;
        this.overwritten = true;
    }

    public void updateOverwrite() {
        this.overwritten = false;
        this.doFix = this.isEnabled();
        this.silentFix = this.silent.getValue();
    }

    public boolean handleMoveRelative(Entity entity, float speed, Vec3 movement, float rotationYaw) {
        if (!this.doFix
                || mc.player == null
                || entity != mc.player
                || !RotationHandler.isRotating
                || RotationHandler.targetRotation == null) {
            return false;
        }

        float strafe = (float) movement.x;
        float forward = (float) movement.z;
        float friction = speed;
        float factor = strafe * strafe + forward * forward;

        int angleDiff = (int) ((Mth.wrapDegrees(mc.player.getYRot() - rotationYaw - 22.5f - 135.0f) + 180.0f) / 45.0f);
        float calcYaw = this.silentFix ? rotationYaw + 45.0f * angleDiff : rotationYaw;

        if (this.silentFix) {
            switch (angleDiff) {
                case 1, 3, 5, 7, 9 -> {
                    float calcMoveDir = Math.max(Math.abs(strafe), Math.abs(forward));
                    float calcMultiplier = Mth.sqrt(calcMoveDir * calcMoveDir / Math.min(1.0f, calcMoveDir * 2.0f));
                    boolean movingForward = Math.abs(forward) > 0.005f;
                    boolean movingStrafe = Math.abs(strafe) > 0.005f;
                    if ((movingForward || movingStrafe) && !(movingForward && movingStrafe)) {
                        friction /= calcMultiplier;
                    } else if (movingForward && movingStrafe) {
                        friction *= calcMultiplier;
                    }
                }
                default -> {
                }
            }
        }

        if (factor >= 1.0E-4f) {
            factor = Math.max(Mth.sqrt(factor), 1.0f);
            factor = friction / factor;
            strafe *= factor;
            forward *= factor;

            float yawSin = Mth.sin(calcYaw * ((float) Math.PI / 180.0f));
            float yawCos = Mth.cos(calcYaw * ((float) Math.PI / 180.0f));
            entity.setDeltaMovement(entity.getDeltaMovement().add(
                    strafe * yawCos - forward * yawSin,
                    0.0,
                    forward * yawCos + strafe * yawSin));
        }

        return true;
    }
}
