package com.corvettecole.pixelwatchface.api.awc;

import android.location.Location;
import com.corvettecole.pixelwatchface.models.Weather;
import com.corvettecole.pixelwatchface.models.WeatherProvider;
import com.corvettecole.pixelwatchface.util.Constants.WeatherProviderType;
import com.google.gson.JsonParseException;
import org.json.JSONObject;

public class AviationWeatherCenter extends WeatherProvider {

  public AviationWeatherCenter(Location location) {
    super(location);
  }

  @Override
  public WeatherProviderType getType() {
    return WeatherProviderType.AWC;
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
