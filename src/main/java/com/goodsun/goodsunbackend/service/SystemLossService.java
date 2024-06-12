package com.goodsun.goodsunbackend.service;

import java.util.HashMap;

/**
 * Service class to calculate system losses affecting DC power output.
 *
 * @see <a href="https://doi.org/10.1109/PVSC.2005.1488451">Literature</a>
 * @author Jonas Nunnenmacher
 */
public class SystemLossService {

    private HashMap<String, Double> systemLosses;
    private double totalSystemLoss;

    /**
     * Constructor to initialize system losses and calculate the total system loss.
     * Each loss factor is expressed as a percentage.
     */
    public SystemLossService(){
        // Initialize system losses
        this.systemLosses = new HashMap<>();
        systemLosses.put("soiling", 2.0);
        systemLosses.put("snow", 0.0);
        systemLosses.put("mismatch", 2.0);
        systemLosses.put("wiring", 2.0);
        systemLosses.put("connections", 0.5);
        systemLosses.put("light-induced degradation", 1.5);
        systemLosses.put("nameplate rating", 1.0);
        systemLosses.put("age", 0.0);
        systemLosses.put("availability", 3.0);

        // Calculate the total system loss
        totalSystemLoss = calcTotalSystemLoss(this.systemLosses);
    }

    /**
     * Calculates the total system loss based on individual loss factors.
     *
     * @param systemLosses A HashMap containing individual system loss factors.
     * @return The total system loss as a percentage.
     */
    private double calcTotalSystemLoss (HashMap<String, Double> systemLosses){
        double totalDerateFactor = 1.0;
        for (Double loss : systemLosses.values()) {
            totalDerateFactor = totalDerateFactor * (1 - loss / 100);
        }
        return 100 * (1 - totalDerateFactor);
    }

    /**
     * Adjusts the DC power output of a solar panel based on the total system loss.
     *
     * @param dcPower The DC power output before considering system losses.
     * @return The DC power output after considering system losses.
     */
    public double getDCpowerWithSystemLosses (double dcPower) {
        return dcPower * 1 - totalSystemLoss / 100;
    }
}
