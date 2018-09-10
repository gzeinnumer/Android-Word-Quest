package com.aar.app.wordsearch.data.sqlite;

import android.provider.BaseColumns;

/**
 * Created by abdularis on 18/07/17.
 */

abstract class DbContract {

    static class GameRound implements BaseColumns {
        static final String TABLE_NAME = "game_round";

        static final String COL_NAME = "name";
        static final String COL_DURATION = "duration";
        static final String COL_GRID_ROW_COUNT = "grid_row_count";
        static final String COL_GRID_COL_COUNT = "grid_col_count";
        static final String COL_GRID_DATA = "grid_data";
    }

}
