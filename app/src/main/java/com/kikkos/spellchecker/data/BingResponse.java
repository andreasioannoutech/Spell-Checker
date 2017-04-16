package com.kikkos.spellchecker.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by kikkos on 14/04/2017.
 */

public class BingResponse {

    @SerializedName("_type")
    private String type;

    @SerializedName("flaggedTokens")
    private List<BingFlaggedToken> flaggedTokens;

    public BingResponse(String type, List<BingFlaggedToken> flaggedTokens) {
        this.type = type;
        this.flaggedTokens = flaggedTokens;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<BingFlaggedToken> getFlaggedTokens() {
        return flaggedTokens;
    }

    public void setFlaggedTokens(List<BingFlaggedToken> flaggedTokens) {
        this.flaggedTokens = flaggedTokens;
    }
}
