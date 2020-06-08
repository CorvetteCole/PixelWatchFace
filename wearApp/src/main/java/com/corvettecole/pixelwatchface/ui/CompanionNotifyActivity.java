package com.corvettecole.pixelwatchface.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.phone.PhoneDeviceType;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.wear.widget.ConfirmationOverlay;
import com.corvettecole.pixelwatchface.R;
import com.corvettecole.pixelwatchface.util.Settings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.wearable.intent.RemoteIntent;
import java.util.Set;



public class CompanionNotifyActivity extends WearableActivity implements CapabilityClient.OnCapabilityChangedListener {

  private final String TAG ="CompanionNotifyActivity";

  private Settings mSettings;

  private Node mAndroidPhoneNodeWithApp;

  private TextView mCompanionAppTextView;
  private FloatingActionButton mCompanionAppPositive;
  private FloatingActionButton mCompanionAppNegative;
  private FloatingActionButton mCompanionAppConfirm;

  private static final String CHECKING_MESSAGE = "Checking for companion app...";

  private static final String MISSING_MESSAGE =
          "Looks like the companion app is not installed. This is used to change various settings. Install now?";

  private static final String INSTALLED_MESSAGE = "Companion app installed! Enable weather and change watch face settings on your phone";

  private static final String IOS_MESSAGE = "Companion app is not available on iOS yet. Settings will be coming to the watch face soon!";

  // Name of capability listed in Phone app's wear.xml.
  private static final String CAPABILITY_PHONE_APP = "pixelwatchface_companion_app";

  // Links to install mobile app for both Android (Play Store) and iOS.
  private static final String ANDROID_MARKET_APP_URI =
      "market://details?id=com.corvettecole.pixelwatchface";

  // TODO: Replace with your links/packages.
  private static final String APP_STORE_APP_URI =
      "https://itunes.apple.com/us/app/android-wear/id986496028?mt=8";

  // Result from sending RemoteIntent to phone to open app in play/app store.
  private final ResultReceiver mResultReceiver = new ResultReceiver(new Handler()) {
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

      if (resultCode == RemoteIntent.RESULT_OK) {
        new ConfirmationOverlay().showOn(CompanionNotifyActivity.this);

      } else if (resultCode == RemoteIntent.RESULT_FAILED) {
        new ConfirmationOverlay()
            .setType(ConfirmationOverlay.FAILURE_ANIMATION)
            .showOn(CompanionNotifyActivity.this);

      } else {
        throw new IllegalStateException("Unexpected result " + resultCode);
      }
    }
  };



  @Override
  public void onEnterAmbient(Bundle ambientDetails) {
    Log.d(TAG, "onEnterAmbient");
    mCompanionAppPositive.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
    mCompanionAppConfirm.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
    mCompanionAppNegative.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK));
    super.onEnterAmbient(ambientDetails);
  }

  @Override
  public void onExitAmbient() {
    Log.d(TAG, "onExitAmbient");
    mCompanionAppPositive.setBackgroundTintList(
        ColorStateList.valueOf(getApplicationContext().getColor(R.color.circular_button_normal)));
    mCompanionAppConfirm.setBackgroundTintList(
        ColorStateList.valueOf(getApplicationContext().getColor(R.color.circular_button_normal)));
    mCompanionAppNegative.setBackgroundTintList(
        ColorStateList.valueOf(getApplicationContext().getColor(R.color.circular_button_disabled)));
    super.onExitAmbient();
  }

  @Override
  protected void onPause() {
    Log.d(TAG, "onPause()");
    super.onPause();

    Wearable.getCapabilityClient(this).removeListener(this, CAPABILITY_PHONE_APP);
  }

  @Override
  protected void onResume() {
    Log.d(TAG, "onResume()");
    super.onResume();

    Wearable.getCapabilityClient(this).addListener(this, CAPABILITY_PHONE_APP);

    checkIfPhoneHasApp();
  }

  @Override
  public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {

  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.companion_layout);
    mCompanionAppPositive = findViewById(R.id.companionAppPositive);
    mCompanionAppNegative = findViewById(R.id.companionAppNegative);
    mCompanionAppTextView = findViewById(R.id.companionAppTextView);
    mCompanionAppConfirm = findViewById(R.id.companionAppConfirmation);


    mSettings = Settings.getInstance(getApplicationContext());

    setAmbientEnabled();
    setAutoResumeEnabled(true);



    mCompanionAppPositive.setOnClickListener(v -> {
      mSettings.setCompanionAppNotified(true);
      openAppInStoreOnPhone();

    });

    mCompanionAppConfirm.setOnClickListener(v -> {
      mSettings.setCompanionAppNotified(true);
      finish();
    });

    mCompanionAppNegative.setOnClickListener(v -> {
      mSettings.setCompanionAppNotified(true);
      finish();
    });


  }

  private void checkIfPhoneHasApp() {
    Log.d(TAG, "checkIfPhoneHasApp()");

    Task<CapabilityInfo> capabilityInfoTask = Wearable.getCapabilityClient(this)
        .getCapability(CAPABILITY_PHONE_APP, CapabilityClient.FILTER_ALL);

    capabilityInfoTask.addOnCompleteListener(new OnCompleteListener<CapabilityInfo>() {
      @Override
      public void onComplete(Task<CapabilityInfo> task) {

        if (task.isSuccessful()) {
          Log.d(TAG, "Capability request succeeded.");
          CapabilityInfo capabilityInfo = task.getResult();
          mAndroidPhoneNodeWithApp = pickBestNodeId(capabilityInfo.getNodes());

        } else {
          Log.d(TAG, "Capability request failed to return any results.");
        }

        verifyNodeAndUpdateUI();
      }
    });
  }

  private void verifyNodeAndUpdateUI() {

    if (mAndroidPhoneNodeWithApp != null) {

      String installMessage =
          String.format(INSTALLED_MESSAGE, mAndroidPhoneNodeWithApp.getDisplayName());
      Log.d(TAG, installMessage);
      mCompanionAppTextView.setText(installMessage);
      mCompanionAppPositive.setVisibility(View.INVISIBLE);
      mCompanionAppNegative.setVisibility(View.INVISIBLE);
      mCompanionAppConfirm.setVisibility(View.VISIBLE);

    } else {
      Log.d(TAG, MISSING_MESSAGE);
      mCompanionAppTextView.setText(MISSING_MESSAGE);
      mCompanionAppPositive.setVisibility(View.VISIBLE);
      mCompanionAppNegative.setVisibility(View.VISIBLE);
    }
  }

  private void openAppInStoreOnPhone() {
    Log.d(TAG, "openAppInStoreOnPhone()");

    int phoneDeviceType = PhoneDeviceType.getPhoneDeviceType(getApplicationContext());
    switch (phoneDeviceType) {
      // Paired to Android phone, use Play Store URI.
      case PhoneDeviceType.DEVICE_TYPE_ANDROID:
        Log.d(TAG, "\tDEVICE_TYPE_ANDROID");
        // Create Remote Intent to open Play Store listing of app on remote device.
        Intent intentAndroid =
            new Intent(Intent.ACTION_VIEW)
                .addCategory(Intent.CATEGORY_BROWSABLE)
                .setData(Uri.parse(ANDROID_MARKET_APP_URI));

        RemoteIntent.startRemoteActivity(
            getApplicationContext(),
            intentAndroid,
            mResultReceiver);
        break;

      // Paired to iPhone, use iTunes App Store URI
      case PhoneDeviceType.DEVICE_TYPE_IOS:
        Log.d(TAG, "\tDEVICE_TYPE_IOS");

        // Create Remote Intent to open App Store listing of app on iPhone.
//        Intent intentIOS =
//            new Intent(Intent.ACTION_VIEW)
//                .addCategory(Intent.CATEGORY_BROWSABLE)
//                .setData(Uri.parse(APP_STORE_APP_URI));
//
//        RemoteIntent.startRemoteActivity(
//            getApplicationContext(),
//            intentIOS,
//            mResultReceiver);


        mCompanionAppConfirm.setVisibility(View.VISIBLE);
        mCompanionAppTextView.setText(IOS_MESSAGE);
        break;

      case PhoneDeviceType.DEVICE_TYPE_ERROR_UNKNOWN:
        Log.d(TAG, "\tDEVICE_TYPE_ERROR_UNKNOWN");
        break;
    }
  }

  /*
   * There should only ever be one phone in a node set (much less w/ the correct capability), so
   * I am just grabbing the first one (which should be the only one).
   */
  private Node pickBestNodeId(Set<Node> nodes) {
    Log.d(TAG, "pickBestNodeId(): " + nodes);

    Node bestNodeId = null;
    // Find a nearby node/phone or pick one arbitrarily. Realistically, there is only one phone.
    for (Node node : nodes) {
      bestNodeId = node;
    }
    return bestNodeId;
  }


}