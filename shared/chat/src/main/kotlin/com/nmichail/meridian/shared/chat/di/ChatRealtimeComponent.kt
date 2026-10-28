package com.nmichail.meridian.shared.chat.di

import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.shared.chat.data.realtime.ChatRealtime
import dagger.Component

@ChatRealtimeScope
@Component(
	dependencies = [CoreApi::class],
	modules = [ChatRealtimeModule::class],
)
interface ChatRealtimeComponent {

	fun chatRealtime(): ChatRealtime

	@Component.Factory
	interface Factory {

		fun create(core: CoreApi): ChatRealtimeComponent
	}
}