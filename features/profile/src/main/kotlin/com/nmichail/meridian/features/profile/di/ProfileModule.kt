package com.nmichail.meridian.features.profile.di

import androidx.lifecycle.ViewModel
import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.presentation.di.ViewModelKey
import com.nmichail.meridian.features.profile.data.api.ProfileApi
import com.nmichail.meridian.features.profile.data.api.createProfileApi
import com.nmichail.meridian.features.profile.data.repository.ProfileRepositoryImpl
import com.nmichail.meridian.features.profile.domain.repository.ProfileRepository
import com.nmichail.meridian.features.profile.presentation.ProfileViewModel
import com.nmichail.meridian.shared.session.di.AuthorizationModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import de.jensklingenberg.ktorfit.Ktorfit

@Module(includes = [AuthorizationModule::class])
abstract class ProfileModule {

	companion object {

		@Provides
		@PerFeature
		@JvmStatic
		fun provideProfileApi(ktorfit: Ktorfit): ProfileApi = ktorfit.createProfileApi()
	}

	@Binds
	abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

	@Binds
	@IntoMap
	@ViewModelKey(ProfileViewModel::class)
	abstract fun bindProfileViewModel(viewModel: ProfileViewModel): ViewModel
}
