package com.goodsun.goodsunbackend.model.calculation;

import com.goodsun.goodsunbackend.model.request.*;

import java.util.ArrayList;

/**
 * The UserPvSystem class represents a user's photovoltaic (PV) system,
 * including the GPS coordinates, PV modules, and inverter rating.
 * @author Jonas Nunnenmacher
 */
public class UserPvSystem {
    private GpsCoordinates gpsCoordinates;
    private ArrayList<PvModule> pvModules;
    private double inverterAcRating;

    /**
     * Constructs a UserPvSystem instance.
     *
     * @param gpsCoordinates the GPS coordinates of the PV system
     * @param pvModules the list of PV modules in the system
     * @param inverterAcRating the AC rating of the inverter
     */
    private UserPvSystem(GpsCoordinates gpsCoordinates, ArrayList<PvModule> pvModules, double inverterAcRating) {
        this.gpsCoordinates = gpsCoordinates;
        this.pvModules = pvModules;
        this.inverterAcRating = inverterAcRating;
    }

    /**
     * Creates a UserPvSystem instance from user data.
     *
     * @param userData the user data containing information about the PV system
     * @return a new UserPvSystem instance
     */
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

    /**
     * Removes shadowing effects from all PV modules in the system.
     */
    public void removeShadowing() {
        for (int i = 0; i < this.getPvModules().size(); i++) {
            PvModule pvModule = this.getPvModules().get(i);
            pvModule.removeShadowing();
        }
    }
}
