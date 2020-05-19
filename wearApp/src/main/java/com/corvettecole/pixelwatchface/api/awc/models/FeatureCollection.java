
package com.corvettecole.pixelwatchface.api.awc.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FeatureCollection {

  @SerializedName("type")
  @Expose
  private String type;
  @SerializedName("features")
  @Expose
  private List<Feature> features = null;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<Feature> getFeatures() {
    return features;
  }

  public void setFeatures(List<Feature> features) {
    this.features = features;
  }

}
