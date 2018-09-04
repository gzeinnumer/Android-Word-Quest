package com.aar.app.wordsearch.features.mainmenu;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.aar.app.wordsearch.R;
import com.aar.app.wordsearch.features.ViewModelFactory;
import com.aar.app.wordsearch.WordSearchApp;
import com.aar.app.wordsearch.features.FullscreenActivity;
import com.aar.app.wordsearch.features.gamehistory.GameHistoryActivity;
import com.aar.app.wordsearch.features.gameplay.GamePlayActivity;
import com.aar.app.wordsearch.model.GameTheme;
import com.aar.app.wordsearch.features.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainMenuActivity extends FullscreenActivity {

    @BindView(R.id.spinnerGridSize) Spinner mGridSizeSpinner;
    @BindView(R.id.spinnerTheme) Spinner mThemeSpinner;
    private ArrayAdapter<String> mThemeSpinnerAdapter;

    @BindArray(R.array.game_round_dimension_values)
    int[] mGameRoundDimValues;

    @Inject
    ViewModelFactory mViewModelFactory;
    MainMenuViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ButterKnife.bind(this);
        initSpinnersData();

        ((WordSearchApp) getApplication()).getAppComponent().inject(this);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MainMenuViewModel.class);
        mViewModel.getOnGameThemeLoaded().observe(this, this::showThemeList);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.loadThemes();
    }

    private void initSpinnersData() {
        mGridSizeSpinner.setAdapter(new ArrayAdapter<>(
                this,
                R.layout.item_spinner,
                getResources().getStringArray(R.array.game_round_dimensions)
        ));

        mThemeSpinnerAdapter = new ArrayAdapter<>(
                this,
                R.layout.item_spinner
        );
        mThemeSpinner.setAdapter(mThemeSpinnerAdapter);
    }

    private void showThemeList(List<GameTheme> gameThemes) {
        mThemeSpinnerAdapter.clear();
        mThemeSpinnerAdapter.addAll(getStringListFromThemes(gameThemes));
    }

    private List<String> getStringListFromThemes(List<GameTheme> gameThemes) {
        List<String> strings = new ArrayList<>();
        for (GameTheme gameTheme : gameThemes) {
            strings.add(gameTheme.getName());
        }
        return strings;
    }

    @OnClick(R.id.settings_button)
    public void onSettingsClick() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.new_game_btn)
    public void onNewGameClick() {
        int dim = mGameRoundDimValues[ mGridSizeSpinner.getSelectedItemPosition() ];
        Intent intent = new Intent(MainMenuActivity.this, GamePlayActivity.class);
        intent.putExtra(GamePlayActivity.EXTRA_ROW_COUNT, dim);
        intent.putExtra(GamePlayActivity.EXTRA_COL_COUNT, dim);
        startActivity(intent);
    }

    @OnClick(R.id.btnHistory)
    public void onHistoryClick() {
        Intent i = new Intent(this, GameHistoryActivity.class);
        startActivity(i);
    }

}
