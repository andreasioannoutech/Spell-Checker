package com.kikkos.spellchecker.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kikkos on 14/04/2017.
 */

public class BingSuggestion {

    @SerializedName("suggestion")
    private String suggestion;

    @SerializedName("score")
    private int score;

    public BingSuggestion(String suggestion, int score) {
        this.suggestion = suggestion;
        this.score = score;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
