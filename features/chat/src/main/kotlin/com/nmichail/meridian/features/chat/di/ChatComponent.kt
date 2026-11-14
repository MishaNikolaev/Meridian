package com.nmichail.meridian.features.chat.di

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.core.presentation.di.ViewModelModule
import com.nmichail.meridian.features.chat.presentation.ChatRouter
import com.nmichail.meridian.shared.chat.di.ChatRealtimeComponent
import com.nmichail.meridian.shared.chat.di.ChatSessionApi
import dagger.BindsInstance
import dagger.Component

@PerFeature
@Component(
	dependencies = [CoreApi::class, ChatRealtimeComponent::class, ChatSessionApi::class],
	modules = [ChatFeatureModule::class, ViewModelModule::class],
)
interface ChatComponent : ChatFeatureApi {

	@Component.Factory
	interface Factory {

		fun create(
			core: CoreApi,
			chatRealtime: ChatRealtimeComponent,
			session: ChatSessionApi,
			@BindsInstance router: ChatRouter,
		): ChatComponent
	}
}