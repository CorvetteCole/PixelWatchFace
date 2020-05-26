package com.corvettecole.pixelwatchface.watchface;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.wear.widget.CircularProgressLayout;
import com.corvettecole.pixelwatchface.R;
import com.corvettecole.pixelwatchface.util.Settings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class WatchPermissionRequestActivity extends WearableActivity {

  // TODO simplify permission codes, kind of confusing how you have it laid out

  private static int PERMISSIONS_CODE = 7;
  String[] mPermissions;
  int mRequestCode;


  private final String TAG = "PermissionRequestActivity";
  private Settings mSettings;

  private FloatingActionButton mLocationRequestPositive;
  private CircularProgressLayout mCircularProgressLayout;
  private FloatingActionButton mLocationRequestNegative;

  @SuppressLint("LongLogTag")
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    mCircularProgressLayout.setIndeterminate(false);
    boolean shouldExit = true;
    if (requestCode == mRequestCode) {
      for (int i = 0; i < permissions.length; i++) {
        String permission = permissions[i];
        int grantResult = grantResults[i];
        Log.d("PermissionRequestActivity",
            "" + permission + " " + (grantResult == PackageManager.PERMISSION_GRANTED ? "granted"
                : "revoked"));
        if (grantResult == PackageManager.PERMISSION_DENIED) {
          shouldExit = false;
        }
      }
    }
    if (shouldExit) {
      finish();
    }
  }

  @Override
  public void onEnterAmbient(Bundle ambientDetails) {
    Log.d(TAG, "onEnterAmbient");
    mCircularProgressLayout.setIndeterminate(false);
    mLocationRequestPositive.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
    mLocationRequestNegative.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
    super.onEnterAmbient(ambientDetails);
  }

  @Override
  public void onExitAmbient() {
    Log.d(TAG, "onExitAmbient");
    mLocationRequestPositive.setBackgroundTintList(
        ColorStateList.valueOf(getApplicationContext().getColor(R.color.circular_button_normal)));
    mLocationRequestNegative.setBackgroundTintList(
        ColorStateList.valueOf(getApplicationContext().getColor(R.color.circular_button_disabled)));
    super.onExitAmbient();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.permission_layout);
    //mPermissions = new String[2];
    mSettings = Settings.getInstance(getApplicationContext());

    setAmbientEnabled();
    setAutoResumeEnabled(true);

    mPermissions = this.getIntent().getStringArrayExtra("KEY_PERMISSIONS");
    mRequestCode = this.getIntent().getIntExtra("KEY_REQUEST_CODE", PERMISSIONS_CODE);

    mLocationRequestPositive = findViewById(R.id.locationRequestPositive);
    mCircularProgressLayout = findViewById(R.id.circular_progress);
    mLocationRequestNegative = findViewById(R.id.locationRequestNegative);

    mLocationRequestPositive.setOnClickListener(v -> {
      mCircularProgressLayout.setIndeterminate(true);
      ActivityCompat.requestPermissions(this, mPermissions, mRequestCode);
    });

    mLocationRequestNegative.setOnClickListener(v -> {
      mSettings.setWeatherDisabled();
      finish();
    });

  }


}
