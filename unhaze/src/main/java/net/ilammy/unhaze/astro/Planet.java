package net.ilammy.unhaze.astro;

import com.mhuss.AstroLib.AstroDate;
import com.mhuss.AstroLib.NoInitException;
import com.mhuss.AstroLib.ObsInfo;
import com.mhuss.AstroLib.PlanetData;

import java.util.Calendar;
import java.util.TimeZone;

public class Planet {
    private final int id;

    /**
     * Get a planet.
     *
     * @param id a constant from com.mhuss.astrolib.Planets
     */
    public Planet(int id) {
        this.id = id;
    }

    /**
     * Compute planet's location on a given date.
     */
    public PlanetLocation getLocation(Calendar date) {
        PlanetData data = new PlanetData(this.id, toJulian(date), new ObsInfo());

        try {
            double rightAscension = rightAscension(data);
            double declination = declination(data);
            double distance = distance(data);
            return new PlanetLocation(rightAscension, declination, distance);
        }
        catch (NoInitException e) {
            // This exception should never happen because we always initialize PlanetData,
            // but the library insists on using a checked exception.
            return null;
        }
    }

    private static double toJulian(Calendar date) {
        Calendar utc = (Calendar) date.clone();
        utc.setTimeZone(TimeZone.getTimeZone("UTC"));

        int day = utc.get(Calendar.DAY_OF_MONTH);
        int month = utc.get(Calendar.MONTH) + 1;
        int year = utc.get(Calendar.YEAR);
        int hour = utc.get(Calendar.HOUR_OF_DAY);
        int min = utc.get(Calendar.MINUTE);
        int sec = utc.get(Calendar.SECOND);

        return new AstroDate(day, month, year, hour, min, sec).jd();
    }

    private static double rightAscension(PlanetData data) throws NoInitException {
        double value = data.getRightAscension();

        while (value < 0) {
            value += 2 * Math.PI;
        }
        while (value > 2 * Math.PI) {
            value -= 2 * Math.PI;
        }

        return value;
    }

    private static double declination(PlanetData data) throws NoInitException {
        double value = data.getDeclination();

        while (value < -Math.PI) {
            value += 2 * Math.PI;
        }
        while (value > Math.PI) {
            value -= 2 * Math.PI;
        }

        return value;
    }

    private static double distance(PlanetData data) throws NoInitException {
        // Yes, the library has some issues with naming, but its computations are correct.
        double x = data.getEquatorialLat();
        double y = data.getEquatorialLon();
        double z = data.getEquatorialRadius();
        return Math.sqrt(x * x + y * y + z * z);
    }
}
