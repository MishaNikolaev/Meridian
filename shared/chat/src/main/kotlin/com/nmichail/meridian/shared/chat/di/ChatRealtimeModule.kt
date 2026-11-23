package com.nmichail.meridian.shared.chat.di

import com.nmichail.meridian.core.network.config.NetworkConfig
import com.nmichail.meridian.core.network.session.SessionRefresher
import com.nmichail.meridian.shared.chat.data.realtime.ChatRealtimeImpl
import com.nmichail.meridian.shared.chat.domain.realtime.ChatRealtime
import com.nmichail.meridian.shared.session.domain.usecase.GetAccessTokenUseCase
import dagger.Module
import dagger.Provides

@Module
object ChatRealtimeModule {

	@Provides
	@ChatRealtimeScope
	@JvmStatic
	fun provideChatRealtime(
		config: NetworkConfig,
		getAccessTokenUseCase: GetAccessTokenUseCase,
		refresher: SessionRefresher,
	): ChatRealtime =
		ChatRealtimeImpl(
			config = config,
			getAccessTokenUseCase = getAccessTokenUseCase,
			refresher = refresher,
		)
}