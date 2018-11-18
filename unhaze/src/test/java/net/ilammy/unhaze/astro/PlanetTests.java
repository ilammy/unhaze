package net.ilammy.unhaze.astro;

import com.mhuss.AstroLib.Planets;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.TimeZone;

// All expected test data has been obtained from NASA's JPL HORIZONS system:
//
//     https://ssd.jpl.nasa.gov/?horizons
//
// Request the observer ephemeris, look at apparent right ascension and declination.
//
// I really trust these people to get the math right.

public class PlanetTests {
    // Moon's apparent angular diameter is about 30 arcminutes (0.5 degrees).
    // We'd like to have precision around 0.5 arcminute or so, that's fine enough.
    private static final double DELTA_RAD = Math.toRadians(0.5 / 60.0);

    private static final double KM_IN_AU = 149_598_000.0;

    // For Moon we'd better be more or less precise. Say, 100 km?
    private static final double DELTA_AU_MOON = 100.0 / KM_IN_AU;

    // Ten Earths, give or take. Should be enough for interplanetary distances, right?
    private static final double DELTA_AU_PLANETS = (10 * 6357.0) / KM_IN_AU;

    private static void assertCloseAngles(double expected, double actual) {
        double diff = Math.abs(expected - actual);

        if (diff > Math.PI) {
            diff = 2 * Math.PI - diff;
        }

        Assert.assertTrue(diff < DELTA_RAD);
    }

    private static void assertReallyCloseDistance(double expected, double actual) {
        double diff = Math.abs(expected - actual);
        Assert.assertTrue(diff < DELTA_AU_MOON);
    }

    private static void assertCloseDistance(double expected, double actual) {
        double diff = Math.abs(expected - actual);
        Assert.assertTrue(diff < DELTA_AU_PLANETS);
    }

    @Test
    public void sunLocationOnVernalEquinox2000() {
        Planet sun = new Planet(Planets.SUN);

        // 2000-03-20 07:30:00 UTC
        Calendar vernalEquinox = new Calendar.Builder()
                .setDate(2000, 2, 20)
                .setTimeOfDay(7, 30, 0)
                .setTimeZone(TimeZone.getTimeZone("UTC"))
                .build();

        PlanetLocation location = sun.getLocation(vernalEquinox);

        assertCloseAngles(Math.toRadians(359.99664), location.rightAscension);
        assertCloseAngles(Math.toRadians( -0.00138), location.declination);
        assertCloseDistance(0.99596024881145, location.distance);
    }

    @Test
    public void venusLocationAtSomePoint() {
        Planet venus = new Planet(Planets.VENUS);

        // 2018-11-19 00:21 Europe/Kiev
        Calendar now = new Calendar.Builder()
                .setDate(2018, 10, 19)
                .setTimeOfDay(0, 21, 0)
                .setTimeZone(TimeZone.getTimeZone("Europe/Kiev"))
                .build();

        PlanetLocation location = venus.getLocation(now);

        assertCloseAngles(Math.toRadians(203.21272), location.rightAscension);
        assertCloseAngles(Math.toRadians(-10.54996), location.declination);
        assertCloseDistance(0.33765630167922, location.distance);
    }

    @Test
    public void lunaLocationAtSomePoint() {
        Planet luna = new Planet(Planets.LUNA);

        // 2018-11-19 01:02 Europe/Kiev
        Calendar now = new Calendar.Builder()
                .setDate(2018, 10, 19)
                .setTimeOfDay(1, 2, 0)
                .setTimeZone(TimeZone.getTimeZone("Europe/Kiev"))
                .build();

        PlanetLocation location = luna.getLocation(now);

        assertCloseAngles(Math.toRadians( 5.23141), location.rightAscension);
        assertCloseAngles(Math.toRadians(-2.74301), location.declination);
        assertReallyCloseDistance(0.00262817452728, location.distance);
    }

    @Test
    public void neptuneLocationAtSomePoint() {
        Planet neptune = new Planet(Planets.NEPTUNE);

        // 2018-11-19 20:11 Europe/Kiev
        Calendar now = new Calendar.Builder()
                .setDate(2018, 10, 19)
                .setTimeOfDay(20, 11, 0)
                .setTimeZone(TimeZone.getTimeZone("Europe/Kiev"))
                .build();

        PlanetLocation location = neptune.getLocation(now);

        assertCloseAngles(Math.toRadians(345.36543), location.rightAscension);
        assertCloseAngles(Math.toRadians( -7.31931), location.declination);
        assertCloseDistance(29.6463777802174, location.distance);
    }
}
