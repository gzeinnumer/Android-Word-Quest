package com.aar.app.wsp.features.gameover;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.widget.TextView;

import com.aar.app.wsp.R;
import com.aar.app.wsp.features.ViewModelFactory;
import com.aar.app.wsp.WordSearchApp;
import com.aar.app.wsp.commons.DurationFormatter;
import com.aar.app.wsp.features.gameplay.GamePlayActivity;
import com.aar.app.wsp.model.GameDataInfo;
import com.aar.app.wsp.features.FullscreenActivity;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GameOverActivity extends FullscreenActivity {
    public static final String EXTRA_GAME_ROUND_ID =
            "com.paperplanes.wsp.presentation.ui.activity.GameOverActivity";

    @Inject
    ViewModelFactory mViewModelFactory;

    @BindView(R.id.game_stat_text)
    TextView mGameStatText;

    private int mGameId;
    private GameOverViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        ButterKnife.bind(this);
        ((WordSearchApp) getApplication()).getAppComponent().inject(this);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(GameOverViewModel.class);
        mViewModel.getOnGameDataInfoLoaded().observe(this, this::showGameStat);
        mViewModel.getOnGameDataReset().observe(this, gameDataId -> {
            Intent i = new Intent(GameOverActivity.this, GamePlayActivity.class);
            i.putExtra(GamePlayActivity.EXTRA_GAME_DATA_ID, gameDataId);
            startActivity(i);
            finish();
        });

        if (getIntent().getExtras() != null) {
            mGameId = getIntent().getExtras().getInt(EXTRA_GAME_ROUND_ID);
            mViewModel.loadData(mGameId);
        }
    }

    @OnClick(R.id.main_menu_btn)
    public void onMainMenuClick() {
        onBackPressed();
    }

    @OnClick(R.id.btnReplay)
    public void onReplayClick() {
        mViewModel.resetCurrentGameData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        goToMainMenu();
    }

    private void goToMainMenu() {
        if (getPreferences().deleteAfterFinish()) {
            mViewModel.deleteGameRound(mGameId);
        }
        NavUtils.navigateUpTo(this, new Intent());
        finish();
    }

    public void showGameStat(GameDataInfo info) {
        String strGridSize = info.getGridRowCount() + " x " + info.getGridColCount();

        String str = getString(R.string.finish_text);
        str = str.replaceAll(":gridSize", strGridSize);
        str = str.replaceAll(":uwCount", String.valueOf(info.getUsedWordsCount()));
        str = str.replaceAll(":duration", DurationFormatter.fromInteger(info.getDuration()));

        mGameStatText.setText(str);
    }
}
