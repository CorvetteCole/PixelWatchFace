package com.corvettecole.pixelwatchface;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;
import android.widget.Toast;


import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
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
public class PixelWatchFace extends CanvasWatchFaceService  {
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

        private Typeface productSans;

        //settings
        private boolean use24HourTime;
        private boolean weatherEnabled;
        private boolean useCelsius;
        private boolean showWeatherIcon;

        SharedPreferences sharedPreferences;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(PixelWatchFace.this)
                    .build());

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            mCalendar = Calendar.getInstance();
            //Resources resources = PixelWatchFace.this.getResources();

            // Initializes syncing with companion app
            Wearable.getDataClient(getApplicationContext()).addListener(this);

            // Initializes background.
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.background));
            productSans = ResourcesCompat.getFont(getApplicationContext(), R.font.product_sans_regular);

            // Initializes Watch Face.
            mTimePaint = new Paint();
            mTimePaint.setTypeface(productSans);
            mTimePaint.setAntiAlias(true);
            mTimePaint.setColor(
                    ContextCompat.getColor(getApplicationContext(), R.color.digital_text));
            mDatePaint = new Paint();
            mDatePaint.setTypeface(productSans);
            mDatePaint.setAntiAlias(true);
            mDatePaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.digital_text));

            // Loads locally saved settings values
            loadPreferences(sharedPreferences);
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

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
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

            String dateText = String.format("%.3s %.3s %d", android.text.format.DateFormat.format("EEEE", mCalendar), android.text.format.DateFormat.format("MMMM", mCalendar), mCalendar.get(Calendar.DAY_OF_MONTH));
            float dateXOffset = computeXOffset(dateText, mDatePaint, bounds);
            float dateYOffset = computeDateYOffset(dateText, mDatePaint);
            canvas.drawText(dateText, dateXOffset, mTimeYOffset + dateYOffset, mDatePaint);

            //draw wearOS icon
            float mIconXOffset = bounds.exactCenterX() - (wearOSBitmap.getWidth() / 2);
            float mIconYOffset = mTimeYOffset - mTimeYOffset / 2  - wearOSBitmap.getHeight() - 16.0f;
            canvas.drawBitmap(wearOSBitmap, mIconXOffset, mIconYOffset, null);
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

        private void updateSettings(DataMap dataMap) {
            String TAG = "updateSettings";
            try {
                Log.d(TAG, "timestamp: " + dataMap.getLong("timestamp"));
                use24HourTime = dataMap.getBoolean("use_24_hour_time");
                weatherEnabled = dataMap.getBoolean("weather_enabled");
                useCelsius = dataMap.getBoolean("use_celsius");
                showWeatherIcon = dataMap.getBoolean("show_weather_icon");
                savePreferences(sharedPreferences);
                invalidate(); //should invalidate the view and force a redraw
            } catch (Exception e){
                Log.e(TAG, "error processing DataMap");
                Log.e(TAG, e.toString());
            }
        }

        private void savePreferences(SharedPreferences sharedPreferences){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("use_24_hour_time", use24HourTime);
            editor.putBoolean("weather_enabled", weatherEnabled);
            editor.putBoolean("use_celsius", useCelsius);
            editor.putBoolean("show_weather_icon", showWeatherIcon);
            editor.apply();
        }

        private void loadPreferences(SharedPreferences sharedPreferences){
            use24HourTime = sharedPreferences.getBoolean("use_24_hour_time", false);
            weatherEnabled = sharedPreferences.getBoolean("weather_enabled", false);
            useCelsius = sharedPreferences.getBoolean("use_celsius", false);
            showWeatherIcon = sharedPreferences.getBoolean("show_weather_icon", false);
        }

        private int getHour(Calendar mCalendar){
            //#TODO add 24 hour time code here if that option is enabled
            int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
            //if (timeFormatUS){
                if (hour == 0){
                    return 12;
                } else if (hour > 12){
                    return hour - 12;
                } else {
                    return hour;
                }
            //} else {
            //  return hour;
            //}
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
