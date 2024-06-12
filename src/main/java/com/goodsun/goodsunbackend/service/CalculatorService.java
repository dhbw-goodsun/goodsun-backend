package com.goodsun.goodsunbackend.service;

import com.goodsun.goodsunbackend.data.FileDataCollector;
import com.goodsun.goodsunbackend.model.calculation.FileData;
import com.goodsun.goodsunbackend.model.calculation.PvModule;
import com.goodsun.goodsunbackend.model.calculation.SunPosition;
import com.goodsun.goodsunbackend.model.calculation.UserPvSystem;
import com.goodsun.goodsunbackend.model.request.GpsCoordinates;
import com.goodsun.goodsunbackend.model.request.UserData;
import com.goodsun.goodsunbackend.model.response.Results;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Service class responsible for calculating PV system results.
 *
 * @author Jonas Nunnenmacher
 */
@Component
public class CalculatorService {

    /**
     * Calculates the results for the provided user data.
     *
     * @param userData the user data containing information about the PV system
     * @return the results of the calculation, including output with and without shadowing effects
     */
    public Results calculateResult(UserData userData) {
        //get and correct relevant Data from user request
        UserPvSystem userPvSystem = UserPvSystem.getUserPvSystem(userData);

        // calc output with shadowing
        int calculatedOutput = calculateOutput(userPvSystem);

        //calc output without shadowing
        userPvSystem.removeShadowing();

        int calculatedOutputNoShadows = calculateOutput(userPvSystem);
        return new Results(calculatedOutput, calculatedOutputNoShadows);
    }

    /**
     * Calculates the total output of the PV system per year over the years 2017-2019.
     *
     * @param userPvSystem the user's PV system configuration
     * @return the averaged total output of the PV system per year in kWh
     */
    private int calculateOutput(UserPvSystem userPvSystem) {
        double calculatedOutput = 0;

        //get corresponding FileData
        GpsGridService gpsGridService = new GpsGridService();
        FileDataCollector fileDataCollector = new FileDataCollector();
        GpsCoordinates correspondingGpsCoords = gpsGridService.getCorrespondingGpsCoords(userPvSystem.getGpsCoordinates());

        for (int year = 2017; year < 2020; year++) {
            List<FileData> fileData = fileDataCollector.loadFileData(correspondingGpsCoords, year);
            // calculate
            for (FileData data : fileData) {
                if (data.globalHorizontalIrradiance() > 0) {
                    double calculatedTimestepOutput = calculateTimestepOutput(userPvSystem, data);
                    calculatedOutput = calculatedOutput + calculatedTimestepOutput;
                }
            }
        }


        // adjust to kWh per year
        calculatedOutput = calculatedOutput * 0.001 / 3;
        return (int) calculatedOutput;
    }

    /**
     * Calculates the output of the PV system for a given 15 min time step.
     *
     * @param userPvSystem the user's PV system configuration
     * @param fileData the weather data for the current time step
     * @return the output of the PV system for the current time step in Wh
     */
    private double calculateTimestepOutput(UserPvSystem userPvSystem, FileData fileData){
        //calculate sunPosition
        SunPositionService sunPositionService = new SunPositionService();
        SunPosition sunPosition = sunPositionService.getSunPosition(userPvSystem.getGpsCoordinates(), fileData.localDateTime());

        //calculate planeOfArrayIrradiance and modules output DcPower
        PlaneOfArrayIrradianceService poaIrradiancaService = new PlaneOfArrayIrradianceService();
        ModuleService moduleService = new ModuleService();
        double dcPower = 0;
        for (int i = 0; i < userPvSystem.getPvModules().size(); i++) {
            PvModule pvModule =  userPvSystem.getPvModules().get(i);
            double poaIrradiance = poaIrradiancaService.getPOAIrradiance(fileData, pvModule, sunPosition);
            dcPower = dcPower + moduleService.getDCpower(poaIrradiance, fileData, pvModule.getDcRating());
        }

        // reduce modules dcPower by system losses
        SystemLossService systemLossService = new SystemLossService();
        dcPower = systemLossService.getDCpowerWithSystemLosses(dcPower);

        // calculate inverter output acPower
        InverterService inverterService = new InverterService();
        double acPower = inverterService.getAcPower(dcPower, userPvSystem.getInverterAcRating());

        // correct for 15 minute intervals
        acPower = acPower / 4;

        return acPower;
    }
}
