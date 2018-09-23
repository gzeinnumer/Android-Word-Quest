package com.aar.app.wordsearch.data.network;

import android.os.Build;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    // base url api hosted in firebase hosting
    private static final String BASE_URL = "https://word-search-backend.firebaseapp.com/api/";

    // firebase hosting only enable TLS 1.2 protocol, but TLS 1.2 is problematic in android kitkat and below
    // so we need fallback url, it's url from firebase functions which enable TLS 1.0, 1.1, 1.2
    private static final String BASE_URL_FALLBACK = "https://us-central1-word-search-backend.cloudfunctions.net/webApp/api/";

    private static RetrofitClient INSTANCE = null;

    private Retrofit mRetrofit;

    public static RetrofitClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RetrofitClient();
        }
        return INSTANCE;
    }

    private RetrofitClient() {
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        String baseUrl;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            baseUrl = BASE_URL;
        } else {
            baseUrl = BASE_URL_FALLBACK;
        }

        mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public WordDataService getWordDataService() {
        return mRetrofit.create(WordDataService.class);
    }
}
