package com.ad.sukasukanginep.Model;

import com.google.gson.annotations.SerializedName;

public class ValueUploadGambar {

    public boolean success;
    @SerializedName("message")
    public String message;

    public String getMessage() {
        return message;
    }

    public boolean getSuccess() {
        return success;
    }

}
