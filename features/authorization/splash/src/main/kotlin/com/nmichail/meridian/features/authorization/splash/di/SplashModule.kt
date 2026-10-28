package com.nmichail.meridian.features.authorization.splash.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.authorization.splash.presentation.SplashViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class SplashModule {

	@Binds
	@IntoMap
	@ViewModelKey(SplashViewModel::class)
	abstract fun bindSplashViewModel(viewModel: SplashViewModel): ViewModel
}