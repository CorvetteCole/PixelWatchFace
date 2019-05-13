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
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

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


    public class CurrentWeather {
        private String mIcon;
        private long mTime;
        private double mTemperature;
        private double mHumidity;
        private double mPrecipChance;
        private String mSummary;
        private String mTimeZone;
        private Bitmap mIconBitmap;
        private String mWeatherProvider;

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



        public Bitmap getIconBitmap(){
            if (mIconBitmap == null){
                mIconBitmap = Bitmap.createScaledBitmap(drawableToBitmap(getDrawable(getIconId())), 34, 34, false);
            }
            return mIconBitmap;
        }

        public int getIconId() {
            //#TODO use custom icons so as to have fitting icons for every weather condition from any provider (see these: http://adamwhitcroft.com/climacons/)
            int iconId = R.drawable.clear_day;
            if (getWeatherProvider().equalsIgnoreCase("DarkSky")) {
                // clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night.
                switch (mIcon) {
                    case "clear-day":
                        iconId = R.drawable.clear_day;
                        break;
                    case "clear-night":
                        iconId = R.drawable.clear_night;
                        break;
                    case "rain":
                        iconId = R.drawable.rain;
                        break;
                    case "snow":
                        iconId = R.drawable.snow;
                        break;
                    case "sleet":
                        iconId = R.drawable.sleet;
                        break;
                    case "wind":
                        iconId = R.drawable.wind;
                        break;
                    case "fog":
                        iconId = R.drawable.fog;
                        break;
                    case "cloudy":
                        iconId = R.drawable.cloudy;
                        break;
                    case "partly-cloudy-day":
                        iconId = R.drawable.partly_cloudy;
                        break;
                    case "partly-cloudy-night":
                        iconId = R.drawable.cloudy_night;
                        break;
                }
            } else if (getWeatherProvider().equalsIgnoreCase("OpenStreetMap")){
                switch (mIcon) {
                    case "01d":
                        iconId = R.drawable.clear_day;
                        break;
                    case "01n":
                        iconId = R.drawable.clear_night;
                        break;
                    case "02d":
                        iconId = R.drawable.partly_cloudy;
                        break;
                    case "02n":
                        iconId = R.drawable.cloudy_night;
                        break;
                    case "03d":
                        iconId = R.drawable.partly_cloudy;
                        break;
                    case "03n":
                        iconId = R.drawable.cloudy_night;
                        break;
                    case "04d":
                        iconId = R.drawable.cloudy;
                        break;
                    case "04n":
                        iconId = R.drawable.cloudy;
                        break;
                    case "09d":
                        iconId = R.drawable.rain;
                        break;
                    case "09n":
                        iconId = R.drawable.rain;
                        break;
                    case "10d":
                        iconId = R.drawable.rain;
                        break;
                    case "10n":
                        iconId = R.drawable.rain;
                        break;
                    case "11d":
                        iconId = R.drawable.rain;
                        break;
                    case "11n":
                        iconId = R.drawable.rain;
                        break;
                    case "13d":
                        iconId = R.drawable.snow;
                        break;
                    case "13n":
                        iconId = R.drawable.snow;
                        break;
                    case "50d":
                        iconId = R.drawable.fog;
                        break;
                    case "50n":
                        iconId = R.drawable.fog;
                        break;
                }
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

        public String getWeatherProvider() {
            return mWeatherProvider;
        }

        public void setWeatherProvider(String mWeatherProvider) {
            this.mWeatherProvider = mWeatherProvider;
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
        private final Bitmap mWearOSBitmap = drawableToBitmap(getDrawable(R.drawable.ic_wear_os_logo));
        private final Bitmap mWearOSBitmapAmbient = drawableToBitmap(getDrawable(R.drawable.ic_wear_os_logo_ambient));
        private boolean mRegisteredTimeZoneReceiver = false;
        private Paint mBackgroundPaint;
        private Paint mTimePaint;
        private Paint mDatePaint;
        private Paint mWeatherPaint;
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

        private boolean mSubscriptionActive;

        SharedPreferences mSharedPreferences;
        private final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1;
        private boolean forceWeatherUpdate = false;

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
            mTimePaint.setStrokeWidth(2f);

            mDatePaint = new Paint();
            mDatePaint.setTypeface(mProductSans);
            mDatePaint.setAntiAlias(true);
            mDatePaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.digital_text));
            mDatePaint.setStrokeWidth(1f);

            mWeatherPaint = new Paint();
            mWeatherPaint.setTypeface(mProductSans);
            mWeatherPaint.setAntiAlias(true);
            mWeatherPaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.digital_text));
            mWeatherPaint.setStrokeWidth(1f);


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
            mWeatherPaint.setTextSize(dateTextSize);


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

            if (inAmbientMode){
                //mTimePaint.setStyle(Paint.Style.STROKE);
                mTimePaint.setColor(Color.GRAY);
                if (mShowInfoBarInAmbient){
                    //mDatePaint.setStyle(Paint.Style.STROKE);
                    mDatePaint.setColor(Color.GRAY);
                    mWeatherPaint.setColor(Color.GRAY);
                }
            } else {
                mTimePaint.setStyle(Paint.Style.FILL);
                mDatePaint.setStyle(Paint.Style.FILL);
                mWeatherPaint.setStyle(Paint.Style.FILL);
                mTimePaint.setColor(Color.WHITE);
                mDatePaint.setColor(Color.WHITE);
                mWeatherPaint.setColor(Color.WHITE);

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

            String mTimeText = String.format("%d:%02d", getHour(mCalendar), mCalendar.get(Calendar.MINUTE));
            float mTimeXOffset = computeXOffset(mTimeText, mTimePaint, bounds);
            float mTimeYOffset = computeTimeYOffset(mTimeText, mTimePaint, bounds);
            canvas.drawText(mTimeText, mTimeXOffset, mTimeYOffset, mTimePaint);

            //draw Date
            String dateText;
            if (mUseEuropeanDateFormat){
                dateText = String.format("%.3s, %d %.3s", android.text.format.DateFormat.format("EEEE", mCalendar), mCalendar.get(Calendar.DAY_OF_MONTH),
                        android.text.format.DateFormat.format("MMMM", mCalendar));
            } else {
                dateText = String.format("%.3s, %.3s %d", android.text.format.DateFormat.format("EEEE", mCalendar),
                        android.text.format.DateFormat.format("MMMM", mCalendar), mCalendar.get(Calendar.DAY_OF_MONTH));
            }

            //TODO : Refactor and cleanup code 
            String temperatureText = "";
            float totalLength;
            float centerX = bounds.exactCenterX();
            float dateTextLength = mDatePaint.measureText(dateText);

            //start new code to draw date
            float infoBarXOffset = centerX - (dateTextLength / 2.0f);
            float infoBarYOffset = computeInfoBarYOffset(dateText, mDatePaint);
            if (mShowInfoBarInAmbient || !mAmbient) {
                canvas.drawText(dateText, infoBarXOffset, mTimeYOffset + infoBarYOffset, mDatePaint);
            }
            //end new code

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
                    totalLength = dateTextLength + bitmapMargin + mLastWeather.getIconBitmap().getWidth() + mDatePaint.measureText(temperatureText);
                } else {
                    totalLength = dateTextLength + bitmapMargin + mDatePaint.measureText(temperatureText);
                }
            } else if (!mShowTemperature && mShowWeather && mLastWeather != null){
                totalLength = dateTextLength + bitmapMargin/2 + mLastWeather.getIconBitmap().getWidth();
            } else {
                totalLength = dateTextLength;
            }

            //comment out old code
            /*
            float infoBarXOffset = centerX - (totalLength / 2.0f);
            float infoBarYOffset = computeInfoBarYOffset(dateText, mDatePaint);

            if (mShowInfoBarInAmbient || !mAmbient) {
                canvas.drawText(dateText, infoBarXOffset, mTimeYOffset + infoBarYOffset, mDatePaint);
                if (mShowWeather && mLastWeather != null) {
                    canvas.drawBitmap(mLastWeather.getIconBitmap(), infoBarXOffset + (dateTextLength + bitmapMargin / 2),
                            mTimeYOffset + infoBarYOffset - mLastWeather.getIconBitmap().getHeight() + 6.0f, null);
                    canvas.drawText(temperatureText, infoBarXOffset + (dateTextLength + bitmapMargin + mLastWeather.getIconBitmap().getWidth()), mTimeYOffset + infoBarYOffset, mDatePaint);
                } else if (!mShowWeather && mShowTemperature && mLastWeather != null) {
                    canvas.drawText(temperatureText, infoBarXOffset + (dateTextLength + bitmapMargin), mTimeYOffset + infoBarYOffset, mDatePaint);
                }
            }
            */


            //new code Draw Weather always
            float weatherTextLength = mWeatherPaint.measureText(temperatureText);
            float infoWeatherXOffSet = centerX - (weatherTextLength / 2.0f);
            float infoWeatherYOffSet = computeInfoBarYOffset(temperatureText, mWeatherPaint);
            if (!mShowWeather && mShowTemperature && mLastWeather != null) {
                canvas.drawText(temperatureText, infoWeatherXOffSet, mTimeYOffset + infoBarYOffset + infoWeatherYOffSet, mWeatherPaint);
            }
            // end new code

            //


            //draw wearOS icon
            if (mAmbient){
                float mIconXOffset = bounds.exactCenterX() - (mWearOSBitmapAmbient.getWidth() / 2.0f);
                float mIconYOffset = mTimeYOffset - mTimeYOffset / 2 - mWearOSBitmapAmbient.getHeight() - 16.0f;
                canvas.drawBitmap(mWearOSBitmapAmbient, mIconXOffset, mIconYOffset, null);
            } else {
                float mIconXOffset = bounds.exactCenterX() - (mWearOSBitmap.getWidth() / 2.0f);
                float mIconYOffset = mTimeYOffset - mTimeYOffset / 2 - mWearOSBitmap.getHeight() - 16.0f;
                canvas.drawBitmap(mWearOSBitmap, mIconXOffset, mIconYOffset, null);
            }

            if (forceWeatherUpdate || (shouldTimerBeRunning() && ((mShowTemperature || mShowWeather) && (mLastWeatherUpdateTime == 0 || (System.currentTimeMillis() - mLastWeatherUpdateTime >= 30 * ONE_MIN && System.currentTimeMillis() - mLastWeatherUpdateFailedTime > 5 * ONE_MIN))))) {
                forceWeatherUpdate = false;

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

        private CurrentWeather getCurrentDetails(String jsonData, Boolean useDarkSky) throws JSONException {
            final String TAG = "getCurrentDetails";
            JSONObject forecast = new JSONObject(jsonData);
            CurrentWeather currentWeather = new CurrentWeather();
            if (useDarkSky) {
                currentWeather.setWeatherProvider("DarkSky");
                String timezone = forecast.getString("timezone");
                JSONObject currently = forecast.getJSONObject("currently");
                currentWeather.setHumidity(currently.getDouble("humidity"));
                currentWeather.setTime(currently.getLong("time"));
                currentWeather.setIcon(currently.getString("icon"));
                currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
                currentWeather.setSummary(currently.getString("summary"));
                currentWeather.setTemperature(currently.getDouble("temperature"));
                currentWeather.setTimeZone(timezone);
                Log.d(TAG, currentWeather.getFormattedTime());
            } else {
                currentWeather.setWeatherProvider("OpenStreetMap");
                String tempIcon = forecast.getJSONArray("weather").toString();
                JSONObject main = forecast.getJSONObject("main");
                currentWeather.setHumidity(main.getDouble("humidity")/100); //adjust OpenStreetMap format to dark sky format with /100
                currentWeather.setTemperature(main.getDouble("temp"));
                Log.d(TAG, tempIcon.substring(tempIcon.indexOf("\"icon\":\"") + 8, tempIcon.indexOf("\"}]")));
                currentWeather.setIcon(tempIcon.substring(tempIcon.indexOf("\"icon\":\"") + 8, tempIcon.indexOf("\"}]")));
            }

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
                mDarkSkyAPIKey = dataMap.getString("dark_sky_api_key");

                mUseEuropeanDateFormat = dataMap.getBoolean("use_european_date");
                mShowTemperatureDecimalPoint = dataMap.getBoolean("show_temperature_decimal");
                mShowInfoBarInAmbient = dataMap.getBoolean("show_infobar_ambient", false);


                boolean useDarkSkyTemp = mUseDarkSky;
                mUseDarkSky = dataMap.getBoolean("use_dark_sky", false);
                if (useDarkSkyTemp != mUseDarkSky){  //detect if weather provider has changed
                    forceWeatherUpdate = true;
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
            editor.putBoolean("use_european_date", mUseEuropeanDateFormat);
            editor.putBoolean("show_temperature_decimal", mShowTemperatureDecimalPoint);
            editor.putBoolean("show_infobar_ambient", mShowInfoBarInAmbient);


            editor.putString("dark_sky_api_key", mDarkSkyAPIKey);
            editor.putBoolean("use_dark_sky", mUseDarkSky);
            editor.apply();
        }

        private void loadPreferences(SharedPreferences sharedPreferences){
            mUse24HourTime = sharedPreferences.getBoolean("use_24_hour_time", false);
            mShowTemperature = sharedPreferences.getBoolean("show_temperature", false);
            mUseCelsius = sharedPreferences.getBoolean("use_celsius", false);
            mShowWeather = sharedPreferences.getBoolean("show_weather", false);

            mShowInfoBarInAmbient = sharedPreferences.getBoolean("show_infobar_ambient", false);

            mUseEuropeanDateFormat = sharedPreferences.getBoolean("use_european_date", false);
            mShowTemperatureDecimalPoint = sharedPreferences.getBoolean("show_temperature_decimal", false);

            mDarkSkyAPIKey = sharedPreferences.getString("dark_sky_api_key", "");
            mUseDarkSky = sharedPreferences.getBoolean("use_dark_sky", false);
        }

        private int getHour(Calendar mCalendar){
            if (mUse24HourTime){
                return mCalendar.get(Calendar.HOUR_OF_DAY);
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
