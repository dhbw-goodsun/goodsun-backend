package com.goodsun.goodsunbackend.service;

import java.util.HashMap;

public class SystemLossService {

    private HashMap<String, Double> systemLosses;
    private double totalSystemLoss;

    public SystemLossService(){
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
        totalSystemLoss = calcTotalSystemLoss(this.systemLosses);
    }

    private double calcTotalSystemLoss (HashMap<String, Double> systemLosses){
        double totalDerateFactor = 1.0;
        for (Double loss : systemLosses.values()) {
            totalDerateFactor = totalDerateFactor * (1 - loss / 100);
        }
        return 100 * (1 - totalDerateFactor);
    }

    public double getDCpowerWithSystemLosses (double dcPower) {
        return dcPower * 1 - totalSystemLoss / 100;
    }
}
