package com.nmichail.meridian.shared.busy.di

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.shared.busy.data.api.BusyApi
import com.nmichail.meridian.shared.busy.data.api.createBusyApi
import com.nmichail.meridian.shared.busy.data.repository.BusyRepositoryImpl
import com.nmichail.meridian.shared.busy.domain.repository.BusyRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
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
}
