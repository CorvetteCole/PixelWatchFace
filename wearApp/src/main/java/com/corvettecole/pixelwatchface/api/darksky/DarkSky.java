package com.corvettecole.pixelwatchface.api.darksky;

import android.location.Location;
import com.corvettecole.pixelwatchface.R;
import com.corvettecole.pixelwatchface.api.WeatherProvider;
import com.corvettecole.pixelwatchface.api.darksky.models.Currently;
import com.corvettecole.pixelwatchface.api.darksky.models.Forecast;
import com.corvettecole.pixelwatchface.models.Weather;
import com.corvettecole.pixelwatchface.models.WeatherProviderType;
import com.google.gson.JsonParseException;
import java.util.Locale;
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
    return "https://api.darksky.net/forecast/" +
        mKey + "/" + mLocation.getLatitude() + "," + mLocation.getLongitude() + "?lang=" + Locale
        .getDefault().getLanguage() + "&units=si";
  }

  @Override
  public Weather parseWeatherResponse(JSONObject jsonObject) throws JsonParseException {
    Weather weather = new Weather();

    Forecast forecast = mGson.fromJson(jsonObject.toString(), Forecast.class);
    weather.setTime(forecast.getCurrently().getTime());
    weather.setHumidity(forecast.getCurrently().getHumidity());
    weather.setTemperature(forecast.getCurrently().getTemperature());
    weather.setIconID(getWeatherIcon(forecast.getCurrently()));

    return weather;
  }


  private int getWeatherIcon(Currently currently) {
    switch (currently.getIcon()) {
      case "clear-day":
        return R.drawable.sunny;
      case "clear-night":
        return R.drawable.clear_night;
      case "rain":
        return R.drawable.showers_rain;
      case "snow":
        return R.drawable.snow_showers;
      case "sleet":
        return R.drawable.wintry_mix;
      case "fog":
        return R.drawable.haze_fog_dust_smoke;
      default:
      case "wind":
      case "cloudy":
        return R.drawable.cloudy;
      case "partly-cloudy-day":
        return R.drawable.partly_cloudy;
      case "partly-cloudy-night":
        return R.drawable.partly_cloudy_night;
    }
  }
}
