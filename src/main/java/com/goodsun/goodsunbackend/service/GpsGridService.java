package com.goodsun.goodsunbackend.service;

import com.goodsun.goodsunbackend.model.request.GpsCoordinates;

public class GpsGridService {
    public GpsCoordinates getCorrespondingGpsCoords(GpsCoordinates gpsCoordinates){

        // calc corresponding longitude auf .5
        double correspondingLongitude = (int) gpsCoordinates.longitude() + 0.5;;
        // calc corresponding latitude auf .25 and .75
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