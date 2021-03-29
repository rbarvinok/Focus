package ua.focus.javaclass.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GPS {

    private String time;
    private double latitude;
    private double longitude;
    private double altitude;
    private double speed;
    private double speed3D;
    private long ts;



    @AllArgsConstructor
    @Data
    public static class TimeLatitude {
        private String time;
        private double latitude;
    }

    @AllArgsConstructor
    @Data
    public static class TimeLongitude {
        private String time;
        private double longitude;
    }

    @AllArgsConstructor
    @Data
    public static class TimeAltitude {
        private String time;
        private double altitude;
    }

    @AllArgsConstructor
    @Data
    public static class TimeSpeed {
        private String time;
        private double speed;
    }

    @AllArgsConstructor
    @Data
    public static class TimeSpeed3D {
        private String time;
        private double speed3D;
    }

    @AllArgsConstructor
    @Data
    public static class TS {
        private long ts;
    }


    @Override
    public String toString() {
        return time + ",    " + latitude + ",    " + longitude + ",    " + altitude + ",    " + speed + ",    " + speed3D + ",    " + ts + "\n";

    }
}
