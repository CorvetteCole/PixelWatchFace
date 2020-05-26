package com.corvettecole.pixelwatchface.api.met;

import android.location.Location;
import com.corvettecole.pixelwatchface.R;
import com.corvettecole.pixelwatchface.api.WeatherProvider;
import com.corvettecole.pixelwatchface.api.met.models.ForecastTimeInstant;
import com.corvettecole.pixelwatchface.api.met.models.ForecastTimeStepDataNext1Hours;
import com.corvettecole.pixelwatchface.api.met.models.METJSONForecast;
import com.corvettecole.pixelwatchface.models.Weather;
import com.corvettecole.pixelwatchface.models.WeatherProviderType;
import com.corvettecole.pixelwatchface.models.metar.CloudQuantity;
import com.google.gson.JsonParseException;
import java.time.ZonedDateTime;
import org.json.JSONObject;

public class NorwegianMeteorologicalInstitute extends WeatherProvider {

  public NorwegianMeteorologicalInstitute(Location location) {
    super(location);
  }

  @Override
  public WeatherProviderType getType() {
    return WeatherProviderType.MET;
  }

  @Override
  public String getWeatherURL() {
    String url =
        "https://api.met.no/weatherapi/locationforecast/2.0/?lat=" + mLocation.getLatitude()
            + "&lon=" + mLocation.getLongitude();
    if (mLocation.hasAltitude()) {
      url += "&altitude=" + Math.round(mLocation.getAltitude());
    }
    return url;
  }

  @Override
  public Weather parseWeatherResponse(JSONObject jsonObject) throws JsonParseException {
    Weather weather = new Weather();

    METJSONForecast forecast = mGson.fromJson(jsonObject.toString(), METJSONForecast.class);

    ZonedDateTime zonedDateTime = ZonedDateTime
        .parse(forecast.getProperties().getTimeseries().get(0).getTime());

    ForecastTimeInstant forecastTimeInstant = forecast.getProperties().getTimeseries().get(0)
        .getData().getInstant()
        .getDetails();
    weather.setTemperature(forecastTimeInstant.getAirTemperature().doubleValue());
    weather.setHumidity(forecastTimeInstant.getRelativeHumidity().doubleValue());
    weather.setTime(zonedDateTime.toEpochSecond());
    weather.setIconID(
        getWeatherIcon(forecast.getProperties().getTimeseries().get(0).getData().getNext1Hours(),
            getCloudQuantity(forecastTimeInstant.getCloudAreaFraction().doubleValue())));
    return weather;
  }


  private CloudQuantity getCloudQuantity(double cloudAreaFraction) {
    cloudAreaFraction = cloudAreaFraction / 100.0;
    if (cloudAreaFraction == 0) {
      return CloudQuantity.SKC;
    } else if (cloudAreaFraction < 3.0 / 8.0) {
      return CloudQuantity.FEW;
    } else if (cloudAreaFraction < 5.0 / 8.0) {
      return CloudQuantity.SCT;
    } else if (cloudAreaFraction < 7.0 / 8.0) {
      return CloudQuantity.BKN;
    } else {
      return CloudQuantity.OVC;
    }
  }

  private int getWeatherIcon(ForecastTimeStepDataNext1Hours forecast, CloudQuantity cloudQuantity) {
    switch (forecast.getSummary().getSymbolCode()) {
      case CLEARSKY_DAY:
      case CLEARSKY_NIGHT:
      case CLEARSKY_POLARTWILIGHT:
      case FAIR_DAY:
      case FAIR_NIGHT:
      case FAIR_POLARTWILIGHT:
      case CLOUDY:
      case PARTLYCLOUDY_DAY:
      case PARTLYCLOUDY_NIGHT:
      case PARTLYCLOUDY_POLARTWILIGHT:
        return getCloudIcon(cloudQuantity);
      case LIGHTSSNOWSHOWERSANDTHUNDER_DAY:
      case LIGHTSNOWSHOWERS_DAY:
      case LIGHTSSNOWSHOWERSANDTHUNDER_NIGHT:
      case LIGHTSNOWSHOWERS_NIGHT:
      case LIGHTSSNOWSHOWERSANDTHUNDER_POLARTWILIGHT:
      case LIGHTSNOWSHOWERS_POLARTWILIGHT:
      case LIGHTSNOWANDTHUNDER:
      case LIGHTSNOW:
        return R.drawable.flurries;
      case HEAVYSLEETSHOWERSANDTHUNDER_DAY:
      case HEAVYSLEETSHOWERSANDTHUNDER_NIGHT:
      case HEAVYSLEETSHOWERSANDTHUNDER_POLARTWILIGHT:
      case LIGHTSLEET:
      case HEAVYSLEET:
      case SLEETANDTHUNDER:
      case SLEET:
      case LIGHTSSLEETSHOWERSANDTHUNDER_DAY:
      case LIGHTSSLEETSHOWERSANDTHUNDER_NIGHT:
      case LIGHTSSLEETSHOWERSANDTHUNDER_POLARTWILIGHT:
      case LIGHTSLEETANDTHUNDER:
      case HEAVYSLEETANDTHUNDER:
      case SLEETSHOWERSANDTHUNDER_DAY:
      case SLEETSHOWERSANDTHUNDER_NIGHT:
      case SLEETSHOWERSANDTHUNDER_POLARTWILIGHT:
      case SLEETSHOWERS_DAY:
      case SLEETSHOWERS_NIGHT:
      case SLEETSHOWERS_POLARTWILIGHT:
      case HEAVYSLEETSHOWERS_DAY:
      case HEAVYSLEETSHOWERS_NIGHT:
      case HEAVYSLEETSHOWERS_POLARTWILIGHT:
      case LIGHTSLEETSHOWERS_DAY:
      case LIGHTSLEETSHOWERS_NIGHT:
      case LIGHTSLEETSHOWERS_POLARTWILIGHT:
        return R.drawable.wintry_mix;
      case HEAVYSNOW:
      case HEAVYSNOWANDTHUNDER:
      case SNOWANDTHUNDER:
      case HEAVYSNOWSHOWERSANDTHUNDER_DAY:
      case HEAVYSNOWSHOWERSANDTHUNDER_NIGHT:
      case HEAVYSNOWSHOWERSANDTHUNDER_POLARTWILIGHT:
      case HEAVYSNOWSHOWERS_DAY:
      case HEAVYSNOWSHOWERS_NIGHT:
      case HEAVYSNOWSHOWERS_POLARTWILIGHT:
        return R.drawable.blizzard;
      case RAINANDTHUNDER:
      case HEAVYRAINSHOWERSANDTHUNDER_DAY:
      case HEAVYRAINSHOWERSANDTHUNDER_NIGHT:
      case HEAVYRAINSHOWERSANDTHUNDER_POLARTWILIGHT:
      case HEAVYRAINANDTHUNDER:
      case RAINSHOWERSANDTHUNDER_DAY:
      case RAINSHOWERSANDTHUNDER_NIGHT:
      case RAINSHOWERSANDTHUNDER_POLARTWILIGHT:
        return R.drawable.strong_tstorms;
      case HEAVYRAINSHOWERS_DAY:
      case HEAVYRAINSHOWERS_NIGHT:
      case HEAVYRAINSHOWERS_POLARTWILIGHT:
      case HEAVYRAIN:
        return R.drawable.heavy_rain;
      case LIGHTRAINSHOWERS_DAY:
      case RAINSHOWERS_DAY:
        return R.drawable.scattered_showers_day;
      case LIGHTRAINSHOWERS_NIGHT:
      case RAINSHOWERS_NIGHT:
        return R.drawable.scattered_showers_night;
      case LIGHTRAINSHOWERS_POLARTWILIGHT:
      case RAINSHOWERS_POLARTWILIGHT:
        return isDay() ? R.drawable.scattered_showers_day : R.drawable.scattered_showers_night;
      case SNOW:
      case SNOWSHOWERS_DAY:
      case SNOWSHOWERS_NIGHT:
      case SNOWSHOWERS_POLARTWILIGHT:
      case SNOWSHOWERSANDTHUNDER_DAY:
      case SNOWSHOWERSANDTHUNDER_NIGHT:
      case SNOWSHOWERSANDTHUNDER_POLARTWILIGHT:
        return R.drawable.snow_showers;
      case LIGHTRAIN:
        return R.drawable.drizzle;
      case RAIN:
        return R.drawable.showers_rain;
      case LIGHTRAINSHOWERSANDTHUNDER_DAY:
        return R.drawable.isolated_scattered_tstorms_day;
      case LIGHTRAINSHOWERSANDTHUNDER_NIGHT:
        return R.drawable.isolated_scattered_tstorms_night;
      case LIGHTRAINANDTHUNDER:
      case LIGHTRAINSHOWERSANDTHUNDER_POLARTWILIGHT:
        return isDay() ? R.drawable.isolated_scattered_tstorms_day
            : R.drawable.isolated_scattered_tstorms_night;
      case FOG:
        return R.drawable.haze_fog_dust_smoke;
    }
    return R.drawable.cloudy;
  }

}
