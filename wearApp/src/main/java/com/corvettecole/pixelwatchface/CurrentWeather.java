package com.corvettecole.pixelwatchface;

import android.content.Context;
import android.graphics.Bitmap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static com.corvettecole.pixelwatchface.Utils.drawableToBitmap;

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

    public Bitmap getIconBitmap(Context context){
        if (mIconBitmap == null){
            mIconBitmap = Bitmap.createScaledBitmap(drawableToBitmap(context.getDrawable(getIconId())), 34, 34, false);
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
