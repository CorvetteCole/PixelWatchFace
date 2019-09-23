package com.corvettecole.pixelwatchface;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class PermissionRequestActivity extends Activity {
    private static int PERMISSIONS_CODE = 0;
    String[] mPermissions;
    int mRequestCode;

    @SuppressLint("LongLogTag")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == mRequestCode) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];
                Log.d("PermissionRequestActivity", "" + permission + " " + (grantResult==PackageManager.PERMISSION_GRANTED?"granted":"revoked"));
            }
        }
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPermissions = new String[1];
        mPermissions[0] = this.getIntent().getStringExtra("KEY_PERMISSIONS");
        mRequestCode = this.getIntent().getIntExtra("KEY_REQUEST_CODE", PERMISSIONS_CODE);

        ActivityCompat.requestPermissions(this, mPermissions, mRequestCode);
    }}
