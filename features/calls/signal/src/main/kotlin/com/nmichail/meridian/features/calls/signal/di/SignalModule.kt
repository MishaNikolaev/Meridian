package com.nmichail.meridian.features.calls.signal.di

import com.nmichail.meridian.shared.chat.di.ChatSessionScope
import com.nmichail.meridian.features.calls.signal.data.api.SignalApi
import com.nmichail.meridian.features.calls.signal.data.api.createSignalApi
import com.nmichail.meridian.features.calls.signal.data.repository.SignalRepositoryImpl
import com.nmichail.meridian.features.calls.signal.domain.repository.SignalRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import de.jensklingenberg.ktorfit.Ktorfit

@Module
abstract class SignalModule {
	companion object {
		@Provides
		@ChatSessionScope
		@JvmStatic
		fun provideSignalApi(ktorfit: Ktorfit): SignalApi = ktorfit.createSignalApi()
	}

	@Binds
	@ChatSessionScope
	abstract fun bindSignalRepository(impl: SignalRepositoryImpl): SignalRepository
}
