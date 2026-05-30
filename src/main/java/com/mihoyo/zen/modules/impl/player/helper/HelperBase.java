package com.mihoyo.zen.modules.impl.player.helper;

import lombok.Getter;
import com.mihoyo.zen.ClientBase;
import com.mihoyo.zen.event.impl.MotionEvent;
import com.mihoyo.zen.event.impl.PreMotionEvent;
import com.mihoyo.zen.event.impl.RenderEvent;
import com.mihoyo.zen.event.impl.TickEvent;
import com.mihoyo.zen.utils.rotation.Rotation;

public abstract class HelperBase
extends ClientBase {
    @Getter
    private final String name;

    public HelperBase(String string) {
        this.name = string;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onTick(TickEvent tickEvent) {
    }

    public void onMotion(MotionEvent motionEvent) {
    }

    public void onRender(RenderEvent renderEvent) {
    }

    public void onPreMotion(PreMotionEvent preMotionEvent) {
    }

    public boolean isActive() {
        return false;
    }

    public Rotation getTargetRotation() {
        return null;
    }

    }