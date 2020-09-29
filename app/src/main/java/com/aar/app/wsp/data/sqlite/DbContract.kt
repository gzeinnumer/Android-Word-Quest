package com.aar.app.wsp.data.sqlite

/**
 * Created by abdularis on 18/07/17.
 */
object DbContract {
    object GameRound {
        const val _ID = "_id"
        const val _COUNT = "_count"
        const val TABLE_NAME = "game_round"
        const val COL_NAME = "name"
        const val COL_DURATION = "duration"
        const val COL_GRID_ROW_COUNT = "grid_row_count"
        const val COL_GRID_COL_COUNT = "grid_col_count"
        const val COL_GRID_DATA = "grid_data"
        const val COL_GAME_MODE = "game_mode"
        const val COL_MAX_DURATION = "max_duration"
    }
}