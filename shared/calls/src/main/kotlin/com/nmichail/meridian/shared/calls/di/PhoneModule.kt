package com.nmichail.meridian.shared.calls.di

import com.nmichail.meridian.shared.chat.di.ChatSessionScope
import com.nmichail.meridian.shared.calls.data.api.PhoneApi
import com.nmichail.meridian.shared.calls.data.api.createPhoneApi
import com.nmichail.meridian.shared.calls.data.repository.PhoneRepositoryImpl
import com.nmichail.meridian.shared.calls.domain.repository.PhoneRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import de.jensklingenberg.ktorfit.Ktorfit

@Module
abstract class PhoneModule {
	companion object {
		@Provides
		@ChatSessionScope
		@JvmStatic
		fun providePhoneApi(ktorfit: Ktorfit): PhoneApi = ktorfit.createPhoneApi()
	}

	@Binds
	@ChatSessionScope
	abstract fun bindPhoneRepository(impl: PhoneRepositoryImpl): PhoneRepository
}