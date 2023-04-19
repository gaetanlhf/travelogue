package fr.insset.ccm.m1.sag.travelogue.helper;

import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fr.insset.ccm.m1.sag.travelogue.entity.GpsPoint;

public class GenerateKml {
    public static void generate(File fileName, String name, List<GpsPoint> points) throws IOException {
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n<Document>\n<Placemark>\n";
        name = "<name>" + name + "</name>\n<description>" + name +"</description>\n";
        String segments = "<Point>\n<coordinates>\n";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        for (GpsPoint location : points) {
            segments += ""+ location.getLatitude() + "," + location.getLongitude() +",0\n";
        }
        segments += "</coordinates>\n</Point>\n";
        String footer = "</Placemark>\n</Document>\n</kml>";
        FileWriter writer = new FileWriter(fileName.getAbsoluteFile(), false);
        writer.append(header);
        writer.append(name);
        writer.append(segments);
        writer.append(footer);
        writer.flush();
        writer.close();

    }
}
