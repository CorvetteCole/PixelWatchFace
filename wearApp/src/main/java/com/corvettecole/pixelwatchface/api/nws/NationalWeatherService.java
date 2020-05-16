package com.corvettecole.pixelwatchface.api.nws;

import android.location.Location;
import com.corvettecole.pixelwatchface.models.Weather;
import com.corvettecole.pixelwatchface.models.WeatherProvider;
import com.corvettecole.pixelwatchface.util.Constants.WeatherProviderType;
import com.google.gson.JsonParseException;
import org.json.JSONObject;

public class NationalWeatherService extends WeatherProvider {

  public NationalWeatherService(Location location) {
    super(location);
  }

  @Override
  public boolean isMultistep() {
    return true;
  }

  @Override
  public String getMultistepURL() {
    return null;
  }

  @Override
  public void parseMultistepResponse(JSONObject jsonObject) {

  }

  @Override
  public WeatherProviderType getType() {
    return WeatherProviderType.NWS;
  }

  @Override
  public String getWeatherURL() {
    return null;
  }

  @Override
  public Weather parseWeatherResponse(JSONObject jsonObject) throws JsonParseException {
    return null;
  }
}
