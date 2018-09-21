package com.aar.app.wordsearch.data.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL = "https://us-central1-word-search-backend.cloudfunctions.net/api/";
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

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public WordDataService getWordDataService() {
        return mRetrofit.create(WordDataService.class);
    }
}
