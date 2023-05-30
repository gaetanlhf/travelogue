package fr.insset.ccm.m1.sag.travelogue.helper;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimestampDate {
    public static String getDate(String timestampStr) {
        long timestamp;
        timestamp = Long.parseLong(timestampStr) * 1000;
        Locale currentLocale = Locale.getDefault();
        TimeZone currentTimezone = TimeZone.getDefault();
        Date date = new Date(timestamp);
        DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, currentLocale);
        dateFormat.setTimeZone(currentTimezone);
        String dateString = dateFormat.format(date);
        return dateString;
    }
}
