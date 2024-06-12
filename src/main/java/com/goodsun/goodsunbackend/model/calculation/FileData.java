package com.goodsun.goodsunbackend.model.calculation;

import com.goodsun.goodsunbackend.model.request.GpsCoordinates;

import java.time.LocalDateTime;

/**
 * Record class representing the data from a weather data file.
 *
 * @param gpsCoordinates the GPS coordinates where the data was recorded
 * @param localDateTime the date and time when the data was recorded
 * @param temperature the temperature at the recorded time and location
 * @param diffuseHorizontalIrradiance the diffuse horizontal irradiance at the recorded time and location
 * @param directNormalIrradiance the direct normal irradiance at the recorded time and location
 * @param globalHorizontalIrradiance the global horizontal irradiance at the recorded time and location
 * @param windSpeed the wind speed at the recorded time and location
 * @author Jonas Nunnenmacher
 */
public record FileData(GpsCoordinates gpsCoordinates,
                       LocalDateTime localDateTime,
                       double temperature,
                       double diffuseHorizontalIrradiance,
                       double directNormalIrradiance,
                       double globalHorizontalIrradiance,
                       double windSpeed) {}
