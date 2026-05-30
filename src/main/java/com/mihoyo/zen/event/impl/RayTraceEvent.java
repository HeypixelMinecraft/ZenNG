package com.mihoyo.zen.event.impl;

import lombok.*;
import net.minecraft.world.entity.Entity;
import com.mihoyo.zen.event.EventMarker;

@Data
@AllArgsConstructor
public class RayTraceEvent
implements EventMarker {
    @Getter @Setter
    public Entity entity;
    @Getter @Setter
    public float yaw;
    @Getter @Setter
    public float pitch;
}