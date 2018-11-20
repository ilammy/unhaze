package net.ilammy.unhaze.astro;

import android.content.Context;
import android.content.res.XmlResourceParser;

import net.ilammy.unhaze.R;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

public class Stars {
    private static final ArrayList<Star> stars = new ArrayList<>();

    public static List<Star> knownStars() {
        return stars;
    }

    public static void loadStars(Context context) {
        XmlResourceParser xmlStars = context.getResources().getXml(R.xml.stars);

        try {
            Star nextStar = null;
            int eventType = xmlStars.getEventType();
            String currentTag = null;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    String tag = xmlStars.getName();
                    if (tag.equals("stars")) {
                        stars.clear();
                    }
                    if (tag.equals("star")) {
                        nextStar = new Star();
                    }
                    currentTag = tag;
                }
                if (eventType == XmlPullParser.TEXT) {
                    String value = xmlStars.getText();
                    if (currentTag.equals("hipparcosNumber")) {
                        nextStar.hipparcosNumber = Integer.parseInt(value);
                    }
                    if (currentTag.equals("rightAscension")) {
                        nextStar.rightAscension = Double.parseDouble(value);
                    }
                    if (currentTag.equals("rightAscensionDeltaPerYear")) {
                        nextStar.rightAscensionDeltaPerYear = Double.parseDouble(value);
                    }
                    if (currentTag.equals("declination")) {
                        nextStar.declination = Double.parseDouble(value);
                    }
                    if (currentTag.equals("declinationDeltaPerYear")) {
                        nextStar.declinationDeltaPerYear = Double.parseDouble(value);
                    }
                    if (currentTag.equals("distance")) {
                        nextStar.distance = Double.parseDouble(value);
                    }
                    if (currentTag.equals("magnitude")) {
                        nextStar.magnitude = Double.parseDouble(value);
                    }
                    if (currentTag.equals("spectralClass")) {
                        nextStar.spectralClass = value;
                    }
                    if (currentTag.equals("bayerName")) {
                        nextStar.bayerName = value;
                    }
                    if (currentTag.equals("flamsteedName")) {
                        nextStar.flamsteedName = value;
                    }
                    if (currentTag.equals("constellation")) {
                        nextStar.constellation = Constellation.fromIAU(value);
                    }
                }
                if (eventType == XmlPullParser.END_TAG) {
                    String tag = xmlStars.getName();
                    if (tag.equals("stars")) {
                        break;
                    }
                    if (tag.equals("star")) {
                        stars.add(nextStar);
                    }
                    currentTag = null;
                }
                eventType = xmlStars.next();
            }
        } catch (Exception e) {
            // This should not really happen, but crash just in case.
            throw new RuntimeException("failed to parse star database", e);
        }
    }
}
