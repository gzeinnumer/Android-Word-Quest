package com.aar.app.wsp.data.network

import com.aar.app.wsp.data.network.responses.WordsUpdateResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface WordDataService {
    @GET("words")
    fun fetchWordsData(@Query("revision") currentRevision: Int): Observable<WordsUpdateResponse>
}