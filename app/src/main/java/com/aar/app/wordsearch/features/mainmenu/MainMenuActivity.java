package com.aar.app.wordsearch.features.mainmenu;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.aar.app.wordsearch.R;
import com.aar.app.wordsearch.easyadapter.MultiTypeAdapter;
import com.aar.app.wordsearch.features.ViewModelFactory;
import com.aar.app.wordsearch.WordSearchApp;
import com.aar.app.wordsearch.features.FullscreenActivity;
import com.aar.app.wordsearch.features.gamehistory.GameHistoryActivity;
import com.aar.app.wordsearch.features.gameplay.GamePlayActivity;
import com.aar.app.wordsearch.features.gamethemeselector.ThemeSelectorActivity;
import com.aar.app.wordsearch.model.GameTheme;
import com.aar.app.wordsearch.features.settings.SettingsActivity;

import javax.inject.Inject;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainMenuActivity extends FullscreenActivity {

    @BindView(R.id.rvThemes)
    RecyclerView mRvThemes;
    @BindView(R.id.spinnerGridSize) Spinner mGridSizeSpinner;

    @BindArray(R.array.game_round_dimension_values)
    int[] mGameRoundDimValues;

    @Inject
    ViewModelFactory mViewModelFactory;
    MainMenuViewModel mViewModel;

    private MultiTypeAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        ButterKnife.bind(this);
        initSpinnersData();

        ((WordSearchApp) getApplication()).getAppComponent().inject(this);

        mAdapter = new MultiTypeAdapter();
        mAdapter.addDelegate(
                GameTheme.class,
                R.layout.item_theme,
                (model, holder) -> holder.<TextView>find(R.id.textTheme).setText(model.getName()),
                null
        );

        mRvThemes.setLayoutManager(new GridLayoutManager(this, 3));
        mRvThemes.setAdapter(mAdapter);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(MainMenuViewModel.class);
        mViewModel.getOnGameThemeLoaded().observe(this, mAdapter::setItems);
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

//        int dim = mGameRoundDimValues[ mGridSizeSpinner.getSelectedItemPosition() ];
//        Intent intent = new Intent(MainMenuActivity.this, GamePlayActivity.class);
//        intent.putExtra(GamePlayActivity.EXTRA_GAME_THEME_ID, -1);
//        intent.putExtra(GamePlayActivity.EXTRA_ROW_COUNT, dim);
//        intent.putExtra(GamePlayActivity.EXTRA_COL_COUNT, dim);
//        startActivity(intent);
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
