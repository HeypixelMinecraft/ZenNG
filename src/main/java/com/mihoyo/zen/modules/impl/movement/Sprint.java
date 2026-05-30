package com.mihoyo.zen.modules.impl.movement;

import java.util.HashMap;
import net.minecraft.client.KeyMapping;
import com.mihoyo.zen.event.impl.RotationEvent;
import com.mihoyo.zen.modules.Category;
import com.mihoyo.zen.modules.Module;
import com.mihoyo.zen.modules.impl.player.InventoryManager;
import com.mihoyo.zen.event.EventTarget;

public class Sprint
extends Module {
    private final HashMap<String, String> keyMappings = new HashMap<>();
    public Sprint() {
        super("Sprint", Category.MOVEMENT);
        this.setEnabled(true);
    }

    @EventTarget
    public void onRotation(RotationEvent rotationEvent) {
        if (GuiMove.INSTANCE.isEnabled() && InventoryManager.isPerformingAction) {
            return;
        }
        mc.options.toggleSprint().set(false);
        KeyMapping.set(mc.options.keySprint.getKey(), true);
    }
}