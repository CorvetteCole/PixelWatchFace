package com.corvettecole.pixelwatchface.models.metar;

/**
 * Enumeration for descriptive. The first attribute is the code used in the metar. The second
 * attribute is the meaning of the code.
 *
 * @author mivek
 */
public enum Descriptor {
  /**
   * Showers.
   */
  SHOWERS("SH"),
  /**
   * Shallow.
   */
  SHALLOW("MI"),
  /**
   * Patches.
   */
  PATCHES("BC"),
  /**
   * Partial.
   */
  PARTIAL("PR"),
  /**
   * Low drifting.
   */
  DRIFTING("DR"),
  /**
   * Thunderstorm.
   */
  THUNDERSTORM("TS"),
  /**
   * blowing.
   */
  BLOWING("BL"),
  /**
   * Freezing.
   */
  FREEZING("FZ");

  /**
   * Shortcut of the descriptive.
   */
  private String mCode = "";


  /**
   * Constructor.
   *
   * @param code A string for the shortcut.
   */
  Descriptor(final String code) {
    mCode = code;
  }

  public static Descriptor getEnum(final String code) throws IllegalArgumentException {
    for (Descriptor v : values()) {
      if (v.getCode().equalsIgnoreCase(code)) {
        return v;
      }
    }
    throw new IllegalArgumentException();
  }

  /**
   * return cod.
   *
   * @return string.
   */
  public String getCode() {
    return mCode;
  }
}
