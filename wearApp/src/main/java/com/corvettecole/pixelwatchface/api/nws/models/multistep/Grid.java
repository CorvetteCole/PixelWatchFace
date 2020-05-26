
package com.corvettecole.pixelwatchface.api.nws.models.multistep;

import com.corvettecole.pixelwatchface.api.nws.models.Geometry;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Grid {

  //    @SerializedName("@context")
//    @Expose
//    private List<String> context = null;
  @SerializedName("id")
  @Expose
  private String id;
  @SerializedName("type")
  @Expose
  private String type;
  @SerializedName("geometry")
  @Expose
  private Geometry geometry;
  @SerializedName("properties")
  @Expose
  private Properties properties;

//    public List<String> getContext() {
//        return context;
//    }
//
//    public void setContext(List<String> context) {
//        this.context = context;
//    }

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

  public Geometry getGeometry() {
    return geometry;
  }

  public void setGeometry(Geometry geometry) {
    this.geometry = geometry;
  }

  public Properties getProperties() {
    return properties;
  }

  public void setProperties(Properties properties) {
    this.properties = properties;
  }

}
