package com.nmichail.meridian.shared.organization.di

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.shared.organization.data.api.OrganizationApi
import com.nmichail.meridian.shared.organization.data.api.createOrganizationApi
import com.nmichail.meridian.shared.organization.data.repository.OrganizationRepositoryImpl
import com.nmichail.meridian.shared.organization.domain.repository.OrganizationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import de.jensklingenberg.ktorfit.Ktorfit

@Module
abstract class OrganizationModule {

	companion object {

		@Provides
		@PerFeature
		@JvmStatic
		fun provideOrganizationApi(ktorfit: Ktorfit): OrganizationApi = ktorfit.createOrganizationApi()
	}

	@Binds
	abstract fun bindOrganizationRepository(impl: OrganizationRepositoryImpl): OrganizationRepository
}