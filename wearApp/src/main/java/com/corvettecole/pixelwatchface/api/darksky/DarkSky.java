package com.corvettecole.pixelwatchface.api.darksky;

import android.location.Location;
import com.corvettecole.pixelwatchface.models.Weather;
import com.corvettecole.pixelwatchface.models.WeatherProvider;
import com.corvettecole.pixelwatchface.util.Constants.WeatherProviderType;
import com.google.gson.JsonParseException;
import org.json.JSONObject;

public class DarkSky extends WeatherProvider {

  private String mKey;

  public DarkSky(Location location, String key) {
    super(location);
    this.mKey = key;
  }

  @Override
  public WeatherProviderType getType() {
    return WeatherProviderType.DS;
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
