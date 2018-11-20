package net.ilammy.unhaze.astro;

public class Star {
    public final int hipparcosNumber;
    public final double rightAscension;
    public final double declination;
    public final double distance;
    public final double magnitude;
    public final String spectralClass;
    public final String bayerName;
    public final String flamsteedName;
    public final Constellation constellation;

    Star(Builder builder) {
        this.hipparcosNumber = builder.hipparcosNumber;
        this.rightAscension = builder.rightAscension;
        this.declination = builder.declination;
        this.distance = builder.distance;
        this.magnitude = builder.magnitude;
        this.spectralClass = builder.spectralClass;
        this.bayerName = builder.bayerName;
        this.flamsteedName = builder.flamsteedName;
        this.constellation = builder.constellation;
    }

    static class Builder {
        int hipparcosNumber;
        double rightAscension;
        double declination;
        double distance;
        double magnitude;
        String spectralClass;
        String bayerName;
        String flamsteedName;
        Constellation constellation;
    }
}
