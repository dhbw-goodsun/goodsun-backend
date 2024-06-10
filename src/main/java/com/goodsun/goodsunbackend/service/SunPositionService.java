package com.goodsun.goodsunbackend.service;

import com.goodsun.goodsunbackend.model.calculation.Julian;
import com.goodsun.goodsunbackend.model.calculation.SunPosition;
import com.goodsun.goodsunbackend.model.request.GpsCoordinates;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class SunPositionService {

    public SunPosition getSunPosition(GpsCoordinates gpsCoordinates, LocalDateTime localDateTime) {
        Julian dateTimeJulian = convertToJulian(localDateTime.minusHours(1));
        double timeVariableN = calcTimeVariableN(dateTimeJulian);
        double meanEclipticLongitudeSunL = calcMeanEclipticLongitudeSunL(timeVariableN);
        double meanAnomalyEclipticLongitudeSunG = calcMeanAnomalyEclipticLongitudeSunG(timeVariableN);
        double eclipticLongitudeSunL = calcEclipticLongitudeSunL(meanEclipticLongitudeSunL, meanAnomalyEclipticLongitudeSunG);
        double obliquityEclipticE = calcObliquityEclipticE(timeVariableN);
        double rightAscensionA = calcRightAscensionA(obliquityEclipticE, eclipticLongitudeSunL);
        double declinationD = calcDeclinationD(obliquityEclipticE, eclipticLongitudeSunL);
        double meanGreenwichSiderealTime = calcMeanGreenwichSiderealTime(dateTimeJulian);
        double greenwichHourAngleSpringTH = calcGreenwichHourAngleSpringTH(meanGreenwichSiderealTime);
        double geoCoordinateHourAngleSpringTH = calcGeoCoordinateHourAngleSpringTH(greenwichHourAngleSpringTH, gpsCoordinates.longitude());
        double geoCoordinateHourAngleSunT = calcGeoCoordinateHourAngleSunT(geoCoordinateHourAngleSpringTH, rightAscensionA);
        double azimuth = calcAzimuth(geoCoordinateHourAngleSunT, gpsCoordinates.latitude(), declinationD);
        double elevation = calcElevation(geoCoordinateHourAngleSunT, gpsCoordinates.latitude(), declinationD);
        return new SunPosition(azimuth,elevation);
    }

    private Julian convertToJulian(LocalDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        if (dateTime.getMonthValue() < 3) {
            year -= 1;
            month += 12;
        }
        double a = Math.floor(year/100);
        double b = 2 - a + Math.floor(a /4);
        double dayFraction = ((double) dateTime.getHour() /24) + ((double) dateTime.getMinute() /1440);
        double julianDay = Math.floor(365.25 * (year + 4716)) + Math.floor(30.6001 * (month + 1)) + dateTime.getDayOfMonth() + b - 1524.5;
        double julianDateTime = julianDay + dayFraction;
        return new Julian(julianDay, dayFraction, julianDateTime, dateTime);
    }

    private double calcElevation(double geoCoordinateHourAngleSunT, double latitude, double declinationD) {
        return Math.toDegrees(Math.asin(this.cos(declinationD) * this.cos(geoCoordinateHourAngleSunT) * this.cos(latitude) + this.sin(declinationD) * sin(latitude)));
    }

    private double calcAzimuth(double geoCoordinateHourAngleSunT, double latitude, double declinationD) {
        return Math.toDegrees(Math.atan(this.sin(geoCoordinateHourAngleSunT) / (this.cos(geoCoordinateHourAngleSunT) * this.sin(latitude) - this.tan(declinationD) * this.cos(latitude)))) + 180.0;
    }

    private double calcGeoCoordinateHourAngleSunT(double geoCoordinateHourAngleSpring, double rightAscensionA) {
        return geoCoordinateHourAngleSpring - rightAscensionA;
    }

    private double calcGeoCoordinateHourAngleSpringTH(double greenwichHourAngleSpring, double longitude) {
        return greenwichHourAngleSpring + longitude;
    }

    private double calcGreenwichHourAngleSpringTH(double meanGreenwichSiderealTime) {
        return meanGreenwichSiderealTime * 15.0;
    }
    private double calcMeanGreenwichSiderealTime(Julian dateTimeJulian) {
        double t0 = (dateTimeJulian.day() - 2451545.0) / 36525.0;
        double t = dateTimeJulian.universalTime().getHour()
                + (double) dateTimeJulian.universalTime().getMinute()/60;
        return (6.697376 + 2400.05134 * t0 + 1.002738 * t) % 24;
    }

    private double calcDeclinationD(double obliquityEclipticE, double eclipticLongitudeSunL) {
        return Math.toDegrees(Math.asin(this.sin(obliquityEclipticE) * this.sin(eclipticLongitudeSunL)));
    }
    private double calcRightAscensionA(double obliquityEclipticE, double eclipticLongitudeSunL) {
        return Math.toDegrees(Math.atan2(this.cos(obliquityEclipticE) * this.sin(eclipticLongitudeSunL), this.cos(eclipticLongitudeSunL))); //korrekt
    }
    private double calcObliquityEclipticE(double timeVariableN) {
        return 23.439 - 0.0000004 * timeVariableN;
    }
    private double calcEclipticLongitudeSunL(double meanEclipticLongitudeSunL, double meanAnomalyEclipticLongitudeSunG) {
        return meanEclipticLongitudeSunL + 1.915 * this.sin(meanAnomalyEclipticLongitudeSunG) + 0.01997 * this.sin(2* meanAnomalyEclipticLongitudeSunG);
    }

    private double calcMeanAnomalyEclipticLongitudeSunG(double timeVariableN) {
        return correctDegree(357.528 + 0.9856003 * timeVariableN);
    }
    private double calcMeanEclipticLongitudeSunL(double timeVariableN) {
        return correctDegree(280.460 + 0.9856474 * timeVariableN);
    }

    private double calcTimeVariableN(Julian dateTimeJulian){
        return dateTimeJulian.dateTime() - 2451545.0;
    }
    public static double correctDegree(double degree) {
        degree = degree % 360;
        return (degree < 0) ? degree + 360 : degree;
    }

    private double cos(double degree){
        return Math.cos(Math.toRadians(degree));
    }
    private double sin(double degree){
        return Math.sin(Math.toRadians(degree));
    }
    private double tan(double degree){
        return Math.tan(Math.toRadians(degree));
    }

}
