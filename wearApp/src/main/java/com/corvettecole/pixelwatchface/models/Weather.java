package com.corvettecole.pixelwatchface.models;

import android.graphics.Bitmap;

public class Weather {

  private long mTime;
  private double mTemperature, mHumidity, mPrecipitationChance;
  private int mIconID;
  // TODO figure out if we REALLY need to dynamically generate bitmaps... Can't they be bundled like that?
  private transient Bitmap mIconBitmap; // don't serialize bitmap since that increases file size. We can do that dynamically

  public Weather(long mTime, double mTemperature, double mHumidity, double mPrecipitationChance,
      int mIconID) {
    this.mTime = mTime;
    this.mTemperature = mTemperature;
    this.mHumidity = mHumidity;
    this.mPrecipitationChance = mPrecipitationChance;
    this.mIconID = mIconID;
  }
}
