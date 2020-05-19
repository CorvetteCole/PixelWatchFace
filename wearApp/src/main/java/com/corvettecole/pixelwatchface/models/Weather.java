package com.corvettecole.pixelwatchface.models;

import android.graphics.Bitmap;

public class Weather {

  private long mTime;
  private double mTemperature, mHumidity, mPrecipitationChance;
  private int mIconID;
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

  public Bitmap getIconBitmap() {
    // TODO implement this
    return mIconBitmap;
  }
}
