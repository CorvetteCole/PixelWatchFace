
package com.corvettecole.pixelwatchface.api.awc.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Properties {

  @SerializedName("data")
  @Expose
  private String data;
  @SerializedName("id")
  @Expose
  private String id;
  @SerializedName("site")
  @Expose
  private String site;
  @SerializedName("prior")
  @Expose
  private String prior;
  @SerializedName("obsTime")
  @Expose
  private String obsTime;
  @SerializedName("temp")
  @Expose
  private Double temp;
  @SerializedName("dewp")
  @Expose
  private Double dewp;
  @SerializedName("wspd")
  @Expose
  private Integer wspd;
  @SerializedName("wdir")
  @Expose
  private Integer wdir;
  @SerializedName("ceil")
  @Expose
  private Integer ceil;
  @SerializedName("cover")
  @Expose
  private String cover;
  @SerializedName("visib")
  @Expose
  private Double visib;
  @SerializedName("fltcat")
  @Expose
  private String fltcat;
  @SerializedName("altim")
  @Expose
  private Double altim;
  @SerializedName("slp")
  @Expose
  private Double slp;
  @SerializedName("wx")
  @Expose
  private String wx;
  @SerializedName("rawOb")
  @Expose
  private String rawOb;
  @SerializedName("rawTaf")
  @Expose
  private String rawTaf;

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSite() {
    return site;
  }

  public void setSite(String site) {
    this.site = site;
  }

  public String getPrior() {
    return prior;
  }

  public void setPrior(String prior) {
    this.prior = prior;
  }

  public String getObsTime() {
    return obsTime;
  }

  public void setObsTime(String obsTime) {
    this.obsTime = obsTime;
  }

  public Double getTemp() {
    return temp;
  }

  public void setTemp(Double temp) {
    this.temp = temp;
  }

  public Double getDewp() {
    return dewp;
  }

  public void setDewp(Double dewp) {
    this.dewp = dewp;
  }

  public Integer getWspd() {
    return wspd;
  }

  public void setWspd(Integer wspd) {
    this.wspd = wspd;
  }

  public Integer getWdir() {
    return wdir;
  }

  public void setWdir(Integer wdir) {
    this.wdir = wdir;
  }

  public Integer getCeil() {
    return ceil;
  }

  public void setCeil(Integer ceil) {
    this.ceil = ceil;
  }

  public String getCover() {
    return cover;
  }

  public void setCover(String cover) {
    this.cover = cover;
  }

  public Double getVisib() {
    return visib;
  }

  public void setVisib(Double visib) {
    this.visib = visib;
  }

  public String getFltcat() {
    return fltcat;
  }

  public void setFltcat(String fltcat) {
    this.fltcat = fltcat;
  }

  public Double getAltim() {
    return altim;
  }

  public void setAltim(Double altim) {
    this.altim = altim;
  }

  public Double getSlp() {
    return slp;
  }

  public void setSlp(Double slp) {
    this.slp = slp;
  }

  public String getWx() {
    return wx;
  }

  public void setWx(String wx) {
    this.wx = wx;
  }

  public String getRawOb() {
    return rawOb;
  }

  public void setRawOb(String rawOb) {
    this.rawOb = rawOb;
  }

  public String getRawTaf() {
    return rawTaf;
  }

  public void setRawTaf(String rawTaf) {
    this.rawTaf = rawTaf;
  }

}
