
package com.corvettecole.pixelwatchface.api.darksky.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

  @SerializedName("time")
  @Expose
  private Integer time;
  @SerializedName("precipIntensity")
  @Expose
  private Double precipIntensity;
  @SerializedName("precipProbability")
  @Expose
  private Double precipProbability;

  public Integer getTime() {
    return time;
  }

  public void setTime(Integer time) {
    this.time = time;
  }

  public Double getPrecipIntensity() {
    return precipIntensity;
  }

  public void setPrecipIntensity(Double precipIntensity) {
    this.precipIntensity = precipIntensity;
  }

  public Double getPrecipProbability() {
    return precipProbability;
  }

  public void setPrecipProbability(Double precipProbability) {
    this.precipProbability = precipProbability;
  }

}
