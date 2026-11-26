package com.nmichail.meridian.shared.chat.di

import com.nmichail.meridian.core.network.session.SessionRefresher
import com.nmichail.meridian.shared.chat.data.realtime.ChatRealtimeImpl
import com.nmichail.meridian.shared.chat.data.realtime.ChatSocketImpl
import com.nmichail.meridian.shared.chat.domain.realtime.ChatRealtime
import com.nmichail.meridian.shared.chat.domain.realtime.ChatSocket
import com.nmichail.meridian.shared.session.domain.usecase.GetAccessTokenUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class ChatRealtimeModule {
	companion object {
		@Provides
		@ChatRealtimeScope
		@JvmStatic
		fun provideChatRealtime(
			socket: ChatSocket,
			getAccessTokenUseCase: GetAccessTokenUseCase,
			refresher: SessionRefresher,
		): ChatRealtime =
			ChatRealtimeImpl(
				socket = socket,
				getAccessTokenUseCase = getAccessTokenUseCase,
				refresher = refresher,
			)
	}

	@Binds
	@ChatRealtimeScope
	abstract fun bindChatSocket(impl: ChatSocketImpl): ChatSocket
}
