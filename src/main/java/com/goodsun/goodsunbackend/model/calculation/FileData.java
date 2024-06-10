package com.goodsun.goodsunbackend.model.calculation;

import com.goodsun.goodsunbackend.model.request.GpsCoordinates;

import java.time.LocalDateTime;

public record FileData(GpsCoordinates gpsCoordinates,
                       LocalDateTime localDateTime,
                       double temperature,
                       double diffuseHorizontalIrradiance,
                       double directNormalIrradiance,
                       double globalHorizontalIrradiance,
                       double windSpeed) {}
