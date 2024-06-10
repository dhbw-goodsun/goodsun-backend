package com.goodsun.goodsunbackend.model.calculation;

import com.goodsun.goodsunbackend.model.request.*;

import java.util.ArrayList;

public class UserPvSystem {
    private GpsCoordinates gpsCoordinates;
    private ArrayList<PvModule> pvModules;
    private double inverterAcRating;

    private UserPvSystem(GpsCoordinates gpsCoordinates, ArrayList<PvModule> pvModules, double inverterAcRating) {
        this.gpsCoordinates = gpsCoordinates;
        this.pvModules = pvModules;
        this.inverterAcRating = inverterAcRating;
    }

    public static UserPvSystem getUserPvSystem(UserData userData) {

        ArrayList<PvModule> pvModules = new ArrayList<>();
        for (int i = 0; i < userData.userPanels().length; i++) {
              pvModules.add(PvModule.getPvModule(userData.userPanels()[i]));
        }
        return new UserPvSystem(userData.userGPSCoords(), pvModules, userData.userInverters()[0].inverterWatts());
    }

    public GpsCoordinates getGpsCoordinates() {
        return gpsCoordinates;
    }

    public ArrayList<PvModule> getPvModules() {
        return pvModules;
    }

    public double getInverterAcRating() {
        return inverterAcRating;
    }

    @Override
    public String toString() {
        return "UserPvSystem{" +
                "gpsCoordinates=" + gpsCoordinates +
                ", pvModules=" + pvModules +
                ", inverterAcRating=" + inverterAcRating +
                '}';
    }

    public void removeShadowing() {
        for (int i = 0; i < this.getPvModules().size(); i++) {
            PvModule pvModule = this.getPvModules().get(i);
            pvModule.removeShadowing();
        }
    }
}
