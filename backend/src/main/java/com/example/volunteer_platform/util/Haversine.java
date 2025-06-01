package com.example.volunteer_platform.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Haversine {

    private static final Logger logger = LoggerFactory.getLogger(Haversine.class);
    private static final double EARTH_RADIUS_KM = 6371.0;

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) *
                        Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double distance = EARTH_RADIUS_KM * c;
        logger.debug("Calculated distance between ({},{}) and ({},{}) is: {} km", lat1, lon1, lat2, lon2, distance);
        return distance;
    }
}