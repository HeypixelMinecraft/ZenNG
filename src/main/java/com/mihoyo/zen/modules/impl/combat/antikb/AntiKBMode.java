package com.mihoyo.zen.modules.impl.combat.antikb;

import java.util.HashMap;
import java.util.Optional;
import com.mihoyo.zen.ClientBase;
import com.mihoyo.zen.event.impl.DisconnectEvent;
import com.mihoyo.zen.event.impl.GameTickEvent;
import com.mihoyo.zen.event.impl.MotionEvent;
import com.mihoyo.zen.event.impl.PreMotionEvent;
import com.mihoyo.zen.event.impl.ReceivePacketEvent;
import com.mihoyo.zen.event.impl.Render2DEvent;
import com.mihoyo.zen.event.impl.RenderEvent;
import com.mihoyo.zen.event.impl.RotationEvent;
import com.mihoyo.zen.event.impl.SprintEvent;
import com.mihoyo.zen.event.impl.StrafeEvent;
import com.mihoyo.zen.event.impl.TickEvent;

public abstract class AntiKBMode
extends ClientBase {
    protected final String name;
    private static final HashMap<Class<? extends AntiKBMode>, AntiKBMode> modes = new HashMap<>();

    public AntiKBMode(String string) {
        this.name = string;
    }

    public static void initModes() {
        modes.put(JumpResetMode.class, new JumpResetMode());
        modes.put(MixMode.class, new MixMode());
        modes.put(NoXZMode.class, new NoXZMode());
    }

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract String getName();

    public static Optional<AntiKBMode> findMode(String string) {
        return modes.values().stream().filter(antiKBMode -> antiKBMode.name.equals(string)).findFirst();
    }

    public abstract void onRotation(RotationEvent var1);

    public abstract void onReceivePacket(ReceivePacketEvent var1);

    public abstract void onDisconnect(DisconnectEvent var1);

    public abstract void onPreMotion(PreMotionEvent var1);

    public abstract void onGameTick(GameTickEvent var1);

    public abstract void onSprint(SprintEvent var1);

    public abstract void onTick(TickEvent var1);

    public abstract void onStrafe(StrafeEvent var1);

    public abstract void onMotion(MotionEvent var1);

    public void onRender(RenderEvent renderEvent) {
    }

    public void onRender2D(Render2DEvent render2DEvent) {
    }

    public boolean isActive() {
        return false;
    }
}