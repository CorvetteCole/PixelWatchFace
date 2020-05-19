package com.corvettecole.pixelwatchface.api.awc.models.metar;

/**
 * Enumeration for phenomenon. The first attribute is the code used in the metar.
 */
public enum Precipitation {
  /**
   * Rain.
   */
  RAIN("RA"),
  /**
   * Drizzle.
   */
  DRIZZLE("DZ"),
  /**
   * Snow.
   */
  SNOW("SN"),
  /**
   * Snow grains.
   */
  SNOW_GRAINS("SG"),
  /**
   * Ice pellets.
   */
  ICE_PELLETS("PL"),
  /**
   * Ice crystals.
   */
  ICE_CRYSTALS("IC"),
  /**
   * Hail.
   */
  HAIL("GR"),
  /**
   * Small hail.
   */
  SMALL_HAIL("GS"),
  /**
   * Unknown precipitation.
   */
  UNKNOWN_PRECIPITATION("UP"),
  /**
   * Fog.
   */
  FOG("FG"),
  /**
   * Volcanic ashes.
   */
  VOLCANIC_ASH("VA"),
  /**
   * Mist.
   */
  MIST("BR"),
  /**
   * Haze.
   */
  HAZE("HZ"),
  /**
   * Widespread dust.
   */
  WIDESPREAD_DUST("DU"),
  /**
   * Smoke.
   */
  SMOKE("FU"),
  /**
   * Sand.
   */
  SAND("SA"),
  /**
   * Spray.
   */
  SPRAY("PY"),
  /**
   * Squall.
   */
  SQUALL("SQ"),
  /**
   * Sand whirl.
   */
  SAND_WHIRLS("PO"),
  /**
   * Duststorm.
   */
  DUSTSTORM("DS"),
  /**
   * Sandstorm.
   */
  SANDSTORM("SS"),
  /**
   * Funnel cloud.
   */
  FUNNEL_CLOUD("FC");

  /**
   * Shortcut of the phenomenon.
   */
  private String mCode;

  /**
   * Constructor.
   *
   * @param code string for the shortcut
   **/
  Precipitation(final String code) {
    mCode = code;
  }

  public static Precipitation getEnum(final String code) throws IllegalArgumentException {
    for (Precipitation v : values()) {
      if (v.getCode().equalsIgnoreCase(code)) {
        return v;
      }
    }
    throw new IllegalArgumentException();
  }


  /**
   * Returns the shortcut.
   *
   * @return string.
   */
  public String getCode() {
    return mCode;
  }
}
