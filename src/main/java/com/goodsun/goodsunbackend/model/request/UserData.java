package com.goodsun.goodsunbackend.model.request;

/**
 * Record class representing user input data for a PV system.
 * Analogous to the UserData from the JSON request.
 *
 * @param userGPSCoords the GPS coordinates of the user's location
 * @param userPanels an array of solar panels owned by the user
 * @param userInverters an array of inverters owned by the user
 * @author Jonas Nunnenmacher
 */
public record UserData(GpsCoordinates userGPSCoords, SolarPanel[] userPanels, Inverter[] userInverters) {}