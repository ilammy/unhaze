package net.ilammy.unhaze.astro;

import java.util.Calendar;

public class Star {
    int hipparcosNumber;
    double rightAscension;
    double rightAscensionDeltaPerYear;
    double declination;
    double declinationDeltaPerYear;
    double distance;
    double magnitude;
    String spectralClass;
    String bayerName;
    String flamsteedName;
    Constellation constellation;

    /**
     * HIPPARCOS catalog number (zero if not assigned).
     */
    public int getHipparcosNumber() {
        return hipparcosNumber;
    }

    /**
     * Right ascension for J2000 in radians at given date.
     */
    public double getRightAscension(Calendar date) {
        // TODO: compute with adjustments
        return rightAscension;
    }

    /**
     * Declination for J2000 in radians at given date.
     */
    public double getDeclination(Calendar date) {
        // TODO: compute with adjustments
        return declination;
    }

    /**
     * Distance from the Sun in parsecs.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Apparent visual magnitude.
     */
    public double getMagnitude() {
        return magnitude;
    }

    /**
     * Spectral class (OBAFGKM).
     */
    public String getSpectralClass() {
        return spectralClass;
    }

    /**
     * Bayer designation (empty if none).
     */
    public String getBayerName() {
        // TODO: use proper Unicode maybe?
        return bayerName;
    }

    /**
     * Flamsteed number (empty if none).
     */
    public String getFlamsteedName() {
        return flamsteedName;
    }

    /**
     * Constellation this star belongs to.
     */
    public Constellation getConstellation() {
        return constellation;
    }
}
