package com.goodsun.goodsunbackend.service;

import com.goodsun.goodsunbackend.model.request.GpsCoordinates;

/**
 * Service class responsible for calculating corresponding GPS coordinates from the grid.
 *
 * @author Jonas Nunnenmacher
 */
public class GpsGridService {

    /**
     * Calculates the corresponding GPS coordinates based on the provided coordinates.
     *
     * @param gpsCoordinates the original GPS coordinates
     * @return the corresponding GPS coordinates from the grid
     */
    public GpsCoordinates getCorrespondingGpsCoords(GpsCoordinates gpsCoordinates){

        // calc corresponding longitude to .5
        double correspondingLongitude = (int) gpsCoordinates.longitude() + 0.5;;
        // calc corresponding latitude to .25 and .75
        int intCorrespondingLatitude = (int) gpsCoordinates.latitude();
        double decCorrespondingLatitude;
        if (gpsCoordinates.latitude() - intCorrespondingLatitude < 0.5){
            decCorrespondingLatitude = 0.25;
        } else {
            decCorrespondingLatitude = 0.75;
        }
        double correspondingLatidude = intCorrespondingLatitude + decCorrespondingLatitude;

        return new GpsCoordinates(correspondingLongitude, correspondingLatidude);
    }
}