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
    switch (current.getWeather().get(0).getId()) {

      case LIGHT_RAIN_THUNDERSTORM:
      case RAGGED_THUNDERSTORM:
      case LIGHT_DRIZZLE_THUNDERSTORM:
      case DRIZZLE_THUNDERSTORM:
        return isDay() ? R.drawable.isolated_scattered_tstorms_day
            : R.drawable.isolated_scattered_tstorms_night;
      case HEAVY_DRIZZLE_THUNDERSTORM:
      case RAIN_THUNDERSTORM:
      case HEAVY_RAIN_THUNDERSTORM:
      case LIGHT_THUNDERSTORM:
      case THUNDERSTORM:
      case HEAVY_THUNDERSTORM:
        return R.drawable.strong_tstorms;
      case LIGHT_DRIZZLE:
      case DRIZZLE:
      case HEAVY_DRIZZLE:
      case LIGHT_DRIZZLE_RAIN:
        return R.drawable.drizzle;

      case SHOWER_RAIN_DRIZZLE:
      case HEAVY_SHOWER_RAIN_DRIZZLE:
      case SHOWER_DRIZZLE:
      case SHOWER_RAIN:
      case LIGHT_SHOWER_RAIN:
      case HEAVY_SHOWER_RAIN:
      case RAGGED_SHOWER_RAIN:
        return isDay() ? R.drawable.scattered_showers_day : R.drawable.scattered_showers_night;

      case DRIZZLE_RAIN:
      case HEAVY_DRIZZLE_RAIN:
      case LIGHT_RAIN:
      case MODERATE_RAIN:
        return R.drawable.showers_rain;
      case HEAVY_RAIN:
      case VERY_HEAVY_RAIN:
      case EXTREME_RAIN:
        return R.drawable.heavy_rain;
      case FREEZING_RAIN:
      case SLEET:
      case LIGHT_SHOWER_SLEET:
      case SHOWER_SLEET:
      case LIGHT_RAIN_SNOW:
      case RAIN_SNOW:
        return R.drawable.wintry_mix;
      case LIGHT_SNOW:
      case LIGHT_SHOWER_SNOW:
        return R.drawable.flurries;
      case SNOW:
      case SHOWER_SNOW:
        return R.drawable.snow_showers;
      case HEAVY_SNOW:
      case HEAVY_SHOWER_SNOW:
        return R.drawable.blizzard;
      case MIST:
      case SMOKE:
      case HAZE:
      case DUST_WHIRLS:
      case FOG:
      case SAND:
      case DUST:
      case ASH:
      case SQUALL:
      case TORNADO:
        return R.drawable.haze_fog_dust_smoke;
      case CLEAR:
        return isDay() ? R.drawable.sunny : R.drawable.clear_night;
      case CLOUDS_FEW:
        return isDay() ? R.drawable.mostly_sunny : R.drawable.mostly_clear_night;
      case CLOUDS_SCATTERED:
        return isDay() ? R.drawable.partly_cloudy : R.drawable.partly_cloudy_night;
      case CLOUDS_BROKEN:
        return isDay() ? R.drawable.mostly_cloudy_day : R.drawable.mostly_cloudy_night;
      default:
      case CLOUDS_OVERCAST:
        return R.drawable.cloudy;
    }
  }


}
