package com.corvettecole.pixelwatchface.workers;

import static com.corvettecole.pixelwatchface.util.Constants.KEY_LAST_LOCATION;
import static com.corvettecole.pixelwatchface.util.Constants.KEY_LAST_WEATHER_PROVIDER;
import static com.corvettecole.pixelwatchface.util.Constants.KEY_LOCATION;
import static com.corvettecole.pixelwatchface.util.Constants.KEY_WEATHER_JSON;
import static com.corvettecole.pixelwatchface.util.Constants.WEATHER_PROVIDER_DISTANCE_UPDATE_THRESHOLD;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.Location;
import android.util.ArraySet;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.corvettecole.pixelwatchface.R;
import com.corvettecole.pixelwatchface.api.awc.AviationWeatherCenter;
import com.corvettecole.pixelwatchface.api.darksky.DarkSky;
import com.corvettecole.pixelwatchface.api.met.NorweigenMeteorologicalInstitute;
import com.corvettecole.pixelwatchface.api.nws.NationalWeatherService;
import com.corvettecole.pixelwatchface.api.owm.OpenWeatherMap;
import com.corvettecole.pixelwatchface.models.WeatherProvider;
import com.corvettecole.pixelwatchface.util.Constants.WeatherProviderType;
import com.corvettecole.pixelwatchface.util.Settings;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.JSONObject;

public class WeatherUpdateWorker extends Worker {

  private Settings mSettings;

  private RequestQueue mRequestQueue;
  private Gson mGson;
  private SharedPreferences mSharedPreferences;
  private boolean mLegacyUseDarkSky;

  public WeatherUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
    super(context, workerParams);
  }

  @NonNull
  @Override
  public Result doWork() {
    String TAG = "weather_update_worker";
    Log.d(TAG, "starting weather work...");
    mGson = new Gson();
    mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    mLegacyUseDarkSky = mSharedPreferences.getBoolean("use_dark_sky", false);
    mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    Location location = mGson.fromJson(getInputData().getString(KEY_LOCATION), Location.class);
    if (location != null) {
      return updateWeather(location);
    } else {
      // return failure because location was never properly retrieved in chain (shouldn't ever happen)
      return Result.failure();
    }
  }


  private WeatherProvider getWeatherProvider(Location location, Location lastLocation,
      ArraySet<WeatherProviderType> weatherProviderFailures, WeatherProviderType lastProvider) {
    if (lastProvider == null || lastLocation == null
        || location.distanceTo(lastLocation) >= WEATHER_PROVIDER_DISTANCE_UPDATE_THRESHOLD
        || weatherProviderFailures.contains(lastProvider)) {
      Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
      mSharedPreferences.edit().putString(KEY_LAST_LOCATION, mGson.toJson(location)).apply();
      boolean inUS = false;
      try {
        inUS = Objects.equals(
            gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0)
                .getCountryCode(), "US");
      } catch (IOException e) {
        Log.e("getWeatherProvider", "Geocoder failure, nonfatal");
        e.printStackTrace();
      }
      if (mLegacyUseDarkSky) {
        return getWeatherProviderFromType(WeatherProviderType.DS, location);
      } else if (inUS && !weatherProviderFailures.contains(WeatherProviderType.NWS)) {
        return getWeatherProviderFromType(WeatherProviderType.NWS, location);
      } else if (!weatherProviderFailures.contains(WeatherProviderType.AWC)) {
        return getWeatherProviderFromType(WeatherProviderType.AWC, location);
      } else if (!weatherProviderFailures.contains(WeatherProviderType.OWM)) {
        return getWeatherProviderFromType(WeatherProviderType.OWM, location);
      } else {
        return getWeatherProviderFromType(WeatherProviderType.MET, location);
      }
    } else {
      return getWeatherProviderFromType(lastProvider, location);
    }
  }

  private WeatherProvider getWeatherProviderFromType(WeatherProviderType type, Location location) {
    // TODO fill this out
    switch (type) {
      case NWS:
        return new NationalWeatherService(location);
      case AWC:
        return new AviationWeatherCenter(location);
      case OWM:
        return new OpenWeatherMap(location,
            getApplicationContext().getString(R.string.openstreetmap_api_key));
      case DS:
        return new DarkSky(location, mSharedPreferences.getString("dark_sky_api_key", ""));
      case MET: // MET is the fallback API
      default:
        return new NorweigenMeteorologicalInstitute(location);
    }
  }

  private Result updateWeather(Location location) {
    Location lastLocation = null;
    WeatherProviderType lastWeatherProviderType = null;
    try {
      lastLocation = mGson
          .fromJson(mSharedPreferences.getString(KEY_LAST_LOCATION, null), Location.class);
      lastWeatherProviderType = mGson
          .fromJson(mSharedPreferences.getString(KEY_LAST_WEATHER_PROVIDER, null),
              WeatherProviderType.class);
    } catch (Exception e) {
      // TODO this should probably be improved
    }
    return updateWeather(location, lastLocation, new ArraySet<WeatherProviderType>(),
        lastWeatherProviderType);
  }

  private Result updateWeather(Location location, Location lastLocation,
      ArraySet<WeatherProviderType> weatherProviderFailures,
      WeatherProviderType lastWeatherProvider) {

    WeatherProvider weatherProvider = getWeatherProvider(location, lastLocation,
        weatherProviderFailures, lastWeatherProvider);
    Result weatherResult = getWeather(weatherProvider);
    if (weatherResult.equals(Result.failure())) {
      if (weatherProvider.getType().equals(WeatherProviderType.MET)) {
        return weatherResult; // return failure if it still failed using MET
      }
      weatherProviderFailures.add(weatherProvider.getType());
      return updateWeather(location, lastLocation, weatherProviderFailures,
          weatherProvider.getType());
    } else {
      mSharedPreferences.edit().putString(KEY_LAST_WEATHER_PROVIDER, mGson.toJson(weatherProvider))
          .apply();
      return weatherResult;
    }

  }

  private Result getWeather(WeatherProvider weatherProvider) {
    if (weatherProvider.isMultistep()) {
      RequestFuture<JSONObject> multistepFuture = RequestFuture.newFuture();
      JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
          weatherProvider.getMultistepURL(),
          null, multistepFuture, multistepFuture);
      mRequestQueue.add(jsonObjectRequest);
      try {
        weatherProvider.parseMultistepResponse(multistepFuture.get(20, TimeUnit.SECONDS));
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        e.printStackTrace();
        return Result.failure();
      }
    }
    RequestFuture<JSONObject> weatherFuture = RequestFuture.newFuture();
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
        weatherProvider.getWeatherURL(),
        null, weatherFuture, weatherFuture);
    mRequestQueue.add(jsonObjectRequest);

    try {
      return Result.success(new Data.Builder().putString(KEY_WEATHER_JSON, mGson
          .toJson(weatherProvider.parseWeatherResponse(weatherFuture.get(20, TimeUnit.SECONDS))))
          .build());
    } catch (JsonParseException | InterruptedException | ExecutionException | TimeoutException e) {
      return Result.failure();
    }
  }

}
