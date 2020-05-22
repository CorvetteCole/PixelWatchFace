package com.corvettecole.pixelwatchface.workers;

import static com.corvettecole.pixelwatchface.util.Constants.KEY_ALTITUDE;
import static com.corvettecole.pixelwatchface.util.Constants.KEY_LAST_LOCATION;
import static com.corvettecole.pixelwatchface.util.Constants.KEY_LAST_WEATHER_PROVIDER;
import static com.corvettecole.pixelwatchface.util.Constants.KEY_LATITUDE;
import static com.corvettecole.pixelwatchface.util.Constants.KEY_LOCATION_PROVIDER;
import static com.corvettecole.pixelwatchface.util.Constants.KEY_LONGITUDE;
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
import com.corvettecole.pixelwatchface.models.Weather;
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
  private String TAG = "weather_update_worker";

  public WeatherUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
    super(context, workerParams);
  }

  @NonNull
  @Override
  public Result doWork() {

    Log.d(TAG, "starting weather work...");
    mGson = new Gson();
    mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    mLegacyUseDarkSky = mSharedPreferences.getBoolean("use_dark_sky", false);
    mRequestQueue = Volley.newRequestQueue(getApplicationContext());

    Location location = new Location(getInputData().getString(KEY_LOCATION_PROVIDER));
    location.setLongitude(getInputData().getDouble(KEY_LONGITUDE, -1));
    location.setLatitude(getInputData().getDouble(KEY_LATITUDE, -1));
    location.setAltitude(getInputData().getDouble(KEY_ALTITUDE, 0));

    if (location.getLongitude() != -1 && location.getLatitude() != -1) {
      return updateWeather(location);
    } else {
      // return failure because location was never properly retrieved in chain (shouldn't ever happen)
      return Result.failure();
    }
  }

  private Result updateWeather(Location location) {
    // if weather provider should be updated, start with using METAR data (AWC). Otherwise, use the one we know works.
    if (shouldUpdateWeatherProvider(location)) {
      Log.d(TAG, "shouldUpdateWeatherProvider, using AWC or DS initially");
      if (!mLegacyUseDarkSky) {
        return getWeather(getWeatherProviderFromType(WeatherProviderType.AWC, location), false);
      } else {
        return getWeather(getWeatherProviderFromType(WeatherProviderType.DS, location), false);
      }
    } else {
      WeatherProviderType lastWeatherProviderType = mGson
          .fromJson(mSharedPreferences.getString(KEY_LAST_WEATHER_PROVIDER, null),
              WeatherProviderType.class);
      return getWeather(getWeatherProviderFromType(lastWeatherProviderType, location), false);
    }
  }

  private boolean shouldUpdateWeatherProvider(Location currentLocation) {
    String lastLocationJSON = mSharedPreferences.getString(KEY_LAST_LOCATION, "");
    String lastWeatherProviderTypeJSON = mSharedPreferences
        .getString(KEY_LAST_WEATHER_PROVIDER, "");

    if (lastLocationJSON.isEmpty() || lastWeatherProviderTypeJSON.isEmpty()) {
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
    Log.d(TAG, "location longitude: " + location.getLongitude());
    switch (type) {
      case NWS:
        return new NationalWeatherService(location);
      case AWC:
        return new AviationWeatherCenter(location);
      case OWM:
        return new OpenWeatherMap(location,
            getApplicationContext().getString(R.string.openstreetmap_api_key));
      case DS:
        //return new DarkSky(location, "39d38087bcdfab798f021b77ff9a0d8f");
        return new DarkSky(location, mSharedPreferences.getString("dark_sky_api_key", ""));
      case MET: // MET is the fallback API
      default:
        return new NorwegianMeteorologicalInstitute(location);
    }
  }

  private Result getWeather(WeatherProvider weatherProvider, boolean failed) {
    Log.d(TAG, "attempting to get weather from " + weatherProvider.getType().toString());
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
          headers.put("User-Agent", "weather@corvettecole.com");
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
    // TODO remove before release so we don't leak API keys
    //Log.d(TAG, "request URL: " + weatherProvider.getWeatherURL());
    RequestFuture<JSONObject> weatherFuture = RequestFuture.newFuture();
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
        weatherProvider.getWeatherURL(),
        null, weatherFuture, weatherFuture) {
      @Override
      public Map<String, String> getHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", "weather@corvettecole.com");
        return headers;
      }
    };
    mRequestQueue.add(jsonObjectRequest);

    JSONObject weatherJSON = new JSONObject();

    try {
      weatherJSON = weatherFuture.get(20, TimeUnit.SECONDS);
    } catch (JsonParseException | InterruptedException | ExecutionException | TimeoutException e) {
      e.printStackTrace();
      return Result.failure();
    }

    if (weatherJSON.length() > 0) {
      try {
        Weather weather = weatherProvider.parseWeatherResponse(weatherJSON);
        return Result.success(new Data.Builder().putString(KEY_WEATHER_JSON, mGson
            .toJson(weather))
            .build());
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        return weatherProvider.shouldRetry() ? Result.retry() : Result.failure();
      }


    } else {
      Log.d(TAG, "weatherJSON too small, failing");
      return Result.failure();
    }

  }

}
