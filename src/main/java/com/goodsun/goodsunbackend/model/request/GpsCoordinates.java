package com.goodsun.goodsunbackend.model.request;

/**
 * Record class representing GPS coordinates with longitude and latitude values.
 * Analogous to the GpsCoordinates from the JSON request.
 *
 * @param longitude the longitude coordinate
 * @param latitude the latitude coordinate
 * @author Jonas Nunnenmacher
 */
public record GpsCoordinates(double longitude, double latitude) {}