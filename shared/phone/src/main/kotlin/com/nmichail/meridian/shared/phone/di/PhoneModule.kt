package com.nmichail.meridian.shared.phone.di

import com.nmichail.meridian.shared.chat.di.ChatSessionScope
import com.nmichail.meridian.shared.phone.data.api.PhoneApi
import com.nmichail.meridian.shared.phone.data.api.createPhoneApi
import com.nmichail.meridian.shared.phone.data.repository.PhoneRepositoryImpl
import com.nmichail.meridian.shared.phone.domain.repository.PhoneRepository
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