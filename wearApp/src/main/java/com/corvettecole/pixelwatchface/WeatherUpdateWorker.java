package com.corvettecole.pixelwatchface;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.core.app.ActivityCompat;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class WeatherUpdateWorker extends ListenableWorker {

  /**
   * @param appContext   The application {@link Context}
   * @param workerParams Parameters to setup the internal state of this worker
   */
  public WeatherUpdateWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
    super(appContext, workerParams);
  }

  @NonNull
  @Override
  public ListenableFuture<Result> startWork() {
    String TAG = "WeatherUpdateWorker";
    CurrentWeather currentWeather = CurrentWeather.getInstance(getApplicationContext());
    FusedLocationProviderClient mFusedLocationClient = LocationServices
        .getFusedLocationProviderClient(getApplicationContext());
    return CallbackToFutureAdapter.getFuture(completer -> {
      mFusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
        if (location != null) {
          Log.d(TAG, "updating forecast with location: (" + location.getLatitude() + "," + location
              .getLongitude() + ")");
          try {
            completer.set(
                currentWeather.updateForecast(location.getLatitude(), location.getLongitude())
                    .get());
          } catch (Exception e) {
            e.printStackTrace();
            completer.setException(e);
          }
        } else {
          if (ActivityCompat.checkSelfPermission(getApplicationContext(),
              Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            completer.set(Result.failure());
          } else {
            // if no location, but permission exists, try again
            completer.set(Result.retry());
          }
        }
      });
      return completer;
    });
  }
}
