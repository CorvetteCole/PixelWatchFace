package com.corvettecole.pixelwatchface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class Utils {

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

    public static int getHour(Calendar mCalendar, Boolean mUse24HourTime){
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

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;

        if(networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }
        return isAvailable;
    }

    public static double convertToCelsius(double fahrenheit){
        return (fahrenheit - 32)/1.8;
    }

    public static CurrentWeather getCurrentDetails(String jsonData, Boolean useDarkSky) throws JSONException {
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

}
