package com.corvettecole.pixelwatchface.api.awc.models.metar;

/**
 * Enumeration for indicator. The first attribute is the code used in the metar. The second
 * attribute is the meaning of the code.
 */
public enum Intensity {
  /**
   * Light intensity.
   */
  LIGHT("-"),
  /**
   * Moderate intensity
   */
  MODERATE(""),
  /**
   * Heavy intensity.
   */
  HEAVY("+"),
  /**
   * In vicinity.
   */
  IN_VICINITY("VC");

  /**
   * The shortcut of the intensity.
   */
  private String mCode = "";

  /**
   * Constructor.
   *
   * @param code A String for the shortcut.
   */
  Intensity(final String code) {
    mCode = code;

  }

  /**
   * Returns the enum with the same shortcut than the value.
   *
   * @param code String of the intensity searched.
   * @return a intensity with the same code.
   * @throws IllegalArgumentException error if not found.
   */
  public static Intensity getEnum(final String code) throws IllegalArgumentException {
    for (Intensity v : values()) {
      if (v.getCode().equalsIgnoreCase(code)) {
        return v;
      }
    }
    throw new IllegalArgumentException();
  }

  /**
   * Returns shortcut.
   *
   * @return string.
   */
  public String getCode() {
    return mCode;
  }
}
