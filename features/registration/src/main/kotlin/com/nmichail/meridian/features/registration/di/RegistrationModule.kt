package com.nmichail.meridian.features.registration.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.registration.data.api.RegistrationApi
import com.nmichail.meridian.features.registration.data.api.createRegistrationApi
import com.nmichail.meridian.features.registration.data.repository.RegistrationRepositoryImpl
import com.nmichail.meridian.features.registration.domain.repository.RegistrationRepository
import com.nmichail.meridian.features.registration.presentation.RegistrationViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import de.jensklingenberg.ktorfit.Ktorfit

@Module
abstract class RegistrationModule {

	companion object {

		@Provides
		@PerFeature
		@JvmStatic
		fun provideRegistrationApi(ktorfit: Ktorfit): RegistrationApi = ktorfit.createRegistrationApi()
	}

	@Binds
	@IntoMap
	@ViewModelKey(RegistrationViewModel::class)
	abstract fun bindRegistrationViewModel(viewModel: RegistrationViewModel): ViewModel

	@Binds
	abstract fun bindRegistrationRepository(impl: RegistrationRepositoryImpl): RegistrationRepository
}
