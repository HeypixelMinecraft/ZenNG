package com.mihoyo.zen.event.impl;

import com.mihoyo.zen.event.EventMarker;
import com.mihoyo.zen.event.Prioritized;

public abstract class PrioritizedEvent
implements Prioritized,
EventMarker {
    private final byte priority;

    protected PrioritizedEvent(byte by) {
        this.priority = by;
    }

    @Override
    public byte getPriority() {
        return this.priority;
    }
}