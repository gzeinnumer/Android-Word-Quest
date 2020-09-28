package com.aar.app.wsp.features.gamethemeselector

import androidx.room.ColumnInfo

class GameThemeItem {
    @ColumnInfo(name = "id")
    var id = 0

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "words_count")
    var wordsCount = 0

}