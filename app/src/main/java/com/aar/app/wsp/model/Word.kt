package com.aar.app.wsp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by abdularis on 08/07/17.
 */
@Entity(tableName = "words")
open class Word @JvmOverloads constructor (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "game_theme_id")
    var gameThemeId: Int = 0,

    @ColumnInfo(name = "string")
    var string: String = ""
)