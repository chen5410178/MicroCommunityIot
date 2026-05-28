package com.java110.hal.http.yiteSmart.util;

public class Gcj02ToBd09Util {

    private static final double DEFAULT_LNG = 114.062454;
    private static final double DEFAULT_LAT = 35.510621;

    public static double[] convert(String lngStr, String latStr) {
        double x = parseOrDefault(lngStr, DEFAULT_LNG);
        double y = parseOrDefault(latStr, DEFAULT_LAT);
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * Math.PI * 3000.0 / 180.0);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * Math.PI * 3000.0 / 180.0);
        double bdLng = z * Math.cos(theta) + 0.0065;
        double bdLat = z * Math.sin(theta) + 0.006;
        return new double[]{bdLng, bdLat};
    }

    private static double parseOrDefault(String s, double defaultValue) {
        if (s == null || s.isEmpty()) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
