package com.mihoyo.zen.event.impl;

import lombok.Generated;
import net.minecraft.world.entity.Entity;
import com.mihoyo.zen.event.EventMarker;

public record EntityRemoveEvent(boolean dead, Entity entity)
        implements EventMarker {
    @Override
    @Generated
    public boolean dead() {
        return this.dead;
    }

    @Override
    @Generated
    public Entity entity() {
        return this.entity;
    }

    @Generated
    public EntityRemoveEvent {
    }
}