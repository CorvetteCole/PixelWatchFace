package com.corvettecole.pixelwatchface.workers;

import static com.corvettecole.pixelwatchface.util.Constants.KEY_LAST_LOCATION;
import static com.corvettecole.pixelwatchface.util.Constants.KEY_LAST_WEATHER_PROVIDER;
import static com.corvettecole.pixelwatchface.util.Constants.KEY_LOCATION;
import static com.corvettecole.pixelwatchface.util.Constants.KEY_WEATHER_JSON;
import static com.corvettecole.pixelwatchface.util.Constants.WEATHER_PROVIDER_DISTANCE_UPDATE_THRESHOLD;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
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
import com.corvettecole.pixelwatchface.api.WeatherProvider;
import com.corvettecole.pixelwatchface.api.awc.AviationWeatherCenter;
import com.corvettecole.pixelwatchface.api.darksky.DarkSky;
import com.corvettecole.pixelwatchface.api.met.NorwegianMeteorologicalInstitute;
import com.corvettecole.pixelwatchface.api.nws.NationalWeatherService;
import com.corvettecole.pixelwatchface.api.owm.OpenWeatherMap;
import com.corvettecole.pixelwatchface.models.WeatherProviderType;
import com.corvettecole.pixelwatchface.util.Settings;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import java.util.HashMap;
import java.util.Map;
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

  private Result updateWeather(Location location) {
    // if weather provider should be updated, start with using METAR data (AWC). Otherwise, use the one we know works.
    if (shouldUpdateWeatherProvider(location)) {
      return getWeather(getWeatherProviderFromType(WeatherProviderType.AWC, location), false);
    } else {
      WeatherProviderType lastWeatherProviderType = mGson
          .fromJson(mSharedPreferences.getString(KEY_LAST_WEATHER_PROVIDER, null),
              WeatherProviderType.class);
      return getWeather(getWeatherProviderFromType(lastWeatherProviderType, location), false);
    }
  }

  private boolean shouldUpdateWeatherProvider(Location currentLocation) {
    String lastLocationJSON = mSharedPreferences.getString(KEY_LAST_LOCATION, null);
    String lastWeatherProviderTypeJSON = mSharedPreferences
        .getString(KEY_LAST_WEATHER_PROVIDER, null);

    if (lastLocationJSON == null || lastWeatherProviderTypeJSON == null) {
      return true;
    } else {
      Location lastLocation = mGson.fromJson(lastLocationJSON, Location.class);
      return currentLocation.distanceTo(lastLocation) >= WEATHER_PROVIDER_DISTANCE_UPDATE_THRESHOLD;
    }
  }

  private WeatherProvider getNextWeatherProvider(WeatherProvider lastWeatherProvider) {
    // switch case of what to return when this type fails
    switch (lastWeatherProvider.getType()) {
      default:
      case DS:
      case NWS:
        return getWeatherProviderFromType(WeatherProviderType.AWC,
            lastWeatherProvider.getLocation());
      case AWC:
        return getWeatherProviderFromType(WeatherProviderType.OWM,
            lastWeatherProvider.getLocation());
      case OWM:
        return getWeatherProviderFromType(WeatherProviderType.MET,
            lastWeatherProvider.getLocation());
      case MET:
        // return null to indicate that all APIs have failed
        return null;
    }
  }

  private WeatherProvider getWeatherProviderFromType(WeatherProviderType type, Location location) {
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
        return new NorwegianMeteorologicalInstitute(location);
    }
  }

  private Result getWeather(WeatherProvider weatherProvider, boolean failed) {
    boolean hasRequestedRetry = weatherProvider.shouldRetry();
    if (failed) {
      weatherProvider = getNextWeatherProvider(weatherProvider);
      if (weatherProvider == null) {
        mSharedPreferences.edit().putString(KEY_LAST_WEATHER_PROVIDER, null)
            .apply();
        return Result.failure();
      }
    }

    Result weatherResult = getWeatherFromProvider(weatherProvider);

    if (weatherResult.equals(Result.failure())
        || (weatherResult.equals(Result.retry())) && hasRequestedRetry) {
      return getWeather(weatherProvider, true);
    } else if (weatherResult.equals(Result.retry())) {
      return getWeather(weatherProvider, false);
    } else {
      mSharedPreferences.edit().putString(KEY_LAST_WEATHER_PROVIDER, mGson.toJson(weatherProvider))
          .apply();
      return weatherResult;
    }
  }

  private Result getWeatherFromProvider(WeatherProvider weatherProvider) {
    if (weatherProvider.isMultistep()) {
      RequestFuture<JSONObject> multistepFuture = RequestFuture.newFuture();
      JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
          weatherProvider.getMultistepURL(),
          null, multistepFuture, multistepFuture) {
        @Override
        public Map<String, String> getHeaders() {
          Map<String, String> headers = new HashMap<String, String>();
          headers.put("com.corvettecole.pixelwatchface", "weather@corvettecole.com");
          return headers;
        }
      };
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
        null, weatherFuture, weatherFuture) {
      @Override
      public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", "(Pixel Watch Face, weather@corvettecole.com)");
        return headers;
      }
    };
    mRequestQueue.add(jsonObjectRequest);

    try {
      return Result.success(new Data.Builder().putString(KEY_WEATHER_JSON, mGson
          .toJson(weatherProvider.parseWeatherResponse(weatherFuture.get(20, TimeUnit.SECONDS))))
          .build());
    } catch (IllegalArgumentException e) {
      return weatherProvider.shouldRetry() ? Result.retry() : Result.failure();
    } catch (JsonParseException | InterruptedException | ExecutionException | TimeoutException e) {
      return Result.failure();
    }
  }

}
