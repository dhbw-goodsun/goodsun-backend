package com.goodsun.goodsunbackend.service;

public class InverterService {

    static final double NOMINAL_EFFICIENCY = 0.96; //default value
    static final double REFERENCE_EFFICIENCY = 0.9637;

    private double calcNameplateDCRating(double inverterAcRating) {
        return inverterAcRating / NOMINAL_EFFICIENCY;
    }
    private double calcEfficiencyCurveScalingFactor() {return NOMINAL_EFFICIENCY / REFERENCE_EFFICIENCY; }

    public double getAcPower(double dcPower, double inverterAcRating){
        double nameplateDcRating = calcNameplateDCRating(inverterAcRating);
        if (dcPower<nameplateDcRating) {
            return dcPower * calcPartLoadEfficiency(dcPower, nameplateDcRating);
        } else {
            return inverterAcRating;
        }
    }
    private double calcPartLoadEfficiency(double dcPower, double nameplateDcRating) {
        double loadFraction = dcPower / nameplateDcRating;
        return calcEfficiencyCurveScalingFactor() * - 0.0162 * loadFraction - 0.0059 / loadFraction + 0.9858;
    }
}