package com.corvettecole.pixelwatchface.models.metar;

public class WeatherCondition {

  private Descriptor mDescriptor;
  private Intensity mIntensity;
  private Precipitation mPrecipitation;

  public WeatherCondition(Precipitation precipitation, Intensity intensity, Descriptor descriptor) {
    this.mDescriptor = descriptor;
    this.mIntensity = intensity;
    this.mPrecipitation = precipitation;
  }

  public WeatherCondition() {
  }

  public Descriptor getDescriptor() {
    return mDescriptor;
  }

  public void setDescriptor(Descriptor mDescriptor) {
    this.mDescriptor = mDescriptor;
  }

  public Intensity getIntensity() {
    return mIntensity;
  }

  public void setIntensity(Intensity mIntensity) {
    this.mIntensity = mIntensity;
  }

  public Precipitation getPrecipitation() {
    return mPrecipitation;
  }

  public void setPrecipitation(Precipitation mPrecipitation) {
    this.mPrecipitation = mPrecipitation;
  }
}
