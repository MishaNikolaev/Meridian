package com.nmichail.meridian.features.calls.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.calls.data.api.CallsApi
import com.nmichail.meridian.features.calls.data.api.createCallsApi
import com.nmichail.meridian.features.calls.data.repository.CallsRepositoryImpl
import com.nmichail.meridian.features.calls.domain.repository.CallsRepository
import com.nmichail.meridian.features.calls.presentation.CallsViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import de.jensklingenberg.ktorfit.Ktorfit

@Module
abstract class CallsFeatureModule {
	companion object {
		@Provides
		@JvmStatic
		fun provideCallsApi(ktorfit: Ktorfit): CallsApi = ktorfit.createCallsApi()
	}

	@Binds
	abstract fun bindCallsRepository(repository: CallsRepositoryImpl): CallsRepository

	@Binds
	@IntoMap
	@ViewModelKey(CallsViewModel::class)
	abstract fun bindCallsViewModel(viewModel: CallsViewModel): ViewModel
}
