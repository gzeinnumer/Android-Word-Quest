package com.aar.app.wsp.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.aar.app.wsp.di.ViewModelFactory
import com.aar.app.wsp.di.ViewModelKey
import com.aar.app.wsp.features.gamehistory.GameHistoryViewModel
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
}