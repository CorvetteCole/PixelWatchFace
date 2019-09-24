package com.corvettecole.pixelwatchface;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.concurrent.futures.CallbackToFutureAdapter;
import androidx.work.ListenableWorker;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Future;

import static com.corvettecole.pixelwatchface.Utils.ONE_MIN;
import static com.corvettecole.pixelwatchface.Utils.convertToCelsius;
import static com.corvettecole.pixelwatchface.Utils.drawableToBitmap;
import static com.corvettecole.pixelwatchface.Utils.isNetworkAvailable;

public class CurrentWeather {

    private static volatile CurrentWeather instance;

    private String mIconName = "clear-day";
    private long mTime;
    private double mTemperature = Double.MIN_VALUE;
    private double mHumidity;
    private double mPrecipChance;
    private String mSummary;
    private String mTimeZone;
    private Bitmap mIconBitmap;
    private String mWeatherProvider;

    private Context mApplicationContext;

    private String mOpenWeatherMapKey;
    private String mDarkSkyKey;

    private boolean mUseDarkSky = false;
    private boolean mUseCelsius = false;
    private boolean mShowTemperatureDecimalPoint = false;

    private long mLastWeatherUpdateTime = 0;
    private long mLastWeatherUpdateFailedTime = 0;
    private boolean mWeatherUpdating = false;

    OkHttpClient client;

    private CurrentWeather(Context context) {
        if (instance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class");
        } else {
            mApplicationContext = context.getApplicationContext();
            mOpenWeatherMapKey = context.getString(R.string.openstreetmap_api_key);
            client = new OkHttpClient();
        }
    }

    public static CurrentWeather getInstance(Context context) {
        if (instance == null) {
            synchronized (CurrentWeather.class) {
                if (instance == null) {
                    instance = new CurrentWeather(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    // TODO consider putting the weather update code in the WeatherUpdateWorker
    public Future<ListenableWorker.Result> updateForecast(double latitude, double longitude) {
        return CallbackToFutureAdapter.getFuture(completer -> {
            final String TAG = "getForecast";
            String forecastUrl;

            if (mUseDarkSky && mDarkSkyKey != null) {
                forecastUrl = "https://api.forecast.io/forecast/" +
                        mDarkSkyKey + "/" + latitude + "," + longitude + "?lang=" + Locale.getDefault().getLanguage();
                Log.d(TAG, "forecastURL: " + "https://api.forecast.io/forecast/" +
                        mDarkSkyKey + "/" + latitude + "," + longitude + "?lang=" + Locale.getDefault().getLanguage());
            } else {
                forecastUrl = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&units=imperial&appid=" + mOpenWeatherMapKey;
            }


            mWeatherUpdating = true;

            if (client == null) {
                client = new OkHttpClient();
            }

            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                    Log.d(TAG, "Couldn't retrieve weather data");
                    // TODO figure out if i can just use freakin volley because seriously fuck okhttp
                    //return e;
                    completer.setException(e);
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    mWeatherUpdating = false;
                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            try {
                                parseWeatherJSON(jsonData);
                                mLastWeatherUpdateTime = System.currentTimeMillis();
                                completer.set(ListenableWorker.Result.success());
                                //invalidate();
                            } catch (JSONException e) {
                                completer.set(ListenableWorker.Result.retry());
                                Log.e(TAG, e.toString());
                                mLastWeatherUpdateFailedTime = System.currentTimeMillis();
                            }
                        } else {
                            Log.d(TAG, "Couldn't retrieve weather data: response not successful");
                            completer.set(ListenableWorker.Result.retry());
                            mLastWeatherUpdateFailedTime = System.currentTimeMillis();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                        completer.set(ListenableWorker.Result.retry());
                        mLastWeatherUpdateFailedTime = System.currentTimeMillis();
                    }
                }
            });
            return completer;
        });
    }

    private void parseWeatherJSON(String json) throws JSONException {
        final String TAG = "parseWeatherJSON";

        JSONObject forecast = new JSONObject(json);
        if (mUseDarkSky) {
            setWeatherProvider("DarkSky");
            mTimeZone = forecast.getString("timezone");
            JSONObject currently = forecast.getJSONObject("currently");
            mHumidity = currently.getDouble("humidity");
            mTime = currently.getLong("time");
            mIconName = currently.getString("icon");
            mPrecipChance = currently.getDouble("precipProbability");
            mSummary = currently.getString("summary");
            mTemperature = currently.getDouble("temperature");
            Log.d(TAG, getFormattedTime());
        } else {
            mWeatherProvider = "OpenStreetMap";
            String tempIcon = forecast.getJSONArray("weather").toString();
            JSONObject main = forecast.getJSONObject("main");
            mHumidity = main.getDouble("humidity") / 100; //adjust OpenStreetMap format to dark sky format with /100
            mTemperature = main.getDouble("temp");
            Log.d(TAG, tempIcon.substring(tempIcon.indexOf("\"icon\":\"") + 8, tempIcon.indexOf("\"}]")));
            mIconName = tempIcon.substring(tempIcon.indexOf("\"icon\":\"") + 8, tempIcon.indexOf("\"}]"));
        }

    }

    public String getFormattedTemperature() {
        String unit = mUseCelsius ? "°C" : "°F";
        if (mTemperature == Double.MIN_VALUE){
            if (mShowTemperatureDecimalPoint) {
                return "--.- " + unit;
            } else {
                return "-- " + unit;
            }
        } else {
            double temperature = mUseCelsius ? convertToCelsius(mTemperature) : mTemperature;
            if (mShowTemperatureDecimalPoint) {
                return String.format("%.1f %s", temperature, unit);
            } else {
                return String.format("%d %s", Math.round(temperature), unit);
            }
        }
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public Bitmap getIconBitmap(Context context) {
        if (mIconBitmap == null) {
            mIconBitmap = Bitmap.createScaledBitmap(drawableToBitmap(context.getDrawable(getIconId())), 34, 34, false);
        }
        return mIconBitmap;
    }

    public int getIconId() {
        //#TODO use custom icons so as to have fitting icons for every weather condition from any provider (see these: http://adamwhitcroft.com/climacons/)
        int iconId = R.drawable.clear_day;
        if (mWeatherProvider != null) {
            if (mWeatherProvider.equalsIgnoreCase("DarkSky")) {
                // clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night.
                switch (mIconName) {
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
            } else if (mWeatherProvider.equalsIgnoreCase("OpenStreetMap")) {
                switch (mIconName) {
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

    public double getHumidity() {
        return mHumidity;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public int getPrecipChance() {
        double precipPercentage = mPrecipChance * 100;
        return (int) Math.round(precipPercentage);
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

    public void setDarkSkyKey(String mDarkSkyKey) {
        this.mDarkSkyKey = mDarkSkyKey;
    }

    public void setUseDarkSky(boolean mUseDarkSky) {
        this.mUseDarkSky = mUseDarkSky;
    }

    public void setUseCelsius(boolean mUseCelsius) {
        this.mUseCelsius = mUseCelsius;
    }

    public void setShowTemperatureDecimalPoint(boolean showTemperatureDecimalPoint){
        this.mShowTemperatureDecimalPoint = showTemperatureDecimalPoint;
    }
}
