package com.corvettecole.pixelwatchface;

public class Constants {

    public static final String WEATHER_UPDATE_WORKER = "weather_update_worker";
    public static final int WEATHER_UPDATE_INTERVAL = 30;
    public static final int WEATHER_BACKOFF_DELAY_ONETIME = 30; // seconds
    public static final int WEATHER_BACKOFF_DELAY_PERIODIC = 5; // minutes
    public static final int WEATHER_FLEX_PERIOD = 15; // minutes, run within 15 minutes of the 30 minute period

    public enum UPDATE_REQUIRED {
        WEATHER,
        FONT
    }

}
