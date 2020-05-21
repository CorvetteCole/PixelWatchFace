package com.corvettecole.pixelwatchface.api.nws.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Geometry {

  @SerializedName("type")
  @Expose
  private String type;
  @SerializedName("geometries")
  @Expose
  private List<Coordinates> geometries = new ArrayList<Coordinates>();

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public List<Coordinates> getGeometries() {
    return geometries;
  }

  public void setGeometries(List<Coordinates> geometries) {
    this.geometries = geometries;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(Geometry.class.getName()).append('@')
        .append(Integer.toHexString(System.identityHashCode(this))).append('[');
    sb.append("type");
    sb.append('=');
    sb.append(((this.type == null) ? "<null>" : this.type));
    sb.append(',');
    sb.append("geometries");
    sb.append('=');
    sb.append(((this.geometries == null) ? "<null>" : this.geometries));
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
    result = ((result * 31) + ((this.type == null) ? 0 : this.type.hashCode()));
    result = ((result * 31) + ((this.geometries == null) ? 0 : this.geometries.hashCode()));
    return result;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof Geometry) == false) {
      return false;
    }
    Geometry rhs = ((Geometry) other);
    return (((this.type == rhs.type) || ((this.type != null) && this.type.equals(rhs.type))) && (
        (this.geometries == rhs.geometries) || ((this.geometries != null) && this.geometries
            .equals(rhs.geometries))));
  }

}
