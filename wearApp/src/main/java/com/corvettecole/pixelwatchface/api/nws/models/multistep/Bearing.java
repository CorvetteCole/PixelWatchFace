
package com.corvettecole.pixelwatchface.api.nws.models.multistep;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bearing {

    @SerializedName("value")
    @Expose
    private Integer value;
    @SerializedName("unitCode")
    @Expose
    private String unitCode;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

}
