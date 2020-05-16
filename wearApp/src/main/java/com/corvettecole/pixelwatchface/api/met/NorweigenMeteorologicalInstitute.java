package com.corvettecole.pixelwatchface.api.met;

import android.location.Location;
import com.corvettecole.pixelwatchface.models.Weather;
import com.corvettecole.pixelwatchface.models.WeatherProvider;
import com.corvettecole.pixelwatchface.util.Constants.WeatherProviderType;
import com.google.gson.JsonParseException;
import org.json.JSONObject;

public class NorweigenMeteorologicalInstitute extends WeatherProvider {

  public NorweigenMeteorologicalInstitute(Location location) {
    super(location);
  }

  @Override
  public WeatherProviderType getType() {
    return WeatherProviderType.MET;
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
