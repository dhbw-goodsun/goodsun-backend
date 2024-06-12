package com.goodsun.goodsunbackend.model.calculation;

/**
 * Record class representing the position of the sun.
 *
 * @param azimuth the azimuth angle of the sun
 * @param elevation the elevation angle of the sun
 * @author Jonas Nunnenmacher
 */
public record SunPosition(double azimuth, double elevation) {
}
