package com.aar.app.wsp.data.network.responses

import com.google.gson.annotations.SerializedName

class ThemeResponse {
    @SerializedName("theme")
    var name: String? = null

    @SerializedName("words")
    var words: List<String>? = null
}