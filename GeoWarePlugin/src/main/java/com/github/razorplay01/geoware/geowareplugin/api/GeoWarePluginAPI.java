package com.github.razorplay01.geoware.geowareplugin.api;

/**
 * Public API for interacting with GeoWarePlugin.
 */
public interface GeoWarePluginAPI {
    /**
     * Gets the public API for managing player points.
     *
     * @return The PointsManagerAPI instance
     */
    PointsManagerAPI getPointsManagerAPI();
}