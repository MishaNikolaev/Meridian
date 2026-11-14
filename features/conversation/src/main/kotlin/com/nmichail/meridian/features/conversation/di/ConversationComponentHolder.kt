package com.nmichail.meridian.features.conversation.di

import com.nmichail.meridian.core.di.ComponentHolder

object ConversationComponentHolder : ComponentHolder<ConversationFeatureApi> {

	private var component: ConversationFeatureApi? = null

	fun init(dependencies: ConversationFeatureDependencies) {
		check(component == null) { "ConversationComponent is already initialized" }
		component = DaggerConversationComponent.factory().create(
			dependencies.core(),
			dependencies.chatRealtime(),
			dependencies.session(),
			dependencies.router(),
		)
	}

	fun isInitialized(): Boolean = component != null

	override fun get(): ConversationFeatureApi =
		checkNotNull(component) { "ConversationComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
