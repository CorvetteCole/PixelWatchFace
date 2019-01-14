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

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private Switch use24HourTimeSwitch;
    private Switch showTemperatureSwitch;
    private Switch useCelsiusSwitch;
    private Switch showWeatherSwitch;

    private boolean use24HourTime;
    private boolean showTemperature;
    private boolean useCelsius;
    private boolean showWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        use24HourTimeSwitch = findViewById(R.id.timeFormatSwitch);
        showTemperatureSwitch = findViewById(R.id.temperatureSwitch);
        useCelsiusSwitch = findViewById(R.id.celsiusSwitch);
        showWeatherSwitch = findViewById(R.id.weatherSwitch);

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

        showTemperatureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //use commit() instead of apply() to ensure data is written to sharedprefs before syncToWear runs
                sharedPreferences.edit().putBoolean("show_temperature", isChecked).apply();
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

        showWeatherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //use commit() instead of apply() to ensure data is written to sharedprefs before syncToWear runs
                sharedPreferences.edit().putBoolean("show_weather", isChecked).apply();
                syncToWear();
            }
        });

    }

    private void loadPreferences(){
        use24HourTime = sharedPreferences.getBoolean("use_24_hour_time", false);
        showTemperature = sharedPreferences.getBoolean("show_temperature", false);
        useCelsius = sharedPreferences.getBoolean("use_celsius", false);
        showWeather = sharedPreferences.getBoolean("show_weather", false);
    }

    private void syncToWear(){
        Toast.makeText(this, "something changed, syncing to watch", Toast.LENGTH_SHORT).show();
        loadPreferences();
        String TAG = "syncToWear";
        DataClient mDataClient = Wearable.getDataClient(this);
        PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/settings");

        /* Reference DataMap retrieval code on the WearOS app
                mUse24HourTime = dataMap.getBoolean("use_24_hour_time");
                mShowTemperature = dataMap.getBoolean("show_temperature");
                mUseCelsius = dataMap.getBoolean("use_celsius");
                mShowWeather = dataMap.getBoolean("show_weather");
                */

        DataMap dataMap = new DataMap();
        dataMap.putLong("timestamp", System.currentTimeMillis());
        dataMap.putBoolean("use_24_hour_time", use24HourTime);
        dataMap.putBoolean("show_temperature", showTemperature);
        dataMap.putBoolean("use_celsius", useCelsius);
        dataMap.putBoolean("show_weather", showWeather);

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
        showTemperatureSwitch.setChecked(showTemperature);
        useCelsiusSwitch.setChecked(useCelsius);
        showWeatherSwitch.setChecked(showWeather);
    }

}
