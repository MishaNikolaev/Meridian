package com.nmichail.meridian.shared.chat.di

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.shared.chat.data.api.ChatApi
import com.nmichail.meridian.shared.chat.data.api.ChatEditApi
import com.nmichail.meridian.shared.chat.data.api.createChatApi
import com.nmichail.meridian.shared.chat.data.api.createChatEditApi
import com.nmichail.meridian.shared.chat.data.datasource.ChatCacheDataSource
import com.nmichail.meridian.shared.chat.data.datasource.ChatCacheDataSourceImpl
import com.nmichail.meridian.shared.chat.data.datasource.DeletionDataSource
import com.nmichail.meridian.shared.chat.data.datasource.DeletionDataSourceImpl
import com.nmichail.meridian.shared.chat.data.datasource.MemberDataSource
import com.nmichail.meridian.shared.chat.data.datasource.MemberDataSourceImpl
import com.nmichail.meridian.shared.chat.data.datasource.ReactionDataSource
import com.nmichail.meridian.shared.chat.data.datasource.ReactionDataSourceImpl
import com.nmichail.meridian.shared.chat.data.repository.ChatRepositoryImpl
import com.nmichail.meridian.shared.chat.data.repository.GroupEditRepositoryImpl
import com.nmichail.meridian.shared.chat.domain.repository.ChatRepository
import com.nmichail.meridian.shared.chat.domain.repository.GroupEditRepository
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

		@Provides
		@PerFeature
		@JvmStatic
		fun provideChatEditApi(ktorfit: Ktorfit): ChatEditApi = ktorfit.createChatEditApi()
	}

	@Binds
	abstract fun bindChatCacheDataSource(impl: ChatCacheDataSourceImpl): ChatCacheDataSource

	@Binds
	abstract fun bindMemberDataSource(impl: MemberDataSourceImpl): MemberDataSource

	@Binds
	abstract fun bindDeletionDataSource(impl: DeletionDataSourceImpl): DeletionDataSource

	@Binds
	abstract fun bindReactionDataSource(impl: ReactionDataSourceImpl): ReactionDataSource

	@Binds
	abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository

	@Binds
	abstract fun bindGroupEditRepository(impl: GroupEditRepositoryImpl): GroupEditRepository
}
