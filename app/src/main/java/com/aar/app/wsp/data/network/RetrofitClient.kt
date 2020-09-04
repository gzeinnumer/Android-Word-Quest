package com.aar.app.wsp.data.network

import android.os.Build
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient private constructor() {
    private val retrofit: Retrofit

    val wordDataService: WordDataService
        get() = retrofit.create(WordDataService::class.java)

    companion object {
        // base url api hosted in firebase hosting
        private const val BASE_URL = "https://word-search-backend.firebaseapp.com/api/"

        // firebase hosting only enable TLS 1.2 protocol, but TLS 1.2 is problematic in android kitkat and below
        // so we need fallback url, it's url from firebase functions which enable TLS 1.0, 1.1, 1.2
        private const val BASE_URL_FALLBACK = "https://us-central1-word-search-backend.cloudfunctions.net/webApp/api/"

        private var INSTANCE: RetrofitClient? = null

        @JvmStatic
        val instance: RetrofitClient?
            get() {
                if (INSTANCE == null) {
                    INSTANCE = RetrofitClient()
                }
                return INSTANCE
            }
    }

    init {
        val gson = GsonBuilder()
            .setLenient()
            .create()
        val baseUrl = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BASE_URL
        } else {
            BASE_URL_FALLBACK
        }
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}