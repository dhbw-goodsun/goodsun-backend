package com.goodsun.goodsunbackend.service;

import com.goodsun.goodsunbackend.model.calculation.FileData;

public class ModuleService {
    public double getDCpower(double poaIrradiance, FileData fileData, double dcRating){
        double temperatureCoefficient = -0.47 * 0.01; // PVWatts Manual V5 Table 3, Annahme: Standard Module
        double referenceCellTemp = 25;
        double cellTemp = getCellTemp(fileData);
        return poaIrradiance / 1000 * dcRating * (1 + temperatureCoefficient * (cellTemp - referenceCellTemp));
    }
    private double getCellTemp(FileData fileData) {
        double backSurfaceTemp = getBackSurfaceTemp(fileData);
        double temperatureDifference = (3.0 + 1.0 + 3.0 + 0.0) / 4;
        return backSurfaceTemp + fileData.globalHorizontalIrradiance() / 1000 * temperatureDifference;
    }
    private double getBackSurfaceTemp(FileData fileData) {
        // TODO: correct wind speed (down from 10 m)
        double a = (-3.47 + -2.98 + -3.56 + -2.81) / 4;
        double b = (-0.0594 + -0.0471 + -0.075 + -0.0455);
        return fileData.globalHorizontalIrradiance() * Math.exp(a + b * fileData.windSpeed()) + fileData.temperature();
    }

}