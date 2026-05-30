package com.mihoyo.zen.event.impl;

import lombok.*;
import com.mihoyo.zen.event.EventMarker;

@Data
@AllArgsConstructor
public class FallFlyingEvent
implements EventMarker {
    @Getter @Setter
    private float pitch;
}