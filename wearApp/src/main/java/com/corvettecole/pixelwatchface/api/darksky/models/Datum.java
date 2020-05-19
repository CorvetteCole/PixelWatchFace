
package com.corvettecole.pixelwatchface.api.darksky.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

  @SerializedName("time")
  @Expose
  private Integer time;
  @SerializedName("precipIntensity")
  @Expose
  private Integer precipIntensity;
  @SerializedName("precipProbability")
  @Expose
  private Integer precipProbability;

  public Integer getTime() {
    return time;
  }

  public void setTime(Integer time) {
    this.time = time;
  }

  public Integer getPrecipIntensity() {
    return precipIntensity;
  }

  public void setPrecipIntensity(Integer precipIntensity) {
    this.precipIntensity = precipIntensity;
  }

  public Integer getPrecipProbability() {
    return precipProbability;
  }

  public void setPrecipProbability(Integer precipProbability) {
    this.precipProbability = precipProbability;
  }

}
