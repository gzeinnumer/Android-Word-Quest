package com.aar.app.wordsearch.features.mainmenu;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.aar.app.wordsearch.R;
import com.aar.app.wordsearch.features.FullscreenActivity;
import com.aar.app.wordsearch.features.gamehistory.GameHistoryActivity;
import com.aar.app.wordsearch.features.gameplay.GamePlayActivity;
import com.aar.app.wordsearch.features.gamethemeselector.ThemeSelectorActivity;
import com.aar.app.wordsearch.model.GameTheme;
import com.aar.app.wordsearch.features.settings.SettingsActivity;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainMenuActivity extends FullscreenActivity {

    @BindView(R.id.spinnerGridSize) Spinner mGridSizeSpinner;

    @BindArray(R.array.game_round_dimension_values)
    int[] mGameRoundDimValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ButterKnife.bind(this);
        initSpinnersData();
    }

    private void initSpinnersData() {
        mGridSizeSpinner.setAdapter(new ArrayAdapter<>(
                this,
                R.layout.item_spinner,
                getResources().getStringArray(R.array.game_round_dimensions)
        ));
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
        int dim = mGameRoundDimValues[ mGridSizeSpinner.getSelectedItemPosition() ];
        Intent intent = new Intent(MainMenuActivity.this, GamePlayActivity.class);
        intent.putExtra(GamePlayActivity.EXTRA_GAME_THEME_ID, gameThemeId);
        intent.putExtra(GamePlayActivity.EXTRA_ROW_COUNT, dim);
        intent.putExtra(GamePlayActivity.EXTRA_COL_COUNT, dim);
        startActivity(intent);
    }
}
