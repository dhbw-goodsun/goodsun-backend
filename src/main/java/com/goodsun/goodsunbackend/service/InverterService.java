package com.goodsun.goodsunbackend.service;

/**
 * Service class responsible for calculating inverter-related parameters.
 *
 * @see <a href="https://www.nrel.gov/docs/fy14osti/62641.pdf">Literature</a>
 * @author Jonas Nunnenmacher
 */
public class InverterService {

    static final double NOMINAL_EFFICIENCY = 0.96; //default value
    static final double REFERENCE_EFFICIENCY = 0.9637;

    /**
     * Calculates the DC rating of the inverter.
     *
     * @param inverterAcRating the AC rating of the inverter
     * @return the DC rating of the inverter
     */
    private double calcNameplateDCRating(double inverterAcRating) {
        return inverterAcRating / NOMINAL_EFFICIENCY;
    }

    /**
     * Calculates the efficiency curve scaling factor.
     *
     * @return the efficiency curve scaling factor
     */
    private double calcEfficiencyCurveScalingFactor() {return NOMINAL_EFFICIENCY / REFERENCE_EFFICIENCY; }

    /**
     * Calculates the AC power output of the inverter based on the DC power input.
     *
     * @param dcPower the DC power input
     * @param inverterAcRating the AC rating of the inverter
     * @return the AC power output of the inverter
     */
    public double getAcPower(double dcPower, double inverterAcRating){
        double nameplateDcRating = calcNameplateDCRating(inverterAcRating);
        if (dcPower<nameplateDcRating) {
            return dcPower * calcPartLoadEfficiency(dcPower, nameplateDcRating);
        } else {
            return inverterAcRating;
        }
    }

    /**
     * Calculates the part-load efficiency of the inverter.
     *
     * @param dcPower the DC power input
     * @param nameplateDcRating the nameplate DC rating of the inverter
     * @return the part-load efficiency of the inverter
     */
    private double calcPartLoadEfficiency(double dcPower, double nameplateDcRating) {
        double loadFraction = dcPower / nameplateDcRating;
        return calcEfficiencyCurveScalingFactor() * - 0.0162 * loadFraction - 0.0059 / loadFraction + 0.9858;
    }
}