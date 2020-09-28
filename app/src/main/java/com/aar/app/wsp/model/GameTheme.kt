package com.aar.app.wsp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "game_themes")
class GameTheme @JvmOverloads constructor(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var id: Int = -1,
    @ColumnInfo(name = "name")
    var name: String = ""
) {
    companion object {
        @JvmField
        val NONE = GameTheme(-1, "")
    }
}