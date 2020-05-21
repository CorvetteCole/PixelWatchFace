package com.corvettecole.pixelwatchface.api.nws.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Properties {

  @SerializedName("updated")
  @Expose
  private String updated;
  @SerializedName("units")
  @Expose
  private String units;
  @SerializedName("forecastGenerator")
  @Expose
  private String forecastGenerator;
  @SerializedName("generatedAt")
  @Expose
  private String generatedAt;
  @SerializedName("updateTime")
  @Expose
  private String updateTime;
  @SerializedName("validTimes")
  @Expose
  private String validTimes;
  @SerializedName("elevation")
  @Expose
  private Elevation elevation;
  @SerializedName("periods")
  @Expose
  private List<Period> periods = new ArrayList<Period>();

  public String getUpdated() {
    return updated;
  }

  public void setUpdated(String updated) {
    this.updated = updated;
  }

  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  public String getForecastGenerator() {
    return forecastGenerator;
  }

  public void setForecastGenerator(String forecastGenerator) {
    this.forecastGenerator = forecastGenerator;
  }

  public String getGeneratedAt() {
    return generatedAt;
  }

  public void setGeneratedAt(String generatedAt) {
    this.generatedAt = generatedAt;
  }

  public String getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(String updateTime) {
    this.updateTime = updateTime;
  }

  public String getValidTimes() {
    return validTimes;
  }

  public void setValidTimes(String validTimes) {
    this.validTimes = validTimes;
  }

  public Elevation getElevation() {
    return elevation;
  }

  public void setElevation(Elevation elevation) {
    this.elevation = elevation;
  }

  public List<Period> getPeriods() {
    return periods;
  }

  public void setPeriods(List<Period> periods) {
    this.periods = periods;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(Properties.class.getName()).append('@')
        .append(Integer.toHexString(System.identityHashCode(this))).append('[');
    sb.append("updated");
    sb.append('=');
    sb.append(((this.updated == null) ? "<null>" : this.updated));
    sb.append(',');
    sb.append("units");
    sb.append('=');
    sb.append(((this.units == null) ? "<null>" : this.units));
    sb.append(',');
    sb.append("forecastGenerator");
    sb.append('=');
    sb.append(((this.forecastGenerator == null) ? "<null>" : this.forecastGenerator));
    sb.append(',');
    sb.append("generatedAt");
    sb.append('=');
    sb.append(((this.generatedAt == null) ? "<null>" : this.generatedAt));
    sb.append(',');
    sb.append("updateTime");
    sb.append('=');
    sb.append(((this.updateTime == null) ? "<null>" : this.updateTime));
    sb.append(',');
    sb.append("validTimes");
    sb.append('=');
    sb.append(((this.validTimes == null) ? "<null>" : this.validTimes));
    sb.append(',');
    sb.append("elevation");
    sb.append('=');
    sb.append(((this.elevation == null) ? "<null>" : this.elevation));
    sb.append(',');
    sb.append("periods");
    sb.append('=');
    sb.append(((this.periods == null) ? "<null>" : this.periods));
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
    result = ((result * 31) + ((this.elevation == null) ? 0 : this.elevation.hashCode()));
    result = ((result * 31) + ((this.validTimes == null) ? 0 : this.validTimes.hashCode()));
    result = ((result * 31) + ((this.forecastGenerator == null) ? 0
        : this.forecastGenerator.hashCode()));
    result = ((result * 31) + ((this.generatedAt == null) ? 0 : this.generatedAt.hashCode()));
    result = ((result * 31) + ((this.periods == null) ? 0 : this.periods.hashCode()));
    result = ((result * 31) + ((this.updateTime == null) ? 0 : this.updateTime.hashCode()));
    result = ((result * 31) + ((this.units == null) ? 0 : this.units.hashCode()));
    result = ((result * 31) + ((this.updated == null) ? 0 : this.updated.hashCode()));
    return result;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof Properties) == false) {
      return false;
    }
    Properties rhs = ((Properties) other);
    return (((((((((this.elevation == rhs.elevation) || ((this.elevation != null) && this.elevation
        .equals(rhs.elevation))) && ((this.validTimes == rhs.validTimes) || (
        (this.validTimes != null) && this.validTimes.equals(rhs.validTimes)))) && (
        (this.forecastGenerator == rhs.forecastGenerator) || ((this.forecastGenerator != null)
            && this.forecastGenerator.equals(rhs.forecastGenerator)))) && (
        (this.generatedAt == rhs.generatedAt) || ((this.generatedAt != null) && this.generatedAt
            .equals(rhs.generatedAt)))) && ((this.periods == rhs.periods) || ((this.periods != null)
        && this.periods.equals(rhs.periods)))) && ((this.updateTime == rhs.updateTime) || (
        (this.updateTime != null) && this.updateTime.equals(rhs.updateTime)))) && (
        (this.units == rhs.units) || ((this.units != null) && this.units.equals(rhs.units)))) && (
        (this.updated == rhs.updated) || ((this.updated != null) && this.updated
            .equals(rhs.updated))));
  }

}
