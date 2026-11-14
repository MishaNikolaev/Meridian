package com.nmichail.meridian.features.conversation.di

import com.nmichail.meridian.core.di.BaseDependencyHolder
import com.nmichail.meridian.core.di.DependencyHolder0
import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.conversation.presentation.ConversationRouter
import com.nmichail.meridian.shared.chat.di.ChatRealtimeComponent
import com.nmichail.meridian.shared.chat.di.ChatSessionApi

interface ConversationFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun chatRealtime(): ChatRealtimeComponent

	fun session(): ChatSessionApi

	fun router(): ConversationRouter
}

fun conversationFeatureDependencies(
	core: CoreApi,
	chatRealtime: ChatRealtimeComponent,
	session: ChatSessionApi,
	router: ConversationRouter,
): ConversationFeatureDependencies = object : DependencyHolder0<ConversationFeatureDependencies>() {
	override val block: (BaseDependencyHolder<ConversationFeatureDependencies>) -> ConversationFeatureDependencies =
		{ holder ->
			object : ConversationFeatureDependencies {
				override fun core(): CoreApi = core

				override fun chatRealtime(): ChatRealtimeComponent = chatRealtime

				override fun session(): ChatSessionApi = session

				override fun router(): ConversationRouter = router

				override val dependencyHolder: BaseDependencyHolder<out FeatureDependencies> = holder
			}
		}
}.dependencies
