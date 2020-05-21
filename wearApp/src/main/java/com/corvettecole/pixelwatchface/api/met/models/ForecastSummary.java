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
 * Summary of weather conditions.
 */
public class ForecastSummary {

  @SerializedName("symbol_code")
  private WeatherSymbol symbolCode = null;

  public ForecastSummary symbolCode(WeatherSymbol symbolCode) {
    this.symbolCode = symbolCode;
    return this;
  }

  /**
   * Get symbolCode
   *
   * @return symbolCode
   **/
  public WeatherSymbol getSymbolCode() {
    return symbolCode;
  }

  public void setSymbolCode(WeatherSymbol symbolCode) {
    this.symbolCode = symbolCode;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ForecastSummary forecastSummary = (ForecastSummary) o;
    return Objects.equals(this.symbolCode, forecastSummary.symbolCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(symbolCode);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ForecastSummary {\n");

    sb.append("    symbolCode: ").append(toIndentedString(symbolCode)).append("\n");
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

