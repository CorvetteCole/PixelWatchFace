package com.corvettecole.pixelwatchface.api.nws;

import android.location.Location;
import com.corvettecole.pixelwatchface.R;
import com.corvettecole.pixelwatchface.api.WeatherProvider;
import com.corvettecole.pixelwatchface.api.nws.models.Forecast;
import com.corvettecole.pixelwatchface.api.nws.models.Period;
import com.corvettecole.pixelwatchface.models.Weather;
import com.corvettecole.pixelwatchface.models.WeatherProviderType;
import com.google.gson.JsonParseException;
import java.time.ZonedDateTime;
import org.json.JSONObject;

public class NationalWeatherService extends WeatherProvider {

  private boolean mIsDay = true;

  public NationalWeatherService(Location location) {
    super(location);
  }

  @Override
  protected boolean isDay() {
    return mIsDay;
  }

  @Override
  public boolean isMultistep() {
    return false;
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
    return "https://api.weather.gov/points/" + mLocation.getLatitude() + "," + mLocation
        .getLongitude() + "/forecast/hourly?units=si";
  }

  @Override
  public Weather parseWeatherResponse(JSONObject jsonObject) throws JsonParseException {
    Weather weather = new Weather();

    Forecast forecast = mGson.fromJson(jsonObject.toString(), Forecast.class);
    Period mostRecent = forecast.getProperties().getPeriods().get(0);

    mIsDay = mostRecent.getIsDaytime();
    weather.setTemperature(mostRecent.getTemperature());
    ZonedDateTime zonedDateTime = ZonedDateTime.parse(forecast.getProperties().getUpdateTime());
    weather.setTime(zonedDateTime.toEpochSecond());
    // TODO implement weather icon getting... or don't? METAR data should handle this
    weather.setIconID(R.drawable.ic_error);
    return weather;
  }



}
