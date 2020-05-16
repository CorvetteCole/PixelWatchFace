package com.corvettecole.pixelwatchface.models;

import android.location.Location;
import com.corvettecole.pixelwatchface.util.Constants.WeatherProviderType;
import com.google.gson.JsonParseException;
import org.json.JSONObject;

public abstract class WeatherProvider {

  // include instructor in classes that implement this that has needed info to generate forecast URL

  protected Location mLocation;

  protected WeatherProvider(Location location) {
    this.mLocation = location;
  }

  public abstract WeatherProviderType getType();

  public boolean isMultistep() {
    return false;
  }

  ;

  public String getMultistepURL() {
    return null;
  }

  public void parseMultistepResponse(JSONObject jsonObject) {
  }

  public abstract String getWeatherURL();

  public abstract Weather parseWeatherResponse(JSONObject jsonObject) throws JsonParseException;
}

