
package com.corvettecole.pixelwatchface.api.darksky.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Forecast {

  @SerializedName("latitude")
  @Expose
  private Double latitude;
  @SerializedName("longitude")
  @Expose
  private Double longitude;
  @SerializedName("timezone")
  @Expose
  private String timezone;
  @SerializedName("currently")
  @Expose
  private Currently currently;
  @SerializedName("minutely")
  @Expose
  private Minutely minutely;
  @SerializedName("hourly")
  @Expose
  private Hourly hourly;
  @SerializedName("daily")
  @Expose
  private Daily daily;
  @SerializedName("alerts")
  @Expose
  private List<Alert> alerts = null;
  @SerializedName("flags")
  @Expose
  private Flags flags;
  @SerializedName("offset")
  @Expose
  private Integer offset;

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public Currently getCurrently() {
    return currently;
  }

  public void setCurrently(Currently currently) {
    this.currently = currently;
  }

  public Minutely getMinutely() {
    return minutely;
  }

  public void setMinutely(Minutely minutely) {
    this.minutely = minutely;
  }

  public Hourly getHourly() {
    return hourly;
  }

  public void setHourly(Hourly hourly) {
    this.hourly = hourly;
  }

  public Daily getDaily() {
    return daily;
  }

  public void setDaily(Daily daily) {
    this.daily = daily;
  }

  public List<Alert> getAlerts() {
    return alerts;
  }

  public void setAlerts(List<Alert> alerts) {
    this.alerts = alerts;
  }

  public Flags getFlags() {
    return flags;
  }

  public void setFlags(Flags flags) {
    this.flags = flags;
  }

  public Integer getOffset() {
    return offset;
  }

  public void setOffset(Integer offset) {
    this.offset = offset;
  }

}
