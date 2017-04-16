package com.kikkos.spellchecker.io;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by kikkos on 13/04/2017.
 */

public class ApiClient {
    public static final String BASE_URL = "https://api.cognitive.microsoft.com/bing/v5.0/spellcheck/";
    private static Retrofit retrofit = null;
    // adding  header and query parameter to all requests
    private static OkHttpClient okHttpClient = new OkHttpClient().newBuilder().addInterceptor(new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            // get url and add constant parameter for spell mode
            HttpUrl originalHttpUrl = originalRequest.url();
            HttpUrl url = originalHttpUrl.newBuilder().addQueryParameter("mode", "spell").build();
            // request customization, here we add the header and the constant parameter
            Request.Builder builder = originalRequest
                    .newBuilder()
                    .url(url)
                    .header("Ocp-Apim-Subscription-Key", "BING SPELL CHECK API KEY HERE");

            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        }
    }).build();

    public static Retrofit getClient() {
        if (retrofit == null && okHttpClient != null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
