package com.aar.app.wsp.di.component;

import com.aar.app.wsp.di.modules.AppModule;
import com.aar.app.wsp.di.modules.DataSourceModule;
import com.aar.app.wsp.features.FullscreenActivity;
import com.aar.app.wsp.features.gamehistory.GameHistoryActivity;
import com.aar.app.wsp.features.gameover.GameOverActivity;
import com.aar.app.wsp.features.gameplay.GamePlayActivity;
import com.aar.app.wsp.features.gamethemeselector.ThemeSelectorActivity;
import com.aar.app.wsp.features.mainmenu.MainMenuActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by abdularis on 18/07/17.
 */

@Singleton
@Component(modules = {AppModule.class, DataSourceModule.class})
public interface AppComponent {

    void inject(GamePlayActivity activity);

    void inject(MainMenuActivity activity);

    void inject(GameOverActivity activity);

    void inject(FullscreenActivity activity);

    void inject(GameHistoryActivity activity);

    void inject(ThemeSelectorActivity activity);

}
