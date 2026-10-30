package com.nmichail.meridian.features.person.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.person.data.api.PersonApi
import com.nmichail.meridian.features.person.data.api.createPersonApi
import com.nmichail.meridian.features.person.data.repository.PersonRepositoryImpl
import com.nmichail.meridian.features.person.domain.repository.PersonRepository
import com.nmichail.meridian.features.person.presentation.PersonViewModel
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import de.jensklingenberg.ktorfit.Ktorfit

@Module
abstract class PersonModule {

	companion object {

		@Provides
		@PerFeature
		@JvmStatic
		fun providePersonApi(ktorfit: Ktorfit): PersonApi = ktorfit.createPersonApi()
	}

	@Binds
	abstract fun bindPersonRepository(impl: PersonRepositoryImpl): PersonRepository

	@Binds
	@IntoMap
	@ViewModelKey(PersonViewModel::class)
	abstract fun bindPersonViewModel(viewModel: PersonViewModel): ViewModel
}
