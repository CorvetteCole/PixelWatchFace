package com.corvettecole.pixelwatchface;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import static com.corvettecole.pixelwatchface.Utils.convertToCelsius;
import static com.corvettecole.pixelwatchface.Utils.drawableToBitmap;
import static com.corvettecole.pixelwatchface.Utils.getCurrentDetails;
import static com.corvettecole.pixelwatchface.Utils.getHour;
import static com.corvettecole.pixelwatchface.Utils.isNetworkAvailable;

/**
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */
public class PixelWatchFace extends CanvasWatchFaceService {
    /**
     * Update rate in milliseconds for interactive mode. Defaults to one minute
     * because the watch face needs to update minutes in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.MINUTES.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private static class EngineHandler extends Handler {
        private final WeakReference<PixelWatchFace.Engine> mWeakReference;

        public EngineHandler(PixelWatchFace.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            PixelWatchFace.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    public static class PermissionRequestActivity extends Activity {
        private static int PERMISSIONS_CODE = 0;
        String[] mPermissions;
        int mRequestCode;

        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
            final String TAG = "PermissionRequest";
            if (requestCode == mRequestCode) {
                for (int i = 0; i < permissions.length; i++) {
                    String permission = permissions[i];
                    int grantResult = grantResults[i];
                    Log.d(TAG, "" + permission + " " + (grantResult == PackageManager.PERMISSION_GRANTED ? "granted" : "revoked"));
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
        }
    }

    private class Engine extends CanvasWatchFaceService.Engine implements DataClient.OnDataChangedListener {

        private final Handler mUpdateTimeHandler = new EngineHandler(this);
        private Calendar mCalendar;
        private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            }
        };
        private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mBatteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                invalidate();
            }
        };

        private FusedLocationProviderClient mFusedLocationClient;
        private final Bitmap mWearOSBitmap = drawableToBitmap(getDrawable(R.drawable.ic_wear_os_logo));
        private final Bitmap mWearOSBitmapAmbient = drawableToBitmap(getDrawable(R.drawable.ic_wear_os_logo_ambient));
        private boolean mRegisteredTimeZoneReceiver = false;
        private boolean mRegisteredBatteryReceiver = false;
        private Paint mBackgroundPaint;
        private Paint mTimePaint;
        private Paint mInfoPaint;
        private int mBatteryLevel;
        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        private boolean mLowBitAmbient;
        private boolean mBurnInProtection;
        private boolean mAmbient;

        private float mIconTitleYOffset;

        private Typeface mProductSans;

        //settings
        private boolean mUse24HourTime;
        private boolean mShowTemperature;
        private boolean mUseCelsius;
        private boolean mShowWeather;
        private boolean mUseEuropeanDateFormat;
        private boolean mShowTemperatureDecimalPoint;
        private boolean mShowInfoBarInAmbient;

        private long mLastWeatherUpdateTime = 0;
        private long mLastWeatherUpdateFailedTime = 0;
        private CurrentWeather mLastWeather;
        private final long ONE_MIN = 60000;
        private long mGetLastLocationCalled = 0;

        private String mDarkSkyAPIKey;
        private boolean mUseDarkSky;

        private boolean mShowBattery;

        private boolean mSubscriptionActive;

        SharedPreferences mSharedPreferences;
        private final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1;
        private boolean mForceWeatherUpdate = false;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(PixelWatchFace.this)
                    .build());

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(PixelWatchFace.this);
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            mCalendar = Calendar.getInstance();
            //Resources resources = PixelWatchFace.this.getResources();

            // Initializes syncing with companion app
            Wearable.getDataClient(getApplicationContext()).addListener(this);

            // Initializes background.
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.background));
            mProductSans = ResourcesCompat.getFont(getApplicationContext(), R.font.product_sans_regular);

            // Initializes Watch Face.
            mTimePaint = new Paint();
            mTimePaint.setTypeface(mProductSans);
            mTimePaint.setAntiAlias(true);
            mTimePaint.setColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.digital_text));
            mTimePaint.setStrokeWidth(3f);

            mInfoPaint = new Paint();
            mInfoPaint.setTypeface(mProductSans);
            mInfoPaint.setAntiAlias(true);
            mInfoPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.digital_text));
            mInfoPaint.setStrokeWidth(2f);

            // Loads locally saved settings values
            loadPreferences(mSharedPreferences);
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            Wearable.getDataClient(getApplicationContext()).removeListener(this);
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);

            if (visible) {
                registerReceivers();

                // Update time zone in case it changed while we weren't visible.
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else {
                unregisterReceivers();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceivers() {
            if (mRegisteredBatteryReceiver && mRegisteredTimeZoneReceiver) {
                return;
            }
            if (!mRegisteredBatteryReceiver) {
                mRegisteredBatteryReceiver = true;
                IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                PixelWatchFace.this.registerReceiver(mBatteryReceiver, filter);
            }
            if (!mRegisteredTimeZoneReceiver) {
                mRegisteredTimeZoneReceiver = true;
                IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
                PixelWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
            }
        }

        private void unregisterReceivers() {
            if (!mRegisteredBatteryReceiver && !mRegisteredTimeZoneReceiver) {
                return;
            }
            if (mRegisteredTimeZoneReceiver) {
                mRegisteredTimeZoneReceiver = false;
                PixelWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
            }
            if (mRegisteredBatteryReceiver) {
                mRegisteredBatteryReceiver = false;
                PixelWatchFace.this.unregisterReceiver(mBatteryReceiver);
            }
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);

            // Load resources that have alternate values for round watches.
            Resources resources = PixelWatchFace.this.getResources();
            boolean isRound = insets.isRound();
            float timeTextSize = resources.getDimension(isRound
                    ? R.dimen.digital_time_text_size_round : R.dimen.digital_time_text_size);
            float dateTextSize = resources.getDimension(isRound
                    ? R.dimen.digital_date_text_size_round : R.dimen.digital_date_text_size);

            mTimePaint.setTextSize(timeTextSize);
            mInfoPaint.setTextSize(dateTextSize);


        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            mBurnInProtection = properties.getBoolean(PROPERTY_BURN_IN_PROTECTION, false);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);

            mAmbient = inAmbientMode;
            if (mLowBitAmbient) {
                mTimePaint.setAntiAlias(!inAmbientMode);
                mInfoPaint.setAntiAlias(!inAmbientMode);
            }

            if (inAmbientMode){
                mTimePaint.setStyle(Paint.Style.STROKE);
                if (mShowInfoBarInAmbient){
                    //TODO: change date between the pixel ambient gray and white instead of making it stroked
                    mInfoPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.digital_text_ambient));
                }
            } else {
                mTimePaint.setStyle(Paint.Style.FILL);
                mInfoPaint.setStyle(Paint.Style.FILL);
                mInfoPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.digital_text));

            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }


        @SuppressLint("DefaultLocale")
        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            final String TAG = "onDraw";

            // Draw the background.
            canvas.drawColor(Color.BLACK);
            // canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint); does some thing


            // Draw H:MM
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);

            // pad hour with 0 or not depending on if 24 hour time is being used
            String mTimeText = "";
            if (mUse24HourTime){
                mTimeText = String.format("%02d:%02d", getHour(mCalendar, mUse24HourTime), mCalendar.get(Calendar.MINUTE));
            } else {
                mTimeText = String.format("%d:%02d", getHour(mCalendar, mUse24HourTime), mCalendar.get(Calendar.MINUTE));
            }

            float mTimeXOffset = computeXOffset(mTimeText, mTimePaint, bounds);
            float timeYOffset = computeTimeYOffset(mTimeText, mTimePaint, bounds);
            canvas.drawText(mTimeText, mTimeXOffset, timeYOffset, mTimePaint);
            String dateText;
            if (mUseEuropeanDateFormat){
                dateText = String.format("%.3s, %d %.3s", android.text.format.DateFormat.format("EEEE", mCalendar), mCalendar.get(Calendar.DAY_OF_MONTH),
                        android.text.format.DateFormat.format("MMMM", mCalendar));
            } else {
                dateText = String.format("%.3s, %.3s %d", android.text.format.DateFormat.format("EEEE", mCalendar),
                        android.text.format.DateFormat.format("MMMM", mCalendar), mCalendar.get(Calendar.DAY_OF_MONTH));
            }

            String temperatureText = "";
            float totalLength;
            float centerX = bounds.exactCenterX();
            float dateTextLength = mInfoPaint.measureText(dateText);

            float bitmapMargin = 20.0f;
            if (mShowTemperature && mLastWeather != null){
                    if (mUseCelsius) {
                        if (mShowTemperatureDecimalPoint){
                            temperatureText = String.format("%.1f °C", convertToCelsius(mLastWeather.getTemperature()));
                        } else {
                            temperatureText = String.format("%d °C", Math.round(convertToCelsius(mLastWeather.getTemperature())));
                        }
                    } else {
                        if (mShowTemperatureDecimalPoint){
                            temperatureText = String.format("%.1f °F", mLastWeather.getTemperature());
                        } else {
                            temperatureText = String.format("%d °F", Math.round(mLastWeather.getTemperature()));
                        }
                    }
                    if (mShowWeather){
                        totalLength = dateTextLength + bitmapMargin + mLastWeather.getIconBitmap(getApplicationContext()).getWidth() + mInfoPaint.measureText(temperatureText);
                    } else {
                        totalLength = dateTextLength + bitmapMargin + mInfoPaint.measureText(temperatureText);
                    }
            } else if (!mShowTemperature && mShowWeather && mLastWeather != null){
                totalLength = dateTextLength + bitmapMargin/2 + mLastWeather.getIconBitmap(getApplicationContext()).getWidth();
            } else {
                totalLength = dateTextLength;
            }

            float infoBarXOffset = centerX - (totalLength / 2.0f);
            float infoBarYOffset = computeInfoBarYOffset(dateText, mInfoPaint);

            // draw infobar
            if (mShowInfoBarInAmbient || !mAmbient) {



                canvas.drawText(dateText, infoBarXOffset, timeYOffset + infoBarYOffset, mInfoPaint);
                if (mShowWeather && mLastWeather != null) {
                    canvas.drawBitmap(mLastWeather.getIconBitmap(getApplicationContext()), infoBarXOffset + (dateTextLength + bitmapMargin / 2),
                            timeYOffset + infoBarYOffset - mLastWeather.getIconBitmap(getApplicationContext()).getHeight() + 6.0f, null);
                    canvas.drawText(temperatureText, infoBarXOffset + (dateTextLength + bitmapMargin + mLastWeather.getIconBitmap(getApplicationContext()).getWidth()), timeYOffset + infoBarYOffset, mInfoPaint);
                } else if (!mShowWeather && mShowTemperature && mLastWeather != null) {
                    canvas.drawText(temperatureText, infoBarXOffset + (dateTextLength + bitmapMargin), timeYOffset + infoBarYOffset, mInfoPaint);
                }
            }

            // draw battery percentage
            if (mShowBattery){
                String battery = String.format("%d%%", mBatteryLevel);
                float batteryXOffset = computeXOffset(battery, mInfoPaint, bounds);
                float batteryYOffset = computerBatteryYOffset(battery, mInfoPaint, bounds);

                canvas.drawText(battery, batteryXOffset, batteryYOffset, mInfoPaint);
            }

            // draw wearOS icon
            if (mAmbient){
                float mIconXOffset = bounds.exactCenterX() - (mWearOSBitmapAmbient.getWidth() / 2.0f);
                float mIconYOffset = timeYOffset - timeYOffset / 2 - mWearOSBitmapAmbient.getHeight() - 16.0f;
                canvas.drawBitmap(mWearOSBitmapAmbient, mIconXOffset, mIconYOffset, null);
            } else {
                float mIconXOffset = bounds.exactCenterX() - (mWearOSBitmap.getWidth() / 2.0f);
                float mIconYOffset = timeYOffset - timeYOffset / 2 - mWearOSBitmap.getHeight() - 16.0f;
                canvas.drawBitmap(mWearOSBitmap, mIconXOffset, mIconYOffset, null);
            }

            if (mForceWeatherUpdate || (shouldTimerBeRunning() && ((mShowTemperature || mShowWeather) && (mLastWeatherUpdateTime == 0 || (System.currentTimeMillis() - mLastWeatherUpdateTime >= 30 * ONE_MIN && System.currentTimeMillis() - mLastWeatherUpdateFailedTime > 5 * ONE_MIN))))) {
                mForceWeatherUpdate = false;

                mLastWeatherUpdateTime = 1;  //ensures that this code doesn't run every minute if mLastWeatherUpdateTime remains 0. Instead it will run every 30 min like usual.
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions();
                }

                mGetLastLocationCalled = System.currentTimeMillis();
                Log.d(TAG, "calling getLastLocation()");
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    Log.d(TAG, "called getForecast(" + location.getLatitude() + "," + location.getLongitude() + ")");
                                    getForecast(location.getLatitude(), location.getLongitude(), mUseDarkSky);
                                } else {
                                    mLastWeatherUpdateFailedTime = System.currentTimeMillis();
                                }
                            }
                        });
            }
        }




        private float computeXOffset(String text, Paint paint, Rect watchBounds) {
            float centerX = watchBounds.exactCenterX();
            float textLength = paint.measureText(text);
            return centerX - (textLength / 2.0f);
        }

        private float computeTimeYOffset(String timeText, Paint timePaint, Rect watchBounds) {
            float centerY = watchBounds.exactCenterY();
            Rect textBounds = new Rect();
            timePaint.getTextBounds(timeText, 0, timeText.length(), textBounds);
            int textHeight = textBounds.height();
            return centerY + (textHeight / 2.0f) - 25.0f; //-XX.Xf is the offset up from the center
        }

        private float computeInfoBarYOffset(String dateText, Paint datePaint) {
            Rect textBounds = new Rect();
            datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
            return textBounds.height() + 27.0f;
        }

        private float computerBatteryYOffset(String batteryText, Paint batteryPaint, Rect watchBounds) {
            Rect textBounds = new Rect();
            batteryPaint.getTextBounds(batteryText, 0, batteryText.length(), textBounds);
            return watchBounds.bottom - textBounds.height() * 1.5f/* / 2.0f*/;
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
                    if (item.getUri().getPath().compareTo("/settings") == 0) {
                        Log.d(TAG, "Companion app changed a setting!");
                        dataMap = DataMapItem.fromDataItem(item).getDataMap();
                        Log.d(TAG, dataMap.toString());
                        dataMap = dataMap.getDataMap("com.corvettecole.pixelwatchface");
                        Log.d(TAG, dataMap.toString());
                        updateSettings(dataMap);
                        //syncToPhone();
                    }
                } else if (event.getType() == DataEvent.TYPE_DELETED) {
                    // DataItem deleted
                }
            }
        }

        private void getForecast(double latitude, double longitude, boolean useDarkSky) {
            final String TAG = "getForecast";
            String apiKey = getString(R.string.openstreetmap_api_key);
            String forecastUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=imperial&appid=" + apiKey;

            if (useDarkSky) {
                if (mDarkSkyAPIKey != null) {
                    apiKey = mDarkSkyAPIKey;
                }
                forecastUrl = "https://api.forecast.io/forecast/" +
                        apiKey + "/" + latitude + "," + longitude + "?lang=" + Locale.getDefault().getLanguage();
                Log.d(TAG, "forecastURL: " + "https://api.forecast.io/forecast/" +
                        apiKey + "/" + latitude + "," + longitude + "?lang=" + Locale.getDefault().getLanguage());
            }


            if (isNetworkAvailable(getApplicationContext())){

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(forecastUrl)
                        .build();

                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.d(TAG, "Couldn't retrieve weather data");
                        mLastWeatherUpdateFailedTime = System.currentTimeMillis();
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        try {
                            String jsonData = response.body().string();
                            Log.v(TAG, jsonData);
                            if (response.isSuccessful()) {
                                try {
                                    mLastWeather = getCurrentDetails(jsonData, mUseDarkSky);
                                    mLastWeatherUpdateTime = System.currentTimeMillis();
                                    invalidate();
                                } catch (JSONException e) {
                                    Log.e(TAG, e.toString());
                                    mLastWeatherUpdateFailedTime = System.currentTimeMillis();
                                }
                            } else {
                                Log.d(TAG, "Couldn't retrieve weather data: response not successful");
                                mLastWeatherUpdateFailedTime = System.currentTimeMillis();
                            }
                        }
                        catch (IOException e) {
                            Log.e(TAG, e.toString());
                            mLastWeatherUpdateFailedTime = System.currentTimeMillis();
                        }
                    }
                });
            }

            else {
                Log.d(TAG, "Couldn't retrieve weather data: network not available");
            }
        }

        private void updateSettings(DataMap dataMap) {
            String TAG = "updateSettings";
            boolean showTemperature = mShowTemperature;
            boolean showWeather = mShowWeather;


            try {
                Log.d(TAG, "timestamp: " + dataMap.getLong("timestamp"));
                mUse24HourTime = dataMap.getBoolean("use_24_hour_time");
                mShowTemperature = dataMap.getBoolean("show_temperature");
                mUseCelsius = dataMap.getBoolean("use_celsius");
                mShowWeather = dataMap.getBoolean("show_weather");
                mDarkSkyAPIKey = dataMap.getString("dark_sky_api_key");

                mUseEuropeanDateFormat = dataMap.getBoolean("use_european_date");
                mShowTemperatureDecimalPoint = dataMap.getBoolean("show_temperature_decimal");
                mShowInfoBarInAmbient = dataMap.getBoolean("show_infobar_ambient", false);

                mShowBattery = dataMap.getBoolean("show_battery", true);

                boolean useDarkSkyTemp = mUseDarkSky;
                mUseDarkSky = dataMap.getBoolean("use_dark_sky", false);
                if (useDarkSkyTemp != mUseDarkSky || showTemperature != mShowTemperature || showWeather != mShowWeather){  //detect if weather provider has changed
                    mForceWeatherUpdate = true;
                }

                requestPermissions();

                savePreferences(mSharedPreferences);
                invalidate(); //should invalidate the view and force a redraw
            } catch (Exception e){
                Log.e(TAG, "error processing DataMap");
                Log.e(TAG, e.toString());
            }
        }

        private void requestPermissions(){
            if ((mShowWeather || mShowTemperature) && ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                Intent mPermissionRequestIntent = new Intent(getBaseContext(), PermissionRequestActivity.class);
                mPermissionRequestIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mPermissionRequestIntent.putExtra("KEY_PERMISSIONS", new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
                startActivity(mPermissionRequestIntent);
            }
        }

        private void savePreferences(SharedPreferences sharedPreferences){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("use_24_hour_time", mUse24HourTime);
            editor.putBoolean("show_temperature", mShowTemperature);
            editor.putBoolean("use_celsius", mUseCelsius);
            editor.putBoolean("show_weather", mShowWeather);
            editor.putBoolean("use_european_date", mUseEuropeanDateFormat);
            editor.putBoolean("show_temperature_decimal", mShowTemperatureDecimalPoint);
            editor.putBoolean("show_infobar_ambient", mShowInfoBarInAmbient);
            editor.putBoolean("show_battery", mShowBattery);

            editor.putString("dark_sky_api_key", mDarkSkyAPIKey);
            editor.putBoolean("use_dark_sky", mUseDarkSky);
            editor.apply();
        }

        private void loadPreferences(SharedPreferences sharedPreferences){
            mUse24HourTime = sharedPreferences.getBoolean("use_24_hour_time", false);
            mShowTemperature = sharedPreferences.getBoolean("show_temperature", false);
            mUseCelsius = sharedPreferences.getBoolean("use_celsius", false);
            mShowWeather = sharedPreferences.getBoolean("show_weather", false);

            mShowInfoBarInAmbient = true;
            //mShowInfoBarInAmbient = sharedPreferences.getBoolean("show_infobar_ambient", true);

            mUseEuropeanDateFormat = sharedPreferences.getBoolean("use_european_date", false);
            mShowTemperatureDecimalPoint = sharedPreferences.getBoolean("show_temperature_decimal", false);

            mDarkSkyAPIKey = sharedPreferences.getString("dark_sky_api_key", "");
            mUseDarkSky = sharedPreferences.getBoolean("use_dark_sky", false);

            mShowBattery = sharedPreferences.getBoolean("show_battery", true);
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

        // Class for debugging
        /*
        private void syncToPhone(){
            String TAG = "syncToPhone";
            DataClient mDataClient = Wearable.getDataClient(getApplicationContext());
            PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/watch_status");

            DataMap dataMap = new DataMap();
            dataMap.putLong("wear_timestamp", System.currentTimeMillis());
            dataMap.putBoolean("use_24_hour_time", mUse24HourTime);
            dataMap.putBoolean("show_temperature", mShowTemperature);
            dataMap.putBoolean("use_celsius", mUseCelsius);
            dataMap.putBoolean("show_weather", mShowWeather);

            putDataMapReq.getDataMap().putDataMap("com.corvettecole.pixelwatchface", dataMap);
            PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
            putDataReq.setUrgent();
            Task<DataItem> putDataTask = mDataClient.putDataItem(putDataReq);
            if (putDataTask.isSuccessful()){
                Log.d(TAG, "Current stats synced to phone");
            }
        }
        */

    }
}
