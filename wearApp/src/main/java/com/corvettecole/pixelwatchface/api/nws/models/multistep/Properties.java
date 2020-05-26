
package com.corvettecole.pixelwatchface.api.nws.models.multistep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Properties {

  @SerializedName("@id")
  @Expose
  private String id;
  @SerializedName("@type")
  @Expose
  private String type;
  @SerializedName("cwa")
  @Expose
  private String cwa;
  @SerializedName("forecastOffice")
  @Expose
  private String forecastOffice;
  @SerializedName("gridX")
  @Expose
  private Integer gridX;
  @SerializedName("gridY")
  @Expose
  private Integer gridY;
  @SerializedName("forecast")
  @Expose
  private String forecast;
  @SerializedName("forecastHourly")
  @Expose
  private String forecastHourly;
  @SerializedName("forecastGridData")
  @Expose
  private String forecastGridData;
  @SerializedName("observationStations")
  @Expose
  private String observationStations;
  @SerializedName("relativeLocation")
  @Expose
  private RelativeLocation relativeLocation;
  @SerializedName("forecastZone")
  @Expose
  private String forecastZone;
  @SerializedName("county")
  @Expose
  private String county;
  @SerializedName("fireWeatherZone")
  @Expose
  private String fireWeatherZone;
  @SerializedName("timeZone")
  @Expose
  private String timeZone;
  @SerializedName("radarStation")
  @Expose
  private String radarStation;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCwa() {
    return cwa;
  }

  public void setCwa(String cwa) {
    this.cwa = cwa;
  }

  public String getForecastOffice() {
    return forecastOffice;
  }

  public void setForecastOffice(String forecastOffice) {
    this.forecastOffice = forecastOffice;
  }

  public Integer getGridX() {
    return gridX;
  }

  public void setGridX(Integer gridX) {
    this.gridX = gridX;
  }

  public Integer getGridY() {
    return gridY;
  }

  public void setGridY(Integer gridY) {
    this.gridY = gridY;
  }

  public String getForecast() {
    return forecast;
  }

  public void setForecast(String forecast) {
    this.forecast = forecast;
  }

  public String getForecastHourly() {
    return forecastHourly;
  }

  public void setForecastHourly(String forecastHourly) {
    this.forecastHourly = forecastHourly;
  }

  public String getForecastGridData() {
    return forecastGridData;
  }

  public void setForecastGridData(String forecastGridData) {
    this.forecastGridData = forecastGridData;
  }

  public String getObservationStations() {
    return observationStations;
  }

  public void setObservationStations(String observationStations) {
    this.observationStations = observationStations;
  }

  public RelativeLocation getRelativeLocation() {
    return relativeLocation;
  }

  public void setRelativeLocation(RelativeLocation relativeLocation) {
    this.relativeLocation = relativeLocation;
  }

  public String getForecastZone() {
    return forecastZone;
  }

  public void setForecastZone(String forecastZone) {
    this.forecastZone = forecastZone;
  }

  public String getCounty() {
    return county;
  }

  public void setCounty(String county) {
    this.county = county;
  }

  public String getFireWeatherZone() {
    return fireWeatherZone;
  }

  public void setFireWeatherZone(String fireWeatherZone) {
    this.fireWeatherZone = fireWeatherZone;
  }

  public String getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(String timeZone) {
    this.timeZone = timeZone;
  }

  public String getRadarStation() {
    return radarStation;
  }

  public void setRadarStation(String radarStation) {
    this.radarStation = radarStation;
  }

}
