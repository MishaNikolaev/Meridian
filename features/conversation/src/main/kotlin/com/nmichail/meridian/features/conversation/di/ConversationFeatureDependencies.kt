package com.nmichail.meridian.features.conversation.di

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

class ConversationFeatureDependenciesImpl(
	private val core: CoreApi,
	private val chatRealtime: ChatRealtimeComponent,
	private val session: ChatSessionApi,
	private val router: ConversationRouter,
) : ConversationFeatureDependencies {

	override fun core(): CoreApi = core

	override fun chatRealtime(): ChatRealtimeComponent = chatRealtime

	override fun session(): ChatSessionApi = session

	override fun router(): ConversationRouter = router
}