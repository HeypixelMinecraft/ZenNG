package com.mihoyo.zen.event.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.mihoyo.zen.event.EventMarker;

@Data
@AllArgsConstructor
public class JumpMarkerEvent
implements EventMarker {
    private float yaw;
}