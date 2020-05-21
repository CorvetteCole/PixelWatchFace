package com.corvettecole.pixelwatchface.api.nws.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Elevation {

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

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(Elevation.class.getName()).append('@')
        .append(Integer.toHexString(System.identityHashCode(this))).append('[');
    sb.append("value");
    sb.append('=');
    sb.append(((this.value == null) ? "<null>" : this.value));
    sb.append(',');
    sb.append("unitCode");
    sb.append('=');
    sb.append(((this.unitCode == null) ? "<null>" : this.unitCode));
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
    result = ((result * 31) + ((this.value == null) ? 0 : this.value.hashCode()));
    result = ((result * 31) + ((this.unitCode == null) ? 0 : this.unitCode.hashCode()));
    return result;
  }

  @Override
  public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if ((other instanceof Elevation) == false) {
      return false;
    }
    Elevation rhs = ((Elevation) other);
    return (((this.value == rhs.value) || ((this.value != null) && this.value.equals(rhs.value)))
        && ((this.unitCode == rhs.unitCode) || ((this.unitCode != null) && this.unitCode
        .equals(rhs.unitCode))));
  }

}
