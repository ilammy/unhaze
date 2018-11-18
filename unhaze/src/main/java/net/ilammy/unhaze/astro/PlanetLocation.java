package net.ilammy.unhaze.astro;

/*
 * This is location of a planet in geocentric equatorial polar coordinate system.
 *
 * - The origin is the center of the Earth.
 *
 * - The reference plane is the equator plane. The North Pole is above the equator plane.
 *
 * - The reference vector is direction towards the Sun on vernal equinox of the J2000 epoch,
 *   that is around 2000-03-20 07:30:00 UTC.
 *
 * - Right ascension is angle in radians from the reference vector to the projection of
 *   planet's location onto the reference plane, measured in counterclockwise direction.
 *   It has [0, 2π) range.
 *
 * - Declination is angle in radians between the reference plane and the radius-vector of
 *   the planet. It has (-π, +π) range with positive values being above the plane.
 *
 * - Distance is to the planet's center. It is measured in AU.
 */
public class PlanetLocation {
    public final double rightAscension;
    public final double declination;
    public final double distance;

    public PlanetLocation(double rightAscension, double declination, double distance) {
        this.rightAscension = rightAscension;
        this.declination = declination;
        this.distance = distance;
    }
}
