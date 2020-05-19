package com.corvettecole.pixelwatchface.models.metar;

/**
 * Enumeration for cloud quantity. The first attribute is the code used in the metar. The second
 * attribute is the meaning of the code.
 *
 * @author mivek
 */
public enum CloudQuantity {
  /**
   * Sky clear.
   */
  SKC("SKC"),
  /**
   * Few clouds.
   */
  FEW("FEW"),
  /**
   * Broken ceiling.
   */
  BKN("BKN"),
  /**
   * Scattered.
   */
  SCT("SCT"),
  /**
   * Overcast.
   */
  OVC("OVC"),
  /**
   * No significant cloud.
   */
  NSC("NSC");

  /**
   * Shortcut of the cloud quanity.
   */
  private String mCode = "";


  /**
   * Constructor.
   *
   * @param code a string representing the shortcut.
   */
  CloudQuantity(final String code) {
    mCode = code;

  }

  public static CloudQuantity getEnum(final String code) throws IllegalArgumentException {
    for (CloudQuantity v : values()) {
      if (v.getCode().equalsIgnoreCase(code)) {
        return v;
      }
    }
    throw new IllegalArgumentException();
  }


  /**
   * Returns the code.
   *
   * @return a string.
   */
  public String getCode() {
    return mCode;
  }

}
