
package com.corvettecole.pixelwatchface.api.nws.models.multistep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Properties_ {

  @SerializedName("city")
  @Expose
  private String city;
  @SerializedName("state")
  @Expose
  private String state;
  @SerializedName("distance")
  @Expose
  private Distance distance;
  @SerializedName("bearing")
  @Expose
  private Bearing bearing;

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public Distance getDistance() {
    return distance;
  }

  public void setDistance(Distance distance) {
    this.distance = distance;
  }

  public Bearing getBearing() {
    return bearing;
  }

  public void setBearing(Bearing bearing) {
    this.bearing = bearing;
  }

}
