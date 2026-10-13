package com.nmichail.meridian.features.home.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.core.preferences.Preferences
import com.nmichail.meridian.core.validation.NotEmptyValidator
import com.nmichail.meridian.features.home.presentation.HomeStoreFactory
import com.nmichail.meridian.features.home.presentation.HomeViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
abstract class HomeModule {

	@Binds
	@IntoMap
	@ViewModelKey(HomeViewModel::class)
	abstract fun bindHomeViewModel(viewModel: HomeViewModel): ViewModel

	companion object {

		@Provides
		@PerFeature
		@JvmStatic
		fun provideStoreFactory(
			preferences: Preferences,
			validator: NotEmptyValidator,
		): HomeStoreFactory = HomeStoreFactory(
			preferences = preferences,
			validator = validator,
		)
	}
}
