package com.goodsun.goodsunbackend.service;

import com.goodsun.goodsunbackend.model.calculation.FileData;

/**
 * Service class responsible for calculating module-related parameters, especially the DC power output.
 *
 * @see <a href="https://energy.sandia.gov/wp-content/gallery/uploads/043535.pdf">Literature (1)</a>
 * @see <a href="https://www.nrel.gov/docs/fy14osti/62641.pdf">Literature (2)</a>
 * @author Jonas Nunnenmacher
 */
public class ModuleService {

    /**
     * Calculates the DC power output of a module based on irradiance and environmental conditions.
     *
     * @param poaIrradiance the plane of array irradiance (in W/m^2)
     * @param fileData the environmental data for the module
     * @param dcRating the DC rating of the module (in W)
     * @return the DC power output of the module
     */
    public double getDCpower(double poaIrradiance, FileData fileData, double dcRating){
        double temperatureCoefficient = -0.47 * 0.01; // PVWatts Manual V5 Table 3, assumption: standard module
        double referenceCellTemp = 25;
        double cellTemp = getCellTemp(fileData);
        return poaIrradiance / 1000 * dcRating * (1 + temperatureCoefficient * (cellTemp - referenceCellTemp));
    }

    /**
     * Calculates the cell temperature of the module based on environmental conditions.
     *
     * @param fileData the environmental data for the module
     * @return the cell temperature of the module
     */
    private double getCellTemp(FileData fileData) {
        double backSurfaceTemp = getBackSurfaceTemp(fileData);
        double temperatureDifference = (3.0 + 1.0 + 3.0 + 0.0) / 4;
        return backSurfaceTemp + fileData.globalHorizontalIrradiance() / 1000 * temperatureDifference;
    }

    /**
     * Calculates the back surface temperature of the module based on environmental conditions.
     *
     * @param fileData the environmental data for the module
     * @return the back surface temperature of the module
     */
    private double getBackSurfaceTemp(FileData fileData) {
        double a = (-3.47 + -2.98 + -3.56 + -2.81) / 4;
        double b = (-0.0594 + -0.0471 + -0.075 + -0.0455);
        return fileData.globalHorizontalIrradiance() * Math.exp(a + b * fileData.windSpeed()) + fileData.temperature();
    }

}