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
 * Parameters which applies to this exact point in time
 */
public class ForecastTimeStepDataInstant {

  @SerializedName("details")
  private ForecastTimeInstant details = null;

  public ForecastTimeStepDataInstant details(ForecastTimeInstant details) {
    this.details = details;
    return this;
  }

  /**
   * Get details
   *
   * @return details
   **/
  public ForecastTimeInstant getDetails() {
    return details;
  }

  public void setDetails(ForecastTimeInstant details) {
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
    ForecastTimeStepDataInstant forecastTimeStepDataInstant = (ForecastTimeStepDataInstant) o;
    return Objects.equals(this.details, forecastTimeStepDataInstant.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(details);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ForecastTimeStepDataInstant {\n");

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

