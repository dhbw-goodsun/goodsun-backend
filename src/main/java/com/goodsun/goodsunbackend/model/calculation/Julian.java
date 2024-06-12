package com.goodsun.goodsunbackend.model.calculation;

import java.time.LocalDateTime;

/**
 * Record class representing Julian date.
 *
 * @param day the Julian day number
 * @param dayFraction the fraction of the day in Julian date
 * @param dateTime the full Julian date
 * @param universalTime the corresponding Universal Time (UTC)
 * @author Jonas Nunnenmacher
 */
public record Julian(double day, double dayFraction, double dateTime, LocalDateTime universalTime) {

}