package com.nmichail.meridian.features.conversation.di

import com.nmichail.meridian.core.di.ComponentHolder
import com.nmichail.meridian.core.network.di.CoreApi
import com.nmichail.meridian.features.conversation.presentation.ConversationRouter

object ConversationComponentHolder : ComponentHolder<ConversationFeatureApi> {

	private var component: ConversationFeatureApi? = null

	fun init(core: CoreApi, router: ConversationRouter) {
		if (component == null) {
			component = DaggerConversationComponent.factory().create(core, router)
		}
	}

	override fun get(): ConversationFeatureApi =
		checkNotNull(component) { "ConversationComponent was not initialized" }

	override fun reset() {
		component = null
	}
}
