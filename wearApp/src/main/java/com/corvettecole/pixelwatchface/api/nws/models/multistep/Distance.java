
package com.corvettecole.pixelwatchface.api.nws.models.multistep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Distance {

  @SerializedName("value")
  @Expose
  private Double value;
  @SerializedName("unitCode")
  @Expose
  private String unitCode;

  public Double getValue() {
    return value;
  }

  public void setValue(Double value) {
    this.value = value;
  }

  public String getUnitCode() {
    return unitCode;
  }

  public void setUnitCode(String unitCode) {
    this.unitCode = unitCode;
  }

}
