package com.goodsun.goodsunbackend.model.request;

/**
 * Record class representing an inverter used in a PV system.
 * Analogous to the Inverter from the JSON request.
 *
 * @param inverterID the unique identifier of the inverter
 * @param inverterWatts the power rating of the inverter in watts
 * @param inverterName the name of the inverter
 * @param inverterDescription a description of the inverter
 * @author Jonas Nunnenmacher
 */
public record Inverter(String inverterID, double inverterWatts, String inverterName, String inverterDescription){}