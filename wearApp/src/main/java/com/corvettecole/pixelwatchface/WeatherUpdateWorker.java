package com.corvettecole.pixelwatchface;

import static com.corvettecole.pixelwatchface.Constants.KEY_LATITUDE;
import static com.corvettecole.pixelwatchface.Constants.KEY_LONGITUDE;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherUpdateWorker extends Worker {

  private Settings mSettings;
  private CurrentWeather mCurrentWeather;
  private RequestQueue mRequestQueue;

  public WeatherUpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
    super(context, workerParams);
  }

  @NonNull
  @Override
  public Result doWork() {
    String TAG = "weather_update_worker";
    Log.d(TAG, "starting weather work...");
    mSettings = Settings.getInstance(getApplicationContext());
    mRequestQueue = Volley.newRequestQueue(getApplicationContext());
    mCurrentWeather = CurrentWeather.getInstance(getApplicationContext());
    double latitude = getInputData().getDouble(KEY_LATITUDE, -1);
    double longitude = getInputData().getDouble(KEY_LONGITUDE, -1);
    Log.d(TAG, "latitude: " + latitude + " longitude: " + longitude);
    if (latitude != -1 && longitude != -1) {
      return updateForecast(latitude, longitude);
    } else {
      // return failure because location was never properly retrieved in chain
      return Result.failure();
    }

  }

  public Result updateForecast(double latitude, double longitude) {
    final String TAG = "getForecast";
    String forecastUrl;

    if (mSettings.isUseDarkSky() && mSettings.getDarkSkyAPIKey() != null) {
      forecastUrl = "https://api.forecast.io/forecast/" +
          mSettings.getDarkSkyAPIKey() + "/" + latitude + "," + longitude + "?lang=" + Locale
          .getDefault().getLanguage();
      Log.d(TAG, "forecastURL: " + "https://api.forecast.io/forecast/" +
          mSettings.getDarkSkyAPIKey() + "/" + latitude + "," + longitude + "?lang=" + Locale
          .getDefault().getLanguage());
    } else {
      forecastUrl =
          "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude
              + "&units=imperial&appid=" + mSettings.getOpenWeatherMapKey();
    }

    RequestFuture<JSONObject> future = RequestFuture.newFuture();
    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, forecastUrl,
        null, future, future);

    mRequestQueue.add(jsonObjectRequest);

    try {
      // 20 seconds to retrieve weather because it should be fast
      mCurrentWeather.parseWeatherJSON(future.get(20, TimeUnit.SECONDS));
      return Result.success();
    } catch (InterruptedException | TimeoutException | ExecutionException e) {
      Log.e(TAG, "error retrieving weather, will retry...");
      e.printStackTrace();
      return Result.retry();
    } catch (JSONException e) {
      Log.e(TAG, "error parsing weather, probably requires app update...");
      e.printStackTrace();
      return Result.failure();
    }
  }

}
