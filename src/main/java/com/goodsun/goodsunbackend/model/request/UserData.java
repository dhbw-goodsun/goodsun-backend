package com.goodsun.goodsunbackend.model.request;

public record UserData(GpsCoordinates userGPSCoords, SolarPanel[] userPanels, Inverter[] userInverters) {}