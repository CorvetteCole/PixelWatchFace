package com.corvettecole.pixelwatchface;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;

public class Settings {


  private boolean use24HourTime, showTemperature, showWeatherIcon, useCelsius,
      useEuropeanDateFormat, useThinAmbient, showInfoBarAmbient, showTemperatureFractional,
      showBattery, showWearIcon, useDarkSky;

  private String darkSkyAPIKey;

  private SharedPreferences sharedPreferences;

  private static volatile Settings instance;

  private Settings(Context context) {
    if (instance != null) {
      throw new RuntimeException(
          "Use getInstance() method to get the single instance of this class");
    } else {
      sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
      loadPreferences();
    }
  }

  public static Settings getInstance(Context context) {
    if (instance == null) {
      synchronized (Settings.class) {
        if (instance == null) {
          instance = new Settings(context.getApplicationContext());
        }
      }
    }
    return instance;
  }

  public boolean isUse24HourTime() {
    return use24HourTime;
  }

  public boolean isShowTemperature() {
    return showTemperature;
  }

  public boolean isShowWeatherIcon() {
    return showWeatherIcon;
  }

  public boolean isUseCelsius() {
    return useCelsius;
  }

  public boolean isUseEuropeanDateFormat() {
    return useEuropeanDateFormat;
  }

  public boolean isUseThinAmbient() {
    return useThinAmbient;
  }

  public boolean isShowInfoBarAmbient() {
    return showInfoBarAmbient;
  }

  public boolean isShowTemperatureFractional() {
    return showTemperatureFractional;
  }

  public boolean isShowBattery() {
    return showBattery;
  }

  public boolean isShowWearIcon() {
    return showWearIcon;
  }

  public boolean isUseDarkSky() {
    return useDarkSky;
  }

  public String getDarkSkyAPIKey() {
    return darkSkyAPIKey;
  }

  public ArrayList<Constants.UPDATE_REQUIRED> updateSettings(
      DataMap dataMap) {  // returns if weather update required
    String TAG = "updateSettings";
    boolean tempShowTemperature = showTemperature;
    boolean tempShowWeatherIcon = showWeatherIcon;
    boolean tempUseDarkSky = useDarkSky;

    boolean tempUseThinAmbient = useThinAmbient;

    ArrayList<Constants.UPDATE_REQUIRED> updatesRequired = new ArrayList<>();

    Log.d(TAG, "timestamp: " + dataMap.getLong("timestamp"));
    use24HourTime = dataMap.getBoolean("use_24_hour_time");

    showTemperature = dataMap.getBoolean("show_temperature");
    useCelsius = dataMap.getBoolean("use_celsius");
    showWeatherIcon = dataMap.getBoolean("show_weather");
    darkSkyAPIKey = dataMap.getString("dark_sky_api_key");

    useEuropeanDateFormat = dataMap.getBoolean("use_european_date");
    showTemperatureFractional = dataMap.getBoolean("show_temperature_decimal");

    useThinAmbient = dataMap.getBoolean("use_thin_ambient");
    showInfoBarAmbient = dataMap.getBoolean("show_infobar_ambient");

    showBattery = dataMap.getBoolean("show_battery", true);
    showWearIcon = dataMap.getBoolean("show_wear_icon", false);

    useDarkSky = dataMap.getBoolean("use_dark_sky", false);

    savePreferences();
    if (tempUseDarkSky != useDarkSky || showTemperature != tempShowTemperature || showWeatherIcon
        != tempShowWeatherIcon) {  //detect if weather related settings has changed
      updatesRequired.add(Constants.UPDATE_REQUIRED.WEATHER);
    }
    if (tempUseThinAmbient != useThinAmbient) { // check if font needs update
      updatesRequired.add(Constants.UPDATE_REQUIRED.FONT);
    }

    return updatesRequired;

  }

  private void loadPreferences() {
    use24HourTime = sharedPreferences.getBoolean("use_24_hour_time", false);
    showTemperature = sharedPreferences.getBoolean("show_temperature", true);
    useCelsius = sharedPreferences.getBoolean("use_celsius", false);
    showWeatherIcon = sharedPreferences.getBoolean("show_weather", true);

    useThinAmbient = sharedPreferences.getBoolean("use_thin_ambient", false);
    showInfoBarAmbient = sharedPreferences.getBoolean("show_infobar_ambient", true);

    useEuropeanDateFormat = sharedPreferences.getBoolean("use_european_date", false);
    showTemperatureFractional = sharedPreferences.getBoolean("show_temperature_decimal", false);

    darkSkyAPIKey = sharedPreferences.getString("dark_sky_api_key", "");
    useDarkSky = sharedPreferences.getBoolean("use_dark_sky", false);

    showBattery = sharedPreferences.getBoolean("show_battery", true);
    showWearIcon = sharedPreferences.getBoolean("show_wear_icon", false);
  }

  private void savePreferences() {
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean("use_24_hour_time", use24HourTime);
    editor.putBoolean("show_temperature", showTemperature);
    editor.putBoolean("use_celsius", useCelsius);
    editor.putBoolean("show_weather", showWeatherIcon);
    editor.putBoolean("use_european_date", useEuropeanDateFormat);
    editor.putBoolean("show_temperature_decimal", showTemperatureFractional);
    editor.putBoolean("use_thin_ambient", useThinAmbient);
    editor.putBoolean("show_infobar_ambient", showInfoBarAmbient);
    editor.putBoolean("show_battery", showBattery);
    editor.putBoolean("show_wear_icon", showWearIcon);

    editor.putString("dark_sky_api_key", darkSkyAPIKey);
    editor.putBoolean("use_dark_sky", useDarkSky);
    editor.apply();
  }

}
