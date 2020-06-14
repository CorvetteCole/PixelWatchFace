package com.corvettecole.pixelwatchface.models;

import static com.corvettecole.pixelwatchface.util.WatchFaceUtil.convertToFahrenheit;
import static com.corvettecole.pixelwatchface.util.WatchFaceUtil.drawableToBitmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import com.corvettecole.pixelwatchface.R;
import com.corvettecole.pixelwatchface.util.Constants;

import java.text.DecimalFormatSymbols;

public class Weather {

  private long mTime;
  private double mTemperature = Constants.NO_TEMPERATURE, mHumidity, mPrecipitationChance;
  private int mIconID = R.drawable.ic_error;
  // TODO figure out if we REALLY need to dynamically generate bitmaps... Can't they be bundled like that?
  private transient Bitmap mIconBitmap; // don't serialize bitmap since that increases file size. We can do that dynamically

  public Weather() {
  }

  public Weather(long mTime, double mTemperature,
      int mIconID) {
    this.mTime = mTime;
    this.mTemperature = mTemperature; // in Celsius
    this.mIconID = mIconID;
  }

  public long getTime() {
    return mTime;
  }

  public void setTime(long mTime) {
    this.mTime = mTime;
  }

  public double getTemperature() {
    return mTemperature;
  }

  public void setTemperature(double mTemperature) {
    this.mTemperature = mTemperature;
  }

  public double getHumidity() {
    return mHumidity;
  }

  public void setHumidity(double mHumidity) {
    this.mHumidity = mHumidity;
  }

  public double getPrecipitationChance() {
    return mPrecipitationChance;
  }

  public void setPrecipitationChance(double mPrecipitationChance) {
    this.mPrecipitationChance = mPrecipitationChance;
  }

  public int getIconID() {
    return mIconID;
  }

  public void setIconID(int mIconID) {
    this.mIconID = mIconID;
  }

  public void setIconBitmap(Bitmap bitmap) {
    mIconBitmap = bitmap;
  }

  public Bitmap getIconBitmap(Context context) {
    if (mIconBitmap == null) {
      mIconBitmap = Bitmap
          .createScaledBitmap(drawableToBitmap(context.getDrawable(mIconID)), 34, 34, false);
    }
    return mIconBitmap;
  }

  @SuppressLint("DefaultLocale")
  public String getFormattedTemperature(boolean useCelsius, boolean showTemperatureFractional) {
    String unit = useCelsius ? "°C" : "°F";
    char decimalSep = DecimalFormatSymbols.getInstance().getDecimalSeparator();
    if (mTemperature == Double.MIN_VALUE) {
      if (showTemperatureFractional) {
        return "--" + decimalSep + "-" + unit;
      } else {
        return "--" + unit;
      }
    } else {
      double temperature = useCelsius ? mTemperature : convertToFahrenheit(mTemperature);
      if (showTemperatureFractional) {
        return String.format("%.1f%s", temperature, unit);
      } else {
        return String.format("%d%s", Math.round(temperature), unit);
      }
    }
  }
}
