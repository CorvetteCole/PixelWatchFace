package com.corvettecole.pixelwatchface.api.nws.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Period {

  @SerializedName("number")
  @Expose
  private Integer number;
  @SerializedName("name")
  @Expose
  private String name;
  @SerializedName("startTime")
  @Expose
  private String startTime;
  @SerializedName("endTime")
  @Expose
  private String endTime;
  @SerializedName("isDaytime")
  @Expose
  private Boolean isDaytime;
  @SerializedName("temperature")
  @Expose
  private Integer temperature;
  @SerializedName("temperatureUnit")
  @Expose
  private String temperatureUnit;
  @SerializedName("temperatureTrend")
  @Expose
  private Object temperatureTrend;
  @SerializedName("windSpeed")
  @Expose
  private String windSpeed;
  @SerializedName("windDirection")
  @Expose
  private String windDirection;
  @SerializedName("icon")
  @Expose
  private String icon;
  @SerializedName("shortForecast")
  @Expose
  private String shortForecast;
  @SerializedName("detailedForecast")
  @Expose
  private String detailedForecast;

  public Integer getNumber() {
    return number;
  }

  public void setNumber(Integer number) {
    this.number = number;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public Boolean getIsDaytime() {
    return isDaytime;
  }

  public void setIsDaytime(Boolean isDaytime) {
    this.isDaytime = isDaytime;
  }

  public Integer getTemperature() {
    return temperature;
  }

  public void setTemperature(Integer temperature) {
    this.temperature = temperature;
  }

  public String getTemperatureUnit() {
    return temperatureUnit;
  }

  public void setTemperatureUnit(String temperatureUnit) {
    this.temperatureUnit = temperatureUnit;
  }

  public Object getTemperatureTrend() {
    return temperatureTrend;
  }

  public void setTemperatureTrend(Object temperatureTrend) {
    this.temperatureTrend = temperatureTrend;
  }

  public String getWindSpeed() {
    return windSpeed;
  }

  public void setWindSpeed(String windSpeed) {
    this.windSpeed = windSpeed;
  }

  public String getWindDirection() {
    return windDirection;
  }

  public void setWindDirection(String windDirection) {
    this.windDirection = windDirection;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getShortForecast() {
    return shortForecast;
  }

  public void setShortForecast(String shortForecast) {
    this.shortForecast = shortForecast;
  }

  public String getDetailedForecast() {
    return detailedForecast;
  }

  public void setDetailedForecast(String detailedForecast) {
    this.detailedForecast = detailedForecast;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(Period.class.getName()).append('@')
        .append(Integer.toHexString(System.identityHashCode(this))).append('[');
    sb.append("number");
    sb.append('=');
    sb.append(((this.number == null) ? "<null>" : this.number));
    sb.append(',');
    sb.append("name");
    sb.append('=');
    sb.append(((this.name == null) ? "<null>" : this.name));
    sb.append(',');
    sb.append("startTime");
    sb.append('=');
    sb.append(((this.startTime == null) ? "<null>" : this.startTime));
    sb.append(',');
    sb.append("endTime");
    sb.append('=');
    sb.append(((this.endTime == null) ? "<null>" : this.endTime));
    sb.append(',');
    sb.append("isDaytime");
    sb.append('=');
    sb.append(((this.isDaytime == null) ? "<null>" : this.isDaytime));
    sb.append(',');
    sb.append("temperature");
    sb.append('=');
    sb.append(((this.temperature == null) ? "<null>" : this.temperature));
    sb.append(',');
    sb.append("temperatureUnit");
    sb.append('=');
    sb.append(((this.temperatureUnit == null) ? "<null>" : this.temperatureUnit));
    sb.append(',');
    sb.append("temperatureTrend");
    sb.append('=');
    sb.append(((this.temperatureTrend == null) ? "<null>" : this.temperatureTrend));
    sb.append(',');
    sb.append("windSpeed");
    sb.append('=');
    sb.append(((this.windSpeed == null) ? "<null>" : this.windSpeed));
    sb.append(',');
    sb.append("windDirection");
    sb.append('=');
    sb.append(((this.windDirection == null) ? "<null>" : this.windDirection));
    sb.append(',');
    sb.append("icon");
    sb.append('=');
    sb.append(((this.icon == null) ? "<null>" : this.icon));
    sb.append(',');
    sb.append("shortForecast");
    sb.append('=');
    sb.append(((this.shortForecast == null) ? "<null>" : this.shortForecast));
    sb.append(',');
    sb.append("detailedForecast");
    sb.append('=');
    sb.append(((this.detailedForecast == null) ? "<null>" : this.detailedForecast));
    sb.append(',');
    if (sb.charAt((sb.length() - 1)) == ',') {
      sb.setCharAt((sb.length() - 1), ']');
    } else {
      sb.append(']');
    }
    return sb.toString();
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = ((result * 31) + ((this.detailedForecast == null) ? 0
        : this.detailedForecast.hashCode()));
    result = ((result * 31) + ((this.temperatureTrend == null) ? 0
        : this.temperatureTrend.hashCode()));
    result = ((result * 31) + ((this.shortForecast == null) ? 0 : this.shortForecast.hashCode()));
    result = ((result * 31) + ((this.icon == null) ? 0 : this.icon.hashCode()));
    result = ((result * 31) + ((this.number == null) ? 0 : this.number.hashCode()));
    result = ((result * 31) + ((this.temperatureUnit == null) ? 0
        : this.temperatureUnit.hashCode()));
    result = ((result * 31) + ((this.name == null) ? 0 : this.name.hashCode()));
    result = ((result * 31) + ((this.temperature == null) ? 0 : this.temperature.hashCode()));
    result = ((result * 31) + ((this.startTime == null) ? 0 : this.startTime.hashCode()));
    result = ((result * 31) + ((this.isDaytime == null) ? 0 : this.isDaytime.hashCode()));
    result = ((result * 31) + ((this.endTime == null) ? 0 : this.endTime.hashCode()));
    result = ((result * 31) + ((this.windDirection == null) ? 0 : this.windDirection.hashCode()));
    result = ((result * 31) + ((this.windSpeed == null) ? 0 : this.windSpeed.hashCode()));
    return result;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof Period) == false) {
      return false;
    }
    Period rhs = ((Period) other);
    return (((((((((((
        (((this.detailedForecast == rhs.detailedForecast) || ((this.detailedForecast != null)
            && this.detailedForecast.equals(rhs.detailedForecast))) && (
            (this.temperatureTrend == rhs.temperatureTrend) || ((this.temperatureTrend != null)
                && this.temperatureTrend.equals(rhs.temperatureTrend)))) && (
            (this.shortForecast == rhs.shortForecast) || ((this.shortForecast != null)
                && this.shortForecast.equals(rhs.shortForecast)))) && ((this.icon == rhs.icon) || (
        (this.icon != null) && this.icon.equals(rhs.icon)))) && ((this.number == rhs.number) || (
        (this.number != null) && this.number.equals(rhs.number)))) && (
        (this.temperatureUnit == rhs.temperatureUnit) || ((this.temperatureUnit != null)
            && this.temperatureUnit.equals(rhs.temperatureUnit)))) && ((this.name == rhs.name) || (
        (this.name != null) && this.name.equals(rhs.name)))) && (
        (this.temperature == rhs.temperature) || ((this.temperature != null) && this.temperature
            .equals(rhs.temperature)))) && ((this.startTime == rhs.startTime) || (
        (this.startTime != null) && this.startTime.equals(rhs.startTime)))) && (
        (this.isDaytime == rhs.isDaytime) || ((this.isDaytime != null) && this.isDaytime
            .equals(rhs.isDaytime)))) && ((this.endTime == rhs.endTime) || ((this.endTime != null)
        && this.endTime.equals(rhs.endTime)))) && ((this.windDirection == rhs.windDirection) || (
        (this.windDirection != null) && this.windDirection.equals(rhs.windDirection)))) && (
        (this.windSpeed == rhs.windSpeed) || ((this.windSpeed != null) && this.windSpeed
            .equals(rhs.windSpeed))));
  }

}
