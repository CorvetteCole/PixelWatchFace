
package com.corvettecole.pixelwatchface.api.darksky.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Minutely {

  @SerializedName("summary")
  @Expose
  private String summary;
  @SerializedName("icon")
  @Expose
  private String icon;
  @SerializedName("data")
  @Expose
  private List<Datum> data = null;

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public List<Datum> getData() {
    return data;
  }

  public void setData(List<Datum> data) {
    this.data = data;
  }

}
