package com.nmichail.meridian.features.chat.di

import com.nmichail.meridian.core.di.BaseDependencyHolder
import com.nmichail.meridian.core.di.DependencyHolder0
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

fun chatFeatureDependencies(
	core: CoreApi,
	chatRealtime: ChatRealtimeComponent,
	session: ChatSessionApi,
	router: ChatRouter,
): ChatFeatureDependencies = object : DependencyHolder0<ChatFeatureDependencies>() {
	override val block: (BaseDependencyHolder<ChatFeatureDependencies>) -> ChatFeatureDependencies = { holder ->
		object : ChatFeatureDependencies {
			override fun core(): CoreApi = core

			override fun chatRealtime(): ChatRealtimeComponent = chatRealtime

			override fun session(): ChatSessionApi = session

			override fun router(): ChatRouter = router

			override val dependencyHolder: BaseDependencyHolder<out FeatureDependencies> = holder
		}
	}
}.dependencies