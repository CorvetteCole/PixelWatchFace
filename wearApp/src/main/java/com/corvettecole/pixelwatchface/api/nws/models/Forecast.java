package com.corvettecole.pixelwatchface.api.nws.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Forecast {

//  @SerializedName("@context")
//  @Expose
//  private List<String> context = new ArrayList<String>();
@SerializedName("type")
@Expose
private String type;
  @SerializedName("geometry")
  @Expose
  private Geometry geometry;
  @SerializedName("properties")
  @Expose
  private Properties properties;

//  public List<String> getContext() {
//    return context;
//  }
//
//  public void setContext(List<String> context) {
//    this.context = context;
//  }

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

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(Forecast.class.getName()).append('@')
        .append(Integer.toHexString(System.identityHashCode(this))).append('[');
//    sb.append("context");
//    sb.append('=');
//    sb.append(((this.context == null) ? "<null>" : this.context));
//    sb.append(',');
    sb.append("type");
    sb.append('=');
    sb.append(((this.type == null) ? "<null>" : this.type));
    sb.append(',');
    sb.append("geometry");
    sb.append('=');
    sb.append(((this.geometry == null) ? "<null>" : this.geometry));
    sb.append(',');
    sb.append("properties");
    sb.append('=');
    sb.append(((this.properties == null) ? "<null>" : this.properties));
    sb.append(',');
    if (sb.charAt((sb.length() - 1)) == ',') {
      sb.setCharAt((sb.length() - 1), ']');
    } else {
      sb.append(']');
    }
    return sb.toString();
  }

  @Override
  public int hashCode() {
    int result = 1;
    result = ((result * 31) + ((this.geometry == null) ? 0 : this.geometry.hashCode()));
    result = ((result * 31) + ((this.type == null) ? 0 : this.type.hashCode()));
    result = ((result * 31) + ((this.properties == null) ? 0 : this.properties.hashCode()));
    return result;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof Forecast) == false) {
      return false;
    }
    Forecast rhs = ((Forecast) other);
    return (((((this.geometry == rhs.geometry) || ((this.geometry != null)
        && this.geometry.equals(rhs.geometry)))) && ((this.type == rhs.type) || ((this.type != null)
        && this.type.equals(rhs.type)))) && ((this.properties == rhs.properties) || (
        (this.properties != null) && this.properties.equals(rhs.properties))));
  }

}
