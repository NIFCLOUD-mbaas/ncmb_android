package com.nifcloud.mbaas.core.model;

import com.google.gson.annotations.SerializedName;

public class NcmbSetting {
    @SerializedName("ncmb")
    private Ncmb ncmb;

    public Ncmb getNcmb() {
        return ncmb;
    }

    public void setNcmb(Ncmb ncmb) {
        this.ncmb = ncmb;
    }

    @Override
    public String toString() {
        return "NcmbSetting{" +
                "ncmb=" + ncmb +
                '}';
    }
}
