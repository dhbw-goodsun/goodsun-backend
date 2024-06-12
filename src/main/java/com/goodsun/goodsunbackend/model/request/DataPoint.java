package com.goodsun.goodsunbackend.model.request;

/**
 * Record class representing a data point with azimuth and elevation values.
 * Analogous to the DataPoint from the JSON request.
 *
 * @param azimuth the azimuth angle of the data point
 * @param elevation the elevation angle of the data point
 */
public record DataPoint(double azimuth, double elevation){}