/*
 * Locationforecast_2_0
 * Weather forecast for a specified place
 *
 * OpenAPI spec version: 2.0
 * Contact: weatherapi-adm@met.no
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.corvettecole.pixelwatchface.api.met.models;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;

/**
 * Parameters with validity times over six hours. Will not exist for all time steps.
 */
public class ForecastTimeStepDataNext6Hours {

  @SerializedName("summary")
  private ForecastSummary summary = null;

  @SerializedName("details")
  private ForecastTimePeriod details = null;

  public ForecastTimeStepDataNext6Hours summary(ForecastSummary summary) {
    this.summary = summary;
    return this;
  }

  /**
   * Get summary
   *
   * @return summary
   **/
  public ForecastSummary getSummary() {
    return summary;
  }

  public void setSummary(ForecastSummary summary) {
    this.summary = summary;
  }

  public ForecastTimeStepDataNext6Hours details(ForecastTimePeriod details) {
    this.details = details;
    return this;
  }

  /**
   * Get details
   *
   * @return details
   **/
  public ForecastTimePeriod getDetails() {
    return details;
  }

  public void setDetails(ForecastTimePeriod details) {
    this.details = details;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ForecastTimeStepDataNext6Hours forecastTimeStepDataNext6Hours = (ForecastTimeStepDataNext6Hours) o;
    return Objects.equals(this.summary, forecastTimeStepDataNext6Hours.summary) &&
        Objects.equals(this.details, forecastTimeStepDataNext6Hours.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(summary, details);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ForecastTimeStepDataNext6Hours {\n");

    sb.append("    summary: ").append(toIndentedString(summary)).append("\n");
    sb.append("    details: ").append(toIndentedString(details)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first
   * line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}

