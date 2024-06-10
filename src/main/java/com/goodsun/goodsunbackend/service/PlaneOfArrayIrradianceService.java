package com.goodsun.goodsunbackend.service;

import com.goodsun.goodsunbackend.model.calculation.FileData;
import com.goodsun.goodsunbackend.model.calculation.PvModule;
import com.goodsun.goodsunbackend.model.calculation.SunPosition;

public class PlaneOfArrayIrradianceService {
    // poa irradiance - aoi - sun position

    // hay & davies am besten in realistischen szenarios
    // mit poaBeam ist aoi * dni
    // albedo fix 0,2
    // direct nur wenn nicht verschattet, sonst nur diffuse und ground reflected
    public double getPOAIrradiance(FileData fileData, PvModule pvModule, SunPosition sunPosition){
        double poaIrradience = 0;
        double anisotropyIndex = calcAnisotropyIndex(fileData.directNormalIrradiance(), fileData.localDateTime().getDayOfYear());
        poaIrradience =     getPoaBeam(fileData.directNormalIrradiance(), pvModule, sunPosition)
                            + getPoaDiffuse(anisotropyIndex, fileData.diffuseHorizontalIrradiance(), Math.toRadians(pvModule.getElevation()))
                            + getPoaGroundReflected(fileData.globalHorizontalIrradiance(), Math.toRadians(pvModule.getElevation()));
        return poaIrradience;
    }



    private double getPoaBeam(double directNormalIrradiance, PvModule pvModule, SunPosition sunPosition) {
        if (pvModule.getShadowingElevationForAzimuth((int) sunPosition.azimuth()) >= sunPosition.elevation()){
            return 0.0;
        } else {
            double angleOfIncidence = calcAngleOfIncidence(sunPosition.azimuth(), sunPosition.elevation(), pvModule.getAzimuth(), pvModule.getElevation());
            return getReflectionCorrectedPOAbeam(directNormalIrradiance, angleOfIncidence);
        }
    }

    private double getReflectionCorrectedPOAbeam(double directNormalIrradience, double angleOfIncidence){
        //nach PVWatts V1 weil Standard panels angenommen werden -> konservativ 0,5% mehr yield p.a. bei Anti Reflection Glass
        double rawPoaBeam = getRawPOAbeam(directNormalIrradience, angleOfIncidence);
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


    private double getRawPOAbeam(double directNormalIrradiance, double angleOfIncidence){
        return directNormalIrradiance * Math.cos(Math.toRadians(angleOfIncidence));
    }

    private double getPoaDiffuse(double anisotropyIndex, double diffuseHorizontalIrradiance, double elevationInRadians) {
        return diffuseHorizontalIrradiance * (1 - anisotropyIndex) * ((1 + Math.cos(elevationInRadians)) / 2); }

    private double getPoaGroundReflected(double globalHorizontalIrradiance, double elevationInRadians) {
        double groundAlbedo = 0.2;
        return globalHorizontalIrradiance * groundAlbedo * ((1 + Math.cos(elevationInRadians)) / 2);}

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

    private double calcExtraterrestrialRadiation(int dayOfYear) {
        double solarConstant = 1367;
        double beta = (2*Math.PI*dayOfYear)*365;
        return solarConstant * (1.00011 + 0.034221 *Math.cos(beta) + 0.00128 *Math.sin(beta) + 0.000719 * Math.cos(2*beta) + 0.000077 * Math.sin(2*beta));
    }

    private double calcAnisotropyIndex(double directNormalIrradiance, int dayOfYear){
        return directNormalIrradiance / calcExtraterrestrialRadiation(dayOfYear);
    }
}