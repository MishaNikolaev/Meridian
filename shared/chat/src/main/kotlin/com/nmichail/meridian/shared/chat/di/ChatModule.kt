package com.nmichail.meridian.shared.chat.di

import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.api.createChatApi
import com.nmichail.meridian.shared.chat.data.datasource.ChatDataSource
import com.nmichail.meridian.shared.chat.data.datasource.ChatDataSourceImpl
import com.nmichail.meridian.shared.chat.data.datasource.MemberDataSource
import com.nmichail.meridian.shared.chat.data.datasource.MemberDataSourceImpl
import com.nmichail.meridian.shared.chat.data.datasource.MessageDataSource
import com.nmichail.meridian.shared.chat.data.datasource.MessageDataSourceImpl
import com.nmichail.meridian.shared.chat.data.datasource.ReactionDataSource
import com.nmichail.meridian.shared.chat.data.datasource.ReactionDataSourceImpl
import com.nmichail.meridian.shared.chat.data.repository.ChatRepositoryImpl
import com.nmichail.meridian.shared.chat.data.repository.ReactionRepositoryImpl
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import com.nmichail.meridian.shared.chat.domain.repository.ReactionRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import de.jensklingenberg.ktorfit.Ktorfit

@Module
abstract class ChatModule {
	companion object {
		@Provides
		@ChatSessionScope
		@JvmStatic
		fun provideChatApi(ktorfit: Ktorfit): ChatApi = ktorfit.createChatApi()
	}

	@Binds
	@ChatSessionScope
	abstract fun bindChatDataSource(impl: ChatDataSourceImpl): ChatDataSource

	@Binds
	@ChatSessionScope
	abstract fun bindMessageDataSource(impl: MessageDataSourceImpl): MessageDataSource

	@Binds
	abstract fun bindMemberDataSource(impl: MemberDataSourceImpl): MemberDataSource

	@Binds
	abstract fun bindReactionDataSource(impl: ReactionDataSourceImpl): ReactionDataSource

	@Binds
	@ChatSessionScope
	abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

	@Binds
	@ChatSessionScope
	abstract fun bindReactionRepository(impl: ReactionRepositoryImpl): ReactionRepository
}
