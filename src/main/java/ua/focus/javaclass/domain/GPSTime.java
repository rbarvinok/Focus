package ua.focus.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class GPSTime {

    private String time;
    private double latitude;
    private double longitude;
    private double altitude;
    private double speed;
    private double speed3D;
    private double timeUTC;
    private String localTime;


    @AllArgsConstructor
    @Data
    public static class Time {
        private String time;
    }

    @AllArgsConstructor
    @Data
    public static class LocalTime {
        private String localTime;
    }


    @AllArgsConstructor
    @Data
    public static class Latitude {
        private double latitude;
        private double longitude;
    }

    @AllArgsConstructor
    @Data
    public static class Longitude {
        private double longitude;
    }

    @AllArgsConstructor
    @Data
    public static class Altitude {
        private String time;
        private double altitude;
    }

    @AllArgsConstructor
    @Data
    public static class Speed {
        private String time;
        private double speed;
    }

    @AllArgsConstructor
    @Data
    public static class TimeUTC {
        private double timeUTC;
    }

    @Override
    public String toString() {
        return time + ",    " + localTime + ",    " + latitude + ",    " + longitude + ",    " + altitude + ",    " + speed + "\n";
    }


    public String toStringKML() {
        return longitude + "," + latitude + "," + altitude + "\n";
    }

}
