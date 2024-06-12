package com.goodsun.goodsunbackend.service;

import com.goodsun.goodsunbackend.model.calculation.FileData;
import com.goodsun.goodsunbackend.model.calculation.PvModule;
import com.goodsun.goodsunbackend.model.calculation.SunPosition;

/**
 * Service class responsible for calculating plane of array (POA) irradiance for photovoltaic modules.
 *
 * @see <a href="https://www.nrel.gov/docs/fy14osti/62641.pdf">Literature (1)</a>
 * @see <a href="https://www.nrel.gov/docs/fy14osti/60272.pdf">Literature (2)</a>
 * @see <a href="https://doi.org/10.1016/j.solener.2006.03.009">Literature (3)</a>lou
 * @see <a href="https://doi.org/10.3390/en10111688">Literature (4)</a>
 * @see <a href="https://www.wiley.com/en-us/Solar+Engineering+of+Thermal+Processes%2C+4th+Edition-p-9780470873663">Literature (5)</a>
 * @see <a href="https://pvpmc.sandia.gov/modeling-guide/1-weather-design-inputs/irradiance-insolation/extraterrestrial-radiation/">Literature (6)</a>
 * @author Jonas Nunnenmacher
 */


public class PlaneOfArrayIrradianceService {

    /**
     * Calculates the plane of array (POA) irradiance for a photovoltaic module based on environmental conditions.
     * Calculates POA as the sum of beam irradiance, sky diffuse irradiance and ground diffuse irradiance
     *
     * @param fileData the environmental data for the module
     * @param pvModule the photovoltaic module
     * @param sunPosition the position of the sun
     * @return the plane of array (POA) irradiance (W/m^2)
     */
    public double getPOAIrradiance(FileData fileData, PvModule pvModule, SunPosition sunPosition){
        double poaIrradience = 0;
        double anisotropyIndex = calcAnisotropyIndex(fileData.directNormalIrradiance(), fileData.localDateTime().getDayOfYear());
        poaIrradience =     getPoaBeam(fileData.directNormalIrradiance(), pvModule, sunPosition)
                            + getPoaDiffuse(anisotropyIndex, fileData.diffuseHorizontalIrradiance(), Math.toRadians(pvModule.getElevation()))
                            + getPoaGroundReflected(fileData.globalHorizontalIrradiance(), Math.toRadians(pvModule.getElevation()));
        return poaIrradience;
    }

    /**
     * Calculates the beam (direct) irradiance for a photovoltaic module based on direct normal irradiance and sun position.
     *
     * @param directNormalIrradiance the direct normal irradiance (in W/m^2)
     * @param pvModule the photovoltaic module
     * @param sunPosition the position of the sun
     * @return the beam (direct) irradiance (W/m^2)
     */
    private double getPoaBeam(double directNormalIrradiance, PvModule pvModule, SunPosition sunPosition) {
        if (pvModule.getShadowingElevationForAzimuth((int) sunPosition.azimuth()) >= sunPosition.elevation()){
            return 0.0;
        } else {
            double angleOfIncidence = calcAngleOfIncidence(sunPosition.azimuth(), sunPosition.elevation(), pvModule.getAzimuth(), pvModule.getElevation());
            return getReflectionCorrectedPOAbeam(directNormalIrradiance, angleOfIncidence);
        }
    }

    /**
     * Calculates the reflection-corrected beam (direct) irradiance based on direct normal irradiance and angle of incidence.
     *
     * @param directNormalIrradiance the direct normal irradiance (in W/m^2)
     * @param angleOfIncidence the angle of incidence (in degrees)
     * @return the reflection-corrected beam (direct) irradiance (W/m^2)
     */
    private double getReflectionCorrectedPOAbeam(double directNormalIrradiance, double angleOfIncidence){
        double rawPoaBeam = getRawPOAbeam(directNormalIrradiance, angleOfIncidence);
        if (angleOfIncidence > 50) {
            double correctionFactor = 1 +
                    -2.438e-3 * angleOfIncidence +
                    3.103e-4 * Math.pow(angleOfIncidence, 2) +
                    -1.246e-5 * Math.pow(angleOfIncidence, 3) +
                    2.112e-7 * Math.pow(angleOfIncidence, 4) +
                    -1.359e-9 * Math.pow(angleOfIncidence, 5);
            return rawPoaBeam * correctionFactor;
        } else {
            return rawPoaBeam;
        }
    }

    /**
     * Calculates the raw beam (direct) irradiance based on direct normal irradiance and angle of incidence.
     *
     * @param directNormalIrradiance the direct normal irradiance (in W/m^2)
     * @param angleOfIncidence the angle of incidence (in degrees)
     * @return the raw beam (direct) irradiance
     */
    private double getRawPOAbeam(double directNormalIrradiance, double angleOfIncidence){
        return directNormalIrradiance * Math.cos(Math.toRadians(angleOfIncidence));
    }

    /**
     * Calculates the sky diffuse irradiance for a photovoltaic module based on diffuse horizontal irradiance and module elevation.
     *
     * @param anisotropyIndex the anisotropy index
     * @param diffuseHorizontalIrradiance the diffuse horizontal irradiance (in W/m^2)
     * @param elevationInRadians the module elevation (in radians)
     * @return the sky diffuse irradiance
     */
    private double getPoaDiffuse(double anisotropyIndex, double diffuseHorizontalIrradiance, double elevationInRadians) {
        return diffuseHorizontalIrradiance * (1 - anisotropyIndex) * ((1 + Math.cos(elevationInRadians)) / 2); }

    /**
     * Calculates the ground-reflected irradiance for a photovoltaic module based on global horizontal irradiance and module elevation.
     *
     * @param globalHorizontalIrradiance the global horizontal irradiance (in W/m^2)
     * @param elevationInRadians the module elevation (in radians)
     * @return the ground-reflected irradiance
     */
    private double getPoaGroundReflected(double globalHorizontalIrradiance, double elevationInRadians) {
        double groundAlbedo = 0.2;
        return globalHorizontalIrradiance * groundAlbedo * ((1 + Math.cos(elevationInRadians)) / 2);}

    /**
     * Calculates the angle of incidence between the sun and a photovoltaic module.
     *
     * @param azimuthSun the azimuth angle of the sun (in degrees)
     * @param elevationSun the elevation angle of the sun (in degrees)
     * @param azimuthPanel the azimuth angle of the module (in degrees)
     * @param tiltPanel the tilt angle of the module (in degrees)
     * @return the angle of incidence (in degrees)
     */
    public double calcAngleOfIncidence(double azimuthSun,
                                       double elevationSun,
                                       double azimuthPanel,
                                       double tiltPanel) {
        double angleOfIncidence;
        double azimuthSunRad = Math.toRadians(azimuthSun);
        double solarZenithRad = Math.toRadians(90.0 - elevationSun);
        double azimuthPanelRad = Math.toRadians(azimuthPanel);
        double tiltPanelRad = Math.toRadians(tiltPanel);
        double innerValue = Math.sin(solarZenithRad) * Math.cos(azimuthPanelRad - azimuthSunRad) * Math.sin(tiltPanelRad) +
                Math.cos(solarZenithRad) * Math.cos(tiltPanelRad);
        angleOfIncidence = Math.toDegrees(Math.acos(innerValue));
        if(angleOfIncidence>90) angleOfIncidence = 90;
        return angleOfIncidence;
    }

    /**
     * Calculates the extraterrestrial solar radiation for a given day of the year.
     *
     * @param dayOfYear the day of the year
     * @return the extraterrestrial solar radiation (in W/m^2)
     */
    private double calcExtraterrestrialRadiation(int dayOfYear) {
        double solarConstant = 1367;
        double beta = (2*Math.PI*dayOfYear)*365;
        return solarConstant * (1.00011 + 0.034221 *Math.cos(beta) + 0.00128 *Math.sin(beta) + 0.000719 * Math.cos(2*beta) + 0.000077 * Math.sin(2*beta));
    }

    /**
     * Calculates the anisotropy index based on direct normal irradiance and day of the year.
     *
     * @param directNormalIrradiance the direct normal irradiance (in W/m^2)
     * @param dayOfYear the day of the year
     * @return the anisotropy index
     */
    private double calcAnisotropyIndex(double directNormalIrradiance, int dayOfYear){
        return directNormalIrradiance / calcExtraterrestrialRadiation(dayOfYear);
    }
}