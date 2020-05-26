
package com.corvettecole.pixelwatchface.api.nws.models.multistep;

import com.corvettecole.pixelwatchface.api.nws.models.Geometry;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RelativeLocation {

  @SerializedName("type")
  @Expose
  private String type;
  @SerializedName("geometry")
  @Expose
  private Geometry geometry;
  @SerializedName("properties")
  @Expose
  private Properties_ properties;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  public Properties_ getProperties() {
    return properties;
  }

  public void setProperties(Properties_ properties) {
    this.properties = properties;
  }

}
