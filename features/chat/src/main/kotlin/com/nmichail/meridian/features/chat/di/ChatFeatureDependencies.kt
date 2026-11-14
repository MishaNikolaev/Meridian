package com.nmichail.meridian.features.chat.di

import com.nmichail.meridian.core.di.FeatureDependencies
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.chat.presentation.ChatRouter
import com.nmichail.meridian.shared.chat.di.ChatRealtimeComponent
import com.nmichail.meridian.shared.chat.di.ChatSessionApi

interface ChatFeatureDependencies : FeatureDependencies {

	fun core(): CoreApi

	fun chatRealtime(): ChatRealtimeComponent

	fun session(): ChatSessionApi

	fun router(): ChatRouter
}

class ChatFeatureDependenciesImpl(
	private val core: CoreApi,
	private val chatRealtime: ChatRealtimeComponent,
	private val session: ChatSessionApi,
	private val router: ChatRouter,
) : ChatFeatureDependencies {

	override fun core(): CoreApi = core

	override fun chatRealtime(): ChatRealtimeComponent = chatRealtime

	override fun session(): ChatSessionApi = session

	override fun router(): ChatRouter = router
}