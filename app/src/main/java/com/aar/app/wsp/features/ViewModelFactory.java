package com.aar.app.wsp.features;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.aar.app.wsp.features.gamehistory.GameHistoryViewModel;
import com.aar.app.wsp.features.gameover.GameOverViewModel;
import com.aar.app.wsp.features.gameplay.GamePlayViewModel;
import com.aar.app.wsp.features.gamethemeselector.ThemeSelectorViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private GameOverViewModel mGameOverViewModel;
    private GamePlayViewModel mGamePlayViewModel;
    private GameHistoryViewModel mGameHistoryViewModel;
    private ThemeSelectorViewModel mThemeSelectorViewModel;

    public ViewModelFactory(GameOverViewModel gameOverViewModel,
                            GamePlayViewModel gamePlayViewModel,
                            GameHistoryViewModel gameHistoryViewModel,
                            ThemeSelectorViewModel themeSelectorViewModel) {
        mGameOverViewModel = gameOverViewModel;
        mGamePlayViewModel = gamePlayViewModel;
        mGameHistoryViewModel = gameHistoryViewModel;
        mThemeSelectorViewModel = themeSelectorViewModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(GameOverViewModel.class)) {
            return (T) mGameOverViewModel;
        } else if (modelClass.isAssignableFrom(GamePlayViewModel.class)) {
            return (T) mGamePlayViewModel;
        } else if (modelClass.isAssignableFrom(GameHistoryViewModel.class)) {
            return (T) mGameHistoryViewModel;
        } else if (modelClass.isAssignableFrom(ThemeSelectorViewModel.class)) {
            return (T) mThemeSelectorViewModel;
        }
        throw new IllegalArgumentException("Unknown view model");
    }
}
