package com.nmichail.meridian.shared.session.di

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.shared.session.data.api.AuthorizationApi
import com.nmichail.meridian.shared.session.data.api.createAuthorizationApi
import com.nmichail.meridian.shared.session.data.repository.AuthorizationRepositoryImpl
import com.nmichail.meridian.shared.session.domain.repository.AuthorizationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import de.jensklingenberg.ktorfit.Ktorfit

@Module
abstract class AuthorizationModule {

	companion object {

		@Provides
		@PerFeature
		@JvmStatic
		fun provideAuthorizationApi(ktorfit: Ktorfit): AuthorizationApi = ktorfit.createAuthorizationApi()
	}

	@Binds
	abstract fun bindAuthorizationRepository(impl: AuthorizationRepositoryImpl): AuthorizationRepository
}
