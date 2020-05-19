package com.corvettecole.pixelwatchface.api.owm;

import android.location.Location;
import com.corvettecole.pixelwatchface.R;
import com.corvettecole.pixelwatchface.api.WeatherProvider;
import com.corvettecole.pixelwatchface.api.owm.models.Current;
import com.corvettecole.pixelwatchface.models.Weather;
import com.corvettecole.pixelwatchface.models.WeatherProviderType;
import com.google.gson.JsonParseException;
import org.json.JSONObject;

public class OpenWeatherMap extends WeatherProvider {

  private String mKey;

  public OpenWeatherMap(Location location, String key) {
    super(location);
    this.mKey = key;
  }

  @Override
  public WeatherProviderType getType() {
    return WeatherProviderType.OWM;
  }

  @Override
  public String getWeatherURL() {
    return "https://api.openweathermap.org/data/2.5/weather?lat=" + mLocation.getLatitude()
        + "&lon=" + mLocation.getLongitude()
        + "&units=metric&appid=" + mKey;
  }

  @Override
  public Weather parseWeatherResponse(JSONObject jsonObject) throws JsonParseException {
    Weather weather = new Weather();

    Current current = mGson.fromJson(jsonObject.toString(), Current.class);

    weather.setTemperature(current.getMain().getTemp());
    weather.setHumidity(current.getMain().getHumidity());
    weather.setTime(current.getDt());
    weather.setIconID(getWeatherIcon(current));

    return weather;
  }

  private int getWeatherIcon(Current current) {
    // TODO write this code
    return R.drawable.sunny;


  }


}
