package com.mihoyo.zen.event.impl;

import lombok.*;
import com.mihoyo.zen.event.EventMarker;

@AllArgsConstructor
@Data
public class UseItemRayTraceEvent
implements EventMarker {
    @Getter @Setter
    private float yaw;
    @Getter @Setter
    private float pitch;
}