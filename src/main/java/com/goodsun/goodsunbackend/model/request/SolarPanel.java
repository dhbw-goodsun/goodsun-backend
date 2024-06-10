package com.goodsun.goodsunbackend.model.request;

public record SolarPanel(String panelID, String panelDescription, double panelWatts, double panelAzimuth, double panelElevation, PanelObstacleData[] panelObstacleDatasets){}