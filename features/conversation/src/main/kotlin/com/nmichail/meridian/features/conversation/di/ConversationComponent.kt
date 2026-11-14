package com.nmichail.meridian.features.conversation.di

import com.nmichail.meridian.core.di.PerFeature
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.core.presentation.di.ViewModelModule
import com.nmichail.meridian.features.conversation.presentation.ConversationRouter
import com.nmichail.meridian.shared.chat.di.ChatRealtimeComponent
import com.nmichail.meridian.shared.chat.di.ChatSessionApi
import dagger.BindsInstance
import dagger.Component

@PerFeature
@Component(
	dependencies = [CoreApi::class, ChatRealtimeComponent::class, ChatSessionApi::class],
	modules = [ConversationModule::class, ViewModelModule::class],
)
internal interface ConversationComponent : ConversationFeatureApi {

	@Component.Factory
	interface Factory {

		fun create(
			core: CoreApi,
			chatRealtime: ChatRealtimeComponent,
			session: ChatSessionApi,
			@BindsInstance router: ConversationRouter,
		): ConversationComponent
	}
}