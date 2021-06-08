package ua.focus.javaclass.domain;

import lombok.experimental.UtilityClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import static ua.focus.controller.Controller.localZone;

@UtilityClass
public class GPSTimeCalculator {

    public static GPSTime gpsTimes(GPS gps) {

        GPSTime gpsTime = new GPSTime();

        //time
        gpsTime.setTime(gps.getTime());

        //LocalTime
        //long unixSeconds = Long.parseLong(Double.toString(gps.getTs()));
        long unixSeconds = gps.getTs();
        // convert seconds to milliseconds
        Date date = new Date(unixSeconds / 1000);
        // the format of your date
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.S");
        // give a timezone reference for formatting (see comment at the bottom)
        sdf.setTimeZone(TimeZone.getTimeZone(localZone));
        String formattedDate = sdf.format(date);
        //System.out.println(formattedDate + "\n");
        gpsTime.setLocalTime(formattedDate);

        //Latitude
        gpsTime.setLatitude(gps.getLatitude());

        //Longitude
        gpsTime.setLongitude(gps.getLongitude());

        //Altitude
        gpsTime.setAltitude(gps.getAltitude());

        //Speed
        gpsTime.setSpeed(gps.getSpeed());

        //TimeUTC
        gpsTime.setTimeUTC(gps.getTs());

        return gpsTime;
    }

    public static List<GPSTime> gpsTimesBulk(List<GPS> gps) {
        return gps.stream().map(GPSTimeCalculator::gpsTimes).collect(Collectors.toList());
    }


}
