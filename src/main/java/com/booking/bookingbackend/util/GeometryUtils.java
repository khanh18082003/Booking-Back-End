package com.booking.bookingbackend.util;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

public class GeometryUtils {

  private static final double R = 6378137; // Bán kính chuẩn

  public static Point toWebMercator(double longitude, double latitude) {
    double x = R * longitude * Math.PI / 180;
    double y = R * Math.log(Math.tan(Math.PI / 4 + latitude * Math.PI / 360));

    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 3857);
    return geometryFactory.createPoint(new Coordinate(x, y));
  }

  public static double[] transformLatLong(double longitude, double latitude) {
    double x = R * longitude * Math.PI / 180;
    double y = R * Math.log(Math.tan(Math.PI / 4 + latitude * Math.PI / 360));

    return new double[]{x, y};
  }


}
