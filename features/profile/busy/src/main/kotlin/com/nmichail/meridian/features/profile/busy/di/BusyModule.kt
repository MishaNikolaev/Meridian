package com.nmichail.meridian.features.profile.busy.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.profile.busy.data.api.BusyApi
import com.nmichail.meridian.features.profile.busy.data.api.createBusyApi
import com.nmichail.meridian.features.profile.busy.data.repository.BusyRepositoryImpl
import com.nmichail.meridian.features.profile.busy.domain.repository.BusyRepository
import com.nmichail.meridian.features.profile.busy.presentation.BusyViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import de.jensklingenberg.ktorfit.Ktorfit

@Module
abstract class BusyModule {

	companion object {

		@Provides
		@PerFeature
		@JvmStatic
		fun provideBusyApi(ktorfit: Ktorfit): BusyApi = ktorfit.createBusyApi()
	}

	@Binds
	abstract fun bindBusyRepository(impl: BusyRepositoryImpl): BusyRepository

	@Binds
	@IntoMap
	@ViewModelKey(BusyViewModel::class)
	abstract fun bindBusyViewModel(viewModel: BusyViewModel): ViewModel
}