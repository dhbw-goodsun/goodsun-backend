package com.goodsun.goodsunbackend.model.request;

/**
 * Record class representing a solar panel for the pv system.
 * Analogous to the SolarPanel from the JSON request.
 *
 * @param panelID the unique identifier of the solar panel
 * @param panelDescription a description of the solar panel
 * @param panelWatts the power rating of the solar panel in watts
 * @param panelAzimuth the azimuth angle of the solar panel
 * @param panelElevation the elevation angle of the solar panel
 * @param panelObstacleDatasets an array of obstacle datasets for the solar panel
 * @author Jonas Nunnenmacher
 */
public record SolarPanel(String panelID, String panelDescription, double panelWatts, double panelAzimuth, double panelElevation, PanelObstacleData[] panelObstacleDatasets){}