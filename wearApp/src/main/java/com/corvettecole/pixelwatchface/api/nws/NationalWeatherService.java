package com.corvettecole.pixelwatchface.api.nws;

import android.location.Location;
import com.corvettecole.pixelwatchface.R;
import com.corvettecole.pixelwatchface.api.WeatherProvider;
import com.corvettecole.pixelwatchface.api.nws.models.Forecast;
import com.corvettecole.pixelwatchface.api.nws.models.Period;
import com.corvettecole.pixelwatchface.api.nws.models.multistep.Grid;
import com.corvettecole.pixelwatchface.models.Weather;
import com.corvettecole.pixelwatchface.models.WeatherProviderType;
import com.corvettecole.pixelwatchface.models.metar.CloudQuantity;
import com.google.gson.JsonParseException;
import java.time.ZonedDateTime;
import org.json.JSONObject;

public class NationalWeatherService extends WeatherProvider {

  private String mHourlyForecastURL = "";

  public NationalWeatherService(Location location) {
    super(location);
  }

  @Override
  public boolean isMultistep() {
    return true;
  }

  @Override
  public String getMultistepURL() {
    return "https://api.weather.gov/points/" + mLocation.getLatitude() + "," + mLocation
        .getLongitude();
  }

  @Override
  public void parseMultistepResponse(JSONObject jsonObject) {
    Grid grid = mGson.fromJson(jsonObject.toString(), Grid.class);
    mHourlyForecastURL = grid.getProperties().getForecastHourly();
  }

  @Override
  public WeatherProviderType getType() {
    return WeatherProviderType.NWS;
  }

  @Override
  public String getWeatherURL() {
    return mHourlyForecastURL + "?units=si";
  }

  @Override
  public Weather parseWeatherResponse(JSONObject jsonObject)
      throws JsonParseException, IllegalArgumentException {
    Weather weather = new Weather();

    Forecast forecast = mGson.fromJson(jsonObject.toString(), Forecast.class);
    Period mostRecent = forecast.getProperties().getPeriods().get(0);

    weather.setTemperature(mostRecent.getTemperature());
    ZonedDateTime zonedDateTime = ZonedDateTime.parse(forecast.getProperties().getUpdateTime());
    weather.setTime(zonedDateTime.toEpochSecond());
    weather.setIconID(getWeatherIconID(mostRecent));
    return weather;
  }

  private int getWeatherIconID(Period period) throws IllegalArgumentException {
    int iconID = getWeatherIconIDFromIconURL(period.getIcon());
    if (iconID == R.drawable.ic_error) { // invalid, try getting weather using other method
      iconID = getWeatherIconIDFromDescription(period.getShortForecast());
      if (iconID == R.drawable.ic_error) {
        throw new IllegalArgumentException(
            "Unable to understand NWS weather icon. IconURL: " + period.getIcon()
                + " shortForecast: " + period.getShortForecast());
      }
    }
    return iconID;
  }

  private int getWeatherIconIDFromDescription(String description) {
    description = description.toLowerCase();

    if (description.contains("thunderstorm") || description.contains("t-storm") || description
        .contains("tstm")) {
      if (description.contains("scatter")) {
        return isDay() ? R.drawable.isolated_scattered_tstorms_day
            : R.drawable.isolated_scattered_tstorms_night;
      } else {
        return R.drawable.strong_tstorms;
      }
    }

    if (description.contains("freezing") || description.contains("sleet") || description
        .contains("mix") || description.contains("crystals") || description.contains("hail")) {
      return R.drawable.wintry_mix;
    }

    if (description.contains("rain") || description.contains("shower")) {
      if (description.contains("snow")) {
        return R.drawable.wintry_mix;
      }

      if (description.contains("heavy")) {
        return R.drawable.heavy_rain;
      } else if (description.contains("scatter")) {
        return isDay() ? R.drawable.scattered_showers_day : R.drawable.scattered_showers_night;
      } else {
        return R.drawable.showers_rain;
      }
    } else if (description.contains("drizzle")) {
      return R.drawable.drizzle;
    }

    if (description.contains("flurries")) {
      return R.drawable.flurries;
    }

    if (description.contains("blizzard")) {
      return R.drawable.blizzard;
    }

    if (description.contains("snow")) {
      return R.drawable.snow_showers;
    }

    if (description.contains("cloudy")) {
      if (description.contains("partly")) {
        return isDay() ? R.drawable.partly_cloudy : R.drawable.partly_cloudy_night;
      } else if (description.contains("mostly")) {
        return isDay() ? R.drawable.mostly_cloudy_day : R.drawable.mostly_cloudy_night;
      } else {
        return R.drawable.cloudy;
      }
    }

    if (description.contains("clearing")) {
      return isDay() ? R.drawable.mostly_sunny : R.drawable.mostly_clear_night;
    }

    if (description.contains("clouds")) {
      return R.drawable.cloudy;
    }

    if (description.contains("hot") || description.contains("cold") || description.contains("sunny")
        || description.contains("clear") || description.contains("breezy") || description
        .contains("windy") || description.contains("blustery")) {
      return isDay() ? R.drawable.sunny : R.drawable.clear_night;
    }

    if (!(description.contains("error") || description.contains("none"))) {
      return R.drawable.ic_error;
    } else {
      return R.drawable.haze_fog_dust_smoke;
    }
  }

  private int getWeatherIconIDFromIconURL(String iconURL) {
    iconURL = iconURL.toLowerCase();

    if ((iconURL.contains("ra") && iconURL.contains("sn")) || iconURL.contains("fzra") || iconURL
        .contains("mix") || (iconURL
        .contains("ra") && iconURL.contains("ip"))) { // rain snow, freezing rain, rain hail
      return R.drawable.wintry_mix;
    } else if (iconURL.contains("tsra") && iconURL.contains("sct")) { // scattered thunderstorm
      return isDay() ? R.drawable.isolated_scattered_tstorms_day
          : R.drawable.isolated_scattered_tstorms_night;
    } else if (iconURL.contains("tsra")) { // thunderstorm
      return R.drawable.strong_tstorms;
    } else if (iconURL.contains("skc")) { // sky clear
      return getCloudIcon(CloudQuantity.SKC);
    } else if (iconURL.contains("few")) { // few
      return getCloudIcon(CloudQuantity.FEW);
    } else if (iconURL.contains("sct")) { // scattered
      return getCloudIcon(CloudQuantity.SCT);
    } else if (iconURL.contains("bkn")) { // broken
      return getCloudIcon(CloudQuantity.BKN);
    } else if (iconURL.contains("overcast")) { // overcast
      return getCloudIcon(CloudQuantity.OVC);
    } else if (iconURL.contains("fg") || iconURL.contains("du") || iconURL
        .contains("fu")) { // haze, fog, etc
      return R.drawable.haze_fog_dust_smoke;
    } else if (iconURL.contains("blizzard")) { // blizzard
      return R.drawable.blizzard;
    } else if (iconURL.contains("ip")) { // hail??
      return R.drawable.wintry_mix;
    } else if (iconURL.contains("shwr")) { // high showers, so scattered
      return isDay() ? R.drawable.scattered_showers_day : R.drawable.scattered_showers_night;
    } else if (iconURL.contains("shra")) { // rain showers
      return R.drawable.showers_rain;
    } else if (iconURL.contains("rain")) {  // rain
      return R.drawable.heavy_rain;
    } else if (iconURL.contains("snow")) { // snow
      return R.drawable.snow_showers;
    } else { // no weather, return clear sky
      return R.drawable.ic_error;
    }


  }


}
