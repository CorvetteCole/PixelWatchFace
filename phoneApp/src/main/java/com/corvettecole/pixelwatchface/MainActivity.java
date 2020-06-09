package com.corvettecole.pixelwatchface;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClient.SkuType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.Purchase.PurchaseState;
import com.android.billingclient.api.Purchase.PurchasesResult;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.corvettecole.pixelwatchface.util.UnitLocale;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
    /*DataClient.OnDataChangedListener*/ BillingClientStateListener, PurchasesUpdatedListener,
    AcknowledgePurchaseResponseListener {

  private SharedPreferences sharedPreferences;
  private Switch use24HourTimeSwitch;
  private Switch showTemperatureSwitch;
  private Switch useCelsiusSwitch;
  private Switch showWeatherSwitch;

  private Switch useEuropeanDateFormatSwitch;
  private Switch showTemperatureDecimalSwitch;
  private Switch useThinAmbientSwitch;
  private Switch showInfoBarAmbientSwitch;
  private Switch showBatterySwitch;
  private Switch showWearIconSwitch;

  private TextView darkSkyTextView;
  private Switch useDarkSkySwitch;
  private EditText darkSkyKeyEditText;

  private TextView advancedTextView;
  private MaterialButton advancedPurchaseButton;
  private MaterialButton advancedFreebieButton;
  private ProgressBar advancedProgressBar;

  private boolean use24HourTime;
  private boolean showTemperature;
  private boolean useCelsius;
  private boolean showWeather;
  private boolean useEuropeanDateFormat;
  private boolean showTemperatureDecimalPoint;
  private String darkSkyAPIKey;
  private boolean useDarkSky;
  private boolean useThinAmbient;
  private boolean showInfoBarAmbient;
  private boolean showBattery;
  private boolean showWearIcon;
  private boolean advanced;
  private boolean firstLaunch;

  private BillingClient billingClient;

  private final int UPDATE_REQUEST_CODE = 1;

  String[] supportOptions = new String[]{"$1", "$3", "$5", "$10"};

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    //Wearable.getDataClient(getApplicationContext()).addListener(this);

    use24HourTimeSwitch = findViewById(R.id.timeFormatSwitch);
    showTemperatureSwitch = findViewById(R.id.temperatureSwitch);
    useCelsiusSwitch = findViewById(R.id.celsiusSwitch);
    showWeatherSwitch = findViewById(R.id.weatherSwitch);

    useEuropeanDateFormatSwitch = findViewById(R.id.dateFormatSwitch);
    showTemperatureDecimalSwitch = findViewById(R.id.temperaturePrecisionSwitch);
    useThinAmbientSwitch = findViewById(R.id.useThinAmbientSwitch);
    showInfoBarAmbientSwitch = findViewById(R.id.infoBarAmbientSwitch);
    showBatterySwitch = findViewById(R.id.batterySwitch);
    showWearIconSwitch = findViewById(R.id.wearIconSwitch);

    useDarkSkySwitch = findViewById(R.id.useDarkSkySwitch);
    darkSkyKeyEditText = findViewById(R.id.darkSkyEditText);
    darkSkyTextView = findViewById(R.id.darkSkyExplanation);

    advancedTextView = findViewById(R.id.advancedPurchaseText);
    advancedPurchaseButton = findViewById(R.id.advancedPurchaseButton);
    advancedFreebieButton = findViewById(R.id.advancedFreebieButton);
    advancedProgressBar = findViewById(R.id.advancedPurchaseLoading);

    loadPreferences();
    if (shouldSuggestSettings()) {
      setSuggestedSettings();
    }

    loadSettingStates();

    updatePurchaseUI(advanced);

//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                        getApplicationContext(),
//                        R.layout.dropdown_menu_popup_menu,
//                        supportOptions);
//        AutoCompleteTextView editTextFilledExposedDropdown =
//                findViewById(R.id.filled_exposed_dropdown);
//        editTextFilledExposedDropdown.setAdapter(adapter);

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
        if (isChecked) {
          Snackbar.make(findViewById(android.R.id.content),
              "Check your watch face for locations prompt", Snackbar.LENGTH_LONG).show();
        }
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
        if (isChecked) {
          Snackbar.make(findViewById(android.R.id.content),
              "Check your watch face for locations prompt", Snackbar.LENGTH_LONG).show();
        }
      }
    });

    useEuropeanDateFormatSwitch
        .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            sharedPreferences.edit().putBoolean("use_european_date", isChecked).apply();
            syncToWear();
          }
        });

    showTemperatureDecimalSwitch
        .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            sharedPreferences.edit().putBoolean("show_temperature_decimal", isChecked).apply();
            syncToWear();
          }
        });

    useThinAmbientSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        sharedPreferences.edit().putBoolean("use_thin_ambient", isChecked).apply();
        syncToWear();
      }
    });

    showInfoBarAmbientSwitch
        .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
            sharedPreferences.edit().putBoolean("show_infobar_ambient", isChecked).apply();
            syncToWear();
          }
        });

    showBatterySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        sharedPreferences.edit().putBoolean("show_battery", isChecked).apply();
        syncToWear();
      }
    });

    showWearIconSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        sharedPreferences.edit().putBoolean("show_wear_icon", isChecked).apply();
        syncToWear();
      }
    });

    if (!darkSkyAPIKey.isEmpty()) {

      useDarkSkySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
          sharedPreferences.edit().putBoolean("use_dark_sky", isChecked).apply();
          syncToWear();
        }
      });

      darkSkyKeyEditText.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
          sharedPreferences.edit()
              .putString("dark_sky_api_key", darkSkyKeyEditText.getText().toString()).apply();
          syncToWear();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
      });
    } else {
      useDarkSkySwitch.setVisibility(View.GONE);
      darkSkyKeyEditText.setVisibility(View.GONE);
      darkSkyTextView.setVisibility(View.GONE);
      if (useDarkSky) {
        useDarkSky = false;
        syncToWear();
      }
    }

    checkForUpdate();

    billingClient = BillingClient.newBuilder(getApplicationContext()).setListener(this).enablePendingPurchases().build();
    billingClient.startConnection(this);


    checkPurchases();

  }

  private void checkForUpdate() {
    // Creates instance of the manager.
    AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());

// Returns an intent object that you use to check for an update.
    com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager
        .getAppUpdateInfo();

// Checks that the platform will allow the specified type of update.
    appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
      if (appUpdateInfo.updateAvailability()
          == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
        // If an in-app update is already running, resume the update.
        try {
          appUpdateManager.startUpdateFlowForResult(
              appUpdateInfo,
              IMMEDIATE,
              this,
              UPDATE_REQUEST_CODE);
        } catch (SendIntentException e) {
          e.printStackTrace();
        }
      } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
          // For a flexible update, use AppUpdateType.FLEXIBLE
          && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
        // Request the update.
        try {
          appUpdateManager
              .startUpdateFlowForResult(appUpdateInfo, IMMEDIATE, this, UPDATE_REQUEST_CODE);
        } catch (SendIntentException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private void setSuggestedSettings() {
    use24HourTime = DateFormat.is24HourFormat(getApplicationContext());
    boolean metric = UnitLocale.getDefault() == UnitLocale.Metric;
    useCelsius = metric;
    useEuropeanDateFormat = metric;
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putBoolean("use_24_hour_time", use24HourTime);
    editor.putBoolean("use_celsius", useCelsius);
    editor.putBoolean("use_european_date", useEuropeanDateFormat).apply();
    syncToWear();
  }

  private boolean shouldSuggestSettings() {
    // if any of the settings are not their initial default values, or this isn't the first time the app was launched
    return showBattery && !use24HourTime && !showTemperature && useCelsius && !showWeather &&
        !useEuropeanDateFormat && !showTemperatureDecimalPoint && !useThinAmbient &&
        showInfoBarAmbient && !showWearIcon && !advanced && firstLaunch;
  }

  private void loadPreferences() {
    use24HourTime = sharedPreferences.getBoolean("use_24_hour_time", false);
    showTemperature = sharedPreferences.getBoolean("show_temperature", false);
    useCelsius = sharedPreferences.getBoolean("use_celsius", true);
    showWeather = sharedPreferences.getBoolean("show_weather", false);
    useEuropeanDateFormat = sharedPreferences.getBoolean("use_european_date", false);
    showTemperatureDecimalPoint = sharedPreferences.getBoolean("show_temperature_decimal", false);
    useThinAmbient = sharedPreferences.getBoolean("use_thin_ambient", false);
    showInfoBarAmbient = sharedPreferences.getBoolean("show_infobar_ambient", true);
    showBattery = sharedPreferences.getBoolean("show_battery", true);
    showWearIcon = sharedPreferences.getBoolean("show_wear_icon", false);

    darkSkyAPIKey = sharedPreferences.getString("dark_sky_api_key", "");
    useDarkSky = sharedPreferences.getBoolean("use_dark_sky", false);

    advanced = sharedPreferences.getBoolean("advanced", false);

    firstLaunch = sharedPreferences.getBoolean("first_launch", true);
  }

  private void syncToWear() {
    //Toast.makeText(this, "something changed, syncing to watch", Toast.LENGTH_SHORT).show();
    Snackbar.make(findViewById(android.R.id.content), "Syncing to watch...", Snackbar.LENGTH_SHORT)
        .show();
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

    putDataMapReq.getDataMap().putLong("timestamp", System.currentTimeMillis());
    putDataMapReq.getDataMap().putBoolean("use_24_hour_time", use24HourTime);
    putDataMapReq.getDataMap().putBoolean("show_temperature", showTemperature);
    putDataMapReq.getDataMap().putBoolean("use_celsius", useCelsius);
    putDataMapReq.getDataMap().putBoolean("show_weather", showWeather);
    putDataMapReq.getDataMap().putBoolean("use_european_date", useEuropeanDateFormat);
    putDataMapReq.getDataMap().putBoolean("show_temperature_decimal", showTemperatureDecimalPoint);
    putDataMapReq.getDataMap().putBoolean("use_thin_ambient", useThinAmbient);
    putDataMapReq.getDataMap().putBoolean("show_infobar_ambient", showInfoBarAmbient);
    putDataMapReq.getDataMap().putString("dark_sky_api_key", darkSkyAPIKey);
    putDataMapReq.getDataMap().putBoolean("use_dark_sky", useDarkSky);
    putDataMapReq.getDataMap().putBoolean("show_battery", showBattery);
    putDataMapReq.getDataMap().putBoolean("show_wear_icon", showWearIcon);
    putDataMapReq.getDataMap().putBoolean("advanced", advanced);

    putDataMapReq.setUrgent();
    Task<DataItem> putDataTask = mDataClient.putDataItem(putDataMapReq.asPutDataRequest());
    if (putDataTask.isSuccessful()) {
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


  private void loadSettingStates() {

    showTemperatureSwitch.setEnabled(advanced);
    showWeatherSwitch.setEnabled(advanced);
    useCelsiusSwitch.setEnabled(advanced);
    showTemperatureDecimalSwitch.setEnabled(advanced);
    useDarkSkySwitch.setEnabled(advanced);
    darkSkyKeyEditText.setEnabled(advanced);

    use24HourTimeSwitch.setChecked(use24HourTime);
    showTemperatureSwitch.setChecked(showTemperature);
    useCelsiusSwitch.setChecked(useCelsius);
    showWeatherSwitch.setChecked(showWeather);
    useEuropeanDateFormatSwitch.setChecked(useEuropeanDateFormat);
    showTemperatureDecimalSwitch.setChecked(showTemperatureDecimalPoint);
    useThinAmbientSwitch.setChecked(useThinAmbient);
    showInfoBarAmbientSwitch.setChecked(showInfoBarAmbient);
    showBatterySwitch.setChecked(showBattery);
    showWearIconSwitch.setChecked(showWearIcon);
    useDarkSkySwitch.setChecked(useDarkSky);
    darkSkyKeyEditText.setText(darkSkyAPIKey);
  }

  private void updateStatus(DataMap dataMap) {
    String TAG = "updateStatus";
    try {
      long timestamp = dataMap.getLong("wear_timestamp");
      boolean mUse24HourTime = dataMap.getBoolean("use_24_hour_time");
      boolean mShowTemperature = dataMap.getBoolean("show_temperature");
      boolean mUseCelsius = dataMap.getBoolean("use_celsius");
      boolean mShowWeather = dataMap.getBoolean("show_weather");

      Calendar calendar = Calendar.getInstance();
      calendar.setTimeInMillis(timestamp);


    } catch (Exception e) {
      Log.e(TAG, "error processing DataMap");
      Log.e(TAG, e.toString());
    }
  }

//    @Override
//    public void onDataChanged(DataEventBuffer dataEvents) {
//        String TAG = "onDataChanged";
//        Log.d(TAG, "Data changed");
//        DataMap dataMap = new DataMap();
//        for (DataEvent event : dataEvents) {
//            if (event.getType() == DataEvent.TYPE_CHANGED) {
//                // DataItem changed
//                DataItem item = event.getDataItem();
//                Log.d(TAG, "DataItem uri: " + item.getUri());
//                if (item.getUri().getPath().compareTo("/watch_status") == 0) {
//                    Log.d(TAG, "Companion app changed a setting!");
//                    dataMap = DataMapItem.fromDataItem(item).getDataMap();
//                    Log.d(TAG, dataMap.toString());
//                    dataMap = dataMap.getDataMap("com.corvettecole.pixelwatchface");
//                    Log.d(TAG, dataMap.toString());
//                }
//            } else if (event.getType() == DataEvent.TYPE_DELETED) {
//                // DataItem deleted
//            }
//        }
//        updateStatus(dataMap);
//    }

  @Override
  public void onDestroy() {
    //Wearable.getDataClient(getApplicationContext()).removeListener(this);

    super.onDestroy();
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override
  public void onBillingSetupFinished(BillingResult billingResult) {
    if (billingResult.getResponseCode() == BillingResponseCode.OK) {
      if (!advanced) {
        advancedTextView.setText(getApplicationContext().getText(R.string.purchase_loading));
      }
      List<String> skuList = new ArrayList<>();
      skuList.add("unlock_weather");
      SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
      params.setSkusList(skuList).setType(SkuType.INAPP);
      billingClient.querySkuDetailsAsync(params.build(),
          new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult,
                List<SkuDetails> skuDetailsList) {
              if (billingResult.getResponseCode() == BillingResponseCode.OK
                  && skuDetailsList != null) {
                // Process the result.
                for (SkuDetails skuDetails : skuDetailsList) {
                  String sku = skuDetails.getSku();
                  if ("unlock_weather".equals(sku)) {
                    if (!advanced) {
                      advancedTextView
                          .setText(getApplicationContext().getText(R.string.purchase_prompt));
                      advancedProgressBar.setVisibility(View.GONE);
                      advancedPurchaseButton.setVisibility(View.VISIBLE);
                      advancedFreebieButton.setVisibility(View.VISIBLE);
                    }
                    advancedPurchaseButton.setText(String
                        .format(getApplicationContext().getString(R.string.purchase_button),
                            skuDetails.getPrice()));

                    advancedPurchaseButton.setOnClickListener(v -> {
                      Log.d("test", "button pressed");
                      BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                          .setSkuDetails(skuDetails)
                          .build();
                      BillingResult billingResult1 = billingClient
                          .launchBillingFlow(MainActivity.this, flowParams);
                      if (billingResult1.getResponseCode()
                          == BillingResponseCode.ITEM_ALREADY_OWNED) {
                        advancedPurchaseButton.setText(R.string.purchase_button_pending);
                        checkPurchases();
                      }
                    });

                    advancedFreebieButton.setOnClickListener(v -> {
                      Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                      intent
                          .putExtra(Intent.EXTRA_SUBJECT, "Unlock Code Request - Pixel Watch Face");
                      intent.setData(Uri.parse(
                          "mailto:support@corvettecole.com")); // or just "mailto:" for blank
                      intent.addFlags(
                          Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                      startActivity(intent);
                    });


                  }
                }
              }
            }
          });
    }
  }

  private void updatePurchaseUI(boolean isPurchased) {
    if (isPurchased) {
      advancedProgressBar.setVisibility(View.GONE);
      advancedPurchaseButton.setVisibility(View.GONE);
      advancedFreebieButton.setVisibility(View.GONE);
      advancedTextView.setText(getApplicationContext().getText(R.string.purchase_purchased_thanks));
    } else {
      advancedProgressBar.setVisibility(View.VISIBLE);
      advancedPurchaseButton.setVisibility(View.GONE);
      advancedFreebieButton.setVisibility(View.GONE);
      //advancedTextView.setText(getApplicationContext().getText(R.string.purchase_prompt));
    }
  }

  private void checkPurchases() {
    PurchasesResult purchasesResult = billingClient.queryPurchases(SkuType.INAPP);
    if (purchasesResult.getBillingResult().getResponseCode() == BillingResponseCode.OK) {
      boolean advancedPurchasePresent = false;
      for (Purchase purchase : purchasesResult.getPurchasesList()) {
        handlePurchase(purchase);
        if (purchase.getSku().equals("unlock_weather")){
          advancedPurchasePresent = true;
        }
      }
      if (!advancedPurchasePresent && advanced){
        revokeAdvancedPurchase();
      }
    }
  }

  private void revokeAdvancedPurchase(){
    advanced = false;
    sharedPreferences.edit()
        .putBoolean("advanced", advanced).apply();
    loadSettingStates();
    syncToWear();
    updatePurchaseUI(false);
    advancedTextView.setText(getApplicationContext().getText(R.string.purchase_prompt));
  }

  private void handlePurchase(Purchase purchase) {
    if (purchase.getSku().equals("unlock_weather")) {
      if (purchase.getPurchaseState() == PurchaseState.PURCHASED) {

        // Grant entitlement to the user.
        advanced = true;
        sharedPreferences.edit()
            .putBoolean("advanced", advanced).apply();
        loadSettingStates();
        syncToWear();
        updatePurchaseUI(true);

        // Acknowledge the purchase if it hasn't already been acknowledged.
        if (!purchase.isAcknowledged()) {
          AcknowledgePurchaseParams acknowledgePurchaseParams =
              AcknowledgePurchaseParams.newBuilder()
                  .setPurchaseToken(purchase.getPurchaseToken())
                  .build();
          billingClient.acknowledgePurchase(acknowledgePurchaseParams, this);
        }
      } else if (purchase.getPurchaseState() == PurchaseState.PENDING){
        Snackbar.make(findViewById(android.R.id.content), "Purchase pending... weather will unlock when purchase is complete", Snackbar.LENGTH_LONG)
            .show();
      } else if (advanced){
        // advanced mode should no longer be active
       revokeAdvancedPurchase();

      }

    }
  }


  @Override
  public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
    if (billingResult.getResponseCode() == BillingResponseCode.OK
        && purchases != null) {
      for (Purchase purchase : purchases) {
        handlePurchase(purchase);
      }
    } else if (billingResult.getResponseCode() == BillingResponseCode.USER_CANCELED) {
      Snackbar.make(findViewById(android.R.id.content), "Purchase cancelled", Snackbar.LENGTH_SHORT)
          .show();
    } else {
      // Handle any other error codes.
    }
  }

  @Override
  public void onBillingServiceDisconnected() {
    billingClient.startConnection(this);
  }

  @Override
  public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
    if (billingResult.getResponseCode() != BillingResponseCode.OK) {
      Log.e("acknowledgePurchase", "Error acknowledging purchase, this shouldn't happen");
    }
  }
}
