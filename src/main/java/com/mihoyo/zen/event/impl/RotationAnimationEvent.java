package com.mihoyo.zen.event.impl;

import lombok.*;
import com.mihoyo.zen.event.EventMarker;

@ToString
@Data
@AllArgsConstructor
public class RotationAnimationEvent
implements EventMarker {
    @Getter @Setter
    private float yaw, lastYaw;
    @Getter @Setter
    private float pitch, lastPitch;
}