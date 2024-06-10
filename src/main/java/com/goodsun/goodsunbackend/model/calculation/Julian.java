package com.goodsun.goodsunbackend.model.calculation;

import java.time.LocalDateTime;
public record Julian(double day, double dayFraction, double dateTime, LocalDateTime universalTime) {

}