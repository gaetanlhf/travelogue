package fr.insset.ccm.m1.sag.travelogue.helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import fr.insset.ccm.m1.sag.travelogue.entity.GpsPoint;

public class GenerateGpx {
    public static void generate(File fileName, String name, List<GpsPoint> points) throws IOException {
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"Travelogue\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\"><trk>\n";
        name = "<name>" + name + "</name><trkseg>\n";
        String segments = "";
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        for (GpsPoint location : points) {
            segments += "<trkpt lat=\"" + location.getLatitude() + "\" lon=\"" + location.getLongitude() + "\"><time>" + df.format(new Date(Integer.parseInt(location.getTimestamp()) * 1000L)) + "</time></trkpt>\n";
        }
        String footer = "</trkseg></trk></gpx>";
        FileWriter writer = new FileWriter(fileName.getAbsoluteFile(), false);
        writer.append(header);
        writer.append(name);
        writer.append(segments);
        writer.append(footer);
        writer.flush();
        writer.close();

    }
}
