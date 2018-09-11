package com.aar.app.wordsearch.features.mainmenu;

import android.content.Intent;
import android.os.Bundle;

import com.aar.app.wordsearch.R;
import com.aar.app.wordsearch.features.FullscreenActivity;
import com.aar.app.wordsearch.features.gamehistory.GameHistoryActivity;
import com.aar.app.wordsearch.features.gameplay.GamePlayActivity;
import com.aar.app.wordsearch.features.gamethemeselector.ThemeSelectorActivity;
import com.aar.app.wordsearch.model.GameMode;
import com.aar.app.wordsearch.model.GameTheme;
import com.aar.app.wordsearch.features.settings.SettingsActivity;
import com.github.abdularis.horizontalselector.HorizontalSelector;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainMenuActivity extends FullscreenActivity {

    @BindView(R.id.selectorGridSize) HorizontalSelector mGridSizeSelector;
    @BindView(R.id.selectorGameMode) HorizontalSelector mGameModeSelector;

    @BindArray(R.array.game_round_dimension_values)
    int[] mGameRoundDimValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.settings_button)
    public void onSettingsClick() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.new_game_btn)
    public void onNewGameClick() {
        Intent intent = new Intent(this, ThemeSelectorActivity.class);
        startActivityForResult(intent, 100);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @OnClick(R.id.btnHistory)
    public void onHistoryClick() {
        Intent i = new Intent(this, GameHistoryActivity.class);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            startNewGame(
                    data.getIntExtra(ThemeSelectorActivity.EXTRA_THEME_ID, GameTheme.NONE.getId())
            );
        }
    }

    private void startNewGame(int gameThemeId) {
        int dim = mGameRoundDimValues[ mGridSizeSelector.getCurrentIndex() ];
        Intent intent = new Intent(MainMenuActivity.this, GamePlayActivity.class);
        intent.putExtra(GamePlayActivity.EXTRA_GAME_MODE, getGameModeFromSpinner());
        intent.putExtra(GamePlayActivity.EXTRA_GAME_THEME_ID, gameThemeId);
        intent.putExtra(GamePlayActivity.EXTRA_ROW_COUNT, dim);
        intent.putExtra(GamePlayActivity.EXTRA_COL_COUNT, dim);
        startActivity(intent);
    }

    private GameMode getGameModeFromSpinner() {
        if (mGameModeSelector.getCurrentValue() != null) {
            String selected = mGameModeSelector.getCurrentValue();
            if (selected.equals(getString(R.string.mode_hidden))) {
                return GameMode.Hidden;
            } else {
                return GameMode.Normal;
            }
        }
        return GameMode.Normal;
    }
}
