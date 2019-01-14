package com.corvettecole.pixelwatchface;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Switch use24HourTimeSwitch;
    private Switch weatherEnabledSwitch;
    private Switch useCelsiusSwitch;
    private Switch showWeatherIconSwitch;

    private boolean use24HourTime;
    private boolean weatherEnabled;
    private boolean useCelsius;
    private boolean showWeatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        use24HourTimeSwitch = findViewById(R.id.timeFormatSwitch);
        weatherEnabledSwitch = findViewById(R.id.weatherSwitch);
        useCelsiusSwitch = findViewById(R.id.celsiusSwitch);
        showWeatherIconSwitch = findViewById(R.id.weatherIconSwitch);

        loadPreferences();
        loadToggleStates();

        use24HourTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //use commit() instead of apply() to ensure data is written to sharedprefs before syncToWear runs
                sharedPreferences.edit().putBoolean("use_24_hour_time", isChecked).apply();
                syncToWear();
            }
        });

        weatherEnabledSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //use commit() instead of apply() to ensure data is written to sharedprefs before syncToWear runs
                sharedPreferences.edit().putBoolean("weather_enabled", isChecked).apply();
                syncToWear();
            }
        });

        useCelsiusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //use commit() instead of apply() to ensure data is written to sharedprefs before syncToWear runs
                sharedPreferences.edit().putBoolean("use_celsius", isChecked).apply();
                syncToWear();
            }
        });

        showWeatherIconSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //use commit() instead of apply() to ensure data is written to sharedprefs before syncToWear runs
                sharedPreferences.edit().putBoolean("show_weather_icon", isChecked).apply();
                syncToWear();
            }
        });

    }

    private void loadPreferences(){
        use24HourTime = sharedPreferences.getBoolean("use_24_hour_time", false);
        weatherEnabled = sharedPreferences.getBoolean("weather_enabled", false);
        useCelsius = sharedPreferences.getBoolean("use_celsius", false);
        showWeatherIcon = sharedPreferences.getBoolean("show_weather_icon", false);
    }

    private void syncToWear(){
        Toast.makeText(this, "something changed, syncing to watch", Toast.LENGTH_SHORT).show();
        loadPreferences();
        String TAG = "syncToWear";
        DataClient mDataClient = Wearable.getDataClient(this);
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/settings");

        /* Reference DataMap retrieval code on the WearOS app
            Log.d(TAG, "timestamp: " + dataMap.getLong("timestamp"));
            use24HourTime = dataMap.getBoolean("use_24_hour_time", false);
            weatherEnabled = dataMap.getBoolean("weather_enabled", false);
            useCelsius = dataMap.getBoolean("use_celsius", false);
            showWeatherIcon = dataMap.getBoolean("show_weather_icon");
         */

        DataMap dataMap = new DataMap();
        dataMap.putLong("timestamp", System.currentTimeMillis());
        dataMap.putBoolean("use_24_hour_time", use24HourTime);
        dataMap.putBoolean("weather_enabled", weatherEnabled);
        dataMap.putBoolean("use_celsius", useCelsius);
        dataMap.putBoolean("show_weather_icon", showWeatherIcon);

        putDataMapReq.getDataMap().putDataMap("com.corvettecole.pixelwatchface", dataMap);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        putDataReq.setUrgent();
        Task<DataItem> putDataTask = mDataClient.putDataItem(putDataReq);
        if (putDataTask.isSuccessful()){
            Log.d(TAG, "Settings synced to wearable");
        }
    }

    private void loadToggleStates(){
        use24HourTimeSwitch.setChecked(use24HourTime);
        weatherEnabledSwitch.setChecked(weatherEnabled);
        useCelsiusSwitch.setChecked(useCelsius);
        showWeatherIconSwitch.setChecked(showWeatherIcon);
    }

}
