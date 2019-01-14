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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;
import android.widget.Toast;


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
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Digital watch face with seconds. In ambient mode, the seconds aren't displayed. On devices with
 * low-bit ambient mode, the text is drawn without anti-aliasing in ambient mode.
 * <p>
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */
public class PixelWatchFace extends CanvasWatchFaceService {
    /**
     * Update rate in milliseconds for interactive mode. Defaults to one second
     * because the watch face needs to update seconds in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

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


    /**
     * Created by Ken W. Alger on 7 Jan 2015.
     */
    public class CurrentWeather {
        private String mIcon;
        private long mTime;
        private double mTemperature;
        private double mHumidity;
        private double mPrecipChance;
        private String mSummary;
        private String mTimeZone;

        public String getTimeZone() {
            return mTimeZone;
        }

        public void setTimeZone(String timeZone) {
            mTimeZone = timeZone;
        }

        public String getIcon() {
            return mIcon;
        }

        public void setIcon(String icon) {
            mIcon = icon;
        }

        public int getIconId() {
            //#TODO use custom icons (not these)
            // clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night.
            int iconId = R.drawable.clear_day;

            if (mIcon.equals("clear-day")) {
                iconId = R.drawable.clear_day;
            }
            else if (mIcon.equals("clear-night")) {
                iconId = R.drawable.clear_night;
            }
            else if (mIcon.equals("rain")) {
                iconId = R.drawable.rain;
            }
            else if (mIcon.equals("snow")) {
                iconId = R.drawable.snow;
            }
            else if (mIcon.equals("sleet")) {
                iconId = R.drawable.sleet;
            }
            else if (mIcon.equals("wind")) {
                iconId = R.drawable.wind;
            }
            else if (mIcon.equals("fog")) {
                iconId = R.drawable.fog;
            }
            else if (mIcon.equals("cloudy")) {
                iconId = R.drawable.cloudy;
            }
            else if (mIcon.equals("partly-cloudy-day")) {
                iconId = R.drawable.partly_cloudy;
            }
            else if (mIcon.equals("partly-cloudy-night")) {
                iconId = R.drawable.cloudy_night;
            }
            return iconId;
        }

        public long getTime() {
            return mTime;
        }

        public String getFormattedTime() {
            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
            formatter.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
            Date dateTime = new Date(getTime() * 1000);
            String timeString = formatter.format(dateTime);

            return timeString;
        }

        public void setTime(long time) {
            mTime = time;
        }

        public double getTemperature() {
            return mTemperature;
        }

        public void setTemperature(double temperature) {
            mTemperature = temperature;
        }

        public double getHumidity() {
            return mHumidity;
        }

        public void setHumidity(double humidity) {
            mHumidity = humidity;
        }

        public int getPrecipChance() {
            double precipPercentage = mPrecipChance * 100;
            return (int)Math.round(precipPercentage);
        }

        public void setPrecipChance(double precipChance) {
            mPrecipChance = precipChance;
        }

        public String getSummary() {
            return mSummary;
        }

        public void setSummary(String summary) {
            mSummary = summary;
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
        private FusedLocationProviderClient mFusedLocationClient;
        private final Bitmap wearOSBitmap = drawableToBitmap(getDrawable(R.drawable.ic_wear_os_logo));
        private boolean mRegisteredTimeZoneReceiver = false;
        private Paint mBackgroundPaint;
        private Paint mTimePaint;
        private Paint mDatePaint;
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

        private long mLastWeatherUpdateTime = 0;
        private CurrentWeather mLastWeather;
        private final long ONE_MIN = 60000;
        private long mGetLastLocationCalled = 0;

        SharedPreferences mSharedPreferences;
        private final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(PixelWatchFace.this)
                    .build());

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
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
            mDatePaint = new Paint();
            mDatePaint.setTypeface(mProductSans);
            mDatePaint.setAntiAlias(true);
            mDatePaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.digital_text));

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
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                mCalendar.setTimeZone(TimeZone.getDefault());
                invalidate();
            } else {
                unregisterReceiver();
            }

            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            PixelWatchFace.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            PixelWatchFace.this.unregisterReceiver(mTimeZoneReceiver);
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
            mDatePaint.setTextSize(dateTextSize);


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
                mDatePaint.setAntiAlias(!inAmbientMode);
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
            if (isInAmbientMode()) {
                canvas.drawColor(Color.BLACK);
            } else {
                canvas.drawRect(0, 0, bounds.width(), bounds.height(), mBackgroundPaint);
            }

            // Draw H:MM in ambient mode or H:MM:SS in interactive mode.
            long now = System.currentTimeMillis();
            mCalendar.setTimeInMillis(now);

            String mTimeText = String.format("%d:%02d", getHour(mCalendar), mCalendar.get(Calendar.MINUTE));
            float mTimeXOffset = computeXOffset(mTimeText, mTimePaint, bounds);
            float mTimeYOffset = computeTimeYOffset(mTimeText, mTimePaint, bounds);
            canvas.drawText(mTimeText, mTimeXOffset, mTimeYOffset, mTimePaint);

            String dateText;
            if (mShowTemperature && mLastWeather != null){
                if (mUseCelsius) {
                    dateText = String.format("%.3s %.3s %d - %.2f°C", android.text.format.DateFormat.format("EEEE", mCalendar),
                            android.text.format.DateFormat.format("MMMM", mCalendar), mCalendar.get(Calendar.DAY_OF_MONTH), convertToCelsius(mLastWeather.getTemperature()));
                } else {
                    dateText = String.format("%.3s %.3s %d - %.2f°F", android.text.format.DateFormat.format("EEEE", mCalendar),
                            android.text.format.DateFormat.format("MMMM", mCalendar), mCalendar.get(Calendar.DAY_OF_MONTH), mLastWeather.getTemperature());
                }
            } else {
                dateText = String.format("%.3s %.3s %d", android.text.format.DateFormat.format("EEEE", mCalendar),
                        android.text.format.DateFormat.format("MMMM", mCalendar), mCalendar.get(Calendar.DAY_OF_MONTH));
            }
            float dateXOffset = computeXOffset(dateText, mDatePaint, bounds);
            float dateYOffset = computeDateYOffset(dateText, mDatePaint);
            canvas.drawText(dateText, dateXOffset, mTimeYOffset + dateYOffset, mDatePaint);

            //draw wearOS icon
            float mIconXOffset = bounds.exactCenterX() - (wearOSBitmap.getWidth() / 2);
            float mIconYOffset = mTimeYOffset - mTimeYOffset / 2 - wearOSBitmap.getHeight() - 16.0f;
            canvas.drawBitmap(wearOSBitmap, mIconXOffset, mIconYOffset, null);

            if (mShowTemperature && (mLastWeatherUpdateTime == 0 || System.currentTimeMillis() - mLastWeatherUpdateTime >= 30 * ONE_MIN)) {

                mLastWeatherUpdateTime = 1;  //ensures that this code doesn't run every minute if mLastWeatherUpdateTime remains 0. Instead it will run every 30 min like usual.
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                if (System.currentTimeMillis() - mGetLastLocationCalled > ONE_MIN) {
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
                                        getForecast(location.getLatitude(), location.getLongitude());
                                    }
                                }
                            });
                }
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
                    if (item.getUri().getPath().compareTo("/settings") == 0) {
                        Toast.makeText(getApplicationContext(), "companion app changed a setting!", Toast.LENGTH_LONG).show();
                        dataMap = DataMapItem.fromDataItem(item).getDataMap();
                        Log.d(TAG, dataMap.toString());
                        dataMap = dataMap.getDataMap("com.corvettecole.pixelwatchface");
                        Log.d(TAG, dataMap.toString());
                    }
                } else if (event.getType() == DataEvent.TYPE_DELETED) {
                    // DataItem deleted
                }
            }
            updateSettings(dataMap);
        }

        private void getForecast(double latitude, double longitude) {
            final String TAG = "getForecast";
            String apiKey = getString(R.string.dark_sky_api_key);
            String forecastUrl = "https://api.forecast.io/forecast/" +
                    apiKey + "/" + latitude + "," + longitude;


            if (isNetworkAvailable()){

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(forecastUrl)
                        .build();

                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.d(TAG, "Couldn't retrieve weather data");
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        try {
                            String jsonData = response.body().string();
                            Log.v(TAG, jsonData);
                            if (response.isSuccessful()) {
                                try {
                                    mLastWeather = getCurrentDetails(jsonData);
                                    mLastWeatherUpdateTime = System.currentTimeMillis();
                                    invalidate();
                                } catch (JSONException e) {
                                    Log.e(TAG, e.toString());
                                }
                            } else {
                                Log.d(TAG, "Couldn't retrieve weather data: response not successful");
                            }
                        }
                        catch (IOException e) {
                            Log.e(TAG, e.toString());
                        }
                    }
                });
            }

            else {
                Log.d(TAG, "Couldn't retrieve weather data: network not available");
            }
        }

        private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
            final String TAG = "getCurrentDetails";
            JSONObject forecast = new JSONObject(jsonData);
            String timezone = forecast.getString("timezone");
            Log.i(TAG, "timezone: " + timezone);

            JSONObject currently = forecast.getJSONObject("currently");

            CurrentWeather currentWeather = new CurrentWeather();
            currentWeather.setHumidity(currently.getDouble("humidity"));
            currentWeather.setTime(currently.getLong("time"));
            currentWeather.setIcon(currently.getString("icon"));
            currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
            currentWeather.setSummary(currently.getString("summary"));
            currentWeather.setTemperature(currently.getDouble("temperature"));
            currentWeather.setTimeZone(timezone);

            Log.d(TAG, currentWeather.getFormattedTime());

            return currentWeather;
        }

        private double convertToCelsius(double fahrenheit){
            return (fahrenheit - 32)/1.8;
        }

        private boolean isNetworkAvailable() {
            ConnectivityManager manager = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            boolean isAvailable = false;

            if(networkInfo != null && networkInfo.isConnected()) {
                isAvailable = true;
            }
            return isAvailable;
        }

        private void updateSettings(DataMap dataMap) {
            String TAG = "updateSettings";
            try {
                Log.d(TAG, "timestamp: " + dataMap.getLong("timestamp"));
                mUse24HourTime = dataMap.getBoolean("use_24_hour_time");
                mShowTemperature = dataMap.getBoolean("show_temperature");
                mUseCelsius = dataMap.getBoolean("use_celsius");
                mShowWeather = dataMap.getBoolean("show_weather");

                requestPermissions();

                savePreferences(mSharedPreferences);
                invalidate(); //should invalidate the view and force a redraw
            } catch (Exception e){
                Log.e(TAG, "error processing DataMap");
                Log.e(TAG, e.toString());
            }
        }

        private void requestPermissions(){
            if (mShowTemperature && ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED){
                Intent mPermissionRequestIntent = new Intent(getBaseContext(), PermissionRequestActivity.class);
                mPermissionRequestIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (mShowWeather && ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    mPermissionRequestIntent.putExtra("KEY_PERMISSIONS", new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION});
                } else {
                    mPermissionRequestIntent.putExtra("KEY_PERMISSIONS", Manifest.permission.ACCESS_COARSE_LOCATION);
                }
                startActivity(mPermissionRequestIntent);
            }
        }

        private void savePreferences(SharedPreferences sharedPreferences){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("use_24_hour_time", mUse24HourTime);
            editor.putBoolean("show_temperature", mShowTemperature);
            editor.putBoolean("use_celsius", mUseCelsius);
            editor.putBoolean("show_weather", mShowWeather);
            editor.apply();
        }

        private void loadPreferences(SharedPreferences sharedPreferences){
            mUse24HourTime = sharedPreferences.getBoolean("use_24_hour_time", false);
            mShowTemperature = sharedPreferences.getBoolean("show_temperature", false);
            mUseCelsius = sharedPreferences.getBoolean("use_celsius", false);
            mShowWeather = sharedPreferences.getBoolean("show_weather", false);
        }

        private int getHour(Calendar mCalendar){
            if (mUse24HourTime){
                return mCalendar.get(Calendar.HOUR);
            } else {
                int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
                if (hour == 0) {
                    return 12;
                } else if (hour > 12) {
                    return hour - 12;
                } else {
                    return hour;
                }
            }
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
    }

    private float computeXOffset(String text, Paint paint, Rect watchBounds) {
        float centerX = watchBounds.exactCenterX();
        float timeLength = paint.measureText(text);
        return centerX - (timeLength / 2.0f);
    }

    private float computeTimeYOffset(String timeText, Paint timePaint, Rect watchBounds) {
        float centerY = watchBounds.exactCenterY();
        Rect textBounds = new Rect();
        timePaint.getTextBounds(timeText, 0, timeText.length(), textBounds);
        int textHeight = textBounds.height();
        return centerY + (textHeight / 2.0f) - 25.0f; //-XX.Xf is the offset up from the center
    }

    private float computeDateYOffset(String dateText, Paint datePaint) {
        Rect textBounds = new Rect();
        datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
        return textBounds.height() + 14.0f;
    }


    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
