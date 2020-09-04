package com.aar.app.wsp.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aar.app.wsp.di.ViewModelFactory
import com.aar.app.wsp.di.ViewModelKey
import com.aar.app.wsp.features.gamehistory.GameHistoryViewModel
import com.aar.app.wsp.features.gameover.GameOverViewModel
import com.aar.app.wsp.features.gameplay.GamePlayViewModel
import com.aar.app.wsp.features.gamethemeselector.ThemeSelectorViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    abstract fun provideViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(GameHistoryViewModel::class)
    abstract fun gameHistoryViewModel(vm: GameHistoryViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GameOverViewModel::class)
    abstract fun gameOverViewModel(vm: GameOverViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(GamePlayViewModel::class)
    abstract fun gamePlayViewModel(vm: GamePlayViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ThemeSelectorViewModel::class)
    abstract fun themeSelectorViewModel(vm: ThemeSelectorViewModel): ViewModel
}