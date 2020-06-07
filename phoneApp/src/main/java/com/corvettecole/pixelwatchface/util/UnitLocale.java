package com.corvettecole.pixelwatchface.util;

import java.util.Locale;

public class UnitLocale {

  public static UnitLocale Imperial = new UnitLocale();
  public static UnitLocale Metric = new UnitLocale();

  public static UnitLocale getDefault() {
    return getFrom(Locale.getDefault());
  }

  public static UnitLocale getFrom(Locale locale) {
    String countryCode = locale.getCountry();
    if ("US".equals(countryCode)) {
      return Imperial; // USA
    }
    if ("LR".equals(countryCode)) {
      return Imperial; // Liberia
    }
    if ("MM".equals(countryCode)) {
      return Imperial; // Myanmar
    }
    return Metric;
  }
}
