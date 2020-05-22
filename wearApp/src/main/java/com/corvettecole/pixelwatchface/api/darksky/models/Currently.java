
package com.corvettecole.pixelwatchface.api.darksky.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Currently {

  @SerializedName("time")
  @Expose
  private Integer time;
  @SerializedName("summary")
  @Expose
  private String summary;
  @SerializedName("icon")
  @Expose
  private String icon;
  @SerializedName("nearestStormDistance")
  @Expose
  private Integer nearestStormDistance;
  @SerializedName("nearestStormBearing")
  @Expose
  private Integer nearestStormBearing;
  @SerializedName("precipIntensity")
  @Expose
  private Integer precipIntensity;
  @SerializedName("precipProbability")
  @Expose
  private Integer precipProbability;
  @SerializedName("temperature")
  @Expose
  private Double temperature;
  @SerializedName("apparentTemperature")
  @Expose
  private Double apparentTemperature;
  @SerializedName("dewPoint")
  @Expose
  private Double dewPoint;
  @SerializedName("humidity")
  @Expose
  private Double humidity;
  @SerializedName("pressure")
  @Expose
  private Double pressure;
  @SerializedName("windSpeed")
  @Expose
  private Double windSpeed;
  @SerializedName("windGust")
  @Expose
  private Double windGust;
  @SerializedName("windBearing")
  @Expose
  private Integer windBearing;
  @SerializedName("cloudCover")
  @Expose
  private Double cloudCover;
  @SerializedName("uvIndex")
  @Expose
  private Integer uvIndex;
  @SerializedName("visibility")
  @Expose
  private Double visibility;
  @SerializedName("ozone")
  @Expose
  private Double ozone;

  public Integer getTime() {
    return time;
  }

  public void setTime(Integer time) {
    this.time = time;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public Integer getNearestStormDistance() {
    return nearestStormDistance;
  }

  public void setNearestStormDistance(Integer nearestStormDistance) {
    this.nearestStormDistance = nearestStormDistance;
  }

  public Integer getNearestStormBearing() {
    return nearestStormBearing;
  }

  public void setNearestStormBearing(Integer nearestStormBearing) {
    this.nearestStormBearing = nearestStormBearing;
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

  public Double getTemperature() {
    return temperature;
  }

  public void setTemperature(Double temperature) {
    this.temperature = temperature;
  }

  public Double getApparentTemperature() {
    return apparentTemperature;
  }

  public void setApparentTemperature(Double apparentTemperature) {
    this.apparentTemperature = apparentTemperature;
  }

  public Double getDewPoint() {
    return dewPoint;
  }

  public void setDewPoint(Double dewPoint) {
    this.dewPoint = dewPoint;
  }

  public Double getHumidity() {
    return humidity;
  }

  public void setHumidity(Double humidity) {
    this.humidity = humidity;
  }

  public Double getPressure() {
    return pressure;
  }

  public void setPressure(Double pressure) {
    this.pressure = pressure;
  }

  public Double getWindSpeed() {
    return windSpeed;
  }

  public void setWindSpeed(Double windSpeed) {
    this.windSpeed = windSpeed;
  }

  public Double getWindGust() {
    return windGust;
  }

  public void setWindGust(Double windGust) {
    this.windGust = windGust;
  }

  public Integer getWindBearing() {
    return windBearing;
  }

  public void setWindBearing(Integer windBearing) {
    this.windBearing = windBearing;
  }

  public Double getCloudCover() {
    return cloudCover;
  }

  public void setCloudCover(Double cloudCover) {
    this.cloudCover = cloudCover;
  }

  public Integer getUvIndex() {
    return uvIndex;
  }

  public void setUvIndex(Integer uvIndex) {
    this.uvIndex = uvIndex;
  }

  public Double getVisibility() {
    return visibility;
  }

  public void setVisibility(Double visibility) {
    this.visibility = visibility;
  }

  public Double getOzone() {
    return ozone;
  }

  public void setOzone(Double ozone) {
    this.ozone = ozone;
  }

}
