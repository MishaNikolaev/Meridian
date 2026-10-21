package com.nmichail.meridian.shared.chat.di

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.api.createChatApi
import com.nmichail.meridian.shared.chat.data.repository.ChatRepositoryImpl
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import de.jensklingenberg.ktorfit.Ktorfit

@Module
abstract class ChatModule {

	companion object {

		@Provides
		@PerFeature
		@JvmStatic
		fun provideChatApi(ktorfit: Ktorfit): ChatApi = ktorfit.createChatApi()
	}

	@Binds
	abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
}
