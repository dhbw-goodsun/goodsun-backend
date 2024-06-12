package com.goodsun.goodsunbackend.model.response;

/**
 * Record class representing the results of a PV system calculation.
 *
 * @param calculatedOutput the calculated output of the PV system with shadowing effects
 * @param calculatedOutputNoShadow the calculated output of the PV system without shadowing effects
 * @author Jonas Nunnenmacher
 */
public record Results (int calculatedOutput, int calculatedOutputNoShadow){
}