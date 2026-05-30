package com.mihoyo.zen.utils.misc;

import java.io.InputStream;

/**
 * Unified resource opener for the OpenZen jar.
 *
 * <p>Always returns {@code null} (not throwing) when the resource is missing,
 * matching the contract of {@link Class#getResourceAsStream(String)}.</p>
 */
public final class Assets {
    private Assets() {
    }

    public static InputStream open(String classpath) {
        return Assets.class.getResourceAsStream(classpath);
    }
}
