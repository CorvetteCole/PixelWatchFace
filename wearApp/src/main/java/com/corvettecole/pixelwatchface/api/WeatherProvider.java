package com.corvettecole.pixelwatchface.api;

import android.location.Location;
import com.corvettecole.pixelwatchface.models.Weather;
import com.corvettecole.pixelwatchface.models.WeatherProviderType;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import java.util.Calendar;
import java.util.TimeZone;
import org.json.JSONObject;

public abstract class WeatherProvider {

  // include instructor in classes that implement this that has needed info to generate forecast URL

  protected Location mLocation;
  protected Gson mGson;
  protected boolean mRetry;

  protected WeatherProvider(Location location) {
    this.mLocation = location;
    mGson = new Gson();
  }

  protected boolean isDay() {
    System.out.println(TimeZone.getDefault().getID());
    Calendar currentTime = Calendar.getInstance();
    SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(
        new com.luckycatlabs.sunrisesunset.dto.Location(mLocation.getLatitude(),
            mLocation.getLongitude()), TimeZone.getDefault());
    Calendar sunrise = calculator.getOfficialSunriseCalendarForDate(currentTime);
    Calendar sunset = calculator.getOfficialSunsetCalendarForDate(currentTime);
    return currentTime.after(sunrise) && currentTime.before(sunset);
  }

  public abstract WeatherProviderType getType();

  public boolean isMultistep() {
    return false;
  }

  public String getMultistepURL() {
    return null;
  }

  public void parseMultistepResponse(JSONObject jsonObject)
      throws JsonParseException, IllegalArgumentException {
  }

  public abstract String getWeatherURL();

  public abstract Weather parseWeatherResponse(JSONObject jsonObject)
      throws JsonParseException, IllegalArgumentException;

  public Location getLocation() {
    return mLocation;
  }

  public boolean shouldRetry() {
    return mRetry;
  }

}

