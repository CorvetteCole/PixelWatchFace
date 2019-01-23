package com.corvettecole.pixelwatchface;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements DataClient.OnDataChangedListener {

    private SharedPreferences sharedPreferences;
    private Switch use24HourTimeSwitch;
    private Switch showTemperatureSwitch;
    private Switch useCelsiusSwitch;
    private Switch showWeatherSwitch;
    private Switch useDarkSkySwitch;

    private TextView timestampTextView;
    private TextView timeFormatTextView;
    private TextView showTemperatureTextView;
    private TextView useCelsiusTextView;
    private TextView showWeatherTextView;

    private EditText darkSkyKeyEditText;

    private Button debugButton;

    private boolean use24HourTime;
    private boolean showTemperature;
    private boolean useCelsius;
    private boolean showWeather;
    private String darkSkyAPIKey;
    private boolean useDarkSky;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Wearable.getDataClient(getApplicationContext()).addListener(this);

        use24HourTimeSwitch = findViewById(R.id.timeFormatSwitch);
        showTemperatureSwitch = findViewById(R.id.temperatureSwitch);
        useCelsiusSwitch = findViewById(R.id.celsiusSwitch);
        showWeatherSwitch = findViewById(R.id.weatherSwitch);
        useDarkSkySwitch = findViewById(R.id.useDarkSkySwitch);

        timestampTextView = findViewById(R.id.timestampTextView);
        timeFormatTextView = findViewById(R.id.timeFormatTextView);
        showTemperatureTextView = findViewById(R.id.showTemperatureTextView);
        useCelsiusTextView = findViewById(R.id.useCelsiusTextView);
        showWeatherTextView = findViewById(R.id.showWeatherTextView);

        darkSkyKeyEditText = findViewById(R.id.darkSkyEditText);

        debugButton = findViewById(R.id.launchDebug);


        loadPreferences();
        loadSettingStates();

        use24HourTimeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("use_24_hour_time", isChecked).apply();
                syncToWear();
            }
        });

        showTemperatureSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("show_temperature", isChecked).apply();
                syncToWear();
            }
        });

        useCelsiusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("use_celsius", isChecked).apply();
                syncToWear();
            }
        });

        showWeatherSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("show_weather", isChecked).apply();
                syncToWear();
            }
        });

        useDarkSkySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sharedPreferences.edit().putBoolean("use_dark_sky", isChecked).apply();
                darkSkyKeyEditText.setEnabled(isChecked);
                syncToWear();
            }
        });

        darkSkyKeyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sharedPreferences.edit().putString("dark_sky_api_key", darkSkyKeyEditText.getText().toString()).apply();
                syncToWear();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        debugButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent debugIntent = new Intent(MainActivity.this, DebugActivity.class);
                startActivity(debugIntent);
            }
        });


    }

    private void loadPreferences(){
        use24HourTime = sharedPreferences.getBoolean("use_24_hour_time", false);
        showTemperature = sharedPreferences.getBoolean("show_temperature", false);
        useCelsius = sharedPreferences.getBoolean("use_celsius", false);
        showWeather = sharedPreferences.getBoolean("show_weather", false);
        darkSkyAPIKey = sharedPreferences.getString("dark_sky_api_key", "");
        useDarkSky = sharedPreferences.getBoolean("use_dark_sky", false);
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
        dataMap.putString("dark_sky_api_key", darkSkyAPIKey);
        dataMap.putBoolean("use_dark_sky", useDarkSky);

        putDataMapReq.getDataMap().putDataMap("com.corvettecole.pixelwatchface", dataMap);
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        putDataReq.setUrgent();
        Task<DataItem> putDataTask = mDataClient.putDataItem(putDataReq);
        if (putDataTask.isSuccessful()){
            Log.d(TAG, "Settings synced to wearable");
        }
    }

    /*
            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/settings/watch_status");

            DataMap dataMap = new DataMap();
            dataMap.putLong("wear_timestamp", System.currentTimeMillis());
            dataMap.putBoolean("use_24_hour_time", mUse24HourTime);
            dataMap.putBoolean("show_temperature", mShowTemperature);
            dataMap.putBoolean("use_celsius", mUseCelsius);
            dataMap.putBoolean("show_weather", mShowWeather);
     */


    private void loadSettingStates(){
        use24HourTimeSwitch.setChecked(use24HourTime);
        showTemperatureSwitch.setChecked(showTemperature);
        useCelsiusSwitch.setChecked(useCelsius);
        showWeatherSwitch.setChecked(showWeather);
        useDarkSkySwitch.setChecked(useDarkSky);
        if (!useDarkSky) {
            darkSkyKeyEditText.setEnabled(false);
        }
        darkSkyKeyEditText.setText(darkSkyAPIKey);
    }

    private void updateStatus(DataMap dataMap){
        String TAG = "updateStatus";
        try {
            long timestamp = dataMap.getLong("wear_timestamp");
            boolean mUse24HourTime = dataMap.getBoolean("use_24_hour_time");
            boolean mShowTemperature = dataMap.getBoolean("show_temperature");
            boolean mUseCelsius = dataMap.getBoolean("use_celsius");
            boolean mShowWeather = dataMap.getBoolean("show_weather");

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);

            timestampTextView.setText("last synced: " + timestamp + "ms or " + calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
            timeFormatTextView.setText("use_24_hour_time: " + mUse24HourTime);
            showTemperatureTextView.setText("show_temperature: " + mShowTemperature);
            useCelsiusTextView.setText("use_celsius: " + mUseCelsius);
            showWeatherTextView.setText("show_weather: " + mShowWeather);

        } catch (Exception e){
            Log.e(TAG, "error processing DataMap");
            Log.e(TAG, e.toString());
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        String TAG = "onDataChanged";
        Log.d(TAG, "Data changed");
        DataMap dataMap = new DataMap();
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                Log.d(TAG, "DataItem uri: " + item.getUri());
                if (item.getUri().getPath().compareTo("/watch_status") == 0) {
                    Log.d(TAG, "Companion app changed a setting!");
                    dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    Log.d(TAG, dataMap.toString());
                    dataMap = dataMap.getDataMap("com.corvettecole.pixelwatchface");
                    Log.d(TAG, dataMap.toString());
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
        updateStatus(dataMap);
    }

    @Override
    public void onDestroy() {
        Wearable.getDataClient(getApplicationContext()).removeListener(this);
        super.onDestroy();
    }
}
