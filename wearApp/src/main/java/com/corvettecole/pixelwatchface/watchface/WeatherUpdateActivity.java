package com.corvettecole.pixelwatchface.watchface;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import com.corvettecole.pixelwatchface.R;
import com.corvettecole.pixelwatchface.util.Settings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WeatherUpdateActivity extends WearableActivity {

  private Settings mSettings;

  private FloatingActionButton mWeatherUpdateConfirm;

  @Override
  public void onEnterAmbient(Bundle ambientDetails) {

    mWeatherUpdateConfirm.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));

    super.onEnterAmbient(ambientDetails);
  }

  @Override
  public void onExitAmbient() {

    mWeatherUpdateConfirm.setBackgroundTintList(
        ColorStateList.valueOf(getApplicationContext().getColor(R.color.circular_button_normal)));
    super.onExitAmbient();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.weather_update_layout);

    setAmbientEnabled();
    setAutoResumeEnabled(true);

    mSettings = Settings.getInstance(getApplicationContext());



    mWeatherUpdateConfirm = findViewById(R.id.weatherUpdateConfirm);

    mWeatherUpdateConfirm.setOnClickListener(v -> {
      mSettings.setWeatherChangeNotified(true);
      if (!mSettings.isCompanionAppNotified()) { // show companion app notify
        Intent companionNotifyIntent = new Intent(getBaseContext(),
            CompanionNotifyActivity.class);
        companionNotifyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(companionNotifyIntent);
      }
      finish();
    });

  }

}
