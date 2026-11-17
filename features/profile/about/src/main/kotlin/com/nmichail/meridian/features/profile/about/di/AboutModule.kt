package com.nmichail.meridian.features.profile.about.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.profile.about.presentation.AboutViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class AboutModule {

	@Binds
	@IntoMap
	@ViewModelKey(AboutViewModel::class)
	abstract fun bindAboutViewModel(viewModel: AboutViewModel): ViewModel
}