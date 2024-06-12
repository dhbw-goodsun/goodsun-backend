package com.goodsun.goodsunbackend.model.request;

/**
 * Record class representing obstacle data (which represents the shadowing information) for a solar panel.
 * Analogous to the PanelObstacleData from the JSON request.
 *
 * @param dataSetID the ID of the obstacle dataset
 * @param dataPoints an array of data points representing azimuth and elevation values
 * @author Jonas Nunnenmacher
 */
public record PanelObstacleData(double dataSetID, DataPoint[] dataPoints){}