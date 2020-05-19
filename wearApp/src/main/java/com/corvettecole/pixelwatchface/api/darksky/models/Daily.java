
package com.corvettecole.pixelwatchface.api.darksky.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Daily {

  @SerializedName("summary")
  @Expose
  private String summary;
  @SerializedName("icon")
  @Expose
  private String icon;
  @SerializedName("data")
  @Expose
  private List<Datum__> data = null;

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

  public List<Datum__> getData() {
    return data;
  }

  public void setData(List<Datum__> data) {
    this.data = data;
  }

}
