package com.aar.app.wsp.features.gameover;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.core.app.NavUtils;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.aar.app.wsp.R;
import com.aar.app.wsp.features.ViewModelFactory;
import com.aar.app.wsp.WordSearchApp;
import com.aar.app.wsp.commons.DurationFormatter;
import com.aar.app.wsp.features.gameplay.GamePlayActivity;
import com.aar.app.wsp.model.GameData;
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

    @BindView(R.id.textCongrat) TextView mTextCongrat;
    @BindView(R.id.game_stat_text) TextView mTextGameStat;

    private GameOverViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        ButterKnife.bind(this);
        ((WordSearchApp) getApplication()).getAppComponent().inject(this);

        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(GameOverViewModel.class);
        mViewModel.getOnGameDataLoaded().observe(this, this::showGameStat);
        mViewModel.getOnGameDataReset().observe(this, gameDataId -> {
            Intent i = new Intent(GameOverActivity.this, GamePlayActivity.class);
            i.putExtra(GamePlayActivity.EXTRA_GAME_DATA_ID, gameDataId);
            startActivity(i);
            finish();
        });

        if (getIntent().getExtras() != null) {
            int gid = getIntent().getExtras().getInt(EXTRA_GAME_ROUND_ID);
            mViewModel.loadData(gid);
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
            mViewModel.deleteGameRound();
        }
        NavUtils.navigateUpTo(this, new Intent());
        finish();
    }

    public void showGameStat(GameData gd) {
        if (gd.isGameOver()) {
            mTextCongrat.setText(R.string.lbl_game_over);
            mTextGameStat.setVisibility(View.GONE);
        } else {
            String strGridSize = gd.getGrid().getRowCount() + " x " + gd.getGrid().getColCount();

            String str = getString(R.string.finish_text);
            str = str.replaceAll(":gridSize", strGridSize);
            str = str.replaceAll(":uwCount", String.valueOf(gd.getUsedWords().size()));
            str = str.replaceAll(":duration", DurationFormatter.fromInteger(gd.getDuration()));

            mTextCongrat.setText(R.string.congratulations);
            mTextGameStat.setVisibility(View.VISIBLE);
            mTextGameStat.setText(str);
        }
    }
}
