package com.nmichail.meridian.features.chat.di

import com.nmichail.meridian.core.di.ComponentHolder

object ChatComponentHolder : ComponentHolder<ChatFeatureApi> {

	private var component: ChatFeatureApi? = null

	fun init(dependencies: ChatFeatureDependencies) {
		check(component == null) { "ChatComponent is already initialized" }
		component = DaggerChatComponent.factory().create(
			dependencies.core(),
			dependencies.chatRealtime(),
			dependencies.session(),
			dependencies.router(),
		)
	}

	fun isInitialized(): Boolean = component != null

	override fun get(): ChatFeatureApi =
		checkNotNull(component) { "ChatComponent was not initialized" }

	override fun reset() {
		component = null
	}
}