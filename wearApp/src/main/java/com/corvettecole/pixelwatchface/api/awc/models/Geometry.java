
package com.corvettecole.pixelwatchface.api.awc.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Geometry {

  @SerializedName("type")
  @Expose
  private String type;
  @SerializedName("coordinates")
  @Expose
  private List<Double> coordinates = null;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<Double> getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(List<Double> coordinates) {
    this.coordinates = coordinates;
  }

}
