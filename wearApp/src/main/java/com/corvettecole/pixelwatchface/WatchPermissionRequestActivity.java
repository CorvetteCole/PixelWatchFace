package com.corvettecole.pixelwatchface;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.core.app.ActivityCompat;

public class WatchPermissionRequestActivity extends Activity {

  // TODO simplify permission codes, kind of confusing how you have it laid out

  private static int PERMISSIONS_CODE = 7;
  String[] mPermissions;
  int mRequestCode;

  private Button permButton;

  @SuppressLint("LongLogTag")
  @Override
  public void onRequestPermissionsResult(int requestCode, String[] permissions,
      int[] grantResults) {
    if (requestCode == mRequestCode) {
      for (int i = 0; i < permissions.length; i++) {
        String permission = permissions[i];
        int grantResult = grantResults[i];
        Log.d("PermissionRequestActivity",
            "" + permission + " " + (grantResult == PackageManager.PERMISSION_GRANTED ? "granted"
                : "revoked"));
      }
    }
    finish();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.permission_layout);
    //mPermissions = new String[2];

    mPermissions = this.getIntent().getStringArrayExtra("KEY_PERMISSIONS");
    mRequestCode = this.getIntent().getIntExtra("KEY_REQUEST_CODE", PERMISSIONS_CODE);

    permButton = findViewById(R.id.permButton);

    permButton.setOnClickListener(v -> {
      ActivityCompat.requestPermissions(this, mPermissions, mRequestCode);
    });






    //ActivityCompat.requestPermissions(this, mPermissions, mRequestCode);
  }
}
