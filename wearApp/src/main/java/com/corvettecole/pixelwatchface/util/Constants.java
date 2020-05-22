package com.corvettecole.pixelwatchface.util;

public class Constants {

  public static final String LOCATION_UPDATE_WORKER = "location_update_worker";
  public static final String WEATHER_UPDATE_WORKER = "weather_update_worker";
  public static final int WEATHER_UPDATE_INTERVAL = 60; // minutes
  public static final int WEATHER_BACKOFF_DELAY_ONETIME = 30; // seconds
  public static final int WEATHER_BACKOFF_DELAY_PERIODIC = 5; // minutes
  public static final int WEATHER_FLEX_PERIOD = 15; // minutes, run within 15 minutes of the 30 minute period
  public static final double NO_TEMPERATURE = Double.MIN_VALUE;

  public static final String KEY_LATITUDE = "latitude";
  public static final String KEY_LONGITUDE = "longitude";
  public static final String KEY_ALTITUDE = "altitude";
  public static final String KEY_LOCATION_PROVIDER = "location_provider";

  public static final String KEY_LOCATION = "location_object";

  public static final String KEY_LAST_LOCATION = "last_location_object";
  public static final String KEY_LAST_WEATHER_PROVIDER = "last_weather_provider";

  public static final String KEY_WEATHER_JSON = "weather";


  // ratio values for watch face placement
  public static final float WEATHER_ICON_MARGIN_RATIO = 1.7f;
  public static final float WEATHER_ICON_Y_OFFSET_RATIO = 1.2f;
  public static final float INFO_BAR_Y_SPACING_RATIO = 1.8f;

  public static final float WEATHER_PROVIDER_DISTANCE_UPDATE_THRESHOLD = 15000; //15km

  public static final double BOUNDING_BOX_OFFSET = 0.3;

  public enum UpdatesRequired {
    WEATHER,
    FONT
  }
}
