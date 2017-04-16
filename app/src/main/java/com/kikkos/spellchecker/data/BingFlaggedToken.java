package com.kikkos.spellchecker.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by kikkos on 14/04/2017.
 */

public class BingFlaggedToken {

    @SerializedName("offset")
    private int offset;

    @SerializedName("token")
    private String token;

    @SerializedName("type")
    private String type;

    @SerializedName("suggestions")
    private List<BingSuggestion> suggestions;

    public BingFlaggedToken(int offset, String token, String type, List<BingSuggestion> suggestions) {
        this.offset = offset;
        this.token = token;
        this.type = type;
        this.suggestions = suggestions;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<BingSuggestion> getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(List<BingSuggestion> suggestions) {
        this.suggestions = suggestions;
    }
}
