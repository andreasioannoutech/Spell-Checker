package com.kikkos.spellchecker.io;

import com.kikkos.spellchecker.data.BingResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by kikkos on 13/04/2017.
 */

public interface SpellSuggestionApiResponse {

    @GET(".")
    Observable<BingResponse> getSuggestion(@Query("text") String token);
}
